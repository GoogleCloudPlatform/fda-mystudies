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
package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;

/**
 * Project Name: MyStudies-UserReg-WS
 *
 * @author Chiranjibi Dash, Date: Dec 18, 2019, Time: 5:33:21 PM
 */
public interface FdaEaUserDetailsDao {

  // save user details in user_details table
  UserDetails saveUser(UserDetails userDetails) throws SystemException;

  UserDetails loadUserDetailsByUserId(String userId) throws SystemException;

  UserDetails loadEmailCodeByUserId(String userId) throws SystemException;
}
