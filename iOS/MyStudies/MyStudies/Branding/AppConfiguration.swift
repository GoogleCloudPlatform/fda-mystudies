//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

enum AppConfiguration {

  private enum JSONKey {
    static let appID = "ApplicationID"
  }

  /// App ID from Study builder.
  static var appID: String {
    return (try? Configuration.value(for: JSONKey.appID)) ?? ""
  }

}
