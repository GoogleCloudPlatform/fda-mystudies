/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.helper;

import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantBoRepository;
import com.google.cloud.healthcare.fdamystudies.responsedatastore.model.ParticipantBo;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class TestDataHelper {

  @Autowired private ParticipantBoRepository participantBoRepository;

  public ParticipantBo saveParticipant() {
    ParticipantBo participant = new ParticipantBo();
    participant.setTokenIdentifier(IdGenerator.id());
    participant.setParticipantIdentifier(IdGenerator.id());

    return participantBoRepository.saveAndFlush(participant);
  }
}
