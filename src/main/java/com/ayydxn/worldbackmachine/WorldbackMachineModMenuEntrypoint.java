package com.ayydxn.worldbackmachine;

import com.ayydxn.worldbackmachine.options.WorldbackMachineGameOptions;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class WorldbackMachineModMenuEntrypoint implements ModMenuApi
{
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory()
    {
        return screen ->
        {
            WorldbackMachineGameOptions gameOptions = WorldbackMachineMod.getInstance().getGameOptions();
            return gameOptions.getOptionsScreen(screen);
        };
    }
}
