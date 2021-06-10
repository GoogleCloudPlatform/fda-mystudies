/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.bean.UserIdAccessLevelInfo;
import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;

public interface UsersDAO {

  public String activateOrDeactivateUser(
      String userId, int userStatus, String loginUser, SessionObject userSession);

  public UserIdAccessLevelInfo addOrUpdateUserDetails(
      UserBO userBO, String permissions, String selectedStudies, String permissionValues);

  public String enforcePasswordChange(String userId, String email);

  public List<String> getActiveUserEmailIds();

  public List<Integer> getPermissionsByUserId(String userId);

  public List<String> getSuperAdminList();

  public UserBO getSuperAdminNameByEmailId(String emailId);

  public UserBO getUserDetails(String userId);

  public List<UserBO> getUserList();

  public String getUserPermissionByUserId(String sessionUserId);

  public RoleBO getUserRole(String roleId);

  public List<RoleBO> getUserRoleList();
}
