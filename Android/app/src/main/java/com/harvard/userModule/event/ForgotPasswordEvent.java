package com.harvard.usermodule.event;

import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;

public class ForgotPasswordEvent {
  private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

  public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
    return mRegistrationServerConfigEvent;
  }

  public void setmRegistrationServerConfigEvent(
      RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
    this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
  }
}
