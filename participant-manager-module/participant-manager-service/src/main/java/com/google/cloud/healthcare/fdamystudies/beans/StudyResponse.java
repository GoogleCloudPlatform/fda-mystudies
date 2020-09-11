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

@Getter
@NoArgsConstructor
public class StudyResponse extends BaseResponse {
  private List<StudyDetails> studies = new ArrayList<>();

  private int sitePermissionCount;

  public StudyResponse(
      MessageCode messageCode, List<StudyDetails> studies, int sitePermissionCount) {
    super(messageCode);
    this.studies.addAll(studies);
    this.sitePermissionCount = sitePermissionCount;
  }
}
