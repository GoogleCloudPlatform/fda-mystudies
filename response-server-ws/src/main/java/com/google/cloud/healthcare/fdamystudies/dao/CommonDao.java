/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;

public interface CommonDao {

  boolean validateServerClientCredentials(
      String applicationId, String clientId, String clientSecret);

  public ParticipantBo getParticipantInfoDetails(String participantId);
}
