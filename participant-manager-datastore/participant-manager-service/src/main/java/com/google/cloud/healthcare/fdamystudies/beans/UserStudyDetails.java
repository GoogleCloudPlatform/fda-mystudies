/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class UserStudyDetails {

  private String studyId;

  private String customStudyId;

  private String studyName;

  private boolean selected;

  private Integer permission;

  private int totalSitesCount;

  private int selectedSitesCount;

  private List<UserSiteDetails> sites = new ArrayList<>();
}
