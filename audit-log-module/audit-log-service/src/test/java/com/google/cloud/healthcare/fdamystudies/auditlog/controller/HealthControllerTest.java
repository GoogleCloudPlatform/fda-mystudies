/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import com.google.cloud.healthcare.fdamystudies.auditlog.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

public class HealthControllerTest extends BaseMockIT {

  @Test
  public void health() throws Exception {
    performGet(ApiEndpoint.HEALTH.getPath(), new HttpHeaders(), "OK", OK);
  }
}
