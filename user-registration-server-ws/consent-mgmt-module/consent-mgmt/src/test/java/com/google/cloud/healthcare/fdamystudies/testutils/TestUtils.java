package com.google.cloud.healthcare.fdamystudies.testutils;

import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpHeaders;

public class TestUtils {

  public static void addUserIdHeader(HttpHeaders headers) {
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
  }

  public static HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("correlationId", IdGenerator.id());
    headers.add("appVersion", "1.0");
    headers.add("appId", "GCPMS001");
    headers.add("source", "IntegrationTests");
    return headers;
  }

  public static void addHeader(HttpHeaders headers, String headerName, String headerValue) {
    headers.add(headerName, headerValue);
  }

  public static void addContentTypeAcceptHeaders(HttpHeaders headers) {
    addHeader(headers, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    addHeader(headers, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
  }
}
