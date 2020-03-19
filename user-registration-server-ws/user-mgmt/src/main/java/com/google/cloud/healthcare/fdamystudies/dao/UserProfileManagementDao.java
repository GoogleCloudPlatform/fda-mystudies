/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.DeactivateAcctBean;
import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;

public interface UserProfileManagementDao {

  public UserDetails getParticipantInfoDetails(String userId);

  public ErrorBean updateUserProfile(String userId, UserDetails userDetail, AuthInfoBO authInfo);

  public AuthInfoBO getAuthInfo(Integer userDetailsId);

  public UserDetails getParticipantDetailsByEmail(
      String email, Integer appInfoId, Integer orgInfoId);

  public LoginAttemptsBO getLoginAttempts(String email);

  public UserDetails saveParticipant(UserDetails participant);

  public AppInfoDetailsBO getAppPropertiesDetailsByAppId(Integer appId);

  public void resetLoginAttempts(String email);

  public UserDetails getParticipantDetails(String id);

  public boolean deActivateAcct(
      String userId, DeactivateAcctBean deactivateBean, Integer userDetailsId);
}
