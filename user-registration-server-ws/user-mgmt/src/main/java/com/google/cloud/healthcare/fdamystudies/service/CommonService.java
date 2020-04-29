/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.exceptions.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.exceptions.UnAuthorizedRequestException;
import com.google.cloud.healthcare.fdamystudies.model.AuditLogBo;

public interface CommonService {

  public String validatedUserAppDetailsByAllApi(
      String userId, String email, String appId, String orgId);

  public Integer validateAccessToken(String userId, String accessToken, String clientToken);

  public AppOrgInfoBean getUserAppDetailsByAllApi(
      String userId, String emailId, String appId, String orgId);

  public AuditLogBo createAuditLog(
      String userId,
      String activityName,
      String activtyDesc,
      String clientId,
      String participantId,
      String studyId,
      String accessLevel);

  public boolean validateServerClientCredentials(String clientId, String clientSecret)
      throws SystemException, UnAuthorizedRequestException, InvalidRequestException;
}
