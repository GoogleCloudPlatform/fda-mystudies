/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.exception.SystemException;

public interface ClientService {

  public boolean isValidClient(String clientToken, String userId) throws SystemException;

  public String checkClientInfo(String clientId, String secretKey) throws SystemException;
}
