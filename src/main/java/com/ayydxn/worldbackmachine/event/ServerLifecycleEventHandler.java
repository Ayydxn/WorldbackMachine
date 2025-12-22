package com.ayydxn.worldbackmachine.event;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.cloud.AutomaticBackupScheduler;
import com.ayydxn.worldbackmachine.cloud.CloudStorageManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import org.jspecify.annotations.NonNull;

public class ServerLifecycleEventHandler implements ServerLifecycleEvents.ServerStarted, ServerLifecycleEvents.ServerStopping
{
    private final CloudStorageManager cloudStorageManager;
    private final AutomaticBackupScheduler automaticBackupScheduler;

    public ServerLifecycleEventHandler(CloudStorageManager cloudStorageManager, AutomaticBackupScheduler automaticBackupScheduler)
    {
        this.cloudStorageManager = cloudStorageManager;
        this.automaticBackupScheduler = automaticBackupScheduler;
    }

    @Override
    public void onServerStarted(@NonNull MinecraftServer server)
    {
        WorldbackMachineMod.LOGGER.info("The server has started. Attempting to begin automatic worlds backups...");

        this.automaticBackupScheduler.startAutomaticBackups(server);
    }

    @Override
    public void onServerStopping(@NonNull MinecraftServer server)
    {
        WorldbackMachineMod.LOGGER.info("Performing one last backup as the server stops...");

        this.automaticBackupScheduler.stopAutomaticBackups();
        this.cloudStorageManager.uploadWorld(server);
    }
}
