package com.api.framework.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response POJO for the POST {@code /api/users} endpoint on Reqres.in.
 *
 * <p>{@code @JsonIgnoreProperties(ignoreUnknown = true)} protects the test suite
 * from breaking if the API adds new fields in a future version.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateUserResponse {

    @JsonProperty("name")
    private String name;

    @JsonProperty("job")
    private String job;

    /** Auto-generated server-side identifier returned in the creation response. */
    @JsonProperty("id")
    private String id;

    /** ISO-8601 timestamp of when the resource was created. */
    @JsonProperty("createdAt")
    private String createdAt;
}

