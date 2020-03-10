package com.harvard.usermodule.event;

import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;

/**
 * Created by Naveen Raj on 03/13/2017.
 */

public class LogoutEvent {
    private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

    public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
        return mRegistrationServerConfigEvent;
    }

    public void setmRegistrationServerConfigEvent(RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
        this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
    }
}
