/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface CommonDao {

  public UserDetailsEntity getUserInfoDetails(String userId);

  public String getStudyId(String customStudyId);

  public StudyEntity getStudyDetails(String customStudyId);
}
