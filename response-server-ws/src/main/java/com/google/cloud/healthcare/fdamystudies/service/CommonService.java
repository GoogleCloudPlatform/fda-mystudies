/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.service;

public interface CommonService {
  Integer validateAccessToken(String userId, String accessToken, String clientToken);

  boolean validateServerClientCredentials(String applicationId, String clientId,
      String clientSecret);

}
