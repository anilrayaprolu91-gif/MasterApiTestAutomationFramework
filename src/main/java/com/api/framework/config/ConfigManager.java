package com.api.framework.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ConfigManager {

    private static final Logger logger =
            LogManager.getLogger(ConfigManager.class);

    private static volatile ConfigManager instance;

    private final ConfigSource systemPropertySource;
    private final ConfigSource environmentSource;
    private final ConfigSource propertiesFileSource;

    private ConfigManager() {

        this.systemPropertySource =
                new SystemPropertyConfigSource();

        this.environmentSource =
                new EnvironmentConfigSource();

        this.propertiesFileSource =
                new PropertiesFileConfigSource();

        logger.info("Configuration sources initialised");
    }

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

    public String getProperty(String key) {

        String value =
                systemPropertySource.get(key);

        if (value != null) {
            return value;
        }

        value =
                environmentSource.get(key);

        if (value != null) {
            return value;
        }

        value =
                propertiesFileSource.get(key);

        if (value != null) {
            return value;
        }

        throw new IllegalStateException(
                "Required configuration key '" + key +
                        "' is not defined."
        );
    }

    public String getProperty(
            String key,
            String defaultValue) {

        try {
            return getProperty(key);

        } catch (IllegalStateException e) {

            logger.debug(
                    "Key '{}' not found, using default '{}'",
                    key,
                    defaultValue
            );

            return defaultValue;
        }
    }

    public int getIntProperty(String key) {

        String value = getProperty(key);

        try {

            return Integer.parseInt(value.trim());

        } catch (NumberFormatException e) {

            throw new IllegalStateException(
                    "Configuration key '" + key +
                            "' must be a valid integer, but was: '"
                            + value + "'.",
                    e
            );
        }
    }
}