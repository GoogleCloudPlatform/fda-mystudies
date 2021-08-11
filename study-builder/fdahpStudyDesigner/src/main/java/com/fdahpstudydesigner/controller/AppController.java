package com.fdahpstudydesigner.controller;

import com.fdahpstudydesigner.bean.AppListBean;
import com.fdahpstudydesigner.bean.AppSessionBean;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.AppSequenceBo;
import com.fdahpstudydesigner.bo.AppsBo;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.service.AppService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

  private static XLogger logger = XLoggerFactory.getXLogger(StudyController.class.getName());

  @Autowired private AppService appService;

  @RequestMapping("/adminApps/appList.do")
  public ModelAndView getApps(HttpServletRequest request) {
    logger.entry("begin getApps()");
    ModelAndView mav = new ModelAndView("loginPage");
    ModelMap map = new ModelMap();
    List<AppListBean> appList = null;
    try {
      SessionObject sesObj =
          (SessionObject)
              request.getSession().getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
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
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appsList.do");
    ModelMap map = new ModelMap();
    AppsBo appBo = null;
    String sucMsg = "";
    String errMsg = "";
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
        if (null
            != request
                .getSession()
                .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request
              .getSession()
              .removeAttribute(sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null
            != request
                .getSession()
                .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request
              .getSession()
              .removeAttribute(sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG);
        }
        String appId =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String)
                            request
                                .getSession()
                                .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.APP_ID))
                    ? ""
                    : request
                        .getSession()
                        .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.APP_ID));
        String permission =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String)
                            request
                                .getSession()
                                .getAttribute(
                                    sessionAppCount + FdahpStudyDesignerConstants.PERMISSION))
                    ? ""
                    : request
                        .getSession()
                        .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.PERMISSION));
        String isLive =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String)
                            request
                                .getSession()
                                .getAttribute(
                                    sessionAppCount + FdahpStudyDesignerConstants.IS_LIVE))
                    ? ""
                    : request
                        .getSession()
                        .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.IS_LIVE));

        if (FdahpStudyDesignerUtil.isEmpty(isLive)) {
          request
              .getSession()
              .removeAttribute(sessionAppCount + FdahpStudyDesignerConstants.IS_LIVE);
        }
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());
        }
        map.addAttribute("appBo", appBo);
        map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
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
    String message = FdahpStudyDesignerConstants.FAILURE;
    boolean flag = false;
    try {
      HttpSession session = request.getSession();
      SessionObject userSession =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      if (userSession != null) {
        String appId =
            FdahpStudyDesignerUtil.isEmpty(request.getParameter("appId"))
                ? ""
                : request.getParameter("appId");
        //    flag = appService.validateAppId(appId);
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

  @RequestMapping("/adminApps/saveOrUpdateAppInfo.do")
  public ModelAndView saveOrUpdateAppInfo(
      HttpServletRequest request,
      @ModelAttribute(FdahpStudyDesignerConstants.STUDY_BO) AppsBo appsBo) {
    logger.entry("begin saveOrUpdateAppInfo()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appsList.do");
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
      Integer sessionAppCount =
          StringUtils.isNumeric(request.getParameter("_S"))
              ? Integer.parseInt(request.getParameter("_S"))
              : 0;
      if ((sesObj != null)
          && (sesObj.getStudySession() != null)
          && sesObj.getStudySession().contains(sessionAppCount)) {
        if (StringUtils.isEmpty(appsBo.getId())) {
          AppSequenceBo appSequenceBo = new AppSequenceBo();
          appSequenceBo.setAppInfo(true);
          appsBo.setAppSequenceBo(appSequenceBo);
          appsBo.setAppsStatus(FdahpStudyDesignerConstants.STUDY_PRE_LAUNCH);
        }
        // appsBo.setUserId(sesObj.getUserId());

        appsBo.setButtonText(buttonText);
        //  appsBo.setDescription(StringEscapeUtils.unescapeHtml4(appsBo.getDescription()));

        //   message = appService.saveOrUpdateStudy(appsBo, sesObj.getUserId(), sesObj);

        request
            .getSession()
            .setAttribute(
                sessionAppCount + FdahpStudyDesignerConstants.STUDY_ID, appsBo.getId() + "");
        map.addAttribute("_S", sessionAppCount);
        if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
          if (StringUtils.isNotEmpty(appsBo.getCustomAppId())) {
            auditRequest.setAppId(appsBo.getCustomAppId());
            //  auditLogEventHelper.logEvent(STUDY_SAVED_IN_DRAFT_STATE, auditRequest);
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.CUSTOM_STUDY_ID,
                    appsBo.getCustomAppId());
          }
          if (buttonText.equalsIgnoreCase(FdahpStudyDesignerConstants.COMPLETED_BUTTON)) {
            map.addAttribute("buttonText", buttonText);
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.COMPLETE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewAppInfo.do", map);
          } else {
            request
                .getSession()
                .setAttribute(
                    sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG,
                    propMap.get(FdahpStudyDesignerConstants.SAVE_STUDY_SUCCESS_MESSAGE));
            return new ModelAndView("redirect:viewAppInfo.do", map);
          }
        } else {
          request
              .getSession()
              .setAttribute(
                  sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG, "Error in set AppInfo");
          return new ModelAndView("redirect:viewBasicInfo.do", map);
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
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appsList.do");
    ModelMap map = new ModelMap();
    AppsBo appBo = null;
    String sucMsg = "";
    String errMsg = "";
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
        if (null
            != request
                .getSession()
                .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG)) {
          sucMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.SUC_MSG, sucMsg);
          request
              .getSession()
              .removeAttribute(sessionAppCount + FdahpStudyDesignerConstants.SUC_MSG);
        }
        if (null
            != request
                .getSession()
                .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG)) {
          errMsg =
              (String)
                  request
                      .getSession()
                      .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG);
          map.addAttribute(FdahpStudyDesignerConstants.ERR_MSG, errMsg);
          request
              .getSession()
              .removeAttribute(sessionAppCount + FdahpStudyDesignerConstants.ERR_MSG);
        }
        String appId =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String)
                            request
                                .getSession()
                                .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.APP_ID))
                    ? ""
                    : request
                        .getSession()
                        .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.APP_ID));
        String permission =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String)
                            request
                                .getSession()
                                .getAttribute(
                                    sessionAppCount + FdahpStudyDesignerConstants.PERMISSION))
                    ? ""
                    : request
                        .getSession()
                        .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.PERMISSION));
        String isLive =
            (String)
                (FdahpStudyDesignerUtil.isEmpty(
                        (String)
                            request
                                .getSession()
                                .getAttribute(
                                    sessionAppCount + FdahpStudyDesignerConstants.IS_LIVE))
                    ? ""
                    : request
                        .getSession()
                        .getAttribute(sessionAppCount + FdahpStudyDesignerConstants.IS_LIVE));

        if (FdahpStudyDesignerUtil.isEmpty(isLive)) {
          request
              .getSession()
              .removeAttribute(sessionAppCount + FdahpStudyDesignerConstants.IS_LIVE);
        }
        if (FdahpStudyDesignerUtil.isNotEmpty(appId)) {
          appBo = appService.getAppById(appId, sesObj.getUserId());
        }
        map.addAttribute("appBo", appBo);
        map.addAttribute(FdahpStudyDesignerConstants.PERMISSION, permission);
        map.addAttribute("_S", sessionAppCount);
        mav = new ModelAndView("viewAppSettings", map);
      }
    } catch (Exception e) {
      logger.error("AppController - viewAppSettings - ERROR", e);
    }
    logger.exit("viewAppSettings - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/viewAppProperties.do")
  public ModelAndView viewAppProperties(HttpServletRequest request) {
    logger.entry("begin viewAppProperties");
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appsList.do");
    ModelMap map = new ModelMap();

    try {

      mav = new ModelAndView("viewAppProperties", map);
    } catch (Exception e) {
      logger.error("AppController - viewAppProperties - ERROR", e);
    }
    logger.exit("viewAppProperties - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/viewDevConfigs.do")
  public ModelAndView viewDevConfigs(HttpServletRequest request) {
    logger.entry("begin viewDevConfigs");
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appsList.do");
    ModelMap map = new ModelMap();

    try {

      mav = new ModelAndView("viewDevConfigs", map);
    } catch (Exception e) {
      logger.error("AppController - viewDevConfigs - ERROR", e);
    }
    logger.exit("viewDevConfigs - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/appActionList.do")
  public ModelAndView viewAppActionList(HttpServletRequest request) {
    logger.entry("begin appActionList");
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appsList.do");
    ModelMap map = new ModelMap();

    try {

      mav = new ModelAndView("appActionList", map);
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
        FdahpStudyDesignerUtil.isEmpty(request.getParameter(FdahpStudyDesignerConstants.APP_ID))
            ? ""
            : request.getParameter(FdahpStudyDesignerConstants.APP_ID);
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

          if ((sesObj.getStudySessionBeans() != null) && !sesObj.getStudySessionBeans().isEmpty()) {
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
      request.getSession().setAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT, sesObj);
      request.getSession().setAttribute("sessionAppCount", sessionAppCount);
      request
          .getSession()
          .setAttribute(sessionAppCount + FdahpStudyDesignerConstants.APP_ID, appId);
      request
          .getSession()
          .setAttribute(sessionAppCount + FdahpStudyDesignerConstants.PERMISSION, permission);
      request
          .getSession()
          .setAttribute(sessionAppCount + FdahpStudyDesignerConstants.IS_LIVE, isLive);

      modelAndView = new ModelAndView("redirect:/adminApps/viewAppsInfo.do", map);
    } catch (Exception e) {
      logger.error("StudyController - viewStudyDetails - ERROR", e);
    }
    return modelAndView;
  }
}
