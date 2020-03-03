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

import java.util.List;
import com.google.cloud.healthcare.fdamystudies.bean.RegisterUser;
import com.google.cloud.healthcare.fdamystudies.bean.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminUser;

public interface UserRegAdminUserDao {

  String saveDetails(RegisterUser user, String userId) throws SystemException;

  UserRegAdminUser checkPermission(Integer adminId) throws SystemException;

  Boolean checkDuplicateEntryUsingEmailId(String email) throws SystemException;

  Boolean checkEmailIdExists(String email) throws SystemException;

  List<UserRegAdminUser> getAllRecords() throws SystemException;

  String updateUser(SetUpAccountRequest request, String authUserId) throws SystemException;
}
