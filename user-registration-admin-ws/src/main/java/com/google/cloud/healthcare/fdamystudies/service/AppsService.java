/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.service;

import java.util.List;
import com.google.cloud.healthcare.fdamystudies.bean.DashboardBean;
import com.google.cloud.healthcare.fdamystudies.bean.GetAppParticipantRegistryServiceResponse;
import com.google.cloud.healthcare.fdamystudies.bean.GetAppsDetailsServiceResponseBean;
import com.google.cloud.healthcare.fdamystudies.exception.InvalidUserIdException;
import com.google.cloud.healthcare.fdamystudies.exception.SystemException;

public interface AppsService {
  public DashboardBean getApps(Integer userId);

  List<GetAppsDetailsServiceResponseBean> getAppsDetails(Integer userId)
      throws SystemException, InvalidUserIdException;

  public GetAppParticipantRegistryServiceResponse getAppParticipantRegistry(
      Integer appId, Integer adminId) throws SystemException, InvalidUserIdException;
}
