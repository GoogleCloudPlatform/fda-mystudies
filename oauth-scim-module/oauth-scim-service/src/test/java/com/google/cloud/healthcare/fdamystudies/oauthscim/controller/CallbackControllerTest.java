package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ACCOUNT_STATUS_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.AUTHORIZATION;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.MOBILE_PLATFORM_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.USER_ID_COOKIE;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_FAILED;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimEvent.SIGNIN_SUCCEEDED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.IdGenerator;
import com.google.cloud.healthcare.fdamystudies.common.MobilePlatform;
import com.google.cloud.healthcare.fdamystudies.oauthscim.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.oauthscim.config.RedirectConfig;
import java.util.Collections;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class CallbackControllerTest extends BaseMockIT {

  private static final String USER_ID_VALUE = "4e626d41-7f42-43a6-b749-ee4b6635ac66";

  @Autowired private RedirectConfig redirectConfig;

  @Test
  public void shouldReturnErrorPageWhenAuthorizationCodeIsEmpty() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("code", "");

    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie userIdCookie = new Cookie(USER_ID_COOKIE, USER_ID_VALUE);
    Cookie accountStatusCookie = new Cookie(ACCOUNT_STATUS_COOKIE, "0");

    mockMvc
        .perform(
            get(ApiEndpoint.CALLBACK.getPath())
                .headers(getCommonHeaders())
                .contextPath(getContextPath())
                .queryParams(queryParams)
                .cookie(mobilePlatformCookie, userIdCookie, accountStatusCookie))
        .andDo(print())
        .andExpect(view().name(ERROR_VIEW_NAME));

    verifyAuditEventCall(SIGNIN_FAILED);
  }

  @Test
  public void shouldReturnErrorPageWhenUserIdCookieNotFound() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("code", AUTH_CODE_VALUE);

    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie accountStatusCookie = new Cookie(ACCOUNT_STATUS_COOKIE, "0");

    mockMvc
        .perform(
            get(ApiEndpoint.CALLBACK.getPath())
                .headers(getCommonHeaders())
                .contextPath(getContextPath())
                .queryParams(queryParams)
                .cookie(mobilePlatformCookie, accountStatusCookie))
        .andDo(print())
        .andExpect(view().name(ERROR_VIEW_NAME));

    verifyAuditEventCall(SIGNIN_FAILED);
  }

  @Test
  public void shouldRedirectToCallbackUrl() throws Exception {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.add("code", AUTH_CODE_VALUE);

    HttpHeaders headers = getCommonHeaders();

    Cookie mobilePlatformCookie =
        new Cookie(MOBILE_PLATFORM_COOKIE, MobilePlatform.UNKNOWN.getValue());
    Cookie userIdCookie = new Cookie(USER_ID_COOKIE, USER_ID_VALUE);
    Cookie accountStatusCookie = new Cookie(ACCOUNT_STATUS_COOKIE, "0");

    String callbackUrl = redirectConfig.getCallbackUrl(MobilePlatform.UNKNOWN.getValue());
    String expectedRedirectUrl =
        String.format(
            "%s?code=%s&userId=%s&accountStatus=0", callbackUrl, AUTH_CODE_VALUE, USER_ID_VALUE);

    mockMvc
        .perform(
            get(ApiEndpoint.CALLBACK.getPath())
                .contextPath(getContextPath())
                .headers(headers)
                .queryParams(queryParams)
                .cookie(mobilePlatformCookie, userIdCookie, accountStatusCookie))
        .andDo(print())
        .andExpect(status().is3xxRedirection())
        .andExpect(redirectedUrl(expectedRedirectUrl))
        .andReturn();
    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(SIGNIN_SUCCEEDED.getEventCode(), auditRequest);
    verifyAuditEventCall(auditEventMap, SIGNIN_SUCCEEDED);
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
