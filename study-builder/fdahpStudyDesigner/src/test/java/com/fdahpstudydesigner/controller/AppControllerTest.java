/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.PathMappingUri.SAVE_OR_UPDATE_APP_INFO;
import static com.fdahpstudydesigner.common.PathMappingUri.SAVE_OR_UPDATE_APP_PROPERTIES;
import static com.fdahpstudydesigner.common.PathMappingUri.SAVE_OR_UPDATE_APP_SETTING;
import static com.fdahpstudydesigner.common.PathMappingUri.SAVE_OR_UPDATE_DEVELOPER_CONFIG;
import static com.fdahpstudydesigner.common.PathMappingUri.STUDY_LIST;
import static com.fdahpstudydesigner.common.PathMappingUri.UPDATE_APP_ACTION;
import static com.fdahpstudydesigner.common.PathMappingUri.VIEW_APP_LIST;
import static com.fdahpstudydesigner.common.PathMappingUri.VIEW_APP_RECORD;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.ANDROID_APP_MARKED_AS_DISTRIBUTED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_ASSOCIATED_STUDIES_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_DEVELOPER_CONFIGURATIONS_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_DEVELOPER_CONFIGURATIONS_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_INFORMATION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_INFORMATION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_LIST_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_PROPERTIES_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_PROPERTIES_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_PUBLISHED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_RECORD_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_SETTINGS_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_SETTINGS_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.IOS_APP_MARKED_AS_DISTRIBUTED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_APP_CREATION_INITIATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_APP_RECORD_CREATED;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.BUTTON_TEXT;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.CUSTOM_APP_ID;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.SESSION_OBJECT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.fdahpstudydesigner.bean.AppSessionBean;
import com.fdahpstudydesigner.bo.AppsBo;
import com.fdahpstudydesigner.common.BaseMockIT;
import com.fdahpstudydesigner.common.UserAccessLevel;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AppControllerTest extends BaseMockIT {

  private static final String USER_ID_VALUE = "1";

  private static final String APP_ID_VALUE = "402883077b963c04017b9645fba20009";

  @Test
  public void AppListViewedSuccess() throws Exception {
    HttpHeaders headers = getCommonHeaders();

    SessionObject session = getSessionObject();
    session.setUserId(USER_ID_VALUE);

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(SESSION_OBJECT, session);

    MockHttpServletRequestBuilder requestBuilder =
        post(VIEW_APP_LIST.getPath()).headers(headers).sessionAttrs(sessionAttributes);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("appListPage"));

    verifyAuditEventCall(APP_LIST_VIEWED);
  }

  @Test
  public void shouldViewAppRecord() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();
    sessionAttributes.put("0appId", APP_ID_VALUE);

    MockHttpServletRequestBuilder requestBuilder =
        post(VIEW_APP_RECORD.getPath()).headers(headers).sessionAttrs(sessionAttributes);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("viewAppsInfo"));

    verifyAuditEventCall(APP_RECORD_VIEWED);
  }

  @Test
  public void shouldIntiateAppCreation() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    MockHttpServletRequestBuilder requestBuilder =
        post(VIEW_APP_RECORD.getPath()).headers(headers).sessionAttrs(sessionAttributes);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(view().name("viewAppsInfo"));

    verifyAuditEventCall(NEW_APP_CREATION_INITIATED);
  }

  @Test
  public void shouldSaveAppSection() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    AppsBo appsBo = new AppsBo();
    appsBo.setCustomAppId("GCP001");
    appsBo.setName("GCP_GatewayAPP");

    MockHttpServletRequestBuilder requestBuilder =
        post(SAVE_OR_UPDATE_APP_INFO.getPath())
            .headers(headers)
            .param(BUTTON_TEXT, "save")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, appsBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewAppsInfo.do"));

    verifyAuditEventCall(APP_INFORMATION_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldMarkAsCompleteAppSection() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    AppsBo appsBo = new AppsBo();
    appsBo.setCustomAppId("GCP001");
    appsBo.setName("GCP_GatewayAPP");

    MockHttpServletRequestBuilder requestBuilder =
        post(SAVE_OR_UPDATE_APP_INFO.getPath())
            .headers(headers)
            .param(BUTTON_TEXT, "completed")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, appsBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewAppsInfo.do"));

    verifyAuditEventCall(APP_INFORMATION_MARKED_COMPLETE);
  }

  @Test
  public void shouldSaveAppSetting() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    AppsBo appsBo = new AppsBo();
    appsBo.setId(APP_ID_VALUE);
    appsBo.setType("GT");
    appsBo.setAppPlatform("I,A");

    MockHttpServletRequestBuilder requestBuilder =
        post(SAVE_OR_UPDATE_APP_SETTING.getPath())
            .headers(headers)
            .param(BUTTON_TEXT, "save")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, appsBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewAppSettings.do"));

    verifyAuditEventCall(APP_SETTINGS_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldMarkAsCompleteAppSetting() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    AppsBo appsBo = new AppsBo();
    appsBo.setId(APP_ID_VALUE);
    appsBo.setType("GT");
    appsBo.setAppPlatform("I,A");

    MockHttpServletRequestBuilder requestBuilder =
        post(SAVE_OR_UPDATE_APP_SETTING.getPath())
            .headers(headers)
            .param(BUTTON_TEXT, "completed")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, appsBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewAppSettings.do"));

    verifyAuditEventCall(APP_SETTINGS_MARKED_COMPLETE);
  }

  @Test
  public void shouldSaveAppProperties() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    AppsBo appsBo = new AppsBo();
    appsBo.setId(APP_ID_VALUE);
    appsBo.setFeedbackEmailAddress("feedback@grr.la");
    appsBo.setContactEmailAddress("contact@grr.la");
    appsBo.setAppSupportEmailAddress("support@grr.la");

    MockHttpServletRequestBuilder requestBuilder =
        post(SAVE_OR_UPDATE_APP_PROPERTIES.getPath())
            .headers(headers)
            .param(BUTTON_TEXT, "save")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, appsBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewAppProperties.do"));

    verifyAuditEventCall(APP_PROPERTIES_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldMarkAsCompleteAppProperties() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    AppsBo appsBo = new AppsBo();
    appsBo.setId(APP_ID_VALUE);
    appsBo.setFeedbackEmailAddress("feedback@grr.la");
    appsBo.setContactEmailAddress("contact@grr.la");
    appsBo.setAppSupportEmailAddress("support@grr.la");

    MockHttpServletRequestBuilder requestBuilder =
        post(SAVE_OR_UPDATE_APP_PROPERTIES.getPath())
            .headers(headers)
            .param(BUTTON_TEXT, "completed")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, appsBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewAppProperties.do"));

    verifyAuditEventCall(APP_PROPERTIES_MARKED_COMPLETE);
  }

  @Test
  public void shouldSaveDeveloperConfig() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    AppsBo appsBo = new AppsBo();
    appsBo.setId(APP_ID_VALUE);
    appsBo.setAndroidAppBuildVersion("1.0");
    appsBo.setIosAppBuildVersion("1.0");

    MockHttpServletRequestBuilder requestBuilder =
        post(SAVE_OR_UPDATE_DEVELOPER_CONFIG.getPath())
            .headers(headers)
            .param(BUTTON_TEXT, "save")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, appsBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewDevConfigs.do"));

    verifyAuditEventCall(APP_DEVELOPER_CONFIGURATIONS_SAVED_OR_UPDATED);
  }

  @Test
  public void shouldMarkAsCompleteDeveloperConfig() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    AppsBo appsBo = new AppsBo();
    appsBo.setId(APP_ID_VALUE);
    appsBo.setAndroidAppBuildVersion("1.0");
    appsBo.setIosAppBuildVersion("1.0");

    MockHttpServletRequestBuilder requestBuilder =
        post(SAVE_OR_UPDATE_DEVELOPER_CONFIG.getPath())
            .headers(headers)
            .param(BUTTON_TEXT, "completed")
            .sessionAttrs(sessionAttributes);

    addParams(requestBuilder, appsBo);

    mockMvc
        .perform(requestBuilder)
        .andDo(print())
        .andExpect(status().isFound())
        .andExpect(view().name("redirect:viewDevConfigs.do"));

    verifyAuditEventCall(APP_DEVELOPER_CONFIGURATIONS_MARKED_COMPLETE);
  }

  @Test
  public void shouldUpdatesPublishedToApp() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    mockMvc
        .perform(
            post(UPDATE_APP_ACTION.getPath())
                .param("appId", APP_ID_VALUE)
                .param(BUTTON_TEXT, "publishAppId")
                .param(CUSTOM_APP_ID, "BTCDEVV004")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(APP_PUBLISHED);
  }

  @Test
  public void shouldCreateApp() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    mockMvc
        .perform(
            post(UPDATE_APP_ACTION.getPath())
                .param("appId", APP_ID_VALUE)
                .param(BUTTON_TEXT, "createAppId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(NEW_APP_RECORD_CREATED);
  }

  @Test
  public void shouldIOSDistributed() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    mockMvc
        .perform(
            post(UPDATE_APP_ACTION.getPath())
                .param("appId", APP_ID_VALUE)
                .param(BUTTON_TEXT, "iosDistributedId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(IOS_APP_MARKED_AS_DISTRIBUTED);
  }

  @Test
  public void shouldAndroidDistributed() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    mockMvc
        .perform(
            post(UPDATE_APP_ACTION.getPath())
                .param("appId", APP_ID_VALUE)
                .param(BUTTON_TEXT, "androidDistributedId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(ANDROID_APP_MARKED_AS_DISTRIBUTED);
  }

  @Test
  public void shouldDeactivateApp() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    HashMap<String, Object> sessionAttributes = getSessionAttrs();

    mockMvc
        .perform(
            post(UPDATE_APP_ACTION.getPath())
                .param("appId", APP_ID_VALUE)
                .param(BUTTON_TEXT, "deactivateId")
                .headers(headers)
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    // verifyAuditEventCall(APP_DEACTIVATED);
  }

  @Test
  public void shouldViewAppAssociatedStudyList() throws Exception {
    HttpHeaders headers = getCommonHeaders();
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setStudySession(new ArrayList<>(Arrays.asList(0)));
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(FdahpStudyDesignerConstants.SESSION_OBJECT, session);

    mockMvc
        .perform(
            post(STUDY_LIST.getPath())
                .headers(headers)
                .param("appId", "GCP001")
                .sessionAttrs(sessionAttributes))
        .andDo(print())
        .andExpect(status().isOk());

    verifyAuditEventCall(APP_ASSOCIATED_STUDIES_VIEWED);
  }

  private HashMap<String, Object> getSessionAttrs() {
    SessionObject session = new SessionObject();
    session.setUserId(USER_ID_VALUE);
    session.setSessionId(UUID.randomUUID().toString());
    session.setAccessLevel(UserAccessLevel.SUPER_ADMIN.getValue());

    session.setAppSession(new ArrayList<>(Arrays.asList(0)));
    List<AppSessionBean> appSessionBeans = new ArrayList<>();
    AppSessionBean appSessionBean = new AppSessionBean();

    appSessionBean.setAppId(APP_ID_VALUE);
    appSessionBean.setSessionAppCount(0);
    appSessionBeans.add(appSessionBean);
    session.setAppSessionBeans(appSessionBeans);

    HashMap<String, Object> sessionAttributes = getSessionAttributes();
    sessionAttributes.put(SESSION_OBJECT, session);
    return sessionAttributes;
  }
}
