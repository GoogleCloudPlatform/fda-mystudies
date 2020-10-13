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
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
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
          "SELECT app.id AS appId, COUNT(study.id) AS count "
              + "FROM study_info study, app_info app "
              + "WHERE app.id = study.app_info_id GROUP BY app.id ",
      nativeQuery = true)
  public List<AppCount> findAppStudiesCount();
}
