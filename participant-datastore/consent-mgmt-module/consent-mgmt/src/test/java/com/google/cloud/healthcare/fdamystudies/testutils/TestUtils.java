/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.testutils;

import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpHeaders;

public class TestUtils {

  public static final String VALID_BEARER_TOKEN = "Bearer 7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  public static void addUserIdHeader(HttpHeaders headers) {
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
  }

  public static HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    headers.add("correlationId", IdGenerator.id());
    headers.add("appVersion", "1.0");
    headers.add("appId", "GCPMS001");
    headers.add("source", "MOBILE APPS");
    headers.add("Authorization", VALID_BEARER_TOKEN);
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
