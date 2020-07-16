/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;

public interface CommonDao {

  public String validatedUserAppDetailsByAllApi(String userId, String email, int appId, int orgId);

  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId, String orgId);

  public Integer getUserInfoDetails(String userId);

  public List<AppInfoDetailsBO> getAppInfoSet(HashSet<String> appIds);

  public List<StudyInfoBO> getStudyInfoSet(HashSet<String> studyInfoSet);

  public Map<Integer, Map<String, JSONArray>> getStudyLevelDeviceToken(
      List<StudyInfoBO> studyInfoIds);

  public String getParticicpantId(Integer id, String customeStudyId);
}
