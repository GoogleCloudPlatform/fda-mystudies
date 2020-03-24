/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UserNotFoundException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;

public interface UserSessionService {

  public String deleteTokenExpireDateByUserId(String userId)
      throws UserNotFoundException, SystemException;

  public AuthInfoBO loadSessionByUserId(String userId) throws SystemException;

  public AuthInfoBO save(AuthInfoBO authInfo) throws SystemException;
}
