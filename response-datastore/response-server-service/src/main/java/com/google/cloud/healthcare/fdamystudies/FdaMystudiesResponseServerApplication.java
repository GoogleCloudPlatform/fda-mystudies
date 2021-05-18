/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */
package com.google.cloud.healthcare.fdamystudies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.google.cloud.healthcare.fdamystudies"})
@EntityScan("com.google.cloud.healthcare.fdamystudies.response.model")
public class FdaMystudiesResponseServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(FdaMystudiesResponseServerApplication.class, args);
  }
}
