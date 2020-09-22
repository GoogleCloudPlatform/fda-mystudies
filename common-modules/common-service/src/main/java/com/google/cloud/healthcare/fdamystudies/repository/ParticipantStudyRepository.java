/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

  @Query("SELECT COUNT(ps) FROM ParticipantStudyEntity ps WHERE ps.study.id = :studyId")
  public Long countbyStudyId(String studyId);

  @Query(
      "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.participantRegistrySite.id = :participantRegistrySiteId")
  public Optional<ParticipantStudyEntity> findByParticipantRegistrySiteId(
      String participantRegistrySiteId);

  @Query(
      "SELECT COUNT(ps.id) FROM ParticipantStudyEntity ps  "
          + "WHERE ps.status IN(:status) AND ps.study.id=:studyId")
  public Optional<Long> findByStudyIdAndStatus(List<String> status, String studyId);

  @Query(
      "SELECT participantStudy from ParticipantStudyEntity participantStudy "
          + "where participantStudy.site.id = :siteId and participantStudy.status = :status")
  public List<ParticipantStudyEntity> findBySiteIdAndStatus(String siteId, String status);

  @Query(
      "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.study.id in (:appsStudyInfoIds) AND ps.userDetails.id in (:userDetailsIds)")
  public List<ParticipantStudyEntity> findByAppIdAndUserId(
      @Param("appsStudyInfoIds") List<String> appsStudyInfoIds,
      @Param("userDetailsIds") List<String> userDetailsIds);

  @Query(
      "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.participantRegistrySite.id = :participantRegistrySiteId")
  public List<ParticipantStudyEntity> findParticipantsEnrollment(String participantRegistrySiteId);

  @Query("SELECT ps FROM ParticipantStudyEntity ps WHERE ps.site.id in (:siteIds)")
  public List<ParticipantStudyEntity> findBySiteIds(@Param("siteIds") List<String> usersSiteIds);

  @Query(
      "SELECT ps FROM ParticipantStudyEntity ps WHERE ps.participantRegistrySite.id in (:registryIds)")
  public List<ParticipantStudyEntity> findParticipantsByParticipantRegistrySite(
      List<String> registryIds);
}
