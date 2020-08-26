/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.bean.QuestionnaireActivityStepsBean;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class QuestionnaireActivityStepsBeanPredicate {

  public static Predicate<QuestionnaireActivityStepsBean> questionKeyMatch(String questionKey) {
    return p -> (p.getKey() != null && p.getKey().equalsIgnoreCase(questionKey));
  }

  public static Predicate<QuestionnaireActivityStepsBean> taskKeyMatch(String taskKey) {
    return p -> (p.getFormat() != null && p.getFormat().containsKey(taskKey));
  }

  public static List<QuestionnaireActivityStepsBean> filterAndGetByQuestionKey(
      List<QuestionnaireActivityStepsBean> questionnaireActivityStepsBeanList,
      Predicate<QuestionnaireActivityStepsBean> predicate) {
    return questionnaireActivityStepsBeanList
        .stream()
        .filter(predicate)
        .collect(Collectors.<QuestionnaireActivityStepsBean>toList());
  }
}
