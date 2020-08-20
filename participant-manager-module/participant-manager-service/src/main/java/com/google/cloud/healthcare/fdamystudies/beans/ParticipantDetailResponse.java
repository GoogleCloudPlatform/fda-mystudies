/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ParticipantDetailResponse extends BaseResponse {

  private ParticipantDetail participantDetails;

  public ParticipantDetailResponse(ErrorCode errorCode) {
    super(errorCode);
  }

  public ParticipantDetailResponse(MessageCode messageCode, ParticipantDetail participantDetails) {
    super(messageCode);
    this.participantDetails = participantDetails;
  }
}
