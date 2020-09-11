/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.StudyInfoBO;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;

public interface CommonDao {

  public String validatedUserAppDetailsByAllApi(String userId, String email, int appId);

  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId);

  public Integer getUserInfoDetails(String userId);

  public List<AppInfoDetailsBO> getAppInfoSet(HashSet<String> appIds);

  public List<StudyInfoBO> getStudyInfoSet(HashSet<String> studyInfoSet);

  public Map<Integer, Map<String, JSONArray>> getStudyLevelDeviceToken(
      List<StudyInfoBO> studyInfoIds);

  public String getParticicpantId(Integer id, String customeStudyId);
}
