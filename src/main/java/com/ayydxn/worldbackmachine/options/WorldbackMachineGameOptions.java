package com.ayydxn.worldbackmachine.options;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.cloud.CloudStorageManager;
import com.google.gson.FieldNamingPolicy;
import dev.isxander.yacl3.api.controller.ValueFormatter;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.TimeUnit;

/**
 * Manages the configuration for the mod.
 * <p>
 * This class handles loading, saving, and accessing configuration values stored in a JSON file.
 * Configuration includes cloud provider selection, backup settings, and other user preferences.
 * <p>
 * Additionally, it uses YetAnotherConfigLib's {@link ConfigClassHandler} API for managing instances and updating the configuration file.
 *
 * @see CloudStorageManager
 *
 * @author Ayydxn
 */
public class WorldbackMachineGameOptions
{
    public static final ConfigClassHandler<WorldbackMachineGameOptions> HANDLER = ConfigClassHandler.createBuilder(WorldbackMachineGameOptions.class)
            .id(Identifier.of(WorldbackMachineMod.MOD_ID, "worldback-machine-settings"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(FabricLoader.getInstance().getConfigDir().resolve("worldback-machine-settings.json5"))
                    .appendGsonBuilder(gsonBuilder -> gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY))
                    .setJson5(true)
                    .build())
            .build();

    // TODO: (Ayydxn) Currently not configurable right now. Not like we're working with enum values, so we'll need a way to show this in an UI.
    @SerialEntry(comment = "The internal of the currently selected cloud storage provider. It's recommend to only change this via the in-game settings menu.")
    public String activeCloudStorageProvider = "googledrive";

    @AutoGen(category = "worldback_machine")
    @CustomName("Enable Auto Backups")
    @CustomDescription("If enabled, the mod is allowed to automatically create and upload backups of the world on a configurable interval.")
    @Boolean(formatter = Boolean.Formatter.ON_OFF, colored = true)
    @SerialEntry(comment = "If the mod is allowed to automatically create and upload backups of the world on a configurable interval")
    public boolean areAutoBackupsEnabled = true;

    @AutoGen(category = "worldback_machine")
    @CustomName("Backup Interval")
    @CustomDescription("How often the mod will create and upload backups of the world.")
    @IntSlider(min = 3600, max = 86400, step = 1)
    @CustomFormat(TimeValueFormatter.class)
    @SerialEntry(comment = "How often backups are created and uploaded in seconds")
    public int backupIntervalSeconds = (int) TimeUnit.HOURS.toSeconds(1L);

    /**
     * Writes the current options out to a JSON file.
     */
    public void save()
    {
        HANDLER.save();
    }

    /**
     * Generates and gets an instance of a {@link Screen screen} using YetAnotherConfigLib's AutoGen API.
     *
     * @return An instance of a YACL options {@link Screen}
     */
    public Screen getOptionsScreen(Screen parentScreen)
    {
        return HANDLER.generateGui().generateScreen(parentScreen);
    }

    public record TimeValueFormatter(int value) implements ValueFormatter<Integer>
    {
        public TimeValueFormatter()
        {
            this(60);
        }

        @Override
        public Text format(Integer value)
        {
            // Convert seconds to a human-readable format
            long hours = value / 3600;
            long minutes = (value % 3600) / 60;
            long seconds = value % 60;

            StringBuilder formattedString = new StringBuilder();

            if (hours > 0)
                formattedString.append(hours).append(" Hours ");

            if (minutes > 0)
                formattedString.append(minutes).append(" Minutes ");

            formattedString.append(seconds).append(" Seconds");

            return Text.of(formattedString.toString().trim());
        }
    }
}
