/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog;

import static org.junit.Assert.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.cloud.healthcare.fdamystudies.auditlog.controller.AuditLogEventController;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;

class ApplicationTest extends BaseMockIT {

  @Autowired AuditLogEventController controller;

  @Test
  void contextLoads() {
    assertNotNull(controller);
  }
}
