// License Agreement for FDA My Studies
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

enum RegistrationMethods: String {
  
  case register
  case confirmRegistration
  case userProfile
  case updateUserProfile
  case userPreferences
  case updatePreferences
  case withdraw
  case deleteAccount
  case resendConfirmation
  case deactivate
  case verifyEmailId
  case versionInfo

  var description: String {
    switch self {

    default:
      return self.apiPath
    }
  }

  var apiPath: String {
    switch self {
    case .register:
      return self.rawValue
    case .verifyEmailId:
      return self.rawValue
    default:
      return self.rawValue
    }
  }

  var method: Method {

    switch self {

    case .deleteAccount, .confirmRegistration, .userProfile,
      .userPreferences, .versionInfo:
      // GET Methods
      return Method(
        methodName: self.apiPath,
        methodType: .httpMethodGet,
        requestType: .requestTypeHTTP
      )
    case .withdraw, .deactivate:
      // DELETE Methods
      return Method(
        methodName: self.apiPath,
        methodType: .httpMethodDELETE,
        requestType: .requestTypeJSON
      )
    default:
      // POST Methods
      return Method(
        methodName: self.apiPath,
        methodType: .httpMethodPOST,
        requestType: .requestTypeJSON
      )

    }
  }

}
// MARK: - Set the server end points
enum RegistrationServerURLConstants {
  static let ProductionURL = API.registrationURL
  static let DevelopmentURL = API.registrationURL  // This will change based on config file.
}

class RegistrationServerConfiguration: NetworkConfiguration {
  static let configuration = RegistrationServerConfiguration()

  // MARK: Delegates
  override func getProductionURL() -> String {
    return RegistrationServerURLConstants.ProductionURL
  }

  override func getDevelopmentURL() -> String {
    return RegistrationServerURLConstants.DevelopmentURL
  }

  override func getDefaultHeaders() -> [String: String] {

    let clientId = RegistrationServerAPIKey.apiKey
    let seceretKey = RegistrationServerSecretKey.secretKey

    var header = [
      "appId": AppConfiguration.appID,
      "orgId": AppConfiguration.orgID,
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
