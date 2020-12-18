/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.beans;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppPropertiesDetailsBean {
  private String appId;
  private String appType;
  private String iosBundleId;
  private String androidBundleId;
  private String iosCertificate;
  private String iosCertificatePassword;
  private String email;
  private String emailPassword;
  private String androidServerKey;
  private String registerEmailSubject;
  private String registerEmailBody;
  private String forgotPassEmailSubject;
  private String forgotPassEmailBody;
  private boolean methodHandler;
}
