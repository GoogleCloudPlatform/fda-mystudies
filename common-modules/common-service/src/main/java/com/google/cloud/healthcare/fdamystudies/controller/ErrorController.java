/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.common.ErrorResponse;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ErrorController extends AbstractErrorController {

  private XLogger logger = XLoggerFactory.getXLogger(ErrorController.class.getName());

  public ErrorController(ErrorAttributes errorAttributes) {
    super(errorAttributes);
  }

  @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public ErrorResponse handleError(HttpServletRequest request) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(request, true);
    ErrorResponse er = new ErrorResponse(errorAttributes);
    logger.error(
        String.format(
            "%s failed with error attributes %s", request.getRequestURI(), errorAttributes));
    return er;
  }

  @Override
  public String getErrorPath() {
    return "/error";
  }
}
