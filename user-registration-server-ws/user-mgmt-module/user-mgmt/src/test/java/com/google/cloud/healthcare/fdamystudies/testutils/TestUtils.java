package com.google.cloud.healthcare.fdamystudies.testutils;

import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpHeaders;

public class TestUtils {
  public static HttpHeaders getCommonHeaders(String... addOptionalHeaderNames) {

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);

    if (ArrayUtils.contains(addOptionalHeaderNames, Constants.USER_ID_HEADER)) {
      headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    }

    if (ArrayUtils.contains(addOptionalHeaderNames, Constants.APP_ID_HEADER)) {
      headers.add(Constants.APP_ID_HEADER, Constants.APP_ID_VALUE);
    }

    if (ArrayUtils.contains(addOptionalHeaderNames, Constants.ORG_ID_HEADER)) {
      headers.add(Constants.ORG_ID_HEADER, Constants.ORG_ID_VALUE);
    }

    return headers;
  }
}
