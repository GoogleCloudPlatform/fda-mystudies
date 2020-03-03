/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;

@RestController
@RequestMapping(value = "/user/")
public class UserLoginController {

  private static final Logger logger = LogManager.getLogger(UserLoginController.class);

  private static final Gson gson = new Gson();

  @RequestMapping(value = "ping")
  public ResponseEntity<?> ping() {
    logger.info(" UserLoginController - ping()");
    return new ResponseEntity<>(gson.toJson("UR  web app ws API works!"), HttpStatus.OK);
  }
}
