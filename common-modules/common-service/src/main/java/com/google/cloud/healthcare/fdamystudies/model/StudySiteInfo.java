/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.sql.Timestamp;

public interface StudySiteInfo {

  String getCustomId();

  String getStudyId();

  String getStudyName();

  String getLogoImageUrl();

  Integer getEdit();

  String getStudyType();

  Timestamp getStudyCreatedTimeStamp();

  Timestamp getSiteCreatedTimeStamp();

  Integer getStudyPermission();

  Integer getEditPermission();

  String getSiteId();

  String getSiteName();

  String getAppId();

  String getAppName();

  String getCustomAppId();

  String getStudyStatus();

  String getSiteStatus();

  Long getTargetEnrollment();
}
