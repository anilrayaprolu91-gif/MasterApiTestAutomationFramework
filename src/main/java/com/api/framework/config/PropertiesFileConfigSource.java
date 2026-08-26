package com.api.framework.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFileConfigSource implements ConfigSource {

    private static final String CONFIG_FILE = "config.properties";

    private final Properties properties;

    public PropertiesFileConfigSource() {
        this.properties = loadProperties();
    }

    @Override
    public String get(String key) {

        String value = properties.getProperty(key);

        if (value == null || value.isBlank()) {
            return null;
        }

        return value;
    }

    private Properties loadProperties() {

        Properties props = new Properties();

        try (InputStream inputStream =
                     getClass()
                             .getClassLoader()
                             .getResourceAsStream(CONFIG_FILE)) {

            if (inputStream == null) {
                throw new IllegalStateException(
                        "Configuration file '" + CONFIG_FILE +
                                "' not found on the classpath."
                );
            }

            props.load(inputStream);

            return props;

        } catch (IOException e) {

            throw new IllegalStateException(
                    "Failed to load configuration file: "
                            + CONFIG_FILE,
                    e
            );
        }
    }
}