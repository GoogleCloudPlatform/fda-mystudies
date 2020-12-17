/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

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

  PARTICIPANT_USER_DATASTORE(
      "PARTICIPANT USER DATASTORE",
      "The back-end component that manage app user / study participant data (includes PII)"),

  PARTICIPANT_CONSENT_DATASTORE(
      "PARTICIPANT CONSENT DATASTORE",
      "The back-end component that manage consent details (includes PII)"),

  PARTICIPANT_ENROLL_DATASTORE(
      "PARTICIPANT ENROLL DATASTORE",
      "The back-end component that manage study participant enrollement details (includes PII)"),

  STUDY_BUILDER("STUDY BUILDER", "The UI that researchers use to design and deploy studies"),

  STUDY_DATASTORE(
      "STUDY DATASTORE",
      "This is the back-end component that manages the study builder data and holds the study configurations"),

  SCIM_AUTH_SERVER(
      "SCIM AUTH SERVER",
      "This component implements login/consent flow and integrates with ORY Hydra for access and refresh tokens"),

  PARTICIPANT_MANAGER_DATASTORE(
      "PARTICIPANT MANAGER DATASTORE",
      "This is the back-end component that manages the study builder data and holds the study configurations"),

  PARTICIPANT_MANAGER(
      "PARTICIPANT MANAGER",
      "The UI that administrators use to invite participants to studies and track enrollment progress"),

  CLOUD_STORAGE(
      "CLOUD STORAGE", "for storing and accessing data on Google Cloud Platform infrastructure"),

  NATIVE_PUSH_NOTIFICATION_SERVER(
      "NATIVE PUSH NOTIFICATION SERVER",
      "This will enables third party application developers to send notification data to applications installed on native mobile device");

  private String value;

  private String description;

  private static final XLogger logger =
      XLoggerFactory.getXLogger(PlatformComponent.class.getName());

  public static PlatformComponent fromValue(String value) {
    try {
      value = StringUtils.isNotBlank(value) ? URLDecoder.decode(value, "UTF-8") : value;
    } catch (UnsupportedEncodingException e) {
      logger.error(String.format("Unable to decode '%s' using URLDecoder", value), e);
      return null;
    }
    for (PlatformComponent type : PlatformComponent.values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    return null;
  }
}
