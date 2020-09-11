/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ParticipantRegistryResponse extends BaseResponse {
  private ParticipantRegistryDetail participantRegistryDetail;

  public ParticipantRegistryResponse(
      MessageCode messageCode, ParticipantRegistryDetail participantRegistryDetail) {
    super(messageCode);
    this.participantRegistryDetail = participantRegistryDetail;
  }
}
