/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.CONSENT_DOCUMENT_DOWNLOADED;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.repository.ParticipantStudyRepository;
import com.google.cloud.healthcare.fdamystudies.service.ConsentService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.jayway.jsonpath.JsonPath;
import java.util.Base64;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class ConsentControllerTest extends BaseMockIT {

  @Autowired private ConsentController controller;

  @Autowired private ConsentService consentService;

  @Autowired private TestDataHelper testDataHelper;

  @Autowired private ParticipantStudyRepository participantStudyRepository;

  @Autowired private Storage mockStorage;

  @Autowired private AppPropertyConfig appPropConfig;

  protected MvcResult result;

  private UserRegAdminEntity userRegAdminEntity;

  private StudyEntity studyEntity;

  private AppEntity appEntity;

  private SiteEntity siteEntity;

  private ParticipantRegistrySiteEntity participantRegistrySiteEntity;

  private ParticipantStudyEntity participantStudyEntity;

  private StudyConsentEntity studyConsentEntity;

  @BeforeEach
  public void setUp() {
    userRegAdminEntity = testDataHelper.createUserRegAdminEntity();
    studyEntity = testDataHelper.createStudyEntity(userRegAdminEntity, appEntity);
    siteEntity = testDataHelper.createSiteEntity(studyEntity, userRegAdminEntity, appEntity);
    participantStudyEntity =
        testDataHelper.createParticipantStudyEntity(
            siteEntity, studyEntity, participantRegistrySiteEntity);
    studyConsentEntity = testDataHelper.createStudyConsentEntity(participantStudyEntity);
  }

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(mockMvc);
    assertNotNull(consentService);
  }

  @Test
  public void shouldReturnConsentDocument() throws Exception {
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    BlobId validBlobId = BlobId.of(appPropConfig.getBucketName(), "documents/test-document.pdf");
    Blob mockedBlob = mock(Blob.class);

    String content = "sample consent document content";
    byte[] encodedContent = Base64.getEncoder().encode(content.getBytes());
    when(mockedBlob.getContent()).thenReturn(encodedContent);

    when(this.mockStorage.get(eq(validBlobId))).thenReturn(mockedBlob);

    MvcResult result =
        mockMvc
            .perform(
                get(ApiEndpoint.GET_CONSENT_DOCUMENT.getPath(), studyConsentEntity.getId())
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(
                jsonPath("$.message").value(MessageCode.GET_CONSENT_DOCUMENT_SUCCESS.getMessage()))
            .andReturn();

    String sampleContent = JsonPath.read(result.getResponse().getContentAsString(), "$.content");

    assertThat(Base64.getDecoder().decode(sampleContent.getBytes()), is(encodedContent));

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setParticipantId(participantStudyEntity.getId());
    auditRequest.setAppId(studyEntity.getAppId());
    auditRequest.setStudyId(studyEntity.getId());
    auditRequest.setUserId(userRegAdminEntity.getId());

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(CONSENT_DOCUMENT_DOWNLOADED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, CONSENT_DOCUMENT_DOWNLOADED);
    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnSitePermissionAccessDeniedForConsentDocument() throws Exception {
    // Site 1: set siteEntity without sitePermissionEntity
    siteEntity = testDataHelper.newSiteEntity();
    studyConsentEntity.getParticipantStudy().setSite(siteEntity);
    testDataHelper.getStudyConsentRepository().save(studyConsentEntity);

    // Step 2: Call API and expect SITE_PERMISSION_ACEESS_DENIED error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_CONSENT_DOCUMENT.getPath(), studyConsentEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(
            jsonPath(
                "$.error_description",
                is(ErrorCode.SITE_PERMISSION_ACCESS_DENIED.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnConsentDataNotAvailableWithNullStudyForConsentDocument()
      throws Exception {
    // Site 1: set siteEntity to null
    studyConsentEntity.setParticipantStudy(null);
    testDataHelper.getStudyConsentRepository().save(studyConsentEntity);

    // Step 2: Call API and expect CONSENT_DATA_NOT_AVAILABLE error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_CONSENT_DOCUMENT.getPath(), studyConsentEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error_description", is(ErrorCode.CONSENT_DATA_NOT_AVAILABLE.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @Test
  public void shouldReturnConsentDataNotAvailableWithNullSiteForConsentDocument() throws Exception {
    // Site 1: set siteEntity to null
    studyConsentEntity.getParticipantStudy().setSite(null);
    testDataHelper.getStudyConsentRepository().save(studyConsentEntity);

    // Step 2: Call API and expect CONSENT_DATA_NOT_AVAILABLE error
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    mockMvc
        .perform(
            get(ApiEndpoint.GET_CONSENT_DOCUMENT.getPath(), studyConsentEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(
            jsonPath(
                "$.error_description", is(ErrorCode.CONSENT_DATA_NOT_AVAILABLE.getDescription())));

    verifyTokenIntrospectRequest();
  }

  @AfterEach
  public void cleanUp() {
    testDataHelper.getStudyConsentRepository().deleteAll();
    testDataHelper.getParticipantStudyRepository().deleteAll();
    testDataHelper.getSiteRepository().deleteAll();
    testDataHelper.getStudyRepository().deleteAll();
    testDataHelper.getUserRegAdminRepository().deleteAll();
  }
}
