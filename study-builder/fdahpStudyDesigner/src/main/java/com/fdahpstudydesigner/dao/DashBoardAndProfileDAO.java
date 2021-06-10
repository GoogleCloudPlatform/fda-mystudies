/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.bo.MasterDataBO;
import com.fdahpstudydesigner.bo.UserBO;

public interface DashBoardAndProfileDAO {

  public MasterDataBO getMasterData(String type);

  public String isEmailValid(String email);

  public String updateProfileDetails(UserBO userBO, String userId);
}
