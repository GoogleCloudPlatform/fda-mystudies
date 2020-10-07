/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InviteParticipantResponse extends BaseResponse {

  private List<String> invitedParticipantIds = new ArrayList<>();

  private List<String> failedParticipantIds = new ArrayList<>();

  public InviteParticipantResponse(
      MessageCode messageCode,
      List<String> invitedParticipantIds,
      List<String> failedParticipantIds) {
    super(messageCode);
    this.invitedParticipantIds.addAll(invitedParticipantIds);
    this.failedParticipantIds.addAll(failedParticipantIds);
  }
}
