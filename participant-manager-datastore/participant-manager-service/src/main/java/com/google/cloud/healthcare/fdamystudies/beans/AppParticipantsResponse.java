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
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Setter
@Getter
@ToString
@Component
@Scope(value = "prototype")
public class AppParticipantsResponse extends BaseResponse {

  private String appId;

  private String customId;

  private String name;

  private List<ParticipantDetail> participants = new ArrayList<>();

  private Long totalParticipantCount;

  public AppParticipantsResponse(
      MessageCode messageCode, String appId, String customId, String name) {
    super(messageCode);
    this.appId = appId;
    this.customId = customId;
    this.name = name;
  }
}
