package com.api.framework.bootstrap;

import java.nio.file.Path;

public record BootstrapConfiguration(
        String serviceName,
        Path openApiSpecification) {

    public BootstrapConfiguration {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("serviceName must not be blank");
        }
        if (openApiSpecification == null) {
            throw new IllegalArgumentException("openApiSpecification is required");
        }
    }
}
