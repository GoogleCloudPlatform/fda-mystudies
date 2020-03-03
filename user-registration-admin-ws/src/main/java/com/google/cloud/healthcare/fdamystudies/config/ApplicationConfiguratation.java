/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Configuration
@PropertySources({
  @PropertySource("classpath:application-appConfig.properties"),
  @PropertySource("classpath:application-${spring.profiles.active}.properties")
})
public class ApplicationConfiguratation {
  // application-appConfig.properties properties

  @Value("${fromEmailAddress}")
  private String fromEmailAddress;

  @Value("${fromEmailPasswod}")
  private String fromEmailPasswod;

  @Value("${sslFactoryValue}")
  private String sslFactoryValue;

  @Value("${smtpPortValue}")
  private String smtpPortValue;

  @Value("${smtpHostName}")
  private String smtpHostName;

  @Value("${sessionTimeOutInMinutes}")
  private String sessionTimeOutInMinutes;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${authServerAccessTokenValidationUrl}")
  private String authServerAccessTokenValidationUrl;

  @Value("${clientId}")
  private String clientId;

  @Value("${secretKey}")
  private String secretKey;

  @Value("${auth.server.url}")
  private String authServerUrl;

  @Value("${register.url}")
  private String authServerRegisterStatusUrl;

  @Value("${auth.server.updateStatusUrl}")
  private String authServerUpdateStatusUrl;

  @Value("${auth.server.deleteStatusUrl}")
  private String authServerDeleteStatusUrl;

  @Value("${participant.invite.subject}")
  private String participantInviteSubject;

  @Value("${participant.invite.body}")
  private String participantInviteBody;

  @Value("${newlyCreatedTimeframeMinutes}")
  private Integer newlyCreatedTimeframeMinutes;

  @Value("${appEnv}")
  private String appEnv;
}
