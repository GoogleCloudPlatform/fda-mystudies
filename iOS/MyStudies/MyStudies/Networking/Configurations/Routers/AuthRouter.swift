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

typealias StringDictionary = [String: String]

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

  case changePassword(params: JSONDictionary, userID: String)
  case logout(userID: String)
  case forgotPassword(params: JSONDictionary, headers: [String: String])
  case auth(params: JSONDictionary)
  case codeGrant(params: JSONDictionary, headers: [String: String])

  static let hydraServerURL = API.hydraURL
  static let authServerURL = API.authURL
  static let redirectURL = authServerURL + "/callback"
  static let oauthVersion = "/oauth2"

  var baseURLPath: String {
    switch self {
    case .auth:
      return AuthRouter.hydraServerURL + AuthRouter.oauthVersion
    case .codeGrant:
      return "\(AuthRouter.authServerURL)\(AuthRouter.oauthVersion)"
    default:
      return AuthRouter.authServerURL
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

    case .forgotPassword(let parameters, let headers):
      return Request(
        method: .post,
        path: "/user/reset_password",
        encoding: JSONEncoding.default,
        parameters: parameters,
        headers: headers
      )

    case .logout(let userID):
      return Request(
        method: .post,
        path: "/users/\(userID)/logout",
        encoding: JSONEncoding.default
      )

    case .changePassword(let parameters, let userID):
      return Request(
        method: .put,
        path: "/users/\(userID)/change_password",
        encoding: JSONEncoding.default,
        parameters: parameters
      )
    }
  }

  private var defaultHeaders: StringDictionary {
    switch self {
    case .auth:
      return [:]
    default:
      return SessionService.Audit.headers
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

    for header in defaultHeaders {
      mutableUrlRequest.setValue(header.value, forHTTPHeaderField: header.key)
    }

    if let accessToken = User.currentUser.authToken {
      mutableUrlRequest.setValue(accessToken, forHTTPHeaderField: "Authorization")
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
