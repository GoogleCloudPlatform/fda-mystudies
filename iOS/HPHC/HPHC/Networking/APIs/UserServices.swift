// License Agreement for FDA My Studies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is
// hereby granted, free of charge, to any person obtaining a copy of this software and associated
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

// MARK: - common keys
let kAppVersion = "appVersion"
let kOSType = "os"
let kDeviceToken = "deviceToken"

// MARK: - Registration Server API Constants
let kUserFirstName = "firstName"
let kUserLastName = "lastName"
let kUserEmailId = "emailId"
let kUserSettings = "settings"
let kUserId = "userId"
let kLocale = "locale"
let kParticipantInfo = "participantInfo"
let kUserProfile = "profile"
let kUserInfo = "info"
let kUserOS = "os"
let kUserAppVersion = "appVersion"
let kUserPassword = "password"
let kUserLogoutReason = "reason"
let kBasicInfo = "info"
let kStudyId = "studyId"
let kDeleteData = "deleteData"
let kUserVerified = "verified"
let kUserAuthToken = "auth"
let kStudies = "studies"
let kActivites = "activities"
let kActivityKey = "activity"
let kConsent = "consent"
let kEligibility = "eligibility"
let kUserEligibilityStatus = "eligbibilityStatus"
let kUserConsentStatus = "consentStatus"
let kUserOldPassword = "currentPassword"
let kUserNewPassword = "newPassword"
let kUserIsTempPassword = "resetPassword"
let kPasscodeIsPending = "PASSCODESETUP"
let kShowNotification = "SHOWNOTIFICATION"
let kConsentpdf = "pdf"

// MARK: - Settings Api Constants
let kSettingsRemoteNotifications = "remoteNotifications"
let kSettingsLocalNotifications = "localNotifications"
let kSettingsPassCode = "passcode"
let kSettingsTouchId = "touchId"
let kSettingsLeadTime = "reminderLeadTime"
let kSettingsLocale = "locale"
let kVerifyCode = "code"
let kDeactivateAccountDeleteData = "deleteData"
let kBookmarked = "bookmarked"
let kStatus = "status"
let kActivityId = "activityId"
let kActivityVersion = "activityVersion"
let kActivityRunId = "activityRunId"
let kCompletion = "completion"
let kAdherence = "adherence"

// MARK: - Logout Api constants
let kLogoutReason = "reason"
let kLogoutReasonValue = "Logout"

// MARK: - Refresh token constants
let kRefreshToken = "refreshToken"

struct FailedUserServices {
  var requestParams: [String: Any]? = [:]
  var headerParams: [String: String]? = [:]
  var method: Method!
}

class UserServices: NSObject {

  let networkManager = NetworkManager.sharedInstance()
  var delegate: NMWebServiceDelegate?
  var requestParams: [String: Any]? = [:]
  var headerParams: [String: String]? = [:]
  var method: Method!
  var failedRequestServices = FailedUserServices()

  // MARK: - Requests

  /// Creates a request to login an `User`
  /// - Parameter delegate: Class object to receive response
  func loginUser(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let params = [
      kUserEmailId: user.emailId!,
      kUserPassword: user.password!,
      "appId": Utilities.getBundleIdentifier(),
    ]

    let method = RegistrationMethods.login.method

    self.sendRequestWith(method: method, params: params, headers: nil)

  }

  /// Creates a request for new `User`
  /// - Parameter delegate: Class object to receive response
  func registerUser(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser

    let params = [
      kUserEmailId: user.emailId!,
      kUserPassword: user.password!,
      "appId": Utilities.getBundleIdentifier(),
    ]

    let method = RegistrationMethods.register.method
    self.sendRequestWith(method: method, params: params, headers: nil)

  }

  /// Creates a request to confirm `User` registation
  /// - Parameter delegate: Class object to receive response
  func confirmUserRegistration(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!]
    let method = RegistrationMethods.confirmRegistration.method
    self.sendRequestWith(method: method, params: nil, headers: headerParams)

  }

  /// Creates a request to verify an `User`
  /// - Parameters:
  ///   - emailId: Email Id of the`User ` to verify
  ///   - verificationCode: Code which is to be verified
  ///   - delegate: Class object to receive response
  func verifyEmail(emailId: String, verificationCode: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let param = [
      kVerifyCode: verificationCode,
      kUserEmailId: emailId,
    ]
    let method = RegistrationMethods.verify.method
    self.sendRequestWith(method: method, params: param, headers: nil)

  }

  /// Creates a request to reconfirm an `User`
  /// - Parameters:
  ///   - emailId: Email Id of the `User
  ///   - delegate: Class object to receive response
  func resendEmailConfirmation(emailId: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate
    let params = [kUserEmailId: emailId]
    let method = RegistrationMethods.resendConfirmation.method
    self.sendRequestWith(method: method, params: params, headers: nil)

  }

  /// Creates a request to logout an `User`
  /// - Parameter delegate: Class object to receive response
  func logoutUser(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!]
    let params = [kUserLogoutReason: user.logoutReason.rawValue]

    let method = RegistrationMethods.logout.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)

  }

  /// Creates a request to delete an `User` account
  /// - Parameter delegate: Class object to receive response
  func deleteAccount(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserAuthToken: user.authToken] as [String: String]
    let method = RegistrationMethods.deleteAccount.method
    self.sendRequestWith(method: method, params: nil, headers: headerParams)
  }

  /// Creates a request to deactivate an `User` account
  /// - Parameters:
  ///   - listOfStudyIds: Collection of Study Id
  ///   - delegate: Class object to receive response
  func deActivateAccount(listOfStudyIds: [String], delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [
      kUserAuthToken: user.authToken,
      kUserId: user.userId!,
    ] as [String: String]

    let params = [kDeactivateAccountDeleteData: listOfStudyIds]

    let method = RegistrationMethods.deactivate.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to reset the `User` password
  /// - Parameters:
  ///   - email: Email Id of the`User `
  ///   - delegate: Class object to receive response
  func forgotPassword(email: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    // let user = User.currentUser
    let params = [kUserEmailId: email]
    let method = RegistrationMethods.forgotPassword.method

    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to change the `User` password
  /// - Parameters:
  ///   - oldPassword:
  ///   - newPassword:
  ///   - delegate: Class object to receive response
  func changePassword(oldPassword: String, newPassword: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!]
    let params = [
      kUserOldPassword: oldPassword,
      kUserNewPassword: newPassword,
    ]

    let method = RegistrationMethods.changePassword.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to get `User` profile information
  /// - Parameter delegate: Class object to receive response
  func getUserProfile(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser

    let headerParams = [kUserId: user.userId!]
    let method = RegistrationMethods.userProfile.method
    self.sendRequestWith(method: method, params: nil, headers: headerParams)
  }

  /// Creates a request to update `User` profile
  /// - Parameter delegate: Class object to receive response
  func updateUserProfile(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!]

    let settings = [
      kSettingsRemoteNotifications: (user.settings?.remoteNotifications)! as Bool,
      kSettingsTouchId: (user.settings?.touchId)! as Bool,
      kSettingsPassCode: (user.settings?.passcode)! as Bool,
      kSettingsLocalNotifications: (user.settings?.localNotifications)! as Bool,
      kSettingsLeadTime: (user.settings?.leadTime)! as String,
      kSettingsLocale: (user.settings?.locale)! as String,
    ] as [String: Any]

    let version = Utilities.getAppVersion()
    let token = Utilities.getBundleIdentifier()
    let info = [
      kAppVersion: version,
      kOSType: "ios",
      kDeviceToken: token,
    ]

    let params = [
      kUserSettings: settings,
      kBasicInfo: info,
      kParticipantInfo: [],
    ] as [String: Any]

    let method = RegistrationMethods.updateUserProfile.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update `User` profile
  /// - Parameters:
  ///   - deviceToken:
  ///   - delegate: Class object to receive response
  func updateUserProfile(deviceToken: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate
    let user = User.currentUser
    let headerParams = [kUserId: user.userId!]
    let version = Utilities.getAppVersion()
    let info = [
      kAppVersion: version,
      kOSType: "ios",
      kDeviceToken: deviceToken,
    ]

    let params = [

      kBasicInfo: info,
      kParticipantInfo: [],
    ] as [String: Any]

    let method = RegistrationMethods.updateUserProfile.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to get `User`preferences
  /// - Parameter delegate: Class object to receive response
  func getUserPreference(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [
      kUserId: user.userId!,
      kUserAuthToken: user.authToken,
    ] as [String: String]

    let method = RegistrationMethods.userPreferences.method

    self.sendRequestWith(method: method, params: nil, headers: headerParams)
  }

  /// Creates a request to get `Study` States
  /// - Parameter delegate: Class object to receive response
  func getStudyStates(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!] as [String: String]
    let method = RegistrationMethods.studyState.method

    self.sendRequestWith(method: method, params: nil, headers: headerParams)
  }

  /// Creates a request to update `Study` status
  /// - Parameters:
  ///   - studyStatus: Instance of `UserStudyStatus` to update
  ///   - delegate: Class object to receive response
  func updateCompletionAdherence(studyStatus: UserStudyStatus, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!]

    let params = [kStudies: [studyStatus.getCompletionAdherence()]] as [String: Any]
    let method = RegistrationMethods.updateStudyState.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update `Study` bookmark status
  /// - Parameters:
  ///   - studyStatus: Instance of `UserStudyStatus` to update
  ///   - delegate: Class object to receive response
  func updateStudyBookmarkStatus(studyStatus: UserStudyStatus, delegate: NMWebServiceDelegate) {
    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!]

    let params = [kStudies: [studyStatus.getBookmarkUserStudyStatus()]] as [String: Any]
    let method = RegistrationMethods.updateStudyState.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update `Activity` bookmark status
  /// - Parameters:
  ///   - activityStauts: Instance of `UserActivityStatus` to update
  ///   - delegate: Class object to receive response
  func updateActivityBookmarkStatus(
    activityStauts: UserActivityStatus,
    delegate: NMWebServiceDelegate
  ) {
    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId] as [String: String]

    let params = [kActivites: [activityStauts.getBookmarkUserActivityStatus()]] as [String: Any]
    let method = RegistrationMethods.updateActivityState.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update `Study` participation status
  /// - Parameters:
  ///   - studyStauts: Instance of `UserStudyStatus` to update
  ///   - delegate: Class object to receive response
  func updateUserParticipatedStatus(studyStauts: UserStudyStatus, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId] as [String: String]
    let params = [kStudies: [studyStauts.getParticipatedUserStudyStatus()]] as [String: Any]
    let method = RegistrationMethods.updateStudyState.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update `Activity` participation status
  /// - Parameters:
  ///   - studyId: ID of Study
  ///   - activityStatus: Instance of `UserActivityStatus` to update
  ///   - delegate: Class object to receive response
  func updateUserActivityParticipatedStatus(
    studyId: String,
    activityStatus: UserActivityStatus,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId] as [String: String]
    let params = [
      kStudyId: studyId,
      kActivity: [activityStatus.getParticipatedUserActivityStatus()],
    ] as [String: Any]
    let method = RegistrationMethods.updateActivityState.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update Consent status
  /// - Parameters:
  ///   - eligibilityStatus:
  ///   - consentStatus: Instance of `ConsentStatus`
  ///   - delegate: Class object to receive response
  func updateUserEligibilityConsentStatus(
    eligibilityStatus: Bool,
    consentStatus: ConsentStatus,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [
      kUserId: user.userId! as String,
      kUserAuthToken: user.authToken! as String,
    ]

    let consentVersion: String?
    if (ConsentBuilder.currentConsent?.version?.count)! > 0 {
      consentVersion = ConsentBuilder.currentConsent?.version!
    } else {
      consentVersion = "1"
    }

    let base64data = ConsentBuilder.currentConsent?.consentResult?.consentPdfData!
      .base64EncodedString()

    let consent = [
      kConsentDocumentVersion: consentVersion! as String,
      kStatus: consentStatus.rawValue,
      kConsentpdf: "\(base64data!)" as Any,
    ] as [String: Any]

    let params = [
      kStudyId: (Study.currentStudy?.studyId!)! as String,
      kEligibility: eligibilityStatus,
      kConsent: consent,
      kConsentSharing: "",
    ] as [String: Any]
    let method = RegistrationMethods.updateEligibilityConsentStatus.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to get Consent pdf
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getConsentPDFForStudy(studyId: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let params = [
      kStudyId: studyId,
      "consentVersion": "",
    ]

    let headerParams = [kUserId: user.userId!]
    let method = RegistrationMethods.consentPDF.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update `Activity` status
  /// - Parameter delegate: Class object to receive response
  func updateUserActivityState(_ delegate: NMWebServiceDelegate) {
    self.delegate = delegate
  }

  /// Creates a request to get `Activity` status
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getUserActivityState(studyId: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let params = [kStudyId: studyId]
    let headerParams = [kUserId: user.userId!]
    let method = RegistrationMethods.activityState.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to withdraw from `Study`
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - shouldDeleteData: withdraw status
  ///   - delegate: Class object to receive response
  func withdrawFromStudy(studyId: String, shouldDeleteData: Bool, delegate: NMWebServiceDelegate) {

    self.delegate = delegate
    let user = User.currentUser
    let headerParams = [kUserId: user.userId! as String]

    let params = [
      kStudyId: studyId,
      kDeleteData: shouldDeleteData,
    ] as [String: Any]

    let method = RegistrationMethods.withdraw.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update RefreshToken
  func updateToken() {

    let user = User.currentUser

    let param = [kRefreshToken: user.refreshToken!]
    let method = RegistrationMethods.refreshToken.method
    self.sendRequestWith(method: method, params: param, headers: nil)

  }

  /// Creattes a request to sync offline data
  /// - Parameters:
  ///   - method: Instance of `Method`
  ///   - params:  Request Params
  ///   - headers: Request headers
  ///   - delegate: Class object to receive response
  func syncOfflineSavedData(
    method: Method,
    params: [String: Any]?,
    headers: [String: String]?,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate
    self.sendRequestWith(method: method, params: params, headers: headers)
  }

  // MARK: Parsers

  /// Handles login response
  /// - Parameter response: Webservice response
  func handleUserLoginResponse(response: [String: Any]) {

    let user = User.currentUser
    user.userId = (response[kUserId] as? String)!
    user.verified = (response[kUserVerified] as? Bool)!
    user.authToken = (response[kUserAuthToken] as? String)!
    if let refreshToken = response[kRefreshToken] as? String {
      user.refreshToken = refreshToken

    }

    if let isTempPassword = response[kUserIsTempPassword] as? Bool {
      user.isLoginWithTempPassword = isTempPassword
    }

    if user.verified! && !user.isLoginWithTempPassword {

      // Set user type & save current user to DB
      user.userType = UserType.FDAUser
      DBHandler().saveCurrentUser(user: user)

      // Updating Key & Vector
      let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
      appDelegate.updateKeyAndInitializationVector()

      FDAKeychain.shared[kUserAuthTokenKeychainKey] = user.authToken
      FDAKeychain.shared[kUserRefreshTokenKeychainKey] = user.refreshToken

      let ud = UserDefaults.standard
      ud.set(true, forKey: kPasscodeIsPending)
      ud.synchronize()

      StudyFilterHandler.instance.previousAppliedFilters = []

    }

  }

  /// Handles registration response
  /// - Parameter response: Webservice response
  func handleUserRegistrationResponse(response: [String: Any]) {

    let user = User.currentUser
    user.userId = (response[kUserId] as? String)!
    user.verified = (response[kUserVerified] as? Bool)!
    user.authToken = (response[kUserAuthToken] as? String)!

    user.refreshToken = (response[kRefreshToken] as? String)!
    StudyFilterHandler.instance.previousAppliedFilters = []

  }

  /// Handles registration confirmation response
  /// - Parameter response: Webservice response
  func handleConfirmRegistrationResponse(response: [String: Any]) {

    let user = User.currentUser
    if let varified = response[kUserVerified] as? Bool {

      user.verified = varified
      if user.verified! {

        user.userType = UserType.FDAUser

        FDAKeychain.shared[kUserAuthTokenKeychainKey] = user.authToken
        FDAKeychain.shared[kUserRefreshTokenKeychainKey] = user.refreshToken

        // Save Current User to DB
        DBHandler().saveCurrentUser(user: user)
        StudyFilterHandler.instance.previousAppliedFilters = []
      }
    }
  }

  /// Handles email verification response
  /// - Parameter response: Webservice response
  func handleEmailVerifyResponse(response: [String: Any]) {

    let user = User.currentUser
    user.verified = true

    if user.verified! {

      if user.authToken != nil {

        user.userType = UserType.FDAUser

        FDAKeychain.shared[kUserAuthTokenKeychainKey] = user.authToken
        FDAKeychain.shared[kUserRefreshTokenKeychainKey] = user.refreshToken

        let ud = UserDefaults.standard
        ud.set(true, forKey: kPasscodeIsPending)
        ud.synchronize()

        DBHandler().saveCurrentUser(user: user)
      }
    }
  }

  /// handles `User` profile response
  /// - Parameter response: Webservice response
  func handleGetUserProfileResponse(response: [String: Any]) {

    let user = User.currentUser

    // settings
    let settings = (response[kUserSettings] as? [String: Any])!
    let userSettings = Settings()
    userSettings.setSettings(dict: settings as NSDictionary)
    user.settings = userSettings

    DBHandler.saveUserSettingsToDatabase()

    // profile
    let profile = (response[kUserProfile] as? [String: Any])!
    user.emailId = profile[kUserEmailId] as? String
    user.firstName = profile[kUserFirstName] as? String
    user.lastName = profile[kUserLastName] as? String
  }

  func handleUpdateUserProfileResponse(response: [String: Any]) {
  }

  func handleResendEmailConfirmationResponse(response: [String: Any]) {
  }

  /// Handles change password response
  /// - Parameter response: Webservice response
  func handleChangePasswordResponse(response: [String: Any]) {

    let user = User.currentUser
    if user.verified! {
      user.userType = UserType.FDAUser
      DBHandler().saveCurrentUser(user: user)
      let ud = UserDefaults.standard
      ud.set(user.userId!, forKey: kUserId)
      ud.synchronize()
    }

  }

  /// Handles `User` preference response
  /// - Parameter response: Webservice response
  func handleGetPreferenceResponse(response: [String: Any]) {

    let user = User.currentUser
    if let studies = response[kStudies] as? [[String: Any]] {
      for study in studies {
        let participatedStudy = UserStudyStatus(detail: study)
        user.participatedStudies.append(participatedStudy)
      }
    }

  }

  /// Handles `Study` status response
  /// - Parameter response: Webservice response
  func handleGetStudyStatesResponse(response: [String: Any]) {
    let user = User.currentUser
    user.participatedStudies.removeAll()
    if let studies = response[kStudies] as? [[String: Any]] {

      for study in studies {
        let participatedStudy = UserStudyStatus(detail: study)
        user.participatedStudies.append(participatedStudy)
      }
    }
  }

  /// Handles `Activity` status response
  /// - Parameter response: Webservice response
  func handleGetActivityStatesResponse(response: [String: Any]) {
    let user = User.currentUser
    if let activites = response[kActivites] as? [[String: Any]] {
      if Study.currentStudy != nil {
        for activity in activites {
          let participatedActivity = UserActivityStatus(
            detail: activity,
            studyId: (Study.currentStudy?.studyId)!
          )
          user.participatedActivites.append(participatedActivity)
        }
      }
    }
  }

  func handleUpdateEligibilityConsentStatusResponse(response: [String: Any]) {

  }

  func handleGetConsentPDFResponse(response: [String: Any]) {

    if Utilities.isValidValue(someObject: response[kConsent] as AnyObject?) {
      //Do nothing
    }
  }

  func handleUpdateActivityStateResponse(response: [String: Any]) {

  }

  func handleGetActivityStateResponse(response: [String: Any]) {
    _ = (response[kActivites] as? [[String: Any]])!
  }

  func handleWithdrawFromStudyResponse(response: [String: Any]) {
  }

  func handleLogoutResponse(response: [String: Any]) {

    let appDomain = Bundle.main.bundleIdentifier!
    UserDefaults.standard.removePersistentDomain(forName: appDomain)
    UserDefaults.standard.synchronize()

    // Delete from database
    DBHandler.deleteCurrentUser()

    // reset user object
    User.resetCurrentUser()

    // delete complete database
    DBHandler.deleteAll()

    // cancel all local notification
    LocalNotification.cancelAllLocalNotification()

    // reset Filters
    StudyFilterHandler.instance.previousAppliedFilters = []
    StudyFilterHandler.instance.searchText = ""

    // delete keychain values
    FDAKeychain.shared[kUserAuthTokenKeychainKey] = nil
    FDAKeychain.shared[kUserRefreshTokenKeychainKey] = nil

  }

  func handleDeleteAccountResponse(response: [String: Any]) {

    let appDomain = Bundle.main.bundleIdentifier!
    UserDefaults.standard.removePersistentDomain(forName: appDomain)
    UserDefaults.standard.synchronize()

    // Delete from database
    DBHandler.deleteCurrentUser()

    // reset user object
    User.resetCurrentUser()

    // delete complete database
    DBHandler.deleteAll()

    // cancel all local notification
    LocalNotification.cancelAllLocalNotification()

    // reset Filters
    StudyFilterHandler.instance.previousAppliedFilters = []
    StudyFilterHandler.instance.searchText = ""

  }

  func handleDeActivateAccountResponse(response: [String: Any]) {
    let ud = UserDefaults.standard
    ud.removeObject(forKey: kUserAuthToken)
    ud.removeObject(forKey: kUserId)
    ud.synchronize()

    let appDomain = Bundle.main.bundleIdentifier!
    UserDefaults.standard.removePersistentDomain(forName: appDomain)
    UserDefaults.standard.synchronize()

    // Delete from database
    DBHandler.deleteCurrentUser()

    // reset user object
    User.resetCurrentUser()

    // delete complete database
    DBHandler.deleteAll()

    // cancel all local notification
    LocalNotification.cancelAllLocalNotification()

    // reset Filters
    StudyFilterHandler.instance.previousAppliedFilters = []
    StudyFilterHandler.instance.searchText = ""
  }

  func handleUpdateTokenResponse(response: [String: Any]) {

    let user = User.currentUser
    user.authToken = (response[kUserAuthToken] as? String)!

    FDAKeychain.shared[kUserAuthTokenKeychainKey] = user.authToken
    FDAKeychain.shared[kUserRefreshTokenKeychainKey] = user.refreshToken

    DBHandler().saveCurrentUser(user: user)
    //re-send request which failed due to session expired

    let headerParams = self.failedRequestServices.headerParams == nil
      ? [:] : self.failedRequestServices.headerParams
    self.sendRequestWith(
      method: self.failedRequestServices.method,
      params: (self.requestParams == nil ? nil : self.requestParams),
      headers: headerParams
    )

  }

  /// Sends Request
  /// - Parameters:
  ///   - method: instance of `Method`
  ///   - params: request params
  ///   - headers: request headers
  private func sendRequestWith(method: Method, params: [String: Any]?, headers: [String: String]?) {

    self.requestParams = params
    self.headerParams = headers
    self.method = method
    networkManager.composeRequest(
      RegistrationServerConfiguration.configuration,
      method: method,
      params: params as NSDictionary?,
      headers: headers as NSDictionary?,
      delegate: self
    )
  }

}
extension UserServices: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    delegate?.startedRequest(manager, requestName: requestName)
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    Logger.sharedInstance.info(
      "RUS Received Data: \(requestName), \(String(describing: response))"
    )
    switch requestName {
    case RegistrationMethods.login.description as String:

      self.handleUserLoginResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.register.description as String:

      self.handleUserRegistrationResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.confirmRegistration.description as String:

      self.handleConfirmRegistrationResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.verify.description as String:

      self.handleEmailVerifyResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.userProfile.description as String:

      self.handleGetUserProfileResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.updateUserProfile.description as String:

      self.handleUpdateUserProfileResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.userPreferences.description as String:

      self.handleGetPreferenceResponse(response: (response as? [String: Any])!)
    case RegistrationMethods.changePassword.description as String:

      self.handleChangePasswordResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.updatePreferences.description as String: break  //did not handled response

    case RegistrationMethods.updateEligibilityConsentStatus.description as String: break
    case RegistrationMethods.consentPDF.description as String: break
    case RegistrationMethods.studyState.description as String:
      self.handleGetStudyStatesResponse(response: (response as? [String: Any])!)
    case RegistrationMethods.updateStudyState.description as String: break
    case RegistrationMethods.updateActivityState.description as String: break
    case RegistrationMethods.activityState.description as String:
      self.handleGetActivityStatesResponse(response: (response as? [String: Any])!)
    case RegistrationMethods.withdraw.description as String: break
    case RegistrationMethods.forgotPassword.description as String: break

    case RegistrationMethods.logout.description as String:
      self.handleLogoutResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.deleteAccount.description as String:
      self.handleDeleteAccountResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.deactivate.description as String:
      self.handleDeActivateAccountResponse(response: (response as? [String: Any])!)
    case RegistrationMethods.refreshToken.description as String:
      self.handleUpdateTokenResponse(response: (response as? [String: Any])!)
    default: break
    }

    delegate?.finishedRequest(manager, requestName: requestName, response: response)

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    if error.code == 401 {

      self.failedRequestServices.headerParams = self.headerParams
      self.failedRequestServices.requestParams = self.requestParams
      self.failedRequestServices.method = self.method

      Logger.sharedInstance.error("Failed: Refresh token Expired", error)

      if User.currentUser.refreshToken == ""
        && requestName as String
          != RegistrationMethods
          .login
          .description
      {
        // Unauthorized Access
        let errorInfo = ["NSLocalizedDescription": "Your Session is Expired"]
        let localError = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
        delegate?.failedRequest(manager, requestName: requestName, error: localError)

      } else {
        // Update Refresh Token
        self.updateToken()
      }

    } else {

      var errorInfo = error.userInfo
      var localError = error
      if error.code == 403 {
        errorInfo = ["NSLocalizedDescription": "Your Session is Expired"]
        localError = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
      }

      delegate?.failedRequest(manager, requestName: requestName, error: localError)

      // handle failed request due to network connectivity
      if requestName as String == RegistrationMethods.updateStudyState.description
        || requestName
          as String == RegistrationMethods.updateActivityState.description
      {

        if error.code == NoNetworkErrorCode {
          // save in database
          DBHandler.saveRequestInformation(
            params: self.requestParams,
            headers: self.headerParams,
            method: requestName as String,
            server: "registration"
          )
        }
      }
    }
  }
}
