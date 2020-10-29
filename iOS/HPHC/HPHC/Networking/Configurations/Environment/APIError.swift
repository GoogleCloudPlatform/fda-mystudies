//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Alamofire
import Foundation

protocol ErrorPresentable: Error {
  var title: String? { get }
  var message: String? { get }
}

struct ApiError: ErrorPresentable {

  // MARK: - Properties
  var title: String?
  var message: String?
  var code: Int?

  // MARK: - Initializers
  init(title: String? = nil, message: String? = nil, code: HTTPError? = nil) {
    self.title = title
    self.message = message ?? code?.description
    self.code = code?.rawValue
  }

  init?(data: Data?) {
    if let errorDict = data?.toJSONDictionary() {
      message = errorDict["error_description"] as? String
      self.code = errorDict["status"] as? Int
      if Int(CFNetworkErrors.cfurlErrorNotConnectedToInternet.rawValue) == code {
        title = LocalizableString.offlineError.localizedString
        message = LocalizableString.checkInternet.localizedString
      }
    } else { return nil }
  }

  init(error: AFError) {
    self.code = error.responseCode
    message = error.localizedDescription
    if Int(CFNetworkErrors.cfurlErrorNotConnectedToInternet.rawValue) == code {
      title = LocalizableString.offlineError.localizedString
      message = LocalizableString.checkInternet.localizedString
    }
  }

  func toNSError() -> NSError {
    let errorInfo = ["NSLocalizedDescription": message ?? ""]
    return NSError(domain: title ?? "", code: code ?? 0, userInfo: errorInfo)
  }

  // MARK: - Utils
  static var defaultError: ApiError {
    return ApiError(
      title: LocalizableString.connectionError.localizedString,
      message: LocalizableString.connectionProblem.localizedString,
      code: nil
    )
  }

  static var sessionExpiredError: ApiError {
    return ApiError(
      message: LocalizableString.sessionExpired.localizedString,
      code: HTTPError.forbidden
    )
  }

  /// Data parsing issue.
  static var unwrapError: ApiError {
    return ApiError(title: "Something bad happened", message: "please try again later.", code: nil)
  }
}

enum HTTPError: Int {

  case notFound = 404
  case tokenExpired = 401
  case forbidden = 403
  case badRequest = 400

  var description: String? {
    switch self {
    case .notFound:
      return LocalizableString.resourceNotAvailable.localizedString
    default:
      return ""
    }
  }
}
