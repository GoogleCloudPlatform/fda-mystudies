/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import java.util.List;

public interface ParticipantStudiesInfoDao {

  List<ParticipantStudiesBO> getParticipantStudiesInfo(Integer userDetailsId)
      throws SystemException;
}
