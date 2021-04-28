/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class AppUtil {
  private static final XLogger logger = XLoggerFactory.getXLogger(AppUtil.class.getName());

  public static ErrorBean dynamicResponse(
      int code, String userMessage, String type, String detailMessage) {
    ErrorBean error = null;
    try {
      error = new ErrorBean(code, userMessage, type, detailMessage);
    } catch (Exception e) {
      logger.error("ERROR: AppUtil - dynamicResponse() - error()", e);
    }
    return error;
  }

  public static String makeStudyCollectionName(String studyId) {
    if (!StringUtils.isBlank(studyId)) {
      return studyId + AppConstants.HYPHEN + AppConstants.RESPONSES;
    }
    return null;
  }

  public static String makeParticipantCollectionName(String studyId, String siteId) {
    if (!StringUtils.isBlank(studyId) && !StringUtils.isBlank(siteId)) {
      return studyId
          + AppConstants.HYPHEN
          + siteId
          + AppConstants.HYPHEN
          + AppConstants.PARTICIPANT_METADATA_KEY;
    }
    return null;
  }

  public static String makeActivitiesCollectionName(String studyId, String siteId) {
    if (!StringUtils.isBlank(studyId) && !StringUtils.isBlank(siteId)) {
      return studyId
          + AppConstants.HYPHEN
          + siteId
          + AppConstants.HYPHEN
          + AppConstants.ACTIVITIES_COLLECTION_NAME;
    }
    return null;
  }
}
