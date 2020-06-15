/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationConfiguratation;

public class EmailNotification {

  private static final Logger logger = LogManager.getLogger(EmailNotification.class);

  private static ApplicationConfiguratation appConfig = null;

  public EmailNotification(ApplicationConfiguratation appConfig) {
    setAppConfig(appConfig);
  }

  public static ApplicationConfiguratation getAppConfig() {
    return appConfig;
  }

  public static void setAppConfig(ApplicationConfiguratation appConfig) {
    EmailNotification.appConfig = appConfig;
  }
}
