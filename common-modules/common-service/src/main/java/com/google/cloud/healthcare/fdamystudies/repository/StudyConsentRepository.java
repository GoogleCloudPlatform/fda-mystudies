/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface StudyConsentRepository extends JpaRepository<StudyConsentEntity, String> {

  @Query(
      "SELECT sc FROM StudyConsentEntity sc WHERE sc.participantStudy.id in (:participantStudyIds)")
  public List<StudyConsentEntity> findByParticipantRegistrySiteId(List<String> participantStudyIds);

  @Query("SELECT sc FROM StudyConsentEntity sc WHERE sc.id =:consentId")
  Optional<StudyConsentEntity> findByConsentId(String consentId);
}
