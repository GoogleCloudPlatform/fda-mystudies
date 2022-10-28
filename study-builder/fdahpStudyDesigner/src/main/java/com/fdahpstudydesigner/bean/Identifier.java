/* Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

public class Identifier {
  private String use;
  private Object type;
  private String value;

  public String getUse() {
    return use;
  }

  public void setUse(String use) {
    this.use = use;
  }

  public Object getType() {
    return type;
  }

  public void setType(Object type) {
    this.type = type;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
