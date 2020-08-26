/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class UserStudyPermissionRequest {

  private String studyId;

  private String customStudyId;

  private String studyName;

  private boolean selected;

  private Integer permission;

  private Integer totalSitesCount;

  private Integer selectedSitesCount;

  private List<UserSitePermissionRequest> sites;
}
