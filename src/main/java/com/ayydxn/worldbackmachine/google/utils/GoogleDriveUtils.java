package com.ayydxn.worldbackmachine.google.utils;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

public class GoogleDriveUtils
{
    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @param jsonFactory The JSON factory used to load the file containing all required API credentials.
     * @param scopes A list of scopes that will be used to interact with certain parts of the API.
     * @return An authorized Credential object.
     * @throws java.io.IOException If the credentials.json file cannot be found.
     */
    public static Credential getCredentials(NetHttpTransport HTTP_TRANSPORT, JsonFactory jsonFactory, List<String> scopes) throws IOException
    {
        // Load client secrets.
        InputStream credentialsFileInputStream = Files.newInputStream(WorldbackMachineConstants.CREDENTIALS_FILE);
        if (credentialsFileInputStream == null)
            throw new FileNotFoundException(String.format("Failed to locate credentials file at '%s'", WorldbackMachineConstants.CREDENTIALS_FILE));

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(credentialsFileInputStream));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(new FileDataStoreFactory(WorldbackMachineConstants.TOKENS_DIRECTORY.toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .build();

        //returns an authorized Credential object.
        return new AuthorizationCodeInstalledApp(flow, receiver, url -> Util.getOperatingSystem().open(url))
                .authorize("user");
    }
}
