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
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class WireMockInitializer
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static WireMockServer wireMockServer = null;

  private static final int WIREMOCK_PORT = 8080;

  @Override
  public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
    configurableApplicationContext
        .getBeanFactory()
        .registerSingleton("wireMockServer", getWiremockServerInstance());
  }

  private WireMockServer getWiremockServerInstance() {
    if (wireMockServer == null) {
      wireMockServer =
          new WireMockServer(
              new WireMockConfiguration()
                  .port(WIREMOCK_PORT)
                  .fileSource(new ClasspathFileSourceWithoutLeadingSlash()));
      wireMockServer.start();
    }
    return wireMockServer;
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
