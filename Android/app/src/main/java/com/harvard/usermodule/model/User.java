/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.usermodule.model;

import java.util.ArrayList;

public class User {
  private String firstName;
  private String lastName;
  private String emailId;
  private String userId;
  private boolean verified;
  private String authToken;
  private String userType;
  private Settings settings;
  private ArrayList<UserStudyStatus> userStudyStatuses = new ArrayList<>();
  private ArrayList<UserActivityStatus> userActivityStatuses = new ArrayList<>();

  public String getmFirstName() {
    return firstName;
  }

  public void setmFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getmLastName() {
    return lastName;
  }

  public void setmLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getmEmailId() {
    return emailId;
  }

  public void setmEmailId(String emailId) {
    this.emailId = emailId;
  }

  public String getmUserId() {
    return userId;
  }

  public void setmUserId(String userId) {
    this.userId = userId;
  }

  public boolean ismVerified() {
    return verified;
  }

  public void setmVerified(boolean verified) {
    this.verified = verified;
  }

  public String getmAuthToken() {
    return authToken;
  }

  public void setmAuthToken(String authToken) {
    this.authToken = authToken;
  }

  public String getmUserType() {
    return userType;
  }

  public void setmUserType(String userType) {
    this.userType = userType;
  }

  public Settings getmSettings() {
    return settings;
  }

  public void setmSettings(Settings settings) {
    this.settings = settings;
  }

  public ArrayList<UserStudyStatus> getmUserStudyStatuses() {
    return userStudyStatuses;
  }

  public void setmUserStudyStatuses(ArrayList<UserStudyStatus> userStudyStatuses) {
    this.userStudyStatuses = userStudyStatuses;
  }

  public ArrayList<UserActivityStatus> getmUserActivityStatuses() {
    return userActivityStatuses;
  }

  public void setmUserActivityStatuses(ArrayList<UserActivityStatus> userActivityStatuses) {
    this.userActivityStatuses = userActivityStatuses;
  }
}
