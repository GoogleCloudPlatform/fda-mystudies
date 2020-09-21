/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.util.List;

public interface UserProfileManagementDao {

  public UserDetailsEntity getParticipantInfoDetails(String userId);

  public ErrorBean updateUserProfile(
      String userId, UserDetailsEntity userDetail, AuthInfoEntity authInfo);

  public AuthInfoEntity getAuthInfo(String id);

  public UserDetailsEntity getParticipantDetailsByEmail(String email, AppEntity app);

  public LoginAttemptsEntity getLoginAttempts(String email);

  public UserDetailsEntity saveParticipant(UserDetailsEntity participant);

  public AppEntity getAppPropertiesDetailsByAppId(String appId);

  public void resetLoginAttempts(String email);

  public UserDetailsEntity getParticipantDetails(String id);

  public boolean deActivateAcct(String userId, List<String> deleteData, String userDetailsId);
}
