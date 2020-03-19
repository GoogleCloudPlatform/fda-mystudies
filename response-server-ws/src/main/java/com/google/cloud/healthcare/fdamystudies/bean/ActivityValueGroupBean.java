/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.List;

public class ActivityValueGroupBean {
  private List<QuestionnaireActivityStepsBean> valueGroup = new ArrayList<>();

  public List<QuestionnaireActivityStepsBean> getValueGroup() {
    return valueGroup;
  }

  public void setValueGroup(List<QuestionnaireActivityStepsBean> valueGroup) {
    this.valueGroup = valueGroup;
  }
}
