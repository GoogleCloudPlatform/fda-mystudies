/*
 * Copyright 2020 Google LLC
 *
 * Use of this source code is governed by an MIT-style
 * license that can be found in the LICENSE file or at
 * https://opensource.org/licenses/MIT.
 */

package com.google.cloud.healthcare.fdamystudies.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class UserProfile implements Serializable {

  private static final long serialVersionUID = 2134499422682465794L;

  private String id;

  private String name;

  private String password;

  private String email;

  private Integer zipCode;

  private Integer telephone;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Integer getZipCode() {
    return zipCode;
  }

  public void setZipCode(Integer zipCode) {
    this.zipCode = zipCode;
  }

  public Integer getTelephone() {
    return telephone;
  }

  public void setTelephone(Integer telephone) {
    this.telephone = telephone;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("User Profile [");
    if (name != null) {
      builder.append("name=").append(name).append(", ");
    }
    if (password != null) {
      builder.append("password=").append(password).append(", ");
    }
    if (email != null) {
      builder.append("email=").append(email).append(", ");
    }
    if (zipCode != null) {
      builder.append("zipCode=").append(zipCode).append(", ");
    }
    if (telephone != null) {
      builder.append("telephone=").append(telephone).append(", ");
    }
    builder.append("]");

    int idx = builder.lastIndexOf(", ");
    if (idx != -1) {
      builder.replace(idx, builder.length() - 1, "");
    }
    return builder.toString();
  }
}
