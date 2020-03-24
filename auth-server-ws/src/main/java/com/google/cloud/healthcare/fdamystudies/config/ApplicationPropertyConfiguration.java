package com.google.cloud.healthcare.fdamystudies.config;

import java.io.Serializable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import lombok.Getter;
import lombok.Setter;

@Configuration
// @PropertySource("classpath:application-${spring.profiles.active}.properties")
@PropertySource("classpath:appConfigurations.properties")
@Setter
@Getter
public class ApplicationPropertyConfiguration implements Serializable {

  private static final long serialVersionUID = 2189883675260389666L;

  @Value("${sessionTimeOutInMinutes}")
  private String sessionTimeOutInMinutes;

  @Value("${max.login.attempts}")
  private String maxLoginAttempts;

  @Value("${expiration.login.attempts.minute}")
  private String expirationLoginAttemptsMinute;

  @Value("${from.email.address}")
  private String fromEmailAddress;

  @Value("${from.email.password}")
  private String fromEmailPassword;

  @Value("${factory.value}")
  private String sslFactoryValue;

  @Value("${port}")
  private String smtpPortValue;

  @Value("${host.name}")
  private String smtpHostName;

  @Value("${password.expiration.in.day}")
  private String passwdExpiryInDay;

  @Value("${password.expiration.in.min}")
  private String passwdExpiryInMin;

  @Value("${passwd.reset.link.subject}")
  private String passwdResetLinkSubject;

  @Value("${passwd.reset.link.content}")
  private String passwdResetLinkContent;

  @Value("${password.history.count}")
  private String passwordHistoryCount;

  @Value("${interceptor}")
  private String interceptorUrls;

  @Value("${verification.expiration.in.hour}")
  private String verificationExpInHr;
}
