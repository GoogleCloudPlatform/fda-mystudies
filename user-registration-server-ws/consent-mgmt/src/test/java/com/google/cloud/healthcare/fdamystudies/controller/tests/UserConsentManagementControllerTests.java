package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentReqBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStatusBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.controller.UserConsentManagementController;
import com.google.cloud.healthcare.fdamystudies.model.ActivityLogBO;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementService;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserConsentManagementControllerTests {

  @Mock private UserConsentManagementService userConsentManagementService;

  @Mock private CommonService commonService;

  @Mock private FileStorageService cloudStorageService;

  @InjectMocks private UserConsentManagementController userConsentManagementController;

  @Test
  public void testGetStudyConsentPDF() {
    HttpServletResponse response = new MockHttpServletResponse();

    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "userId";
    String consentVersion = "1.0";

    StudyInfoBean studyInfo = new StudyInfoBean(studyInfoId);

    // Mockito expectations
    when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);
    ConsentStudyResponseBean resp = new ConsentStudyResponseBean("success");
    when(userConsentManagementService.getStudyConsentDetails(userId, studyInfoId, consentVersion))
        .thenReturn(resp);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.getStudyConsentPDF(
            userId, studyId, consentVersion, response);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(ConsentStudyResponseBean.class, responseEntity.getBody().getClass());
    assertEquals(resp, responseEntity.getBody());
  }

  @Test
  public void testGetStudyConsentPDFNoDataFound() throws IOException {
    HttpServletResponse response = new MockHttpServletResponse();

    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "userId";
    String consentVersion = "1.0";

    StudyInfoBean studyInfo = new StudyInfoBean(studyInfoId);

    // Mockito expectations
    when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);

    when(userConsentManagementService.getStudyConsentDetails(userId, studyInfoId, consentVersion))
        .thenReturn(null);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.getStudyConsentPDF(
            userId, studyId, consentVersion, response);
    assertNull(responseEntity);
    assertEquals(400, response.getStatus());
  }

  @Test
  public void testGetStudyConsentPDFInvalidData() {
    HttpServletResponse response = new MockHttpServletResponse();

    String studyId = "";
    int studyInfoId = 1;
    String userId = "userId";
    String consentVersion = "1.0";

    ResponseEntity<?> responseEntity =
        userConsentManagementController.getStudyConsentPDF(
            userId, studyId, consentVersion, response);

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    verify(userConsentManagementService, never()).getStudyInfoId(studyId);
    verify(userConsentManagementService, never())
        .getStudyConsentDetails(userId, studyInfoId, consentVersion);
  }

  @Test
  public void testUpdateEligibilityConsentStatus() {
    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "userId";
    String consentVersion = "1.0";
    Integer userDetailsId = 1;

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentReqBean reqBean =
        new ConsentReqBean(
            "1.0", "completed", Base64.getEncoder().encodeToString("content".getBytes()));
    ConsentStatusBean bean = new ConsentStatusBean(studyId, Boolean.TRUE, reqBean, "true");

    StudyConsentBO studyConsent = new StudyConsentBO();
    StudyInfoBean studyInfo = new StudyInfoBean(studyInfoId);
    ParticipantStudiesBO participantStudies = new ParticipantStudiesBO();
    String underDirectory = userId + "/" + studyId;

    String now = new SimpleDateFormat("MMddyyyyHHmmss").format(new Date());
    String fileName = String.join("_", userId, studyId, consentVersion, now).concat(".pdf");

    when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);
    when(userConsentManagementService.getParticipantStudies(studyInfoId, userId))
        .thenReturn(participantStudies);
    when(userConsentManagementService.saveParticipantStudies(
            Collections.singletonList(participantStudies)))
        .thenReturn("SUCCESS");
    when(userConsentManagementService.getStudyConsent(userId, studyInfoId, consentVersion))
        .thenReturn(studyConsent);
    when(userConsentManagementService.getUserDetailsId(userId)).thenReturn(userDetailsId);
    when(userConsentManagementService.saveStudyConsent(studyConsent)).thenReturn("SUCCESS");
    when(cloudStorageService.saveFile(fileName, reqBean.getPdf(), underDirectory))
        .thenReturn(fileName + "/" + underDirectory);

    ActivityLogBO activity = new ActivityLogBO();
    when(commonService.createActivityLog(anyString(), anyString(), anyString()))
        .thenReturn(activity);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(ErrorBean.class, responseEntity.getBody().getClass());
  }

  @Test
  public void testUpdateEligibilityConsentStatusVersionRequired() {
    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "userId";
    String consentVersion = "1.0";

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentReqBean reqBean =
        new ConsentReqBean(
            "", "completed", Base64.getEncoder().encodeToString("content".getBytes()));
    ConsentStatusBean bean = new ConsentStatusBean(studyId, Boolean.TRUE, reqBean, "true");

    StudyInfoBean studyInfo = new StudyInfoBean(studyInfoId);
    ParticipantStudiesBO participantStudies = new ParticipantStudiesBO();

    when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);
    when(userConsentManagementService.getParticipantStudies(studyInfoId, userId))
        .thenReturn(participantStudies);
    when(userConsentManagementService.saveParticipantStudies(
            Collections.singletonList(participantStudies)))
        .thenReturn("SUCCESS");

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);

    assertNull(responseEntity);
    assertEquals(400, response.getStatus());
    verify(userConsentManagementService, never())
        .getStudyConsent(userId, studyInfoId, consentVersion);
    verify(userConsentManagementService, never()).getUserDetailsId(userId);
    verify(userConsentManagementService, never()).saveStudyConsent(any(StudyConsentBO.class));
    verify(cloudStorageService, never()).saveFile(anyString(), anyString(), anyString());
  }

  @Test
  public void testUpdateEligibilityConsentStatusNoDataAvailable() {
    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "userId";
    String consentVersion = "1.0";

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentReqBean reqBean =
        new ConsentReqBean(
            "", "completed", Base64.getEncoder().encodeToString("content".getBytes()));
    ConsentStatusBean bean = new ConsentStatusBean(studyId, Boolean.TRUE, reqBean, "true");

    StudyInfoBean studyInfo = new StudyInfoBean(studyInfoId);

    when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);
    when(userConsentManagementService.getParticipantStudies(studyInfoId, userId)).thenReturn(null);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);

    assertNull(responseEntity);
    assertEquals(400, response.getStatus());
    verify(userConsentManagementService, never())
        .getStudyConsent(userId, studyInfoId, consentVersion);
    verify(userConsentManagementService, never()).getUserDetailsId(anyString());
    verify(userConsentManagementService, never()).saveStudyConsent(any(StudyConsentBO.class));
    verify(cloudStorageService, never()).saveFile(anyString(), anyString(), anyString());
    verify(userConsentManagementService, never()).saveParticipantStudies(anyList());
  }

  @Test
  public void testUpdateEligibilityConsentStatusInvalidUserId() {
    String studyId = "testStudy";
    String userId = "";

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentReqBean reqBean =
        new ConsentReqBean(
            "1.0", "completed", Base64.getEncoder().encodeToString("content".getBytes()));

    ConsentStatusBean bean = new ConsentStatusBean(studyId, Boolean.TRUE, reqBean, "true");

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);

    assertNull(responseEntity);
    assertEquals(400, response.getStatus());
    verify(userConsentManagementService, never()).getStudyInfoId(anyString());
    verify(userConsentManagementService, never()).getParticipantStudies(anyInt(), anyString());
    verify(userConsentManagementService, never())
        .getStudyConsent(anyString(), anyInt(), anyString());
    verify(userConsentManagementService, never()).getUserDetailsId(anyString());
    verify(userConsentManagementService, never()).saveStudyConsent(any(StudyConsentBO.class));
    verify(cloudStorageService, never()).saveFile(anyString(), anyString(), anyString());
    verify(userConsentManagementService, never()).saveParticipantStudies(anyList());
  }

  @Test
  public void testUpdateEligibilityConsentStatusInvalidConsent() {
    String studyId = "testStudy";
    String userId = "";

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentStatusBean bean = new ConsentStatusBean(studyId, Boolean.TRUE, null, "true");

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);

    assertNull(responseEntity);
    assertEquals(400, response.getStatus());
    verify(userConsentManagementService, never()).getStudyInfoId(anyString());
    verify(userConsentManagementService, never()).getParticipantStudies(anyInt(), anyString());
    verify(userConsentManagementService, never())
        .getStudyConsent(anyString(), anyInt(), anyString());
    verify(userConsentManagementService, never()).getUserDetailsId(anyString());
    verify(userConsentManagementService, never()).saveStudyConsent(any(StudyConsentBO.class));
    verify(cloudStorageService, never()).saveFile(anyString(), anyString(), anyString());
    verify(userConsentManagementService, never()).saveParticipantStudies(anyList());
  }
}
