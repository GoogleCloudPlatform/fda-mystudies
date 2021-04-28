/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.PARTICIPANT_ID_NOT_RECEIVED;
import static com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEvent.PARTICIPANT_ID_RECEIVED;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentBodyProvider;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBodyProvider;
import com.google.cloud.healthcare.fdamystudies.common.EnrollAuditEventHelper;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.OAuthService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import javax.validation.constraints.NotNull;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class EnrollmentManagementUtil {

  private static final Random generator = new Random();
  static final String SOURCE = "0123456789abcdefghijklmnopqrstuvwxyz";
  static SecureRandom secureRnd = new SecureRandom();
  private static final String validInputChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private Integer charLength = validInputChars.length();
  private static final XLogger logger =
      XLoggerFactory.getXLogger(EnrollmentManagementUtil.class.getName());

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired EnrollAuditEventHelper enrollAuditEventHelper;

  @Autowired private OAuthService oAuthService;

  public boolean isChecksumValid(@NotNull String token) {
    try {
      //      return isValid(token.toUpperCase());
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  public boolean isValid(String input) {
    return (!input.isEmpty() && ((codePointTotal(input, true) % charLength) == 0));
  }

  private int codePointTotal(String input, boolean withChecksum) {
    int factor = withChecksum ? 1 : 2;
    int sum = 0;

    // Starting from the right and working leftwards is easier since
    // the initial "factor" will always be "2"
    for (int i = input.length() - 1; i >= 0; i--) {
      int codePoint = codePointFromCharacter(input.charAt(i));
      if (codePoint < 0) {
        throw new IllegalArgumentException(
            "Input string '"
                + input
                + "' not from valid input character set '"
                + validInputChars
                + "'");
      }
      int addend = factor * codePoint;

      // Alternate the "factor" that each "codePoint" is multiplied by
      factor = (factor == 2) ? 1 : 2;

      // Sum the digits of the "addend" as expressed in base "n"
      addend = (addend / charLength) + (addend % charLength);
      sum += addend;
    }
    return sum;
  }

  private int codePointFromCharacter(char character) {
    return validInputChars.indexOf(character);
  }

  public static String randomString(int length) {
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(SOURCE.charAt(secureRnd.nextInt(SOURCE.length())));
    }
    return sb.toString();
  }

  public static Date getCurrentUtilDateTime() {
    Date date = new Date();
    Calendar currentDate = Calendar.getInstance();
    String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate.getTime());
    try {
      date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateNow);
    } catch (Exception e) {
      logger.error("EnrollmentManagementUtil - getCurrentUtilDateTime() :: ERROR ", e);
    }
    return date;
  }

  public static String getHashedValue(String input) {
    String generatedHash = null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] bytes = md.digest(input.getBytes());
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < bytes.length; i++) {
        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
      }
      generatedHash = sb.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.error("No Such Algorithm Exception: ", e);
    }
    return generatedHash;
  }

  public String getParticipantId(
      String applicationId,
      String hashedTokenValue,
      String studyId,
      Float studyVersion,
      AuditLogEventRequest auditRequest) {
    logger.entry("Begin getParticipantId()");
    HttpHeaders headers = null;
    EnrollmentBodyProvider bodyProvider = null;
    HttpEntity<EnrollmentBodyProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    String participantId = "";

    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("Authorization", "Bearer " + oAuthService.getAccessToken());
      AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

      bodyProvider = new EnrollmentBodyProvider();
      bodyProvider.setTokenIdentifier(hashedTokenValue);
      bodyProvider.setCustomStudyId(studyId);
      bodyProvider.setStudyVersion(String.valueOf(studyVersion));

      requestBody = new HttpEntity<>(bodyProvider, headers);
      responseEntity =
          restTemplate.postForEntity(appConfig.getAddParticipantId(), requestBody, String.class);
      if (responseEntity.getStatusCode() == HttpStatus.OK) {
        participantId = (String) responseEntity.getBody();
        auditRequest.setParticipantId(participantId);

        enrollAuditEventHelper.logEvent(PARTICIPANT_ID_RECEIVED, auditRequest);
      }

    } catch (Exception e) {
      enrollAuditEventHelper.logEvent(PARTICIPANT_ID_NOT_RECEIVED, auditRequest);
      logger.error("EnrollmentManagementUtil getParticipantId() - Ends ", e);
      throw e;
    }

    logger.exit("getParticipantId() - Ends ");
    return participantId;
  }

  public String withDrawParticipantFromStudy(
      String participantId, Float studyVersion, String studyId, AuditLogEventRequest auditRequest) {
    logger.entry("Begin withDrawParticipantFromStudy()");
    HttpHeaders headers = null;
    HttpEntity<WithdrawFromStudyBodyProvider> request = null;
    String message = "";

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + oAuthService.getAccessToken());
    AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

    request = new HttpEntity<>(null, headers);

    String url =
        appConfig.getWithdrawStudyUrl()
            + "?studyId="
            + studyId
            + "&studyVersion="
            + String.valueOf(studyVersion)
            + "&participantId="
            + participantId;

    ResponseEntity<?> response = restTemplate.postForEntity(url, request, String.class);

    if (response.getStatusCode() == HttpStatus.OK) {
      message = "SUCCESS";
    }

    logger.exit("withDrawParticipantFromStudy() - Ends ");
    return message;
  }
}
