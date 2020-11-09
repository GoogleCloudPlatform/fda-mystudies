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
let kUserAuthToken = "accessToken"
let kAuthorization = "Authorization"
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
  weak var delegate: NMWebServiceDelegate?
  var requestParams: [String: Any]? = [:]
  var headerParams: [String: String]? = [:]
  var method: Method!
  var failedRequestServices = FailedUserServices()

  struct JSONKey {
    static let tempRegID = "tempRegId"
  }

  // MARK: - Requests

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
  func verifyEmail(
    emailId: String,
    verificationCode: String,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate
    let param = [
      kVerifyCode: verificationCode,
      "emailId": emailId,
    ]

    let method = RegistrationMethods.verifyEmailId.method
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

  /// Creates a request to deactivate an `User` account
  /// - Parameters:
  ///   - listOfStudyIds: Collection of Study Id
  ///   - delegate: Class object to receive response
  func deActivateAccount(studiesToDelete: [StudyToDelete], delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    var studiesDict: [JSONDictionary] = []
    for studyToDelete in studiesToDelete {
      let dict: JSONDictionary =
        [
          "studyId": studyToDelete.studyId,
          "delete": "\(studyToDelete.shouldDelete ?? false)",
        ]
      studiesDict.append(dict)
    }
    let headerParams =
      [
        kUserId: user.userId ?? "",
      ]

    let params = [kDeactivateAccountDeleteData: studiesDict]
    let method = RegistrationMethods.deactivate.method
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

    let settings =
      [
        kSettingsRemoteNotifications: (user.settings?.remoteNotifications)! as Bool,
        kSettingsTouchId: (user.settings?.touchId)! as Bool,
        kSettingsPassCode: (user.settings?.passcode)! as Bool,
        kSettingsLocalNotifications: (user.settings?.localNotifications)! as Bool,
        kSettingsLeadTime: (user.settings?.leadTime)! as String,
        kSettingsLocale: (user.settings?.locale)! as String,
      ] as [String: Any]

    let version = Utilities.getAppVersion()

    let info = [
      kAppVersion: version,
      kOSType: "ios",
      kDeviceToken: "",
    ]

    let params =
      [
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

    let params =
      [
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
    let headerParams =
      [
        kUserId: user.userId!,
        kUserAuthToken: user.authToken,
      ] as? [String: String]

    let method = RegistrationMethods.userPreferences.method

    self.sendRequestWith(method: method, params: nil, headers: headerParams)
  }

  /// Creates a request to send `User` feedback
  /// - Parameter delegate: Class object to receive response
  func sendUserFeedback(delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = RegistrationMethods.feedback.method
    let params = [
      kFeedbackBody: FeedbackDetail.feedback,
      kFeedbackSubject: FeedbackDetail.subject,
    ]
    self.sendRequestWith(method: method, params: params, headers: nil)

  }

  /// Creates a request to send ContactUs Request
  /// - Parameter delegate: Class object to receive response
  func sendUserContactUsRequest(delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = RegistrationMethods.contactUs.method
    let params = [
      kFeedbackBody: ContactUsFields.message,
      kFeedbackSubject: ContactUsFields.subject,
      kContactusEmail: ContactUsFields.email,
      kContactusFirstname: ContactUsFields.firstName,
    ]
    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to update `Activity` status
  /// - Parameter delegate: Class object to receive response
  func updateUserActivityState(_ delegate: NMWebServiceDelegate) {
    self.delegate = delegate
  }

  // MARK: Parsers

  /// Handles registration response
  /// - Parameter response: Webservice response
  func handleUserRegistrationResponse(response: [String: Any]) {
    let user = User.currentUser
    user.userId = response[kUserId] as? String ?? ""
    StudyFilterHandler.instance.previousAppliedFilters = []
  }

  /// Handles registration confirmation response
  /// - Parameter response: Webservice response
  func handleConfirmRegistrationResponse(response: [String: Any]) {

    let user = User.currentUser
    if let varified = response[kUserVerified] as? Bool {

      user.verified = varified
      if user.verified {

        user.userType = UserType.loggedInUser

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
    user.tempRegID = response[JSONKey.tempRegID] as? String
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

  func handleUpdateEligibilityConsentStatusResponse(response: [String: Any]) {}

  func handleGetConsentPDFResponse(response: [String: Any]) {}

  func handleUpdateActivityStateResponse(response: [String: Any]) {}

  func handleGetActivityStateResponse(response: [String: Any]) {
    _ = (response[kActivites] as? [[String: Any]])!
  }

  func handleWithdrawFromStudyResponse(response: [String: Any]) {}

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
    switch requestName {
    case RegistrationMethods.register.description as String:
      self.handleUserRegistrationResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.confirmRegistration.description as String:
      self.handleConfirmRegistrationResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.verifyEmailId.description as String:
      self.handleEmailVerifyResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.userProfile.description as String:
      self.handleGetUserProfileResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.updateUserProfile.description as String:
      self.handleUpdateUserProfileResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.userPreferences.description as String:
      self.handleGetPreferenceResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.updatePreferences.description as String: break  //did not handled response

    case RegistrationMethods.deactivate.description as String:
      self.handleDeActivateAccountResponse(response: (response as? [String: Any])!)

    default: break
    }

    delegate?.finishedRequest(manager, requestName: requestName, response: response)

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    if requestName as String == AuthServerMethods.getRefreshedToken.description && error.code == 401 {  //unauthorized
      delegate?.failedRequest(manager, requestName: requestName, error: error)
    } else if error.code == 401 {

      self.failedRequestServices.headerParams = self.headerParams
      self.failedRequestServices.requestParams = self.requestParams
      self.failedRequestServices.method = self.method

      if User.currentUser.refreshToken == ""
        && requestName as String
          != AuthServerMethods
          .login
          .description
      {
        // Unauthorized Access
        let errorInfo = ["NSLocalizedDescription": "Your Session is Expired"]
        let localError = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
        delegate?.failedRequest(manager, requestName: requestName, error: localError)

      } else {
        // Update Refresh Token
        AuthServices().updateToken(delegate: self)
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
      if requestName
        as String == ResponseMethods.updateActivityState.description
      {

        if error.code == kNoNetworkErrorCode {
          // save in database
          DBHandler.saveRequestInformation(
            params: self.requestParams,
            headers: self.headerParams,
            method: requestName as String,
            server: SyncUpdate.ServerType.response.rawValue
          )
        }
      }
    }
  }
}
