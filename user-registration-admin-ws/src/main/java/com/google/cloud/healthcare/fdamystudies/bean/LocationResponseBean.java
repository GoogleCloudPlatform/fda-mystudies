/**
 * *****************************************************************************
 *
 * <p>Copyright 2020 Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * *****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LocationResponseBean {

  private List<LocationBean> locations;

  private ErrorBean errorBean;

  public List<LocationBean> getLocations() {
    return locations;
  }

  public void setLocations(List<LocationBean> locations) {
    this.locations = locations;
  }

  public ErrorBean getErrorBean() {
    return errorBean;
  }

  public void setErrorBean(ErrorBean errorBean) {
    this.errorBean = errorBean;
  }
}
