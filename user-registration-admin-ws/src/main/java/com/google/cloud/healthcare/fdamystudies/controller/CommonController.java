/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.cloud.healthcare.fdamystudies.service.CommonService;

@RestController
@RequestMapping("/")
public class CommonController {

  private static Logger logger = LoggerFactory.getLogger(CommonController.class);

  @Autowired CommonService commonService;

  @GetMapping("ping")
  public String testPing() {
    String pingMessage = "";
    logger.info("testPing() : Starts");
    try {
      pingMessage = "ur web app ws API works!.. :)";
    } catch (Exception e) {
      logger.error("testPing() : ERROR", e);
    }
    logger.info("testPing() : Ends");
    return pingMessage;
  }
}
