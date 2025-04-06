package com.ayydxn.worldbackmachine.google;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.google.utils.GoogleDriveUtils;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class GoogleDriveBootstrap
{
    private static final List<String> SCOPES = Lists.newArrayList(DriveScopes.DRIVE_FILE);

    private static boolean hasPerformedBootstrapping = false;

    /**
     * Creates an instance of the Google Drive API wrapper. This function can only be called once.
     *
     * @return An instance of {@link GoogleDriveAPI} or null, if function as already been called before.
     */
    public static @Nullable GoogleDriveAPI bootstrap() throws IOException, GeneralSecurityException
    {
        if (hasPerformedBootstrapping)
        {
            WorldbackMachineMod.getLogger().warn("Cannot bootstrap the Google Drive API multiple times!");
            return null;
        }

        WorldbackMachineMod.getLogger().info("Bootstrapping Google Drive API...");

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        Drive driveClientService = new Drive.Builder(httpTransport, jsonFactory, GoogleDriveUtils.getCredentials(httpTransport, jsonFactory, SCOPES))
                .setApplicationName("Worldback Machine")
                .build();

        hasPerformedBootstrapping = true;

        return GoogleDriveAPI.create(driveClientService);
    }
}
