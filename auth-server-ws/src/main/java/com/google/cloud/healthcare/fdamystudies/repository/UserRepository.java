/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.healthcare.fdamystudies.model.DaoUserBO;

@Repository
public interface UserRepository extends JpaRepository<DaoUserBO, Integer> {

  interface MyView {
    Integer getId();
  }

  <T> List<T> findByUserId(String userId, Class<T> d);

  DaoUserBO findByUserId(String userId);

  DaoUserBO findByEmailIdAndAppIdAndOrgIdAndAppCode(
      String username, String appId, String orgId, String appCode);

  @Transactional
  long deleteByUserId(String userId);

  DaoUserBO findByUserIdAndAppIdAndOrgId(String userId, String appId, String orgId);

  DaoUserBO findByEmailIdAndAppCode(String emailId, String appCode);
}
