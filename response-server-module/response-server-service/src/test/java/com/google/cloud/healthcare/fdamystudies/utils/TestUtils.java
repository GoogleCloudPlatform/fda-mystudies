/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import javax.ws.rs.core.MediaType;
import org.springframework.http.HttpHeaders;

import static com.google.cloud.healthcare.fdamystudies.utils.Constants.ACCESS_TOKEN_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.ACCESS_TOKEN_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.CLIENT_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.CLIENT_ID_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.CLIENT_SECRET_KEY_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.CLIENT_SECRET_KEY_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.CLIENT_TOKEN_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.CLIENT_TOKEN_VALUE;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.VALID_USER_ID;

public class TestUtils {

  public static HttpHeaders newCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_ID_HEADER, CLIENT_ID_VALUE);
    headers.add(CLIENT_SECRET_KEY_HEADER, CLIENT_SECRET_KEY_VALUE);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    return headers;
  }

  public static HttpHeaders newHeadersUser() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_TOKEN_HEADER, CLIENT_TOKEN_VALUE);
    headers.add(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_VALUE);
    headers.add(USER_ID_HEADER, VALID_USER_ID);
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    return headers;
  }
}
