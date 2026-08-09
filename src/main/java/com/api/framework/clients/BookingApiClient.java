package com.api.framework.clients;

import com.api.framework.models.request.BookingRequest;
import com.api.framework.models.request.PartialBookingUpdateRequest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.function.Supplier;

import static io.restassured.RestAssured.given;

/**
 * API client for the Restful Booker booking resources.
 */
public class BookingApiClient extends BaseApiClient {

    private static final String BOOKINGS_ENDPOINT = "/booking";
    private static final String PING_ENDPOINT = "/ping";
    private static final int HTTP_TEAPOT = 418;
    private static final int MAX_WRITE_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1_500L;

    public BookingApiClient() {
        super("base.uri.restfulbooker");
    }

    @Step("GET Restful Booker ping")
    public Response ping() {
        logger.info("Calling Restful Booker ping endpoint");
        return given(getBaseSpec())
                .accept(ContentType.TEXT)
                .when()
                .get(PING_ENDPOINT)
                .then()
                .extract().response();
    }

    @Step("GET booking ids by firstname='{firstName}' and lastname='{lastName}'")
    public Response findBookingsByName(String firstName, String lastName) {
        logger.info("Searching bookings for firstname='{}', lastname='{}'", firstName, lastName);
        return given(getBaseSpec())
                .accept(ContentType.JSON)
                .queryParam("firstname", firstName)
                .queryParam("lastname", lastName)
                .when()
                .get(BOOKINGS_ENDPOINT)
                .then()
                .extract().response();
    }

    @Step("GET booking by id={bookingId}")
    public Response getBookingById(int bookingId) {
        logger.info("Fetching booking id={}", bookingId);
        return given(getBaseSpec())
                .accept(ContentType.JSON)
                .when()
                .get(BOOKINGS_ENDPOINT + "/{bookingId}", bookingId)
                .then()
                .extract().response();
    }

    @Step("POST create booking for guest '{request.firstname} {request.lastname}'")
    public Response createBooking(BookingRequest request) {
        logger.info("Creating booking for guest='{} {}'", request.getFirstname(), request.getLastname());
        return executeWriteRequestWithRetry("create booking", () -> given(getBaseSpec())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(request)
                .when()
                .post(BOOKINGS_ENDPOINT)
                .then()
                .extract().response());
    }

    @Step("PUT update booking id={bookingId}")
    public Response updateBooking(int bookingId, BookingRequest request, String token) {
        logger.info("Fully updating booking id={} for guest='{} {}'", bookingId, request.getFirstname(), request.getLastname());
        return executeWriteRequestWithRetry("update booking", () -> given(getBaseSpec())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Cookie", "token=" + token)
                .body(request)
                .when()
                .put(BOOKINGS_ENDPOINT + "/{bookingId}", bookingId)
                .then()
                .extract().response());
    }

    @Step("PATCH booking id={bookingId}")
    public Response partiallyUpdateBooking(int bookingId, PartialBookingUpdateRequest request, String token) {
        logger.info("Partially updating booking id={}", bookingId);
        return executeWriteRequestWithRetry("patch booking", () -> given(getBaseSpec())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("Cookie", "token=" + token)
                .body(request)
                .when()
                .patch(BOOKINGS_ENDPOINT + "/{bookingId}", bookingId)
                .then()
                .extract().response());
    }

    @Step("DELETE booking id={bookingId}")
    public Response deleteBooking(int bookingId, String token) {
        logger.info("Deleting booking id={}", bookingId);
        return executeWriteRequestWithRetry("delete booking", () -> given(getBaseSpec())
                .header("Cookie", "token=" + token)
                .when()
                .delete(BOOKINGS_ENDPOINT + "/{bookingId}", bookingId)
                .then()
                .extract().response());
    }

    private Response executeWriteRequestWithRetry(String operationName, Supplier<Response> requestExecution) {
        Response response = null;

        for (int attempt = 1; attempt <= MAX_WRITE_RETRIES; attempt++) {
            response = requestExecution.get();
            if (response.getStatusCode() != HTTP_TEAPOT || attempt == MAX_WRITE_RETRIES) {
                return response;
            }

            long delay = RETRY_DELAY_MS * attempt;
            logger.warn("Restful Booker returned HTTP 418 for '{}' on attempt {}/{}. Retrying in {} ms.",
                    operationName, attempt, MAX_WRITE_RETRIES, delay);
            pause(delay);
        }

        return response;
    }

    private void pause(long delayInMillis) {
        try {
            Thread.sleep(delayInMillis);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Retry wait interrupted while calling Restful Booker.", exception);
        }
    }
}
