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
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.HttpHeaders;

public class TestUtils {
  private static final String VALID_BEARER_TOKEN = "Bearer 7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  public static HttpHeaders getCommonHeaders(String... addOptionalHeaderNames) {

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add("correlationId", IdGenerator.id());
    headers.add("appVersion", "1.0");
    headers.add(Constants.APP_ID_HEADER, Constants.APP_ID_VALUE);
    headers.add("source", "MOBILE APPS");

    if (ArrayUtils.contains(addOptionalHeaderNames, Constants.USER_ID_HEADER)) {
      headers.add(Constants.USER_ID_HEADER, Constants.VALID_USER_ID);
    }

    return headers;
  }
}
