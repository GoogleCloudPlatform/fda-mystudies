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

package com.harvard.fda.studyappmodule;

import com.harvard.fda.FDAEventBus;
import com.harvard.fda.studyappmodule.events.ContactUsEvent;
import com.harvard.fda.studyappmodule.events.EnrollIdEvent;
import com.harvard.fda.studyappmodule.events.FeedbackEvent;
import com.harvard.fda.studyappmodule.events.GetActivityInfoEvent;
import com.harvard.fda.studyappmodule.events.GetActivityListEvent;
import com.harvard.fda.studyappmodule.events.GetConsentMetaDataEvent;
import com.harvard.fda.studyappmodule.events.GetResourceListEvent;
import com.harvard.fda.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.fda.studyappmodule.events.GetUserStudyListEvent;
import com.harvard.fda.studyappmodule.events.ProcessResponseDataEvent;
import com.harvard.fda.studyappmodule.events.ProcessResponseEvent;
import com.harvard.fda.studyappmodule.events.UpdateEligibilityConsentStatusEvent;
import com.harvard.fda.studyappmodule.events.VerifyEnrollmentIdEvent;
import com.harvard.fda.studyappmodule.events.WithdrawFromStudyEvent;
import com.harvard.fda.usermodule.event.GetTermsAndConditionEvent;

/**
 * Created by Rohit on 3/6/2017.
 */

public class StudyModulePresenter {
    public void performGetGateWayStudyInfo(GetUserStudyInfoEvent getUserStudyInfoEvent) {
        FDAEventBus.postEvent(getUserStudyInfoEvent);
    }


    public void performGetConsentMetaData(GetConsentMetaDataEvent getConsentMetaDataEvent) {
        FDAEventBus.postEvent(getConsentMetaDataEvent);
    }

    public void performVerifyEnrollmentId(VerifyEnrollmentIdEvent verifyEnrollmentIdEvent) {
        FDAEventBus.postEvent(verifyEnrollmentIdEvent);
    }

    public void performEnrollId(EnrollIdEvent enrollIdEvent) {
        FDAEventBus.postEvent(enrollIdEvent);
    }

    public void performUpdateEligibilityConsent(UpdateEligibilityConsentStatusEvent updateEligibilityConsentStatusEvent) {
        FDAEventBus.postEvent(updateEligibilityConsentStatusEvent);
    }

    public void performGetGateWayStudyList(GetUserStudyListEvent userStudyListEvent) {
        FDAEventBus.postEvent(userStudyListEvent);
    }

    public void performGetTermsAndCondition(GetTermsAndConditionEvent getTermsAndConditionEvent) {
        FDAEventBus.postEvent(getTermsAndConditionEvent);
    }

    public void performGetActivityList(GetActivityListEvent getActivityListEvent) {
        FDAEventBus.postEvent(getActivityListEvent);
    }

    public void performGetActivityInfo(GetActivityInfoEvent getActivityInfoEvent) {
        FDAEventBus.postEvent(getActivityInfoEvent);
    }

    public void performContactUsEvent(ContactUsEvent contactUsEvent) {
        FDAEventBus.postEvent(contactUsEvent);
    }

    public void performContactUsEvent(FeedbackEvent feedbackEvent) {
        FDAEventBus.postEvent(feedbackEvent);
    }

    public void performGetResourceListEvent(GetResourceListEvent getResourceListEvent) {
        FDAEventBus.postEvent(getResourceListEvent);
    }

    public void performProcessResponse(ProcessResponseEvent processResponseEvent) {
        FDAEventBus.postEvent(processResponseEvent);
    }

    public void performWithdrawFromStudy(WithdrawFromStudyEvent withdrawFromStudyEvent) {
        FDAEventBus.postEvent(withdrawFromStudyEvent);
    }
    public void performProcessData(ProcessResponseDataEvent processResponseDataEvent) {
        FDAEventBus.postEvent(processResponseDataEvent);
    }

}
