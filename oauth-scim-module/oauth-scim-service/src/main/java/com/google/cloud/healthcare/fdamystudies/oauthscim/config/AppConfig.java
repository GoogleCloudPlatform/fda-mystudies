/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.config;

import com.google.cloud.healthcare.fdamystudies.config.CommonModuleConfiguration;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

@Configuration
@Profile({"dev", "local", "qa", "mockit"})
public class AppConfig extends CommonModuleConfiguration {

  @Autowired ServletContext context;

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    registry
        .addViewController(String.format("%s/login", context.getContextPath()))
        .setViewName("login");
    registry
        .addViewController(String.format("%s/signin", context.getContextPath()))
        .setViewName("signin");
    registry
        .addViewController(String.format("%s/error", context.getContextPath()))
        .setViewName("error");
  }
}
