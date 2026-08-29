package com.api.framework.clients;

import com.api.framework.http.ApiExecutor;
import com.api.framework.http.ApiRequest;
import com.api.framework.http.RequestSpecProvider;
import com.api.framework.http.SoapRequestSpecProvider;
import io.qameta.allure.Step;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



import static io.restassured.RestAssured.given;

/**
 * Dedicated API client for SOAP/XML web-services.
 *
 * <p>Unlike REST clients, SOAP calls share a single endpoint URL and are
 * distinguished by the {@code SOAPAction} header and XML body template.
 * This client targets the public
 * <a href="https://www.dataaccess.com/webservicesserver/numberconversion.wso">
 * DataAccess NumberConversion WSDL</a>.
 */
public class SoapApiClient {

    private final ApiExecutor apiExecutor;


    private static final Logger logger = LogManager.getLogger(SoapApiClient.class);
//    private final RequestSpecProvider soapSpec;

    /** Template for a SOAP 1.1 NumberToWords envelope. */
   private static final String NUMBER_TO_WORDS_ENVELOPE =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                    + "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
                    + "  <soap:Body>"
                    + "    <NumberToWords xmlns=\"http://www.dataaccess.com/webservicesserver/\">"
                    + "      <ubiNum>%d</ubiNum>"
                    + "    </NumberToWords>"
                    + "  </soap:Body>"
                  + "</soap:Envelope>";

    public SoapApiClient(ApiExecutor apiExecutor) {
        this.apiExecutor = apiExecutor;
//        this.soapSpec =
//                new SoapRequestSpecProvider();
    }


    /**
     * Calls the {@code NumberToWords} SOAP operation.
     *
     * @param number the unsigned integer to convert to a word representation
     * @return the full HTTP response containing the SOAP envelope
     */
    @Step("SOAP NumberToWords — convert {number} to words")
    public Response convertNumberToWords(long number) {
        String soapBody = String.format(NUMBER_TO_WORDS_ENVELOPE, number);
        logger.info("Invoking NumberToWords SOAP operation with number={}", number);

//        return given(soapSpec.get())
//                .header("SOAPAction", "\"http://www.dataaccess.com/webservicesserver/NumberToWords\"")
//                .body(soapBody)
//                .when()
//                .post("/NumberConversion.wso")
//                .then()
//                .extract().response();

        return apiExecutor.execute(
                ApiRequest.builder(Method.POST, "/NumberConversion.wso").header("SOAPAction", "\"http://www.dataaccess.com/webservicesserver/NumberToWords\"")
                        .body(soapBody)
                        .build()
        );
    }


}

