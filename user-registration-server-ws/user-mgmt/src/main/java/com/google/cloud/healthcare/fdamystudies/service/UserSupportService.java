/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

public interface UserSupportService {

  public boolean feedback(String subject, String body);

  public boolean contactUsDetails(String subject, String body, String firstName, String email);
}
