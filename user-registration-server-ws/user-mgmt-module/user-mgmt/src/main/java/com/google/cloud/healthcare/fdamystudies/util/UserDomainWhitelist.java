/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.util;

import com.google.cloud.healthcare.fdamystudies.config.ApplicationPropertyConfiguration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserDomainWhitelist {

  private static Logger logger = LoggerFactory.getLogger(UserDomainWhitelist.class);

  @Autowired private ApplicationPropertyConfiguration appConfig;

  Optional<HashSet<String>> whitelistedDomains;

  // Expects `whitelistedUserDomains` is a comma separated string of domains to whitelist.
  @PostConstruct
  public void initializeUserDomainWhitelist() {
    String domains = appConfig.getWhitelistedUserDomains();
    if (domains.isEmpty()) {
      logger.info("No user domain whitelist specified. All domains allowed.");
      whitelistedDomains = Optional.empty();
    } else {
      logger.info("User domain whitelist specified. Will filter non-whitelisted domains.");
      whitelistedDomains = Optional.of(new HashSet<String>(Arrays.asList(domains.split(",", -1))));
    }
  }

  // Returns true if an email address domain is whitelisted for use. If no whitelist is present
  // then returns true by default.
  public Boolean isValidDomain(String email) {
    if (!whitelistedDomains.isPresent()) return true;
    return whitelistedDomains.get().contains(email.substring(email.lastIndexOf("@") + 1));
  }
};
