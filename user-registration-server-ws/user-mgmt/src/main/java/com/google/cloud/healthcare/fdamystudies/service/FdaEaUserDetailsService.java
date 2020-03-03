/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
/** */
package com.google.cloud.healthcare.fdamystudies.service;

import javax.transaction.Transactional;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidUserIdOrEmailCodeException;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;

/**
 * Project Name: UserManagementServiceBundle
 *
 * @author Chiranjibi Dash, Date: 03-Jan-2020, Time: 3:38:55 pm
 */
public interface FdaEaUserDetailsService {

  @Transactional
  UserDetails saveUser(UserDetails userDetails) throws SystemException;

  UserDetails loadUserDetailsByUserId(String userId) throws SystemException;

  // List<UserDetails> loadParticipantDetailsListByEmail(String emailId);

  boolean verifyCode(String code, String userId)
      throws SystemException, InvalidUserIdOrEmailCodeException;
}
