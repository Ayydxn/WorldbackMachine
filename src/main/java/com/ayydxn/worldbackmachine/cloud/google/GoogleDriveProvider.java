package com.ayydxn.worldbackmachine.cloud.google;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.cloud.CloudStorageProvider;
import com.ayydxn.worldbackmachine.utils.WorldbackMachineConstants;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.Lists;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * @author Ayydxn
 */
public class GoogleDriveProvider implements CloudStorageProvider
{
    private final List<String> scopes = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private final Path credentialsFile = FabricLoader.getInstance().getModContainer(WorldbackMachineMod.MOD_ID)
            .map(modContainer -> modContainer.findPath("auth/google/credentials.json").orElseThrow())
            .orElseThrow();

    private Drive driveClientService;
    private String saveFolderID;

    @Override
    public boolean authenticate() throws IOException
    {
        WorldbackMachineMod.LOGGER.info("Authenticating with the Google Drive API...");

        try
        {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            Credential credentials = GoogleDriveUtils.getCredentials(httpTransport, jsonFactory, credentialsFile, this.scopes);

            this.driveClientService = new Drive.Builder(httpTransport, jsonFactory, credentials)
                    .setApplicationName("Worldback Machine")
                    .build();

            this.saveFolderID = this.getOrCreateFolder(WorldbackMachineConstants.SAVE_FOLDER_NAME);

            WorldbackMachineMod.LOGGER.info("Successfully authenticated with the Google Drive API!");

            return true;
        }
        catch (GeneralSecurityException exception)
        {
            WorldbackMachineMod.LOGGER.error(exception);

            return false;
        }
    }

    @Override
    public void uploadFile(java.io.File file, String name) throws IOException
    {
        if (!this.isAuthenticated())
            throw new IOException("Tried to upload a file despite being unauthenticated!");

        String fileID = this.getFileIDByName(name);

        File fileMetadata = new File()
                .setName(name);

        // (Ayydxn) Despite the entire point of the mod, maybe not always it'll be a ZIP file?
        FileContent fileContent = new FileContent("application/zip", file);

        if (fileID != null)
        {
            this.driveClientService.files()
                    .update(fileID, fileMetadata, fileContent)
                    .setAddParents(this.saveFolderID)
                    .execute();
        }
        else
        {
            fileMetadata.setParents(Collections.singletonList(this.saveFolderID));

            this.driveClientService.files()
                    .create(fileMetadata, fileContent)
                    .setFields("id, name")
                    .execute();
        }
    }

    @Override
    public void downloadFile(String name, java.io.File destinationPath) throws IOException
    {
        if (!this.isAuthenticated())
            throw new IOException("Tried to download a file despite being unauthenticated!");

        String fileID = this.getFileIDByName(name);
        if (fileID == null)
            throw new IllegalArgumentException(String.format("Tried to download the file '%s' which doesn't exist!", name));

        try (OutputStream outputStream = new FileOutputStream(destinationPath))
        {
            this.driveClientService.files()
                    .get(fileID)
                    .executeMediaAndDownloadTo(outputStream);
        }
    }

    @Override
    public void deleteFile(String name) throws IOException
    {
        if (!this.isAuthenticated())
            throw new IOException("Tried to delete a file despite being unauthenticated!");

        String fileID = this.getFileIDByName(name);
        if (fileID != null)
        {
            this.driveClientService.files()
                    .delete(fileID)
                    .execute();
        }
    }

    @Override
    public @NotNull List<String> listFiles() throws IOException
    {
        if (!this.isAuthenticated())
            throw new IOException("Tried to list all files despite being unauthenticated!");

        List<String> fileNames = Lists.newArrayList();
        String query = "'" + this.saveFolderID + "' in parents and trashed=false";

        FileList queryResult = this.driveClientService.files()
                .list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();

        List<File> files = queryResult.getFiles();
        if (files != null)
        {
            for (File file : files)
                fileNames.add(file.getName());
        }

        return fileNames;
    }

    @Override
    public void signOut()
    {
        this.driveClientService = null;

        try
        {
            FileUtils.deleteDirectory(new java.io.File(WorldbackMachineConstants.TOKENS_DIRECTORY + "/google"));
        }
        catch (IOException exception)
        {
            WorldbackMachineMod.LOGGER.error("Failed to sign out of Google Drive: {}", exception.getMessage());
        }
    }

    @Override
    public boolean isAuthenticated()
    {
        return this.driveClientService != null;
    }

    @Override
    public String getProviderName()
    {
        return "Google Drive";
    }

    private String getOrCreateFolder(String folderName) throws IOException
    {
        // Check if the folder already exists
        String query = "name='" + folderName +
                "' and mimeType='application/vnd.google-apps.folder'" +
                " and trashed=false";

        FileList result = this.driveClientService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id, name)")
                .execute();

        List<File> files = result.getFiles();
        if (files != null && !files.isEmpty())
            return files.getFirst().getId();

        // Create it since it doesn't already exist
        File folderMetadata = new File();
        folderMetadata.setName(folderName);
        folderMetadata.setMimeType("application/vnd.google-apps.folder");

        File folder = this.driveClientService.files().create(folderMetadata)
                .setFields("id")
                .execute();

        return folder.getId();
    }

    private String getFileIDByName(String name) throws IOException
    {
        String query = "name='" + name + "'" +
                " and '" + this.saveFolderID + "'" +
                " in parents and trashed=false";

        FileList queryResult = this.driveClientService.files().list()
                .setQ(query)
                .setSpaces("drive")
                .setFields("files(id)")
                .execute();

        List<File> files = queryResult.getFiles();

        return (files != null && !files.isEmpty()) ? files.getFirst().getId() : null;
    }
}
