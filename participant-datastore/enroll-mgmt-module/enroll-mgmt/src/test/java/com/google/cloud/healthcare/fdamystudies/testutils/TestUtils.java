/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.testutils;

import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.PlatformComponent;
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
    headers.add("source", PlatformComponent.MOBILE_APPS.getValue());
    return headers;
  }
}
