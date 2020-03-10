package com.harvard.studyappmodule.events;

import com.harvard.webservicemodule.events.ResponseServerConfigEvent;

/**
 * Created by Naveen Raj on 05/02/2017.
 */

public class WithdrawFromStudyEvent {
    private ResponseServerConfigEvent mResponseServerConfigEvent;

    public ResponseServerConfigEvent getResponseServerConfigEvent() {
        return mResponseServerConfigEvent;
    }

    public void setResponseServerConfigEvent(ResponseServerConfigEvent responseServerConfigEvent) {
        mResponseServerConfigEvent = responseServerConfigEvent;
    }
}
