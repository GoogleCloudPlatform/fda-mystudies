/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.model.ActivityLogBO;

public interface CommonService {

  public Integer validateAccessToken(String userId, String accessToken, String clientToken);

  public Integer getUserDetailsId(String userId);

  public ActivityLogBO createActivityLog(String userId, String activityName, String activtyDesc);
}
