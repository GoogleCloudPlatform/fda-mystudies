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
    headers.add(Constants.CORRELATION_ID_HEADER, "corr");
    return headers;
  }

  public static void addHeader(HttpHeaders headers, String headerName, String headerValue) {
    headers.add(headerName, headerValue);
  }

  public static void addContentTypeAcceptHeaders(HttpHeaders headers) {
    addHeader(headers, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    addHeader(headers, HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
  }

  public static String generateString(String key, int size) {
    StringBuilder sb = new StringBuilder();
    String str = "very-long-" + key + "-";
    for (int i = 0; i <= size; ) {
      sb.append(str);
      i += str.length();
    }
    return sb.substring(0, size);
  }

  public static void main(String[] args) {
    System.out.println(generateString("app_id", 255).length());
  }
}
