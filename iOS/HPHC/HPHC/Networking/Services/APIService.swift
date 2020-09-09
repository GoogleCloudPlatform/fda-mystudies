//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Alamofire
import Foundation

// Completion Handler
typealias handler = (_ data: Data?, _ status: Bool, _ error: ApiError?) -> Void

class APIService {

  static let instance = APIService()
  private init() {}

  func requestForData(with router: URLRequestConvertible, completion: @escaping handler) {

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
      case .failure(_):
        completion(nil, false, ApiError.defaultError) // TODO: Parse errors.
      }
    }
  }

}
