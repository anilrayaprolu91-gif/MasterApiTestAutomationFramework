package com.api.framework.config;

public class EnvironmentConfigSource implements ConfigSource {

    @Override
    public String get(String key) {

        String environmentKey =
                key.replace(".", "_").toUpperCase();

        String value = System.getenv(environmentKey);

        if (value == null || value.isBlank()) {
            return null;
        }

        return value;
    }
}