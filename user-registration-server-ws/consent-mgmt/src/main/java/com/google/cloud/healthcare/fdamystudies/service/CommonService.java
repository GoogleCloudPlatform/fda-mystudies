/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.model.AuditLogBo;

public interface CommonService {

  public Integer validateAccessToken(String userId, String accessToken, String clientToken);

  public Integer getUserDetailsId(String userId);

  public AuditLogBo createAuditLog(String userId, String event, String description);

  AuditLogBo createAuditLog(
      String userId,
      String event,
      String description,
      String accessLevel,
      String participantId,
      String studyId);

  AuditLogBo createAuditLog(
      String userId, String event, String description, String accessLevel);
}
