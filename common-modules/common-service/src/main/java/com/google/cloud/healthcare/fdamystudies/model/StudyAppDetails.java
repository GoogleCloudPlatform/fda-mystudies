/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

public interface StudyAppDetails {

  String getStudyId();

  String getCustomStudyId();

  String getStudyName();

  String getStudyType();

  String getStudyStatus();

  String getAppId();

  String getCustomAppId();

  String getAppName();

  Integer getTargetEnrollment();

  Integer getEdit();
}
