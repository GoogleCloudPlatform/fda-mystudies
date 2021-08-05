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
}
