/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Provides questionnaire activity metadata information {@link ActivityMetadataBean} and activity
 * steps details {@link QuestionnaireActivityStepsBean}.
 */
@Setter
@Getter
@ToString
public class QuestionnaireActivityStructureBean {

  private String type = AppConstants.EMPTY_STR;
  private ActivityMetadataBean metadata = new ActivityMetadataBean();
  private List<QuestionnaireActivityStepsBean> steps = new ArrayList<>();
}
