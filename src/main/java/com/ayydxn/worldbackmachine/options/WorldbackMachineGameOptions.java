package com.ayydxn.worldbackmachine.options;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.cloud.CloudStorageManager;
import com.google.gson.FieldNamingPolicy;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.fabricmc.loader.api.FabricLoader;
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

    @SerialEntry(comment = "The internal of the currently selected cloud storage provider. It's recommend to only change this via the in-game settings menu since other mods can register their own providers.")
    public String cloudStorageProvider = "google_drive";

    @SerialEntry(comment = "If the mod is allowed to automatically create and upload backups of the world on a configurable interval")
    public boolean areAutoBackupsEnabled = true;

    @SerialEntry(comment = "How often backups are created and uploaded in seconds")
    public int backupIntervalSeconds = (int) TimeUnit.HOURS.toSeconds(1L);

    /**
     * Writes the current options out to a JSON file.
     */
    public void save()
    {
        HANDLER.save();
    }
}
