package com.api.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Thread-safe Singleton configuration manager.
 *
 * <p>Loads properties from {@code src/test/resources/config.properties} once on first access,
 * then serves all subsequent reads from an in-memory cache. Environment variables always
 * take precedence over file values, which makes CI/CD overrides trivial.
 *
 * <p>Usage:
 * <pre>{@code
 *   String baseUri = ConfigManager.getInstance().getProperty("base.uri.reqres");
 * }</pre>
 */
public final class ConfigManager {

    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final String CONFIG_FILE = "config.properties";

    // Volatile guarantees visibility across threads under double-checked locking.
    private static volatile ConfigManager instance;
    private final Properties properties;

    // Private constructor — only called once.
    private ConfigManager() {
        properties = loadProperties();
    }

    /**
     * Returns the single shared instance, creating it lazily on first access.
     */
    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    /**
     * Retrieves a configuration value.
     *
     * <p>Resolution order: system property → environment variable → properties file.
     *
     * @param key the property key
     * @return the resolved value
     * @throws IllegalStateException if the key is missing from all sources
     */
    public String getProperty(String key) {
        // System properties set via -Dkey=value on the Maven command line win first.
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }

        // Environment variables (useful for secrets in CI/CD pipelines).
        String envValue = System.getenv(key.replace(".", "_").toUpperCase());
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }

        // Fall back to the bundled properties file.
        String propValue = properties.getProperty(key);
        if (propValue != null && !propValue.isBlank()) {
            return propValue;
        }

        throw new IllegalStateException(
                "Required configuration key '" + key + "' is not defined in any source "
                        + "(system property, environment variable, or " + CONFIG_FILE + ")."
        );
    }

    /**
     * Convenience overload that returns a default value instead of throwing.
     */
    public String getProperty(String key, String defaultValue) {
        try {
            return getProperty(key);
        } catch (IllegalStateException e) {
            logger.debug("Key '{}' not found, using default: '{}'", key, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Returns an integer property, throwing if the value cannot be parsed.
     */
    public int getIntProperty(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    "Configuration key '" + key + "' must be a valid integer, but was: '" + value + "'.", e
            );
        }
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException(
                        "Configuration file '" + CONFIG_FILE + "' not found on the classpath. "
                                + "Expected location: src/test/resources/" + CONFIG_FILE
                );
            }
            props.load(in);
            logger.info("Configuration loaded from classpath:{}", CONFIG_FILE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load configuration file: " + CONFIG_FILE, e);
        }
        return props;
    }
}

