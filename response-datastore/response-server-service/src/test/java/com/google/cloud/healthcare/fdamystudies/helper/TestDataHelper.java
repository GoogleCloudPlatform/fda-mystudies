/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.helper;

import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantInfoRepository;
import com.google.cloud.healthcare.fdamystudies.response.model.ParticipantInfoEntity;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class TestDataHelper {

  @Autowired private ParticipantInfoRepository participantBoRepository;

  public ParticipantInfoEntity saveParticipant() {
    ParticipantInfoEntity participant = new ParticipantInfoEntity();
    participant.setTokenId(IdGenerator.id());
    participant.setParticipantId(IdGenerator.id());
    participant.setStudyId("CovidStudy");

    return participantBoRepository.saveAndFlush(participant);
  }
}
