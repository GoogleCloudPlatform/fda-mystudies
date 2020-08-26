/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

// Helper class to initialize environment variables that exist in the appConfigurations.properties
// file.
// If a client does not depend on these values then using this class to override them is fine for
// test purposes.
public class TestApplicationContextInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  @Override
  public void initialize(ConfigurableApplicationContext applicationContext) {
    System.setProperty("FROM_EMAIL_ADDRESS", "dummy-value");
    System.setProperty("FROM_EMAIL_PASSWORD", "dummy-value");
    System.setProperty("AUTH_SERVER_URL", "dummy-value");
    System.setProperty("RESPONSE_SERVER_URL", "dummy-value");
    System.setProperty("CLIENT_ID", "dummy-value");
    System.setProperty("SECRET_KEY", "dummy-value");
    System.setProperty("SMTP_HOSTNAME", "dummy-value");
    System.setProperty("SMTP_USE_IP_WHITELIST", "false");
    System.setProperty("FROM_EMAIL_DOMAIN", "dummy-value");
    System.setProperty("INSTITUTION_RESOURCES_BUCKET_NAME", "dummy-value");
  }
}
