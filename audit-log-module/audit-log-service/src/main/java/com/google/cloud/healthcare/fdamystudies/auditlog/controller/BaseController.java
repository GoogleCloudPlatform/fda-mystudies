/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import com.google.cloud.healthcare.fdamystudies.auditlog.service.OAuthService;

public class BaseController {

  @Autowired private OAuthService oAuthService;

  public OAuthService getOAuthService() {
    return oAuthService;
  }
}
