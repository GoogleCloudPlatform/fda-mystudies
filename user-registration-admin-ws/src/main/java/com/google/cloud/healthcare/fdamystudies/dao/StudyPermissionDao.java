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
import com.google.cloud.healthcare.fdamystudies.model.StudyPermission;

public interface StudyPermissionDao {

  public StudyPermission getStudyPermissionForUser(Integer studyId, Integer userId)
      throws SystemException;

  List<Integer> getStudyPermission(Integer adminUserId, Integer appInfoId) throws SystemException;

  List<StudyPermission> getStudyPermissionDetails(Integer adminUserId, Integer appInfoId)
      throws SystemException;
}
