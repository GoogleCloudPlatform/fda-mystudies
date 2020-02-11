package com.harvard.studyAppModule.events;

import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;

/**
 * Created by Naveen Raj on 04/18/2017.
 */

public class UpdateEligibilityConsentStatusEvent {

    private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

    public RegistrationServerConfigEvent getRegistrationServerConfigEvent() {
        return mRegistrationServerConfigEvent;
    }

    public void setRegistrationServerConfigEvent(RegistrationServerConfigEvent registrationServerConfigEvent) {
        mRegistrationServerConfigEvent = registrationServerConfigEvent;
    }
}
