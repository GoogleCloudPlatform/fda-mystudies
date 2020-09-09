//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Alamofire
import Foundation

// Completion Handler
typealias RequestHandler = (_ data: Data?, _ status: Bool, _ error: ApiError?) -> Void
typealias StatusHandler = (_ status: Bool, _ error: ApiError?) -> Void

struct HydraAPI {

  // MARK: - API

  /// Generates request for Hydra web login.
  /// - Parameter tempRegID: Temporary registration ID.
  /// - Returns: Instance of `URLRequest`
  static func loginRequest(tempRegID: String = "") -> URLRequest? {

    let parameters: JSONDictionary = [
      "client_id": "oauth-scim-client-id",
      "scope": "offline_access",
      "response_type": "code",
      "appId": AppConfiguration.appID,
      "appVersion": Utilities.getAppVersion(),
      "mobilePlatform": Utilities.currentDevicePlatform(),
      "tempRegId": tempRegID,
      "code_challenge_method": "S256",
      "code_challenge": SessionService.instance.codeChallenge,
      "correlationId": SessionService.instance.correlationID,
      "redirect_uri": AuthRouter.redirectURL + AuthRouter.oauthPath + "/login",
      "state": String.randomString(length: 21),
    ]
    return try?
      AuthRouter
      .auth(params: parameters)
      .asURLRequest()
  }

  static func logout(user: User, completion: @escaping StatusHandler) {}

  static func grant(
    user: User,
    with code: String,
    completion: @escaping StatusHandler
  ) {

    let headers: [String: String] = [
      "Content-Type": "application/x-www-form-urlencoded",
      "correlationId": SessionService.instance.correlationID,
      "appId": AppConfiguration.appID,
      "mobilePlatform": Utilities.currentDevicePlatform(),
    ]
    let verifier = SessionService.instance.codeVerifier
    print("Code:", verifier)
    let params: JSONDictionary = [
      "grant_type": "authorization_code",
      "scope": "openid offline",
      "redirect_uri": AuthRouter.redirectURL + AuthRouter.oauthPath + "/login",
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
}
