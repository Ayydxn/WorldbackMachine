package com.ayydxn.worldbackmachine;

import com.ayydxn.worldbackmachine.options.gui.WorldbackMachineOptionsScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class WorldbackMachineModMenuEntrypoint implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return screen -> new WorldbackMachineOptionsScreen(screen).getYACLScreen();
    }
}
