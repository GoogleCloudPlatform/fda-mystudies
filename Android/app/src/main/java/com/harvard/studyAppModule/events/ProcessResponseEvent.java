package com.harvard.studyappmodule.events;

import com.harvard.webservicemodule.events.ResponseServerConfigEvent;

public class ProcessResponseEvent {
  private ResponseServerConfigEvent mResponseServerConfigEvent;

  public ResponseServerConfigEvent getResponseServerConfigEvent() {
    return mResponseServerConfigEvent;
  }

  public void setResponseServerConfigEvent(ResponseServerConfigEvent responseServerConfigEvent) {
    mResponseServerConfigEvent = responseServerConfigEvent;
  }
}
