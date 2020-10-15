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
import UIKit

let kVerifyViewControllerSegue = "VerifyViewControllerSegue"
let kVerficationMessageFromForgotPassword =
  """
  Your registered email(xyz@gmail.com) is pending verification. Enter the Verification Code received \
  on this email to complete verification and try the Forgot Password action again.
  """

class ForgotPasswordViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet var buttonSubmit: UIButton?

  @IBOutlet var textFieldEmail: UITextField?

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  // MARK: - ViewController Delegates

  override func viewDidLoad() {
    super.viewDidLoad()

    // Used to set border color for bottom view
    buttonSubmit?.layer.borderColor = kUicolorForButtonBackground
    self.title = NSLocalizedString(kForgotPasswordTitleText, comment: "")

    // Used for background tap dismiss keyboard
    let gestureRecognizwe: UITapGestureRecognizer = UITapGestureRecognizer.init(
      target: self,
      action: #selector(ForgotPasswordViewController.dismissKeyboard)
    )
    self.view?.addGestureRecognizer(gestureRecognizwe)

    self.addBackBarButton()
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)

    // unhide navigationbar
    self.navigationController?.setNavigationBarHidden(false, animated: true)

    setNeedsStatusBarAppearanceUpdate()

  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    textFieldEmail?.becomeFirstResponder()
  }

  // MARK: - Utility Methods

  /// Dismiss key board when clicked on Background.
  @objc func dismissKeyboard() {
    self.view.endEditing(true)
  }

  /// Navigate the screen to VerifyViewController.
  func navigateToVerifyViewController() {
    self.performSegue(withIdentifier: kVerifyViewControllerSegue, sender: self)
  }

  func showAlertMessages(textMessage: String) {
    UIUtilities.showAlertMessage(
      "",
      errorMessage: NSLocalizedString(textMessage, comment: ""),
      errorAlertActionTitle: NSLocalizedString("OK", comment: ""),
      viewControllerUsed: self
    )
  }

  // MARK: - Button Action

  /// To check all the validations
  /// before making a logout webservice call.
  @IBAction func submitButtonAction(_ sender: Any) {
    self.dismissKeyboard()
    if textFieldEmail?.text == "" {
      self.showAlertMessages(textMessage: kMessageEmailBlank)

    } else if !(Utilities.isValidEmail(testStr: (textFieldEmail?.text)!)) {
      self.showAlertMessages(textMessage: kMessageValidEmail)
    } else if let email = textFieldEmail?.text {
      requestPassword(with: email)
    }
  }

  // MARK: - Segue Methods
  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {

    if let verifyController = segue.destination as? VerificationViewController {

      let message = kVerficationMessageFromForgotPassword
      let modifiedMessage = message.replacingOccurrences(
        of: kDefaultEmail,
        with: (textFieldEmail?.text)!
      )

      verifyController.labelMessage = modifiedMessage
      verifyController.viewLoadFrom = .forgotPassword
      verifyController.emailId = textFieldEmail?.text
    }
  }

  // MARK: - APIs

  /// Requests for temporary password.
  /// - Parameter email: Registered email ID to recieve the password.
  fileprivate func requestPassword(with email: String) {
    self.addProgressIndicator()
    UserAPI.forgotPassword(email: email) { (status, error) in
      self.removeProgressIndicator()
      if status {
        UIUtilities.showAlertMessageWithActionHandler(
          NSLocalizedString(kTitleMessage, comment: ""),
          message: NSLocalizedString(kForgotPasswordResponseMessage, comment: ""),
          buttonTitle: NSLocalizedString(kTitleOk, comment: ""),
          viewControllerUsed: self
        ) {
          _ = self.navigationController?.popViewController(animated: true)
        }
      } else if let error = error {
        if error.code == HTTPError.forbidden.rawValue {
          // User not verified
          self.navigateToVerifyViewController()
        } else {
          self.presentDefaultAlertWithError(
            error: error,
            animated: true,
            action: nil,
            completion: nil
          )
        }
      }
    }
  }

}

// MARK: - Webservices Delegates
extension ForgotPasswordViewController: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    self.addProgressIndicator()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    self.removeProgressIndicator()

    if requestName as String == AuthServerMethods.forgotPassword.description {
      UIUtilities.showAlertMessageWithActionHandler(
        NSLocalizedString(kTitleMessage, comment: ""),
        message: NSLocalizedString(kForgotPasswordResponseMessage, comment: ""),
        buttonTitle: NSLocalizedString(kTitleOk, comment: ""),
        viewControllerUsed: self
      ) {
        _ = self.navigationController?.popViewController(animated: true)
      }
    } else {
      // for resend email
      UIUtilities.showAlertWithTitleAndMessage(
        title: NSLocalizedString(kAlertMessageText, comment: "") as NSString,
        message: NSLocalizedString(kAlertMessageResendEmail, comment: "") as NSString
      )

    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    self.removeProgressIndicator()

    if requestName as String == AuthServerMethods.forgotPassword.description
      && error.code == HTTPError.forbidden.rawValue
    {
      self.navigateToVerifyViewController()
    } else {
      // if resend email fails
      UIUtilities.showAlertWithTitleAndMessage(
        title: NSLocalizedString(kTitleError, comment: "") as NSString,
        message: error.localizedDescription as NSString
      )
    }
  }
}

// MARK: - TextField Delegates
extension ForgotPasswordViewController: UITextFieldDelegate {

  func textField(
    _ textField: UITextField,
    shouldChangeCharactersIn range: NSRange,
    replacementString string: String
  ) -> Bool {
    let finalString = textField.text! + string
    if string == " " || finalString.count > 255 {
      return false
    } else {
      return true
    }
  }
}
