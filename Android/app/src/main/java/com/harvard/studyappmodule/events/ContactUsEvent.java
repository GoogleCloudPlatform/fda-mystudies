package com.harvard.studyappmodule.events;

import com.harvard.webservicemodule.events.WCPConfigEvent;

/**
 * Created by Rajeesh on 4/12/2017.
 */

public class ContactUsEvent {
    private WCPConfigEvent wcpConfigEvent;

    public WCPConfigEvent getWcpConfigEvent() {
        return wcpConfigEvent;
    }

    public void setWcpConfigEvent(WCPConfigEvent wcpConfigEvent) {
        this.wcpConfigEvent = wcpConfigEvent;
    }
}
