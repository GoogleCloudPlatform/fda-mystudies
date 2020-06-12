/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.cloud.healthcare.fdamystudies.config.WireMockInitializer;

@ContextConfiguration(initializers = {WireMockInitializer.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mockit")
@AutoConfigureMockMvc
public class BaseMockIT {

  protected static final ResultMatcher OK = status().isOk();

  protected static final ResultMatcher BAD_REQUEST = status().isBadRequest();

  protected static final ResultMatcher FORBIDDEN = status().isForbidden();

  protected static final ResultMatcher UNAUTHORIZED = status().isUnauthorized();

  protected static final ResultMatcher CONFLICT = status().isConflict();

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

  protected void performPost(
      String path,
      String requestJson,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher resultMatcher)
      throws Exception {
    mockMvc
        .perform(post(path).content(requestJson).headers(headers))
        .andDo(print())
        .andExpect(resultMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)));
  }

  protected void performGet(
      String path,
      String requestJson,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher resultMatcher)
      throws Exception {
    mockMvc
        .perform(get(path).content(requestJson).headers(headers))
        .andDo(print())
        .andExpect(resultMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)));
  }

  protected void performDelete(
      String path,
      String requestJson,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher resultMatcher)
      throws Exception {
    mockMvc
        .perform(delete(path).content(requestJson).headers(headers))
        .andDo(print())
        .andExpect(resultMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)));
  }
}
