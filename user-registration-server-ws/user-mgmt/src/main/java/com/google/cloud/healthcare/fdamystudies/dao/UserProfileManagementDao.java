/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import java.util.List;

public interface UserProfileManagementDao {

  public UserDetailsBO getParticipantInfoDetails(String userId);

  public ErrorBean updateUserProfile(String userId, UserDetailsBO userDetail, AuthInfoBO authInfo);

  public AuthInfoBO getAuthInfo(Integer userDetailsId);

  public UserDetailsBO getParticipantDetailsByEmail(
      String email, Integer appInfoId, Integer orgInfoId);

  public LoginAttemptsBO getLoginAttempts(String email);

  public UserDetailsBO saveParticipant(UserDetailsBO participant);

  public AppInfoDetailsBO getAppPropertiesDetailsByAppId(Integer appId);

  public void resetLoginAttempts(String email);

  public UserDetailsBO getParticipantDetails(String id);

  public boolean deActivateAcct(String userId, List<String> deleteData, Integer userDetailsId);

  public ErrorBean removeDeviceToken(int userId);

  public ErrorBean updateAppVersion(AuthInfoBO authInfo);
}
