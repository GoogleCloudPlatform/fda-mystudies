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
import com.harvard.base.BaseSubscriber;
import com.harvard.studyappmodule.events.ConsentPdfEvent;
import com.harvard.studyappmodule.events.DeleteAccountEvent;
import com.harvard.usermodule.event.ChangePasswordEvent;
import com.harvard.usermodule.event.DeleteAccountRegServerEvent;
import com.harvard.usermodule.event.DeleteAccountResServerEvent;
import com.harvard.usermodule.event.ForgotPasswordEvent;
import com.harvard.usermodule.event.GetPreferenceEvent;
import com.harvard.usermodule.event.GetUserEvent;
import com.harvard.usermodule.event.GetUserProfileEvent;
import com.harvard.usermodule.event.LoginEvent;
import com.harvard.usermodule.event.LogoutEvent;
import com.harvard.usermodule.event.ParticipentEnrollmentEvent;
import com.harvard.usermodule.event.RegisterUserEvent;
import com.harvard.usermodule.event.ResendEmailEvent;
import com.harvard.usermodule.event.SetPasscodeEvent;
import com.harvard.usermodule.event.SetTouchIdEvent;
import com.harvard.usermodule.event.SetUserEvent;
import com.harvard.usermodule.event.SignOutEvent;
import com.harvard.usermodule.event.TouchIdSigninEvent;
import com.harvard.usermodule.event.UpdatePreferenceEvent;
import com.harvard.usermodule.event.UpdateUserProfileEvent;
import com.harvard.usermodule.event.ValidatePasscodeEvent;
import com.harvard.usermodule.event.VerifyUserEvent;

public class UserModuleSubscriber extends BaseSubscriber {
  /** get User. */
  public void onEvent(GetUserEvent event) {}

  /** Set user. */
  public void onEvent(SetUserEvent event) {}

  /** Register user. */
  public void onEvent(RegisterUserEvent registerUserEvent) {
    FdaEventBus.postEvent(registerUserEvent.getParticipantDatastoreServerConfigEvent());
  }

  /** verify account. */
  public void onEvent(VerifyUserEvent verifyUserEvent) {
    FdaEventBus.postEvent(verifyUserEvent.getParticipantDatastoreServerConfigEvent());
  }

  /** set touchId. */
  public void onEvent(SetTouchIdEvent setTouchIdEvent) {}

  /** set passcode. */
  public void onEvent(SetPasscodeEvent setPasscodeEvent) {}

  /** validate passcode. */
  public void onEvent(ValidatePasscodeEvent validatePasscodeEvent) {}

  /** Login. */
  public void onEvent(LoginEvent loginEvent) {
    FdaEventBus.postEvent(loginEvent.getAuthServerConfigEvent());
  }

  /** touchId login. */
  public void onEvent(TouchIdSigninEvent touchIdSigninEvent) {
    FdaEventBus.postEvent(touchIdSigninEvent.getParticipantDatastoreServerConfigEvent());
  }

  /** Participent Enrollment. */
  public void onEvent(ParticipentEnrollmentEvent participentEnrollmentEvent) {
    FdaEventBus.postEvent(participentEnrollmentEvent.getResponseDatastoreServerConfigEvent());
  }

  /** Forgot password. */
  public void onEvent(ForgotPasswordEvent forgotPasswordEvent) {
    FdaEventBus.postEvent(forgotPasswordEvent.getAuthServerConfigEvent());
  }

  /** change password. */
  public void onEvent(ChangePasswordEvent changePasswordEvent) {
    FdaEventBus.postEvent(changePasswordEvent.getAuthServerConfigEvent());
  }

  /** delete account from registration server. */
  public void onEvent(DeleteAccountRegServerEvent deleteAccountRegServerEvent) {
    FdaEventBus.postEvent(deleteAccountRegServerEvent.getParticipantDatastoreServerConfigEvent());
  }

  /** delete account from response server. */
  public void onEvent(DeleteAccountResServerEvent deleteAccountResServerEvent) {
    FdaEventBus.postEvent(deleteAccountResServerEvent.getResponseDatastoreServerConfigEvent());
  }

  /** Sign out. */
  public void onEvent(SignOutEvent signOutEvent) {
    FdaEventBus.postEvent(signOutEvent.getParticipantDatastoreServerConfigEvent());
  }

  public void onEvent(GetUserProfileEvent getUserProfileEvent) {
    FdaEventBus.postEvent(getUserProfileEvent.getParticipantDatastoreServerConfigEvent());
  }

  public void onEvent(UpdateUserProfileEvent updateUserProfileEvent) {
    FdaEventBus.postEvent(updateUserProfileEvent.getParticipantDatastoreServerConfigEvent());
  }

  public void onEvent(UpdatePreferenceEvent updatePreferenceEvent) {
    FdaEventBus.postEvent(updatePreferenceEvent.getParticipantDatastoreServerEnrollmentConfigEvent());
  }

  public void onEvent(ResendEmailEvent resendEmailEvent) {
    FdaEventBus.postEvent(resendEmailEvent.getParticipantDatastoreServerConfigEvent());
  }

  public void onEvent(LogoutEvent logoutEvent) {
    FdaEventBus.postEvent(logoutEvent.getAuthServerConfigEvent());
  }

  public void onEvent(GetPreferenceEvent getPreferenceEvent) {
    FdaEventBus.postEvent(getPreferenceEvent.getParticipantDatastoreServerEnrollmentConfigEvent());
  }

  public void onEvent(DeleteAccountEvent deleteAccountEvent) {
    FdaEventBus.postEvent(deleteAccountEvent.getParticipantDatastoreServerConfigEvent());
  }

  public void onEvent(ConsentPdfEvent consentPdfEvent) {
    FdaEventBus.postEvent(consentPdfEvent.getParticipantDatastoreServerConsentConfigEvent());
  }
}
