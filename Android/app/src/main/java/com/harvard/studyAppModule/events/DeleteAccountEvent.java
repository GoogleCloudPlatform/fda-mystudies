package com.harvard.studyappmodule.events;

import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;

public class DeleteAccountEvent {
  private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

  public RegistrationServerConfigEvent getmRegistrationServerConfigEvent() {
    return mRegistrationServerConfigEvent;
  }

  public void setmRegistrationServerConfigEvent(
      RegistrationServerConfigEvent mRegistrationServerConfigEvent) {
    this.mRegistrationServerConfigEvent = mRegistrationServerConfigEvent;
  }
}
