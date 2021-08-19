package com.fdahpstudydesigner.controller;

import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.ACTION_DEACTIVATE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.ACTION_LUNCH;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.ACTION_SUC_MSG;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.ACTION_UPDATES;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.ACTION_UPDATES_SUCCESS_MSG;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.APPLICATION_JSON;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.APP_BO;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.APP_ID;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.BUTTON_TEXT;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.COMPLETED_BUTTON;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.CUSTOM_APP_ID;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.CUSTOM_STUDY_ID;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.ERR_MSG;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.FAILURE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.FAILURE_UPDATE_STUDY_MESSAGE;
import static com.fdahpstudydesigner.util.FdahpStudyDesignerConstants.IS_LIVE;
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

  @RequestMapping("/adminApps/appList.do")
  public ModelAndView getApps(HttpServletRequest request) {
    logger.entry("begin getApps()");
    ModelAndView mav = new ModelAndView("loginPage");
    ModelMap map = new ModelMap();
    List<AppListBean> appList = null;
    try {
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      appList = appService.getAppList(sesObj.getUserId());
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
        String isLive =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + IS_LIVE))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + IS_LIVE));

        if (FdahpStudyDesignerUtil.isEmpty(isLive)) {
          request.getSession().removeAttribute(sessionAppCount + IS_LIVE);
        }
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());
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
            //  auditLogEventHelper.logEvent(STUDY_SAVED_IN_DRAFT_STATE, auditRequest);
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
            return new ModelAndView("redirect:viewAppSettings.do", map);
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
        String isLive =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + IS_LIVE))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + IS_LIVE));

        if (FdahpStudyDesignerUtil.isEmpty(isLive)) {
          request.getSession().removeAttribute(sessionAppCount + IS_LIVE);
        }
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());

          if (appBo.getType() == null) {
            appBo.setType(FdahpStudyDesignerConstants.STUDY_TYPE_GT);
          }
        }
        map.addAttribute(APP_BO, appBo);
        map.addAttribute(PERMISSION, permission);
        map.addAttribute("_S", sessionAppCount);
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
            return new ModelAndView("redirect:viewAppProperties.do", map);
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
        String isLive =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + IS_LIVE))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + IS_LIVE));

        if (FdahpStudyDesignerUtil.isEmpty(isLive)) {
          request.getSession().removeAttribute(sessionAppCount + IS_LIVE);
        }
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
        String isLive =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + IS_LIVE))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + IS_LIVE));

        if (FdahpStudyDesignerUtil.isEmpty(isLive)) {
          request.getSession().removeAttribute(sessionAppCount + IS_LIVE);
        }
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
        String isLive =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String) request.getSession().getAttribute(sessionAppCount + IS_LIVE))
                    ? ""
                    : request.getSession().getAttribute(sessionAppCount + IS_LIVE));

        if (FdahpStudyDesignerUtil.isEmpty(isLive)) {
          request.getSession().removeAttribute(sessionAppCount + IS_LIVE);
        }
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());
        }
        markAsCompleted = appService.validateAppActions(appId);
        map.addAttribute(APP_BO, appBo);
        map.addAttribute(PERMISSION, permission);
        map.addAttribute("_S", sessionAppCount);
        map.addAttribute("markAsCompleted", markAsCompleted);
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
    String isLive =
        FdahpStudyDesignerUtil.isEmpty(request.getParameter(IS_LIVE))
            ? ""
            : request.getParameter(IS_LIVE);
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
                && sessionBean.getIsLive().equals(isLive)
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
          appSessionBean.setIsLive(isLive);
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
      request.getSession().setAttribute(sessionAppCount + IS_LIVE, isLive);

      modelAndView = new ModelAndView("redirect:/adminApps/viewAppsInfo.do", map);
    } catch (Exception e) {
      logger.error("StudyController - viewStudyDetails - ERROR", e);
    }
    return modelAndView;
  }

  @RequestMapping(value = "/adminApps/updateAppAction", method = RequestMethod.POST)
  public ModelAndView updateAppAction(HttpServletRequest request, HttpServletResponse response) {
    logger.entry("begin updateAppAction()");
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FAILURE;
    String successMessage = "";
    try {
      SessionObject sesObj = (SessionObject) request.getSession().getAttribute(SESSION_OBJECT);
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionAppCount)) {
        String appId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter(APP_ID))
                ? ""
                : request.getParameter(APP_ID);
        String customAppId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter(CUSTOM_APP_ID))
                ? ""
                : request.getParameter(CUSTOM_APP_ID);

        String buttonText =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter(BUTTON_TEXT))
                ? ""
                : request.getParameter(BUTTON_TEXT);
        if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(buttonText)) {
          message = appService.updateAppAction(appId, buttonText, sesObj);
          if (message.equalsIgnoreCase(SUCCESS)) {
            /*  if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_LUNCH)) {
               successMessage = FdahpStudyDesignerConstants.ACTION_LUNCH_SUCCESS_MSG;
               submitResponseToUserRegistrationServer(customAppId, request);
            //   submitResponseToResponseServer(customStudyId, request);
             } else*/ if (buttonText.equalsIgnoreCase(ACTION_UPDATES)) {
              successMessage = ACTION_UPDATES_SUCCESS_MSG;
              submitAppDetailsResponseToUserRegistrationServer(customAppId, request);
              //  submitResponseToResponseServer(customStudyId, request);
            } /*else if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.ACTION_RESUME)) {
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
              }*/
            if (buttonText.equalsIgnoreCase(ACTION_DEACTIVATE)
                || buttonText.equalsIgnoreCase(ACTION_LUNCH)
                || buttonText.equalsIgnoreCase(ACTION_UPDATES)) {
              request.getSession().setAttribute(ACTION_SUC_MSG, successMessage);
            } else {
              request.getSession().setAttribute(sessionAppCount + ACTION_SUC_MSG, successMessage);
            }
          } else {
            if (message.equalsIgnoreCase(FAILURE)) {
              request.getSession().setAttribute(ERR_MSG, FAILURE_UPDATE_STUDY_MESSAGE);
            }
          }
        }
      }
      jsonobject.put(MESSAGE, message);
      response.setContentType(APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
    } catch (Exception e) {
      logger.error("AppController - updateStudyActionOnAction() - ERROR", e);
    }
    logger.exit("updateStudyActionOnAction() - Ends");
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
        //  auditLogEventHelper.logEvent(STUDY_METADATA_SENT_TO_PARTICIPANT_DATASTORE,
        // auditRequest);
        logger.info(
            "AppController - submitAppDetailsResponseToUserRegistrationServer() - INFO ==>> SUCCESS");
      } else {
        //    auditLogEventHelper.logEvent(STUDY_METADATA_SEND_OPERATION_FAILED, auditRequest);
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
            return new ModelAndView("redirect:viewDevConfigs.do", map);
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
}
