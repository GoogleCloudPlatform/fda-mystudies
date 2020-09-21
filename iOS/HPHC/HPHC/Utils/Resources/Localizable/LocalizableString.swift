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
  case next = "Next"
  case done = "Done"

  // MARK: - Resources
  case resourceNotAvailable = "This resource is currently unavailable."
  case aboutStudy = "About the Study"
  case leaveSubtitle = "This will also delete your app account."

  // MARK: - Activities
  case missedActivity =
    """
    Thanks for your continued participation in this study.
    Your consistency in contributing your experiences helps make this study useful.
    """

  // MARK: - Consent
  case learnMore = "Learn more"

  // MARK: - Errors
  case sessionExpired = "Your Session is Expired"

  // MARK: - Force Upgrade
  case blockerScreenLabelText = "Please update the app to the latest version to continue."
  case appStoreUpdateText = "Please go to App Store to update the app to the latest version."

  var localizedString: String { return NSLocalizedString(rawValue, comment: "") }
}
