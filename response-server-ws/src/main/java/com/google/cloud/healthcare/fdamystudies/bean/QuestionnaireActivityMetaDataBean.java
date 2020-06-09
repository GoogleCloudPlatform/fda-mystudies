/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Provides questionnaire activity metadata details {@link QuestionnaireActivityStructureBean} in
 * response.
 */
@Setter
@Getter
@ToString
public class QuestionnaireActivityMetaDataBean {

  private String message = AppConstants.FAILURE;
  private QuestionnaireActivityStructureBean activity = new QuestionnaireActivityStructureBean();
}
