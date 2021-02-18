/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import static com.google.cloud.healthcare.fdamystudies.common.ColumnConstraints.SMALL_LENGTH;

import javax.validation.constraints.Size;
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

  public StudiesBean(
      String studyId, Boolean bookmarked, Integer completion, Integer adherence, String siteId) {
    super();
    this.studyId = studyId;
    this.bookmarked = bookmarked;
    this.completion = completion;
    this.adherence = adherence;
    this.siteId = siteId;
  }

  private String studyId;

  @Size(max = SMALL_LENGTH)
  private String status;

  private Boolean bookmarked;
  private String enrolledDate;
  private Integer completion;
  private Integer adherence;
  private String participantId;
  private String siteId;
}
