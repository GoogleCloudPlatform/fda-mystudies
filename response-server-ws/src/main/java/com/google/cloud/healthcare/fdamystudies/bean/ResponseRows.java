/**
 * ***************************************************************************** Copyright 2020
 * Google LLC
 *
 * <p>Use of this source code is governed by an MIT-style license that can be found in the LICENSE
 * file or at https://opensource.org/licenses/MIT.
 * ****************************************************************************
 */
package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResponseRows {
  private List<Map<Object, Object>> data = new ArrayList<>();

  public List<Map<Object, Object>> getData() {
    return data;
  }

  public void setData(List<Map<Object, Object>> data) {
    this.data = data;
  }
}
