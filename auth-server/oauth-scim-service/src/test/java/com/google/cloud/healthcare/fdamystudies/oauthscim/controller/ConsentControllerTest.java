/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint.CONSENT_PAGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTHORIZATION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CONSENT_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CONSENT_CHALLENGE_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.MOBILE_PLATFORM_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.MobilePlatform;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import java.util.Collections;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class ConsentControllerTest extends BaseMockIT {

  protected static final String CONSENT_CHALLENGE_VALUE = "c11e62a0-5555-4b0e-b498-c878f5e4bd85";

  private static final String USER_ID_VALUE = "4e626d41-7f42-43a6-b749-ee4b6635ac66";

  @Test
  public void shouldReturnConsentPage() throws Exception {
    mockMvc
        .perform(
            get(ApiEndpoint.CONSENT_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(getConsentQSParams()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString("<title>Please wait</title>")))
        .andReturn();
  }

  @Test
  public void shouldReturnLoginPage() throws Exception {
    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie consentChallengeCookie = new Cookie(CONSENT_CHALLENGE_COOKIE, CONSENT_CHALLENGE_VALUE);
    Cookie userIdCookie = new Cookie(USER_ID_COOKIE, USER_ID_VALUE);
    mockMvc
        .perform(
            post(CONSENT_PAGE.getPath())
                .contextPath(getContextPath())
                .queryParams(getConsentQSParams())
                .cookie(mobilePlatformCookie, consentChallengeCookie, userIdCookie))
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(ApiEndpoint.LOGIN_PAGE.getUrl()))
        .andReturn();
  }

  @Test
  public void shouldReturnErrorPage() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    mockMvc
        .perform(
            get(ApiEndpoint.CONSENT_PAGE.getPath())
                .headers(getCommonHeaders())
                .contextPath(getContextPath())
                .queryParams(queryParams))
        .andDo(print())
        .andExpect(view().name(ERROR_VIEW_NAME));

    verifyAuditEventCall(SIGNIN_FAILED);
  }

  private MultiValueMap<String, String> getConsentQSParams() {
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(CONSENT_CHALLENGE, CONSENT_CHALLENGE_VALUE);
    return requestParams;
  }

  private HttpHeaders getCommonHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    headers.add(AUTHORIZATION, VALID_BEARER_TOKEN);
    headers.add("appVersion", "1.0");
    headers.add("appId", "SCIM AUTH SERVER");
    headers.add("studyId", "MyStudies");
    headers.add("source", "SCIM AUTH SERVER");
    headers.add("correlationId", IdGenerator.id());
    return headers;
  }
}
