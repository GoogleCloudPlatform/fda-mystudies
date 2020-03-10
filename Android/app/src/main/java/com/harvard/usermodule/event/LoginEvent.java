package com.harvard.usermodule.event;

import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;

/**
 * Created by Rohit on 2/17/2017.
 */

public class LoginEvent {
    private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

    public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
        return mRegistrationServerConfigEvent;
    }

    public void setmRegistrationServerConfigEvent(RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
        this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
    }
}
