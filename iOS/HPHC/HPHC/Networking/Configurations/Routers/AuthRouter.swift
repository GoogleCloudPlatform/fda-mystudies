//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Alamofire
import Foundation

enum RequestError: Error {
  case invalidBaseURL
}

enum AuthRouter: URLRequestConvertible {

  private struct Request {

    let method: Alamofire.HTTPMethod
    let path: String
    let encoding: ParameterEncoding?
    let parameters: JSONDictionary?
    let headers: [String: String]

    init(
      method: Alamofire.HTTPMethod,
      path: String,
      encoding: ParameterEncoding? = nil,
      parameters: JSONDictionary? = nil,
      headers: [String: String] = [:]
    ) {
      self.method = method
      self.path = path
      self.encoding = encoding
      self.parameters = parameters
      self.headers = headers
    }

  }

  case auth(params: JSONDictionary)
  case codeGrant(params: JSONDictionary, headers: [String: String])

  static let baseHostPath = API.hydraURL
  static let redirectURL = baseHostPath + oauthPath + "/callback"
  static let oauthPath = "/oauth-scim-service"
  static let oauthVersion = "/oauth2"

  var baseURLPath: String {
    switch self {
    case .auth:
      return "\(AuthRouter.baseHostPath)\(AuthRouter.oauthVersion)"
    case .codeGrant:
      return "\(AuthRouter.baseHostPath)\(AuthRouter.oauthPath)\(AuthRouter.oauthVersion)"
    }
  }

  private var request: Request {
    switch self {
    case .auth(let parameters):
      return Request(
        method: .get,
        path: "/auth",
        encoding: URLEncoding.default,
        parameters: parameters
      )

    case .codeGrant(let params, let headers):
      return Request(
        method: .post,
        path: "/token",
        encoding: URLEncoding.httpBody,
        parameters: params,
        headers: headers
      )
    }
  }

  func asURLRequest() throws -> URLRequest {

    guard let url = URL(string: baseURLPath) else { throw RequestError.invalidBaseURL }

    var mutableUrlRequest = try URLRequest(
      url: url.appendingPathComponent(request.path),
      method: request.method
    )

    mutableUrlRequest.timeoutInterval = TimeInterval(30)

    for i in request.headers {
      mutableUrlRequest.setValue(i.value, forHTTPHeaderField: i.key)
    }

    if let encoding = request.encoding {
      let finalRequest = try encoding.encode(
        mutableUrlRequest,
        with: request.parameters
      )
      return finalRequest
    } else {
      return mutableUrlRequest as URLRequest
    }
  }

}
