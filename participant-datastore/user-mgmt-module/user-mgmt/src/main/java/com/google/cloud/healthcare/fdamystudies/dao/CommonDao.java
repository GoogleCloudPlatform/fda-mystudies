/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.beans.AppOrgInfoBean;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;

public interface CommonDao {

  public String validatedUserAppDetailsByAllApi(String userId, String email, String appId);

  public AppOrgInfoBean getUserAppDetailsByAllApi(String userId, String appId);

  public String getUserInfoDetails(String userId);

  public List<AppEntity> getAppInfoSet(HashSet<String> appIds);

  public List<StudyEntity> getStudyInfoSet(HashSet<String> studyInfoSet);

  public Map<String, Map<String, JSONArray>> getStudyLevelDeviceToken(
      List<StudyEntity> studyInfoIds);

  public String getParticipantId(String id, String customStudyId);
}
