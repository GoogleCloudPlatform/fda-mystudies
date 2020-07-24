/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.repository;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.google.cloud.healthcare.fdamystudies.model.AppCount;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;

@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity, String> {

  @Query(
      "SELECT ud.appInfo.id AS appId,COUNT(ud.appInfo.id) AS count FROM UserDetailsEntity ud "
          + "WHERE ud.appInfo.id in (:appIds) GROUP BY ud.appInfo.id")
  public List<AppCount> findAppUsersCount(@Param("appIds") List<String> usersAppsIds);

  @Query("SELECT ud FROM UserDetailsEntity ud WHERE ud.appInfo.id = :appInfoId")
  public List<UserDetailsEntity> findByAppId(String appInfoId);
}
