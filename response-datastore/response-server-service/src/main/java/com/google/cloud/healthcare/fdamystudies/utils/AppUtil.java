/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.healthcare.v1.CloudHealthcare;
import com.google.api.services.healthcare.v1.CloudHealthcareScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.healthcare.fdamystudies.bean.ErrorBean;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

public class AppUtil {
  private static final XLogger logger = XLoggerFactory.getXLogger(AppUtil.class.getName());
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();
  private static final NetHttpTransport HTTP_TRANSPORT = new NetHttpTransport();

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

  public static String convertDateToOtherFormat(
      String dateString, Locale locale, String inputFormat, String outputFormat)
      throws ParseException {
    DateFormat sdf = new SimpleDateFormat(inputFormat);
    DateFormat sdf1 = new SimpleDateFormat(outputFormat);
    sdf1.setTimeZone(TimeZone.getTimeZone(locale.getDisplayName()));
    // sdf1.setTimeZone(TimeZone.getTimeZone("GMT" + dateString.substring(dateString.length() -
    // 5)));

    return sdf1.format(sdf.parse(dateString));
  }

  public static CloudHealthcare createClient() throws ProcessResponseException {
    try {
      // Use Application Default Credentials (ADC) to authenticate the requests
      GoogleCredentials credential =
          GoogleCredentials.getApplicationDefault()
              .createScoped(Collections.singleton(CloudHealthcareScopes.CLOUD_PLATFORM));

      // Create a HttpRequestInitializer, which will provide a baseline configuration to all
      // requests.
      HttpRequestInitializer requestInitializer =
          request -> {
            new HttpCredentialsAdapter(credential).initialize(request);
            request.setConnectTimeout(60000); // 1 minute connect timeout
            request.setReadTimeout(60000); // 1 minute read timeout
          };

      // Build the client for interacting with the service.
      return new CloudHealthcare.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
          .setApplicationName("your-application-name")
          .build();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
      throw new ProcessResponseException(e.getMessage());
    }
  }

  public static String convertDateToOtherFormat1(
      String dateString, String inputFormat, String outputFormat) throws ParseException {
    DateFormat sdf = new SimpleDateFormat(inputFormat);
    DateFormat sdf1 = new SimpleDateFormat(outputFormat);
    sdf1.setTimeZone(TimeZone.getTimeZone("GMT" + dateString.substring(dateString.length() - 5)));

    return sdf1.format(sdf.parse(dateString));
  }
}
