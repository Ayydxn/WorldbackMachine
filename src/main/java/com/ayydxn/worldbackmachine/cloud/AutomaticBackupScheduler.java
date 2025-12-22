package com.ayydxn.worldbackmachine.cloud;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.options.WorldbackMachineGameOptions;
import net.minecraft.server.MinecraftServer;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Manages the automatic backup scheduling for Minecraft worlds.
 * <p>
 * This class handles periodic, automatic backups of the current world to cloud storage at configurable intervals.
 * It uses a {@link Timer} to schedule backups and ensures they run on the server thread.
 *
 * @see CloudStorageManager
 */
public class AutomaticBackupScheduler
{
    private final CloudStorageManager cloudStorageManager;
    private final WorldbackMachineGameOptions gameOptions;

    private Timer backupTimer;
    private MinecraftServer server;
    private long backupIntervalMillis = TimeUnit.HOURS.toMillis(1L);

    public AutomaticBackupScheduler(CloudStorageManager cloudStorageManager)
    {
        this.cloudStorageManager = cloudStorageManager;
        this.gameOptions = WorldbackMachineMod.getInstance().getGameOptions();

        long backupIntervalSeconds = this.gameOptions.backupIntervalSeconds;
        if (backupIntervalSeconds > 0)
            this.backupIntervalMillis = TimeUnit.SECONDS.toMillis(backupIntervalSeconds);
    }

    /**
     * Starts automatic backup scheduling.
     * <p>
     * Begins the automatic backup timer with the configured interval.
     * If auto-backup is disabled in configuration, this method exits without starting the timer.
     * Any existing timer is canceled first.
     *
     * @param server the Minecraft server instance to back up
     */
    public void startAutomaticBackups(MinecraftServer server)
    {
        this.server = server;

        if (!this.gameOptions.areAutoBackupsEnabled)
        {
            WorldbackMachineMod.LOGGER.info("Cannot start automatic backups are they are disabled");
            return;
        }

        if (this.backupTimer != null)
            this.backupTimer.cancel();

        this.backupTimer = new Timer("WorldbackMachine-AutomaticBackup", true);
        this.backupTimer.scheduleAtFixedRate(new BackupTask(), 0L, this.backupIntervalMillis);

        WorldbackMachineMod.LOGGER.info("Auto-backup scheduled every {} minutes", TimeUnit.MILLISECONDS.toMinutes(this.backupIntervalMillis));
    }

    /**
     * Stops the automatic backup timer.
     * <p>
     * Cancels any running backup timer.
     * After calling this method, no more automatic backups will occur until {@link #startAutomaticBackups(MinecraftServer)} is called again.
     */
    public void stopAutomaticBackups()
    {
        if (this.backupTimer != null)
        {
            this.backupTimer.cancel();
            this.backupTimer = null;
        }
    }

    /**
     * Performs a backup operation.
     * <p>
     * Executes the backup on the server thread to avoid concurrency issues with world access.
     * Logs the backup process and any errors.
     * This method is called automatically by the timer or manually via {@link #triggerManualBackup()}.
     */
    private void performBackup()
    {
        if (this.server == null)
            return;

        try
        {
            WorldbackMachineMod.LOGGER.info("Performing automatic backup...");

            // Execute on server thread to avoid concurrency issues
            server.execute(() ->
            {
                try
                {
                    this.cloudStorageManager.uploadWorld(server);

                    WorldbackMachineMod.LOGGER.info("Automatic backup completed!");
                }
                catch (Exception exception)
                {
                    WorldbackMachineMod.LOGGER.error(exception);
                }
            });
        }
        catch (Exception exception)
        {
            WorldbackMachineMod.LOGGER.error(exception);
        }
    }

    /**
     * Triggers an immediate manual backup.
     * <p>
     * Executes a backup immediately without waiting for the next scheduled interval.
     * This does not affect the automatic backup schedule.
     */
    public void triggerManualBackup()
    {
        if (this.server == null)
        {
            WorldbackMachineMod.LOGGER.warn("A server is not available for backup!");
            return;
        }

        this.performBackup();
    }

    /**
     * Gets the current backup interval in milliseconds.
     *
     * @return the backup interval in milliseconds
     */
    public long getBackupInterval()
    {
        return this.backupIntervalMillis;
    }

    /**
     * Sets a new backup interval and restarts the timer.
     * <p>
     * Updates the backup interval to the specified number of seconds.
     * If auto-backup is enabled, the timer is restarted with the new interval.
     *
     * @param seconds the new backup interval in seconds (must be positive)
     * @throws IllegalArgumentException if seconds is not positive
     */
    public void setBackupInterval(int seconds)
    {
        if (seconds <= 0)
            throw new IllegalArgumentException(String.format("A backup interval cannot be negative! (%d)", seconds));

        this.backupIntervalMillis = TimeUnit.SECONDS.toMillis(seconds);

        this.gameOptions.backupIntervalSeconds = seconds;
        this.gameOptions.save();

        // Restart the timer with the new interval
        if (this.server != null && this.gameOptions.areAutoBackupsEnabled)
        {
            this.stopAutomaticBackups();
            this.startAutomaticBackups(server);
        }

        WorldbackMachineMod.LOGGER.info("Backup interval has been updated to {} seconds", seconds);
    }

    private class BackupTask extends TimerTask
    {
        @Override
        public void run()
        {
            AutomaticBackupScheduler.this.performBackup();
        }
    }
}
