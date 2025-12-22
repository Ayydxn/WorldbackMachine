package com.ayydxn.worldbackmachine.cloud;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.options.WorldbackMachineGameOptions;
import com.google.common.collect.ImmutableList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Responsible for managing the cloud storage operations for world backups.
 * <p>
 * This class provides a unified interface for registering and interacting with cloud storage providers
 * and managing world saves across the different cloud storage providers that are available.
 *
 * @author Ayydxn
 * @see CloudStorageProvider
 */
public class CloudStorageManager
{
    private final CloudStorageProviderRegistry storageProviderRegistry;

    private CloudStorageProvider activeStorageProvider;

    public CloudStorageManager()
    {
        this.storageProviderRegistry = new CloudStorageProviderRegistry();

        this.registerBuiltInProviders();
    }

    /**
     * Registers the built-in cloud providers.
     *
     * <p>This method registers Google Drive and OneDrive as the default
     * providers included with the Cloud Saves mod.
     */
    private void registerBuiltInProviders()
    {
        WorldbackMachineMod.LOGGER.info("Registered {} built-in cloud storage providers", this.storageProviderRegistry.getProviderCount());
    }

    /**
     * Uploads the current world to cloud storage.
     * <p>
     * This will compress the entire world directory into a ZIP file and upload it to the active cloud storage provider.
     * The operation is logged and any errors are caught and logged.
     *
     * @param server The Minecraft server instance containing the world to upload
     */
    @SuppressWarnings("DataFlowIssue")
    public void uploadWorld(MinecraftServer server)
    {
        if (this.activeStorageProvider == null || !activeStorageProvider.isAuthenticated())
        {
            WorldbackMachineMod.LOGGER.warn("Failed to upload world! No authenticated cloud storage provider is available!");
            return;
        }

        try
        {
            Path worldPath = server.getSavePath(WorldSavePath.ROOT);
            String worldName = ((LevelProperties) server.getWorld(World.OVERWORLD).getLevelProperties()).getLevelName();

            WorldbackMachineMod.LOGGER.info("Starting backup of world '{}'...", worldName);

            // Create temporary zip file
            File temporaryWorldZip = this.createWorldBackup(worldPath, worldName);

            // Upload to cloud
            this.activeStorageProvider.uploadFile(temporaryWorldZip, worldName + ".zip");

            // Clean up
            Validate.isTrue(temporaryWorldZip.delete(), "Failed to delete temporary world ZIP file!");

            WorldbackMachineMod.LOGGER.info("World backup completed successfully");
        }
        catch (Exception exception)
        {
            WorldbackMachineMod.LOGGER.error(exception);
        }
    }

    /**
     * Downloads a world from cloud storage.
     * <p>
     * Downloads a world backup from cloud storage and extracts it to the specified target path.
     *
     * @param worldName  the name of the world to download (without .zip extension)
     * @param targetPath the directory where the world should be extracted
     */
    public void downloadWorld(String worldName, Path targetPath)
    {
        if (this.activeStorageProvider == null || !this.activeStorageProvider.isAuthenticated())
        {
            WorldbackMachineMod.LOGGER.warn("Failed to download world '{}'! No authenticated cloud storage provider is available!", worldName);
            return;
        }

        try
        {
            WorldbackMachineMod.LOGGER.info("Downloading world '{}'...", worldName);

            // Download zip file
            File temporaryWorldZip = File.createTempFile(worldName.toLowerCase().replace(" ", "_") + "_download_", ".zip");
            this.activeStorageProvider.downloadFile(worldName + ".zip", temporaryWorldZip);

            // Extract to target path
            this.extractWorldBackup(temporaryWorldZip, targetPath);

            // Clean up
            Validate.isTrue(temporaryWorldZip.delete());

            WorldbackMachineMod.LOGGER.info("Successfully downloaded backup of world '{}'!", worldName);
        }
        catch (Exception exception)
        {
            WorldbackMachineMod.LOGGER.error(exception);
        }
    }

    /**
     * Creates a compressed ZIP backup of a world.
     * <p>
     * This walks through the entire world directory and compresses all files into a temporary ZIP file.
     * The ZIP maintains the relative directory structure of the world.
     *
     * @param worldPath The path to the world directory
     * @param worldName The name of the world
     * @return A temporary file containing the compressed world
     * @throws IOException if compression fails
     */
    private File createWorldBackup(Path worldPath, String worldName) throws IOException
    {
        File tempZip = File.createTempFile(worldName.toLowerCase().replace(" ", "_") + "_backup_", ".zip");

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempZip)))
        {
            try (Stream<Path> worldPathStream = Files.walk(worldPath))
            {
                worldPathStream.filter(path -> !Files.isDirectory(path))
                        .forEach(path ->
                        {
                            try
                            {
                                String zipEntryName = worldPath.relativize(path).toString();
                                zos.putNextEntry(new ZipEntry(zipEntryName));

                                Files.copy(path, zos);

                                zos.closeEntry();
                            }
                            catch (IOException exception)
                            {
                                WorldbackMachineMod.LOGGER.error(exception);
                            }
                        });
            }
        }

        return tempZip;
    }

    /**
     * Extracts a world backup ZIP file to a target directory.
     * <p>
     * Extracts all files from the ZIP while preserving the directory
     * structure. Creates any necessary parent directories.
     *
     * @param zipFile    the ZIP file to extract
     * @param targetPath the directory where files should be extracted
     * @throws IOException if extraction fails
     */
    private void extractWorldBackup(File zipFile, Path targetPath) throws IOException
    {
        Files.createDirectories(targetPath);

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile)))
        {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null)
            {
                Path filePath = targetPath.resolve(entry.getName());

                if (entry.isDirectory())
                {
                    Files.createDirectories(filePath);
                }
                else
                {
                    Files.createDirectories(filePath.getParent());
                    Files.copy(zipInputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipInputStream.closeEntry();
            }
        }
    }

    /**
     * Lists all worlds available in cloud storage.
     *
     * <p>Retrieves a list of all world backup files from the active
     * cloud provider. Returns world names without the .zip extension.
     *
     * @return list of world names, or empty list if not authenticated or on error
     */
    public List<String> listCloudWorlds()
    {
        if (this.activeStorageProvider == null || !this.activeStorageProvider.isAuthenticated())
            return Collections.emptyList();

        try
        {
            List<String> files = this.activeStorageProvider.listFiles();

            // Filter for zip files and remove extension
            return files.stream()
                    .filter(f -> f.endsWith(".zip"))
                    .map(f -> f.substring(0, f.length() - 4))
                    .toList();
        }
        catch (Exception exception)
        {
            WorldbackMachineMod.LOGGER.error(exception);

            return Collections.emptyList();
        }
    }

    /**
     * Locks the provider registry.
     *
     * <p>This should be called after all mods have initialized to prevent
     * runtime registration of providers. Called automatically by the mod.
     */
    public void lockRegistry()
    {
        this.storageProviderRegistry.lock();
    }

    /**
     * Gets the provider registry.
     *
     * <p>The registry allows querying and managing registered providers.
     * After mod initialization, the registry is locked and no new providers
     * can be registered.
     *
     * @return the provider registry
     */
    public CloudStorageProviderRegistry getRegistry()
    {
        return this.storageProviderRegistry;
    }

    /**
     * Gets an {@link ImmutableList immutable list} of the names of all available storage providers.
     *
     * @return An {@link ImmutableList} of the names of all available cloud storage providers.
     */
    public List<String> getAvailableProviders()
    {
        return ImmutableList.copyOf(this.storageProviderRegistry.getRegisteredProviderNames());
    }

    /**
     * Gets the currently active cloud provider.
     *
     * @return the active provider, or null if none is set
     */
    public CloudStorageProvider getActiveProvider()
    {
        return this.activeStorageProvider;
    }

    /**
     * Sets the active cloud storage provider.
     *
     * @param providerName the name of the provider to activate (case-insensitive)
     * @throws IllegalArgumentException if the provider name is not registered
     */
    public void setActiveProvider(String providerName)
    {
        CloudStorageProvider cloudStorageProvider = this.storageProviderRegistry.getProviderInstance(providerName);
        if (cloudStorageProvider != null)
        {
            this.activeStorageProvider = cloudStorageProvider;

            WorldbackMachineGameOptions gameOptions = WorldbackMachineMod.getInstance().getGameOptions();
            gameOptions.activeCloudStorageProvider = providerName.toLowerCase();
            gameOptions.save();

            WorldbackMachineMod.LOGGER.info("The active cloud provider has been switched to '{}'", cloudStorageProvider.getProviderName());
        }
        else
        {
            Set<String> availableProviderNames = this.storageProviderRegistry.getRegisteredProviderNames();
            StringBuilder availableProvidersString = new StringBuilder();

            for (String availableProvider : availableProviderNames)
            {
                availableProvidersString.append("- ")
                        .append(availableProvider)
                        .append("\n");
            }

            throw new IllegalArgumentException(String.format("Provider '%s' was not registered. The available providers are:\n%s",
                    providerName, availableProvidersString));
        }
    }
}
