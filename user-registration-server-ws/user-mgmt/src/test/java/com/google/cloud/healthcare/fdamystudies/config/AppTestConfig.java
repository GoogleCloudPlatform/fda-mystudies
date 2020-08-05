package com.google.cloud.healthcare.fdamystudies.config;

import static org.mockito.Mockito.mock;

import com.google.cloud.healthcare.fdamystudies.util.EmailNotification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("mockit")
@Configuration
public class AppTestConfig {

  @Bean
  @Primary
  public EmailNotification emailNotification() throws Exception {
    return mock(EmailNotification.class);
  }
}
