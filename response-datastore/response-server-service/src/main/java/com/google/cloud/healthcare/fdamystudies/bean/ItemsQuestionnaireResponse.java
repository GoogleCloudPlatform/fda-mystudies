/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ItemsQuestionnaireResponse {
  private String linkId;
  private String text;
  private String definition;
  private List<Answer> answer = new LinkedList<>();
  private List<ItemsQuestionnaireResponse> item = new LinkedList<>();
}
