/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
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

package com.fdahpstudydesigner.util;

import com.fdahpstudydesigner.bean.StudySessionBean;
import java.io.Serializable;
import java.util.List;

public class SessionObject implements Serializable {

  private static final long serialVersionUID = 9080727824545069556L;

  private Integer adminstratorId = 0;
  private Integer auditLogUniqueId = 0;
  private String createdDate = "";
  private String currentHomeUrl = "";
  private String email = "";
  private String firstName = "";
  private Boolean isAdminstrating = false;
  private boolean isSuperAdmin = false;
  private String lastName = "";
  private boolean loginStatus = false;
  private String passwordExpiryDateTime;
  private String phoneNumber = "";
  private String privacyPolicyText = "";
  private List<Integer> studySession;
  private List<StudySessionBean> studySessionBeans;
  private String superAdminId = null;
  private String termsText = "";
  private String userId = "";
  private String userName = "";
  private String userPermissions = "";
  private String userType = "";
  private String role = "";
  private String sessionId;
  private String accessLevel = "";
  private String correlationId;
  private String gcpBucketName;

  public String getCorrelationId() {
    return correlationId;
  }

  public Integer getAdminstratorId() {
    return adminstratorId;
  }

  public Integer getAuditLogUniqueId() {
    return auditLogUniqueId;
  }

  public String getCreatedDate() {
    return createdDate;
  }

  public String getCurrentHomeUrl() {
    return currentHomeUrl;
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public Boolean getIsAdminstrating() {
    return isAdminstrating;
  }

  public String getLastName() {
    return lastName;
  }

  public String getPasswordExpiryDateTime() {
    return passwordExpiryDateTime;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getPrivacyPolicyText() {
    return privacyPolicyText;
  }

  public List<Integer> getStudySession() {
    return studySession;
  }

  public List<StudySessionBean> getStudySessionBeans() {
    return studySessionBeans;
  }

  public String getSuperAdminId() {
    return superAdminId;
  }

  public String getTermsText() {
    return termsText;
  }

  public String getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getUserPermissions() {
    return userPermissions;
  }

  public String getUserType() {
    return userType;
  }

  public boolean isLoginStatus() {
    return loginStatus;
  }

  public boolean isSuperAdmin() {
    return isSuperAdmin;
  }

  public void setAdminstratorId(Integer adminstratorId) {
    this.adminstratorId = adminstratorId;
  }

  public void setAuditLogUniqueId(Integer auditLogUniqueId) {
    this.auditLogUniqueId = auditLogUniqueId;
  }

  public void setCreatedDate(String createdDate) {
    this.createdDate = createdDate;
  }

  public void setCurrentHomeUrl(String currentHomeUrl) {
    this.currentHomeUrl = currentHomeUrl;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setIsAdminstrating(Boolean isAdminstrating) {
    this.isAdminstrating = isAdminstrating;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setLoginStatus(boolean loginStatus) {
    this.loginStatus = loginStatus;
  }

  public void setPasswordExpiryDateTime(String passwordExpiryDateTime) {
    this.passwordExpiryDateTime = passwordExpiryDateTime;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public void setPrivacyPolicyText(String privacyPolicyText) {
    this.privacyPolicyText = privacyPolicyText;
  }

  public void setStudySession(List<Integer> studySession) {
    this.studySession = studySession;
  }

  public void setStudySessionBeans(List<StudySessionBean> studySessionBeans) {
    this.studySessionBeans = studySessionBeans;
  }

  public void setSuperAdmin(boolean isSuperAdmin) {
    this.isSuperAdmin = isSuperAdmin;
  }

  public void setSuperAdminId(String superAdminId) {
    this.superAdminId = superAdminId;
  }

  public void setTermsText(String termsText) {
    this.termsText = termsText;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setUserPermissions(String userPermissions) {
    this.userPermissions = userPermissions;
  }

  public void setUserType(String userType) {
    this.userType = userType;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getAccessLevel() {
    return accessLevel;
  }

  public void setAccessLevel(String accessLevel) {
    this.accessLevel = accessLevel;
  }

  public void setCorrelationId(String correlationId) {
    this.correlationId = correlationId;
  }

  /** @return the gcpBucketName */
  public String getGcpBucketName() {
    return gcpBucketName;
  }

  /** @param gcpBucketName the gcpBucketName to set */
  public void setGcpBucketName(String gcpBucketName) {
    this.gcpBucketName = gcpBucketName;
  }
}
