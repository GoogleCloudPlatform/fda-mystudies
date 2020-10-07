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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@ToString
@Setter
@Getter
@Component
@Scope(value = "prototype")
public class AppStudyResponse {
  private String studyId;

  private String customStudyId;

  private String studyName;

  private int totalSitesCount;

  private List<AppSiteResponse> sites = new ArrayList<>();
}
