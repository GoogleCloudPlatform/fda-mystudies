/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;

public enum UserAccessLevel {
  SUPER_ADMIN("SUPERADMIN", "Has all permissions "),

  STUDY_BUILDER_ADMIN(
      "STUDY BUILDER ADMIN",
      "Has Manage Users permission and may or may not have other permissions"),

  APP_STUDY_ADMIN(
      "APP/STUDY ADMIN",
      "Has App-level Notifications permission (or) Permissions to 1 or more Studies. Does not have Manage Users permission."),

  SITE_ADMIN(
      "SITE ADMIN",
      "Any non-superadmin user who will have a subset of Manage Locations and Manage App/Study/Site permissions");

  private String value;

  private String description;

  public String getValue() {
    return value;
  }

  public String getDescription() {
    return description;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  private UserAccessLevel(String value, String description) {
    this.value = value;
    this.description = description;
  }
}
