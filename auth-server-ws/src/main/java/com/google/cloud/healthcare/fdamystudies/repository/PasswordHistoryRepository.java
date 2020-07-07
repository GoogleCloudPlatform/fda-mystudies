/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.model.PasswordHistoryBO;

@Repository
public interface PasswordHistoryRepository extends JpaRepository<PasswordHistoryBO, Integer> {

  List<PasswordHistoryBO> findByUserId(String userId);

  List<PasswordHistoryBO> findByUserIdOrderByPasswordHistoryIdAsc(String userId);

  @Transactional
  long deleteByUserId(String userId);
}
