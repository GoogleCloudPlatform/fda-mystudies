/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import static com.google.cloud.healthcare.fdamystudies.common.UserMgmntEvent.WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusRequest;
import com.google.cloud.healthcare.fdamystudies.beans.UpdateEmailStatusResponse;
import com.google.cloud.healthcare.fdamystudies.beans.WithdrawFromStudyBodyProvider;
import com.google.cloud.healthcare.fdamystudies.common.UserMgmntAuditHelper;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.mapper.AuditEventMapper;
import com.google.cloud.healthcare.fdamystudies.service.OAuthService;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class UserManagementUtil {

  private static final XLogger logger =
      XLoggerFactory.getXLogger(UserManagementUtil.class.getName());

  @Autowired private RestTemplate restTemplate;

  @Autowired private ApplicationPropertyConfiguration appConfig;

  @Autowired private OAuthService oauthService;

  @Autowired UserMgmntAuditHelper userMgmntAuditHelper;

  public UpdateEmailStatusResponse updateUserInfoInAuthServer(
      UpdateEmailStatusRequest updateEmailStatusRequest,
      String userId,
      AuditLogEventRequest auditRequest) {
    logger.entry("Begin updateUserInfoInAuthServer()");

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + oauthService.getAccessToken());
    AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

    HttpEntity<UpdateEmailStatusRequest> request =
        new HttpEntity<>(updateEmailStatusRequest, headers);
    ResponseEntity<UpdateEmailStatusResponse> responseEntity =
        restTemplate.exchange(
            appConfig.getAuthServerUpdateStatusUrl(),
            HttpMethod.PUT,
            request,
            UpdateEmailStatusResponse.class,
            userId);
    UpdateEmailStatusResponse updateEmailResponse = responseEntity.getBody();

    logger.debug(
        String.format(
            "status =%d, message=%s",
            updateEmailResponse.getHttpStatusCode(), updateEmailResponse.getMessage()));
    return updateEmailResponse;
  }

  public static Date getCurrentUtilDateTime() {
    Date date = new Date();
    Calendar currentDate = Calendar.getInstance();
    String dateNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentDate.getTime());
    try {
      date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateNow);
    } catch (Exception e) {
      logger.error("UserManagementUtil - getCurrentUtilDateTime() :: ERROR ", e);
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

  public String withdrawParticipantFromStudy(
      String participantId,
      String studyId,
      String studyVersion,
      AuditLogEventRequest auditRequest) {
    logger.entry("Begin withDrawParticipantFromStudy()");
    HttpHeaders headers = null;
    HttpEntity<WithdrawFromStudyBodyProvider> request = null;

    String message = MyStudiesUserRegUtil.ErrorCodes.FAILURE.getValue();

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + oauthService.getAccessToken());
    AuditEventMapper.addAuditEventHeaderParams(headers, auditRequest);

    request = new HttpEntity<>(null, headers);

    String url =
        appConfig.getWithdrawStudyUrl()
            + "?studyId="
            + studyId
            + "&studyVersion="
            + studyVersion
            + "&participantId="
            + participantId;

    ResponseEntity<?> response = restTemplate.postForEntity(url, request, String.class);

    logger.info("Withdrawal status code" + response.getStatusCode());
    if (response.getStatusCode() == HttpStatus.OK) {
      userMgmntAuditHelper.logEvent(WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE, auditRequest);
      logger.info("WITHDRAWAL_INTIMATED_TO_RESPONSE_DATASTORE");
      message = MyStudiesUserRegUtil.ErrorCodes.SUCCESS.getValue();
    }

    logger.exit("withDrawParticipantFromStudy() - Ends ");
    return message;
  }

  public static String genarateEmailContent(String emailContentName, Map<String, String> keyValue) {
    logger.entry("Begin genarateEmailContent()");
    if (StringUtils.isNotEmpty(emailContentName)) {
      for (Map.Entry<String, String> entry : keyValue.entrySet()) {
        emailContentName =
            emailContentName.replace(
                entry.getKey(), StringUtils.isBlank(entry.getValue()) ? "" : entry.getValue());
      }
    }
    logger.exit("genarateEmailContent() :: Ends");
    return emailContentName;
  }

  public void deleteUserInfoInAuthServer(String userId) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.add("Authorization", "Bearer " + oauthService.getAccessToken());

    HttpEntity<Object> entity = new HttpEntity<>(headers);

    restTemplate.exchange(
        appConfig.getAuthServerDeleteStatusUrl(), HttpMethod.DELETE, entity, Void.class, userId);
  }
}
