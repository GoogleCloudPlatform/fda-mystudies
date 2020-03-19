/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRespBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserRequestBean;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;

public interface UserManagementProfileService {

  public UserProfileRespBean getParticipantInfoDetails(
      String userId, Integer appInfoId, Integer orgInfoId);

  public ErrorBean updateUserProfile(String userId, UserRequestBean user);

  public UserDetails getParticipantDetailsByEmail(
      String email, Integer appInfoId, Integer orgInfoId);

  public LoginAttemptsBO getLoginAttempts(String email);

  public void resetLoginAttempts(String email);

  public int sendPasswordResetLinkthroughEmail(
      String emailId, String tempPassword, UserDetails participantDetails);

  public UserDetails getParticipantDetails(String id);

  /* public Boolean getPasswordHistory(String userId, String newPassword);

  public ResponseBean savePasswordHistory(
      String userId, String password, String applicationId, String orgId);*/

  public UserDetails saveParticipant(UserDetails participant);

  public String deActivateAcct(
      String userId, DeactivateAcctBean deactivateBean, String accessToken, String clientToken);

  public int resendConfirmationthroughEmail(
      String applicationId, String securityToken, String emailId);

  /*public AppOrgInfoBean getUserAppDetailsByAllApi(
  String userId, String emailId, String appId, String orgId);*/
}
