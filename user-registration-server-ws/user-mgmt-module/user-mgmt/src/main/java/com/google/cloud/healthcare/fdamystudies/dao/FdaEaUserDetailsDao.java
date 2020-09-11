/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserAppDetailsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;

public interface FdaEaUserDetailsDao {

  UserDetailsBO saveUser(UserDetailsBO userDetailsBO);

  UserDetailsBO loadUserDetailsByUserId(String userId);

  UserDetailsBO loadEmailCodeByUserId(String userId);

  boolean updateStatus(UserDetailsBO participantDetails);

  boolean saveAllRecords(
      UserDetailsBO userDetailsBO, AuthInfoBO authInfo, UserAppDetailsBO userAppDetails);
}
