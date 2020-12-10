//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

extension Data {
  func toJSONDictionary() -> [String: AnyObject]? {

    guard
      let json = try? JSONSerialization.jsonObject(with: self as Data, options: [.allowFragments])
    else {
      return nil
    }
    guard let jsonDic = json as? [String: AnyObject] else {
      return nil
    }
    return jsonDic
  }
}
