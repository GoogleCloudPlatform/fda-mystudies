/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.sql.Timestamp;

public interface StudyParticipantDetails {

  String getEmail();

  String getSiteId();

  String getOnboardingStatus();

  String getLocationName();

  String getLocationCustomId();

  String getEnrolledStatus();

  Timestamp getEnrolledDate();

  Timestamp getInvitedDate();

  String getParticipantId();

  Timestamp getCreatedTime();
}
