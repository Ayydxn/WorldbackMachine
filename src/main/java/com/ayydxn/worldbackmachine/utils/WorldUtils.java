package com.ayydxn.worldbackmachine.utils;

import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;

public class WorldUtils
{
    public static String getWorldName(World world)
    {
        return ((LevelProperties) world.getServer().getWorld(World.OVERWORLD).getLevelProperties()).levelInfo.getLevelName();
    }
}
