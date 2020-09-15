/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;

public interface UserManagementProfileService {

  public UserProfileRespBean getParticipantInfoDetails(String userId, Integer appInfoId);

  public ErrorBean updateUserProfile(String userId, UserRequestBean user);

  public UserDetailsBO getParticipantDetailsByEmail(String email, Integer appInfoId);

  public LoginAttemptsBO getLoginAttempts(String email);

  public void resetLoginAttempts(String email);

  public UserDetailsBO getParticipantDetails(String id);

  public UserDetailsBO saveParticipant(UserDetailsBO participant);

  public String deactivateAccount(
      String userId, DeactivateAcctBean deactivateBean, AuditLogEventRequest auditRequest);

  public int resendConfirmationthroughEmail(
      String applicationId, String securityToken, String emailId);
}
