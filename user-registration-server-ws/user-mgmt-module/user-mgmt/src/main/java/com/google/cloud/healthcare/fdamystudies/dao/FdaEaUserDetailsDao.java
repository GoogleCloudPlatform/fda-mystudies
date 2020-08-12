/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;

public interface FdaEaUserDetailsDao {

  UserDetailsBO saveUser(UserDetailsBO userDetailsBO) throws SystemException;

  UserDetailsBO loadUserDetailsByUserId(String userId) throws SystemException;

  UserDetailsBO loadEmailCodeByUserId(String userId) throws SystemException;

  boolean updateStatus(UserDetailsBO participantDetails);

  boolean saveAllRecords(
      UserDetailsBO userDetailsBO, AuthInfoBO authInfo, UserAppDetailsBO userAppDetails)
      throws SystemException;
}
