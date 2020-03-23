/*
 *Copyright 2020 Google LLC
 *
 *Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 *or at https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;

@Repository
public interface AuthInfoBORepository extends JpaRepository<AuthInfoBO, Integer> {

  /*@Query(
      "SELECT a.devicetoken as devicetoken,a.devicetype as devicetype FROM UserAppDetailsBO u,AuthInfoBO a where u.userDetailsId = a.userId and u.appInfoId in (:appIds) and a.authkey != '0' and a.remotenotificationflag=true and (a.devicetoken is not NULL and a.devicetoken != '' and a.devicetype is not NULL and a.devicetype != '') ")
  public List<AuthInfoBO> findByAppId(List<Integer> id);*/
}
