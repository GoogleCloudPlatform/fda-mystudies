/* Copyright 2022 Google LLC
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
public class FHIRQuestionnaireFhirBean {

  private String id;
  private String resourceType;
  private List<Identifier> identifier = new LinkedList<>();
  private String version;
  private String name;
  private String title;
  private String status;
  private String date;
  private Object extension = new LinkedList<>();
  private Object item = new LinkedList<>();
  private Meta meta;
}
