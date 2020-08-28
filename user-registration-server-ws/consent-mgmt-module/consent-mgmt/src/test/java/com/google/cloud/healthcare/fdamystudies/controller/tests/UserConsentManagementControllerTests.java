/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.INFORMED_CONSENT_PROVIDED_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.SIGNED_CONSENT_DOCUMENT_SAVED;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.STUDY_ENROLLMENT_FAILED;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.USER_ENROLLED_INTO_STUDY;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentReqBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStatusBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.controller.UserConsentManagementController;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementServiceImpl;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.MockUtils;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
public class UserConsentManagementControllerTests extends BaseMockIT {

  @Mock private FileStorageService cloudStorageService;

  @InjectMocks @Autowired private UserConsentManagementServiceImpl userConsentManagementService;

  @InjectMocks @Autowired private UserConsentManagementController controller;

  @Autowired private ObjectMapper objectMapper;

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(cloudStorageService);
    assertNotNull(userConsentManagementService);
  }

  @Test
  public void ping() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    mockMvc.perform(get("/ping").headers(headers)).andDo(print()).andExpect(status().isOk());
  }

  @Test
  public void updateEligibilityConsentStatus() throws Exception {

    // Set mockito expectations for saving file into cloudStorageService
    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    ConsentReqBean consent =
        new ConsentReqBean(
            Constants.VERSION_1_0, Constants.STATUS_COMPLETE, Constants.ENCODED_CONTENT_1_0);
    ConsentStatusBean consentStatus =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    String requestJson = getObjectMapper().writeValueAsString(consentStatus);

    // Invoke /updateEligibilityConsentStatus to save study consent first time
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    TestUtils.addContentTypeAcceptHeaders(headers);
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.UPDATE_CONSENT_SUCCESS_MSG)));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_ENROLLED_INTO_STUDY.getEventCode(), auditRequest);
    auditEventMap.put(INFORMED_CONSENT_PROVIDED_FOR_STUDY.getEventCode(), auditRequest);
    auditEventMap.put(SIGNED_CONSENT_DOCUMENT_SAVED.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap,
        USER_ENROLLED_INTO_STUDY,
        INFORMED_CONSENT_PROVIDED_FOR_STUDY,
        SIGNED_CONSENT_DOCUMENT_SAVED);
    verifyTokenIntrospectRequest();

    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(cloudStorageService, Constants.CONTENT_1_0);

    // Reset Audit Event calls
    clearAuditRequests();
    auditEventMap.clear();

    // Invoke /consentDocument to get consent and verify pdf content
    String path =
        String.format(
            "/myStudiesConsentMgmtWS/consentDocument?studyId=%s&consentVersion=%s",
            Constants.STUDYOF_HEALTH, Constants.VERSION_1_0);
    mockMvc
        .perform(get(path).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.ENCODED_CONTENT_1_0)));

    auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);

    auditEventMap.put(
        READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT);
    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void updateEligibilityConsentStatusUpdateExisting() throws Exception {

    // Set mockito expectations for saving file into cloudStorageService
    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    ConsentReqBean consent =
        new ConsentReqBean(
            Constants.VERSION_1_0,
            Constants.STATUS_COMPLETE,
            Constants.ENCODED_CONTENT_1_0_UPDATED);
    ConsentStatusBean consentStatus =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    String requestJson = getObjectMapper().writeValueAsString(consentStatus);

    // Invoke http api endpoint to Update study consent pdf content value
    HttpHeaders headers = TestUtils.getCommonHeaders();
    TestUtils.addContentTypeAcceptHeaders(headers);
    headers.add("Authorization", VALID_BEARER_TOKEN);
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.UPDATE_CONSENT_SUCCESS_MSG)));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_ENROLLED_INTO_STUDY.getEventCode(), auditRequest);
    auditEventMap.put(INFORMED_CONSENT_PROVIDED_FOR_STUDY.getEventCode(), auditRequest);
    auditEventMap.put(SIGNED_CONSENT_DOCUMENT_SAVED.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap,
        USER_ENROLLED_INTO_STUDY,
        INFORMED_CONSENT_PROVIDED_FOR_STUDY,
        SIGNED_CONSENT_DOCUMENT_SAVED);
    verifyTokenIntrospectRequest();

    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(
        cloudStorageService, Constants.CONTENT_1_0_UPDATED);

    // Reset Audit Event calls
    clearAuditRequests();
    auditEventMap.clear();

    // Invoke /consentDocument to get consent and verify pdf content
    String path =
        String.format(
            "/myStudiesConsentMgmtWS/consentDocument?studyId=%s&consentVersion=%s",
            Constants.STUDYOF_HEALTH, Constants.VERSION_1_0);
    mockMvc
        .perform(get(path).headers(headers).contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.ENCODED_CONTENT_1_0_UPDATED)));

    auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);

    auditEventMap.put(
        READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT);
    verifyTokenIntrospectRequest(2);
  }

  @Test
  public void updateEligibilityConsentStatusAddNewVersion() throws Exception {

    // Set mockito expectations for saving file into cloudStorageService
    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    ConsentReqBean consent =
        new ConsentReqBean(
            Constants.VERSION_1_2, Constants.STATUS_COMPLETE, Constants.ENCODED_CONTENT_1_2);
    ConsentStatusBean consentStatus =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    String requestJson = getObjectMapper().writeValueAsString(consentStatus);

    // Invoke http api endpoint to Add new study consent pdf version
    HttpHeaders headers = TestUtils.getCommonHeaders();
    TestUtils.addContentTypeAcceptHeaders(headers);
    headers.add("Authorization", VALID_BEARER_TOKEN);
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.UPDATE_CONSENT_SUCCESS_MSG)));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(USER_ENROLLED_INTO_STUDY.getEventCode(), auditRequest);
    auditEventMap.put(INFORMED_CONSENT_PROVIDED_FOR_STUDY.getEventCode(), auditRequest);
    auditEventMap.put(SIGNED_CONSENT_DOCUMENT_SAVED.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap,
        USER_ENROLLED_INTO_STUDY,
        INFORMED_CONSENT_PROVIDED_FOR_STUDY,
        SIGNED_CONSENT_DOCUMENT_SAVED);
    verifyTokenIntrospectRequest();

    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(cloudStorageService, Constants.CONTENT_1_2);

    // Reset Audit Event calls
    clearAuditRequests();
    auditEventMap.clear();

    // Invoke http api endpoint to get consent and verify pdf content
    mockMvc
        .perform(
            get(ApiEndpoint.CONSENT_DOCUMENT.getPath())
                .headers(headers)
                .contextPath(getContextPath())
                .param("studyId", Constants.STUDYOF_HEALTH)
                .param("consentVersion", Constants.VERSION_1_2))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.ENCODED_CONTENT_1_2)));
    auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);

    auditEventMap.put(
        READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT);
    verifyTokenIntrospectRequest(2);

    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(
        cloudStorageService, Constants.CONTENT_1_0_UPDATED);

    // Reset Audit Event calls
    clearAuditRequests();
    auditEventMap.clear();

    // Invoke http api endpoint to get old consent and verify pdf content

    mockMvc
        .perform(
            get(ApiEndpoint.CONSENT_DOCUMENT.getPath())
                .headers(headers)
                .contextPath(getContextPath())
                .param("studyId", Constants.STUDYOF_HEALTH)
                .param("consentVersion", Constants.VERSION_1_0))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.ENCODED_CONTENT_1_0_UPDATED)));

    auditEventMap.put(
        READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT);
    verifyTokenIntrospectRequest(3);

    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(cloudStorageService, Constants.CONTENT_1_2);

    // Reset Audit Event calls
    clearAuditRequests();
    auditEventMap.clear();

    // Invoke http api endpoint to get content without mentioning version
    mockMvc
        .perform(
            get(ApiEndpoint.CONSENT_DOCUMENT.getPath())
                .headers(headers)
                .contextPath(getContextPath())
                .param("studyId", Constants.STUDYOF_HEALTH))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(content().string(containsString(Constants.ENCODED_CONTENT_1_2)));

    auditEventMap.put(
        READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT);
    verifyTokenIntrospectRequest(4);
  }

  @Test
  public void updateEligibilityConsentStatusInvalidInput() throws Exception {

    // Invoke /updateEligibilityConsentStatus to Add new study consent pdf version

    // without consent request
    HttpHeaders headers = TestUtils.getCommonHeaders();
    headers.add("Authorization", VALID_BEARER_TOKEN);
    TestUtils.addContentTypeAcceptHeaders(headers);
    String requestJson = "";
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest();

    // without consent
    ConsentStatusBean consentRequest = new ConsentStatusBean();
    requestJson = getObjectMapper().writeValueAsString(consentRequest);
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(2);

    // without consent version
    ConsentReqBean consent =
        new ConsentReqBean(null, Constants.STATUS_COMPLETE, Constants.ENCODED_CONTENT_1_0);
    consentRequest =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    requestJson = getObjectMapper().writeValueAsString(consentRequest);

    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(3);

    // without consent pdf content
    consent = new ConsentReqBean(Constants.VERSION_1_0, Constants.STATUS_COMPLETE, null);
    consentRequest =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    requestJson = getObjectMapper().writeValueAsString(consentRequest);

    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(4);

    // without consent status
    consent = new ConsentReqBean(Constants.VERSION_1_0, null, Constants.ENCODED_CONTENT_1_0);
    consentRequest =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    requestJson = getObjectMapper().writeValueAsString(consentRequest);

    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(5);

    // without studyId
    consent =
        new ConsentReqBean(
            Constants.VERSION_1_0, Constants.STATUS_COMPLETE, Constants.ENCODED_CONTENT_1_0);
    consentRequest = new ConsentStatusBean(null, true, consent, Constants.SHARING_VALUE);
    requestJson = getObjectMapper().writeValueAsString(consentRequest);

    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(6);

    // without userId header
    consent =
        new ConsentReqBean(
            Constants.VERSION_1_0, Constants.STATUS_COMPLETE, Constants.ENCODED_CONTENT_1_0);
    consentRequest =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    requestJson = getObjectMapper().writeValueAsString(consentRequest);

    headers.remove(Constants.USER_ID_HEADER);
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(7);

    // without a matching entry for userId and studyId in participantStudies
    consent =
        new ConsentReqBean(
            Constants.VERSION_1_0, Constants.STATUS_COMPLETE, Constants.ENCODED_CONTENT_1_0);
    consentRequest =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    requestJson = getObjectMapper().writeValueAsString(consentRequest);

    headers.remove(Constants.USER_ID_HEADER);
    headers.add(Constants.USER_ID_HEADER, Constants.INVALID_USER_ID);

    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(8);

    // with empty version
    consent = new ConsentReqBean("", Constants.STATUS_COMPLETE, Constants.CONTENT_1_0);
    consentRequest = new ConsentStatusBean(Constants.STUDYOF_HEALTH, false, consent, null);
    requestJson = getObjectMapper().writeValueAsString(consentRequest);

    headers.remove(Constants.USER_ID_HEADER);
    TestUtils.addUserIdHeader(headers);
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest());

    verifyTokenIntrospectRequest(9);
  }

  @Test
  public void testUpdateEligibilityConsentStatusEmptyPdf() throws Exception {
    HttpHeaders headers = TestUtils.getCommonHeaders();
    TestUtils.addContentTypeAcceptHeaders(headers);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    // Invoke http api endpoint to Add new study consent pdf version
    ConsentReqBean consent =
        new ConsentReqBean(Constants.VERSION_1_2, Constants.STATUS_COMPLETE, "");
    ConsentStatusBean consentRequest =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    String requestJson = getObjectMapper().writeValueAsString(consentRequest);

    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyTokenIntrospectRequest();

    // Verify that cloud storage wasn't called
    verify(cloudStorageService, times(0)).saveFile(anyString(), anyString(), anyString());

    // Invoke http api endpoint to Add new study consent pdf version
    consent = new ConsentReqBean(Constants.VERSION_1_3, Constants.STATUS_COMPLETE, "");
    consentRequest =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    requestJson = getObjectMapper().writeValueAsString(consentRequest);

    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    verifyTokenIntrospectRequest(2);

    // Verify that cloud storage wasn't called
    verify(cloudStorageService, times(0)).saveFile(anyString(), anyString(), anyString());
  }

  @Test
  public void testUpdateEligibilityConsentStatusSaveFailure() throws Exception {

    // Set mockito expectations for saving file into cloudStorageService
    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    // Invoke http api endpoint to Add new study consent pdf version
    HttpHeaders headers = TestUtils.getCommonHeaders();
    TestUtils.addContentTypeAcceptHeaders(headers);
    headers.add("Authorization", VALID_BEARER_TOKEN);

    ConsentReqBean consent =
        new ConsentReqBean(
            Constants.VERSION_VERY_LONG, Constants.STATUS_COMPLETE, Constants.ENCODED_CONTENT_1_2);
    ConsentStatusBean consentStatus =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, Constants.SHARING_VALUE);
    String requestJson = getObjectMapper().writeValueAsString(consentStatus);
    mockMvc
        .perform(
            post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                .content(requestJson)
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(STUDY_ENROLLMENT_FAILED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, STUDY_ENROLLMENT_FAILED);
  }

  @Test
  public void shouldReturnReadOperationFailedForConsent() throws Exception {

    HttpHeaders headers = TestUtils.getCommonHeaders();
    TestUtils.addContentTypeAcceptHeaders(headers);

    mockMvc
        .perform(
            get(ApiEndpoint.CONSENT_DOCUMENT.getPath())
                .headers(headers)
                .contextPath(getContextPath())
                .param("studyId", Constants.INVALID_STUDY_ID)
                .param("consentVersion", Constants.VERSION_1_0))
        .andDo(print())
        .andExpect(status().isBadRequest());

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.INVALID_STUDY_ID);

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(
        READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_FAILED_FOR_SIGNED_CONSENT_DOCUMENT);
    verifyTokenIntrospectRequest();
  }
}
