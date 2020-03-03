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

import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserDetailsResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.UserProfileUpdateBean;

public interface UserProfileServiceDao {

  public UserDetailsResponseBean getUserProfile(String authUserId);

  public ErrorBean updateUserProfile(String userId, UserProfileUpdateBean userReqBean);

  UserDetailsResponseBean getUserProfileById(Integer userId);
}
