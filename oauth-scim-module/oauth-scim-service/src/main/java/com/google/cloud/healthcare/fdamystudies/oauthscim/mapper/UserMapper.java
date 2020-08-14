/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.mapper;

import com.google.cloud.healthcare.fdamystudies.beans.UserRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UserResponse;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.UserAccountStatus;
import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;

public final class UserMapper {

  private UserMapper() {}

  public static UserEntity fromUserRequest(UserRequest userRequest) {
    UserEntity entity = new UserEntity();
    entity.setAppId(userRequest.getAppId());
    entity.setEmail(userRequest.getEmail());
    entity.setStatus(userRequest.getStatus());
    if (UserAccountStatus.ACTIVE.getStatus() == userRequest.getStatus()) {
      entity.setTempRegId(IdGenerator.id());
    }
    entity.setUserId(IdGenerator.id());
    return entity;
  }

  public static UserResponse toUserResponse(UserEntity userEntity) {
    UserResponse response = new UserResponse();
    response.setUserId(userEntity.getUserId());
    response.setTempRegId(userEntity.getTempRegId());
    return response;
  }
}
