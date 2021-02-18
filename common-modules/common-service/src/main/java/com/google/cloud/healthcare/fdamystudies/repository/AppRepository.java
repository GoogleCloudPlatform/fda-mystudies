/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.AppCount;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.AppParticipantsInfo;
import com.google.cloud.healthcare.fdamystudies.model.AppStudyInfo;
import com.google.cloud.healthcare.fdamystudies.model.AppStudySiteInfo;
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
              + "AND psi.status='enrolled' "
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
              + "WHERE st.ur_admin_user_id = :userId)) rstAlias "
              + "WHERE app_name LIKE %:searchTerm% OR custom_app_id LIKE %:searchTerm% "
              + "GROUP BY created_time,app_info_id,custom_app_id,app_name "
              + "ORDER BY created_time DESC LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  public List<AppStudyInfo> findAppsByUserId(
      @Param("userId") String userId, Integer limit, Integer offset, String searchTerm);

  @Query(
      value =
          "SELECT sp.app_info_id as appId, COUNT(ps.site_id) AS count "
              + "FROM participant_study_info ps, sites_permissions sp "
              + "WHERE ps.site_id=sp.site_id AND ps.status='enrolled' "
              + "AND sp.ur_admin_user_id = :userId "
              + "GROUP BY sp.app_info_id ",
      nativeQuery = true)
  public List<AppCount> findEnrolledCountByAppId(String userId);

  @Query(
      value =
          "SELECT sp.app_info_id as appId, COUNT(ps.site_id) AS count "
              + "FROM participant_study_info ps, sites_permissions sp, study_info si, sites st "
              + "WHERE si.id=sp.study_id AND ps.site_id=sp.site_id AND si.type='OPEN' AND ps.status='enrolled' AND st.id=sp.site_id "
              + "AND (st.target_enrollment=0 OR st.target_enrollment IS NULL) "
              + "AND sp.ur_admin_user_id = :userId "
              + "GROUP BY sp.app_info_id",
      nativeQuery = true)
  public List<AppCount> findEnrolledWithoutTarget(String userId);

  @Query(
      value =
          "SELECT ai.id as appId, COUNT(ps.site_id) AS count "
              + "FROM participant_study_info ps, study_info si, sites st, app_info ai "
              + "WHERE ai.id=si.app_info_id AND si.id=st.study_id AND ps.site_id=st.id AND si.type='OPEN' AND ps.status='enrolled' "
              + "AND (st.target_enrollment=0 OR st.target_enrollment IS NULL) "
              + "GROUP BY appId",
      nativeQuery = true)
  public List<AppCount> findEnrolledWithoutTarget();

  @Query(
      value =
          "SELECT ud.id AS userDetailsId, ud.email AS email,ud.status AS registrationStatus, ud.verification_time AS registrationDate, "
              + "st.name AS studyName, st.id AS studyId, st.custom_id AS customStudyId, st.type AS studyType, peh.created_time "
              + "FROM user_details ud "
              + "LEFT JOIN participant_enrollment_history peh ON ud.id = peh.user_details_id "
              + "LEFT JOIN study_info st ON st.id=peh.study_info_id "
              + "WHERE ud.app_info_id=:appId AND ud.id IN (:userDetailIds) "
              + "ORDER BY CASE :orderByCondition WHEN 'email_asc' THEN ud.email END ASC, "
              + "         CASE :orderByCondition WHEN 'registrationDate_asc' THEN ud.verification_time END ASC, "
              + "         CASE :orderByCondition WHEN 'registrationStatus_asc' THEN ud.status END ASC, "
              + "         CASE :orderByCondition WHEN 'email_desc' THEN ud.email END DESC, "
              + "         CASE :orderByCondition WHEN 'registrationDate_desc' THEN ud.verification_time END DESC, "
              + "         CASE :orderByCondition WHEN 'registrationStatus_desc' THEN ud.status END DESC, "
              + "peh.created_time DESC ",
      nativeQuery = true)
  public List<AppParticipantsInfo> findUserDetailsByAppId(
      String appId, List<String> userDetailIds, String orderByCondition);

  @Query(
      value =
          "SELECT * FROM app_info "
              + "WHERE app_name LIKE %:searchTerm% OR custom_app_id LIKE %:searchTerm% "
              + "ORDER BY created_time DESC LIMIT :limit OFFSET :offset ",
      nativeQuery = true)
  public List<AppEntity> findAll(Integer limit, Integer offset, String searchTerm);

  @Query(
      value =
          "SELECT ai.id AS appId, ai.app_name AS appName, 'app' AS permissionLevel, ai.custom_app_id AS customAppId, si.name AS studyName, loc.name AS locationName, loc.custom_id AS locationCustomId, sp.edit AS edit, sp.study_id AS studyId, sp.site_id AS siteId, si.custom_id AS customStudyId, loc.id AS locationId, loc.description AS locationDescription "
              + "FROM app_info ai, study_info si, sites st, sites_permissions sp, locations loc "
              + "WHERE ai.id=sp.app_info_id AND sp.study_id=si.id AND sp.site_id=st.id AND st.location_id=loc.id AND sp.ur_admin_user_id=:userId "
              + "AND sp.app_info_id IN (SELECT ap.app_info_id FROM app_permissions ap WHERE ap.ur_admin_user_id=:userId) "
              + "UNION ALL "
              + "SELECT ai.id AS appId, ai.app_name AS appName, 'study' AS permissionLevel, ai.custom_app_id AS customAppId, si.name AS studyName, loc.name AS locationName, loc.custom_id AS locationCustomId, sp.edit AS edit, sp.study_id AS studyId, sp.site_id AS siteId, si.custom_id AS customStudyId, loc.id AS locationId, loc.description AS locationDescription "
              + "FROM app_info ai, study_info si, sites st, sites_permissions sp, locations loc "
              + "WHERE ai.id=sp.app_info_id AND sp.study_id=si.id AND sp.site_id=st.id AND st.location_id=loc.id AND sp.ur_admin_user_id=:userId "
              + "AND sp.app_info_id NOT IN (SELECT ap.app_info_id FROM app_permissions ap WHERE ap.ur_admin_user_id=:userId) "
              + "AND sp.study_id IN (SELECT study_id FROM study_permissions WHERE ur_admin_user_id=:userId) "
              + "UNION ALL "
              + "SELECT ai.id AS appId, ai.app_name AS appName, 'site' AS permissionLevel, ai.custom_app_id AS customAppId, si.name AS studyName, loc.name AS locationName, loc.custom_id AS locationCustomId, sp.edit AS edit, sp.study_id AS studyId, sp.site_id AS siteId, si.custom_id AS customStudyId, loc.id AS locationId, loc.description AS locationDescription "
              + "FROM app_info ai, study_info si, sites st, sites_permissions sp, locations loc "
              + "WHERE ai.id=sp.app_info_id AND sp.study_id=si.id AND sp.site_id=st.id AND st.location_id=loc.id AND sp.ur_admin_user_id=:userId "
              + "AND sp.app_info_id NOT IN (SELECT ap.app_info_id FROM app_permissions ap WHERE ap.ur_admin_user_id=:userId) "
              + "AND sp.study_id NOT IN (SELECT study_id FROM study_permissions WHERE ur_admin_user_id=:userId) "
              + "UNION ALL "
              + "SELECT DISTINCT ai.id AS appId, ai.app_name AS appName, 'study' AS permissionLevel, ai.custom_app_id AS customAppId, si.name AS studyName, NULL as locationName, NULL AS locationCustomId, sp.edit AS edit, sp.study_id AS studyId, null AS siteId, null AS customStudyId, null AS locationId, null AS locationDescription  "
              + "FROM app_info ai, study_info si, sites st, study_permissions sp "
              + "WHERE ai.id=sp.app_info_id AND sp.study_id=si.id AND sp.ur_admin_user_id=:userId "
              + "AND sp.app_info_id NOT IN (SELECT ap.app_info_id FROM app_permissions ap WHERE ap.ur_admin_user_id=:userId) "
              + "AND sp.study_id NOT IN (SELECT study_id FROM sites_permissions WHERE ur_admin_user_id=:userId) "
              + "UNION ALL "
              + "SELECT DISTINCT ai.id AS appId, ai.app_name AS appName, 'app' AS permissionLevel, ai.custom_app_id AS customAppId, si.name AS studyName, NULL as locationName, NULL AS locationCustomId, sp.edit AS edit, sp.study_id AS studyId, null AS siteId, null AS customStudyId, null AS locationId, null AS locationDescription "
              + "FROM app_info ai, study_info si, sites st, study_permissions sp "
              + "WHERE ai.id=sp.app_info_id AND sp.study_id=si.id AND sp.ur_admin_user_id=:userId "
              + "AND sp.app_info_id IN (SELECT ap.app_info_id FROM app_permissions ap WHERE ap.ur_admin_user_id=:userId) "
              + "AND sp.study_id NOT IN (SELECT study_id FROM sites_permissions WHERE ur_admin_user_id=:userId) ",
      nativeQuery = true)
  public List<AppStudySiteInfo> findAppsStudiesSitesByUserId(@Param("userId") String userId);

  @Query(
      value =
          "SELECT ai.id AS appId, ai.app_name AS appName, ai.custom_app_id AS customAppId, si.name AS studyName, loc.name AS locationName, loc.custom_id AS locationCustomId, si.id AS studyId, st.id AS siteId, si.custom_id AS customStudyId, loc.id AS locationId, loc.description AS locationDescription "
              + "FROM app_info ai, study_info si, sites st, locations loc "
              + "WHERE st.location_id=loc.id AND st.study_id=si.id AND si.app_info_id=ai.id "
              + "AND si.id NOT IN (SELECT study_id FROM study_permissions WHERE app_info_id IN (:appIds) AND ur_admin_user_id= :userId) "
              + "AND st.id NOT IN (SELECT site_id FROM sites_permissions WHERE app_info_id IN (:appIds) AND ur_admin_user_id=:userId) "
              + "AND ai.id IN (:appIds) "
              + "UNION ALL "
              + "SELECT ai.id AS appId, ai.app_name AS appName, ai.custom_app_id AS customAppId, si.name AS studyName, null AS locationName, null AS locationCustomId, si.id AS studyId, null AS siteId, si.custom_id AS customStudyId, null AS locationId, null AS locationDescription "
              + "FROM app_info ai, study_info si "
              + "WHERE si.app_info_id=ai.id "
              + "AND si.id NOT IN (SELECT study_id FROM study_permissions WHERE app_info_id IN (:appIds) AND ur_admin_user_id=:userId) "
              + "AND si.id NOT IN (SELECT study.id FROM sites site, study_info study WHERE study.id = site.study_id AND study.app_info_id IN (:appIds)) "
              + "AND ai.id IN (:appIds) ",
      nativeQuery = true)
  public List<AppStudySiteInfo> findUnselectedAppsStudiesSites(
      List<String> appIds, @Param("userId") String userId);

  @Query(
      value =
          "SELECT ud.id FROM user_details ud "
              + "WHERE ud.app_info_id=:appId "
              + "AND ud.status != :excludeStatus "
              + "AND ud.email LIKE %:searchTerm% "
              + "ORDER BY CASE :orderByCondition WHEN 'email_asc' THEN ud.email END ASC, "
              + "         CASE :orderByCondition WHEN 'registrationDate_asc' THEN ud.verification_time END ASC, "
              + "         CASE :orderByCondition WHEN 'registrationStatus_asc' THEN ud.status END ASC, "
              + "         CASE :orderByCondition WHEN 'email_desc' THEN ud.email END DESC, "
              + "         CASE :orderByCondition WHEN 'registrationDate_desc' THEN ud.verification_time END DESC, "
              + "         CASE :orderByCondition WHEN 'registrationStatus_desc' THEN ud.status END DESC "
              + "LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  public List<String> findUserDetailIds(
      String appId,
      Integer excludeStatus,
      Integer limit,
      Integer offset,
      String orderByCondition,
      String searchTerm);

  @Query(
      value =
          "SELECT COUNT(ud.id) FROM user_details ud "
              + "WHERE ud.app_info_id=:appId AND ud.status != :excludeStatus "
              + "AND ud.email LIKE %:searchTerm% ",
      nativeQuery = true)
  public Long countParticipantByAppIdAndSearchTerm(
      String appId, Integer excludeStatus, String searchTerm);

  @Query(
      value =
          "SELECT ai.id AS appId, ai.app_name AS appName, ai.custom_app_id AS customAppId, si.name AS studyName, loc.name AS locationName, loc.custom_id AS locationCustomId, si.id AS studyId, st.id AS siteId, si.custom_id AS customStudyId, loc.id AS locationId, loc.description AS locationDescription "
              + "FROM app_info ai, study_info si, sites st, locations loc "
              + "WHERE st.location_id=loc.id AND st.study_id=si.id AND si.app_info_id=ai.id "
              + "UNION ALL "
              + "SELECT ai.id AS appId, ai.app_name AS appName, ai.custom_app_id AS customAppId, si.name AS studyName, null AS locationName, null AS locationCustomId, si.id AS studyId, null AS siteId, si.custom_id AS customStudyId, null AS locationId, null AS locationDescription "
              + "FROM app_info ai, study_info si "
              + "WHERE si.app_info_id=ai.id "
              + "AND si.id NOT IN (SELECT study.id FROM sites site, study_info study WHERE study.id = site.study_id)",
      nativeQuery = true)
  public List<AppStudySiteInfo> findAppsStudiesSites();
}
