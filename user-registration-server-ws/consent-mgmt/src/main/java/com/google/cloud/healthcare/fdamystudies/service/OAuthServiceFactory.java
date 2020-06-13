/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OAuthServiceFactory {

  @Value("${security.oauth2.provider}")
  private String oauthProvider;

  @Autowired private HydraOAuthServiceImpl hydraOAuthServiceImpl;

  public OAuthService getOAuthService() {
    if (StringUtils.equalsIgnoreCase("hydra", oauthProvider)) {
      return hydraOAuthServiceImpl;
    }
    return null;
  }
}
