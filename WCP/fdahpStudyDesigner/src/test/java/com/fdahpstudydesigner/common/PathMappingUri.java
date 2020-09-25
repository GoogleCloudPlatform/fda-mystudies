/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.common;

public enum PathMappingUri {
  ACTIVATE_OR_DEACTIVATE_USER("/adminUsersEdit/activateOrDeactivateUser.do"),

  SESSION_OUT("/sessionOut.do"),

  CHANGE_PASSWORD("/changePassword.do"),

  FORGOT_PASSWORD("/forgotPassword.do"),

  SECURITY_TOKEN_VALIDATION("/createPassword.do"),

  ADD_PASSWORD("/addPassword.do"),

  ADD_OR_UPDATE_USER_DETAILS("/adminUsersEdit/addOrUpdateUserDetails.do"),

  ENFORCE_PASSWORD_CHANGE("/adminUsersEdit/enforcePasswordChange.do"),

  RESEND_ACTIVATE_DETAILS_LINK("/adminUsersEdit/resendActivateDetailsLink.do"),

  VIEW_USER_DETAILS("/adminUsersView/viewUserDetails.do");

  private final String path;

  private PathMappingUri(String path) {
    this.path = path;
  }

  public String getPath() {
    return this.path;
  }
}
