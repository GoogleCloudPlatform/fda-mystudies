/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

import javax.annotation.PostConstruct;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
    value = "commonservice.encryptor.enabled",
    havingValue = "true",
    matchIfMissing = false)
public class TextEncryptor {

  @Value("${encryptor.password}")
  private String encryptorPassword;

  private AES256TextEncryptor encryptor;

  @PostConstruct
  public void init() {
    encryptor = new AES256TextEncryptor();
    encryptor.setPasswordCharArray(encryptorPassword.toCharArray());
  }

  public String encrypt(String text) {
    return encryptor.encrypt(text);
  }

  public String decrypt(String encryptedText) {
    return encryptor.decrypt(encryptedText);
  }
}
