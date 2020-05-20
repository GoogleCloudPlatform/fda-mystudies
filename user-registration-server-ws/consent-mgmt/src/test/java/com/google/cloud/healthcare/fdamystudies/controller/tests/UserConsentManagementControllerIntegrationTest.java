package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Base64;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockit;
import com.google.cloud.healthcare.fdamystudies.controller.UserConsentManagementController;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementServiceImpl;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.MockUtils;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;

@ExtendWith(MockitoExtension.class)
public class UserConsentManagementControllerIntegrationTest extends BaseMockit {

  @Mock private FileStorageService cloudStorageService;

  @InjectMocks @Autowired private UserConsentManagementServiceImpl userConsentManagementService;

  @InjectMocks @Autowired private UserConsentManagementController controller;

  @Test
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(getRestTemplate());
    assertNotNull(cloudStorageService);
    assertNotNull(userConsentManagementService);
  }

  @Test
  public void ping() {

    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);

    ResponseEntity<String> response = getRestTemplate().getForEntity("/ping", String.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  @Test
  public void updateEligibilityConsentStatus() {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addUserIdHeader(headers);

    // Invoke http api endpoint to save study consent first time
    // Set mockito expectations for saving file into cloudStorageService

    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(Constants.FIELD_STUDY_ID, "StudyofHealth");
    consentRequest.put(Constants.FIELD_ELIGIBILITY, true);

    String pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes());
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_0);
    consent.put(Constants.FIELD_STATUS, "");
    consent.put(Constants.FIELD_PDF, pdfValue);
    consentRequest.set(Constants.FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    TestUtils.assertUpdateEligibility(responseEntity);

    // Invoke http api endpoint to get consent and verify pdf content
    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(cloudStorageService, Constants.CONTENT_1_0);

    requestEntity = new HttpEntity<>(headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth&consentVersion=" + Constants.VERSION_1_0,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    TestUtils.assertGetConsentResponse(responseEntity, pdfValue, Constants.VERSION_1_0);
  }

  @Test
  public void updateEligibilityConsentStatusUpdateExisting() {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addUserIdHeader(headers);

    // Invoke http api endpoint to Update study consent pdf content value
    // Set mockito expectations for saving file into cloudStorageService
    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(Constants.FIELD_STUDY_ID, "StudyofHealth");
    consentRequest.put(Constants.FIELD_ELIGIBILITY, true);

    String pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_0_UPDATED.getBytes());
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_0);
    consent.put(Constants.FIELD_STATUS, "complete");
    consent.put(Constants.FIELD_PDF, pdfValue);
    consentRequest.set(Constants.FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    TestUtils.assertUpdateEligibility(responseEntity);

    // Invoke http api endpoint to get consent and verify pdf content
    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(
        cloudStorageService, Constants.CONTENT_1_0_UPDATED);

    requestEntity = new HttpEntity<>(headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth&consentVersion=" + Constants.VERSION_1_0,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    TestUtils.assertGetConsentResponse(responseEntity, pdfValue, Constants.VERSION_1_0);
  }

  @Test
  public void updateEligibilityConsentStatusAddNewVersion() {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addUserIdHeader(headers);

    // Invoke http api endpoint to Add new study consent pdf version
    // Set mockito expectations for saving file into cloudStorageService
    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(Constants.FIELD_STUDY_ID, "StudyofHealth");
    consentRequest.put(Constants.FIELD_ELIGIBILITY, true);

    String pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_2.getBytes());
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_2);
    consent.put(Constants.FIELD_STATUS, "complete");
    consent.put(Constants.FIELD_PDF, pdfValue);
    consentRequest.set(Constants.FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    TestUtils.assertUpdateEligibility(responseEntity);

    // Invoke http api endpoint to get consent and verify pdf content
    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(cloudStorageService, Constants.CONTENT_1_2);

    requestEntity = new HttpEntity<>(headers);

    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth&consentVersion=" + Constants.VERSION_1_2,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    TestUtils.assertGetConsentResponse(responseEntity, pdfValue, Constants.VERSION_1_2);

    // Invoke http api endpoint to get old consent and verify pdf content
    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(
        cloudStorageService, Constants.CONTENT_1_0_UPDATED);

    requestEntity = new HttpEntity<>(headers);
    pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_0_UPDATED.getBytes());
    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth&consentVersion=" + Constants.VERSION_1_0,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    TestUtils.assertGetConsentResponse(responseEntity, pdfValue, Constants.VERSION_1_0);

    // Invoke http api endpoint to get content without mentioning version
    // Set mockito expectations for downloading content from cloudStorage
    MockUtils.setCloudStorageDownloadExpectations(cloudStorageService, Constants.CONTENT_1_2);

    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth",
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);
    pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_2.getBytes());
    TestUtils.assertGetConsentResponse(responseEntity, pdfValue, Constants.VERSION_1_2);
  }

  @Test
  public void updateEligibilityConsentStatusInvalidInput() {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addUserIdHeader(headers);
    TestUtils.addJsonHeaders(headers);

    // Invoke http api endpoint to Add new study consent pdf version
    // Set mockito expectations for saving file into cloudStorageService

    // without consent request
    ObjectNode consentRequest = null;
    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent
    consentRequest = new ObjectMapper().createObjectNode();
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent version
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consentRequest.set(Constants.FIELD_CONSENT, consent);

    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent pdf content
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_0);
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent status
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_0);
    consent.put(
        Constants.FIELD_PDF, Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without studyId
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_0);
    consent.put(
        Constants.FIELD_PDF, Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    consent.put(Constants.FIELD_STATUS, "complete");
    consentRequest.put(Constants.FIELD_STUDY_ID, "");

    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without userId header
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_0);
    consent.put(
        Constants.FIELD_PDF, Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    consent.put(Constants.FIELD_STATUS, "complete");
    consentRequest.put(Constants.FIELD_STUDY_ID, "StudyofHealth");

    headers.put(Constants.USER_ID_HEADER, Collections.singletonList(""));
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

    // without a matching entry for userId and studyId in participantStudies
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_0);
    consent.put(
        Constants.FIELD_PDF, Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    consent.put(Constants.FIELD_STATUS, "complete");
    consentRequest.put(Constants.FIELD_STUDY_ID, "StudyofHealth");

    headers.put(Constants.USER_ID_HEADER, Collections.singletonList("BhGsYUyd"));
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // with empty version
    consent.put(Constants.FIELD_VERSION, "");
    consent.put(
        Constants.FIELD_PDF, Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    consent.put(Constants.FIELD_STATUS, "complete");
    consentRequest.put(Constants.FIELD_STUDY_ID, "StudyofHealth");

    headers.put(Constants.USER_ID_HEADER, Collections.singletonList(Constants.VALID_USER_ID));
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void testUpdateEligibilityConsentStatusEmptyPdf() {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addUserIdHeader(headers);

    // Invoke http api endpoint to Add new study consent pdf version
    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(Constants.FIELD_STUDY_ID, "StudyofHealth");

    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_2);
    consent.put(Constants.FIELD_STATUS, "complete");
    consent.put(Constants.FIELD_PDF, ""); // empty pdf
    consentRequest.set(Constants.FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    TestUtils.assertUpdateEligibility(responseEntity);

    // Verify that cloud storage wasn't called
    verify(cloudStorageService, times(0)).saveFile(anyString(), anyString(), anyString());

    // Invoke http api endpoint to Add new study consent pdf version
    consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(Constants.FIELD_STUDY_ID, "StudyofHealth");

    consent = new ObjectMapper().createObjectNode();
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_1_3);
    consent.put(Constants.FIELD_STATUS, "complete");
    consent.put(Constants.FIELD_PDF, ""); // empty pdf
    consentRequest.set(Constants.FIELD_CONSENT, consent);

    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    TestUtils.assertUpdateEligibility(responseEntity);
    // Verify that cloud storage wasn't called
    verify(cloudStorageService, times(0)).saveFile(anyString(), anyString(), anyString());
  }

  @Test
  public void testUpdateEligibilityConsentStatusSaveFailure() {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addUserIdHeader(headers);

    // Invoke http api endpoint to Add new study consent pdf version
    // Set mockito expectations for saving file into cloudStorageService
    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(Constants.FIELD_STUDY_ID, "StudyofHealth");

    String pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_2.getBytes());
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_BAD);
    consent.put(Constants.FIELD_STATUS, "complete");
    consent.put(Constants.FIELD_PDF, pdfValue);
    consentRequest.set(Constants.FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
  }
}
