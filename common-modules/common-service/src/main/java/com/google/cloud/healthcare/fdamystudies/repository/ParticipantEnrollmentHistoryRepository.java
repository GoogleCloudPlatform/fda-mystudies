/*
 * Copyright 2020 Google LLC
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
              + "peh.status AS enrollmentStatus, peh.withdrawal_time AS withdrawalDate,  peh.enrolled_time AS enrolledDate, loc.custom_id AS locationCustomId, loc.name AS locationName "
              + "FROM participant_enrollment_history peh, locations loc, sites s "
              + "WHERE peh.site_id=s.id AND s.location_id=loc.id AND peh.status IN ('enrolled','withdrawn') AND "
              + "peh.user_details_id IN (:userIds) AND peh.app_info_id=:appId "
              + "ORDER BY loc.name DESC",
      nativeQuery = true)
  public List<ParticipantEnrollmentHistory> findParticipantEnrollmentHistoryByAppId(
      String appId, List<String> userIds);

  @Query(
      value =
          "SELECT peh.status AS enrollmentStatus, peh.withdrawal_time AS withdrawalDate,  peh.enrolled_time AS enrolledDate "
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
          "UPDATE participant_enrollment_history SET status=:status, withdrawal_time= now() WHERE "
              + "withdrawal_time IS NULL AND study_info_id=:studyId AND "
              + "(user_details_id =:userId OR participant_registry_site_id=:participantRegistryId)",
      nativeQuery = true)
  public void updateWithdrawalDateAndStatus(
      String userId, String studyId, String status, String participantRegistryId);

  @Modifying
  @Transactional
  @Query(
      value =
          "UPDATE participant_enrollment_history SET status=:status, withdrawal_time= now() WHERE "
              + "withdrawal_time IS NULL AND user_details_id =:userId",
      nativeQuery = true)
  public void updateWithdrawalDateAndStatusForDeactivatedUser(String userId, String status);
}
