/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;

public interface FdaEaUserDetailsDao {

  UserDetailsEntity saveUser(UserDetailsEntity userDetailsBO) throws SystemException;

  UserDetailsEntity loadUserDetailsByUserId(String userId) throws SystemException;

  UserDetailsEntity loadEmailCodeByUserId(String userId) throws SystemException;

  boolean updateStatus(UserDetailsEntity participantDetails);

  boolean saveAllRecords(
      UserDetailsEntity userDetailsBO, AuthInfoEntity authInfo, UserAppDetailsEntity userAppDetails)
      throws SystemException;
}
