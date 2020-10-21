/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteCount;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
public interface ParticipantRegistrySiteRepository
    extends JpaRepository<ParticipantRegistrySiteEntity, String> {

  @Query("SELECT pr FROM ParticipantRegistrySiteEntity pr WHERE pr.site.id in (:siteIds)")
  public List<ParticipantRegistrySiteEntity> findBySiteIds(@Param("siteIds") List<String> siteIds);

  @Query(
      "SELECT pr FROM ParticipantRegistrySiteEntity pr WHERE pr.study.id = :studyId and pr.email = :email")
  public Optional<ParticipantRegistrySiteEntity> findByStudyIdAndEmail(
      String studyId, String email);

  @Query(
      "SELECT pr.onboardingStatus AS onboardingStatus, count(pr.email) AS count FROM ParticipantRegistrySiteEntity pr WHERE pr.site.id= :siteId group by pr.onboardingStatus")
  public List<ParticipantRegistrySiteCount> findStatusCountBySiteId(String siteId);

  @Query(
      "SELECT pr FROM ParticipantRegistrySiteEntity pr "
          + "where pr.site.id = :siteId and pr.onboardingStatus = :onboardingStatus order by created desc")
  public Page<ParticipantRegistrySiteEntity> findBySiteIdAndStatusForPage(
      String siteId, String onboardingStatus, Pageable pageable);

  @Query(
      "SELECT pr FROM ParticipantRegistrySiteEntity pr "
          + "where pr.site.id = :siteId and pr.onboardingStatus = :onboardingStatus order by created desc")
  public List<ParticipantRegistrySiteEntity> findBySiteIdAndStatus(
      String siteId, String onboardingStatus);

  @Query("SELECT COUNT(pr) FROM ParticipantRegistrySiteEntity pr WHERE pr.site.id=:siteId")
  public Long countbysiteId(String siteId);

  @Query(
      "SELECT COUNT(pr) FROM ParticipantRegistrySiteEntity pr where pr.site.id = :siteId and pr.onboardingStatus = :onboardingStatus")
  public Long countBySiteIdAndStatus(String siteId, String onboardingStatus);

  @Query("SELECT pr FROM ParticipantRegistrySiteEntity pr WHERE pr.site.id =:siteId")
  public Page<ParticipantRegistrySiteEntity> findBySiteIdForPage(String siteId, Pageable pageable);

  @Query("SELECT pr FROM ParticipantRegistrySiteEntity pr WHERE pr.site.id =:siteId")
  public List<ParticipantRegistrySiteEntity> findBySiteId(String siteId);

  @Query("SELECT pr FROM ParticipantRegistrySiteEntity pr WHERE pr.id in (:ids)")
  public List<ParticipantRegistrySiteEntity> findByIds(@Param("ids") List<String> ids);

  @Query(
      value =
          "SELECT pr FROM ParticipantRegistrySiteEntity pr "
              + "where pr.study.id = :studyId and pr.email IN (:emails)")
  public List<ParticipantRegistrySiteEntity> findByStudyIdAndEmails(
      String studyId, Set<String> emails);

  @Modifying
  @Query(
      "update ParticipantRegistrySiteEntity pr set pr.onboardingStatus=:status where pr.id IN (:ids)")
  public void updateOnboardingStatus(@Param("status") String status, List<String> ids);

  @Query("SELECT pr FROM ParticipantRegistrySiteEntity pr WHERE pr.study.id=:studyId")
  public List<ParticipantRegistrySiteEntity> findByStudyId(String studyId);

  @Query("SELECT pr FROM ParticipantRegistrySiteEntity pr WHERE pr.study.id=:studyId")
  public Page<ParticipantRegistrySiteEntity> findByStudyIdForPagination(
      String studyId, Pageable pageable);
}
