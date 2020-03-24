/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;

@Repository
public interface SessionRepository extends JpaRepository<AuthInfoBO, Integer> {

  AuthInfoBO findByUserId(String userId);

  @Transactional
  long deleteByUserId(String userId);

  AuthInfoBO findByUserIdAndAccessToken(String userId, String accessToken);

  AuthInfoBO findByRefreshTokenAndUserId(String refreshToken, String userId);

  AuthInfoBO findByClientToken(String clientToken);
}
