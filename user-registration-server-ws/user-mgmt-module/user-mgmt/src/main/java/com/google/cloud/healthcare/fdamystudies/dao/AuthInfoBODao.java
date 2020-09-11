/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AuthInfoBO;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;

public interface AuthInfoBODao {
  AuthInfoBO save(AuthInfoBO authInfo);

  Map<String, JSONArray> getDeviceTokenOfAllUsers(List<AppInfoDetailsBO> appInfos);
}
