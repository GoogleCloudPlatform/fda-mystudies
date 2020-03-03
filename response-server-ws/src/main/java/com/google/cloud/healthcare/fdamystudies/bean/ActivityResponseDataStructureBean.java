/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.List;

public class ActivityResponseDataStructureBean {
  private String resultType = "";
  private String startTime = "";
  private String endTime = "";

  private List<QuestionnaireActivityStepsBean> results = new ArrayList<>();

  public String getResultType() {
    return resultType;
  }

  public void setResultType(String resultType) {
    this.resultType = resultType;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public List<QuestionnaireActivityStepsBean> getResults() {
    return results;
  }

  public void setResults(List<QuestionnaireActivityStepsBean> results) {
    this.results = results;
  }

}
