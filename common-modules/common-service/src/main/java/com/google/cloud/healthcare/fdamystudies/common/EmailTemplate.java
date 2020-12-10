/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.common;

public enum EmailTemplate {
  ACCOUNT_CREATED_EMAIL_TEMPLATE("ACCOUNT_CREATED_EMAIL_TEMPLATE"),
  ACCOUNT_UPDATED_EMAIL_TEMPLATE("ACCOUNT_UPDATED_EMAIL_TEMPLATE");

  private String template;

  private EmailTemplate(String template) {
    this.template = template;
  }

  public String getTemplate() {
    return template;
  }
}
