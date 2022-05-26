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
import IQKeyboardManagerSwift
import UIKit
import GoogleUtilities
import GoogleDataTransport
import FirebaseAnalytics

let kVerifyMessageFromSignUp =
  "An email has been sent to xyz@gmail.com. Please type in the verification code received in the email to complete account setup."

enum SignUpLoadFrom: Int {
  case gatewayOverview
  case login
  case menu
  /// From menu->Login->Signup
  case menuLogin

  /// From joinStudy->Login->Signup.
  case joinStudyLogin
}

class SignUpViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet var tableView: UITableView?

  @IBOutlet var tableViewFooterView: UIView?
  @IBOutlet var buttonSubmit: UIButton?
  @IBOutlet var buttonAgree: UIButton?
  @IBOutlet var labelTermsAndConditions: FRHyperLabel?
  @IBOutlet var termsAndCondition: LinkTextView?

  // MARK: - Properties
  var tableViewRowDetails: NSMutableArray?

  lazy var agreedToTerms: Bool = false
  lazy var confirmPassword = ""
  var user: User!
  lazy var viewLoadFrom: SignUpLoadFrom = .menu
  lazy var termsPageOpened = false

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  // MARK: - ViewController Lifecycle

  override func viewDidLoad() {
    super.viewDidLoad()
    
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "New User"
    ])

    self.navigationController?.navigationBar.backgroundColor = .white
    navigationController?.navigationBar.barTintColor = .white
    UINavigationBar.appearance().backgroundColor = .white
    // Used to set border color for bottom view
    buttonSubmit?.layer.borderColor = kUicolorForButtonBackground

    self.title = NSLocalizedString(kSignUpTitleText, comment: "")

    // load plist info
    let plistPath = Bundle.main.path(
      forResource: "SignUpPlist",
      ofType: ".plist",
      inDirectory: nil
    )
    tableViewRowDetails = NSMutableArray.init(contentsOfFile: plistPath!)

    // Used for background tap dismiss keyboard
    let tapGestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(
      target: self,
      action: #selector(SignUpViewController.dismissKeyboard)
    )
    self.tableView?.addGestureRecognizer(tapGestureRecognizer)

    // unhide navigationbar
    self.navigationController?.setNavigationBarHidden(false, animated: true)

    TermsAndPolicy.currentTermsAndPolicy = TermsAndPolicy()
    let policyURL = Branding.privacyPolicyURL
    let terms = Branding.termsAndConditionURL
    TermsAndPolicy.currentTermsAndPolicy?.initWith(terms: terms, policy: policyURL)
    self.agreeToTermsAndConditions()
    setNavigationBarColor()
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    self.navigationController?.navigationBar.backgroundColor = .white
    navigationController?.navigationBar.barTintColor = .white
    UINavigationBar.appearance().backgroundColor = .white

    if termsPageOpened {
      termsPageOpened = false
    } else {
      // unhide navigationbar
      self.navigationController?.setNavigationBarHidden(false, animated: true)

      let valPassword = self.user?.password ?? ""
      let valEmail = self.user?.emailId ?? ""
      User.resetCurrentUser()
      self.user = User.currentUser
      self.user.password = valPassword
      self.user.emailId = valEmail

      if viewLoadFrom == .menu {
        self.setNavigationBarItem()
      } else {
        self.addBackBarButton()
      }

      setNeedsStatusBarAppearanceUpdate()
    }
  }

  // MARK: - Utility Methods

  ///  Attributed string for Terms & Privacy Policy.
  func agreeToTermsAndConditions() {
    
    self.termsAndCondition?.delegate = self
    let attributedString = (termsAndCondition?.attributedText.mutableCopy() as? NSMutableAttributedString)!

    var foundRange = attributedString.mutableString.range(of: "terms")
    
    attributedString.addAttribute(
      NSAttributedString.Key.link,
      value: (TermsAndPolicy.currentTermsAndPolicy?.termsURL!)! as String,
      range: foundRange
    )

    foundRange = attributedString.mutableString.range(of: "privacy policy")
    attributedString.addAttribute(
      NSAttributedString.Key.link,
      value: (TermsAndPolicy.currentTermsAndPolicy?.policyURL!)! as String,
      range: foundRange
    )

    termsAndCondition?.attributedText = attributedString

    termsAndCondition?.linkTextAttributes = convertToOptionalNSAttributedStringKeyDictionary(
      [NSAttributedString.Key.foregroundColor.rawValue: Utilities.getUIColorFromHex(0x007CBA)]
    )

  }

  /// Dismiss key board when clicked on Background.
  @objc func dismissKeyboard() {
    self.view.endEditing(true)
  }

  /// All validation checks and Password,Email complexity checks.
  /// - Returns: A Boolean value indicating all the fields with valid data.
  func validateAllFields() -> Bool {
    //(user.firstName?.isEmpty)! && (user.lastName?.isEmpty)! &&
    if (self.user.emailId?.isEmpty)! && (self.user.password?.isEmpty)!
      && confirmPassword
        .isEmpty
    {
      self.showAlertMessages(textMessage: kMessageAllFieldsAreEmpty)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Sign-Up required fields alert"
      ])
      return false
    } else if self.user.emailId == "" {
      self.showAlertMessages(textMessage: kMessageEmailBlank)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Enter email alert"
      ])
      return false
    } else if !(Utilities.isValidEmail(testStr: self.user.emailId!)) {
      self.showAlertMessages(textMessage: kMessageValidEmail)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Enter validMail alert"
      ])
      return false
    } else if self.user.password == "" {
      self.showAlertMessages(textMessage: kMessagePasswordBlank)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Enter password alert"
      ])
      return false
    } else if Utilities.isPasswordValid(text: (self.user.password)!) == false {
      self.showAlertMessages(textMessage: kMessageValidatePasswordComplexity)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Password criteria alert"
      ])
      return false
    } else if (self.user.password)! == user.emailId {
      self.showAlertMessages(textMessage: kMessagePasswordMatchingToOtherFeilds)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Password+Email match alert"
      ])
      return false
    } else if confirmPassword == "" {
      self.showAlertMessages(textMessage: kMessageProfileConfirmPasswordBlank)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Confirm password alert"
      ])
      return false
    } else if self.user.password != confirmPassword {
      self.showAlertMessages(textMessage: kMessageValidatePasswords)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Password dont match alert"
      ])
      return false
    }
    return true
  }
  
  func validateEmailField() {
    if self.user.emailId == "" {
    } else if !(Utilities.isValidEmail(testStr: self.user.emailId!)) {
      self.showAlertMessages(textMessage: kMessageValidEmail)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Enter validMail Alert"
      ])
    }
  }
  
  func validatePasswordField() {
    if self.user.password == "" {
    } else if Utilities.isPasswordValid(text: (self.user.password)!) == false {
      self.showAlertMessages(textMessage: kMessageValidatePasswordComplexity)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Password criteria alert"
      ])
    } else if (self.user.password)! == user.emailId {
      self.showAlertMessages(textMessage: kMessagePasswordMatchingToOtherFeilds)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Password+Email match alert"
      ])
    }
  }
  
  func validateConfirmPasswordField() {
    if confirmPassword == "" {
    } else if self.user.password ?? "" != "" && self.user.password != confirmPassword {
      self.showAlertMessages(textMessage: kMessageValidatePasswords)
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Password dont match alert"
      ])
    }
  }

  /// Used to show the alert using Utility.
  func showAlertMessages(textMessage: String) {
    UIUtilities.showAlertMessage(
      "",
      errorMessage: NSLocalizedString(textMessage, comment: ""),
      errorAlertActionTitle: NSLocalizedString("OK", comment: ""),
      viewControllerUsed: self
    )
  }

  /// Method to navigate to Verification Controller.
  func navigateToVerificationController() {
    self.performSegue(withIdentifier: "verificationSegue", sender: nil)
  }

  // MARK: - Button Actions

  /// Used to check all the validations
  /// before making a Register webservice call.
  @IBAction func submitButtonAction(_ sender: Any) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Sign-Up Submit"
    ])

    self.view.endEditing(true)

    if self.validateAllFields() == true {
      if !(agreedToTerms) {
        self.showAlertMessages(textMessage: kMessageAgreeToTermsAndConditions)
        Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
          buttonClickReasonsKey: "Review terms/conditions alert"
        ])
      } else {
        // Call the Webservice
        UserServices().registerUser(self as NMWebServiceDelegate)
      }
    }
  }

  /// Agree to terms and conditions button clicked.
  @IBAction func agreeButtonAction(_ sender: Any) {
    if (sender as? UIButton)!.isSelected {
      (sender as? UIButton)!.isSelected = !(sender as? UIButton)!.isSelected
      agreedToTerms = false
    } else {
      agreedToTerms = true
      (sender as? UIButton)!.isSelected = !(sender as? UIButton)!.isSelected
    }
    
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Sign-Up Terms&Policy"
    ])
  }

  // MARK: - Segue Method

  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    if let verificationController = segue.destination as? VerificationViewController {
      switch viewLoadFrom {
      case .menu:
        verificationController.shouldCreateMenu = false
        verificationController.viewLoadFrom = .signup
      case .menuLogin:
        verificationController.shouldCreateMenu = false
        verificationController.viewLoadFrom = .login
      case .joinStudyLogin:
        verificationController.shouldCreateMenu = false
        verificationController.viewLoadFrom = .joinStudy
      case .login:
        verificationController.shouldCreateMenu = true
        verificationController.viewLoadFrom = .login
      case .gatewayOverview:
        verificationController.shouldCreateMenu = true
        verificationController.viewLoadFrom = .signup
      }
      let message = kVerifyMessageFromSignUp
      let modifiedMessage = message.replacingOccurrences(
        of: kDefaultEmail,
        with: User.currentUser.emailId!
      )
      verificationController.labelMessage = modifiedMessage

    }
  }
}

// MARK: - Gesture Delegate
extension SignUpViewController: UIGestureRecognizerDelegate {
  func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    if gestureRecognizer.isKind(of: UITapGestureRecognizer.classForCoder()) {
      if gestureRecognizer.numberOfTouches == 2 {
        return false
      }
    }
    return true
  }
}

// MARK: - UITextView Delegate
class LinkTextView: UITextView {

  override func canPerformAction(_ action: Selector, withSender sender: Any?) -> Bool {
    return false
  }

  override func addGestureRecognizer(_ gestureRecognizer: UIGestureRecognizer) {
    if gestureRecognizer.isKind(of: UITapGestureRecognizer.classForCoder()) {
      gestureRecognizer.isEnabled = false
    }
    super.addGestureRecognizer(gestureRecognizer)
  }
}

// MARK: - Textfield Delegate
extension SignUpViewController: UITextViewDelegate {

  func textView(_ textView: UITextView, shouldInteractWith URL: URL, in characterRange: NSRange)
    -> Bool
  {

    var link: String = TermsAndPolicy.currentTermsAndPolicy?.termsURL ?? ""
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Sign-Up Terms"
    ])
    var title: String = kNavigationTitleTerms
    if URL.absoluteString == TermsAndPolicy.currentTermsAndPolicy?.policyURL
      && characterRange
        .length == String("privacy policy").count
    {
      link = TermsAndPolicy.currentTermsAndPolicy?.policyURL ?? ""
      title = kNavigationTitlePrivacyPolicy
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
        buttonClickReasonsKey: "Sign-Up PrivacyPolicy"
      ])
    }
    
    guard !link.isEmpty else { return false }
    let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
    let webViewController =
      (loginStoryboard.instantiateViewController(withIdentifier: "WebViewController")
      as? UINavigationController)!
    let webview = (webViewController.viewControllers[0] as? WebViewController)!
    webview.requestLink = link
    webview.title = title
    self.navigationController?.present(webViewController, animated: true, completion: nil)
    termsPageOpened = true

    return false
  }

  func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldReceive press: UIPress)
    -> Bool
  {
    return false
  }

  func textViewDidChangeSelection(_ textView: UITextView) {
    if !NSEqualRanges(textView.selectedRange, NSRange(location: 0, length: 0)) {
      textView.selectedRange = NSRange(location: 0, length: 0)
    }
  }
}

// MARK: - TableView Datasource
extension SignUpViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return tableViewRowDetails!.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    let tableViewData = (tableViewRowDetails?.object(at: indexPath.row) as? NSDictionary)!
    let cell =
      (tableView.dequeueReusableCell(
        withIdentifier: kSignUpTableViewCellIdentifier,
        for: indexPath
      )
      as? SignUpTableViewCell)!

    cell.textFieldValue?.text = ""
    var isSecuredEntry: Bool = false

    cell.textFieldValue?.tag = indexPath.row

    var keyBoardType: UIKeyboardType? = UIKeyboardType.default
    let textFieldTag = TextFieldTags(rawValue: indexPath.row)!

    // Cell TextField data setup
    switch textFieldTag {
    case .password:
      isSecuredEntry = true
      cell.textFieldValue?.text = self.user.password
    case .confirmPassword:
      isSecuredEntry = true
      cell.textFieldValue?.text = confirmPassword
    case .emailId:
      keyBoardType = .emailAddress
      isSecuredEntry = false
      cell.textFieldValue?.text = self.user.emailId
    }
    // Cell Data Setup
    cell.populateCellData(
      data: tableViewData,
      securedText: isSecuredEntry,
      keyboardType: keyBoardType
    )

    cell.backgroundColor = UIColor.clear
    return cell
  }
}

// MARK: - TableView Delegates
extension SignUpViewController: UITableViewDelegate {

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    tableView.deselectRow(at: indexPath, animated: true)
  }
}

// MARK: - Textfield Delegate
extension SignUpViewController: UITextFieldDelegate {

  func textFieldDidBeginEditing(_ textField: UITextField) {
    if textField.tag == TextFieldTags.emailId.rawValue {
      textField.keyboardType = .emailAddress
      textField.isSecureTextEntry = false
    }
  }

  func textField(
    _ textField: UITextField,
    shouldChangeCharactersIn range: NSRange,
    replacementString string: String
  ) -> Bool {

    let tag: TextFieldTags = TextFieldTags(rawValue: textField.tag)!
    let finalString = textField.text! + string

    if string == " " {
      return false
    }

    if tag == .emailId {
      if string == " " || finalString.count > 255 {
        return false
      } else {
        return true
      }
    } else if tag == .password || tag == .confirmPassword {
      textField.isSecureTextEntry = true
      if finalString.count > 64 {
        return false
      } else {
        if range.location == textField.text?.count && string == " " {

          textField.text = textField.text?.appending("\u{00a0}")
          return false
        }
        return true
      }
    } else {
      return true
    }
  }

  func textFieldDidEndEditing(_ textField: UITextField) {

    textField.text = textField.text?.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)

    let tag: TextFieldTags = TextFieldTags(rawValue: textField.tag)!

    switch tag {
    case .emailId:
      self.user.emailId = textField.text!
      textField.isSecureTextEntry = false
      validateEmailField()

    case .password:
      if let password = textField.text {
        if !password.isEmpty,
          !Utilities.isPasswordValid(text: password)
        {
          self.showAlertMessages(textMessage: kMessageValidatePasswordComplexity)
          Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
            buttonClickReasonsKey: "Password criteria alert"
          ])
        }
        self.user.password = password
        validatePasswordField()
      }
      textField.isSecureTextEntry = true
    case .confirmPassword:
      confirmPassword = textField.text!
      textField.isSecureTextEntry = true
      validateConfirmPasswordField()
    }
  }
}

// MARK: - Webservice delegates
extension SignUpViewController: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {

    self.addProgressIndicator()
    if requestName.isEqual(to: RegistrationMethods.register.rawValue) {

    }
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    self.removeProgressIndicator()
    if requestName.isEqual(to: RegistrationMethods.register.description) {

      let delegate = (UIApplication.shared.delegate as? AppDelegate)!
      delegate.calculateTimeZoneChange()
      ORKPasscodeViewController.removePasscodeFromKeychain()
      self.navigateToVerificationController()
    } else {
      self.agreeToTermsAndConditions()
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    self.removeProgressIndicator()
    UIUtilities.showAlertWithTitleAndMessage(
      title: NSLocalizedString(kErrorTitle, comment: "") as NSString,
      message: error.localizedDescription as NSString
    )
  }
}

// Helper function inserted by Swift 4.2 migrator.
private func convertToOptionalNSAttributedStringKeyDictionary(_ input: [String: Any]?)
  -> [NSAttributedString.Key: Any]?
{
  guard let input = input else { return nil }
  return Dictionary(
    uniqueKeysWithValues: input.map { key, value in
      (NSAttributedString.Key(rawValue: key), value)
    }
  )
}
