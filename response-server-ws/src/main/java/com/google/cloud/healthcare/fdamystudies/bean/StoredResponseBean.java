/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.bean;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class StoredResponseBean {

  /*
   * This class returns the Participant response data for an activity Id. It returns the latest
   * record. This bean has a structure to support the response bean format that the mobile app
   * currently expects. Changing the bean to make it more intuitive would require changes to the
   * mobile app code, which will be done in a later project.
   */

  private List<String> schemaName = new ArrayList<>();
  private String queryName;
  private List<ResponseRows> rows;
  private int rowCount;
}
