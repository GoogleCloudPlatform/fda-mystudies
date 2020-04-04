/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
  private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

  @GetMapping("/health")
  public String ping() {
    logger.info(" Response PingController - ping()");
    return "MyStudies Response Server is up!";
  }
}
