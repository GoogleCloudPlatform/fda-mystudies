/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import java.util.List;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetailsBO, Integer> {

	public List<UserDetailsBO> findByLastName(String lastname);
}
