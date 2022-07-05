/* Copyright 2021 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.fdahpstudydesigner.bean;

import java.util.LinkedList;
import java.util.List;

public class FHIRQuestionnaire {

  private String id;
  private String resourceType;
  private List<Identifier> identifier = new LinkedList<>();
  private String version;
  private String name;
  private String title;
  private String status;
  private String date; // lastModified date
  private EffectivePeriod effectivePeriod;
  private List<Extension> extension = new LinkedList<>();
  private List<ItemsQuestionnaire> item = new LinkedList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getResourceType() {
    return resourceType;
  }

  public void setResourceType(String resourceType) {
    this.resourceType = resourceType;
  }

  public List<Identifier> getIdentifier() {
    return identifier;
  }

  public void setIdentifier(List<Identifier> identifier) {
    this.identifier = identifier;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public EffectivePeriod getEffectivePeriod() {
    return effectivePeriod;
  }

  public void setEffectivePeriod(EffectivePeriod effectivePeriod) {
    this.effectivePeriod = effectivePeriod;
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
}
