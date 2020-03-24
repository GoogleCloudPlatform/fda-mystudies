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

package com.harvard.usermodule;

import com.harvard.FDAEventBus;
import com.harvard.studyappmodule.events.ConsentPDFEvent;
import com.harvard.studyappmodule.events.DeleteAccountEvent;
import com.harvard.usermodule.event.ActivityStateEvent;
import com.harvard.usermodule.event.ChangePasswordEvent;
import com.harvard.usermodule.event.ForgotPasswordEvent;
import com.harvard.usermodule.event.GetPreferenceEvent;
import com.harvard.usermodule.event.GetUserProfileEvent;
import com.harvard.usermodule.event.LoginEvent;
import com.harvard.usermodule.event.LogoutEvent;
import com.harvard.usermodule.event.RegisterUserEvent;
import com.harvard.usermodule.event.ResendEmailEvent;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.event.UpdateUserProfileEvent;
import com.harvard.usermodule.event.VerifyUserEvent;

public class UserModulePresenter {
  public void performLogin(LoginEvent loginEvent) {
    FDAEventBus.postEvent(loginEvent);
  }

  public void performRegistration(RegisterUserEvent registerUserEvent) {
    FDAEventBus.postEvent(registerUserEvent);
  }

  void performVerifyRegistration(VerifyUserEvent verifyUserEvent) {
    FDAEventBus.postEvent(verifyUserEvent);
  }

  void performForgotPassword(ForgotPasswordEvent forgotPasswordEvent) {
    FDAEventBus.postEvent(forgotPasswordEvent);
  }

  public void performChangePassword(ChangePasswordEvent changePasswordEvent) {
    FDAEventBus.postEvent(changePasswordEvent);
  }

  public void performGetUserProfile(GetUserProfileEvent getUserProfileEvent) {
    FDAEventBus.postEvent(getUserProfileEvent);
  }

  public void performUpdateUserProfile(UpdateUserProfileEvent updateUserProfileEvent) {
    FDAEventBus.postEvent(updateUserProfileEvent);
  }

  public void performUpdateUserPreference(UpdatePreferenceEvent updatePreferenceEvent) {
    FDAEventBus.postEvent(updatePreferenceEvent);
  }

  public void performGetUserPreference(GetPreferenceEvent getPreferenceEvent) {
    FDAEventBus.postEvent(getPreferenceEvent);
  }

  public void performActivityState(ActivityStateEvent activityStateEvent) {
    FDAEventBus.postEvent(activityStateEvent);
  }

  public void performLogout(LogoutEvent logoutEvent) {
    FDAEventBus.postEvent(logoutEvent);
  }

  public void performDeleteAccount(DeleteAccountEvent deleteAccountEvent) {
    FDAEventBus.postEvent(deleteAccountEvent);
  }

  void performResendEmail(ResendEmailEvent resendEmailEvent) {
    FDAEventBus.postEvent(resendEmailEvent);
  }

  public void performConsentPDF(ConsentPDFEvent consentPDFEvent) {
    FDAEventBus.postEvent(consentPDFEvent);
  }
}
