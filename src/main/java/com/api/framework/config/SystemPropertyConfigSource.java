package com.api.framework.config;

public class SystemPropertyConfigSource implements ConfigSource {

    @Override
    public String get(String key) {

        String value = System.getProperty(key);

        if (value == null || value.isBlank()) {
            return null;
        }

        return value;
    }
}