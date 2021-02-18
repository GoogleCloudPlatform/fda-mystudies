/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.EnrolledInvitedCount;
import com.google.cloud.healthcare.fdamystudies.model.SiteCount;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudySiteInfo;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface SiteRepository extends JpaRepository<SiteEntity, String> {

  @Query(
      "SELECT site from SiteEntity site where site.location.id = :locationId and site.study.id= :studyId")
  public List<SiteEntity> findByLocationIdAndStudyId(String locationId, String studyId);

  @Query(
      "SELECT site from SiteEntity site where site.location.id = :locationId and site.status= :status")
  public List<SiteEntity> findByLocationIdAndStatus(String locationId, Integer status);

  @Query("SELECT site from SiteEntity site where site.study.id IN (:studyIds)")
  public List<SiteEntity> findByStudyIds(@Param("studyIds") List<String> studyIds);

  @Query("SELECT site from SiteEntity site where site.study.id= :studyId")
  public Optional<SiteEntity> findByStudyId(String studyId);

  @Query("SELECT site from SiteEntity site where site.study.id= :studyId and site.study.type=:type")
  public Optional<SiteEntity> findByStudyIdAndType(String studyId, String type);

  @Query(
      value =
          "SELECT study.id AS studyId, IFNULL(COUNT(st.id),0) AS count "
              + "FROM sites st, study_info study "
              + "WHERE study.id = st.study_id GROUP BY study.id ",
      nativeQuery = true)
  public List<SiteCount> findStudySitesCount();

  @Modifying
  @Query(
      value =
          "SELECT invites.site_id AS siteId, invites.invitedCount, IFNULL(enrolled.enrolledCount, 0) AS enrolledCount "
              + "FROM ( "
              + "SELECT prs.site_id, COUNT(prs.onboarding_status) AS invitedCount "
              + "FROM participant_registry_site prs, sites_permissions sp "
              + "WHERE prs.site_id=sp.site_id AND prs.onboarding_status='I' AND sp.ur_admin_user_id =:userId "
              + "GROUP BY prs.site_id) AS invites "
              + "LEFT JOIN ( "
              + "SELECT ps.site_id, COUNT(ps.site_id) AS enrolledCount "
              + "FROM participant_study_info ps, sites_permissions sp "
              + "WHERE ps.site_id=sp.site_id AND ps.status='enrolled' AND sp.ur_admin_user_id =:userId "
              + "GROUP BY ps.site_id) AS enrolled ON invites.site_id=enrolled.site_id ",
      nativeQuery = true)
  public List<EnrolledInvitedCount> getEnrolledInvitedCountByUserId(@Param("userId") String userId);

  @Query(
      value =
          "SELECT invites.site_id AS siteId, invites.invitedCount, IFNULL(enrolled.enrolledCount, 0) AS enrolledCount "
              + "FROM "
              + "( "
              + "SELECT prs.site_id, COUNT(prs.onboarding_status) AS invitedCount "
              + "FROM participant_registry_site prs, sites si, study_info st "
              + "WHERE prs.site_id=si.id AND si.study_id=st.id AND st.type='CLOSE' AND prs.onboarding_status='I' "
              + "GROUP BY prs.site_id "
              + ") AS invites "
              + "LEFT JOIN "
              + "( "
              + "SELECT ps.site_id, COUNT(ps.site_id) AS enrolledCount "
              + "FROM participant_study_info ps, sites si "
              + "WHERE ps.site_id=si.id AND ps.status='enrolled' "
              + "GROUP BY ps.site_id "
              + ") AS enrolled ON invites.site_id=enrolled.site_id ",
      nativeQuery = true)
  public List<EnrolledInvitedCount> getEnrolledInvitedCount();

  @Query("SELECT site from SiteEntity site where site.study.id= :studyId")
  public List<SiteEntity> findSitesByStudyId(String studyId);

  @Modifying
  @Transactional
  @Query(
      value =
          "INSERT INTO sites_permissions (id, ur_admin_user_id, study_id, app_info_id, edit, created_by, created_time, site_id) "
              + "SELECT CONCAT(SUBSTRING(MD5(RAND()) FROM 1 FOR 8), SUBSTRING(MD5(RAND()) FROM 1 FOR 32)), ur_admin_user_id, study_id, app_info_id, edit, created_by, NOW(), :siteId "
              + "FROM study_permissions "
              + "WHERE study_id=:studyId",
      nativeQuery = true)
  public void addSitePermissions(String studyId, String siteId);

  @Query(
      value =
          "SELECT study_created AS studyCreatedTimeStamp, site_created AS siteCreatedTimeStamp, study_id AS studyId, site_id AS siteId, IFNULL(target_enrollment, 0) AS targetEnrollment, site_name AS siteName, custom_id AS customId, study_name AS studyName, TYPE AS studyType, custom_app_id AS customAppId, app_id AS appId, app_name AS appName, logo_image_url AS logoImageUrl, STATUS AS studyStatus, edit AS editPermission, study_permission AS studyPermission "
              + "FROM( "
              + "SELECT DISTINCT stu.created_time AS study_created, si.created_time AS site_created, sp.study_id, si.id AS site_id, si.target_enrollment AS target_enrollment, loc.name AS site_name,stu.custom_id AS custom_id,stu.name AS study_name, stu.type AS TYPE, ai.custom_app_id AS custom_app_id, ai.id AS app_id, ai.app_name AS app_name,stu.logo_image_url AS logo_image_url,stu.status AS STATUS, sp.edit AS edit, 1 AS study_permission "
              + "FROM study_permissions sp "
              + "LEFT JOIN study_info stu ON stu.id= sp.study_id "
              + "LEFT JOIN sites si ON si.study_id=stu.id "
              + "LEFT JOIN locations loc ON loc.id=si.location_id "
              + "LEFT JOIN app_info ai ON ai.id=stu.app_info_id "
              + "WHERE sp.ur_admin_user_id=:userId AND sp.study_id IN (:studyIds) UNION ALL "
              + "SELECT DISTINCT stu.created_time AS study_created, si.created_time AS site_created, sp.study_id, si.id AS site_id, si.target_enrollment AS target_enrollment, loc.name AS site_name,stu.custom_id AS custom_id,stu.name AS study_name, stu.type AS TYPE, ai.custom_app_id AS custom_app_id, ai.id AS app_id, ai.app_name AS app_name,stu.logo_image_url AS logo_image_url,stu.status AS STATUS, sp.edit AS edit, 0 AS study_permission "
              + "FROM sites_permissions sp, sites si, locations loc, study_info stu, app_info ai "
              + "WHERE si.id=sp.site_id AND sp.ur_admin_user_id =:userId AND si.location_id=loc.id AND si.status=1 AND stu.id= sp.study_id AND stu.app_info_id =ai.id "
              + "AND sp.study_id NOT IN ( "
              + "SELECT study_id FROM study_permissions WHERE ur_admin_user_id =:userId)  "
              + ")rstAlias "
              + "WHERE study_id IN (:studyIds) AND (study_name LIKE %:searchTerm% OR custom_id LIKE %:searchTerm% OR (site_name LIKE %:searchTerm% AND TYPE='CLOSE'))  "
              + "ORDER BY study_created DESC",
      nativeQuery = true)
  public List<StudySiteInfo> getStudySiteDetails(
      String userId, List<String> studyIds, String searchTerm);

  @Query(
      value =
          "SELECT si.id AS siteId, IFNULL(COUNT(psi.site_id), 0) AS enrolledCount "
              + "FROM sites si , participant_study_info psi "
              + "WHERE si.id=psi.site_id AND psi.status='enrolled' "
              + "GROUP BY si.id ",
      nativeQuery = true)
  public List<EnrolledInvitedCount> findEnrolledCountForOpenStudy();

  @Query(
      value =
          "SELECT distinct invites.study_id AS siteId, invites.invitedCount , IFNULL(enrolled.enrolledCount, 0) AS enrolledCount "
              + "FROM ( "
              + "SELECT si.study_id, si.target_enrollment AS invitedCount "
              + "FROM sites si, study_info st, sites_permissions sp "
              + "WHERE si.study_id=st.id AND sp.ur_admin_user_id=:userId "
              + "AND sp.study_id=si.study_id AND st.type='OPEN' "
              + ") AS invites "
              + "LEFT JOIN ( "
              + "SELECT ps.study_info_id, COUNT(ps.study_info_id) AS enrolledCount "
              + "FROM participant_study_info ps, sites_permissions sp "
              + "WHERE ps.site_id=sp.site_id AND ps.status='enrolled' AND sp.ur_admin_user_id =:userId "
              + "GROUP BY ps.study_info_id) AS enrolled ON invites.study_id=enrolled.study_info_id ",
      nativeQuery = true)
  public List<EnrolledInvitedCount> getInvitedEnrolledCountForOpenStudy(
      @Param("userId") String userId);
}
