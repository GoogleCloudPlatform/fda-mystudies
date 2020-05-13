package com.google.cloud.healthcare.fdamystudies.controller.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.controller.UserConsentManagementController;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementService;
import com.google.cloud.healthcare.fdamystudies.utils.ErrorCode;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
public class UserConsentManagementControllerTests {

  @Mock private List<String> list;

  @Mock private UserConsentManagementService userConsentManagementService;

  @Mock private CommonService commonService;

  @Mock private FileStorageService cloudStorageService;

  @Mock private ApplicationPropertyConfiguration appConfig;

  @InjectMocks private UserConsentManagementController userConsentManagementController;

  @Test
  public void testList() {
    // Sample Mockito unit test that uses mocked java.util.List //

    list.add("X");

    Mockito.verify(list, times(1)).add("X");

    Mockito.when(list.size()).thenReturn(2);
    assertEquals(2, list.size());
  }

  @Test
  public void testGetStudyConsentPDF() {
    HttpServletResponse response = new MockHttpServletResponse();

    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "jdusYdDLI";
    String consentVersion = "1.0";

    StudyInfoBean studyInfo = new StudyInfoBean();
    studyInfo.setStudyInfoId(studyInfoId);

    // Mockito expectations
    Mockito.when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);
    ConsentStudyResponseBean resp = new ConsentStudyResponseBean();
    Mockito.when(
            userConsentManagementService.getStudyConsentDetails(
                userId, studyInfoId, consentVersion))
        .thenReturn(resp);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.getStudyConsentPDF(
            userId, studyId, consentVersion, response);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(ConsentStudyResponseBean.class, responseEntity.getBody().getClass());
    assertEquals("success", ((ConsentStudyResponseBean) responseEntity.getBody()).getMessage());
  }

  @Test
  public void testGetStudyConsentPDFNoDataFound() throws IOException {
    HttpServletResponse response = new MockHttpServletResponse();

    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "jdusYdDLI";
    String consentVersion = "1.0";

    StudyInfoBean studyInfo = new StudyInfoBean();
    studyInfo.setStudyInfoId(studyInfoId);

    // Mockito expectations
    Mockito.when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);
    Mockito.when(
            userConsentManagementService.getStudyConsentDetails(
                userId, studyInfoId, consentVersion))
        .thenReturn(null);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.getStudyConsentPDF(
            userId, studyId, consentVersion, response);
    assertEquals(null, responseEntity);
    assertEquals(400, response.getStatus());
  }

  @Test
  public void testGetStudyConsentPDFInvalidData() {
    HttpServletResponse response = new MockHttpServletResponse();

    String studyId = "";
    int studyInfoId = 1;
    String userId = "jdusYdDLI";
    String consentVersion = "1.0";

    StudyInfoBean studyInfo = new StudyInfoBean();
    studyInfo.setStudyInfoId(studyInfoId);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.getStudyConsentPDF(
            userId, studyId, consentVersion, response);

    assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    Mockito.verify(userConsentManagementService, never()).getStudyInfoId(studyId);
    Mockito.verify(userConsentManagementService, never())
        .getStudyConsentDetails(userId, studyInfoId, consentVersion);
  }

  @Test
  public void testUpdateEligibilityConsentStatus() {
    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "jdusYdDLI";
    String consentVersion = "1.0";
    Integer userDetailsId = 1;

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentStatusBean bean = new ConsentStatusBean();
    bean.setStudyId(studyId);
    bean.setEligibility(Boolean.TRUE);
    bean.setSharing("true");
    ConsentReqBean reqBean = new ConsentReqBean();
    reqBean.setPdf(Base64.getEncoder().encodeToString("content".getBytes()));
    reqBean.setVersion("1.0");
    reqBean.setStatus("completed");
    bean.setConsent(reqBean);

    StudyConsentBO studyConsent = new StudyConsentBO();
    StudyInfoBean studyInfo = new StudyInfoBean();
    studyInfo.setStudyInfoId(studyInfoId);
    ParticipantStudiesBO participantStudies = new ParticipantStudiesBO();
    String underDirectory = userId + "/" + studyId;
    String fileName =
        userId
            + "_"
            + studyId
            + "_"
            + consentVersion
            + "_"
            + new SimpleDateFormat("MMddyyyyHHmmss").format(new Date())
            + ".pdf";

    Mockito.when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);
    Mockito.when(userConsentManagementService.getParticipantStudies(studyInfoId, userId))
        .thenReturn(participantStudies);
    Mockito.when(
            userConsentManagementService.saveParticipantStudies(
                Collections.singletonList(participantStudies)))
        .thenReturn("SUCCESS");
    Mockito.when(userConsentManagementService.getStudyConsent(userId, studyInfoId, consentVersion))
        .thenReturn(studyConsent);
    Mockito.when(userConsentManagementService.getUserDetailsId(userId)).thenReturn(userDetailsId);
    Mockito.when(userConsentManagementService.saveStudyConsent(studyConsent)).thenReturn("SUCCESS");
    Mockito.when(cloudStorageService.saveFile(fileName, reqBean.getPdf(), underDirectory))
        .thenReturn(fileName + "/" + underDirectory);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(ErrorBean.class, responseEntity.getBody().getClass());
    assertEquals(
        ErrorCode.EC_200.code(), ((ErrorBean) responseEntity.getBody()).getCode().intValue());
  }

  @Test
  public void testUpdateEligibilityConsentStatusVersionRequired() {
    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "jdusYdDLI";
    String consentVersion = "1.0";

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentStatusBean bean = new ConsentStatusBean();
    bean.setStudyId(studyId);
    bean.setEligibility(Boolean.TRUE);
    bean.setSharing("true");
    ConsentReqBean reqBean = new ConsentReqBean();
    reqBean.setVersion("");
    reqBean.setPdf(Base64.getEncoder().encodeToString("content".getBytes()));
    reqBean.setStatus("completed");
    bean.setConsent(reqBean);

    StudyInfoBean studyInfo = new StudyInfoBean();
    studyInfo.setStudyInfoId(studyInfoId);
    ParticipantStudiesBO participantStudies = new ParticipantStudiesBO();

    Mockito.when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);
    Mockito.when(userConsentManagementService.getParticipantStudies(studyInfoId, userId))
        .thenReturn(participantStudies);
    Mockito.when(
            userConsentManagementService.saveParticipantStudies(
                Collections.singletonList(participantStudies)))
        .thenReturn("SUCCESS");

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);

    assertEquals(null, responseEntity);
    assertEquals(400, response.getStatus());
    Mockito.verify(userConsentManagementService, never())
        .getStudyConsent(userId, studyInfoId, consentVersion);
    Mockito.verify(userConsentManagementService, never()).getUserDetailsId(userId);
    Mockito.verify(userConsentManagementService, never())
        .saveStudyConsent(ArgumentMatchers.any(StudyConsentBO.class));
    Mockito.verify(cloudStorageService, never())
        .saveFile(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString());
  }

  @Test
  public void testUpdateEligibilityConsentStatusNoDataAvailable() {
    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "jdusYdDLI";
    String consentVersion = "1.0";

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentStatusBean bean = new ConsentStatusBean();
    bean.setStudyId(studyId);
    bean.setEligibility(Boolean.TRUE);
    bean.setSharing("true");
    ConsentReqBean reqBean = new ConsentReqBean();
    reqBean.setVersion("");
    reqBean.setPdf(Base64.getEncoder().encodeToString("content".getBytes()));
    reqBean.setStatus("completed");
    bean.setConsent(reqBean);

    StudyInfoBean studyInfo = new StudyInfoBean();
    studyInfo.setStudyInfoId(studyInfoId);

    Mockito.when(userConsentManagementService.getStudyInfoId(studyId)).thenReturn(studyInfo);
    Mockito.when(userConsentManagementService.getParticipantStudies(studyInfoId, userId))
        .thenReturn(null);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);

    assertEquals(null, responseEntity);
    assertEquals(400, response.getStatus());
    Mockito.verify(userConsentManagementService, never())
        .getStudyConsent(userId, studyInfoId, consentVersion);
    Mockito.verify(userConsentManagementService, never())
        .getUserDetailsId(ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .saveStudyConsent(ArgumentMatchers.any(StudyConsentBO.class));
    Mockito.verify(cloudStorageService, never())
        .saveFile(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .saveParticipantStudies(ArgumentMatchers.anyList());
  }

  @Test
  public void testUpdateEligibilityConsentStatusInvalidUserId() {
    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "";

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentStatusBean bean = new ConsentStatusBean();
    bean.setStudyId(studyId);
    bean.setEligibility(Boolean.TRUE);
    bean.setSharing("true");
    ConsentReqBean reqBean = new ConsentReqBean();
    reqBean.setVersion("1.0");
    reqBean.setPdf(Base64.getEncoder().encodeToString("content".getBytes()));
    reqBean.setStatus("completed");
    bean.setConsent(reqBean);

    StudyInfoBean studyInfo = new StudyInfoBean();
    studyInfo.setStudyInfoId(studyInfoId);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);

    assertEquals(null, responseEntity);
    assertEquals(400, response.getStatus());
    Mockito.verify(userConsentManagementService, never())
        .getStudyInfoId(ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .getParticipantStudies(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .getStudyConsent(
            ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .getUserDetailsId(ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .saveStudyConsent(ArgumentMatchers.any(StudyConsentBO.class));
    Mockito.verify(cloudStorageService, never())
        .saveFile(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .saveParticipantStudies(ArgumentMatchers.anyList());
  }

  @Test
  public void testUpdateEligibilityConsentStatusInvalidConsent() {
    String studyId = "testStudy";
    int studyInfoId = 1;
    String userId = "";

    HttpServletResponse response = new MockHttpServletResponse();
    ConsentStatusBean bean = new ConsentStatusBean();
    bean.setStudyId(studyId);
    bean.setEligibility(Boolean.TRUE);
    bean.setSharing("true");
    bean.setConsent(null);

    StudyInfoBean studyInfo = new StudyInfoBean();
    studyInfo.setStudyInfoId(studyInfoId);

    ResponseEntity<?> responseEntity =
        userConsentManagementController.updateEligibilityConsentStatus(userId, bean, response);

    assertEquals(null, responseEntity);
    assertEquals(400, response.getStatus());
    Mockito.verify(userConsentManagementService, never())
        .getStudyInfoId(ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .getParticipantStudies(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .getStudyConsent(
            ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .getUserDetailsId(ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .saveStudyConsent(ArgumentMatchers.any(StudyConsentBO.class));
    Mockito.verify(cloudStorageService, never())
        .saveFile(
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString());
    Mockito.verify(userConsentManagementService, never())
        .saveParticipantStudies(ArgumentMatchers.anyList());
  }
}
