/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class StudiesBean {
  private String studyId = "";
  private String status = "";
  private Boolean bookmarked;
  private String enrolledDate = "";
  private Integer completion;
  private Integer adherence;
  private String participantId;
}
