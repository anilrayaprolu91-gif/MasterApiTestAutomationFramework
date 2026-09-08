package com.api.framework.smoke;

import com.api.framework.openapi.ApiEndpoint;
import com.api.framework.openapi.ApiRisk;

public final class SafeSmokePolicy implements SmokePolicy {
    @Override
    public boolean shouldRun(ApiEndpoint endpoint) {
        return endpoint != null
                && endpoint.operationId() != null
                && !endpoint.operationId().isBlank()
                && endpoint.risk() == ApiRisk.SAFE;
    }
}
