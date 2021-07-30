package com.fdahpstudydesigner.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

  private static XLogger logger = XLoggerFactory.getXLogger(StudyController.class.getName());

  @RequestMapping("/adminStudies/appList.do")
  public ModelAndView getApps(HttpServletRequest request) {
    ModelMap map = new ModelMap();

    ModelAndView mav = new ModelAndView("appListPage", map);

    logger.exit("getApps() - Ends");
    return mav;
  }
}
