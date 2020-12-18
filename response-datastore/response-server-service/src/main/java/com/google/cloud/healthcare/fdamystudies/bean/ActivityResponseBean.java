/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ActivityResponseBean {
  private String applicationId = AppConstants.EMPTY_STR;
  private String participantId;
  private String tokenIdentifier;
  private String siteId;
  private String sharingConsent;
  private Boolean withdrawalStatus;
  private String type;
  private ActivityMetadataBean metadata = new ActivityMetadataBean();
  private ActivityResponseDataStructureBean data = new ActivityResponseDataStructureBean();
  private String createdTimestamp;
}
