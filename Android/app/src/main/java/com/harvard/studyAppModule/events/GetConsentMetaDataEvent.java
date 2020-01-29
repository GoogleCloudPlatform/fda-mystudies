package com.harvard.studyappmodule.events;

import com.harvard.webservicemodule.events.WCPConfigEvent;

public class GetConsentMetaDataEvent {
  private WCPConfigEvent wcpConfigEvent;

  public WCPConfigEvent getWcpConfigEvent() {
    return wcpConfigEvent;
  }

  public void setWcpConfigEvent(WCPConfigEvent wcpConfigEvent) {
    this.wcpConfigEvent = wcpConfigEvent;
  }
}
