/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import java.util.List;
import com.google.cloud.healthcare.fdamystudies.bean.RegisterUser;
import com.google.cloud.healthcare.fdamystudies.bean.SetUpAccountRequest;
import com.google.cloud.healthcare.fdamystudies.bean.SetUpAccountResponse;
import com.google.cloud.healthcare.fdamystudies.bean.User;
import com.google.cloud.healthcare.fdamystudies.exception.DuplicateEntryFoundException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidEmailIdException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UserNotInvited;

public interface ManageUserService {

  String saveUser(String userId, RegisterUser user)
      throws SystemException, InvalidUserIdException, DuplicateEntryFoundException;

  List<User> getUsers(String userId) throws SystemException, InvalidUserIdException;

  SetUpAccountResponse saveUser(SetUpAccountRequest request)
      throws SystemException, UserNotInvited, InvalidEmailIdException;
}
