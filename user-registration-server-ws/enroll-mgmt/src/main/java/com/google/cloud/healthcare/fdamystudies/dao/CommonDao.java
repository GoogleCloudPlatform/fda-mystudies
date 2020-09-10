/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.ActivityLogBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonDao {

  public UserDetailsBO getUserInfoDetails(String userId);

  public Integer getStudyId(String customStudyId);

  public StudyInfoBO getStudyDetails(String customStudyId);

  public List<ActivityLogBO> createActivityLogList(
      String userId, String activityName, List<String> activityDescList);

  public ActivityLogBO createActivityLog(String userId, String activityName, String activtyDesc);
}
