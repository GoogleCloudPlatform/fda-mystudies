/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.userModule.webserviceModelTemp;

import io.realm.RealmObject;

/**
 * Created by Rohit on 3/2/2017.
 */

public class Settings extends RealmObject {
    private boolean localNotifications;
    private boolean remoteNotifications;
    private boolean passcode;
    private String reminderLeadTime;
    private boolean touchId;

    public boolean isLocalNotifications() {
        return localNotifications;
    }

    public void setLocalNotifications(boolean localNotifications) {
        this.localNotifications = localNotifications;
    }

    public boolean isRemoteNotifications() {
        return remoteNotifications;
    }

    public void setRemoteNotifications(boolean remoteNotifications) {
        this.remoteNotifications = remoteNotifications;
    }

    public boolean isPasscode() {
        return passcode;
    }

    public void setPasscode(boolean passcode) {
        this.passcode = passcode;
    }

    public String getRemindersTime() {
        return reminderLeadTime;
    }

    public void setRemindersTime(String remindersTime) {
        this.reminderLeadTime = remindersTime;
    }

    public boolean isTouchId() {
        return touchId;
    }

    public void setTouchId(boolean touchId) {
        this.touchId = touchId;
    }
}
