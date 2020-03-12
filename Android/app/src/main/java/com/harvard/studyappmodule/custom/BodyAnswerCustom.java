package com.harvard.studyappmodule.custom;

import android.content.Context;
import org.researchstack.backbone.ui.step.body.BodyAnswer;

public class BodyAnswerCustom extends BodyAnswer {
  private String reason;

  public BodyAnswerCustom(boolean isValid, String reason, String... params) {
    super(isValid, 0, params);
    this.reason = reason;
  }

  @Override
  public String getString(Context context) {
    return reason;
  }
}
