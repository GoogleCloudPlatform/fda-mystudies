/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.RoleBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public interface UsersService {

  public String activateOrDeactivateUser(
      int userId,
      int userStatus,
      int loginUser,
      SessionObject userSession,
      HttpServletRequest request);

  public String addOrUpdateUserDetails(
      HttpServletRequest request,
      UserBO userBO,
      String permissions,
      String selectedStudies,
      String permissionValues,
      SessionObject userSession,
      AuditLogEventRequest auditRequest);

  public String enforcePasswordChange(Integer userId, String email);

  public List<String> getActiveUserEmailIds();

  public List<Integer> getPermissionsByUserId(Integer userId);

  public UserBO getUserDetails(Integer userId);

  public List<UserBO> getUserList();

  public Integer getUserPermissionByUserId(Integer sessionUserId);

  public RoleBO getUserRole(int roleId);

  public List<RoleBO> getUserRoleList();
}
