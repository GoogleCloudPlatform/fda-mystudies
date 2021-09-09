/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.model;

import java.sql.Timestamp;

public interface AppStudyInfo {

  String getAppId();

  String getCustomAppId();

  String getStudyId();

  String getAppName();

  Long getStudyCount();

  Timestamp getCreatedTimestamp();

  String getAppStatus();
}
