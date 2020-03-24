/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.RefreshTokenBean;
import com.google.cloud.healthcare.fdamystudies.controller.bean.AuthInfoBean;
import com.google.cloud.healthcare.fdamystudies.controller.bean.CheckCredentialRequest;
import com.google.cloud.healthcare.fdamystudies.controller.bean.RegisterUser;
import com.google.cloud.healthcare.fdamystudies.controller.bean.ResponseBean;
import com.google.cloud.healthcare.fdamystudies.controller.bean.UpdateInfo;
import com.google.cloud.healthcare.fdamystudies.exception.DuplicateUserRegistrationException;
import com.google.cloud.healthcare.fdamystudies.exception.EmailIdAlreadyVerifiedException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidClientException;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UserNotFoundException;
import com.google.cloud.healthcare.fdamystudies.model.DaoUserBO;
import com.google.cloud.healthcare.fdamystudies.model.LoginAttemptsBO;
import com.google.cloud.healthcare.fdamystudies.service.bean.RefreshTokenServiceResponse;
import com.google.cloud.healthcare.fdamystudies.service.bean.ServiceRegistrationSuccessResponse;

public interface UserDetailsService {

  public DaoUserBO loadUserByEmailIdAndAppIdAndOrgIdAndAppCode(
      String userName, String appId, String orgId, String appCode) throws SystemException;

  public ServiceRegistrationSuccessResponse save(
      RegisterUser user, String appId, String orgId, String appCode)
      throws DuplicateUserRegistrationException, SystemException;

  public Integer validateAccessToken(AuthInfoBean authInfo) throws SystemException;

  public RefreshTokenServiceResponse generateNewTokens(
      RefreshTokenBean refreshToken, String userId, String appCode)
      throws SystemException, UserNotFoundException, InvalidUserIdException, InvalidClientException;

  public boolean verify(CheckCredentialRequest checkCredentialRequest) throws SystemException;

  public String updateStatus(UpdateInfo userInfo, String userId)
      throws SystemException, UserNotFoundException, EmailIdAlreadyVerifiedException;

  public String deleteUserDetails(String userId) throws UserNotFoundException, SystemException;

  public int sendPasswordResetLinkthroughEmail(
      String emailId, String tempPassword, DaoUserBO participantDetails);

  public DaoUserBO loadUserByEmailIdAndAppCode(String emailId, String appCode)
      throws SystemException;

  public LoginAttemptsBO getLoginAttempts(String email);

  public void resetLoginAttempts(String email);

  public DaoUserBO loadUserByUserId(String userId) throws SystemException;

  public ResponseBean changePassword(DaoUserBO userInfo);

  public ResponseBean deactivateAcct(DaoUserBO userInfo);

  public LoginAttemptsBO updateLoginFailureAttempts(String email);

  public DaoUserBO saveUserDetails(DaoUserBO participant);

  public Boolean getPasswordHistory(String userId, String newPassword);

  public String savePasswordHistory(String userId, String password, String salt);
}
