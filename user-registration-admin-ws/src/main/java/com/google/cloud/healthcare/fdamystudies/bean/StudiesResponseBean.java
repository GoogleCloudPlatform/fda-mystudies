/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Component
@Scope(value = "prototype")
public class StudiesResponseBean {
  private Integer studyId;
  private String customStudyId;
  private String studyName;
  private Boolean selected = false;
  private Boolean disabled = false;
  private Integer permission = 0;

  private List<SitesResponseBean> sites;
}
