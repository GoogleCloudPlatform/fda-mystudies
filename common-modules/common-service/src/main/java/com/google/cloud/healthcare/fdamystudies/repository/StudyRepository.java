/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.AppCount;
import com.google.cloud.healthcare.fdamystudies.model.LocationIdStudyNamesPair;
import com.google.cloud.healthcare.fdamystudies.model.StudyAppDetails;
import com.google.cloud.healthcare.fdamystudies.model.StudyCount;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyParticipantDetails;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface StudyRepository extends JpaRepository<StudyEntity, String> {

  @Query("SELECT study from StudyEntity study where study.id=:studyId")
  public Optional<StudyEntity> findByStudyId(String studyId);

  @Query("SELECT study from StudyEntity study where study.app.id in (:appIds)")
  public List<StudyEntity> findByAppIds(@Param("appIds") List<String> appIds);

  @Query(
      value =
          "SELECT s.location_id AS locationId, GROUP_CONCAT(DISTINCT si.name SEPARATOR ',') AS studyNames from sites s, study_info si where s.study_id=si.id AND s.location_id in (:locationIds) GROUP BY s.location_id",
      nativeQuery = true)
  public List<LocationIdStudyNamesPair> getStudyNameLocationIdPairs(List<String> locationIds);

  @Query(
      value =
          "SELECT GROUP_CONCAT(DISTINCT si.name SEPARATOR ',') from sites s, study_info si "
              + "where s.study_id=si.id AND s.location_id = :locationId",
      nativeQuery = true)
  public String getStudyNamesByLocationId(String locationId);

  @Query("SELECT study FROM StudyEntity study where study.app.id = :appInfoId")
  public List<StudyEntity> findByAppId(String appInfoId);

  @Query(
      value =
          "SELECT app.id AS appId, IFNULL(COUNT(study.id), 0) AS COUNT "
              + "FROM study_info study, app_info app "
              + "WHERE app.id = study.app_info_id AND (select count(si.id) from sites si where si.study_id=study.id) >0 "
              + "GROUP BY app.id",
      nativeQuery = true)
  public List<AppCount> findAppStudiesCount();

  @Query(
      value =
          "SELECT studyId, SUM(target_invited_count) AS COUNT "
              + "FROM ( "
              + "SELECT study.id AS studyId, SUM(site.target_enrollment) AS target_invited_count "
              + "FROM study_info study, sites site "
              + "WHERE study.id=site.study_id AND study.type='OPEN' "
              + "GROUP BY study.id UNION ALL "
              + "SELECT study.id AS studyId, COUNT(prs.onboarding_status) AS target_invited_count "
              + "FROM study_info study, participant_registry_site prs "
              + "WHERE study.id=prs.study_info_id AND prs.onboarding_status='I' AND study.type='CLOSE' "
              + "GROUP BY study.id "
              + ") rstAlias "
              + "GROUP BY studyId ",
      nativeQuery = true)
  public List<StudyCount> findInvitedCountByStudyId();

  @Query(
      value =
          "SELECT study.id AS studyId, COUNT(psi.site_id) AS COUNT "
              + "FROM study_info study, participant_study_info psi "
              + "WHERE study.id=psi.study_info_id AND psi.status='inProgress' "
              + "GROUP BY study.id ",
      nativeQuery = true)
  public List<StudyCount> findEnrolledCountByStudyId();

  @Query(
      value =
          "SELECT DISTINCT si.id AS studyId, si.name AS studyName, si.custom_id AS customStudyId,si.type AS studyType, "
              + "ai.id AS appId,ai.app_name AS appName,ai.custom_app_id AS customAppId, "
              + "st.target_enrollment AS targetEnrollment, stp.edit AS edit "
              + "FROM app_info ai, study_info si, sites st,sites_permissions stp "
              + "WHERE ai.id=si.app_info_id AND st.study_id=si.id AND si.id=:studyId AND stp.study_id=si.id AND stp.ur_admin_user_id=:userId ",
      nativeQuery = true)
  public Optional<StudyAppDetails> getStudyAppDetails(String studyId, String userId);

  @Query(
      value =
          "SELECT DISTINCT si.id AS studyId, si.name AS studyName, si.custom_id AS customStudyId,si.type AS studyType, "
              + "ai.id AS appId,ai.app_name AS appName,ai.custom_app_id AS customAppId, "
              + "st.target_enrollment AS targetEnrollment "
              + "FROM app_info ai, study_info si, sites st "
              + "WHERE ai.id=si.app_info_id AND st.study_id=si.id AND si.id=:studyId ",
      nativeQuery = true)
  public Optional<StudyAppDetails> getStudyAppDetailsForSuperAdmin(String studyId);

  @Query(
      value =
          "SELECT prs.created_time AS createdTime, prs.email AS email, psi.enrolled_time AS enrolledDate, psi.status AS enrolledStatus "
              + ",prs.site_id AS siteId, prs.onboarding_status AS onboardingStatus, "
              + "loc.name AS locationName, loc.custom_id AS locationCustomId, "
              + "prs.invitation_time AS invitedDate, prs.id AS participantId "
              + "FROM participant_registry_site prs "
              + "LEFT JOIN participant_study_info psi ON prs.id=psi.participant_registry_site_id "
              + "LEFT JOIN sites si ON si.id=prs.site_id "
              + "LEFT JOIN locations loc ON loc.id=si.location_id "
              + "WHERE prs.study_info_id=:studyId "
              + "ORDER BY prs.created_time, prs.email DESC",
      nativeQuery = true)
  public List<StudyParticipantDetails> getStudyParticipantDetails(String studyId);
}
