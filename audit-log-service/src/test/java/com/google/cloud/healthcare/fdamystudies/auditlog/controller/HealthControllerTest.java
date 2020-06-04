/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import com.google.cloud.healthcare.fdamystudies.auditlog.common.ApiEndpoints;
import com.google.cloud.healthcare.fdamystudies.auditlog.common.BaseMockIT;

public class HealthControllerTest extends BaseMockIT {

  @Test
  public void health() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

    performGet(ApiEndpoints.HEALTH.getPath(), null, headers, "OK", OK);
  }
}
