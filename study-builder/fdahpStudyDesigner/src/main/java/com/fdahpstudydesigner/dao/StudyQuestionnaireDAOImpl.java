/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.dao;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_FORM_STEP_DELETED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_INSTRUCTION_STEP_DELETED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_QUESTION_STEP_DELETED;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.QUESTION_ID;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.STEP_ID;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.QuestionnaireStepBean;
import com.fdahpstudydesigner.bo.ActiveTaskAtrributeValuesBo;
import com.fdahpstudydesigner.bo.ActiveTaskBo;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.FormBo;
import com.fdahpstudydesigner.bo.FormMappingBo;
import com.fdahpstudydesigner.bo.HealthKitKeysInfo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.QuestionConditionBranchBo;
import com.fdahpstudydesigner.bo.QuestionReponseTypeBo;
import com.fdahpstudydesigner.bo.QuestionResponseSubTypeBo;
import com.fdahpstudydesigner.bo.QuestionResponseTypeMasterInfoBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnaireCustomScheduleBo;
import com.fdahpstudydesigner.bo.QuestionnairesFrequenciesBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import com.fdahpstudydesigner.bo.StudyVersionBo;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class StudyQuestionnaireDAOImpl implements StudyQuestionnaireDAO {

  private static Logger logger = Logger.getLogger(StudyQuestionnaireDAOImpl.class.getName());

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  @Autowired private AuditLogDAO auditLogDAO;

  @Autowired private HttpServletRequest request;

  HibernateTemplate hibernateTemplate;

  private Query query = null;

  String queryString = "";

  private Transaction transaction = null;

  @SuppressWarnings("unchecked")
  @Override
  public String checkFromQuestionShortTitle(
      Integer questionnaireId,
      String shortTitle,
      String questionnaireShortTitle,
      String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - checkQuestionnaireStepShortTitle() - starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    List<QuestionnairesStepsBo> questionnairesStepsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if ((questionnaireShortTitle != null) && !questionnaireShortTitle.isEmpty()) {
        query =
            session
                .createQuery(
                    "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId IN"
                        + "(select QBO.id From QuestionnaireBo QBO where QBO.shortTitle=:questionnaireShortTitle and"
                        + " QBO.studyId in(select id From StudyBo SBO WHERE customStudyId=:customStudyId)) and"
                        + " QSBO.stepShortTitle=:shortTitle")
                .setParameter("questionnaireShortTitle", questionnaireShortTitle)
                .setParameter("customStudyId", customStudyId)
                .setParameter("shortTitle", shortTitle);
        questionnairesStepsBo = query.list();
        if ((questionnairesStepsBo != null) && !questionnairesStepsBo.isEmpty()) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        } else {
          String searchQuery =
              "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
                  + " and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.short_title=:questionnaireShortTitle "
                  + " and Q.study_id in(select id From studies SBO WHERE custom_study_id=:customStudyId "
                  + " ) and QSBO.step_type='Form' and QBO.short_title=:shortTitle ";
          BigInteger subCount =
              (BigInteger)
                  session
                      .createSQLQuery(searchQuery)
                      .setParameter("customStudyId", customStudyId)
                      .setParameter("questionnaireShortTitle", questionnaireShortTitle)
                      .setParameter("shortTitle", shortTitle)
                      .uniqueResult();
          if ((subCount != null) && (subCount.intValue() > 0)) {
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      } else {
        query =
            session
                .getNamedQuery("checkQuestionnaireStepShortTitle")
                .setInteger("questionnaireId", questionnaireId)
                .setString("shortTitle", shortTitle);
        questionnairesStepsBo = query.list();
        if ((questionnairesStepsBo != null) && !questionnairesStepsBo.isEmpty()) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        } else {
          String searchQuery =
              "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
                  + "and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.id=:questionnaireId "
                  + " and QSBO.step_type='Form' and QBO.short_title=:shortTitle ";
          BigInteger subCount =
              (BigInteger)
                  session
                      .createSQLQuery(searchQuery)
                      .setInteger("questionnaireId", questionnaireId)
                      .setString("shortTitle", shortTitle)
                      .uniqueResult();
          if ((subCount != null) && (subCount.intValue() > 0)) {
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      }

    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - checkQuestionnaireStepShortTitle() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - checkQuestionnaireStepShortTitle() - Ends");
    return message;
  }

  @Override
  public String checkQuestionnaireResponseTypeValidation(Integer studyId, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - checkQuestionnaireResponseTypeValidation() - starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    BigInteger questionCount = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      // checking of all the question step of questionnaire
      String searchQuery =
          "select count(*) from questions QBO,questionnaires_steps QSBO,questionnaires Q where QBO.id=QSBO.instruction_form_id"
              + " and QSBO.questionnaires_id=Q.id and Q.study_id=:studyId and Q.active=1 and QSBO.step_type= :questionStep"
              + " and QSBO.active=1 and QBO.active=1 and QBO.response_type=3";
      BigInteger count =
          (BigInteger)
              session
                  .createSQLQuery(searchQuery)
                  .setInteger("studyId", studyId)
                  .setString("questionStep", FdahpStudyDesignerConstants.QUESTION_STEP)
                  .uniqueResult();
      if ((count != null) && (count.intValue() > 0)) {
        message = FdahpStudyDesignerConstants.SUCCESS;
      } else {
        // checking of all question of form step of questionnaire
        String searchQuuery =
            "select count(*) from questions q,form_mapping f,questionnaires_steps qs,questionnaires qq where q.id=f.question_id"
                + " and f.form_id=qs.instruction_form_id and qs.questionnaires_id=qq.id and qq.study_id= :studyId"
                + " and qq.active=1 and qs.step_type='Form' and qs.active=1 and f.active=1 and q.response_type=3 and q.active=1";
        questionCount =
            (BigInteger)
                session.createSQLQuery(searchQuuery).setInteger("studyId", studyId).uniqueResult();
        if ((questionCount != null) && (questionCount.intValue() > 0)) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      }
    } catch (Exception e) {
      logger.error(
          "StudyQuestionnaireDAOImpl - checkQuestionnaireResponseTypeValidation() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - checkQuestionnaireResponseTypeValidation() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String checkQuestionnaireShortTitle(
      Integer studyId, String shortTitle, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - checkQuestionnaireShortTitle() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    List<QuestionnaireBo> questionnaireBo = null;
    List<ActiveTaskBo> taskBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if ((customStudyId != null) && !customStudyId.isEmpty()) {
        // checking in the live version questionnaire
        query =
            session.createQuery(
                "From QuestionnaireBo QBO where QBO.studyId IN(select id From StudyBo SBO WHERE customStudyId=:customStudyId ) and QBO.shortTitle=:shortTitle ");
        questionnaireBo =
            query
                .setString("customStudyId", customStudyId)
                .setString("shortTitle", shortTitle)
                .list();
        if ((questionnaireBo != null) && !questionnaireBo.isEmpty()) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        } else {
          queryString =
              "from ActiveTaskBo where studyId IN(select id From StudyBo SBO WHERE customStudyId=:customStudyId )  and shortTitle=:shortTitle ";
          taskBo =
              session
                  .createQuery(queryString)
                  .setString("customStudyId", customStudyId)
                  .setString("shortTitle", shortTitle)
                  .list();

          if ((taskBo != null) && !taskBo.isEmpty()) {
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      } else {
        // checking in the draft version questionnaire
        query =
            session
                .getNamedQuery("checkQuestionnaireShortTitle")
                .setInteger("studyId", studyId)
                .setString("shortTitle", shortTitle);
        questionnaireBo = query.list();

        if ((questionnaireBo != null) && !questionnaireBo.isEmpty()) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        } else {
          queryString = "from ActiveTaskBo where studyId=:studyId and shortTitle=:shortTitle";
          taskBo =
              session
                  .createQuery(queryString)
                  .setInteger("studyId", studyId)
                  .setString("shortTitle", shortTitle)
                  .list();

          if ((taskBo != null) && !taskBo.isEmpty()) {
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - checkQuestionnaireShortTitle() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - checkQuestionnaireShortTitle() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String checkQuestionnaireStepShortTitle(
      Integer questionnaireId,
      String stepType,
      String shortTitle,
      String questionnaireShortTitle,
      String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - checkQuestionnaireStepShortTitle() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    List<QuestionnairesStepsBo> questionnairesStepsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if ((questionnaireShortTitle != null) && !questionnaireShortTitle.isEmpty()) {
        query =
            session.createQuery(
                "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId IN (select QBO.id From QuestionnaireBo QBO where QBO.shortTitle=:questionnaireShortTitle "
                    + " and QBO.studyId in(select id From StudyBo SBO WHERE customStudyId=:customStudyId "
                    + " )) and QSBO.stepShortTitle=:shortTitle ");
        questionnairesStepsBo =
            query
                .setString("questionnaireShortTitle", questionnaireShortTitle)
                .setString("customStudyId", customStudyId)
                .setString("shortTitle", shortTitle)
                .list();
        if ((questionnairesStepsBo != null) && !questionnairesStepsBo.isEmpty()) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        } else {
          String searchQuery =
              "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
                  + " and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.short_title=:questionnaireShortTitle "
                  + " and Q.study_id in(select id From studies SBO WHERE custom_study_id=:customStudyId "
                  + " ) and QSBO.step_type='Form' and QBO.short_title=:shortTitle ";
          BigInteger subCount =
              (BigInteger)
                  session
                      .createSQLQuery(searchQuery)
                      .setString("questionnaireShortTitle", questionnaireShortTitle)
                      .setString("customStudyId", customStudyId)
                      .setString("shortTitle", shortTitle)
                      .uniqueResult();
          if ((subCount != null) && (subCount.intValue() > 0)) {
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      } else {
        query =
            session
                .getNamedQuery("checkQuestionnaireStepShortTitle")
                .setInteger("questionnaireId", questionnaireId)
                .setString("shortTitle", shortTitle);
        questionnairesStepsBo = query.list();
        if ((questionnairesStepsBo != null) && !questionnairesStepsBo.isEmpty()) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        } else {
          String searchQuery =
              "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
                  + " and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.id=:questionnaireId "
                  + " and QSBO.step_type='Form' and QBO.short_title=:shortTitle ";
          BigInteger subCount =
              (BigInteger)
                  session
                      .createSQLQuery(searchQuery)
                      .setInteger("questionnaireId", questionnaireId)
                      .setString("shortTitle", shortTitle)
                      .uniqueResult();
          if ((subCount != null) && (subCount.intValue() > 0)) {
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - checkQuestionnaireStepShortTitle() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - checkQuestionnaireStepShortTitle() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String checkStatShortTitle(Integer studyId, String shortTitle, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - checkQuestionnaireStepShortTitle() - starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    List<QuestionsBo> questionsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      // checking with custom study in live version and draft version
      if ((customStudyId != null) && !customStudyId.isEmpty()) {
        // checking in the question step stastic data
        String serachQuery =
            "select count(*) from questions qbo,questionnaires_steps qsbo,questionnaires q where qbo.id=qsbo.instruction_form_id and qsbo.questionnaires_id=q.id and q.study_id in(select id From studies SBO WHERE custom_study_id=:customStudyId "
                + ") and qsbo.step_type=:stepType "
                + " and qbo.stat_short_name=:shortTitle";
        BigInteger count =
            (BigInteger)
                session
                    .createSQLQuery(serachQuery)
                    .setString("customStudyId", customStudyId)
                    .setString("stepType", FdahpStudyDesignerConstants.QUESTION_STEP)
                    .setString("shortTitle", shortTitle)
                    .uniqueResult();
        if ((count != null) && (count.intValue() > 0)) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        } else {
          // checking in the form step questions stastic data
          String searchQuery =
              "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
                  + "and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.study_id IN(select id From studies SBO WHERE custom_study_id= :customStudyId"
                  + " ) and QSBO.step_type='Form' and QBO.stat_short_name=:shortTitle ";
          BigInteger subCount =
              (BigInteger)
                  session
                      .createSQLQuery(searchQuery)
                      .setString("customStudyId", customStudyId)
                      .setString("shortTitle", shortTitle)
                      .uniqueResult();
          if ((subCount != null) && (subCount.intValue() > 0)) {
            message = FdahpStudyDesignerConstants.SUCCESS;
          } else {
            // checking in the active task stastic data
            String taskQuery =
                "from ActiveTaskAtrributeValuesBo where activeTaskId in(select id from ActiveTaskBo where studyId IN(select id From StudyBo SBO WHERE customStudyId=:customStudyId "
                    + " ) ) and identifierNameStat=:shortTitle ";
            List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos =
                session
                    .createQuery(taskQuery)
                    .setString("customStudyId", customStudyId)
                    .setString("shortTitle", shortTitle)
                    .list();
            if ((activeTaskAtrributeValuesBos != null) && !activeTaskAtrributeValuesBos.isEmpty()) {
              message = FdahpStudyDesignerConstants.SUCCESS;
            }
          }
        }
      } else {
        // checking with study if custom study id is not available
        query =
            session.createQuery(
                "From QuestionsBo QBO where QBO.id IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId IN (select id from QuestionnaireBo Q where Q.studyId=:studyId "
                    + " ) and QSBO.stepType=:stepType "
                    + " and QSBO.active=1) and QBO.statShortName=:shortTitle ");
        questionsBo =
            query
                .setInteger("studyId", studyId)
                .setString("stepType", FdahpStudyDesignerConstants.QUESTION_STEP)
                .setString("shortTitle", shortTitle)
                .list();
        if ((questionsBo != null) && !questionsBo.isEmpty()) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        } else {
          String searchQuuery =
              "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id "
                  + "and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.study_id=:studyId"
                  + " and QSBO.step_type='Form' and QBO.stat_short_name=:shortTitle";
          questionsBo =
              session
                  .createQuery(searchQuuery)
                  .setInteger("studyId", studyId)
                  .setString("shortTitle", shortTitle)
                  .list();

          if ((questionsBo != null) && !questionsBo.isEmpty()) {
            message = FdahpStudyDesignerConstants.SUCCESS;
          } else {
            String taskQuery =
                "from ActiveTaskAtrributeValuesBo where activeTaskId in(select id from ActiveTaskBo where studyId=:studyId "
                    + ") and identifierNameStat=:shortTitle ";
            List<ActiveTaskAtrributeValuesBo> activeTaskAtrributeValuesBos =
                session
                    .createQuery(taskQuery)
                    .setInteger("studyId", studyId)
                    .setString("shortTitle", shortTitle)
                    .list();
            if ((activeTaskAtrributeValuesBos != null) && !activeTaskAtrributeValuesBos.isEmpty()) {
              message = FdahpStudyDesignerConstants.SUCCESS;
            }
          }
        }
      }

    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - checkStatShortTitle() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - checkStatShortTitle() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public QuestionnaireBo copyStudyQuestionnaireBo(
      Integer questionnaireId, String customStudyId, SessionObject sessionObject) {
    logger.info("StudyQuestionnaireDAOImpl - copyStudyQuestionnaireBo() - Starts");
    QuestionnaireBo questionnaireBo = null;
    QuestionnaireBo newQuestionnaireBo = null;
    Session session = null;
    QuestionReponseTypeBo questionReponseTypeBo = null;
    try {
      // Questionarries
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      query = session.createQuery(" From QuestionnaireBo QBO WHERE QBO.id=:questionnaireId ");
      questionnaireBo =
          (QuestionnaireBo) query.setInteger("questionnaireId", questionnaireId).uniqueResult();
      if (questionnaireBo != null) {
        String searchQuery = null;
        newQuestionnaireBo = SerializationUtils.clone(questionnaireBo);
        newQuestionnaireBo.setId(null);
        newQuestionnaireBo.setLive(0);
        newQuestionnaireBo.setCreatedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
        newQuestionnaireBo.setCreatedBy(sessionObject.getUserId());
        newQuestionnaireBo.setModifiedBy(null);
        newQuestionnaireBo.setModifiedDate(null);
        newQuestionnaireBo.setShortTitle(null);
        newQuestionnaireBo.setStatus(false);
        newQuestionnaireBo.setVersion(0f);
        session.save(newQuestionnaireBo);

        /** Questionnaire Schedule Purpose copying Start * */
        if (StringUtils.isNotEmpty(questionnaireBo.getFrequency())) {
          if (questionnaireBo
              .getFrequency()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
            searchQuery =
                "From QuestionnaireCustomScheduleBo QCSBO where QCSBO.questionnairesId=:questionnaireId ";
            List<QuestionnaireCustomScheduleBo> questionnaireCustomScheduleList =
                session
                    .createQuery(searchQuery)
                    .setInteger("questionnaireId", questionnaireBo.getId())
                    .list();
            if ((questionnaireCustomScheduleList != null)
                && !questionnaireCustomScheduleList.isEmpty()) {
              for (QuestionnaireCustomScheduleBo customScheduleBo :
                  questionnaireCustomScheduleList) {
                QuestionnaireCustomScheduleBo newCustomScheduleBo =
                    SerializationUtils.clone(customScheduleBo);
                newCustomScheduleBo.setQuestionnairesId(newQuestionnaireBo.getId());
                newCustomScheduleBo.setId(null);
                newCustomScheduleBo.setUsed(false);
                session.save(newCustomScheduleBo);
              }
            }
          } else {
            searchQuery =
                "From QuestionnairesFrequenciesBo QFBO where QFBO.questionnairesId=:questionnaireId ";
            List<QuestionnairesFrequenciesBo> questionnairesFrequenciesList =
                session
                    .createQuery(searchQuery)
                    .setInteger("questionnaireId", questionnaireBo.getId())
                    .list();
            if ((questionnairesFrequenciesList != null)
                && !questionnairesFrequenciesList.isEmpty()) {
              for (QuestionnairesFrequenciesBo questionnairesFrequenciesBo :
                  questionnairesFrequenciesList) {
                QuestionnairesFrequenciesBo newQuestionnairesFrequenciesBo =
                    SerializationUtils.clone(questionnairesFrequenciesBo);
                newQuestionnairesFrequenciesBo.setQuestionnairesId(newQuestionnaireBo.getId());
                newQuestionnairesFrequenciesBo.setId(null);
                session.save(newQuestionnairesFrequenciesBo);
              }
            }
          }
        }
        /** Questionnaire Schedule Purpose copying End * */

        /** Questionnaire Content purpose copying Start * */
        List<Integer> destinationList = new ArrayList<>();
        Map<Integer, Integer> destionationMapList = new HashMap<>();

        List<QuestionnairesStepsBo> existedQuestionnairesStepsBoList = null;
        List<QuestionnairesStepsBo> newQuestionnairesStepsBoList = new ArrayList<>();
        List<QuestionResponseSubTypeBo> existingQuestionResponseSubTypeList = new ArrayList<>();
        List<QuestionResponseSubTypeBo> newQuestionResponseSubTypeList = new ArrayList<>();

        List<QuestionReponseTypeBo> existingQuestionResponseTypeList = new ArrayList<>();
        List<QuestionReponseTypeBo> newQuestionResponseTypeList = new ArrayList<>();

        query =
            session
                .getNamedQuery("getQuestionnaireStepList")
                .setInteger("questionnaireId", questionnaireBo.getId());
        existedQuestionnairesStepsBoList = query.list();
        // copying the questionnaire steps
        if ((existedQuestionnairesStepsBoList != null)
            && !existedQuestionnairesStepsBoList.isEmpty()) {
          for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
            Integer destionStep = questionnairesStepsBo.getDestinationStep();
            if (destionStep.equals(0)) {
              destinationList.add(-1);
            } else {
              for (int i = 0; i < existedQuestionnairesStepsBoList.size(); i++) {
                if ((existedQuestionnairesStepsBoList.get(i).getStepId() != null)
                    && destionStep.equals(existedQuestionnairesStepsBoList.get(i).getStepId())) {
                  destinationList.add(i);
                  break;
                }
              }
            }
            destionationMapList.put(
                questionnairesStepsBo.getSequenceNo(), questionnairesStepsBo.getStepId());
          }
          for (QuestionnairesStepsBo questionnairesStepsBo : existedQuestionnairesStepsBoList) {
            if (StringUtils.isNotEmpty(questionnairesStepsBo.getStepType())) {
              QuestionnairesStepsBo newQuestionnairesStepsBo =
                  SerializationUtils.clone(questionnairesStepsBo);
              newQuestionnairesStepsBo.setQuestionnairesId(newQuestionnaireBo.getId());
              newQuestionnairesStepsBo.setStepId(null);
              newQuestionnairesStepsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              newQuestionnairesStepsBo.setCreatedBy(sessionObject.getUserId());
              newQuestionnairesStepsBo.setModifiedBy(null);
              newQuestionnairesStepsBo.setModifiedOn(null);
              session.save(newQuestionnairesStepsBo);
              if (questionnairesStepsBo
                  .getStepType()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.INSTRUCTION_STEP)) {
                // copying the instruction step
                InstructionsBo instructionsBo =
                    (InstructionsBo)
                        session
                            .getNamedQuery("getInstructionStep")
                            .setInteger("id", questionnairesStepsBo.getInstructionFormId())
                            .uniqueResult();
                if (instructionsBo != null) {
                  InstructionsBo newInstructionsBo = SerializationUtils.clone(instructionsBo);
                  newInstructionsBo.setId(null);
                  newInstructionsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                  newInstructionsBo.setCreatedBy(sessionObject.getUserId());
                  newInstructionsBo.setModifiedBy(null);
                  newInstructionsBo.setModifiedOn(null);
                  session.save(newInstructionsBo);

                  // updating new InstructionId
                  newQuestionnairesStepsBo.setInstructionFormId(newInstructionsBo.getId());
                }
              } else if (questionnairesStepsBo
                  .getStepType()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTION_STEP)) {
                // copying the question step
                QuestionsBo questionsBo =
                    (QuestionsBo)
                        session
                            .getNamedQuery("getQuestionStep")
                            .setInteger("stepId", questionnairesStepsBo.getInstructionFormId())
                            .uniqueResult();
                if (questionsBo != null) {

                  // Question response subType
                  List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
                      session
                          .getNamedQuery("getQuestionSubResponse")
                          .setInteger("responseTypeId", questionsBo.getId())
                          .list();

                  List<QuestionConditionBranchBo> questionConditionBranchList =
                      session
                          .getNamedQuery("getQuestionConditionBranchList")
                          .setInteger("questionId", questionsBo.getId())
                          .list();

                  // Question response Type
                  questionReponseTypeBo =
                      (QuestionReponseTypeBo)
                          session
                              .getNamedQuery("getQuestionResponse")
                              .setInteger("questionsResponseTypeId", questionsBo.getId())
                              .uniqueResult();

                  QuestionsBo newQuestionsBo = SerializationUtils.clone(questionsBo);
                  newQuestionsBo.setId(null);
                  newQuestionsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                  newQuestionsBo.setCreatedBy(sessionObject.getUserId());
                  newQuestionsBo.setModifiedBy(null);
                  newQuestionsBo.setModifiedOn(null);
                  newQuestionsBo.setAnchorDateId(null);
                  if (questionsBo
                      .getUseStasticData()
                      .equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
                    newQuestionsBo.setStatShortName(null);
                    newQuestionsBo.setStatus(false);
                    newQuestionnairesStepsBo.setStatus(false);
                  }
                  session.save(newQuestionsBo);

                  // Question response Type
                  if (questionReponseTypeBo != null) {
                    QuestionReponseTypeBo newQuestionReponseTypeBo =
                        SerializationUtils.clone(questionReponseTypeBo);
                    newQuestionReponseTypeBo.setResponseTypeId(null);
                    newQuestionReponseTypeBo.setQuestionsResponseTypeId(newQuestionsBo.getId());
                    newQuestionReponseTypeBo.setOtherDestinationStepId(null);
                    session.save(newQuestionReponseTypeBo);
                    if ((questionReponseTypeBo.getOtherType() != null)
                        && StringUtils.isNotEmpty(questionReponseTypeBo.getOtherType())
                        && questionReponseTypeBo.getOtherType().equals("on")) {
                      existingQuestionResponseTypeList.add(questionReponseTypeBo);
                      newQuestionResponseTypeList.add(newQuestionReponseTypeBo);
                    }
                  }

                  // Question Condition branching logic
                  if ((questionConditionBranchList != null)
                      && !questionConditionBranchList.isEmpty()) {
                    for (QuestionConditionBranchBo questionConditionBranchBo :
                        questionConditionBranchList) {
                      QuestionConditionBranchBo newQuestionConditionBranchBo =
                          SerializationUtils.clone(questionConditionBranchBo);
                      newQuestionConditionBranchBo.setConditionId(null);
                      newQuestionConditionBranchBo.setQuestionId(newQuestionsBo.getId());
                      session.save(newQuestionConditionBranchBo);
                    }
                  }

                  // Question response subType
                  if ((questionResponseSubTypeList != null)
                      && !questionResponseSubTypeList.isEmpty()) {
                    existingQuestionResponseSubTypeList.addAll(questionResponseSubTypeList);

                    for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                        questionResponseSubTypeList) {

                      QuestionResponseSubTypeBo newQuestionResponseSubTypeBo =
                          SerializationUtils.clone(questionResponseSubTypeBo);
                      newQuestionResponseSubTypeBo.setResponseSubTypeValueId(null);
                      newQuestionResponseSubTypeBo.setResponseTypeId(newQuestionsBo.getId());
                      newQuestionResponseSubTypeBo.setDestinationStepId(null);
                      session.save(newQuestionResponseSubTypeBo);
                      newQuestionResponseSubTypeList.add(newQuestionResponseSubTypeBo);
                    }
                  }

                  // updating new InstructionId
                  newQuestionnairesStepsBo.setInstructionFormId(newQuestionsBo.getId());
                }
              } else if (questionnairesStepsBo
                  .getStepType()
                  .equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)) {
                // copying the form step
                FormBo formBo =
                    (FormBo)
                        session
                            .getNamedQuery("getFormBoStep")
                            .setInteger("stepId", questionnairesStepsBo.getInstructionFormId())
                            .uniqueResult();
                if (formBo != null) {

                  FormBo newFormBo = SerializationUtils.clone(formBo);
                  newFormBo.setFormId(null);
                  session.save(newFormBo);

                  List<FormMappingBo> formMappingBoList =
                      session
                          .getNamedQuery("getFormByFormId")
                          .setInteger("formId", formBo.getFormId())
                          .list();
                  if ((formMappingBoList != null) && !formMappingBoList.isEmpty()) {
                    for (FormMappingBo formMappingBo : formMappingBoList) {
                      FormMappingBo newMappingBo = SerializationUtils.clone(formMappingBo);
                      newMappingBo.setFormId(newFormBo.getFormId());
                      newMappingBo.setId(null);

                      QuestionsBo questionsBo =
                          (QuestionsBo)
                              session
                                  .getNamedQuery("getQuestionByFormId")
                                  .setInteger("formId", formMappingBo.getQuestionId())
                                  .uniqueResult();
                      if (questionsBo != null) {

                        // Question response subType
                        List<QuestionResponseSubTypeBo> questionResponseSubTypeList =
                            session
                                .getNamedQuery("getQuestionSubResponse")
                                .setInteger("responseTypeId", questionsBo.getId())
                                .list();

                        // Question response Type
                        questionReponseTypeBo =
                            (QuestionReponseTypeBo)
                                session
                                    .getNamedQuery("getQuestionResponse")
                                    .setInteger("questionsResponseTypeId", questionsBo.getId())
                                    .uniqueResult();

                        QuestionsBo newQuestionsBo = SerializationUtils.clone(questionsBo);
                        newQuestionsBo.setId(null);

                        newQuestionsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                        newQuestionsBo.setCreatedBy(sessionObject.getUserId());
                        newQuestionsBo.setModifiedBy(null);
                        newQuestionsBo.setModifiedOn(null);
                        newQuestionsBo.setAnchorDateId(null);
                        if (questionsBo
                            .getUseStasticData()
                            .equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
                          newQuestionsBo.setStatShortName(null);
                          newQuestionsBo.setStatus(false);
                          newQuestionnairesStepsBo.setStatus(false);
                        }

                        session.save(newQuestionsBo);

                        // Question response Type
                        if (questionReponseTypeBo != null) {
                          QuestionReponseTypeBo newQuestionReponseTypeBo =
                              SerializationUtils.clone(questionReponseTypeBo);
                          newQuestionReponseTypeBo.setResponseTypeId(null);
                          newQuestionReponseTypeBo.setQuestionsResponseTypeId(
                              newQuestionsBo.getId());
                          session.save(newQuestionReponseTypeBo);
                        }

                        // Question response subType
                        if ((questionResponseSubTypeList != null)
                            && !questionResponseSubTypeList.isEmpty()) {
                          for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                              questionResponseSubTypeList) {
                            QuestionResponseSubTypeBo newQuestionResponseSubTypeBo =
                                SerializationUtils.clone(questionResponseSubTypeBo);
                            newQuestionResponseSubTypeBo.setResponseSubTypeValueId(null);
                            newQuestionResponseSubTypeBo.setResponseTypeId(newQuestionsBo.getId());
                            session.save(newQuestionResponseSubTypeBo);
                          }
                        }

                        // adding questionId
                        newMappingBo.setQuestionId(newQuestionsBo.getId());
                        session.save(newMappingBo);
                      }
                    }
                  }
                  // updating new formId

                  newQuestionnairesStepsBo.setInstructionFormId(newFormBo.getFormId());
                }
              }
              session.update(newQuestionnairesStepsBo);
              newQuestionnairesStepsBoList.add(newQuestionnairesStepsBo);
            }
          }
        }
        // updating the copied destination steps for questionnaire steps
        if ((destinationList != null) && !destinationList.isEmpty()) {
          for (int i = 0; i < destinationList.size(); i++) {
            int desId = 0;
            if (destinationList.get(i) != -1) {
              desId = newQuestionnairesStepsBoList.get(destinationList.get(i)).getStepId();
            }
            newQuestionnairesStepsBoList.get(i).setDestinationStep(desId);
            session.update(newQuestionnairesStepsBoList.get(i));
          }
        }
        List<Integer> sequenceSubTypeList = new ArrayList<>();
        List<Integer> destinationResList = new ArrayList<>();
        // getting the list of all copied choice based destinations
        if ((existingQuestionResponseSubTypeList != null)
            && !existingQuestionResponseSubTypeList.isEmpty()) {
          for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
              existingQuestionResponseSubTypeList) {
            if (questionResponseSubTypeBo.getDestinationStepId() == null) {
              sequenceSubTypeList.add(null);
            } else if ((questionResponseSubTypeBo.getDestinationStepId() != null)
                && questionResponseSubTypeBo.getDestinationStepId().equals(0)) {
              sequenceSubTypeList.add(-1);
            } else {
              if ((existedQuestionnairesStepsBoList != null)
                  && !existedQuestionnairesStepsBoList.isEmpty()) {
                for (QuestionnairesStepsBo questionnairesStepsBo :
                    existedQuestionnairesStepsBoList) {
                  if ((questionResponseSubTypeBo.getDestinationStepId() != null)
                      && questionResponseSubTypeBo
                          .getDestinationStepId()
                          .equals(questionnairesStepsBo.getStepId())) {
                    sequenceSubTypeList.add(questionnairesStepsBo.getSequenceNo());
                    break;
                  }
                }
              }
            }
          }
        }
        if ((sequenceSubTypeList != null) && !sequenceSubTypeList.isEmpty()) {
          for (int i = 0; i < sequenceSubTypeList.size(); i++) {
            Integer desId = null;
            if (sequenceSubTypeList.get(i) == null) {
              desId = null;
            } else if (sequenceSubTypeList.get(i).equals(-1)) {
              desId = 0;
            } else {
              for (QuestionnairesStepsBo questionnairesStepsBo : newQuestionnairesStepsBoList) {
                if (sequenceSubTypeList.get(i).equals(questionnairesStepsBo.getSequenceNo())) {
                  desId = questionnairesStepsBo.getStepId();
                  break;
                }
              }
            }
            destinationResList.add(desId);
          }
          // updating the choice based destination steps
          for (int i = 0; i < destinationResList.size(); i++) {
            newQuestionResponseSubTypeList.get(i).setDestinationStepId(destinationResList.get(i));
            session.update(newQuestionResponseSubTypeList.get(i));
          }
        }

        // for other type , update the destination in questionresponsetype table
        /** start * */
        List<Integer> sequenceTypeList = new ArrayList<>();
        List<Integer> destinationResTypeList = new ArrayList<>();
        if ((existingQuestionResponseTypeList != null)
            && !existingQuestionResponseTypeList.isEmpty()) {
          for (QuestionReponseTypeBo questionResponseTypeBo : existingQuestionResponseTypeList) {
            if (questionResponseTypeBo.getOtherDestinationStepId() == null) {
              sequenceTypeList.add(null);
            } else if ((questionResponseTypeBo.getOtherDestinationStepId() != null)
                && questionResponseTypeBo.getOtherDestinationStepId().equals(0)) {
              sequenceTypeList.add(-1);
            } else {
              if ((existedQuestionnairesStepsBoList != null)
                  && !existedQuestionnairesStepsBoList.isEmpty()) {
                for (QuestionnairesStepsBo questionnairesStepsBo :
                    existedQuestionnairesStepsBoList) {
                  if ((questionResponseTypeBo.getOtherDestinationStepId() != null)
                      && questionResponseTypeBo
                          .getOtherDestinationStepId()
                          .equals(questionnairesStepsBo.getStepId())) {
                    sequenceTypeList.add(questionnairesStepsBo.getSequenceNo());
                    break;
                  }
                }
              }
            }
          }
        }
        if ((sequenceTypeList != null) && !sequenceTypeList.isEmpty()) {
          for (int i = 0; i < sequenceTypeList.size(); i++) {
            Integer desId = null;
            if (sequenceTypeList.get(i) == null) {
              desId = null;
            } else if (sequenceTypeList.get(i).equals(-1)) {
              desId = 0;
            } else {
              for (QuestionnairesStepsBo questionnairesStepsBo : newQuestionnairesStepsBoList) {
                if (sequenceTypeList.get(i).equals(questionnairesStepsBo.getSequenceNo())) {
                  desId = questionnairesStepsBo.getStepId();
                  break;
                }
              }
            }
            destinationResTypeList.add(desId);
          }
          for (int i = 0; i < destinationResTypeList.size(); i++) {
            newQuestionResponseTypeList
                .get(i)
                .setOtherDestinationStepId(destinationResTypeList.get(i));
            session.update(newQuestionResponseTypeList.get(i));
          }
        }
        /** * end ** */
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyDAOImpl - resetDraftStudyByCustomStudyId() - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - copyStudyQuestionnaireBo() - Ends");
    return newQuestionnaireBo;
  }

  @Override
  public String deleteFromStepQuestion(
      Integer formId,
      Integer questionId,
      SessionObject sessionObject,
      String customStudyId,
      AuditLogEventRequest auditRequest) {
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    logger.info("StudyQuestionnaireDAOImpl - deleteFromStepQuestion() - Starts");
    FormMappingBo formMappingBo = null;
    StudyVersionBo studyVersionBo = null;
    Map<String, String> values = new HashMap<>();
    try {
      auditRequest.setStudyId(customStudyId);

      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      query =
          session
              .getNamedQuery("getStudyByCustomStudyId")
              .setString("customStudyId", customStudyId);
      query.setMaxResults(1);
      studyVersionBo = (StudyVersionBo) query.uniqueResult();
      query =
          session
              .getNamedQuery("getFormQuestion")
              .setInteger("formId", formId)
              .setInteger("questionId", questionId);
      formMappingBo = (FormMappingBo) query.uniqueResult();
      if (formMappingBo != null) {
        String updateQuery =
            "update FormMappingBo FMBO set FMBO.sequenceNo=FMBO.sequenceNo-1 where FMBO.formId=:formId "
                + " and FMBO.active=1 and FMBO.sequenceNo >=:sequenceNo ";
        query =
            session
                .createQuery(updateQuery)
                .setInteger("formId", formMappingBo.getFormId())
                .setInteger("sequenceNo", formMappingBo.getSequenceNo());
        query.executeUpdate();
        // delete anchordate start
        StudyBo studyBo =
            (StudyBo)
                session
                    .createQuery("from StudyBo where customStudyId=:customStudyId and live=0")
                    .setString("customStudyId", customStudyId)
                    .uniqueResult();
        if (studyBo != null) {
          boolean isChange = true;
          message =
              updateAnchordateInQuestionnaire(
                  session,
                  transaction,
                  studyVersionBo,
                  null,
                  sessionObject,
                  studyBo.getId(),
                  null,
                  questionId,
                  "",
                  isChange);
          if (!message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            return message;
          }
        }
        // delete anchordate end
        if (studyVersionBo != null) {
          // doing the soft delete after study is launched
          formMappingBo.setActive(false);
          session.saveOrUpdate(formMappingBo);
          String deleteQuery =
              "Update QuestionsBo QBO set QBO.active=0,QBO.modifiedBy=:userId "
                  + ",QBO.modifiedOn=:currentDateAndTime "
                  + " where QBO.id=:questionId ";
          query =
              session
                  .createQuery(deleteQuery)
                  .setInteger("userId", sessionObject.getUserId())
                  .setString("currentDateAndTime", FdahpStudyDesignerUtil.getCurrentDateTime())
                  .setInteger("questionId", questionId);
          query.executeUpdate();

          String deleteResponse =
              "Update QuestionReponseTypeBo QRBO set QRBO.active=0 where QRBO.questionsResponseTypeId=:questionId ";
          query = session.createQuery(deleteResponse).setInteger("questionId", questionId);
          query.executeUpdate();

          String deleteSubResponse =
              "Update QuestionResponseSubTypeBo QRSBO set QRSBO.active=0 where QRSBO.responseTypeId=:questionId ";

          query = session.createQuery(deleteSubResponse).setInteger("questionId", questionId);
          query.executeUpdate();
        } else {
          // doing the hard delete before study launched
          String deleteQuery = "delete QuestionsBo QBO where QBO.id=:questionId ";
          query = session.createQuery(deleteQuery).setInteger("questionId", questionId);
          query.executeUpdate();

          String deleteResponse =
              "delete QuestionReponseTypeBo QRBO where QRBO.questionsResponseTypeId=:questionId ";
          query = session.createQuery(deleteResponse).setInteger("questionId", questionId);
          query.executeUpdate();

          String deleteSubResponse =
              "delete QuestionResponseSubTypeBo QRSBO  where QRSBO.responseTypeId=:questionId ";
          query = session.createQuery(deleteSubResponse).setInteger("questionId", questionId);
          query.executeUpdate();

          session.delete(formMappingBo);
        }
        message = FdahpStudyDesignerConstants.SUCCESS;
      }
      if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
        query =
            session
                .getNamedQuery("StudyBo.getStudyBycustomStudyId")
                .setString("customStudyId", customStudyId);
        query.setMaxResults(1);
        StudyBo study = (StudyBo) query.uniqueResult();
        auditRequest.setStudyVersion(study.getVersion().toString());
        auditRequest.setAppId(study.getAppId());
      }
      values.put(QUESTION_ID, questionId.toString());
      values.put(STEP_ID, formId.toString());
      auditLogEventHelper.logEvent(STUDY_QUESTION_STEP_DELETED, auditRequest, values);

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - deleteFromStepQuestion() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - deleteFromStepQuestion() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String deleteQuestionnaireStep(
      Integer stepId,
      Integer questionnaireId,
      String stepType,
      SessionObject sessionObject,
      String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - deleteQuestionnaireStep() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    List<QuestionnairesStepsBo> questionnaireStepList = null;
    StudyVersionBo studyVersionBo = null;
    String searchQuery = null;
    Map<String, String> values = new HashMap<>();
    StudyBuilderAuditEvent eventEnum = null;
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      auditRequest.setStudyId(customStudyId);
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      query =
          session
              .getNamedQuery("getStudyByCustomStudyId")
              .setString("customStudyId", customStudyId);
      query.setMaxResults(1);
      studyVersionBo = (StudyVersionBo) query.uniqueResult();

      // Anchordate delete based on stepId start
      if (!stepType.equalsIgnoreCase(FdahpStudyDesignerConstants.INSTRUCTION_STEP)) {
        StudyBo studyBo =
            (StudyBo)
                session
                    .createQuery("from StudyBo where customStudyId=:customStudyId and live=0 ")
                    .setString("customStudyId", customStudyId)
                    .uniqueResult();
        if (studyBo != null) {
          boolean isChange = true;
          message =
              updateAnchordateInQuestionnaire(
                  session,
                  transaction,
                  studyVersionBo,
                  questionnaireId,
                  sessionObject,
                  studyBo.getId(),
                  stepId,
                  null,
                  stepType,
                  isChange);
          if (!message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            return message;
          }
        }
      }
      // Anchordate delete based on stepId end
      query =
          session
              .getNamedQuery("StudyBo.getStudyBycustomStudyId")
              .setString("customStudyId", customStudyId);
      query.setMaxResults(1);
      StudyBo study = (StudyBo) query.uniqueResult();
      auditRequest.setStudyVersion(study.getVersion().toString());
      auditRequest.setAppId(study.getAppId());
      if (studyVersionBo != null) {
        // doing the soft delete after study launch
        searchQuery =
            "From QuestionnairesStepsBo QSBO where QSBO.instructionFormId=:stepId "
                + " and QSBO.questionnairesId=:questionnaireId "
                + " and QSBO.stepType=:stepType ";
        questionnairesStepsBo =
            (QuestionnairesStepsBo)
                session
                    .createQuery(searchQuery)
                    .setInteger("stepId", stepId)
                    .setInteger("questionnaireId", questionnaireId)
                    .setString("stepType", stepType)
                    .uniqueResult();
        if (questionnairesStepsBo != null) {

          questionnairesStepsBo.setActive(false);
          session.saveOrUpdate(questionnairesStepsBo);

          query =
              session
                  .createSQLQuery(
                      "CALL deleteQuestionnaireStep(:questionnaireId,:modifiedOn,:modifiedBy,:sequenceNo,:stepId,:steptype)")
                  .setInteger("questionnaireId", questionnaireId)
                  .setString("modifiedOn", FdahpStudyDesignerUtil.getCurrentDateTime())
                  .setInteger("modifiedBy", sessionObject.getUserId())
                  .setInteger("sequenceNo", 0)
                  .setInteger("stepId", stepId)
                  .setString("steptype", stepType);
          query.executeUpdate();

          QuestionnaireBo questionnaireBo =
              (QuestionnaireBo)
                  session
                      .createQuery("from QuestionnaireBo QBO where QBO.id=:questionnaireId")
                      .setInteger("questionnaireId", questionnaireId)
                      .uniqueResult();
          if (questionnaireBo != null) {
            values.put(QUESTION_ID, questionnaireBo.getShortTitle());
          }

          values.put(STEP_ID, questionnairesStepsBo.getStepShortTitle());

          if (questionnairesStepsBo
              .getStepType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.INSTRUCTION_STEP)) {
            eventEnum = STUDY_INSTRUCTION_STEP_DELETED;

          } else if (questionnairesStepsBo
              .getStepType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTION_STEP)) {
            eventEnum = STUDY_QUESTION_STEP_DELETED;

          } else if (questionnairesStepsBo
              .getStepType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)) {
            eventEnum = STUDY_FORM_STEP_DELETED;
          }

          auditLogEventHelper.logEvent(eventEnum, auditRequest, values);
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      } else {
        // doing the hard delete before study launch
        message =
            deleteQuestionnaireStep(
                stepId,
                questionnaireId,
                stepType,
                customStudyId,
                sessionObject,
                session,
                auditRequest,
                transaction);
      }
      // Reset destination steps in Questionnaire Starts
      searchQuery =
          "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
              + " and QSBO.active=1 order by QSBO.sequenceNo ASC";

      questionnaireStepList =
          session.createQuery(searchQuery).setInteger("questionnaireId", questionnaireId).list();
      if ((null != questionnaireStepList) && !questionnaireStepList.isEmpty()) {
        if (questionnaireStepList.size() == 1) {
          questionnaireStepList.get(0).setDestinationStep(0);
          questionnaireStepList.get(0).setSequenceNo(1);
          session.update(questionnaireStepList.get(0));
        } else {
          int i;
          for (i = 0; i < (questionnaireStepList.size() - 1); i++) {
            questionnaireStepList
                .get(i)
                .setDestinationStep(questionnaireStepList.get(i + 1).getStepId());
            questionnaireStepList.get(i).setSequenceNo(i + 1);
            session.update(questionnaireStepList.get(i));
          }
          questionnaireStepList.get(i).setDestinationStep(0);
          questionnaireStepList.get(i).setSequenceNo(i + 1);
          session.update(questionnaireStepList.get(i));
        }
      }

      String questionResponseQuery =
          "update response_sub_type_value rs,questionnaires_steps q set rs.destination_step_id = NULL "
              + "where rs.response_type_id=q.instruction_form_id and q.step_type=:stepType "
              + " and q.questionnaires_id=:questionnaireId "
              + " and rs.active=1 and q.active=1";
      query =
          session
              .createSQLQuery(questionResponseQuery)
              .setString("stepType", FdahpStudyDesignerConstants.QUESTION_STEP)
              .setInteger("questionnaireId", questionnaireId);
      query.executeUpdate();

      String questionConditionResponseQuery =
          "update questions qs,questionnaires_steps q,response_type_value rs  set qs.status = 0 where"
              + " rs.questions_response_type_id=q.instruction_form_id and q.step_type=:stepType "
              + " and q.questionnaires_id=:questionnaireId "
              + " and qs.id=q.instruction_form_id and qs.active=1 and rs.active=1 and q.active=1 and rs.formula_based_logic='Yes'";

      query =
          session
              .createSQLQuery(questionConditionResponseQuery)
              .setString("stepType", FdahpStudyDesignerConstants.QUESTION_STEP)
              .setInteger("questionnaireId", questionnaireId);
      query.executeUpdate();

      // Reset destination steps in Questionnaire Ends

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - deleteQuestionnaireStep() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - deleteQuestionnaireStep() - Ends");
    return message;
  }

  public String deleteQuestionnaireStep(
      Integer stepId,
      Integer questionnaireId,
      String stepType,
      String customStudyId,
      SessionObject sessionObject,
      Session session,
      AuditLogEventRequest auditRequest,
      Transaction transaction) {
    String message = FdahpStudyDesignerConstants.FAILURE;
    logger.info("StudyQuestionnaireDAOImpl - deleteQuestionnaireStep(session,transction) - starts");
    String searchQuery = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    Map<String, String> values = new HashMap<>();
    try {
      auditRequest.setStudyId(customStudyId);
      QuestionnaireBo questionnaireBo =
          (QuestionnaireBo)
              session
                  .createQuery("from QuestionnaireBo QBO where QBO.id=:questionnaireId")
                  .setInteger("questionnaireId", questionnaireId)
                  .uniqueResult();
      if (questionnaireBo != null) {
        values.put(QUESTION_ID, questionnaireBo.getShortTitle());
      }

      searchQuery =
          "From QuestionnairesStepsBo QSBO where QSBO.instructionFormId=:stepId "
              + " and QSBO.questionnairesId=:questionnaireId "
              + " and QSBO.stepType=:stepType ";
      questionnairesStepsBo =
          (QuestionnairesStepsBo)
              session
                  .createQuery(searchQuery)
                  .setInteger("stepId", stepId)
                  .setInteger("questionnaireId", questionnaireId)
                  .setString("stepType", stepType)
                  .uniqueResult();
      if (questionnairesStepsBo != null) {
        values.put(STEP_ID, questionnairesStepsBo.getStepShortTitle());

        String updateQuery =
            "update QuestionnairesStepsBo QSBO set QSBO.sequenceNo=QSBO.sequenceNo-1,QSBO.modifiedBy=:userId "
                + ",QSBO.modifiedOn=:currentDateAndTime "
                + " where QSBO.questionnairesId=:questionnairesId "
                + " and QSBO.active=1 and QSBO.sequenceNo >=:sequenceNo ";
        query =
            session
                .createQuery(updateQuery)
                .setInteger("userId", sessionObject.getUserId())
                .setString("currentDateAndTime", FdahpStudyDesignerUtil.getCurrentDateTime())
                .setInteger("questionnairesId", questionnairesStepsBo.getQuestionnairesId())
                .setInteger("sequenceNo", questionnairesStepsBo.getSequenceNo());
        query.executeUpdate();

        if (stepType.equalsIgnoreCase(FdahpStudyDesignerConstants.INSTRUCTION_STEP)) {
          String deleteQuery = "delete InstructionsBo IBO where IBO.id=:stepId ";
          query = session.createQuery(deleteQuery).setInteger("stepId", stepId);
          query.executeUpdate();
          auditLogEventHelper.logEvent(STUDY_INSTRUCTION_STEP_DELETED, auditRequest, values);

        } else if (stepType.equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTION_STEP)) {
          String deleteQuery = "delete QuestionsBo QBO where QBO.id=:stepId ";
          query = session.createQuery(deleteQuery).setInteger("stepId", stepId);
          query.executeUpdate();
          auditLogEventHelper.logEvent(STUDY_QUESTION_STEP_DELETED, auditRequest, values);

          String deleteResponse =
              "delete QuestionReponseTypeBo QRBO where QRBO.questionsResponseTypeId=:stepId ";
          query = session.createQuery(deleteResponse).setInteger("stepId", stepId);
          query.executeUpdate();

          String deleteSubResponse =
              "delete QuestionResponseSubTypeBo QRSBO  where QRSBO.responseTypeId=:stepId ";
          query = session.createQuery(deleteSubResponse).setInteger("stepId", stepId);
          query.executeUpdate();

        } else if (stepType.equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)) {
          String subQuery =
              "select FMBO.questionId from FormMappingBo FMBO where FMBO.formId=:stepId ";
          query = session.createQuery(subQuery).setInteger("stepId", stepId);
          if ((query.list() != null) && !query.list().isEmpty()) {
            String deleteQuery = "delete QuestionsBo QBO where QBO.id IN (" + subQuery + ")";
            query = session.createQuery(deleteQuery).setInteger("stepId", stepId);
            query.executeUpdate();

            String deleteResponse =
                "delete QuestionReponseTypeBo QRBO where QRBO.questionsResponseTypeId IN ("
                    + subQuery
                    + ")";
            query = session.createQuery(deleteResponse).setInteger("stepId", stepId);
            query.executeUpdate();

            String deleteSubResponse =
                "delete QuestionResponseSubTypeBo QRSBO  where QRSBO.responseTypeId IN ("
                    + subQuery
                    + ")";
            query = session.createQuery(deleteSubResponse).setInteger("stepId", stepId);
            query.executeUpdate();
          }

          String formMappingDelete = "delete FormMappingBo FMBO where FMBO.formId=:stepId ";
          query = session.createQuery(formMappingDelete).setInteger("stepId", stepId);
          ;
          query.executeUpdate();

          String formDelete = "delete FormBo FBO where FBO.formId=:stepId ";
          query = session.createQuery(formDelete).setInteger("stepId", stepId);
          ;
          query.executeUpdate();
          auditLogEventHelper.logEvent(STUDY_FORM_STEP_DELETED, auditRequest, values);
        }
        session.delete(questionnairesStepsBo);
        message = FdahpStudyDesignerConstants.SUCCESS;
      }
    } catch (Exception e) {
      transaction.rollback();
      logger.error(
          "StudyQuestionnaireDAOImpl - deleteQuestionnaireStep(session,transction) - ERROR ", e);
    }
    logger.info("StudyQuestionnaireDAOImpl - deleteQuestionnaireStep(session,transction) - Ends");
    return message;
  }

  @Override
  public String deleteQuestuionnaireInfo(
      Integer studyId, Integer questionnaireId, SessionObject sessionObject, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - deleteQuestuionnaireInfo() - Starts");
    Session session = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    StudyVersionBo studyVersionBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();

      query =
          session
              .getNamedQuery("getStudyByCustomStudyId")
              .setString("customStudyId", customStudyId);
      query.setMaxResults(1);
      studyVersionBo = (StudyVersionBo) query.uniqueResult();

      // delete anchordate from question start
      boolean isChange = true;
      message =
          updateAnchordateInQuestionnaire(
              session,
              transaction,
              studyVersionBo,
              questionnaireId,
              sessionObject,
              studyId,
              null,
              null,
              "",
              isChange);
      if (!message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
        return message;
      }
      // delete anchordate from question end

      if (studyVersionBo != null) {
        // doing the soft delete after study launch
        query =
            session
                .createSQLQuery(
                    "CALL deleteQuestionnaire(:questionnaireId,:modifiedOn,:modifiedBy,:studyId)")
                .setInteger("questionnaireId", questionnaireId)
                .setString("modifiedOn", FdahpStudyDesignerUtil.getCurrentDateTime())
                .setInteger("modifiedBy", sessionObject.getUserId())
                .setInteger("studyId", studyId);
        query.executeUpdate();
        message = FdahpStudyDesignerConstants.SUCCESS;
      } else {
        // doing the hard delete before study launch
        message =
            deleteQuestuionnaireInfo(studyId, questionnaireId, customStudyId, session, transaction);
      }

      queryString =
          "DELETE From NotificationBO where questionnarieId=:questionnaireId "
              + " AND notificationSent=false";
      session
          .createQuery(queryString)
          .setInteger("questionnaireId", questionnaireId)
          .executeUpdate();
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - deleteQuestuionnaireInfo() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - deleteQuestuionnaireInfo() - Ends");
    return message;
  }

  public String deleteQuestuionnaireInfo(
      Integer studyId,
      Integer questionnaireId,
      String customStudyId,
      Session session,
      Transaction transaction) {
    logger.info(
        "StudyQuestionnaireDAOImpl - deleteQuestuionnaireInfo(session,transction) - starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    int count = 0;
    try {
      String deleteInsQuery =
          "delete InstructionsBo IBO where IBO.id IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
              + " and QSBO.active=1 and QSBO.stepType=:stepType) ";
      query =
          session
              .createQuery(deleteInsQuery)
              .setInteger("questionnaireId", questionnaireId)
              .setString("stepType", FdahpStudyDesignerConstants.INSTRUCTION_STEP);
      query.executeUpdate();

      String deleteQuesQuery =
          "delete QuestionsBo QBO where QBO.id IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
              + " and QSBO.active=1 and QSBO.stepType=:stepType) ";
      query =
          session
              .createQuery(deleteQuesQuery)
              .setInteger("questionnaireId", questionnaireId)
              .setString("stepType", FdahpStudyDesignerConstants.QUESTION_STEP);
      query.executeUpdate();

      String deleteResponse =
          "delete QuestionReponseTypeBo QRBO where QRBO.questionsResponseTypeId IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
              + " and QSBO.active=1 and QSBO.stepType=:stepType) ";
      query =
          session
              .createQuery(deleteResponse)
              .setInteger("questionnaireId", questionnaireId)
              .setString("stepType", FdahpStudyDesignerConstants.QUESTION_STEP);
      query.executeUpdate();

      String deleteSubResponse =
          "delete QuestionResponseSubTypeBo QRSBO  where QRSBO.responseTypeId IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
              + " and QSBO.active=1 and QSBO.stepType=:stepType ) ";
      query =
          session
              .createQuery(deleteSubResponse)
              .setInteger("questionnaireId", questionnaireId)
              .setString("stepType", FdahpStudyDesignerConstants.QUESTION_STEP);
      query.executeUpdate();

      String subQuery =
          "select FMBO.questionId from FormMappingBo FMBO where FMBO.formId IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
              + " and QSBO.active=1 and QSBO.stepType=:stepType ) ";
      query =
          session
              .createQuery(subQuery)
              .setInteger("questionnaireId", questionnaireId)
              .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP);
      if ((query.list() != null) && !query.list().isEmpty()) {

        String deleteFormResponse =
            "delete QuestionReponseTypeBo QRBO where QRBO.questionsResponseTypeId IN ("
                + subQuery
                + ")";
        query =
            session
                .createQuery(deleteFormResponse)
                .setInteger("questionnaireId", questionnaireId)
                .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP);
        query.executeUpdate();

        String deleteFormSubResponse =
            "delete QuestionResponseSubTypeBo QRSBO  where QRSBO.responseTypeId IN ("
                + subQuery
                + ")";
        query =
            session
                .createQuery(deleteFormSubResponse)
                .setInteger("questionnaireId", questionnaireId)
                .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP);
        query.executeUpdate();

        String deleteFormQuery = "delete QuestionsBo QBO where QBO.id IN (" + subQuery + ")";
        query =
            session
                .createQuery(deleteFormQuery)
                .setInteger("questionnaireId", questionnaireId)
                .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP);
        query.executeUpdate();
      }

      String formMappingDelete =
          "delete FormMappingBo FMBO where FMBO.formId IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
              + " and QSBO.active=1 and QSBO.stepType=:stepType) ";
      query =
          session
              .createQuery(formMappingDelete)
              .setInteger("questionnaireId", questionnaireId)
              .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP);
      query.executeUpdate();

      String formDelete =
          "delete FormBo FBO where FBO.formId IN (select QSBO.instructionFormId from QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
              + " and QSBO.active=1 and QSBO.stepType=:stepType) ";
      query =
          session
              .createQuery(formDelete)
              .setInteger("questionnaireId", questionnaireId)
              .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP);
      query.executeUpdate();

      String searchQuery =
          "delete QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId";
      query = session.createQuery(searchQuery).setInteger("questionnaireId", questionnaireId);
      query.executeUpdate();

      String deletecustomFreQuery =
          "delete from questionnaires_custom_frequencies where questionnaires_id=:questionnaireId ";
      query =
          session
              .createSQLQuery(deletecustomFreQuery)
              .setInteger("questionnaireId", questionnaireId);
      query.executeUpdate();

      String deleteFreQuery =
          "delete from questionnaires_frequencies where questionnaires_id=:questionnaireId ";
      query = session.createSQLQuery(deleteFreQuery).setInteger("questionnaireId", questionnaireId);
      query.executeUpdate();

      String deleteQuery =
          "delete QuestionnaireBo QBO where QBO.studyId=:studyId "
              + " and QBO.id=:questionnaireId ";
      query =
          session
              .createQuery(deleteQuery)
              .setInteger("studyId", studyId)
              .setInteger("questionnaireId", questionnaireId);
      count = query.executeUpdate();

      if (count > 0) {
        message = FdahpStudyDesignerConstants.SUCCESS;
      }

    } catch (Exception e) {
      transaction.rollback();
      logger.error(
          "StudyQuestionnaireDAOImpl - deleteQuestuionnaireInfo(session,transction) - ERROR ", e);
    }
    logger.info("StudyQuestionnaireDAOImpl - deleteQuestuionnaireInfo(session,transction) - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<HealthKitKeysInfo> getHeanlthKitKeyInfoList() {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionReponseTypeList() - Starts");
    Session session = null;
    List<HealthKitKeysInfo> healthKitKeysInfoList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.getNamedQuery("getHealthKitKeyInfo");
      healthKitKeysInfoList = query.list();
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getQuestionReponseTypeList() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getQuestionReponseTypeList() - Ends");
    return healthKitKeysInfoList;
  }

  @Override
  public InstructionsBo getInstructionsBo(
      Integer instructionId,
      String questionnaireShortTitle,
      String customStudyId,
      Integer questionnaireId) {
    logger.info("StudyQuestionnaireDAOImpl - getInstructionsBo - Starts");
    Session session = null;
    InstructionsBo instructionsBo = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      instructionsBo = (InstructionsBo) session.get(InstructionsBo.class, instructionId);
      if (instructionsBo != null) {
        if (questionnaireId != null) {
          query =
              session
                  .createQuery(
                      "From QuestionnairesStepsBo QSBO where QSBO.instructionFormId=:instructionFormId "
                          + " and QSBO.stepType=:stepType "
                          + " and QSBO.active=1 and QSBO.questionnairesId=:questionnaireId ")
                  .setInteger("instructionFormId", instructionsBo.getId())
                  .setInteger("questionnaireId", questionnaireId)
                  .setString("stepType", FdahpStudyDesignerConstants.INSTRUCTION_STEP);
        } else {
          query =
              session
                  .getNamedQuery("getQuestionnaireStep")
                  .setInteger("instructionFormId", instructionsBo.getId())
                  .setString("stepType", FdahpStudyDesignerConstants.INSTRUCTION_STEP);
        }
        questionnairesStepsBo = (QuestionnairesStepsBo) query.uniqueResult();

        if (StringUtils.isNotEmpty(questionnaireShortTitle)) {
          // Duplicate ShortTitle per QuestionnaireStepBo Start
          BigInteger shortTitleCount =
              (BigInteger)
                  session
                      .createSQLQuery(
                          "select count(*) from questionnaires_steps qs where qs.questionnaires_id  "
                              + " in(select q.id from questionnaires q where q.short_title=:questionnaireShortTitle "
                              + " and q.active=1 and q.is_live=1 and q.custom_study_id=:customStudyId ) "
                              + "and qs.step_short_title = :shortTitle "
                              + " and qs.active=1")
                      .setString("questionnaireShortTitle", questionnaireShortTitle)
                      .setString("customStudyId", customStudyId)
                      .setString("shortTitle", questionnairesStepsBo.getStepShortTitle())
                      .uniqueResult();
          if ((shortTitleCount != null) && (shortTitleCount.intValue() > 0)) {
            questionnairesStepsBo.setIsShorTitleDuplicate(shortTitleCount.intValue());
          } else {
            questionnairesStepsBo.setIsShorTitleDuplicate(0);
          }
        } else {
          questionnairesStepsBo.setIsShorTitleDuplicate(0);
        }
        // Duplicate ShortTitle per QuestionnaireStepBo End
        instructionsBo.setQuestionnairesStepsBo(questionnairesStepsBo);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getInstructionsBo() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getInstructionsBo - Ends");
    return instructionsBo;
  }

  @Override
  @SuppressWarnings({"unchecked"})
  public List<QuestionConditionBranchBo> getQuestionConditionalBranchingLogic(
      Session session, Integer questionId) {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionConditionalBranchingLogic() - Starts");
    List<QuestionConditionBranchBo> questionConditionBranchList = null;
    List<QuestionConditionBranchBo> newQuestionConditionBranchList = null;
    Session newSession = null;
    try {
      if (session == null) {
        newSession = hibernateTemplate.getSessionFactory().openSession();
      }
      String searchQuery =
          "From QuestionConditionBranchBo QCBO where QCBO.questionId=:questionId "
              + " order by QCBO.sequenceNo ASC";
      if (newSession != null) {
        query = newSession.createQuery(searchQuery).setInteger("questionId", questionId);
      } else {
        query = session.createQuery(searchQuery).setInteger("questionId", questionId);
      }
      questionConditionBranchList = query.list();
      if (session == null) {
        newQuestionConditionBranchList = new ArrayList<>();
        newQuestionConditionBranchList = questionConditionBranchList;
      } else {
        if ((questionConditionBranchList != null) && !questionConditionBranchList.isEmpty()) {
          newQuestionConditionBranchList = new ArrayList<>();
          for (QuestionConditionBranchBo questionConditionBranchBo : questionConditionBranchList) {
            if ((questionConditionBranchBo.getInputType() != null)
                && (questionConditionBranchBo.getInputType().equalsIgnoreCase("MF")
                    || questionConditionBranchBo.getInputType().equalsIgnoreCase("F"))) {
              List<QuestionConditionBranchBo> conditionBranchList = new ArrayList<>();
              for (QuestionConditionBranchBo conditionBranchBo : questionConditionBranchList) {
                if (questionConditionBranchBo
                    .getSequenceNo()
                    .equals(conditionBranchBo.getParentSequenceNo())) {
                  conditionBranchList.add(conditionBranchBo);
                }
              }
              questionConditionBranchBo.setQuestionConditionBranchBos(conditionBranchList);
              newQuestionConditionBranchList.add(questionConditionBranchBo);
            }
          }
        }
      }
    } catch (Exception e) {
      logger.error(
          "StudyQuestionnaireDAOImpl - getQuestionConditionalBranchingLogic() - ERROR ", e);
    } finally {
      if (null != newSession) {
        newSession.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getQuestionConditionalBranchingLogic() - Ends");
    return newQuestionConditionBranchList;
  }

  @SuppressWarnings("unchecked")
  @Override
  public QuestionnaireBo getQuestionnaireById(Integer questionnaireId, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionnaireById() - Starts");
    Session session = null;
    QuestionnaireBo questionnaireBo = null;

    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      questionnaireBo = (QuestionnaireBo) session.get(QuestionnaireBo.class, questionnaireId);
      if (null != questionnaireBo) {
        if (StringUtils.isNotEmpty(customStudyId)) {
          // Duplicate ShortTitle per QuestionnaireBo Start
          BigInteger shortTitleCount =
              (BigInteger)
                  session
                      .createSQLQuery(
                          "select count(*) from questionnaires "
                              + "where short_title=:shortTilte "
                              + " and custom_study_id =:customStudyId"
                              + " and active=1 and is_live=1")
                      .setString("shortTilte", questionnaireBo.getShortTitle())
                      .setString("customStudyId", customStudyId)
                      .uniqueResult();
          if ((shortTitleCount != null)
              && (shortTitleCount.intValue() > 0)
              && questionnaireBo.getScheduleType().equals("AnchorDate")) {
            questionnaireBo.setShortTitleDuplicate(shortTitleCount.intValue());
          } else if ((shortTitleCount != null)
              && (shortTitleCount.intValue() > 0)
              && questionnaireBo.getScheduleType().equals("Regular")) {
            if (questionnaireBo
                    .getFrequency()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)
                || questionnaireBo
                    .getFrequency()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MONTHLY)
                || questionnaireBo
                    .getFrequency()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)
                || questionnaireBo
                    .getFrequency()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_WEEKLY)
                || questionnaireBo
                    .getFrequency()
                    .equalsIgnoreCase(
                        FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
              questionnaireBo.setShortTitleDuplicate(shortTitleCount.intValue());
            } else {
              questionnaireBo.setShortTitleDuplicate(0);
            }
          } else {
            questionnaireBo.setShortTitleDuplicate(0);
          }

        } else {
          questionnaireBo.setShortTitleDuplicate(0);
        }
        // Duplicate ShortTitle per QuestionnaireBo End
        String searchQuery = "";
        if ((null != questionnaireBo.getFrequency()) && !questionnaireBo.getFrequency().isEmpty()) {
          if (questionnaireBo
              .getFrequency()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
            searchQuery =
                "From QuestionnaireCustomScheduleBo QCSBO where QCSBO.questionnairesId=:questionnairesId ";
            query =
                session
                    .createQuery(searchQuery)
                    .setInteger("questionnairesId", questionnaireBo.getId());
            List<QuestionnaireCustomScheduleBo> questionnaireCustomScheduleList = query.list();
            questionnaireBo.setQuestionnaireCustomScheduleBo(questionnaireCustomScheduleList);
          } else {
            searchQuery =
                "From QuestionnairesFrequenciesBo QFBO where QFBO.questionnairesId=:questionnairesId ";
            query =
                session
                    .createQuery(searchQuery)
                    .setInteger("questionnairesId", questionnaireBo.getId());
            if (questionnaireBo
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {
              List<QuestionnairesFrequenciesBo> questionnairesFrequenciesList = query.list();
              questionnaireBo.setQuestionnairesFrequenciesList(questionnairesFrequenciesList);
            } else {
              QuestionnairesFrequenciesBo questionnairesFrequenciesBo =
                  (QuestionnairesFrequenciesBo) query.uniqueResult();
              questionnaireBo.setQuestionnairesFrequenciesBo(questionnairesFrequenciesBo);
            }
          }
        }
        if (questionnaireBo.getVersion() != null) {
          questionnaireBo.setQuestionnarieVersion(" (V" + questionnaireBo.getVersion() + ")");
        }
      }

    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getQuestionnaireById() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getQuestionnaireById() - Ends");
    return questionnaireBo;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<QuestionnairesStepsBo> getQuestionnairesStepsList(
      Integer questionnaireId, Integer sequenceNo) {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionnaireStepList() - Starts");
    Session session = null;
    List<QuestionnairesStepsBo> questionnairesStepsList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session
              .getNamedQuery("getForwardQuestionnaireSteps")
              .setInteger("questionnairesId", questionnaireId)
              .setInteger("sequenceNo", sequenceNo);
      questionnairesStepsList = query.list();
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - deleteFromStepQuestion() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    return questionnairesStepsList;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public QuestionnairesStepsBo getQuestionnaireStep(
      Integer stepId,
      String stepType,
      String questionnaireShortTitle,
      String customStudyId,
      Integer questionnaireId) {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionnaireStep() - Starts");
    Session session = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (questionnaireId != null) {
        query =
            session
                .createQuery(
                    "From QuestionnairesStepsBo QSBO where QSBO.instructionFormId=:instructionFormId "
                        + " and QSBO.stepType=:stepType "
                        + " and QSBO.active=1 and QSBO.questionnairesId=:questionnaireId ")
                .setInteger("instructionFormId", stepId)
                .setString("stepType", stepType)
                .setInteger("questionnaireId", questionnaireId);
      } else {
        query =
            session
                .getNamedQuery("getQuestionnaireStep")
                .setInteger("instructionFormId", stepId)
                .setString("stepType", stepType);
      }
      questionnairesStepsBo = (QuestionnairesStepsBo) query.uniqueResult();
      if ((null != questionnairesStepsBo) && (questionnairesStepsBo.getStepType() != null)) {
        if (StringUtils.isNotEmpty(questionnaireShortTitle)) {
          // Duplicate ShortTitle per QuestionnaireStepBo Start
          BigInteger shortTitleCount =
              (BigInteger)
                  session
                      .createSQLQuery(
                          "select count(*) from questionnaires_steps qs where qs.questionnaires_id  "
                              + " in(select q.id from questionnaires q where q.short_title=:questionnaireShortTitle "
                              + " and q.active=1 and q.is_live=1 and q.custom_study_id=:customStudyId )"
                              + " and qs.step_short_title=:shortTitle ")
                      .setString("questionnaireShortTitle", questionnaireShortTitle)
                      .setString("customStudyId", customStudyId)
                      .setString("shortTitle", questionnairesStepsBo.getStepShortTitle())
                      .uniqueResult();
          if ((shortTitleCount != null) && (shortTitleCount.intValue() > 0)) {
            questionnairesStepsBo.setIsShorTitleDuplicate(shortTitleCount.intValue());
          } else {
            questionnairesStepsBo.setIsShorTitleDuplicate(0);
          }
        } else {
          questionnairesStepsBo.setIsShorTitleDuplicate(0);
        }
        // Duplicate ShortTitle per QuestionnaireStepBo End

        if (questionnairesStepsBo
            .getStepType()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTION_STEP)) {
          // get the one question step of questionnaire
          QuestionsBo questionsBo = null;
          query = session.getNamedQuery("getQuestionStep").setInteger("stepId", stepId);
          questionsBo = (QuestionsBo) query.uniqueResult();
          if ((questionsBo != null) && (questionsBo.getId() != null)) {
            if (StringUtils.isNotEmpty(questionnaireShortTitle)) {
              // Duplicate statShortTitle per questionsBo Start
              if (StringUtils.isNotEmpty(questionsBo.getStatShortName())) {
                BigInteger quesionStatshortTitleCount =
                    (BigInteger)
                        session
                            .createSQLQuery(
                                "select count(*) From questions QBO,questionnaires_steps QSBO,questionnaires Q where QBO.id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.short_title=:questionnaireShortTitle "
                                    + " and Q.active=1 and Q.is_live=1 and Q.custom_study_id=:customStudyId "
                                    + " and QSBO.step_type='Question' and QBO.stat_short_name=:shortName "
                                    + " and QBO.active=1")
                            .setString("questionnaireShortTitle", questionnaireShortTitle)
                            .setString("customStudyId", customStudyId)
                            .setString("shortName", questionsBo.getStatShortName())
                            .uniqueResult();
                if ((quesionStatshortTitleCount != null)
                    && (quesionStatshortTitleCount.intValue() > 0)) {
                  questionsBo.setIsStatShortNameDuplicate(quesionStatshortTitleCount.intValue());
                } else {
                  questionsBo.setIsStatShortNameDuplicate(0);
                }
              }
            } else {
              questionsBo.setIsStatShortNameDuplicate(0);
            }
            // Duplicate statShortTitle per questionsBo End

            // get the response level attributes values of an
            // questions
            QuestionReponseTypeBo questionReponseTypeBo = null;
            logger.info(
                "StudyQuestionnaireDAOImpl - getQuestionnaireStep() - questionsResponseTypeId:"
                    + questionsBo.getId());
            query =
                session
                    .getNamedQuery("getQuestionResponse")
                    .setInteger("questionsResponseTypeId", questionsBo.getId());
            query.setMaxResults(1);
            questionReponseTypeBo = (QuestionReponseTypeBo) query.uniqueResult();
            if ((questionReponseTypeBo != null)
                && (questionReponseTypeBo.getStyle() != null)
                && StringUtils.isNotEmpty(questionReponseTypeBo.getStyle())) {
              questionReponseTypeBo.setStyle(questionReponseTypeBo.getStyle());
              if ((FdahpStudyDesignerConstants.DATE)
                  .equalsIgnoreCase(questionReponseTypeBo.getStyle())) {
                if ((questionReponseTypeBo.getMinDate() != null)
                    && StringUtils.isNotEmpty(questionReponseTypeBo.getMinDate())) {
                  questionReponseTypeBo.setMinDate(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          questionReponseTypeBo.getMinDate(),
                          FdahpStudyDesignerConstants.DB_SDF_DATE,
                          FdahpStudyDesignerConstants.UI_SDF_DATE));
                }
                if ((questionReponseTypeBo.getMaxDate() != null)
                    && StringUtils.isNotEmpty(questionReponseTypeBo.getMaxDate())) {
                  questionReponseTypeBo.setMaxDate(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          questionReponseTypeBo.getMaxDate(),
                          FdahpStudyDesignerConstants.DB_SDF_DATE,
                          FdahpStudyDesignerConstants.UI_SDF_DATE));
                }
                if ((questionReponseTypeBo.getDefaultDate() != null)
                    && StringUtils.isNotEmpty(questionReponseTypeBo.getDefaultDate())) {
                  questionReponseTypeBo.setDefaultDate(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          questionReponseTypeBo.getDefaultDate(),
                          FdahpStudyDesignerConstants.DB_SDF_DATE,
                          FdahpStudyDesignerConstants.UI_SDF_DATE));
                }
              } else if ((FdahpStudyDesignerConstants.DATE_TIME)
                  .equalsIgnoreCase(questionReponseTypeBo.getStyle())) {
                if ((questionReponseTypeBo.getMinDate() != null)
                    && StringUtils.isNotEmpty(questionReponseTypeBo.getMinDate())) {
                  questionReponseTypeBo.setMinDate(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          questionReponseTypeBo.getMinDate(),
                          FdahpStudyDesignerConstants.DB_SDF_DATE_TIME,
                          FdahpStudyDesignerConstants.REQUIRED_DATE_TIME));
                }
                if ((questionReponseTypeBo.getMaxDate() != null)
                    && StringUtils.isNotEmpty(questionReponseTypeBo.getMaxDate())) {
                  questionReponseTypeBo.setMaxDate(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          questionReponseTypeBo.getMaxDate(),
                          FdahpStudyDesignerConstants.DB_SDF_DATE_TIME,
                          FdahpStudyDesignerConstants.REQUIRED_DATE_TIME));
                }
                if ((questionReponseTypeBo.getDefaultDate() != null)
                    && StringUtils.isNotEmpty(questionReponseTypeBo.getDefaultDate())) {
                  questionReponseTypeBo.setDefaultDate(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          questionReponseTypeBo.getDefaultDate(),
                          FdahpStudyDesignerConstants.DB_SDF_DATE_TIME,
                          FdahpStudyDesignerConstants.REQUIRED_DATE_TIME));
                }
              }
            }
            if ((questionReponseTypeBo != null)
                && (questionReponseTypeBo.getFormulaBasedLogic() != null)
                && questionReponseTypeBo
                    .getFormulaBasedLogic()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
              List<QuestionConditionBranchBo> questionConditionBranchList =
                  getQuestionConditionalBranchingLogic(session, questionsBo.getId());
              questionnairesStepsBo.setQuestionConditionBranchBoList(questionConditionBranchList);
            }
            questionnairesStepsBo.setQuestionReponseTypeBo(questionReponseTypeBo);

            List<QuestionResponseSubTypeBo> questionResponseSubTypeList = null;
            query =
                session
                    .getNamedQuery("getQuestionSubResponse")
                    .setInteger("responseTypeId", questionsBo.getId());
            questionResponseSubTypeList = query.list();
            // appending the current date time to the image url
            if ((null != questionResponseSubTypeList) && !questionResponseSubTypeList.isEmpty()) {
              for (QuestionResponseSubTypeBo s : questionResponseSubTypeList) {
                if (FdahpStudyDesignerUtil.isNotEmpty(s.getImage())) {
                  if (s.getImage().contains("?v=")) {
                    String imagePathArr[] = s.getImage().split("\\?");
                    s.setImage(imagePathArr[0] + "?v=" + new Date().getTime());
                  } else {
                    s.setImage(s.getImage() + "?v=" + new Date().getTime());
                  }
                }
                if (FdahpStudyDesignerUtil.isNotEmpty(s.getSelectedImage())) {
                  if (s.getSelectedImage().contains("?v=")) {
                    String imagePathArr[] = s.getSelectedImage().split("\\?");
                    s.setSelectedImage(imagePathArr[0] + "?v=" + new Date().getTime());
                  } else {
                    s.setSelectedImage(s.getSelectedImage() + "?v=" + new Date().getTime());
                  }
                }
              }
            }
            questionnairesStepsBo.setQuestionResponseSubTypeList(questionResponseSubTypeList);

            // Phase 2a ancordate start
            if (questionsBo.getAnchorDateId() != null) {
              String name =
                  (String)
                      session
                          .createSQLQuery(
                              "select name from anchordate_type where id=:anchorDateId ")
                          .setInteger("anchorDateId", questionsBo.getAnchorDateId())
                          .uniqueResult();
              questionsBo.setAnchorDateName(name);
            }
            // phase 2a anchordate end

          }
          questionnairesStepsBo.setQuestionsBo(questionsBo);

        } else if (questionnairesStepsBo
            .getStepType()
            .equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)) {
          // get the one from step of an questionnaire
          String fromQuery =
              "select f.form_id,f.question_id,f.sequence_no, q.id, q.question,q.response_type,q.add_line_chart,q.use_stastic_data,q.status,q.use_anchor_date from questions q, form_mapping f where q.id=f.question_id and f.form_id=:stepId "
                  + " and f.active=1 order by f.form_id";
          Iterator iterator =
              session.createSQLQuery(fromQuery).setInteger("stepId", stepId).list().iterator();
          TreeMap<Integer, QuestionnaireStepBean> formQuestionMap = new TreeMap<>();
          boolean isDone = true;
          while (iterator.hasNext()) {
            Object[] objects = (Object[]) iterator.next();
            Integer formId = (Integer) objects[0];
            Integer sequenceNo = (Integer) objects[2];
            Integer questionId = (Integer) objects[3];
            String questionText = (String) objects[4];
            Integer responseType = (Integer) objects[5];
            String lineChart = (String) objects[6];
            String statData = (String) objects[7];
            Boolean status = (Boolean) objects[8];
            Boolean useAnchorDate = (Boolean) objects[9];
            QuestionnaireStepBean questionnaireStepBean = new QuestionnaireStepBean();
            questionnaireStepBean.setStepId(formId);
            questionnaireStepBean.setQuestionInstructionId(questionId);
            questionnaireStepBean.setTitle(questionText);
            questionnaireStepBean.setSequenceNo(sequenceNo);
            questionnaireStepBean.setStepType(FdahpStudyDesignerConstants.FORM_STEP);
            questionnaireStepBean.setResponseType(responseType);
            questionnaireStepBean.setLineChart(lineChart);
            questionnaireStepBean.setStatData(statData);
            questionnaireStepBean.setStatus(status);
            questionnaireStepBean.setUseAnchorDate(useAnchorDate);
            formQuestionMap.put(sequenceNo, questionnaireStepBean);
            if (!status) {
              isDone = false;
            }
          }
          questionnairesStepsBo.setStatus(isDone);
          questionnairesStepsBo.setFormQuestionMap(formQuestionMap);
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getQuestionnaireStep() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getQuestionnaireStep() - Ends");
    return questionnairesStepsBo;
  }

  @SuppressWarnings("unchecked")
  @Override
  public SortedMap<Integer, QuestionnaireStepBean> getQuestionnaireStepList(
      Integer questionnaireId) {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionnaireStepList() - Ends");
    Session session = null;
    List<QuestionnairesStepsBo> questionnairesStepsList = null;
    Map<String, Integer> sequenceNoMap = new HashMap<>();
    SortedMap<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<>();
    Map<Integer, String> destinationText = new HashMap<>();
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query =
          session
              .getNamedQuery("getQuestionnaireStepList")
              .setInteger("questionnaireId", questionnaireId);
      questionnairesStepsList = query.list();
      List<Integer> instructionIdList = new ArrayList<>();
      List<Integer> questionIdList = new ArrayList<>();
      List<Integer> formIdList = new ArrayList<>();
      Map<String, Integer> destinationMap = new HashMap<>();
      Map<Integer, Boolean> formStatusMap = new HashMap<>();
      destinationText.put(0, "Completion Step");
      // setting the sequenceNo and destination steps to the map based on
      // the individual steps later using this map to set the destination
      // step name
      for (QuestionnairesStepsBo questionaireSteps : questionnairesStepsList) {
        destinationText.put(
            questionaireSteps.getStepId(),
            questionaireSteps.getSequenceNo() + ":" + questionaireSteps.getStepShortTitle());
        switch (questionaireSteps.getStepType()) {
          case FdahpStudyDesignerConstants.INSTRUCTION_STEP:
            instructionIdList.add(questionaireSteps.getInstructionFormId());
            sequenceNoMap.put(
                String.valueOf(questionaireSteps.getInstructionFormId())
                    + FdahpStudyDesignerConstants.INSTRUCTION_STEP,
                questionaireSteps.getSequenceNo());
            destinationMap.put(
                String.valueOf(questionaireSteps.getInstructionFormId())
                    + FdahpStudyDesignerConstants.INSTRUCTION_STEP,
                questionaireSteps.getDestinationStep());
            break;
          case FdahpStudyDesignerConstants.QUESTION_STEP:
            questionIdList.add(questionaireSteps.getInstructionFormId());
            sequenceNoMap.put(
                String.valueOf(questionaireSteps.getInstructionFormId())
                    + FdahpStudyDesignerConstants.QUESTION_STEP,
                questionaireSteps.getSequenceNo());
            destinationMap.put(
                String.valueOf(questionaireSteps.getInstructionFormId())
                    + FdahpStudyDesignerConstants.QUESTION_STEP,
                questionaireSteps.getDestinationStep());
            break;
          case FdahpStudyDesignerConstants.FORM_STEP:
            formIdList.add(questionaireSteps.getInstructionFormId());
            sequenceNoMap.put(
                String.valueOf(questionaireSteps.getInstructionFormId())
                    + FdahpStudyDesignerConstants.FORM_STEP,
                questionaireSteps.getSequenceNo());
            destinationMap.put(
                String.valueOf(questionaireSteps.getInstructionFormId())
                    + FdahpStudyDesignerConstants.FORM_STEP,
                questionaireSteps.getDestinationStep());
            formStatusMap.put(
                questionaireSteps.getInstructionFormId(), questionaireSteps.getStatus());
            break;
          default:
            break;
        }
      }
      // get the list of instruction step in questionnaire
      if (!instructionIdList.isEmpty()) {
        List<InstructionsBo> instructionsList = null;
        query =
            session.createQuery(
                " from InstructionsBo IBO where IBO.active=1 and IBO.id in ( :instructionIdList )");
        instructionsList = query.setParameterList("instructionIdList", instructionIdList).list();
        if ((instructionsList != null) && !instructionsList.isEmpty()) {
          for (InstructionsBo instructionsBo : instructionsList) {
            QuestionnaireStepBean questionnaireStepBean = new QuestionnaireStepBean();
            questionnaireStepBean.setStepId(instructionsBo.getId());
            questionnaireStepBean.setStepType(FdahpStudyDesignerConstants.INSTRUCTION_STEP);
            questionnaireStepBean.setSequenceNo(
                sequenceNoMap.get(
                    instructionsBo.getId() + FdahpStudyDesignerConstants.INSTRUCTION_STEP));
            questionnaireStepBean.setTitle(instructionsBo.getInstructionTitle());
            questionnaireStepBean.setStatus(instructionsBo.getStatus());
            questionnaireStepBean.setDestinationStep(
                destinationMap.get(
                    instructionsBo.getId() + FdahpStudyDesignerConstants.INSTRUCTION_STEP));
            questionnaireStepBean.setDestinationText(
                destinationText.get(
                    destinationMap.get(
                        instructionsBo.getId() + FdahpStudyDesignerConstants.INSTRUCTION_STEP)));
            qTreeMap.put(
                sequenceNoMap.get(
                    instructionsBo.getId() + FdahpStudyDesignerConstants.INSTRUCTION_STEP),
                questionnaireStepBean);
          }
        }
      }
      // get the list of question step inside the questionnaire
      if (!questionIdList.isEmpty()) {
        List<QuestionsBo> questionsList = null;
        query =
            session.createQuery(
                " from QuestionsBo QBO where QBO.active=1 and QBO.id in ( :questionIdList )");

        questionsList = query.setParameterList("questionIdList", questionIdList).list();
        if ((questionsList != null) && !questionsList.isEmpty()) {
          for (QuestionsBo questionsBo : questionsList) {
            QuestionnaireStepBean questionnaireStepBean = new QuestionnaireStepBean();
            questionnaireStepBean.setStepId(questionsBo.getId());
            questionnaireStepBean.setStepType(FdahpStudyDesignerConstants.QUESTION_STEP);
            questionnaireStepBean.setSequenceNo(
                sequenceNoMap.get(questionsBo.getId() + FdahpStudyDesignerConstants.QUESTION_STEP));
            questionnaireStepBean.setTitle(questionsBo.getQuestion());
            questionnaireStepBean.setResponseType(questionsBo.getResponseType());
            questionnaireStepBean.setLineChart(questionsBo.getAddLineChart());
            questionnaireStepBean.setStatData(questionsBo.getUseStasticData());
            questionnaireStepBean.setStatus(questionsBo.getStatus());
            questionnaireStepBean.setDestinationStep(
                destinationMap.get(
                    questionsBo.getId() + FdahpStudyDesignerConstants.QUESTION_STEP));
            questionnaireStepBean.setUseAnchorDate(questionsBo.getUseAnchorDate());
            questionnaireStepBean.setDestinationText(
                destinationText.get(
                    destinationMap.get(
                        questionsBo.getId() + FdahpStudyDesignerConstants.QUESTION_STEP)));
            qTreeMap.put(
                sequenceNoMap.get(questionsBo.getId() + FdahpStudyDesignerConstants.QUESTION_STEP),
                questionnaireStepBean);
          }
        }
      }
      // get the list of form step which contains the multiple questions
      // of questionnaire
      if (!formIdList.isEmpty()) {
        String fromQuery =
            "select f.form_id,f.question_id,f.sequence_no, q.id, q.question,q.response_type,q.add_line_chart,q.use_stastic_data,q.status,q.use_anchor_date from questions q, form_mapping f where q.id=f.question_id and q.active=1 and f.form_id IN ("
                + " :formIdList ) and f.active=1 order by f.form_id";
        List<?> result =
            session.createSQLQuery(fromQuery).setParameterList("formIdList", formIdList).list();
        for (int i = 0; i < formIdList.size(); i++) {
          QuestionnaireStepBean fQuestionnaireStepBean = new QuestionnaireStepBean();
          TreeMap<Integer, QuestionnaireStepBean> formQuestionMap = new TreeMap<>();
          for (int j = 0; j < result.size(); j++) {
            Object[] objects = (Object[]) result.get(j);
            Integer formId = (Integer) objects[0];
            Integer sequenceNo = (Integer) objects[2];
            Integer questionId = (Integer) objects[3];
            String questionText = (String) objects[4];
            Integer responseType = (Integer) objects[5];
            String lineChart = (String) objects[6];
            String statData = (String) objects[7];
            Boolean status = (Boolean) objects[8];
            Boolean useAnchorDate = (Boolean) objects[9];
            if (formIdList.get(i).equals(formId)) {
              QuestionnaireStepBean questionnaireStepBean = new QuestionnaireStepBean();
              questionnaireStepBean.setStepId(formId);
              questionnaireStepBean.setQuestionInstructionId(questionId);
              questionnaireStepBean.setTitle(questionText);
              questionnaireStepBean.setSequenceNo(sequenceNo);
              questionnaireStepBean.setStepType(FdahpStudyDesignerConstants.FORM_STEP);
              questionnaireStepBean.setResponseType(responseType);
              questionnaireStepBean.setLineChart(lineChart);
              questionnaireStepBean.setStatData(statData);
              questionnaireStepBean.setStatus(status);
              questionnaireStepBean.setUseAnchorDate(useAnchorDate);
              formQuestionMap.put(sequenceNo, questionnaireStepBean);
            }
          }
          fQuestionnaireStepBean.setStepId(formIdList.get(i));
          fQuestionnaireStepBean.setStepType(FdahpStudyDesignerConstants.FORM_STEP);
          fQuestionnaireStepBean.setSequenceNo(
              sequenceNoMap.get(formIdList.get(i) + FdahpStudyDesignerConstants.FORM_STEP));
          fQuestionnaireStepBean.setFromMap(formQuestionMap);
          fQuestionnaireStepBean.setStatus(formStatusMap.get(formIdList.get(i)));
          fQuestionnaireStepBean.setDestinationStep(
              destinationMap.get(formIdList.get(i) + FdahpStudyDesignerConstants.FORM_STEP));
          fQuestionnaireStepBean.setDestinationText(
              destinationText.get(
                  destinationMap.get(formIdList.get(i) + FdahpStudyDesignerConstants.FORM_STEP)));
          qTreeMap.put(
              sequenceNoMap.get(formIdList.get(i) + FdahpStudyDesignerConstants.FORM_STEP),
              fQuestionnaireStepBean);
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getQuestionnaireStepList() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getQuestionnaireStepList() - Ends");
    return qTreeMap;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<QuestionResponseTypeMasterInfoBo> getQuestionReponseTypeList() {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionReponseTypeList() - Starts");
    Session session = null;
    List<QuestionResponseTypeMasterInfoBo> questionResponseTypeMasterInfoBos = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      query = session.getNamedQuery("getResponseTypes");
      questionResponseTypeMasterInfoBos = query.list();
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getQuestionReponseTypeList() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getQuestionReponseTypeList() - Ends");
    return questionResponseTypeMasterInfoBos;
  }

  @SuppressWarnings("unchecked")
  @Override
  public QuestionsBo getQuestionsById(
      Integer questionId, String questionnaireShortTitle, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionsById() - Starts");
    Session session = null;
    QuestionsBo questionsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      questionsBo = (QuestionsBo) session.get(QuestionsBo.class, questionId);
      if (questionsBo != null) {
        try {
          if (StringUtils.isNotEmpty(questionnaireShortTitle)) {
            // Duplicate ShortTitle per questionsBo Start
            BigInteger quesionshortTitleCount =
                (BigInteger)
                    session
                        .createSQLQuery(
                            "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.short_title=:questionnaireShortTitle "
                                + " and Q.active=1 and Q.is_live=1 and Q.custom_study_id=:customStudyId "
                                + " and QSBO.step_type='Form' and QBO.short_title=:shortTitle "
                                + " and QBO.active=1")
                        .setString("questionnaireShortTitle", questionnaireShortTitle)
                        .setString("customStudyId", customStudyId)
                        .setString("shortTitle", questionsBo.getShortTitle())
                        .uniqueResult();
            if ((quesionshortTitleCount != null) && (quesionshortTitleCount.intValue() > 0)) {
              questionsBo.setIsShorTitleDuplicate(quesionshortTitleCount.intValue());
            } else {
              questionsBo.setIsShorTitleDuplicate(0);
              // Duplicate ShortTitle per questionsBo End
            }

            // Duplicate statShortTitle per questionsBo Start
            if (StringUtils.isNotEmpty(questionsBo.getStatShortName())) {
              BigInteger quesionStatshortTitleCount =
                  (BigInteger)
                      session
                          .createSQLQuery(
                              "select count(*) From questions QBO,form_mapping f,questionnaires_steps QSBO,questionnaires Q where QBO.id=f.question_id and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=Q.id and Q.short_title=:questionnaireShortTitle "
                                  + " and Q.active=1 and Q.is_live=1 and Q.custom_study_id=:customStudyId "
                                  + " and QSBO.step_type='Form' and QBO.stat_short_name=:shortTitle "
                                  + " and QBO.active=1")
                          .setString("questionnaireShortTitle", questionnaireShortTitle)
                          .setString("customStudyId", customStudyId)
                          .setString("shortTitle", questionsBo.getStatShortName())
                          .uniqueResult();
              if ((quesionStatshortTitleCount != null)
                  && (quesionStatshortTitleCount.intValue() > 0)) {
                questionsBo.setIsStatShortNameDuplicate(quesionStatshortTitleCount.intValue());
              } else {
                questionsBo.setIsStatShortNameDuplicate(0);
              }
            }

            // Duplicate statShortTitle per questionsBo Ends
          } else {
            questionsBo.setIsStatShortNameDuplicate(0);
            questionsBo.setIsShorTitleDuplicate(0);
          }
        } catch (Exception e) {
          logger.error("StudyQuestionnaireDAOImpl - getQuestionsById() - SUB  ERROR ", e);
        }
        QuestionReponseTypeBo questionReponseTypeBo = null;
        logger.info(
            "StudyQuestionnaireDAOImpl - getQuestionnaireStep() - questionsResponseTypeId:"
                + questionsBo.getId());
        query =
            session
                .getNamedQuery("getQuestionResponse")
                .setInteger("questionsResponseTypeId", questionsBo.getId());
        query.setMaxResults(1);
        questionReponseTypeBo = (QuestionReponseTypeBo) query.uniqueResult();
        if ((questionReponseTypeBo != null)
            && (questionReponseTypeBo.getStyle() != null)
            && StringUtils.isNotEmpty(questionReponseTypeBo.getStyle())) {
          questionReponseTypeBo.setStyle(questionReponseTypeBo.getStyle());
          // changing the date format to database date format
          if ((FdahpStudyDesignerConstants.DATE)
              .equalsIgnoreCase(questionReponseTypeBo.getStyle())) {
            if ((questionReponseTypeBo.getMinDate() != null)
                && StringUtils.isNotEmpty(questionReponseTypeBo.getMinDate())) {
              questionReponseTypeBo.setMinDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionReponseTypeBo.getMinDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
            }
            if ((questionReponseTypeBo.getMaxDate() != null)
                && StringUtils.isNotEmpty(questionReponseTypeBo.getMaxDate())) {
              questionReponseTypeBo.setMaxDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionReponseTypeBo.getMaxDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
            }
            if ((questionReponseTypeBo.getDefaultDate() != null)
                && StringUtils.isNotEmpty(questionReponseTypeBo.getDefaultDate())) {
              questionReponseTypeBo.setDefaultDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionReponseTypeBo.getDefaultDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE,
                      FdahpStudyDesignerConstants.UI_SDF_DATE));
            }
          } else if ((FdahpStudyDesignerConstants.DATE_TIME)
              .equalsIgnoreCase(questionReponseTypeBo.getStyle())) {
            if ((questionReponseTypeBo.getMinDate() != null)
                && StringUtils.isNotEmpty(questionReponseTypeBo.getMinDate())) {
              questionReponseTypeBo.setMinDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionReponseTypeBo.getMinDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE_TIME,
                      FdahpStudyDesignerConstants.REQUIRED_DATE_TIME));
            }
            if ((questionReponseTypeBo.getMaxDate() != null)
                && StringUtils.isNotEmpty(questionReponseTypeBo.getMaxDate())) {
              questionReponseTypeBo.setMaxDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionReponseTypeBo.getMaxDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE_TIME,
                      FdahpStudyDesignerConstants.REQUIRED_DATE_TIME));
            }
            if ((questionReponseTypeBo.getDefaultDate() != null)
                && StringUtils.isNotEmpty(questionReponseTypeBo.getDefaultDate())) {
              questionReponseTypeBo.setDefaultDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionReponseTypeBo.getDefaultDate(),
                      FdahpStudyDesignerConstants.DB_SDF_DATE_TIME,
                      FdahpStudyDesignerConstants.REQUIRED_DATE_TIME));
            }
          }
        }
        questionsBo.setQuestionReponseTypeBo(questionReponseTypeBo);

        List<QuestionResponseSubTypeBo> questionResponseSubTypeList = null;
        query =
            session
                .getNamedQuery("getQuestionSubResponse")
                .setInteger("responseTypeId", questionsBo.getId());
        questionResponseSubTypeList = query.list();
        // appending the current date and time for image urls
        if ((null != questionResponseSubTypeList) && !questionResponseSubTypeList.isEmpty()) {
          for (QuestionResponseSubTypeBo s : questionResponseSubTypeList) {
            if (FdahpStudyDesignerUtil.isNotEmpty(s.getImage())) {
              if (s.getImage().contains("?v=")) {
                String imagePathArr[] = s.getImage().split("\\?");
                s.setImage(imagePathArr[0] + "?v=" + new Date().getTime());
              } else {
                s.setImage(s.getImage() + "?v=" + new Date().getTime());
              }
            }
            if (FdahpStudyDesignerUtil.isNotEmpty(s.getSelectedImage())) {
              if (s.getSelectedImage().contains("?v=")) {
                String imagePathArr[] = s.getSelectedImage().split("\\?");
                s.setSelectedImage(imagePathArr[0] + "?v=" + new Date().getTime());
              } else {
                s.setSelectedImage(s.getSelectedImage() + "?v=" + new Date().getTime());
              }
            }
          }
        }
        questionsBo.setQuestionResponseSubTypeList(questionResponseSubTypeList);

        // Phase 2a ancordate start
        if (questionsBo.getAnchorDateId() != null) {
          String name =
              (String)
                  session
                      .createSQLQuery("select name from anchordate_type where id=:anchorDateId")
                      .setParameter("anchorDateId", questionsBo.getAnchorDateId())
                      .uniqueResult();
          questionsBo.setAnchorDateName(name);
        }
        // phase 2a anchordate end
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getQuestionsById() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getQuestionsById() - Ends");
    return questionsBo;
  }

  public QuestionReponseTypeBo getQuestionsResponseTypeBo(
      QuestionReponseTypeBo questionsResponseTypeBo, Session session) {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionsResponseTypeBo() - Starts");
    QuestionReponseTypeBo addOrUpdateQuestionsResponseTypeBo = null;
    try {
      if ((questionsResponseTypeBo != null) && (session != null)) {
        if (questionsResponseTypeBo.getResponseTypeId() != null) {
          addOrUpdateQuestionsResponseTypeBo =
              (QuestionReponseTypeBo)
                  session.get(
                      QuestionReponseTypeBo.class, questionsResponseTypeBo.getResponseTypeId());
        } else {
          addOrUpdateQuestionsResponseTypeBo = new QuestionReponseTypeBo();
          addOrUpdateQuestionsResponseTypeBo.setActive(true);
        }
        if (questionsResponseTypeBo.getQuestionsResponseTypeId() != null) {
          addOrUpdateQuestionsResponseTypeBo.setQuestionsResponseTypeId(
              questionsResponseTypeBo.getQuestionsResponseTypeId());
        }
        if (questionsResponseTypeBo.getMinValue() != null) {
          addOrUpdateQuestionsResponseTypeBo.setMinValue(questionsResponseTypeBo.getMinValue());
        }
        if (questionsResponseTypeBo.getMaxValue() != null) {
          addOrUpdateQuestionsResponseTypeBo.setMaxValue(questionsResponseTypeBo.getMaxValue());
        }
        if (questionsResponseTypeBo.getDefaultValue() != null) {
          addOrUpdateQuestionsResponseTypeBo.setDefaultValue(
              questionsResponseTypeBo.getDefaultValue());
        }
        if (questionsResponseTypeBo.getStep() != null) {
          addOrUpdateQuestionsResponseTypeBo.setStep(questionsResponseTypeBo.getStep());
        }
        if (questionsResponseTypeBo.getVertical() != null) {
          addOrUpdateQuestionsResponseTypeBo.setVertical(questionsResponseTypeBo.getVertical());
        }
        addOrUpdateQuestionsResponseTypeBo.setMinDescription(
            questionsResponseTypeBo.getMinDescription());
        addOrUpdateQuestionsResponseTypeBo.setMaxDescription(
            questionsResponseTypeBo.getMaxDescription());
        if (questionsResponseTypeBo.getMaxFractionDigits() != null) {
          addOrUpdateQuestionsResponseTypeBo.setMaxFractionDigits(
              questionsResponseTypeBo.getMaxFractionDigits());
        }
        if ((questionsResponseTypeBo.getTextChoices() != null)
            && StringUtils.isNotEmpty(questionsResponseTypeBo.getTextChoices())) {
          addOrUpdateQuestionsResponseTypeBo.setTextChoices(
              questionsResponseTypeBo.getTextChoices());
        }
        if ((questionsResponseTypeBo.getSelectionStyle() != null)
            && StringUtils.isNotEmpty(questionsResponseTypeBo.getSelectionStyle())) {
          addOrUpdateQuestionsResponseTypeBo.setSelectionStyle(
              questionsResponseTypeBo.getSelectionStyle());
        }
        if ((questionsResponseTypeBo.getImageSize() != null)
            && StringUtils.isNotEmpty(questionsResponseTypeBo.getImageSize())) {
          addOrUpdateQuestionsResponseTypeBo.setImageSize(questionsResponseTypeBo.getImageSize());
        }
        if ((questionsResponseTypeBo.getStyle() != null)
            && StringUtils.isNotEmpty(questionsResponseTypeBo.getStyle())) {
          addOrUpdateQuestionsResponseTypeBo.setStyle(questionsResponseTypeBo.getStyle());
          if ((FdahpStudyDesignerConstants.DATE)
              .equalsIgnoreCase(questionsResponseTypeBo.getStyle())) {
            if ((questionsResponseTypeBo.getMinDate() != null)
                && StringUtils.isNotEmpty(questionsResponseTypeBo.getMinDate())) {
              addOrUpdateQuestionsResponseTypeBo.setMinDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionsResponseTypeBo.getMinDate(),
                      FdahpStudyDesignerConstants.UI_SDF_DATE,
                      FdahpStudyDesignerConstants.DB_SDF_DATE));
            } else {
              addOrUpdateQuestionsResponseTypeBo.setMinDate(null);
            }
            if ((questionsResponseTypeBo.getMaxDate() != null)
                && StringUtils.isNotEmpty(questionsResponseTypeBo.getMaxDate())) {
              addOrUpdateQuestionsResponseTypeBo.setMaxDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionsResponseTypeBo.getMaxDate(),
                      FdahpStudyDesignerConstants.UI_SDF_DATE,
                      FdahpStudyDesignerConstants.DB_SDF_DATE));
            } else {
              addOrUpdateQuestionsResponseTypeBo.setMaxDate(null);
            }
            if ((questionsResponseTypeBo.getDefaultDate() != null)
                && StringUtils.isNotEmpty(questionsResponseTypeBo.getDefaultDate())) {
              addOrUpdateQuestionsResponseTypeBo.setDefaultDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionsResponseTypeBo.getDefaultDate(),
                      FdahpStudyDesignerConstants.UI_SDF_DATE,
                      FdahpStudyDesignerConstants.DB_SDF_DATE));
            } else {
              addOrUpdateQuestionsResponseTypeBo.setDefaultDate(null);
            }
          } else if ((FdahpStudyDesignerConstants.DATE_TIME)
              .equalsIgnoreCase(questionsResponseTypeBo.getStyle())) {
            if ((questionsResponseTypeBo.getMinDate() != null)
                && StringUtils.isNotEmpty(questionsResponseTypeBo.getMinDate())) {
              addOrUpdateQuestionsResponseTypeBo.setMinDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionsResponseTypeBo.getMinDate(),
                      FdahpStudyDesignerConstants.REQUIRED_DATE_TIME,
                      FdahpStudyDesignerConstants.DB_SDF_DATE_TIME));
            } else {
              addOrUpdateQuestionsResponseTypeBo.setMinDate(null);
            }
            if ((questionsResponseTypeBo.getMaxDate() != null)
                && StringUtils.isNotEmpty(questionsResponseTypeBo.getMaxDate())) {
              addOrUpdateQuestionsResponseTypeBo.setMaxDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionsResponseTypeBo.getMaxDate(),
                      FdahpStudyDesignerConstants.REQUIRED_DATE_TIME,
                      FdahpStudyDesignerConstants.DB_SDF_DATE_TIME));
            } else {
              addOrUpdateQuestionsResponseTypeBo.setMaxDate(null);
            }
            if ((questionsResponseTypeBo.getDefaultDate() != null)
                && StringUtils.isNotEmpty(questionsResponseTypeBo.getDefaultDate())) {
              addOrUpdateQuestionsResponseTypeBo.setDefaultDate(
                  FdahpStudyDesignerUtil.getFormattedDate(
                      questionsResponseTypeBo.getDefaultDate(),
                      FdahpStudyDesignerConstants.REQUIRED_DATE_TIME,
                      FdahpStudyDesignerConstants.DB_SDF_DATE_TIME));
            } else {
              addOrUpdateQuestionsResponseTypeBo.setDefaultDate(null);
            }
          }
        }
        addOrUpdateQuestionsResponseTypeBo.setPlaceholder(questionsResponseTypeBo.getPlaceholder());
        addOrUpdateQuestionsResponseTypeBo.setUnit(questionsResponseTypeBo.getUnit());
        addOrUpdateQuestionsResponseTypeBo.setMaxLength(questionsResponseTypeBo.getMaxLength());
        if ((questionsResponseTypeBo.getValidationRegex() != null)
            && StringUtils.isNotEmpty(questionsResponseTypeBo.getValidationRegex())) {
          addOrUpdateQuestionsResponseTypeBo.setValidationRegex(
              questionsResponseTypeBo.getValidationRegex());
        }
        addOrUpdateQuestionsResponseTypeBo.setInvalidMessage(
            questionsResponseTypeBo.getInvalidMessage());
        if (questionsResponseTypeBo.getMultipleLines() != null) {
          addOrUpdateQuestionsResponseTypeBo.setMultipleLines(
              questionsResponseTypeBo.getMultipleLines());
        }
        if ((questionsResponseTypeBo.getMeasurementSystem() != null)
            && StringUtils.isNotEmpty(questionsResponseTypeBo.getMeasurementSystem())) {
          addOrUpdateQuestionsResponseTypeBo.setMeasurementSystem(
              questionsResponseTypeBo.getMeasurementSystem());
        }
        if (questionsResponseTypeBo.getUseCurrentLocation() != null) {
          addOrUpdateQuestionsResponseTypeBo.setUseCurrentLocation(
              questionsResponseTypeBo.getUseCurrentLocation());
        }
        addOrUpdateQuestionsResponseTypeBo.setValidationCondition(
            questionsResponseTypeBo.getValidationCondition());
        addOrUpdateQuestionsResponseTypeBo.setValidationCharacters(
            questionsResponseTypeBo.getValidationCharacters());
        addOrUpdateQuestionsResponseTypeBo.setValidationExceptText(
            questionsResponseTypeBo.getValidationExceptText());

        addOrUpdateQuestionsResponseTypeBo.setValidationRegex(
            FdahpStudyDesignerUtil.getRegExpression(
                questionsResponseTypeBo.getValidationCondition(),
                questionsResponseTypeBo.getValidationCharacters(),
                questionsResponseTypeBo.getValidationExceptText()));

        String fileName;
        if (questionsResponseTypeBo.getMinImageFile() != null) {
          if ((questionsResponseTypeBo.getMinImage() != null)
              && StringUtils.isNotEmpty(questionsResponseTypeBo.getMinImage())) {
            addOrUpdateQuestionsResponseTypeBo.setMinImage(questionsResponseTypeBo.getMinImage());
          } else {
            if ((questionsResponseTypeBo.getMinImageFile().getOriginalFilename() != null)
                && StringUtils.isNotEmpty(
                    questionsResponseTypeBo.getMinImageFile().getOriginalFilename())) {
              fileName =
                  FdahpStudyDesignerUtil.getStandardFileName(
                      FdahpStudyDesignerConstants.QUESTION_STEP_IMAGE + 0,
                      questionsResponseTypeBo.getMinImageFile().getOriginalFilename(),
                      String.valueOf(questionsResponseTypeBo.getQuestionsResponseTypeId()));

              String imagePath =
                  FdahpStudyDesignerUtil.saveImage(
                      questionsResponseTypeBo.getMinImageFile(),
                      fileName,
                      FdahpStudyDesignerConstants.QUESTIONNAIRE);
              addOrUpdateQuestionsResponseTypeBo.setMinImage(imagePath);
            } else {
              addOrUpdateQuestionsResponseTypeBo.setMinImage(null);
            }
          }
        } else {
          if (StringUtils.isEmpty(questionsResponseTypeBo.getMinImage())) {
            addOrUpdateQuestionsResponseTypeBo.setMinImage(null);
          }
        }
        if (questionsResponseTypeBo.getMaxImageFile() != null) {
          if ((questionsResponseTypeBo.getMaxImage() != null)
              && StringUtils.isNotEmpty(questionsResponseTypeBo.getMaxImage())) {
            addOrUpdateQuestionsResponseTypeBo.setMaxImage(questionsResponseTypeBo.getMaxImage());
          } else {
            if ((questionsResponseTypeBo.getMaxImageFile().getOriginalFilename() != null)
                && StringUtils.isNotEmpty(
                    questionsResponseTypeBo.getMaxImageFile().getOriginalFilename())) {
              fileName =
                  FdahpStudyDesignerUtil.getStandardFileName(
                      FdahpStudyDesignerConstants.QUESTION_STEP_IMAGE + 1,
                      questionsResponseTypeBo.getMaxImageFile().getOriginalFilename(),
                      String.valueOf(questionsResponseTypeBo.getQuestionsResponseTypeId()));
              String imagePath =
                  FdahpStudyDesignerUtil.saveImage(
                      questionsResponseTypeBo.getMaxImageFile(),
                      fileName,
                      FdahpStudyDesignerConstants.QUESTIONNAIRE);

              addOrUpdateQuestionsResponseTypeBo.setMaxImage(imagePath);
            } else {
              addOrUpdateQuestionsResponseTypeBo.setMaxImage(null);
            }
          }
        } else {
          if (StringUtils.isEmpty(questionsResponseTypeBo.getMaxImage())) {
            addOrUpdateQuestionsResponseTypeBo.setMaxImage(null);
          }
        }
        if ((questionsResponseTypeBo.getDefaultTime() != null)
            && StringUtils.isNotEmpty(questionsResponseTypeBo.getDefaultTime())) {
          addOrUpdateQuestionsResponseTypeBo.setDefaultTime(
              questionsResponseTypeBo.getDefaultTime());
        }
        if ((questionsResponseTypeBo.getFormulaBasedLogic() != null)
            && StringUtils.isNotEmpty(questionsResponseTypeBo.getFormulaBasedLogic())) {
          addOrUpdateQuestionsResponseTypeBo.setFormulaBasedLogic(
              questionsResponseTypeBo.getFormulaBasedLogic());
        }
        if ((questionsResponseTypeBo.getConditionFormula() != null)
            && StringUtils.isNotEmpty(questionsResponseTypeBo.getConditionFormula())) {
          addOrUpdateQuestionsResponseTypeBo.setConditionFormula(
              questionsResponseTypeBo
                  .getConditionFormula()
                  .replace("&lt;", "<")
                  .replace("&gt;", ">"));
        }

        /** Other type set addded by ronalin * */
        if (StringUtils.isNotEmpty(questionsResponseTypeBo.getOtherType())
            && questionsResponseTypeBo.getOtherType().equals("on")) {
          addOrUpdateQuestionsResponseTypeBo.setOtherType(questionsResponseTypeBo.getOtherType());
          addOrUpdateQuestionsResponseTypeBo.setOtherText(questionsResponseTypeBo.getOtherText());
          addOrUpdateQuestionsResponseTypeBo.setOtherValue(questionsResponseTypeBo.getOtherValue());
          addOrUpdateQuestionsResponseTypeBo.setOtherExclusive(
              questionsResponseTypeBo.getOtherExclusive());
          addOrUpdateQuestionsResponseTypeBo.setOtherDestinationStepId(
              questionsResponseTypeBo.getOtherDestinationStepId());
          addOrUpdateQuestionsResponseTypeBo.setOtherDescription(
              questionsResponseTypeBo.getOtherDescription());
          addOrUpdateQuestionsResponseTypeBo.setOtherIncludeText(
              questionsResponseTypeBo.getOtherIncludeText());
          if (StringUtils.isNotEmpty(questionsResponseTypeBo.getOtherIncludeText())
              && questionsResponseTypeBo.getOtherIncludeText().equals("Yes")) {
            addOrUpdateQuestionsResponseTypeBo.setOtherPlaceholderText(
                questionsResponseTypeBo.getOtherPlaceholderText());
            addOrUpdateQuestionsResponseTypeBo.setOtherParticipantFill(
                questionsResponseTypeBo.getOtherParticipantFill());
          } else {
            addOrUpdateQuestionsResponseTypeBo.setOtherPlaceholderText(null);
            addOrUpdateQuestionsResponseTypeBo.setOtherParticipantFill(null);
          }
        } else {
          addOrUpdateQuestionsResponseTypeBo.setOtherType(null);
          addOrUpdateQuestionsResponseTypeBo.setOtherText(null);
          addOrUpdateQuestionsResponseTypeBo.setOtherValue(null);
          addOrUpdateQuestionsResponseTypeBo.setOtherExclusive(null);
          addOrUpdateQuestionsResponseTypeBo.setOtherDestinationStepId(null);
          addOrUpdateQuestionsResponseTypeBo.setOtherDescription(null);
          addOrUpdateQuestionsResponseTypeBo.setOtherIncludeText(null);
          addOrUpdateQuestionsResponseTypeBo.setOtherPlaceholderText(null);
          addOrUpdateQuestionsResponseTypeBo.setOtherParticipantFill(null);
        }
        /** Other type set addded by ronalin * */
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getQuestionsResponseTypeBo() - Error", e);
    }
    logger.info("StudyQuestionnaireDAOImpl - getQuestionsResponseTypeBo() - Ends");
    return addOrUpdateQuestionsResponseTypeBo;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<QuestionnaireBo> getStudyQuestionnairesByStudyId(String studyId, Boolean isLive) {
    logger.info("StudyQuestionnaireDAOImpl - getStudyQuestionnairesByStudyId() - Starts");
    Session session = null;
    List<QuestionnaireBo> questionnaires = null;
    String searchQuery = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if (StringUtils.isNotEmpty(studyId)) {
        if (isLive) {
          searchQuery =
              "From QuestionnaireBo QBO WHERE QBO.customStudyId =:studyId "
                  + " and QBO.active=1 and QBO.live=1 order by QBO.createdDate DESC";
          query = session.createQuery(searchQuery).setString("studyId", studyId);
        } else {
          query =
              session
                  .getNamedQuery("getQuestionariesByStudyId")
                  .setInteger("studyId", Integer.parseInt(studyId));
        }
        questionnaires = query.list();
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getStudyQuestionnairesByStudyId() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getStudyQuestionnairesByStudyId() - Ends");
    return questionnaires;
  }

  @Override
  public Boolean isAnchorDateExistsForStudy(Integer studyId, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - isAnchorDateExistsForStudy() - starts");
    boolean isExists = false;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      if ((customStudyId != null) && StringUtils.isNotEmpty(customStudyId)) {
        // checking in the question step anchor date is selected or not
        String searchQuery =
            "select count(q.use_anchor_date) from questions q,questionnaires_steps qsq,questionnaires qq  where q.id=qsq.instruction_form_id and qsq.step_type='Question' "
                + "and qsq.active=1 and qsq.questionnaires_id=qq.id and qq.study_id in(select s.id from studies s where s.custom_study_id=:customStudyId "
                + " and s.is_live=0) and qq.active=1 and q.active=1;";
        BigInteger count =
            (BigInteger)
                session
                    .createSQLQuery(searchQuery)
                    .setString("customStudyId", customStudyId)
                    .uniqueResult();
        if (count.intValue() > 0) {
          isExists = true;
        } else {
          // checking in the form step question anchor date is
          // selected or not
          String subQuery =
              "select count(q.use_anchor_date) from questions q,form_mapping fm,form f,questionnaires_steps qsf,questionnaires qq where q.id=fm.question_id and f.form_id=fm.form_id and f.active=1 "
                  + "and f.form_id=qsf.instruction_form_id and qsf.step_type='Form' and qsf.questionnaires_id=qq.id and study_id in (select s.id from studies s where s.custom_study_id=:customStudyId "
                  + " and s.is_live=0) and q.active=1";
          BigInteger subCount =
              (BigInteger)
                  session
                      .createSQLQuery(subQuery)
                      .setString("customStudyId", customStudyId)
                      .uniqueResult();
          if ((subCount != null) && (subCount.intValue() > 0)) {
            isExists = true;
          }
        }
      } else {
        // checking in the question step anchor date is selected or not
        String searchQuery =
            "select count(q.use_anchor_date) from questions q,questionnaires_steps qsq,questionnaires qq  where q.id=qsq.instruction_form_id and qsq.step_type='Question' "
                + "and qsq.active=1 and qsq.questionnaires_id=qq.id and qq.study_id=:studyId "
                + " and qq.active=1 and q.active=1;";
        BigInteger count =
            (BigInteger)
                session.createSQLQuery(searchQuery).setInteger("studyId", studyId).uniqueResult();
        if (count.intValue() > 0) {
          isExists = true;
        } else {
          // checking in the form step question anchor date is
          // selected or not
          String subQuery =
              "select count(q.use_anchor_date) from questions q,form_mapping fm,form f,questionnaires_steps qsf,questionnaires qq where q.id=fm.question_id and f.form_id=fm.form_id and f.active=1 "
                  + "and f.form_id=qsf.instruction_form_id and qsf.step_type='Form' and qsf.questionnaires_id=qq.id and study_id=:studyId "
                  + " and q.active=1";
          BigInteger subCount =
              (BigInteger)
                  session.createSQLQuery(subQuery).setInteger("studyId", studyId).uniqueResult();
          if ((subCount != null) && (subCount.intValue() > 0)) {
            isExists = true;
          }
        }
      }
      if (!isExists) {
        char isEnrollAnchorExist =
            (char)
                session
                    .createSQLQuery(
                        "select s.enrollmentdate_as_anchordate from studies s where s.id=:studyId ")
                    .setInteger("studyId", studyId)
                    .uniqueResult();
        if ((isEnrollAnchorExist != ' ') && (isEnrollAnchorExist == 'Y')) {
          isExists = true;
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - isAnchorDateExistsForStudy() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - isAnchorDateExistsForStudy() - Ends");
    return isExists;
  }

  @Override
  public Boolean isQuestionnairesCompleted(Integer studyId) {
    logger.info("StudyQuestionnaireDAOImpl - isAnchorDateExistsForStudy() - starts");
    boolean isExists = true;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      String searchQuery =
          "select sum(q.status = 0) as no from questionnaires_steps q where q.questionnaires_id in (select id from questionnaires where study_id=:studyId "
              + " and active=1) and q.active=1";
      BigDecimal count =
          (BigDecimal)
              session.createSQLQuery(searchQuery).setInteger("studyId", studyId).uniqueResult();
      if ((count != null) && (count.intValue() > 0)) {
        isExists = false;
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - isAnchorDateExistsForStudy() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - isAnchorDateExistsForStudy() - Ends");
    return isExists;
  }

  @Override
  public String reOrderFormStepQuestions(Integer formId, int oldOrderNumber, int newOrderNumber) {
    logger.info("StudyQuestionnaireDAOImpl - reOrderFormStepQuestions() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    int count = 0;
    FormMappingBo formMappingBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      String updateQuery = "";
      query =
          session
              .getNamedQuery("getFromByIdAndSequenceNo")
              .setInteger("formId", formId)
              .setInteger("oldOrderNumber", oldOrderNumber);
      formMappingBo = (FormMappingBo) query.uniqueResult();
      if (formMappingBo != null) {
        if (oldOrderNumber < newOrderNumber) {
          updateQuery =
              "update FormMappingBo FMBO set FMBO.sequenceNo=FMBO.sequenceNo-1 where FMBO.formId=:formId "
                  + " and FMBO.sequenceNo <=:newOrderNumber "
                  + " and FMBO.sequenceNo >:oldOrderNumber "
                  + " and FMBO.active=1";
          query =
              session
                  .createQuery(updateQuery)
                  .setInteger("formId", formId)
                  .setInteger("newOrderNumber", newOrderNumber)
                  .setInteger("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session
                    .getNamedQuery("updateFromQuestionSequenceNo")
                    .setInteger("newOrderNumber", newOrderNumber)
                    .setInteger("id", formMappingBo.getId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        } else if (oldOrderNumber > newOrderNumber) {
          updateQuery =
              "update FormMappingBo FMBO set FMBO.sequenceNo=FMBO.sequenceNo+1 where FMBO.formId=:formId "
                  + " and FMBO.sequenceNo >=:newOrderNumber "
                  + " and FMBO.sequenceNo <:oldOrderNumber "
                  + " and FMBO.active=1";
          query =
              session
                  .createQuery(updateQuery)
                  .setInteger("formId", formId)
                  .setInteger("newOrderNumber", newOrderNumber)
                  .setInteger("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session
                    .getNamedQuery("updateFromQuestionSequenceNo")
                    .setInteger("newOrderNumber", newOrderNumber)
                    .setInteger("id", formMappingBo.getId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - reOrderFormStepQuestions() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - reOrderFormStepQuestions() - Ends");
    return message;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String reOrderQuestionnaireSteps(
      Integer questionnaireId, int oldOrderNumber, int newOrderNumber) {
    logger.info("StudyQuestionnaireDAOImpl - reOrderQuestionnaireSteps() - Starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    int count = 0;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    List<QuestionnairesStepsBo> questionnaireStepList = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      String updateQuery = "";
      query =
          session
              .createQuery(
                  "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
                      + " and QSBO.sequenceNo =:oldOrderNumber "
                      + " and QSBO.active=1")
              .setInteger("questionnaireId", questionnaireId)
              .setInteger("oldOrderNumber", oldOrderNumber);
      questionnairesStepsBo = (QuestionnairesStepsBo) query.uniqueResult();
      if (questionnairesStepsBo != null) {

        if (oldOrderNumber < newOrderNumber) {
          updateQuery =
              "update QuestionnairesStepsBo QSBO set QSBO.sequenceNo=QSBO.sequenceNo-1 where QSBO.questionnairesId=:questionnaireId "
                  + " and QSBO.sequenceNo <=:newOrderNumber "
                  + " and QSBO.sequenceNo >:oldOrderNumber "
                  + " and QSBO.active=1";
          query =
              session
                  .createQuery(updateQuery)
                  .setInteger("questionnaireId", questionnaireId)
                  .setInteger("newOrderNumber", newOrderNumber)
                  .setInteger("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session
                    .createQuery(
                        "update QuestionnairesStepsBo q set q.sequenceNo=:newOrderNumber "
                            + " where q.stepId=:stepId "
                            + " and q.active=1")
                    .setInteger("newOrderNumber", newOrderNumber)
                    .setInteger("stepId", questionnairesStepsBo.getStepId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        } else if (oldOrderNumber > newOrderNumber) {
          updateQuery =
              "update QuestionnairesStepsBo QSBO set QSBO.sequenceNo=QSBO.sequenceNo+1 where QSBO.questionnairesId=:questionnaireId "
                  + " and QSBO.sequenceNo >=:newOrderNumber "
                  + " and QSBO.sequenceNo <:oldOrderNumber "
                  + " and QSBO.active=1";
          query =
              session
                  .createQuery(updateQuery)
                  .setInteger("questionnaireId", questionnaireId)
                  .setInteger("newOrderNumber", newOrderNumber)
                  .setInteger("oldOrderNumber", oldOrderNumber);
          count = query.executeUpdate();
          if (count > 0) {
            query =
                session
                    .createQuery(
                        "update QuestionnairesStepsBo Q set Q.sequenceNo=:newOrderNumber "
                            + " where Q.stepId=:stepId "
                            + " and Q.active=1")
                    .setInteger("newOrderNumber", newOrderNumber)
                    .setInteger("stepId", questionnairesStepsBo.getStepId());
            count = query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }

        // Reset destination steps in Questionnaire Starts
        if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
          String searchQuery =
              "From QuestionnairesStepsBo QSBO where QSBO.questionnairesId=:questionnaireId "
                  + "and QSBO.active=1 order by QSBO.sequenceNo ASC";
          questionnaireStepList = session.createQuery(searchQuery).list();
          if ((null != questionnaireStepList) && !questionnaireStepList.isEmpty()) {
            if (questionnaireStepList.size() == 1) {
              questionnaireStepList.get(0).setDestinationStep(0);
              questionnaireStepList.get(0).setSequenceNo(1);
              session.update(questionnaireStepList.get(0));
            } else {
              int i;
              for (i = 0; i < (questionnaireStepList.size() - 1); i++) {
                questionnaireStepList
                    .get(i)
                    .setDestinationStep(questionnaireStepList.get(i + 1).getStepId());
                questionnaireStepList.get(i).setSequenceNo(i + 1);
                session.update(questionnaireStepList.get(i));
              }
              questionnaireStepList.get(i).setDestinationStep(0);
              questionnaireStepList.get(i).setSequenceNo(i + 1);
              session.update(questionnaireStepList.get(i));
            }
          }
          String questionResponseQuery =
              "update response_sub_type_value rs,questionnaires_steps q set rs.destination_step_id = NULL "
                  + "where rs.response_type_id=q.instruction_form_id and q.step_type=:type"
                  + " and q.questionnaires_id=:questionnaireId "
                  + " and rs.active=1 and q.active=1";
          query =
              session
                  .createSQLQuery(questionResponseQuery)
                  .setParameter("type", FdahpStudyDesignerConstants.QUESTION_STEP)
                  .setInteger("questionnaireId", questionnaireId);
          query.executeUpdate();

          String questionConditionResponseQuery =
              "update questions qs,questionnaires_steps q,response_type_value rs  set qs.status = 0 where"
                  + " rs.questions_response_type_id=q.instruction_form_id and q.step_type=:type"
                  + " and q.questionnaires_id=:questionnaireId "
                  + " and qs.id=q.instruction_form_id and qs.active=1 and rs.active=1 and q.active=1 and rs.formula_based_logic='Yes'";

          query =
              session
                  .createSQLQuery(questionConditionResponseQuery)
                  .setParameter("type", FdahpStudyDesignerConstants.QUESTION_STEP)
                  .setInteger("questionnaireId", questionnaireId);
          query.executeUpdate();
        }
        // Reset destination steps in Questionnaire Ends
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - reOrderQuestionnaireSteps() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - reOrderQuestionnaireSteps() - Ends");
    return message;
  }

  @Override
  public QuestionnairesStepsBo saveOrUpdateFromQuestionnaireStep(
      QuestionnairesStepsBo questionnairesStepsBo, SessionObject sesObj, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - saveOrUpdateFromQuestionnaireStep() - Starts");
    Session session = null;
    QuestionnairesStepsBo addOrUpdateQuestionnairesStepsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (questionnairesStepsBo != null) {
        if (questionnairesStepsBo.getStepId() != null) {
          addOrUpdateQuestionnairesStepsBo =
              (QuestionnairesStepsBo)
                  session.get(QuestionnairesStepsBo.class, questionnairesStepsBo.getStepId());
        } else {
          addOrUpdateQuestionnairesStepsBo = new QuestionnairesStepsBo();
          addOrUpdateQuestionnairesStepsBo.setActive(true);
          addOrUpdateQuestionnairesStepsBo.setDestinationStep(0);
        }
        if ((questionnairesStepsBo.getStepShortTitle() != null)
            && !questionnairesStepsBo.getStepShortTitle().isEmpty()) {
          addOrUpdateQuestionnairesStepsBo.setStepShortTitle(
              questionnairesStepsBo.getStepShortTitle());
        }
        if ((questionnairesStepsBo.getSkiappable() != null)
            && !questionnairesStepsBo.getSkiappable().isEmpty()) {
          addOrUpdateQuestionnairesStepsBo.setSkiappable(questionnairesStepsBo.getSkiappable());
        }
        if ((questionnairesStepsBo.getRepeatable() != null)
            && !questionnairesStepsBo.getRepeatable().isEmpty()) {
          addOrUpdateQuestionnairesStepsBo.setRepeatable(questionnairesStepsBo.getRepeatable());
        }
        addOrUpdateQuestionnairesStepsBo.setRepeatableText(
            questionnairesStepsBo.getRepeatableText());
        if (questionnairesStepsBo.getDestinationStep() != null) {
          addOrUpdateQuestionnairesStepsBo.setDestinationStep(
              questionnairesStepsBo.getDestinationStep());
        }
        if (questionnairesStepsBo.getQuestionnairesId() != null) {
          addOrUpdateQuestionnairesStepsBo.setQuestionnairesId(
              questionnairesStepsBo.getQuestionnairesId());
        }
        if (questionnairesStepsBo.getInstructionFormId() != null) {
          addOrUpdateQuestionnairesStepsBo.setInstructionFormId(
              questionnairesStepsBo.getInstructionFormId());
        }
        if (questionnairesStepsBo.getStepType() != null) {
          addOrUpdateQuestionnairesStepsBo.setStepType(questionnairesStepsBo.getStepType());
        }
        if (questionnairesStepsBo.getCreatedOn() != null) {
          addOrUpdateQuestionnairesStepsBo.setCreatedOn(questionnairesStepsBo.getCreatedOn());
        }
        if (questionnairesStepsBo.getCreatedBy() != null) {
          addOrUpdateQuestionnairesStepsBo.setCreatedBy(questionnairesStepsBo.getCreatedBy());
        }
        if (questionnairesStepsBo.getModifiedOn() != null) {
          addOrUpdateQuestionnairesStepsBo.setModifiedOn(questionnairesStepsBo.getModifiedOn());
        }
        if (questionnairesStepsBo.getModifiedBy() != null) {
          addOrUpdateQuestionnairesStepsBo.setModifiedBy(questionnairesStepsBo.getModifiedBy());
        }
        if (questionnairesStepsBo.getType() != null) {
          if (questionnairesStepsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
            addOrUpdateQuestionnairesStepsBo.setStatus(false);

            query =
                session
                    .createSQLQuery(
                        "update questionnaires q set q.status=0 where q.id=:questionnaireId ")
                    .setInteger(
                        "questionnaireId", addOrUpdateQuestionnairesStepsBo.getQuestionnairesId());
            query.executeUpdate();
          } else if (questionnairesStepsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
            addOrUpdateQuestionnairesStepsBo.setStatus(true);
          }
        }
        int count = 0;
        // adding the form step to questionnaire for the first time form
        // step creation
        if ((addOrUpdateQuestionnairesStepsBo.getQuestionnairesId() != null)
            && (addOrUpdateQuestionnairesStepsBo.getStepId() == null)) {
          FormBo formBo = new FormBo();
          formBo.setActive(true);
          formBo.setCreatedOn(addOrUpdateQuestionnairesStepsBo.getCreatedOn());
          formBo.setCreatedBy(addOrUpdateQuestionnairesStepsBo.getCreatedBy());
          session.saveOrUpdate(formBo);
          addOrUpdateQuestionnairesStepsBo.setQuestionnairesId(
              addOrUpdateQuestionnairesStepsBo.getQuestionnairesId());
          addOrUpdateQuestionnairesStepsBo.setInstructionFormId(formBo.getFormId());
          addOrUpdateQuestionnairesStepsBo.setStepType(FdahpStudyDesignerConstants.FORM_STEP);
          QuestionnairesStepsBo existedQuestionnairesStepsBo = null;
          query =
              session
                  .getNamedQuery("getQuestionnaireStepSequenceNo")
                  .setInteger(
                      "questionnairesId", addOrUpdateQuestionnairesStepsBo.getQuestionnairesId());
          query.setMaxResults(1);
          existedQuestionnairesStepsBo = (QuestionnairesStepsBo) query.uniqueResult();
          if (existedQuestionnairesStepsBo != null) {
            count = existedQuestionnairesStepsBo.getSequenceNo() + 1;
          } else {
            count = count + 1;
          }
          addOrUpdateQuestionnairesStepsBo.setSequenceNo(count);
        }
        session.saveOrUpdate(addOrUpdateQuestionnairesStepsBo);
        if ((addOrUpdateQuestionnairesStepsBo != null) && (count > 0)) {
          String updateQuery =
              "update QuestionnairesStepsBo QSBO set QSBO.destinationStep=:stepId"
                  + " where "
                  + "QSBO.destinationStep=0 and QSBO.sequenceNo=:sequenceNo"
                  + " and QSBO.questionnairesId=:questionnairesId ";
          session
              .createQuery(updateQuery)
              .setInteger("sequenceNo", (count - 1))
              .setInteger(
                  "questionnairesId", addOrUpdateQuestionnairesStepsBo.getQuestionnairesId())
              .setInteger("stepId", addOrUpdateQuestionnairesStepsBo.getStepId())
              .executeUpdate();
        }
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - saveOrUpdateFromQuestionnaireStep() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - saveOrUpdateFromQuestionnaireStep() - Ends");
    return addOrUpdateQuestionnairesStepsBo;
  }

  @Override
  public InstructionsBo saveOrUpdateInstructionsBo(
      InstructionsBo instructionsBo, SessionObject sessionObject, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - saveOrUpdateInstructionsBo() - Starts");
    Session session = null;
    QuestionnairesStepsBo existedQuestionnairesStepsBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.saveOrUpdate(instructionsBo);
      QuestionnairesStepsBo questionnairesStepsBo = null;
      if ((instructionsBo != null)
          && (instructionsBo.getId() != null)
          && (instructionsBo.getQuestionnairesStepsBo() != null)) {
        if (instructionsBo.getQuestionnairesStepsBo().getStepId() != null) {
          questionnairesStepsBo =
              (QuestionnairesStepsBo)
                  session.get(
                      QuestionnairesStepsBo.class,
                      instructionsBo.getQuestionnairesStepsBo().getStepId());
        } else {
          questionnairesStepsBo = new QuestionnairesStepsBo();
          questionnairesStepsBo.setActive(true);
          questionnairesStepsBo.setDestinationStep(0);
        }
        questionnairesStepsBo.setQuestionnairesId(instructionsBo.getQuestionnaireId());
        questionnairesStepsBo.setInstructionFormId(instructionsBo.getId());
        questionnairesStepsBo.setStepType(FdahpStudyDesignerConstants.INSTRUCTION_STEP);
        if ((instructionsBo.getQuestionnairesStepsBo().getStepShortTitle() != null)
            && !instructionsBo.getQuestionnairesStepsBo().getStepShortTitle().isEmpty()) {
          questionnairesStepsBo.setStepShortTitle(
              instructionsBo.getQuestionnairesStepsBo().getStepShortTitle());
        }
        if ((instructionsBo.getQuestionnairesStepsBo().getSkiappable() != null)
            && !instructionsBo.getQuestionnairesStepsBo().getSkiappable().isEmpty()) {
          questionnairesStepsBo.setSkiappable(
              instructionsBo.getQuestionnairesStepsBo().getSkiappable());
        }
        if ((instructionsBo.getQuestionnairesStepsBo().getRepeatable() != null)
            && !instructionsBo.getQuestionnairesStepsBo().getRepeatable().isEmpty()) {
          questionnairesStepsBo.setRepeatable(
              instructionsBo.getQuestionnairesStepsBo().getRepeatable());
        }
        if ((instructionsBo.getQuestionnairesStepsBo().getRepeatableText() != null)
            && !instructionsBo.getQuestionnairesStepsBo().getRepeatableText().isEmpty()) {
          questionnairesStepsBo.setRepeatableText(
              instructionsBo.getQuestionnairesStepsBo().getRepeatableText());
        }
        if (instructionsBo.getQuestionnairesStepsBo().getDestinationStep() != null) {
          questionnairesStepsBo.setDestinationStep(
              instructionsBo.getQuestionnairesStepsBo().getDestinationStep());
        }
        if (instructionsBo.getQuestionnairesStepsBo().getCreatedOn() != null) {
          questionnairesStepsBo.setCreatedOn(
              instructionsBo.getQuestionnairesStepsBo().getCreatedOn());
        }
        if (instructionsBo.getQuestionnairesStepsBo().getCreatedBy() != null) {
          questionnairesStepsBo.setCreatedBy(
              instructionsBo.getQuestionnairesStepsBo().getCreatedBy());
        }
        if (instructionsBo.getQuestionnairesStepsBo().getModifiedOn() != null) {
          questionnairesStepsBo.setModifiedOn(
              instructionsBo.getQuestionnairesStepsBo().getModifiedOn());
        }
        if (instructionsBo.getQuestionnairesStepsBo().getModifiedBy() != null) {
          questionnairesStepsBo.setModifiedBy(
              instructionsBo.getQuestionnairesStepsBo().getModifiedBy());
        }
        if (instructionsBo.getType() != null) {
          if (instructionsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
            questionnairesStepsBo.setStatus(false);

            query =
                session
                    .createSQLQuery(
                        "update questionnaires q set q.status=0 where q.id=:questionnairesId ")
                    .setInteger("questionnairesId", questionnairesStepsBo.getQuestionnairesId());
            query.executeUpdate();
          } else if (instructionsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
            questionnairesStepsBo.setStatus(true);
          }
        }
        int count = 0;
        if ((instructionsBo.getQuestionnaireId() != null)
            && (questionnairesStepsBo.getStepId() == null)) {
          query =
              session
                  .getNamedQuery("getQuestionnaireStepSequenceNo")
                  .setInteger("questionnairesId", instructionsBo.getQuestionnaireId());
          query.setMaxResults(1);
          existedQuestionnairesStepsBo = (QuestionnairesStepsBo) query.uniqueResult();
          if (existedQuestionnairesStepsBo != null) {
            count = existedQuestionnairesStepsBo.getSequenceNo() + 1;
          } else {
            count = count + 1;
          }
          questionnairesStepsBo.setSequenceNo(count);
        }
        session.saveOrUpdate(questionnairesStepsBo);
        instructionsBo.setQuestionnairesStepsBo(questionnairesStepsBo);
        if ((questionnairesStepsBo != null) && (count > 0)) {
          String updateQuery =
              "update QuestionnairesStepsBo QSBO set QSBO.destinationStep=:stepId "
                  + " where "
                  + "QSBO.destinationStep=0 and QSBO.sequenceNo=:sequenceNo"
                  + " and QSBO.questionnairesId=:questionnairesId ";
          session
              .createQuery(updateQuery)
              .setInteger("stepId", questionnairesStepsBo.getStepId())
              .setInteger("questionnairesId", instructionsBo.getQuestionnaireId())
              .setInteger("sequenceNo", (count - 1))
              .executeUpdate();
        }
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - saveOrUpdateInstructionsBo() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - saveOrUpdateInstructionsBo() - Ends");
    return instructionsBo;
  }

  @Override
  public QuestionsBo saveOrUpdateQuestion(QuestionsBo questionsBo) {
    logger.info("StudyQuestionnaireDAOImpl - saveOrUpdateQuestion() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      // Ancrodate text start
      if ((questionsBo.getUseAnchorDate() != null) && questionsBo.getUseAnchorDate()) {
        if (StringUtils.isNotEmpty(questionsBo.getAnchorDateName())) {
          Integer studyId = this.getStudyIdByCustomStudy(session, questionsBo.getCustomStudyId());
          AnchorDateTypeBo anchorDateTypeBo = new AnchorDateTypeBo();
          anchorDateTypeBo.setId(questionsBo.getAnchorDateId());
          anchorDateTypeBo.setCustomStudyId(questionsBo.getCustomStudyId());
          anchorDateTypeBo.setStudyId(studyId);
          anchorDateTypeBo.setName(questionsBo.getAnchorDateName());
          anchorDateTypeBo.setHasAnchortypeDraft(1);
          session.saveOrUpdate(anchorDateTypeBo);
          if (anchorDateTypeBo.getId() != null) {
            questionsBo.setAnchorDateId(anchorDateTypeBo.getId());
          }
        }
      } else {
        if ((questionsBo.getAnchorDateId() != null) && (questionsBo.getId() != null)) {
          query =
              session
                  .getNamedQuery("getStudyByCustomStudyId")
                  .setString("customStudyId", questionsBo.getCustomStudyId());
          query.setMaxResults(1);
          StudyVersionBo studyVersionBo = (StudyVersionBo) query.uniqueResult();
          Integer studyId = this.getStudyIdByCustomStudy(session, questionsBo.getCustomStudyId());
          SessionObject sessionObject = new SessionObject();
          sessionObject.setUserId(questionsBo.getModifiedBy());
          boolean isChange = false;
          if ((questionsBo.getIsShorTitleDuplicate() != null)
              && (questionsBo.getIsShorTitleDuplicate() > 0)) {
            isChange = true;
          }
          updateAnchordateInQuestionnaire(
              session,
              transaction,
              studyVersionBo,
              null,
              sessionObject,
              studyId,
              null,
              questionsBo.getId(),
              "",
              isChange);
          questionsBo.setAnchorDateId(null);
        }
      }
      // Anchordate Text end
      session.saveOrUpdate(questionsBo);
      if ((questionsBo != null)
          && (questionsBo.getId() != null)
          && (questionsBo.getFromId() != null)) {
        QuestionReponseTypeBo addQuestionReponseTypeBo =
            getQuestionsResponseTypeBo(questionsBo.getQuestionReponseTypeBo(), session);
        if (addQuestionReponseTypeBo != null) {
          if (addQuestionReponseTypeBo.getQuestionsResponseTypeId() == null) {
            addQuestionReponseTypeBo.setQuestionsResponseTypeId(questionsBo.getId());
          }
          session.saveOrUpdate(addQuestionReponseTypeBo);
        }
        questionsBo.setQuestionReponseTypeBo(addQuestionReponseTypeBo);
        if ((questionsBo.getQuestionResponseSubTypeList() != null)
            && !questionsBo.getQuestionResponseSubTypeList().isEmpty()) {
          String deletQuesry =
              "Delete From QuestionResponseSubTypeBo QRSTBO where QRSTBO.responseTypeId=:responseTypeId ";
          session
              .createQuery(deletQuesry)
              .setInteger("responseTypeId", questionsBo.getId())
              .executeUpdate();
          if ((questionsBo.getResponseType() == 4)
              || (questionsBo.getResponseType() == 3)
              || (questionsBo.getResponseType() == 6)
              || (questionsBo.getResponseType() == 5)) {
            int i = 0;
            // uploading the images for ImageChoice response type
            // questions
            for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                questionsBo.getQuestionResponseSubTypeList()) {

              if (questionsBo.getResponseType() != 5) {
                if (((questionResponseSubTypeBo.getText() != null)
                        && !questionResponseSubTypeBo.getText().isEmpty())
                    || ((questionResponseSubTypeBo.getValue() != null)
                        && !questionResponseSubTypeBo.getValue().isEmpty())
                    || ((questionResponseSubTypeBo.getDescription() != null)
                        && !questionResponseSubTypeBo.getDescription().isEmpty())
                    || ((questionResponseSubTypeBo.getExclusive() != null)
                        && !questionResponseSubTypeBo.getExclusive().isEmpty())) {
                  questionResponseSubTypeBo.setResponseTypeId(questionsBo.getId());
                  questionResponseSubTypeBo.setActive(true);
                  session.save(questionResponseSubTypeBo);
                }

              } else {
                if (((questionResponseSubTypeBo.getText() != null)
                        && !questionResponseSubTypeBo.getText().isEmpty())
                    || ((questionResponseSubTypeBo.getValue() != null)
                        && !questionResponseSubTypeBo.getValue().isEmpty())
                    || questionResponseSubTypeBo.getImageFile() != null
                    || questionResponseSubTypeBo.getSelectImageFile() != null) {
                  String fileName;
                  if (questionResponseSubTypeBo.getImageFile() != null) {
                    if ((questionResponseSubTypeBo.getImage() != null)
                        && !questionResponseSubTypeBo.getImage().isEmpty()) {
                      questionResponseSubTypeBo.setImage(questionResponseSubTypeBo.getImage());
                    } else {
                      fileName =
                          FdahpStudyDesignerUtil.getStandardFileName(
                              FdahpStudyDesignerConstants.FORM_STEP_IMAGE + i,
                              questionResponseSubTypeBo.getImageFile().getOriginalFilename(),
                              String.valueOf(questionsBo.getId()));

                      String imagePath =
                          FdahpStudyDesignerUtil.saveImage(
                              questionResponseSubTypeBo.getImageFile(),
                              fileName,
                              FdahpStudyDesignerConstants.QUESTIONNAIRE);
                      questionResponseSubTypeBo.setImage(imagePath);
                    }
                  }
                  if (questionResponseSubTypeBo.getSelectImageFile() != null) {
                    if ((questionResponseSubTypeBo.getSelectedImage() != null)
                        && !questionResponseSubTypeBo.getSelectedImage().isEmpty()) {
                      questionResponseSubTypeBo.setSelectedImage(
                          questionResponseSubTypeBo.getSelectedImage());
                    } else {
                      fileName =
                          FdahpStudyDesignerUtil.getStandardFileName(
                              FdahpStudyDesignerConstants.FORM_STEP_SELECTEDIMAGE + i,
                              questionResponseSubTypeBo.getSelectImageFile().getOriginalFilename(),
                              String.valueOf(questionsBo.getId()));
                      String imagePath =
                          FdahpStudyDesignerUtil.saveImage(
                              questionResponseSubTypeBo.getSelectImageFile(),
                              fileName,
                              FdahpStudyDesignerConstants.QUESTIONNAIRE);
                      questionResponseSubTypeBo.setSelectedImage(imagePath);
                    }
                  }
                  questionResponseSubTypeBo.setResponseTypeId(questionsBo.getId());
                  questionResponseSubTypeBo.setActive(true);
                  session.save(questionResponseSubTypeBo);
                }
              }
              i = i + 1;
            }
          } else {
            for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                questionsBo.getQuestionResponseSubTypeList()) {
              questionResponseSubTypeBo.setResponseTypeId(questionsBo.getId());
              questionResponseSubTypeBo.setActive(true);
              session.save(questionResponseSubTypeBo);
            }
          }
        }
        // updating the questionnaire and questionnaire step status to
        // incomplete because the admin saving the content question not
        // mark as completed
        if (!questionsBo.getStatus()) {
          if (questionsBo.getQuestionnaireId() != null) {
            query =
                session
                    .createQuery(
                        "From QuestionnairesStepsBo QSBO where QSBO.instructionFormId=:fromId "
                            + " and QSBO.stepType=:stepType "
                            + " and QSBO.active=1 and QSBO.questionnairesId=:questionnairesId ")
                    .setInteger("fromId", questionsBo.getFromId())
                    .setInteger("questionnairesId", questionsBo.getQuestionnaireId())
                    .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP);
          } else {
            query =
                session
                    .getNamedQuery("getQuestionnaireStep")
                    .setInteger("instructionFormId", questionsBo.getFromId())
                    .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP);
          }

          QuestionnairesStepsBo questionnairesStepsBo =
              (QuestionnairesStepsBo) query.uniqueResult();
          if ((questionnairesStepsBo != null) && questionnairesStepsBo.getStatus()) {
            questionnairesStepsBo.setStatus(false);
            session.saveOrUpdate(questionnairesStepsBo);
            query =
                session
                    .createSQLQuery(
                        "update questionnaires q set q.status=0 where q.id=:questionnairesId ")
                    .setInteger("questionnairesId", questionnairesStepsBo.getQuestionnairesId());
            query.executeUpdate();
          }
        }
        query =
            session.getNamedQuery("getFormMappingBO").setInteger("questionId", questionsBo.getId());
        FormMappingBo formMappingBo = (FormMappingBo) query.uniqueResult();
        if (formMappingBo == null) {
          formMappingBo = new FormMappingBo();
          formMappingBo.setFormId(questionsBo.getFromId());
          formMappingBo.setQuestionId(questionsBo.getId());
          formMappingBo.setActive(true);
          int sequenceNo = 0;
          query =
              session
                  .createQuery(
                      "From FormMappingBo FMBO where FMBO.formId=:formId "
                          + " and FMBO.active=1 order by FMBO.sequenceNo DESC ")
                  .setInteger("formId", questionsBo.getFromId());
          query.setMaxResults(1);
          FormMappingBo existedFormMappingBo = (FormMappingBo) query.uniqueResult();
          if (existedFormMappingBo != null) {
            sequenceNo = existedFormMappingBo.getSequenceNo() + 1;
          } else {
            sequenceNo = sequenceNo + 1;
          }
          formMappingBo.setSequenceNo(sequenceNo);
          session.save(formMappingBo);
        }
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - saveOrUpdateQuestion() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - saveOrUpdateQuestion() - Ends");
    return questionsBo;
  }

  @Override
  public QuestionnaireBo saveORUpdateQuestionnaire(
      QuestionnaireBo questionnaireBo, SessionObject sessionObject, String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - saveORUpdateQuestionnaire() - Starts");
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      session.saveOrUpdate(questionnaireBo);
      if (questionnaireBo.getType().equalsIgnoreCase(FdahpStudyDesignerConstants.SCHEDULE)) {
        if ((questionnaireBo != null) && (questionnaireBo.getId() != null)) {
          if ((questionnaireBo.getQuestionnairesFrequenciesList() != null)
              && !questionnaireBo.getQuestionnairesFrequenciesList().isEmpty()) {
            query =
                session
                    .createSQLQuery("CALL deleteQuestionnaireFrequencies(:questionnaireId)")
                    .setInteger("questionnaireId", questionnaireBo.getId());
            query.executeUpdate();
            for (QuestionnairesFrequenciesBo questionnairesFrequenciesBo :
                questionnaireBo.getQuestionnairesFrequenciesList()) {
              if (questionnairesFrequenciesBo.getFrequencyTime() != null) {
                questionnairesFrequenciesBo.setFrequencyTime(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        questionnairesFrequenciesBo.getFrequencyTime(),
                        FdahpStudyDesignerConstants.SDF_TIME,
                        FdahpStudyDesignerConstants.UI_SDF_TIME));
                if (questionnairesFrequenciesBo.getQuestionnairesId() == null) {
                  questionnairesFrequenciesBo.setId(null);
                  questionnairesFrequenciesBo.setQuestionnairesId(questionnaireBo.getId());
                }
                session.saveOrUpdate(questionnairesFrequenciesBo);
              }
            }
          }
          if (questionnaireBo.getQuestionnairesFrequenciesBo() != null) {
            QuestionnairesFrequenciesBo questionnairesFrequenciesBo =
                questionnaireBo.getQuestionnairesFrequenciesBo();
            if (!questionnaireBo
                    .getFrequency()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)
                && !questionnaireBo
                    .getFrequency()
                    .equalsIgnoreCase(questionnaireBo.getPreviousFrequency())) {
              query =
                  session
                      .createSQLQuery("CALL deleteQuestionnaireFrequencies(:questionnaireId)")
                      .setInteger("questionnaireId", questionnaireBo.getId());
              query.executeUpdate();
            }
            if ((questionnairesFrequenciesBo.getFrequencyDate() != null)
                || (questionnairesFrequenciesBo.getFrequencyTime() != null)
                || questionnaireBo
                    .getFrequency()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
              if (questionnairesFrequenciesBo.getQuestionnairesId() == null) {
                questionnairesFrequenciesBo.setQuestionnairesId(questionnaireBo.getId());
              }
              if ((questionnaireBo.getQuestionnairesFrequenciesBo().getFrequencyDate() != null)
                  && !questionnaireBo
                      .getQuestionnairesFrequenciesBo()
                      .getFrequencyDate()
                      .isEmpty()) {
                questionnairesFrequenciesBo.setFrequencyDate(
                    FdahpStudyDesignerUtil.getFormattedDate(
                        questionnaireBo.getQuestionnairesFrequenciesBo().getFrequencyDate(),
                        FdahpStudyDesignerConstants.UI_SDF_DATE,
                        FdahpStudyDesignerConstants.SD_DATE_FORMAT));
              }
              if ((questionnaireBo.getQuestionnairesFrequenciesBo().getFrequencyTime() != null)
                  && !questionnaireBo
                      .getQuestionnairesFrequenciesBo()
                      .getFrequencyTime()
                      .isEmpty()) {
                questionnaireBo
                    .getQuestionnairesFrequenciesBo()
                    .setFrequencyTime(
                        FdahpStudyDesignerUtil.getFormattedDate(
                            questionnaireBo.getQuestionnairesFrequenciesBo().getFrequencyTime(),
                            FdahpStudyDesignerConstants.SDF_TIME,
                            FdahpStudyDesignerConstants.UI_SDF_TIME));
              }
              session.saveOrUpdate(questionnairesFrequenciesBo);
            }
          }
          if ((questionnaireBo.getQuestionnaireCustomScheduleBo() != null)
              && !questionnaireBo.getQuestionnaireCustomScheduleBo().isEmpty()) {
            query =
                session
                    .createSQLQuery("CALL deleteQuestionnaireFrequencies(:questionnaireId)")
                    .setInteger("questionnaireId", questionnaireBo.getId());
            query.executeUpdate();
            for (QuestionnaireCustomScheduleBo questionnaireCustomScheduleBo :
                questionnaireBo.getQuestionnaireCustomScheduleBo()) {
              if (questionnaireCustomScheduleBo.getFrequencyTime() != null) {
                if (questionnaireCustomScheduleBo.getQuestionnairesId() == null) {
                  questionnaireCustomScheduleBo.setQuestionnairesId(questionnaireBo.getId());
                }
                if ((questionnaireCustomScheduleBo.getFrequencyEndDate() != null)
                    && !questionnaireCustomScheduleBo.getFrequencyEndDate().isEmpty()) {
                  questionnaireCustomScheduleBo.setFrequencyEndDate(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          questionnaireCustomScheduleBo.getFrequencyEndDate(),
                          FdahpStudyDesignerConstants.UI_SDF_DATE,
                          FdahpStudyDesignerConstants.SD_DATE_FORMAT));
                }
                if ((questionnaireCustomScheduleBo.getFrequencyStartDate() != null)
                    && !questionnaireCustomScheduleBo.getFrequencyStartDate().isEmpty()) {
                  questionnaireCustomScheduleBo.setFrequencyStartDate(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          questionnaireCustomScheduleBo.getFrequencyStartDate(),
                          FdahpStudyDesignerConstants.UI_SDF_DATE,
                          FdahpStudyDesignerConstants.SD_DATE_FORMAT));
                }
                if ((questionnaireCustomScheduleBo.getFrequencyTime() != null)
                    && !questionnaireCustomScheduleBo.getFrequencyTime().isEmpty()) {
                  questionnaireCustomScheduleBo.setFrequencyTime(
                      FdahpStudyDesignerUtil.getFormattedDate(
                          questionnaireCustomScheduleBo.getFrequencyTime(),
                          FdahpStudyDesignerConstants.SDF_TIME,
                          FdahpStudyDesignerConstants.UI_SDF_TIME));
                }
                questionnaireCustomScheduleBo.setxDaysSign(
                    questionnaireCustomScheduleBo.isxDaysSign());
                if (questionnaireCustomScheduleBo.getTimePeriodFromDays() != null) {
                  questionnaireCustomScheduleBo.setTimePeriodFromDays(
                      questionnaireCustomScheduleBo.getTimePeriodFromDays());
                }
                questionnaireCustomScheduleBo.setyDaysSign(
                    questionnaireCustomScheduleBo.isyDaysSign());
                if (questionnaireCustomScheduleBo.getTimePeriodToDays() != null) {
                  questionnaireCustomScheduleBo.setTimePeriodToDays(
                      questionnaireCustomScheduleBo.getTimePeriodToDays());
                }
                session.saveOrUpdate(questionnaireCustomScheduleBo);
              }
            }
          }
        }
      }
      // updating the anchor date of an study while change the
      // questionnaire frequency in schedule part
      if (!questionnaireBo
          .getFrequency()
          .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME)) {
        String updateFromQuery =
            "update questions QBO,form_mapping f,questionnaires_steps QSBO SET QBO.use_anchor_date = 0 where "
                + " QBO.id=f.question_id and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=:questionnaireId "
                + " and QSBO.active=1 "
                + " and QSBO.step_type=:stepType "
                + " and QBO.active=1";
        query =
            session
                .createSQLQuery(updateFromQuery)
                .setInteger("questionnaireId", questionnaireBo.getId())
                .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP);
        query.executeUpdate();

        String updateQuestionSteps =
            "Update questions QBO,questionnaires_steps QSBO SET QBO.use_anchor_date = 0 where QBO.id=QSBO.instruction_form_id"
                + " and QSBO.questionnaires_id=:questionnaireId "
                + " and QSBO.active=1 and "
                + " QSBO.step_type=:stepType "
                + "  and QBO.active=1";
        query =
            session
                .createSQLQuery(updateQuestionSteps)
                .setInteger("questionnaireId", questionnaireBo.getId())
                .setString("stepType", FdahpStudyDesignerConstants.QUESTION_STEP);
        query.executeUpdate();
      }
      // updating the stastic option of dashboard while change the
      // questionnaire frequency
      if ((questionnaireBo.getFrequency() != null)
          && questionnaireBo
              .getFrequency()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {
        if ((questionnaireBo.getCurrentFrequency() != null)
            && (questionnaireBo.getPreviousFrequency() != null)
            && !questionnaireBo
                .getCurrentFrequency()
                .equalsIgnoreCase(questionnaireBo.getPreviousFrequency())) {
          updateLineChartSchedule(
              questionnaireBo.getId(),
              questionnaireBo.getCurrentFrequency(),
              sessionObject,
              session,
              transaction,
              customStudyId);
        }
      } else if ((questionnaireBo.getPreviousFrequency() != null)
          && !questionnaireBo
              .getFrequency()
              .equalsIgnoreCase(questionnaireBo.getPreviousFrequency())) {
        updateLineChartSchedule(
            questionnaireBo.getId(),
            questionnaireBo.getFrequency(),
            sessionObject,
            session,
            transaction,
            customStudyId);
      }

      if ((questionnaireBo != null) && questionnaireBo.getStatus()) {
        auditLogDAO.updateDraftToEditedStatus(
            session,
            transaction,
            sessionObject.getUserId(),
            FdahpStudyDesignerConstants.DRAFT_QUESTIONNAIRE,
            questionnaireBo.getStudyId());

        // Notification Purpose needed Started
        queryString = " From StudyBo where customStudyId=:customStudyId and live=1";
        StudyBo studyBo =
            (StudyBo)
                session
                    .createQuery(queryString)
                    .setString("customStudyId", customStudyId)
                    .uniqueResult();
        if (studyBo != null) {
          queryString = " From StudyBo where id=" + questionnaireBo.getStudyId();
          StudyBo draftStudyBo = (StudyBo) session.createQuery(queryString).uniqueResult();
          NotificationBO notificationBO = null;
          queryString =
              "From NotificationBO where questionnarieId=:questionnarieId "
                  + "and studyId=:studyId ";
          notificationBO =
              (NotificationBO)
                  session
                      .createQuery(queryString)
                      .setMaxResults(1)
                      .setInteger("studyId", questionnaireBo.getStudyId())
                      .setInteger("questionnarieId", questionnaireBo.getId())
                      .uniqueResult();
          if (!questionnaireBo.getScheduleType().equalsIgnoreCase("AnchorDate")) {
            if (notificationBO == null) {
              notificationBO = new NotificationBO();
              notificationBO.setStudyId(questionnaireBo.getStudyId());
              notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
              if (StringUtils.isNotEmpty(studyBo.getAppId())) {
                notificationBO.setAppId(studyBo.getAppId());
              }
              notificationBO.setNotificationType(FdahpStudyDesignerConstants.NOTIFICATION_ST);
              notificationBO.setNotificationSubType(
                  FdahpStudyDesignerConstants.NOTIFICATION_SUBTYPE_ACTIVITY);
              notificationBO.setNotificationScheduleType(
                  FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
              notificationBO.setQuestionnarieId(questionnaireBo.getId());
              notificationBO.setNotificationStatus(false);
              notificationBO.setCreatedBy(sessionObject.getUserId());
              notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              notificationBO.setNotificationSent(false);
            } else {
              notificationBO.setModifiedBy(sessionObject.getUserId());
              notificationBO.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            }
            notificationBO.setNotificationText(
                FdahpStudyDesignerConstants.NOTIFICATION_ACTIVETASK_TEXT
                    .replace("$shortTitle", questionnaireBo.getTitle())
                    .replace("$customId", draftStudyBo.getName()));
            if (!notificationBO.isNotificationSent()) {
              session.saveOrUpdate(notificationBO);
            }
          }
        }
        // Notification Purpose needed End
      }
      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - saveORUpdateQuestionnaire() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - saveORUpdateQuestionnaire() - Ends");
    return questionnaireBo;
  }

  @Override
  public QuestionnairesStepsBo saveOrUpdateQuestionStep(
      QuestionnairesStepsBo questionnairesStepsBo,
      SessionObject sessionObject,
      String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - saveOrUpdateQuestionStep() - Starts");
    Session session = null;
    QuestionnairesStepsBo addOrUpdateQuestionnairesStepsBo = null;
    boolean isChange = false;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      if (questionnairesStepsBo != null) {
        if (questionnairesStepsBo.getStepId() != null) {
          addOrUpdateQuestionnairesStepsBo =
              (QuestionnairesStepsBo)
                  session.get(QuestionnairesStepsBo.class, questionnairesStepsBo.getStepId());
        } else {
          addOrUpdateQuestionnairesStepsBo = new QuestionnairesStepsBo();
          addOrUpdateQuestionnairesStepsBo.setActive(true);
          addOrUpdateQuestionnairesStepsBo.setDestinationStep(0);
        }
        if ((questionnairesStepsBo.getStepShortTitle() != null)
            && !questionnairesStepsBo.getStepShortTitle().isEmpty()) {
          addOrUpdateQuestionnairesStepsBo.setStepShortTitle(
              questionnairesStepsBo.getStepShortTitle());
        }
        if ((questionnairesStepsBo.getSkiappable() != null)
            && !questionnairesStepsBo.getSkiappable().isEmpty()) {
          addOrUpdateQuestionnairesStepsBo.setSkiappable(questionnairesStepsBo.getSkiappable());
        }
        if ((questionnairesStepsBo.getRepeatable() != null)
            && !questionnairesStepsBo.getRepeatable().isEmpty()) {
          addOrUpdateQuestionnairesStepsBo.setRepeatable(questionnairesStepsBo.getRepeatable());
        }
        if ((questionnairesStepsBo.getRepeatableText() != null)
            && !questionnairesStepsBo.getRepeatableText().isEmpty()) {
          addOrUpdateQuestionnairesStepsBo.setRepeatableText(
              questionnairesStepsBo.getRepeatableText());
        }
        if (questionnairesStepsBo.getDestinationStep() != null) {
          addOrUpdateQuestionnairesStepsBo.setDestinationStep(
              questionnairesStepsBo.getDestinationStep());
        }
        if (questionnairesStepsBo.getQuestionnairesId() != null) {
          addOrUpdateQuestionnairesStepsBo.setQuestionnairesId(
              questionnairesStepsBo.getQuestionnairesId());
        }
        if (questionnairesStepsBo.getInstructionFormId() != null) {
          addOrUpdateQuestionnairesStepsBo.setInstructionFormId(
              questionnairesStepsBo.getInstructionFormId());
        }
        if (questionnairesStepsBo.getStepType() != null) {
          addOrUpdateQuestionnairesStepsBo.setStepType(questionnairesStepsBo.getStepType());
        }
        if (questionnairesStepsBo.getType() != null) {
          if (questionnairesStepsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)) {
            addOrUpdateQuestionnairesStepsBo.setStatus(false);

            query =
                session
                    .createSQLQuery(
                        "update questionnaires q set q.status=0 where q.id=:questionnairesId ")
                    .setInteger(
                        "questionnairesId", addOrUpdateQuestionnairesStepsBo.getQuestionnairesId());
            query.executeUpdate();
          } else if (questionnairesStepsBo
              .getType()
              .equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)) {
            addOrUpdateQuestionnairesStepsBo.setStatus(true);
          }
        }
        int count = 0;
        if (questionnairesStepsBo.getQuestionsBo() != null) {
          addOrUpdateQuestionnairesStepsBo.setQuestionnairesId(
              addOrUpdateQuestionnairesStepsBo.getQuestionnairesId());
          QuestionsBo questionsBo = questionnairesStepsBo.getQuestionsBo();
          // Ancrodate text start
          if ((questionnairesStepsBo.getQuestionsBo().getUseAnchorDate() != null)
              && questionnairesStepsBo.getQuestionsBo().getUseAnchorDate()) {
            if (StringUtils.isNotEmpty(
                questionnairesStepsBo.getQuestionsBo().getAnchorDateName())) {
              Integer studyId = this.getStudyIdByCustomStudy(session, customStudyId);
              AnchorDateTypeBo anchorDateTypeBo = new AnchorDateTypeBo();
              anchorDateTypeBo.setId(questionnairesStepsBo.getQuestionsBo().getAnchorDateId());
              anchorDateTypeBo.setCustomStudyId(customStudyId);
              anchorDateTypeBo.setStudyId(studyId);
              anchorDateTypeBo.setName(questionnairesStepsBo.getQuestionsBo().getAnchorDateName());
              anchorDateTypeBo.setHasAnchortypeDraft(1);
              session.saveOrUpdate(anchorDateTypeBo);
              if (anchorDateTypeBo.getId() != null) {
                questionsBo.setAnchorDateId(anchorDateTypeBo.getId());
              }
            }
          } else {
            if ((questionnairesStepsBo.getIsShorTitleDuplicate() != null)
                && (questionnairesStepsBo.getIsShorTitleDuplicate() > 0)) {
              isChange = true;
            }
            if (StringUtils.isEmpty(customStudyId)) {
              customStudyId = questionsBo.getCustomStudyId();
            }
            if ((questionnairesStepsBo.getQuestionsBo().getAnchorDateId() != null)
                && (questionsBo != null)
                && (questionsBo.getId() != null)) {
              query =
                  session
                      .getNamedQuery("getStudyByCustomStudyId")
                      .setString("customStudyId", customStudyId);
              query.setMaxResults(1);
              StudyVersionBo studyVersionBo = (StudyVersionBo) query.uniqueResult();
              Integer studyId = this.getStudyIdByCustomStudy(session, customStudyId);
              updateAnchordateInQuestionnaire(
                  session,
                  transaction,
                  studyVersionBo,
                  null,
                  sessionObject,
                  studyId,
                  null,
                  questionsBo.getId(),
                  "",
                  isChange);
            }

            questionsBo.setAnchorDateId(null);
          }
          // Anchordate Text end
          session.saveOrUpdate(questionsBo);
          addOrUpdateQuestionnairesStepsBo.setQuestionsBo(questionsBo);
          // adding or updating the response level attributes
          if ((questionsBo != null)
              && (questionsBo.getId() != null)
              && (questionnairesStepsBo.getQuestionReponseTypeBo() != null)) {
            QuestionReponseTypeBo questionResponseTypeBo =
                getQuestionsResponseTypeBo(
                    questionnairesStepsBo.getQuestionReponseTypeBo(), session);
            if (questionResponseTypeBo != null) {
              if (questionResponseTypeBo.getQuestionsResponseTypeId() == null) {
                questionResponseTypeBo.setQuestionsResponseTypeId(questionsBo.getId());
              }
              session.saveOrUpdate(questionResponseTypeBo);
            }
            addOrUpdateQuestionnairesStepsBo.setQuestionReponseTypeBo(questionResponseTypeBo);
            if ((questionnairesStepsBo.getQuestionResponseSubTypeList() != null)
                && !questionnairesStepsBo.getQuestionResponseSubTypeList().isEmpty()) {
              String deletQuesry =
                  "Delete From QuestionResponseSubTypeBo QRSTBO where QRSTBO.responseTypeId=:responseTypeId ";
              session
                  .createQuery(deletQuesry)
                  .setInteger("responseTypeId", questionsBo.getId())
                  .executeUpdate();
              // upload the images in response level
              if ((questionnairesStepsBo.getQuestionsBo().getResponseType() == 4)
                  || (questionnairesStepsBo.getQuestionsBo().getResponseType() == 3)
                  || (questionnairesStepsBo.getQuestionsBo().getResponseType() == 6)
                  || (questionnairesStepsBo.getQuestionsBo().getResponseType() == 5)) {
                int j = 0;
                for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                    questionnairesStepsBo.getQuestionResponseSubTypeList()) {
                  if (questionsBo.getResponseType() != 5) {
                    if (((questionResponseSubTypeBo.getText() != null)
                            && !questionResponseSubTypeBo.getText().isEmpty())
                        || ((questionResponseSubTypeBo.getValue() != null)
                            && !questionResponseSubTypeBo.getValue().isEmpty())
                        || ((questionResponseSubTypeBo.getDescription() != null)
                            && !questionResponseSubTypeBo.getDescription().isEmpty())
                        || ((questionResponseSubTypeBo.getExclusive() != null)
                            && !questionResponseSubTypeBo.getExclusive().isEmpty())) {
                      questionResponseSubTypeBo.setResponseTypeId(questionsBo.getId());
                      questionResponseSubTypeBo.setActive(true);
                      session.save(questionResponseSubTypeBo);
                    }

                  } else {
                    if (((questionResponseSubTypeBo.getText() != null)
                            && !questionResponseSubTypeBo.getText().isEmpty())
                        || ((questionResponseSubTypeBo.getValue() != null)
                            && !questionResponseSubTypeBo.getValue().isEmpty())
                        || questionResponseSubTypeBo.getImageFile() != null
                        || questionResponseSubTypeBo.getSelectImageFile() != null) {
                      String fileName;
                      if (questionResponseSubTypeBo.getImageFile() != null) {
                        if ((questionResponseSubTypeBo.getImage() != null)
                            && !questionResponseSubTypeBo.getImage().isEmpty()) {
                          questionResponseSubTypeBo.setImage(questionResponseSubTypeBo.getImage());
                        } else {
                          fileName =
                              FdahpStudyDesignerUtil.getStandardFileName(
                                  FdahpStudyDesignerConstants.QUESTION_STEP_IMAGE + j,
                                  questionResponseSubTypeBo.getImageFile().getOriginalFilename(),
                                  String.valueOf(questionnairesStepsBo.getQuestionsBo().getId()));

                          String imagePath =
                              FdahpStudyDesignerUtil.saveImage(
                                  questionResponseSubTypeBo.getImageFile(),
                                  fileName,
                                  FdahpStudyDesignerConstants.QUESTIONNAIRE);
                          questionResponseSubTypeBo.setImage(imagePath);
                        }
                      }
                      if (questionResponseSubTypeBo.getSelectImageFile() != null) {
                        if ((questionResponseSubTypeBo.getSelectedImage() != null)
                            && !questionResponseSubTypeBo.getSelectedImage().isEmpty()) {
                          questionResponseSubTypeBo.setSelectedImage(
                              questionResponseSubTypeBo.getSelectedImage());
                        } else {
                          fileName =
                              FdahpStudyDesignerUtil.getStandardFileName(
                                  FdahpStudyDesignerConstants.QUESTION_STEP_SELECTEDIMAGE + j,
                                  questionResponseSubTypeBo
                                      .getSelectImageFile()
                                      .getOriginalFilename(),
                                  String.valueOf(questionnairesStepsBo.getQuestionsBo().getId()));
                          String imagePath =
                              FdahpStudyDesignerUtil.saveImage(
                                  questionResponseSubTypeBo.getSelectImageFile(),
                                  fileName,
                                  FdahpStudyDesignerConstants.QUESTIONNAIRE);
                          questionResponseSubTypeBo.setSelectedImage(imagePath);
                        }
                      }
                      questionResponseSubTypeBo.setResponseTypeId(questionsBo.getId());
                      questionResponseSubTypeBo.setActive(true);
                      session.save(questionResponseSubTypeBo);
                    }
                  }
                  j = j + 1;
                }
              } else {
                for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                    questionnairesStepsBo.getQuestionResponseSubTypeList()) {
                  questionResponseSubTypeBo.setResponseTypeId(questionsBo.getId());
                  questionResponseSubTypeBo.setActive(true);
                  session.save(questionResponseSubTypeBo);
                }
              }
            } else {
              String deletQuesry =
                  "Delete From QuestionResponseSubTypeBo QRSTBO where QRSTBO.responseTypeId="
                      + questionsBo.getId();
              session.createQuery(deletQuesry).executeUpdate();
            }
            // condition branching adding for the response type that
            // results in the data type 'double' and checked as
            // formula based branching is yes
            if ((questionResponseTypeBo != null)
                && questionResponseTypeBo
                    .getFormulaBasedLogic()
                    .equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
              if ((questionnairesStepsBo.getQuestionConditionBranchBoList() != null)
                  && !questionnairesStepsBo.getQuestionConditionBranchBoList().isEmpty()) {
                String deleteQuery =
                    "delete from question_condtion_branching where question_id=:questionId ";
                session
                    .createSQLQuery(deleteQuery)
                    .setInteger("questionId", questionsBo.getId())
                    .executeUpdate();
                for (QuestionConditionBranchBo questionConditionBranchBo :
                    questionnairesStepsBo.getQuestionConditionBranchBoList()) {
                  if (questionConditionBranchBo.getQuestionId() == null) {
                    questionConditionBranchBo.setQuestionId(questionsBo.getId());
                  }
                  if ((questionConditionBranchBo.getInputType() != null)
                      && questionConditionBranchBo.getInputType().equalsIgnoreCase("MF")) {
                    questionConditionBranchBo.setInputTypeValue(
                        questionConditionBranchBo
                            .getInputTypeValue()
                            .replace("&lt;", "<")
                            .replace("&gt;", ">"));
                    session.save(questionConditionBranchBo);
                  }
                  if ((questionConditionBranchBo.getQuestionConditionBranchBos() != null)
                      && !questionConditionBranchBo.getQuestionConditionBranchBos().isEmpty()) {
                    for (QuestionConditionBranchBo conditionBranchBo :
                        questionConditionBranchBo.getQuestionConditionBranchBos()) {
                      if ((conditionBranchBo.getInputType() != null)
                          && (conditionBranchBo.getInputTypeValue() != null)) {
                        if (conditionBranchBo.getQuestionId() == null) {
                          conditionBranchBo.setQuestionId(questionsBo.getId());
                        }
                        conditionBranchBo.setInputTypeValue(
                            conditionBranchBo
                                .getInputTypeValue()
                                .replace("&lt;", "<")
                                .replace("&gt;", ">"));
                        session.save(conditionBranchBo);
                      }
                    }
                  }
                }
              }
            } else {
              String deleteQuery =
                  "delete from question_condtion_branching where question_id=:questionId ";
              session
                  .createSQLQuery(deleteQuery)
                  .setInteger("questionId", questionsBo.getId())
                  .executeUpdate();
            }
          }

          if (questionsBo != null) {
            addOrUpdateQuestionnairesStepsBo.setInstructionFormId(questionsBo.getId());
          }
          // updating the sequence no of step based on the previous
          // sequence no
          if ((addOrUpdateQuestionnairesStepsBo.getQuestionnairesId() != null)
              && (addOrUpdateQuestionnairesStepsBo.getStepId() == null)) {
            QuestionnairesStepsBo existedQuestionnairesStepsBo = null;
            query =
                session
                    .getNamedQuery("getQuestionnaireStepSequenceNo")
                    .setInteger(
                        "questionnairesId", addOrUpdateQuestionnairesStepsBo.getQuestionnairesId());
            query.setMaxResults(1);
            existedQuestionnairesStepsBo = (QuestionnairesStepsBo) query.uniqueResult();
            if (existedQuestionnairesStepsBo != null) {
              count = existedQuestionnairesStepsBo.getSequenceNo() + 1;
            } else {
              count = count + 1;
            }
            addOrUpdateQuestionnairesStepsBo.setSequenceNo(count);
          }
        }
        session.saveOrUpdate(addOrUpdateQuestionnairesStepsBo);
        // updating the destination step for previous step
        if ((addOrUpdateQuestionnairesStepsBo != null) && (count > 0)) {
          String updateQuery =
              "update QuestionnairesStepsBo QSBO set QSBO.destinationStep=:stepId "
                  + " where "
                  + "QSBO.destinationStep=0 and QSBO.sequenceNo=:sequenceNo"
                  + " and QSBO.questionnairesId=:questionnairesId ";
          session
              .createQuery(updateQuery)
              .setInteger("sequenceNo", (count - 1))
              .setInteger("stepId", addOrUpdateQuestionnairesStepsBo.getStepId())
              .setInteger(
                  "questionnairesId", addOrUpdateQuestionnairesStepsBo.getQuestionnairesId())
              .executeUpdate();
        }
      }

      transaction.commit();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - saveOrUpdateQuestionStep() - Error", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - saveOrUpdateQuestionStep() - Ends");
    return addOrUpdateQuestionnairesStepsBo;
  }

  @Autowired
  public void setSessionFactory(SessionFactory sessionFactory) {
    this.hibernateTemplate = new HibernateTemplate(sessionFactory);
  }

  public String updateLineChartSchedule(
      Integer questionnaireId,
      String frequency,
      SessionObject sessionObject,
      Session session,
      Transaction transaction,
      String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - updateLineChartSchedule() - starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    String[] timeRange = null;
    Session newSession = null;
    try {
      if (session == null) {
        newSession = hibernateTemplate.getSessionFactory().openSession();
        transaction = newSession.beginTransaction();
      }
      timeRange = FdahpStudyDesignerUtil.getTimeRangeString(frequency);
      // updating the question steps
      String searchQuery =
          " update questions QBO,questionnaires_steps QSBO set QBO.status=0, QBO.modified_by=:userId "
              + ",QBO.modified_on=:currentDateAndTime "
              + " where QBO.id=QSBO.instruction_form_id and QSBO.questionnaires_id=:questionnaireId "
              + " and QSBO.step_type='Question' and QSBO.active=1 and QBO.active=1 and QBO.add_line_chart='Yes' and QBO.line_chart_timerange not in ("
              + " :timeRange )";
      if (newSession != null) {
        newSession
            .createSQLQuery(searchQuery)
            .setInteger("userId", sessionObject.getUserId())
            .setString("currentDateAndTime", FdahpStudyDesignerUtil.getCurrentDateTime())
            .setInteger("questionnaireId", questionnaireId)
            .setParameterList("timeRange", Arrays.asList(timeRange))
            .executeUpdate();
      } else {
        session
            .createSQLQuery(searchQuery)
            .setInteger("userId", sessionObject.getUserId())
            .setString("currentDateAndTime", FdahpStudyDesignerUtil.getCurrentDateTime())
            .setInteger("questionnaireId", questionnaireId)
            .setParameterList("timeRange", Arrays.asList(timeRange))
            .executeUpdate();
      }
      // updating the form step questions
      String formQuery =
          "update questionnaires_steps qs,form_mapping f, questions QBO  set qs.status=0,qs.modified_by=:userId "
              + ",qs.modified_on=:currentDateAndTime "
              + ",QBO.status=0,QBO.modified_by=:userId "
              + ",QBO.modified_on=:currentDateAndTime"
              + " where qs.step_type = 'Form' and qs.instruction_form_id= f.form_id"
              + " and f.question_id = QBO.id and f.active=1 and QBO.active=1 and QBO.add_line_chart='Yes' "
              + " and QBO.line_chart_timerange not in (:timeRange "
              + " ) and qs.questionnaires_id=:questionnaireId "
              + " and qs.active=1";
      if (newSession != null) {
        newSession
            .createSQLQuery(formQuery)
            .setInteger("userId", sessionObject.getUserId())
            .setString("currentDateAndTime", FdahpStudyDesignerUtil.getCurrentDateTime())
            .setInteger("questionnaireId", questionnaireId)
            .setParameterList("timeRange", Arrays.asList(timeRange))
            .executeUpdate();
      } else {
        session
            .createSQLQuery(formQuery)
            .setInteger("userId", sessionObject.getUserId())
            .setString("currentDateAndTime", FdahpStudyDesignerUtil.getCurrentDateTime())
            .setInteger("questionnaireId", questionnaireId)
            .setParameterList("timeRange", Arrays.asList(timeRange))
            .executeUpdate();
      }

      if (session == null) {
        transaction.commit();
      }
    } catch (Exception e) {
      if ((session == null) && (null != transaction)) {
        transaction.rollback();
      }
      logger.error("StudyQuestionnaireDAOImpl - updateLineChartSchedule() - ERROR ", e);
    } finally {
      if (null != newSession) {
        newSession.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - updateLineChartSchedule() - Ends");
    return message;
  }

  @Override
  public String validateLineChartSchedule(Integer questionnaireId, String frequency) {
    logger.info("StudyQuestionnaireDAOImpl - validateLineChartSchedule() - starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    String[] timeRange = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      timeRange = FdahpStudyDesignerUtil.getTimeRangeString(frequency);
      // checking in the question step
      String searchQuery =
          "select count(*) from questions QBO,questionnaires_steps QSBO where QBO.id=QSBO.instruction_form_id and QSBO.questionnaires_id=:questionnaireId "
              + " and QSBO.active=1 and QSBO.step_type=:stepType "
              + " and QBO.active=1 and QBO.add_line_chart='Yes' and QBO.line_chart_timerange not in ( :timeRange ) ";

      BigInteger count =
          (BigInteger)
              session
                  .createSQLQuery(searchQuery)
                  .setString("stepType", FdahpStudyDesignerConstants.QUESTION_STEP)
                  .setInteger("questionnaireId", questionnaireId)
                  .setParameterList("timeRange", Arrays.asList(timeRange))
                  .uniqueResult();
      if ((count != null) && (count.intValue() > 0)) {
        message = FdahpStudyDesignerConstants.SUCCESS;
      } else {
        // checking in the form step questions
        String searchSubQuery =
            "select count(*) from questions QBO,form_mapping f,questionnaires_steps QSBO where QBO.id=f.question_id and f.form_id=QSBO.instruction_form_id and QSBO.questionnaires_id=:questionnaireId "
                + " and QSBO.active=1 and QSBO.step_type=:stepType "
                + " and QBO.active=1 and QBO.add_line_chart = 'Yes' and QBO.line_chart_timerange not in (:timeRange) ";
        BigInteger subCount =
            (BigInteger)
                session
                    .createSQLQuery(searchSubQuery)
                    .setString("stepType", FdahpStudyDesignerConstants.FORM_STEP)
                    .setInteger("questionnaireId", questionnaireId)
                    .setParameterList("timeRange", Arrays.asList(timeRange))
                    .uniqueResult();
        if ((subCount != null) && (subCount.intValue() > 0)) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - validateLineChartSchedule() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - validateLineChartSchedule() - Ends");
    return message;
  }

  @Override
  public String validateRepetableFormQuestionStats(Integer formId) {
    logger.info("StudyQuestionnaireDAOImpl - validateRepetableFormQuestionStats() - starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      String searchQuery =
          "select count(*) from questions q,form_mapping f where q.id=f.question_id and q.active=1 and f.active=1 and f.form_id=:formId "
              + " and (q.add_line_chart = 'Yes' or q.use_stastic_data='Yes' or q.use_anchor_date=true)";
      BigInteger questionCount =
          (BigInteger)
              session.createSQLQuery(searchQuery).setInteger("formId", formId).uniqueResult();
      if ((questionCount != null) && (questionCount.intValue() > 0)) {
        message = FdahpStudyDesignerConstants.SUCCESS;
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - validateRepetableFormQuestionStats() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - validateRepetableFormQuestionStats() - Ends");
    return message;
  }

  @Override
  public String checkUniqueAnchorDateName(
      String anchordateText, String customStudyId, String anchorDateId) {
    logger.info("StudyQuestionnaireDAOImpl - checkUniqueAnchorDateName() - starts");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Session session = null;
    Integer dbAnchorId = 0;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();

      if (StringUtils.isNotEmpty(anchorDateId)) {
        dbAnchorId =
            (Integer)
                session
                    .createSQLQuery("select q.id from anchordate_type q where q.id=:anchorDateId ")
                    .setString("anchorDateId", anchorDateId)
                    .uniqueResult();
        if (!dbAnchorId.equals(Integer.parseInt(anchorDateId))) {
          dbAnchorId = 0;
        }
      }
      if (dbAnchorId == 0) {
        String searchQuery =
            "select count(*) from anchordate_type a"
                + " where a.name=:anchordateText "
                + " and a.custom_study_id=:customStudyId ";
        BigInteger questionCount =
            (BigInteger)
                session
                    .createSQLQuery(searchQuery)
                    .setString("anchordateText", anchordateText)
                    .setString("customStudyId", customStudyId)
                    .uniqueResult();
        if ((questionCount != null) && (questionCount.intValue() > 0)) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - checkUniqueAnchorDateName() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - checkUniqueAnchorDateName() - Ends");
    return message;
  }

  @Override
  public Integer getStudyIdByCustomStudy(Session session, String customStudyId) {
    logger.info(
        "INFO: ActivityMetaDataDao - getQuestionnaireFrequencyAncorDetailsForManuallySchedule() :: Starts");
    Integer studyId = null;
    try {
      String searchQuery =
          "select id from studies where custom_study_id=:customStudyId  and is_live=0";
      studyId =
          (Integer)
              session
                  .createSQLQuery(searchQuery)
                  .setString("customStudyId", customStudyId)
                  .uniqueResult();

    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - checkUniqueAnchorDateName() - ERROR ", e);
    }
    return studyId;
  }

  @SuppressWarnings("unchecked")
  @Override
  public List<AnchorDateTypeBo> getAnchorTypesByStudyId(String customStudyId) {
    logger.info("StudyQuestionnaireDAOImpl - getAnchorTypesByStudyId - Starts");
    Session session = null;
    List<AnchorDateTypeBo> anchorDateTypeBos = null;
    String queryString = "";
    String subQuery = "";
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      StudyBo studyBo =
          (StudyBo)
              session
                  .createQuery("from StudyBo where customStudyId=:customStudyId and live=0")
                  .setString("customStudyId", customStudyId)
                  .uniqueResult();
      if (studyBo != null) {
        if (!studyBo.isEnrollmentdateAsAnchordate()) {
          subQuery = "and name != '" + FdahpStudyDesignerConstants.ANCHOR_TYPE_ENROLLMENTDATE + "'";
        }
      }

      // Added by sweta
      queryString =
          "From AnchorDateTypeBo where customStudyId=:customStudyId  and hasAnchortypeDraft=1";
      anchorDateTypeBos =
          session.createQuery(queryString).setString("customStudyId", customStudyId).list();

    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - getAnchorTypesByStudyId - ERROR ", e);
    } finally {
      if ((null != session) && session.isOpen()) {
        session.close();
      }
    }
    logger.info("StudyQuestionnaireDAOImpl - getAnchorTypesByStudyId - Ends");
    return anchorDateTypeBos;
  }

  @Override
  public boolean isAnchorDateExistByQuestionnaire(Integer questionnaireId) {
    logger.info("StudyQuestionnaireDAOImpl - isAnchorDateExistByQuestionnaire - Starts");
    Session session = null;
    Boolean isExist = false;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      // checking in the question step anchor date is selected or not
      String searchQuery =
          "select count(q.anchor_date_id) from questions q,questionnaires_steps qsq,questionnaires qq  where q.id=qsq.instruction_form_id and qsq.step_type='Question' "
              + "and qsq.active=1 and qsq.questionnaires_id=qq.id and qq.id=:questionnaireId "
              + " and qq.active=1 and q.active=1;";
      BigInteger count =
          (BigInteger)
              session
                  .createSQLQuery(searchQuery)
                  .setInteger("questionnaireId", questionnaireId)
                  .uniqueResult();
      if (count.intValue() > 0) {
        isExist = true;
      } else {
        // checking in the form step question anchor date is
        // selected or not
        String subQuery =
            "select count(q.anchor_date_id) from questions q,form_mapping fm,form f,questionnaires_steps qsf,questionnaires qq where q.id=fm.question_id and f.form_id=fm.form_id and f.active=1 "
                + "and f.form_id=qsf.instruction_form_id and qsf.step_type='Form' and qsf.questionnaires_id=qq.id and qq.id=:questionnaireId "
                + " and q.active=1";
        BigInteger subCount =
            (BigInteger)
                session
                    .createSQLQuery(subQuery)
                    .setInteger("questionnaireId", questionnaireId)
                    .uniqueResult();
        if ((subCount != null) && (subCount.intValue() > 0)) {
          isExist = true;
        }
      }

    } catch (Exception e) {
      logger.error("StudyQuestionnaireDAOImpl - isAnchorDateExistByQuestionnaire() - ERROR ", e);
    }
    logger.info("StudyQuestionnaireDAOImpl - isAnchorDateExistByQuestionnaire - Ends");
    return isExist;
  }

  @SuppressWarnings("unchecked")
  @Override
  public String updateAnchordateInQuestionnaire(
      Session session,
      Transaction transaction,
      StudyVersionBo studyVersionBo,
      Integer questionnaireId,
      SessionObject sessionObject,
      Integer studyId,
      Integer stepId,
      Integer questionId,
      String stepType,
      boolean isChange) {
    logger.info("StudyQuestionnaireDAOImpl - updateAnchordateInQuestionnaire - Starts");
    List<Integer> anchorIds = new ArrayList<Integer>();
    List<Integer> anchorExistIds = new ArrayList<Integer>();
    Boolean isAnchorUsed = false;
    String searchQuery = "";
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      if (!stepType.isEmpty()) {
        if (stepType.equalsIgnoreCase(FdahpStudyDesignerConstants.QUESTION_STEP)) {
          searchQuery =
              "select q.anchor_date_id from questions q,questionnaires_steps qsq,questionnaires qq where q.id=qsq.instruction_form_id and qsq.step_type='Question' "
                  + "and qsq.active=1 and qsq.questionnaires_id=qq.id and qq.id=:questionnaireId "
                  + " and q.id=:stepId "
                  + " and qq.active=1 and q.active=1"
                  + " and q.anchor_date_id IS NOT NULL;";
          List<Integer> aIds =
              session
                  .createSQLQuery(searchQuery)
                  .setInteger("stepId", stepId)
                  .setInteger("questionnaireId", questionnaireId)
                  .list();

          if ((aIds != null) && (aIds.size() > 0)) {
            anchorIds.addAll(aIds);
          }
        } else if (stepType.equalsIgnoreCase(FdahpStudyDesignerConstants.FORM_STEP)) {
          String subQuery =
              "select q.anchor_date_id from questions q,form_mapping fm,form f,questionnaires_steps qsf,questionnaires qq where q.id=fm.question_id and f.form_id=fm.form_id and f.active=1 "
                  + "and f.form_id=qsf.instruction_form_id and qsf.step_type='Form' and qsf.questionnaires_id=qq.id and qq.id=:questionnaireId "
                  + " and f.form_id=:stepId "
                  + " and q.active=1"
                  + " and q.anchor_date_id IS NOT NULL;";
          List<Integer> aaIds =
              session
                  .createSQLQuery(subQuery)
                  .setInteger("stepId", stepId)
                  .setInteger("questionnaireId", questionnaireId)
                  .list();
          if ((aaIds != null) && (aaIds.size() > 0)) {
            anchorIds.addAll(aaIds);
          }
        }
      }
      // Question level deletion
      if (questionId != null) {
        searchQuery =
            "select q.anchor_date_id from questions q where q.active=1 and q.id=:id"
                + " and q.anchor_date_id IS NOT NULL;";
        List<Integer> aIds =
            session.createSQLQuery(searchQuery).setParameter("id", questionId).list();
        if ((aIds != null) && (aIds.size() > 0)) {
          anchorIds.addAll(aIds);
        }
      }
      // Questionnaire level deletion
      if ((stepId == null) && (questionnaireId != null)) {
        // checking in the question step anchor date is selected or not
        searchQuery =
            "select q.anchor_date_id from questions q,questionnaires_steps qsq,questionnaires qq where q.id=qsq.instruction_form_id and qsq.step_type='Question'"
                + " and qsq.active=1 and qsq.questionnaires_id=qq.id and qq.id=:questionnaireId "
                + " and qq.active=1 and q.active=1"
                + " and q.anchor_date_id IS NOT NULL;";
        List<Integer> aIds =
            session
                .createSQLQuery(searchQuery)
                .setInteger("questionnaireId", questionnaireId)
                .list();
        if ((aIds != null) && (aIds.size() > 0)) {
          anchorIds.addAll(aIds);
        }
        // checking in the form step question anchor date is
        // selected or not
        String subQuery =
            "select q.anchor_date_id from questions q,form_mapping fm,form f,questionnaires_steps qsf,questionnaires qq where q.id=fm.question_id and f.form_id=fm.form_id and f.active=1 "
                + "and f.form_id=qsf.instruction_form_id and qsf.step_type='Form' and qsf.questionnaires_id=qq.id and qq.id=:questionnaireId"
                + " and q.active=1"
                + " and q.anchor_date_id IS NOT NULL;";
        List<Integer> aaIds =
            session.createSQLQuery(subQuery).setInteger("questionnaireId", questionnaireId).list();
        if ((aaIds != null) && (aaIds.size() > 0)) {
          anchorIds.addAll(aaIds);
        }
      }
      if (!anchorIds.isEmpty() && (anchorIds.size() > 0)) {
        searchQuery =
            "select q.id from questionnaires q where q.schedule_type=:type"
                + " and q.anchor_date_id in( :anchorIds )";
        anchorExistIds =
            session
                .createSQLQuery(searchQuery)
                .setParameter("type", FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE)
                .setParameterList("anchorIds", anchorIds)
                .list();
        if (!anchorExistIds.isEmpty() && (anchorExistIds.size() > 0)) {
          isAnchorUsed = true;
        } else {
          searchQuery =
              "select q.id from active_task q where q.schedule_type=:type"
                  + " and q.anchor_date_id in( :anchorIds )";
          anchorExistIds =
              session
                  .createSQLQuery(searchQuery)
                  .setParameter("type", FdahpStudyDesignerConstants.SCHEDULETYPE_ANCHORDATE)
                  .setParameterList("anchorIds", anchorIds)
                  .list();
          if (!anchorExistIds.isEmpty() && (anchorExistIds.size() > 0)) {
            isAnchorUsed = true;
          } else {
            searchQuery = "select q.id from resources q where q.anchor_date_id in( :anchorIds )";
            anchorExistIds =
                session.createSQLQuery(searchQuery).setParameterList("anchorIds", anchorIds).list();
            if (!anchorExistIds.isEmpty() && (anchorExistIds.size() > 0)) {
              isAnchorUsed = true;
            }
          }
        }
        if ((studyVersionBo != null) && isChange) {
          if (isAnchorUsed) {
            message = FdahpStudyDesignerConstants.FAILURE + "anchorused";
            return message;
          } else {
            String deleteAncQuery = "delete from anchordate_type where id IN( :anchorIds )";
            query = session.createSQLQuery(deleteAncQuery).setParameterList("anchorIds", anchorIds);
            query.executeUpdate();
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        } else {
          if (isAnchorUsed) {
            StudySequenceBo studySequence =
                (StudySequenceBo)
                    session
                        .getNamedQuery("getStudySequenceByStudyId")
                        .setInteger("studyId", studyId)
                        .uniqueResult();
            if (studySequence != null) {
              int count1 =
                  session
                      .createSQLQuery(
                          "update questionnaires set status=0,anchor_date_id=null,"
                              + "modified_by=:userId "
                              + ",modified_date=:currentDateAndTime"
                              + " where active=1 and anchor_date_id in( :anchorIds ) ")
                      .setInteger("userId", sessionObject.getUserId())
                      .setParameterList("anchorIds", anchorIds)
                      .setString("currentDateAndTime", FdahpStudyDesignerUtil.getCurrentDateTime())
                      .executeUpdate();
              if (count1 > 0) {
                studySequence.setStudyExcQuestionnaries(false);
                auditLogDAO.updateDraftToEditedStatus(
                    session,
                    transaction,
                    sessionObject.getUserId(),
                    FdahpStudyDesignerConstants.DRAFT_QUESTIONNAIRE,
                    studyId);
              }
              int count2 =
                  session
                      .createSQLQuery(
                          "update active_task set action=0 ,anchor_date_id=null, modified_by=:userId "
                              + ",modified_date=:currentDateAndTime"
                              + " where active=1 and anchor_date_id in( :anchorIds )")
                      .setInteger("userId", sessionObject.getUserId())
                      .setParameterList("anchorIds", anchorIds)
                      .setString("currentDateAndTime", FdahpStudyDesignerUtil.getCurrentDateTime())
                      .executeUpdate();
              if (count2 > 0) {
                studySequence.setStudyExcActiveTask(false);
                auditLogDAO.updateDraftToEditedStatus(
                    session,
                    transaction,
                    sessionObject.getUserId(),
                    FdahpStudyDesignerConstants.DRAFT_ACTIVETASK,
                    studyId);
              }
              int count3 =
                  session
                      .createSQLQuery(
                          "update resources set action=0,anchor_date_id=null "
                              + "where status=1 and anchor_date_id in( :anchorIds )")
                      .setParameterList("anchorIds", anchorIds)
                      .executeUpdate();

              if (count3 > 0) {
                studySequence.setMiscellaneousResources(false);
                auditLogDAO.updateDraftToEditedStatus(
                    session,
                    transaction,
                    sessionObject.getUserId(),
                    FdahpStudyDesignerConstants.DRAFT_STUDY,
                    studyId);
              }
              session.saveOrUpdate(studySequence);
            }
          }
          String deleteAncQuery = "delete from anchordate_type where id IN( :anchorIds )";
          query = session.createSQLQuery(deleteAncQuery).setParameterList("anchorIds", anchorIds);
          query.executeUpdate();
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      } else {
        message = FdahpStudyDesignerConstants.SUCCESS;
      }
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - updateAnchordateInQuestionnaire - ERROR ", e);
    }
    logger.info("StudyQuestionnaireDAOImpl - updateAnchordateInQuestionnaire - Ends");
    return message;
  }

  @Override
  public QuestionnaireBo getQuestionnaireById(Integer questionnaireId) {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionnaireById() - Starts");
    Session session = null;
    QuestionnaireBo questionnaireBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      questionnaireBo =
          (QuestionnaireBo)
              session
                  .createQuery("from QuestionnaireBo QBO where QBO.id=:questionnaireId")
                  .setInteger("questionnaireId", questionnaireId)
                  .uniqueResult();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - getQuestionnaireById() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }

    return questionnaireBo;
  }

  @Override
  public QuestionsBo getQuestionById(Integer questionId) {
    logger.info("StudyQuestionnaireDAOImpl - getQuestionById() - Starts");
    Session session = null;
    QuestionsBo questionBo = null;
    try {
      session = hibernateTemplate.getSessionFactory().openSession();
      transaction = session.beginTransaction();
      questionBo =
          (QuestionsBo)
              session
                  .createQuery("from QuestionsBo QBO where QBO.id=:questionId")
                  .setInteger("questionId", questionId)
                  .uniqueResult();
    } catch (Exception e) {
      transaction.rollback();
      logger.error("StudyQuestionnaireDAOImpl - getQuestionById() - ERROR ", e);
    } finally {
      if (session != null) {
        session.close();
      }
    }

    return questionBo;
  }
}
