/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
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
public interface AppPermissionRepository extends JpaRepository<AppPermissionEntity, String> {

  @Query(
      "SELECT ap from AppPermissionEntity ap where ap.urAdminUser.id=:adminId and ap.app.id=:appId")
  public Optional<AppPermissionEntity> findByUserIdAndAppId(String adminId, String appId);

  @Query(
      "SELECT ap FROM AppPermissionEntity ap WHERE ap.app.id IN (:appIds) AND ap.urAdminUser.id=:userId")
  public List<AppPermissionEntity> findAppPermissionsOfUserByAppIds(
      @Param("appIds") List<String> usersAppsIds, String userId);

  @Transactional
  @Modifying
  @Query("DELETE from AppPermissionEntity ap where ap.urAdminUser.id=:adminId")
  public void deleteByAdminUserId(String adminId);

  @Query("SELECT ap from AppPermissionEntity ap where ap.urAdminUser.id=:adminId")
  public List<AppPermissionEntity> findByAdminUserId(String adminId);

  @Query(
      "SELECT ap FROM AppPermissionEntity ap where ap.urAdminUser.id in (:siteAdminIdList) and ap.app.id in (:appIdList)")
  public List<AppPermissionEntity> findByUserIdsAndAppIds(
      @Param("siteAdminIdList") List<String> siteAdminIdList,
      @Param("appIdList") List<String> appIdList);
}
