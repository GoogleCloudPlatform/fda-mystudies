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

import Foundation

let kConsentDocumentType = "type"
let kConsentDocumentVersion = "version"
let kConsentDocumentContent = "content"

enum StudyStatus: String {
  case active = "Active"
  case upcoming = "Upcoming"
  case closed = "Closed"
  case paused = "Paused"

  var sortIndex: Int {
    switch self {
    case .active:
      return 0
    case .upcoming:
      return 1
    case .paused:
      return 2
    case .closed:
      return 3

    }
  }
}

enum StudyWithdrawalConfigrationType: String {
  case deleteData = "delete_data"
  case askUser = "ask_user"
  case noAction = "no_action"

  case notAvailable = "NotAvailable"
}

// MARK: ConsentDocument
struct ConsentDocument {

  var htmlString: String?
  var mimeType: MimeType?
  var version: String?

  /// Initializes all properties
  /// - Parameter consentDoucumentdict: `JSONDictionary` contains all properties of `ConsentDocument`
  mutating func initData(consentDoucumentdict: [String: Any]) {
    if Utilities.isValidObject(someObject: consentDoucumentdict as AnyObject?) {

      if Utilities.isValidValue(
        someObject: consentDoucumentdict[kConsentDocumentType] as AnyObject
      ) {
        self.mimeType = MimeType(
          rawValue: consentDoucumentdict[kConsentDocumentType] as! String
        )
      }

      if Utilities.isValidValue(
        someObject: consentDoucumentdict[kConsentDocumentContent] as AnyObject
      ) {
        self.htmlString = consentDoucumentdict[kConsentDocumentContent] as? String
      }

      if Utilities.isValidValue(
        someObject: consentDoucumentdict[kConsentDocumentVersion] as AnyObject
      ) {
        self.version = consentDoucumentdict[kConsentDocumentVersion] as? String
      }
    }
  }
}

// MARK: Study
class Study: Hashable {

  // MARK: Properties

  /// Unique identifier
  var studyId: String!

  var name: String?

  /// Current Version
  var version: String?

  /// Updated Version
  var newVersion: String?

  var identifer: String?

  var category: String?
  var startDate: String?
  var endEnd: String?

  /// Current Study Status
  var status: StudyStatus = .active

  var sponserName: String?

  var description: String?
  var brandingConfiguration: String?
  var logoURL: String?
  var overview: Overview!
  var activities: [Activity]! = []

  /// Resources for Study & Activities
  var resources: [Resource]? = []

  var userParticipateState: UserStudyStatus! = nil

  var studySettings: StudySettings!

  /// Study Consent Document
  var consentDocument: ConsentDocument?

  var signedConsentVersion: String?

  var signedConsentFilePath: String?
  var anchorDate: StudyAnchorDate?
  var activitiesLocalNotificationUpdated = false
  var totalIncompleteRuns = 0
  var totalCompleteRuns = 0

  func hash(into hasher: inout Hasher) {
    hasher.combine(self.studyId)
  }

  var withdrawalConfigration: StudyWithdrawalConfigration?

  static var currentStudy: Study?
  static var currentActivity: Activity?

  init() {

  }

  /// Initialize all properties
  /// - Parameter studyDetail: `JSONDictionary` with properties of `Study`
  init(studyDetail: [String: Any]) {

    if Utilities.isValidObject(someObject: studyDetail as AnyObject?) {

      if Utilities.isValidValue(someObject: studyDetail[kStudyId] as AnyObject) {
        self.studyId = studyDetail[kStudyId] as? String
      }

      if Utilities.isValidValue(someObject: studyDetail[kStudyTitle] as AnyObject) {
        self.name = studyDetail[kStudyTitle] as? String
      }
      if Utilities.isValidValue(someObject: studyDetail[kStudyVersion] as AnyObject) {
        self.version = studyDetail[kStudyVersion] as? String
      }

      if Utilities.isValidValue(someObject: studyDetail[kStudyCategory] as AnyObject) {
        self.category = studyDetail[kStudyCategory] as? String
      }
      if Utilities.isValidValue(someObject: studyDetail[kStudySponserName] as AnyObject) {
        self.sponserName = studyDetail[kStudySponserName] as? String
      }
      if Utilities.isValidValue(someObject: studyDetail[kStudyTagLine] as AnyObject) {
        self.description = studyDetail[kStudyTagLine] as? String
      }
      if Utilities.isValidValue(someObject: studyDetail[kStudyLogoURL] as AnyObject) {
        self.logoURL = studyDetail[kStudyLogoURL] as? String
      }
      if Utilities.isValidValue(someObject: studyDetail[kStudyStatus] as AnyObject) {
        self.status = StudyStatus.init(rawValue: studyDetail[kStudyStatus] as! String)!
      }

      if Utilities.isValidObject(someObject: studyDetail[kStudySettings] as AnyObject) {
        self.studySettings = StudySettings(
          settings: studyDetail[kStudySettings] as! [String: Any]
        )
      } else {
        self.studySettings = StudySettings()
      }

      let currentUser = User.currentUser

      if let userStudyStatus = currentUser.participatedStudies.filter({ $0.studyId == self.studyId }
      ).last {
        self.userParticipateState = userStudyStatus
      } else {
        self.userParticipateState = UserStudyStatus()
      }
    }
  }

  static func == (lhs: Study, rhs: Study) -> Bool {
    return lhs.studyId == rhs.studyId
  }

  /// Updates `Study` current activity
  /// - Parameter activity: Instance of Activity.
  class func updateCurrentActivity(activity: Activity) {
    Study.currentActivity = activity
  }

  /// Updates current `Study`
  /// - Parameter study: `Study` instance
  class func updateCurrentStudy(study: Study) {
    Study.currentStudy = study
  }
}

// MARK: StudySettings
class StudySettings {

  lazy var enrollingAllowed = true
  lazy var rejoinStudyAfterWithdrawn = false
  lazy var platform = "ios"

  init() {

  }

  /// Initializes all properties
  /// - Parameter settings: `JSONDictionary` contains all properties of `StudySettings`
  init(settings: [String: Any]) {

    if Utilities.isValidObject(someObject: settings as AnyObject?) {

      if Utilities.isValidValue(someObject: settings[kStudyEnrolling] as AnyObject) {
        self.enrollingAllowed = (settings[kStudyEnrolling] as? Bool)!
      }
      if Utilities.isValidValue(someObject: settings[kStudyRejoin] as AnyObject) {
        self.rejoinStudyAfterWithdrawn = (settings[kStudyRejoin] as? Bool)!
      }
      if Utilities.isValidValue(someObject: settings[kStudyPlatform] as AnyObject) {
        self.platform = (settings[kStudyPlatform] as? String)!
      }
    }
  }
}

// MARK: StudyAnchorDate
class StudyAnchorDate {

  var date: Date?
  var anchorDateType: String?
  var anchorDateActivityId: String?
  var anchorDateActivityVersion: String?
  var anchorDateQuestionKey: String?

  init() {
  }

  /// Initializes all properties
  /// - Parameter detail: JSONDictionary contains all properties of `StudyAnchorDate`
  init(detail: [String: Any]) {

    if Utilities.isValidObject(someObject: detail as AnyObject?) {

      if Utilities.isValidValue(someObject: detail[kStudyAnchorDateType] as AnyObject) {
        self.anchorDateType = (detail[kStudyAnchorDateType] as? String)!
      }

      if Utilities.isValidObject(
        someObject: detail[kStudyAnchorDateQuestionInfo] as AnyObject?
      ) {

        let questionInfo = detail[kStudyAnchorDateQuestionInfo] as! [String: Any]

        if Utilities.isValidValue(
          someObject: questionInfo[kStudyAnchorDateActivityId] as AnyObject
        ) {
          self.anchorDateActivityId = (questionInfo[kStudyAnchorDateActivityId] as? String)!
        }

        if Utilities.isValidValue(
          someObject: questionInfo[kStudyAnchorDateActivityVersion] as AnyObject
        ) {
          self.anchorDateActivityVersion = (questionInfo[kStudyAnchorDateActivityVersion] as? String)!
        }

        if Utilities.isValidValue(
          someObject: questionInfo[kStudyAnchorDateQuestionKey] as AnyObject
        ) {
          self.anchorDateQuestionKey = (questionInfo[kStudyAnchorDateQuestionKey] as? String)!
        }
      }
    }
  }

  /// Sets Anchor Date based on AnchorDateType
  /// - Parameter date: Instance of Date
  func setAnchorDateFromQuestion(date: String) {

    if self.anchorDateType == "date-question" {
      guard let date = Utilities.getDateFromString(dateString: date) else {
        return
      }
      self.date = date
      DBHandler.saveAnchorDate(date: self.date!, studyId: (Study.currentStudy?.studyId)!)
    }
  }

}

// MARK: - StudyWithdrawalConfigration
class StudyWithdrawalConfigration {
  var message: String? = ""
  var type: StudyWithdrawalConfigrationType? = .notAvailable

  init() {
  }

  /// Initializes all properties
  /// - Parameter withdrawalConfigration: `JSONDictionary` contains all proeprties of `StudyWithdrawalConfigration`
  init(withdrawalConfigration: [String: Any]) {
    if Utilities.isValidObject(someObject: withdrawalConfigration as AnyObject?) {

      if Utilities.isValidValue(
        someObject: withdrawalConfigration[kStudyWithdrawalMessage] as AnyObject
      ) {
        self.message = (withdrawalConfigration[kStudyWithdrawalMessage] as? String)!
      } else {
        self.message = ""
      }

      if Utilities.isValidValue(
        someObject: withdrawalConfigration[kStudyWithdrawalType] as AnyObject
      ) {
        self.type = StudyWithdrawalConfigrationType(
          rawValue: (withdrawalConfigration[kStudyWithdrawalType] as? String)!
        )
      }

    }

  }

}
// MARK: StudyUpdates
struct StudyUpdates {

  static var studyInfoUpdated = false
  static var studyConsentUpdated = false
  static var studyActivitiesUpdated = false
  static var studyResourcesUpdated = false
  static var studyVersion: String?
  static var studyStatus: String?

  init() {
  }

  /// Initializes all properties
  /// - Parameter detail: JSONDictionary` contains all proeprties of `StudyUpdates`
  init(detail: [String: Any]) {

    if Utilities.isValidObject(someObject: detail[kStudyUpdates] as AnyObject?) {

      let updates = detail[kStudyUpdates] as! [String: Any]

      if Utilities.isValidValue(someObject: updates[kStudyResources] as AnyObject) {
        StudyUpdates.studyResourcesUpdated = (updates[kStudyResources] as? Bool)!
      }
      if Utilities.isValidValue(someObject: updates[kStudyInfo] as AnyObject) {
        StudyUpdates.studyInfoUpdated = (updates[kStudyInfo] as? Bool)!
      }
      if Utilities.isValidValue(someObject: updates[kStudyConsent] as AnyObject) {
        StudyUpdates.studyConsentUpdated = (updates[kStudyConsent] as? Bool)!
      }
      if Utilities.isValidValue(someObject: updates[kStudyActivities] as AnyObject) {
        StudyUpdates.studyActivitiesUpdated = (updates[kStudyActivities] as? Bool)!
      }
      if Utilities.isValidValue(someObject: updates["status"] as AnyObject) {
        StudyUpdates.studyStatus = updates["status"] as? String
      }
    }
    StudyUpdates.studyVersion = detail[kStudyCurrentVersion] as? String
  }
}
