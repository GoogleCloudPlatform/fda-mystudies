/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.AppMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AppContactEmailsResponse;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;

public interface AppsService {

  public ErrorBean saveAppMetadata(AppMetadataBean appMetadataBean);

  public AppContactEmailsResponse getAppContactEmails(String appId);

  public ErrorBean deactivateAppAndUsers(String customAppId, AuditLogEventRequest auditRequest);
}
