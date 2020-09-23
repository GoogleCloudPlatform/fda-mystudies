/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import static com.google.cloud.healthcare.fdamystudies.utils.Constants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.VALID_USER_ID;

import java.util.Collections;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class TestUtils {

  protected static final String VALID_BEARER_TOKEN = "Bearer 7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  public static HttpHeaders newCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", VALID_BEARER_TOKEN);
    return headers;
  }

  public static HttpHeaders newHeadersUser() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    headers.add(USER_ID_HEADER, VALID_USER_ID);
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    return headers;
  }
}
