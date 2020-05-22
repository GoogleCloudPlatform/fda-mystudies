package com.google.cloud.healthcare.fdamystudies.service.tests;

import static com.google.cloud.healthcare.fdamystudies.utils.MyStudiesUserRegUtil.ErrorCodes.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.HibernateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.ConsentStudyResponseBean;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.dao.UserConsentManagementDao;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementServiceImpl;
import com.google.cloud.healthcare.fdamystudies.testutils.MockUtils;

@RunWith(MockitoJUnitRunner.class)
public class UserConsentManagementServiceTests {

  @Mock private UserConsentManagementDao userConsentManagementDao;

  @Mock private FileStorageService cloudStorageService;

  @InjectMocks private UserConsentManagementServiceImpl userConsentManagementService;

  @Test
  public void testGetParticipantStudies() {
    Integer studyId = 1;
    String userId = "userId";

    StudyInfoBO sBO = new StudyInfoBO(studyId);
    UserDetailsBO uBO = new UserDetailsBO(userId);
    ParticipantStudiesBO bo = new ParticipantStudiesBO(uBO, sBO);

    when(userConsentManagementDao.getParticipantStudies(studyId, userId)).thenReturn(bo);
    ParticipantStudiesBO result =
        userConsentManagementService.getParticipantStudies(studyId, userId);

    assertEquals(bo, result);
  }

  @Test
  public void testGetParticipantStudiesExceptionCase() {
    Integer studyId = 1;
    String userId = "userId";
    when(userConsentManagementDao.getParticipantStudies(studyId, userId))
        .thenThrow(HibernateException.class);
    ParticipantStudiesBO result =
        userConsentManagementService.getParticipantStudies(studyId, userId);
    assertNull(result);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSaveParticipantStudies() {
    List<ParticipantStudiesBO> participantStudiesList = new LinkedList<>();
    ParticipantStudiesBO participantStudiesBO = new ParticipantStudiesBO("", null);
    participantStudiesList.add(participantStudiesBO);

    when(userConsentManagementDao.saveParticipantStudies(participantStudiesList))
        .thenReturn(SUCCESS.getValue());
    String result = userConsentManagementService.saveParticipantStudies(participantStudiesList);

    assertEquals(SUCCESS.getValue(), result);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSaveParticipantStudiesExceptionCase() {
    List<ParticipantStudiesBO> participantStudiesList = new LinkedList<>();
    ParticipantStudiesBO participantStudiesBO = new ParticipantStudiesBO("", null);
    participantStudiesList.add(participantStudiesBO);

    when(userConsentManagementDao.saveParticipantStudies(participantStudiesList))
        .thenThrow(HibernateException.class);
    String result = userConsentManagementService.saveParticipantStudies(participantStudiesList);

    assertEquals("FAILURE", result);
  }

  @Test
  public void testGetStudyConsent() {
    String userId = "userId";
    Integer studyId = 2;
    String consentVersion = "1.0";

    StudyConsentBO bo = new StudyConsentBO(studyId);

    when(userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion)).thenReturn(bo);
    StudyConsentBO result =
        userConsentManagementService.getStudyConsent(userId, studyId, consentVersion);
    assertEquals(studyId, result.getStudyInfoId());
  }

  @Test
  public void testGetStudyConsentExceptionCase() {
    String userId = "userId";
    Integer studyId = 2;
    String consentVersion = "1.0";

    when(userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion))
        .thenThrow(HibernateException.class);
    StudyConsentBO result =
        userConsentManagementService.getStudyConsent(userId, studyId, consentVersion);
    assertNull(result);
  }

  @Test
  public void testSaveStudyConsent() {
    StudyConsentBO studyConsentBO = new StudyConsentBO();
    when(userConsentManagementDao.saveStudyConsent(studyConsentBO)).thenReturn(SUCCESS.getValue());

    String result = userConsentManagementService.saveStudyConsent(studyConsentBO);
    assertEquals(SUCCESS.getValue(), result);
  }

  @Test
  public void testSaveStudyConsentExceptionCase() {
    StudyConsentBO studyConsentBO = new StudyConsentBO();
    when(userConsentManagementDao.saveStudyConsent(studyConsentBO))
        .thenThrow(HibernateException.class);

    String result = userConsentManagementService.saveStudyConsent(studyConsentBO);
    assertEquals("FAILURE", result);
  }

  @Test
  public void testGetStudyInfoId() {
    String studyId = "testStudyId";
    StudyInfoBean bean = new StudyInfoBean(2);

    when(userConsentManagementDao.getStudyInfoId(studyId)).thenReturn(bean);
    StudyInfoBean result = userConsentManagementService.getStudyInfoId(studyId);
    assertEquals(bean.getStudyInfoId(), result.getStudyInfoId());
    assertEquals(bean, result);
  }

  @Test
  public void testGetStudyInfoIdExceptionCase() {
    String studyId = "testStudyId";
    when(userConsentManagementDao.getStudyInfoId(studyId)).thenThrow(HibernateException.class);
    StudyInfoBean bean = userConsentManagementService.getStudyInfoId(studyId);
    assertNull(bean);
  }

  @Test
  public void testGetUserDetailsId() {
    String userId = "userId";
    Integer userDetailsId = 3;

    when(userConsentManagementDao.getUserDetailsId(userId)).thenReturn(userDetailsId);
    Integer result = userConsentManagementService.getUserDetailsId(userId);
    assertEquals(userDetailsId, result);
  }

  @Test
  public void testGetUserDetailsIdExceptionCase() {
    String userId = "userId";

    when(userConsentManagementDao.getUserDetailsId(userId)).thenThrow(HibernateException.class);
    Integer result = userConsentManagementService.getUserDetailsId(userId);
    assertNull(result);
  }

  @Test
  public void testGetStudyConsentDetails() {
    String userId = "userId";
    Integer studyId = 3;
    String consentVersion = "1.0";
    StudyConsentBO studyConsent = new StudyConsentBO(consentVersion, "pdf content", "pdf path", 1);

    when(userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion))
        .thenReturn(studyConsent);
    ParticipantStudiesBO participantStudies = new ParticipantStudiesBO(null, "sharing");
    when(userConsentManagementDao.getParticipantStudies(studyId, userId))
        .thenReturn(participantStudies);
    MockUtils.setCloudStorageDownloadExpectations(cloudStorageService, "pdf content");
    ConsentStudyResponseBean bean =
        userConsentManagementService.getStudyConsentDetails(userId, studyId, consentVersion);
    ConsentResponseBean consentBean =
        new ConsentResponseBean(
            consentVersion,
            "application/pdf",
            Base64.getEncoder().encodeToString("pdf content".getBytes()));
    assertEquals(consentBean, bean.getConsent());
    assertEquals("sharing", participantStudies.getSharing());
  }

  @Test
  public void testGetStudyConsentDetailsNoStudyConsent() {
    String userId = "userId";
    Integer studyId = 3;
    String consentVersion = "1.0";

    // The DAO doesn't find any study consent with given parameters
    when(userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion))
        .thenReturn(null);
    ConsentStudyResponseBean bean =
        userConsentManagementService.getStudyConsentDetails(userId, studyId, consentVersion);
    ConsentResponseBean consentBean = new ConsentResponseBean();
    assertEquals(consentBean, bean.getConsent());
  }

  @Test
  public void testGetStudyConsentDetailsWithoutCloudStorage() {
    String userId = "userId";
    Integer studyId = 3;
    String consentVersion = "1.0";
    StudyConsentBO studyConsent = new StudyConsentBO(consentVersion, "pdf content", "pdf path", 0);

    when(userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion))
        .thenReturn(studyConsent);
    ParticipantStudiesBO participantStudies = new ParticipantStudiesBO(null, "sharing");
    when(userConsentManagementDao.getParticipantStudies(studyId, userId))
        .thenReturn(participantStudies);
    ConsentStudyResponseBean bean =
        userConsentManagementService.getStudyConsentDetails(userId, studyId, consentVersion);
    ConsentResponseBean consentBean =
        new ConsentResponseBean(consentVersion, "application/pdf", "pdf content");
    assertEquals(consentBean, bean.getConsent());
    assertEquals("sharing", participantStudies.getSharing());
  }

  @Test
  public void testGetStudyConsentDetailsCloudExceptionCase() {
    String userId = "userId";
    Integer studyId = 3;
    String consentVersion = "1.0";
    StudyConsentBO studyConsent = new StudyConsentBO(consentVersion, "pdf content", "pdf path", 1);

    when(userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion))
        .thenReturn(studyConsent);
    ParticipantStudiesBO participantStudies = new ParticipantStudiesBO(null, "sharing");
    MockUtils.setCloudStorageDownloadExceptionExpectations(cloudStorageService);
    ConsentStudyResponseBean bean =
        userConsentManagementService.getStudyConsentDetails(userId, studyId, consentVersion);
    ConsentResponseBean consentBean = new ConsentResponseBean(consentVersion, null, "pdf content");
    assertEquals(consentBean, bean.getConsent());
    assertNull(bean.getSharing());
  }
}
