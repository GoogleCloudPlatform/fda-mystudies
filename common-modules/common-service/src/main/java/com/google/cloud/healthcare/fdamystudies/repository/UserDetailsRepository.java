/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.AppCount;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity, String> {

  @Query(
      "SELECT ud.app.id AS appId,COUNT(ud.app.id) AS count FROM UserDetailsEntity ud "
          + "WHERE ud.app.id in (:appIds) GROUP BY ud.app.id")
  public List<AppCount> findAppUsersCount(@Param("appIds") List<String> usersAppsIds);

  @Query("SELECT ud FROM UserDetailsEntity ud WHERE ud.app.id = :appInfoId")
  public List<UserDetailsEntity> findByAppId(String appInfoId);

  <T> List<T> findByUserId(String userId, Class<T> d);

  UserDetailsEntity findByUserId(String userId);

  List<UserDetailsEntity> findByEmail(String emailId);

  @Query("SELECT ud FROM UserDetailsEntity ud WHERE ud.email = :email and ud.app.appId = :appId")
  Optional<UserDetailsEntity> findByEmailAndAppId(String email, String appId);

  public List<UserDetailsEntity> findByLastName(String lastname);
  
  @Query("SELECT ud FROM UserDetailsEntity ud WHERE ud.userId = :userId")
  public Optional<UserDetailsEntity> findByUserId(String userId);
}
