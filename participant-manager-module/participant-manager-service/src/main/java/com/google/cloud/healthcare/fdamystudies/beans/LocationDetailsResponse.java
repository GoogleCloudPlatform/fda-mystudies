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

@Setter
@Getter
public class LocationDetailsResponse extends BaseResponse {

  private String locationId;

  private String customId;

  private String description;

  private String name;

  private Integer status;

  private List<String> studies = new ArrayList<>();

  public LocationDetailsResponse(MessageCode messageCode) {
    super(messageCode);
  }
}
