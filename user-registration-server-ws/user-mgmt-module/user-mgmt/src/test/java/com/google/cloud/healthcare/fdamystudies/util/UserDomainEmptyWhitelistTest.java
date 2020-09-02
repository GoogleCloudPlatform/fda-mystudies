/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.cloud.healthcare.fdamystudies.TestApplicationContextInitializer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

// TODO (#761) Added @Ignore to test classes written by UNC team, should be fixed later or next
// track

@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"email.whitelisted_domains="})
@ActiveProfiles("test")
@ContextConfiguration(initializers = TestApplicationContextInitializer.class)
@Ignore
public class UserDomainEmptyWhitelistTest {

  @Autowired UserDomainWhitelist whitelist;

  @Test
  public void emptyWhitelist() {
    assertThat(whitelist.isValidDomain("wamills@domain1.net")).isEqualTo(true);
    assertThat(whitelist.isValidDomain("wamills@domain2.com")).isEqualTo(true);
  }
}
