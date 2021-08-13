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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.bean.AppMetadataBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppRepository;
import com.google.cloud.healthcare.fdamystudies.service.AppsService;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class AppsControllerTest extends BaseMockIT {

  private static final String APP_METADATA_PATH = "/participant-user-datastore/appmetadata";

  @Autowired private AppsController appsController;

  @Autowired private AppsService appsServices;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private AppRepository appRepository;

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
        "url",
        "url",
        "url",
        "url",
        "orgName",
        "bundleId",
        "serverKey",
        "iosBUndleId",
        "iosServerKey",
        "XCode",
        "0",
        Integer.valueOf(0),
        "0",
        Integer.valueOf(0));
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
