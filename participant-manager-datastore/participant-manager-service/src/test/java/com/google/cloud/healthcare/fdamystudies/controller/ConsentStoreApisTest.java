/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style license that can be found in the LICENSE file
 * or at https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.DATA_SHARING;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.PDF_PATH;
import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;
import static com.google.cloud.healthcare.fdamystudies.common.ParticipantManagerEvent.CONSENT_DOCUMENT_DOWNLOADED;
import static com.google.cloud.healthcare.fdamystudies.common.TestConstants.CONSENT_VERSION;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.api.services.healthcare.v1.model.ConsentArtifact;
import com.google.api.services.healthcare.v1.model.Image;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.common.ApiEndpoint;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockIT;
import com.google.cloud.healthcare.fdamystudies.common.ErrorCode;
import com.google.cloud.healthcare.fdamystudies.common.MessageCode;
import com.google.cloud.healthcare.fdamystudies.common.OnboardingStatus;
import com.google.cloud.healthcare.fdamystudies.common.TestConstants;
import com.google.cloud.healthcare.fdamystudies.config.AppPropertyConfig;
import com.google.cloud.healthcare.fdamystudies.helper.TestDataHelper;
import com.google.cloud.healthcare.fdamystudies.mapper.ConsentManagementAPIs;
import com.google.cloud.healthcare.fdamystudies.model.AppEntity;
import com.google.cloud.healthcare.fdamystudies.model.LocationEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantRegistrySiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.SiteEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentEntity;
import com.google.cloud.healthcare.fdamystudies.model.StudyEntity;
import com.google.cloud.healthcare.fdamystudies.model.UserRegAdminEntity;
import com.google.cloud.healthcare.fdamystudies.service.ConsentService;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.map.HashedMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MvcResult;
/**
 * Test Apis for consent store
 *
 * @author
 */
@TestPropertySource(properties = {"enableConsentManagementAPI=true"})
public class ConsentStoreApisTest extends BaseMockIT {

  @Autowired private ConsentController controller;

  @Autowired private ConsentService consentService;

  @Autowired private TestDataHelper testDataHelper;

  @Autowired private Storage mockStorage;

  @Autowired private AppPropertyConfig appPropConfig;

  @MockBean ConsentManagementAPIs consentManagementAPIs;

  protected MvcResult result;

  private UserRegAdminEntity userRegAdminEntity;

  private StudyEntity studyEntity;

  private AppEntity appEntity;

  private SiteEntity siteEntity;

  private LocationEntity locationEntity;

  private ParticipantRegistrySiteEntity participantRegistrySiteEntity;

  private ParticipantStudyEntity participantStudyEntity;

  private StudyConsentEntity studyConsentEntity;

  public static final String participat_id = "8a7f80bf7e2a4073017e2a40a47a000a";
  /** test @ BeforeEach */
  @BeforeEach
  public void setUp() {
    // firebase initialization
    try {
      FileInputStream serviceAccount =
          new FileInputStream(TestConstants.GOOGLE_DUMY_CREDENTIAL_JSON_FILE);

      FirebaseOptions options =
          FirebaseOptions.builder()
              .setCredentials(GoogleCredentials.fromStream(serviceAccount))
              .build();

      FirebaseApp.initializeApp(options);
    } catch (IOException e) {
      e.printStackTrace();
    }
    locationEntity = testDataHelper.createSiteLocation();
    userRegAdminEntity = testDataHelper.createUserRegAdminEntity();
    appEntity = testDataHelper.createAppEntityForSiteControllerTest(userRegAdminEntity);
    studyEntity = testDataHelper.createStudyEntity(userRegAdminEntity, appEntity);
    siteEntity = testDataHelper.createSiteEntity(studyEntity, userRegAdminEntity, appEntity);
    participantRegistrySiteEntity =
        testDataHelper.createParticipantRegistrySite(siteEntity, studyEntity);
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
    locationEntity = testDataHelper.createLocation();
    appEntity = testDataHelper.createAppEntity(userRegAdminEntity);
    siteEntity.setLocation(locationEntity);
    participantStudyEntity.setSite(siteEntity);
    studyEntity.setApp(appEntity);
    participantStudyEntity.setParticipantId(participat_id);
    studyConsentEntity.setParticipantStudy(participantStudyEntity);
    testDataHelper.getParticipantStudyRepository().saveAndFlush(participantStudyEntity);
    testDataHelper.getStudyConsentRepository().saveAndFlush(studyConsentEntity);

    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.set(USER_ID_HEADER, userRegAdminEntity.getId());

    when(consentManagementAPIs.getConsentArtifact(anyString()))
        .thenReturn(getConsentArtifact().get(0));

    MvcResult result =
        mockMvc
            .perform(
                get(
                        ApiEndpoint.GET_CONSENT_DOCUMENT.getPath(),
                        "Activity@7de366ad-687b-4819-80b8-0df1feb25e96")
                    .headers(headers)
                    .contextPath(getContextPath()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.type").value(MediaType.APPLICATION_PDF_VALUE))
            .andExpect(
                jsonPath("$.message").value(MessageCode.GET_CONSENT_DOCUMENT_SUCCESS.getMessage()))
            .andReturn();

    AuditLogEventRequest auditRequest = new AuditLogEventRequest();
    auditRequest.setSiteId(siteEntity.getId());
    auditRequest.setParticipantId(participantStudyEntity.getId());
    auditRequest.setUserId(userRegAdminEntity.getId());
    auditRequest.setStudyId(studyEntity.getCustomId());
    auditRequest.setAppId(appEntity.getAppId());
    auditRequest.setStudyVersion(String.valueOf(studyEntity.getVersion()));

    Map<String, AuditLogEventRequest> auditEventMap = new HashedMap<>();
    auditEventMap.put(CONSENT_DOCUMENT_DOWNLOADED.getEventCode(), auditRequest);

    verifyAuditEventCall(auditEventMap, CONSENT_DOCUMENT_DOWNLOADED);
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
            get(
                    ApiEndpoint.GET_CONSENT_DOCUMENT.getPath(),
                    "Activity@7de366ad-687b-4819-80b8-0df1feb25e96")
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
  public void shouldReturnParticipantDetailsForSuperAdmin() throws Exception {
    // Step 1: Set data needed to get Participant details
    participantRegistrySiteEntity.getStudy().setApp(appEntity);
    participantRegistrySiteEntity.setOnboardingStatus(OnboardingStatus.NEW.getCode());
    testDataHelper
        .getParticipantRegistrySiteRepository()
        .saveAndFlush(participantRegistrySiteEntity);
    siteEntity.setLocation(locationEntity);
    testDataHelper.getSiteRepository().saveAndFlush(siteEntity);

    when(consentManagementAPIs.getListOfConsentArtifact(anyString(), anyString()))
        .thenReturn(getConsentArtifact());

    // Step 2: Call API and expect GET_PARTICIPANT_DETAILS_SUCCESS message
    HttpHeaders headers = testDataHelper.newCommonHeaders();
    headers.add(USER_ID_HEADER, userRegAdminEntity.getId());
    mockMvc
        .perform(
            get(
                    ApiEndpoint.GET_PARTICIPANT_DETAILS.getPath(),
                    participantRegistrySiteEntity.getId())
                .headers(headers)
                .contextPath(getContextPath()))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.participantDetails.consentHistory").isArray())
        .andExpect(jsonPath("$.participantDetails.consentHistory", hasSize(1)))
        .andExpect(
            jsonPath("$.participantDetails.consentHistory[0].consentVersion", is(CONSENT_VERSION)))
        .andExpect(
            jsonPath("$.message", is(MessageCode.GET_PARTICIPANT_DETAILS_SUCCESS.getMessage())));

    verifyTokenIntrospectRequest();
  }
  /** for clean up testDataHelper */
  @AfterEach
  public void cleanUp() {
    testDataHelper.getStudyConsentRepository().deleteAll();
    testDataHelper.getParticipantStudyRepository().deleteAll();
    testDataHelper.getParticipantRegistrySiteRepository().deleteAll();
    testDataHelper.getSiteRepository().deleteAll();
    testDataHelper.getStudyRepository().deleteAll();
    testDataHelper.getAppRepository().deleteAll();
    testDataHelper.getUserRegAdminRepository().deleteAll();
    testDataHelper.getLocationRepository().deleteAll();
  }
  /**
   * To fetch ConsentArtifact
   *
   * @return
   */
  public List<ConsentArtifact> getConsentArtifact() {

    Map<String, String> metadata = new HashedMap<String, String>();
    metadata.put(
        PDF_PATH,
        "31jan2022/554cf0f8-9eb1-43bf-b03f-9565f6b6bb7a-18a428eaj1e49x4fa4qa477u4d6e7ee091a2/"
            + "1.1_01312022073150.pdf");
    metadata.put("ParticipantStudyInfoId", studyConsentEntity.getParticipantStudy().getId());
    metadata.put(DATA_SHARING, studyConsentEntity.getSharing());
    //   metadata.put(SITE_ID, studyConsentEntity.getParticipantStudy().getSite().getId());

    ConsentArtifact consentArtifact = new ConsentArtifact();
    consentArtifact.setMetadata(metadata).setConsentContentVersion("1.0");
    String content = "sample consent document content";
    byte[] encodedContent = Base64.getEncoder().encode(content.getBytes());

    Image image = new Image();
    image.setRawBytes(encodedContent.toString());
    List<Image> images = new ArrayList<>(Arrays.asList(image));
    consentArtifact.setConsentContentScreenshots(images);
    consentArtifact.setUserId(participat_id);
    consentArtifact.setName(
        "projects/test-project/locations/us-east4/datasets/test-consent/"
            + "consentStores/test-consent/consentArtifacts/0e787edf-404d-4f6b-835e-97b04b0f284b");

    return new ArrayList<>(Arrays.asList(consentArtifact));
  }
}
