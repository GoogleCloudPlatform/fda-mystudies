/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.beans.EnrollmentBodyProvider;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBodyProvider;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidRequestException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.exception.UnAuthorizedRequestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

@Component
public class EnrollmentManagementUtil {

  private static final Random generator = new Random();
  static final String SOURCE = "0123456789abcdefghijklmnopqrstuvwxyz";
  static SecureRandom secureRnd = new SecureRandom();
  private static final String validInputChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  private Integer charLength = validInputChars.length();
  private static final Logger logger = LoggerFactory.getLogger(EnrollmentManagementUtil.class);

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

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
    System.out.println("validInputChars.indexOf(character)->" + validInputChars.indexOf(character));
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

  public String getParticipantId(String applicationId, String hashedTokenValue, String studyId)
      throws InvalidRequestException, UnAuthorizedRequestException, SystemException {
    logger.info("EnrollmentManagementUtil deactivateAcct() - starts ");
    HttpHeaders headers = null;
    EnrollmentBodyProvider bodyProvider = null;
    HttpEntity<EnrollmentBodyProvider> requestBody = null;
    ResponseEntity<?> responseEntity = null;
    String participantId = "";
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set("applicationId", applicationId);
      headers.set(AppConstants.CLIENT_ID, appConfig.getClientId());
      headers.set(AppConstants.SECRET_KEY, getHashedValue(appConfig.getSecretKey()));
      bodyProvider = new EnrollmentBodyProvider();
      bodyProvider.setTokenIdentifier(hashedTokenValue);
      bodyProvider.setCustomStudyId(studyId);
      requestBody = new HttpEntity<>(bodyProvider, headers);
      responseEntity =
          restTemplate.postForEntity(appConfig.getAddParticipantId(), requestBody, String.class);
      if (responseEntity.getStatusCode() == HttpStatus.OK) {
        participantId = (String) responseEntity.getBody();
      }
    } catch (RestClientResponseException e) {

      if (e.getRawStatusCode() == 401) {
        throw new UnAuthorizedRequestException();
      } else if (e.getRawStatusCode() == 400) {
        throw new InvalidRequestException();
      } else {
        throw new SystemException();
      }
    } catch (Exception e) {
      logger.error("EnrollmentManagementUtil deactivateAcct() - Ends ", e);
      throw new SystemException();
    }
    logger.info("EnrollmentManagementUtil deactivateAcct() - Ends ");
    return participantId;
  }

  public String withDrawParticipantFromStudy(String participantId, String studyId, boolean delete)
      throws UnAuthorizedRequestException, InvalidRequestException, SystemException {
    logger.info("EnrollmentManagementUtil withDrawParticipantFromStudy() - starts ");
    HttpHeaders headers = null;
    HttpEntity<WithdrawFromStudyBodyProvider> request = null;
    String message = "";
    try {
      headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      headers.set(AppConstants.APPLICATION_ID, null);
      headers.set(AppConstants.CLIENT_ID, appConfig.getClientId());
      headers.set(AppConstants.SECRET_KEY, getHashedValue(appConfig.getSecretKey()));

      request = new HttpEntity<>(null, headers);

      String url =
          appConfig.getWithdrawStudyUrl()
              + "?studyId="
              + studyId
              + "&participantId="
              + participantId
              + "&deleteResponses="
              + String.valueOf(delete);

      ResponseEntity<?> response = restTemplate.postForEntity(url, request, String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        message = "SUCCESS";
      }

    } catch (RestClientResponseException e) {
      if (e.getRawStatusCode() == 401) {
        logger.error("Invalid client credential");
        throw new UnAuthorizedRequestException();
      } else if (e.getRawStatusCode() == 400) {
        logger.error("Client verification ended with Bad Request. Missing required parameters");
        throw new InvalidRequestException();
      } else {
        throw new SystemException();
      }
    } catch (Exception e) {
      logger.error("EnrollmentManagementUtil withDrawParticipantFromStudy() - Ends ", e);
      throw new SystemException();
    }
    logger.info("EnrollmentManagementUtil withDrawParticipantFromStudy() - Ends ");
    return message;
  }
}
