/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.surveyscheduler.datamodel;

import java.util.ArrayList;

public class MonthlyRuns {
  private ArrayList<Runs> monthly;

  public ArrayList<Runs> getMonthly() {
    return monthly;
  }

  public void setMonthly(ArrayList<Runs> monthly) {
    this.monthly = monthly;
  }
}
