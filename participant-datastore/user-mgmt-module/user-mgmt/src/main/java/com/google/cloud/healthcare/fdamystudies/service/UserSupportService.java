/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.ContactUsReqBean;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.FeedbackReqBean;

public interface UserSupportService {

  public EmailResponse feedback(FeedbackReqBean feedbackRequest, AuditLogEventRequest auditRequest);

  public EmailResponse contactUsDetails(
      ContactUsReqBean contactUsRequest, AuditLogEventRequest auditRequest) throws Exception;
}
