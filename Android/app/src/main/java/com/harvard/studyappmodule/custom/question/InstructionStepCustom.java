package com.harvard.studyappmodule.custom.question;

import org.researchstack.backbone.step.Step;

public class InstructionStepCustom extends Step {
  /**
   * Customized instruction step that supports html content.
   *
   * @param identifier unique identifier to identify the step
   * @param title title of the step
   * @param detailText description of the step
   */
  public InstructionStepCustom(String identifier, String title, String detailText) {
    super(identifier, title);
    setText(detailText);
    setOptional(false);
  }

  @Override
  public Class getStepLayoutClass() {
    return InstructionStepLayoutCustom.class;
  }
}
