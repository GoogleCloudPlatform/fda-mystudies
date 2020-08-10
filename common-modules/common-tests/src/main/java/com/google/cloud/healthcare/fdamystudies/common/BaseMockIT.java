/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.cloud.healthcare.fdamystudies.config.CommonModuleConfiguration;
import com.google.cloud.healthcare.fdamystudies.config.WireMockInitializer;
import java.util.Base64;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@ContextConfiguration(initializers = {WireMockInitializer.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("mockit")
@TestPropertySource({
  "classpath:application-mockit.properties",
  "classpath:application-mockit-common.properties"
})
public class BaseMockIT {
  private XLogger logger = XLoggerFactory.getXLogger(BaseMockIT.class.getName());

  protected static final String VALID_BEARER_TOKEN = "Bearer 7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  protected static final String VALID_TOKEN = "7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  protected static final String INVALID_BEARER_TOKEN =
      "Bearer cd57710c-1d19-4058-8bfe-a6aac3a39e35";

  protected static final String INVALID_TOKEN = "cd57710c-1d19-4058-8bfe-a6aac3a39e35";

  protected static final String AUTH_CODE_VALUE = "28889b79-d7c6-4fe3-990c-bd239c6ce199";

  protected static final ResultMatcher OK = status().isOk();

  protected static final ResultMatcher BAD_REQUEST = status().isBadRequest();

  protected static final ResultMatcher UNAUTHORIZED = status().isUnauthorized();

  protected static final ResultMatcher CREATED = status().isCreated();

  protected static final ResultMatcher NOT_FOUND = status().isNotFound();

  @Autowired private WireMockServer wireMockServer;

  @Autowired protected MockMvc mockMvc;

  @Autowired protected ServletContext servletContext;

  protected WireMockServer getWireMockServer() {
    return wireMockServer;
  }

  protected String getContextPath() {
    return servletContext.getContextPath();
  }

  protected String getEncodedAuthorization(String clientId, String clientSecret) {
    String credentials = clientId + ":" + clientSecret;
    return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
  }

  protected MvcResult performGet(
      String path,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder =
        get(path).contextPath(servletContext.getContextPath()).headers(headers);

    if (cookies.length > 0) {
      reqBuilder.cookie(cookies);
    }

    return mockMvc
        .perform(reqBuilder)
        .andDo(print())
        .andExpect(httpStatusMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)))
        .andReturn();
  }

  protected MvcResult performPost(
      String path,
      String requestBody,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder =
        post(path)
            .contextPath(servletContext.getContextPath())
            .content(requestBody)
            .headers(headers);

    if (cookies.length > 0) {
      reqBuilder.cookie(cookies);
    }

    return mockMvc
        .perform(reqBuilder)
        .andDo(print())
        .andExpect(httpStatusMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)))
        .andReturn();
  }

  @BeforeEach
  void setUp(TestInfo testInfo) {
    logger.entry(String.format("TEST STARTED: %s", testInfo.getDisplayName()));
  }

  @AfterEach
  void tearDown(TestInfo testInfo) {
    logger.exit(String.format("TEST FINISHED: %s", testInfo.getDisplayName()));
  }

  @TestConfiguration
  @Import(CommonModuleConfiguration.class)
  static class BaseMockITConfiguration {}
}
