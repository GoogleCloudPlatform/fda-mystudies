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

/**
 * Provides questionnaire activity metadata information {@link ActivityMetadataBean} and activity
 * steps details {@link QuestionnaireActivityStepsBean}.
 * 
 */
public class QuestionnaireActivityStructureBean {

  private String type = "";
  private ActivityMetadataBean metadata = new ActivityMetadataBean();
  private List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public ActivityMetadataBean getMetadata() {
    return metadata;
  }

  public void setMetadata(ActivityMetadataBean metadata) {
    this.metadata = metadata;
  }

  public List<QuestionnaireActivityStepsBean> getSteps() {
    return steps;
  }

  public void setSteps(List<QuestionnaireActivityStepsBean> steps) {
    this.steps = steps;
  }
}
