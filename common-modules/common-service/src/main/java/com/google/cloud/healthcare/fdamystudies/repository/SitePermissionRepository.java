/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
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
public interface SitePermissionRepository extends JpaRepository<SitePermissionEntity, String> {
  @Query(
      "SELECT sp FROM SitePermissionEntity sp WHERE sp.urAdminUser.id=:userId ORDER BY sp.created DESC")
  public List<SitePermissionEntity> findSitePermissionByUserId(String userId);

  @Query(
      "SELECT sp FROM SitePermissionEntity sp WHERE sp.urAdminUser.id = :userId and sp.site.id = :siteId")
  public Optional<SitePermissionEntity> findByUserIdAndSiteId(String userId, String siteId);

  @Query(
      "SELECT sitePermission from SitePermissionEntity sitePermission "
          + "where sitePermission.site.id=:siteId")
  public List<SitePermissionEntity> findBySiteId(String siteId);

  @Transactional
  @Modifying
  @Query("DELETE FROM SitePermissionEntity sp WHERE sp.urAdminUser.id=:adminId")
  public void deleteByAdminUserId(String adminId);

  @Query(
      "SELECT sp FROM SitePermissionEntity sp "
          + "WHERE sp.urAdminUser.id = :userId and sp.site.id = :siteId")
  public Optional<SitePermissionEntity> findSitePermissionByUserIdAndSiteId(
      String userId, String siteId);

  @Query(
      "SELECT sp from SitePermissionEntity sp "
          + "where sp.urAdminUser.id=:adminId and sp.site.id=:siteId and sp.study.id=:studyId and sp.app.id=:appId")
  public Optional<SitePermissionEntity> findByAdminIdAndAppIdAndStudyIdAndSiteId(
      String siteId, String adminId, String appId, String studyId);

  @Query(
      "SELECT sp FROM SitePermissionEntity sp WHERE sp.urAdminUser.id = :userId and sp.study.id = :studyId")
  public List<SitePermissionEntity> findByUserIdAndStudyId(String userId, String studyId);
}
