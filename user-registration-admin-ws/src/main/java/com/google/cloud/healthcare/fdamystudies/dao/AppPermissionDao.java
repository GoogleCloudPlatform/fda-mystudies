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
import com.google.cloud.healthcare.fdamystudies.model.AppPermission;

public interface AppPermissionDao {

  AppPermission checkPermission(Integer adminId, Integer appId) throws SystemException;

  List<AppPermission> checkPermission(Integer adminId) throws SystemException;
}
