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
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;

@Repository
public interface AuthInfoBORepository extends JpaRepository<AuthInfoBO, Integer> {

  @Query(
      "SELECT a FROM UserAppDetailsBO u,AuthInfoBO a where u.userDetailsId = a.userId and u.appInfoId in (?1) and a.remoteNotificationFlag=1 and (a.deviceToken is not NULL and a.deviceToken != '' and a.deviceType is not NULL and a.deviceType != '') ")
  public List<AuthInfoBO> findDevicesTokens(List<Integer> appIds);
}
