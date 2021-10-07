/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.ANDROID_APP_MARKED_AS_DISTRIBUTED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_DEACTIVATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_LIST_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_PUBLISHED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.APP_RECORD_VIEWED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.IOS_APP_MARKED_AS_DISTRIBUTED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_APP_CREATION_INITIATED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.NEW_APP_RECORD_CREATED;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.APPLICATION_JSON;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.APP_BO;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.APP_ID;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.BUTTON_TEXT;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.COMPLETED_BUTTON;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.CUSTOM_APP_ID;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.CUSTOM_STUDY_ID;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.ERR_MSG;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.FAILURE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.FAILURE_APP_MESSAGE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.FAILURE_DEACTIVATE_APP_MESSAGE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.MESSAGE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.PERMISSION;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.SESSION_OBJECT;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.SUCCESS;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.SUC_MSG;

import com.fdahpstudydesigner.bean.AppDetailsBean;
import com.fdahpstudydesigner.bean.AppListBean;
import com.fdahpstudydesigner.bean.AppSessionBean;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.AppsBo;
import com.fdahpstudydesigner.common.StudyBuilderAuditEvent;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.dao.AppDAO;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.service.AppService;
import com.fdahpstudydesigner.service.OAuthService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

  private static XLogger logger = XLoggerFactory.getXLogger(StudyController.class.getName());

  @Autowired private AppService appService;

  @Autowired private OAuthService oauthService;

  @Autowired private RestTemplate restTemplate;

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  @Autowired private AppDAO appDAO;

  @RequestMapping("/adminApps/appList.do")
  public ModelAndView getApps(HttpServletRequest request) {
    logger.entry("begin getApps()");
    ModelAndView mav = new ModelAndView("loginPage");
    ModelMap map = new ModelMap();
    List<AppListBean> appList = null;
    String sucMsg = "";
    String errMsg = "";

    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      appList = appService.getAppList(sesObj.getUserId());
      auditLogEventHelper.logEvent(APP_LIST_VIEWED, auditRequest);

      if (null != request.getSession().getAttribute("sucMsgAppActions")) {
        sucMsg = (String) request.getSession().getAttribute("sucMsgAppActions");
        map.addAttribute("sucMsgAppActions", sucMsg);
        request.getSession().removeAttribute("sucMsgAppActions");
      }
      if (null != request.getSession().getAttribute("errMsgAppActions")) {
        errMsg = (String) request.getSession().getAttribute("errMsgAppActions");
        map.addAttribute("errMsgAppActions", errMsg);
        request.getSession().removeAttribute("errMsgAppActions");
      }

      if (null != request.getSession().getAttribute("sucMsgViewAssocStudies")) {
        request.getSession().removeAttribute("sucMsgViewAssocStudies");
      }

      map.addAttribute("appBos", appList);
      mav = new ModelAndView("appListPage", map);

    } catch (Exception e) {
      logger.error("AppController - getApps - ERROR", e);
    }
    logger.exit("getApps() - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/viewAppsInfo.do")
  public ModelAndView viewAppsBasicInfo(HttpServletRequest request) {
    logger.entry("begin viewAppsBasicInfo");
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appList.do");
    ModelMap map = new ModelMap();
    AppsBo appBo = null;
    String sucMsg = "";
    String errMsg = "";
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {
        if (null != request.getSession().getAttribute("sucMsgAppActions")) {
          request.getSession().removeAttribute("sucMsgAppActions");
        }

        if (null != request.getSession().getAttribute("errMsgAppActions")) {
          request.getSession().removeAttribute("errMsgAppActions");
        }

        if (null != request.getSession().getAttribute(sessionAppCount + SUC_MSG)) {
          sucMsg = (String) request.getSession().getAttribute(sessionAppCount + SUC_MSG);
          map.addAttribute(SUC_MSG, sucMsg);
          request.getSession().removeAttribute(sessionAppCount + SUC_MSG);
        }
        if (null != request.getSession().getAttribute(sessionAppCount + ERR_MSG)) {
          errMsg = (String) request.getSession().getAttribute(sessionAppCount + ERR_MSG);
          map.addAttribute(ERR_MSG, errMsg);
          request.getSession().removeAttribute(sessionAppCount + ERR_MSG);
        }
        String appId =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + APP_ID))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + APP_ID));
        String permission =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + PERMISSION))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + PERMISSION));
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());
        }
        auditRequest.setUserId(sesObj.getUserId());

        if (appBo == null) {
          auditLogEventHelper.logEvent(NEW_APP_CREATION_INITIATED, auditRequest);
        } else {
          auditRequest.setAppId(appBo.getCustomAppId());
          auditLogEventHelper.logEvent(APP_RECORD_VIEWED, auditRequest);
        }

        map.addAttribute(APP_BO, appBo);
        map.addAttribute(PERMISSION, permission);
        map.addAttribute("_S", sessionAppCount);

        mav = new ModelAndView("viewAppsInfo", map);
      }
    } catch (Exception e) {
      logger.error("AppController - viewAppsBasicInfo - ERROR", e);
    }
    logger.exit("viewAppsBasicInfo - Ends");
    return mav;
  }

  @RequestMapping(value = "/adminApps/validateAppId.do", method = RequestMethod.POST)
  public void validateAppId(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    logger.entry("begin validateAppId()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out;
    String message = FAILURE;
    boolean flag = false;
    try {
      HttpSession session = request.getSession();
      SessionObject userSession = (SessionObject) session.getAttribute(SESSION_OBJECT);
      if (userSession != null) {
        String appId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("appId"))
                ? ""
                : request.getParameter("appId");
        flag = appService.validateAppId(appId);
        if (flag) {
          message = SUCCESS;
        }
      }
    } catch (Exception e) {
      logger.error("StudyController - validateAppId() - ERROR ", e);
    }
    logger.exit("validateAppId() - Ends ");
    jsonobject.put(MESSAGE, message);
    response.setContentType(APPLICATION_JSON);
    out = response.getWriter();
    out.print(jsonobject);
  }

  @RequestMapping("/adminApps/saveOrUpdateAppInfo.do")
  public ModelAndView saveOrUpdateAppInfo(
      HttpServletRequest request, @ModelAttribute("appsBo") AppsBo appsBo) {
    logger.entry("begin saveOrUpdateAppInfo()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appList.do");
    String buttonText = "";
    String message = FAILURE;
    ModelMap map = new ModelMap();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      buttonText =
          FdahpStudyDesignerUtil.isEmpty(request.getParameter(BUTTON_TEXT))
              ? ""
              : request.getParameter(BUTTON_TEXT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {
        appsBo.setUserId(sesObj.getUserId());
        appsBo.setButtonText(buttonText);

        message = appService.saveOrUpdateApp(appsBo, sesObj);

        request.getSession().setAttribute(sessionAppCount + APP_ID, appsBo.getId() + "");
        map.addAttribute("_S", sessionAppCount);
        if (SUCCESS.equals(message)) {
          if (StringUtils.isNotEmpty(appsBo.getCustomAppId())) {
            auditRequest.setAppId(appsBo.getCustomAppId());
            request
                .getSession()
                .setAttribute(sessionAppCount + CUSTOM_STUDY_ID, appsBo.getCustomAppId());
          }
          if (buttonText.equalsIgnoreCase(COMPLETED_BUTTON)) {
            map.addAttribute("buttonText", buttonText);
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewAppsInfo.do", map);
          } else {
            request
                .getSession()
                .setAttribute(sessionAppCount + SUC_MSG, propMap.get(SAVE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewAppsInfo.do", map);
          }
        } else {
          request.getSession().setAttribute(sessionAppCount + ERR_MSG, "Error in set AppInfo");
          return new ModelAndView("redirect:viewAppsInfo.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("AppController - saveOrUpdateAppInfo - ERROR", e);
    }
    logger.exit("saveOrUpdateAppInfo() - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/viewAppSettings.do")
  public ModelAndView viewAppSettings(HttpServletRequest request) {
    logger.entry("begin viewAppSettings");
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appList.do");
    ModelMap map = new ModelMap();
    AppsBo appBo = null;
    String sucMsg = "";
    String errMsg = "";
    int countOfStudies = 0;
    try {
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {
        if (null != request.getSession().getAttribute(sessionAppCount + SUC_MSG)) {
          sucMsg = (String) request.getSession().getAttribute(sessionAppCount + SUC_MSG);
          map.addAttribute(SUC_MSG, sucMsg);
          request.getSession().removeAttribute(sessionAppCount + SUC_MSG);
        }
        if (null != request.getSession().getAttribute(sessionAppCount + ERR_MSG)) {
          errMsg = (String) request.getSession().getAttribute(sessionAppCount + ERR_MSG);
          map.addAttribute(ERR_MSG, errMsg);
          request.getSession().removeAttribute(sessionAppCount + ERR_MSG);
        }
        String appId =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + APP_ID))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + APP_ID));
        String permission =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + PERMISSION))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + PERMISSION));
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());

          countOfStudies = appService.getStudiesCountByAppId(appBo.getCustomAppId());

          if (appBo.getType() == null) {
            appBo.setType(FdahpStudyDesignerConstants.STUDY_TYPE_GT);
          }
        }
        map.addAttribute(APP_BO, appBo);
        map.addAttribute(PERMISSION, permission);
        map.addAttribute("_S", sessionAppCount);
        map.addAttribute("countOfStudies", countOfStudies);
        mav = new ModelAndView("viewAppSettings", map);
      }
    } catch (Exception e) {
      logger.error("AppController - viewAppSettings - ERROR", e);
    }
    logger.exit("viewAppSettings - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/saveOrUpdateAppSettingAndAdmins.do")
  public ModelAndView saveOrUpdateAppSettingAndAdmins(HttpServletRequest request, AppsBo appsBo) {
    logger.entry("begin saveOrUpdateAppSettingAndAdmins()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appList.do");
    String message = FdahpStudyDesignerConstants.FAILURE;
    ModelMap map = new ModelMap();
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {

        String buttonText =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT);
        appsBo.setButtonText(buttonText);
        appsBo.setUserId(sesObj.getUserId());
        message = appService.saveOrUpdateAppSettings(appsBo, sesObj);
        request.getSession().setAttribute(sessionAppCount + APP_ID, appsBo.getId() + "");
        map.addAttribute("_S", sessionAppCount);
        if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
          if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
            map.addAttribute("buttonText", buttonText);
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewAppSettings.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewAppSettings.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Error encountered. Your settings could not be saved.");
          return new ModelAndView("redirect:viewAppSettings.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("AppController - saveOrUpdateAppSettingAndAdmins - ERROR", e);
    }
    logger.exit("saveOrUpdateAppSettingAndAdmins() - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/viewAppProperties.do")
  public ModelAndView viewAppProperties(HttpServletRequest request) {
    logger.entry("begin viewAppProperties");
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appList.do");
    ModelMap map = new ModelMap();
    AppsBo appBo = null;
    String sucMsg = "";
    String errMsg = "";
    try {
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {
        if (null != request.getSession().getAttribute(sessionAppCount + SUC_MSG)) {
          sucMsg = (String) request.getSession().getAttribute(sessionAppCount + SUC_MSG);
          map.addAttribute(SUC_MSG, sucMsg);
          request.getSession().removeAttribute(sessionAppCount + SUC_MSG);
        }
        if (null != request.getSession().getAttribute(sessionAppCount + ERR_MSG)) {
          errMsg = (String) request.getSession().getAttribute(sessionAppCount + ERR_MSG);
          map.addAttribute(ERR_MSG, errMsg);
          request.getSession().removeAttribute(sessionAppCount + ERR_MSG);
        }
        String appId =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + APP_ID))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + APP_ID));
        String permission =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + PERMISSION))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + PERMISSION));
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());
        }
        map.addAttribute(APP_BO, appBo);
        map.addAttribute(PERMISSION, permission);
        map.addAttribute("_S", sessionAppCount);
        mav = new ModelAndView("viewAppProperties", map);
      }
    } catch (Exception e) {
      logger.error("AppController - viewAppProperties - ERROR", e);
    }
    logger.exit("viewAppProperties - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/viewDevConfigs.do")
  public ModelAndView viewDevConfigs(HttpServletRequest request) {
    logger.entry("begin viewDevConfigs");
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appList.do");
    ModelMap map = new ModelMap();
    AppsBo appBo = null;
    String sucMsg = "";
    String errMsg = "";
    try {
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {
        if (null != request.getSession().getAttribute(sessionAppCount + SUC_MSG)) {
          sucMsg = (String) request.getSession().getAttribute(sessionAppCount + SUC_MSG);
          map.addAttribute(SUC_MSG, sucMsg);
          request.getSession().removeAttribute(sessionAppCount + SUC_MSG);
        }
        if (null != request.getSession().getAttribute(sessionAppCount + ERR_MSG)) {
          errMsg = (String) request.getSession().getAttribute(sessionAppCount + ERR_MSG);
          map.addAttribute(ERR_MSG, errMsg);
          request.getSession().removeAttribute(sessionAppCount + ERR_MSG);
        }
        String appId =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + APP_ID))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + APP_ID));
        String permission =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + PERMISSION))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + PERMISSION));
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());
        }
        map.addAttribute(APP_BO, appBo);
        map.addAttribute(PERMISSION, permission);
        map.addAttribute("_S", sessionAppCount);

        mav = new ModelAndView("viewDevConfigs", map);
      }
    } catch (Exception e) {
      logger.error("AppController - viewDevConfigs - ERROR", e);
    }
    logger.exit("viewDevConfigs - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/appActionList.do")
  public ModelAndView viewAppActionList(HttpServletRequest request) {
    logger.entry("begin appActionList");
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appList.do");
    ModelMap map = new ModelMap();
    AppsBo appBo = null;
    String sucMsg = "";
    String errMsg = "";
    boolean markAsCompleted = false;
    int countOfStudiesAssociated = 0;
    try {
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {
        if (null != request.getSession().getAttribute(sessionAppCount + SUC_MSG)) {
          sucMsg = (String) request.getSession().getAttribute(sessionAppCount + SUC_MSG);
          map.addAttribute(SUC_MSG, sucMsg);
          request.getSession().removeAttribute(sessionAppCount + SUC_MSG);
        }
        if (null != request.getSession().getAttribute(sessionAppCount + ERR_MSG)) {
          errMsg = (String) request.getSession().getAttribute(sessionAppCount + ERR_MSG);
          map.addAttribute(ERR_MSG, errMsg);
          request.getSession().removeAttribute(sessionAppCount + ERR_MSG);
        }
        String appId =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + APP_ID))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + APP_ID));
        String permission =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + PERMISSION))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + PERMISSION));
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());
        }

        if (FdahpStudyDesignerUtil.isNotEmpty(appBo.getCustomAppId())) {
          countOfStudiesAssociated = appService.getStudiesByAppId(appBo.getCustomAppId());
        }
        markAsCompleted = appService.validateAppActions(appId);
        map.addAttribute(APP_BO, appBo);
        map.addAttribute(PERMISSION, permission);
        map.addAttribute("_S", sessionAppCount);
        map.addAttribute("markAsCompleted", markAsCompleted);
        map.addAttribute("countOfStudiesAssociated", countOfStudiesAssociated);
        mav = new ModelAndView("appActionList", map);
      }
    } catch (Exception e) {
      logger.error("AppController - appActionList - ERROR", e);
    }
    logger.exit("appActionList - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/viewAppDetails.do")
  public ModelAndView viewAppDetails(HttpServletRequest request) {
    Integer sessionAppCount;
    ModelMap map = new ModelMap();
    ModelAndView modelAndView = new ModelAndView("redirect:/adminApps/appList.do");
    String appId =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter(APP_ID))
            ? ""
            : request.getParameter(APP_ID);
    String permission =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter(PERMISSION))
            ? ""
            : request.getParameter(PERMISSION);
    SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
    List<Integer> appSessionList = new ArrayList<>();
    List<AppSessionBean> appSessionBeans = new ArrayList<>();
    AppSessionBean appSessionBean = null;

    try {

      sessionAppCount =
          (Integer)
              (request.getSession().getAttribute("sessionAppCount") != null
                  ? request.getSession().getAttribute("sessionAppCount")
                  : 0);
      if (sesObj != null) {
        if ((sesObj.getAppSessionBeans() != null) && !sesObj.getAppSessionBeans().isEmpty()) {
          for (AppSessionBean sessionBean : sesObj.getAppSessionBeans()) {
            if ((sessionBean != null)
                && sessionBean.getPermission().equals(permission)
                && sessionBean.getAppId().equals(appId)) {
              appSessionBean = sessionBean;
            }
          }
        }

        if (appSessionBean != null) {
          sessionAppCount = appSessionBean.getSessionAppCount();
        } else {
          ++sessionAppCount;
          if ((sesObj.getAppSession() != null) && !sesObj.getAppSession().isEmpty()) {
            appSessionList.addAll(sesObj.getAppSession());
          }
          appSessionList.add(sessionAppCount);
          sesObj.setAppSession(appSessionList);

          if ((sesObj.getAppSessionBeans() != null) && !sesObj.getAppSessionBeans().isEmpty()) {
            appSessionBeans.addAll(sesObj.getAppSessionBeans());
          }
          appSessionBean = new AppSessionBean();
          appSessionBean.setPermission(permission);
          appSessionBean.setSessionAppCount(sessionAppCount);
          appSessionBean.setAppId(appId);
          appSessionBeans.add(appSessionBean);
          sesObj.setAppSessionBeans(appSessionBeans);
        }
      }

      map.addAttribute("_S", sessionAppCount);
      request.getSession().setAttribute(SESSION_OBJECT, sesObj);
      request.getSession().setAttribute("sessionStudyCount", sessionAppCount);
      request.getSession().setAttribute(sessionAppCount + APP_ID, appId);
      request.getSession().setAttribute(sessionAppCount + PERMISSION, permission);

      modelAndView = new ModelAndView("redirect:/adminApps/viewAppsInfo.do", map);
    } catch (Exception e) {
      logger.error("StudyController - viewStudyDetails - ERROR", e);
    }
    return modelAndView;
  }

  @RequestMapping(value = "/adminApps/updateAppAction.do", method = RequestMethod.POST)
  public ModelAndView updateAppAction(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin updateAppAction()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FAILURE;
    String successMessage = "";
    StudyBuilderAuditEvent auditLogEvent = null;
    String buttonText = "";
    String appId = "";
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {
        appId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter(APP_ID))
                ? ""
                : request.getParameter(APP_ID);
        String customAppId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter(CUSTOM_APP_ID))
                ? ""
                : request.getParameter(CUSTOM_APP_ID);

        buttonText =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter(BUTTON_TEXT))
                ? ""
                : request.getParameter(BUTTON_TEXT);
        if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(buttonText)) {
          message = appService.updateAppAction(appId, buttonText, sesObj, auditRequest);
          if (message.equalsIgnoreCase(SUCCESS)) {

            if (buttonText.equalsIgnoreCase("createAppId")) {
              successMessage = "App record created";
              auditLogEvent = NEW_APP_RECORD_CREATED;
              submitAppDetailsResponseToUserRegistrationServer(customAppId, request);
            } else if (buttonText.equalsIgnoreCase("publishAppId")) {
              successMessage = "App published";
              auditLogEvent = APP_PUBLISHED;
              submitAppDetailsResponseToUserRegistrationServer(customAppId, request);
            } else if (buttonText.equalsIgnoreCase("iosDistributedId")) {
              successMessage = "App marked as 'distributed'";
              auditLogEvent = IOS_APP_MARKED_AS_DISTRIBUTED;
            } else if (buttonText.equalsIgnoreCase("androidDistributedId")) {
              successMessage = "App marked as 'distributed'";
              auditLogEvent = ANDROID_APP_MARKED_AS_DISTRIBUTED;
            } else if (buttonText.equalsIgnoreCase("deactivateId")) {
              successMessage = "App record deactivated";
              auditLogEvent = APP_DEACTIVATED;
              deactivateAppsAndUsersInUserRegistrationServer(customAppId, request, appId);
            }
            request.getSession().setAttribute("sucMsgAppActions", successMessage);
            auditLogEventHelper.logEvent(auditLogEvent, auditRequest);
          } else {
            if (message.equalsIgnoreCase(FAILURE)) {
              request.getSession().setAttribute("errMsgAppActions", FAILURE_APP_MESSAGE);
            }
          }
        }
      }
      jsonobject.put(MESSAGE, message);
      response.setContentType(APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      if (buttonText.equalsIgnoreCase("deactivateId")) {
        appDAO.changeSatusToActive(appId);
        request.getSession().setAttribute("errMsgAppActions", FAILURE_DEACTIVATE_APP_MESSAGE);
      } else {

        if (message.equalsIgnoreCase(FAILURE)) {
          request.getSession().setAttribute("errMsgAppActions", FAILURE_APP_MESSAGE);
        }
      }
      logger.error("AppController - updateAppAction() - ERROR", e);
    }
    logger.exit("updateAppAction() - Ends");
    return null;
  }

  private void submitAppDetailsResponseToUserRegistrationServer(
      String customAppId, HttpServletRequest request) {
    logger.entry("begin submitAppDetailsResponseToUserRegistrationServer()");
    HttpHeaders headers = null;
    HttpEntity<AppDetailsBean> requestEntity = null;
    ResponseEntity<?> userRegistrationResponseEntity = null;
    AppDetailsBean appDetailsBean = null;
    String userRegistrationServerUrl = "";
    Map<String, String> map = new HashMap<>();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      map = FdahpStudyDesignerUtil.getAppProperties();
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.add("Authorization", "Bearer " + oauthService.getAccessToken());
      AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

      userRegistrationServerUrl = map.get("userRegistrationServerAppMetadataUrl");

      appDetailsBean = appService.getAppDetailsBean(customAppId);

      requestEntity = new HttpEntity<AppDetailsBean>(appDetailsBean, headers);

      userRegistrationResponseEntity =
          restTemplate.exchange(
              userRegistrationServerUrl, HttpMethod.POST, requestEntity, String.class);

      if (userRegistrationResponseEntity.getStatusCode() == HttpStatus.OK) {
        logger.info(
            "AppController - submitAppDetailsResponseToUserRegistrationServer() - INFO ==>> SUCCESS");
      } else {
        logger.error(
            "AppController - submitResponseToUserRegistrationServer() - ERROR ==>> FAILURE");
        throw new Exception("There is some issue in submitting data to User Registration Server ");
      }
    } catch (Exception e) {
      logger.error(
          "AppController - submitAppDetailsResponseToUserRegistrationServer() - ERROR ", e);
    }
    logger.exit("submitAppDetailsResponseToUserRegistrationServer() - Ends ");
  }

  private void deactivateAppsAndUsersInUserRegistrationServer(
      String customAppId, HttpServletRequest request, String appId) throws Exception {
    logger.entry("begin deactivateAppsAndUsersInUserRegistrationServer()");
    HttpHeaders headers = null;
    HttpEntity<String> requestEntity = null;
    ResponseEntity<?> userRegistrationResponseEntity = null;
    String userRegistrationDeactivateUrl = "";
    Map<String, String> map = new HashMap<>();
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    map = FdahpStudyDesignerUtil.getAppProperties();
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + oauthService.getAccessToken());
    AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

    userRegistrationDeactivateUrl = map.get("usermanagementServerDeactivateapp");

    requestEntity = new HttpEntity<>(headers);

    userRegistrationResponseEntity =
        restTemplate.exchange(
            userRegistrationDeactivateUrl,
            HttpMethod.PUT,
            requestEntity,
            String.class,
            customAppId);

    if (userRegistrationResponseEntity.getStatusCode() == HttpStatus.OK) {
      logger.info(
          "AppController - deactivateAppsAndUsersInUserRegistrationServer() - INFO ==>> SUCCESS");
    } else {
      appDAO.changeSatusToActive(appId);
      logger.error(
          "AppController - deactivateAppsAndUsersInUserRegistrationServer() - ERROR ==>> FAILURE");
      throw new Exception("There is some issue in submitting data to User Registration Server ");
    }

    logger.exit("deactivateAppsAndUsersInUserRegistrationServer() - Ends ");
  }

  @RequestMapping("/adminApps/saveOrUpdateAppProperties.do")
  public ModelAndView saveOrUpdateAppProperties(HttpServletRequest request, AppsBo appsBo) {
    logger.entry("begin saveOrUpdateAppProperties()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appList.do");
    String message = FdahpStudyDesignerConstants.FAILURE;
    ModelMap map = new ModelMap();
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {

        String buttonText =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT);
        appsBo.setButtonText(buttonText);
        appsBo.setUserId(sesObj.getUserId());
        message = appService.saveOrUpdateAppProperties(appsBo, sesObj);
        request.getSession().setAttribute(sessionAppCount + APP_ID, appsBo.getId() + "");
        map.addAttribute("_S", sessionAppCount);
        if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
          if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
            map.addAttribute("buttonText", buttonText);
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewAppProperties.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewAppProperties.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Error encountered. Your settings could not be saved.");
          return new ModelAndView("redirect:viewAppProperties.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("AppController - saveOrUpdateAppProperties - ERROR", e);
    }
    logger.exit("saveOrUpdateAppProperties() - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/saveOrUpdateAppDeveloperConfig.do")
  public ModelAndView saveOrUpdateAppDeveloperConfig(HttpServletRequest request, AppsBo appsBo) {
    logger.entry("begin saveOrUpdateAppDeveloperConfig()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appList.do");
    String message = FdahpStudyDesignerConstants.FAILURE;
    ModelMap map = new ModelMap();
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getAppSession() != null)
          && sesObj.getAppSession().contains(sessionAppCount)) {

        String buttonText =
            FdahpStudyDesignerUtil.isEmpty(
                    request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT))
                ? ""
                : request.getParameter(FdahpStudyDesignerConstants.BUTTON_TEXT);
        appsBo.setButtonText(buttonText);
        appsBo.setUserId(sesObj.getUserId());
        message = appService.saveOrUpdateAppDeveloperConfig(appsBo, sesObj);
        request.getSession().setAttribute(sessionAppCount + APP_ID, appsBo.getId() + "");
        map.addAttribute("_S", sessionAppCount);
        if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
          if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
            map.addAttribute("buttonText", buttonText);
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewDevConfigs.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewDevConfigs.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG,
                  "Error encountered. Your settings could not be saved.");
          return new ModelAndView("redirect:viewAppProperties.do", map);
        }
      }
    } catch (Exception e) {
      logger.error("AppController - saveOrUpdateAppDeveloperConfig - ERROR", e);
    }
    logger.exit("saveOrUpdateAppDeveloperConfig() - Ends");
    return mav;
  }
}
