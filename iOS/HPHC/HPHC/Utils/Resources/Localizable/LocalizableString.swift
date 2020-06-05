//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

enum LocalizableString: String {

  // MARK: - Global
  case connectionError = "Connection error"
  case connectionProblem = "There was a problem, please try again."
  case ok = "Ok"

  // MARK: Resources
  case resourceNotAvailable = "This resource is currently unavailable."

  var localizedString: String { return NSLocalizedString(rawValue, comment: "") }
}
