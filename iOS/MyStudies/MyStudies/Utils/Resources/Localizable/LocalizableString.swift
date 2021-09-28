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
  case offlineError = "Offline error"
  case checkInternet = "Check your internet connection and try again."

  case next = "Next"
  case done = "Done"

  // MARK: Resources
  case resourceNotAvailable = "This resource is currently unavailable."
  case aboutStudy = "About the study"
  case resourceTerms = "Terms"
  case resourcePrivacy = "Privacy policy"
  case leaveSubtitle = "This will also delete your app account."
  case newResourceMessage = "New resource available"

  // MARK: - Consent
  case learnMore = "Learn more"

  // MARK: - Errors
  case sessionExpired = "Your session is expired"

  // MARK: - Force Upgrade
  case blockerScreenLabelText = "Please update to the latest version of the app to continue."
  case appStoreUpdateText = "Please go to AppStore to update to the latest version of the app."

  // MARK: - Errors
  case badHappened = "Something bad happened"
  var localizedString: String { return NSLocalizedString(rawValue, comment: "") }
}
