package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.Base64;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentReqBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStatusBean;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockit;
import com.google.cloud.healthcare.fdamystudies.controller.UserConsentManagementController;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementServiceImpl;
import com.google.cloud.healthcare.fdamystudies.testutils.Constants;
import com.google.cloud.healthcare.fdamystudies.testutils.MockUtils;
import com.google.cloud.healthcare.fdamystudies.testutils.TestUtils;

@ExtendWith(MockitoExtension.class)
public class UserConsentManagementControllerTests extends BaseMockit {

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
  public void updateEligibilityConsentStatus() throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addJsonHeaders(headers);
    TestUtils.addUserIdHeader(headers);

    // Invoke http api endpoint to save study consent first time
    // Set mockito expectations for saving file into cloudStorageService

    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    String pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes());
    ConsentReqBean consent = new ConsentReqBean(Constants.VERSION_1_0, "", pdfValue);
    ConsentStatusBean consentStatus =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, null);
    String requestJson = new ObjectMapper().writeValueAsString(consentStatus);

    HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
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
                "/consentDocument?studyId="
                    + Constants.STUDYOF_HEALTH
                    + "&consentVersion="
                    + Constants.VERSION_1_0,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    TestUtils.assertGetConsentResponse(responseEntity, pdfValue, Constants.VERSION_1_0);
  }

  @Test
  public void updateEligibilityConsentStatusUpdateExisting() throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addJsonHeaders(headers);
    TestUtils.addUserIdHeader(headers);

    // Invoke http api endpoint to Update study consent pdf content value
    // Set mockito expectations for saving file into cloudStorageService
    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    String pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_0_UPDATED.getBytes());
    ConsentReqBean consent =
        new ConsentReqBean(Constants.VERSION_1_0, Constants.STATUS_COMPLETE, pdfValue);
    ConsentStatusBean consentStatus =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, null);
    String requestJson = new ObjectMapper().writeValueAsString(consentStatus);

    HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
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
                "/consentDocument?studyId="
                    + Constants.STUDYOF_HEALTH
                    + "&consentVersion="
                    + Constants.VERSION_1_0,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    TestUtils.assertGetConsentResponse(responseEntity, pdfValue, Constants.VERSION_1_0);
  }

  @Test
  public void updateEligibilityConsentStatusAddNewVersion() throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addUserIdHeader(headers);
    TestUtils.addJsonHeaders(headers);

    // Invoke http api endpoint to Add new study consent pdf version
    // Set mockito expectations for saving file into cloudStorageService
    MockUtils.setCloudStorageSaveFileExpectations(cloudStorageService);

    String pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_2.getBytes());
    ConsentReqBean consent =
        new ConsentReqBean(Constants.VERSION_1_2, Constants.STATUS_COMPLETE, pdfValue);
    ConsentStatusBean consentStatus =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, null);
    String requestJson = new ObjectMapper().writeValueAsString(consentStatus);

    HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
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
                "/consentDocument?studyId="
                    + Constants.STUDYOF_HEALTH
                    + "&consentVersion="
                    + Constants.VERSION_1_2,
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
                "/consentDocument?studyId="
                    + Constants.STUDYOF_HEALTH
                    + "&consentVersion="
                    + Constants.VERSION_1_0,
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
  public void updateEligibilityConsentStatusInvalidInput() throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addUserIdHeader(headers);
    TestUtils.addJsonHeaders(headers);

    // Invoke http api endpoint to Add new study consent pdf version
    // Set mockito expectations for saving file into cloudStorageService

    // without consent request
    HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent
    ConsentStatusBean consentRequest = new ConsentStatusBean();
    String requestJson = new ObjectMapper().writeValueAsString(consentRequest);
    requestEntity = new HttpEntity<>(requestJson, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent version
    ConsentReqBean consent = new ConsentReqBean(null, Constants.STATUS_COMPLETE, "");
    consentRequest = new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, "sharing");
    requestJson = new ObjectMapper().writeValueAsString(consentRequest);

    requestEntity = new HttpEntity<>(requestJson, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent pdf content
    consent = new ConsentReqBean(Constants.VERSION_1_0, Constants.STATUS_COMPLETE, null);
    consentRequest = new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, null);
    requestJson = new ObjectMapper().writeValueAsString(consentRequest);

    requestEntity = new HttpEntity<>(requestJson, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent status
    consent =
        new ConsentReqBean(
            Constants.VERSION_1_0,
            null,
            Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    consentRequest = new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, "sharing");
    requestJson = new ObjectMapper().writeValueAsString(consentRequest);

    requestEntity = new HttpEntity<>(requestJson, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without studyId
    consent =
        new ConsentReqBean(
            Constants.VERSION_1_0,
            Constants.STATUS_COMPLETE,
            Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    consentRequest = new ConsentStatusBean(null, true, consent, "sharing");
    requestJson = new ObjectMapper().writeValueAsString(consentRequest);

    requestEntity = new HttpEntity<>(requestJson, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without userId header
    consent =
        new ConsentReqBean(
            Constants.VERSION_1_0,
            Constants.STATUS_COMPLETE,
            Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    consentRequest = new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, "sharing");
    requestJson = new ObjectMapper().writeValueAsString(consentRequest);

    headers.remove(Constants.USER_ID_HEADER);
    requestEntity = new HttpEntity<>(requestJson, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

    // without a matching entry for userId and studyId in participantStudies
    consent =
        new ConsentReqBean(
            Constants.VERSION_1_0,
            Constants.STATUS_COMPLETE,
            Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    consentRequest = new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, null);
    requestJson = new ObjectMapper().writeValueAsString(consentRequest);

    headers.remove(Constants.USER_ID_HEADER);
    headers.add(Constants.USER_ID_HEADER, "invalid userId");

    requestEntity = new HttpEntity<>(requestJson, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // with empty version
    consent =
        new ConsentReqBean(
            "",
            Constants.STATUS_COMPLETE,
            Base64.getEncoder().encodeToString(Constants.CONTENT_1_0.getBytes()));
    consentRequest = new ConsentStatusBean(Constants.STUDYOF_HEALTH, false, consent, null);
    requestJson = new ObjectMapper().writeValueAsString(consentRequest);

    headers.remove(Constants.USER_ID_HEADER);
    TestUtils.addUserIdHeader(headers);
    requestEntity = new HttpEntity<>(requestJson, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  public void testUpdateEligibilityConsentStatusEmptyPdf() throws JsonProcessingException {
    HttpHeaders headers = new HttpHeaders();
    TestUtils.addTokenHeaders(headers);
    TestUtils.addUserIdHeader(headers);
    TestUtils.addJsonHeaders(headers);

    // Invoke http api endpoint to Add new study consent pdf version
    ConsentReqBean consent =
        new ConsentReqBean(Constants.VERSION_1_2, Constants.STATUS_COMPLETE, "");
    ConsentStatusBean consentRequest =
        new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, "sharing");
    String requestJson = new ObjectMapper().writeValueAsString(consentRequest);

    HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    TestUtils.assertUpdateEligibility(responseEntity);

    // Verify that cloud storage wasn't called
    verify(cloudStorageService, times(0)).saveFile(anyString(), anyString(), anyString());

    // Invoke http api endpoint to Add new study consent pdf version
    consent = new ConsentReqBean(Constants.VERSION_1_3, Constants.STATUS_COMPLETE, "");
    consentRequest = new ConsentStatusBean(Constants.STUDYOF_HEALTH, true, consent, null);
    requestJson = new ObjectMapper().writeValueAsString(consentRequest);

    requestEntity = new HttpEntity<>(requestJson, headers);
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
    consentRequest.put(Constants.FIELD_STUDY_ID, Constants.STUDYOF_HEALTH);

    String pdfValue = Base64.getEncoder().encodeToString(Constants.CONTENT_1_2.getBytes());
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(Constants.FIELD_VERSION, Constants.VERSION_VERY_LONG);
    consent.put(Constants.FIELD_STATUS, Constants.STATUS_COMPLETE);
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
