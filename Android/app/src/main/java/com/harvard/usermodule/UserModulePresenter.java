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

import com.harvard.FdaEventBus;
import com.harvard.studyappmodule.events.ConsentPdfEvent;
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
import com.harvard.usermodule.event.UpdateAppVersionEvent;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.event.UpdateUserProfileEvent;
import com.harvard.usermodule.event.VerifyUserEvent;

public class UserModulePresenter {
  public void performLogin(LoginEvent loginEvent) {
    FdaEventBus.postEvent(loginEvent);
  }

  public void performRegistration(RegisterUserEvent registerUserEvent) {
    FdaEventBus.postEvent(registerUserEvent);
  }

  void performVerifyRegistration(VerifyUserEvent verifyUserEvent) {
    FdaEventBus.postEvent(verifyUserEvent);
  }

  void performForgotPassword(ForgotPasswordEvent forgotPasswordEvent) {
    FdaEventBus.postEvent(forgotPasswordEvent);
  }

  public void performChangePassword(ChangePasswordEvent changePasswordEvent) {
    FdaEventBus.postEvent(changePasswordEvent);
  }

  public void performGetUserProfile(GetUserProfileEvent getUserProfileEvent) {
    FdaEventBus.postEvent(getUserProfileEvent);
  }

  public void performUpdateUserProfile(UpdateUserProfileEvent updateUserProfileEvent) {
    FdaEventBus.postEvent(updateUserProfileEvent);
  }

  public void performUpdateAppVersion(UpdateAppVersionEvent updateAppVersionEvent) {
    FdaEventBus.postEvent(updateAppVersionEvent);
  }

  public void performUpdateUserPreference(UpdatePreferenceEvent updatePreferenceEvent) {
    FdaEventBus.postEvent(updatePreferenceEvent);
  }

  public void performGetUserPreference(GetPreferenceEvent getPreferenceEvent) {
    FdaEventBus.postEvent(getPreferenceEvent);
  }

  public void performActivityState(ActivityStateEvent activityStateEvent) {
    FdaEventBus.postEvent(activityStateEvent);
  }

  public void performLogout(LogoutEvent logoutEvent) {
    FdaEventBus.postEvent(logoutEvent);
  }

  public void performDeleteAccount(DeleteAccountEvent deleteAccountEvent) {
    FdaEventBus.postEvent(deleteAccountEvent);
  }

  void performResendEmail(ResendEmailEvent resendEmailEvent) {
    FdaEventBus.postEvent(resendEmailEvent);
  }

  public void performConsentPdf(ConsentPdfEvent consentPdfEvent) {
    FdaEventBus.postEvent(consentPdfEvent);
  }
}
