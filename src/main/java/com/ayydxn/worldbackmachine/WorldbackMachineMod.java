package com.ayydxn.worldbackmachine;

import com.ayydxn.worldbackmachine.api.WorldbackMachineApi;
import com.ayydxn.worldbackmachine.cloud.AutomaticBackupScheduler;
import com.ayydxn.worldbackmachine.cloud.CloudStorageManager;
import com.ayydxn.worldbackmachine.event.ServerLifecycleEventHandler;
import com.ayydxn.worldbackmachine.options.WorldbackMachineGameOptions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The main mod class for Worldback Machine.
 * <p>
 * The class is responsible for initializing core systems such as API integration with the player's selected cloud provider
 * and starting the automatic backup scheduler.
 *
 * @author Ayydxn
 */
public class WorldbackMachineMod implements ModInitializer
{
    private static WorldbackMachineMod INSTANCE;

    public static final Logger LOGGER = (Logger) LogManager.getLogger("Worldback Machine");
    public static final String MOD_ID = "worldback-machine";

    private CloudStorageManager cloudStorageManager;
    private AutomaticBackupScheduler automaticBackupScheduler;

    @Override
    public void onInitialize()
    {
        INSTANCE = this;

        String modVersion = FabricLoader.getInstance().getModContainer(WorldbackMachineMod.MOD_ID).orElseThrow()
                .getMetadata().getVersion().getFriendlyString();

        LOGGER.info("Initializing Worldback Machine... (Version: {})", modVersion);

        // Load mod settings
        WorldbackMachineGameOptions.HANDLER.load();

        // Initialize the cloud storage manager.
        // This will also register the mod's built-in cloud storage providers.
        this.cloudStorageManager = new CloudStorageManager();

        // Register cloud storage providers from other mods
        this.registerCustomCloudStorageProviders();

        // Lock the register and prevent further registration of storage providers
        this.cloudStorageManager.lockRegistry();

        // Initialize the automatic backup scheduler
        this.automaticBackupScheduler = new AutomaticBackupScheduler(this.cloudStorageManager);

        // Register event handlers
        ServerLifecycleEventHandler serverLifecycleEventHandler = new ServerLifecycleEventHandler(this.cloudStorageManager, this.automaticBackupScheduler);

        ServerLifecycleEvents.SERVER_STARTED.register(serverLifecycleEventHandler);
        ServerLifecycleEvents.SERVER_STOPPING.register(serverLifecycleEventHandler);
    }

    private void registerCustomCloudStorageProviders()
    {
        LOGGER.info("Registering cloud storage providers from external mods...");

        List<EntrypointContainer<WorldbackMachineApi>> entrypointContainers = FabricLoader.getInstance().getEntrypointContainers("worldback-machine",
                WorldbackMachineApi.class);

        Map<String, Integer> externalModsAndProvidersRegistered = Maps.newHashMap();
        int initializersLoaded = 0;
        int providersLoaded = 0;

        try
        {
            for (EntrypointContainer<WorldbackMachineApi> entrypointContainer : entrypointContainers)
            {
                initializersLoaded++;

                String modID = entrypointContainer.getProvider().getMetadata().getId();

                try
                {
                    WorldbackMachineApi worldbackMachineApi = entrypointContainer.getEntrypoint();
                    int currentProviderCount = this.cloudStorageManager.getRegistry().getProviderCount();

                    worldbackMachineApi.registerCloudStorageProviders(this.cloudStorageManager.getRegistry());

                    int newProviderCount = this.cloudStorageManager.getRegistry().getProviderCount();
                    int numberOfProvidersRegistered = newProviderCount - currentProviderCount;

                    externalModsAndProvidersRegistered.put(modID, numberOfProvidersRegistered);

                    if (numberOfProvidersRegistered > 0)
                    {
                        LOGGER.info("Mod '{}' has registered {} cloud storage {}", modID, numberOfProvidersRegistered,
                                numberOfProvidersRegistered == 1 ? "provider" : "providers");

                        providersLoaded += numberOfProvidersRegistered;
                    }
                }
                catch (Exception exception)
                {
                    LOGGER.error(exception);
                }
            }

            LOGGER.info("Registered {} custom storage providers from {} mods:", providersLoaded, initializersLoaded);

            for (var entry : externalModsAndProvidersRegistered.entrySet())
                LOGGER.info("- {} ({} {})", entry.getKey(), entry.getValue(), entry.getValue() == 1 ? "provider" : "providers");

            List<String> availableProviders = this.cloudStorageManager.getAvailableProviders();
            LOGGER.info("Total registered providers ({}): {}", availableProviders.size(), String.join(", ", availableProviders));
        }
        catch (Exception exception)
        {
            LOGGER.error(exception);
        }
    }

    /**
     * Returns the instance of Worldback Machine that is currently available
     *
     * @throws IllegalStateException If an instance of Worldback Machine was not available
     * @return The currently active instance of Worldback Machine
     */
    public static WorldbackMachineMod getInstance()
    {
        if (INSTANCE == null)
            throw new IllegalStateException("Tried to get an instance of Worldback Machine when one wasn't available!");

        return INSTANCE;
    }

    /**
     * Returns an instance of the mod's currently configured settings.
     *
     * @return An instance of the mod's currently configured settings.
     */
    public WorldbackMachineGameOptions getGameOptions()
    {
        return WorldbackMachineGameOptions.HANDLER.instance();
    }
}