/*
 * Copyright 2020-2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.controller;

import java.beans.PropertyEditorSupport;
import org.apache.commons.text.StringEscapeUtils;
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
      String safe = Jsoup.clean(text, Whitelist.simpleText());
      setValue(StringEscapeUtils.unescapeHtml4(safe));
    }
  }

  @InitBinder
  public void bindPropertyCleaner(WebDataBinder webDataBinder) {
    CustomPropertyEditor propertyCleaner = new CustomPropertyEditor();
    webDataBinder.registerCustomEditor(String.class, propertyCleaner);
  }

  @InitBinder
  public void propertyCleaner(WebDataBinder webDataBinder) {
    CustomPropertyEditor propertyCleaner = new CustomPropertyEditor();
    webDataBinder.registerCustomEditor(String[].class, propertyCleaner);
  }
}
