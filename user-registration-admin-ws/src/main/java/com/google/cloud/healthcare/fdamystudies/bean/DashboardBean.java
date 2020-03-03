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

import java.util.ArrayList;
import java.util.List;

public class DashboardBean {
  List<StudyBean> studies = new ArrayList<>();
  private ErrorBean error = new ErrorBean();
  List<AppBean> apps = new ArrayList<>();

  public List<StudyBean> getStudies() {
    return studies;
  }

  public void setStudies(List<StudyBean> studies) {
    this.studies = studies;
  }

  public ErrorBean getError() {
    return error;
  }

  public void setError(ErrorBean error) {
    this.error = error;
  }

  public List<AppBean> getApps() {
    return apps;
  }

  public void setApps(List<AppBean> apps) {
    this.apps = apps;
  }
}
