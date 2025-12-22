package com.ayydxn.worldbackmachine.api;

import com.ayydxn.worldbackmachine.cloud.CloudStorageProvider;
import com.ayydxn.worldbackmachine.cloud.CloudStorageProviderRegistry;

/**
 * Entrypoint interface for interacting with Worldback Machine's APIs.
 * <p>
 * Other mods can implement this interface to interact with the mod in ways such as registering their own cloud storage providers.
 * Implementations should be declared in the mod's fabric.mod.json under the "worldback-machine" entrypoint.
 *
 * <h2>Example Implementation:</h2>
 * <pre>{@code
 * public class MyModInitializer implements WorldbackMachineApi {
 *     public static final String MOD_ID = "example";
 *
 *     @Override
 *     public void registerCloudStorageProviders(CloudStorageProviderRegistry registry) {
 *         registry.register("mycloud", new MyCloudStorageProvider(), "example");
 *         registry.register("anothercloud", new AnotherProvider(), "example");
 *     }
 * }
 * }</pre>
 *
 * <h2>fabric.mod.json Configuration:</h2>
 * <pre>{@code
 * {
 *   "entrypoints": {
 *     "worldback-machine": [
 *       "com.example.MyModInitializer"
 *     ]
 *   },
 *   "depends": {
 *     "worldback-machine": ">=2026.1.0"
 *   }
 * }
 * }</pre>
 *
 * @see CloudStorageProviderRegistry
 * @see CloudStorageProvider
 *
 * @author Ayydxn
 */
@FunctionalInterface
public interface WorldbackMachineApi
{
    void registerCloudStorageProviders(CloudStorageProviderRegistry storageProviderRegistry);
}
