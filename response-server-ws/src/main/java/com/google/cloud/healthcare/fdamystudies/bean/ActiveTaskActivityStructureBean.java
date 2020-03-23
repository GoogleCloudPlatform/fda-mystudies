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
import com.google.cloud.healthcare.fdamystudies.utils.AppConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Provides active task details. i.e. type of activity, metadata information {@link
 * ActivityMetadataBean} and steps details {@link ActiveTaskActivityStepsBean}.
 */
@Setter
@Getter
@ToString
public class ActiveTaskActivityStructureBean {

  private String type = AppConstants.EMPTY_STR;
  private ActivityMetadataBean metadata = new ActivityMetadataBean();
  private List<ActiveTaskActivityStepsBean> steps = new ArrayList<>();
}
