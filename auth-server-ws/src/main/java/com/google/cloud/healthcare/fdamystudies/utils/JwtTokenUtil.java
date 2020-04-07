/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.utils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;
import com.google.cloud.healthcare.fdamystudies.model.DaoUserBO;
import com.google.cloud.healthcare.fdamystudies.repository.UserRepository;
import com.google.cloud.healthcare.fdamystudies.service.UserSessionService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

  private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

  private static final long serialVersionUID = -2550185165626007488L;

  @Autowired private UserSessionService sessionService;

  @Autowired private UserRepository userRepo;

  @Autowired private ApplicationPropertyConfiguration config;

  @Value("${jwt.secret}")
  private String secret;

  public AuthInfoBO generateToken(
      UserDetails userDetails, String appId, String orgId, String appCode) throws SystemException {
    logger.info("JwtTokenUtil generateToken() - starts");
    Claims claims = Jwts.claims().setSubject(userDetails.getUsername());
    claims.put("Authorities", userDetails.getAuthorities());

    AuthInfoBO token = prepareToken(claims, userDetails.getUsername(), appId, orgId, appCode);
    logger.info("JwtTokenUtil generateToken() - ends");

    return token;
  }

  private AuthInfoBO prepareToken(
      Map<String, Object> claims, String subject, String appId, String orgId, String appCode)
      throws SystemException {

    logger.info("JwtTokenUtil prepareToken() - starts");

    try {
      String token = getToken(claims, subject);

      DaoUserBO userDetails = null;
      if ("MA".equals(appCode)) {
        userDetails =
            userRepo.findByEmailIdAndAppIdAndOrgIdAndAppCode(subject, appId, orgId, appCode);
      } else {
        userDetails = userRepo.findByEmailIdAndAppCode(subject, appCode);
      }
      AuthInfoBO updatedSession = null;
      if (userDetails != null) {
        AuthInfoBO sessionDetails = sessionService.loadSessionByUserId(userDetails.getUserId());
        if (sessionDetails == null) {
          AuthInfoBO session = new AuthInfoBO();
          session.setExpireDate(
              LocalDateTime.now(ZoneId.systemDefault())
                  .plusMinutes(Long.valueOf(config.getSessionTimeOutInMinutes())));
          session.setAccessToken(token);
          // prepare RefreshToken
          String refreshToken =
              RandomStringUtils.randomAlphanumeric(15)
                  + "-"
                  + RandomStringUtils.randomAlphanumeric(15)
                  + "-"
                  + RandomStringUtils.randomAlphanumeric(15);
          session.setRefreshToken(refreshToken);

          String clientToken =
              RandomStringUtils.randomAlphanumeric(15)
                  + "-"
                  + RandomStringUtils.randomAlphanumeric(15)
                  + "-"
                  + RandomStringUtils.randomAlphanumeric(15);
          session.setClientToken(clientToken);

          String userId = getUserId(subject, appId, orgId, appCode);
          session.setUserId(userId);
          updatedSession = sessionService.save(session);
          logger.info("JwtTokenUtil prepareToken() - ends");
        } else {
          // USER TRYING TO LOGIN AGAIN
          sessionDetails.setAccessToken(token);

          String refreshToken =
              RandomStringUtils.randomAlphanumeric(15)
                  + "-"
                  + RandomStringUtils.randomAlphanumeric(15)
                  + "-"
                  + RandomStringUtils.randomAlphanumeric(15);
          sessionDetails.setRefreshToken(refreshToken);
          sessionDetails.setExpireDate(
              LocalDateTime.now(ZoneId.systemDefault())
                  .plusMinutes(Long.valueOf(config.getSessionTimeOutInMinutes())));
          String clientToken =
              RandomStringUtils.randomAlphanumeric(15)
                  + "-"
                  + RandomStringUtils.randomAlphanumeric(15)
                  + "-"
                  + RandomStringUtils.randomAlphanumeric(15);
          sessionDetails.setClientToken(clientToken);
          // Finally update in DB
          updatedSession = sessionService.save(sessionDetails);
        }
      }

      logger.info("JwtTokenUtil prepareToken() - ends");
      return updatedSession;
    } catch (Exception e) {
      logger.error("JwtTokenUtil prepareToken() - error() ", e);
      throw new SystemException();
    }
  }

  private String getUserId(String subject, String appId, String orgId, String appCode)
      throws SystemException {

    logger.info("JwtTokenUtil getUserId() - starts");
    try {
      if (subject != null) {
        DaoUserBO userDetails =
            userRepo.findByEmailIdAndAppIdAndOrgIdAndAppCode(subject, appId, orgId, appCode);

        if (userDetails != null) {
          return userDetails.getUserId();
        } else return null;

      } else {
        logger.info("JwtTokenUtil getUserId() - ends");
        return null;
      }
    } catch (Exception e) {
      logger.error("JwtTokenUtil getUserId() - error() ", e);
      throw new SystemException();
    }
  }

  private String getToken(Map<String, Object> claims, String subject) {
    logger.info("JwtTokenUtil.getToken(): token created");

    return Jwts.builder()
        .setClaims(claims)
        .setSubject(subject)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }
}
