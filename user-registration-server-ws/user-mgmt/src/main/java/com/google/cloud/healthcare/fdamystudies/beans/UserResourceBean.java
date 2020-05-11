/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class UserResourceBean {
  public enum Type {
	  @JsonProperty("report")
	  PERSONALIZED_REPORT,
	  @JsonProperty("resource")
	  INSTITUTION_RESOURCE
  };

  private String title;
  private String content;
  private Type resourceType;
}
