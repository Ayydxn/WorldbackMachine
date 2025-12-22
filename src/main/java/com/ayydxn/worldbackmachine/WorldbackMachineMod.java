package com.ayydxn.worldbackmachine;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

public class WorldbackMachineMod implements ModInitializer
{
    public static final Logger LOGGER = (Logger) LogManager.getLogger("Worldback Machine");
    public static final String MOD_ID = "worldback-machine";

    @Override
    public void onInitialize()
    {
        String modVersion = FabricLoader.getInstance().getModContainer(WorldbackMachineMod.MOD_ID).orElseThrow()
                .getMetadata().getVersion().getFriendlyString();

        LOGGER.info("Initializing Worldback Machine... (Version: {})", modVersion);
    }
}