/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.google.cloud.healthcare.fdamystudies.consent.model")
public class MyStudiesUserRegWsApplication {

  public static void main(String[] args) {

    SpringApplication.run(MyStudiesUserRegWsApplication.class, args);
  }
}
