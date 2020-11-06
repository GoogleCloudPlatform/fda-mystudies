//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import UIKit

enum AuthServerMethods: String {

  case login
  case forgotPassword
  case logout
  case changePassword
  case getRefreshedToken

  var description: String {
    switch self {
    default:
      return self.apiPath
    }
  }

  var apiPath: String {
    switch self {
    default:
      return self.rawValue
    }
  }

  var method: Method {

    switch self {

    case .logout:
      //DELETE Methods
      return Method(
        methodName: self.apiPath,
        methodType: .httpMethodDELETE,
        requestType: .requestTypeJSON
      )
    default:
      //POST Methods
      return Method(
        methodName: self.apiPath,
        methodType: .httpMethodPOST,
        requestType: .requestTypeJSON
      )

    }
  }

}
// MARK: - Set the server end points
enum AuthServerURLConstants {
  static let ProductionURL = API.authURL
  static let DevelopmentURL = API.authURL
}

class AuthServerConfiguration: NetworkConfiguration {
  static let configuration = AuthServerConfiguration()

  // MARK: Delegates
  override func getProductionURL() -> String {
    return AuthServerURLConstants.ProductionURL
  }

  override func getDevelopmentURL() -> String {
    return AuthServerURLConstants.DevelopmentURL
  }

  override func getDefaultHeaders() -> [String: String] {

    let clientId = RegistrationServerAPIKey.apiKey
    let secretKey = RegistrationServerSecretKey.secretKey

    var header = [
      "appId": AppConfiguration.appID,
      "orgId": AppConfiguration.orgID,
    ]

    if User.currentUser.authToken != nil {
      header[kUserAuthToken] = User.currentUser.authToken
    } else {
      header["clientId"] = clientId
      header["secretKey"] = secretKey
    }
    return header
  }

  override func getDefaultRequestParameters() -> [String: Any] {
    return Dictionary()
  }

  override func shouldParseErrorMessage() -> Bool {
    return false
  }

}
