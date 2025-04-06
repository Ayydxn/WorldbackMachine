package com.ayydxn.worldbackmachine;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class WorldbackMachineMod implements ModInitializer
{
    private static final Logger LOGGER = (Logger) LogManager.getLogger("Worldback Machine");
    private static final String MOD_ID = "worldback-machine";

    @Override
    public void onInitialize()
    {
        LOGGER.info("Initializing Worldback Machine... (Version: {})", FabricLoader.getInstance().getModContainer(MOD_ID)
                .orElseThrow(NullPointerException::new).getMetadata().getVersion().getFriendlyString());
    }
}
