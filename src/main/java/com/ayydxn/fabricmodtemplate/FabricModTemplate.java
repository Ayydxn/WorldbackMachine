package com.ayydxn.fabricmodtemplate;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricModTemplate implements ModInitializer
{
    public static final Logger LOGGER = LoggerFactory.getLogger("Fabric Mod Template");

    @Override
    public void onInitialize()
    {
        LOGGER.info("Hello from Ayydxn's Fabric Mod Template!");
    }
}