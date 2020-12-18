/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.repository;

import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  public Optional<UserEntity> findByAppIdAndEmail(String appId, String email);

  @Transactional
  public long deleteByUserId(String userId);

  public Optional<UserEntity> findByUserId(String userId);

  public Optional<UserEntity> findByTempRegId(String tempRegId);

  @Transactional
  @Modifying
  @Query("update UserEntity u set u.tempRegId=null where u.userId=:userId")
  public void removeTempRegIDForUser(@Param("userId") String userId);

  @Transactional
  @Modifying
  @Query("update UserEntity u set u.tempRegId=null where u.created < :timestamp")
  public void removeTempRegIdBeforeTime(@Param("timestamp") Timestamp timestamp);

  @Modifying
  @Query(
      "update UserEntity u set u.email = :email, status= :status, tempRegId= :tempRegId  where u.userId = :userId")
  void updateEmailStatusAndTempRegId(
      @Param("email") String email,
      @Param("status") int status,
      @Param("tempRegId") String tempRegId,
      @Param("userId") String userId);
}
