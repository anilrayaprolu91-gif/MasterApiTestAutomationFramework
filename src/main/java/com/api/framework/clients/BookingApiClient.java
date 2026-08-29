package com.api.framework.clients;

import com.api.framework.http.ApiExecutor;
import com.api.framework.http.ApiRequest;
import com.api.framework.http.RequestSpecProvider;
import com.api.framework.http.RestRequestSpecProvider;
import com.api.framework.models.request.BookingRequest;
import com.api.framework.models.request.PartialBookingUpdateRequest;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

import static io.restassured.RestAssured.given;

/**
 * API client for the Restful Booker booking resources.
 */
public class BookingApiClient {

    private static final String BOOKINGS_ENDPOINT = "/booking";
    private static final String PING_ENDPOINT = "/ping";
    private static final String JSON_ACCEPT_HEADER = "application/json";
    private static final int HTTP_TEAPOT = 418;
    private static final int MAX_WRITE_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1_500L;

    //private final RequestSpecProvider restSpec;
    private static final Logger logger = LogManager.getLogger(BookingApiClient.class);

//    public BookingApiClient() {
//        this.restSpec = new RestRequestSpecProvider("base.uri.restfulbooker");
//    }


    private final ApiExecutor apiExecutor;

    public BookingApiClient(ApiExecutor apiExecutor) {
        this.apiExecutor = apiExecutor;
    }

    @Step("GET Restful Booker ping")
    public Response ping() {
        logger.info("Calling Restful Booker ping endpoint");


//        return given(restSpec.get())
//                .accept(ContentType.TEXT)
//                .when()
//                .get(PING_ENDPOINT)
//                .then()
//                .extract().response();
        return apiExecutor.execute(
                ApiRequest.builder(Method.GET, PING_ENDPOINT)
                        .header("Accept", "text/plain")
                        .build()
        );
    }

    @Step("GET booking ids by firstname='{firstName}' and lastname='{lastName}'")
    public Response findBookingsByName(String firstName, String lastName) {
        logger.info("Searching bookings for firstname='{}', lastname='{}'", firstName, lastName);



//        return given(restSpec.get())
//                .header("Accept", JSON_ACCEPT_HEADER)
//                .queryParam("firstname", firstName)
//                .queryParam("lastname", lastName)
//                .when()
//                .get(BOOKINGS_ENDPOINT)
//                .then()
//                .extract().response();

        return apiExecutor.execute(
                ApiRequest.builder(Method.GET, BOOKINGS_ENDPOINT)
                        .header("Accept", JSON_ACCEPT_HEADER)
                        .queryParam("firstname", firstName)
                        .queryParam("lastname", lastName)
                        .build()
        );
    }

    @Step("GET booking by id={bookingId}")
    public Response getBookingById(int bookingId) {
        logger.info("Fetching booking id={}", bookingId);
//        return given(restSpec.get())
//                .header("Accept", JSON_ACCEPT_HEADER)
//                .when()
//                .get(BOOKINGS_ENDPOINT + "/{bookingId}", bookingId)
//                .then()
//                .extract().response();



        return apiExecutor.execute(
                ApiRequest.builder(Method.GET, BOOKINGS_ENDPOINT + "/{bookingId}")
                        .header("Accept", JSON_ACCEPT_HEADER)
                        .pathParam("bookingId", bookingId)
                        .build()
        );
    }

    @Step("POST create booking for guest '{request.firstname} {request.lastname}'")
    public Response createBooking(BookingRequest request) {
        logger.info("Creating booking for guest='{} {}'", request.getFirstname(), request.getLastname());
//        return executeWriteRequestWithRetry("create booking", () -> given(restSpec.get())
//                .contentType(ContentType.JSON)
//                .header("Accept", JSON_ACCEPT_HEADER)
//                .body(request)
//                .when()
//                .post(BOOKINGS_ENDPOINT)
//                .then()
//                .extract().response());





        return executeWriteRequestWithRetry("create booking", () -> apiExecutor.execute(
                ApiRequest.builder(Method.POST, BOOKINGS_ENDPOINT)
                        .header("Accept", JSON_ACCEPT_HEADER)
                        .body(request)
                        .build()
        ));
    }

    @Step("PUT update booking id={bookingId}")
    public Response updateBooking(int bookingId, BookingRequest request, String token) {
        logger.info("Fully updating booking id={} for guest='{} {}'", bookingId, request.getFirstname(), request.getLastname());
//        return executeWriteRequestWithRetry("update booking", () -> given(restSpec.get())
//                .contentType(ContentType.JSON)
//                .header("Accept", JSON_ACCEPT_HEADER)
//                .header("Cookie", "token=" + token)
//                .body(request)
//                .when()
//                .put(BOOKINGS_ENDPOINT + "/{bookingId}", bookingId)
//                .then()
//                .extract().response());

        return executeWriteRequestWithRetry("update booking", () -> apiExecutor.execute( ApiRequest
                .builder(
                        Method.PUT,
                        BOOKINGS_ENDPOINT + "/{bookingId}"
                )
                .header("Accept", JSON_ACCEPT_HEADER)
                .header("Cookie", "token=" + token)
                .pathParam("bookingId", bookingId)
                .body(request)
                .build()));
    }

    @Step("PATCH booking id={bookingId}")
    public Response partiallyUpdateBooking(int bookingId, PartialBookingUpdateRequest request, String token) {
        logger.info("Partially updating booking id={}", bookingId);
//        return executeWriteRequestWithRetry("patch booking", () -> given(restSpec.get())
//                .contentType(ContentType.JSON)
//                .header("Accept", JSON_ACCEPT_HEADER)
//                .header("Cookie", "token=" + token)
//                .body(request)
//                .when()
//                .patch(BOOKINGS_ENDPOINT + "/{bookingId}", bookingId)
//                .then()
//                .extract().response());






        return executeWriteRequestWithRetry("patch booking", () -> apiExecutor.execute(
                ApiRequest.builder(Method.PATCH, BOOKINGS_ENDPOINT + "/{bookingId}")
                        .header("Accept", JSON_ACCEPT_HEADER)
                        .header("Cookie", "token=" + token)
                        .pathParam("bookingId", bookingId)
                        .body(request)
                        .build()
        ));
    }

    @Step("DELETE booking id={bookingId}")
    public Response deleteBooking(int bookingId, String token) {
        logger.info("Deleting booking id={}", bookingId);
//        return executeWriteRequestWithRetry("delete booking", () -> given(restSpec.get())
//                .header("Cookie", "token=" + token)
//                .when()
//                .delete(BOOKINGS_ENDPOINT + "/{bookingId}", bookingId)
//                .then()
//                .extract().response());

return executeWriteRequestWithRetry("delete booking", () -> apiExecutor.execute(
                ApiRequest.builder(Method.DELETE, BOOKINGS_ENDPOINT + "/{bookingId}")
                        .header("Cookie", "token=" + token)
                        .pathParam("bookingId", bookingId)
                        .build()
        ));
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
