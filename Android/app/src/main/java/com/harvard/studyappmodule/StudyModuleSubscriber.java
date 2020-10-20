/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
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

package com.harvard.studyappmodule;

import com.harvard.FdaEventBus;
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
import com.harvard.usermodule.event.ActivityStateEvent;
import com.harvard.usermodule.event.GetTermsAndConditionEvent;

public class StudyModuleSubscriber extends BaseSubscriber {
  public void onEvent(GetUserStudyInfoEvent getUserStudyInfoEvent) {
    FdaEventBus.postEvent(getUserStudyInfoEvent.getStudyDatastoreConfigEvent());
  }

  public void onEvent(GetConsentMetaDataEvent getConsentMetaDataEvent) {
    FdaEventBus.postEvent(getConsentMetaDataEvent.getStudyDatastoreConfigEvent());
  }

  public void onEvent(VerifyEnrollmentIdEvent verifyEnrollmentIdEvent) {
    FdaEventBus.postEvent(verifyEnrollmentIdEvent.participantDatastoreEnrollmentConfigEvent());
  }

  public void onEvent(GetUserStudyListEvent getUserStudyListEvent) {
    FdaEventBus.postEvent(getUserStudyListEvent.getStudyDatastoreConfigEvent());
  }

  public void onEvent(GetTermsAndConditionEvent getTermsAndConditionEvent) {
    FdaEventBus.postEvent(getTermsAndConditionEvent.getStudyDatastoreConfigEvent());
  }

  public void onEvent(EnrollIdEvent enrollIdEvent) {
    FdaEventBus.postEvent(enrollIdEvent.getParticipantDatastoreEnrollmentConfigEvent());
  }

  public void onEvent(GetActivityListEvent getActivityListEvent) {
    FdaEventBus.postEvent(getActivityListEvent.getStudyDatastoreConfigEvent());
  }

  public void onEvent(GetActivityInfoEvent getActivityInfoEvent) {
    FdaEventBus.postEvent(getActivityInfoEvent.getStudyDatastoreConfigEvent());
  }

  public void onEvent(ContactUsEvent contactUsEvent) {
    FdaEventBus.postEvent(contactUsEvent.getParticipantDatastoreConfigEvent());
  }

  public void onEvent(FeedbackEvent feedbackEvent) {
    FdaEventBus.postEvent(feedbackEvent.getStudyDatastoreConfigEvent());
  }

  public void onEvent(GetResourceListEvent getResourceListEvent) {
    FdaEventBus.postEvent(getResourceListEvent.getStudyDatastoreConfigEvent());
  }

  public void onEvent(UpdateEligibilityConsentStatusEvent updateEligibilityConsentStatusEvent) {
    FdaEventBus.postEvent(
        updateEligibilityConsentStatusEvent.getParticipantDatastoreConsentConfigEvent());
  }

  public void onEvent(ProcessResponseEvent processResponseEvent) {
    FdaEventBus.postEvent(processResponseEvent.getResponseDatastoreConfigEvent());
  }

  public void onEvent(WithdrawFromStudyEvent withdrawFromStudyEvent) {
    FdaEventBus.postEvent(withdrawFromStudyEvent.getResponseDatastoreConfigEvent());
  }

  public void onEvent(ProcessResponseDataEvent processResponseDataEvent) {
    FdaEventBus.postEvent(processResponseDataEvent.getResponseDatastoreConfigEvent());
  }

  public void onEvent(ActivityStateEvent activityStateEvent) {
    FdaEventBus.postEvent(activityStateEvent.getResponseDatastoreConfigEvent());
  }
}
