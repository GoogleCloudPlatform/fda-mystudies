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
    It appears you’ve missed a few study activities in the period you were away.
    This is a gentle reminder to visit the app regularly to complete pending study activities, if any.
    """

  // MARK: - Consent
  case learnMore = "Learn more"

  // MARK: - Force Upgrade
  case blockerScreenLabelText = "Please update to the latest version of the app to continue."
  case appStoreUpdateText = "Please go to AppStore to update to the latest version of the app."

  var localizedString: String { return NSLocalizedString(rawValue, comment: "") }
}
