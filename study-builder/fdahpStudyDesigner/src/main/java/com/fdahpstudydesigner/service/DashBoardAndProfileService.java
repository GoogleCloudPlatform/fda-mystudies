/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bo.MasterDataBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.SessionObject;

public interface DashBoardAndProfileService {

  public MasterDataBO getMasterData(String type);

  public String isEmailValid(String email);

  public String updateProfileDetails(UserBO userBO, String userId, SessionObject userSession);
}
