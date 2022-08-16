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

public class ItemsQuestionnaire {
  private String linkId;
  private String text;
  private String definition;
  private String type;
  private List<EnableWhenBranching> enableWhen;
  private String enableBehavior;
  private Boolean required;
  private Boolean repeats;
  private List<AnswerOption> answerOption = new LinkedList<>();
  private List<Initial> initial = new LinkedList<>();
  private List<ItemsQuestionnaire> item = new LinkedList<>();

  private List<Extension> extension = new LinkedList<>();

  public String getLinkId() {
    return linkId;
  }

  public void setLinkId(String linkId) {
    this.linkId = linkId;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public Boolean getRepeats() {
    return repeats;
  }

  public void setRepeats(Boolean repeats) {
    this.repeats = repeats;
  }

  public List<AnswerOption> getAnswerOption() {
    return answerOption;
  }

  public void setAnswerOption(List<AnswerOption> answerOption) {
    this.answerOption = answerOption;
  }

  public List<Initial> getInitial() {
    return initial;
  }

  public void setInitial(List<Initial> initial) {
    this.initial = initial;
  }

  public List<ItemsQuestionnaire> getItem() {
    return item;
  }

  public void setItem(List<ItemsQuestionnaire> item) {
    this.item = item;
  }

  public List<Extension> getExtension() {
    return extension;
  }

  public void setExtension(List<Extension> extension) {
    this.extension = extension;
  }

  public List<EnableWhenBranching> getEnableWhen() {
    return enableWhen;
  }

  public void setEnableWhen(List<EnableWhenBranching> enableWhen) {
    this.enableWhen = enableWhen;
  }

  public String getEnableBehavior() {
    return enableBehavior;
  }

  public void setEnableBehavior(String enableBehavior) {
    this.enableBehavior = enableBehavior;
  }
}
