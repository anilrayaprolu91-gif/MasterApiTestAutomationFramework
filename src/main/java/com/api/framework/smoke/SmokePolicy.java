package com.api.framework.smoke;

import com.api.framework.openapi.ApiEndpoint;

public interface SmokePolicy {
    boolean shouldRun(ApiEndpoint endpoint);
}
