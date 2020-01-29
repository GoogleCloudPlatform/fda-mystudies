package com.harvard.usermodule.event;

import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;

public class ChangePasswordEvent {
  private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

  public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
    return mRegistrationServerConfigEvent;
  }

  public void setmRegistrationServerConfigEvent(
      RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
    this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
  }
}
