/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.AppCount;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppParticipantsInfo;
import com.google.cloud.healthcare.fdamystudies.model.AppSiteInfo;
import com.google.cloud.healthcare.fdamystudies.model.AppStudyInfo;
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
public interface AppRepository extends JpaRepository<AppEntity, String> {

  @Query("SELECT app from AppEntity app where app.appId=:appId")
  public Optional<AppEntity> findByAppId(String appId);

  @Query(
      value =
          "SELECT appId, SUM(target_invited_count) AS COUNT "
              + "FROM ( "
              + "SELECT app.id AS appId, SUM(site.target_enrollment) AS target_invited_count "
              + "FROM app_info app, study_info AS si, sites site "
              + "WHERE app.id=si.app_info_id AND si.id=site.study_id AND si.type='OPEN' "
              + "GROUP BY app.id UNION ALL "
              + "SELECT app.id AS appId, COUNT(prs.onboarding_status) AS target_invited_count "
              + "FROM app_info app, study_info AS si, participant_registry_site prs "
              + "WHERE app.id=si.app_info_id AND si.id=prs.study_info_id "
              + "AND prs.onboarding_status='I' AND si.type='CLOSE' "
              + "GROUP BY app.id "
              + ") rstAlias "
              + "GROUP BY appId ",
      nativeQuery = true)
  public List<AppCount> findInvitedCountByAppId();

  @Query(
      value =
          "SELECT app.id AS appId, COUNT(psi.site_id) AS count "
              + "FROM app_info app, study_info AS si, participant_study_info psi "
              + "WHERE app.id=si.app_info_id AND si.id=psi.study_info_id "
              + "AND psi.status='inProgress' "
              + "GROUP BY app.id ",
      nativeQuery = true)
  public List<AppCount> findEnrolledCountByAppId();

  @Query(
      value =
          "SELECT appId, SUM(target_invited_count) AS COUNT "
              + "FROM ( "
              + "SELECT app.id AS appId, SUM(site.target_enrollment) AS target_invited_count "
              + "FROM app_info app, study_info AS si, sites site, sites_permissions sp "
              + "WHERE app.id=si.app_info_id AND si.id=site.study_id AND app.id=sp.app_info_id "
              + "AND site.id=sp.site_id AND si.type='OPEN' AND sp.ur_admin_user_id = :userId "
              + "GROUP BY app.id UNION ALL "
              + "SELECT app.id AS appId, COUNT(prs.onboarding_status) AS target_invited_count "
              + "FROM app_info app, study_info AS si, participant_registry_site prs, sites_permissions sp "
              + "WHERE app.id=si.app_info_id AND si.id=prs.study_info_id AND prs.onboarding_status='I' "
              + "AND si.type='CLOSE' AND app.id=sp.app_info_id "
              + "AND prs.site_id=sp.site_id AND sp.ur_admin_user_id = :userId "
              + "GROUP BY app.id "
              + ") rstAlias "
              + "GROUP BY appId ",
      nativeQuery = true)
  public List<AppCount> findInvitedCountByAppId(@Param("userId") String userId);

  @Query(
      value =
          "SELECT created_time AS createdTimeStamp, app_info_id AS appId, custom_app_id AS customAppId, app_name AS appName, COUNT(study_id) As studyCount "
              + "FROM( "
              + "SELECT ai.created_time, sp.app_info_id, ai.custom_app_id, ai.app_name, sp.study_id "
              + "FROM study_permissions sp, app_info ai "
              + "WHERE ai.id=sp.app_info_id AND sp.ur_admin_user_id = :userId AND sp.study_id IN (SELECT sp.study_id FROM sites_permissions sp WHERE sp.ur_admin_user_id = :userId) "
              + "UNION ALL "
              + "SELECT DISTINCT ai.created_time, sp.app_info_id, ai.custom_app_id, ai.app_name, sp.study_id "
              + "FROM sites_permissions sp, app_info ai, sites s "
              + "WHERE ai.id=sp.app_info_id AND s.id=sp.site_id AND s.status=1 AND sp.ur_admin_user_id = :userId AND sp.study_id NOT IN ( "
              + "SELECT st.study_id "
              + "FROM study_permissions st "
              + "WHERE st.ur_admin_user_id = :userId)) rstAlias GROUP BY created_time,app_info_id,custom_app_id,app_name "
              + "ORDER BY created_time DESC ",
      nativeQuery = true)
  public List<AppStudyInfo> findAppsByUserId(@Param("userId") String userId);

  @Query(
      value =
          "SELECT sp.app_info_id as appId, COUNT(ps.site_id) AS count "
              + "FROM participant_study_info ps, sites_permissions sp "
              + "WHERE ps.site_id=sp.site_id AND ps.status='inProgress' "
              + "AND sp.ur_admin_user_id = :userId "
              + "GROUP BY sp.app_info_id ",
      nativeQuery = true)
  public List<AppCount> findEnrolledCountByAppId(String userId);

  @Query(
      value =
          "SELECT sp.app_info_id as appId, COUNT(ps.site_id) AS count "
              + "FROM participant_study_info ps, sites_permissions sp, study_info si, sites st "
              + "WHERE si.id=sp.study_id AND ps.site_id=sp.site_id AND si.type='OPEN' AND ps.status='inProgress' AND st.id=sp.site_id "
              + "AND (st.target_enrollment=0 OR st.target_enrollment IS NULL) "
              + "AND sp.ur_admin_user_id = :userId "
              + "GROUP BY sp.app_info_id",
      nativeQuery = true)
  public List<AppCount> findEnrolledWithoutTarget(String userId);

  @Query(
      value =
          "SELECT ai.id as appId, COUNT(ps.site_id) AS count "
              + "FROM participant_study_info ps, study_info si, sites st, app_info ai "
              + "WHERE ai.id=si.app_info_id AND si.id=st.study_id AND ps.site_id=st.id AND si.type='OPEN' AND ps.status='inProgress' "
              + "AND (st.target_enrollment=0 OR st.target_enrollment IS NULL) "
              + "GROUP BY appId",
      nativeQuery = true)
  public List<AppCount> findEnrolledWithoutTarget();

  @Query(
      value =
          "SELECT DISTINCT ud.id AS userDetailsId, ud.email AS email,ud.status AS registrationStatus, ud.verification_time AS registrationDate, "
              + "st.name AS studyName, st.id AS studyId, st.custom_id AS customStudyId, st.type AS studyType,ps.status AS participantStudyStatus, ps.withdrawal_time AS withdrawalTime,ps.enrolled_time AS enrolledTime "
              + "FROM user_details ud "
              + "LEFT JOIN participant_study_info ps ON ud.id = ps.user_details_id "
              + "LEFT JOIN study_info st ON st.id=ps.study_info_id  AND ps.status NOT IN (:excludeParticipantStudyStatus) "
              + "WHERE ud.app_info_id=:appId "
              + "ORDER BY ud.verification_time,ud.id DESC ",
      nativeQuery = true)
  public List<AppParticipantsInfo> findUserDetailsByAppIdAndStudyStatus(
      String appId, String[] excludeParticipantStudyStatus);

  @Query(
      value =
          "SELECT DISTINCT ud.id AS userDetailsId, ud.email AS email,ud.status AS registrationStatus, ud.verification_time AS registrationDate, "
              + "st.name AS studyName, st.id AS studyId, st.custom_id AS customStudyId, st.type AS studyType,ps.status AS participantStudyStatus, ps.withdrawal_time AS withdrawalTime,ps.enrolled_time AS enrolledTime "
              + "FROM user_details ud "
              + "LEFT JOIN participant_study_info ps ON ud.id = ps.user_details_id "
              + "LEFT JOIN study_info st ON st.id=ps.study_info_id "
              + "WHERE ud.app_info_id=:appId "
              + "ORDER BY ud.verification_time,ud.id DESC ",
      nativeQuery = true)
  public List<AppParticipantsInfo> findUserDetailsByAppId(String appId);

  @Query(
      value =
          "SELECT ud.id AS userDetailsId, psi.site_id as siteId, psi.study_info_id AS studyId, loc.custom_id AS locationCustomId, loc.name AS locationName "
              + "FROM participant_study_info psi, locations loc, sites s, user_details ud "
              + "WHERE ud.id=psi.user_details_id AND psi.study_info_id=s.study_id AND psi.site_id=s.id AND loc.id=s.location_id AND "
              + "ud.app_info_id=:appId AND psi.status NOT IN (:excludeParticipantStudyStatus) "
              + "AND ud.id IN (:userIds)",
      nativeQuery = true)
  public List<AppSiteInfo> findSitesByAppIdAndStudyStatusAndUserIds(
      String appId, String[] excludeParticipantStudyStatus, List<String> userIds);

  @Query(
      value =
          "SELECT ud.id AS userDetailsId, psi.site_id as siteId, psi.study_info_id AS studyId, loc.custom_id AS locationCustomId, loc.name AS locationName "
              + "FROM participant_study_info psi, locations loc, sites s, user_details ud "
              + "WHERE ud.id=psi.user_details_id AND psi.study_info_id=s.study_id AND psi.site_id=s.id AND loc.id=s.location_id AND "
              + "ud.app_info_id=:appId AND ud.id IN (:userIds) ",
      nativeQuery = true)
  public List<AppSiteInfo> findSitesByAppIdAndUserIds(String appId, List<String> userIds);
}
