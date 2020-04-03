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

import Foundation

protocol NetworkConfigurationProtocol {
  func getDevelopmentURL() -> String
  func getProductionURL() -> String
  func getDefaultHeaders() -> [String: String]
  func getDefaultRequestParameters() -> [String: Any]
}
class Method {

  let methodName: String
  let methodType: HTTPMethod
  let requestType: RequestType

  /// Initializer method which initializes all params
  /// - Parameter methodName: name of the method of type String
  /// - Parameter methodType: instance of HttpMethod
  /// - Parameter requestType: instance of RequestType
  init(methodName: String, methodType: HTTPMethod, requestType: RequestType) {
    self.methodName = methodName
    self.methodType = methodType
    self.requestType = requestType
  }
}
class NetworkProtocols: NetworkConfigurationProtocol {

  /// Returns default request parameters of type dictionary
  internal func getDefaultRequestParameters() -> [String: Any] {
    return Dictionary()
  }

  /// Returns default headers of type dictionary
  internal func getDefaultHeaders() -> [String: String] {
    return Dictionary()
  }

  /// Returns production Url of type string
  internal func getProductionURL() -> String {
    return ""
  }

  /// Rreturna development Url of type string
  internal func getDevelopmentURL() -> String {
    return ""
  }

  /// Returna bool value for parsing error message
  internal func shouldParseErrorMessage() -> Bool {
    return false
  }

  /// Parse error response and return an instance of NSError
  /// - Parameter errorResponse: error response of type dictionary
  internal func parseError(errorResponse: [String: Any]) -> NSError {

    var errorCode = 0
    if let errResponse = errorResponse["error"] as? [String: Any] {
      if let errCD = errResponse["code"] as? Int {
        errorCode = errCD
        let errorDesc = errResponse["message"] as? String ?? "Something went wrong"

        let error = NSError(
          domain: NSURLErrorDomain,
          code: errorCode,
          userInfo: [NSLocalizedDescriptionKey: errorDesc]
        )
        return error
      }
    }

    if let code = errorResponse["status"] as? Int {
      let message = errorResponse["message"] as? String ?? ""
      return NSError(domain: message, code: code, userInfo: errorResponse)
    }

    let error = NSError(
      domain: NSURLErrorDomain,
      code: 101,
      userInfo: [NSLocalizedDescriptionKey: "Your error localized description"]
    )
    return error

  }
}
class NetworkConfiguration: NetworkProtocols {
}
