/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.repository;

import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface UserDetailsBORepository extends JpaRepository<UserDetailsEntity, String> {

  interface MyView {
    String getEmailCode();
  }

  <T> List<T> findByUserId(String userId, Class<T> d);

  UserDetailsEntity findByUserId(String userId);

  List<UserDetailsEntity> findByEmail(String emailId);

  @Query("SELECT ud FROM UserDetailsEntity ud WHERE ud.email = :email and ud.app.appId = :appId")
  Optional<UserDetailsEntity> findByEmailAndAppId(String email, String appId);

  public List<UserDetailsEntity> findByLastName(String lastname);
}
