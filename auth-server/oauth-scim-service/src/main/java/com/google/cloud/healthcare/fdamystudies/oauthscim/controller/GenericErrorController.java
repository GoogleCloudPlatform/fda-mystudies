/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.oauthscim.controller;

import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.ERROR_VIEW_NAME;
import static com.google.cloud.healthcare.fdamystudies.oauthscim.common.AuthScimConstants.REDIRECT_ERROR_VIEW;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GenericErrorController {
  private XLogger logger = XLoggerFactory.getXLogger(GenericErrorController.class.getName());

  @GetMapping(value = "/oauth2/error")
  public String error(HttpServletRequest request, HttpServletResponse response, Model model)
      throws UnsupportedEncodingException {
    String errorDetails = request.getQueryString();
    if (StringUtils.isEmpty(errorDetails)) {
      return ERROR_VIEW_NAME;
    }

    logger.error(String.format("Failed due to %s", URLDecoder.decode(errorDetails, "UTF-8")));
    // redirect added to remove error details in URL
    response.setHeader("Location", ERROR_VIEW_NAME);
    response.setStatus(HttpStatus.FOUND.value());
    return REDIRECT_ERROR_VIEW;
  }
}
