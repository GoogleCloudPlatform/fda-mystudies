/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;

public interface ParticipantService {
  String saveParticipant(ParticipantBo participantBo) throws ProcessResponseException;

  boolean isValidParticipant(ParticipantBo participantBo) throws ProcessResponseException;
}
