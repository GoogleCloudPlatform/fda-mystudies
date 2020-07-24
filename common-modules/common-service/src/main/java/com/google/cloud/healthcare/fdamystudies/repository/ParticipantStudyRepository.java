/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.repository;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;

@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
@Repository
public interface ParticipantStudyRepository extends JpaRepository<ParticipantStudyEntity, String> {

  @Query("SELECT ps FROM ParticipantStudyEntity ps WHERE ps.site.id in (:siteIds)")
  public List<ParticipantStudyEntity> findParticipantEnrollmentsBySiteIds(
      @Param("siteIds") List<String> usersSiteIds);

  @Query("SELECT ps FROM ParticipantStudyEntity ps WHERE ps.study.id in (:studyIds)")
  public List<ParticipantStudyEntity> findParticipantsByStudy(@Param("studyIds") String studyIds);
}
