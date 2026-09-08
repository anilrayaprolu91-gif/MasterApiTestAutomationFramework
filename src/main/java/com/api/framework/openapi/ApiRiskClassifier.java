package com.api.framework.openapi;

public interface ApiRiskClassifier {
    ApiRisk classify(String method);
}
