package com.api.framework.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Create-booking response payload from Restful Booker.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingCreationResponse {

    @JsonProperty("bookingid")
    private int bookingid;

    @JsonProperty("booking")
    private BookingResponse booking;
}

