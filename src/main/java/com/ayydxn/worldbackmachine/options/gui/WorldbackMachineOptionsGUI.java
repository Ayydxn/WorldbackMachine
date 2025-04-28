package com.ayydxn.worldbackmachine.options.gui;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.options.WorldbackMacineOptionCategories;
import com.google.common.collect.Lists;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class WorldbackMachineOptionsGUI
{
    public Screen getHandle()
    {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Worldback Machine Options"))
                .categories(Lists.newArrayList(WorldbackMacineOptionCategories.worldbackMachine()))
                .save(WorldbackMachineMod.getInstance().getWorldOptions()::write)
                .build()
                .generateScreen(null);
    }
}
