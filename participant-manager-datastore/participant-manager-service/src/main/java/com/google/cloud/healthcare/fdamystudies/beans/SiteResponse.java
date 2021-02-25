/*
 * Copyright 2020-2021 Google LLC
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

@Getter
@Setter
@NoArgsConstructor
public class SiteResponse extends BaseResponse {

  private String siteId;

  private String siteName;

  public SiteResponse(String siteId, String siteName, MessageCode messageCode) {
    super(messageCode);
    this.siteId = siteId;
    this.siteName = siteName;
  }
}
