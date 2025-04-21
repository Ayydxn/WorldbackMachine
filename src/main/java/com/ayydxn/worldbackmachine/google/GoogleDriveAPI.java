package com.ayydxn.worldbackmachine.google;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.utils.WorldbackMachineConstants;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Optional;

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

    /**
     * Creates a folder with the specified name in the root of the Google Drive.
     *
     * @param folderName The name of the folder.
     * @return The ID of the newly created folder. If a folder of the same name already exists, the ID of that folder is returned instead.
     */
    public Optional<String> createFolder(String folderName)
    {
        String folderID = this.doesFolderExist(folderName);
        if (folderID != null)
            return Optional.of(folderID);

        File folderMetadata = new File()
                .setName(folderName)
                .setMimeType("application/vnd.google-apps.folder");

        try
        {
            File createdFolder = this.driveClientService.files().create(folderMetadata)
                    .setFields("id")
                    .execute();

            return Optional.ofNullable(createdFolder.getId());
        }
        catch (IOException exception)
        {
            WorldbackMachineMod.getLogger().error(exception);
        }

        return Optional.empty();
    }

    /**
     * Checks if a folder of a specified name exists within the root of the Google Drive.
     *
     * @param folderName The name of the folder.
     * @return The ID of the folder if exists, otherwise null.
     */
    public @Nullable String doesFolderExist(String folderName)
    {
        try
        {
            FileList fileList = this.driveClientService.files().list()
                    .setQ(String.format("mimeType='application/vnd.google-apps.folder' and trashed=false and name='%s'", WorldbackMachineConstants.SAVE_FOLDER_NAME))
                    .setSpaces("drive")
                    .setPageToken(null)
                    .execute();

            for (File file : fileList.getFiles())
            {
                if (file.getName().equals(folderName))
                    return file.getId();
            }
        }
        catch (IOException exception)
        {
            WorldbackMachineMod.getLogger().error(exception);
        }

        return null;
    }
}
