/*
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as
 * Contract no. HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.bean.StudyIdBean;
import com.fdahpstudydesigner.bean.StudyListBean;
import com.fdahpstudydesigner.bean.StudyPageBean;
import com.fdahpstudydesigner.bo.ActiveTaskAtrributeValuesBo;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.ActiveTaskCustomScheduleBo;
import com.fdahpstudydesigner.bo.ActiveTaskFrequencyBo;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.Checklist;
import com.fdahpstudydesigner.bo.ComprehensionTestQuestionBo;
import com.fdahpstudydesigner.bo.ComprehensionTestResponseBo;
import com.fdahpstudydesigner.bo.ConsentBo;
import com.fdahpstudydesigner.bo.ConsentInfoBo;
import com.fdahpstudydesigner.bo.ConsentMasterInfoBo;
import com.fdahpstudydesigner.bo.EligibilityBo;
import com.fdahpstudydesigner.bo.EligibilityTestBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.ReferenceTablesBo;
import com.fdahpstudydesigner.bo.ResourceBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudyPageBo;
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.HashMap;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

public interface StudyDAO {

  public String checkActiveTaskTypeValidation(String studyId);

  public int comprehensionTestQuestionOrder(String studyId);

  public int consentInfoOrder(String studyId);

  public String deleteComprehensionTestQuestion(
      String questionId, String studyId, SessionObject sessionObject);

  public String deleteConsentInfo(
      String consentInfoId, String studyId, SessionObject sessionObject, String customStudyId);

  public String deleteEligibilityTestQusAnsById(
      String eligibilityTestId, String studyId, SessionObject sessionObject, String customStudyId);

  public boolean deleteLiveStudy(String customStudyId);

  public String deleteOverviewStudyPageById(String studyId, String pageId);

  public String deleteResourceInfo(
      String resourceInfoId, boolean resourceVisibility, String studyId);

  public boolean deleteStudyByCustomStudyId(String customStudyId);

  public String deleteStudyByIdOrCustomstudyId(
      Session session, Transaction transaction, String studyId, String customStudyId);

  public int eligibilityTestOrderCount(String eligibilityId);

  public List<StudyBo> getAllStudyList();

  public Checklist getchecklistInfo(String studyId);

  public ComprehensionTestQuestionBo getComprehensionTestQuestionById(String questionId);

  public List<ComprehensionTestQuestionBo> getComprehensionTestQuestionList(String studyId);

  public List<ComprehensionTestResponseBo> getComprehensionTestResponseList(
      String comprehensionQuestionId);

  public ConsentBo getConsentDetailsByStudyId(String studyId);

  public ConsentInfoBo getConsentInfoById(String consentInfoId);

  public List<ConsentInfoBo> getConsentInfoDetailsListByStudyId(String studyId);

  public List<ConsentInfoBo> getConsentInfoList(String studyId);

  public List<ConsentMasterInfoBo> getConsentMasterInfoList();

  public StudyIdBean getLiveVersion(String customStudyId);

  public NotificationBO getNotificationByResourceId(String resourseId);

  public List<StudyPageBo> getOverviewStudyPagesById(String studyId, String userId);

  public HashMap<String, List<ReferenceTablesBo>> getreferenceListByCategory();

  public ResourceBO getResourceInfo(String resourceInfoId);

  public List<ResourceBO> getResourceList(String studyId);

  public List<NotificationBO> getSavedNotification(String studyId);

  public StudyBo getStudyById(String studyId, String userId);

  public StudyBo getStudy(String id);

  public EligibilityBo getStudyEligibiltyByStudyId(String studyId);

  public List<StudyListBean> getStudyList(String userId);

  public List<StudyListBean> getStudyListByUserId(String userId);

  public StudyBo getStudyLiveStatusByCustomId(String customStudyId);

  public ResourceBO getStudyProtocol(String studyId);

  public String markAsCompleted(
      String studyId,
      String markCompleted,
      boolean flag,
      SessionObject sesObj,
      String customStudyId);

  public String reOrderComprehensionTestQuestion(
      String studyId, int oldOrderNumber, int newOrderNumber);

  public String reOrderConsentInfoList(String studyId, int oldOrderNumber, int newOrderNumber);

  public String reorderEligibilityTestQusAns(
      String eligibilityId, int oldOrderNumber, int newOrderNumber, String studyId);

  public String reOrderResourceList(String studyId, int oldOrderNumber, int newOrderNumber);

  public boolean resetDraftStudyByCustomStudyId(
      String customStudyId, String action, SessionObject sesObj);

  public int resourceOrder(String studyId);

  public List<ResourceBO> resourcesSaved(String studyId);

  public List<ResourceBO> resourcesWithAnchorDate(String studyId);

  public ConsentBo saveOrCompleteConsentReviewDetails(
      ConsentBo consentBo, SessionObject sesObj, String customStudyId);

  public String saveOrDoneChecklist(Checklist checklist);

  public ComprehensionTestQuestionBo saveOrUpdateComprehensionTestQuestion(
      ComprehensionTestQuestionBo comprehensionTestQuestionBo);

  public ConsentInfoBo saveOrUpdateConsentInfo(
      ConsentInfoBo consentInfoBo, SessionObject sesObj, String customStudyId);

  public String saveOrUpdateEligibilityTestQusAns(
      EligibilityTestBo eligibilityTestBo,
      String studyId,
      SessionObject sessionObject,
      String customStudyId);

  public String saveOrUpdateOverviewStudyPages(StudyPageBean studyPageBean, SessionObject sesObj);

  public String saveOrUpdateResource(ResourceBO resourceBO);

  public String saveOrUpdateStudy(StudyBo studyBo, SessionObject sessionObject);

  public String saveOrUpdateStudyEligibilty(
      EligibilityBo eligibilityBo, SessionObject sesObj, String customStudyId);

  public String saveOrUpdateStudySettings(StudyBo studyBo, SessionObject sesObj);

  public String saveResourceNotification(NotificationBO notificationBO, boolean notiFlag);

  public String updateStudyActionOnAction(String studyId, String buttonText, SessionObject sesObj);

  public String validateActivityComplete(String studyId, String action);

  public String validateEligibilityTestKey(
      String eligibilityTestId, String shortTitle, String eligibilityId);

  public String validateStudyAction(String studyId, String buttonText);

  public boolean validateStudyId(String studyId);

  public List<EligibilityTestBo> viewEligibilityTestQusAnsByEligibilityId(String eligibilityId);

  public EligibilityTestBo viewEligibilityTestQusAnsById(String eligibilityTestId);

  public Boolean isAnchorDateExistForEnrollment(String studyId, String customStudyId);

  public Boolean isAnchorDateExistForEnrollmentDraftStudy(String studyId, String customStudyId);

  public String updateAnchordateForEnrollmentDate(
      StudyBo oldStudyBo, StudyBo updatedStudyBo, Session session, Transaction transaction);

  public boolean validateAppId(
      String customStudyId, String appId, String studyType, String dbCustomStudyId);

  public StudyPermissionBO getStudyPermissionBO(String studyId, String userId);

  public StudyBo getStudyByLatestVersion(String customStudyId);

  public String getStudyCategory(String id);

  public Integer getEligibilityType(String studyId);

  public boolean validateStudyActions(String studyId);

  public List<ConsentBo> getConsentList(String customStudyId);

  public StudySequenceBo getStudySequenceByStudyId(String studyId);

  public AnchorDateTypeBo getAnchorDateDetails(String studyId);

  public List<ComprehensionTestResponseBo> getComprehensionTestResponseList(
      List<String> comprehensionTestQuestionIds);

  public void cloneStudy(StudyBo studyBo, SessionObject sessionObject);

  public void cloneEligibility(EligibilityBo eligibilityBo, String studyId);

  public void cloneComprehensionTest(
      ComprehensionTestQuestionBo comprehensionTestQuestionBo, String studyId);

  public void cloneConsent(ConsentBo consentBo, String studyId);

  public void cloneConsentInfo(ConsentInfoBo consentInfoBo, String studyId);

  public void saveStudyActiveTask(ActiveTaskBo activeTaskBo);

  public void saveActiveTaskAtrributeValuesBo(
      ActiveTaskAtrributeValuesBo activeTaskAtrributeValuesBo);

  public void saveActiveTaskCustomScheduleBo(ActiveTaskCustomScheduleBo activeTaskCustomScheduleBo);

  public void saveActiveTaskFrequencyBo(ActiveTaskFrequencyBo activeTaskFrequencyBo);

  public String saveExportFilePath(String studyId, String filePath);
}
