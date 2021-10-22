// License Agreement for FDA MyStudies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
// Copyright 2020 Google LLC
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
// documentation files (the &quot;Software&quot;), to deal in the Software without restriction, including without
// limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
// Software, and to permit persons to whom the Software is furnished to do so, subject to the following
// conditions:
// The above copyright notice and this permission notice shall be included in all copies or substantial
// portions of the Software.
// Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
// Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
// THE SOFTWARE IS PROVIDED &quot;AS IS&quot;, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
// PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
// OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
// OTHER DEALINGS IN THE SOFTWARE.

import Foundation

// MARK: - Notifications

let kRefreshActivities = Notification.Name(rawValue: "refreshActivities")

// MARK: - Alert Constants

let kTermsAndConditionLink = "http://www.google.com"
let kPrivacyPolicyLink = "http://www.facebook.com"
let kNavigationTitleTerms = "Terms"
let kNavigationTitlePrivacyPolicy = "Privacy policy"

//Used for corner radius Color for sign in , sign up , forgot password etc screens
let kUicolorForButtonBackground = UIColor.init(
  red: 0 / 255.0,
  green: 124 / 255.0,
  blue: 186 / 255.0,
  alpha: 1.0
).cgColor

let kUicolorForCancelBackground = UIColor.init(
  red: 140 / 255.0,
  green: 149 / 255.0,
  blue: 163 / 255.0,
  alpha: 1.0
).cgColor

let kUIColorForSubmitButtonBackground = UIColor.init(
  red: 0 / 255.0,
  green: 124 / 255.0,
  blue: 186 / 255.0,
  alpha: 1.0
)

let kNoNetworkErrorCode = -101
let kCouldNotConnectToServerCode = -1001

// MARK: - Display Constants
let kTitleError = "Error"
let kTitleMessage = "Message"
let kImportantNoteMessage = "Important note"
let kTitleOk = "Ok"
let kTitleCancel = "Cancel"
let kTitleDeleteAccount = "Delete app account"

let kDeleteAccountConfirmationMessage =
  "Are you sure you want to delete your app account?"
let kMessageAccountDeletedSuccess = "Your account has been deleted."
let kMessageAppNotificationOffStayup = "Stay up-to-date! "
let kMessageAppNotificationOffRemainder =
  "Turn ON notifications and reminders in app and phone settings to get notified about study activities in a timely manner."

let kSetPasscodeDescription =
  """
  A passcode helps with quick and secure access to the app. \
  You can turn off the passcode at any time by using a setting provided in the app.
  """

// MARK: - Signin Constants
let kSignInTitleText = "Sign in"
let kSignInTableViewCellIdentifier = "DetailsCell"

// MARK: - ForgotPassword Constants
let kForgotPasswordTitleText = "Password help"
let kForgotPasswordResponseMessage =
  "We have sent a temporary password to your registered email. Please sign in with the temporary password and then change your password."
let kSuccessfulVerification = "Verification successful"
let kResetAfterVerificationMessage = "Your account has been verified, please try resetting your password again."
// MARK: - Logout
let kOnLogoutMessage = "You have been signed out of the app."

// MARK: - Forceupgrade
let kMandatoryForceUMessage = "Please upgrade the app to continue."
let kOpionalForceUMessage = "A new version of this app is available. Do you want to update it now?"
let kFromSplashScreen = "FromSplashScreen"
let kFromBackground = "FromBackground"
let kIsShowUpdateAppVersion = "isShowAppVersionUpdate"
let kIsStudylistGeneral = "isStudylistGeneral"

// MARK: - SignUp Constants
let kSignUpTitleText = "Sign up"
let kAgreeToTermsAndConditionsText = "I agree to the Terms and Privacy Policy"
let kTermsText = "Terms"
let kSignUpTableViewCellIdentifier = "CommonDetailsCell"

// MARK: - NOTIFICATIONS Constants
let kNotificationsTitleText = "Notifications"
let kNotificationTableViewCellIdentifier = "NotificationCell"

// MARK: - Validations Message during signup and sign in process

let kMessageFirstNameBlank = "Please enter your first name."
let kMessageLastNameBlank = "Please enter your last name."
let kMessageEmailBlank = "Please enter your email address."
let kMessagePasswordBlank = "Please enter your password."

let kMessageCurrentPasswordBlank = "Please enter your current password."

let kMessageProfileConfirmPasswordBlank = "Please confirm your password."
let kMessageConfirmPasswordBlank = "Please confirm the password."

let kMessagePasswordMatchingToOtherFeilds = "Your password should not be the same as your email"

let kMessageValidEmail = "Please enter a valid email."

let kMessageValidatePasswords = "Passwords do not match."
let kMessageProfileValidatePasswords = "Passwords do not match."

let kMessageValidatePasswordCharacters = "Password should have minimum of 8 characters."
let kMessageValidatePasswordComplexity =
  """
  Your password must be at least 8 characters long and contain lower case \
  and upper case letters, and numeric and special characters.
  """
let kMessageAgreeToTermsAndConditions = "You must review and accept the terms and conditions to continue"
let kMessageNewPasswordBlank = "Please enter your new password."
let kMessageValidateChangePassword = "New password and old password are same."

// MARK: - ChangePassword Constants
let kChangePasswordTitleText = "CHANGE PASSWORD"
let kCreatePasswordTitleText = "SET UP PASSWORD"
let kChangePawwordCellIdentifer = "changePasswordCell"
let kChangePasswordResponseMessage = "Your password has been changed successfully"

let kMessageAllFieldsAreEmpty = "Please fill in all the required fields"
let kMessageValidFirstName =
  "Please enter valid first name. Please use letters(length:1 - 100 characters)."
let kMessageValidLastName =
  "Please enter valid last name. Please use letters(length:1 - 100 characters)."

let kMessageValidateOldAndNewPasswords = "Old and new passwords should not be the same"

// MARK: - VerificationController
let kMessageVerificationCodeEmpty = "Please enter valid verification code"

// MARK: - FeedbackviewController constants
let kFeedbackTableViewCellIdentifier1 = "FeedbackCellFirst"
let kFeedbackTableViewCellIdentifier2 = "FeedbackCellSecond"
let kMessageFeedbackSubmittedSuccessfuly =
  "Thank you for providing feedback. Your gesture is appreciated."

// MARK: - ContactUsviewController constants
let kContactUsTableViewCellIdentifier = "ContactUsCell"
let kMessageSubjectBlankCheck = "Please enter the subject"
let kMessageMessageBlankCheck = "Please enter the message"
let kMessageContactedSuccessfuly =
  "Thank you for contacting us. We will get back to you as soon as possible."
let kMessageTextViewPlaceHolder = ""

// MARK: - ActivitiesViewController constants
let kBackgroundTableViewColor = UIColor.init(
  red: 216 / 255.0,
  green: 227 / 255.0,
  blue: 230 / 255.0,
  alpha: 1
)
let kActivitiesTableViewCell = "ActivitiesCell"
let kActivitiesTableViewScheduledCell = "ActivitiesCellScheduled"

let kYellowColor = UIColor.init(red: 245 / 255.0, green: 175 / 255.0, blue: 55 / 255.0, alpha: 1.0)
let kBlueColor = UIColor.init(red: 0 / 255.0, green: 124 / 255.0, blue: 186 / 255.0, alpha: 1.0)
let kGreenColor = UIColor.init(red: 76 / 255.0, green: 175 / 255.0, blue: 80 / 255.0, alpha: 1.0)

let kResumeSpaces = "  Resume  "
let kStartSpaces = "  Start  "
let kCompletedSpaces = "  Completed  "
let kInCompletedSpaces = "  Incompleted  "
let kStudySetupMessage = "Please wait as we set up the study for you, this may take a few seconds."

// MARK: - ResourcesViewController constants
let kResourcesTableViewCell = "ResourcesCell"
let kResourceShareError = "Unable to share Resource."
let kRetainDataOnLeaveStudy =
  """
  You are choosing to leave the study. Please choose if your response data can be retained and used \
  for research purposes OR if your response data should be deleted.
  """
let kResourceLeaveGatewayStudy = "Are you sure you want to leave the study?"
let kResourceLeaveStandaloneStudy = "Are you sure you want to leave the study? This will also delete your app account."

// MARK: - StudyDashboardViewController constants
let kWelcomeTableViewCell = "welcomeCell"
let kStudyActivityTableViewCell = "studyActivityCell"
let kPercentageTableViewCell = "percentageCell"
let kActivityTableViewCell = "ActivityCell"
let kStatisticsTableViewCell = "StatisticsCell"
let kTrendTableViewCell = "trendCell"

let kActivityCollectionViewCell = "ActivityCell"
let kStatisticsCollectionViewCell = "StatisticsCell"

let kDarkBlueColor = UIColor.init(red: 0 / 255.0, green: 124 / 255.0, blue: 186 / 255.0, alpha: 1.0)
let kGreyColor = UIColor.init(red: 140 / 255.0, green: 149 / 255.0, blue: 163 / 255.0, alpha: 1.0)

let kDaySpaces = "  DAY  "
let kDay = "DAY"
let kMonthSpaces = "  MONTH  "
let kMonth = "MONTH"
let kWeekSpaces = "  WEEK  "
let kWeek = "WEEK"
let kDashSetupMessage = "Please wait as we set up the dashboard for you, this may take a few seconds."

// MARK: - Eligibility constants

let kMessageForInvalidToken = "Please enter valid enrollment token"

let kMessageValidToken = "Please enter valid token"

let kMessageInvalidTokenOrIfStudyDoesNotExist =
  "Sorry, this token is invalid. Please enter a valid token to continue."
let kMessageconsentConfirmation =
  "By tapping on Agree, you confirm that you have reviewed the consent document and agree to participate in the study."

// MARK: - StudyHomeMessages
let kMessageForStudyUpcomingState =
  "This study is an upcoming one and isn't yet open for enrolling participants. Please check back later."
let kMessageForStudyPausedState = "This study has been temporarily paused. Please check back later."
let kMessageForStudyPausedAfterJoiningState =
  "The study has been temporarily paused. You can participate in activities once it is resumed. Please check back later."
let kMessageForStudyClosedState = "This study has been closed."
let kMessageForStudyWithdrawnState =
  """
  Sorry, this study currently does not allow previously enrolled participants to rejoin \
  after they have withdrawn. Please check back later or explore other studies.
  """
let kMessageForStudyEnrollingNotAllowed =
  "Sorry, enrollment for this study has been closed for now. Please check back later or explore other studies you could join."

// MARK: - StudyDashboardViewController segues
let unwindToStudyListDashboard = "unwindToStudyListDashboardIdentifier"

// MARK: - FilterListViewController Segue
let filterListSegue = "filterscreenSegue"

// MARK: - Staging User Details

let kStagingUserEmailId = "aqibm@boston-technology.com"
let kIsStagingUser = "StagingUser"

// MARK: - AppDelegate Contants.

let kBlockerScreenLabelText = "Please update to the latest version of app to continue."
let kConsentUpdatedTitle = "Consent updated"

let kMessageConsentUpdatedPartTwo =
  " Please review the revised consent terms and provide your informed consent, to continue participating in the study."
let kConsentShareError = "Unable to share the Consent."
let kMessageConsentUpdated =
  "The consent document for this study has been updated."
  + kMessageConsentUpdatedPartTwo

let kReviewTitle = "Review"
let kPasscodeStepIdentifier = "PasscodeStep"
let kPasscodeTaskIdentifier = "PassCodeTask"
let kMessagePasscode = "Passcode"
let kMessagePasscodeSignOut =
  "You will be signed out and will need to sign in again. Are you sure you want to proceed ?"
let kNewProgressViewNIB = "NewProgressView"
let kforgotPasscodeTitle = "Forgot passcode? Sign in again"
let kStudyStoryboard = "Study"
let kPasscodeSetUpText = "Set up a passcode for the app"
let kIphoneSimulator = "iPhone Simulator"

let kBundleIdentier = "CFBundleIdentifier"
let kPDFCreationNotificationId = "pdfCreationNotificationIdentifier"
let ksetUpTimeIdentifier = "setUPTime"
let kCFBundleShortVersion = "CFBundleShortVersionString"

let kResultCount = "resultCount"
let kResultsForAppStore = "results"
let kAppStoreVersion = "version"

let kContinueButtonTitle = NSLocalizedString("Continue", comment: "")
let kType = "type"

let kCurrentVersion = "currentVersion"
let kForceUpdate = "forceUpdate"
let kMessage = "message"
let kVisualStepId = "visual"
let kConfirmation = "Confirmation"

public func log<T>(
  _ object: T?,
  filename: String = #file,
  line: Int = #line,
  funcname: String = #function
) {
  #if DEBUG
    guard let object = object else { return }
    print(
      "***** \(Date()) \(filename.components(separatedBy: "/").last ?? "") (line: \(line)) :: \(funcname) :: \(object)"
    )
  #endif
}

let activityBuilder: ActivityBuilder? = ActivityBuilder.currentActivityBuilder
let consentbuilder: ConsentBuilder? = ConsentBuilder()
