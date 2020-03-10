package com.harvard.studyappmodule;

import com.harvard.FDAEventBus;
import com.harvard.base.BaseSubscriber;
import com.harvard.studyappmodule.events.ContactUsEvent;
import com.harvard.studyappmodule.events.EnrollIdEvent;
import com.harvard.studyappmodule.events.FeedbackEvent;
import com.harvard.studyappmodule.events.GetActivityInfoEvent;
import com.harvard.studyappmodule.events.GetActivityListEvent;
import com.harvard.studyappmodule.events.GetConsentMetaDataEvent;
import com.harvard.studyappmodule.events.GetResourceListEvent;
import com.harvard.studyappmodule.events.GetUserStudyInfoEvent;
import com.harvard.studyappmodule.events.GetUserStudyListEvent;
import com.harvard.studyappmodule.events.ProcessResponseDataEvent;
import com.harvard.studyappmodule.events.ProcessResponseEvent;
import com.harvard.studyappmodule.events.UpdateEligibilityConsentStatusEvent;
import com.harvard.studyappmodule.events.VerifyEnrollmentIdEvent;
import com.harvard.studyappmodule.events.WithdrawFromStudyEvent;
import com.harvard.usermodule.event.GetTermsAndConditionEvent;

/**
 * Created by Rohit on 2/21/2017.
 */

public class StudyModuleSubscriber extends BaseSubscriber {
    public void onEvent(GetUserStudyInfoEvent getUserStudyInfoEvent) {
        FDAEventBus.postEvent(getUserStudyInfoEvent.getWcpConfigEvent());
    }


    public void onEvent(GetConsentMetaDataEvent getConsentMetaDataEvent) {
        FDAEventBus.postEvent(getConsentMetaDataEvent.getWcpConfigEvent());
    }

    public void onEvent(VerifyEnrollmentIdEvent verifyEnrollmentIdEvent) {
        FDAEventBus.postEvent(verifyEnrollmentIdEvent.getResponseServerConfigEvent());
    }

    public void onEvent(GetUserStudyListEvent getUserStudyListEvent) {
        FDAEventBus.postEvent(getUserStudyListEvent.getWcpConfigEvent());
    }

    public void onEvent(GetTermsAndConditionEvent getTermsAndConditionEvent) {
        FDAEventBus.postEvent(getTermsAndConditionEvent.getWcpConfigEvent());
    }

    public void onEvent(EnrollIdEvent enrollIdEvent) {
        FDAEventBus.postEvent(enrollIdEvent.getResponseServerConfigEvent());
    }

    public void onEvent(GetActivityListEvent getActivityListEvent) {
        FDAEventBus.postEvent(getActivityListEvent.getWcpConfigEvent());
    }

    public void onEvent(GetActivityInfoEvent getActivityInfoEvent) {
        FDAEventBus.postEvent(getActivityInfoEvent.getWcpConfigEvent());
    }

    public void onEvent(ContactUsEvent contactUsEvent) {
        FDAEventBus.postEvent(contactUsEvent.getWcpConfigEvent());
    }

    public void onEvent(FeedbackEvent feedbackEvent) {
        FDAEventBus.postEvent(feedbackEvent.getWcpConfigEvent());
    }

    public void onEvent(GetResourceListEvent getResourceListEvent) {
        FDAEventBus.postEvent(getResourceListEvent.getWcpConfigEvent());
    }

    public void onEvent(UpdateEligibilityConsentStatusEvent updateEligibilityConsentStatusEvent) {
        FDAEventBus.postEvent(updateEligibilityConsentStatusEvent.getRegistrationServerConfigEvent());
    }

    public void onEvent(ProcessResponseEvent processResponseEvent) {
        FDAEventBus.postEvent(processResponseEvent.getResponseServerConfigEvent());
    }

    public void onEvent(WithdrawFromStudyEvent withdrawFromStudyEvent) {
        FDAEventBus.postEvent(withdrawFromStudyEvent.getResponseServerConfigEvent());
    }

    public void onEvent(ProcessResponseDataEvent processResponseDataEvent) {
        FDAEventBus.postEvent(processResponseDataEvent.getResponseServerConfigEvent());
    }

}
