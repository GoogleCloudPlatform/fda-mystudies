/*
 *Copyright 2020 Google LLC
 *
 *Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 *or at https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationForm;
import java.io.IOException;

public interface StudiesServices {
  public ErrorBean saveStudyMetadata(StudyMetadataBean studyMetadataBean);

  public ErrorBean SendNotificationAction(
      NotificationForm notificationForm, AuditLogEventRequest auditRequest) throws IOException;
}
