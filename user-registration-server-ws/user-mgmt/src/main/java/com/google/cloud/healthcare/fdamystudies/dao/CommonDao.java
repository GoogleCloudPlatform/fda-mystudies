/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;

public interface CommonDao {

  public String validatedUserAppDetailsByAllApi(String userId, String email, int appId, int orgId);

  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId, String orgId);

  public Integer getUserInfoDetails(String userId);
}
