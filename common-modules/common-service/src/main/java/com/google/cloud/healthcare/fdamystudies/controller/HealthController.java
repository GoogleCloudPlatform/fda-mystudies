/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import java.util.Collections;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  private static Map<String, String> alwaysUp = Collections.singletonMap("status", "UP");

  @GetMapping(
      value = "/healthCheck",
      produces = {MediaType.APPLICATION_JSON_VALUE})
  public Map<String, String> health() {
    return alwaysUp;
  }
}
