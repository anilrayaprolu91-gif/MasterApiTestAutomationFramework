package com.api.framework.tests;

import com.api.framework.base.BaseTest;
import com.api.framework.clients.AuthApiClient;
import com.api.framework.clients.BookingApiClient;
import com.api.framework.models.request.BookingDates;
import com.api.framework.models.request.BookingRequest;
import com.api.framework.models.response.AuthResponse;
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
 * Functional and contract tests for the public Restful Booker API.
 */
@Epic("Restful Booker API")
@Feature("Authentication and Booking Resources")
@Owner("API Automation Team")
@Test(singleThreaded = true)
public class RestfulBookerApiTest extends BaseTest {

    private static final File AUTH_RESPONSE_SCHEMA =
            new File("src/test/resources/schemas/restfulbooker-auth-response-schema.json");
    private static final File BOOKING_RESPONSE_SCHEMA =
            new File("src/test/resources/schemas/restfulbooker-booking-response-schema.json");
    private static final File CREATE_BOOKING_RESPONSE_SCHEMA =
            new File("src/test/resources/schemas/restfulbooker-create-booking-response-schema.json");
    private static final File BOOKING_IDS_RESPONSE_SCHEMA =
            new File("src/test/resources/schemas/restfulbooker-booking-ids-response-schema.json");

    static AuthApiClient authApiClient;
    static BookingApiClient bookingApiClient;
    static String authToken;
    static BookingRequest sharedBookingRequest;
    static BookingCreationResponse sharedBookingCreationResponse;

    @BeforeClass
    public void setUp() {
        authApiClient = new AuthApiClient();
        bookingApiClient = new BookingApiClient();
        authToken = authApiClient.createTokenValue();
        sharedBookingRequest = buildUniqueBookingRequest();
        sharedBookingCreationResponse = createSharedBooking(sharedBookingRequest);
    }


    @Test(description = "GET /ping should confirm the Restful Booker service is reachable")
    @Story("Service Health")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies that the health endpoint returns HTTP 201 and the expected plain-text body.")
    public void ping_shouldReturn201AndCreatedMessage() {
        Response response = bookingApiClient.ping();

        assertThat(response.getStatusCode())
                .as("GET /ping should return HTTP 201")
                .isEqualTo(HttpStatus.SC_CREATED);

        assertThat(response.getBody().asString().trim())
                .as("GET /ping should return the expected heartbeat message")
                .isEqualTo("Created");
    }

    @Test(description = "POST /auth should return a token and match the documented JSON schema")
    @Story("Authentication")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that the documented public demo credentials produce a valid authentication token.")
    public void authenticate_shouldReturnTokenAndMatchSchema() {
        Response response = authApiClient.createToken();

        assertThat(response.getStatusCode())
                .as("POST /auth should return HTTP 200")
                .isEqualTo(HttpStatus.SC_OK);

        response.then().assertThat().body(matchesJsonSchema(AUTH_RESPONSE_SCHEMA));

        AuthResponse authResponse = response.as(AuthResponse.class);
        assertThat(authResponse.getToken())
                .as("Auth token should be present and non-blank")
                .isNotBlank();
    }

    @Test(description = "POST /booking should create a booking and return a schema-valid response")
    @Story("Create Booking")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Creates a unique booking, validates the response schema, and verifies echoed business fields.")
    public void createBooking_shouldReturn200AndMatchSchema() {
        assertThat(sharedBookingCreationResponse)
                .as("Shared booking should be created once in @BeforeClass")
                .isNotNull();

        BookingCreationResponse bookingCreationResponse = sharedBookingCreationResponse;

        assertThat(bookingCreationResponse)
                .as("Shared booking creation response should be present")
                .isNotNull();

        // Schema validation is covered at creation time in @BeforeClass.
        assertThat(bookingCreationResponse.getBookingid())
                .as("Created booking id must be positive")
                .isPositive();

        assertThat(bookingCreationResponse.getBooking().getFirstname())
                .as("Firstname should echo the request payload")
                .isEqualTo(sharedBookingRequest.getFirstname());
        assertThat(bookingCreationResponse.getBooking().getLastname())
                .as("Lastname should echo the request payload")
                .isEqualTo(sharedBookingRequest.getLastname());
        assertThat(bookingCreationResponse.getBooking().getAdditionalneeds())
                .as("Additional needs should echo the request payload")
                .isEqualTo(sharedBookingRequest.getAdditionalneeds());
    }

    @Test(description = "GET /booking/{id} should retrieve a created booking and match the JSON schema")
    @Story("Get Booking By Id")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Creates a unique booking, reads it back by id, validates the schema, and compares key domain fields.")
    public void getBookingById_shouldReturn200AndMatchSchema() {
        Response getResponse = bookingApiClient.getBookingById(sharedBookingCreationResponse.getBookingid());

        assertThat(getResponse.getStatusCode())
                .as("GET /booking/{id} should return HTTP 200 for an existing booking")
                .isEqualTo(HttpStatus.SC_OK);

        getResponse.then().assertThat().body(matchesJsonSchema(BOOKING_RESPONSE_SCHEMA));

        BookingResponse booking = getResponse.as(BookingResponse.class);
        assertThat(booking.getFirstname()).isEqualTo(sharedBookingRequest.getFirstname());
        assertThat(booking.getLastname()).isEqualTo(sharedBookingRequest.getLastname());
        assertThat(booking.getTotalprice()).isEqualTo(sharedBookingRequest.getTotalprice());
        assertThat(booking.isDepositpaid()).isEqualTo(sharedBookingRequest.isDepositpaid());
        assertThat(booking.getBookingdates().getCheckin()).isEqualTo(sharedBookingRequest.getBookingdates().getCheckin());
        assertThat(booking.getBookingdates().getCheckout()).isEqualTo(sharedBookingRequest.getBookingdates().getCheckout());
    }

    @Test(description = "GET /booking with firstname and lastname filters should return the created booking id")
    @Story("Search Bookings")
    @Severity(SeverityLevel.NORMAL)
    @Description("Creates a unique booking and verifies that name-based search returns the newly created booking id.")
    public void findBookingsByName_shouldReturnCreatedBookingId() {
        Response searchResponse = bookingApiClient.findBookingsByName(
                sharedBookingRequest.getFirstname(),
                sharedBookingRequest.getLastname()
        );

        assertThat(searchResponse.getStatusCode())
                .as("GET /booking?firstname=&lastname= should return HTTP 200")
                .isEqualTo(HttpStatus.SC_OK);

        searchResponse.then().assertThat().body(matchesJsonSchema(BOOKING_IDS_RESPONSE_SCHEMA));

        List<BookingId> bookingIds = searchResponse.jsonPath().getList("", BookingId.class);
        assertThat(bookingIds)
                .as("Name-based booking search should return at least one result")
                .isNotEmpty();
        assertThat(bookingIds)
                .extracting(BookingId::getBookingid)
                .as("Name-based booking search should contain the shared booking id")
                .contains(sharedBookingCreationResponse.getBookingid());
    }

    private BookingRequest buildUniqueBookingRequest() {
        LocalDate today = LocalDate.now();

        return BookingRequest.builder()
                .firstname("QAeabcf")
                .lastname("Candidateabcf")
                .totalprice(250)
                .depositpaid(true)
                .bookingdates(BookingDates.builder()
                        .checkin(today.plusDays(7).toString())
                        .checkout(today.plusDays(10).toString())
                        .build())
                .additionalneeds("Breakfast")
                .build();
    }


    private BookingCreationResponse createSharedBooking(BookingRequest request) {
        Response response = bookingApiClient.createBooking(request);

        assertThat(response.getStatusCode())
                .as("Shared POST /booking should return HTTP 200")
                .isEqualTo(HttpStatus.SC_OK);

        response.then().assertThat().body(matchesJsonSchema(CREATE_BOOKING_RESPONSE_SCHEMA));

        BookingCreationResponse bookingCreationResponse = response.as(BookingCreationResponse.class);
        assertThat(bookingCreationResponse.getBookingid())
                .as("Shared booking id must be positive")
                .isPositive();
        return bookingCreationResponse;
    }

    private void deleteBookingQuietly(Integer bookingId, String token) {
        if (bookingId == null || token == null || token.isBlank()) {
            return;
        }

        try {
            Response deleteResponse = bookingApiClient.deleteBooking(bookingId, token);
            logger.info("Cleanup delete executed for bookingId={} with status={}", bookingId, deleteResponse.getStatusCode());
        } catch (Exception exception) {
            logger.warn("Cleanup delete failed for bookingId={} — {}", bookingId, exception.getMessage());
        }
    }
}

