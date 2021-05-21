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

import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.SESSION_EXPIRY;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_SIGNOUT_FAILED;
import static com.fdahpstudydesigner.common.StudyBuilderAuditEvent.USER_SIGNOUT_SUCCEEDED;
import com.fdahpstudydesigner.bean.AuditLogEventRequest;
import com.fdahpstudydesigner.bo.MasterDataBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.common.StudyBuilderAuditEventHelper;
import com.fdahpstudydesigner.mapper.AuditEventMapper;
import com.fdahpstudydesigner.service.DashBoardAndProfileService;
import com.fdahpstudydesigner.service.LoginServiceImpl;
import com.fdahpstudydesigner.util.FdahpStudyDesignerConstants;
import com.fdahpstudydesigner.util.FdahpStudyDesignerUtil;
import com.fdahpstudydesigner.util.SessionObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {

  private static XLogger logger = XLoggerFactory.getXLogger(LoginController.class.getName());

  @Autowired private DashBoardAndProfileService dashBoardAndProfileService;

  @Autowired private StudyBuilderAuditEventHelper auditLogEventHelper;

  private LoginServiceImpl loginService;

  @RequestMapping("/addPassword.do")
  public ModelAndView addPassword(HttpServletRequest request, UserBO userBO) {
    logger.entry("begin addPassword()");
    ModelAndView mv = new ModelAndView("redirect:sessionOut.do");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String sucMsg = "";
    try {

      HttpSession session = request.getSession(false);
      SessionObject sesObj =
          (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      String password =
          FdahpStudyDesignerUtil.isNotEmpty(request.getParameter("password"))
              ? request.getParameter("password").replaceAll(request.getParameter("_csrf"), "")
              : "";
      String securityToken =
          FdahpStudyDesignerUtil.isNotEmpty(request.getParameter("securityToken"))
              ? request.getParameter("securityToken")
              : "";

      boolean isInactiveUser = loginService.isInactiveUser(securityToken);
      boolean isIntialPasswordSetUp = loginService.isIntialPasswordSetUp(securityToken);
      String errorMsg = "";
      if (!isInactiveUser || isIntialPasswordSetUp) {
        errorMsg = loginService.authAndAddPassword(securityToken, password, userBO, sesObj);
      } else {
        errorMsg = propMap.get("user.inactive.msg");
      }

      if (!errorMsg.equals(FdahpStudyDesignerConstants.SUCCESS)) {
        request.getSession(false).setAttribute("errMsg", errorMsg);
        mv = new ModelAndView("redirect:createPassword.do?securityToken=" + securityToken);
      } else {
        if ((userBO != null) && StringUtils.isNotEmpty(userBO.getFirstName())) {
          sucMsg = propMap.get("user.account.setup.msg");
        } else {
          sucMsg = propMap.get("user.newpassword.success.msg");
        }
        mv = new ModelAndView("redirect:sessionOut.do?sucMsg=" + sucMsg);
      }

    } catch (Exception e) {
      logger.error("LoginController - addPassword() - ERROR ", e);
    }

    logger.exit("addPassword() - Ends");
    return mv;
  }

  @RequestMapping("/healthCheck.do")
  @ResponseBody
  public String healthCheck() {
    return "200 OK!";
  }

  @RequestMapping(value = "/profile/changeExpiredPassword.do")
  public ModelAndView changeExpiredPassword(HttpServletRequest request) {
    logger.entry("begin changeExpiredPassword()");
    ModelAndView mv = new ModelAndView("loginPage");
    String errMsg = null;
    String sucMsg = null;
    ModelMap map = null;
    try {
      map = new ModelMap();
      if (null != request.getSession().getAttribute("sucMsg")) {
        sucMsg = (String) request.getSession().getAttribute("sucMsg");
        map.addAttribute("sucMsg", sucMsg);
        request.getSession().removeAttribute("sucMsg");
      }
      if (null != request.getSession().getAttribute("errMsg")) {
        errMsg = (String) request.getSession().getAttribute("errMsg");
        map.addAttribute("errMsg", errMsg);
        request.getSession().removeAttribute("errMsg");
      }
      mv = new ModelAndView("changeExpiredPassword", map);
    } catch (Exception e) {
      logger.error("LoginController - changeExpiredPassword() - ERROR ", e);
    }
    logger.exit("changeExpiredPassword() - Ends");
    return mv;
  }

  @RequestMapping(value = "/changePassword.do")
  public ModelAndView changePassword(HttpServletRequest request) {
    logger.entry("begin changePassword()");
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String message = FdahpStudyDesignerConstants.FAILURE;
    String userId;
    ModelAndView mv = new ModelAndView("redirect:login.do");
    SessionObject sesObj = null;
    HttpSession session = null;
    try {
      session = request.getSession(false);
      sesObj = (SessionObject) session.getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
      userId = sesObj.getUserId();
      String newPassword =
          (null != request.getParameter("newPassword"))
                  && !"".equals(request.getParameter("newPassword"))
              ? request.getParameter("newPassword").replaceAll(request.getParameter("_csrf"), "")
              : "";
      String oldPassword =
          (null != request.getParameter("oldPassword"))
                  && !"".equals(request.getParameter("oldPassword"))
              ? request.getParameter("oldPassword").replaceAll(request.getParameter("_csrf"), "")
              : "";
      message = loginService.changePassword(userId, newPassword, oldPassword, sesObj);
      if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
        sesObj.setPasswordExpiryDateTime(FdahpStudyDesignerUtil.getCurrentDateTime());
        mv =
            new ModelAndView(
                "redirect:sessionOut.do?sucMsg=" + propMap.get("user.force.logout.success"));
        request.getSession().setAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT, sesObj);
      } else {
        request.getSession(false).setAttribute("errMsg", message);
        mv = new ModelAndView("redirect:/profile/changeExpiredPassword.do");
      }
    } catch (Exception e) {
      logger.error("LoginController - changePassword() - ERROR ", e);
    }
    logger.exit("changePassword() - Ends");
    return mv;
  }

  @RequestMapping(value = "/errorRedirect.do")
  public ModelAndView errorRedirect(
      @RequestParam(value = "error", required = false) String error,
      HttpServletRequest request,
      HttpServletResponse response)
      throws IOException {
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    if ((error != null)
        && (("timeOut").equalsIgnoreCase(error) || ("multiUser").equalsIgnoreCase(error))) {
      // TODO(Aswini):Session Expiry AUDIT Event
      request.getSession().setAttribute("errMsg", propMap.get("user.session.timeout"));
    } else if (error != null) {
      request
          .getSession()
          .setAttribute(
              "errMsg",
              FdahpStudyDesignerUtil.getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
    }
    boolean ajax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    if (ajax) {
      JSONObject jsonobject = new JSONObject();
      PrintWriter out = null;
      jsonobject.put(
          FdahpStudyDesignerConstants.MESSAGE,
          FdahpStudyDesignerUtil.getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
      response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
      out = response.getWriter();
      out.print(jsonobject);
      return null;
    } else {
      return new ModelAndView("redirect:login.do");
    }
  }

  @RequestMapping(value = "/forgotPassword.do")
  public ModelAndView forgotPassword(HttpServletRequest request) {
    logger.entry("begin forgotPassword()");
    ModelAndView mav = new ModelAndView("redirect:login.do");
    String message = FdahpStudyDesignerConstants.FAILURE;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    try {
      String email =
          ((null != request.getParameter("email")) && !"".equals(request.getParameter("email")))
              ? request.getParameter("email")
              : "";
      message = loginService.sendPasswordResetLinkToMail(request, email, "", "", auditRequest);
      if (FdahpStudyDesignerConstants.SUCCESS.equals(message)) {
        request.getSession().setAttribute("sucMsg", propMap.get("user.forgot.success.msg"));
      } else {
        request.getSession().setAttribute("errMsg", message);
      }
    } catch (Exception e) {
      logger.error("LoginController - forgotPassword() - ERROR ", e);
    }
    logger.exit("forgotPassword() - Ends");
    return mav;
  }

  @RequestMapping(value = "/login.do")
  public ModelAndView loginPage(HttpServletRequest request) {
    String sucMsg;
    String errMsg;
    ModelMap map = new ModelMap();
    MasterDataBO masterDataBO = null;
    if (null != request.getSession().getAttribute("sucMsg")) {
      sucMsg = (String) request.getSession().getAttribute("sucMsg");
      map.addAttribute("sucMsg", sucMsg);
      request.getSession().removeAttribute("sucMsg");
    }
    if (null != request.getSession().getAttribute("errMsg")) {
      errMsg = (String) request.getSession().getAttribute("errMsg");
      map.addAttribute("errMsg", errMsg);
      request.getSession().removeAttribute("errMsg");
    }
    masterDataBO = dashBoardAndProfileService.getMasterData("terms");
    map.addAttribute("masterDataBO", masterDataBO);
    return new ModelAndView("loginPage", map);
  }

  @RequestMapping("/privacyPolicy.do")
  public ModelAndView privacyPolicy() {
    logger.entry("begin privacyPolicy()");
    ModelMap map = new ModelMap();
    logger.exit("privacyPolicy() - Ends");
    return new ModelAndView("privacypolicy", map);
  }

  @RequestMapping("/terms.do")
  public ModelAndView termsAndConditions() {
    logger.entry("begin termsAndConditions()");
    ModelMap map = new ModelMap();
    logger.exit("termsAndConditions() - Ends");
    return new ModelAndView("termsAndCondition", map);
  }

  @RequestMapping("/validateSecurityToken.do")
  public ModelAndView securityTokenValidate(HttpServletRequest request) {
    ModelMap map = new ModelMap();
    logger.entry("begin createPassword()");
    String securityToken = null;
    boolean checkSecurityToken = false;
    UserBO userBO = null;
    ModelAndView mv = new ModelAndView();
    try {
      if (null != request.getSession(false).getAttribute("sucMsg")) {
        map.addAttribute("sucMsg", request.getSession(false).getAttribute("sucMsg"));
        request.getSession(false).removeAttribute("sucMsg");
      }
      if (null != request.getSession(false).getAttribute("errMsg")) {
        map.addAttribute("errMsg", request.getSession(false).getAttribute("errMsg"));
        request.getSession(false).removeAttribute("errMsg");
      }
      securityToken =
          FdahpStudyDesignerUtil.isNotEmpty(request.getParameter("securityToken"))
              ? request.getParameter("securityToken")
              : "";
      userBO = loginService.checkSecurityToken(securityToken);
      map.addAttribute("securityToken", securityToken);
      if (userBO != null) {
        checkSecurityToken = true;
      }
      map.addAttribute("isValidToken", checkSecurityToken);
      mv = new ModelAndView("emailChangeVarificationPage", map);
    } catch (Exception e) {
      logger.error("LoginController - createPassword() - ERROR ", e);
    }
    logger.exit("createPassword() - Ends");
    return mv;
  }

  @RequestMapping("/sessionOut.do")
  public ModelAndView sessionOut(
      HttpServletRequest request,
      HttpServletResponse response,
      @RequestParam(value = "msg", required = false) String msg,
      @RequestParam(value = "sucMsg", required = false) String sucMsg) {
    logger.entry("begin sessionOut()");
    SessionObject sesObj = null;
    AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
    sesObj =
        (SessionObject)
            request.getSession(false).getAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT);
    try {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication();
      if (auth != null) {
        loginService.logUserLogOut(sesObj);
        new SecurityContextLogoutHandler().logout(request, response, auth);
        auditRequest.setSource(USER_SIGNOUT_SUCCEEDED.getSource().getValue());
        auditRequest.setDestination(USER_SIGNOUT_SUCCEEDED.getDestination().getValue());
        auditLogEventHelper.logEvent(USER_SIGNOUT_SUCCEEDED, auditRequest);
        auditLogEventHelper.logEvent(SESSION_EXPIRY, auditRequest);
      }
      request.getSession(true).setAttribute("errMsg", msg);
      request.getSession(true).setAttribute("sucMsg", sucMsg);
    } catch (Exception e) {
      logger.error("LoginController - sessionOut() - ERROR ", e);
      auditRequest.setSource(USER_SIGNOUT_FAILED.getSource().getValue());
      auditRequest.setDestination(USER_SIGNOUT_FAILED.getDestination().getValue());
      auditLogEventHelper.logEvent(USER_SIGNOUT_FAILED, auditRequest);
    }
    logger.exit("sessionOut() - Ends");
    return new ModelAndView("redirect:login.do");
  }

  @Autowired
  public void setLoginService(LoginServiceImpl loginService) {
    this.loginService = loginService;
  }

  @RequestMapping("/termsAndCondition.do")
  public ModelAndView termsAndCondition() {
    logger.entry("begin termsAndCondition()");
    ModelMap map = new ModelMap();
    logger.exit("termsAndCondition() - Ends");
    return new ModelAndView("termsAndCondition", map);
  }

  @RequestMapping(value = "/unauthorized.do")
  public ModelAndView unauthorized() {
    logger.entry("begin unauthorized()");
    return new ModelAndView("unauthorized");
  }

  @RequestMapping("/validateEmailChangeVerification.do")
  public ModelAndView validateEmailChangeVerification(HttpServletRequest request) {
    logger.entry("begin addPassword()");
    String securityToken = null;
    ModelAndView mv = new ModelAndView("redirect:login.do");
    try {
      securityToken =
          FdahpStudyDesignerUtil.isNotEmpty(request.getParameter("securityToken"))
              ? request.getParameter("securityToken")
              : "";
      loginService.validateEmailChangeVerification(securityToken);
    } catch (Exception e) {
      logger.error("LoginController - addPassword() - ERROR ", e);
    }
    logger.exit("addPassword() - Ends");
    return mv;
  }

  @RequestMapping("/createPassword.do")
  public ModelAndView validateSecurityToken(HttpServletRequest request) {
    logger.entry("begin createPassword()");
    ModelAndView mv = new ModelAndView("redirect:login.do");
    Map<String, String> configMap = FdahpStudyDesignerUtil.getAppProperties();
    try {
      AuditLogEventRequest auditRequest = AuditEventMapper.fromHttpServletRequest(request);
      ModelMap map = new ModelMap();
      if (null != request.getSession(false).getAttribute("sucMsg")) {
        map.addAttribute("sucMsg", request.getSession(false).getAttribute("sucMsg"));
        request.getSession(false).removeAttribute("sucMsg");
      }
      if (null != request.getSession(false).getAttribute("errMsg")) {
        map.addAttribute("errMsg", request.getSession(false).getAttribute("errMsg"));
        request.getSession(false).removeAttribute("errMsg");
      }
      String securityToken =
          FdahpStudyDesignerUtil.isNotEmpty(request.getParameter("securityToken"))
              ? request.getParameter("securityToken")
              : "";
      UserBO userBO = loginService.checkSecurityToken(securityToken);
      boolean isValidToken = userBO != null;

      map.addAttribute("securityToken", securityToken);
      MasterDataBO masterDataBO = dashBoardAndProfileService.getMasterData("terms");

      boolean isInactiveUser = loginService.isInactiveUser(securityToken);

      map.addAttribute("isValidToken", isValidToken);
      map.addAttribute("isInactiveUser", isInactiveUser);
      map.addAttribute("masterDataBO", masterDataBO);
      if ((userBO != null) && (StringUtils.isEmpty(userBO.getUserPassword()))) {
        map.addAttribute("userBO", userBO);
        map.addAttribute("orgName", configMap.get("orgName"));
        mv = new ModelAndView("signUpPage", map);
      } else {
        mv = new ModelAndView("userPasswordReset", map);
      }
    } catch (Exception e) {
      logger.error("LoginController - createPassword() - ERROR ", e);
    }
    logger.exit("createPassword() - Ends");
    return mv;
  }
}
