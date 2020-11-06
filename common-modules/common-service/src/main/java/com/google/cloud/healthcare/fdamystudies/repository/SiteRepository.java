/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.EnrolledInvitedCount;
import com.google.cloud.healthcare.fdamystudies.model.SiteCount;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import java.util.List;
import java.util.Optional;
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
              + "WHERE ps.site_id=sp.site_id AND ps.status='inProgress' AND sp.ur_admin_user_id =:userId "
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
              + "WHERE ps.site_id=si.id AND ps.status='inProgress' "
              + "GROUP BY ps.site_id "
              + ") AS enrolled ON invites.site_id=enrolled.site_id ",
      nativeQuery = true)
  public List<EnrolledInvitedCount> getEnrolledInvitedCount();

  @Query("SELECT site from SiteEntity site where site.study.id= :studyId")
  public List<SiteEntity> findSitesByStudyId(String studyId);
}
