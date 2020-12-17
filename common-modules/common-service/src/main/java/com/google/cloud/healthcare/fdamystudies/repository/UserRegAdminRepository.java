/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface UserRegAdminRepository extends JpaRepository<UserRegAdminEntity, String> {

  @Query("SELECT ua FROM UserRegAdminEntity ua WHERE ua.id = :userId")
  Optional<UserRegAdminEntity> findByUserRegAdminId(String userId);

  public Optional<UserRegAdminEntity> findByEmail(String email);

  @Query("SELECT user from UserRegAdminEntity user where user.urAdminAuthId=:urAdminAuthId")
  public Optional<UserRegAdminEntity> findByUrAdminAuthId(String urAdminAuthId);

  @Query("SELECT user from UserRegAdminEntity user where user.securityCode=:securityCode")
  public Optional<UserRegAdminEntity> findBySecurityCode(String securityCode);

  @Query(
      value =
          "SELECT * "
              + "FROM ur_admin_user "
              + "WHERE email LIKE %:searchTerm% OR first_name LIKE %:searchTerm% OR last_name LIKE %:searchTerm% "
              + "ORDER BY CASE :orderByCondition WHEN 'email_asc' THEN email END ASC, "
              + "CASE :orderByCondition WHEN 'firstName_asc' THEN first_name END ASC, "
              + "CASE :orderByCondition WHEN 'lastName_asc' THEN last_name END ASC, "
              + "CASE :orderByCondition WHEN 'status_asc' THEN STATUS END ASC, "
              + "CASE :orderByCondition WHEN 'email_desc' THEN email END DESC, "
              + "CASE :orderByCondition WHEN 'firstName_desc' THEN first_name END DESC, "
              + "CASE :orderByCondition WHEN 'lastName_desc' THEN last_name END DESC, "
              + "CASE :orderByCondition WHEN 'status_desc' THEN STATUS END DESC "
              + "LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  public List<UserRegAdminEntity> findByLimitAndOffset(
      Integer limit, Integer offset, String orderByCondition, String searchTerm);

  @Query(
      value =
          "SELECT count(id) "
              + "FROM ur_admin_user "
              + "WHERE email LIKE %:searchTerm% OR first_name LIKE %:searchTerm% OR last_name LIKE %:searchTerm% ",
      nativeQuery = true)
  public Long countBySearchTerm(String searchTerm);
}
