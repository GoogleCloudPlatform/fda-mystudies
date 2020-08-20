/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Getter
@Setter
@ToString
@Component
@Scope(value = "prototype")
public class StudyStateBean {

  private String studyId = "";
  private String status = "";
  private String enrolledDate = "";
  private Boolean bookmarked = false;
  private Integer completion = 0;
  private Integer adherence = 0;
  private String participantId = "";
  private String hashedToken = "";
  private String siteId = "";
}
