/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020-2021 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package com.fdahpstudydesigner.bo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "users")
@NamedQueries({
  @NamedQuery(
      name = "getUserByEmail",
      query = "select UBO from UserBO UBO where UBO.userEmail =:email"),
  @NamedQuery(name = "getUserById", query = "SELECT UBO FROM UserBO UBO WHERE UBO.userId =:userId"),
  @NamedQuery(
      name = "getUserBySecurityToken",
      query = "select UBO from UserBO UBO where UBO.securityToken =:securityToken"),
})
public class UserBO implements Serializable {

  private static final long serialVersionUID = 135353554543L;

  @Column(name = "accountNonExpired", length = 1)
  private boolean accountNonExpired;

  @Column(name = "accountNonLocked", length = 1)
  private boolean accountNonLocked;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_date")
  private String createdOn;

  @Column(name = "credentialsNonExpired", length = 1)
  private boolean credentialsNonExpired;

  @Column(name = "email_changed", columnDefinition = "TINYINT(1)")
  private Boolean emailChanged = false;

  @Column(name = "status", length = 1)
  private boolean enabled;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "force_logout")
  @Type(type = "yes_no")
  private boolean forceLogout = false;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "modified_by")
  private String modifiedBy;

  @Column(name = "modified_date")
  private String modifiedOn;

  @Column(name = "password_expiry_datetime")
  private String passwordExpiryDateTime;

  @ManyToMany(
      fetch = FetchType.EAGER,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "user_permission_mapping",
      joinColumns = {@JoinColumn(name = "user_id", nullable = false)},
      inverseJoinColumns = {@JoinColumn(name = "permission_id", nullable = false)})
  private Set<UserPermissions> permissionList = new HashSet<>(0);

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "role_id")
  private String roleId;

  @Transient private String roleName;

  @Column(name = "security_token")
  private String securityToken;

  @Column(name = "token_expiry_date")
  private String tokenExpiryDate;

  @Column(name = "token_used")
  private Boolean tokenUsed;

  @Column(name = "email")
  private String userEmail;

  @Transient private String userFullName;

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  @Column(name = "user_id", updatable = false, nullable = false)
  private String userId;

  @Column(name = "user_login_datetime")
  private String userLastLoginDateTime;

  @Column(name = "password")
  private String userPassword;

  @Column(name = "access_level")
  private String accessLevel;

  public String getCreatedBy() {
    return createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public Boolean getEmailChanged() {
    return emailChanged;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getModifiedBy() {
    return modifiedBy;
  }

  public String getModifiedOn() {
    return modifiedOn;
  }

  public String getPasswordExpiryDateTime() {
    return passwordExpiryDateTime;
  }

  public Set<UserPermissions> getPermissionList() {
    return permissionList;
  }

  public Set<UserPermissions> getPermissions() {
    return permissionList;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getRoleId() {
    return roleId;
  }

  public String getRoleName() {
    return roleName;
  }

  public String getSecurityToken() {
    return securityToken;
  }

  public String getTokenExpiryDate() {
    return tokenExpiryDate;
  }

  public Boolean getTokenUsed() {
    return tokenUsed;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public String getUserFullName() {
    return userFullName;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserLastLoginDateTime() {
    return userLastLoginDateTime;
  }

  public String getUserPassword() {
    return userPassword;
  }

  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isForceLogout() {
    return forceLogout;
  }

  public void setAccountNonExpired(boolean accountNonExpired) {
    this.accountNonExpired = accountNonExpired;
  }

  public void setAccountNonLocked(boolean accountNonLocked) {
    this.accountNonLocked = accountNonLocked;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public void setCredentialsNonExpired(boolean credentialsNonExpired) {
    this.credentialsNonExpired = credentialsNonExpired;
  }

  public void setEmailChanged(Boolean emailChanged) {
    this.emailChanged = emailChanged;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setForceLogout(boolean forceLogout) {
    this.forceLogout = forceLogout;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setModifiedBy(String modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setModifiedOn(String modifiedOn) {
    this.modifiedOn = modifiedOn;
  }

  public void setPasswordExpiryDateTime(String passwordExpiryDateTime) {
    this.passwordExpiryDateTime = passwordExpiryDateTime;
  }

  public void setPermissionList(Set<UserPermissions> permissionList) {
    this.permissionList = permissionList;
  }

  public void setPermissions(Set<UserPermissions> permissionList) {
    this.permissionList = permissionList;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setRoleId(String roleId) {
    this.roleId = roleId;
  }

  public void setRoleName(String roleName) {
    this.roleName = roleName;
  }

  public void setSecurityToken(String securityToken) {
    this.securityToken = securityToken;
  }

  public void setTokenExpiryDate(String tokenExpiryDate) {
    this.tokenExpiryDate = tokenExpiryDate;
  }

  public void setTokenUsed(Boolean tokenUsed) {
    this.tokenUsed = tokenUsed;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public void setUserFullName(String userFullName) {
    this.userFullName = userFullName;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setUserLastLoginDateTime(String userLastLoginDateTime) {
    this.userLastLoginDateTime = userLastLoginDateTime;
  }

  public void setUserPassword(String userPassword) {
    this.userPassword = userPassword;
  }

  public String getAccessLevel() {
    return accessLevel;
  }

  public void setAccessLevel(String accessLevel) {
    this.accessLevel = accessLevel;
  }
}
