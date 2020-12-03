/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

public interface AppStudySiteInfo {

  String getAppId();

  String getAppName();

  String getPermissionLevel();

  String getCustomAppId();

  String getStudyName();

  String getLocationId();

  String getLocationName();

  String getLocationDescription();

  String getLocationCustomId();

  Integer getEdit();

  String getStudyId();

  String getCustomStudyId();

  String getSiteId();

  default String getAppStudyIdKey() {
    return getAppId() + getStudyId();
  }

  default String getAppStudySiteIdKey() {
    return getAppId() + getStudyId() + getSiteId();
  }
}
