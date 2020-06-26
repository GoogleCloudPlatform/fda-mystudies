/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

public class WireMockInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Override
  public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
    WireMockServer wireMockServer = new WireMockServer(new WireMockConfiguration().port(8080));
    wireMockServer.start();

    configurableApplicationContext
        .getBeanFactory()
        .registerSingleton("wireMockServer", wireMockServer);

    configurableApplicationContext.addApplicationListener(
        applicationEvent -> {
          if (applicationEvent instanceof ContextClosedEvent) {
            wireMockServer.stop();
          }
        });
  }
}
