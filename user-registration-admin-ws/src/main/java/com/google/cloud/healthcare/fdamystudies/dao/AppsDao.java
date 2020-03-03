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
import java.util.Map;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.AppInfoDetailsBO;
import com.google.cloud.healthcare.fdamystudies.model.AppPermission;
import com.google.cloud.healthcare.fdamystudies.model.ParticipantStudiesBO;
import com.google.cloud.healthcare.fdamystudies.model.StudyInfoBO;

public interface AppsDao {
  public Map<Integer, Long> getAppUsersCount(List<Integer> usersAppsIds);

  public List<AppPermission> getAppPermissionsOfUserByAppIds(
      List<Integer> usersAppsIds, Integer userId);

  Integer getUserAdminId(String userId) throws SystemException;

  AppInfoDetailsBO getAppInfoDetails(Integer appId) throws SystemException;

  List<StudyInfoBO> getAppsStudies(Integer appInfoId) throws SystemException;

  List<ParticipantStudiesBO> getParticipantEnrollments(
      List<Integer> appsStudyInfoIds, List<Integer> userDetailsIds) throws SystemException;

  List<AppInfoDetailsBO> getAllApps() throws SystemException;
}
