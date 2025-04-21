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
     * <br/>
     * FIXME: (Ayydxn) Filepath shouldn't be hardcoded. Move that to a .env file or something.
     */
    public static final Path CREDENTIALS_FILE = FabricLoader.getInstance().getModContainer(WorldbackMachineMod.MOD_ID)
            .map(modContainer -> modContainer.findPath("auth/credentials.json")).orElseThrow().orElseThrow();

    /**
     * The name of the folder where the mod will store all the world saves on Google Drive.
     */
    public static final String SAVE_FOLDER_NAME = "Worldback Machine";
}
