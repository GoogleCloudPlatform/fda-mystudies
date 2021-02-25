/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.hphc.mystudies.bean;

import com.hphc.mystudies.util.StudyMetaDataConstants;
import java.util.ArrayList;
import java.util.List;

public class StudyInfoResponse {

  private String message = StudyMetaDataConstants.FAILURE;

  private String studyWebsite = "";

  private List<InfoBean> info = new ArrayList<>();

  private AnchorDateBean anchorDate = new AnchorDateBean();

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getStudyWebsite() {
    return studyWebsite;
  }

  public void setStudyWebsite(String studyWebsite) {
    this.studyWebsite = studyWebsite;
  }

  public List<InfoBean> getInfo() {
    return info;
  }

  public void setInfo(List<InfoBean> info) {
    this.info = info;
  }

  public AnchorDateBean getAnchorDate() {
    return anchorDate;
  }

  public void setAnchorDate(AnchorDateBean anchorDate) {
    this.anchorDate = anchorDate;
  }
}
