/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.model.AuditLogBo;

public interface AuditLogService {
  public AuditLogBo createAuditLog(
      String userId,
      String activityName,
      String activtyDesc,
      String clientId,
      String participantId,
      String studyId,
      String accessLevel);
}
