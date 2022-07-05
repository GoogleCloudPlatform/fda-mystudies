/*
 * Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

import java.util.LinkedList;
import java.util.List;

public class SearchQuestionnaireFhirBean {
  private List<QuestionnaireEntry> entry = new LinkedList<>();

  private List<Object> link = new LinkedList<>();

  private String resourceType;
  private int total;
  private String type;

  public List<QuestionnaireEntry> getEntry() {
    return entry;
  }

  public void setEntry(List<QuestionnaireEntry> entry) {
    this.entry = entry;
  }

  public List<Object> getLink() {
    return link;
  }

  public void setLink(List<Object> link) {
    this.link = link;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
