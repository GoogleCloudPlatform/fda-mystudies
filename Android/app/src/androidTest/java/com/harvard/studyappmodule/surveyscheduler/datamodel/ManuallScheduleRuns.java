/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.harvard.studyappmodule.surveyscheduler.datamodel;

import java.util.ArrayList;

public class ManuallScheduleRuns {
  private ArrayList<Runs> manuallschedule;

  public ArrayList<Runs> getManuallschedule() {
    return manuallschedule;
  }

  public void setManuallschedule(ArrayList<Runs> manuallschedule) {
    this.manuallschedule = manuallschedule;
  }
}
