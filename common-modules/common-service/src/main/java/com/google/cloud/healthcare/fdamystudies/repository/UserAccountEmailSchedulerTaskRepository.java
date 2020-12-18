/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.UserAccountEmailSchedulerTaskEntity;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
@ConditionalOnProperty(
    value = "participant.manager.repository.enabled",
    havingValue = "true",
    matchIfMissing = false)
public interface UserAccountEmailSchedulerTaskRepository
    extends JpaRepository<UserAccountEmailSchedulerTaskEntity, String> {

  @Query(
      value = "SELECT * FROM user_account_email_scheduler_tasks WHERE status = 0",
      nativeQuery = true)
  public List<UserAccountEmailSchedulerTaskEntity> findAllWithStatusZero();

  @Modifying
  @Query(
      value =
          "UPDATE user_account_email_scheduler_tasks set status=:newStatus WHERE user_id =:userId",
      nativeQuery = true)
  public int updateStatus(@Param("userId") String userId, @Param("newStatus") int status);

  @Modifying
  @Query(
      value = "DELETE from user_account_email_scheduler_tasks WHERE user_id =:userId",
      nativeQuery = true)
  public int deleteByUserId(@Param("userId") String userId);
}
