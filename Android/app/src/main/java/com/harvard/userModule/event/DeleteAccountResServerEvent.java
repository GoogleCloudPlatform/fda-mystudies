package com.harvard.usermodule.event;

import com.harvard.webservicemodule.events.ResponseServerConfigEvent;

public class DeleteAccountResServerEvent {
  private ResponseServerConfigEvent mResponseServerConfigEvent;

  public ResponseServerConfigEvent getmResponseServerConfigEvent() {
    return mResponseServerConfigEvent;
  }

  public void setmResponseServerConfigEvent(ResponseServerConfigEvent mResponseServerConfigEvent) {
    this.mResponseServerConfigEvent = mResponseServerConfigEvent;
  }
}
