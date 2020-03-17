//
//  AuthServerConfiguration.swift
//  HPHC
//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import UIKit

enum ConsentServerMethods: String {
  //TODO : Write exact name for request method
  case updateEligibilityConsentStatus

  case consentPDF

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

    case .consentPDF:
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

  //Staging server
  static let ProductionURL = "https://hpreg-stage.lkcompliant.net/fdahpUserRegWS/"

  static let DevelopmentURL = "http://192.168.0.44:3247/myStudiesConsentMgmtWS/"

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

    var infoDict: NSDictionary?
    if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
      infoDict = NSDictionary(contentsOfFile: path)
    }
    let appId = infoDict!["ApplicationID"] as! String
    let orgId = infoDict!["OrganizationID"] as! String
    let clientId = RegistrationServerAPIKey.apiKey
    let seceretKey = RegistrationServerSecretKey.secretKey

    var header = [
      "appId": appId,
      "orgId": orgId,
    ]
    if User.currentUser.authToken != nil {
      header[kUserAuthToken] = User.currentUser.authToken
      header["clientToken"] = User.currentUser.clientToken
    } else {
      header["clientId"] = clientId
      header["secretKey"] = seceretKey
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
