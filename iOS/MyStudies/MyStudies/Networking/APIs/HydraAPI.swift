//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Alamofire
import Foundation

// Completion Handler
typealias StatusHandler = (_ status: Bool, _ error: ApiError?) -> Void

struct HydraAPI {

  // MARK: - API

  /// Generates request for Hydra web login.
  /// - Parameter tempRegID: Temporary registration ID.
  /// - Returns: Instance of `URLRequest`
  static func loginRequest() -> URLRequest? {

    let parameters: JSONDictionary = [
      "source": "MOBILE APPS",
      "client_id": API.authClientID,
      "scope": "offline_access",
      "response_type": "code",
      "appId": AppConfiguration.appID,
      "appVersion": Utilities.getAppVersion(),
      "mobilePlatform": Utilities.currentDevicePlatform(),
      "tempRegId": User.currentUser.tempRegID ?? "",
      "code_challenge_method": "S256",
      "code_challenge": SessionService.instance.codeChallenge,
      "correlationId": SessionService.correlationID,
      "redirect_uri": AuthRouter.redirectURL,
      "state": String.randomString(length: 21),
      "appName": Utilities.appName(),
      "app": "//\(Bundle.main.bundleIdentifier ?? "")/mystudies",
      "fromEmail": UserManageApps.appDetails?.fromEmail ?? "",
      "contactEmail": UserManageApps.appDetails?.contactUsEmail ?? "",
      "supportEmail": UserManageApps.appDetails?.supportEmail ?? "",
    ]
    return
      try? AuthRouter
      .auth(params: parameters)
      .asURLRequest()
  }

  static func grant(
    user: User,
    with code: String,
    completion: @escaping StatusHandler
  ) {

    let headers: [String: String] = [
      "Content-Type": "application/x-www-form-urlencoded"
    ]

    let params: JSONDictionary = [
      "grant_type": "authorization_code",
      "scope": "openid offline",
      "redirect_uri": AuthRouter.redirectURL,
      "code_verifier": SessionService.instance.codeVerifier,
      "userId": user.userId ?? "",
      "code": code,
    ]

    let router = AuthRouter.codeGrant(params: params, headers: headers)
    APIService.instance.requestForData(with: router) { (data, status, error) in
      if status {
        if let authDict = data?.toJSONDictionary() {
          User.currentUser.authenticate(with: authDict)
          completion(true, nil)
        } else {
          completion(false, .unwrapError)
        }
      } else {
        completion(false, error)
      }
    }
  }

  static func refreshToken(completion: @escaping StatusHandler) {
    APIService.instance.isTokenRefreshing = true
    let params: StringDictionary = [
      "grant_type": "refresh_token",
      "redirect_uri": AuthRouter.redirectURL,
      "client_id": API.authClientID,
      "refresh_token": User.currentUser.refreshToken,
      "userId": User.currentUser.userId,
    ]

    let headers: [String: String] = [
      "Content-Type": "application/x-www-form-urlencoded"
    ]

    let router = AuthRouter.codeGrant(params: params, headers: headers)
    APIService.instance.requestForData(with: router) { (data, status, error) in
      if status {
        if let authDict = data?.toJSONDictionary() {
          User.currentUser.tokenUpdate(with: authDict)
          completion(true, nil)
        } else {
          completion(false, .unwrapError)
        }
      } else {
        if error?.code == HTTPError.internalServerError.rawValue {
          completion(false, ApiError.sessionExpiredError)
        } else {
          completion(false, error)
        }
      }
    }
  }
}
