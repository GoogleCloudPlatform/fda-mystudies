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

enum UserType: Int {
  case anonymousUser = 0
  case loggedInUser
}

enum LogoutReason: String {
  case userAction = "user_action"
  case error = "error"
  case securityJailbroken = "security_jailbroken"
}
enum DayValue: String {
  case sun = "Sun"
  case mon = "Mon"
  case tue = "Tue"
  case wed = "Wed"
  case thu = "Thu"
  case fri = "Fri"
  case sat = "Sat"

  var dayIndex: Int {
    switch self {
    case .sun:
      return 1
    case .mon:
      return 2
    case .tue:
      return 3
    case .wed:
      return 4
    case .thu:
      return 5
    case .fri:
      return 6
    case .sat:
      return 7

    }
  }
}

enum AccountStatus: Int {

  /// User account verified  
  case verified = 0

  /// User account not verified
  case pending = 1

  /// Logged In with temporary password
  case tempPassword = 3
}

let kUserValueForOS = "ios"

let kCFBundleShortVersionString = "CFBundleShortVersionString"
let kTerms = "terms"
let kPolicy = "privacy"

// MARK: User
class User {

  var firstName: String?
  var lastName: String?
  var emailId: String?
  var settings: Settings?
  var userType: UserType?
  var userId: String!
  var password: String? = ""
  var refreshToken: String! = ""

  /// Temporary ID to Auto login the user after successfull verification.
  var tempRegID: String?
  var verified: Bool = false
  var authToken: String!
  var participatedStudies: [UserStudyStatus]! = []
  var participatedActivites: [UserActivityStatus]! = []
  var logoutReason: LogoutReason = .userAction
  var isLoggedInWithTempPassword: Bool = false

  /// sharedInstance
  private static var _currentUser: User?

  static var currentUser: User {
    if _currentUser == nil { _currentUser = User() }
    return _currentUser!
  }

  static func resetCurrentUser() {
    _currentUser = nil
  }

  struct JSONKey {
    static let accessToken = "access_token"
    static let tokenType = "token_type"
    static let refreshToken = "refresh_token"
  }

  /// Default Initializer which initialize all properties
  init() {
    self.firstName = ""
    self.lastName = ""
    self.emailId = ""
    self.settings = Settings()
    self.userType = UserType.anonymousUser
    self.userId = ""
    self.verified = false
    self.refreshToken = ""
  }

  /// Initializer which initialize all properties
  /// - Parameters:
  ///   - firstName: `User` First Name
  ///   - lastName: `User` Last Name
  ///   - emailId: `User` Email Id
  ///   - userType: `User` Type
  ///   - userId: `User` ID
  init(
    firstName: String?,
    lastName: String?,
    emailId: String?,
    userType: UserType?,
    userId: String?
  ) {
    self.firstName = firstName
    self.lastName = lastName
    self.emailId = emailId
    self.userType = userType
    self.userId = userId
  }

  /// Setter method for `FirstName`
  /// - Parameter firstName:
  func setFirstName(firstName: String) {
    self.firstName = firstName
  }

  /// Setter method for `LastName`
  /// - Parameter lastName:
  func setLastName(lastName: String) {
    self.lastName = lastName
  }

  /// Setter method for `EmailId`
  /// - Parameter emailId:
  func setEmailId(emailId: String) {
    self.emailId = emailId
  }

  /// Setter method for `UserType`
  /// - Parameter userType:
  func setUserType(userType: UserType) {
    self.userType = userType
  }

  /// Setter method for `UserId`
  /// - Parameter userId:
  func setUserId(userId: String) {
    self.userId = userId
  }

  /// Comines FirstName and lastName
  func getFullName() -> String {
    return firstName! + " " + lastName!
  }

  /// Setter method for `User` which initialize all properties
  /// - Parameter dict: `JSONDictionary` which contains all properties of `User`
  func setUser(dict: NSDictionary) {

    if Utilities.isValidObject(someObject: dict) {

      if Utilities.isValidValue(someObject: dict[kUserFirstName] as AnyObject) {
        self.firstName = dict[kUserFirstName] as? String
      }
      if Utilities.isValidValue(someObject: dict[kUserLastName] as AnyObject) {
        self.lastName = dict[kUserLastName] as? String
      }
      if Utilities.isValidValue(someObject: dict[kUserEmailId] as AnyObject) {
        self.emailId = dict[kUserEmailId] as? String
      }
      if Utilities.isValidObject(someObject: dict[kUserSettings] as AnyObject) {
        self.settings?.setSettings(dict: (dict[kUserSettings] as? NSDictionary)!)
      }
      if Utilities.isValidValue(someObject: dict[kUserId] as AnyObject) {
        self.userId = dict[kUserId] as? String
      }
      if Utilities.isValidValue(someObject: dict[kRefreshToken] as AnyObject) {
        self.refreshToken = dict[kRefreshToken] as? String
      }
    }
  }

  /// Creates `NSMutableDictionary` of `User` based on userType
  /// - `Returns:  object of NSMutableDictionary`
  func getUserProfileDict() -> NSMutableDictionary {
    let dataDict = NSMutableDictionary()

    if self.userType == .loggedInUser {

      if self.userId != nil {
        dataDict.setValue(self.userId, forKey: ((kUserId as NSCopying) as? String)!)
      }
      let profileDict = NSMutableDictionary()

      if self.firstName != nil {
        profileDict.setValue(
          self.firstName,
          forKey: ((kUserFirstName as NSCopying) as? String)!
        )

      } else {
        profileDict.setValue("", forKey: ((kUserFirstName as NSCopying) as? String)!)
      }
      if self.lastName != nil {
        profileDict.setValue(
          self.lastName,
          forKey: ((kUserLastName as NSCopying) as? String)!
        )

      } else {
        profileDict.setValue("", forKey: ((kUserLastName as NSCopying) as? String)!)
      }

      let infoDict = NSMutableDictionary()
      infoDict.setValue(kUserValueForOS, forKey: kUserOS)

      if let version = Bundle.main.infoDictionary?[kCFBundleShortVersionString] as? String {
        infoDict.setValue(version, forKey: kUserAppVersion)
      }

      dataDict.setObject(profileDict, forKey: kUserProfile as NSCopying)
      dataDict.setObject(profileDict, forKey: kUserInfo as NSCopying)
    }
    return dataDict
  }

  /// Create an instance of UserStudyStatus based on StudyId
  /// - Parameters:
  ///   - studyId: StudyId to filter `UserActivityStatus`
  ///   - completion: Current Study runs count
  ///   - adherence: Current Study runs count
  /// - Returns: object of `UserStudyStatus`
  func udpateCompletionAndAdherence(studyId: String, completion: Int, adherence: Int)
    -> UserStudyStatus
  {

    let studies = self.participatedStudies as [UserStudyStatus]
    if let study = studies.filter({ $0.studyId == studyId }).first {

      study.adherence = adherence
      study.completion = completion

      return study

    } else {
      let studyStatus = UserStudyStatus()

      studyStatus.studyId = studyId
      studyStatus.adherence = adherence
      studyStatus.completion = completion
      self.participatedStudies.append(studyStatus)

      return studyStatus
    }
  }

  // MARK: Study Bookmark

  /// Checks `Study` bookmarked status based on StudyId
  /// - Parameter studyId: StudyId to filter `UserActivityStatus`
  /// - Returns: Boolean state of  `Study` bookmarked
  func isStudyBookmarked(studyId: String) -> Bool {

    let studies = self.participatedStudies as [UserStudyStatus]
    if let study = studies.filter({ $0.studyId == studyId }).first {
      return study.bookmarked
    }
    return false

  }

  /// Set bookmarked status to a `Study`
  /// - Parameter studyId: StudyId to filter `UserActivityStatus`
  /// - Returns: An object of `UserStudyStatus`
  func bookmarkStudy(studyId: String) -> UserStudyStatus {

    let studies = self.participatedStudies as [UserStudyStatus]
    if let study = studies.filter({ $0.studyId == studyId }).first {
      study.bookmarked = true
      return study
    } else {
      let studyStatus = UserStudyStatus()
      studyStatus.bookmarked = true
      studyStatus.studyId = studyId
      self.participatedStudies.append(studyStatus)
      return studyStatus
    }

  }

  /// Changes bookmarked status to false based on StudyID
  /// - Parameter studyId: StudyId to filter `UserActivityStatus`
  /// - Returns: An object of `UserStudyStatus`
  func removeBookbarkStudy(studyId: String) -> UserStudyStatus? {

    let studies = self.participatedStudies as [UserStudyStatus]
    if let study = studies.filter({ $0.studyId == studyId }).first {
      study.bookmarked = false
      return study
    }
    return nil
  }

  // MARK: Activity Bookmark

  /// Checks `Activity` bookmarked status based on StudyId and ActivityId
  /// - Parameters:
  ///   - studyId: StudyId to filter `UserActivityStatus`
  ///   - activityId: ActivityId to filter `UserActivityStatus`
  /// - Returns: Boolean state of `Activity` bookmarked
  func isActivityBookmarked(studyId: String, activityId: String) -> Bool {

    let activityes = self.participatedActivites as [UserActivityStatus]
    if let activity = activityes.filter({ $0.studyId == studyId && $0.activityId == activityId })
      .first
    {
      return activity.bookmarked
    }
    return false

  }

  /// Set bookmarked status to a `Activity`
  /// - Parameters:
  ///   - studyId: StudyId to filter `UserActivityStatus`
  ///   - activityId: ActivityId to filter `UserActivityStatus`
  /// - Returns: An object of `UserActivityStatus`
  func bookmarkActivity(studyId: String, activityId: String) -> UserActivityStatus {

    let activityes = self.participatedActivites as [UserActivityStatus]
    if let activity = activityes.filter({ $0.studyId == studyId && $0.activityId == activityId })
      .first
    {
      activity.bookmarked = true
      return activity
    } else {
      let activityStatus = UserActivityStatus()
      activityStatus.bookmarked = true
      activityStatus.studyId = studyId
      activityStatus.activityId = activityId
      self.participatedActivites.append(activityStatus)
      return activityStatus
    }

  }

  /// Changes bookmarked status to false based on StudyID and ActivityID
  /// - Parameters:
  ///   - studyId: StudyId  to filter `UserActivityStatus`
  ///   - activityId: ActivityId to filter `UserActivityStatus`
  func removeBookbarkActivity(studyId: String, activityId: String) {

    let activityes = self.participatedActivites as [UserActivityStatus]
    if let activity = activityes.filter({ $0.studyId == studyId && $0.activityId == activityId })
      .first
    {
      activity.bookmarked = true
    }
  }

  // MARK: Study Status

  /// Updates `Study` status
  /// - Parameters:
  ///   - studyId: StudyId to filter `UserStudyStatus`
  ///   - status: StudyStatus to be assigned to `UserStudyStatus`
  /// - Returns: Object of `UserStudyStatus`
  func updateStudyStatus(studyId: String, status: UserStudyStatus.StudyStatus) -> UserStudyStatus {

    let studies = self.participatedStudies as [UserStudyStatus]
    if let study = studies.filter({ $0.studyId == studyId }).first {
      study.status = status
      return study
    } else {
      let studyStatus = UserStudyStatus()
      studyStatus.status = status
      studyStatus.studyId = studyId
      self.participatedStudies.append(studyStatus)
      return studyStatus
    }
  }

  /// Updates `Study` participant ID
  /// - Parameters:
  ///   - studyId: StudyId  to filter `UserStudyStatus`
  ///   - participantId:
  func updateParticipantId(studyId: String, participantId: String) -> UserStudyStatus {

    let studies = self.participatedStudies as [UserStudyStatus]
    if let study = studies.filter({ $0.studyId == studyId }).first {
      study.participantId = participantId
      return study
    } else {
      let studyStatus = UserStudyStatus()
      studyStatus.participantId = participantId
      studyStatus.studyId = studyId
      self.participatedStudies.append(studyStatus)
      return studyStatus
    }
  }

  /// Retrives the `Study` status based on StudyID
  /// - Parameter studyId: StudyId which is used to filter
  /// - Returns: Study status
  func getStudyStatus(studyId: String) -> UserStudyStatus.StudyStatus {

    let studies = self.participatedStudies as [UserStudyStatus]
    if let study = studies.filter({ $0.studyId == studyId }).first {
      return study.status
    }
    return .yetToJoin
  }

  // MARK: Activity Status

  /// Updates `Activity` Status based on ActivityID and RunID
  /// - Parameters:
  ///   - studyId: StudyID to be assigned to `UserActivityStatus`
  ///   - activityId: ActivityID to filter `UserActivityStatus`
  ///   - runId: RunID to filter `UserActivityStatus`
  ///   - status: ActivityStatus to be assigned to `UserActivityStatus`
  /// - Returns:
  func updateActivityStatus(
    studyId: String,
    activityId: String,
    runId: String,
    status: UserActivityStatus.ActivityStatus
  ) -> UserActivityStatus {

    let activityes = self.participatedActivites as [UserActivityStatus]
    if let activity = activityes.filter({ $0.activityId == activityId && $0.activityRunId == runId }
    ).first {
      activity.status = status
      return activity
    } else {
      let activityStatus = UserActivityStatus()
      activityStatus.status = status
      activityStatus.studyId = studyId
      activityStatus.activityId = activityId
      activityStatus.activityRunId = runId
      self.participatedActivites.append(activityStatus)
      return activityStatus
    }
  }

  /// Retrives Activity Status based on ActivityID
  /// - Parameters:
  ///   - studyId:
  ///   - activityId: ActivityID to filter `UserActivityStatus`
  func getActivityStatus(studyId: String, activityId: String)
    -> UserActivityStatus.ActivityStatus?
  {

    let activityes = self.participatedActivites as [UserActivityStatus]
    if let activity = activityes.filter({ $0.activityId == activityId }).first {
      return activity.status
    }
    return .yetToJoin
  }

  func authenticate(with dict: JSONDictionary) {

    let tokenType = dict[JSONKey.tokenType] as? String ?? ""
    let accessToken = dict[JSONKey.accessToken] as? String ?? ""
    authToken = tokenType.capitalized + " " + accessToken
    refreshToken = dict[JSONKey.refreshToken] as? String ?? ""

    if self.verified && !self.isLoggedInWithTempPassword {

      // Set user type & save current user to DB
      userType = UserType.loggedInUser
      DBHandler().saveCurrentUser(user: self)

      // Updating Key & Vector
      let appDelegate = UIApplication.shared.delegate as? AppDelegate
      appDelegate?.updateKeyAndInitializationVector()

      FDAKeychain.shared[kUserAuthTokenKeychainKey] = authToken
      FDAKeychain.shared[kUserRefreshTokenKeychainKey] = refreshToken

      UserDefaults.standard.set(true, forKey: kPasscodeIsPending)  // For passcode setup

      StudyFilterHandler.instance.previousAppliedFilters = []
    }
  }
}

// MARK: User Settings
class Settings {

  var remoteNotifications: Bool?
  var localNotifications: Bool?
  var touchId: Bool?
  var passcode: Bool?
  var leadTime: String?
  var locale: String?

  /// Initializes all Properties
  init() {
    self.remoteNotifications = false
    self.localNotifications = true
    self.touchId = false
    self.passcode = false
    self.leadTime = "00:00"
    self.locale = ""
  }

  /// Initializes properties of `Settings`
  /// - Parameters:
  ///   - remoteNotifications:
  ///   - localNotifications: Boolean indicating local notification enabled.
  ///   - touchId: Boolean indicating TouchID enabled.
  ///   - passcode: Boolean indicating passcode enabled.
  init(remoteNotifications: Bool?, localNotifications: Bool?, touchId: Bool?, passcode: Bool?) {
    self.remoteNotifications = remoteNotifications
    self.localNotifications = localNotifications
    self.touchId = touchId
    self.passcode = passcode
  }

  /// Setter method for `remoteNotification`
  /// - Parameter value:
  func setRemoteNotification(value: Bool) {
    self.remoteNotifications = value
  }

  /// Setter methos for `localNotification`
  /// - Parameter value:
  func setLocalNotification(value: Bool) {
    self.localNotifications = value
  }

  /// Setter method for `touchId`
  /// - Parameter value:
  func setTouchId(value: Bool) {
    self.touchId = value
  }

  /// Setter method for `password`
  /// - Parameter value:
  func setPasscode(value: Bool) {
    self.passcode = value
  }

  /// Setter method for `leadTime`
  /// - Parameter value:
  func setLeadTime(value: String) {
    self.leadTime = value
  }

  /// Setter method for `Settings` which initialize all properties
  /// - Parameter dict: `JSONDictionary` which contains all properties of `Settings`
  func setSettings(dict: NSDictionary) {

    if Utilities.isValidObject(someObject: dict) {

      if Utilities.isValidValue(someObject: dict[kSettingsRemoteNotifications] as AnyObject) {
        self.remoteNotifications = dict[kSettingsRemoteNotifications] as? Bool
      }
      if Utilities.isValidValue(someObject: dict[kSettingsLocalNotifications] as AnyObject) {
        self.localNotifications = dict[kSettingsLocalNotifications] as? Bool
      }
      if Utilities.isValidValue(someObject: dict[kSettingsPassCode] as AnyObject) {
        self.passcode = dict[kSettingsPassCode] as? Bool
      }
      if Utilities.isValidValue(someObject: dict[kSettingsTouchId] as AnyObject) {
        self.touchId = dict[kSettingsTouchId] as? Bool
      }
      if Utilities.isValidValue(someObject: dict[kSettingsLeadTime] as AnyObject) {
        self.leadTime = dict[kSettingsLeadTime] as? String
      }
      if Utilities.isValidValue(someObject: dict[kLocale] as AnyObject) {
        self.locale = dict[kLocale] as? String
      }
    }
  }
}

// MARK: StudyStatus
class UserStudyStatus {

  enum StudyStatus: Int {

    case yetToJoin
    case notEligible
    case inProgress
    case completed
    case withdrawn

    var sortIndex: Int {
      switch self {
      case .inProgress:
        return 0
      case .yetToJoin:
        return 1
      case .completed:
        return 2
      case .withdrawn:
        return 3
      case .notEligible:
        return 4
      }
    }

    var description: String {
      switch self {
      case .yetToJoin:
        return "Yet To Join"
      case .inProgress:
        return "In Progress"
      case .completed:
        return "Completed"
      case .notEligible:
        return "Not Eligible"
      case .withdrawn:
        return "Withdrawn"

      }
    }

    var closedStudyDescription: String {
      switch self {
      case .yetToJoin:
        return "No participation"
      case .inProgress:
        return "Partial Participation"
      case .completed:
        return "Completed"
      case .notEligible:
        return "Not Eligible"
      case .withdrawn:
        return "Withdrawn"

      }
    }

    var upcomingStudyDescription: String {
      return "Yet to Join"
    }

    var paramValue: String {
      switch self {
      case .yetToJoin:
        return "yetToJoin"
      case .inProgress:
        return "inProgress"
      case .completed:
        return "completed"
      case .notEligible:
        return "notEligible"
      case .withdrawn:
        return "Withdrawn"

      }
    }

  }

  lazy var bookmarked: Bool = false
  lazy var studyId: String = ""
  lazy var status: StudyStatus = .yetToJoin
  lazy var consent: String = ""

  /// User joined Date for study
  var joiningDate: Date!

  lazy var completion: Int = 0
  lazy var adherence: Int = 0

  var participantId: String?
  var siteID: String!
  var tokenIdentifier: String!

  init() {}

  /// Initializer which initialize all properties
  /// - Parameter detail: `JSONDictionary` contians all properties of `UerStudyStatus`
  init(detail: [String: Any]) {

    if Utilities.isValidObject(someObject: detail as AnyObject?) {

      if Utilities.isValidValue(someObject: detail[kStudyId] as AnyObject) {
        self.studyId = (detail[kStudyId] as? String)!
      }
      if Utilities.isValidValue(someObject: detail[kBookmarked] as AnyObject) {
        self.bookmarked = (detail[kBookmarked] as? Bool)!
      }
      if Utilities.isValidValue(someObject: detail[kCompletion] as AnyObject) {
        self.completion = (detail[kCompletion] as? Int)!
      }
      if Utilities.isValidValue(someObject: detail[kAdherence] as AnyObject) {
        self.adherence = (detail[kAdherence] as? Int)!
      }
      if Utilities.isValidValue(someObject: detail[kStudyParticipantId] as AnyObject) {
        self.participantId = detail[kStudyParticipantId] as? String
      }
      self.siteID = detail["siteId"] as? String ?? ""
      self.tokenIdentifier = detail["hashedToken"] as? String ?? ""
      if Utilities.isValidValue(someObject: detail[kStudyEnrolledDate] as AnyObject) {
        self.joiningDate = Utilities.getDateFromString(
          dateString: (detail[kStudyEnrolledDate] as? String)!
        )
      }
      if Utilities.isValidValue(someObject: detail[kStatus] as AnyObject) {

        let statusValue = (detail[kStatus] as? String)!

        if StudyStatus.inProgress.paramValue == statusValue {
          self.status = .inProgress

        } else if StudyStatus.notEligible.paramValue == statusValue {
          self.status = .notEligible

        } else if StudyStatus.completed.paramValue == statusValue {
          self.status = .completed

        } else if StudyStatus.withdrawn.paramValue == statusValue {
          self.status = .withdrawn
        }
      }
      if self.status == .yetToJoin || self.status == .withdrawn || self.status == .notEligible {
        self.participantId = nil
        self.tokenIdentifier = ""
        self.siteID = ""
        self.joiningDate = nil
      }
    }
  }

  /// `JSONDictionary` contains StudyID and Bookmarked status
  /// - Returns: `JSONDictionary` object
  func getBookmarkUserStudyStatus() -> [String: Any] {

    let studyDetail =
      [
        kStudyId: self.studyId,
        kBookmarked: self.bookmarked,
      ] as [String: Any]
    return studyDetail
  }

  /// JSONDictionary` contains StudyID, StudyStatus and ParticipantID
  /// - Returns: `JSONDictionary` object
  func getParticipatedUserStudyStatus() -> [String: Any] {

    let id = self.participantId ?? ""
    let studyDetail =
      [
        kStudyId: self.studyId,
        kStudyStatus: self.status.paramValue,
        kStudyParticipantId: id,
      ] as [String: Any]
    return studyDetail
  }

  /// JSONDictionary` contains StudyID, Completion Status and Adherence Status
  /// - Returns: `JSONDictionary` object
  func getCompletionAdherence() -> [String: Any] {
    let studyDetail =
      [
        kStudyId: self.studyId,
        "completion": completion,
        "adherence": adherence,
      ] as [String: Any]
    return studyDetail
  }

}

// MARK: Terms & Policy
class TermsAndPolicy {
  var termsURL: String?
  var policyURL: String?
  static var currentTermsAndPolicy: TermsAndPolicy?

  /// Default Initializer
  init() {
    self.termsURL = ""
    self.policyURL = ""
  }

  /// Initializes all properties of `TermsAndPolicy`
  /// - Parameters:
  ///   - terms: Terms Url
  ///   - policy: Policy Url
  func initWith(terms: String, policy: String) {
    self.termsURL = terms
    self.policyURL = policy
  }

  /// Initializes all properties of `TermsAndPolicy`
  /// - Parameters:
  ///   - dict: `JSONDictionary` contains all properties of `TermsAndPolicy`
  func initWithDict(dict: [String: Any]) {
    if Utilities.isValidObject(someObject: dict as AnyObject) {

      if Utilities.isValidValue(someObject: dict[kTerms] as AnyObject?) {

        self.termsURL = (dict[kTerms] as! String?)?.trimmingCharacters(
          in: CharacterSet.whitespacesAndNewlines
        )
      } else {
        self.termsURL = ""
      }

      if Utilities.isValidValue(someObject: dict[kPolicy] as AnyObject?) {
        self.policyURL = (dict[kPolicy] as! String?)?.trimmingCharacters(
          in: CharacterSet.whitespacesAndNewlines
        )

      } else {
        self.policyURL = ""
      }
    }
  }

}

// MARK: ActivityStatus
class UserActivityStatus {

  enum ActivityStatus: Int {
    case yetToJoin
    case inProgress
    case completed
    case abandoned
    case expired

    var sortIndex: Int {
      switch self {
      case .inProgress:
        return 0
      case .yetToJoin:
        return 1
      case .completed:
        return 2
      case .abandoned:
        return 3
      case .expired:
        return 4

      }
    }

    var description: String {
      switch self {
      case .yetToJoin:
        return "  Start  "
      case .inProgress:
        return "  Resume  "
      case .completed:
        return "  Completed  "
      case .abandoned:
        return "  Incomplete  "
      case .expired:
        return "  Expired  "

      }
    }

    var paramValue: String {
      switch self {
      case .yetToJoin:
        return "yetToJoin"
      case .inProgress:
        return "inProgress"
      case .completed:
        return "completed"
      case .abandoned:
        return "abandoned"
      case .expired:
        return "expired"

      }
    }
  }

  var bookmarked: Bool = false
  var activityId: String! = ""
  var studyId: String! = ""
  var activityVersion: String! = "1.0"
  var activityRunId: String! = ""
  var totalRuns = 0
  var compeltedRuns = 0
  var incompletedRuns = 0
  var status: ActivityStatus = .yetToJoin

  init() {

  }

  /// Initializes all properties
  /// - Parameters:
  ///   - detail: `JSONDictionary` contains all properties of `UserActivityStatus`
  ///   - studyId: StudyID
  init(detail: [String: Any], studyId: String) {

    if Utilities.isValidObject(someObject: detail as AnyObject?) {

      if Utilities.isValidValue(someObject: detail[kStudyId] as AnyObject) {
        self.studyId = (detail[kStudyId] as? String)!
      }
      if Utilities.isValidValue(someObject: detail[kActivityId] as AnyObject) {
        self.activityId = (detail[kActivityId] as? String)!
      }
      if Utilities.isValidValue(someObject: detail[kActivityVersion] as AnyObject) {
        self.activityVersion = (detail[kActivityVersion] as? String)!
      }
      if Utilities.isValidValue(someObject: detail[kBookmarked] as AnyObject) {
        self.bookmarked = (detail[kBookmarked] as? Bool)!
      }
      if Utilities.isValidValue(someObject: detail[kActivityRunId] as AnyObject) {
        self.activityRunId = (detail[kActivityRunId] as? String)!
      }

      let runDetail = (detail["activityRun"] as? [String: Any])!
      if Utilities.isValidValue(someObject: runDetail["completed"] as AnyObject) {
        self.compeltedRuns = (runDetail["completed"] as? Int)!
      }

      self.studyId = studyId

      if Utilities.isValidValue(someObject: detail[kActivityStatus] as AnyObject) {

        var statusValue = detail[kActivityStatus] as? String ?? ""
        statusValue = statusValue.lowercased()

        if ActivityStatus.inProgress.paramValue.lowercased() == statusValue {
          self.status = .inProgress

        } else if ActivityStatus.yetToJoin.paramValue.lowercased() == statusValue {
          self.status = .yetToJoin

        } else if ActivityStatus.completed.paramValue.lowercased() == statusValue {
          self.status = .completed

        } else if ActivityStatus.abandoned.paramValue.lowercased() == statusValue {
          self.status = .abandoned
        }
      }
    }
  }

  /// `JSONDictionary` contains StudyId, ActivityId and Bookmarked Status
  /// - Returns: `JSONDictionary` object
  func getBookmarkUserActivityStatus() -> [String: Any] {

    let studyDetail =
      [
        kStudyId: self.studyId ?? "",
        kActivityId: self.activityId ?? "",
        kBookmarked: self.bookmarked,
      ] as [String: Any]
    return studyDetail
  }

  /// `JSONDictionary` contains StudyId, ActivityId and ActivityRuns
  /// - Returns: `JSONDictionary` object
  func getParticipatedUserActivityStatus() -> [String: Any] {

    let runDetail = [
      "total": self.totalRuns,
      "completed": self.compeltedRuns,
      "missed": self.incompletedRuns,
    ]

    let studyDetail =
      [
        kActivityId: self.activityId ?? "",
        kActivityRunId: self.activityRunId ?? "",
        kActivityStatus: self.status.paramValue,
        "activityRun": runDetail,
        kActivityVersion: self.activityVersion ?? "",
      ] as [String: Any]

    return studyDetail
  }
}
