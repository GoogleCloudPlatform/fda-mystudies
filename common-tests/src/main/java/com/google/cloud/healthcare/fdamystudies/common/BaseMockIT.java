/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Base64;
import java.util.Collections;
import javax.servlet.http.Cookie;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.cloud.healthcare.fdamystudies.config.WireMockInitializer;

@ContextConfiguration(initializers = {WireMockInitializer.class})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("mockit")
public class BaseMockIT {

  protected static final String AUTHORIZATION = "Authorization";

  public static final String CORRELATION_ID = "correlation_id";

  protected static final String VALID_BEARER_TOKEN = "Bearer 7fd50c2c-d618-493c-89d6-f1887e3e4bb8";

  protected static final String INVALID_BEARER_TOKEN =
      "Bearer cd57710c-1d19-4058-8bfe-a6aac3a39e35";

  protected static final String VALID_CORRELATION_ID = "8a56d20c-d755-4487-b80d-22d5fa383046";

  protected static final ResultMatcher OK = status().isOk();

  protected static final ResultMatcher BAD_REQUEST = status().isBadRequest();

  protected static final ResultMatcher UNAUTHORIZED = status().isUnauthorized();

  protected static final ResultMatcher CREATED = status().isCreated();

  protected static final ResultMatcher NOT_FOUND = status().isNotFound();

  protected static final ResultMatcher UNSUPPORTED_MEDIATYPE = status().isUnsupportedMediaType();

  protected static final ResultMatcher REDIRECTION = status().is3xxRedirection();

  protected static final ResultMatcher CONFLICT = status().isConflict();

  @Autowired private WireMockServer wireMockServer;

  @LocalServerPort protected int randomServerPort;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private MockMvc mockMvc;

  @Value("${server.servlet.contextPath}")
  private String contextPath;

  protected WireMockServer getWireMockServer() {
    return wireMockServer;
  }

  protected ObjectNode getObjectNode() {
    return objectMapper.createObjectNode();
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  protected String getEncodedAuthorization(String clientId, String clientSecret) {
    String credentials = clientId + ":" + clientSecret;
    return Base64.getEncoder().encodeToString(credentials.getBytes());
  }

  protected HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add(CORRELATION_ID, VALID_CORRELATION_ID);
    return headers;
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
        post(path).contextPath(contextPath).content(requestBody).headers(headers);

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
      MultiValueMap<String, String> paramsMap,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder =
        post(path).contextPath(contextPath).params(paramsMap).headers(headers);

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
      MultiValueMap<String, String> paramsMap,
      HttpHeaders headers,
      String redirectUrl,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder =
        post(path).contextPath(contextPath).params(paramsMap).headers(headers);

    if (cookies.length > 0) {
      reqBuilder.cookie(cookies);
    }

    return mockMvc
        .perform(reqBuilder)
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(redirectUrl))
        .andReturn();
  }

  protected MvcResult performPatch(
      String path,
      String requestBody,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder =
        patch(path).contextPath(contextPath).headers(headers);

    if (cookies.length > 0) {
      reqBuilder.cookie(cookies);
    }

    if (StringUtils.isNotEmpty(requestBody)) {
      reqBuilder.content(requestBody);
    }

    return mockMvc
        .perform(reqBuilder)
        .andDo(print())
        .andExpect(httpStatusMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)))
        .andReturn();
  }

  protected MvcResult performGet(
      String path,
      MultiValueMap<String, String> queryParamsMap,
      HttpHeaders headers,
      String redirectUrl,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder = get(path).contextPath(contextPath).headers(headers);

    if (queryParamsMap != null) {
      reqBuilder.queryParams(queryParamsMap);
    }

    if (cookies.length > 0) {
      reqBuilder.cookie(cookies);
    }

    return mockMvc
        .perform(reqBuilder)
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(redirectUrl))
        .andReturn();
  }

  protected MvcResult performGet(
      String path,
      MultiValueMap<String, String> paramsMap,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder = get(path).contextPath(contextPath).headers(headers);

    if (cookies.length > 0) {
      reqBuilder.cookie(cookies);
    }

    if (paramsMap != null) {
      reqBuilder.params(paramsMap);
    }

    return mockMvc
        .perform(reqBuilder)
        .andDo(print())
        .andExpect(httpStatusMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)))
        .andReturn();
  }

  protected MvcResult performGet(
      String path,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder = get(path).contextPath(contextPath).headers(headers);

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

  protected MvcResult performDelete(
      String path,
      String requestBody,
      HttpHeaders headers,
      String expectedTextInResponseBody,
      ResultMatcher httpStatusMatcher,
      Cookie... cookies)
      throws Exception {

    MockHttpServletRequestBuilder reqBuilder =
        delete(path).contextPath(contextPath).headers(headers);

    if (cookies.length > 0) {
      reqBuilder.cookie(cookies);
    }

    if (StringUtils.isNotEmpty(requestBody)) {
      reqBuilder.content(requestBody);
    }

    return mockMvc
        .perform(reqBuilder)
        .andDo(print())
        .andExpect(httpStatusMatcher)
        .andExpect(content().string(containsString(expectedTextInResponseBody)))
        .andReturn();
  }
}
