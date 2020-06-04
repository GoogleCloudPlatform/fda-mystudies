/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.auditlog.common;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.cloud.healthcare.fdamystudies.auditlog.config.WireMockInitializer;

@ContextConfiguration(initializers = {WireMockInitializer.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("mockit")
public class BaseMockIT {

  protected static final String VALID_BEARER_TOKEN = "Bearer 7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  protected static final String INVALID_BEARER_TOKEN =
      "Bearer cd57710c-1d19-4058-8bfe-a6aac3a39e35";

  protected static final ResultMatcher OK = status().isOk();

  protected static final ResultMatcher BAD_REQUEST = status().isBadRequest();

  protected static final ResultMatcher UNAUTHORIZED = status().isUnauthorized();

  protected static final ResultMatcher CREATED = status().isCreated();

  protected static final ResultMatcher NOT_FOUND = status().isNotFound();

  @Autowired private WireMockServer wireMockServer;

  @Autowired protected MockMvc mockMvc;

  @Value("${server.servlet.contextPath}")
  protected String contextPath;

  protected WireMockServer getWireMockServer() {
    return wireMockServer;
  }

  protected void performPost(
      String path,
      String requestBody,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher)
      throws Exception {
    mockMvc
        .perform(post(path).contextPath(contextPath).content(requestBody).headers(headers))
        .andDo(print())
        .andExpect(httpStatusMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)));
  }

  protected void performGet(
      String path,
      String requestBody,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher)
      throws Exception {

    if (StringUtils.isEmpty(requestBody)) {
      mockMvc
          .perform(get(path).contextPath(contextPath).headers(headers))
          .andDo(print())
          .andExpect(httpStatusMatcher)
          .andExpect(content().string(containsString(expectedTextInResponseBody)));
    } else {
      mockMvc
          .perform(get(path).contextPath(contextPath).content(requestBody).headers(headers))
          .andDo(print())
          .andExpect(httpStatusMatcher)
          .andExpect(content().string(containsString(expectedTextInResponseBody)));
    }
  }
}
