/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
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
import com.fdahpstudydesigner.bo.QuestionnaireCustomScheduleBo;
import com.fdahpstudydesigner.bo.QuestionnairesFrequenciesBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.dao.StudyQuestionnaireDAO;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudyQuestionnaireServiceImpl implements StudyQuestionnaireService {

  private static XLogger logger =
      XLoggerFactory.getXLogger(StudyQuestionnaireServiceImpl.class.getName());

  @Autowired private StudyQuestionnaireDAO studyQuestionnaireDAO;

  @Override
  public String checkFromQuestionShortTitle(
      Integer questionnaireId,
      String shortTitle,
      String questionnaireShortTitle,
      String customStudyId) {
    logger.entry("begin checkFromQuestionShortTitle()");
    return studyQuestionnaireDAO.checkFromQuestionShortTitle(
        questionnaireId, shortTitle, questionnaireShortTitle, customStudyId);
  }

  @Override
  public String checkQuestionnaireResponseTypeValidation(Integer studyId, String customStudyId) {
    logger.entry("begin checkQuestionnaireResponseTypeValidation()");
    return studyQuestionnaireDAO.checkQuestionnaireResponseTypeValidation(studyId, customStudyId);
  }

  @Override
  public String checkQuestionnaireShortTitle(
      Integer studyId, String shortTitle, String customStudyId) {
    logger.entry("begin checkQuestionnaireShortTitle()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message =
          studyQuestionnaireDAO.checkQuestionnaireShortTitle(studyId, shortTitle, customStudyId);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getQuestionnaireStepList - Error", e);
    }
    logger.exit("checkQuestionnaireShortTitle() - Ends");
    return message;
  }

  @Override
  public String checkQuestionnaireStepShortTitle(
      Integer questionnaireId,
      String stepType,
      String shortTitle,
      String questionnaireShortTitle,
      String customStudyId) {
    logger.entry("begin checkQuestionnaireStepShortTitle()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message =
          studyQuestionnaireDAO.checkQuestionnaireStepShortTitle(
              questionnaireId, stepType, shortTitle, questionnaireShortTitle, customStudyId);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - checkQuestionnaireStepShortTitle - Error", e);
    }
    logger.exit("checkQuestionnaireStepShortTitle() - Ends");
    return message;
  }

  @Override
  public String checkStatShortTitle(Integer studyId, String shortTitle, String customStudyId) {
    logger.entry("begin checkStatShortTitle");
    return studyQuestionnaireDAO.checkStatShortTitle(studyId, shortTitle, customStudyId);
  }

  @Override
  public QuestionnaireBo copyStudyQuestionnaireBo(
      Integer questionnaireId, String customStudyId, SessionObject sessionObject) {
    logger.entry("begin copyStudyQuestionnaireBo");
    return studyQuestionnaireDAO.copyStudyQuestionnaireBo(
        questionnaireId, customStudyId, sessionObject);
  }

  @Override
  public String deleteFromStepQuestion(
      Integer formId,
      Integer questionId,
      SessionObject sessionObject,
      String customStudyId,
      AuditLogEventRequest auditRequest) {
    logger.entry("begin deleteFromStepQuestion()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message =
          studyQuestionnaireDAO.deleteFromStepQuestion(
              formId, questionId, sessionObject, customStudyId, auditRequest);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - deleteFromStepQuestion - Error", e);
    }
    logger.exit("deleteFromStepQuestion() - Ends");
    return message;
  }

  @Override
  public String deleteQuestionnaireStep(
      Integer stepId,
      Integer questionnaireId,
      String stepType,
      SessionObject sessionObject,
      String customStudyId) {
    logger.entry("begin deleteQuestionnaireStep()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message =
          studyQuestionnaireDAO.deleteQuestionnaireStep(
              stepId, questionnaireId, stepType, sessionObject, customStudyId);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - deleteQuestionnaireStep - Error", e);
    }
    logger.exit("deleteQuestionnaireStep() - Ends");
    return message;
  }

  @Override
  public String deletQuestionnaire(
      Integer studyId, Integer questionnaireId, SessionObject sessionObject, String customStudyId) {
    logger.entry("begin deletQuestionnaire");
    return studyQuestionnaireDAO.deleteQuestuionnaireInfo(
        studyId, questionnaireId, sessionObject, customStudyId);
  }

  @Override
  public List<HealthKitKeysInfo> getHeanlthKitKeyInfoList() {
    logger.entry("begin getHeanlthKitKeyInfoList");
    return studyQuestionnaireDAO.getHeanlthKitKeyInfoList();
  }

  @Override
  public InstructionsBo getInstructionsBo(
      Integer instructionId,
      String questionnaireShortTitle,
      String customStudyId,
      Integer questionnaireId) {
    logger.entry("begin getInstructionsBo");
    InstructionsBo instructionsBo = null;
    try {
      instructionsBo =
          studyQuestionnaireDAO.getInstructionsBo(
              instructionId, questionnaireShortTitle, customStudyId, questionnaireId);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getInstructionsBo - ERROR ", e);
    }
    logger.exit("getInstructionsBo - Ends");
    return instructionsBo;
  }

  @Override
  public QuestionnaireBo getQuestionnaireById(Integer questionnaireId, String customStudyId) {
    logger.entry("begin getQuestionnaireById");
    QuestionnaireBo questionnaireBo = null;
    try {
      questionnaireBo = studyQuestionnaireDAO.getQuestionnaireById(questionnaireId, customStudyId);
      if (null != questionnaireBo) {
        if ((questionnaireBo.getStudyLifetimeStart() != null)
            && !questionnaireBo.getStudyLifetimeStart().isEmpty()) {
          questionnaireBo.setStudyLifetimeStart(
              FdahpStudyDesignerUtil.getFormattedDate(
                  questionnaireBo.getStudyLifetimeStart(),
                  FdahpStudyDesignerConstants.DB_SDF_DATE,
                  FdahpStudyDesignerConstants.UI_SDF_DATE));
        }
        if ((questionnaireBo.getStudyLifetimeEnd() != null)
            && !questionnaireBo.getStudyLifetimeEnd().isEmpty()) {
          questionnaireBo.setStudyLifetimeEnd(
              FdahpStudyDesignerUtil.getFormattedDate(
                  questionnaireBo.getStudyLifetimeEnd(),
                  FdahpStudyDesignerConstants.DB_SDF_DATE,
                  FdahpStudyDesignerConstants.UI_SDF_DATE));
        }
        if ((questionnaireBo.getQuestionnairesFrequenciesBo() != null)
            && (questionnaireBo.getQuestionnairesFrequenciesBo().getFrequencyDate() != null)) {

          questionnaireBo
              .getQuestionnairesFrequenciesBo()
              .setFrequencyDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionnaireBo.getQuestionnairesFrequenciesBo().getFrequencyDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
        }
        if ((questionnaireBo.getQuestionnairesFrequenciesBo() != null)
            && StringUtils.isNotBlank(
                questionnaireBo.getQuestionnairesFrequenciesBo().getFrequencyTime())) {
          questionnaireBo
              .getQuestionnairesFrequenciesBo()
              .setFrequencyTime(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionnaireBo.getQuestionnairesFrequenciesBo().getFrequencyTime(),
                      FdahpStudyDesignerConstants.UI_SDF_TIME,
                      FdahpStudyDesignerConstants.SDF_TIME));
        }
        if ((questionnaireBo.getQuestionnairesFrequenciesList() != null)
            && !questionnaireBo.getQuestionnairesFrequenciesList().isEmpty()) {
          for (QuestionnairesFrequenciesBo questionnairesFrequenciesBo :
              questionnaireBo.getQuestionnairesFrequenciesList()) {
            if (questionnairesFrequenciesBo.getFrequencyDate() != null) {
              questionnairesFrequenciesBo.setFrequencyDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionnairesFrequenciesBo.getFrequencyDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
            }
            if (StringUtils.isNotBlank(questionnairesFrequenciesBo.getFrequencyTime())) {
              questionnairesFrequenciesBo.setFrequencyTime(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionnairesFrequenciesBo.getFrequencyTime(),
                      FdahpStudyDesignerConstants.UI_SDF_TIME,
                      FdahpStudyDesignerConstants.SDF_TIME));
            }
          }
        }
        if ((questionnaireBo.getQuestionnaireCustomScheduleBo() != null)
            && !questionnaireBo.getQuestionnaireCustomScheduleBo().isEmpty()) {
          for (QuestionnaireCustomScheduleBo questionnaireCustomScheduleBo :
              questionnaireBo.getQuestionnaireCustomScheduleBo()) {
            if (questionnaireCustomScheduleBo.getFrequencyStartDate() != null) {
              questionnaireCustomScheduleBo.setFrequencyStartDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionnaireCustomScheduleBo.getFrequencyStartDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
            }
            if (questionnaireCustomScheduleBo.getFrequencyEndDate() != null) {
              questionnaireCustomScheduleBo.setFrequencyEndDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionnaireCustomScheduleBo.getFrequencyEndDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
            }
            if (StringUtils.isNotBlank(questionnaireCustomScheduleBo.getFrequencyEndTime())) {
              questionnaireCustomScheduleBo.setFrequencyEndTime(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionnaireCustomScheduleBo.getFrequencyEndTime(),
                      FdahpStudyDesignerConstants.UI_SDF_TIME,
                      FdahpStudyDesignerConstants.SDF_TIME));
            }
            if (StringUtils.isNotBlank(questionnaireCustomScheduleBo.getFrequencyStartTime())) {
              questionnaireCustomScheduleBo.setFrequencyStartTime(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionnaireCustomScheduleBo.getFrequencyStartTime(),
                      FdahpStudyDesignerConstants.UI_SDF_TIME,
                      FdahpStudyDesignerConstants.SDF_TIME));
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getQuestionnaireById - Error", e);
    }
    logger.exit("getQuestionnaireById - Ends");
    return questionnaireBo;
  }

  @Override
  public List<QuestionnairesStepsBo> getQuestionnairesStepsList(
      Integer questionnaireId, Integer sequenceNo) {
    logger.entry("begin getQuestionnairesStepsList()");
    List<QuestionnairesStepsBo> questionnairesStepsList = null;
    try {
      questionnairesStepsList =
          studyQuestionnaireDAO.getQuestionnairesStepsList(questionnaireId, sequenceNo);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getQuestionnairesStepsList - Error", e);
    }
    logger.exit("getQuestionnairesStepsList() - Ends");
    return questionnairesStepsList;
  }

  @Override
  public QuestionnairesStepsBo getQuestionnaireStep(
      Integer stepId,
      String stepType,
      String questionnaireShortTitle,
      String customStudyId,
      Integer questionnaireId) {
    logger.entry("begin getQuestionnaireStep()");
    QuestionnairesStepsBo questionnairesStepsBo = null;
    try {
      questionnairesStepsBo =
          studyQuestionnaireDAO.getQuestionnaireStep(
              stepId, stepType, questionnaireShortTitle, customStudyId, questionnaireId);
      if ((questionnairesStepsBo != null)
          && stepType.equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)
          && (questionnairesStepsBo.getFormQuestionMap() != null)) {
        List<QuestionResponseTypeMasterInfoBo> questionResponseTypeMasterInfoList =
            studyQuestionnaireDAO.getQuestionReponseTypeList();
        if ((questionResponseTypeMasterInfoList != null)
            && !questionResponseTypeMasterInfoList.isEmpty()) {
          for (QuestionResponseTypeMasterInfoBo questionResponseTypeMasterInfoBo :
              questionResponseTypeMasterInfoList) {
            for (Entry<Integer, QuestionnaireStepBean> entry :
                questionnairesStepsBo.getFormQuestionMap().entrySet()) {
              QuestionnaireStepBean questionnaireStepBean = entry.getValue();
              if ((questionnaireStepBean.getResponseType() != null)
                  && questionnaireStepBean
                      .getResponseType()
                      .equals(questionResponseTypeMasterInfoBo.getId())) {
                if (FdahpStudyDesignerConstants.DATE.equalsIgnoreCase(
                    questionResponseTypeMasterInfoBo.getResponseType())) {
                  questionnaireStepBean.setResponseTypeText(
                      questionResponseTypeMasterInfoBo.getResponseType());
                } else {
                  questionnaireStepBean.setResponseTypeText(
                      questionResponseTypeMasterInfoBo.getDataType());
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getQuestionnaireStep - Error", e);
    }
    logger.exit("getQuestionnaireStep() - Ends");
    return questionnairesStepsBo;
  }

  @Override
  public SortedMap<Integer, QuestionnaireStepBean> getQuestionnaireStepList(
      Integer questionnaireId) {
    logger.entry("begin getQuestionnaireStepList()");
    SortedMap<Integer, QuestionnaireStepBean> questionnaireStepMap = null;
    try {
      questionnaireStepMap = studyQuestionnaireDAO.getQuestionnaireStepList(questionnaireId);
      if (questionnaireStepMap != null) {
        List<QuestionResponseTypeMasterInfoBo> questionResponseTypeMasterInfoList =
            studyQuestionnaireDAO.getQuestionReponseTypeList();
        if ((questionResponseTypeMasterInfoList != null)
            && !questionResponseTypeMasterInfoList.isEmpty()) {
          for (QuestionResponseTypeMasterInfoBo questionResponseTypeMasterInfoBo :
              questionResponseTypeMasterInfoList) {
            for (Entry<Integer, QuestionnaireStepBean> entry : questionnaireStepMap.entrySet()) {
              QuestionnaireStepBean questionnaireStepBean = entry.getValue();
              if (questionResponseTypeMasterInfoBo
                  .getId()
                  .equals(questionnaireStepBean.getResponseType())) {
                if (FdahpStudyDesignerConstants.DATE.equalsIgnoreCase(
                    questionResponseTypeMasterInfoBo.getResponseType())) {
                  questionnaireStepBean.setResponseTypeText(
                      questionResponseTypeMasterInfoBo.getResponseType());
                } else {
                  questionnaireStepBean.setResponseTypeText(
                      questionResponseTypeMasterInfoBo.getDataType());
                }
              }
              if (entry.getValue().getFromMap() != null) {
                for (Entry<Integer, QuestionnaireStepBean> entryKey :
                    entry.getValue().getFromMap().entrySet()) {
                  if (questionResponseTypeMasterInfoBo
                      .getId()
                      .equals(entryKey.getValue().getResponseType())) {
                    if (FdahpStudyDesignerConstants.DATE.equalsIgnoreCase(
                        questionResponseTypeMasterInfoBo.getResponseType())) {
                      questionnaireStepBean.setResponseTypeText(
                          questionResponseTypeMasterInfoBo.getResponseType());
                    } else {
                      questionnaireStepBean.setResponseTypeText(
                          questionResponseTypeMasterInfoBo.getDataType());
                    }
                  }
                }
              }
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getQuestionnaireStepList - Error", e);
    }
    logger.exit("getQuestionnaireStepList() - Ends");
    return questionnaireStepMap;
  }

  @Override
  public List<QuestionResponseTypeMasterInfoBo> getQuestionReponseTypeList() {
    logger.entry("begin getQuestionReponseTypeList()");
    List<QuestionResponseTypeMasterInfoBo> questionResponseTypeMasterInfoList = null;
    try {
      questionResponseTypeMasterInfoList = studyQuestionnaireDAO.getQuestionReponseTypeList();
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getQuestionReponseTypeList - Error", e);
    }
    logger.exit("getQuestionReponseTypeList() - Ends");
    return questionResponseTypeMasterInfoList;
  }

  @Override
  public QuestionsBo getQuestionsById(
      Integer questionId, String questionnaireShortTitle, String customStudyId) {
    logger.entry("begin getQuestionsById()");
    QuestionsBo questionsBo = null;
    try {
      questionsBo =
          studyQuestionnaireDAO.getQuestionsById(
              questionId, questionnaireShortTitle, customStudyId);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getQuestionsById - Error", e);
    }
    logger.exit("getQuestionsById() - Ends");
    return questionsBo;
  }

  @Override
  public List<QuestionnaireBo> getStudyQuestionnairesByStudyId(String studyId, Boolean isLive) {
    logger.entry("begin getStudyQuestionnairesByStudyId()");
    List<QuestionnaireBo> questionnaires = null;
    try {
      questionnaires = studyQuestionnaireDAO.getStudyQuestionnairesByStudyId(studyId, isLive);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getStudyQuestionnairesByStudyId() - ERROR ", e);
    }
    logger.exit("getStudyQuestionnairesByStudyId() - Ends");
    return questionnaires;
  }

  @Override
  public Boolean isAnchorDateExistsForStudy(Integer studyId, String customStudyId) {
    logger.entry("begin isAnchorDateExistsForStudy");
    return studyQuestionnaireDAO.isAnchorDateExistsForStudy(studyId, customStudyId);
  }

  @Override
  public Boolean isQuestionnairesCompleted(Integer studyId) {
    logger.entry("begin isAnchorDateExistsForStudy");
    return studyQuestionnaireDAO.isQuestionnairesCompleted(studyId);
  }

  @Override
  public String reOrderFormStepQuestions(Integer formId, int oldOrderNumber, int newOrderNumber) {
    logger.entry("begin reOrderFormStepQuestions()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message =
          studyQuestionnaireDAO.reOrderFormStepQuestions(formId, oldOrderNumber, newOrderNumber);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - reOrderFormStepQuestions - Error", e);
    }
    logger.exit("reOrderFormStepQuestions() - Ends");
    return message;
  }

  @Override
  public String reOrderQuestionnaireSteps(
      Integer questionnaireId, int oldOrderNumber, int newOrderNumber) {
    logger.entry("begin reOrderQuestionnaireSteps");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message =
          studyQuestionnaireDAO.reOrderQuestionnaireSteps(
              questionnaireId, oldOrderNumber, newOrderNumber);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - reOrderQuestionnaireSteps - Error", e);
    }
    logger.exit("reOrderQuestionnaireSteps() - Ends");
    return message;
  }

  @Override
  public QuestionnairesStepsBo saveOrUpdateFromStepQuestionnaire(
      QuestionnairesStepsBo questionnairesStepsBo, SessionObject sesObj, String customStudyId) {
    logger.entry("begin saveOrUpdateFromStepQuestionnaire()");
    QuestionnairesStepsBo addOrUpdateQuestionnairesStepsBo = null;
    try {
      addOrUpdateQuestionnairesStepsBo =
          studyQuestionnaireDAO.saveOrUpdateFromQuestionnaireStep(
              questionnairesStepsBo, sesObj, customStudyId);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - saveOrUpdateFromStepQuestionnaire - Error", e);
    }
    logger.exit("saveOrUpdateFromStepQuestionnaire() - Ends");
    return addOrUpdateQuestionnairesStepsBo;
  }

  @Override
  public InstructionsBo saveOrUpdateInstructionsBo(
      InstructionsBo instructionsBo, SessionObject sessionObject, String customStudyId) {
    logger.entry("begin saveOrUpdateInstructionsBo()");
    InstructionsBo addOrUpdateInstructionsBo = null;
    try {
      if (null != instructionsBo) {
        if (instructionsBo.getId() != null) {
          addOrUpdateInstructionsBo =
              studyQuestionnaireDAO.getInstructionsBo(
                  instructionsBo.getId(), "", customStudyId, instructionsBo.getQuestionnaireId());
        } else {
          addOrUpdateInstructionsBo = new InstructionsBo();
          addOrUpdateInstructionsBo.setActive(true);
        }
        if ((instructionsBo.getInstructionText() != null)
            && !instructionsBo.getInstructionText().isEmpty()) {
          addOrUpdateInstructionsBo.setInstructionText(instructionsBo.getInstructionText());
        }
        if ((instructionsBo.getInstructionTitle() != null)
            && !instructionsBo.getInstructionTitle().isEmpty()) {
          addOrUpdateInstructionsBo.setInstructionTitle(instructionsBo.getInstructionTitle());
        }
        if ((instructionsBo.getCreatedOn() != null) && !instructionsBo.getCreatedOn().isEmpty()) {
          addOrUpdateInstructionsBo.setCreatedOn(instructionsBo.getCreatedOn());
        }
        if (instructionsBo.getCreatedBy() != null) {
          addOrUpdateInstructionsBo.setCreatedBy(instructionsBo.getCreatedBy());
        }
        if ((instructionsBo.getModifiedOn() != null) && !instructionsBo.getModifiedOn().isEmpty()) {
          addOrUpdateInstructionsBo.setModifiedOn(instructionsBo.getModifiedOn());
        }
        if (instructionsBo.getModifiedBy() != null) {
          addOrUpdateInstructionsBo.setModifiedBy(instructionsBo.getModifiedBy());
        }
        if (instructionsBo.getQuestionnaireId() != null) {
          addOrUpdateInstructionsBo.setQuestionnaireId(instructionsBo.getQuestionnaireId());
        }
        if (instructionsBo.getQuestionnairesStepsBo() != null) {
          addOrUpdateInstructionsBo.setQuestionnairesStepsBo(
              instructionsBo.getQuestionnairesStepsBo());
        }
        if ((instructionsBo.getType() != null) && !instructionsBo.getType().isEmpty()) {
          addOrUpdateInstructionsBo.setType(instructionsBo.getType());
          if (instructionsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
            addOrUpdateInstructionsBo.setStatus(false);
          } else if (instructionsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
            addOrUpdateInstructionsBo.setStatus(true);
          }
        }
        addOrUpdateInstructionsBo =
            studyQuestionnaireDAO.saveOrUpdateInstructionsBo(
                addOrUpdateInstructionsBo, sessionObject, customStudyId);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - saveOrUpdateInstructionsBo - Error", e);
    }
    logger.exit("saveOrUpdateInstructionsBo() - Ends");
    return addOrUpdateInstructionsBo;
  }

  @Override
  public QuestionsBo saveOrUpdateQuestion(
      QuestionsBo questionsBo, SessionObject sesObj, String customStudyId) {
    logger.entry("begin saveOrUpdateQuestion()");
    QuestionsBo addQuestionsBo = null;
    try {
      if (null != questionsBo) {
        if (questionsBo.getId() != null) {
          addQuestionsBo =
              studyQuestionnaireDAO.getQuestionsById(questionsBo.getId(), null, customStudyId);
        } else {
          addQuestionsBo = new QuestionsBo();
          addQuestionsBo.setActive(true);
        }
        if (questionsBo.getShortTitle() != null) {
          addQuestionsBo.setShortTitle(questionsBo.getShortTitle());
        }
        if (questionsBo.getQuestion() != null) {
          addQuestionsBo.setQuestion(questionsBo.getQuestion());
        }
        addQuestionsBo.setDescription(questionsBo.getDescription());
        if (questionsBo.getSkippable() != null) {
          addQuestionsBo.setSkippable(questionsBo.getSkippable());
        }
        if (questionsBo.getAddLineChart() != null) {
          addQuestionsBo.setAddLineChart(questionsBo.getAddLineChart());
        }
        if (questionsBo.getLineChartTimeRange() != null) {
          addQuestionsBo.setLineChartTimeRange(questionsBo.getLineChartTimeRange());
        }
        if (questionsBo.getAllowRollbackChart() != null) {
          addQuestionsBo.setAllowRollbackChart(questionsBo.getAllowRollbackChart());
        }
        if (questionsBo.getChartTitle() != null) {
          addQuestionsBo.setChartTitle(questionsBo.getChartTitle());
        }
        if (questionsBo.getUseStasticData() != null) {
          addQuestionsBo.setUseStasticData(questionsBo.getUseStasticData());
        }
        if (questionsBo.getStatShortName() != null) {
          addQuestionsBo.setStatShortName(questionsBo.getStatShortName());
        }
        if (questionsBo.getStatDisplayName() != null) {
          addQuestionsBo.setStatDisplayName(questionsBo.getStatDisplayName());
        }
        if (questionsBo.getStatDisplayUnits() != null) {
          addQuestionsBo.setStatDisplayUnits(questionsBo.getStatDisplayUnits());
        }
        if (questionsBo.getStatType() != null) {
          addQuestionsBo.setStatType(questionsBo.getStatType());
        }
        if (questionsBo.getStatFormula() != null) {
          addQuestionsBo.setStatFormula(questionsBo.getStatFormula());
        }
        if (questionsBo.getResponseType() != null) {
          addQuestionsBo.setResponseType(questionsBo.getResponseType());
        }
        if (questionsBo.getCreatedOn() != null) {
          addQuestionsBo.setCreatedOn(questionsBo.getCreatedOn());
        }
        if (questionsBo.getCreatedBy() != null) {
          addQuestionsBo.setCreatedBy(questionsBo.getCreatedBy());
        }
        if (questionsBo.getModifiedOn() != null) {
          addQuestionsBo.setModifiedOn(questionsBo.getModifiedOn());
        }
        if (questionsBo.getModifiedBy() != null) {
          addQuestionsBo.setModifiedBy(questionsBo.getModifiedBy());
        }
        if (questionsBo.getQuestionReponseTypeBo() != null) {
          addQuestionsBo.setQuestionReponseTypeBo(questionsBo.getQuestionReponseTypeBo());
        }
        if (questionsBo.getQuestionResponseSubTypeList() != null) {
          addQuestionsBo.setQuestionResponseSubTypeList(
              questionsBo.getQuestionResponseSubTypeList());
        }
        if (questionsBo.getFromId() != null) {
          addQuestionsBo.setFromId(questionsBo.getFromId());
        }
        if (questionsBo.getUseAnchorDate() != null) {
          addQuestionsBo.setUseAnchorDate(questionsBo.getUseAnchorDate());
          addQuestionsBo.setAnchorDateName(questionsBo.getAnchorDateName());
          if (questionsBo.getAnchorDateId() != null) {
            addQuestionsBo.setAnchorDateId(questionsBo.getAnchorDateId());
          }
        }
        if (questionsBo.getQuestionnaireId() != null) {
          addQuestionsBo.setQuestionnaireId(questionsBo.getQuestionnaireId());
        }
        if (questionsBo.getAllowHealthKit() != null) {
          addQuestionsBo.setAllowHealthKit(questionsBo.getAllowHealthKit());
        }
        if (questionsBo.getHealthkitDatatype() != null) {
          addQuestionsBo.setHealthkitDatatype(questionsBo.getHealthkitDatatype());
        }
        if (questionsBo.getType() != null) {
          if (questionsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
            addQuestionsBo.setStatus(false);
          } else if (questionsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
            addQuestionsBo.setStatus(true);
          }
        }
        if ((questionsBo.getIsShorTitleDuplicate() != null)
            && (questionsBo.getIsShorTitleDuplicate() > 0)) {
          addQuestionsBo.setIsShorTitleDuplicate(questionsBo.getIsShorTitleDuplicate());
        }

        addQuestionsBo.setCustomStudyId(customStudyId);
        addQuestionsBo = studyQuestionnaireDAO.saveOrUpdateQuestion(addQuestionsBo);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - saveOrUpdateQuestion - Error", e);
    }
    logger.exit("saveOrUpdateQuestion() - Ends");
    return addQuestionsBo;
  }

  @Override
  public QuestionnaireBo saveOrUpdateQuestionnaire(
      QuestionnaireBo questionnaireBo, SessionObject sessionObject, String customStudyId) {
    logger.entry("begin saveORUpdateQuestionnaire()");
    QuestionnaireBo addQuestionnaireBo = null;
    try {
      if (null != questionnaireBo) {
        if (questionnaireBo.getId() != null) {
          addQuestionnaireBo =
              studyQuestionnaireDAO.getQuestionnaireById(questionnaireBo.getId(), customStudyId);
        } else {
          addQuestionnaireBo = new QuestionnaireBo();
          addQuestionnaireBo.setActive(true);
        }
        if (questionnaireBo.getStudyId() != null) {
          addQuestionnaireBo.setStudyId(questionnaireBo.getStudyId());
        }
        if (questionnaireBo.getFrequency() != null) {
          addQuestionnaireBo.setFrequency(questionnaireBo.getFrequency());
        }
        if (questionnaireBo.getScheduleType() != null) {
          addQuestionnaireBo.setScheduleType(questionnaireBo.getScheduleType());
        }
        if (questionnaireBo.getAnchorDateId() != null) {
          addQuestionnaireBo.setAnchorDateId(questionnaireBo.getAnchorDateId());
        }
        if ((questionnaireBo.getFrequency() != null)
            && !questionnaireBo
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
          if (StringUtils.isNotBlank(questionnaireBo.getStudyLifetimeStart())
              && !("NA").equalsIgnoreCase(questionnaireBo.getStudyLifetimeStart())
              && !questionnaireBo.getStudyLifetimeStart().isEmpty()) {
            addQuestionnaireBo.setStudyLifetimeStart(
                FdahpStudyDesignerUtil.getFormattedDate(
                    questionnaireBo.getStudyLifetimeStart(),
                    FdahpStudyDesignerConstants.UI_SDF_DATE,
                    FdahpStudyDesignerConstants.DB_SDF_DATE));
            if (questionnaireBo.getAnchorDateId() != null) {
              addQuestionnaireBo.setAnchorDateId(questionnaireBo.getAnchorDateId());
            }
          } else {
            addQuestionnaireBo.setStudyLifetimeStart(null);
          }
          if (StringUtils.isNotBlank(questionnaireBo.getStudyLifetimeEnd())
              && !("NA").equalsIgnoreCase(questionnaireBo.getStudyLifetimeEnd())) {
            addQuestionnaireBo.setStudyLifetimeEnd(
                FdahpStudyDesignerUtil.getFormattedDate(
                    questionnaireBo.getStudyLifetimeEnd(),
                    FdahpStudyDesignerConstants.UI_SDF_DATE,
                    FdahpStudyDesignerConstants.DB_SDF_DATE));
          } else {
            addQuestionnaireBo.setStudyLifetimeEnd(null);
          }
        }
        if (questionnaireBo.getTitle() != null) {
          addQuestionnaireBo.setTitle(questionnaireBo.getTitle());
        }
        if (questionnaireBo.getShortTitle() != null) {
          addQuestionnaireBo.setShortTitle(questionnaireBo.getShortTitle());
        }
        if (questionnaireBo.getCreatedDate() != null) {
          addQuestionnaireBo.setCreatedDate(questionnaireBo.getCreatedDate());
        }
        if (questionnaireBo.getCreatedBy() != null) {
          addQuestionnaireBo.setCreatedBy(questionnaireBo.getCreatedBy());
        }
        if (questionnaireBo.getModifiedDate() != null) {
          addQuestionnaireBo.setModifiedDate(questionnaireBo.getModifiedDate());
        }
        if (questionnaireBo.getModifiedBy() != null) {
          addQuestionnaireBo.setModifiedBy(questionnaireBo.getModifiedBy());
        }
        addQuestionnaireBo.setRepeatQuestionnaire(questionnaireBo.getRepeatQuestionnaire());
        if (questionnaireBo.getDayOfTheWeek() != null) {
          addQuestionnaireBo.setDayOfTheWeek(questionnaireBo.getDayOfTheWeek());
        }
        if (questionnaireBo.getType() != null) {
          addQuestionnaireBo.setType(questionnaireBo.getType());
        }
        if (questionnaireBo.getBranching() != null) {
          addQuestionnaireBo.setBranching(questionnaireBo.getBranching());
        }
        if (questionnaireBo.getStatus() != null) {
          addQuestionnaireBo.setStatus(questionnaireBo.getStatus());
          if (questionnaireBo.getStatus()) {
            questionnaireBo.setIsChange(1);
          } else {
            questionnaireBo.setIsChange(0);
          }
        }
        if (questionnaireBo.getFrequency() != null) {
          if (!questionnaireBo
              .getFrequency()
              .equalsIgnoreCase(questionnaireBo.getPreviousFrequency())) {
            addQuestionnaireBo.setQuestionnaireCustomScheduleBo(
                questionnaireBo.getQuestionnaireCustomScheduleBo());
            addQuestionnaireBo.setQuestionnairesFrequenciesList(
                questionnaireBo.getQuestionnairesFrequenciesList());
            if (questionnaireBo
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
              if (questionnaireBo.getQuestionnairesFrequenciesBo() != null) {
                if (!questionnaireBo.getQuestionnairesFrequenciesBo().getIsLaunchStudy()) {
                  if (StringUtils.isNotBlank(questionnaireBo.getStudyLifetimeStart())
                      && !("NA").equalsIgnoreCase(questionnaireBo.getStudyLifetimeStart())
                      && !questionnaireBo.getStudyLifetimeStart().isEmpty()) {
                    addQuestionnaireBo.setStudyLifetimeStart(
                        FdahpStudyDesignerUtil.getFormattedDate(
                            questionnaireBo.getStudyLifetimeStart(),
                            FdahpStudyDesignerConstants.UI_SDF_DATE,
                            FdahpStudyDesignerConstants.DB_SDF_DATE));
                  }
                }
                if (!questionnaireBo.getQuestionnairesFrequenciesBo().getIsStudyLifeTime()) {
                  if (StringUtils.isNotBlank(questionnaireBo.getStudyLifetimeEnd())
                      && !("NA").equalsIgnoreCase(questionnaireBo.getStudyLifetimeEnd())) {
                    addQuestionnaireBo.setStudyLifetimeEnd(
                        FdahpStudyDesignerUtil.getFormattedDate(
                            questionnaireBo.getStudyLifetimeEnd(),
                            FdahpStudyDesignerConstants.UI_SDF_DATE,
                            FdahpStudyDesignerConstants.DB_SDF_DATE));
                  } else {
                    addQuestionnaireBo.setStudyLifetimeEnd(null);
                  }
                }
              }
            }
            addQuestionnaireBo.setQuestionnairesFrequenciesBo(
                questionnaireBo.getQuestionnairesFrequenciesBo());
          } else {
            if ((questionnaireBo.getQuestionnaireCustomScheduleBo() != null)
                && !questionnaireBo.getQuestionnaireCustomScheduleBo().isEmpty()) {
              addQuestionnaireBo.setQuestionnaireCustomScheduleBo(
                  questionnaireBo.getQuestionnaireCustomScheduleBo());
            }
            if ((questionnaireBo.getQuestionnairesFrequenciesList() != null)
                && !questionnaireBo.getQuestionnairesFrequenciesList().isEmpty()) {
              addQuestionnaireBo.setQuestionnairesFrequenciesList(
                  questionnaireBo.getQuestionnairesFrequenciesList());
            }
            if (questionnaireBo.getQuestionnairesFrequenciesBo() != null) {
              if (questionnaireBo
                  .getFrequency()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
                if (!questionnaireBo.getQuestionnairesFrequenciesBo().getIsLaunchStudy()) {
                  if (StringUtils.isNotBlank(questionnaireBo.getStudyLifetimeStart())
                      && !("NA").equalsIgnoreCase(questionnaireBo.getStudyLifetimeStart())
                      && !questionnaireBo.getStudyLifetimeStart().isEmpty()) {
                    addQuestionnaireBo.setStudyLifetimeStart(
                        FdahpStudyDesignerUtil.getFormattedDate(
                            questionnaireBo.getStudyLifetimeStart(),
                            FdahpStudyDesignerConstants.UI_SDF_DATE,
                            FdahpStudyDesignerConstants.DB_SDF_DATE));
                  }
                }
                if (!questionnaireBo.getQuestionnairesFrequenciesBo().getIsStudyLifeTime()) {
                  if (StringUtils.isNotBlank(questionnaireBo.getStudyLifetimeEnd())
                      && !("NA").equalsIgnoreCase(questionnaireBo.getStudyLifetimeEnd())) {
                    addQuestionnaireBo.setStudyLifetimeEnd(
                        FdahpStudyDesignerUtil.getFormattedDate(
                            questionnaireBo.getStudyLifetimeEnd(),
                            FdahpStudyDesignerConstants.UI_SDF_DATE,
                            FdahpStudyDesignerConstants.DB_SDF_DATE));
                  }
                }
              }
              addQuestionnaireBo.setQuestionnairesFrequenciesBo(
                  questionnaireBo.getQuestionnairesFrequenciesBo());
            }
          }
        }
        if (questionnaireBo.getPreviousFrequency() != null) {
          addQuestionnaireBo.setPreviousFrequency(questionnaireBo.getPreviousFrequency());
        }
        if (questionnaireBo.getCurrentFrequency() != null) {
          addQuestionnaireBo.setCurrentFrequency(questionnaireBo.getCurrentFrequency());
        }
        addQuestionnaireBo.setIsChange(questionnaireBo.getIsChange());
        addQuestionnaireBo =
            studyQuestionnaireDAO.saveORUpdateQuestionnaire(
                addQuestionnaireBo, sessionObject, customStudyId);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - saveORUpdateQuestionnaire - Error", e);
    }
    logger.exit("saveORUpdateQuestionnaire() - Ends");
    return addQuestionnaireBo;
  }

  @Override
  public QuestionnaireBo saveOrUpdateQuestionnaireSchedule(
      QuestionnaireBo questionnaireBo, SessionObject sessionObject, String customStudyId) {
    logger.entry("begin saveOrUpdateQuestionnaireSchedule()");
    QuestionnaireBo addQuestionnaireBo = null;
    try {
      addQuestionnaireBo =
          studyQuestionnaireDAO.saveORUpdateQuestionnaire(
              questionnaireBo, sessionObject, customStudyId);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - saveOrUpdateQuestionnaireSchedule - Error", e);
    }
    logger.exit("saveOrUpdateQuestionnaireSchedule() - Ends");
    return addQuestionnaireBo;
  }

  @Override
  public QuestionnairesStepsBo saveOrUpdateQuestionStep(
      QuestionnairesStepsBo questionnairesStepsBo,
      SessionObject sessionObject,
      String customStudyId) {
    logger.entry("begin saveOrUpdateQuestionStep()");
    QuestionnairesStepsBo addOrUpdateQuestionnairesStepsBo = null;
    try {
      QuestionsBo addQuestionsBo = null;
      if ((questionnairesStepsBo != null) && (questionnairesStepsBo.getQuestionsBo() != null)) {
        if (questionnairesStepsBo.getQuestionsBo().getId() != null) {
          addQuestionsBo =
              studyQuestionnaireDAO.getQuestionsById(
                  questionnairesStepsBo.getQuestionsBo().getId(), null, customStudyId);
          if (questionnairesStepsBo.getModifiedOn() != null) {
            addQuestionsBo.setModifiedOn(questionnairesStepsBo.getModifiedOn());
          }
          if (questionnairesStepsBo.getModifiedBy() != null) {
            addQuestionsBo.setModifiedBy(questionnairesStepsBo.getModifiedBy());
          }
        } else {
          addQuestionsBo = new QuestionsBo();
          if (questionnairesStepsBo.getCreatedOn() != null) {
            addQuestionsBo.setCreatedOn(questionnairesStepsBo.getCreatedOn());
          }
          if (questionnairesStepsBo.getCreatedBy() != null) {
            addQuestionsBo.setCreatedBy(questionnairesStepsBo.getCreatedBy());
          }
          addQuestionsBo.setActive(true);
        }
        if (questionnairesStepsBo.getQuestionsBo().getQuestion() != null) {
          addQuestionsBo.setQuestion(questionnairesStepsBo.getQuestionsBo().getQuestion());
        }
        addQuestionsBo.setDescription(questionnairesStepsBo.getQuestionsBo().getDescription());
        if (questionnairesStepsBo.getQuestionsBo().getSkippable() != null) {
          addQuestionsBo.setSkippable(questionnairesStepsBo.getQuestionsBo().getSkippable());
        }
        if (questionnairesStepsBo.getQuestionsBo().getAddLineChart() != null) {
          addQuestionsBo.setAddLineChart(questionnairesStepsBo.getQuestionsBo().getAddLineChart());
        }
        if (questionnairesStepsBo.getQuestionsBo().getLineChartTimeRange() != null) {
          addQuestionsBo.setLineChartTimeRange(
              questionnairesStepsBo.getQuestionsBo().getLineChartTimeRange());
        }
        if (questionnairesStepsBo.getQuestionsBo().getAllowRollbackChart() != null) {
          addQuestionsBo.setAllowRollbackChart(
              questionnairesStepsBo.getQuestionsBo().getAllowRollbackChart());
        }
        if (questionnairesStepsBo.getQuestionsBo().getChartTitle() != null) {
          addQuestionsBo.setChartTitle(questionnairesStepsBo.getQuestionsBo().getChartTitle());
        }
        if (questionnairesStepsBo.getQuestionsBo().getUseStasticData() != null) {
          addQuestionsBo.setUseStasticData(
              questionnairesStepsBo.getQuestionsBo().getUseStasticData());
        }
        if (questionnairesStepsBo.getQuestionsBo().getStatShortName() != null) {
          addQuestionsBo.setStatShortName(
              questionnairesStepsBo.getQuestionsBo().getStatShortName());
        }
        if (questionnairesStepsBo.getQuestionsBo().getStatDisplayName() != null) {
          addQuestionsBo.setStatDisplayName(
              questionnairesStepsBo.getQuestionsBo().getStatDisplayName());
        }
        if (questionnairesStepsBo.getQuestionsBo().getStatDisplayUnits() != null) {
          addQuestionsBo.setStatDisplayUnits(
              questionnairesStepsBo.getQuestionsBo().getStatDisplayUnits());
        }
        if (questionnairesStepsBo.getQuestionsBo().getStatType() != null) {
          addQuestionsBo.setStatType(questionnairesStepsBo.getQuestionsBo().getStatType());
        }
        if (questionnairesStepsBo.getQuestionsBo().getStatFormula() != null) {
          addQuestionsBo.setStatFormula(questionnairesStepsBo.getQuestionsBo().getStatFormula());
        }
        if (questionnairesStepsBo.getQuestionsBo().getResponseType() != null) {
          addQuestionsBo.setResponseType(questionnairesStepsBo.getQuestionsBo().getResponseType());
        }
        if (questionnairesStepsBo.getQuestionsBo().getUseAnchorDate() != null) {
          addQuestionsBo.setUseAnchorDate(
              questionnairesStepsBo.getQuestionsBo().getUseAnchorDate());
          if (StringUtils.isNotEmpty(questionnairesStepsBo.getQuestionsBo().getAnchorDateName())) {
            addQuestionsBo.setAnchorDateName(
                questionnairesStepsBo.getQuestionsBo().getAnchorDateName());
          }
          if (questionnairesStepsBo.getQuestionsBo().getAnchorDateId() != null) {
            addQuestionsBo.setAnchorDateId(
                questionnairesStepsBo.getQuestionsBo().getAnchorDateId());
          }
        }
        if (questionnairesStepsBo.getQuestionsBo().getAllowHealthKit() != null) {
          addQuestionsBo.setAllowHealthKit(
              questionnairesStepsBo.getQuestionsBo().getAllowHealthKit());
        }
        if (questionnairesStepsBo.getQuestionsBo().getHealthkitDatatype() != null) {
          addQuestionsBo.setHealthkitDatatype(
              questionnairesStepsBo.getQuestionsBo().getHealthkitDatatype());
        }
        if (questionnairesStepsBo.getType() != null) {
          if (questionnairesStepsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
            addQuestionsBo.setStatus(false);
          } else if (questionnairesStepsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
            addQuestionsBo.setStatus(true);
          }
        }

        questionnairesStepsBo.setQuestionsBo(addQuestionsBo);
      }
      addOrUpdateQuestionnairesStepsBo =
          studyQuestionnaireDAO.saveOrUpdateQuestionStep(
              questionnairesStepsBo, sessionObject, customStudyId);

    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - saveOrUpdateQuestionStep - Error", e);
    }
    logger.exit("saveOrUpdateQuestionStep() - Ends");
    return addOrUpdateQuestionnairesStepsBo;
  }

  @Override
  public String validateLineChartSchedule(Integer questionnaireId, String frequency) {
    logger.entry("begin validateLineChartSchedule()");
    return studyQuestionnaireDAO.validateLineChartSchedule(questionnaireId, frequency);
  }

  @Override
  public FormulaInfoBean validateQuestionConditionalBranchingLogic(
      String lhs, String rhs, String operator, String input) {
    logger.entry("begin validateQuestionConditionalBranchingLogic()");
    FormulaInfoBean formulaInfoBean = new FormulaInfoBean();
    if (StringUtils.isNotEmpty(lhs)
        && StringUtils.isNotEmpty(rhs)
        && StringUtils.isNotEmpty(operator)) {
      formulaInfoBean =
          FdahpStudyDesignerUtil.getConditionalFormulaResult(lhs, rhs, operator, input);
    }
    logger.exit("validateQuestionConditionalBranchingLogic() - Ends");
    return formulaInfoBean;
  }

  @Override
  public String validateRepetableFormQuestionStats(Integer formId) {
    logger.entry("begin validateRepetableFormQuestionStats()");
    return studyQuestionnaireDAO.validateRepetableFormQuestionStats(formId);
  }

  @Override
  public String checkUniqueAnchorDateName(
      String anchordateText, String customStudyId, String anchorDateId) {
    logger.entry("begin checkUniqueAnchorDateName()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      message =
          studyQuestionnaireDAO.checkUniqueAnchorDateName(
              anchordateText, customStudyId, anchorDateId);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - checkUniqueAnchorDateName - Error", e);
    }
    logger.exit("checkUniqueAnchorDateName() - Ends");
    return message;
  }

  @Override
  public List<AnchorDateTypeBo> getAnchorTypesByStudyId(String customStudyId) {
    logger.entry("begin getAnchorTypesByStudyId()");
    List<AnchorDateTypeBo> anchorDateTypeBos = null;
    HashMap<String, AnchorDateTypeBo> anchorMap = new HashMap<>();
    try {
      anchorDateTypeBos = studyQuestionnaireDAO.getAnchorTypesByStudyId(customStudyId);
      for (AnchorDateTypeBo anchorDateTypeBo : anchorDateTypeBos) {
        anchorMap.put(anchorDateTypeBo.getName(), anchorDateTypeBo);
      }
      anchorDateTypeBos = new ArrayList<>(anchorMap.values());
    } catch (Exception e) {
      logger.error("StudyQuestionnaireServiceImpl - getAnchorTypesByStudyId - Error", e);
    }
    logger.exit("getAnchorTypesByStudyId() - Ends");
    return anchorDateTypeBos;
  }

  @Override
  public boolean isAnchorDateExistByQuestionnaire(Integer questionnaireId) {
    logger.entry("begin isAnchorDateExistByQuestionnaire");
    return studyQuestionnaireDAO.isAnchorDateExistByQuestionnaire(questionnaireId);
  }

  @Override
  public QuestionnaireBo getQuestionnaireById(Integer questionnaireId) {
    logger.entry("begin getQuestionnaireById");
    return studyQuestionnaireDAO.getQuestionnaireById(questionnaireId);
  }

  @Override
  public QuestionsBo getQuestionById(Integer questionId) {
    logger.entry("begin getQuestionById");
    return studyQuestionnaireDAO.getQuestionById(questionId);
  }
}
