package com.ayydxn.worldbackmachine.google;

import com.google.api.services.drive.Drive;

public class GoogleDriveAPI
{
    private final Drive driveClientService;

    private GoogleDriveAPI(Drive driveClientService)
    {
        this.driveClientService = driveClientService;
    }

    /**
     * Creates an instance of this class.
     *
     * @param driveClientService An authorized Google Drive API client service.
     * @return An instance of this class.
     */
    public static GoogleDriveAPI create(Drive driveClientService)
    {
        return new GoogleDriveAPI(driveClientService);
    }
}
