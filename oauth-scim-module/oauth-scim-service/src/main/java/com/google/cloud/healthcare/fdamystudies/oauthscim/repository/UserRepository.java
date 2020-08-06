/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.repository;

import com.google.cloud.healthcare.fdamystudies.oauthscim.model.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  public Optional<UserEntity> findByAppIdAndOrgIdAndEmail(String appId, String orgId, String email);

  @Transactional
  public long deleteByUserId(String userId);

  public Optional<UserEntity> findByUserId(String userId);

  public Optional<UserEntity> findByTempRegId(String tempRegId);
}
