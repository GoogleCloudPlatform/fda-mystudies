package com.fdahpstudydesigner.controller;

import java.beans.PropertyEditorSupport;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

public class CleanPropertyAdvice {
  public static class CustomPropertyEditor extends PropertyEditorSupport {

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
}
