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
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ToString
@NoArgsConstructor
public class SetUpAccountResponse extends BaseResponse {

  private String userId;

  private String tempRegId;

  private String authUserId;

  public SetUpAccountResponse(
      String userId, String tempRegId, String authUserId, MessageCode messageCode) {
    super(messageCode);
    this.userId = userId;
    this.tempRegId = tempRegId;
    this.authUserId = authUserId;
  }
}
