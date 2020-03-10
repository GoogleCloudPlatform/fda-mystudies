package com.harvard.usermodule;

import com.harvard.FDAEventBus;
import com.harvard.studyappmodule.events.ConsentPDFEvent;
import com.harvard.studyappmodule.events.DeleteAccountEvent;
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

/**
 * Created by Rohit on 2/20/2017.
 */

public class UserModulePresenter {
    public void performLogin(LoginEvent loginEvent) {
        FDAEventBus.postEvent(loginEvent);
    }

    public void performRegistration(RegisterUserEvent registerUserEvent) {
        FDAEventBus.postEvent(registerUserEvent);
    }

    public void performVerifyRegistration(VerifyUserEvent verifyUserEvent) {
        FDAEventBus.postEvent(verifyUserEvent);
    }

    public void performForgotPassword(ForgotPasswordEvent forgotPasswordEvent) {
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

    public void performLogout(LogoutEvent logoutEvent) {
        FDAEventBus.postEvent(logoutEvent);
    }

    public void performDeleteAccount(DeleteAccountEvent deleteAccountEvent) {
        FDAEventBus.postEvent(deleteAccountEvent);
    }

    public void performResendEmail(ResendEmailEvent resendEmailEvent) {
        FDAEventBus.postEvent(resendEmailEvent);
    }

    public void performConsentPDF(ConsentPDFEvent consentPDFEvent) {
        FDAEventBus.postEvent(consentPDFEvent);
    }


}
