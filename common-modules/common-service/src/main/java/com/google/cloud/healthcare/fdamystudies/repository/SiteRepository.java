/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.SiteCount;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
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
}
