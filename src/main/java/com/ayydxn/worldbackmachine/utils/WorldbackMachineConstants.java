package com.ayydxn.worldbackmachine.utils;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class WorldbackMachineConstants
{
    /**
     * A directory which holds the user's authorization tokens after they authenticate with the Google Drive API.
     */
    public static final Path TOKENS_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("tokens");

    /**
     * The file which contains your authorization credentials for the Google Drive API.
     */
    public static final Path CREDENTIALS_FILE = FabricLoader.getInstance().getModContainer(WorldbackMachineMod.MOD_ID)
            .map(modContainer -> modContainer.findPath("auth/credentials.json")).orElseThrow().orElseThrow();
}
