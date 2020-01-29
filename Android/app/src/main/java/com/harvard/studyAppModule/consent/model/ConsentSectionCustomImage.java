package com.harvard.studyappmodule.consent.model;

import org.researchstack.backbone.model.ConsentSection;

public class ConsentSectionCustomImage extends ConsentSection {
  private String customImageName;

  /**
   * Returns an initialized consent section using the specified type.
   *
   * @param type The consent section type.
   */
  public ConsentSectionCustomImage(Type type) {
    super(type);
  }

  public void setCustomImageName(String imageName) {
    customImageName = imageName;
  }

  @Override
  public String getCustomImageName() {
    return customImageName;
  }
}
