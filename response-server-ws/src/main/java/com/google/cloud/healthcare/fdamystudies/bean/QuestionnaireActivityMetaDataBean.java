/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

/**
 * Provides questionnaire activity metadata details {@link QuestionnaireActivityStructureBean} in
 * response.
 */
public class QuestionnaireActivityMetaDataBean {

  private String message = AppConstants.FAILURE;
  private QuestionnaireActivityStructureBean activity = new QuestionnaireActivityStructureBean();

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public QuestionnaireActivityStructureBean getActivity() {
    return activity;
  }

  public void setActivity(QuestionnaireActivityStructureBean activity) {
    this.activity = activity;
  }
}
