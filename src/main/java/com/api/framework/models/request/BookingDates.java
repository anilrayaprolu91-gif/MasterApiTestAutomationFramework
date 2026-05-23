package com.api.framework.models.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Nested booking date window used by Restful Booker create/update payloads.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDates {

    @JsonProperty("checkin")
    private String checkin;

    @JsonProperty("checkout")
    private String checkout;
}

