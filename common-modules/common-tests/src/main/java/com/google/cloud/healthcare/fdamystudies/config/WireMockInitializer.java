/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.common.ClasspathFileSource;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

public class WireMockInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {
  private XLogger logger = XLoggerFactory.getXLogger(WireMockInitializer.class.getName());

  @Override
  public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
    WireMockServer wireMockServer =
        new WireMockServer(
            new WireMockConfiguration()
                .port(8080)
                .fileSource(new ClasspathFileSourceWithoutLeadingSlash()));

    configurableApplicationContext
        .getBeanFactory()
        .registerSingleton("wireMockServer", wireMockServer);

    configurableApplicationContext.addApplicationListener(
        applicationEvent -> {
          if (applicationEvent instanceof ApplicationStartedEvent) {
            try {
              logger.error("Restart the WireMockServer");
              // We have to sleep briefly to finish serving the shutdown request before stopping the
              // server, as there's no support in Jetty for shutting down after the current request.
              // See http://stackoverflow.com/questions/4650713
              Thread.sleep(1000);
              wireMockServer.stop();
              logger.error("WireMockServer stopped,starting the server again");
              wireMockServer.start();
            } catch (Exception e) {
              logger.error("Unable to restart WireMockServer", e);
            }
          }

          if (applicationEvent instanceof ContextClosedEvent) {
            try {
              logger.error("stop the WireMockServer");
              // We have to sleep briefly to finish serving the shutdown request before stopping the
              // server, as
              // there's no support in Jetty for shutting down after the current request.
              // See http://stackoverflow.com/questions/4650713
              Thread.sleep(1000);
              wireMockServer.stop();
            } catch (Exception e) {
              logger.error("Unable to stop WireMockServer", e);
            }
          }
        });
  }

  /*
   * Without this class Wiremock tries to find the mappings directory under /mappings and the classloader will not find this
   * directory because of the leading slash. This class removes the leading slash and as a consequence the classloader
   * will find the mappings directory.
   */
  class ClasspathFileSourceWithoutLeadingSlash extends ClasspathFileSource {

    ClasspathFileSourceWithoutLeadingSlash() {
      super("");
    }

    @Override
    public FileSource child(String subDirectoryName) {
      return new ClasspathFileSource(subDirectoryName);
    }
  }
}
