package com.google.cloud.healthcare.fdamystudies.testutils;

import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpHeaders;

public class TestUtils {

  public static HttpHeaders getCommonHeaders() {

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    headers.add("correlationId", IdGenerator.id());
    headers.add("appVersion", "1.0");
    headers.add("appId", "GCPMS001");
    return headers;
  }

  public static HttpHeaders getCommonHeaderValues() {

    HttpHeaders headers = new HttpHeaders();

    headers.add("correlationId", IdGenerator.id());
    headers.add("appVersion", "1.0");
    headers.add("appId", "GCPMS001");
    return headers;
  }
}
