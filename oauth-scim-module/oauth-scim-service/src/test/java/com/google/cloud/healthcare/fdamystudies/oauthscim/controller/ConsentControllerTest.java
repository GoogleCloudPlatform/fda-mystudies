/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint.CONSENT_PAGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.CONSENT_CHALLENGE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.MOBILE_PLATFORM;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.MobilePlatform;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import javax.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
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
    Cookie mobilePlatformCookie = new Cookie(MOBILE_PLATFORM, MobilePlatform.UNKNOWN.getValue());
    Cookie consentChallengeCookie = new Cookie(CONSENT_CHALLENGE, CONSENT_CHALLENGE_VALUE);
    Cookie userIdCookie = new Cookie(USER_ID, USER_ID_VALUE);
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

  private MultiValueMap<String, String> getConsentQSParams() {
    MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
    requestParams.add(CONSENT_CHALLENGE, CONSENT_CHALLENGE_VALUE);
    return requestParams;
  }
}
