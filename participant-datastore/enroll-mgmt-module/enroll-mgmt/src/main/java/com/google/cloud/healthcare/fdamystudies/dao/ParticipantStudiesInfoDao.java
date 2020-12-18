/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.dao;

import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudyEntity;
import java.util.List;
import javax.transaction.SystemException;

public interface ParticipantStudiesInfoDao {

  List<ParticipantStudyEntity> getParticipantStudiesInfo(String userDetailsId)
      throws SystemException;
}
