package com.api.framework.bootstrap;

import com.api.framework.openapi.DefaultApiRiskClassifier;
import com.api.framework.openapi.OpenApiAnalyzer;
import com.api.framework.openapi.OpenApiReader;
import com.api.framework.smoke.SafeSmokePolicy;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.nio.file.Path;

public class ApiBootstrapperTest {
    @Test
    public void shouldDiscoverEndpointsAndSelectSafeSmokeCandidates() {
        Path specification = Path.of("src/test/resources/api-bootstrap/openapi/customer-service.yaml");

        ApiBootstrapper bootstrapper = new ApiBootstrapper(
                new OpenApiReader(),
                new OpenApiAnalyzer(new DefaultApiRiskClassifier()),
                new SafeSmokePolicy());

        BootstrapResult result = bootstrapper.bootstrap(
                new BootstrapConfiguration("customer-service", specification));

        Assertions.assertThat(result.endpoints()).hasSize(4);
        Assertions.assertThat(result.smokeCandidates())
                .extracting(endpoint -> endpoint.operationId())
                .containsExactlyInAnyOrder("listCustomers", "getCustomer");
        Assertions.assertThat(result.endpoints())
                .anyMatch(endpoint -> endpoint.operationId().equals("deleteCustomer")
                        && endpoint.risk().name().equals("DESTRUCTIVE"));
    }
}
