/*******************************************************************************
 * Copyright 2020 Google LLC
 * 
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 ******************************************************************************/
package com.google.cloud.healthcare.fdamystudies.bean;

import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;

/**
 * Provides active task metadata details in response. i.e. status and metadata information of
 * activity {@link ActiveTaskActivityStructureBean}.
 * 
 *
 * 
 */
public class ActiveTaskActivityMetaDataBean {

  private String message = AppConstants.FAILURE;
  private ActiveTaskActivityStructureBean activity = new ActiveTaskActivityStructureBean();

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public ActiveTaskActivityStructureBean getActivity() {
    return activity;
  }

  public void setActivity(ActiveTaskActivityStructureBean activity) {
    this.activity = activity;
  }

}
