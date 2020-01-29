package com.harvard.studyappmodule.events;

import com.harvard.webservicemodule.events.RegistrationServerConfigEvent;

public class UpdateEligibilityConsentStatusEvent {

  private RegistrationServerConfigEvent mRegistrationServerConfigEvent;

  public RegistrationServerConfigEvent getRegistrationServerConfigEvent() {
    return mRegistrationServerConfigEvent;
  }

  public void setRegistrationServerConfigEvent(
      RegistrationServerConfigEvent registrationServerConfigEvent) {
    mRegistrationServerConfigEvent = registrationServerConfigEvent;
  }
}
