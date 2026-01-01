package com.ayydxn.worldbackmachine.options.gui;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.cloud.CloudStorageManager;
import com.ayydxn.worldbackmachine.cloud.CloudStorageProvider;
import com.ayydxn.worldbackmachine.options.WorldbackMachineGameOptions;
import com.google.common.collect.Lists;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import dev.isxander.yacl3.gui.controllers.cycling.CyclingListController;
import dev.isxander.yacl3.gui.controllers.slider.IntegerSliderController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class WorldbackMachineOptionsScreen
{
    private final WorldbackMachineGameOptions gameOptions;
    private final Screen parentScreen;

    public WorldbackMachineOptionsScreen(Screen parentScreen)
    {
        this.gameOptions = WorldbackMachineMod.getInstance().getGameOptions();
        this.parentScreen = parentScreen;
    }

    /**
     * Generates and gets an instance of a {@link Screen screen} using YetAnotherConfigLib.
     *
     * @return An instance of a YACL options {@link Screen screen}
     */
    public Screen getYACLScreen()
    {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.literal("Worldback Machine Options"))
                .categories(Collections.singletonList(this.getMainOptionsCategory()))
                .save(WorldbackMachineGameOptions.HANDLER::save)
                .build()
                .generateScreen(parentScreen);
    }

    private ConfigCategory getMainOptionsCategory()
    {
        return ConfigCategory.createBuilder()
                .name(Text.translatable("worldback_machine.options.category.worldback_machine"))
                .groups(Lists.newArrayList(this.getGeneralOptionsGroup(), this.getBackupsOptionsGroup()))
                .build();
    }

    private OptionGroup getGeneralOptionsGroup()
    {
        CloudStorageManager cloudStorageManager = WorldbackMachineMod.getInstance().getCloudStorageManager();

        String availableCloudStorageProviders = cloudStorageManager.getRegistry().getAllProviders().values().stream()
                .map(cloudStorageProvider -> "- " + cloudStorageProvider.getProviderName())
                .collect(Collectors.joining("\n"));

        Option<String> cloudStorageProviderOption = Option.<String>createBuilder()
                .name(Text.translatable("worldback_machine.options.general.cloud_storage_provider"))
                .description(OptionDescription.of(Text.translatable("worldback_machine.options.general.cloud_storage_provider.description")
                        .append(availableCloudStorageProviders)))
                .binding("google_drive", () -> this.gameOptions.cloudStorageProvider, newValue ->
                {
                    this.gameOptions.cloudStorageProvider = newValue;

                    cloudStorageManager.setActiveProvider(this.gameOptions.cloudStorageProvider);
                })
                .customController(option -> new CyclingListController<>(option, cloudStorageManager.getAvailableProviders(), value ->
                {
                    CloudStorageProvider providerInstance = Objects.requireNonNull(cloudStorageManager.getRegistry().getProviderInstance(value));

                    return Text.literal(providerInstance.getProviderName());
                }))
                .build();

        return OptionGroup.createBuilder()
                .name(Text.translatable("worldback_machine.options.group.general"))
                .options(Collections.singletonList(cloudStorageProviderOption))
                .build();
    }

    private OptionGroup getBackupsOptionsGroup()
    {
        int thirtyMinsInSeconds = (int) TimeUnit.MINUTES.toSeconds(30L);
        int oneDayInSeconds = (int) TimeUnit.DAYS.toSeconds(1L);

        Option<Integer> backupIntervalSeconds = Option.<Integer>createBuilder()
                .name(Text.translatable("worldback_machine.options.backups.backup_interval_seconds"))
                .description(OptionDescription.of(Text.translatable("worldback_machine.options.backups.backup_interval_seconds.description")))
                .binding((int) TimeUnit.HOURS.toSeconds(1L), () -> this.gameOptions.backupIntervalSeconds, newValue -> this.gameOptions.backupIntervalSeconds = newValue)
                .customController(option -> new IntegerSliderController(option, thirtyMinsInSeconds, oneDayInSeconds, 1, value ->
                {
                    long hours = value / 3600;
                    long minutes = (value % 3600) / 60;
                    long seconds = value % 60;

                    StringBuilder formattedString = new StringBuilder();

                    if (hours > 0)
                        formattedString.append(hours).append(" Hours ");

                    if (minutes > 0)
                        formattedString.append(minutes).append(" Minutes ");

                    if (seconds > 0)
                        formattedString.append(seconds).append(" Seconds");

                    return Text.of(formattedString.toString().trim());
                }))
                .build();

        Option<Boolean> areAutoBackupsEnabledOption = Option.<Boolean>createBuilder()
                .name(Text.translatable("worldback_machine.options.backups.are_auto_backups_enabled"))
                .description(OptionDescription.of(Text.translatable("worldback_machine.options.backups.are_auto_backups_enabled.description")))
                .binding(true, () -> this.gameOptions.areAutoBackupsEnabled, newValue -> this.gameOptions.areAutoBackupsEnabled = newValue)
                .customController(BooleanController::new)
                .addListener((option, event) -> backupIntervalSeconds.setAvailable(option.pendingValue()))
                .build();

        return OptionGroup.createBuilder()
                .name(Text.translatable("worldback_machine.options.group.backups"))
                .options(Lists.newArrayList(areAutoBackupsEnabledOption, backupIntervalSeconds))
                .build();
    }
}
