/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.EnrolledInvitedCount;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
@Repository
public interface ParticipantStudyRepository extends JpaRepository<ParticipantStudyEntity, String> {

  @Query("SELECT ps FROM ParticipantStudyEntity ps WHERE ps.site.id in (:siteIds)")
  public List<ParticipantStudyEntity> findParticipantEnrollmentsBySiteIds(
      @Param("siteIds") List<String> usersSiteIds);

  @Query("SELECT ps FROM ParticipantStudyEntity ps WHERE ps.study.id = :studyId")
  public Page<ParticipantStudyEntity> findParticipantsByStudyForPage(
      @Param("studyId") String studyId, Pageable page);

  @Query("SELECT ps FROM ParticipantStudyEntity ps WHERE ps.study.id = :studyId")
  public List<ParticipantStudyEntity> findParticipantsByStudy(@Param("studyId") String studyId);

  @Query(
      "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.participantRegistrySite.id = :participantRegistrySiteId")
  public Optional<ParticipantStudyEntity> findByParticipantRegistrySiteId(
      String participantRegistrySiteId);

  @Query(
      "SELECT COUNT(ps.id) FROM ParticipantStudyEntity ps  "
          + "WHERE ps.status IN(:status) AND ps.study.id=:studyId AND ps.site.id=:siteId")
  public Optional<Long> findByStudyIdSiteIdAndStatus(
      List<String> status, String studyId, String siteId);

  @Query(
      "SELECT participantStudy from ParticipantStudyEntity participantStudy "
          + "where participantStudy.site.id = :siteId and participantStudy.status = :status")
  public List<ParticipantStudyEntity> findBySiteIdAndStatus(String siteId, String status);

  @Query(
      "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.study.id in (:studyIds) AND ps.userDetails.id in (:userIds)")
  public List<ParticipantStudyEntity> findByStudyIdsAndUserIds(
      @Param("studyIds") List<String> studyIds, @Param("userIds") List<String> userIds);

  @Query(
      "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.participantRegistrySite.id = :participantRegistrySiteId")
  public List<ParticipantStudyEntity> findParticipantsEnrollment(String participantRegistrySiteId);

  @Query("SELECT ps FROM ParticipantStudyEntity ps WHERE ps.site.id in (:siteIds)")
  public List<ParticipantStudyEntity> findBySiteIds(@Param("siteIds") List<String> usersSiteIds);

  @Query(
      "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.participantRegistrySite.id in (:registryIds)")
  public List<ParticipantStudyEntity> findParticipantsByParticipantRegistrySite(
      List<String> registryIds);

  @Query("SELECT ps FROM ParticipantStudyEntity ps WHERE ps.participantId = :participantId")
  public Optional<ParticipantStudyEntity> findByParticipantId(String participantId);

  @Query(
      value =
          "SELECT ps.site_id AS siteId, COUNT(ps.site_id) AS enrolledCount FROM participant_study_info ps WHERE ps.status='enrolled' AND ps.site_id IN (:siteIds) GROUP BY ps.site_id ",
      nativeQuery = true)
  public List<EnrolledInvitedCount> getEnrolledCountForOpenStudy(List<String> siteIds);

  @Query(
      "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.study.id = :studyId AND ps.userDetails.id = :userId")
  public Optional<ParticipantStudyEntity> findByStudyIdAndUserId(
      @Param("studyId") String studyId, @Param("userId") String userId);

  @Modifying
  @Query(
      "update ParticipantStudyEntity ps set ps.status=:enrollmentStatus, ps.enrolledDate=null, ps.withdrawalDate=null WHERE ps.participantRegistrySite.id IN (:ids)")
  public void updateEnrollmentStatus(List<String> ids, String enrollmentStatus);

  @Query(
      value =
          "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.study.id = :studyId AND ps.userDetails.userId = :userId AND ps.site.id = :siteId")
  public Optional<ParticipantStudyEntity> findByStudyIdAndSiteId(
      String studyId, String userId, String siteId);

  @Query(
      value =
          "SELECT ps.id "
              + "FROM participant_registry_site prs, participant_study_info ps, study_info stu "
              + "WHERE prs.id=ps.participant_registry_site_id AND stu.id=ps.study_info_id AND prs.email=:email AND stu.custom_id IN (:studyCustomIds) AND ps.status IN ('yetToEnroll','notEligible','withdrawn') ",
      nativeQuery = true)
  public List<String> findByEmailAndStudyCustomIds(String email, List<String> studyCustomIds);

  @Query(
      value =
          "SELECT ps.id "
              + "FROM participant_registry_site prs, participant_study_info ps "
              + "WHERE prs.id=ps.participant_registry_site_id AND prs.email=:email AND ps.site_id IN (:siteIds)",
      nativeQuery = true)
  public List<String> findByEmailAndSiteIds(String email, List<String> siteIds);

  @Query(
      value =
          " SELECT ps.id from  participant_study_info ps, user_details ud "
              + "WHERE ps.user_details_id=ud.id AND ps.study_info_id=:studyId "
              + "AND ud.user_id=:userId ",
      nativeQuery = true)
  public List<String> findByStudyIdAndUserDetailId(String studyId, String userId);

  @Query(
      value =
          "SELECT ps.site_id FROM participant_registry_site prs, participant_study_info ps, user_details ud "
              + "WHERE prs.id=ps.participant_registry_site_id AND ud.user_id=:userId AND upper(prs.enrollment_token)=:token ",
      nativeQuery = true)
  public String getSiteId(String userId, String token);
}
