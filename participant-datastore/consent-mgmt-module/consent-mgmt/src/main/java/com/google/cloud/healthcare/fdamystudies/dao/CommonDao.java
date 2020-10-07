/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.bean.AppOrgInfoBean;

public interface CommonDao {

  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId);

  public String getUserDetailsId(String userId);
}
