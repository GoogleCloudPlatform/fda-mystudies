package com.google.cloud.healthcare.fdamystudies.testutils;

import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpHeaders;

public class TestUtils {

  public static void addUserIdHeader(HttpHeaders headers) {
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
  }

  public static HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(Constants.CLIENT_TOKEN_HEADER, Constants.CLIENT_TOKEN_VALUE);
    headers.add(Constants.ACCESS_TOKEN_HEADER, Constants.ACCESS_TOKEN_VALUE);
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
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
