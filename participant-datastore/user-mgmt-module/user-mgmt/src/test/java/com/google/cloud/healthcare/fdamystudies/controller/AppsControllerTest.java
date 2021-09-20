/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.bean.AppMetadataBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.UserStatus;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.service.AppsService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.jayway.jsonpath.JsonPath;
import java.util.Optional;
import javax.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;

public class AppsControllerTest extends BaseMockIT {

  private static final String APP_METADATA_PATH = "/participant-user-datastore/apps/appmetadata";

  @Autowired private AppsController appsController;

  @Autowired private AppsService appsServices;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private AppRepository appRepository;

  private static final String ERROR_DESCRIPTION = "$.error_description";

  private static final String CUSTOM_APP_ID = "appId";

  private static final String GET_APP_CONTACT_EMAILS_PATH = "/participant-user-datastore/apps";

  private static final String APP_DEACTIVATE_PATH =
      "/participant-user-datastore/apps/{customAppId}/appDeactivate";

  @Test
  public void contextLoads() {
    assertNotNull(appsController);
    assertNotNull(mockMvc);
    assertNotNull(appsServices);
  }

  @Test
  public void addUpdateAppMetadataSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    String requestJson = getObjectMapper().writeValueAsString(createAppMetadataBean());

    mockMvc
        .perform(
            post(APP_METADATA_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(String.valueOf(HttpStatus.OK.value()))));

    Optional<AppEntity> optApp = appRepository.findByAppId(Constants.APP_ID_VALUE);
    AppEntity app = optApp.get();
    assertNotNull(app);

    assertEquals("orgName", app.getOrganizationName());
  }

  @Test
  public void addAppMetadataSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    AppMetadataBean metadataBean = createAppMetadataBean();
    metadataBean.setAppId(Constants.NEW_APP_ID_VALUE);
    String requestJson = getObjectMapper().writeValueAsString(metadataBean);

    mockMvc
        .perform(
            post(APP_METADATA_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(String.valueOf(HttpStatus.OK.value()))));

    Optional<AppEntity> optApp = appRepository.findByAppId(Constants.NEW_APP_ID_VALUE);
    AppEntity app = optApp.get();
    assertNotNull(app);

    assertEquals(Constants.CONTACT_EMAIL_ID, app.getContactUsToEmail());
  }

  @Test
  public void addUpdateAppMetadataBadRequest() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    // without appId
    AppMetadataBean metadataBean = createAppMetadataBean();
    metadataBean.setAppId("");
    String requestJson = getObjectMapper().writeValueAsString(metadataBean);
    mockMvc
        .perform(
            post(APP_METADATA_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    // verifyTokenIntrospectRequest(1);

    // without appName
    metadataBean = createAppMetadataBean();
    metadataBean.setAppName("");
    requestJson = getObjectMapper().writeValueAsString(metadataBean);
    mockMvc
        .perform(
            post(APP_METADATA_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    //  verifyTokenIntrospectRequest(2);

  }

  public AppMetadataBean createAppMetadataBean() {
    return new AppMetadataBean(
        Constants.APP_ID_VALUE,
        Constants.APP_NAME,
        "Gateway",
        "I",
        Constants.CONTACT_EMAIL_ID,
        "feedback@gmail.com",
        "appsSupport@gmail.com",
        "from@gmail.com",
        "https://www.web.com",
        "https://www.web.com",
        "https://www.web.com",
        "https://www.web.com",
        "https://www.web.com",
        "orgName",
        "bundleId",
        "serverKey",
        "iosBUndleId",
        "iosServerKey",
        "Active",
        "1.0",
        false,
        "1.0",
        true);
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  @Test
  public void getAppContactEmailsSuccess() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    MvcResult result =
        mockMvc
            .perform(
                get(GET_APP_CONTACT_EMAILS_PATH)
                    .param(CUSTOM_APP_ID, "GCPMS002")
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    String contactUsEmailId =
        JsonPath.read(result.getResponse().getContentAsString(), "$.contactUsEmail");
    assertEquals(Constants.CONTACT_US_EMAIL, contactUsEmailId);

    String fromEmailId = JsonPath.read(result.getResponse().getContentAsString(), "$.fromEmail");
    assertEquals(Constants.FROM_EMAIL, fromEmailId);

    String appName = JsonPath.read(result.getResponse().getContentAsString(), "$.appName");
    assertEquals(Constants.APP_NAME_TEST, appName);

    String supportEmail =
        JsonPath.read(result.getResponse().getContentAsString(), "$.supportEmail");
    assertEquals(Constants.SUPPORT_EMAIL, supportEmail);
  }

  @Test
  public void getAppContactEmailsBadRequest() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    MvcResult result =
        mockMvc
            .perform(
                get(GET_APP_CONTACT_EMAILS_PATH)
                    .param(CUSTOM_APP_ID, "")
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andReturn();

    String contactUsEmailId =
        JsonPath.read(result.getResponse().getContentAsString(), ERROR_DESCRIPTION);
    assertEquals("The request cannot be fulfilled due to bad syntax", contactUsEmailId);
  }

  @Test
  public void getAppContactEmailsNotFound() throws Exception {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON);
    MvcResult result =
        mockMvc
            .perform(
                get(GET_APP_CONTACT_EMAILS_PATH)
                    .param(CUSTOM_APP_ID, "GCPMS0012")
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn();

    String contactUsEmailId =
        JsonPath.read(result.getResponse().getContentAsString(), ERROR_DESCRIPTION);
    assertEquals("App not found", contactUsEmailId);
  }

  @Test
  public void appDeactivateSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();

    mockMvc
        .perform(
            put(APP_DEACTIVATE_PATH, "GCPMS003").headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(String.valueOf(HttpStatus.OK.value()))));

    //   verifyTokenIntrospectRequest(1);

    Optional<AppEntity> optApp = appRepository.findByAppId("GCPMS003");
    AppEntity app = optApp.get();
    assertNotNull(app);

    assertEquals(UserStatus.DEACTIVATED.getDescription(), app.getAppStatus());
  }

  @Test
  public void appDeactivateNotFound() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    // without appId
    MvcResult result =
        mockMvc
            .perform(
                put(APP_DEACTIVATE_PATH, "UnkownApp")
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andReturn();
    String errorDesc = JsonPath.read(result.getResponse().getContentAsString(), ERROR_DESCRIPTION);
    assertEquals(ErrorCode.APP_NOT_FOUND.getDescription(), errorDesc);
  }
}
