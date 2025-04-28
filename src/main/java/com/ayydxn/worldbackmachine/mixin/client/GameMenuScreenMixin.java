package com.ayydxn.worldbackmachine.mixin.client;

import com.ayydxn.worldbackmachine.options.gui.WorldbackMachineOptionsGUI;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen
{
    public GameMenuScreenMixin(Text title)
    {
        super(title);
    }

    @Inject(method = "initWidgets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;I)Lnet/minecraft/client/gui/widget/Widget;"))
    public void addWorldbackMachineOptionScreenButton(CallbackInfo ci, @Local GridWidget.Adder adder, @Local Text text)
    {
        if (this.client.isInSingleplayer())
        {
            adder.add(ButtonWidget.builder(Text.translatable("worldback-machine.options.gui_button"), button ->
            {
                Screen worldbackMachineOptionsScreen = new WorldbackMachineOptionsGUI().getHandle();

                this.client.setScreen(worldbackMachineOptionsScreen);
            }).width(204).build(), 2);
        }
    }
}
