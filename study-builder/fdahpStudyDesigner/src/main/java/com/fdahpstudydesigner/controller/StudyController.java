/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
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

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_ASSOCIATED_STUDIES_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.LAST_PUBLISHED_VERSION_OF_STUDY_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_STUDY_CREATION_INITIATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ACCESSED_IN_EDIT_MODE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_CONSENT_SECTIONS_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_CONSENT_SECTIONS_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_COPIED_INTO_NEW;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_COPY_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ELIGIBILITY_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_ELIGIBILITY_SECTION_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_EXPORTED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_EXPORT_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_IMPORTED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_IMPORT_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_LIST_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_METADATA_SEND_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_METADATA_SEND_OPERATION_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_METADATA_SENT_TO_PARTICIPANT_DATASTORE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_METADATA_SENT_TO_RESPONSE_DATASTORE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NEW_RESOURCE_CREATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_NOTIFICATIONS_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_QUESTIONNAIRES_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_RESOURCE_MARKED_COMPLETED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_RESOURCE_SAVED_OR_UPDATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_RESOURCE_SECTION_MARKED_COMPLETE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_SAVED_IN_DRAFT_STATE;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.STUDY_VIEWED;

import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bean.StudyDetailsBean;
import com.fdahpstudydesigner.bean.StudyIdBean;
import com.fdahpstudydesigner.bean.StudyListBean;
import com.fdahpstudydesigner.bean.StudyPageBean;
import com.fdahpstudydesigner.bean.StudySessionBean;
import com.fdahpstudydesigner.bo.AnchorDateTypeBo;
import com.fdahpstudydesigner.bo.AppsBo;
import com.fdahpstudydesigner.bo.Checklist;
import com.fdahpstudydesigner.bo.ComprehensionTestQuestionBo;
import com.fdahpstudydesigner.bo.ConsentBo;
import com.fdahpstudydesigner.bo.ConsentInfoBo;
import com.fdahpstudydesigner.bo.ConsentMasterInfoBo;
import com.fdahpstudydesigner.bo.EligibilityBo;
import com.fdahpstudydesigner.bo.EligibilityTestBo;
import com.fdahpstudydesigner.bo.NotificationBO;
import com.fdahpstudydesigner.bo.NotificationHistoryBO;
import com.fdahpstudydesigner.bo.ReferenceTablesBo;
import com.fdahpstudydesigner.bo.ResourceBO;
import com.fdahpstudydesigner.bo.StudyBo;
import com.fdahpstudydesigner.bo.StudyPageBo;
import com.fdahpstudydesigner.bo.StudyPermissionBO;
import com.fdahpstudydesigner.bo.StudySequenceBo;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.dao.StudyDAO;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.service.AppService;
import com.fdahpstudydesigner.service.NotificationService;
import com.fdahpstudydesigner.service.OAuthService;
import com.fdahpstudydesigner.service.StudyExportImportService;
import com.fdahpstudydesigner.service.StudyQuestionnaireService;
import com.fdahpstudydesigner.service.StudyService;
import com.fdahpstudydesigner.service.UsersService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StudyController {

  private static XLogger logger = XLoggerFactory.getXLogger(StudyController.class.getName());

  @Autowired private NotificationService notificationService;

  @Autowired private StudyQuestionnaireService studyQuestionnaireService;

  @Autowired private StudyService studyService;

  @Autowired private UsersService usersService;

  @Autowired private RestTemplate restTemplate;

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  @Autowired private StudyExportImportService studyExportImportService;

  @Autowired private StudyDAO studyDao;

  @Autowired private OAuthService oauthService;

  @Autowired private AppService appService;

  @RequestMapping("/adminStudies/actionList.do")
  public ModelAndView actionList(HttpServletRequest request) {
    logger.entry("begin actionList()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    String errMsg = "";
    StudyBo studyBo = null;
    StudyBo liveStudyBo = null;
    String actionSucMsg = "";
    StudyPermissionBO studyPermissionBO = null;
    boolean markAsCompleted = false;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
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
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ACTION_SUC_MSG)) {
          actionSucMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ACTION_SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ACTION_SUC_MSG, actionSucMsg);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ACTION_SUC_MSG);
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
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        if (FdahpStudyDesignerUtil.isEmpty(studyId)) {
          studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        }
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        if (FdahpStudyDesignerUtil.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          liveStudyBo = studyService.getStudyLiveStatusByCustomId(studyBo.getCustomStudyId());
          studyPermissionBO =
              studyService.findStudyPermissionBO(studyBo.getId(), sesObj.getUserId());

          String signedUrl = "";
          if (liveStudyBo != null) {
            if (liveStudyBo != null
                && studyBo.getExportTime() != null
                && liveStudyBo.getExportTime() != null) {
              signedUrl =
                  studyBo.getExportTime().before(liveStudyBo.getExportTime())
                      ? liveStudyBo.getExportSignedUrl()
                      : studyBo.getExportSignedUrl();
            } else if (liveStudyBo != null
                && studyBo.getExportTime() != null
                && liveStudyBo.getExportTime() == null) {
              signedUrl = studyBo.getExportSignedUrl();
            } else if (liveStudyBo != null
                && studyBo.getExportTime() == null
                && liveStudyBo.getExportTime() != null) {
              signedUrl = liveStudyBo.getExportSignedUrl();
            }
          } else {
            signedUrl =
                StringUtils.isNotEmpty(studyBo.getExportSignedUrl())
                    ? studyBo.getExportSignedUrl()
                    : "";
          }

          markAsCompleted = studyService.validateStudyActions(studyId);
          map.addAttribute("_S", sessionStudyCount);
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
          map.addAttribute("liveStudyBo", liveStudyBo);
          map.addAttribute("studyPermissionBO", studyPermissionBO);
          map.addAttribute("markAsCompleted", markAsCompleted);
          map.addAttribute("signedUrlExpiryTime", propMap.get("signed.url.duration.in.hours"));
          map.addAttribute("releaseVersion", propMap.get("release.version"));
          map.addAttribute(
              "exportSignedUrl",
              StringUtils.isNotEmpty(signedUrl)
                  ? URLEncoder.encode(signedUrl, StandardCharsets.UTF_8.toString())
                  : "");

          mav = new ModelAndView("actionList", map);
        } else {
          return new ModelAndView("redirect:/adminStudies/studyList.do");
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - actionList - ERROR", e);
    }
    logger.exit("actionList() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/comprehensionTestMarkAsCompleted.do")
  public ModelAndView comprehensionTestMarkAsCompleted(HttpServletRequest request) {
    logger.entry("begin comprehensionTestMarkAsCompleted()");
    ModelAndView mav = new ModelAndView("redirect:studyList.do");
    ModelMap map = new ModelMap();
    String message = FdahpStudyDesignerConstants.FAILURE;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
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
        }
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        message =
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.COMPREHENSION_TEST, sesObj, customStudyId);
        map.addAttribute("_S", sessionStudyCount);
        if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
          map.addAttribute("buttonText", FdahpStudyDesignerConstants.COMPLETED_BUTTON);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                  propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
          mav = new ModelAndView("redirect:comprehensionQuestionList.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  FdahpStudyDesignerConstants.UNABLE_TO_MARK_AS_COMPLETE);
          mav = new ModelAndView("redirect:comprehensionQuestionList.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - comprehensionTestMarkAsCompleted() - ERROR", e);
    }
    logger.exit("comprehensionTestMarkAsCompleted() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/consentMarkAsCompleted.do")
  public ModelAndView consentMarkAsCompleted(HttpServletRequest request) {
    logger.entry("begin consentMarkAsCompleted()");
    ModelAndView mav = new ModelAndView("redirect:studyList.do");
    ModelMap map = new ModelMap();
    String message = FdahpStudyDesignerConstants.FAILURE;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
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
        }
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        auditRequest.setStudyId(customStudyId);
        message =
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.CONESENT, sesObj, customStudyId);
        StudyBo studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
        if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
          auditRequest.setStudyVersion(studyBo.getVersion().toString());
          auditRequest.setAppId(studyBo.getAppId());
          auditLogEventHelper.logEvent(STUDY_CONSENT_SECTIONS_MARKED_COMPLETE, auditRequest);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                  propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
          map.addAttribute("_S", sessionStudyCount);
          map.addAttribute("buttonText", FdahpStudyDesignerConstants.COMPLETED_BUTTON);
          mav = new ModelAndView("redirect:consentListPage.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  FdahpStudyDesignerConstants.UNABLE_TO_MARK_AS_COMPLETE);
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:consentListPage.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - consentMarkAsCompleted() - ERROR", e);
    }
    logger.exit("consentMarkAsCompleted() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/consentReviewMarkAsCompleted.do")
  public ModelAndView consentReviewMarkAsCompleted(HttpServletRequest request) {
    logger.entry("begin consentReviewMarkAsCompleted()");
    ModelAndView mav = new ModelAndView("redirect:studyList.do");
    ModelMap map = new ModelMap();
    String message = FdahpStudyDesignerConstants.FAILURE;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
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
        }
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        message =
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.CONESENT_REVIEW, sesObj, customStudyId);
        map.addAttribute("_S", sessionStudyCount);
        if (request.getParameter("isActive") != null
            && request.getParameter("isActive").equals("consentReview")) {
          map.addAttribute("isActive", "consentReview");
        }
        if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                  propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
          mav = new ModelAndView("redirect:consentReview.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  FdahpStudyDesignerConstants.UNABLE_TO_MARK_AS_COMPLETE);
          mav = new ModelAndView("redirect:consentReview.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - consentReviewMarkAsCompleted() - ERROR", e);
    }
    logger.exit("consentReviewMarkAsCompleted() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/crateNewStudy.do")
  public ModelAndView crateNewStudy(HttpServletRequest request) {
    logger.entry("begin crateNewStudy()");
    new ModelMap();
    ModelAndView modelAndView = new ModelAndView("redirect:/adminStudies/studyList.do");
    boolean flag = false;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      String customStudyId =
          FdahpStudyDesignerUtil.isEmpty(
                  request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID))
              ? ""
              : request.getParameter(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
      if (StringUtils.isNotEmpty(customStudyId) && (sesObj != null)) {
        flag = studyService.copyliveStudyByCustomStudyId(customStudyId, sesObj);
      }
      if (flag) {
        request
            .getSession()
            .setAttribute(
                FdahpStudyDesignerConstants.ACTION_SUC_MSG,
                FdahpStudyDesignerConstants.COPY_STUDY_SUCCESS_MSG);
      } else {
        request
            .getSession()
            .setAttribute(
                FdahpStudyDesignerConstants.ERR_MSG,
                FdahpStudyDesignerConstants.COPY_STUDY_FAILURE_MSG);
      }
    } catch (Exception e) {
      logger.error("StudyController - crateNewStudy - ERROR", e);
    }
    logger.exit("crateNewStudy() - Ends");
    return modelAndView;
  }

  @RequestMapping("/adminStudies/deleteComprehensionQuestion.do")
  public void deleteComprehensionTestQuestion(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin deleteComprehensionTestQuestion()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (sesObj != null) {
        String comprehensionQuestionId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.COMPREHENSION_QUESTION_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.COMPREHENSION_QUESTION_ID);
        String studyId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        if (StringUtils.isNotEmpty(comprehensionQuestionId) && StringUtils.isNotEmpty(studyId)) {
          message =
              studyService.deleteComprehensionTestQuestion(
                  comprehensionQuestionId, studyId, sesObj);
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - deleteComprehensionTestQuestion - ERROR", e);
    }
    logger.exit("deleteComprehensionTestQuestion() - Ends");
  }

  @RequestMapping(value = "/adminStudies/deleteConsentInfo.do", method = RequestMethod.POST)
  public void deleteConsentInfo(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin deleteConsentInfo()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
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
        String consentInfoId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.CONSENT_INFO_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.CONSENT_INFO_ID);
        String studyId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (!consentInfoId.isEmpty() && !studyId.isEmpty()) {
          message = studyService.deleteConsentInfo(consentInfoId, studyId, sesObj, customStudyId);
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - deleteConsentInfo - ERROR", e);
    }
    logger.exit("deleteConsentInfo() - Ends");
  }

  @RequestMapping(
      value = "/adminStudies/deleteEligibiltyTestQusAns.do",
      method = RequestMethod.POST)
  public void deleteEligibiltyTestQusAns(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin deleteEligibiltyTestQusAns()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    String customStudyId = "";
    List<EligibilityTestBo> testBos;
    ObjectMapper mapper = new ObjectMapper();
    JSONArray eligibilityTestJsonArray = null;
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
        String eligibilityTestId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("eligibilityTestId"))
                ? "0"
                : request.getParameter("eligibilityTestId");
        String eligibilityId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("eligibilityId"))
                ? "0"
                : request.getParameter("eligibilityId");
        String studyId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? "0"
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        message =
            studyService.deleteEligibilityTestQusAnsById(
                eligibilityTestId, studyId, sesObj, customStudyId);
        if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
          testBos = studyService.viewEligibilityTestQusAnsByEligibilityId(eligibilityId);
          if ((testBos != null) && !testBos.isEmpty()) {
            eligibilityTestJsonArray = new JSONArray(mapper.writeValueAsString(testBos));
          }
          jsonobject.put("eligibiltyTestList", eligibilityTestJsonArray);
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - deleteEligibiltyTestQusAns - ERROR", e);
    }
    logger.exit("deleteEligibiltyTestQusAns() - Ends");
  }

  @RequestMapping(value = "/adminStudies/deleteResourceInfo.do", method = RequestMethod.POST)
  public void deleteResourceInfo(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin deleteResourceInfo()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    List<ResourceBO> resourcesSavedList = null;
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
        String resourceInfoId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.RESOURCE_INFO_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.RESOURCE_INFO_ID);
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
        }
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (!resourceInfoId.isEmpty()) {
          message = studyService.deleteResourceInfo(resourceInfoId, sesObj, customStudyId, studyId);
        }
        resourcesSavedList = studyService.resourcesSaved(studyId);
        if (!resourcesSavedList.isEmpty()) {
          jsonobject.put("resourceSaved", true);
        } else {
          jsonobject.put("resourceSaved", false);
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - deleteResourceInfo() - ERROR", e);
    }
    logger.exit("deleteConsentInfo() - Ends");
  }

  @RequestMapping("/deleteStudy.do")
  public ModelAndView deleteStudy(HttpServletRequest request) {
    logger.entry("begin deleteStudy()");
    ModelAndView mav = new ModelAndView("redirect:login.do");
    boolean flag = false;
    try {

      String cusId =
          FdahpStudyDesignerUtil.isEmpty(request.getParameter("cusId"))
              ? ""
              : request.getParameter("cusId");
      if (!cusId.isEmpty()) {
        flag = studyService.deleteStudyByCustomStudyId(cusId);
        if (flag) {
          request.getSession(false).setAttribute("sucMsg", "deleted successfully");
        } else {
          request.getSession(false).setAttribute("errMsg", "DB issue or study does not exist");
        }
      }

    } catch (Exception e) {
      logger.error("StudyController - deleteStudy - ERROR", e);
    }
    logger.exit("deleteStudy() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/deleteStudyNotification.do")
  public ModelAndView deleteStudyNotification(HttpServletRequest request) {
    logger.entry("begin deleteStudyNotification()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelMap map = new ModelMap();
    try {
      HttpSession session = request.getSession();
      SessionObject sessionObject =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sessionObject != null)
          && (sessionObject.getStudySession() != null)
          && sessionObject.getStudySession().contains(sessionStudyCount)) {
        String notificationId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID);
        if (null != notificationId) {
          String notificationType = FdahpStudyDesignerConstants.STUDYLEVEL;
          message =
              notificationService.deleteNotification(
                  notificationId, sessionObject, notificationType);
          if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get("delete.notification.success.message"));
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    propMap.get("delete.notification.error.message"));
          }
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:/adminStudies/viewStudyNotificationList.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - deleteStudyNotification - ERROR", e);
    }
    return mav;
  }

  @RequestMapping(value = "/downloadPdf.do")
  public ModelAndView downloadPdf(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    logger.entry("begin downloadPdf()");
    Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();
    InputStream is = null;
    SessionObject sesObj =
        (SessionObject)
            request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
    Integer sessionStudyCount =
        StringUtils.isNumeric(request.getParameter("_S"))
            ? Integer.parseInt(request.getParameter("_S"))
            : 0;
    ModelAndView mav = null;
    try {
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        String fileName =
            (request.getParameter("fileName")) == null ? "" : request.getParameter("fileName");
        String fileFolder =
            (request.getParameter("fileFolder")) == null ? "" : request.getParameter("fileFolder");
        String studyId =
            (request.getParameter("studyId")) == null ? "" : request.getParameter("studyId");
        if (StringUtils.isNotBlank(fileName) && StringUtils.isNotBlank(fileFolder)) {
          request.getSession().setAttribute(sessionStudyCount + "fileName", fileName);
          request.getSession().setAttribute(sessionStudyCount + "fileFolder", fileFolder);
        } else {
          fileName = (String) request.getSession().getAttribute(sessionStudyCount + "fileName");
          fileFolder = (String) request.getSession().getAttribute(sessionStudyCount + "fileFolder");
        }

        StudyBo study = studyService.getStudyInfo(studyId);

        String path =
            FdahpStudyDesignerConstants.STUDIES
                + FdahpStudyDesignerConstants.PATH_SEPARATOR
                + study.getCustomStudyId()
                + FdahpStudyDesignerConstants.PATH_SEPARATOR
                + fileFolder
                + "/"
                + fileName;
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get(configMap.get("cloud.bucket.name"), path);
        ReadChannel readChannel = blob.reader();
        InputStream inputStream = Channels.newInputStream(readChannel);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        IOUtils.copy(inputStream, response.getOutputStream());
        response.flushBuffer();
        inputStream.close();

      } else {
        mav = new ModelAndView("redirect:studyList.do");
      }
    } catch (Exception e) {
      logger.error("StudyController - downloadPdf() - ERROR", e);
    } finally {
      if (null != is) {
        is.close();
      }
    }
    logger.exit("downloadPdf() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/getChecklist.do")
  public ModelAndView getChecklist(HttpServletRequest request) {
    logger.entry("begin getChecklist()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    String sucMsg = "";
    String errMsg = "";
    StudyBo studyBo = null;
    Checklist checklist = null;
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
        map.addAttribute("_S", sessionStudyCount);
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        if (StringUtils.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          checklist = studyService.getchecklistInfo(studyId);
        }
        map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
        map.addAttribute("checklist", checklist);
        map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
        mav = new ModelAndView("checklist", map);
      }
    } catch (Exception e) {
      logger.error("StudyController - getChecklist() - ERROR", e);
    }
    logger.exit("getChecklist() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/comprehensionQuestionList.do")
  public ModelAndView getComprehensionQuestionList(HttpServletRequest request) {
    logger.entry("begin getComprehensionQuestionList()");
    ModelAndView mav = new ModelAndView("comprehensionListPage");
    ModelMap map = new ModelMap();
    StudyBo studyBo = null;
    ConsentBo consentBo = null;
    String sucMsg = "";
    String errMsg = "";
    String consentStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      List<ComprehensionTestQuestionBo> comprehensionTestQuestionList;
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
        request
            .getSession()
            .removeAttribute(
                sessionStudyCount + FdahpStudyDesignerConstants.COMPREHENSION_QUESTION_ID);
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
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
          consentStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_STUDY_ID);
        }
        // Added for live version End
        if (StringUtils.isNotEmpty(studyId)) {
          if (StringUtils.isNotEmpty(consentStudyId)) {
            consentBo = studyService.getConsentDetailsByStudyId(consentStudyId);
          } else {
            consentBo = studyService.getConsentDetailsByStudyId(studyId);
          }
          comprehensionTestQuestionList = studyService.getComprehensionTestQuestionList(studyId);
          boolean markAsComplete = true;
          if ((comprehensionTestQuestionList != null) && !comprehensionTestQuestionList.isEmpty()) {
            for (ComprehensionTestQuestionBo comprehensionTestQuestionBo :
                comprehensionTestQuestionList) {
              if (!comprehensionTestQuestionBo.getStatus()) {
                markAsComplete = false;
                break;
              }
            }
          }
          map.addAttribute("markAsComplete", markAsComplete);
          map.addAttribute("comprehensionTestQuestionList", comprehensionTestQuestionList);
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          // get consentId if exists for studyId
          if (consentBo != null) {
            request
                .getSession()
                .setAttribute(FdahpStudyDesignerConstants.CONSENT_ID, consentBo.getId());
            map.addAttribute(FdahpStudyDesignerConstants.CONESENT, consentBo.getId());
            map.addAttribute("consentBo", consentBo);
          }
        }
        map.addAttribute(FdahpStudyDesignerConstants.STUDY_ID, studyId);
        map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView(FdahpStudyDesignerConstants.COMPREHENSION_LIST_PAGE, map);
      }
    } catch (Exception e) {
      logger.error("StudyController - getComprehensionQuestionList - ERROR", e);
    }
    logger.exit("getComprehensionQuestionList() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/comprehensionQuestionPage.do")
  public ModelAndView getComprehensionQuestionPage(HttpServletRequest request) {
    logger.entry("begin getComprehensionQuestionPage()");
    ModelAndView mav = new ModelAndView("comprehensionQuestionPage");
    ModelMap map = new ModelMap();
    ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
    String sucMsg = "";
    String errMsg = "";
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
        String comprehensionQuestionId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.COMPREHENSION_QUESTION_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.COMPREHENSION_QUESTION_ID);
        String actionType =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE);
        String studyId =
            (String) request.getSession().getAttribute(FdahpStudyDesignerConstants.STUDY_ID);
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
          request.getSession().setAttribute(FdahpStudyDesignerConstants.STUDY_ID, studyId);
        }
        if (StringUtils.isEmpty(comprehensionQuestionId)) {
          comprehensionQuestionId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount
                              + FdahpStudyDesignerConstants.COMPREHENSION_QUESTION_ID);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.COMPREHENSION_QUESTION_ID,
                  comprehensionQuestionId);
        }
        if (StringUtils.isNotEmpty(studyId)) {
          if (("view").equals(actionType)) {
            map.addAttribute(FdahpStudyDesignerConstants.ACTION_PAGE, "view");
          } else {
            map.addAttribute(FdahpStudyDesignerConstants.ACTION_PAGE, "addEdit");
          }
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          map.addAttribute("_S", sessionStudyCount);
        }
        if (StringUtils.isNotEmpty(comprehensionQuestionId)) {
          comprehensionTestQuestionBo =
              studyService.getComprehensionTestQuestionById(comprehensionQuestionId);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.COMPREHENSION_QUESTION_ID,
                  comprehensionQuestionId);
        }
        map.addAttribute("comprehensionQuestionBo", comprehensionTestQuestionBo);
        mav = new ModelAndView("comprehensionQuestionPage", map);
      }
    } catch (Exception e) {
      logger.error("StudyController - getConsentPage - Error", e);
    }
    logger.exit("getComprehensionQuestionPage() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/consentListPage.do")
  public ModelAndView getConsentListPage(HttpServletRequest request) {
    logger.entry("begin getConsentListPage()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    StudyBo studyBo = null;
    ConsentBo consentBo = null;
    String sucMsg = "";
    String errMsg = "";
    String consentStudyId = "";
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
        List<ConsentInfoBo> consentInfoList;
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
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
          consentStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_STUDY_ID);
        }
        // Added for live version End
        if (StringUtils.isNotEmpty(studyId)) {
          boolean markAsComplete = true;
          if (StringUtils.isNotEmpty(consentStudyId)) {
            consentInfoList = studyService.getConsentInfoList(consentStudyId);
          } else {
            consentInfoList = studyService.getConsentInfoList(studyId);
          }
          if ((consentInfoList != null) && !consentInfoList.isEmpty()) {
            for (ConsentInfoBo conInfoBo : consentInfoList) {
              if (!conInfoBo.getStatus()) {
                markAsComplete = false;
                break;
              }
            }
          }
          map.addAttribute("markAsComplete", markAsComplete);
          map.addAttribute(FdahpStudyDesignerConstants.CONSENT_INFO_LIST, consentInfoList);
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_ID, studyId);
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);

          // get consentbo details by studyId
          if (StringUtils.isNotEmpty(consentStudyId)) {
            consentBo = studyService.getConsentDetailsByStudyId(consentStudyId);
          } else {
            consentBo = studyService.getConsentDetailsByStudyId(studyId);
          }
          if (consentBo != null) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_ID, consentBo.getId());
            map.addAttribute(FdahpStudyDesignerConstants.CONSENT_ID, consentBo.getId());
          }
        }
        map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView(FdahpStudyDesignerConstants.CONSENT_INFO_LIST_PAGE, map);
      }
    } catch (Exception e) {
      logger.error("StudyController - getConsentListPage - ERROR", e);
    }
    logger.exit("getConsentListPage() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/consentInfo.do")
  public ModelAndView getConsentPage(HttpServletRequest request) {
    logger.entry("begin getConsentPage()");
    ModelAndView mav = new ModelAndView(FdahpStudyDesignerConstants.CONSENT_INFO_PAGE);
    ModelMap map = new ModelMap();
    ConsentInfoBo consentInfoBo = null;
    StudyBo studyBo = null;
    List<ConsentInfoBo> consentInfoList = new ArrayList<>();
    List<ConsentMasterInfoBo> consentMasterInfoList = new ArrayList<>();
    String sucMsg = "";
    String errMsg = "";
    String consentStudyId = "";
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
        String consentInfoId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.CONSENT_INFO_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.CONSENT_INFO_ID);
        String actionType =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE);
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
          request
              .getSession()
              .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID, studyId);
        }
        // Added for live version Start
        String isLive =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.IS_LIVE);
        if (StringUtils.isNotEmpty(isLive)
            && isLive.equalsIgnoreCase(FdahpStudyDesignerConstants.YES)) {
          consentStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_STUDY_ID);
        }
        // Added for live version End
        if (StringUtils.isEmpty(consentInfoId)) {
          consentInfoId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_INFO_ID);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_INFO_ID, consentInfoId);
        }
        map.addAttribute(FdahpStudyDesignerConstants.STUDY_ID, studyId);
        if (!studyId.isEmpty()) {
          if (StringUtils.isNotEmpty(consentStudyId)) {
            consentInfoList = studyService.getConsentInfoList(consentStudyId);
          } else {
            consentInfoList = studyService.getConsentInfoList(studyId);
          }
          if (("view").equals(actionType)) {
            map.addAttribute(FdahpStudyDesignerConstants.ACTION_PAGE, "view");
          } else {
            map.addAttribute(FdahpStudyDesignerConstants.ACTION_PAGE, "addEdit");
          }
          consentMasterInfoList = studyService.getConsentMasterInfoList();
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          map.addAttribute("consentMasterInfoList", consentMasterInfoList);
          if ((consentMasterInfoList != null) && !consentMasterInfoList.isEmpty()) {
            map.addAttribute(FdahpStudyDesignerConstants.CONSENT_INFO_LIST, consentInfoList);
          }
        }
        if ((consentInfoId != null) && !consentInfoId.isEmpty()) {
          consentInfoBo = studyService.getConsentInfoById(consentInfoId);
          map.addAttribute("consentInfoBo", consentInfoBo);
        }
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView(FdahpStudyDesignerConstants.CONSENT_INFO_PAGE, map);
      }
    } catch (Exception e) {
      logger.error("StudyController - getConsentPage - Error", e);
    }
    logger.exit("getConsentPage - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/consentReview.do")
  public ModelAndView getConsentReviewAndEConsentPage(HttpServletRequest request) {
    logger.entry("begin getConsentReviewAndEConsentPage()");
    ModelAndView mav = new ModelAndView(FdahpStudyDesignerConstants.CONSENT_INFO_PAGE);
    ModelMap map = new ModelMap();
    SessionObject sesObj = null;
    String studyId = "";
    List<ConsentInfoBo> consentInfoBoList = null;
    List<ConsentBo> consentBoList = null;
    String lastPublishedVersion = null;
    StudyBo studyBo = null;
    ConsentBo consentBo = null;
    String sucMsg = "";
    String errMsg = "";
    String consentStudyId = "";
    try {
      sesObj =
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

        if (request
                .getSession()
                .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID)
            != null) {
          studyId =
              request
                  .getSession()
                  .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID)
                  .toString();
        }

        if (StringUtils.isEmpty(studyId)) {
          studyId =
              StringUtils.isEmpty(request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
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
          consentStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_STUDY_ID);
        }
        // Added for live version End
        if (StringUtils.isNotEmpty(studyId)) {
          request
              .getSession()
              .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID, studyId);
          if (StringUtils.isNotEmpty(consentStudyId)) {
            consentInfoBoList = studyService.getConsentInfoDetailsListByStudyId(consentStudyId);
            consentBo = studyService.getConsentDetailsByStudyId(consentStudyId);
          } else {
            consentInfoBoList = studyService.getConsentInfoDetailsListByStudyId(studyId);
            consentBo = studyService.getConsentDetailsByStudyId(studyId);
          }
          if ((null != consentInfoBoList) && !consentInfoBoList.isEmpty()) {
            map.addAttribute(FdahpStudyDesignerConstants.CONSENT_INFO_LIST, consentInfoBoList);
          } else {
            map.addAttribute(FdahpStudyDesignerConstants.CONSENT_INFO_LIST, "");
          }
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);

          consentBoList = studyService.getConsentList(studyBo.getCustomStudyId());
          if (!CollectionUtils.isEmpty(consentBoList)) {
            lastPublishedVersion = 'V' + String.valueOf(consentBoList.get(0).getVersion());
          } else {
            lastPublishedVersion = "N/A";
          }

          if (consentBo != null) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_ID, consentBo.getId());
            map.addAttribute(FdahpStudyDesignerConstants.CONSENT_ID, consentBo.getId());
          }

          String permission =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
          if (StringUtils.isNotEmpty(permission) && ("view").equals(permission)) {
            map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, "view");
          } else {
            map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, "addEdit");
          }
        }
        map.addAttribute(FdahpStudyDesignerConstants.STUDY_ID, studyId);
        map.addAttribute("consentBo", consentBo);
        map.addAttribute("status", studyBo.getStatus());
        map.addAttribute("_S", sessionStudyCount);
        map.addAttribute("lastPublishedVersion", lastPublishedVersion);

        if (request.getParameter("isActive") != null
            && request.getParameter("isActive").equals("consentReview")) {
          map.addAttribute("isActive", "consentReview");
        }
        mav = new ModelAndView("consentReviewAndEConsentPage", map);
      }
    } catch (Exception e) {
      logger.error("StudyController - getConsentReviewAndEConsentPage() - ERROR ", e);
    }
    logger.exit("getConsentReviewAndEConsentPage() :: Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/getResourceList.do")
  public ModelAndView getResourceList(HttpServletRequest request) {
    logger.entry("begin getResourceList()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    String sucMsg = "";
    String errMsg = "";
    String resourceErrMsg = "";
    List<ResourceBO> resourceBOList = null;
    List<ResourceBO> resourcesSavedList = null;
    ResourceBO studyProtocolResourceBO = null;
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
        if (null != request.getSession().getAttribute(sessionStudyCount + "resourceErrMsg")) {
          resourceErrMsg =
              (String) request.getSession().getAttribute(sessionStudyCount + "resourceErrMsg");
          map.addAttribute("resourceErrMsg", resourceErrMsg);
          request.getSession().removeAttribute(sessionStudyCount + "resourceErrMsg");
        }
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        if (StringUtils.isNotEmpty(studyId)) {
          resourceBOList = studyService.getResourceList(studyId);
          studyProtocolResourceBO = studyService.getStudyProtocol(studyId);
          resourcesSavedList = studyService.resourcesSaved(studyId);
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          map.addAttribute("resourceBOList", resourceBOList);
          map.addAttribute("resourcesSavedList", resourcesSavedList);
          map.addAttribute("studyProtocolResourceBO", studyProtocolResourceBO);
        }
        map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
        map.addAttribute("_S", sessionStudyCount);
        mav = new ModelAndView("resourceListPage", map);
      }
    } catch (Exception e) {
      logger.error("StudyController - getResourceList() - ERROR", e);
    }
    logger.exit("getResourceList() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/studyList.do")
  public ModelAndView getStudies(HttpServletRequest request) {
    logger.entry("begin getStudies()");
    ModelAndView mav = new ModelAndView("loginPage");
    ModelMap map = new ModelMap();
    List<StudyListBean> studyBos = null;
    List<AppsBo> appList = null;
    String sucMsg = "";
    String errMsg = "";
    String actionSucMsg = "";
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (sesObj != null) {
        if (null != request.getSession().getAttribute("sucMsgAppActions")) {
          request.getSession().removeAttribute("sucMsgAppActions");
        }

        if (null != request.getSession().getAttribute("sucMsgViewAssocStudies")) {
          request.getSession().removeAttribute("sucMsgViewAssocStudies");
        }

        if (null != request.getSession().getAttribute("errMsgAppActions")) {
          request.getSession().removeAttribute("errMsgAppActions");
        }

        if (null != request.getSession().getAttribute(FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg = (String) request.getSession().getAttribute(FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null != request.getSession().getAttribute(FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg = (String) request.getSession().getAttribute(FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.ERR_MSG);
        }
        if (null != request.getSession().getAttribute(FdahpStudyDesignerConstants.ACTION_SUC_MSG)) {
          actionSucMsg =
              (String)
                  request.getSession().getAttribute(FdahpStudyDesignerConstants.ACTION_SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ACTION_SUC_MSG, actionSucMsg);
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.ACTION_SUC_MSG);
        }
        if (request.getSession().getAttribute(FdahpStudyDesignerConstants.STUDY_ID) != null) {
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.STUDY_ID);
        }
        if (request.getSession().getAttribute(FdahpStudyDesignerConstants.PERMISSION) != null) {
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.PERMISSION);
        }
        if (request.getSession().getAttribute(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID)
            != null) {
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        }
        if (request.getSession().getAttribute(FdahpStudyDesignerConstants.IS_LIVE) != null) {
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.IS_LIVE);
        }
        if (request.getSession().getAttribute(FdahpStudyDesignerConstants.CONSENT_STUDY_ID)
            != null) {
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.CONSENT_STUDY_ID);
        }
        if (request.getSession().getAttribute(FdahpStudyDesignerConstants.ACTIVE_TASK_STUDY_ID)
            != null) {
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.ACTIVE_TASK_STUDY_ID);
        }
        if (request.getSession().getAttribute(FdahpStudyDesignerConstants.QUESTIONNARIE_STUDY_ID)
            != null) {
          request.getSession().removeAttribute(FdahpStudyDesignerConstants.QUESTIONNARIE_STUDY_ID);
        }
        String appId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter(FdahpStudyDesignerConstants.APP_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.APP_ID);
        studyBos = studyService.getStudyList(sesObj.getUserId());
        appList = appService.getApps(sesObj.getUserId());
        map.addAttribute("studyBos", studyBos);
        map.addAttribute("studyListId", "true");
        if (StringUtils.isNotEmpty(appId)) {
          auditRequest.setAppId(appId);
          auditLogEventHelper.logEvent(APP_ASSOCIATED_STUDIES_VIEWED, auditRequest);
          map.addAttribute("appId", appId);
          request
              .getSession()
              .setAttribute(
                  "sucMsgViewAssocStudies",
                  FdahpStudyDesignerConstants.VIEW_ASSOCIATED_STUDIES_MESSAGE);
        }
        map.addAttribute("appBos", appList);
        auditLogEventHelper.logEvent(STUDY_LIST_VIEWED, auditRequest);

        mav = new ModelAndView("studyListPage", map);
      }
    } catch (Exception e) {
      logger.error("StudyController - getStudies - ERROR", e);
    }
    logger.exit("getStudies() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/getStudyNotification.do")
  public ModelAndView getStudyNotification(HttpServletRequest request) {
    logger.entry("begin getStudyNotification()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    NotificationBO notificationBO = null;
    List<NotificationHistoryBO> notificationHistoryNoDateTime = null;
    StudyBo studyBo = null;
    String sucMsg = "";
    String errMsg = "";
    try {
      HttpSession session = request.getSession();
      SessionObject sessionObject =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sessionObject != null)
          && (sessionObject.getStudySession() != null)
          && sessionObject.getStudySession().contains(sessionStudyCount)) {
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
        String notificationId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.NOTIFICATIONID);
        if (StringUtils.isEmpty(notificationId)) {
          notificationId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.NOTIFICATIONID);
        }
        String chkRefreshflag =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CHKREFRESHFLAG);
        if (StringUtils.isEmpty(chkRefreshflag)) {
          chkRefreshflag =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.CHKREFRESHFLAG))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.CHKREFRESHFLAG);
        }
        String actionType =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ACTION_TYPE);
        if (StringUtils.isEmpty(actionType)) {
          actionType =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.ACTION_TYPE);
        }
        String notificationText =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("notificationText"))
                ? ""
                : request.getParameter("notificationText");
        map.addAttribute("_S", sessionStudyCount);
        if (!"".equals(chkRefreshflag)) {
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
          }
          // Getting study details by userId for notification
          studyBo = studyService.getStudyById(studyId, sessionObject.getUserId());
          if (!"".equals(notificationId)) {
            // Fetching notification detail from notification table by
            // Id.
            notificationBO = notificationService.getNotification(notificationId);
            // Fetching notification history of last sent detail from
            // notification table by Id.
            notificationHistoryNoDateTime =
                notificationService.getNotificationHistoryListNoDateTime(notificationId);
            if ("edit".equals(actionType)) {
              notificationBO.setActionPage("edit");
            } else if (FdahpStudyDesignerConstants.ADDORCOPY.equals(actionType)) {
              notificationBO.setActionPage(FdahpStudyDesignerConstants.ADDORCOPY);
            } else if (FdahpStudyDesignerConstants.RESEND.equals(actionType)) {
              if (notificationBO.isNotificationSent()) {
                notificationBO.setScheduleDate("");
                notificationBO.setScheduleTime("");
              }
              notificationBO.setActionPage(FdahpStudyDesignerConstants.RESEND);
            } else {
              notificationBO.setActionPage("view");
            }
            request
                .getSession()
                .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.NOTIFICATIONID);
            request
                .getSession()
                .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ACTION_TYPE);
            request
                .getSession()
                .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CHKREFRESHFLAG);
          } else if (!"".equals(notificationText) && "".equals(notificationId)) {
            notificationBO = new NotificationBO();
            notificationBO.setNotificationText(notificationText);
            notificationBO.setActionPage(FdahpStudyDesignerConstants.ADDORCOPY);
          } else if ("".equals(notificationText) && "".equals(notificationId)) {
            notificationBO = new NotificationBO();
            notificationBO.setActionPage(FdahpStudyDesignerConstants.ADDORCOPY);
          }
          map.addAttribute("notificationBO", notificationBO);
          map.addAttribute("notificationHistoryNoDateTime", notificationHistoryNoDateTime);
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          map.addAttribute("appId", studyBo.getAppId());
          mav = new ModelAndView("addOrEditStudyNotification", map);

        } else {
          mav = new ModelAndView("redirect:viewStudyNotificationList.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - getStudyNotification - ERROR", e);
    }
    logger.exit("getStudyNotification - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/notificationMarkAsCompleted.do")
  public ModelAndView notificationMarkAsCompleted(HttpServletRequest request) {
    logger.entry("begin notificationMarkAsCompleted()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String message = FdahpStudyDesignerConstants.FAILURE;
    String customStudyId = "";
    ModelMap map = new ModelMap();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      auditRequest.setCorrelationId(sesObj.getSessionId());
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
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
        }
        String markCompleted = FdahpStudyDesignerConstants.NOTIFICATION;
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        auditRequest.setStudyId(customStudyId);
        // markCompleted in param specify that notification to update it
        // as completed in table StudySequenceBo
        message = studyService.markAsCompleted(studyId, markCompleted, sesObj, customStudyId);
        map.addAttribute("_S", sessionStudyCount);
        if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
          StudyBo studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          auditRequest.setStudyVersion(studyBo.getVersion().toString());
          auditRequest.setAppId(studyBo.getAppId());
          auditLogEventHelper.logEvent(STUDY_NOTIFICATIONS_SECTION_MARKED_COMPLETE, auditRequest);
          map.addAttribute("buttonText", FdahpStudyDesignerConstants.COMPLETED_BUTTON);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                  propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
          StudyBuilderAuditEvent auditLogEvent = STUDY_NOTIFICATIONS_SECTION_MARKED_COMPLETE;
          auditLogEventHelper.logEvent(auditLogEvent, auditRequest);
          mav = new ModelAndView("redirect:viewStudyNotificationList.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  FdahpStudyDesignerConstants.UNABLE_TO_MARK_AS_COMPLETE);
          mav = new ModelAndView("redirect:viewStudyNotificationList.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - notificationMarkAsCompleted() - ERROR", e);
    }
    logger.exit("notificationMarkAsCompleted() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/overviewStudyPages.do")
  public ModelAndView overviewStudyPages(HttpServletRequest request) {
    logger.entry("begin overviewStudyPages()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    List<StudyPageBo> studyPageBos = null;
    StudyBo studyBo = null;
    String sucMsg = "";
    String errMsg = "";
    StudyPageBean studyPageBean = new StudyPageBean();
    String user = "";
    Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();
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
        String studyId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        if (FdahpStudyDesignerUtil.isEmpty(studyId)) {
          studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        }
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        user =
            (String)
                request
                    .getSession()
                    .getAttribute(
                        sessionStudyCount + FdahpStudyDesignerConstants.LOGOUT_LOGIN_USER);
        if (StringUtils.isNotEmpty(studyId)) {
          studyPageBos = studyService.getOverviewStudyPagesById(studyId, sesObj.getUserId());
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          studyPageBean.setStudyId(studyBo.getId().toString());

          map.addAttribute("studyPageBos", studyPageBos);
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          map.addAttribute("studyPageBean", studyPageBean);
          map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
          map.addAttribute("_S", sessionStudyCount);
          map.addAttribute("user", user);
          map.addAttribute(
              "defaultOverViewImageSignedUrl",
              FdahpStudyDesignerUtil.getImageResources(
                  FdahpStudyDesignerConstants.DEFAULT_IMAGES
                      + "/"
                      + configMap.get("study.defaultImage")));
          map.addAttribute(
              "defaultPageOverviewImageSignedUrl",
              FdahpStudyDesignerUtil.getImageResources(
                  FdahpStudyDesignerConstants.DEFAULT_IMAGES
                      + "/"
                      + configMap.get("study.page2.defaultImage")));

          mav = new ModelAndView("overviewStudyPages", map);
        } else {
          return new ModelAndView("redirect:studyList.do");
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - overviewStudyPages - ERROR", e);
    }
    logger.exit("overviewStudyPages() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/questionnaireMarkAsCompleted.do")
  public ModelAndView questionnaireMarkAsCompleted(HttpServletRequest request) {
    logger.entry("begin questionnaireMarkAsCompleted()");
    ModelAndView mav = new ModelAndView("redirect:studyList.do");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String customStudyId = "";
    ModelMap map = new ModelMap();
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
        }
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        auditRequest.setStudyId(customStudyId);
        message =
            studyService.markAsCompleted(
                studyId, FdahpStudyDesignerConstants.QUESTIONNAIRE, sesObj, customStudyId);
        if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
          StudyBo studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          if (studyBo != null) {
            auditRequest.setStudyVersion(studyBo.getVersion().toString());
            auditRequest.setAppId(studyBo.getAppId());
          }
          auditLogEventHelper.logEvent(STUDY_QUESTIONNAIRES_SECTION_MARKED_COMPLETE, auditRequest);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                  propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
          map.addAttribute("_S", sessionStudyCount);
          map.addAttribute("buttonText", FdahpStudyDesignerConstants.COMPLETED_BUTTON);
          mav = new ModelAndView("redirect:viewStudyQuestionnaires.do", map);
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  FdahpStudyDesignerConstants.UNABLE_TO_MARK_AS_COMPLETE);
          map.addAttribute("_S", sessionStudyCount);
          mav = new ModelAndView("redirect:viewStudyQuestionnaires.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - questionnaireMarkAsCompleted() - ERROR", e);
    }
    logger.exit("questionnaireMarkAsCompleted() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/reloadComprehensionQuestionListPage.do")
  public void reloadComprehensionQuestionListPage(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin reloadComprehensionQuestionListPage()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    ObjectMapper mapper = new ObjectMapper();
    JSONArray comprehensionJsonArray = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      List<ComprehensionTestQuestionBo> comprehensionTestQuestionList;
      if (sesObj != null) {
        String studyId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        if (StringUtils.isNotEmpty(studyId)) {
          comprehensionTestQuestionList = studyService.getComprehensionTestQuestionList(studyId);
          if ((comprehensionTestQuestionList != null) && !comprehensionTestQuestionList.isEmpty()) {
            comprehensionJsonArray =
                new JSONArray(mapper.writeValueAsString(comprehensionTestQuestionList));
          }
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
        jsonobject.put("comprehensionTestQuestionList", comprehensionJsonArray);
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - reloadConsentListPage - ERROR", e);
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      if (out != null) {
        out.print(jsonobject);
      }
    }
    logger.exit("reloadComprehensionQuestionListPage() - Ends");
  }

  @RequestMapping("/adminStudies/reloadConsentListPage.do")
  public void reloadConsentListPage(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin reloadConsentListPage()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    ObjectMapper mapper = new ObjectMapper();
    JSONArray consentJsonArray = null;
    List<ConsentInfoBo> consentInfoList = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (sesObj != null) {
        String studyId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        if (StringUtils.isNotEmpty(studyId)) {
          consentInfoList = studyService.getConsentInfoList(studyId);
          if ((consentInfoList != null) && !consentInfoList.isEmpty()) {
            boolean markAsComplete = true;
            consentJsonArray = new JSONArray(mapper.writeValueAsString(consentInfoList));
            if ((consentInfoList != null) && !consentInfoList.isEmpty()) {
              for (ConsentInfoBo conInfoBo : consentInfoList) {
                if (!conInfoBo.getStatus()) {
                  markAsComplete = false;
                  break;
                }
              }
            }
            jsonobject.put("markAsComplete", markAsComplete);
          }
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
        jsonobject.put(FdahpStudyDesignerConstants.CONSENT_INFO_LIST, consentJsonArray);
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - reloadConsentListPage - ERROR", e);
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      if (out != null) {
        out.print(jsonobject);
      }
    }
    logger.exit("reloadConsentListPage() - Ends");
  }

  @RequestMapping("/adminStudies/reloadResourceListPage.do")
  public void reloadResourceListPage(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin reloadResourceListPage()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    ObjectMapper mapper = new ObjectMapper();
    JSONArray resourceJsonArray = null;
    List<ResourceBO> resourceList = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (sesObj != null) {
        String studyId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        if (StringUtils.isNotEmpty(studyId)) {
          resourceList = studyService.getResourceList(studyId);
          if ((resourceList != null) && !resourceList.isEmpty()) {
            boolean markAsComplete = true;
            resourceJsonArray = new JSONArray(mapper.writeValueAsString(resourceList));
            if ((resourceList != null) && !resourceList.isEmpty()) {
              for (ResourceBO resource : resourceList) {
                if (!resource.isStatus()) {
                  markAsComplete = false;
                  break;
                }
              }
            }
            jsonobject.put("markAsComplete", markAsComplete);
          }
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
        jsonobject.put("resourceList", resourceJsonArray);
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - reloadResourceListPage - ERROR", e);
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      if (out != null) {
        out.print(jsonobject);
      }
    }
    logger.exit("reloadResourceListPage() - Ends");
  }

  @RequestMapping("/adminStudies/reOrderComprehensionTestQuestion.do")
  public void reOrderComprehensionTestQuestion(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin reOrderComprehensionTestQuestion()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      int oldOrderNumber;
      int newOrderNumber;
      if (sesObj != null) {
        String studyId =
            (String) request.getSession().getAttribute(FdahpStudyDesignerConstants.STUDY_ID);
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        String oldOrderNo =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.OLD_ORDER_NUMBER))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.OLD_ORDER_NUMBER);
        String newOrderNo =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.NEW_ORDER_NUMBER))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.NEW_ORDER_NUMBER);
        if (((studyId != null) && !studyId.isEmpty())
            && !oldOrderNo.isEmpty()
            && !newOrderNo.isEmpty()) {
          oldOrderNumber = Integer.valueOf(oldOrderNo);
          newOrderNumber = Integer.valueOf(newOrderNo);
          message =
              studyService.reOrderComprehensionTestQuestion(
                  studyId, oldOrderNumber, newOrderNumber);
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - reOrderComprehensionTestQuestion - ERROR", e);
    }
    logger.exit("reOrderComprehensionTestQuestion() - Ends");
  }

  @RequestMapping(value = "/adminStudies/reOrderConsentInfo.do", method = RequestMethod.POST)
  public void reOrderConsentInfo(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin reOrderConsentInfo()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    ObjectMapper mapper = new ObjectMapper();
    JSONArray consentJsonArray = null;
    List<ConsentInfoBo> consentInfoList = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      int oldOrderNumber;
      int newOrderNumber;
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
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        String oldOrderNo =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.OLD_ORDER_NUMBER))
                ? "0"
                : request.getParameter(FdahpStudyDesignerConstants.OLD_ORDER_NUMBER);
        String newOrderNo =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.NEW_ORDER_NUMBER))
                ? "0"
                : request.getParameter(FdahpStudyDesignerConstants.NEW_ORDER_NUMBER);
        if (((studyId != null) && !studyId.isEmpty())
            && !oldOrderNo.isEmpty()
            && !newOrderNo.isEmpty()) {
          oldOrderNumber = Integer.valueOf(oldOrderNo);
          newOrderNumber = Integer.valueOf(newOrderNo);
          message = studyService.reOrderConsentInfoList(studyId, oldOrderNumber, newOrderNumber);
          if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            consentInfoList = studyService.getConsentInfoList(studyId);
            if ((consentInfoList != null) && !consentInfoList.isEmpty()) {
              consentJsonArray = new JSONArray(mapper.writeValueAsString(consentInfoList));
            }
            jsonobject.put(FdahpStudyDesignerConstants.CONSENT_INFO_LIST, consentJsonArray);
          }
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - reOrderConsentInfo - ERROR", e);
    }
    logger.exit("reOrderConsentInfo() - Ends");
  }

  @RequestMapping(value = "/adminStudies/reOrderResourceList.do", method = RequestMethod.POST)
  public void reOrderResourceList(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin reOrderResourceList()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    ObjectMapper mapper = new ObjectMapper();
    JSONArray resourceJsonArray = null;
    List<ResourceBO> resourceList = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      int oldOrderNumber;
      int newOrderNumber;
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
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        String oldOrderNo =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.OLD_ORDER_NUMBER))
                ? "0"
                : request.getParameter(FdahpStudyDesignerConstants.OLD_ORDER_NUMBER);
        String newOrderNo =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.NEW_ORDER_NUMBER))
                ? "0"
                : request.getParameter(FdahpStudyDesignerConstants.NEW_ORDER_NUMBER);
        if (((studyId != null) && !studyId.isEmpty())
            && !oldOrderNo.isEmpty()
            && !newOrderNo.isEmpty()) {
          oldOrderNumber = Integer.valueOf(oldOrderNo);
          newOrderNumber = Integer.valueOf(newOrderNo);
          message = studyService.reOrderResourceList(studyId, oldOrderNumber, newOrderNumber);
          if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            resourceList = studyService.getResourceList(studyId);
            if ((resourceList != null) && !resourceList.isEmpty()) {
              resourceJsonArray = new JSONArray(mapper.writeValueAsString(resourceList));
            }
            jsonobject.put("resourceList", resourceJsonArray);
          }
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - reOrderResourceList - ERROR", e);
    }
    logger.exit("reOrderResourceList() - Ends");
  }

  @RequestMapping(
      value = "/adminStudies/reOrderStudyEligibiltyTestQusAns.do",
      method = RequestMethod.POST)
  public void reOrderStudyEligibiltyTestQusAns(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin reOrderStudyEligibiltyTestQusAns()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String eligibilityId = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      int oldOrderNumber;
      int newOrderNumber;
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
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        eligibilityId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("eligibilityId"))
                ? ""
                : request.getParameter("eligibilityId");
        String oldOrderNo =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.OLD_ORDER_NUMBER))
                ? "0"
                : request.getParameter(FdahpStudyDesignerConstants.OLD_ORDER_NUMBER);
        String newOrderNo =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.NEW_ORDER_NUMBER))
                ? "0"
                : request.getParameter(FdahpStudyDesignerConstants.NEW_ORDER_NUMBER);
        if (((studyId != null) && !studyId.isEmpty())
            && !oldOrderNo.isEmpty()
            && !newOrderNo.isEmpty()) {
          oldOrderNumber = Integer.valueOf(oldOrderNo);
          newOrderNumber = Integer.valueOf(newOrderNo);
          message =
              studyService.reorderEligibilityTestQusAns(
                  eligibilityId, oldOrderNumber, newOrderNumber, studyId);
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - reOrderStudyEligibiltyTestQusAns - ERROR", e);
    }
    logger.exit("reOrderStudyEligibiltyTestQusAns() - Ends");
  }

  @RequestMapping("/resetStudy.do")
  public ModelAndView resetStudy(HttpServletRequest request) {
    logger.entry("resetStudy()");
    ModelAndView mav = new ModelAndView("redirect:login.do");
    boolean flag = false;
    try {
      String cusId =
          FdahpStudyDesignerUtil.isEmpty(request.getParameter("cusId"))
              ? ""
              : request.getParameter("cusId");
      if (!cusId.isEmpty()) {
        flag = studyService.resetDraftStudyByCustomStudyId(cusId);
        if (flag) {
          request.getSession(false).setAttribute("sucMsg", "Reset successfully");
        } else {
          request.getSession(false).setAttribute("errMsg", "DB issue or study does not exist");
        }
      }

    } catch (Exception e) {
      logger.error("StudyController - resetStudy - ERROR", e);
    }
    logger.exit("resetStudy() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/resourceMarkAsCompleted.do")
  public ModelAndView resourceMarkAsCompleted(HttpServletRequest request) {
    logger.entry("begin saveOrUpdateResource()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String customStudyId = "";
    List<ResourceBO> resourceList;
    Boolean isAnchorDateExistsForStudy;
    ModelMap map = new ModelMap();
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
        }
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        auditRequest.setStudyId(customStudyId);
        resourceList = studyService.resourcesWithAnchorDate(studyId);
        if ((resourceList != null) && !resourceList.isEmpty()) {
          isAnchorDateExistsForStudy =
              studyQuestionnaireService.isAnchorDateExistsForStudy(studyId, customStudyId);
          if (isAnchorDateExistsForStudy) {
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        } else {
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
        map.addAttribute("_S", sessionStudyCount);
        if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
          message =
              studyService.markAsCompleted(
                  studyId, FdahpStudyDesignerConstants.RESOURCE, sesObj, customStudyId);
          if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
            StudyBo studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
            auditRequest.setStudyVersion(studyBo.getVersion().toString());
            auditRequest.setAppId(studyBo.getAppId());
            auditLogEventHelper.logEvent(STUDY_RESOURCE_SECTION_MARKED_COMPLETE, auditRequest);
            map.addAttribute("buttonText", FdahpStudyDesignerConstants.COMPLETED_BUTTON);
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            mav = new ModelAndView("redirect:getResourceList.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    FdahpStudyDesignerConstants.UNABLE_TO_MARK_AS_COMPLETE);
            mav = new ModelAndView("redirect:getResourceList.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + "resourceErrMsg",
                  FdahpStudyDesignerConstants.RESOURCE_ANCHOR_ERROR_MSG);
          mav = new ModelAndView("redirect:getResourceList.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - resourceMarkAsCompleted() - ERROR", e);
    }
    logger.exit("resourceMarkAsCompleted() - Ends");
    return mav;
  }

  @RequestMapping(value = "/adminStudies/saveComprehensionTestQuestion.do")
  public void saveComprehensionTestQuestion(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin saveComprehensionTestQuestion()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    ObjectMapper mapper = new ObjectMapper();
    ComprehensionTestQuestionBo comprehensionTestQuestionBo = null;
    String customStudyId = "";
    ComprehensionTestQuestionBo addComprehensionTestQuestionBo = null;
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
        String comprehensionQuestion = request.getParameter("comprehenstionQuestionInfo");
        if (null != comprehensionQuestion) {
          comprehensionTestQuestionBo =
              mapper.readValue(comprehensionQuestion, ComprehensionTestQuestionBo.class);

          if (comprehensionTestQuestionBo != null) {
            if (StringUtils.isNotEmpty(comprehensionTestQuestionBo.getId())) {
              comprehensionTestQuestionBo.setModifiedBy(sesObj.getUserId());
              comprehensionTestQuestionBo.setModifiedOn(
                  FdahpStudyDesignerUtil.getCurrentDateTime());
              comprehensionTestQuestionBo.setStatus(false);
            } else {
              if (comprehensionTestQuestionBo.getStudyId() != null) {
                int order =
                    studyService.comprehensionTestQuestionOrder(
                        comprehensionTestQuestionBo.getStudyId());
                comprehensionTestQuestionBo.setSequenceNo(order);
              }
              comprehensionTestQuestionBo.setCreatedBy(sesObj.getUserId());
              comprehensionTestQuestionBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
              comprehensionTestQuestionBo.setStatus(false);
            }
            addComprehensionTestQuestionBo =
                studyService.saveOrUpdateComprehensionTestQuestion(comprehensionTestQuestionBo);
          }
        }
        if (addComprehensionTestQuestionBo != null) {
          jsonobject.put("questionId", addComprehensionTestQuestionBo.getId());
          message = FdahpStudyDesignerConstants.SUCCESS;
          if (StringUtils.isNotEmpty(studyId)) {
            studyService.markAsCompleted(
                studyId,
                FdahpStudyDesignerConstants.COMPREHENSION_TEST,
                false,
                sesObj,
                customStudyId);
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
    logger.exit("saveComprehensionTestQuestion() - Ends");
  }

  @RequestMapping(value = "/adminStudies/saveConsentInfo.do")
  public void saveConsentInfo(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin saveConsentInfo()");
    String message = FdahpStudyDesignerConstants.FAILURE;
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    ConsentInfoBo addConsentInfoBo = null;
    ObjectMapper mapper = new ObjectMapper();
    ConsentInfoBo consentInfoBo = null;
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      String conInfo = request.getParameter("consentInfo");
      if (null != conInfo) {
        consentInfoBo = mapper.readValue(conInfo, ConsentInfoBo.class);
      }

      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        if (consentInfoBo != null) {
          if ((consentInfoBo.getStudyId() != null)
              && (StringUtils.isEmpty(consentInfoBo.getId()))) {
            int order = studyService.consentInfoOrder(consentInfoBo.getStudyId());
            consentInfoBo.setSequenceNo(order);
          }
          customStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
          addConsentInfoBo =
              studyService.saveOrUpdateConsentInfo(consentInfoBo, sesObj, customStudyId);
          if (addConsentInfoBo != null) {
            jsonobject.put(FdahpStudyDesignerConstants.CONSENT_INFO_ID, addConsentInfoBo.getId());
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - saveConsentInfo - ERROR", e);
    }
    logger.exit("saveConsentInfo() - Ends");
  }

  @RequestMapping("/adminStudies/saveConsentReviewAndEConsentInfo.do")
  public void saveConsentReviewAndEConsentInfo(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin saveConsentReviewAndEConsentInfo()");
    ConsentBo consentBo = null;
    String consentInfoParamName = "";
    ObjectMapper mapper = new ObjectMapper();
    JSONObject jsonobj = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    String studyId = "";
    String consentId = "";
    String customStudyId = "";
    String comprehensionTest = "";
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
        consentInfoParamName = request.getParameter("consentInfo");
        if (StringUtils.isNotEmpty(consentInfoParamName)) {
          consentBo = mapper.readValue(consentInfoParamName, ConsentBo.class);
          if (consentBo != null) {
            customStudyId =
                (String)
                    request
                        .getSession()
                        .getAttribute(
                            sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
            comprehensionTest = consentBo.getComprehensionTest();
            consentBo =
                studyService.saveOrCompleteConsentReviewDetails(consentBo, sesObj, customStudyId);
            studyId =
                StringUtils.isEmpty(String.valueOf(consentBo.getStudyId()))
                    ? ""
                    : String.valueOf(consentBo.getStudyId());
            consentId =
                StringUtils.isEmpty(String.valueOf(consentBo.getId()))
                    ? ""
                    : String.valueOf(consentBo.getId());
            // setting consentId in requestSession
            request
                .getSession()
                .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_ID);
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_ID, consentBo.getId());
            message = FdahpStudyDesignerConstants.SUCCESS;
            if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
              if ((comprehensionTest != null)
                  && comprehensionTest.equalsIgnoreCase(FdahpStudyDesignerConstants.SAVE_BUTTON)) {
                studyService.markAsCompleted(
                    studyId,
                    FdahpStudyDesignerConstants.COMPREHENSION_TEST,
                    false,
                    sesObj,
                    customStudyId);
              }
            }
          }
        }
      }
      jsonobj.put(FdahpStudyDesignerConstants.MESSAGE, message);
      jsonobj.put(FdahpStudyDesignerConstants.STUDY_ID, studyId);
      jsonobj.put(FdahpStudyDesignerConstants.CONSENT_ID, consentId);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobj);
    } catch (Exception e) {
      logger.error("StudyController - saveConsentReviewAndEConsentInfo() - ERROR ", e);
    }
    logger.exit("saveConsentReviewAndEConsentInfo() :: Ends");
  }

  @RequestMapping("/adminStudies/saveOrDoneChecklist.do")
  public ModelAndView saveOrDoneChecklist(HttpServletRequest request, Checklist checklist) {
    logger.entry("begin saveOrDoneChecklist()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    String checklistId = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String customStudyId = "";
    ModelMap map = new ModelMap();
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
        String actionBut =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("actionBut"))
                ? ""
                : request.getParameter("actionBut");
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
        }
        checklist.setStudyId(studyId);
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        checklistId = studyService.saveOrDoneChecklist(checklist, actionBut, sesObj, customStudyId);
        map.addAttribute("_S", sessionStudyCount);
        if (StringUtils.isNotEmpty(checklistId)) {
          if (checklist.getChecklistId() == null) {
            if (("save").equalsIgnoreCase(actionBut)) {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            } else {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      "Checklist successfully added.");
            }
          } else {
            if (("save").equalsIgnoreCase(actionBut)) {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            } else {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      "Checklist successfully updated.");
            }
          }
        } else {
          if (checklist.getChecklistId() == null) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    "Failed to add checklist.");
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    "Failed to update checklist.");
          }
        }
        mav = new ModelAndView("redirect:getChecklist.do", map);
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrDoneChecklist() - ERROR", e);
    }
    logger.exit("saveOrDoneChecklist() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateBasicInfo.do")
  public ModelAndView saveOrUpdateBasicInfo(
      HttpServletRequest request,
      @ModelAttribute(FdahpStudyDesignerConstants.STUDY_BO) StudyBo studyBo) {
    logger.entry("begin saveOrUpdateBasicInfo()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    String fileName = "";
    String file = "";
    String buttonText = "";
    String message = FdahpStudyDesignerConstants.FAILURE;
    ModelMap map = new ModelMap();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      buttonText =
          FdahpStudyDesignerUtil.isEmpty(
                  request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT))
              ? ""
              : request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        if (StringUtils.isEmpty(studyBo.getId())) {
          StudySequenceBo studySequenceBo = new StudySequenceBo();
          studySequenceBo.setBasicInfo(true);
          studyBo.setStudySequenceBo(studySequenceBo);
          studyBo.setStatus(FdahpStudyDesignerConstants.STUDY_PRE_LAUNCH);
        }
        studyBo.setUserId(sesObj.getUserId());

        studyBo.setButtonText(buttonText);
        studyBo.setDescription(StringEscapeUtils.unescapeHtml4(studyBo.getDescription()));

        message = studyService.saveOrUpdateStudy(studyBo, sesObj.getUserId(), sesObj);

        request
            .getSession()
            .setAttribute(
                sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId() + "");
        map.addAttribute("_S", sessionStudyCount);
        if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
          if (StringUtils.isNotEmpty(studyBo.getCustomStudyId())) {
            auditRequest.setStudyId(studyBo.getCustomStudyId());
            auditRequest.setAppId(studyBo.getAppId());
            auditLogEventHelper.logEvent(STUDY_SAVED_IN_DRAFT_STATE, auditRequest);
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID,
                    studyBo.getCustomStudyId());
          }
          if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
            map.addAttribute("buttonText", buttonText);
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewBasicInfo.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewBasicInfo.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Error in set BasicInfo");
          return new ModelAndView("redirect:viewBasicInfo.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrUpdateBasicInfo - ERROR", e);
    }
    logger.exit("saveOrUpdateBasicInfo() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateComprehensionTestQuestion.do")
  public ModelAndView saveOrUpdateComprehensionTestQuestionPage(
      HttpServletRequest request, ComprehensionTestQuestionBo comprehensionTestQuestionBo) {
    logger.entry("begin saveOrUpdateComprehensionTestQuestionPage()");
    ModelAndView mav = new ModelAndView(FdahpStudyDesignerConstants.CONSENT_INFO_LIST_PAGE);
    ComprehensionTestQuestionBo addComprehensionTestQuestionBo = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelMap map = new ModelMap();
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
        int order = 0;
        if (comprehensionTestQuestionBo != null) {
          if (StringUtils.isNotEmpty(comprehensionTestQuestionBo.getId())) {
            comprehensionTestQuestionBo.setModifiedBy(sesObj.getUserId());
            comprehensionTestQuestionBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            comprehensionTestQuestionBo.setStatus(true);
          } else {
            if (comprehensionTestQuestionBo.getStudyId() != null) {
              order =
                  studyService.comprehensionTestQuestionOrder(
                      comprehensionTestQuestionBo.getStudyId());
              comprehensionTestQuestionBo.setSequenceNo(order);
            }
            comprehensionTestQuestionBo.setCreatedBy(sesObj.getUserId());
            comprehensionTestQuestionBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
            comprehensionTestQuestionBo.setStatus(true);
          }
          addComprehensionTestQuestionBo =
              studyService.saveOrUpdateComprehensionTestQuestion(comprehensionTestQuestionBo);
          map.addAttribute("_S", sessionStudyCount);
          if (addComprehensionTestQuestionBo != null) {
            if (StringUtils.isNotEmpty(addComprehensionTestQuestionBo.getId()) && order == 0) {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get("update.comprehensiontest.success.message"));
            } else {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get("save.comprehensiontest.success.message"));
            }
            return new ModelAndView("redirect:/adminStudies/comprehensionQuestionList.do", map);
          } else {
            request
                .getSession()
                .setAttribute(FdahpStudyDesignerConstants.SUC_MSG, "Unable to add Question added");
            return new ModelAndView("redirect:/adminStudies/comprehensionQuestionList.do", map);
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrUpdateComprehensionTestQuestionPage - ERROR", e);
    }
    logger.exit("saveOrUpdateComprehensionTestQuestionPage - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateConsentInfo.do")
  public ModelAndView saveOrUpdateConsentInfo(
      HttpServletRequest request, ConsentInfoBo consentInfoBo) {
    logger.entry("begin saveOrUpdateConsentInfo");
    ModelAndView mav = new ModelAndView(FdahpStudyDesignerConstants.CONSENT_INFO_LIST_PAGE);
    ConsentInfoBo addConsentInfoBo = null;
    ModelMap map = new ModelMap();
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
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
      String briefsummaryText = request.getParameter("briefSummary");
      consentInfoBo.setBriefSummary(briefsummaryText);

      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        if (consentInfoBo != null) {
          if ((consentInfoBo.getStudyId() != null)
              && (StringUtils.isEmpty(consentInfoBo.getId()))) {
            int order = studyService.consentInfoOrder(consentInfoBo.getStudyId());
            consentInfoBo.setSequenceNo(order);
          }
          customStudyId =
              (String)
                  request.getSession().getAttribute(FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
          addConsentInfoBo =
              studyService.saveOrUpdateConsentInfo(consentInfoBo, sesObj, customStudyId);
          StudyBo studyBo =
              studyService.getStudyById(
                  String.valueOf(consentInfoBo.getStudyId()), sesObj.getUserId());
          if (addConsentInfoBo != null) {
            auditRequest.setStudyId(studyBo.getCustomStudyId());
            auditRequest.setStudyVersion(studyBo.getVersion().toString());
            auditRequest.setAppId(studyBo.getAppId());
            auditLogEventHelper.logEvent(STUDY_CONSENT_SECTIONS_SAVED_OR_UPDATED, auditRequest);
            if (StringUtils.isNotEmpty(consentInfoBo.getId())) {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get("update.consent.success.message"));
            } else {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get("save.consent.success.message"));
            }
            map.addAttribute("_S", sessionStudyCount);
            mav = new ModelAndView("redirect:/adminStudies/consentListPage.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    "Consent not added successfully");
            map.addAttribute("_S", sessionStudyCount);
            mav = new ModelAndView("redirect:/adminStudies/consentListPage.do", map);
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrUpdateConsentInfo - ERROR", e);
    }
    logger.exit("saveOrUpdateConsentInfo - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateResource.do")
  public ModelAndView saveOrUpdateResource(HttpServletRequest request, ResourceBO resourceBO) {
    logger.entry("begin saveOrUpdateResource()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    String resourseId = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelMap map = new ModelMap();
    Map<String, String> values = new HashMap<>();
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
        String textOrPdfParam =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("textOrPdfParam"))
                ? ""
                : request.getParameter("textOrPdfParam");
        String resourceVisibilityParam =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("resourceVisibilityParam"))
                ? ""
                : request.getParameter("resourceVisibilityParam");
        String buttonText =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT);
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String studyProtocol =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL);
        String action =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.ACTION_ON))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.ACTION_ON);
        String resourceTypeParm =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("resourceTypeParm"))
                ? ""
                : request.getParameter("resourceTypeParm");
        if (resourceBO != null) {
          StudyBo studyBo = studyService.getStudyInfo(studyId);
          auditRequest.setStudyId(studyBo.getCustomStudyId());
          auditRequest.setStudyVersion(studyBo.getVersion().toString());
          auditRequest.setAppId(studyBo.getAppId());
          if (!("").equals(buttonText)) {
            if (("save").equalsIgnoreCase(buttonText)) {
              resourceBO.setAction(false);
            } else if (("done").equalsIgnoreCase(buttonText)) {
              resourceBO.setAction(true);
            }
          }
          if (!("").equals(studyProtocol)
              && (FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL).equalsIgnoreCase(studyProtocol)) {
            resourceBO.setStudyProtocol(true);
          } else {
            resourceBO.setStudyProtocol(false);
          }
          resourceBO.setStudyId(studyId);
          resourceBO.setTextOrPdf(("0").equals(textOrPdfParam) ? false : true);
          resourceBO.setResourceVisibility(("0").equals(resourceVisibilityParam) ? false : true);
          if (!resourceBO.isResourceVisibility()) {
            resourceBO.setResourceType("0".equals(resourceTypeParm) ? false : true);
          } else {
            resourceBO.setResourceType(false);
          }
          if ((resourceBO.getStudyId() != null)
              && (StringUtils.isEmpty(resourceBO.getId()))
              && !resourceBO.isStudyProtocol()) {
            int order = studyService.resourceOrder(resourceBO.getStudyId());
            resourceBO.setSequenceNo(order);
          }
          resourseId = studyService.saveOrUpdateResource(resourceBO, sesObj);
        }
        if (StringUtils.isNotEmpty(resourseId)) {
          values.put("resource_id", resourseId.toString());
          auditLogEventHelper.logEvent(STUDY_NEW_RESOURCE_CREATED, auditRequest, values);
          if ((resourceBO != null) && (StringUtils.isEmpty(resourceBO.getId()))) {
            if (("save").equalsIgnoreCase(buttonText)) {
              auditLogEventHelper.logEvent(STUDY_RESOURCE_SAVED_OR_UPDATED, auditRequest, values);
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            } else {
              auditLogEventHelper.logEvent(STUDY_RESOURCE_MARKED_COMPLETED, auditRequest, values);
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      "Resource successfully added");
            }
          } else {
            if (("save").equalsIgnoreCase(buttonText)) {
              auditLogEventHelper.logEvent(STUDY_RESOURCE_SAVED_OR_UPDATED, auditRequest, values);
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            } else {
              auditLogEventHelper.logEvent(STUDY_RESOURCE_MARKED_COMPLETED, auditRequest, values);
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      "Resource successfully updated");
            }
          }
        } else {
          if ((resourceBO != null) && (resourceBO.getId() == null)) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    "Failed to add resource");
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    "Failed to update resource");
          }
        }
        map.addAttribute("_S", sessionStudyCount);
        if (("save").equalsIgnoreCase(buttonText)) {
          request
              .getSession()
              .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ACTION_ON, action);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.RESOURCE_INFO_ID,
                  resourseId + "");
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL,
                  studyProtocol + "");
          mav = new ModelAndView("redirect:addOrEditResource.do", map);
        } else {
          mav = new ModelAndView("redirect:getResourceList.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrUpdateResource() - ERROR", e);
    }
    logger.exit("saveOrUpdateResource() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateSettingAndAdmins.do")
  public ModelAndView saveOrUpdateSettingAndAdmins(HttpServletRequest request, StudyBo studyBo) {
    logger.entry("begin saveOrUpdateSettingAndAdmins()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    String message = FdahpStudyDesignerConstants.FAILURE;
    ModelMap map = new ModelMap();
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

        String buttonText =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT);
        studyBo.setButtonText(buttonText);
        studyBo.setUserId(sesObj.getUserId());
        message = studyService.saveOrUpdateStudySettings(studyBo, sesObj);
        request
            .getSession()
            .setAttribute(
                sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID, studyBo.getId() + "");
        map.addAttribute("_S", sessionStudyCount);
        if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
          if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
            map.addAttribute("buttonText", buttonText);
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewSettingAndAdmins.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewSettingAndAdmins.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Error encountered. Your settings could not be saved.");
          return new ModelAndView("redirect:viewSettingAndAdmins.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrUpdateSettingAndAdmins - ERROR", e);
    }
    logger.exit("saveOrUpdateSettingAndAdmins() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateStudyEligibilty.do")
  public ModelAndView saveOrUpdateStudyEligibilty(
      HttpServletRequest request, EligibilityBo eligibilityBo) {
    logger.entry("begin saveOrUpdateStudyEligibilty");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    String result = FdahpStudyDesignerConstants.FAILURE;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
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
        if (eligibilityBo != null) {
          if (StringUtils.isNotEmpty(eligibilityBo.getId())) {
            eligibilityBo.setModifiedBy(sesObj.getUserId());
            eligibilityBo.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          } else {
            eligibilityBo.setCreatedBy(sesObj.getUserId());
            eligibilityBo.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          }
          customStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
          result = studyService.saveOrUpdateStudyEligibilty(eligibilityBo, sesObj, customStudyId);
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID,
                  eligibilityBo.getStudyId() + "");
        }

        auditRequest.setStudyId(customStudyId);
        map.addAttribute("_S", sessionStudyCount);
        if (FdahpStudyDesignerConstants.SUCCESS.equals(result)) {
          StudyBo studyBo =
              studyService.getStudyById(
                  String.valueOf(eligibilityBo.getStudyId()), sesObj.getUserId());
          auditRequest.setStudyVersion(studyBo.getVersion().toString());
          auditRequest.setAppId(studyBo.getAppId());
          if ((eligibilityBo != null) && ("save").equals(eligibilityBo.getActionType())) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            auditLogEventHelper.logEvent(STUDY_ELIGIBILITY_SECTION_SAVED_OR_UPDATED, auditRequest);
            mav = new ModelAndView("redirect:viewStudyEligibilty.do", map);
          } else {
            auditLogEventHelper.logEvent(STUDY_ELIGIBILITY_SECTION_MARKED_COMPLETE, auditRequest);
            map.addAttribute("buttonText", FdahpStudyDesignerConstants.COMPLETED_BUTTON);
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            mav = new ModelAndView("redirect:viewStudyEligibilty.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Error in set Eligibility");
          mav = new ModelAndView("redirect:viewStudyEligibilty.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrUpdateStudyEligibilty - ERROR", e);
    }
    logger.exit("saveOrUpdateStudyEligibilty - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateStudyEligibiltyTestQusAns.do")
  public ModelAndView saveOrUpdateStudyEligibiltyTestQusAns(
      HttpServletRequest request, EligibilityTestBo eligibilityTestBo) {
    logger.entry("begin saveOrUpdateStudyEligibiltyTestQusAns");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    String result = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String customStudyId = "";
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      String studyId =
          (String)
              request
                  .getSession()
                  .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
      String actionTypeForQuestionPage =
          StringUtils.isNotBlank(request.getParameter("actionTypeForQuestionPage"))
              ? request.getParameter("actionTypeForQuestionPage")
              : "";
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        int seqCount = 0;
        if (eligibilityTestBo != null) {
          if (StringUtils.isNotEmpty(eligibilityTestBo.getId())) {
            seqCount = eligibilityTestBo.getSequenceNo();
          }
          customStudyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(
                          sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
          result =
              studyService.saveOrUpdateEligibilityTestQusAns(
                  eligibilityTestBo, studyId, sesObj, customStudyId);
        }
        map.addAttribute("_S", sessionStudyCount);
        if (StringUtils.isNotEmpty(result)) {
          if ((eligibilityTestBo != null)
              && (FdahpStudyDesignerConstants.ACTION_TYPE_SAVE)
                  .equals(eligibilityTestBo.getType())) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + "actionTypeForQuestionPage", actionTypeForQuestionPage);
            request
                .getSession()
                .setAttribute(sessionStudyCount + "eligibilityTestId", eligibilityTestBo.getId());
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + "eligibilityId", eligibilityTestBo.getEligibilityId());
            mav = new ModelAndView("redirect:viewStudyEligibiltyTestQusAns.do", map);
          } else if ((eligibilityTestBo != null)
              && (FdahpStudyDesignerConstants.ACTION_TYPE_COMPLETE)
                  .equals(eligibilityTestBo.getType())
              && seqCount != 0) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get("update.eligibilitytest.success.message"));
            mav = new ModelAndView("redirect:viewStudyEligibilty.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get("save.eligibilitytest.success.message"));
            mav = new ModelAndView("redirect:viewStudyEligibilty.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Error in set Eligibility Questions");
          mav = new ModelAndView("redirect:viewStudyEligibilty.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrUpdateStudyEligibiltyTestQusAns - ERROR", e);
    }
    logger.exit("saveOrUpdateStudyEligibiltyTestQusAns - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateStudyNotification.do")
  public ModelAndView saveOrUpdateStudyNotification(
      HttpServletRequest request, NotificationBO notificationBO) {
    logger.entry("begin saveOrUpdateStudyNotification");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    String notificationId = null;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String customStudyId = "";

    try {
      HttpSession session = request.getSession();
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      SessionObject sessionObject =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if ((sessionObject != null)
          && (sessionObject.getStudySession() != null)
          && sessionObject.getStudySession().contains(sessionStudyCount)) {
        String notificationType = "Study level";
        String currentDateTime =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("currentDateTime"))
                ? ""
                : request.getParameter("currentDateTime");
        String buttonType =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("buttonType"))
                ? ""
                : request.getParameter("buttonType");
        String actionPage =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ACTION_PAGE);
        String appId1 = (String) request.getAttribute("appId");
        customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        if (StringUtils.isEmpty(actionPage)) {
          actionPage =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.ACTION_PAGE))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.ACTION_PAGE);
        }
        if (notificationBO != null) {
          if (!"".equals(buttonType)) {
            if ("save".equalsIgnoreCase(buttonType)) {
              notificationBO.setNotificationDone(false);
              notificationBO.setNotificationAction(false);
            } else if ("done".equalsIgnoreCase(buttonType)
                || FdahpStudyDesignerConstants.RESEND.equalsIgnoreCase(buttonType)) {
              notificationBO.setNotificationDone(true);
              notificationBO.setNotificationAction(true);
            }
          }
          if (FdahpStudyDesignerConstants.NOTIFICATION_NOTIMMEDIATE.equals(currentDateTime)) {
            notificationBO.setScheduleDate(
                FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleDate())
                    ? String.valueOf(
                        FdahpStudyDesignerUtil.getFormattedDate(
                            notificationBO.getScheduleDate(),
                            FdahpStudyDesignerConstants.UI_SDF_DATE,
                            FdahpStudyDesignerConstants.DB_SDF_DATE))
                    : "");
            notificationBO.setScheduleTime(
                FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleTime())
                    ? String.valueOf(
                        FdahpStudyDesignerUtil.getFormattedDate(
                            notificationBO.getScheduleTime(),
                            FdahpStudyDesignerConstants.SDF_TIME,
                            FdahpStudyDesignerConstants.DB_SDF_TIME))
                    : "");

            notificationBO.setScheduleTimestamp(
                (FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleDate())
                        && FdahpStudyDesignerUtil.isNotEmpty(notificationBO.getScheduleTime()))
                    ? FdahpStudyDesignerUtil.getTimeStamp(
                        notificationBO.getScheduleDate(), notificationBO.getScheduleTime())
                    : null);
            notificationBO.setNotificationScheduleType(
                FdahpStudyDesignerConstants.NOTIFICATION_NOTIMMEDIATE);
          } else if (FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE.equals(currentDateTime)) {
            notificationBO.setScheduleDate(FdahpStudyDesignerUtil.getCurrentDate());
            notificationBO.setScheduleTime(FdahpStudyDesignerUtil.getCurrentTime());
            notificationBO.setScheduleTimestamp(
                FdahpStudyDesignerUtil.getTimeStamp(
                    notificationBO.getScheduleDate(), notificationBO.getScheduleTime()));

            notificationBO.setNotificationScheduleType(
                FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE);
          } else {
            notificationBO.setScheduleDate("");
            notificationBO.setScheduleTime("");
            notificationBO.setScheduleTimestamp(null);
            notificationBO.setNotificationScheduleType("0");
          }
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
          }
          if (StringUtils.isNotEmpty(studyId)) {
            StudyBo studyBo = studyService.getStudyById(studyId, null);
            if (studyBo != null) {
              notificationBO.setCustomStudyId(studyBo.getCustomStudyId());
              notificationBO.setStudyId(studyId);
            }
          }
          if (StringUtils.isEmpty(notificationBO.getNotificationId())) {
            notificationBO.setCreatedBy(sessionObject.getUserId());
            notificationBO.setCreatedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          } else {
            notificationBO.setModifiedBy(sessionObject.getUserId());
            notificationBO.setModifiedOn(FdahpStudyDesignerUtil.getCurrentDateTime());
          }
          notificationId =
              notificationService.saveOrUpdateOrResendNotification(
                  notificationBO, notificationType, buttonType, sessionObject, customStudyId);
        }
        if (StringUtils.isNotEmpty(notificationId)) {
          if (StringUtils.isEmpty(notificationBO.getNotificationId())) {
            if ("save".equalsIgnoreCase(buttonType)) {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            } else {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get("save.notification.success.message"));
            }
          } else {
            if ("save".equalsIgnoreCase(buttonType)) {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            } else if (FdahpStudyDesignerConstants.RESEND.equalsIgnoreCase(buttonType)) {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get("resend.notification.success.message"));
            } else {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                      propMap.get("update.notification.success.message"));
            }
          }
        } else {
          if ("save".equalsIgnoreCase(buttonType) && (notificationBO.getNotificationId() == null)) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    propMap.get("save.notification.error.message"));
          } else if (FdahpStudyDesignerConstants.RESEND.equalsIgnoreCase(buttonType)) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    propMap.get("resend.notification.error.message"));
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                    propMap.get("update.notification.error.message"));
          }
        }
        map.addAttribute("_S", sessionStudyCount);
        if ("save".equalsIgnoreCase(buttonType)
            && !FdahpStudyDesignerConstants.ADDORCOPY.equals(actionPage)) {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.NOTIFICATIONID,
                  notificationId + "");
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.CHKREFRESHFLAG, "Y" + "");
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ACTION_TYPE, "edit" + "");
          mav = new ModelAndView("redirect:getStudyNotification.do", map);
        } else if ("save".equalsIgnoreCase(buttonType)
            && FdahpStudyDesignerConstants.ADDORCOPY.equals(actionPage)) {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.NOTIFICATIONID,
                  notificationId + "");
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.CHKREFRESHFLAG, "Y" + "");
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ACTION_TYPE,
                  FdahpStudyDesignerConstants.ADDORCOPY + "");
          mav = new ModelAndView("redirect:getStudyNotification.do", map);
        } else {
          mav = new ModelAndView("redirect:/adminStudies/viewStudyNotificationList.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrUpdateStudyNotification - ERROR", e);
    }
    logger.exit("saveOrUpdateStudyNotification - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/saveOrUpdateStudyOverviewPage.do")
  public ModelAndView saveOrUpdateStudyOverviewPage(
      HttpServletRequest request, StudyPageBean studyPageBean) {
    logger.entry("begin saveOrUpdateStudyOverviewPage");

    if (request instanceof MultipartHttpServletRequest) {
      MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
      Map<String, String[]> m = multipartRequest.getParameterMap();
      for (Map.Entry<String, String[]> entry : m.entrySet()) {
        if (entry.getKey().equals("description")) {
          String[] descriptions = entry.getValue();
          studyPageBean.setDescription(descriptions);
        }
      }
    }
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String message = FdahpStudyDesignerConstants.FAILURE;
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      String buttonText = studyPageBean.getActionType();
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        studyPageBean.setUserId(sesObj.getUserId());
        message = studyService.saveOrUpdateOverviewStudyPages(studyPageBean, sesObj);
        map.addAttribute("_S", sessionStudyCount);
        if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
          if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
            map.addAttribute("buttonText", buttonText);
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:overviewStudyPages.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:overviewStudyPages.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Error in setting Overview");
          return new ModelAndView("redirect:overviewStudyPages.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - saveOrUpdateStudyOverviewPage - ERROR", e);
    }
    logger.exit("saveOrUpdateStudyOverviewPage - Ends");
    return mav;
  }

  @RequestMapping(value = "/adminStudies/studyPlatformValidation", method = RequestMethod.POST)
  public void studyPlatformValidation(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin studyPlatformValidation()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    String errorMessage = "";
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
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
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
        message =
            studyQuestionnaireService.checkQuestionnaireResponseTypeValidation(
                studyId, customStudyId);
        if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
          errorMessage = FdahpStudyDesignerConstants.PLATFORM_ERROR_MSG_ANDROID;
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      jsonobject.put("errorMessage", errorMessage);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - studyPlatformValidation() - ERROR", e);
    }
    logger.exit("studyPlatformValidation() - Ends");
  }

  @RequestMapping(
      value = "/adminStudies/studyPlatformValidationforActiveTask.do",
      method = RequestMethod.POST)
  public void studyPlatformValidationforActiveTask(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin studyPlatformValidationforActiveTask()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    String errorMessage = "";
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
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        message = studyService.checkActiveTaskTypeValidation(studyId);
        if (message.equals(FdahpStudyDesignerConstants.SUCCESS)) {
          errorMessage = FdahpStudyDesignerConstants.PLATFORM_ACTIVETASK_ERROR_MSG_ANDROID;
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      jsonobject.put("errorMessage", errorMessage);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - studyPlatformValidationforActiveTask() - ERROR", e);
    }
    logger.exit("studyPlatformValidationforActiveTask() - Ends");
  }

  @RequestMapping(value = "/adminStudies/updateStudyAction.do", method = RequestMethod.POST)
  public ModelAndView updateStudyActionOnAction(
      HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin updateStudyActionOnAction()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    String successMessage = "";
    StudyDetailsBean studyDetails = null;
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
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        String customStudyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID);
        String buttonText =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT);
        if (StringUtils.isNotEmpty(studyId) && StringUtils.isNotEmpty(buttonText)) {
          message = studyService.updateStudyActionOnAction(studyId, buttonText, sesObj);
          if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
            if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)) {
              successMessage = FdahpStudyDesignerConstants.ACTION_LUNCH_SUCCESS_MSG;
              submitResponseToUserRegistrationServer(customStudyId, request);
              submitResponseToResponseServer(customStudyId, request);
            } else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UPDATES)) {
              successMessage = FdahpStudyDesignerConstants.ACTION_UPDATES_SUCCESS_MSG;
              submitResponseToUserRegistrationServer(customStudyId, request);
              submitResponseToResponseServer(customStudyId, request);
            } else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_RESUME)) {
              successMessage = FdahpStudyDesignerConstants.ACTION_RESUME_SUCCESS_MSG;
              submitResponseToUserRegistrationServer(customStudyId, request);
              submitResponseToResponseServer(customStudyId, request);
            } else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_PAUSE)) {
              successMessage = FdahpStudyDesignerConstants.ACTION_PAUSE_SUCCESS_MSG;
              submitResponseToUserRegistrationServer(customStudyId, request);
              submitResponseToResponseServer(customStudyId, request);
            } else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_DEACTIVATE)) {
              successMessage = FdahpStudyDesignerConstants.ACTION_DEACTIVATE_SUCCESS_MSG;
              submitResponseToUserRegistrationServer(customStudyId, request);
              submitResponseToResponseServer(customStudyId, request);
            }
            if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_DEACTIVATE)
                || buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)
                || buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UPDATES)) {
              request
                  .getSession()
                  .setAttribute(FdahpStudyDesignerConstants.ACTION_SUC_MSG, successMessage);
            } else {
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.ACTION_SUC_MSG,
                      successMessage);
            }
          } else {
            if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.FAILURE)) {
              request
                  .getSession()
                  .setAttribute(
                      FdahpStudyDesignerConstants.ERR_MSG,
                      FdahpStudyDesignerConstants.FAILURE_UPDATE_STUDY_MESSAGE);
            }
          }
        }
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - updateStudyActionOnAction() - ERROR", e);
    }
    logger.exit("updateStudyActionOnAction() - Ends");
    return null;
  }

  @RequestMapping(
      value = "/adminStudies/validateEligibilityTestKey.do",
      method = RequestMethod.POST)
  public void validateEligibilityTestKey(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin validateEligibilityTestKey()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.FAILURE;
    String eligibilityTestId;
    String eligibilityId;
    String shortTitle;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      eligibilityTestId =
          StringUtils.isNotEmpty(request.getParameter("eligibilityTestId"))
              ? request.getParameter("eligibilityTestId")
              : "";
      shortTitle =
          StringUtils.isNotBlank(request.getParameter("shortTitle"))
              ? request.getParameter("shortTitle")
              : "";
      eligibilityId =
          StringUtils.isNotEmpty(request.getParameter("eligibilityId"))
              ? request.getParameter("eligibilityId")
              : "";
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionStudyCount)) {
        message =
            studyService.validateEligibilityTestKey(eligibilityTestId, shortTitle, eligibilityId);
      }
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("StudyController - validateEligibilityTestKey() - ERROR", e);
    }
    logger.exit("validateEligibilityTestKey() - Ends");
  }

  @RequestMapping(value = "/adminStudies/validateStudyAction.do", method = RequestMethod.POST)
  public void validateStudyAction(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    logger.entry("StudyActiveTasksController - validateStudyAction()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out;
    String message = FdahpStudyDesignerConstants.FAILURE;
    try {
      HttpSession session = request.getSession();
      SessionObject userSession =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((userSession != null)
          && (userSession.getStudySession() != null)
          && userSession.getStudySession().contains(sessionStudyCount)) {
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
        }
        String buttonText =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT);
        if (StringUtils.isNotEmpty(buttonText)) {
          // validation and success/error message should send to
          // actionListPAge
          if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)
              || buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_UPDATES)) {
            message = studyService.validateStudyAction(studyId, buttonText);
          } else {
            message = FdahpStudyDesignerConstants.SUCCESS;
          }
        }
      }
    } catch (Exception e) {
      logger.error("StudyActiveTasksController - validateStudyAction() - ERROR ", e);
    }
    logger.exit("StudyActiveTasksController - validateStudyAction() - Ends ");
    jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
    response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
    out = response.getWriter();
    out.print(jsonobject);
  }

  @RequestMapping(value = "/adminStudies/validateStudyId.do", method = RequestMethod.POST)
  public void validateStudyId(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    logger.entry("begin validateStudyId()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out;
    String message = FdahpStudyDesignerConstants.FAILURE;
    boolean flag = false;
    try {
      HttpSession session = request.getSession();
      SessionObject userSession =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (userSession != null) {
        String customStudyId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("customStudyId"))
                ? ""
                : request.getParameter("customStudyId");
        flag = studyService.validateStudyId(customStudyId);
        if (flag) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - validateStudyId() - ERROR ", e);
    }
    logger.exit("validateStudyId() - Ends ");
    jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
    response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
    out = response.getWriter();
    out.print(jsonobject);
  }

  @RequestMapping("/adminStudies/viewBasicInfo.do")
  public ModelAndView viewBasicInfo(HttpServletRequest request) {
    logger.entry("begin viewBasicInfo");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    HashMap<String, List<ReferenceTablesBo>> referenceMap = null;
    List<ReferenceTablesBo> categoryList = null;
    StudyBo studyBo = null;
    String sucMsg = "";
    String errMsg = "";
    ConsentBo consentBo = null;
    StudyIdBean studyIdBean = null;
    Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();
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
        if (null != request.getSession().getAttribute("sucMsgViewAssocStudies")) {
          request.getSession().removeAttribute("sucMsgViewAssocStudies");
        }
        AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
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
                (FdahpStudyDesignerUtil.isEmpty(
                        (String)
                            request
                                .getSession()
                                .getAttribute(
                                    sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID))
                    ? ""
                    : request
                        .getSession()
                        .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID));
        String permission =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String)
                            request
                                .getSession()
                                .getAttribute(
                                    sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION))
                    ? ""
                    : request
                        .getSession()
                        .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION));
        String isLive =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String)
                            request
                                .getSession()
                                .getAttribute(
                                    sessionStudyCount + FdahpStudyDesignerConstants.IS_LIVE))
                    ? ""
                    : request
                        .getSession()
                        .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.IS_LIVE));

        if (FdahpStudyDesignerUtil.isEmpty(isLive)) {
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.IS_LIVE);
        }

        if (FdahpStudyDesignerUtil.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          if (StringUtils.isNotEmpty(isLive)
              && isLive.equalsIgnoreCase(FdahpStudyDesignerConstants.YES)
              && (studyBo != null)) {
            studyIdBean = studyService.getLiveVersion(studyBo.getCustomStudyId());
            if (studyIdBean != null) {
              consentBo =
                  studyService.getConsentDetailsByStudyId(
                      studyIdBean.getConsentStudyId().toString());
              request
                  .getSession()
                  .setAttribute(
                      sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_STUDY_ID,
                      studyIdBean.getConsentStudyId().toString());
              if (studyIdBean.getActivetaskStudyId() != null) {
                request
                    .getSession()
                    .setAttribute(
                        sessionStudyCount + FdahpStudyDesignerConstants.ACTIVE_TASK_STUDY_ID,
                        studyIdBean.getActivetaskStudyId().toString());
              }
              if (studyIdBean.getQuestionnarieStudyId() != null) {
                request
                    .getSession()
                    .setAttribute(
                        sessionStudyCount + FdahpStudyDesignerConstants.QUESTIONNARIE_STUDY_ID,
                        studyIdBean.getQuestionnarieStudyId().toString());
              }
            }
          } else {
            consentBo = studyService.getConsentDetailsByStudyId(studyId);
          }
          // get consentId if exists for studyId
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_ID);
          if (consentBo != null) {
            request
                .getSession()
                .setAttribute(
                    sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_ID, consentBo.getId());
          } else {
            request
                .getSession()
                .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.CONSENT_ID);
          }
        }
        if (studyBo == null) {
          auditLogEventHelper.logEvent(NEW_STUDY_CREATION_INITIATED, auditRequest);
          studyBo = new StudyBo();
          studyBo.setType(FdahpStudyDesignerConstants.STUDY_TYPE_GT);
        } else if ((studyBo != null) && StringUtils.isNotEmpty(studyBo.getCustomStudyId())) {
          request
              .getSession()
              .setAttribute(
                  sessionStudyCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID,
                  studyBo.getCustomStudyId());

          /*FdahpStudyDesignerUtil.copyImage(
          studyBo.getThumbnailImage(),
          FdahpStudyDesignerConstants.STUDTYLOGO,
          studyBo.getCustomStudyId());*/

          map.addAttribute(
              "signedUrl",
              FdahpStudyDesignerUtil.getImageResources(
                  FdahpStudyDesignerConstants.STUDIES
                      + FdahpStudyDesignerConstants.PATH_SEPARATOR
                      + studyBo.getCustomStudyId()
                      + FdahpStudyDesignerConstants.PATH_SEPARATOR
                      + FdahpStudyDesignerConstants.STUDTYLOGO
                      + FdahpStudyDesignerConstants.PATH_SEPARATOR
                      + studyBo.getThumbnailImage()));
        } else if (StringUtils.isEmpty(studyBo.getCustomStudyId())
            && StringUtils.isNotEmpty(studyBo.getDestinationCustomStudyId())) {

          if (studyBo.getDestinationCustomStudyId().contains("@")) {
            String[] copyCustomIdArray = studyBo.getDestinationCustomStudyId().split("@");
            String customId = "";
            if (copyCustomIdArray[1].contains("COPY")) {
              customId = copyCustomIdArray[0];
              studyBo.setDestinationCustomStudyId(customId);
            } else if (copyCustomIdArray[1].equalsIgnoreCase("EXPORT")) {
              customId = copyCustomIdArray[0];
              studyBo.setDestinationCustomStudyId(customId + "@Export");
            }
          }

          map.addAttribute(
              "signedUrl",
              FdahpStudyDesignerUtil.getImageResources(
                  FdahpStudyDesignerConstants.STUDIES
                      + FdahpStudyDesignerConstants.PATH_SEPARATOR
                      + studyBo.getDestinationCustomStudyId()
                      + FdahpStudyDesignerConstants.PATH_SEPARATOR
                      + FdahpStudyDesignerConstants.STUDTYLOGO
                      + FdahpStudyDesignerConstants.PATH_SEPARATOR
                      + studyBo.getThumbnailImage()));
        }
        map.addAttribute(
            "defaultImageSignedUrl",
            FdahpStudyDesignerUtil.getImageResources(
                FdahpStudyDesignerConstants.DEFAULT_IMAGES
                    + "/"
                    + configMap.get("study.basicInformation.defaultImage")));
        // grouped for Study category , Research Sponsors , Data partner
        referenceMap =
            (HashMap<String, List<ReferenceTablesBo>>) studyService.getreferenceListByCategory();
        if ((referenceMap != null) && (referenceMap.size() > 0)) {
          for (String key : referenceMap.keySet()) {
            if (StringUtils.isNotEmpty(key)) {
              switch (key) {
                case FdahpStudyDesignerConstants.REFERENCE_TYPE_CATEGORIES:
                  categoryList = referenceMap.get(key);
                  break;
                default:
                  break;
              }
            }
          }
        }

        List<AppsBo> apps = appService.getAppsForStudy(sesObj.getUserId());
        AppsBo app = appService.getAppbyCustomAppId(studyBo.getAppId());

        if (app != null) {
          map.addAttribute("appName", app.getName());
          map.addAttribute("appType", app.getType());
          if (app.getType().equals("SD")) {
            apps.add(app);
          }
          boolean appPermission = appService.getAppPermission(app.getId(), sesObj.getUserId());
          map.addAttribute("appPermission", appPermission);
        }
        map.addAttribute("categoryList", categoryList);
        map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
        map.addAttribute("createStudyId", "true");
        map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
        map.addAttribute("_S", sessionStudyCount);
        map.addAttribute("appsList", apps);
        mav = new ModelAndView("viewBasicInfo", map);
      }
    } catch (Exception e) {
      logger.error("StudyController - viewBasicInfo - ERROR", e);
    }
    logger.exit("viewBasicInfo - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/viewSettingAndAdmins.do")
  public ModelAndView viewSettingAndAdmins(HttpServletRequest request) {
    logger.entry("begin viewSettingAndAdmins");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    StudyBo studyBo = null;
    AppsBo appBo = null;
    String sucMsg = "";
    String errMsg = "";
    String user = "";
    boolean isAnchorForEnrollmentLive = false;
    boolean isAnchorForEnrollmentDraft = false;
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
        String studyId =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        if (FdahpStudyDesignerUtil.isEmpty(studyId)) {
          studyId =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        }
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        map.addAttribute("_S", sessionStudyCount);
        user =
            (String)
                request
                    .getSession()
                    .getAttribute(
                        sessionStudyCount + FdahpStudyDesignerConstants.LOGOUT_LOGIN_USER);
        if (FdahpStudyDesignerUtil.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          appBo = appService.getAppbyCustomAppId(studyBo.getAppId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
          map.addAttribute("user", user);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.LOGOUT_LOGIN_USER);
          isAnchorForEnrollmentLive =
              studyService.isAnchorDateExistForEnrollment(
                  studyBo.getId(), studyBo.getCustomStudyId());
          isAnchorForEnrollmentDraft =
              studyService.isAnchorDateExistForEnrollmentDraftStudy(
                  studyBo.getId(), studyBo.getCustomStudyId());
          map.addAttribute("isAnchorForEnrollmentLive", isAnchorForEnrollmentLive);
          map.addAttribute("isAnchorForEnrollmentDraft", isAnchorForEnrollmentDraft);
          map.addAttribute("appBo", appBo);

          mav = new ModelAndView(FdahpStudyDesignerConstants.VIEW_SETTING_AND_ADMINS, map);
        } else {
          return new ModelAndView("redirect:studyList.do");
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - viewSettingAndAdmins - ERROR", e);
    }
    logger.exit("viewSettingAndAdmins - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/viewStudyDetails.do")
  public ModelAndView viewStudyDetails(HttpServletRequest request) {
    Integer sessionStudyCount;
    ModelMap map = new ModelMap();
    StudyBuilderAuditEvent eventEnum = null;
    ModelAndView modelAndView = new ModelAndView("redirect:/adminStudies/studyList.do");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    String studyId =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
            ? ""
            : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
    String permission =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter(FdahpStudyDesignerConstants.PERMISSION))
            ? ""
            : request.getParameter(FdahpStudyDesignerConstants.PERMISSION);
    String isLive =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter(FdahpStudyDesignerConstants.IS_LIVE))
            ? ""
            : request.getParameter(FdahpStudyDesignerConstants.IS_LIVE);
    SessionObject sesObj =
        (SessionObject)
            request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
    List<Integer> studySessionList = new ArrayList<>();
    List<StudySessionBean> studySessionBeans = new ArrayList<>();
    StudySessionBean studySessionBean = null;
    try {
      sessionStudyCount =
          (Integer)
              (request.getSession().getAttribute("sessionStudyCount") != null
                  ? request.getSession().getAttribute("sessionStudyCount")
                  : 0);
      if (sesObj != null) {
        StudyBo studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
        if ((sesObj.getStudySessionBeans() != null) && !sesObj.getStudySessionBeans().isEmpty()) {
          for (StudySessionBean sessionBean : sesObj.getStudySessionBeans()) {
            if ((sessionBean != null)
                && sessionBean.getPermission().equals(permission)
                && sessionBean.getIsLive().equals(isLive)
                && sessionBean.getStudyId().equals(studyId)) {
              studySessionBean = sessionBean;
            }
          }
        }
        if (studySessionBean != null) {
          sessionStudyCount = studySessionBean.getSessionStudyCount();
        } else {
          ++sessionStudyCount;
          if ((sesObj.getStudySession() != null) && !sesObj.getStudySession().isEmpty()) {
            studySessionList.addAll(sesObj.getStudySession());
          }
          studySessionList.add(sessionStudyCount);
          sesObj.setStudySession(studySessionList);

          if ((sesObj.getStudySessionBeans() != null) && !sesObj.getStudySessionBeans().isEmpty()) {
            studySessionBeans.addAll(sesObj.getStudySessionBeans());
          }
          studySessionBean = new StudySessionBean();
          studySessionBean.setIsLive(isLive);
          studySessionBean.setPermission(permission);
          studySessionBean.setSessionStudyCount(sessionStudyCount);
          studySessionBean.setStudyId(studyId);
          studySessionBeans.add(studySessionBean);
          sesObj.setStudySessionBeans(studySessionBeans);
          if (permission.equalsIgnoreCase("View")) {
            auditRequest.setStudyId(studyBo.getCustomStudyId());
            auditRequest.setStudyVersion(studyBo.getVersion().toString());
            auditRequest.setAppId(studyBo.getAppId());
            if (!isLive.isEmpty() && !studyId.isEmpty()) {
              eventEnum = LAST_PUBLISHED_VERSION_OF_STUDY_VIEWED;
            } else {
              eventEnum = STUDY_VIEWED;
            }
          } else {
            if (studyBo != null) {
              auditRequest.setStudyId(studyBo.getCustomStudyId());
              auditRequest.setStudyVersion(studyBo.getVersion().toString());
              auditRequest.setAppId(studyBo.getAppId());
            }
            eventEnum = STUDY_ACCESSED_IN_EDIT_MODE;
          }
          auditLogEventHelper.logEvent(eventEnum, auditRequest);
        }
      }

      map.addAttribute("_S", sessionStudyCount);
      request.getSession().setAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT, sesObj);
      request.getSession().setAttribute("sessionStudyCount", sessionStudyCount);
      request
          .getSession()
          .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID, studyId);
      request
          .getSession()
          .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION, permission);
      request
          .getSession()
          .setAttribute(sessionStudyCount + FdahpStudyDesignerConstants.IS_LIVE, isLive);

      modelAndView = new ModelAndView("redirect:/adminStudies/viewBasicInfo.do", map);
    } catch (Exception e) {
      logger.error("StudyController - viewStudyDetails - ERROR", e);
    }
    return modelAndView;
  }

  @RequestMapping("/adminStudies/viewStudyEligibilty.do")
  public ModelAndView viewStudyEligibilty(HttpServletRequest request) {
    logger.entry("begin viewStudyEligibilty()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    StudyBo studyBo = null, liveStudyBo = null;
    String sucMsg = "";
    String errMsg = "";
    EligibilityBo eligibilityBo;
    List<EligibilityTestBo> eligibilityTestList = new ArrayList<>();
    Boolean isLiveStudy = false;
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
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);

        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? "0"
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        if (StringUtils.isNotEmpty(studyId)) {
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          eligibilityBo = studyService.getStudyEligibiltyByStudyId(studyId);
          liveStudyBo = studyService.getStudyLiveStatusByCustomId(studyBo.getCustomStudyId());
          if (liveStudyBo != null) {
            isLiveStudy = true;
          }
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          if (eligibilityBo == null) {
            eligibilityBo = new EligibilityBo();
            eligibilityBo.setStudyId(studyId);
            eligibilityBo.setInstructionalText(
                FdahpStudyDesignerConstants.ELIGIBILITY_TOKEN_TEXT_DEFAULT);
          }
          if (eligibilityBo.getId() != null) {
            eligibilityTestList =
                studyService.viewEligibilityTestQusAnsByEligibilityId(eligibilityBo.getId());
          }
          map.addAttribute("eligibilityTestList", eligibilityTestList);
          map.addAttribute("eligibility", eligibilityBo);
          map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
          map.addAttribute("_S", sessionStudyCount);
          map.addAttribute("liveStatus", isLiveStudy);
          mav = new ModelAndView("studyEligibiltyPage", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - viewStudyEligibilty - ERROR", e);
    }
    logger.exit("viewStudyEligibilty() - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/viewStudyEligibiltyTestQusAns.do")
  public ModelAndView viewStudyEligibiltyTestQusAns(HttpServletRequest request) {
    logger.entry("begin viewStudyEligibiltyTestQusAns");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    String sucMsg = "";
    String errMsg = "";
    EligibilityTestBo eligibilityTest = null;
    StudyBo studyBo;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      String actionTypeForQuestionPage =
          StringUtils.isNotBlank(request.getParameter("actionTypeForQuestionPage"))
              ? request.getParameter("actionTypeForQuestionPage")
              : "";
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

        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String eligibilityTestId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("eligibilityTestId"))
                ? ""
                : request.getParameter("eligibilityTestId");
        String eligibilityId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("eligibilityId"))
                ? ""
                : request.getParameter("eligibilityId");
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? "0"
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        if (StringUtils.isBlank(actionTypeForQuestionPage)) {
          actionTypeForQuestionPage =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionStudyCount + "actionTypeForQuestionPage");
          request.getSession().removeAttribute(sessionStudyCount + "actionTypeForQuestionPage");
        }
        if (eligibilityTestId.equals("")) {
          eligibilityTestId =
              (String) request.getSession().getAttribute(sessionStudyCount + "eligibilityTestId");
          request.getSession().removeAttribute(sessionStudyCount + "eligibilityTestId");
        }
        if (eligibilityId.equals("")) {
          eligibilityId =
              (String) request.getSession().getAttribute(sessionStudyCount + "eligibilityId");
          request.getSession().removeAttribute(sessionStudyCount + "eligibilityId");
        }
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
        map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
        if (StringUtils.isNotBlank(studyId) && StringUtils.isNotBlank(actionTypeForQuestionPage)) {
          if (eligibilityTestId != null) {
            eligibilityTest = studyService.viewEligibilityTestQusAnsById(eligibilityTestId);
          }
          map.addAttribute("eligibilityTest", eligibilityTest);
          map.addAttribute("eligibilityId", eligibilityId);
          map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
          map.addAttribute("_S", sessionStudyCount);
          map.addAttribute("actionTypeForQuestionPage", actionTypeForQuestionPage);
          mav = new ModelAndView("studyEligibiltyTestPage", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - viewStudyEligibiltyTestQusAns - ERROR", e);
    }
    logger.exit("viewStudyEligibiltyTestQusAns - Ends");
    return mav;
  }

  @RequestMapping("/adminStudies/viewStudyNotificationList.do")
  public ModelAndView viewStudyNotificationList(HttpServletRequest request) {
    logger.entry("begin viewNotificationList()");
    ModelMap map = new ModelMap();
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    String sucMsg = "";
    String errMsg = "";
    List<NotificationBO> notificationList = null;
    List<NotificationBO> notificationSavedList = null;
    StudyBo studyBo = null;
    StudyBo studyLive = null;
    try {
      HttpSession session = request.getSession();
      SessionObject sessionObject =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionStudyCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sessionObject != null)
          && (sessionObject.getStudySession() != null)
          && sessionObject.getStudySession().contains(sessionStudyCount)) {
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
        String type = FdahpStudyDesignerConstants.STUDYLEVEL;
        String studyId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.STUDY_ID);
        String permission =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.PERMISSION);
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        if (StringUtils.isNotEmpty(studyId)) {
          /*
           * Passing studyId in the param to fetch study related notification list and type to
           * define study notification in service level
           */
          notificationList = notificationService.getNotificationList(studyId, type);
          for (NotificationBO notification : notificationList) {
            if (!notification.isNotificationSent()
                && notification
                    .getNotificationScheduleType()
                    .equals(FdahpStudyDesignerConstants.NOTIFICATION_NOTIMMEDIATE)) {
              notification.setCheckNotificationSendingStatus("Scheduled");
            } else if (!notification.isNotificationSent()
                && notification
                    .getNotificationScheduleType()
                    .equals(FdahpStudyDesignerConstants.NOTIFICATION_IMMEDIATE)) {
              notification.setCheckNotificationSendingStatus("Sending");
            } else if (notification.isNotificationSent()) {
              notification.setCheckNotificationSendingStatus("Sent");
            }
          }
          studyBo = studyService.getStudyById(studyId, sessionObject.getUserId());
          if ((studyBo != null)
              && FdahpStudyDesignerConstants.STUDY_ACTIVE.equals(studyBo.getStatus())) {
            studyLive = studyService.getStudyLiveStatusByCustomId(studyBo.getCustomStudyId());
          } else {
            studyLive = studyBo;
          }
          notificationSavedList = studyService.getSavedNotification(studyId);
          map.addAttribute("notificationList", notificationList);
          map.addAttribute("studyLive", studyLive);
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          map.addAttribute("notificationSavedList", notificationSavedList);
        }
        map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
        map.addAttribute("_S", sessionStudyCount);
        map.addAttribute("appId", studyBo.getAppId());
        mav = new ModelAndView("studyNotificationList", map);
      }
    } catch (Exception e) {
      logger.error("StudyController - viewStudyNotificationList() - ERROR ", e);
    }
    logger.exit("viewStudyNotificationList() - ends");
    return mav;
  }

  @RequestMapping(value = "/adminStudies/validateAppId.do", method = RequestMethod.POST)
  public void validateAppId(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    logger.entry("begin validateAppId()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out;
    String message = FdahpStudyDesignerConstants.FAILURE;
    boolean flag = false;
    try {
      HttpSession session = request.getSession();
      SessionObject userSession =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (userSession != null) {
        String customStudyId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("customStudyId"))
                ? ""
                : request.getParameter("customStudyId");

        String appId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("appId"))
                ? ""
                : request.getParameter("appId");

        String studyType =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("studyType"))
                ? ""
                : request.getParameter("studyType");
        String dbCustomStudyId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("dbcustomStudyId"))
                ? ""
                : request.getParameter("dbcustomStudyId");
        flag = studyService.validateAppId(customStudyId, appId, studyType, dbCustomStudyId);
        if (flag) {
          message = FdahpStudyDesignerConstants.SUCCESS;
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - validateAppId() - ERROR ", e);
    }
    logger.exit("validateAppId() - Ends ");
    jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
    response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
    out = response.getWriter();
    out.print(jsonobject);
  }

  private void submitResponseToUserRegistrationServer(
      String customStudyId, HttpServletRequest request) {
    logger.entry("begin submitResponseToUserRegistrationServer()");
    HttpHeaders headers = null;
    HttpEntity<StudyDetailsBean> requestEntity = null;
    ResponseEntity<?> userRegistrationResponseEntity = null;
    StudyDetailsBean studyDetails = null;
    String userRegistrationServerUrl = "";
    Map<String, String> map = new HashMap<>();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      map = FdahpStudyDesignerUtil.getAppProperties();
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.add("Authorization", "Bearer " + oauthService.getAccessToken());
      AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

      userRegistrationServerUrl = map.get("userRegistrationServerUrl");

      studyDetails = studyService.getStudyByLatestVersion(customStudyId);

      requestEntity = new HttpEntity<StudyDetailsBean>(studyDetails, headers);

      userRegistrationResponseEntity =
          restTemplate.exchange(
              userRegistrationServerUrl, HttpMethod.POST, requestEntity, String.class);

      if (userRegistrationResponseEntity.getStatusCode() == HttpStatus.OK) {
        auditLogEventHelper.logEvent(STUDY_METADATA_SENT_TO_PARTICIPANT_DATASTORE, auditRequest);
        logger.info(
            "StudyController - submitResponseToUserRegistrationServer() - INFO ==>> SUCCESS");
      } else {
        auditLogEventHelper.logEvent(STUDY_METADATA_SEND_OPERATION_FAILED, auditRequest);
        logger.error(
            "StudyController - submitResponseToUserRegistrationServer() - ERROR ==>> FAILURE");
        throw new Exception("There is some issue in submitting data to User Registration Server ");
      }
    } catch (Exception e) {
      logger.error("StudyController - submitResponseToUserRegistrationServer() - ERROR ", e);
    }
    logger.exit("submitResponseToUserRegistrationServer() - Ends ");
  }

  private void submitResponseToResponseServer(String customStudyId, HttpServletRequest request) {
    logger.entry("begin submitResponseToResponseServer()");
    HttpHeaders headers = null;
    HttpEntity<StudyDetailsBean> requestEntity = null;
    ResponseEntity<?> responseServerResponseEntity = null;
    StudyDetailsBean studyDetails = null;
    String responseServerUrl = "";
    Map<String, String> map = new HashMap<>();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      map = FdahpStudyDesignerUtil.getAppProperties();
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.add("Authorization", "Bearer " + oauthService.getAccessToken());
      AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

      responseServerUrl = map.get("responseServerUrl");

      studyDetails = studyService.getStudyByLatestVersion(customStudyId);

      requestEntity = new HttpEntity<StudyDetailsBean>(studyDetails, headers);

      responseServerResponseEntity =
          restTemplate.exchange(responseServerUrl, HttpMethod.POST, requestEntity, String.class);

      if (responseServerResponseEntity.getStatusCode() == HttpStatus.OK) {
        auditLogEventHelper.logEvent(STUDY_METADATA_SENT_TO_RESPONSE_DATASTORE, auditRequest);
        logger.info("StudyController - submitResponseToResponseServer() - INFO ==>> SUCCESS");
      } else {
        auditLogEventHelper.logEvent(STUDY_METADATA_SEND_FAILED, auditRequest);
        logger.error("StudyController - submitResponseToResponseServer() - ERROR ==>> FAILURE");
        throw new Exception("There is some issue while submitting data to Response Server ");
      }
    } catch (Exception e) {
      logger.error("StudyController - submitResponseToResponseServer() - ERROR ", e);
    }
    logger.exit("submitResponseToResponseServer() - Ends ");
  }

  @RequestMapping("/adminStudies/addOrEditResource.do")
  public ModelAndView addOrEditResource(HttpServletRequest request) {
    logger.entry("begin addOrEditResource()");
    ModelAndView mav = new ModelAndView("redirect:/adminStudies/studyList.do");
    ModelMap map = new ModelMap();
    ResourceBO resourceBO = null;
    StudyBo studyBo = null;
    String sucMsg = "";
    String errMsg = "";
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
        if (StringUtils.isEmpty(studyId)) {
          studyId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
        }
        String resourceInfoId =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.RESOURCE_INFO_ID);
        if (StringUtils.isEmpty(resourceInfoId)) {
          resourceInfoId =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.RESOURCE_INFO_ID))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.RESOURCE_INFO_ID);
        }
        String studyProtocol =
            (String)
                request
                    .getSession()
                    .getAttribute(
                        sessionStudyCount + FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL);
        if (StringUtils.isEmpty(studyProtocol)) {
          studyProtocol =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL);
        }
        String action =
            (String)
                request
                    .getSession()
                    .getAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ACTION_ON);
        if (StringUtils.isEmpty(action)) {
          action =
              FdahpStudyDesignerUtil.isEmpty(
                      request.getParameter(FdahpStudyDesignerConstants.ACTION_ON))
                  ? ""
                  : request.getParameter(FdahpStudyDesignerConstants.ACTION_ON);
        }
        map.addAttribute("_S", sessionStudyCount);
        if (!FdahpStudyDesignerUtil.isEmpty(action)) {
          if (!("").equals(resourceInfoId)) {
            resourceBO = studyService.getResourceInfo(resourceInfoId);
            request
                .getSession()
                .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.RESOURCE_INFO_ID);
          }
          if ((null != studyProtocol)
              && !("").equals(studyProtocol)
              && (FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL).equalsIgnoreCase(studyProtocol)) {
            map.addAttribute(
                FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL,
                FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL);
            request
                .getSession()
                .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.IS_STUDY_PROTOCOL);
          }
          studyBo = studyService.getStudyById(studyId, sesObj.getUserId());
          map.addAttribute(FdahpStudyDesignerConstants.STUDY_BO, studyBo);
          map.addAttribute("resourceBO", resourceBO);
          map.addAttribute(FdahpStudyDesignerConstants.ACTION_ON, action);
          if ((studyBo != null) && !studyBo.getCustomStudyId().isEmpty()) {
            anchorTypeList =
                studyQuestionnaireService.getAnchorTypesByStudyId(studyBo.getCustomStudyId());
          }
          map.addAttribute("anchorTypeList", anchorTypeList);
          request
              .getSession()
              .removeAttribute(sessionStudyCount + FdahpStudyDesignerConstants.ACTION_ON);
          mav = new ModelAndView("addOrEditResourcePage", map);
        } else {
          mav = new ModelAndView("redirect:getResourceList.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - addOrEditResource() - ERROR", e);
    }
    logger.exit("addOrEditResource() - Ends");
    return mav;
  }

  @RequestMapping(value = "/studies/{studyId}/export.do", method = RequestMethod.POST)
  public void exportStudy(
      HttpServletRequest request, HttpServletResponse response, @PathVariable String studyId)
      throws IOException {
    logger.info("StudyController - exportStudy() - Starts");

    SessionObject sesObj =
        (SessionObject)
            request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);

    String copyVersion =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter("copyVersion"))
            ? ""
            : request.getParameter("copyVersion");

    String message = "";
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    if (StringUtils.isNotEmpty(studyId)) {
      // export study to google cloud storage
      message =
          studyExportImportService.exportStudy(
              studyId, copyVersion, sesObj.getUserId(), auditRequest);
    }
    JSONObject jsonobject = new JSONObject();
    if (message.equalsIgnoreCase(FdahpStudyDesignerConstants.SUCCESS)) {
      jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
      auditLogEventHelper.logEvent(STUDY_EXPORTED, auditRequest);
    } else {
      auditLogEventHelper.logEvent(STUDY_EXPORT_FAILED, auditRequest);
    }

    response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
    PrintWriter out = response.getWriter();
    out.print(jsonobject);

    logger.info("StudyController - exportStudy() - Ends");
  }

  @RequestMapping(value = "/studies/import.do", method = RequestMethod.POST)
  public void importStudy(HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    logger.info("StudyController - importStudy() - Starts");
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    HttpSession session = request.getSession();
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    SessionObject sessionObject =
        (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);

    String signedUrl =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter("signedUrl"))
            ? ""
            : request.getParameter("signedUrl");

    String message = studyExportImportService.importStudy(signedUrl, sessionObject);

    if (message.contains(FdahpStudyDesignerConstants.SUCCESS)) {
      auditRequest.setStudyVersion(FdahpStudyDesignerConstants.STUDY_PRE_LUNCH_VERSION);
      auditRequest.setAppId(FdahpStudyDesignerConstants.NA);
      auditLogEventHelper.logEvent(STUDY_IMPORTED, auditRequest);
    } else {
      auditLogEventHelper.logEvent(STUDY_IMPORT_FAILED, auditRequest);
    }

    jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
    response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
    out = response.getWriter();
    out.print(jsonobject);

    logger.info("StudyController - importStudy() - Ends");
  }

  @RequestMapping(value = "/adminStudies/replicate.do", method = RequestMethod.POST)
  public ModelAndView replicateStudy(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    logger.info("StudyController - replicateStudy() - Starts");
    HttpSession session = request.getSession();
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    SessionObject sessionObject =
        (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);

    String studyId =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
            ? ""
            : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);

    String copyVersion =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter("copyVersion"))
            ? ""
            : request.getParameter("copyVersion");

    StudyBo study = studyService.replicateStudy(studyId, copyVersion, sessionObject, auditRequest);

    if (study != null) {
      auditLogEventHelper.logEvent(STUDY_COPIED_INTO_NEW, auditRequest);
      request
          .getSession()
          .setAttribute(
              FdahpStudyDesignerConstants.SUC_MSG,
              FdahpStudyDesignerConstants.STUDY_REPLICATTE_SUCCESS_MSG);
    } else {
      auditLogEventHelper.logEvent(STUDY_COPY_FAILED, auditRequest);
      request
          .getSession()
          .setAttribute(
              FdahpStudyDesignerConstants.ERR_MSG,
              FdahpStudyDesignerConstants.STUDY_REPLICATTE_FAILURE_MSG);
    }

    logger.info("StudyController - replicateStudy() - Ends");
    return new ModelAndView("redirect:/adminStudies/studyList.do");
  }

  @RequestMapping(value = "/adminStudies/deleteStudy.do")
  public ModelAndView deleteById(HttpServletRequest request) {
    logger.entry("begin studydeleteById()");
    ModelAndView mav = new ModelAndView();
    String msg = "";
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    try {
      HttpSession session = request.getSession();
      SessionObject userSession =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);

      String studyId =
          FdahpStudyDesignerUtil.isEmpty(request.getParameter(FdahpStudyDesignerConstants.STUDY_ID))
              ? ""
              : request.getParameter(FdahpStudyDesignerConstants.STUDY_ID);
      if (StringUtils.isNotEmpty(studyId)) {
        msg = studyService.deleteById(studyId, auditRequest);
      }

      if (FdahpStudyDesignerConstants.SUCCESS.equals(msg)) {

        request
            .getSession()
            .setAttribute(
                FdahpStudyDesignerConstants.SUC_MSG, propMap.get("delete.study.success.message"));
        auditLogEventHelper.logEvent(StudyBuilderAuditEvent.STUDY_DELETED, auditRequest);
      }
    } catch (Exception e) {
      logger.error("StudyController - deleteById() - ERROR", e);
    }
    logger.exit("deleteById() - Ends");

    return new ModelAndView("redirect:/adminStudies/studyList.do");
  }
}
