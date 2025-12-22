package com.ayydxn.worldbackmachine.cloud;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Base definition of a cloud storage provider.
 * <p>
 * This interface defines the functionality that any cloud storage provider must implement
 * in order to fully integrate with the mod and be usable. This includes things such as authentication,
 * file uploading/downloading and other general file management operations.
 *
 * @author Ayydxn
 */
public interface CloudStorageProvider
{
    /**
     * Authenticates with the cloud storage provider's API.
     * <p>
     * Typically, this will the initiate a OAuth2 (or some other) authentication flow that involves signing in via a browser,
     * starting a local server or prompting the user for credentials.
     *
     * @throws IOException If error occurred during the authentication process.
     * @return True if authentication with the API was successful, false otherwise.
     */
    boolean authenticate() throws IOException;

    /**
     * Uploads a {@link File} to cloud storage.
     * <p>
     * Uploads a file to cloud storage with the given name 0into the mod's backup folder. If a file of the same name already exists, it will be overwritten.
     *
     * @param file The local file that will be uploaded
     * @param name The name to upload the file under
     * @throws IOException If the upload fails, or we are not authenticated with the provider's API.
     */
    void uploadFile(File file, String name) throws IOException;

    /**
     * Downloads a file from cloud storage.
     * <p>
     * Downloads a file of the specified name from cloud storage and saves it to the given destination path.
     *
     * @param name The name of the file to download
     * @param destinationPath The local folder in which to save the file
     * @throws IOException If the download fails, the file doesn't exist, or we aren't authenticated with the provider's API.
     */
    void downloadFile(String name, File destinationPath) throws IOException;

    /**
     * Deletes a file from cloud storage.
     *
     * @param name The name of the file to delete
     * @throws IOException If deleting the file fails, or we aren't authenticated with the provider's API.
     */
    void deleteFile(String name) throws IOException;

    /**
     * Gives a list of all files in cloud storage.
     * <p>
     * Returns a list of all the names of the files stored in the mod's backup folder.
     *
     * @throws IOException If retrieving the list fails (for whatever reason), or we aren't authenticated with the provider's API.
     * @return A list of the names of the files (It may be empty, but it will never be null)
     */
    @NotNull
    List<String> listFiles() throws IOException;

    /**
     * Signs out of the cloud provider and clears any stored credentials from it.
     * <p>
     * This will clear any cached tokens, credentials, or authentication state.
     * After calling this method, {@link #isAuthenticated()} should return false.
     */
    void signOut();

    /**
     * Returns whether you are currently authenticated with the cloud storage provider's API.
     *
     * @return Ture if you are authenticated, false otherwise.
     */
    boolean isAuthenticated();

    /**
     * Get the human-readable name of this cloud provider.
     *
     * @return The cloud provider's name (e.g., "Google Drive" or "OneDrive")
     */
    String getProviderName();
}
