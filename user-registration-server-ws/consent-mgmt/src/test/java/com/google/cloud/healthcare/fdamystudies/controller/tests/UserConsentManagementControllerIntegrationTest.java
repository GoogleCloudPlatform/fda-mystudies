package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import java.io.OutputStream;
import java.util.Base64;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.cloud.healthcare.fdamystudies.common.BaseMockit;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.controller.UserConsentManagementController;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementServiceImpl;

@ActiveProfiles("mockit")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
public class UserConsentManagementControllerIntegrationTest extends BaseMockit {

  @Mock private FileStorageService cloudStorageService;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @InjectMocks @Autowired private UserConsentManagementServiceImpl userConsentManagementService;

  @InjectMocks @Autowired private UserConsentManagementController controller;

  public static final String VERSION_1_0 = "1.0";
  public static final String VERSION_1_2 = "1.2";
  public static final String VERSION_1_3 = "1.3";

  public static final String CONTENT_1_0 = "text pdf content";
  public static final String CONTENT_1_0_UPDATED = CONTENT_1_0 + " updated";
  public static final String CONTENT_1_2 = "text pdf content 1.2";

  public static final String ACCESS_TOKEN_VALUE = UUID.randomUUID().toString();
  public static final String CLIENT_TOKEN_VALUE = UUID.randomUUID().toString();

  public static final String ACCESS_TOKEN_HEADER = "accessToken";
  public static final String CLIENT_TOKEN_HEADER = "clientToken";
  public static final String USER_ID_HEADER = "userId";

  public static final String VALID_USER_ID = "kR2g5m2pJPP0P31-WNFYK8Al7jBP0mJ-cTSFJJHJ4DewuCg";

  public static final String FIELD_VERSION = "version";
  public static final String FIELD_STATUS = "status";
  public static final String FIELD_PDF = "pdf";
  public static final String FIELD_CONSENT = "consent";
  public static final String FIELD_ELIGIBILITY = "eligibility";
  public static final String FIELD_SHARING = "sharing";
  public static final String FIELD_STUDY_ID = "studyId";
  public static final String FIELD_MESSAGE = "message";
  public static final String FIELD_TYPE = "type";
  public static final String FIELD_CONTENT = "content";

  public static final String WIREMOCK_URL = "http://localhost:8001/AuthServer/tokenAuthentication";

  @BeforeEach
  public void setWireMockUrl() {
    appConfig.setAuthServerAccessTokenValidationUrl(WIREMOCK_URL);
  }

  @Test
  @Order(1)
  public void contextLoads() {
    assertNotNull(controller);
    assertNotNull(getRestTemplate());
    assertNotNull(cloudStorageService);
    assertNotNull(userConsentManagementService);
  }

  @Test
  @Order(1)
  public void ping() {

    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_TOKEN_HEADER, CLIENT_TOKEN_VALUE);
    headers.add(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_VALUE);

    String response = getRestTemplate().getForObject("/ping", String.class);
    assertEquals("Mystudies UserRegistration Webservice Started !!!", response);
  }

  @Test
  @Order(2)
  public void updateEligibilityConsentStatus() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_TOKEN_HEADER, CLIENT_TOKEN_VALUE);
    headers.add(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_VALUE);
    headers.add(USER_ID_HEADER, VALID_USER_ID);

    // Invoke http api endpoint to save study consent first time
    // Set mockito expectations for saving file into cloudStorageService
    Mockito.when(
            cloudStorageService.saveFile(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenAnswer(
            new Answer<String>() {

              @Override
              public String answer(InvocationOnMock invocation) throws Throwable {
                String fileName = invocation.getArgument(0);
                String underDirectory = invocation.getArgument(2);
                return underDirectory + "/" + fileName;
              }
            });
    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(FIELD_STUDY_ID, "StudyofHealth");
    consentRequest.put(FIELD_ELIGIBILITY, true);

    String pdfValue = Base64.getEncoder().encodeToString(CONTENT_1_0.getBytes());
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(FIELD_VERSION, VERSION_1_0);
    consent.put(FIELD_STATUS, "");
    consent.put(FIELD_PDF, pdfValue);
    consentRequest.put(FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    JsonNode responseBody = responseEntity.getBody();
    assertEquals(200, responseBody.get("code").intValue());
    assertEquals(
        "Eligibility consent has been updated successfully",
        responseBody.get(FIELD_MESSAGE).asText());

    // Invoke http api endpoint to get consent and verify pdf content
    // Set mockito expectations for downloading content from cloudStorage
    Mockito.doAnswer(
            new Answer<Void>() {

              @Override
              public Void answer(InvocationOnMock invocation) throws Throwable {
                OutputStream os = invocation.getArgument(1);
                // This is expected to rerun the actual decoded value
                os.write(CONTENT_1_0.getBytes());
                return null;
              }
            })
        .when(cloudStorageService)
        .downloadFileTo(Mockito.anyString(), Mockito.any(OutputStream.class));
    requestEntity = new HttpEntity<>(headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth&consentVersion=1.0",
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    responseBody = responseEntity.getBody();
    assertEquals("success", responseBody.get(FIELD_MESSAGE).asText());
    consent = (ObjectNode) responseBody.get(FIELD_CONSENT);
    assertEquals(VERSION_1_0, consent.get(FIELD_VERSION).asText());
    assertEquals("application/pdf", consent.get(FIELD_TYPE).asText());
    assertEquals(pdfValue, consent.get(FIELD_CONTENT).asText());
  }

  @Test
  @Order(3)
  public void updateEligibilityConsentStatusUpdateExisting() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_TOKEN_HEADER, CLIENT_TOKEN_VALUE);
    headers.add(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_VALUE);
    headers.add(USER_ID_HEADER, VALID_USER_ID);

    // Invoke http api endpoint to Update study consent pdf content value
    // Set mockito expectations for saving file into cloudStorageService
    Mockito.when(
            cloudStorageService.saveFile(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenAnswer(
            new Answer<String>() {

              @Override
              public String answer(InvocationOnMock invocation) throws Throwable {
                String fileName = invocation.getArgument(0);
                String underDirectory = invocation.getArgument(2);
                return underDirectory + "/" + fileName;
              }
            });
    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(FIELD_STUDY_ID, "StudyofHealth");
    consentRequest.put(FIELD_ELIGIBILITY, true);

    String pdfValue = Base64.getEncoder().encodeToString(CONTENT_1_0_UPDATED.getBytes());
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(FIELD_VERSION, VERSION_1_0);
    consent.put(FIELD_STATUS, "complete");
    consent.put(FIELD_PDF, pdfValue);
    consentRequest.put(FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    JsonNode responseBody = responseEntity.getBody();
    assertEquals(200, responseBody.get("code").intValue());
    assertEquals(
        "Eligibility consent has been updated successfully",
        responseBody.get(FIELD_MESSAGE).asText());

    // Invoke http api endpoint to get consent and verify pdf content
    // Set mockito expectations for downloading content from cloudStorage
    Mockito.doAnswer(
            new Answer<Void>() {

              @Override
              public Void answer(InvocationOnMock invocation) throws Throwable {
                OutputStream os = invocation.getArgument(1);
                // This is expected to rerun the actual decoded value
                os.write(CONTENT_1_0_UPDATED.getBytes());
                return null;
              }
            })
        .when(cloudStorageService)
        .downloadFileTo(Mockito.anyString(), Mockito.any(OutputStream.class));
    requestEntity = new HttpEntity<>(headers);
    String queryParamVersion = VERSION_1_0;
    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth&consentVersion=" + queryParamVersion,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    responseBody = responseEntity.getBody();
    assertEquals("success", responseBody.get(FIELD_MESSAGE).asText());
    consent = (ObjectNode) responseBody.get(FIELD_CONSENT);
    assertEquals(VERSION_1_0, consent.get(FIELD_VERSION).asText());
    assertEquals("application/pdf", consent.get(FIELD_TYPE).asText());
    assertEquals(pdfValue, consent.get(FIELD_CONTENT).asText());
  }

  @Test
  @Order(4)
  public void updateEligibilityConsentStatusAddNewVersion() throws InterruptedException {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_TOKEN_HEADER, CLIENT_TOKEN_VALUE);
    headers.add(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_VALUE);
    headers.add(USER_ID_HEADER, VALID_USER_ID);

    // Add sleep so that different consents are saved with some time difference
    TimeUnit.SECONDS.sleep(1);

    // Invoke http api endpoint to Add new study consent pdf version
    // Set mockito expectations for saving file into cloudStorageService
    Mockito.when(
            cloudStorageService.saveFile(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenAnswer(
            new Answer<String>() {

              @Override
              public String answer(InvocationOnMock invocation) throws Throwable {
                String fileName = invocation.getArgument(0);
                String underDirectory = invocation.getArgument(2);
                return underDirectory + "/" + fileName;
              }
            });
    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(FIELD_STUDY_ID, "StudyofHealth");
    consentRequest.put(FIELD_ELIGIBILITY, true);

    String pdfValue = Base64.getEncoder().encodeToString(CONTENT_1_2.getBytes());
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(FIELD_VERSION, VERSION_1_2);
    consent.put(FIELD_STATUS, "complete");
    consent.put(FIELD_PDF, pdfValue);
    consentRequest.put(FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    JsonNode responseBody = responseEntity.getBody();
    assertEquals(200, responseBody.get("code").intValue());
    assertEquals(
        "Eligibility consent has been updated successfully",
        responseBody.get(FIELD_MESSAGE).asText());

    // Invoke http api endpoint to get consent and verify pdf content
    // Set mockito expectations for downloading content from cloudStorage
    Mockito.doAnswer(
            new Answer<Void>() {

              @Override
              public Void answer(InvocationOnMock invocation) throws Throwable {
                OutputStream os = invocation.getArgument(1);
                // This is expected to rerun the actual decoded value
                os.write(CONTENT_1_2.getBytes());
                return null;
              }
            })
        .when(cloudStorageService)
        .downloadFileTo(Mockito.anyString(), Mockito.any(OutputStream.class));
    requestEntity = new HttpEntity<>(headers);
    String queryParamVersion = VERSION_1_2;

    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth&consentVersion=" + queryParamVersion,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    responseBody = responseEntity.getBody();
    assertEquals("success", responseBody.get(FIELD_MESSAGE).asText());
    consent = (ObjectNode) responseBody.get(FIELD_CONSENT);
    assertEquals(VERSION_1_2, consent.get(FIELD_VERSION).asText());
    assertEquals("application/pdf", consent.get(FIELD_TYPE).asText());
    assertEquals(pdfValue, consent.get(FIELD_CONTENT).asText());

    // Invoke http api endpoint to get old consent and verify pdf content
    // Set mockito expectations for downloading content from cloudStorage
    Mockito.doAnswer(
            new Answer<Void>() {

              @Override
              public Void answer(InvocationOnMock invocation) throws Throwable {
                OutputStream os = invocation.getArgument(1);
                // This is expected to rerun the actual decoded value
                os.write(CONTENT_1_0_UPDATED.getBytes());
                return null;
              }
            })
        .when(cloudStorageService)
        .downloadFileTo(Mockito.anyString(), Mockito.any(OutputStream.class));
    requestEntity = new HttpEntity<>(headers);
    queryParamVersion = VERSION_1_0;
    pdfValue = Base64.getEncoder().encodeToString(CONTENT_1_0_UPDATED.getBytes());
    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth&consentVersion=" + queryParamVersion,
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    responseBody = responseEntity.getBody();
    assertEquals("success", responseBody.get(FIELD_MESSAGE).asText());
    consent = (ObjectNode) responseBody.get(FIELD_CONSENT);
    assertEquals(VERSION_1_0, consent.get(FIELD_VERSION).asText());
    assertEquals("application/pdf", consent.get(FIELD_TYPE).asText());
    assertEquals(pdfValue, consent.get(FIELD_CONTENT).asText());

    // Invoke http api endpoint to get content without mentioning version
    // Set mockito expectations for downloading content from cloudStorage
    Mockito.doAnswer(
            new Answer<Void>() {

              @Override
              public Void answer(InvocationOnMock invocation) throws Throwable {
                OutputStream os = invocation.getArgument(1);
                // This is expected to rerun the actual decoded value
                os.write(CONTENT_1_2.getBytes());
                return null;
              }
            })
        .when(cloudStorageService)
        .downloadFileTo(Mockito.anyString(), Mockito.any(OutputStream.class));
    responseEntity =
        getRestTemplate()
            .exchange(
                "/consentDocument?studyId=StudyofHealth",
                HttpMethod.GET,
                requestEntity,
                JsonNode.class);
    pdfValue = Base64.getEncoder().encodeToString(CONTENT_1_2.getBytes());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    responseBody = responseEntity.getBody();
    assertEquals("success", responseBody.get(FIELD_MESSAGE).asText());
    consent = (ObjectNode) responseBody.get(FIELD_CONSENT);
    assertEquals(VERSION_1_2, consent.get(FIELD_VERSION).asText());
    assertEquals("application/pdf", consent.get(FIELD_TYPE).asText());
    assertEquals(pdfValue, consent.get(FIELD_CONTENT).asText());
  }

  @Test
  @Order(5)
  public void updateEligibilityConsentStatusInvalidInput() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_TOKEN_HEADER, CLIENT_TOKEN_VALUE);
    headers.add(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_VALUE);
    headers.add(USER_ID_HEADER, VALID_USER_ID);
    headers.add("Content-Type", "application/json");
    headers.add("Accept", "application/json");

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
    consentRequest.put(FIELD_CONSENT, consent);

    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent pdf content
    consent.put(FIELD_VERSION, VERSION_1_0);
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without consent status
    consent.put(FIELD_VERSION, VERSION_1_0);
    consent.put(FIELD_PDF, Base64.getEncoder().encodeToString(CONTENT_1_0.getBytes()));
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without studyId
    consent.put(FIELD_VERSION, VERSION_1_0);
    consent.put(FIELD_PDF, Base64.getEncoder().encodeToString(CONTENT_1_0.getBytes()));
    consent.put(FIELD_STATUS, "complete");
    consentRequest.put(FIELD_STUDY_ID, "");

    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // without userId header
    consent.put(FIELD_VERSION, VERSION_1_0);
    consent.put(FIELD_PDF, Base64.getEncoder().encodeToString(CONTENT_1_0.getBytes()));
    consent.put(FIELD_STATUS, "complete");
    consentRequest.put(FIELD_STUDY_ID, "StudyofHealth");

    headers.put(USER_ID_HEADER, Collections.singletonList(""));
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

    // without a matching entry for userId and studyId in participantStudies
    consent.put(FIELD_VERSION, VERSION_1_0);
    consent.put(FIELD_PDF, Base64.getEncoder().encodeToString(CONTENT_1_0.getBytes()));
    consent.put(FIELD_STATUS, "complete");
    consentRequest.put(FIELD_STUDY_ID, "StudyofHealth");

    headers.put(USER_ID_HEADER, Collections.singletonList("BhGsYUyd"));
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    // with empty version
    consent.put(FIELD_VERSION, "");
    consent.put(FIELD_PDF, Base64.getEncoder().encodeToString(CONTENT_1_0.getBytes()));
    consent.put(FIELD_STATUS, "complete");
    consentRequest.put(FIELD_STUDY_ID, "StudyofHealth");

    headers.put(USER_ID_HEADER, Collections.singletonList(VALID_USER_ID));
    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
    assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
  }

  @Test
  @Order(6)
  public void testUpdateEligibilityConsentStatusEmptyPdf() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_TOKEN_HEADER, CLIENT_TOKEN_VALUE);
    headers.add(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_VALUE);
    headers.add(USER_ID_HEADER, VALID_USER_ID);

    // Invoke http api endpoint to Add new study consent pdf version
    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(FIELD_STUDY_ID, "StudyofHealth");

    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(FIELD_VERSION, VERSION_1_2);
    consent.put(FIELD_STATUS, "complete");
    consent.put(FIELD_PDF, ""); // empty pdf
    consentRequest.put(FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    JsonNode responseBody = responseEntity.getBody();
    assertEquals(200, responseBody.get("code").intValue());
    assertEquals(
        "Eligibility consent has been updated successfully",
        responseBody.get(FIELD_MESSAGE).asText());

    // Verify that cloud storage wasn't called
    Mockito.verify(cloudStorageService, times(0))
        .saveFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

    // Invoke http api endpoint to Add new study consent pdf version
    consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(FIELD_STUDY_ID, "StudyofHealth");

    consent = new ObjectMapper().createObjectNode();
    consent.put(FIELD_VERSION, VERSION_1_3);
    consent.put(FIELD_STATUS, "complete");
    consent.put(FIELD_PDF, ""); // empty pdf
    consentRequest.put(FIELD_CONSENT, consent);

    requestEntity = new HttpEntity<>(consentRequest, headers);
    responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);

    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    responseBody = responseEntity.getBody();
    assertEquals(200, responseBody.get("code").intValue());
    assertEquals(
        "Eligibility consent has been updated successfully",
        responseBody.get(FIELD_MESSAGE).asText());
    // Verify that cloud storage wasn't called
    Mockito.verify(cloudStorageService, times(0))
        .saveFile(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void testUpdateEligibilityConsentStatusSaveFailure() {
    HttpHeaders headers = new HttpHeaders();
    headers.add(CLIENT_TOKEN_HEADER, CLIENT_TOKEN_VALUE);
    headers.add(ACCESS_TOKEN_HEADER, ACCESS_TOKEN_VALUE);
    headers.add(USER_ID_HEADER, VALID_USER_ID);

    // Invoke http api endpoint to Add new study consent pdf version
    // Set mockito expectations for saving file into cloudStorageService
    Mockito.when(
            cloudStorageService.saveFile(
                Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
        .thenAnswer(
            new Answer<String>() {

              @Override
              public String answer(InvocationOnMock invocation) throws Throwable {
                String fileName = invocation.getArgument(0);
                String underDirectory = invocation.getArgument(2);
                return underDirectory + "/" + fileName;
              }
            });
    ObjectNode consentRequest = new ObjectMapper().createObjectNode();
    consentRequest.put(FIELD_STUDY_ID, "StudyofHealth");

    String pdfValue = Base64.getEncoder().encodeToString(CONTENT_1_2.getBytes());
    ObjectNode consent = new ObjectMapper().createObjectNode();
    consent.put(FIELD_VERSION, VERSION_1_2);
    consent.put(FIELD_STATUS, "complete");
    consent.put(FIELD_PDF, pdfValue);
    consentRequest.put(FIELD_CONSENT, consent);

    HttpEntity<JsonNode> requestEntity = new HttpEntity<>(consentRequest, headers);
    ResponseEntity<JsonNode> responseEntity =
        getRestTemplate()
            .exchange(
                "/updateEligibilityConsentStatus", HttpMethod.POST, requestEntity, JsonNode.class);
  }
}
