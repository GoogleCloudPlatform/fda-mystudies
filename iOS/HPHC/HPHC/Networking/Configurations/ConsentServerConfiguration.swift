//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import UIKit

enum ConsentServerMethods: String {

  case updateEligibilityConsentStatus
  case consentDocument

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

    case .consentDocument:
      //DELETE Methods
      return Method(
        methodName: self.apiPath,
        methodType: .httpMethodGet,
        requestType: .requestTypeHTTP
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
enum ConsentServerURLConstants {
  static let ProductionURL = API.consentMgmtURL
  static let DevelopmentURL = API.consentMgmtURL
}

class ConsentServerConfiguration: NetworkConfiguration {

  static let configuration = ConsentServerConfiguration()

  // MARK: Delegates
  override func getProductionURL() -> String {
    return ConsentServerURLConstants.ProductionURL
  }

  override func getDevelopmentURL() -> String {
    return ConsentServerURLConstants.DevelopmentURL
  }

  override func getDefaultHeaders() -> [String: String] {

    let header = [
      "appId": AppConfiguration.appID,
      "orgId": AppConfiguration.orgID,
      kUserAuthToken: User.currentUser.authToken ?? "",
    ]
    return header
  }

  override func getDefaultRequestParameters() -> [String: Any] {
    return Dictionary()
  }

  override func shouldParseErrorMessage() -> Bool {
    return false
  }

}
