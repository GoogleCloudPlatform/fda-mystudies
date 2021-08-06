package com.fdahpstudydesigner.controller;

import com.fdahpstudydesigner.bean.AppListBean;
import com.fdahpstudydesigner.service.AppService;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.SessionObject;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
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

    try {

      mav = new ModelAndView("viewAppsInfo", map);
    } catch (Exception e) {
      logger.error("AppController - viewAppsBasicInfo - ERROR", e);
    }
    logger.exit("viewAppsBasicInfo - Ends");
    return mav;
  }

  @RequestMapping("/adminApps/viewAppSettings.do")
  public ModelAndView viewAppSettings(HttpServletRequest request) {
    logger.entry("begin viewAppSettings");
    ModelAndView mav = new ModelAndView("redirect:/adminApps/appsList.do");
    ModelMap map = new ModelMap();

    try {

      mav = new ModelAndView("viewAppSettings", map);
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
}
