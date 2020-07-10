/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.healthcare.fdamystudies.dao.ParticipantDao;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantBo;
import com.google.cloud.healthcare.fdamystudies.utils.ProcessResponseException;

@Service
public class ParticipantServiceImpl implements ParticipantService {
  @Autowired private ParticipantDao participantDao;

  @Override
  public String saveParticipant(ParticipantBo participantBo) throws ProcessResponseException {
    System.out.println("ParticipantServiceImpl saveParticipant starts");
    return participantDao.saveParticipant(participantBo);
  }

  @Override
  public boolean isValidParticipant(ParticipantBo participantBo) throws ProcessResponseException {
    return participantDao.isValidParticipant(participantBo);
  }
}
