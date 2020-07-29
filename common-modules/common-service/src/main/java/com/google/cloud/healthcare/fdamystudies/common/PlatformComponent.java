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
  MOBILE_APP("Mobile App"),
  RESPONSE_DATASTORE("Response Datastore"),
  PARTICIPANT_DATASTORE("Participant Datastore"),
  STUDY_BUILDER("Study Builder"),
  STUDY_BUILDER_APP("Study Builder App"),
  CLOUD_STORAGE("Cloud Storage"),
  SCIM_AUTH_SERVER("SCIM Auth Server"),
  AUTH_SERVER("Auth Server"),
  PARTICIPANT_MANAGER("Participant Manager"),
  PARTICIPANT_MANAGER_APP("Participant Manager App");

  private String value;
}
