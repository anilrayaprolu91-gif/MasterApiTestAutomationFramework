package com.api.framework.tests;

import com.api.framework.base.BaseTest;
import com.api.framework.clients.SoapApiClient;
import com.api.framework.http.SoapRequestSpecProvider;
import com.api.framework.utils.AllureAttachmentUtil;
import com.api.framework.utils.XmlSchemaValidator;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Functional and schema-level tests for the DataAccess NumberConversion SOAP web service.
 *
 * <p>Approach:
 * <ol>
 *   <li>Send a SOAP 1.1 request using {@link SoapRequestSpecProvider}.</li>
 *   <li>Assert the HTTP response code is 200 (SOAP faults are still HTTP 200 or 500
 *       depending on the server; here we expect a successful response).</li>
 *   <li>Extract the raw XML response body.</li>
 *   <li>Validate the XML structure against an XSD file on the classpath using
 *       {@link XmlSchemaValidator} (Java's built-in {@code javax.xml.validation} API).</li>
 *   <li>Apply domain-level assertions on the extracted text value.</li>
 * </ol>
 *
 * <p>The XSD validates the SOAP response body content (the
 * {@code NumberToWordsResponse} element), not the outer SOAP envelope wrapper,
 * keeping the schema focused and reusable.
 */
@Epic("DataAccess NumberConversion SOAP API")
@Feature("NumberToWords Operation")
@Owner("API Automation Team")
public class XmlValidationTest extends BaseTest {

    private SoapApiClient soapApiClient;

    /** XPath to the text result inside the SOAP response body (GPath syntax). */
    private static final String RESULT_XPATH =
            "Envelope.Body.NumberToWordsResponse.NumberToWordsResult";

    /** Classpath path to the XSD that validates the NumberToWordsResponse element. */
    private static final String RESPONSE_XSD_PATH = "schemas/number-to-words-response.xsd";

    @BeforeClass
    public void setUp() {
        soapApiClient = new SoapApiClient();
    }

    // =========================================================================
    //  Test: NumberToWords — happy-path with data-driven inputs
    // =========================================================================

    /**
     * Provides (input number, expected word fragment) pairs for data-driven testing.
     * TestNG will generate one test execution per row.
     */
    @DataProvider(name = "numberToWordsData", parallel = true)
    public Object[][] numberToWordsData() {
        return new Object[][]{
                {500L,  "five hundred"},
                {1L,    "one"},
                {42L,   "forty two"},
                {1000L, "one thousand"},
        };
    }

    @Test(
            dataProvider   = "numberToWordsData",
            description    = "SOAP NumberToWords should convert a number to its English word form"
    )
    @Story("NumberToWords — happy path")
    @Severity(SeverityLevel.BLOCKER)
    @Description("""
            For each input number this test verifies:
            1) The SOAP endpoint responds with HTTP 200.
            2) The raw XML body conforms to the declared XSD schema.
            3) The NumberToWordsResult text contains the expected word fragment
               (case-insensitive, trimmed).
            """)
    public void numberToWords_shouldReturnCorrectWordRepresentation(
            long inputNumber, String expectedFragment) {

        // ── When ─────────────────────────────────────────────────────────────
        Response response = soapApiClient.convertNumberToWords(inputNumber);

        // ── Then — HTTP contract ──────────────────────────────────────────────
        assertThat(response.getStatusCode())
                .as("SOAP endpoint should respond with HTTP 200")
                .isEqualTo(HttpStatus.SC_OK);

        // ── Then — XML Schema validation ──────────────────────────────────────
        // Attach the raw XML to the Allure report before asserting, so failures
        // have full context available in the report.
        String rawXml = response.getBody().asString();
        AllureAttachmentUtil.attachXml("SOAP Response Body", rawXml);

        // Extract the NumberToWordsResponse element from inside the SOAP Body,
        // then validate it against the service-specific XSD.
        String bodyContent = XmlSchemaValidator.extractSoapBodyContent(rawXml);
        AllureAttachmentUtil.attachXml("Extracted SOAP Body Content", bodyContent);
        XmlSchemaValidator.validate(bodyContent, RESPONSE_XSD_PATH);

        // ── Then — Domain assertions ──────────────────────────────────────────
        // REST Assured's xml() GPath convenience method extracts values from the response.
        String result = response.xmlPath().getString(RESULT_XPATH);

        assertThat(result)
                .as("NumberToWordsResult must not be blank")
                .isNotBlank();

        assertThat(result.trim().toLowerCase())
                .as("NumberToWordsResult should contain the expected English word(s)")
                .contains(expectedFragment.toLowerCase());

        logger.info("numberToWords passed — input={}, result='{}'", inputNumber, result.trim());
    }

    // =========================================================================
    //  Test: NumberToWords — boundary value (zero)
    // =========================================================================

    @Test(description = "SOAP NumberToWords should handle zero as input")
    @Story("NumberToWords — boundary values")
    @Severity(SeverityLevel.NORMAL)
    @Description("""
            Verifies that zero is handled gracefully:
            the service should return a non-blank word representation.
            """)
    public void numberToWords_withZero_shouldReturnNonBlankResult() {
        // ── When ─────────────────────────────────────────────────────────────
        Response response = soapApiClient.convertNumberToWords(0L);

        // ── Then ─────────────────────────────────────────────────────────────
        assertThat(response.getStatusCode())
                .as("SOAP endpoint should respond with HTTP 200 for zero")
                .isEqualTo(HttpStatus.SC_OK);

        String rawXml = response.getBody().asString();
        AllureAttachmentUtil.attachXml("SOAP Response Body (zero)", rawXml);

        String bodyContent = XmlSchemaValidator.extractSoapBodyContent(rawXml);
        XmlSchemaValidator.validate(bodyContent, RESPONSE_XSD_PATH);

        String result = response.xmlPath().getString(RESULT_XPATH);
        assertThat(result)
                .as("Result for zero should not be blank")
                .isNotBlank();

        logger.info("numberToWords_withZero passed — result='{}'", result.trim());
    }
}

