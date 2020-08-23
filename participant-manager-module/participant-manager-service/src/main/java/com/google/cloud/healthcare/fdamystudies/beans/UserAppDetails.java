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

@Getter
@Setter
@ToString
public class UserAppDetails {

  private String id;

  private String customId;

  private String name;

  private boolean selected;

  private Integer permission;

  private int totalStudiesCount;

  private int selectedStudiesCount;

  private int totalSitesCount;

  private int selectedSitesCount;

  private List<UserStudyDetails> studies = new ArrayList<>();
}
