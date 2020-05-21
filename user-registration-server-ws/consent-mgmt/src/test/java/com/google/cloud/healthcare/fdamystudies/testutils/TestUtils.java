package com.google.cloud.healthcare.fdamystudies.testutils;

import static org.junit.Assert.assertEquals;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TestUtils {

  public static void addUserIdHeader(HttpHeaders headers) {
    if (headers == null) {
      headers = new HttpHeaders();
    }
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
  }

  public static void addTokenHeaders(HttpHeaders headers) {
    if (headers == null) {
      headers = new HttpHeaders();
    }
    headers.add(Constants.CLIENT_TOKEN_HEADER, Constants.CLIENT_TOKEN_VALUE);
    headers.add(Constants.ACCESS_TOKEN_HEADER, Constants.ACCESS_TOKEN_VALUE);
  }

  public static void addJsonHeaders(HttpHeaders headers) {
    if (headers == null) {
      headers = new HttpHeaders();
    }
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
  }

  public static void assertUpdateEligibility(ResponseEntity<JsonNode> responseEntity) {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    JsonNode responseBody = responseEntity.getBody();
    assertEquals(
        Constants.UPDATE_CONSENT_SUCCESS_MSG, responseBody.get(Constants.FIELD_MESSAGE).asText());
  }

  public static void assertGetConsentResponse(
      ResponseEntity<JsonNode> responseEntity, String pdfValue, String version) {
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    JsonNode responseBody = responseEntity.getBody();
    assertEquals(
        Constants.SUCCESS_MESSAGE_LOWERCASE, responseBody.get(Constants.FIELD_MESSAGE).asText());
    JsonNode consent = (ObjectNode) responseBody.get(Constants.FIELD_CONSENT);
    assertEquals(version, consent.get(Constants.FIELD_VERSION).asText());
    assertEquals("application/pdf", consent.get(Constants.FIELD_TYPE).asText());
    assertEquals(pdfValue, consent.get(Constants.FIELD_CONTENT).asText());
  }
}
