//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Alamofire
import Foundation

// Completion Handler
typealias handler = (_ data: Data?, _ status: Bool, _ error: ApiError?) -> Void

struct FailedRouter {
  let router: URLRequestConvertible
  let handler: handler
}

class APIService {

  static let instance = APIService()
  private init() {}

  private var failedServices: [FailedRouter] = []
  var isTokenRefreshing = false

  func requestForData(with router: URLRequestConvertible, completion: @escaping handler) {

    func addFailedService() {
      self.failedServices.append(FailedRouter(router: router, handler: completion))
    }

    AF.request(router).validate().responseData { (response) in
      switch response.result {
      case .success(let data):
        if let httpResponse = response.response,
          httpResponse.statusCode == 200 || 200..<300 ~= httpResponse.statusCode
        {
          completion(data, true, nil)

        } else {
          completion(nil, false, .defaultError)
        }
      case .failure(let error):
        if let error = ApiError(data: response.data) {
          if let code = error.code,
            code == HTTPError.tokenExpired.rawValue
          {
            if self.isTokenRefreshing {
              completion(nil, false, ApiError.sessionExpiredError)
            } else {
              // Refresh the token
              self.refreshToken()
            }
          } else {
            completion(nil, false, error)
          }
        } else {
          completion(nil, false, ApiError(error: error))
        }
      }
    }
  }

  /// This method will try to refresh the token and update the API's
  private func refreshToken() {

    guard !isTokenRefreshing else { return }

    HydraAPI.refreshToken { [unowned self] (status, error) in
      self.isTokenRefreshing = false
      let failedRouters = self.failedServices
      self.failedServices.removeAll()
      if status {
        for service in failedRouters {
          // Complete the failed requests.
          self.requestForData(with: service.router, completion: service.handler)
        }
      } else {
        for service in failedRouters {
          service.handler(nil, false, error)
        }
      }
    }
  }

}
