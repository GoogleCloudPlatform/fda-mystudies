/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.cloud.healthcare.fdamystudies.config.WireMockInitializer;

@ContextConfiguration(initializers = {WireMockInitializer.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mockit")
@AutoConfigureMockMvc
public class BaseMockIT {

  protected static final String VALID_BEARER_TOKEN = "Bearer 7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  protected static final String INVALID_BEARER_TOKEN =
      "Bearer cd57710c-1d19-4058-8bfe-a6aac3a39e35";

  @Autowired private WireMockServer wireMockServer;

  @LocalServerPort protected int randomServerPort;

  @Autowired private ObjectMapper objectMapper;

  @Autowired protected MockMvc mockMvc;

  protected WireMockServer getWireMockServer() {
    return wireMockServer;
  }

  protected ObjectNode getObjectNode() {
    return objectMapper.createObjectNode();
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
