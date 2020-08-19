/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.UserProfileRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.beans.SetUpAccountResponse;
import com.google.cloud.healthcare.fdamystudies.beans.UserProfileResponse;

public interface UserProfileService {

  public UserProfileResponse getUserProfile(String userId);

  public UserProfileResponse findUserProfileBySecurityCode(String securityCode);

  public UserProfileResponse updateUserProfile(UserProfileRequest userProfileRequest);
  
  public SetUpAccountResponse saveUser(SetUpAccountRequest setUpAccountRequest);
}
