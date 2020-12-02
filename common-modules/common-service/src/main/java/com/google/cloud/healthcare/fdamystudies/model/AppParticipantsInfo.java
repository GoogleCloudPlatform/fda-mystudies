/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.model;

import java.sql.Timestamp;

public interface AppParticipantsInfo {
  String getUserDetailsId();

  String getEmail();

  String getStatus();

  int getRegistrationStatus();

  Timestamp getRegistrationDate();

  String getStudyId();

  String getCustomStudyId();

  String getStudyName();

  String getStudyType();

  String getParticipantStudyStatus();

  Timestamp getWithdrawalTime();

  Timestamp getEnrolledTime();
}
