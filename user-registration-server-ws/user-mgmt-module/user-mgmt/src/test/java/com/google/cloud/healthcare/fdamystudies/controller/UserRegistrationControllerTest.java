/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.google.cloud.healthcare.fdamystudies.beans.UserRegistrationForm;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FdaEaUserDetailsServiceImpl;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserDetailsBO;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

@TestMethodOrder(OrderAnnotation.class)
public class UserRegistrationControllerTest extends BaseMockIT {

  private static final String REGISTER_PATH = "/myStudiesUserMgmtWS/register";

  @Autowired private FdaEaUserDetailsServiceImpl userDetailsService;

  @Autowired private UserRegistrationController controller;

  @Autowired private CommonService service;

  @Autowired private ObjectMapper objectMapper;

  @Value("${register.url}")
  private String authRegisterUrl;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(service);
  }

  @Test
  public void healthCheck() throws Exception {
    mockMvc.perform(get("/healthCheck")).andDo(print()).andExpect(status().isOk());
  }

  @Order(1)
  @Test
  public void shouldReturnBadRequestForInvalidUserDetails() throws Exception {
    HttpHeaders headers =
        TestUtils.getCommonHeaders(
            Constants.APP_ID_HEADER,
            Constants.ORG_ID_HEADER,
            Constants.CLIENT_ID_HEADER,
            Constants.SECRET_KEY_HEADER);

    // password is equalTo emailId
    String requestJson = getRegisterUser(Constants.EMAIL_ID, Constants.EMAIL_ID);
    mockMvc
        .perform(
            post(REGISTER_PATH).content(requestJson).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
        .andExpect(jsonPath("$.message", is(Constants.INVALID_PASSWORD)));

    verify(
        1,
        postRequestedFor(urlEqualTo("/AuthServer/register"))
            .withRequestBody(new ContainsPattern(Constants.EMAIL_ID)));

    // invalid  password
    requestJson = getRegisterUser(Constants.EMAIL_ID, Constants.INVALID_PASSWORD);
    mockMvc
        .perform(
            post(REGISTER_PATH).content(requestJson).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code", is(HttpStatus.BAD_REQUEST.value())))
        .andExpect(jsonPath("$.message", is(Constants.INVALID_PASSWORD)));

    verify(
        1,
        postRequestedFor(urlEqualTo("/AuthServer/register"))
            .withRequestBody(new ContainsPattern(Constants.INVALID_PASSWORD)));
  }

  @Order(2)
  @Test
  // TODO(#668) Remove @Disabled when Github test case failed issue fix
  @Disabled
  public void shouldReturnUnauthorized() throws Exception {

    HttpHeaders headers =
        TestUtils.getCommonHeaders(
            Constants.APP_ID_HEADER,
            Constants.ORG_ID_HEADER,
            Constants.CLIENT_ID_HEADER,
            Constants.SECRET_KEY_HEADER);

    // empty appId
    headers.set(Constants.APP_ID_HEADER, "");
    String requestJson = getRegisterUser(Constants.EMAIL_ID, Constants.PASSWORD);

    mockMvc
        .perform(
            post(REGISTER_PATH).content(requestJson).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.code", is(HttpStatus.UNAUTHORIZED.value())))
        .andExpect(jsonPath("$.message", is("Unauthorized")));

    verify(
        1,
        postRequestedFor(urlEqualTo("/AuthServer/register"))
            .withRequestBody(new ContainsPattern(Constants.PASSWORD)));
  }

  @Order(3)
  @Test
  public void shouldRegisterUser() throws Exception {
    HttpHeaders headers =
        TestUtils.getCommonHeaders(
            Constants.APP_ID_HEADER,
            Constants.ORG_ID_HEADER,
            Constants.CLIENT_ID_HEADER,
            Constants.SECRET_KEY_HEADER);

    String requestJson = getRegisterUser(Constants.EMAIL, Constants.PASSWORD);

    MvcResult result =
        mockMvc
            .perform(
                post(REGISTER_PATH)
                    .content(requestJson)
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.verified").value(Boolean.FALSE))
            .andExpect(jsonPath("$.userId").isNotEmpty())
            .andReturn();

    String userId = JsonPath.read(result.getResponse().getContentAsString(), "$.userId");
    // find userDetails by userId and assert email
    UserDetailsBO daoResp = userDetailsService.loadUserDetailsByUserId(userId);
    assertEquals(Constants.EMAIL, daoResp.getEmail());

    verify(
        1,
        postRequestedFor(urlEqualTo("/AuthServer/register"))
            .withRequestBody(new ContainsPattern(Constants.PASSWORD)));
  }

  private String getRegisterUser(String emailId, String password) throws JsonProcessingException {
    UserRegistrationForm userRegistrationForm = new UserRegistrationForm(emailId, password);
    return getObjectMapper().writeValueAsString(userRegistrationForm);
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
