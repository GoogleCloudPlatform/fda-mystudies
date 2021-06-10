/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.FormulaInfoBean;
import com.fdahpstudydesigner.bean.QuestionnaireStepBean;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.HealthKitKeysInfo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.QuestionResponseTypeMasterInfoBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;
import java.util.SortedMap;

public interface StudyQuestionnaireService {

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

  public String deletQuestionnaire(
      String studyId, String questionnaireId, SessionObject sessionObject, String customStudyId);

  public List<HealthKitKeysInfo> getHeanlthKitKeyInfoList();

  public InstructionsBo getInstructionsBo(
      String instructionId,
      String questionnaireShortTitle,
      String customStudyId,
      String questionnaireId);

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

  public Boolean isAnchorDateExistsForStudy(String studyId, String customStudyId);

  public Boolean isQuestionnairesCompleted(String studyId);

  public String reOrderFormStepQuestions(String formId, int oldOrderNumber, int newOrderNumber);

  public String reOrderQuestionnaireSteps(
      String questionnaireId, int oldOrderNumber, int newOrderNumber);

  public QuestionnairesStepsBo saveOrUpdateFromStepQuestionnaire(
      QuestionnairesStepsBo questionnairesStepsBo, SessionObject sesObj, String customStudyId);

  public InstructionsBo saveOrUpdateInstructionsBo(
      InstructionsBo instructionsBo, SessionObject sessionObject, String customStudyId);

  public QuestionsBo saveOrUpdateQuestion(
      QuestionsBo questionsBo, SessionObject sesObj, String customStudyId);

  public QuestionnaireBo saveOrUpdateQuestionnaire(
      QuestionnaireBo questionnaireBo, SessionObject sessionObject, String customStudyId);

  public QuestionnaireBo saveOrUpdateQuestionnaireSchedule(
      QuestionnaireBo questionnaireBo, SessionObject sessionObject, String customStudyId);

  public QuestionnairesStepsBo saveOrUpdateQuestionStep(
      QuestionnairesStepsBo questionnairesStepsBo,
      SessionObject sessionObject,
      String customStudyId);

  public String validateLineChartSchedule(String questionnaireId, String frequency);

  public FormulaInfoBean validateQuestionConditionalBranchingLogic(
      String lhs, String rhs, String operator, String input);

  public String validateRepetableFormQuestionStats(String formId);

  public String checkUniqueAnchorDateName(
      String anchordateText, String customStudyId, String anchorDateId);

  public List<AnchorDateTypeBo> getAnchorTypesByStudyId(String customStudyId);

  public boolean isAnchorDateExistByQuestionnaire(String questionnaireId);

  public QuestionnaireBo getQuestionnaireById(String questionnaireId);

  public QuestionsBo getQuestionById(String valueOf);
}
