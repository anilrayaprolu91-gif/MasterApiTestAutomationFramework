package com.api.framework.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body POJO for the POST {@code /api/users} endpoint on Reqres.in.
 *
 * <p>Lombok's {@code @Builder} provides a clean, readable construction pattern
 * in tests, while {@code @Data} generates equals/hashCode/toString automatically.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("job")
    private String job;
}

