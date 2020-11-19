// License Agreement for FDA MyStudies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
// Copyright 2020 Google LLC
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the &quot;Software&quot;), to deal in the Software without restriction, including without
// limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
// Software, and to permit persons to whom the Software is furnished to do so, subject to the following
// conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial
// portions of the Software.
// Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
// Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
// THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
// OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.

import UIKit

class StandaloneStudy: NSObject {

  /// Get Basic info of Study from Server.
  func createStudyForStandalone() {
    WCPServices().getStudyBasicInfo(self)
  }

  ///
  func setupStandaloneStudy() {
    if User.currentUser.authToken != nil && User.currentUser.authToken.count > 0 {
      self.getStudyStates()
    } else {
      self.createStudyForStandalone()
    }

  }

  func getStudyStates() {
    EnrollServices().getStudyStates(self)
  }

  func getStudyDashboardInfo() {
    WCPServices().getStudyInformation(studyId: (Study.currentStudy?.studyId)!, delegate: self)
  }

  func fetchStudyDashboardInfo() {

    DBHandler.loadStudyOverview(studyId: (Study.currentStudy?.studyId)!) { (overview) in
      if overview != nil {
        Study.currentStudy?.overview = overview
        self.getStudyUpdates()
      } else {
        self.getStudyDashboardInfo()
      }
    }
  }

  func getStudyUpdates() {

    let study = Study.currentStudy
    DBHandler.loadStudyDetailsToUpdate(
      studyId: (study?.studyId)!,
      completionHandler: { (_) in
        NotificationCenter.default.post(
          name: NSNotification.Name(rawValue: "StudySetupCompleted"),
          object: nil
        )
      }
    )
  }

  func handleStudyListResponse() {
    DBHandler.loadStudyListFromDatabase { (studies) in
      let standaloneStudyId = Utilities.standaloneStudyId()
      let study = studies.filter({ $0.studyId == standaloneStudyId }).last
      Gateway.instance.studies = studies
      if let standaloneStudy = study {
        Study.updateCurrentStudy(study: standaloneStudy)
        self.getStudyDashboardInfo()
      }
    }
  }

}

// MARK: - Webservices Delegates
extension StandaloneStudy: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {}

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    if requestName as String == WCPMethods.study.rawValue {
      self.handleStudyListResponse()
    }

    if requestName as String == WCPMethods.studyInfo.rawValue {

      self.fetchStudyDashboardInfo()
    } else if requestName as String == EnrollmentMethods.studyState.description {

      self.createStudyForStandalone()
    }

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    if error.code == HTTPError.forbidden.rawValue {  // unauthorized
      self.createStudyForStandalone()
    } else if error.code < 0 {  // No Network
      switch requestName as String {
      case WCPMethods.study.rawValue, EnrollmentMethods.studyState.description:
        handleStudyListResponse()

      case WCPMethods.studyInfo.rawValue:
        fetchStudyDashboardInfo()

      default: break
      }
    } else {
      UIUtilities.showAlertWithMessage(alertMessage: error.localizedDescription)
    }
  }

}
