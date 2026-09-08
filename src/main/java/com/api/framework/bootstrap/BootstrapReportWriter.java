package com.api.framework.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class BootstrapReportWriter {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    public Path write(BootstrapResult result, Path outputFile) {
        try {
            if (outputFile.getParent() != null) {
                Files.createDirectories(outputFile.getParent());
            }
            Map<String, Object> report = new LinkedHashMap<>();
            report.put("service", result.service());
            report.put("totalEndpoints", result.endpoints().size());
            report.put("smokeCandidates", result.smokeCandidates().size());
            report.put("endpoints", result.endpoints());
            objectMapper.writeValue(outputFile.toFile(), report);
            return outputFile;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to write API bootstrap report: " + outputFile, e);
        }
    }
}
