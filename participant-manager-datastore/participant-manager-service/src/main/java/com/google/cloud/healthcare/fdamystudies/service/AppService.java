/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AppParticipantsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AppResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;

public interface AppService {
  public AppResponse getApps(String userId);

  public AppResponse getAppsWithOptionalFields(String userId, String[] fields);

  public AppParticipantsResponse getAppParticipants(
      String appId, String userId, AuditLogEventRequest auditRequest);
}
