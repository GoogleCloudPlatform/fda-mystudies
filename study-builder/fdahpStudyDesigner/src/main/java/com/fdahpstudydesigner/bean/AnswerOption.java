/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerOption {
  private Boolean valueBoolean;
  private Integer valueInteger;
  private String valueDateTime;
  private String valueDate;
  private String valueTime;
  private String valueString;
  private List<Extension> extension = new LinkedList<>();
}
