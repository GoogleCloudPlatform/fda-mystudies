/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.InviteParticipantEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyIdAndParticipantRegistryId;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface InviteParticipantsEmailRepository
    extends JpaRepository<InviteParticipantEntity, String> {

  @Query(
      value =
          "SELECT DISTINCT study_info_id as studyId, participant_registry_site_id as participantRegistryId FROM invite_participants WHERE status = 0",
      nativeQuery = true)
  public List<StudyIdAndParticipantRegistryId> findAllWithStatusZero();

  @Modifying
  @Query(
      value =
          "UPDATE invite_participants set status=:newStatus WHERE study_info_id =:studyInfoId and participant_registry_site_id=:participantRegistryId",
      nativeQuery = true)
  public int updateStatus(
      @Param("studyInfoId") String studyInfoId,
      @Param("participantRegistryId") String participantRegistryId,
      @Param("newStatus") int newStatus);

  @Modifying
  @Query(
      value =
          "DELETE from invite_participants WHERE study_info_id =:studyInfoId and participant_registry_site_id=:participantRegistryId",
      nativeQuery = true)
  public int deleteByParticipantRegistryIdAndStudyInfoId(
      String studyInfoId, String participantRegistryId);
}
