package com.api.framework.tests;

import com.api.framework.base.BaseTest;
import com.api.framework.clients.BookingApiClient;
import com.api.framework.models.request.BookingDates;
import com.api.framework.models.request.BookingRequest;
import com.api.framework.models.request.PartialBookingUpdateRequest;
import com.api.framework.models.response.BookingCreationResponse;
import com.api.framework.models.response.BookingId;
import com.api.framework.models.response.BookingResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchema;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-end CRUD flow for Restful Booker.
 */
@Epic("Restful Booker API")
@Feature("End-to-End Booking Lifecycle")
@Story("Create, Read, Update, Patch, Delete")
@Owner("API Automation Team")
@Test(singleThreaded = true)
public class RestfulBookerE2ETest extends BaseTest {

    private static final File BOOKING_RESPONSE_SCHEMA =
            new File("src/test/resources/schemas/restfulbooker-booking-response-schema.json");
    private static final File BOOKING_IDS_RESPONSE_SCHEMA =
            new File("src/test/resources/schemas/restfulbooker-booking-ids-response-schema.json");

    private BookingApiClient bookingApiClient;

    @BeforeClass
    public void setUp() {
        bookingApiClient = new BookingApiClient();
        assertThat(RestfulBookerApiTest.sharedBookingCreationResponse)
                .as("RestfulBookerApiTest must create the shared booking before the E2E suite starts")
                .isNotNull();
    }

    @Test(description = "Full booking lifecycle should succeed end-to-end on Restful Booker")
    @Severity(SeverityLevel.BLOCKER)
    @Description("""
            Executes a realistic booking lifecycle against the public Restful Booker service:
            1) search it by name,
            2) fetch it by id,
            3) fully update it,
            4) partially update it,
            5) delete it,
            6) verify it is no longer retrievable.
            """)
    public void bookingLifecycle_shouldPassEndToEnd() {
        String token = RestfulBookerApiTest.authToken;
        BookingRequest originalRequest = RestfulBookerApiTest.sharedBookingRequest;
        int bookingId = RestfulBookerApiTest.sharedBookingCreationResponse.getBookingid();

        Response createResponse = bookingApiClient.getBookingById(bookingId);
        assertThat(createResponse.getStatusCode())
                .as("Shared booking should be retrievable before the lifecycle updates")
                .isEqualTo(HttpStatus.SC_OK);
        createResponse.then().assertThat().body(matchesJsonSchema(BOOKING_RESPONSE_SCHEMA));
        assertBookingMatchesRequest(createResponse.as(BookingResponse.class), originalRequest);

        Response searchResponse = bookingApiClient.findBookingsByName(
                originalRequest.getFirstname(),
                originalRequest.getLastname()
        );
        assertThat(searchResponse.getStatusCode())
                .as("Booking search should return HTTP 200")
                .isEqualTo(HttpStatus.SC_OK);
        searchResponse.then().assertThat().body(matchesJsonSchema(BOOKING_IDS_RESPONSE_SCHEMA));
        List<BookingId> bookingIds = searchResponse.jsonPath().getList("", BookingId.class);
        assertThat(bookingIds)
                .extracting(BookingId::getBookingid)
                .as("Booking search should include the newly created booking id")
                .contains(bookingId);

        Response getResponse = bookingApiClient.getBookingById(bookingId);
        assertThat(getResponse.getStatusCode())
                .as("GET /booking/{id} should return HTTP 200 for the created booking")
                .isEqualTo(HttpStatus.SC_OK);
        getResponse.then().assertThat().body(matchesJsonSchema(BOOKING_RESPONSE_SCHEMA));
        BookingResponse retrievedBooking = getResponse.as(BookingResponse.class);
        assertBookingMatchesRequest(retrievedBooking, originalRequest);

        BookingRequest fullUpdateRequest = BookingRequest.builder()
                .firstname(originalRequest.getFirstname())
                .lastname(originalRequest.getLastname() + "-Updated")
                .totalprice(450)
                .depositpaid(false)
                .bookingdates(BookingDates.builder()
                        .checkin(originalRequest.getBookingdates().getCheckin())
                        .checkout(LocalDate.parse(originalRequest.getBookingdates().getCheckout()).plusDays(2).toString())
                        .build())
                .additionalneeds("Late Checkout")
                .build();

        Response updateResponse = bookingApiClient.updateBooking(bookingId, fullUpdateRequest, token);
        assertThat(updateResponse.getStatusCode())
                .as("PUT /booking/{id} should return HTTP 200")
                .isEqualTo(HttpStatus.SC_OK);
        updateResponse.then().assertThat().body(matchesJsonSchema(BOOKING_RESPONSE_SCHEMA));
        BookingResponse updatedBooking = updateResponse.as(BookingResponse.class);
        assertBookingMatchesRequest(updatedBooking, fullUpdateRequest);

        PartialBookingUpdateRequest partialUpdateRequest = PartialBookingUpdateRequest.builder()
                .firstname(fullUpdateRequest.getFirstname() + "-Patched")
                .additionalneeds("Dinner")
                .build();

        Response patchResponse = bookingApiClient.partiallyUpdateBooking(bookingId, partialUpdateRequest, token);
        assertThat(patchResponse.getStatusCode())
                .as("PATCH /booking/{id} should return HTTP 200")
                .isEqualTo(HttpStatus.SC_OK);
        patchResponse.then().assertThat().body(matchesJsonSchema(BOOKING_RESPONSE_SCHEMA));

        BookingResponse patchedBooking = patchResponse.as(BookingResponse.class);
        assertThat(patchedBooking.getFirstname())
                .as("Firstname should be updated by the PATCH call")
                .isEqualTo(partialUpdateRequest.getFirstname());
        assertThat(patchedBooking.getAdditionalneeds())
                .as("Additional needs should be updated by the PATCH call")
                .isEqualTo(partialUpdateRequest.getAdditionalneeds());
        assertThat(patchedBooking.getLastname())
                .as("Lastname should remain unchanged after PATCH")
                .isEqualTo(fullUpdateRequest.getLastname());
        assertThat(patchedBooking.getTotalprice())
                .as("Total price should remain unchanged after PATCH")
                .isEqualTo(fullUpdateRequest.getTotalprice());

        Response deleteResponse = bookingApiClient.deleteBooking(bookingId, token);
        assertThat(deleteResponse.getStatusCode())
                .as("DELETE /booking/{id} should return the documented success status")
                .isEqualTo(HttpStatus.SC_CREATED);

        Response getDeletedBookingResponse = bookingApiClient.getBookingById(bookingId);
        assertThat(getDeletedBookingResponse.getStatusCode())
                .as("Deleted booking should no longer be retrievable")
                .isEqualTo(HttpStatus.SC_NOT_FOUND);
    }


    private void assertBookingMatchesRequest(BookingResponse actual, BookingRequest expected) {
        assertThat(actual.getFirstname()).isEqualTo(expected.getFirstname());
        assertThat(actual.getLastname()).isEqualTo(expected.getLastname());
        assertThat(actual.getTotalprice()).isEqualTo(expected.getTotalprice());
        assertThat(actual.isDepositpaid()).isEqualTo(expected.isDepositpaid());
        assertThat(actual.getBookingdates().getCheckin()).isEqualTo(expected.getBookingdates().getCheckin());
        assertThat(actual.getBookingdates().getCheckout()).isEqualTo(expected.getBookingdates().getCheckout());
        assertThat(actual.getAdditionalneeds()).isEqualTo(expected.getAdditionalneeds());
    }
}

