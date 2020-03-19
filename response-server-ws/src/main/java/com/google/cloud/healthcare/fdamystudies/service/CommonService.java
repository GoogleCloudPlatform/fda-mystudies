/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;

public interface CommonService {
  Integer validateAccessToken(String userId, String accessToken, String clientToken);

  boolean validateServerClientCredentials(
      String applicationId, String clientId, String clientSecret);

  public ParticipantBo getParticipantInfoDetails(String participantId);
}
