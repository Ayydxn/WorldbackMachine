package com.ayydxn.worldbackmachine.cloud.google;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.utils.WorldbackMachineConstants;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import net.minecraft.util.Util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A class which contains some simple utility functions for when interacting with the Google Drive API.
 *
 * @author Ayydxn
 */
public class GoogleDriveUtils
{
    /**
     * Creates an authorized Credential object.
     *
     * @param httpTransport The network HTTP Transport.
     * @param jsonFactory The JSON factory used to load the file containing all required API credentials.
     * @param credentialsFile The path to the file containing the Google API authentication credentials
     * @param scopes A list of scopes that will be used to interact with certain parts of the API.
     * @throws java.io.IOException If the credentials.json file cannot be found.
     * @return An authorized Credential object.
     */
    public static Credential getCredentials(NetHttpTransport httpTransport, JsonFactory jsonFactory, Path credentialsFile, List<String> scopes) throws IOException
    {
        try (InputStream credentialsFileInputStream = Files.newInputStream(credentialsFile))
        {
            // Load client secrets.
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(credentialsFileInputStream));

            // Build flow and trigger user authorization request.
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, scopes)
                    .setDataStoreFactory(new FileDataStoreFactory(new File(WorldbackMachineConstants.TOKENS_DIRECTORY + "/google")))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                    .setPort(8888)
                    .build();

            // Return an authorized Credential object.
            return new AuthorizationCodeInstalledApp(flow, receiver, url ->
            {
                WorldbackMachineMod.LOGGER.info("Opening authentication URL in browser... ({})", url);

                Util.getOperatingSystem().open(url);
            }).authorize("user");
        }
        catch (Exception exception)
        {
            throw new FileNotFoundException(String.format("Failed to load Google auth credentials from '%s': %s", credentialsFile, exception.getMessage()));
        }
    }
}
