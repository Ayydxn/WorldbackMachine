package com.ayydxn.worldbackmachine.utils;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class WorldbackMachineConstants
{
    /**
     * A directory which holds the user's authorization tokens after they authenticate with a cloud storage provider's API.
     */
    public static final Path TOKENS_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("worldback-machine/auth-tokens");

    /**
     * The name of the folder where the mod will store all the world saves on cloud storage providers.
     */
    public static final String SAVE_FOLDER_NAME = "Worldback Machine";
}
