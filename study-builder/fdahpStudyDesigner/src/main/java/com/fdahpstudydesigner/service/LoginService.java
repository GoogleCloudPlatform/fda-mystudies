/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.SessionObject;
import javax.servlet.http.HttpServletRequest;

public interface LoginService {

  public String authAndAddPassword(
      String securityToken, String password, UserBO userBO, SessionObject sesObj);

  public String changePassword(
      String userId, String newPassword, String oldPassword, SessionObject sesObj);

  public UserBO checkSecurityToken(String securityToken);

  public Boolean isFrocelyLogOutUser(SessionObject sessionObject);

  public Boolean isUserEnabled(SessionObject sessionObject);

  public Boolean logUserLogOut(SessionObject sessionObject);

  public String sendPasswordResetLinkToMail(
      HttpServletRequest request,
      String email,
      String oldEmail,
      String type,
      AuditLogEventRequest auditRequest);

  public void sendLockedAccountPasswordResetLinkToMail(
      String email, AuditLogEventRequest auditRequest);

  public boolean isInactiveUser(String securityToken);

  public boolean isIntialPasswordSetUp(String securityToken);
}
