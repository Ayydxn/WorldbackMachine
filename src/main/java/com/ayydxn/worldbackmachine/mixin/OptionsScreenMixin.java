package com.ayydxn.worldbackmachine.mixin;

import com.ayydxn.worldbackmachine.options.gui.WorldbackMachineOptionsScreen;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen
{
    public OptionsScreenMixin(Text title)
    {
        super(title);
    }

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ThreePartsLayoutWidget;forEachChild(Ljava/util/function/Consumer;)V"))
    public void addWorldbackMachineOptionsButton(CallbackInfo ci, @Local GridWidget.Adder adder)
    {
        Screen optionsScreen = new WorldbackMachineOptionsScreen((OptionsScreen) (Object) this).getYACLScreen();

        adder.add(ButtonWidget.builder(Text.translatable("worldback_machine.options"), (button) -> this.client.setScreen(optionsScreen))
                .width(308) // Aligns correctly with the other buttons vertically
                .build(), 2);
    }
}
