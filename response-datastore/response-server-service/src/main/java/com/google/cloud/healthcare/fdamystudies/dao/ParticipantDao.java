/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;

public interface ParticipantDao {

  String saveParticipant(ParticipantInfoEntity participantBo) throws ProcessResponseException;

  boolean isValidParticipant(ParticipantInfoEntity participantBo) throws ProcessResponseException;
}
