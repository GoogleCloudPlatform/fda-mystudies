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
import org.springframework.stereotype.Component;

@Component
public class WireMockTestServer {

  private WireMockServer wireMockServer = null;

  public void start() throws InterruptedException {
    wireMockServer =
        new WireMockServer(
            new WireMockConfiguration()
                .port(8080)
                .fileSource(new ClasspathFileSourceWithoutLeadingSlash()));

    wireMockServer.start();
    Thread.sleep(2000);
  }

  public void stop() throws InterruptedException {
    // "Unexpected end of file" implies that the remote server accepted and closed the connection
    // without sending a response. 2 seconds sleep added to avoid this error.
    Thread.sleep(2000);
    wireMockServer.stop();
    Thread.sleep(2000);
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
