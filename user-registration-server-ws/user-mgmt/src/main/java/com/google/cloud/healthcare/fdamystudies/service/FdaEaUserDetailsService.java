/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import javax.transaction.Transactional;
import com.google.cloud.healthcare.fdamystudies.beans.VerifyCodeResponse;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidEmailCodeException;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;

public interface FdaEaUserDetailsService {

  @Transactional
  UserDetailsBO saveUser(UserDetailsBO userDetailsBO) throws SystemException;

  UserDetailsBO loadUserDetailsByUserId(String userId) throws SystemException;

  VerifyCodeResponse verifyCode(String code, String userId)
      throws SystemException, InvalidEmailCodeException, InvalidUserIdException;
}
