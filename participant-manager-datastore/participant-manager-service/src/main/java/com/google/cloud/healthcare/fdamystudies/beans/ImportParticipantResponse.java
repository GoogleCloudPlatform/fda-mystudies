/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ImportParticipantResponse extends BaseResponse {

  private List<ParticipantDetail> participants = new ArrayList<>();

  private Set<String> invalidEmails = new HashSet<>();

  private List<String> duplicateEmails = new ArrayList<>();

  public ImportParticipantResponse(
      MessageCode messageCode, List<ParticipantDetail> participants, List<String> duplicateEmails) {
    super(messageCode);
    this.participants.addAll(participants);
    this.duplicateEmails.addAll(duplicateEmails);
  }
}
