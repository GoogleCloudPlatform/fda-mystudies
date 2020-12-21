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

  public static boolean wireMockRunning = false;

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
    if (!wireMockRunning) {
      wireMockServer.start();
      wireMockRunning = true;
    }
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
