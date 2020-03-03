/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.dao;

import org.springframework.stereotype.Repository;

@Repository
public class CommonDaoImpl implements CommonDao {

  @Override
  public boolean validateServerClientCredentials(String applicationId, String clientId,
      String clientSecret) {
    // TODO - With Auth Server Open ID implementation
    return true;
  }

}
