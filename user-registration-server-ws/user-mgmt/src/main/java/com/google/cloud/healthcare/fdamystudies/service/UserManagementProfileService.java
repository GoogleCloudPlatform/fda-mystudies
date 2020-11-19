/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AppInfoBean;
import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;

public interface UserManagementProfileService {

  public UserProfileRespBean getParticipantInfoDetails(
      String userId, Integer appInfoId, Integer orgInfoId);

  public ErrorBean updateUserProfile(String userId, UserRequestBean user);

  public UserDetailsBO getParticipantDetailsByEmail(
      String email, Integer appInfoId, Integer orgInfoId);

  public LoginAttemptsBO getLoginAttempts(String email);

  public void resetLoginAttempts(String email);

  public UserDetailsBO getParticipantDetails(String id);

  public UserDetailsBO saveParticipant(UserDetailsBO participant);

  public String deActivateAcct(
      String userId, DeactivateAcctBean deactivateBean, String accessToken, String clientToken);

  public int resendConfirmationthroughEmail(
      String applicationId, String securityToken, String emailId);

  public ErrorBean removeDeviceToken(String userId);

  public ErrorBean updateAppVersion(AppInfoBean appInfoBean, String userId);
}
