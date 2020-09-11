/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.service;

import com.google.cloud.healthcare.fdamystudies.usermgmt.model.UserRegAdminUser;

public interface UserRegAdminUserService {

  UserRegAdminUser save(UserRegAdminUser adminUser);
}
