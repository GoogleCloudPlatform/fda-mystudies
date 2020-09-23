//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

class AuthServices: NSObject {

  let networkManager = NetworkManager.sharedInstance()
  weak var delegate: NMWebServiceDelegate?
  var requestParams: [String: Any]? = [:]
  var headerParams: [String: String]? = [:]
  var method: Method!
  var failedRequestServices = FailedUserServices()

  // MARK: Requests

  /// Creates a request to login an `User`
  /// - Parameter delegate: Class object to receive response
  func loginUser(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate
    let user = User.currentUser
    let params = [
      kUserEmailId: user.emailId ?? "",
      kUserPassword: user.password ?? "",
    ]
    let method = AuthServerMethods.login.method
    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to logout an `User`
  /// - Parameter delegate: Class object to receive response
  func logoutUser(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate
    let user = User.currentUser
    let headerParams = [kUserId: user.userId ?? ""]
    let params = [kUserLogoutReason: user.logoutReason.rawValue]
    let method = AuthServerMethods.logout.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to reset the `User` password
  /// - Parameters:
  ///   - email: Email Id of the`User `
  ///   - delegate: Class object to receive response
  func forgotPassword(email: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate
    let params = [kUserEmailId: email]
    let method = AuthServerMethods.forgotPassword.method
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
    let headerParams = [kUserId: user.userId ?? ""]
    let params = [
      kUserOldPassword: oldPassword,
      kUserNewPassword: newPassword,
    ]
    let method = AuthServerMethods.changePassword.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  // MARK: Parsers

  /// Handles login response
  /// - Parameter response: Webservice response
  func handleUserLoginResponse(response: [String: Any]) {

    let user = User.currentUser
    user.userId = response[kUserId] as? String ?? ""
    user.verified = response[kUserVerified] as? Bool ?? false
    user.authToken = response[kUserAuthToken] as? String ?? ""
    user.refreshToken = response[kRefreshToken] as? String ?? ""

    if let isTempPassword = response[kUserIsTempPassword] as? Bool {
      user.isLoggedInWithTempPassword = isTempPassword
    }

    if user.verified && !user.isLoggedInWithTempPassword {

      // Set user type & save current user to DB
      user.userType = UserType.loggedInUser
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

  /// Handles change password response
  /// - Parameter response: Webservice response
  func handleChangePasswordResponse(response: [String: Any]) {

    let user = User.currentUser
    if user.verified {
      user.userType = UserType.loggedInUser
      DBHandler().saveCurrentUser(user: user)
      let ud = UserDefaults.standard
      ud.set(user.userId!, forKey: kUserId)
      ud.synchronize()
    }
  }

  func handleLogoutResponse() {

    let appDomain = Bundle.main.bundleIdentifier!
    UserDefaults.standard.removePersistentDomain(forName: appDomain)
    UserDefaults.standard.synchronize()

    // Delete from database
    DBHandler.deleteCurrentUser()

    // Reset user object
    User.resetCurrentUser()

    // Delete complete database
    DBHandler.deleteAll()

    // Cancel all local notification
    LocalNotification.cancelAllLocalNotification()

    LocalNotification.removeAllDeliveredNotifications()

    // Reset Filters
    StudyFilterHandler.instance.previousAppliedFilters = []
    StudyFilterHandler.instance.searchText = ""

    // Delete keychain values
    FDAKeychain.shared[kUserAuthTokenKeychainKey] = nil
    FDAKeychain.shared[kUserRefreshTokenKeychainKey] = nil
  }

  /// Creates a request to update RefreshToken
  func updateToken(delegate: NMWebServiceDelegate?) {

    let user = User.currentUser
    self.delegate = delegate

    let clientId = RegistrationServerAPIKey.apiKey
    let seceretKey = RegistrationServerSecretKey.secretKey

    let param = [kRefreshToken: user.refreshToken!]
    let header = [
      "clientId": clientId,
      "secretKey": seceretKey,
      "userId": user.userId!,
    ]
    let method = AuthServerMethods.getRefreshedToken.method
    self.sendRequestWith(
      method: method,
      params: param,
      headers: header
    )
  }

  func handleUpdateTokenResponse(response: [String: Any]) {

    let user = User.currentUser
    user.refreshToken = response["refreshToken"] as? String ?? ""
    user.authToken = response[kUserAuthToken] as? String ?? ""
    FDAKeychain.shared[kUserAuthTokenKeychainKey] = user.authToken
    FDAKeychain.shared[kUserRefreshTokenKeychainKey] = user.refreshToken
    DBHandler().saveCurrentUser(user: user)

    // Re-send request which failed due to session expired.
    guard let method = self.failedRequestServices.method else { return }
    let headerParams =
      self.failedRequestServices.headerParams == nil
      ? [:] : self.failedRequestServices.headerParams
    self.sendRequestWith(
      method: method,
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
      AuthServerConfiguration.configuration,
      method: method,
      params: params as NSDictionary?,
      headers: headers as NSDictionary?,
      delegate: self
    )
  }

}
extension AuthServices: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    delegate?.startedRequest(manager, requestName: requestName)
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    let response = response as? JSONDictionary ?? [:]
    switch requestName {
    case AuthServerMethods.login.description as String:
      self.handleUserLoginResponse(response: response)

    case AuthServerMethods.forgotPassword.description as String: break

    case AuthServerMethods.changePassword.description as String:
      self.handleChangePasswordResponse(response: response)

    case AuthServerMethods.logout.description as String:
      self.handleLogoutResponse()

    case AuthServerMethods.getRefreshedToken.description as String:
      self.handleUpdateTokenResponse(response: response)

    default: break
    }

    delegate?.finishedRequest(manager, requestName: requestName, response: response as AnyObject)
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    if requestName as String == AuthServerMethods.getRefreshedToken.description && error.code == 401 {  // Unauthorized
      delegate?.failedRequest(manager, requestName: requestName, error: error)
    } else if error.code == 401 {
      // Save failed service
      self.failedRequestServices.headerParams = self.headerParams
      self.failedRequestServices.requestParams = self.requestParams
      self.failedRequestServices.method = self.method

      if (User.currentUser.refreshToken.isEmpty
        && requestName as String
          != AuthServerMethods
          .login
          .description)
        || requestName as String
          == AuthServerMethods
          .logout
          .description
      {
        // Unauthorized Access
        let errorInfo = ["NSLocalizedDescription": "Your Session is Expired"]
        let localError = NSError(domain: error.domain, code: 403, userInfo: errorInfo)
        delegate?.failedRequest(manager, requestName: requestName, error: localError)

      } else if requestName as String
        == AuthServerMethods
        .changePassword
        .description
      {
        // Update Refresh Token
        AuthServices().updateToken(delegate: self.delegate)
      } else {
        // Return server error
        delegate?.failedRequest(manager, requestName: requestName, error: error)
      }
    } else {

      var errorInfo = error.userInfo
      var localError = error
      if error.code == 403 {
        errorInfo = ["NSLocalizedDescription": "Your Session is Expired"]
        localError = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
      }
      delegate?.failedRequest(manager, requestName: requestName, error: localError)
    }
  }
}
