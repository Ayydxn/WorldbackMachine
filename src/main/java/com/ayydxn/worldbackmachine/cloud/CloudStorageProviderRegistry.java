package com.ayydxn.worldbackmachine.cloud;

import com.ayydxn.worldbackmachine.WorldbackMachineMod;
import com.ayydxn.worldbackmachine.api.WorldbackMachineApi;
import com.google.common.collect.Maps;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * The registry for all cloud storage providers that are available.
 * <p>
 * This class manages the registration and retrieval of cloud storage providers that can be used by the mod.
 * These providers can either be registered by the mod itself, or by external mods through the {@link WorldbackMachineApi} entrypoint.
 * <p>
 * Provider names must be unique and are case-insensitive.
 * When registering a provider, the name will be converted to lowercase automatically.
 *
 * <h2>Usage Example:</h2>
 * <pre>{@code
 * // Register a provider
 * registry.register("dropbox", new DropboxStorageProvider());
 *
 * // Check if provider exists
 * if (registry.isRegistered("dropbox")) {
 *     CloudStorageProvider provider = registry.get("dropbox");
 * }
 *
 * // Get all provider names
 * Set<String> providers = registry.getRegisteredStorageProviderNames();
 * }</pre>
 *
 * @author Ayydxn
 * @see WorldbackMachineApi
 * @see CloudStorageProvider
 */
public class CloudStorageProviderRegistry
{
    // Name of cloud provider -> Instance of the cloud provider
    private final Map<String, CloudStorageProvider> cloudStorageProviders;

    // Name of cloud provider -> Mod ID of the mod that they were registered from
    private final Map<String, String> cloudStorageProviderSourceMods;

    // Whether the registry is accepting new providers
    private boolean isLocked = false;

    public CloudStorageProviderRegistry()
    {
        this.cloudStorageProviders = Maps.newHashMap();
        this.cloudStorageProviderSourceMods = Maps.newHashMap();
    }

    /**
     * Registers a cloud storage provider with source tracking.
     * <p>
     * Provider names are converted to lowercase for consistency.
     * Registering providers is only allowed while the registry isn't locked.
     *
     * @param name     The unique name for this storage provider
     * @param instance An instance of the provider's implementation
     * @param modID    The ID of the mod registering this provider
     *
     * @throws IllegalArgumentException If the name is null, empty, or already registered
     * @throws IllegalStateException    If registry is locked
     * @return The current instance of the registry for method chaining
     */
    public CloudStorageProviderRegistry register(String name, CloudStorageProvider instance, String modID)
    {
        if (this.isLocked)
            throw new IllegalStateException("The registry is locked and not accepting new storage providers! This must be done during mod initialization.");

        if (name == null || name.trim().isBlank())
            throw new IllegalArgumentException("Storage provider names cannot be empty or null!");

        if (instance == null)
            throw new IllegalArgumentException("Storage provider cannot be registered with a null instance!");

        String normalizedProviderName = name.toLowerCase()
                .replace(" ", "_")
                .trim();

        if (this.cloudStorageProviders.containsKey(normalizedProviderName))
        {
            String providerSourceMod = this.cloudStorageProviderSourceMods.get(normalizedProviderName);

            throw new IllegalArgumentException(String.format("Provider '%s' was already registered by mod '%s'!", normalizedProviderName, providerSourceMod));
        }

        this.cloudStorageProviders.put(normalizedProviderName, instance);
        this.cloudStorageProviderSourceMods.put(normalizedProviderName, modID);

        WorldbackMachineMod.LOGGER.info("Mod '{}' has registered cloud provider '{}'", modID, normalizedProviderName);

        return this;
    }

    /**
     * Locks the registry, preventing further registrations.
     * <p>
     * This is called automatically after mod initialization has occurred.
     * After locking, any attempts to register providers will throw an exception.
     */
    public void lock()
    {
        if (!this.isLocked)
        {
            this.isLocked = true;

            WorldbackMachineMod.LOGGER.info("The cloud storage provider has been locked with {} registered {}:",
                    this.cloudStorageProviders.size(), this.cloudStorageProviders.size() == 1 ? "provider" : "providers");

            for (CloudStorageProvider cloudStorageProvider : this.cloudStorageProviders.values())
                WorldbackMachineMod.LOGGER.info("- {}", cloudStorageProvider.getProviderName());
        }
    }

    /**
     * Checks if a provider is registered.
     *
     * @param name the provider name (case-insensitive)
     * @return true if registered, false otherwise
     */
    public boolean isRegistered(String name)
    {
        if (name == null)
            return false;

        return this.cloudStorageProviders.containsKey(name.toLowerCase().replace(" ", "_").trim());
    }

    /**
     * Gets the instance of a registered storage provider of the given name.
     *
     * @param name The storage provider's name (case-insensitive)
     * @return The instance of the provider, or null if it's not registered
     */
    @Nullable
    public CloudStorageProvider getProviderInstance(String name)
    {
        if (name == null)
            return null;

        return this.cloudStorageProviders.get(name.toLowerCase().replace(" ", "_").trim());
    }

    /**
     * Gets an unmodifiable view of all providers.
     *
     * @return An unmodifiable map of provider names to their instances
     */
    public Map<String, CloudStorageProvider> getAllProviders()
    {
        return Collections.unmodifiableMap(this.cloudStorageProviders);
    }

    /**
     * Gets all registered provider names.
     *
     * @return An unmodifiable set of cloud storage provider names
     */
    public Set<String> getRegisteredProviderNames()
    {
        return Collections.unmodifiableSet(this.cloudStorageProviders.keySet());
    }

    /**
     * Gets the source mod ID for a provider.
     *
     * @param name the provider name (case-insensitive)
     * @return The mod ID of the mod that registered this provider, or null if not found
     */
    public String getProviderSourceMod(String name)
    {
        if (name == null)
            return null;

        return this.cloudStorageProviderSourceMods.get(name.toLowerCase().replace(" ", "_").trim());
    }

    /**
     * Gets the number of registered providers.
     *
     * @return The number of registered providers
     */
    public int getProviderCount()
    {
        return this.cloudStorageProviders.size();
    }

    /**
     * Checks if the registry is locked.
     *
     * @return True if the registry is locked, false otherwise
     */
    public boolean isLocked()
    {
        return this.isLocked;
    }
}
