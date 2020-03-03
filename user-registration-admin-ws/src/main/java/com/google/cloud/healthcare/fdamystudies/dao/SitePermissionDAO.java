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
import com.google.cloud.healthcare.fdamystudies.model.SitePermission;

public interface SitePermissionDAO {

  public SitePermission getSitePermissionForUser(Integer siteId, Integer userId);

  public List<Integer> getSiteIdList(Integer adminUserId, Integer appInfoId, Integer studyId)
      throws SystemException;

  public List<SitePermission> getSiteIdListDetails(
      Integer adminUserId, Integer appInfoId, Integer studyId) throws SystemException;
}
