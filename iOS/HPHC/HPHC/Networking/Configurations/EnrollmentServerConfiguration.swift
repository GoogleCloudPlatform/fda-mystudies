//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

enum EnrollmentMethods: String {

  case enroll
  case validateEnrollmentToken
  case updateStudyState
  case studyState
  case withdrawfromstudy

  var description: String {
    switch self {
    default:
      return self.rawValue
    }
  }

  var method: Method {

    switch self {
    case .studyState:
      // GET Methods
      return Method(
        methodName: self.rawValue,
        methodType: .httpMethodGet,
        requestType: .requestTypeHTTP
      )
    case .validateEnrollmentToken, .enroll, .updateStudyState, .withdrawfromstudy:
      return Method(
        methodName: self.rawValue,
        methodType: .httpMethodPOST,
        requestType: .requestTypeJSON
      )
    }
  }

}

// MARK: - Set the server end points.
enum EnrollmentServerURLConstants {
  static let productionURL = API.enrollmentURL
  static let developmentURL = API.enrollmentURL  // This will change based on config file.
}

class EnrollmentServerConfiguration: NetworkConfiguration {
  static let configuration = EnrollmentServerConfiguration()

  // MARK: Delegates
  override func getProductionURL() -> String {
    return EnrollmentServerURLConstants.productionURL
  }

  override func getDevelopmentURL() -> String {
    return EnrollmentServerURLConstants.developmentURL
  }

  override func getDefaultHeaders() -> [String: String] {

    let header = [
      "appId": AppConfiguration.appID,
      kAuthorization: User.currentUser.authToken ?? "",
    ]
    return header
  }

  override func getDefaultRequestParameters() -> [String: Any] {
    return Dictionary()
  }

  override func shouldParseErrorMessage() -> Bool {
    return true
  }

  override func parseError(errorResponse: [String: Any]) -> NSError {

    var error = NSError(
      domain: NSURLErrorDomain,
      code: 101,
      userInfo: [NSLocalizedDescriptionKey: "Could not connect to server"]
    )

    if let errorMessage = errorResponse["exception"] {

      error = NSError(
        domain: NSURLErrorDomain,
        code: 101,
        userInfo: [NSLocalizedDescriptionKey: errorMessage]
      )
    }

    if let code = errorResponse["status"] as? Int {
      let message = errorResponse["message"] as? String ?? ""
      error = NSError(domain: message, code: code, userInfo: errorResponse)
    }

    return error
  }
}
