/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantEnrollmentHistory;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantEnrollmentHistoryEntity;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface ParticipantEnrollmentHistoryRepository
    extends JpaRepository<ParticipantEnrollmentHistoryEntity, String> {
  @Query(
      value =
          "SELECT peh.site_id AS siteId, peh.user_details_id AS userDetailsId, peh.study_info_id AS studyId, "
              + "peh.status AS enrollmentStatus, peh.withdrawal_timestamp AS withdrawalDate,  peh.enrolled_timestamp AS enrolledDate, loc.custom_id AS locationCustomId, loc.name AS locationName "
              + "FROM participant_enrollment_history peh, locations loc, sites s "
              + "WHERE peh.site_id=s.id AND s.location_id=loc.id AND peh.status IN ('enrolled','withdrawn') AND "
              + "peh.user_details_id IN (:userIds) AND peh.app_info_id=:appId "
              + "ORDER BY peh.created_time DESC",
      nativeQuery = true)
  public List<ParticipantEnrollmentHistory> findParticipantEnrollmentHistoryByAppId(
      String appId, List<String> userIds);

  @Query(
      value =
          "SELECT peh.status AS enrollmentStatus, peh.withdrawal_timestamp AS withdrawalDate,  peh.enrolled_timestamp AS enrolledDate "
              + "FROM participant_enrollment_history peh "
              + "WHERE peh.site_id=:siteId AND peh.study_info_id=:studyId AND "
              + "peh.participant_registry_site_id=:participantRegistryId AND peh.app_info_id=:appId "
              + "ORDER BY peh.created_time DESC",
      nativeQuery = true)
  public List<ParticipantEnrollmentHistory> findByAppIdSiteIdAndStudyId(
      String appId, String studyId, String siteId, String participantRegistryId);

  @Modifying
  @Transactional
  @Query(
      value =
          "UPDATE participant_enrollment_history SET status=:status, withdrawal_timestamp= now() WHERE "
              + "withdrawal_timestamp IS NULL AND study_info_id=:studyId AND user_details_id =:userDetailsId",
      nativeQuery = true)
  public void updateWithdrawalDateAndStatus(String userDetailsId, String studyId, String status);

  @Modifying
  @Transactional
  @Query(
      value =
          "UPDATE participant_enrollment_history SET status=:status, withdrawal_timestamp= now() WHERE "
              + "withdrawal_timestamp IS NULL AND user_details_id =:userDetailsId",
      nativeQuery = true)
  public void updateWithdrawalDateAndStatusForDeactivatedUser(String userDetailsId, String status);

  @Query(
      value =
          "SELECT peh.status "
              + "FROM participant_enrollment_history peh "
              + "WHERE peh.site_id=:siteId AND peh.participant_registry_site_id=:participantRegistryId "
              + "ORDER BY peh.created_time DESC LIMIT 1",
      nativeQuery = true)
  public String findBySiteIdAndParticipantRegistryId(String siteId, String participantRegistryId);

  @Query(
      value =
          "SELECT peh.status FROM participant_enrollment_history peh WHERE study_info_id=:studyId AND "
              + "participant_registry_site_id=:participantRegistrySiteId ORDER BY created_time DESC LIMIT 1",
      nativeQuery = true)
  public String findByStudyIdAndParticipantRegistrySiteId(
      String studyId, String participantRegistrySiteId);
}
