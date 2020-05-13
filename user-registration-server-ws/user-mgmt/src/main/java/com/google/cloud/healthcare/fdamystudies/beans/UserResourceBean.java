/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.beans;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashMap;
import java.util.Map;
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
    @JsonProperty("resources")
    INSTITUTION_RESOURCE
  };

  public UserResourceBean(String title, String content, Type type) {
    this.title = title;
    this.content = content;
    this.type = type;
  }

  // Fields match ResourcesBean in Study MetaData server (WCP-WS).
  private String title;
  private String content;
  private Type type;

  private String resourcesId = "";
  private String audience = "All";
  private String notificationText = "";
  private Map<String, Object> availability = new LinkedHashMap<>();
}
