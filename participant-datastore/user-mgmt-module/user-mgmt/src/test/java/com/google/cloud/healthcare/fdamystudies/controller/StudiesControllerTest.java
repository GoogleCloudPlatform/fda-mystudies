/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.STUDY_METADATA_RECEIVED;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationBean;
import com.google.cloud.healthcare.fdamystudies.beans.NotificationForm;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.dao.CommonDaoImpl;
import com.google.cloud.healthcare.fdamystudies.model.AppPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.SitePermissionEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyPermissionEntity;
import com.google.cloud.healthcare.fdamystudies.repository.AppPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.SitePermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.StudyPermissionRepository;
import com.google.cloud.healthcare.fdamystudies.repository.UserRegAdminRepository;
import com.google.cloud.healthcare.fdamystudies.service.StudiesServices;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.healthcare.fdamystudies.util.ErrorCode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class StudiesControllerTest extends BaseMockIT {

  private static final String STUDY_METADATA_PATH =
      "/participant-user-datastore/studies/studymetadata";

  private static final String SEND_NOTIFICATION_PATH =
      "/participant-user-datastore/studies/sendNotification";

  @Autowired private StudiesController studiesController;

  @Autowired private StudiesServices studiesServices;

  @Autowired private CommonDaoImpl commonDao;

  @Autowired private AppPermissionRepository appPermissionRepository;

  @Autowired private StudyPermissionRepository studyPermissionRepository;

  @Autowired private UserRegAdminRepository userRegAdminUserRepository;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private SitePermissionRepository sitePermissionRepository;

  @Test
  public void contextLoads() {
    assertNotNull(studiesController);
    assertNotNull(mockMvc);
    assertNotNull(studiesServices);
    assertNotNull(commonDao);
  }

  public StudyMetadataBean createStudyMetadataBean() {
    return new StudyMetadataBean(
        Constants.STUDY_ID,
        Constants.STUDY_TITLE,
        Constants.STUDY_VERSION,
        Constants.STUDY_TYPE,
        Constants.STUDY_STATUS,
        Constants.STUDY_CATEGORY,
        Constants.STUDY_TAGLINE,
        Constants.STUDY_SPONSOR,
        Constants.STUDY_ENROLLING,
        Constants.APP_ID_VALUE,
        Constants.APP_NAME,
        Constants.APP_DESCRIPTION,
        Constants.LOGO_IMAGE_URL,
        Constants.CONTACT_EMAIL_ID);
  }

  @Test
  public void addUpdateStudyMetadataSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    String requestJson = getObjectMapper().writeValueAsString(createStudyMetadataBean());

    mockMvc
        .perform(
            post(STUDY_METADATA_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(String.valueOf(HttpStatus.OK.value()))));

    HashSet<String> set = new HashSet<>();
    set.add(Constants.STUDY_ID);
    List<StudyEntity> list = commonDao.getStudyInfoSet(set);
    StudyEntity studyInfoBo =
        list.stream()
            .filter(x -> x.getCustomId().equals(Constants.STUDY_ID))
            .findFirst()
            .orElse(null);
    assertNotNull(studyInfoBo);
    assertEquals(Constants.STUDY_SPONSOR, studyInfoBo.getSponsor());
    assertEquals(Constants.STUDY_TAGLINE, studyInfoBo.getTagline());
    assertEquals(Constants.LOGO_IMAGE_URL, studyInfoBo.getLogoImageUrl());

    verifyTokenIntrospectRequest(1);

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setStudyId(Constants.STUDY_ID);
    auditRequest.setStudyVersion(Constants.STUDY_VERSION);
    auditRequest.setAppId(Constants.APP_ID_VALUE);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(STUDY_METADATA_RECEIVED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, STUDY_METADATA_RECEIVED);
  }

  @Test
  public void addStudyMetadataSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    StudyMetadataBean studyMetaDataBean = createStudyMetadataBean();
    studyMetaDataBean.setStudyId(Constants.NEW_STUDY_ID);
    studyMetaDataBean.setAppId(Constants.NEW_APP_ID_VALUE);
    String requestJson = getObjectMapper().writeValueAsString(studyMetaDataBean);
    performPost(
        STUDY_METADATA_PATH, requestJson, headers, String.valueOf(HttpStatus.OK.value()), OK);
    HashSet<String> set = new HashSet<>();
    set.add(Constants.NEW_STUDY_ID);
    List<StudyEntity> list = commonDao.getStudyInfoSet(set);
    StudyEntity studyInfoBo =
        list.stream()
            .filter(x -> x.getCustomId().equals(Constants.NEW_STUDY_ID))
            .findFirst()
            .orElse(null);
    assertNotNull(studyInfoBo);
    assertEquals(Constants.NEW_STUDY_ID, studyInfoBo.getCustomId());
    assertEquals(Constants.LOGO_IMAGE_URL, studyInfoBo.getLogoImageUrl());
  }

  @Test
  public void addStudyMetadataSuccessForPermission() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    StudyMetadataBean studyMetaDataBean = createStudyMetadataBean();
    studyMetaDataBean.setStudyId(Constants.STUDY_ID_1);
    studyMetaDataBean.setAppId(Constants.APP_ID_VALUE);
    String requestJson = getObjectMapper().writeValueAsString(studyMetaDataBean);
    performPost(
        STUDY_METADATA_PATH, requestJson, headers, String.valueOf(HttpStatus.OK.value()), OK);

    List<AppPermissionEntity> appPermissionList = appPermissionRepository.findAll();
    List<StudyPermissionEntity> studyPermissionList = studyPermissionRepository.findAll();
    List<SitePermissionEntity> sitePermissionList = sitePermissionRepository.findAll();

    AppPermissionEntity appPermission =
        appPermissionList
            .stream()
            .filter(x -> x.getApp().getAppId().equals(Constants.APP_ID_VALUE))
            .findFirst()
            .orElse(null);
    StudyPermissionEntity studyPermission =
        studyPermissionList
            .stream()
            .filter(x -> x.getStudy().getCustomId().equals(Constants.STUDY_ID_1))
            .findFirst()
            .orElse(null);
    SitePermissionEntity sitePermission =
        sitePermissionList
            .stream()
            .filter(x -> x.getStudy().getType().equals(Constants.STUDY_TYPE))
            .findFirst()
            .orElse(null);

    assertNotNull(appPermission);
    assertNotNull(studyPermission);
    assertNotNull(sitePermission);
    assertEquals(appPermission.getEdit(), studyPermission.getEdit());
    assertEquals(Constants.STUDY_TYPE, sitePermission.getStudy().getType());
  }

  @Test
  public void addUpdateStudyMetadataBadRequest() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    // without studyId
    StudyMetadataBean metadataBean = createStudyMetadataBean();
    metadataBean.setStudyId("");
    String requestJson = getObjectMapper().writeValueAsString(metadataBean);
    mockMvc
        .perform(
            post(STUDY_METADATA_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(1);

    // without studyVersion
    metadataBean = createStudyMetadataBean();
    metadataBean.setStudyVersion("");
    requestJson = getObjectMapper().writeValueAsString(metadataBean);
    mockMvc
        .perform(
            post(STUDY_METADATA_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(2);

    // without appId
    metadataBean = createStudyMetadataBean();
    metadataBean.setAppId("");
    requestJson = getObjectMapper().writeValueAsString(metadataBean);
    mockMvc
        .perform(
            post(STUDY_METADATA_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(3);
  }

  @Test
  public void sendNotificationBadRequest() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();

    // null body
    NotificationForm notificationForm = null;
    String requestJson = getObjectMapper().writeValueAsString(notificationForm);
    mockMvc
        .perform(
            post(SEND_NOTIFICATION_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(1);

    // empty notificationType
    requestJson =
        getNotificationForm(
            Constants.STUDY_ID, Constants.CUSTOM_STUDY_ID, Constants.APP_ID_HEADER, "");
    mockMvc
        .perform(
            post(SEND_NOTIFICATION_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());
    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void sendNotificationSuccess() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();

    // StudyLevel notificationType
    String requestJson =
        getNotificationForm(
            Constants.STUDY_ID,
            Constants.CUSTOM_STUDY_ID,
            Constants.APP_ID_VALUE,
            Constants.STUDY_LEVEL);

    mockMvc
        .perform(
            post(SEND_NOTIFICATION_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(ErrorCode.EC_200.errorMessage())))
        .andExpect(jsonPath("$.code", is(ErrorCode.EC_200.code())))
        .andExpect(jsonPath("$.response.multicast_id", greaterThan(0L)))
        .andExpect(
            jsonPath(
                "$.response.results[0].message_id", is("0:1491324495516461%31bd1c9631bd1c96")));
    verifyTokenIntrospectRequest(1);

    // GatewayLevel notificationType
    requestJson =
        getNotificationForm(
            Constants.STUDY_ID,
            Constants.CUSTOM_STUDY_ID,
            Constants.APP_ID_VALUE,
            Constants.GATEWAY_LEVEL);

    mockMvc
        .perform(
            post(SEND_NOTIFICATION_PATH)
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", is(ErrorCode.EC_200.errorMessage())))
        .andExpect(jsonPath("$.code", is(ErrorCode.EC_200.code())))
        .andExpect(jsonPath("$.response.multicast_id", greaterThan(0L)))
        .andExpect(
            jsonPath(
                "$.response.results[0].message_id", is("0:1491324495516461%31bd1c9631bd1c96")));

    verifyTokenIntrospectRequest(2);
  }

  private String getNotificationForm(
      String studyId, String customStudyId, String appId, String notificationType)
      throws JsonProcessingException {

    NotificationBean notificationBean = null;
    notificationBean = new NotificationBean(studyId, customStudyId, appId, notificationType);
    List<NotificationBean> list = new ArrayList<NotificationBean>();
    list.add(notificationBean);
    NotificationForm notificationForm = new NotificationForm(list);
    return getObjectMapper().writeValueAsString(notificationForm);
  }

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}
