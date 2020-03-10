package com.harvard.studyappmodule.events;

import com.harvard.webservicemodule.events.ResponseServerConfigEvent;

/**
 * Created by Naveen Raj on 03/28/2017.
 */

public class VerifyEnrollmentIdEvent {
    private ResponseServerConfigEvent mResponseServerConfigEvent;

    public ResponseServerConfigEvent getResponseServerConfigEvent() {
        return mResponseServerConfigEvent;
    }

    public void setResponseServerConfigEvent(ResponseServerConfigEvent responseServerConfigEvent) {
        mResponseServerConfigEvent = responseServerConfigEvent;
    }
}
