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

import com.google.cloud.healthcare.fdamystudies.exceptions.SystemException;
import com.google.cloud.healthcare.fdamystudies.model.UserAppDetailsBO;

/**
 * user-management-service-bundle
 *
 * @author Chiranjibi Dash
 */
public interface UserAppDetailsDao {

  UserAppDetailsBO save(UserAppDetailsBO userAppDetails) throws SystemException;
}
