package com.harvard.userModuleTemp.event;

import com.harvard.webservicemodule.events.WCPConfigEvent;

/**
 * Created by Rohit on 3/27/2017.
 */

public class GetTermsAndConditionEvent {
    private WCPConfigEvent wcpConfigEvent;

    public WCPConfigEvent getWcpConfigEvent() {
        return wcpConfigEvent;
    }

    public void setWcpConfigEvent(WCPConfigEvent wcpConfigEvent) {
        this.wcpConfigEvent = wcpConfigEvent;
    }
}
