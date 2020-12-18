/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StudiesBean {

  public StudiesBean(String studyId, Boolean bookmarked, Integer completion, Integer adherence) {
    super();
    this.studyId = studyId;
    this.bookmarked = bookmarked;
    this.completion = completion;
    this.adherence = adherence;
  }

  private String studyId = "";
  private String status = "";
  private Boolean bookmarked;
  private String enrolledDate = "";
  private Integer completion;
  private Integer adherence;
  private String participantId;
}
