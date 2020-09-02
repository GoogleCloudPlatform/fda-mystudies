/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.UserProfileResponse;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;

public final class UserProfileMapper {

  private UserProfileMapper() {}

  public static UserProfileResponse toUserProfileResponse(
      UserRegAdminEntity userRegAdminEntity, MessageCode messageCode) {
    UserProfileResponse profileResponse = new UserProfileResponse(messageCode);
    profileResponse.setFirstName(userRegAdminEntity.getFirstName());
    profileResponse.setLastName(userRegAdminEntity.getLastName());
    profileResponse.setEmail(userRegAdminEntity.getEmail());
    profileResponse.setUserId(userRegAdminEntity.getId());
    profileResponse.setManageLocations(userRegAdminEntity.getLocationPermission());
    profileResponse.setSuperAdmin(userRegAdminEntity.isSuperAdmin());
    return profileResponse;
  }
}
