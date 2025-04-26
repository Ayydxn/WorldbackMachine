package com.ayydxn.worldbackmachine.options;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.utils.WorldUtils;
import com.ayydxn.worldbackmachine.utils.WorldbackMachineConstants;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class WorldbackMachineWorldOptions
{
    public boolean shouldSaveWorld = true;

    private static World WORLD = null;

    public static WorldbackMachineWorldOptions defaults()
    {
        return new WorldbackMachineWorldOptions();
    }

    public static WorldbackMachineWorldOptions load(World world)
    {
        WorldbackMachineWorldOptions.WORLD = world;

        if (Files.exists(getConfigFilePath()))
        {
            StringBuilder configFileContents = new StringBuilder();

            try
            {
                configFileContents.append(FileUtils.readFileToString(getConfigFilePath().toFile(), StandardCharsets.UTF_8));
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }

            WorldbackMachineWorldOptions iridiumGameOptions = null;

            try
            {
                iridiumGameOptions = WorldbackMachineConstants.GSON.fromJson(configFileContents.toString(), WorldbackMachineWorldOptions.class);
            }
            catch (JsonSyntaxException exception) // If the config file is corrupted on disk.
            {
                WorldbackMachineMod.getLogger().error(exception);
            }

            WorldbackMachineMod.getLogger().info("Successfully loaded world options for world '{}'!", WorldUtils.getWorldName(WORLD));

            return iridiumGameOptions;
        }
        else
        {
            WorldbackMachineMod.getLogger().warn("Failed to load Worldback Machine's options for the world '{}'! Loading defaults...",
                    WorldUtils.getWorldName(WORLD));

            WorldbackMachineWorldOptions defaultIridiumGameOptions = WorldbackMachineWorldOptions.defaults();
            defaultIridiumGameOptions.write();

            return defaultIridiumGameOptions;
        }
    }

    public void write()
    {
        try
        {
            FileUtils.writeStringToFile(getConfigFilePath().toFile(), WorldbackMachineConstants.GSON.toJson(this),
                    StandardCharsets.UTF_8);
        }
        catch (IOException exception)
        {
            WorldbackMachineMod.getLogger().error(exception);
        }
    }

    private static Path getConfigFilePath()
    {
        return Objects.requireNonNull(WORLD.getServer()).getSavePath(WorldSavePath.ROOT).resolve("worldback-machine-world-settings.json");
    }
}
