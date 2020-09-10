/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
import com.google.cloud.healthcare.fdamystudies.model.ActivityLogBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import java.util.List;

public interface CommonService {

  public Integer validateAccessToken(String userId, String accessToken, String clientToken);

  public UserDetailsBO getUserInfoDetails(String userId);

  public List<ActivityLogBO> createActivityLogList(
      String userId, String activityName, List<String> activityDescList);

  public ActivityLogBO createActivityLog(String userId, String activityName, String activtyDesc);

  boolean validateServerClientCredentials(String clientId, String clientSecret)
      throws SystemException, UnAuthorizedRequestException, InvalidRequestException;
}
