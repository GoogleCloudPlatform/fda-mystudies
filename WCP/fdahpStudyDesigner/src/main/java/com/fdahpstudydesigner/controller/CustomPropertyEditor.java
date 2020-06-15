package com.fdahpstudydesigner.controller;

import java.beans.PropertyEditorSupport;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class CustomPropertyEditor extends PropertyEditorSupport {

  public CustomPropertyEditor() {
    super();
  }

  @Override
  public String getAsText() {
    return (String) this.getValue();
  }

  @Override
  public void setAsText(String text) throws IllegalArgumentException {
    if (StringUtils.isEmpty(text)) {
      setValue(null);
    } else {
      String safe = Jsoup.clean(text, Whitelist.simpleText());
      setValue(safe);
    }
  }
}
