/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
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

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.QuestionnaireStepBean;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.FormBo;
import com.fdahpstudydesigner.bo.FormMappingBo;
import com.fdahpstudydesigner.bo.HealthKitKeysInfo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.QuestionConditionBranchBo;
import com.fdahpstudydesigner.bo.QuestionReponseTypeBo;
import com.fdahpstudydesigner.bo.QuestionResponseSubTypeBo;
import com.fdahpstudydesigner.bo.QuestionResponseTypeMasterInfoBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnaireCustomScheduleBo;
import com.fdahpstudydesigner.bo.QuestionnairesFrequenciesBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.StudyVersionBo;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;
import java.util.SortedMap;
import org.hibernate.Session;
import org.hibernate.Transaction;

public interface StudyQuestionnaireDAO {

  public String checkFromQuestionShortTitle(
      String questionnaireId,
      String shortTitle,
      String questionnaireShortTitle,
      String customStudyId);

  public String checkQuestionnaireResponseTypeValidation(String studyId, String customStudyId);

  public String checkQuestionnaireShortTitle(
      String studyId, String shortTitle, String customStudyId);

  public String checkQuestionnaireStepShortTitle(
      String questionnaireId,
      String stepType,
      String shortTitle,
      String questionnaireShortTitle,
      String customStudyId);

  public String checkStatShortTitle(String studyId, String shortTitle, String customStudyId);

  public QuestionnaireBo copyStudyQuestionnaireBo(
      String questionnaireId, String customStudyId, SessionObject sessionObject);

  public String deleteFromStepQuestion(
      String formId,
      String questionId,
      SessionObject sessionObject,
      String customStudyId,
      AuditLogEventRequest auditRequest);

  public String deleteQuestionnaireStep(
      String stepId,
      String questionnaireId,
      String stepType,
      SessionObject sessionObject,
      String customStudyId);

  public String deleteQuestuionnaireInfo(
      String studyId, String questionnaireId, SessionObject sessionObject, String customStudyId);

  public List<HealthKitKeysInfo> getHeanlthKitKeyInfoList();

  public InstructionsBo getInstructionsBo(
      String instructionId,
      String questionnaireShortTitle,
      String customStudyId,
      String questionnaireId);

  public List<QuestionConditionBranchBo> getQuestionConditionalBranchingLogic(
      Session session, String questionId);

  public QuestionnaireBo getQuestionnaireById(String questionnaireId, String customStudyId);

  public List<QuestionnairesStepsBo> getQuestionnairesStepsList(
      String questionnaireId, Integer sequenceNo);

  public QuestionnairesStepsBo getQuestionnaireStep(
      String stepId,
      String stepType,
      String questionnaireShortTitle,
      String customStudyId,
      String questionnaireId);

  public SortedMap<Integer, QuestionnaireStepBean> getQuestionnaireStepList(String questionnaireId);

  public List<QuestionResponseTypeMasterInfoBo> getQuestionReponseTypeList();

  public QuestionsBo getQuestionsById(
      String questionId, String questionnaireShortTitle, String customStudyId);

  public List<QuestionnaireBo> getStudyQuestionnairesByStudyId(String studyId, Boolean isLive);

  public List<QuestionnaireBo> getStudyQuestionnairesByStudyId(String studyId);

  public Boolean isAnchorDateExistsForStudy(String studyId, String customStudyId);

  public Boolean isQuestionnairesCompleted(String studyId);

  public String reOrderFormStepQuestions(String formId, int oldOrderNumber, int newOrderNumber);

  public String reOrderQuestionnaireSteps(
      String questionnaireId, int oldOrderNumber, int newOrderNumber);

  public QuestionnairesStepsBo saveOrUpdateFromQuestionnaireStep(
      QuestionnairesStepsBo questionnairesStepsBo, SessionObject sesObj, String customStudyId);

  public InstructionsBo saveOrUpdateInstructionsBo(
      InstructionsBo instructionsBo, SessionObject sessionObject, String customStudyId);

  public QuestionsBo saveOrUpdateQuestion(QuestionsBo questionsBo);

  public QuestionnaireBo saveORUpdateQuestionnaire(
      QuestionnaireBo questionnaireBo, SessionObject sessionObject, String customStudyId);

  public QuestionnairesStepsBo saveOrUpdateQuestionStep(
      QuestionnairesStepsBo questionnairesStepsBo,
      SessionObject sessionObject,
      String customStudyId);

  public String validateLineChartSchedule(String questionnaireId, String frequency);

  public String validateRepetableFormQuestionStats(String formId);

  public String checkUniqueAnchorDateName(
      String anchordateText, String customStudyId, String anchorDateId);

  public String getStudyIdByCustomStudy(Session session, String customStudyId);

  public List<AnchorDateTypeBo> getAnchorTypesByStudyId(String customStudyId);

  public boolean isAnchorDateExistByQuestionnaire(String questionnaireId);

  public String updateAnchordateInQuestionnaire(
      Session session,
      Transaction transaction,
      StudyVersionBo studyVersionBo,
      String questionnaireId,
      SessionObject sessionObject,
      String studyId,
      String stepId,
      String questionId,
      String stepType,
      boolean isChange,
      String customStudyId);

  public QuestionnaireBo getQuestionnaireById(String questionnaireId);

  public QuestionsBo getQuestionById(String questionId);

  public List<QuestionnairesStepsBo> getQuestionnairesStepsList(List<String> questionnaireIds);

  public List<QuestionsBo> getQuestionsByInstructionFormIds(List<String> instructionFormIds);

  public List<QuestionnairesFrequenciesBo> getQuestionnairesFrequenciesBoList(
      List<String> questionnaireIds);

  public List<QuestionnaireCustomScheduleBo> getQuestionnairesCustomFrequenciesBoList(
      List<String> questionnaireIds);

  public List<FormMappingBo> getFormMappingbyInstructionFormIds(List<String> instructionFormIds);

  public List<InstructionsBo> getInstructionListByInstructionFormIds(
      List<String> instructionFormIds);

  public List<QuestionResponseSubTypeBo> getQuestionResponseSubTypeBoByInstructionFormIds(
      List<String> instructionFormIds);

  public List<QuestionReponseTypeBo> getQuestionResponseTypeBoByInstructionFormIds(
      List<String> instructionFormIds);

  public List<FormBo> getFormsByInstructionFormIds(List<String> instructionFormIds);

  public QuestionnaireBo cloneStudyQuestionnaire(
      String questionnaireId, String studyId, SessionObject sessionObject);

  public List<String> getQuestionsByFormIds(List<String> formIds);
}
