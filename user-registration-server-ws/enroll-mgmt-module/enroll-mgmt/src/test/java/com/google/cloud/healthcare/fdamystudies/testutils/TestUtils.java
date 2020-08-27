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

  public static HttpHeaders getCommonHeaders() {

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    return headers;
  }
}
