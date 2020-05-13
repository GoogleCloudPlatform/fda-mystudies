package com.google.cloud.healthcare.fdamystudies.service.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.HibernateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import com.google.cloud.healthcare.fdamystudies.bean.StudyInfoBean;
import com.google.cloud.healthcare.fdamystudies.dao.UserConsentManagementDao;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyConsentBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsBO;
import com.google.cloud.healthcare.fdamystudies.service.FileStorageService;
import com.google.cloud.healthcare.fdamystudies.service.UserConsentManagementServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class UserConsentManagementServiceTests {

  @Mock private UserConsentManagementDao userConsentManagementDao;

  @Mock private FileStorageService cloudStorageService;

  @InjectMocks private UserConsentManagementServiceImpl userConsentManagementService;

  @Test
  public void testGetParticipantStudies() {

    Integer studyId = Integer.valueOf(1);
    String userId = "sdjUyd";
    ParticipantStudiesBO bo = new ParticipantStudiesBO();

    StudyInfoBO sBO = new StudyInfoBO();
    sBO.setId(studyId);
    bo.setStudyInfo(sBO);
    UserDetailsBO uBO = new UserDetailsBO();
    uBO.setUserId(userId);
    bo.setUserDetails(uBO);

    Mockito.when(userConsentManagementDao.getParticipantStudies(studyId, userId)).thenReturn(bo);
    ParticipantStudiesBO result =
        userConsentManagementService.getParticipantStudies(studyId, userId);

    ArgumentCaptor<Integer> studyIdPassed = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<String> userIdPassed = ArgumentCaptor.forClass(String.class);
    Mockito.verify(userConsentManagementDao)
        .getParticipantStudies(studyIdPassed.capture(), userIdPassed.capture());
    assertEquals(studyId, studyIdPassed.getValue());
    assertEquals(userId, userIdPassed.getValue());
    assertEquals(bo, result);
    assertEquals(bo.getStudyInfo().getId(), result.getStudyInfo().getId());
    assertEquals(bo.getUserDetails().getUserId(), result.getUserDetails().getUserId());
  }

  @Test
  public void testGetParticipantStudiesExceptionCase() {
    Integer studyId = Integer.valueOf(1);
    String userId = "sdjUyd";
    Mockito.when(userConsentManagementDao.getParticipantStudies(studyId, userId))
        .thenThrow(HibernateException.class);
    ParticipantStudiesBO result =
        userConsentManagementService.getParticipantStudies(studyId, userId);
    assertNull(result);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSaveParticipantStudies() {
    List<ParticipantStudiesBO> participantStudiesList = new LinkedList<>();
    ParticipantStudiesBO participantStudiesBO = new ParticipantStudiesBO();
    participantStudiesBO.setParticipantId("");
    participantStudiesList.add(participantStudiesBO);

    ArgumentCaptor<List<ParticipantStudiesBO>> participantStudiesBOListPassed =
        ArgumentCaptor.forClass(List.class);
    ;
    Mockito.when(userConsentManagementDao.saveParticipantStudies(participantStudiesList))
        .thenReturn("SUCCESS");
    String result = userConsentManagementService.saveParticipantStudies(participantStudiesList);
    Mockito.verify(userConsentManagementDao)
        .saveParticipantStudies(participantStudiesBOListPassed.capture());

    assertNotNull(participantStudiesBOListPassed.getValue());
    assertEquals(1, participantStudiesBOListPassed.getValue().size());
    assertEquals(participantStudiesBO, participantStudiesBOListPassed.getValue().get(0));
    assertEquals("SUCCESS", result);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSaveParticipantStudiesExceptionCase() {
    List<ParticipantStudiesBO> participantStudiesList = new LinkedList<>();
    ParticipantStudiesBO participantStudiesBO = new ParticipantStudiesBO();
    participantStudiesBO.setParticipantId("");
    participantStudiesList.add(participantStudiesBO);

    ArgumentCaptor<List<ParticipantStudiesBO>> participantStudiesBOListPassed =
        ArgumentCaptor.forClass(List.class);
    Mockito.when(userConsentManagementDao.saveParticipantStudies(participantStudiesList))
        .thenThrow(HibernateException.class);
    String result = userConsentManagementService.saveParticipantStudies(participantStudiesList);
    Mockito.verify(userConsentManagementDao)
        .saveParticipantStudies(participantStudiesBOListPassed.capture());

    assertNotNull(participantStudiesBOListPassed.getValue());
    assertEquals(1, participantStudiesBOListPassed.getValue().size());
    assertEquals(participantStudiesBO, participantStudiesBOListPassed.getValue().get(0));
    assertEquals("FAILURE", result);
  }

  @Test
  public void testGetStudyConsent() {
    String userId = "hdUydjsII";
    Integer studyId = Integer.valueOf(2);
    String consentVersion = "1.0";

    StudyConsentBO bo = new StudyConsentBO();
    bo.setStudyInfoId(studyId);
    ArgumentCaptor<String> userIdPassed = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Integer> studyIdPassed = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<String> consentVersionPassed = ArgumentCaptor.forClass(String.class);

    Mockito.when(userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion))
        .thenReturn(bo);
    StudyConsentBO result =
        userConsentManagementService.getStudyConsent(userId, studyId, consentVersion);
    Mockito.verify(userConsentManagementDao)
        .getStudyConsent(
            userIdPassed.capture(), studyIdPassed.capture(), consentVersionPassed.capture());
    assertEquals(userId, userIdPassed.getValue());
    assertEquals(studyId, studyIdPassed.getValue());
    assertEquals(consentVersion, consentVersionPassed.getValue());
    assertEquals(studyId, result.getStudyInfoId());
  }

  @Test
  public void testGetStudyConsentExceptionCase() {
    String userId = "hdUydjsII";
    Integer studyId = Integer.valueOf(2);
    String consentVersion = "1.0";
    ArgumentCaptor<String> userIdPassed = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<Integer> studyIdPassed = ArgumentCaptor.forClass(Integer.class);
    ArgumentCaptor<String> consentVersionPassed = ArgumentCaptor.forClass(String.class);

    Mockito.when(userConsentManagementDao.getStudyConsent(userId, studyId, consentVersion))
        .thenThrow(HibernateException.class);
    StudyConsentBO result =
        userConsentManagementService.getStudyConsent(userId, studyId, consentVersion);
    Mockito.verify(userConsentManagementDao)
        .getStudyConsent(
            userIdPassed.capture(), studyIdPassed.capture(), consentVersionPassed.capture());
    assertEquals(userId, userIdPassed.getValue());
    assertEquals(studyId, studyIdPassed.getValue());
    assertEquals(consentVersion, consentVersionPassed.getValue());
    assertNull(result);
  }

  @Test
  public void testSaveStudyConsent() {
    StudyConsentBO studyConsentBO = new StudyConsentBO();
    Mockito.when(userConsentManagementDao.saveStudyConsent(studyConsentBO)).thenReturn("SUCCESS");

    String result = userConsentManagementService.saveStudyConsent(studyConsentBO);
    assertEquals("SUCCESS", result);
  }

  @Test
  public void testSaveStudyConsentExceptionCase() {
    StudyConsentBO studyConsentBO = new StudyConsentBO();
    Mockito.when(userConsentManagementDao.saveStudyConsent(studyConsentBO))
        .thenThrow(HibernateException.class);

    String result = userConsentManagementService.saveStudyConsent(studyConsentBO);
    assertEquals("FAILURE", result);
  }

  @Test
  public void testGetStudyInfoId() {
    String studyId = "testStudyId";
    StudyInfoBean bean = new StudyInfoBean();
    bean.setStudyInfoId(2);

    Mockito.when(userConsentManagementDao.getStudyInfoId(studyId)).thenReturn(bean);
    ArgumentCaptor<String> studyIdPassed = ArgumentCaptor.forClass(String.class);
    StudyInfoBean result = userConsentManagementService.getStudyInfoId(studyId);
    Mockito.verify(userConsentManagementDao).getStudyInfoId(studyIdPassed.capture());
    assertEquals(studyId, studyIdPassed.getValue());
    assertEquals(bean.getStudyInfoId(), result.getStudyInfoId());
    assertEquals(bean, result);
  }

  @Test
  public void testGetStudyInfoIdExceptionCase() {
    String studyId = "testStudyId";
    ArgumentCaptor<String> studyIdPassed = ArgumentCaptor.forClass(String.class);
    Mockito.when(userConsentManagementDao.getStudyInfoId(studyId))
        .thenThrow(HibernateException.class);
    StudyInfoBean bean = userConsentManagementService.getStudyInfoId(studyId);
    Mockito.verify(userConsentManagementDao).getStudyInfoId(studyIdPassed.capture());
    assertEquals(studyId, studyIdPassed.getValue());
    assertNull(bean);
  }

  @Test
  public void testGetUserDetailsId() {
    String userId = "JhYYTdd";
    Integer userDetailsId = Integer.valueOf(3);

    Mockito.when(userConsentManagementDao.getUserDetailsId(userId)).thenReturn(userDetailsId);
    Integer result = userConsentManagementService.getUserDetailsId(userId);
    ArgumentCaptor<String> userIdPassed = ArgumentCaptor.forClass(String.class);
    Mockito.verify(userConsentManagementDao).getUserDetailsId(userIdPassed.capture());
    assertEquals(userId, userIdPassed.getValue());
    assertEquals(userDetailsId, result);
  }

  @Test
  public void testGetUserDetailsIdExceptionCase() {
    String userId = "JhYYTdd";

    Mockito.when(userConsentManagementDao.getUserDetailsId(userId))
        .thenThrow(HibernateException.class);
    Integer result = userConsentManagementService.getUserDetailsId(userId);
    ArgumentCaptor<String> userIdPassed = ArgumentCaptor.forClass(String.class);
    Mockito.verify(userConsentManagementDao).getUserDetailsId(userIdPassed.capture());
    assertEquals(userId, userIdPassed.getValue());
    assertNull(result);
  }
}
