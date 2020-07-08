package com.harvard.studyappmodule.custom.question;

import java.io.Serializable;

public class ChoiceCustomImage<T> implements Serializable {

  private String text;
  private T value;
  private String image;
  private String selectedImage;

  public ChoiceCustomImage(String text, T value, String image, String selectedImage) {
    this.text = text;
    this.value = value;
    this.image = image;
    this.selectedImage = selectedImage;
  }

  public String getText() {
    return text;
  }

  public T getValue() {
    return value;
  }

  public String getImage() {
    return image;
  }

  public String getSelectedImage() {
    return selectedImage;
  }
}
