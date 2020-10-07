//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

extension URL {
  subscript(queryParam: String) -> String? {
    guard let url = URLComponents(string: self.absoluteString) else { return nil }
    return url.queryItems?.first(where: { $0.name == queryParam })?.value
  }
}
