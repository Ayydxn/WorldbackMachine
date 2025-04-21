package com.ayydxn.worldbackmachine;

import com.ayydxn.worldbackmachine.google.GoogleDriveAPI;
import com.ayydxn.worldbackmachine.google.GoogleDriveBootstrap;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class WorldbackMachineMod implements ModInitializer
{
    private static WorldbackMachineMod INSTANCE;

    private static final Logger LOGGER = (Logger) LogManager.getLogger("Worldback Machine");

    public static final String MOD_ID = "worldback-machine";

    private GoogleDriveAPI googleDriveAPI;
    private String saveFolderID;

    @Override
    public void onInitialize()
    {
        INSTANCE = this;

        LOGGER.info("Initializing Worldback Machine... (Version: {})", FabricLoader.getInstance().getModContainer(MOD_ID)
                .orElseThrow(NullPointerException::new).getMetadata().getVersion().getFriendlyString());

        try
        {
            this.googleDriveAPI = GoogleDriveBootstrap.bootstrap();
            Preconditions.checkArgument(this.googleDriveAPI != null);
        }
        catch (Exception exception)
        {
            LOGGER.error(ExceptionUtils.getStackTrace(exception));
        }

        this.saveFolderID = this.googleDriveAPI.createFolder("Worldback Machine")
                .orElseThrow(NullPointerException::new);
    }

    public static WorldbackMachineMod getInstance()
    {
        if (INSTANCE == null)
            throw new IllegalStateException("Tried accessing an instance of Worldback Machine before one was available!");

        return INSTANCE;
    }

    public static Logger getLogger()
    {
        return LOGGER;
    }

    public GoogleDriveAPI getGoogleDriveAPI()
    {
        return this.googleDriveAPI;
    }

    public String getSaveFolderID()
    {
        return this.saveFolderID;
    }
}
