package com.ayydxn.worldbackmachine.options;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import net.minecraft.text.Text;

public class WorldbackMacineOptionCategories
{
    public static ConfigCategory worldbackMachine()
    {
        WorldbackMachineWorldOptions worldbackMachineWorldOptions = WorldbackMachineMod.getInstance().getWorldOptions();

        return ConfigCategory.createBuilder()
                .name(Text.translatable("worldback-machine.options.category.worldback_machine"))
                .option(Option.<Boolean>createBuilder()
                        .name(Text.translatable("worldback-machine.options.worldback_machine.saveWorld"))
                        .description(OptionDescription.of(Text.translatable("worldback-machine.options.worldback_machine.saveWorld.description")))
                        .binding(WorldbackMachineWorldOptions.defaults().shouldSaveWorld, () -> worldbackMachineWorldOptions.shouldSaveWorld, newValue -> worldbackMachineWorldOptions.shouldSaveWorld = newValue)
                        .customController(BooleanController::new)
                        .build())
                .build();
    }
}
