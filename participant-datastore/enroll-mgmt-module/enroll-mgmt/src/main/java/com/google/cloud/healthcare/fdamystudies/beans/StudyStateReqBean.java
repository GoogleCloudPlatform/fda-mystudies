/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class StudyStateReqBean {

  public StudyStateReqBean(List<StudiesBean> studies) {
    super();
    this.studies = studies;
  }

  private List<StudiesBean> studies;
  private List<ActivitiesBean> activity;
  private String studyId;
}
