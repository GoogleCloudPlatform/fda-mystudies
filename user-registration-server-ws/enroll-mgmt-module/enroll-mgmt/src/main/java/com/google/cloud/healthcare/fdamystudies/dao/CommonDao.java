/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.enroll.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.enroll.model.UserDetailsBO;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonDao {

  public UserDetailsBO getUserInfoDetails(String userId);

  public Integer getStudyId(String customStudyId);

  public StudyInfoBO getStudyDetails(String customStudyId);
}
