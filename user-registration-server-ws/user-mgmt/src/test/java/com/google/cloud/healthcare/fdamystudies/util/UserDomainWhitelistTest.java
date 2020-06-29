package com.google.cloud.healthcare.fdamystudies.util;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = { "email.whitelisted_domains=domain1.com,domain2.net" })
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
public class UserDomainWhitelistTest {

  @Autowired
  UserDomainWhitelist whitelist;

  @Test
  public void isWhitelisted() {
    assertThat(whitelist.isValidDomain("wamills@domain1.com")).isEqualTo(true);
    assertThat(whitelist.isValidDomain("wamills@domain2.net")).isEqualTo(true);
  }

  @Test
  public void isNotWhitelisted() {
    assertThat(whitelist.isValidDomain("wamills@domain1.net")).isEqualTo(false);
    assertThat(whitelist.isValidDomain("wamills@domain2.com")).isEqualTo(false);
  }
}
