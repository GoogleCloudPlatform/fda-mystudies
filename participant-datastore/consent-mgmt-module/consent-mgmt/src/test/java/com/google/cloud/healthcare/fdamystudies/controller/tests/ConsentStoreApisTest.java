/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.DATA_SHARING;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PDF_PATH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.VERSION;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.INFORMED_CONSENT_PROVIDED_FOR_STUDY;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT;
import static com.google.cloud.healthcare.fdamystudies.common.ConsentManagementEnum.SIGNED_CONSENT_DOCUMENT_SAVED;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.healthcare.v1.model.Consent;
import com.google.api.services.healthcare.v1.model.ConsentArtifact;
import com.google.api.services.healthcare.v1.model.Image;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentReqBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStatusBean;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.controller.UserConsentManagementController;
import com.google.cloud.healthcare.fdamystudies.mapper.ConsentManagementAPIs;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementServiceImpl;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;
import com.google.cloud.storage.Storage;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
/**
 * To test consent store Apis
 *
 * @author
 */
@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"enableConsentManagementAPI=true"})
public class ConsentStoreApisTest extends BaseMockIT {

  private static final String SITE_ID = "1";

  @InjectMocks @Autowired private UserConsentManagementServiceImpl userConsentManagementService;

  @InjectMocks @Autowired private UserConsentManagementController controller;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired private Storage mockStorage;

  @Mock private FileStorageService cloudStorageService;

  @MockBean ConsentManagementAPIs consentManagementAPIs;

  protected ObjectMapper getObjectMapper() {
    return objectMapper;
  }

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(userConsentManagementService);
  }

  @Test
  public void updateEligibilityConsentStatusAddNewVersion() throws Exception {

    when(cloudStorageService.saveFile(anyString(), anyString(), anyString()))
        .thenAnswer(
            (invocation) -> {
              String fileName = invocation.getArgument(0);
              String underDirectory = invocation.getArgument(2);
              return underDirectory + "/" + fileName;
            });
    when(consentManagementAPIs.createConsentArtifact(
            any(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn("ConsentArtifact created");

    ConsentReqBean consent =
        new ConsentReqBean(
            Constants.VERSION_1_2, Constants.STATUS_COMPLETE, Constants.ENCODED_CONTENT_1_2);
    ConsentStatusBean consentStatus =
        new ConsentStatusBean(
            Constants.STUDYOF_HEALTH, SITE_ID, true, consent, Constants.SHARING_VALUE, null);
    String requestJson = getObjectMapper().writeValueAsString(consentStatus);

    // Invoke http api endpoint to Add new study consent pdf version
    HttpHeaders headers = TestUtils.getCommonHeaders();
    TestUtils.addContentTypeAcceptHeaders(headers);
    headers.add("Authorization", VALID_BEARER_TOKEN);
    MvcResult result =
        mockMvc
            .perform(
                post(ApiEndpoint.UPDATE_ELIGIBILITY_CONSENT.getPath())
                    .content(requestJson)
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(Constants.UPDATE_CONSENT_SUCCESS_MSG)))
            .andExpect(jsonPath("$.consentDocumentFileName").isNotEmpty())
            .andReturn();

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH);
    auditRequest.setStudyVersion("3.1");
    auditRequest.setParticipantId("1");

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(INFORMED_CONSENT_PROVIDED_FOR_STUDY.getEventCode(), auditRequest);
    auditEventMap.put(SIGNED_CONSENT_DOCUMENT_SAVED.getEventCode(), auditRequest);

    verifyAuditEventCall(
        auditEventMap, INFORMED_CONSENT_PROVIDED_FOR_STUDY, SIGNED_CONSENT_DOCUMENT_SAVED);
    verifyTokenIntrospectRequest();

    // Reset Audit Event calls
    clearAuditRequests();
    auditEventMap.clear();

    String consentDocumentFileName =
        JsonPath.read(result.getResponse().getContentAsString(), "$.consentDocumentFileName");

    String content = "sample consent document content";
    String encodedContent = new String(Base64.getEncoder().encode(content.getBytes()));

    when(consentManagementAPIs.getListOfConsents(anyString(), anyString()))
        .thenReturn(getConsent());

    when(consentManagementAPIs.getConsentArtifact(anyString()))
        .thenReturn(getConsentArtifact(consentDocumentFileName, encodedContent));

    // Invoke http api endpoint to get consent and verify pdf content
    result =
        mockMvc
            .perform(
                get(ApiEndpoint.CONSENT_DOCUMENT.getPath())
                    .headers(headers)
                    .contextPath(getContextPath())
                    .param("studyId", Constants.STUDYOF_HEALTH)
                    .param("consentVersion", Constants.VERSION_1_2))
            .andDo(print())
            .andExpect(status().isOk())
            .andReturn();

    String sampleContent =
        JsonPath.read(result.getResponse().getContentAsString(), "$.consent.content");
    assertThat(sampleContent, is(encodedContent));

    auditRequest = new AuditLogEventRequest();
    auditRequest.setUserId(Constants.VALID_USER_ID);
    auditRequest.setStudyId(Constants.STUDYOF_HEALTH);
    auditRequest.setStudyVersion("3.1");
    auditRequest.setParticipantId("1");

    auditEventMap.put(
        READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, READ_OPERATION_SUCCEEDED_FOR_SIGNED_CONSENT_DOCUMENT);
    verifyTokenIntrospectRequest(2);
  }
  /**
   * To ConsentArtifact
   *
   * @param filePath
   * @param encodedContent
   * @return
   */
  public ConsentArtifact getConsentArtifact(String filePath, String encodedContent) {

    Map<String, String> metadata = new HashedMap<String, String>();
    metadata.put(VERSION, Constants.VERSION_1_2);
    metadata.put(DATA_SHARING, Constants.SHARING_VALUE);
    metadata.put(PDF_PATH, filePath);

    ConsentArtifact consentArtifact = new ConsentArtifact();
    consentArtifact.setMetadata(metadata);
    consentArtifact.setConsentContentVersion(Constants.VERSION_1_2);
    Image image = new Image();
    image.setRawBytes(encodedContent);
    List<Image> images = new ArrayList<>(Arrays.asList(image));
    consentArtifact.setConsentContentScreenshots(images);

    return consentArtifact;
  }
  /**
   * To fetch the consent
   *
   * @return
   */
  public List<Consent> getConsent() {
    Consent consent = new Consent();
    consent.setConsentArtifact("name");
    return new ArrayList<>(Arrays.asList(consent));
  }
}
