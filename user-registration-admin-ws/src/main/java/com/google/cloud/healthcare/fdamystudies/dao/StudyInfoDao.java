/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.dao;

import java.util.List;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;

public interface StudyInfoDao {
  StudyInfoBO getStudyInfoDetails(Integer studyId) throws SystemException;

  List<StudyInfoBO> getStudies(List<Integer> appInfoIdList) throws SystemException;
}
