package com.fdahpstudydesigner.controller;

import java.beans.PropertyEditorSupport;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ControllerAdvice
@EnableWebMvc
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

  @InitBinder
  public void bindPropertyCleaner(WebDataBinder webDataBinder) {
    webDataBinder.registerCustomEditor(String.class, new CustomPropertyEditor());
  }
}
