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
public enum UserAccessLevel {
  SUPER_ADMIN("Superadmin"),
  SB_ADMIN("SB Admin"),
  APP_STUDY_ADMIN("App/Study Admin"),
  SITE_ADMIN("Site Admin");

  private String value;
}
