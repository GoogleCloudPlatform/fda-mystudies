/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.util;

import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.dao.LoginDAOImpl;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private LoginDAOImpl loginDAO;

  protected String determineTargetUrl(Authentication authentication) {
    logger.info("CustomSuccessHandler - determineTargetUrl - Starts");
    String url = "";

    if (authentication != null) {
      url = "/adminDashboard/viewDashBoard.do?action=landing";
    } else {
      url = "/unauthorized.do";
    }
    logger.info("CustomSuccessHandler - determineTargetUrl - Ends");
    return url;
  }

  @Override
  protected void handle(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {

    String targetUrl = determineTargetUrl(authentication);
    logger.info("targetUrl:" + targetUrl);
    UserBO userdetails;
    SessionObject sesObj;
    Map<String, String> propMap = FdahpStudyDesignerUtil.getAppProperties();
    String projectName = propMap.get("project.name");
    userdetails = loginDAO.getValidUserByEmail(authentication.getName());
    if (userdetails.isForceLogout()) {
      userdetails.setForceLogout(false);
      loginDAO.updateUser(userdetails);
    }
    sesObj = new SessionObject();
    sesObj.setUserId(userdetails.getUserId());
    sesObj.setFirstName(userdetails.getFirstName());
    sesObj.setLastName(userdetails.getLastName());
    sesObj.setLoginStatus(true);
    sesObj.setCurrentHomeUrl("/" + projectName + targetUrl);
    sesObj.setEmail(userdetails.getUserEmail());
    sesObj.setUserPermissions(FdahpStudyDesignerUtil.getSessionUserRole());
    sesObj.setPasswordExpiryDateTime(userdetails.getPasswordExpiryDateTime());
    sesObj.setCreatedDate(userdetails.getCreatedOn());
    sesObj.setRole(userdetails.getRoleName());
    sesObj.setAccessLevel(userdetails.getAccessLevel());
    sesObj.setCorrelationId(UUID.randomUUID().toString());
    sesObj.setGcpBucketName(propMap.get("cloud.bucket.name"));
    sesObj.setStoragePath(propMap.get("fda.storagePath"));
    sesObj.setExportStudiesBucketName(propMap.get("cloud.bucket.name.export.studies"));

    if (response.isCommitted()) {
      logger.info("Can't redirect");
      return;
    }

    request.getSession().setAttribute(FdahpStudyDesignerConstants.SESSION_OBJECT, sesObj);

    if (null != request.getSession(false).getAttribute("sucMsg")) {
      request.getSession(false).removeAttribute("sucMsg");
    }
    if (null != request.getSession(false).getAttribute("errMsg")) {
      request.getSession(false).removeAttribute("errMsg");
    }
    logger.info("loginBackUrl:" + request.getParameter("loginBackUrl"));
    if (StringUtils.isNotBlank(request.getParameter("loginBackUrl"))) {
      String[] uri = request.getParameter("loginBackUrl").split(projectName);
      targetUrl = uri[1];
    }
    logger.info("targetUrl:" + targetUrl);
    JSONObject jsonobject = new JSONObject();
    PrintWriter out = null;
    String message = FdahpStudyDesignerConstants.SUCCESS;
    jsonobject.put(FdahpStudyDesignerConstants.MESSAGE, message);
    response.setContentType(FdahpStudyDesignerConstants.APPLICATION_JSON);
    out = response.getWriter();
    out.print(jsonobject);
  }

  @Autowired
  public void setLoginDAO(LoginDAOImpl loginDAO) {
    this.loginDAO = loginDAO;
  }
}
