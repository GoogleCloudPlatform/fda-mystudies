/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.testutils;

import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpHeaders;

public class TestUtils {

  public static void addUserIdHeader(HttpHeaders headers) {
    headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
  }

  public static HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
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
