/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlatformComponent {
  MOBILE_APPS(
      "MOBILE APPS", "The iOS and Android apps that participants use to interact with MyStudies"),

  RESPONSE_DATASTORE(
      "RESPONSE DATASTORE",
      "The back-end components that manage participant response data (does not include PII) "),

  PARTICIPANT_DATASTORE(
      "PARTICIPANT DATASTORE",
      "The back-end components that manage app user / study participant data (includes PII)"),

  STUDY_BUILDER("STUDY BUILDER", "The UI that researchers use to design and deploy studies"),

  STUDY_DATASTORE(
      "STUDY DATASTORE",
      "This is the back-end component that manages the study builder data and holds the study configurations"),

  SCIM_AUTH_SERVER(
      "SCIM AUTH SERVER",
      "This component implements login/consent flow and integrates with ORY Hydra for access and refresh tokens"),

  AUTH_SERVER(
      "Auth Server",
      "This is back-end component that manages user accounts and issues access and refresh tokens"),

  PARTICIPANT_MANAGER(
      "PARTICIPANT MANAGER",
      "The UI that administrators use to invite participants to studies and track enrollment progress"),

  CLOUD_STORAGE(
      "CLOUD STORAGE", "for storing and accessing data on Google Cloud Platform infrastructure");

  private String value;

  private String description;

  public static PlatformComponent fromValue(String value) {
    for (PlatformComponent type : PlatformComponent.values()) {
      if (value == type.getValue()) {
        return type;
      }
    }
    return null;
  }
}
