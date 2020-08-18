package com.google.cloud.healthcare.fdamystudies.testutils;

import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpHeaders;

public class TestUtils {

  public static HttpHeaders getCommonHeaders() {

    HttpHeaders headers = new HttpHeaders();
    headers.add(Constants.CLIENT_TOKEN_HEADER, Constants.CLIENT_TOKEN_VALUE);
    headers.add(Constants.ACCESS_TOKEN_HEADER, Constants.ACCESS_TOKEN_VALUE);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    return headers;
  }
}
