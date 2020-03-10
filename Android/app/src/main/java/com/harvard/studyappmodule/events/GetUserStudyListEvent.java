package com.harvard.studyappmodule.events;

import com.harvard.webservicemodule.events.WCPConfigEvent;

/**
 * Created by Rohit on 3/27/2017.
 */

public class GetUserStudyListEvent {
    private WCPConfigEvent wcpConfigEvent;

    public WCPConfigEvent getWcpConfigEvent() {
        return wcpConfigEvent;
    }

    public void setWcpConfigEvent(WCPConfigEvent wcpConfigEvent) {
        this.wcpConfigEvent = wcpConfigEvent;
    }
}
