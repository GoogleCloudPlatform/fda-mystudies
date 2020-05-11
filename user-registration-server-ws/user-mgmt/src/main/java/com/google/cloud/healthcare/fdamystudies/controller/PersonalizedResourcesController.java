/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import com.google.cloud.healthcare.fdamystudies.beans.UserResourceBean;
import com.google.cloud.healthcare.fdamystudies.beans.UserResourcesBean;
import com.google.cloud.healthcare.fdamystudies.service.PersonalizedUserReportService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonalizedResourcesController {
  private static final Logger logger =
      LoggerFactory.getLogger(PersonalizedResourcesController.class);

  @Autowired PersonalizedUserReportService personalizedUserReportService;

  @GetMapping(value = "/getPersonalizedResources")
  public UserResourcesBean getPersonalizedResources(
      @RequestHeader("userId") String userId,
      @RequestHeader("accessToken") String accessToken,
      @RequestHeader("clientToken") String clientToken,
      @RequestParam(name = "studyId") String studyId) {
    logger.info("UserResourcesController getPersonalizedResources() - starts");
    List<UserResourceBean> personalizedUserReports =
        personalizedUserReportService.getLatestPersonalizedUserReports(userId, studyId);
    logger.info("UserResourcesController getPersonalizedResources() - Ends");
    return new UserResourcesBean(personalizedUserReports);
  }
}
