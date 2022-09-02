/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.EmailResponse;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;

public interface UserManagementProfileService {

  public UserProfileRespBean getParticipantInfoDetails(String userId, Integer appInfoId);

  public ErrorBean updateUserProfile(String userId, UserRequestBean user);

  public UserDetailsEntity getParticipantDetailsByEmail(String email, String appInfoId);

  public LoginAttemptsEntity getLoginAttempts(String email);

  public void resetLoginAttempts(String email);

  public UserDetailsEntity getParticipantDetails(String id);

  public UserDetailsEntity saveParticipant(UserDetailsEntity participant);

  public String deactivateAccount(
      String userId, DeactivateAcctBean deactivateBean, AuditLogEventRequest auditRequest);

  public EmailResponse resendConfirmationthroughEmail(
      String applicationId, String securityToken, String emailId, String appName);

  public void processDeactivatePendingRequests();
}
