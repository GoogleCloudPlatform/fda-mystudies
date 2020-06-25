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
  private static final String serializedReportName = "report";
  private static final String serializedResourceName = "resources";

  public enum ResourceType {
    @JsonProperty(serializedReportName)
    PERSONALIZED_REPORT,
    @JsonProperty(serializedResourceName)
    INSTITUTION_RESOURCE;

    @Override
    public String toString() {
      switch (this) {
        case PERSONALIZED_REPORT:
          return serializedReportName;
        case INSTITUTION_RESOURCE:
          return serializedResourceName;
        default:
          throw new IllegalArgumentException();
      }
    }
  };

  public UserResourceBean(String title, String content, ResourceType resourceType, String id) {
    this.title = title;
    this.content = content;
    this.resourceType = resourceType;
    // Prepend the resource type to the ID to ensure personalized report and
    // institution resource IDs don't collide.
    this.resourcesId = resourceType + ":" + id;
  }

  // Fields match ResourcesBean in Study MetaData server (WCP-WS).
  private String title;
  private String content;
  private ResourceType resourceType;
  private String resourcesId = "";

  private String type = "text";
  private String audience = "All";
  private String notificationText = "";
  private Map<String, Object> availability = new LinkedHashMap<>();
}
