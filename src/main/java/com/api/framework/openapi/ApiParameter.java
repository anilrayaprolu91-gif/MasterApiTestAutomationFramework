package com.api.framework.openapi;

public record ApiParameter(
        String name,
        String location,
        boolean required,
        String type) {
}
