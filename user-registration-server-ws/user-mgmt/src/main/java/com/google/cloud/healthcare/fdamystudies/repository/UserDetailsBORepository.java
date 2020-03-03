/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
/** */
package com.google.cloud.healthcare.fdamystudies.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.healthcare.fdamystudies.model.UserDetails;

/**
 * Project Name: MyStudies-UserReg-WS
 *
 * @author Chiranjibi Dash, Date: Dec 18, 2019, Time: 4:33:57 PM
 */
@Repository
@Transactional
public interface UserDetailsBORepository extends JpaRepository<UserDetails, Integer> {

  interface MyView {
    String getEmailCode();
  }

  <T> List<T> findByUserId(String userId, Class<T> d);

  UserDetails findByUserId(String userId);

  List<UserDetails> findByEmail(String emailId);
}
