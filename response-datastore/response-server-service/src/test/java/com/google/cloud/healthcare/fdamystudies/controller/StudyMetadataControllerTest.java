/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.asJsonString;
import static com.google.cloud.healthcare.fdamystudies.common.JsonUtils.readJsonFile;
import static com.google.cloud.healthcare.fdamystudies.common.ResponseServerEvent.STUDY_METADATA_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.utils.Constants.CONTACT_EMAIL_ID;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.bean.StudyMetadataBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.dao.CloudFirestoreResponsesDaoImpl;
import com.google.cloud.healthcare.fdamystudies.service.StudyMetadataServiceImpl;
import com.google.cloud.healthcare.fdamystudies.utils.TestUtils;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MvcResult;

public class StudyMetadataControllerTest extends BaseMockIT {

  private static final String APP_ID_VALUE = "TEST_APP_ID";

  private static final String APP_ID = "appId";

  public static final String LOGO_IMAGE_URL =
      "http://localhost:8098/fdaResources/studylogo/example.jpg";

  @MockBean private CloudFirestoreResponsesDaoImpl responsesDaoMock;

  @InjectMocks private StudyMetadataServiceImpl studyMetadataService;

  @Captor ArgumentCaptor<String> studyCollectionNameCaptor;
  @Captor ArgumentCaptor<String> studyIdCaptor;
  @Captor ArgumentCaptor<Map<String, Object>> dataToStoreCaptor;

  @Test
  void testStudyMetadataSavedContent() throws Exception {
    Map<String, Object> dataToStore = new HashMap<>();
    dataToStore.put(APP_ID, APP_ID_VALUE);

    // Step-1 Mock CloudFirestoreResponsesDaoImpl
    doNothing()
        .when(responsesDaoMock)
        .saveStudyMetadata("TEST_STUDY_ID-RESPONSES", "TEST_STUDY_ID", new HashMap<>());

    HttpHeaders headers = TestUtils.newCommonHeaders();
    StudyMetadataBean studyMetadataBeanRequest = createValidStudyMetadataBean();

    mockMvc
        .perform(
            post(ApiEndpoint.STUDYMETADATA.getPath())
                .contextPath(getContextPath())
                .content(asJsonString(studyMetadataBeanRequest))
                .headers(headers))
        .andDo(print())
        .andExpect(status().isOk());

    verify(responsesDaoMock)
        .saveStudyMetadata(
            studyCollectionNameCaptor.capture(),
            studyIdCaptor.capture(),
            dataToStoreCaptor.capture());

    // Step-4: assert argument capture
    assertEquals("TEST_STUDY_ID", studyIdCaptor.getValue());
    assertEquals("TEST_STUDY_ID-RESPONSES", studyCollectionNameCaptor.getValue());
    assertEquals(APP_ID_VALUE, dataToStoreCaptor.getValue().get(APP_ID));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setStudyId(studyMetadataBeanRequest.getStudyId());
    auditRequest.setStudyVersion("1.0");
    auditRequest.setAppId(studyMetadataBeanRequest.getAppId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(STUDY_METADATA_RECEIVED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, STUDY_METADATA_RECEIVED);
  }

  @Test
  void testStudyMetadataSavedContentInvalidStudyId() throws Exception {
    HttpHeaders headers = TestUtils.newCommonHeaders();
    // Step 1: set empty studyId
    StudyMetadataBean studyMetadataBeanRequest = createValidStudyMetadataBean();
    studyMetadataBeanRequest.setStudyId("");
    // Step 2: call API and expect bad request
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.STUDYMETADATA.getPath())
                    .contextPath(getContextPath())
                    .content(asJsonString(studyMetadataBeanRequest))
                    .headers(headers))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.appErrorCode").isNumber())
            .andExpect(jsonPath("$.userMessage").isNotEmpty())
            .andReturn();

    String actualResponse = result.getResponse().getContentAsString();
    String expectedResponse = readJsonFile("/invalid_args_expected_bad_request_response.json");
    JSONAssert.assertEquals(expectedResponse, actualResponse, JSONCompareMode.NON_EXTENSIBLE);
  }

  private StudyMetadataBean createValidStudyMetadataBean() {
    return new StudyMetadataBean(
        "TEST_STUDY_ID",
        "TEST_TITLE",
        "1.0",
        "Open",
        "Active",
        "Health",
        "TEST_TAGLINE",
        "TEST_SPONSOR",
        "Yes",
        APP_ID_VALUE,
        "Test App",
        "Test app for population health study",
        LOGO_IMAGE_URL,
        CONTACT_EMAIL_ID);
  }
}
