/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(Include.NON_NULL)
public class AppResponse extends BaseResponse {

  private List<AppDetails> apps = new ArrayList<>();

  private int studyPermissionCount;

  private boolean superAdmin;

  public AppResponse(
      MessageCode messageCode,
      List<AppDetails> apps,
      int studyPermissionCount,
      boolean superAdmin) {
    super(messageCode);
    this.apps.addAll(apps);
    this.studyPermissionCount = studyPermissionCount;
    this.superAdmin = superAdmin;
  }

  public AppResponse(MessageCode messageCode, List<AppDetails> apps, boolean superAdmin) {
    super(messageCode);
    this.apps.addAll(apps);
    this.superAdmin = superAdmin;
  }

  public AppResponse(MessageCode messageCode, List<AppDetails> apps) {
    super(messageCode);
    this.apps.addAll(apps);
  }
}
