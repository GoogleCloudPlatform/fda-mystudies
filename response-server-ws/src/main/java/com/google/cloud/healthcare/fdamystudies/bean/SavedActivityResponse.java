/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SavedActivityResponse {
  private String participantId;
  private ActivityMetadataBean metadata;
  private String createdTimestamp;
  private String siteId;
  private String studyVersion;
  private String type;
  private String tokenIdentifier;
  private List<Object> results = new ArrayList<>();
}
