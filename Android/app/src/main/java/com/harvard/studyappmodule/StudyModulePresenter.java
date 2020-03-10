package com.harvard.studyappmodule;

import com.harvard.FDAEventBus;
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
