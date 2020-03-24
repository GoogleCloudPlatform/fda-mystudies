/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AuthInfoBO;

public interface AuthInfoBOService {

  AuthInfoBO save(AuthInfoBO authInfo) throws SystemException;
}
