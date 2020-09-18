/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.PersonalizedUserReportEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonalizedUserReportRepository
    extends JpaRepository<PersonalizedUserReportEntity, Integer> {

  public List<PersonalizedUserReportEntity> findByUserDetailsUserIdAndStudyInfoCustomId(
      String userId, String studyCustomId);
}
