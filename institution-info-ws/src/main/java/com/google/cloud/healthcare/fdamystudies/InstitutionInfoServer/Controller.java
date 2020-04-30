package com.google.cloud.healthcare.fdamystudies.InstitutionInfoServer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class Controller {

  @GetMapping("/healthCheck")
  public ResponseEntity<?> healthCheck() {
	  return ResponseEntity.ok("Institution Info Server up and running!");
  }

}
