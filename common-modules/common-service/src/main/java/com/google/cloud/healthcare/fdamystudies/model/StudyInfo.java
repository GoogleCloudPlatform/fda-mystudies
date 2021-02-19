/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.sql.Timestamp;

public interface StudyInfo {

  String getCustomId();

  String getStudyId();

  String getStudyName();

  String getLogoImageUrl();

  Integer getEdit();

  String getType();

  String getStatus();

  Timestamp getCreatedTimestamp();

  /** @return 0- site level permission only, 1- study and site level permissions */
  Integer getStudyPermission();
}
