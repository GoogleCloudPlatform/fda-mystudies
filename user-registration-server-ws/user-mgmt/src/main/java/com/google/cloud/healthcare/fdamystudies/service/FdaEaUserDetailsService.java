/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidEmailCodeException;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;

public interface FdaEaUserDetailsService {

  @Transactional
  UserDetailsBO saveUser(UserDetailsBO userDetailsBO) throws SystemException;

  UserDetailsBO loadUserDetailsByUserId(String userId) throws SystemException;

  boolean verifyCode(String code, String userId)
      throws SystemException, InvalidEmailCodeException, InvalidUserIdException;

  boolean updateStatus(UserDetailsBO participantDetails)
      throws InvalidRequestException, SystemException;
}
