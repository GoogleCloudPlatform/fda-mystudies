package com.fdahpstudydesigner.bean;

public class QuestionnaireEntry {
  private String fullUrl;
  private FHIRQuestionnaire resource;
  private Object search;

  public String getFullUrl() {
    return fullUrl;
  }

  public void setFullUrl(String fullUrl) {
    this.fullUrl = fullUrl;
  }

  public FHIRQuestionnaire getResource() {
    return resource;
  }

  public void setResource(FHIRQuestionnaire resource) {
    this.resource = resource;
  }

  public Object getSearch() {
    return search;
  }

  public void setSearch(Object search) {
    this.search = search;
  }
}
