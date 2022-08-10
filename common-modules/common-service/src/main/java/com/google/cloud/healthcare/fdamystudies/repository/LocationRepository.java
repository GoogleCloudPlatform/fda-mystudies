/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface LocationRepository extends JpaRepository<LocationEntity, String> {

  @Query(
      value =
          "SELECT * FROM locations WHERE is_default!='Y' AND status = :status AND "
              + "id NOT IN (SELECT DISTINCT location_id FROM sites WHERE study_id = :excludeStudyId)",
      nativeQuery = true)
  public List<LocationEntity> findByStatusAndExcludeStudyId(Integer status, String excludeStudyId);

  public Optional<LocationEntity> findByCustomId(String customId);

  public Optional<LocationEntity> findByName(String name);

  @Query(
      value =
          "SELECT * FROM locations WHERE is_default!='Y' AND (LOWER(custom_id) LIKE %:searchTerm% OR LOWER(name) LIKE %:searchTerm%) "
              + "ORDER BY CASE :orderByCondition WHEN 'locationId_asc' THEN custom_id END ASC, "
              + "         CASE :orderByCondition WHEN 'locationName_asc' THEN name END ASC, "
              + "         CASE :orderByCondition WHEN 'status_asc' THEN status END ASC, "
              + "         CASE :orderByCondition WHEN 'status_desc' THEN status END DESC, "
              + "         CASE :orderByCondition WHEN 'locationId_desc' THEN custom_id END DESC, "
              + "         CASE :orderByCondition WHEN 'locationName_desc' THEN name END DESC "
              + "LIMIT :limit OFFSET :offset ",
      nativeQuery = true)
  public List<LocationEntity> findAll(
      Integer limit, Integer offset, String orderByCondition, String searchTerm);

  @Query(
      value =
          "SELECT count(id) FROM locations WHERE is_default!='Y' AND (custom_id LIKE %:searchTerm% OR name LIKE %:searchTerm%) ",
      nativeQuery = true)
  public Long countLocationBySearchTerm(String searchTerm);

  @Query(value = "SELECT * FROM locations WHERE is_default='Y'", nativeQuery = true)
  public Optional<LocationEntity> findByDefault();

  @Query(
      value =
          "SELECT study.id "
              + "FROM sites AS site "
              + "INNER JOIN study_info AS study "
              + "ON site.study_id = study.id WHERE site.location_id IN (SELECT id FROM locations WHERE is_default ='Y') AND study.type=:type",
      nativeQuery = true)
  public List<String> findByDefaultLocationForStudy(String type);

  @Modifying
  @Transactional
  @Query(value = "update locations set is_default='N' WHERE is_default='Y'", nativeQuery = true)
  public void updateLocation();
}
