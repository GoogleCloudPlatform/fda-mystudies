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
let kVerificationTime = "verificationTime"
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
let kDeactivateAccountDeleteData = "studyData"
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

class UserServices: NSObject {

  let networkManager = NetworkManager.sharedInstance()
  weak var delegate: NMWebServiceDelegate?
  var requestParams: [String: Any]? = [:]
  var headerParams: [String: String]? = [:]
  var method: Method!

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
    ]

    let method = RegistrationMethods.register.method
    self.sendRequestWith(method: method, params: params, headers: nil)

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
        ]
      studiesDict.append(dict)
    }
    let headerParams =
      [
        kUserId: user.userId ?? ""
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
  
  /// Creates a request to get `User` profile information
  /// - Parameter delegate: Class object to receive response
  func getUserManageApps(_ delegate: NMWebServiceDelegate) {
    
    self.delegate = delegate
    let headerParams = ["appId": AppConfiguration.appID]
    let params =
      [
        "appId": AppConfiguration.appID
      ] as [String: Any]
    let method = RegistrationMethods.apps.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)
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
    let deviceToken = UserDefaults.standard.value(forKey: kDeviceToken) as? String ?? ""
    let info = [
      kAppVersion: version,
      kOSType: "ios",
      kDeviceToken: deviceToken,
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
    let ud = UserDefaults.standard
    ud.set(deviceToken, forKey: kDeviceToken)
    ud.synchronize()
    
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

  /// Creates a request to send `User` feedback
  /// - Parameter delegate: Class object to receive response
  func sendUserFeedback(delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = RegistrationMethods.feedback.method
    let params = [
      kFeedbackBody: FeedbackDetail.feedback,
      kFeedbackSubject: FeedbackDetail.subject,
    ]
    let headers = [
      "appName": Utilities.appName()
    ]
    self.sendRequestWith(method: method, params: params, headers: headers)

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
      "supportEmail": UserManageApps.appDetails?.supportEmail ?? "",
      kContactusFirstname: ContactUsFields.firstName,
    ]
    let headers = [
      "appName": Utilities.appName()
    ]
    self.sendRequestWith(method: method, params: params, headers: headers)
  }

  func updateToken(manager: NetworkManager, requestName: NSString, error: NSError) {
    HydraAPI.refreshToken { (status, error) in
      if status {
        self.handleUpdateTokenResponse()
      } else if let error = error {
        self.delegate?.failedRequest(
          manager,
          requestName:
            requestName,
          error:
            error.toNSError()
        )
      }
    }
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

    // profile
    let profile = (response[kUserProfile] as? [String: Any])!
    user.emailId = profile[kUserEmailId] as? String
    user.verificationTime = profile[kVerificationTime] as? String ?? ""
    user.firstName = profile[kUserFirstName] as? String
    user.lastName = profile[kUserLastName] as? String
    DBHandler.saveUserSettingsToDatabase()
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

  func handleUpdateTokenResponse() {
    self.sendRequestWith(
      method: self.method,
      params: self.requestParams ?? [:],
      headers: self.headerParams
    )
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
  
  func handelManageApps(response: [String: Any]) {
    UserManageApps.appDetails = UserManageApps()
    
    let appName = response["appName"] as? String ?? ""
    let code = response["code"] as? String ?? ""
    let contactUsEmail = response["contactUsEmail"] as? String ?? ""
    let supportEmail = response["supportEmail"] as? String ?? ""
    let fromEmail = response["fromEmail"] as? String ?? ""
    let appWebsite = response["appWebsite"] as? String ?? ""
    let privacyPolicyUrl = response["privacyPolicyUrl"] as? String ?? ""
    let termsUrl = response["termsUrl"] as? String ?? ""
    
    var latestVersion = ""
    var isForceUpdate = ""
    
    if let versionDict = response["version"] as? JSONDictionary, let iosDict = versionDict["ios"] as? JSONDictionary {
      latestVersion = iosDict["latestVersion"] as? String ?? ""
      isForceUpdate = iosDict["forceUpdate"] as? String ?? ""
    }
    
    UserManageApps.appDetails?.initWith(appName, contactUsEmail, supportEmail, fromEmail,
                                        appWebsite, privacyPolicyUrl, termsUrl, [latestVersion, isForceUpdate, code])
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

    case RegistrationMethods.updateUserProfile.description as String: break

    case RegistrationMethods.userPreferences.description as String:
      self.handleGetPreferenceResponse(response: (response as? [String: Any])!)

    case RegistrationMethods.updatePreferences.description as String: break  //did not handled response

    case RegistrationMethods.deactivate.description as String:
      self.handleDeActivateAccountResponse(response: (response as? [String: Any])!)
      
    case RegistrationMethods.apps.description as String:
      self.handelManageApps(response: (response as? [String: Any])!)

    default: break
    }

    delegate?.finishedRequest(manager, requestName: requestName, response: response)

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    if error.code == HTTPError.tokenExpired.rawValue {
      // Update Refresh Token
      updateToken(manager: manager, requestName: requestName, error: error)
    } else {
      var errorInfo = error.userInfo
      var localError = error
      if error.code == HTTPError.forbidden.rawValue {
        errorInfo = ["NSLocalizedDescription": LocalizableString.sessionExpired.localizedString]
        localError = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
      }
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
      delegate?.failedRequest(manager, requestName: requestName, error: localError)
    }
  }
}
