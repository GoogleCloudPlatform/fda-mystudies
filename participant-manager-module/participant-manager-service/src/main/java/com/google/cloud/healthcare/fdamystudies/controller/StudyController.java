/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.controller;

import static com.google.cloud.healthcare.fdamystudies.common.CommonConstants.USER_ID_HEADER;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.healthcare.fdamystudies.beans.StudyResponse;
import com.google.cloud.healthcare.fdamystudies.service.StudyService;

@RestController
@RequestMapping("/studies")
public class StudyController {
  private XLogger logger = XLoggerFactory.getXLogger(StudyController.class.getName());

  private static final String STATUS_LOG = "status=%d";

  private static final String BEGIN_REQUEST_LOG = "%s request";

  @Autowired private StudyService studyService;

  @GetMapping
  public ResponseEntity<StudyResponse> getStudies(
      @RequestHeader(name = USER_ID_HEADER) String userId, HttpServletRequest request) {
    logger.entry(BEGIN_REQUEST_LOG, request.getRequestURI());
    StudyResponse studyResponse = studyService.getStudies(userId);
    logger.exit(String.format(STATUS_LOG, studyResponse.getHttpStatusCode()));
    return ResponseEntity.status(studyResponse.getHttpStatusCode()).body(studyResponse);
  }
}
