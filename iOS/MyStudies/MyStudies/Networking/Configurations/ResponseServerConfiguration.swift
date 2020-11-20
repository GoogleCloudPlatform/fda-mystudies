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

enum ResponseMethods: String {

  case processResponse = "process-response"
  case getParticipantResponse = "getresponse"
  case updateActivityState = "update-activity-state"
  case activityState = "get-activity-state"

  var description: String {
    switch self {
    default:
      return self.path
    }
  }

  var base: String {
    switch self {
    default:
      return "participant/"
    }
  }

  var path: String {
    switch self {
    default:
      return base + self.rawValue
    }
  }

  var method: Method {

    switch self {
    case .activityState, .getParticipantResponse:
      // GET Methods
      return Method(
        methodName: self.path,
        methodType: .httpMethodGet,
        requestType: .requestTypeHTTP
      )
    default:
      return Method(
        methodName: path,
        methodType: .httpMethodPOST,
        requestType: .requestTypeJSON
      )
    }
  }

}

// MARK: - Set the server end points.
enum ResponseServerURLConstants {
  static let ProductionURL = API.responseURL
  static let DevelopmentURL = API.responseURL  // This will change based on config file.
}

class ResponseServerConfiguration: NetworkConfiguration {
  static let configuration = ResponseServerConfiguration()

  // MARK: Delegates
  override func getProductionURL() -> String {
    return ResponseServerURLConstants.ProductionURL
  }

  override func getDevelopmentURL() -> String {
    return ResponseServerURLConstants.DevelopmentURL
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

    if let code = errorResponse[RegistrationServerConfiguration.JSONKey.status] as? Int {
      let message =
        errorResponse[RegistrationServerConfiguration.JSONKey.error] as? String
        ?? errorResponse[RegistrationServerConfiguration.JSONKey.errorDesc] as? String ?? ""
      return NSError(
        domain: NSURLErrorDomain,
        code: code,
        userInfo: [NSLocalizedDescriptionKey: message]
      )
    }
    return error
  }
}
