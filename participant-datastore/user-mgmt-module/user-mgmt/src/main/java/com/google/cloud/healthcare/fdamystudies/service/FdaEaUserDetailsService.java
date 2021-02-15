/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.beans.AuditLogEventRequest;
import com.google.cloud.healthcare.fdamystudies.model.UserDetailsEntity;

public interface FdaEaUserDetailsService {

  UserDetailsEntity saveUser(UserDetailsEntity userDetails);

  UserDetailsEntity loadUserDetailsByUserId(String userId);

  boolean verifyCode(String code, UserDetailsEntity participantDetails);

  String updateStatus(UserDetailsEntity participantDetails, AuditLogEventRequest auditRequest);
}
