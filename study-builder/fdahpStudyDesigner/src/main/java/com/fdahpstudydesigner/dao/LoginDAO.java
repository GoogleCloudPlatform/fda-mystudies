/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.UserAttemptsBo;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.bo.UserPasswordHistory;
import java.util.List;

public interface LoginDAO {

  public String changePassword(String userId, String newPassword, String oldPassword);

  public List<UserPasswordHistory> getPasswordHistory(String userId);

  public UserAttemptsBo getUserAttempts(String userEmailId);

  public UserBO getUserBySecurityToken(String securityToken);

  public UserBO getValidUserByEmail(String email);

  public Boolean isFrocelyLogOutUser(String userId);

  public Boolean isUserEnabled(String userId);

  public void passwordLoginBlocked();

  public void resetFailAttempts(String userEmailId);

  public void updateFailAttempts(String userEmailId, AuditLogEventRequest auditRequest);

  public String updatePasswordHistory(String userId, String userPassword);

  public String updateUser(UserBO userBO);

  public String updateUserForResetPassword(UserBO userBO);
}
