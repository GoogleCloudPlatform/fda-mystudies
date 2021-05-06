/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;


public enum PlatformComponent {
  MOBILE_APPS(
      "MOBILE APPS", "The iOS and Android apps that participants use to interact with MyStudies"),

  RESPONSE_DATASTORE(
      "RESPONSE DATASTORE",
      "The back-end components that manage participant response data (does not include PII) "),

  PARTICIPANT_DATASTORE(
      "PARTICIPANT DATASTORE",
      "The back-end components that manage app user / study participant data (includes PII)"),

  PARTICIPANT_USER_DATASTORE(
      "PARTICIPANT USER DATASTORE",
      "The back-end component that manage app user / study participant data (includes PII)"),

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

  private PlatformComponent(String value, String description) {
	this.value = value;
	this.description = description;
}



public String getValue() {
	return value;
}



public void setValue(String value) {
	this.value = value;
}



public String getDescription() {
	return description;
}



public void setDescription(String description) {
	this.description = description;
}



public static PlatformComponent fromValue(String value) {
    for (PlatformComponent type : PlatformComponent.values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    return null;
  }
}
