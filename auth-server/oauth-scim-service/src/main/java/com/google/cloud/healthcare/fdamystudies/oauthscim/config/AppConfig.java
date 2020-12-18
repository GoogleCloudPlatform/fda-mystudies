/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.config;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTO_LOGIN_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.LOGIN_VIEW_NAME;

import com.google.cloud.healthcare.fdamystudies.config.CommonModuleConfiguration;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
public class AppConfig extends CommonModuleConfiguration {

  @Autowired ServletContext context;

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry
        .addViewController(String.format("%s/login", context.getContextPath()))
        .setViewName(LOGIN_VIEW_NAME);
    registry
        .addViewController(String.format("%s/autoLogin", context.getContextPath()))
        .setViewName(AUTO_LOGIN_VIEW_NAME);
    registry
        .addViewController(String.format("%s/error", context.getContextPath()))
        .setViewName(ERROR_VIEW_NAME);
  }
}
