/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ACTIVE_TASK_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NEW_QUESTIONNAIRE_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_QUESTIONNAIRE_DELETED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_QUESTIONNAIRE_MARKED_COMPLETED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_QUESTIONNAIRE_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_QUESTION_STEP_IN_FORM_DELETED;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.FORM_ID;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.QUESTION_ID;
import static com.fdahpstudydesigner.common.StudyBuilderConstants.STEP_ID;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.FormulaInfoBean;
import com.fdahpstudydesigner.bean.QuestionnaireStepBean;
import com.fdahpstudydesigner.bo.ActivetaskFormulaBo;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.HealthKitKeysInfo;
import com.fdahpstudydesigner.bo.InstructionsBo;
import com.fdahpstudydesigner.bo.QuestionResponseSubTypeBo;
import com.fdahpstudydesigner.bo.QuestionResponseTypeMasterInfoBo;
import com.fdahpstudydesigner.bo.QuestionnaireBo;
import com.fdahpstudydesigner.bo.QuestionnairesStepsBo;
import com.fdahpstudydesigner.bo.QuestionsBo;
import com.fdahpstudydesigner.bo.StatisticImageListBo;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.service.StudyActiveTasksService;
import com.fdahpstudydesigner.service.StudyQuestionnaireService;
import com.fdahpstudydesigner.service.StudyService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StudyQuestionnaireController {
  private static XLogger logger =
      XLoggerFactory.getXLogger(StudyQuestionnaireController.class.getName());

  @Autowired private StudyActiveTasksService studyActiveTasksService;

  @Autowired private StudyQuestionnaireService studyQuestionnaireService;

  @Autowired private StudyService studyService;

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  @RequestMapping("/adminStudies/copyQuestionnaire.do")
  public ModelAndView copyStudyQuestionnaire(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin saveOrUpdateFormQuestion");
    ModelAndView mav = new ModelAndView("instructionsStepPage");
    ModelMap map = new ModelMap();
    QuestionnaireBo copyQuestionnaireBo = null;
    String customStudyId = "";
    String studyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");

        if (StringUtils.isNotEmpty(questionnaireId) && StringUtils.isNotEmpty(customStudyId)) {
          copyQuestionnaireBo =
              studyQuestionnaireService.copyStudyQuestionnaireBo(
                  questionnaireId, customStudyId, sesObj);
        }
        if (copyQuestionnaireBo != null) {
          request.getSession().setAttribute(sessionStudyCount + "actionType", "edit");
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                  "Questionnaire copied successfully");
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + "questionnaireId",
                  String.valueOf(copyQuestionnaireBo.getId()));
          map.addAttribute("_S", sessionStudyCount);
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
          mav = new ModelAndView("redirect:/adminStudies/viewQuestionnaire.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Questionnaire not copied successfully");
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/viewStudyQuestionnaires.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveOrUpdateFormQuestion - Error", e);
    }
    logger.exit("saveOrUpdateFormQuestion - Ends");
    return mav;
  }

  @RequestMapping(value = "/adminStudies/deleteFormQuestion.do", method = RequestMethod.POST)
  public void deleteFormQuestionInfo(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin deleteFormQuestionInfo");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<>();
    ObjectMapper mapper = new ObjectMapper();
    JSONObject questionnaireJsonObject = null;
    String customStudyId = "";
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String formId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("formId"))
                ? ""
                : request.getParameter("formId");
        String questionId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionId"))
                ? ""
                : request.getParameter("questionId");
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        String questionnairesId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnairesId"))
                ? ""
                : request.getParameter("questionnairesId");
        String stepShortTitle =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("stepShortTitle"))
                ? ""
                : request.getParameter("stepShortTitle");
        auditRequest.setStudyId(customStudyId);
        if (!formId.isEmpty() && !questionId.isEmpty()) {
          message =
              studyQuestionnaireService.deleteFromStepQuestion(
                  formId, questionId, sesObj, customStudyId, auditRequest);
          if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {

            Map<String, String> values = new HashMap<>();
            QuestionnaireBo questionnaireDetails =
                studyQuestionnaireService.getQuestionnaireById(questionnairesId);
            if (questionnaireDetails != null) {
              values.put(QUESTION_ID, questionnaireDetails.getShortTitle());
            }
            values.put(FORM_ID, stepShortTitle);

            QuestionsBo questionBo = studyQuestionnaireService.getQuestionById(questionId);
            if (questionBo != null) {
              values.put(STEP_ID, questionBo.getShortTitle());
            }
            auditLogEventHelper.logEvent(STUDY_QUESTION_STEP_IN_FORM_DELETED, auditRequest, values);

            questionnairesStepsBo =
                studyQuestionnaireService.getQuestionnaireStep(
                    formId,
                    FdahpStudyDesignerConstants.FORM_STEP,
                    null,
                    customStudyId,
                    questionnairesId);
            if (questionnairesStepsBo != null) {
              questionnairesStepsBo.setType(FdahpStudyDesignerConstants.ACTION_TYPE_SAVE);
              studyQuestionnaireService.saveOrUpdateFromStepQuestionnaire(
                  questionnairesStepsBo, sesObj, customStudyId);
              qTreeMap = questionnairesStepsBo.getFormQuestionMap();
              questionnaireJsonObject = new JSONObject(mapper.writeValueAsString(qTreeMap));
              jsonobject.put("questionnaireJsonObject", questionnaireJsonObject);
              if (qTreeMap != null) {
                boolean isDone = true;
                if (!qTreeMap.isEmpty()) {
                  for (Entry<Integer, QuestionnaireStepBean> entryKey : qTreeMap.entrySet()) {
                    if (!entryKey.getValue().getStatus()) {
                      isDone = false;
                      break;
                    }
                  }
                }
                jsonobject.put("isDone", isDone);
              }
            }
            String studyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
            if (StringUtils.isNotEmpty(studyId)) {
              studyService.markAsCompleted(
                  studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
            }
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - deleteFormQuestionInfo - ERROR", e);
    }
    logger.exit("deleteFormQuestionInfo - Ends");
  }

  @RequestMapping(value = "/adminStudies/deleteQuestionnaire.do", method = RequestMethod.POST)
  public void deleteQuestionnaireInfo(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin deleteQuestionnaireInfo");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.SUCCESS;
    List<QuestionnaireBo> questionnaires = null;
    JSONArray questionnaireJsonArray = null;
    ObjectMapper mapper = new ObjectMapper();
    String customStudyId = "";
    String actMsg = "";
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        String studyId =
            FdahpStudyDesignerUtil.isEmpty(
                        request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                    == true
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        auditRequest.setStudyId(customStudyId);
        if (!studyId.isEmpty() && !questionnaireId.isEmpty()) {
          Map<String, String> values = new HashMap<>();
          QuestionnaireBo questionnaireDetails =
              studyQuestionnaireService.getQuestionnaireById(questionnaireId);
          if (questionnaireDetails != null) {
            values.put(QUESTION_ID, questionnaireDetails.getShortTitle());
          }

          message =
              studyQuestionnaireService.deletQuestionnaire(
                  studyId, questionnaireId, sesObj, customStudyId);
          if (message == FdahpStudyDesignerConstants.SUCCESS) {
            StudyBo studyBo = studyService.getStudyInfo(studyId);
            auditRequest.setStudyVersion(studyBo.getVersion().toString());
            auditRequest.setAppId(studyBo.getAppId());
            auditLogEventHelper.logEvent(STUDY_QUESTIONNAIRE_DELETED, auditRequest, values);
          }

          questionnaires =
              studyQuestionnaireService.getStudyQuestionnairesByStudyId(studyId, false);
          if ((questionnaires != null) && !questionnaires.isEmpty()) {
            questionnaireJsonArray = new JSONArray(mapper.writeValueAsString(questionnaires));
            jsonobject.put(FdahpStudyDesignerConstants.QUESTIONNAIRE_LIST, questionnaireJsonArray);
          }
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
          boolean markAsComplete = true;
          actMsg =
              studyService.validateActivityComplete(
                  studyId, FdahpStudyDesignerConstants.ACTIVITY_TYPE_QUESTIONNAIRE);
          if (!actMsg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            markAsComplete = false;
          }
          jsonobject.put("markAsComplete", markAsComplete);
          jsonobject.put(FdahpStudyDesignerConstants.ACTIVITY_MESSAGE, actMsg);
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - deleteQuestionnaireInfo - ERROR", e);
    }
    logger.exit("deleteQuestionnaireInfo - Ends");
  }

  @RequestMapping(value = "/adminStudies/deleteQuestionnaireStep.do", method = RequestMethod.POST)
  public void deleteQuestionnaireStepInfo(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin deleteQuestionnaireStepInfo");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    QuestionnaireBo questionnaireBo = null;
    Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<Integer, QuestionnaireStepBean>();
    ObjectMapper mapper = new ObjectMapper();
    JSONObject questionnaireJsonObject = null;
    String customStudyId = "";
    boolean isAnchorQuestionnaire = false;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        String stepId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("stepId"))
                ? ""
                : request.getParameter("stepId");
        String stepType =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("stepType"))
                ? ""
                : request.getParameter("stepType");
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        if (!stepId.isEmpty() && !questionnaireId.isEmpty() && !stepType.isEmpty()) {
          message =
              studyQuestionnaireService.deleteQuestionnaireStep(
                  stepId, questionnaireId, stepType, sesObj, customStudyId);
          if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            questionnaireBo =
                studyQuestionnaireService.getQuestionnaireById(questionnaireId, customStudyId);
            if (questionnaireBo != null) {
              questionnaireBo.setStatus(false);
              questionnaireBo.setType(FdahpStudyDesignerConstants.CONTENT);
              studyQuestionnaireService.saveOrUpdateQuestionnaire(
                  questionnaireBo, sesObj, customStudyId);
              qTreeMap =
                  studyQuestionnaireService.getQuestionnaireStepList(questionnaireBo.getId());
              questionnaireJsonObject = new JSONObject(mapper.writeValueAsString(qTreeMap));
              jsonobject.put("questionnaireJsonObject", questionnaireJsonObject);
              if (qTreeMap != null) {
                boolean isDone = true;
                for (Entry<Integer, QuestionnaireStepBean> entry : qTreeMap.entrySet()) {
                  QuestionnaireStepBean questionnaireStepBean = entry.getValue();
                  if ((questionnaireStepBean.getStatus() != null)
                      && !questionnaireStepBean.getStatus()) {
                    isDone = false;
                    break;
                  }
                  if (entry.getValue().getFromMap() != null) {
                    if (!entry.getValue().getFromMap().isEmpty()) {
                      for (Entry<Integer, QuestionnaireStepBean> entryKey :
                          entry.getValue().getFromMap().entrySet()) {
                        if (!entryKey.getValue().getStatus()) {
                          isDone = false;
                          break;
                        }
                      }
                    } else {
                      isDone = false;
                      break;
                    }
                  }
                }
                jsonobject.put("isDone", isDone);
              }
              isAnchorQuestionnaire =
                  studyQuestionnaireService.isAnchorDateExistByQuestionnaire(questionnaireId);
              jsonobject.put("isAnchorQuestionnaire", isAnchorQuestionnaire);
            }
            String studyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
            if (StringUtils.isNotEmpty(studyId)) {
              studyService.markAsCompleted(
                  studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
            }
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - deleteQuestionnaireStepInfo - ERROR", e);
    }
    logger.exit("deleteQuestionnaireStepInfo - Ends");
  }

  @RequestMapping("/adminStudies/formStep.do")
  public ModelAndView getFormStepPage(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin getFormStepPage");
    ModelAndView mav = new ModelAndView("formStepPage");
    String sucMsg = "";
    String errMsg = "";
    ModelMap map = new ModelMap();
    QuestionnaireBo questionnaireBo = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    StudyBo studyBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        request.getSession().removeAttribute(sessionStudyCount + "questionId");
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
        }
        request.getSession().removeAttribute(sessionStudyCount + "actionTypeForFormStep");
        String actionType =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionType"))
                ? ""
                : request.getParameter("actionType");
        if (StringUtils.isEmpty(actionType)) {
          actionType = (String) request.getSession().getAttribute(sessionStudyCount + "actionType");
        }

        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);

        String actionTypeForQuestionPage =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionTypeForQuestionPage"))
                ? ""
                : request.getParameter("actionTypeForQuestionPage");
        if (StringUtils.isEmpty(actionTypeForQuestionPage)) {
          actionTypeForQuestionPage =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + "actionTypeForQuestionPage");
          if ("edit".equals(actionTypeForQuestionPage)) {
            map.addAttribute("actionTypeForQuestionPage", "edit");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "edit");
          } else if ("view".equals(actionTypeForQuestionPage)) {
            map.addAttribute("actionTypeForQuestionPage", "view");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "view");
          } else {
            map.addAttribute("actionTypeForQuestionPage", "add");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "add");
          }
        } else {
          map.addAttribute("actionTypeForQuestionPage", actionTypeForQuestionPage);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + "actionTypeForQuestionPage", actionTypeForQuestionPage);
        }

        String formId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("formId"))
                ? ""
                : request.getParameter("formId");
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                          request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                      == true
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
          request
              .getSession()
              .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID, studyId);
        }
        if (StringUtils.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
        }
        if (StringUtils.isEmpty(formId)) {
          formId = (String) request.getSession().getAttribute(sessionStudyCount + "formId");
          request.getSession().setAttribute(sessionStudyCount + "formId", formId);
        }
        if (StringUtils.isEmpty(questionnaireId)) {
          questionnaireId =
              (String) request.getSession().getAttribute(sessionStudyCount + "questionnaireId");
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);
        }
        if (StringUtils.isNotEmpty(questionnaireId) && (null != studyBo)) {
          request.getSession().removeAttribute(sessionStudyCount + "actionType");
          questionnaireBo =
              studyQuestionnaireService.getQuestionnaireById(
                  questionnaireId, studyBo.getCustomStudyId());
          map.addAttribute("questionnaireBo", questionnaireBo);
          if ("edit".equals(actionType)) {
            map.addAttribute("actionType", "edit");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "edit");
          } else if ("view".equals(actionType)) {
            map.addAttribute("actionType", "view");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "view");
          } else {
            map.addAttribute("actionType", "add");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "add");
          }
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);
        }
        if ((formId != null) && !formId.isEmpty() && (null != studyBo)) {
          questionnairesStepsBo =
              studyQuestionnaireService.getQuestionnaireStep(
                  formId,
                  FdahpStudyDesignerConstants.FORM_STEP,
                  questionnaireBo.getShortTitle(),
                  studyBo.getCustomStudyId(),
                  questionnaireBo.getId());
          if (questionnairesStepsBo != null) {
            List<QuestionnairesStepsBo> destionationStepList =
                studyQuestionnaireService.getQuestionnairesStepsList(
                    questionnairesStepsBo.getQuestionnairesId(),
                    questionnairesStepsBo.getSequenceNo());
            map.addAttribute("destinationStepList", destionationStepList);
            if (!questionnairesStepsBo.getStatus() && StringUtils.isNotEmpty(studyId)) {
              studyService.markAsCompleted(
                  studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
            }
          }
          map.addAttribute("questionnairesStepsBo", questionnairesStepsBo);
          request.getSession().setAttribute(sessionStudyCount + "formId", formId);
        }

        map.addAttribute(
            FdahpStudyDesignerConstants.QUESTION_STEP,
            request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.QUESTION_STEP));
        map.addAttribute("questionnaireId", questionnaireId);
        request
            .getSession()
            .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.QUESTION_STEP);
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView("formStepPage", map);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - getFormStepPage - Error", e);
    }
    logger.exit("getFormStepPage - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/formQuestion.do")
  public ModelAndView getFormStepQuestionPage(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin getFormStepQuestionPage");
    ModelAndView mav = new ModelAndView("questionPage");
    String sucMsg = "";
    String errMsg = "";
    ModelMap map = new ModelMap();
    QuestionnaireBo questionnaireBo = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    StudyBo studyBo = null;
    QuestionsBo questionsBo = null;
    List<String> timeRangeList = new ArrayList<String>();
    List<StatisticImageListBo> statisticImageList = new ArrayList<>();
    List<ActivetaskFormulaBo> activetaskFormulaList = new ArrayList<>();
    List<QuestionResponseTypeMasterInfoBo> questionResponseTypeMasterInfoList = new ArrayList<>();
    List<HealthKitKeysInfo> healthKitKeysInfo = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
        }

        request.getSession().removeAttribute(sessionStudyCount + "actionTypeForFormStep");
        String actionTypeForQuestionPage =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionTypeForQuestionPage"))
                ? ""
                : request.getParameter("actionTypeForQuestionPage");
        if (StringUtils.isEmpty(actionTypeForQuestionPage)) {
          actionTypeForQuestionPage =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + "actionTypeForQuestionPage");
        }

        String actionTypeForFormStep =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionTypeForFormStep"))
                ? ""
                : request.getParameter("actionTypeForFormStep");
        if (StringUtils.isEmpty(actionTypeForFormStep)) {
          actionTypeForFormStep =
              (String)
                  request.getSession().getAttribute(sessionStudyCount + "actionTypeForFormStep");
          if ("edit".equals(actionTypeForFormStep)) {
            map.addAttribute("actionTypeForFormStep", "edit");
            request.getSession().setAttribute(sessionStudyCount + "actionTypeForFormStep", "edit");
          } else if ("view".equals(actionTypeForFormStep)) {
            map.addAttribute("actionTypeForFormStep", "view");
            request.getSession().setAttribute(sessionStudyCount + "actionTypeForFormStep", "view");
          } else {
            map.addAttribute("actionTypeForFormStep", "add");
            request.getSession().setAttribute(sessionStudyCount + "actionTypeForFormStep", "add");
          }
        } else {
          map.addAttribute("actionTypeForFormStep", actionTypeForFormStep);
        }

        String formId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("formId"))
                ? ""
                : request.getParameter("formId");
        String questionId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionId"))
                ? ""
                : request.getParameter("questionId");
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
          request.getSession().setAttribute(FdahpStudyDesignerConstants.STUDY_ID, studyId);
        }
        if (StringUtils.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          boolean isExists =
              studyQuestionnaireService.isAnchorDateExistsForStudy(
                  studyId, studyBo.getCustomStudyId());
          map.addAttribute("isAnchorDate", isExists);
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
        }
        if (StringUtils.isEmpty(formId)) {
          formId = (String) request.getSession().getAttribute(sessionStudyCount + "formId");
          request.getSession().setAttribute(sessionStudyCount + "formId", formId);
        }
        if (StringUtils.isEmpty(questionId)) {
          questionId = (String) request.getSession().getAttribute(sessionStudyCount + "questionId");
          request.getSession().setAttribute(sessionStudyCount + "questionId", questionId);
        }
        if (StringUtils.isEmpty(questionnaireId)) {
          questionnaireId =
              (String) request.getSession().getAttribute(sessionStudyCount + "questionnaireId");
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);
        }
        if (StringUtils.isNotEmpty(questionnaireId)) {
          request.getSession().removeAttribute(sessionStudyCount + "actionTypeForQuestionPage");
          questionnaireBo =
              studyQuestionnaireService.getQuestionnaireById(
                  questionnaireId, studyBo.getCustomStudyId());
          map.addAttribute("questionnaireBo", questionnaireBo);
          if ("edit".equals(actionTypeForQuestionPage)) {
            map.addAttribute("actionTypeForQuestionPage", "edit");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "edit");
          } else if ("view".equals(actionTypeForQuestionPage)) {
            map.addAttribute("actionTypeForQuestionPage", "view");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "view");
          } else {
            map.addAttribute("actionTypeForQuestionPage", "add");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "add");
          }
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);
          if ((questionnaireBo != null) && StringUtils.isNotEmpty(questionnaireBo.getFrequency())) {
            String frequency = questionnaireBo.getFrequency();
            if (questionnaireBo
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {
              if ((questionnaireBo.getQuestionnairesFrequenciesList() != null)
                  && (questionnaireBo.getQuestionnairesFrequenciesList().size() > 1)) {
                frequency = questionnaireBo.getFrequency();
              } else {
                frequency = FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME;
              }
            }
            timeRangeList = FdahpStudyDesignerUtil.getTimeRangeList(frequency);
          }
        }
        if ((formId != null) && !formId.isEmpty()) {
          questionnairesStepsBo =
              studyQuestionnaireService.getQuestionnaireStep(
                  formId,
                  FdahpStudyDesignerConstants.FORM_STEP,
                  questionnaireBo.getShortTitle(),
                  studyBo.getCustomStudyId(),
                  questionnaireBo.getId());
          if ((questionId != null) && !questionId.isEmpty()) {
            questionsBo =
                studyQuestionnaireService.getQuestionsById(
                    questionId, questionnaireBo.getShortTitle(), studyBo.getCustomStudyId());
            map.addAttribute("questionsBo", questionsBo);
            request.getSession().setAttribute(sessionStudyCount + "questionId", questionId);
            if (questionnairesStepsBo != null) {
              List<QuestionnairesStepsBo> destionationStepList =
                  studyQuestionnaireService.getQuestionnairesStepsList(
                      questionnairesStepsBo.getQuestionnairesId(),
                      questionnairesStepsBo.getSequenceNo());
              map.addAttribute("destinationStepList", destionationStepList);
            }
          }
          map.addAttribute("formId", formId);
          map.addAttribute("questionnairesStepsBo", questionnairesStepsBo);
          request.getSession().setAttribute(sessionStudyCount + "formId", formId);
        }
        statisticImageList = studyActiveTasksService.getStatisticImages();
        activetaskFormulaList = studyActiveTasksService.getActivetaskFormulas();
        questionResponseTypeMasterInfoList = studyQuestionnaireService.getQuestionReponseTypeList();
        if (studyBo != null) {
          if (studyBo.getPlatform().contains(FdahpStudyDesignerConstants.IOS)) {
            healthKitKeysInfo = studyQuestionnaireService.getHeanlthKitKeyInfoList();
            map.addAttribute("healthKitKeysInfo", healthKitKeysInfo);
          }
        }
        map.addAttribute("timeRangeList", timeRangeList);
        map.addAttribute("statisticImageList", statisticImageList);
        map.addAttribute("activetaskFormulaList", activetaskFormulaList);
        map.addAttribute("questionResponseTypeMasterInfoList", questionResponseTypeMasterInfoList);
        request
            .getSession()
            .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.QUESTION_STEP, "Yes");
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView("questionPage", map);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - getFormStepQuestionPage - Error", e);
    }
    logger.exit("getFormStepQuestionPage - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/instructionsStep.do")
  public ModelAndView getInstructionsPage(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin getInstructionsPage");
    ModelAndView mav = new ModelAndView("instructionsStepPage");
    String sucMsg = "";
    String errMsg = "";
    ModelMap map = new ModelMap();
    InstructionsBo instructionsBo = null;
    QuestionnaireBo questionnaireBo = null;
    StudyBo studyBo = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg = (String) request.getSession().getAttribute(FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
        }
        String instructionId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("instructionId")) == true
                ? ""
                : request.getParameter("instructionId");
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId")) == true
                ? ""
                : request.getParameter("questionnaireId");
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);

        request.getSession().removeAttribute(sessionStudyCount + "actionTypeForQuestionPage");
        String actionType =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionType"))
                ? ""
                : request.getParameter("actionType");
        if (StringUtils.isEmpty(actionType)) {
          actionType = (String) request.getSession().getAttribute(sessionStudyCount + "actionType");
        }

        String actionTypeForQuestionPage =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionTypeForQuestionPage"))
                ? ""
                : request.getParameter("actionTypeForQuestionPage");
        if (StringUtils.isEmpty(actionTypeForQuestionPage)) {
          actionTypeForQuestionPage =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + "actionTypeForQuestionPage");
          if ("edit".equals(actionTypeForQuestionPage)) {
            map.addAttribute("actionTypeForQuestionPage", "edit");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "edit");
          } else if ("view".equals(actionTypeForQuestionPage)) {
            map.addAttribute("actionTypeForQuestionPage", "view");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "view");
          } else {
            map.addAttribute("actionTypeForQuestionPage", "add");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "add");
          }
        } else {
          map.addAttribute("actionTypeForQuestionPage", actionTypeForQuestionPage);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + "actionTypeForQuestionPage", actionTypeForQuestionPage);
        }
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                          request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                      == true
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
          request
              .getSession()
              .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID, studyId);
        }
        if (StringUtils.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
        }
        if (StringUtils.isEmpty(instructionId)) {
          instructionId =
              (String) request.getSession().getAttribute(sessionStudyCount + "instructionId");
          request.getSession().setAttribute(sessionStudyCount + "instructionId", instructionId);
        }
        if (StringUtils.isEmpty(questionnaireId)) {
          questionnaireId =
              (String) request.getSession().getAttribute(sessionStudyCount + "questionnaireId");
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);
        }
        if (StringUtils.isNotEmpty(questionnaireId) && (null != studyBo)) {
          request.getSession().removeAttribute(sessionStudyCount + "actionType");
          questionnaireBo =
              studyQuestionnaireService.getQuestionnaireById(
                  questionnaireId, studyBo.getCustomStudyId());
          if ("edit".equals(actionType)) {
            map.addAttribute("actionType", "edit");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "edit");
          } else if ("view".equals(actionType)) {
            map.addAttribute("actionType", "view");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "view");
          } else {
            map.addAttribute("actionType", "add");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "add");
          }
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);
          map.addAttribute("questionnaireBo", questionnaireBo);
        }
        if ((instructionId != null) && !instructionId.isEmpty() && (null != studyBo)) {
          instructionsBo =
              studyQuestionnaireService.getInstructionsBo(
                  instructionId,
                  questionnaireBo.getShortTitle(),
                  studyBo.getCustomStudyId(),
                  questionnaireBo.getId());
          if ((instructionsBo != null) && (instructionsBo.getQuestionnairesStepsBo() != null)) {
            List<QuestionnairesStepsBo> questionnairesStepsList =
                studyQuestionnaireService.getQuestionnairesStepsList(
                    instructionsBo.getQuestionnairesStepsBo().getQuestionnairesId(),
                    instructionsBo.getQuestionnairesStepsBo().getSequenceNo());
            map.addAttribute("destinationStepList", questionnairesStepsList);
          }
          map.addAttribute("instructionsBo", instructionsBo);
          request.getSession().setAttribute(sessionStudyCount + "instructionId", instructionId);
        }
        map.addAttribute("questionnaireId", questionnaireId);
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView("instructionsStepPage", map);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - getInstructionsPage - Error", e);
    }
    logger.exit("getInstructionsPage - Ends");
    return mav;
  }

  @RequestMapping(value = "/adminStudies/viewQuestionnaire.do")
  public ModelAndView getQuestionnairePage(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin getQuestionnairePage");
    ModelAndView mav = new ModelAndView("questionnairePage");
    ModelMap map = new ModelMap();
    String sucMsg = "";
    String errMsg = "";
    StudyBo studyBo = null;
    QuestionnaireBo questionnaireBo = null;
    Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<Integer, QuestionnaireStepBean>();
    String customStudyId = "";
    List<AnchorDateTypeBo> anchorTypeList = new ArrayList<>();
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        request.getSession().removeAttribute(sessionStudyCount + "actionTypeForQuestionPage");
        request
            .getSession()
            .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.INSTRUCTION_ID);
        request
            .getSession()
            .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.FORM_ID);
        request
            .getSession()
            .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.QUESTION_ID);
        request
            .getSession()
            .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.QUESTION_STEP);
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg = (String) request.getSession().getAttribute(FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
        }
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String permission =
            (String) request.getSession().getAttribute(sessionStudyCount + "permission");

        String actionType =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionType"))
                ? ""
                : request.getParameter("actionType");
        if (StringUtils.isEmpty(actionType)) {
          actionType = (String) request.getSession().getAttribute(sessionStudyCount + "actionType");
        }
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
          request
              .getSession()
              .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID, studyId);
        }
        if (StringUtils.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          anchorTypeList =
              studyQuestionnaireService.getAnchorTypesByStudyId(studyBo.getCustomStudyId());
          map.addAttribute("anchorTypeList", anchorTypeList);
        }
        if (StringUtils.isEmpty(questionnaireId)) {
          questionnaireId =
              (String) request.getSession().getAttribute(sessionStudyCount + "questionnaireId");
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);
        }
        customStudyId =
            (String) request.getSession().getAttribute(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if ((null != questionnaireId) && !questionnaireId.isEmpty()) {
          questionnaireBo =
              studyQuestionnaireService.getQuestionnaireById(
                  questionnaireId, studyBo.getCustomStudyId());
          if (questionnaireBo != null) {
            map.addAttribute(
                "customCount", questionnaireBo.getQuestionnaireCustomScheduleBo().size());
            map.addAttribute("count", questionnaireBo.getQuestionnairesFrequenciesList().size());
            qTreeMap = studyQuestionnaireService.getQuestionnaireStepList(questionnaireBo.getId());
            if (qTreeMap != null) {
              boolean isDone = true;
              for (Entry<Integer, QuestionnaireStepBean> entry : qTreeMap.entrySet()) {
                QuestionnaireStepBean questionnaireStepBean = entry.getValue();
                if ((questionnaireStepBean.getStatus() != null)
                    && !questionnaireStepBean.getStatus()) {
                  isDone = false;
                  break;
                }
                if (entry.getValue().getFromMap() != null) {
                  if (!entry.getValue().getFromMap().isEmpty()) {
                    for (Entry<Integer, QuestionnaireStepBean> entryKey :
                        entry.getValue().getFromMap().entrySet()) {
                      if (!entryKey.getValue().getStatus()) {
                        isDone = false;
                        break;
                      }
                    }
                  } else {
                    isDone = false;
                    break;
                  }
                }
              }
              map.addAttribute("isDone", isDone);
              if (!isDone && StringUtils.isNotEmpty(studyId)) {
                studyService.markAsCompleted(
                    studyId,
                    FdahpStudyDesignerConstants.QUESTIONNAIRE,
                    false,
                    sesObj,
                    customStudyId);
              }
            }
          }
          if ("edit".equals(actionType)) {
            map.addAttribute("actionType", "edit");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "edit");
          } else {
            map.addAttribute("actionType", "view");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "view");
          }
          map.addAttribute("permission", permission);
          map.addAttribute("qTreeMap", qTreeMap);
          map.addAttribute("questionnaireBo", questionnaireBo);
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);

          boolean isAnchorQuestionnaire =
              studyQuestionnaireService.isAnchorDateExistByQuestionnaire(questionnaireId);
          map.addAttribute("isAnchorQuestionnaire", isAnchorQuestionnaire);
        }
        if ("add".equals(actionType)) {
          map.addAttribute("actionType", "add");
          request.getSession().setAttribute(sessionStudyCount + "actionType", "add");
        }
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView("questionnairePage", map);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - getQuestionnairePage - Error", e);
    }
    logger.exit("getQuestionnairePage - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/questionStep.do")
  public ModelAndView getQuestionStepPage(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin getQuestionStepPage");
    ModelAndView mav = new ModelAndView("questionStepPage");
    String sucMsg = "";
    String errMsg = "";
    ModelMap map = new ModelMap();
    QuestionnaireBo questionnaireBo = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    StudyBo studyBo = null;
    List<String> timeRangeList = new ArrayList<>();
    List<StatisticImageListBo> statisticImageList = new ArrayList<>();
    List<ActivetaskFormulaBo> activetaskFormulaList = new ArrayList<>();
    List<QuestionResponseTypeMasterInfoBo> questionResponseTypeMasterInfoList = new ArrayList<>();
    List<HealthKitKeysInfo> healthKitKeysInfo = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
        }
        String questionId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionId"))
                ? ""
                : request.getParameter("questionId");
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String permission =
            (String) request.getSession().getAttribute(sessionStudyCount + "permission");
        request.getSession().removeAttribute(sessionStudyCount + "actionTypeForQuestionPage");
        String actionType =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionType"))
                ? ""
                : request.getParameter("actionType");
        if (StringUtils.isEmpty(actionType)) {
          actionType = (String) request.getSession().getAttribute(sessionStudyCount + "actionType");
        }

        String actionTypeForQuestionPage =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionTypeForQuestionPage"))
                ? ""
                : request.getParameter("actionTypeForQuestionPage");
        if (StringUtils.isEmpty(actionTypeForQuestionPage)) {
          actionTypeForQuestionPage =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + "actionTypeForQuestionPage");
          if ("edit".equals(actionTypeForQuestionPage)) {
            map.addAttribute("actionTypeForQuestionPage", "edit");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "edit");
          } else if ("view".equals(actionTypeForQuestionPage)) {
            map.addAttribute("actionTypeForQuestionPage", "view");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "view");
          } else {
            map.addAttribute("actionTypeForQuestionPage", "add");
            request
                .getSession()
                .setAttribute(sessionStudyCount + "actionTypeForQuestionPage", "add");
          }
        } else {
          map.addAttribute("actionTypeForQuestionPage", actionTypeForQuestionPage);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + "actionTypeForQuestionPage", actionTypeForQuestionPage);
        }

        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
          request
              .getSession()
              .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID, studyId);
        }
        if (StringUtils.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          /*boolean isExists = studyQuestionnaireService
                .isAnchorDateExistsForStudy(
                        Integer.valueOf(studyId),
                        studyBo.getCustomStudyId());
          map.addAttribute("isAnchorDate", isExists);*/
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
        }
        if (StringUtils.isEmpty(questionId)) {
          questionId = (String) request.getSession().getAttribute(sessionStudyCount + "questionId");
          request.getSession().setAttribute(sessionStudyCount + "questionId", questionId);
        }
        if (StringUtils.isEmpty(questionnaireId)) {
          questionnaireId =
              (String) request.getSession().getAttribute(sessionStudyCount + "questionnaireId");
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);
        }
        if (StringUtils.isNotEmpty(questionnaireId)) {
          request.getSession().removeAttribute(sessionStudyCount + "actionType");
          questionnaireBo =
              studyQuestionnaireService.getQuestionnaireById(
                  questionnaireId, studyBo.getCustomStudyId());
          map.addAttribute("questionnaireBo", questionnaireBo);
          if ((questionnaireBo != null) && StringUtils.isNotEmpty(questionnaireBo.getFrequency())) {
            String frequency = questionnaireBo.getFrequency();
            if (questionnaireBo
                .getFrequency()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.FREQUENCY_TYPE_DAILY)) {
              if ((questionnaireBo.getQuestionnairesFrequenciesList() != null)
                  && (questionnaireBo.getQuestionnairesFrequenciesList().size() > 1)) {
                frequency = questionnaireBo.getFrequency();
              } else {
                frequency = FdahpStudyDesignerConstants.FREQUENCY_TYPE_ONE_TIME;
              }
            }
            timeRangeList = FdahpStudyDesignerUtil.getTimeRangeList(frequency);
          }
          if ("edit".equals(actionType)) {
            map.addAttribute("actionType", "edit");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "edit");
          } else if ("view".equals(actionType)) {
            map.addAttribute("actionType", "view");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "view");
          } else {
            map.addAttribute("actionType", "add");
            request.getSession().setAttribute(sessionStudyCount + "actionType", "add");
          }
          request.getSession().setAttribute(sessionStudyCount + "questionnaireId", questionnaireId);
        }
        if ((questionId != null) && !questionId.isEmpty()) {
          questionnairesStepsBo =
              studyQuestionnaireService.getQuestionnaireStep(
                  questionId,
                  FdahpStudyDesignerConstants.QUESTION_STEP,
                  questionnaireBo.getShortTitle(),
                  studyBo.getCustomStudyId(),
                  questionnaireBo.getId());
          if (questionnairesStepsBo != null) {
            List<QuestionnairesStepsBo> destionationStepList =
                studyQuestionnaireService.getQuestionnairesStepsList(
                    questionnairesStepsBo.getQuestionnairesId(),
                    questionnairesStepsBo.getSequenceNo());
            map.addAttribute("destinationStepList", destionationStepList);
          }
          map.addAttribute("questionnairesStepsBo", questionnairesStepsBo);
          request.getSession().setAttribute(sessionStudyCount + "questionId", questionId);
        }
        statisticImageList = studyActiveTasksService.getStatisticImages();
        activetaskFormulaList = studyActiveTasksService.getActivetaskFormulas();
        questionResponseTypeMasterInfoList = studyQuestionnaireService.getQuestionReponseTypeList();
        if (studyBo != null) {
          if (studyBo.getPlatform().contains(FdahpStudyDesignerConstants.IOS)) {
            healthKitKeysInfo = studyQuestionnaireService.getHeanlthKitKeyInfoList();
            map.addAttribute("healthKitKeysInfo", healthKitKeysInfo);
          }
        }
        map.addAttribute("permission", permission);
        map.addAttribute("timeRangeList", timeRangeList);
        map.addAttribute("statisticImageList", statisticImageList);
        map.addAttribute("activetaskFormulaList", activetaskFormulaList);
        map.addAttribute("questionnaireId", questionnaireId);
        map.addAttribute("questionResponseTypeMasterInfoList", questionResponseTypeMasterInfoList);
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView("questionStepPage", map);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - getQuestionStepPage - Error", e);
    }
    logger.exit("getQuestionStepPage - Ends");
    return mav;
  }

  @RequestMapping(value = "/adminStudies/reOrderFormQuestions.do", method = RequestMethod.POST)
  public void reOrderFromStepQuestionsInfo(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin reOrderQuestionnaireStepInfo");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      int oldOrderNumber;
      int newOrderNumber;
      if (sesObj != null) {
        String formId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("formId"))
                ? ""
                : request.getParameter("formId");
        String oldOrderNo =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("oldOrderNumber"))
                ? ""
                : request.getParameter("oldOrderNumber");
        String newOrderNo =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("newOrderNumber"))
                ? ""
                : request.getParameter("newOrderNumber");
        if (!formId.isEmpty() && !oldOrderNo.isEmpty() && !newOrderNo.isEmpty()) {
          oldOrderNumber = Integer.valueOf(oldOrderNo);
          newOrderNumber = Integer.valueOf(newOrderNo);
          message =
              studyQuestionnaireService.reOrderFormStepQuestions(
                  formId, oldOrderNumber, newOrderNumber);
          if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            String studyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
            String customStudyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(
                            sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
            if (StringUtils.isNotEmpty(studyId)) {
              studyService.markAsCompleted(
                  studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
            }
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - reOrderQuestionnaireStepInfo - ERROR", e);
    }
    logger.exit("reOrderQuestionnaireStepInfo - Ends");
  }

  @RequestMapping(
      value = "/adminStudies/reOrderQuestionnaireStepInfo.do",
      method = RequestMethod.POST)
  public void reOrderQuestionnaireStepInfo(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin reOrderQuestionnaireStepInfo");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<Integer, QuestionnaireStepBean>();
    ObjectMapper mapper = new ObjectMapper();
    JSONObject questionnaireJsonObject = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      int oldOrderNumber = 0;
      int newOrderNumber = 0;
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        String oldOrderNo =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("oldOrderNumber"))
                ? ""
                : request.getParameter("oldOrderNumber");
        String newOrderNo =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("newOrderNumber"))
                ? ""
                : request.getParameter("newOrderNumber");
        if (((questionnaireId != null) && !questionnaireId.isEmpty())
            && !oldOrderNo.isEmpty()
            && !newOrderNo.isEmpty()) {
          oldOrderNumber = Integer.valueOf(oldOrderNo);
          newOrderNumber = Integer.valueOf(newOrderNo);
          message =
              studyQuestionnaireService.reOrderQuestionnaireSteps(
                  questionnaireId, oldOrderNumber, newOrderNumber);
          if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            qTreeMap = studyQuestionnaireService.getQuestionnaireStepList(questionnaireId);
            if (qTreeMap != null) {
              boolean isDone = true;
              for (Entry<Integer, QuestionnaireStepBean> entry : qTreeMap.entrySet()) {
                QuestionnaireStepBean questionnaireStepBean = entry.getValue();
                if ((questionnaireStepBean.getStatus() != null)
                    && !questionnaireStepBean.getStatus()) {
                  isDone = false;
                  break;
                }
                if (entry.getValue().getFromMap() != null) {
                  if (!entry.getValue().getFromMap().isEmpty()) {
                    for (Entry<Integer, QuestionnaireStepBean> entryKey :
                        entry.getValue().getFromMap().entrySet()) {
                      if (!entryKey.getValue().getStatus()) {
                        isDone = false;
                        break;
                      }
                    }
                  } else {
                    isDone = false;
                    break;
                  }
                }
              }
              jsonobject.put("isDone", isDone);
              questionnaireJsonObject = new JSONObject(mapper.writeValueAsString(qTreeMap));
            }
            jsonobject.put("questionnaireJsonObject", questionnaireJsonObject);
            String studyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
            String customStudyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(
                            sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
            if (StringUtils.isNotEmpty(studyId)) {
              studyService.markAsCompleted(
                  studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
            }
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - reOrderQuestionnaireStepInfo - ERROR", e);
    }
    logger.exit("reOrderQuestionnaireStepInfo - Ends");
  }

  @RequestMapping(value = "/adminStudies/saveFromStep.do")
  public void saveFormStep(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin saveFormStep");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    ObjectMapper mapper = new ObjectMapper();
    QuestionnairesStepsBo addQuestionnairesStepsBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String questionnaireStepInfo = request.getParameter("questionnaireStepInfo");
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (null != questionnaireStepInfo) {
          questionnairesStepsBo =
              mapper.readValue(questionnaireStepInfo, QuestionnairesStepsBo.class);
          if (questionnairesStepsBo != null) {
            if (questionnairesStepsBo.getStepId() != null) {
              questionnairesStepsBo.setModifiedBy(sesObj.getUserId());
              questionnairesStepsBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            } else {
              questionnairesStepsBo.setCreatedBy(sesObj.getUserId());
              questionnairesStepsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            }
            addQuestionnairesStepsBo =
                studyQuestionnaireService.saveOrUpdateFromStepQuestionnaire(
                    questionnairesStepsBo, sesObj, customStudyId);
          }
        }
        if (addQuestionnairesStepsBo != null) {
          jsonobject.put("stepId", addQuestionnairesStepsBo.getStepId());
          jsonobject.put("formId", addQuestionnairesStepsBo.getInstructionFormId());
          message = FdahpStudyDesignerConstants.SUCCESS;
          String studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveFormStep - Error", e);
    }
    logger.exit("saveFormStep - Ends");
  }

  @RequestMapping(value = "/adminStudies/saveInstructionStep.do")
  public void saveInstructionStep(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin saveInstructionStep");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    InstructionsBo instructionsBo = null;
    ObjectMapper mapper = new ObjectMapper();
    InstructionsBo addInstructionsBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String instructionsInfo = request.getParameter("instructionsInfo");
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (null != instructionsInfo) {
          instructionsBo = mapper.readValue(instructionsInfo, InstructionsBo.class);
          if (instructionsBo != null) {
            if (instructionsBo.getId() != null) {
              instructionsBo.setModifiedBy(sesObj.getUserId());
              instructionsBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            } else {
              instructionsBo.setCreatedBy(sesObj.getUserId());
              instructionsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              instructionsBo.setActive(true);
            }
            if (instructionsBo.getQuestionnairesStepsBo() != null) {
              if (instructionsBo.getQuestionnairesStepsBo().getStepId() != null) {
                instructionsBo
                    .getQuestionnairesStepsBo()
                    .setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                instructionsBo.getQuestionnairesStepsBo().setModifiedBy(sesObj.getUserId());
              } else {
                instructionsBo
                    .getQuestionnairesStepsBo()
                    .setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
                instructionsBo.getQuestionnairesStepsBo().setCreatedBy(sesObj.getUserId());
              }
            }
            addInstructionsBo =
                studyQuestionnaireService.saveOrUpdateInstructionsBo(
                    instructionsBo, sesObj, customStudyId);
          }
        }
        if (addInstructionsBo != null) {
          jsonobject.put("instructionId", addInstructionsBo.getId());
          jsonobject.put("stepId", addInstructionsBo.getQuestionnairesStepsBo().getStepId());
          message = FdahpStudyDesignerConstants.SUCCESS;
          String studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveInstructionStep - Error", e);
    }
    logger.exit("saveInstructionStep - Ends");
  }

  @RequestMapping("/adminStudies/saveOrUpdateFromQuestion.do")
  public ModelAndView saveOrUpdateFormQuestion(
      HttpServletRequest request, HttpServletResponse response, QuestionsBo questionsBo) {
    logger.entry("begin saveOrUpdateFormQuestion");
    ModelAndView mav = new ModelAndView("instructionsStepPage");
    ModelMap map = new ModelMap();
    QuestionsBo addQuestionsBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (questionsBo != null) {
          if (questionsBo.getId() != null) {
            questionsBo.setModifiedBy(sesObj.getUserId());
            questionsBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          } else {
            questionsBo.setCreatedBy(sesObj.getUserId());
            questionsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          }
          addQuestionsBo =
              studyQuestionnaireService.saveOrUpdateQuestion(questionsBo, sesObj, customStudyId);
        }
        if (addQuestionsBo != null) {
          if (addQuestionsBo.getId() != null) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    "Form question updated successfully");
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    "Form question added successfully");
          }
          String studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/formStep.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Form not added successfully");
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/formQuestion.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveOrUpdateFormQuestion - Error", e);
    }
    logger.exit("saveOrUpdateFormQuestion - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateFromStepQuestionnaire.do")
  public ModelAndView saveOrUpdateFormStepQuestionnaire(
      HttpServletRequest request,
      HttpServletResponse response,
      QuestionnairesStepsBo questionnairesStepsBo) {
    logger.entry("begin saveOrUpdateFormStepQuestionnaire");
    ModelAndView mav = new ModelAndView("instructionsStepPage");
    ModelMap map = new ModelMap();
    QuestionnairesStepsBo addQuestionnairesStepsBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (questionnairesStepsBo != null) {
          if (questionnairesStepsBo.getStepId() != null) {
            questionnairesStepsBo.setModifiedBy(sesObj.getUserId());
            questionnairesStepsBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          } else {
            questionnairesStepsBo.setCreatedBy(sesObj.getUserId());
            questionnairesStepsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          }
          addQuestionnairesStepsBo =
              studyQuestionnaireService.saveOrUpdateFromStepQuestionnaire(
                  questionnairesStepsBo, sesObj, customStudyId);
        }
        if (addQuestionnairesStepsBo != null) {
          String studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
          if (questionnairesStepsBo.getStepId() != null) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    "Form step updated successfully");
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    "Form step added successfully");
          }
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/viewQuestionnaire.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Form not added successfully");
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/formStep.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveOrUpdateFormStepQuestionnaire - Error", e);
    }
    logger.exit("saveOrUpdateFormStepQuestionnaire - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateInstructionStep.do")
  public ModelAndView saveOrUpdateInstructionStep(
      HttpServletRequest request, HttpServletResponse response, InstructionsBo instructionsBo) {
    logger.entry("begin saveOrUpdateInstructionStep");
    ModelAndView mav = new ModelAndView("instructionsStepPage");
    ModelMap map = new ModelMap();
    InstructionsBo addInstructionsBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (instructionsBo != null) {
          if (instructionsBo.getId() != null) {
            instructionsBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            instructionsBo.setModifiedBy(sesObj.getUserId());
          } else {
            instructionsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            instructionsBo.setCreatedBy(sesObj.getUserId());
          }
          if (instructionsBo.getQuestionnairesStepsBo() != null) {
            if (instructionsBo.getQuestionnairesStepsBo().getStepId() != null) {
              instructionsBo
                  .getQuestionnairesStepsBo()
                  .setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              instructionsBo.getQuestionnairesStepsBo().setModifiedBy(sesObj.getUserId());
            } else {
              instructionsBo
                  .getQuestionnairesStepsBo()
                  .setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              instructionsBo.getQuestionnairesStepsBo().setCreatedBy(sesObj.getUserId());
            }
          }
          addInstructionsBo =
              studyQuestionnaireService.saveOrUpdateInstructionsBo(
                  instructionsBo, sesObj, customStudyId);
        }
        if (addInstructionsBo != null) {
          String studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
          if (StringUtils.isNotEmpty(instructionsBo.getId())) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    FdahpStudyDesignerConstants.INSTRUCTION_UPDATED_SUCCESSFULLY);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    FdahpStudyDesignerConstants.INSTRUCTION_ADDED_SUCCESSFULLY);
          }
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/viewQuestionnaire.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  FdahpStudyDesignerConstants.ERR_MSG,
                  FdahpStudyDesignerConstants.INSTRUCTION_UPDATED_SUCCESSFULLY);
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/instructionsStep.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveOrUpdateInstructionStep - Error", e);
    }
    logger.exit("saveOrUpdateInstructionStep - Ends");
    return mav;
  }

  @RequestMapping(
      value = "/adminStudies/saveorUpdateQuestionnaireSchedule.do",
      method = RequestMethod.POST)
  public ModelAndView saveorUpdateQuestionnaireSchedule(
      HttpServletRequest request, HttpServletResponse response, QuestionnaireBo questionnaireBo) {
    logger.entry("begin saveorUpdateQuestionnaireSchedule");
    ModelAndView mav = new ModelAndView("questionnairePage");
    ModelMap map = new ModelMap();
    QuestionnaireBo addQuestionnaireBo = null;
    String customStudyId = "";
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        auditRequest.setStudyId(customStudyId);
        if (questionnaireBo != null) {
          if (questionnaireBo.getId() != null) {
            questionnaireBo.setModifiedBy(sesObj.getUserId());
            questionnaireBo.setModifiedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
            questionnaireBo.setStatus(true);
            questionnaireBo.setIsChange(1);
          } else {
            questionnaireBo.setCreatedBy(sesObj.getUserId());
            questionnaireBo.setCreatedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
            questionnaireBo.setStatus(true);
            questionnaireBo.setIsChange(1);
          }
          addQuestionnaireBo =
              studyQuestionnaireService.saveOrUpdateQuestionnaire(
                  questionnaireBo, sesObj, customStudyId);
          if (addQuestionnaireBo != null) {
            if (questionnaireBo.getId() != null) {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      "Questionnaire updated successfully");
            } else {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      "Questionnaire added successfully");
            }
            String studyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
            if (StringUtils.isNotEmpty(studyId)) {
              String message =
                  studyService.markAsCompleted(
                      studyId,
                      FdahpStudyDesignerConstants.QUESTIONNAIRE,
                      false,
                      sesObj,
                      customStudyId);
              if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
                StudyBo studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
                auditRequest.setStudyVersion(studyBo.getVersion().toString());
                auditRequest.setAppId(studyBo.getAppId());
                auditLogEventHelper.logEvent(
                    STUDY_ACTIVE_TASK_SECTION_MARKED_COMPLETE, auditRequest);
              }
            }
            map.addAttribute("_S", sessionStudyCount);
            mav = new ModelAndView("redirect:/adminStudies/viewStudyQuestionnaires.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    "Questionnaire not added successfully");
            map.addAttribute("_S", sessionStudyCount);
            mav = new ModelAndView("redirect:/adminStudies/viewQuestionnaire.do", map);
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveorUpdateQuestionnaireSchedule - Error", e);
    }
    logger.exit("saveorUpdateQuestionnaireSchedule - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateQuestionStepQuestionnaire.do")
  public ModelAndView saveOrUpdateQuestionStepQuestionnaire(
      HttpServletRequest request,
      HttpServletResponse response,
      QuestionnairesStepsBo questionnairesStepsBo) {
    logger.entry("begin saveOrUpdateFormStepQuestionnaire");
    ModelAndView mav = new ModelAndView("instructionsStepPage");
    ModelMap map = new ModelMap();
    QuestionnairesStepsBo addQuestionnairesStepsBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (questionnairesStepsBo != null) {
          if (questionnairesStepsBo.getStepId() != null) {
            questionnairesStepsBo.setModifiedBy(sesObj.getUserId());
            questionnairesStepsBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          } else {
            questionnairesStepsBo.setCreatedBy(sesObj.getUserId());
            questionnairesStepsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          }
          addQuestionnairesStepsBo =
              studyQuestionnaireService.saveOrUpdateQuestionStep(
                  questionnairesStepsBo, sesObj, customStudyId);
        }
        if (addQuestionnairesStepsBo != null) {
          String studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
          if (StringUtils.isNotEmpty(questionnairesStepsBo.getStepId())) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    "Question step updated successfully");
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    "Question step added successfully");
          }
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/viewQuestionnaire.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Form not added successfully");
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/questionStep.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveOrUpdateFormStepQuestionnaire - Error", e);
    }
    logger.exit("saveOrUpdateFormStepQuestionnaire - Ends");
    return mav;
  }

  @RequestMapping(value = "/adminStudies/saveQuestion.do")
  public void saveQuestion(
      HttpServletRequest request,
      HttpServletResponse response,
      MultipartHttpServletRequest multipleRequest) {
    logger.entry("begin saveQuestion");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    QuestionsBo questionsBo = null;
    ObjectMapper mapper = new ObjectMapper();
    QuestionsBo addQuestionsBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String questionnaireStepInfo = request.getParameter("questionInfo");
        Iterator<String> itr = multipleRequest.getFileNames();
        HashMap<String, MultipartFile> fileMap = new HashMap<>();
        while (itr.hasNext()) {
          CommonsMultipartFile mpf = (CommonsMultipartFile) multipleRequest.getFile(itr.next());
          fileMap.put(mpf.getFileItem().getFieldName(), mpf);
        }
        if (null != questionnaireStepInfo) {
          questionsBo = mapper.readValue(questionnaireStepInfo, QuestionsBo.class);

          if (questionsBo != null) {
            if (StringUtils.isNotEmpty(questionsBo.getId())) {
              questionsBo.setModifiedBy(sesObj.getUserId());
              questionsBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            } else {
              questionsBo.setCreatedBy(sesObj.getUserId());
              questionsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            }
            if ((questionsBo.getResponseType() != null) && (questionsBo.getResponseType() == 5)) {
              if ((questionsBo.getQuestionResponseSubTypeList() != null)
                  && !questionsBo.getQuestionResponseSubTypeList().isEmpty()) {
                for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                    questionsBo.getQuestionResponseSubTypeList()) {
                  String key1 = "imageFile[" + questionResponseSubTypeBo.getImageId() + "]";
                  String key2 = "selectImageFile[" + questionResponseSubTypeBo.getImageId() + "]";
                  if ((fileMap != null) && (fileMap.get(key1) != null)) {
                    questionResponseSubTypeBo.setImageFile(fileMap.get(key1));
                  }
                  if ((fileMap != null) && (fileMap.get(key2) != null)) {
                    questionResponseSubTypeBo.setSelectImageFile(fileMap.get(key2));
                  }
                }
              }
            }
            if ((questionsBo.getResponseType() != null)
                && (questionsBo.getQuestionReponseTypeBo() != null)) {
              if ((fileMap != null) && (fileMap.get("minImageFile") != null)) {
                questionsBo.getQuestionReponseTypeBo().setMinImageFile(fileMap.get("minImageFile"));
              }
              if ((fileMap != null) && (fileMap.get("maxImageFile") != null)) {
                questionsBo.getQuestionReponseTypeBo().setMaxImageFile(fileMap.get("maxImageFile"));
              }
            }
            addQuestionsBo =
                studyQuestionnaireService.saveOrUpdateQuestion(questionsBo, sesObj, customStudyId);
          }
        }
        if (addQuestionsBo != null) {
          jsonobject.put("questionId", addQuestionsBo.getId());
          if (addQuestionsBo.getQuestionReponseTypeBo() != null) {
            jsonobject.put(
                "questionResponseId",
                addQuestionsBo.getQuestionReponseTypeBo().getResponseTypeId());
            jsonobject.put(
                "questionsResponseTypeId",
                addQuestionsBo.getQuestionReponseTypeBo().getQuestionsResponseTypeId());
          }
          message = FdahpStudyDesignerConstants.SUCCESS;
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveQuestion - Error", e);
    }
    logger.exit("saveQuestion - Ends");
  }

  @RequestMapping(value = "/adminStudies/saveQuestionnaireSchedule.do", method = RequestMethod.POST)
  public void saveQuestionnaireSchedule(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin saveQuestionnaireSchedule");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    QuestionnaireBo updateQuestionnaireBo = null;
    ObjectMapper mapper = new ObjectMapper();
    QuestionnaireBo questionnaireBo = null;
    String customStudyId = "";
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String questionnaireScheduleInfo = request.getParameter("questionnaireScheduleInfo");
        if ((questionnaireScheduleInfo != null) && !questionnaireScheduleInfo.isEmpty()) {
          questionnaireBo = mapper.readValue(questionnaireScheduleInfo, QuestionnaireBo.class);
          if (questionnaireBo != null) {
            String studyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
            if (StringUtils.isNotEmpty(questionnaireBo.getId())) {
              questionnaireBo.setModifiedBy(sesObj.getUserId());
              questionnaireBo.setModifiedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
              if (questionnaireBo.getStatus()) {
                request
                    .getSession()
                    .setAttribute(
                        sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                        "Questionnaire updated successfully");
              }
            } else {
              questionnaireBo.setCreatedBy(sesObj.getUserId());
              questionnaireBo.setCreatedDate(FdahpStudyDesignerUtil.getCurrentDateTime());
              if (questionnaireBo.getStatus()) {
                request
                    .getSession()
                    .setAttribute(
                        sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                        "Questionnaire added successfully");
              }
            }
            customStudyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(
                            sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
            StudyBo studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
            auditRequest.setStudyId(customStudyId);
            auditRequest.setStudyVersion(studyBo.getVersion().toString());
            auditRequest.setAppId(studyBo.getAppId());
            updateQuestionnaireBo =
                studyQuestionnaireService.saveOrUpdateQuestionnaire(
                    questionnaireBo, sesObj, customStudyId);
            if (updateQuestionnaireBo != null) {
              jsonobject.put("questionnaireId", updateQuestionnaireBo.getId());
              if (updateQuestionnaireBo.getQuestionnairesFrequenciesBo() != null) {
                jsonobject.put(
                    "questionnaireFrequenceId",
                    updateQuestionnaireBo.getQuestionnairesFrequenciesBo().getId());
              }
              if (StringUtils.isNotEmpty(studyId)) {
                studyService.markAsCompleted(
                    studyId,
                    FdahpStudyDesignerConstants.QUESTIONNAIRE,
                    false,
                    sesObj,
                    customStudyId);
              }
              Map<String, String> values = new HashMap<>();
              values.put("questionnaire_id", updateQuestionnaireBo.getId().toString());
              StudyBuilderAuditEvent event =
                  questionnaireBo.getId() != null
                      ? STUDY_QUESTIONNAIRE_SAVED_OR_UPDATED
                      : STUDY_NEW_QUESTIONNAIRE_CREATED;
              auditLogEventHelper.logEvent(event, auditRequest, values);

              if (questionnaireBo.getStatus()) {
                auditLogEventHelper.logEvent(
                    STUDY_QUESTIONNAIRE_MARKED_COMPLETED, auditRequest, values);
              }
              message = FdahpStudyDesignerConstants.SUCCESS;
            }
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveQuestionnaireSchedule - Error", e);
    }
    logger.exit("saveQuestionnaireSchedule - Ends");
  }

  @RequestMapping(value = "/adminStudies/saveQuestionStep.do", method = RequestMethod.POST)
  public void saveQuestionStep(
      HttpServletResponse response,
      MultipartHttpServletRequest multipleRequest,
      HttpServletRequest request) {
    logger.entry("begin saveQuestionStep");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    QuestionnairesStepsBo questionnairesStepsBo = null;
    ObjectMapper mapper = new ObjectMapper();
    QuestionnairesStepsBo addQuestionnairesStepsBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              multipleRequest.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String questionnaireStepInfo = multipleRequest.getParameter("questionnaireStepInfo");
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        Iterator<String> itr = multipleRequest.getFileNames();
        HashMap<String, MultipartFile> fileMap = new HashMap<>();
        while (itr.hasNext()) {
          CommonsMultipartFile mpf = (CommonsMultipartFile) multipleRequest.getFile(itr.next());
          fileMap.put(mpf.getFileItem().getFieldName(), mpf);
        }
        if (null != questionnaireStepInfo) {
          questionnairesStepsBo =
              mapper.readValue(questionnaireStepInfo, QuestionnairesStepsBo.class);
          if (questionnairesStepsBo != null) {
            if (questionnairesStepsBo.getStepId() != null) {
              questionnairesStepsBo.setModifiedBy(sesObj.getUserId());
              questionnairesStepsBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            } else {
              questionnairesStepsBo.setCreatedBy(sesObj.getUserId());
              questionnairesStepsBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            }
          }
          if ((questionnairesStepsBo.getQuestionsBo() != null)
              && (questionnairesStepsBo.getQuestionsBo().getResponseType() != null)) {
            if (questionnairesStepsBo.getQuestionsBo().getResponseType() == 5) {
              if ((questionnairesStepsBo.getQuestionResponseSubTypeList() != null)
                  && !questionnairesStepsBo.getQuestionResponseSubTypeList().isEmpty()) {
                for (QuestionResponseSubTypeBo questionResponseSubTypeBo :
                    questionnairesStepsBo.getQuestionResponseSubTypeList()) {
                  String key1 = "imageFile[" + questionResponseSubTypeBo.getImageId() + "]";
                  String key2 = "selectImageFile[" + questionResponseSubTypeBo.getImageId() + "]";
                  if ((fileMap != null) && (fileMap.get(key1) != null)) {
                    questionResponseSubTypeBo.setImageFile(fileMap.get(key1));
                  }
                  if ((fileMap != null) && (fileMap.get(key2) != null)) {
                    questionResponseSubTypeBo.setSelectImageFile(fileMap.get(key2));
                  }
                }
              }
            }
            if (questionnairesStepsBo.getQuestionReponseTypeBo() != null) {
              if ((fileMap != null) && (fileMap.get("minImageFile") != null)) {
                questionnairesStepsBo
                    .getQuestionReponseTypeBo()
                    .setMinImageFile(fileMap.get("minImageFile"));
              }
              if ((fileMap != null) && (fileMap.get("maxImageFile") != null)) {
                questionnairesStepsBo
                    .getQuestionReponseTypeBo()
                    .setMaxImageFile(fileMap.get("maxImageFile"));
              }
            }
          }
          addQuestionnairesStepsBo =
              studyQuestionnaireService.saveOrUpdateQuestionStep(
                  questionnairesStepsBo, sesObj, customStudyId);
        }
        if (addQuestionnairesStepsBo != null) {
          jsonobject.put("stepId", addQuestionnairesStepsBo.getStepId());
          String studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
          if (addQuestionnairesStepsBo.getQuestionsBo() != null) {
            jsonobject.put("questionId", addQuestionnairesStepsBo.getQuestionsBo().getId());
          }
          if (addQuestionnairesStepsBo.getQuestionReponseTypeBo() != null) {
            jsonobject.put(
                "questionResponseId",
                addQuestionnairesStepsBo.getQuestionReponseTypeBo().getResponseTypeId());
            jsonobject.put(
                "questionsResponseTypeId",
                addQuestionnairesStepsBo.getQuestionReponseTypeBo().getQuestionsResponseTypeId());
          }
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - saveQuestionStep - Error", e);
    }
    logger.exit("saveQuestionStep - Ends");
  }

  @RequestMapping(
      value = "/adminStudies/validateconditionalFormula.do",
      method = RequestMethod.POST)
  public void validateconditionalFormula(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin validateconditionalFormula");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    ObjectMapper mapper = new ObjectMapper();
    JSONObject formulaResponseJsonObject = null;
    FormulaInfoBean formulaInfoBean = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (sesObj != null) {
        String left_input =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("left_input"))
                ? ""
                : request.getParameter("left_input");
        String right_input =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("right_input"))
                ? ""
                : request.getParameter("right_input");
        String oprator_input =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("oprator_input"))
                ? ""
                : request.getParameter("oprator_input");
        String trialInputVal =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("trialInput"))
                ? ""
                : request.getParameter("trialInput");
        if (!left_input.isEmpty()
            && !right_input.isEmpty()
            && !oprator_input.isEmpty()
            && !trialInputVal.isEmpty()) {
          formulaInfoBean =
              studyQuestionnaireService.validateQuestionConditionalBranchingLogic(
                  left_input, right_input, oprator_input, trialInputVal);
          if (formulaInfoBean != null) {
            formulaResponseJsonObject = new JSONObject(mapper.writeValueAsString(formulaInfoBean));
            jsonobject.put("formulaResponseJsonObject", formulaResponseJsonObject);
            if (formulaInfoBean
                .getMessage()
                .equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
              message = FdahpStudyDesignerConstants.SUCCESS;
            }
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - validateconditionalFormula - ERROR", e);
    }
    logger.exit("validateconditionalFormula - Ends");
  }

  @RequestMapping(value = "/adminStudies/validateLineChartSchedule.do", method = RequestMethod.POST)
  public void validateQuestionnaireLineChartSchedule(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin validateQuestionnaireLineChartSchedule");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    ObjectMapper mapper = new ObjectMapper();
    JSONObject questionnaireJsonObject = null;
    Map<Integer, QuestionnaireStepBean> qTreeMap = new TreeMap<Integer, QuestionnaireStepBean>();
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (sesObj != null) {
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        String frequency =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("frequency"))
                ? ""
                : request.getParameter("frequency");
        if (!questionnaireId.isEmpty() && !frequency.isEmpty()) {
          message = studyQuestionnaireService.validateLineChartSchedule(questionnaireId, frequency);
          if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            qTreeMap = studyQuestionnaireService.getQuestionnaireStepList(questionnaireId);
            questionnaireJsonObject = new JSONObject(mapper.writeValueAsString(qTreeMap));
            jsonobject.put("questionnaireJsonObject", questionnaireJsonObject);
          }
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error(
          "StudyQuestionnaireController - validateQuestionnaireLineChartSchedule - ERROR", e);
    }
    logger.exit("validateQuestionnaireLineChartSchedule - Ends");
  }

  @RequestMapping(value = "/adminStudies/validateQuestionnaireKey.do", method = RequestMethod.POST)
  public void validateQuestionnaireShortTitle(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin validateQuestionnaireShortTitle");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String customStudyId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        if (StringUtils.isEmpty(customStudyId)) {
          customStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        }
        String shortTitle =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("shortTitle"))
                ? ""
                : request.getParameter("shortTitle");
        if (((studyId != null) && !studyId.isEmpty()) && !shortTitle.isEmpty()) {
          message =
              studyQuestionnaireService.checkQuestionnaireShortTitle(
                  studyId, shortTitle, customStudyId);
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - validateQuestionnaireShortTitle - ERROR", e);
    }
    logger.exit("validateQuestionnaireShortTitle - Ends");
  }

  @RequestMapping(
      value = "/adminStudies/validateQuestionnaireStepKey.do",
      method = RequestMethod.POST)
  public void validateQuestionnaireStepShortTitle(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin validateQuestionnaireStepShortTitle");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        String stepType =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("stepType"))
                ? ""
                : request.getParameter("stepType");
        String shortTitle =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("shortTitle"))
                ? ""
                : request.getParameter("shortTitle");
        String questionnaireShortTitle =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireShortTitle"))
                ? ""
                : request.getParameter("questionnaireShortTitle");
        String customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (StringUtils.isEmpty(customStudyId)) {
          customStudyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        }
        if (!questionnaireId.isEmpty() && !stepType.isEmpty() && !shortTitle.isEmpty()) {
          message =
              studyQuestionnaireService.checkQuestionnaireStepShortTitle(
                  questionnaireId, stepType, shortTitle, questionnaireShortTitle, customStudyId);
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - validateQuestionnaireStepShortTitle - ERROR", e);
    }
    logger.exit("validateQuestionnaireStepShortTitle - Ends");
  }

  @RequestMapping(value = "/adminStudies/validateQuestionKey.do", method = RequestMethod.POST)
  public void validateQuestionShortTitle(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin validateQuestionShortTitle");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String questionnaireId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireId"))
                ? ""
                : request.getParameter("questionnaireId");
        String shortTitle =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("shortTitle"))
                ? ""
                : request.getParameter("shortTitle");
        String questionnaireShortTitle =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("questionnaireShortTitle"))
                ? ""
                : request.getParameter("questionnaireShortTitle");
        String customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (StringUtils.isEmpty(customStudyId)) {
          customStudyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        }
        if (!questionnaireId.isEmpty() && !shortTitle.isEmpty()) {
          message =
              studyQuestionnaireService.checkFromQuestionShortTitle(
                  questionnaireId, shortTitle, questionnaireShortTitle, customStudyId);
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - validateQuestionShortTitle - ERROR", e);
    }
    logger.exit("validateQuestionShortTitle - Ends");
  }

  @RequestMapping(value = "/adminStudies/validateStatsShortName.do", method = RequestMethod.POST)
  public void validateQuestionStatsShortTitle(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin validateQuestionStatsShortTitle()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        String shortTitle =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("shortTitle"))
                ? ""
                : request.getParameter("shortTitle");
        if (!studyId.isEmpty() && !shortTitle.isEmpty()) {
          message =
              studyQuestionnaireService.checkStatShortTitle(studyId, shortTitle, customStudyId);
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - validateQuestionStatsShortTitle - ERROR", e);
    }
    logger.exit("validateQuestionStatsShortTitle() - Ends");
  }

  @RequestMapping(
      value = "/adminStudies/validateRepeatableQuestion.do",
      method = RequestMethod.POST)
  public void validateRepeatableQuestion(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin validateRepeatableQuestion()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (sesObj != null) {
        String formId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("formId"))
                ? ""
                : request.getParameter("formId");
        if (!formId.isEmpty()) {
          message = studyQuestionnaireService.validateRepetableFormQuestionStats(formId);
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - validateRepeatableQuestion - ERROR", e);
    }
    logger.exit("validateRepeatableQuestion() - Ends");
  }

  @RequestMapping("/adminStudies/viewStudyQuestionnaires.do")
  public ModelAndView viewStudyQuestionnaires(HttpServletRequest request) {
    logger.entry("begin viewStudyQuestionnaires()");
    ModelAndView mav = new ModelAndView("redirect:viewBasicInfo.do");
    ModelMap map = new ModelMap();
    StudyBo studyBo = null;
    String sucMsg = "";
    String errMsg = "";
    List<QuestionnaireBo> questionnaires = null;
    String activityStudyId = "";
    String customStudyId = "";
    String actMsg = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        request.getSession().removeAttribute(sessionStudyCount + "questionnaireId");
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null
            != request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG);
        }

        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String permission =
            (String) request.getSession().getAttribute(sessionStudyCount + "permission");
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                          request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                      == true
                  ? "0"
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        // Added for live version Start
        String isLive =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.IS_LIVE);
        if (StringUtils.isNotEmpty(isLive)
            && isLive.equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
          activityStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.QUESTIONNARIE_STUDY_ID);
          customStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        }
        // Added for live version End
        if (StringUtils.isNotEmpty(studyId)) {
          request.getSession().removeAttribute(sessionStudyCount + "actionType");
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          if (StringUtils.isNotEmpty(activityStudyId)) {
            questionnaires =
                studyQuestionnaireService.getStudyQuestionnairesByStudyId(customStudyId, true);
          } else {
            questionnaires =
                studyQuestionnaireService.getStudyQuestionnairesByStudyId(studyId, false);
          }
          boolean markAsComplete = true;
          actMsg =
              studyService.validateActivityComplete(
                  studyId, FdahpStudyDesignerConstants.ACTIVITY_TYPE_QUESTIONNAIRE);
          if (!actMsg.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            markAsComplete = false;
          }
          map.addAttribute("markAsComplete", markAsComplete);
          if (!markAsComplete) {
            customStudyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(
                            sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, false, sesObj, customStudyId);
          }
        }
        map.addAttribute("permission", permission);
        map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
        map.addAttribute("questionnaires", questionnaires);
        map.addAttribute(FdahpStudyDesignerConstants.ACTIVITY_MESSAGE, actMsg);
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView("studyQuestionaryListPage", map);
      }
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - viewStudyQuestionnaires - ERROR", e);
    }
    logger.exit("viewStudyQuestionnaires() - Ends");
    return mav;
  }

  @RequestMapping(value = "/adminStudies/validateAnchorDateName.do", method = RequestMethod.POST)
  public void validateAnchorDateName(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin validateAnchorDateName()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String anchordateText =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("anchordateText"))
                ? ""
                : request.getParameter("anchordateText");
        String anchorDateId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("anchorDateId"))
                ? ""
                : request.getParameter("anchorDateId");
        String customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (StringUtils.isEmpty(customStudyId)) {
          customStudyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        }
        if (!anchordateText.isEmpty() && !customStudyId.isEmpty()) {
          message =
              studyQuestionnaireService.checkUniqueAnchorDateName(
                  anchordateText, customStudyId, anchorDateId);
        }
      }
      jsonobject.put("message", message);
      response.setContentType("application/json");
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyQuestionnaireController - validateAnchorDateName - ERROR", e);
    }
    logger.exit("validateAnchorDateName() - Ends");
  }
}
