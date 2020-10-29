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

import IQKeyboardManagerSwift
import UIKit

enum CPTextFeildTags: Int {
  case oldPassword = 100
  case newPassword
  case confirmPassword
}

enum ChangePasswordLoadFrom: Int {
  case login
  case menuLogin
  case profile
  case joinStudy
}

class ChangePasswordViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet var tableView: UITableView?

  @IBOutlet var buttonSubmit: UIButton?

  var tableViewRowDetails: NSMutableArray?
  lazy var newPassword = ""
  lazy var oldPassword = ""
  lazy var confirmPassword = ""
  lazy var temporaryPassword: String = ""
  lazy var viewLoadFrom: ChangePasswordLoadFrom = .profile

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  // MARK: - ViewController Lifecycle

  override func viewDidLoad() {
    super.viewDidLoad()

    // Used to set border color for bottom view
    buttonSubmit?.layer.borderColor = kUicolorForButtonBackground

    // load plist info
    let plistPath = Bundle.main.path(
      forResource: "ChangePasswordData",
      ofType: ".plist",
      inDirectory: nil
    )
    tableViewRowDetails = NSMutableArray.init(contentsOfFile: plistPath!)

    // Used for background tap dismiss keyboard
    let tapGesture: UITapGestureRecognizer = UITapGestureRecognizer.init(
      target: self,
      action: #selector(ChangePasswordViewController.dismissKeyboard)
    )
    self.tableView?.addGestureRecognizer(tapGesture)

    if temporaryPassword.count > 0 {
      oldPassword = temporaryPassword
      tableViewRowDetails?.removeObject(at: 0)
      self.title = NSLocalizedString(kCreatePasswordTitleText, comment: "")
    } else {
      self.title = NSLocalizedString(kChangePasswordTitleText, comment: "")
    }
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    self.navigationController?.setNavigationBarHidden(false, animated: true)
    addBackButton()
  }

  override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    // hide navigationbar
    if viewLoadFrom == .login {
      self.navigationController?.setNavigationBarHidden(true, animated: true)
    }
  }

  // MARK: - UI

  fileprivate func addBackButton() {
    let backBarButton = addBackBarButton()
    backBarButton.removeTarget(self, action: nil, for: .allEvents)
    backBarButton.addTarget(self, action: #selector(goBack), for: .touchUpInside)
  }

  // MARK: - Utility Methods
  func showAlertMessages(textMessage: String) {
    UIUtilities.showAlertMessage(
      "",
      errorMessage: NSLocalizedString(textMessage, comment: ""),
      errorAlertActionTitle: NSLocalizedString("OK", comment: ""),
      viewControllerUsed: self
    )
  }

  @objc private func goBack() {
    if self.viewLoadFrom == .login {
      self.navigationController?.popToRootViewController(animated: true)
    } else {
      self.navigationController?.popViewController(animated: true)
    }
  }
  /// Dismiss key board when clicked on Background.
  @objc func dismissKeyboard() {
    self.view.endEditing(true)
  }

  /// Api Call to Change Password.
  func requestToChangePassword() {
    self.addProgressIndicator()
    UserAPI.changePassword(
      oldPassword: self.oldPassword,
      newPassword: self.newPassword
    ) { (status, error) in
      self.removeProgressIndicator()
      if status {
        self.changePasswordSuccess()
      } else if let error = error {
        self.changePasswordfailed(with: error)
      }
    }
  }

  /// Seting menu View using `FDASlideMenuViewController` and Gateway Storyboard.
  func createMenuView() {

    let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)

    let fdaVC =
      (storyboard.instantiateViewController(withIdentifier: kStoryboardIdentifierSlideMenuVC)
      as? FDASlideMenuViewController)!

    self.navigationController?.pushViewController(fdaVC, animated: true)
  }

  // MARK: - Button Actions

  /// Validations after clicking on submit button
  /// If all the validations satisfy send user feedback request.
  @IBAction func submitButtonAction(_ sender: Any) {

    if self.oldPassword.isEmpty && self.newPassword.isEmpty && self.confirmPassword.isEmpty {
      self.showAlertMessages(textMessage: kMessageAllFieldsAreEmpty)
    } else if self.oldPassword == "" {
      self.showAlertMessages(textMessage: kMessageCurrentPasswordBlank)

    } else if self.newPassword == "" {
      self.showAlertMessages(textMessage: kMessageNewPasswordBlank)
    } else if self.confirmPassword == "" {
      self.showAlertMessages(textMessage: kMessageProfileConfirmPasswordBlank)
    } else if Utilities.isPasswordValid(text: self.newPassword) == false {
      self.showAlertMessages(textMessage: kMessageValidatePasswordComplexity)

    } else if self.newPassword == User.currentUser.emailId {
      self.showAlertMessages(textMessage: kMessagePasswordMatchingToOtherFeilds)

    } else if self.newPassword != self.confirmPassword {
      self.showAlertMessages(textMessage: kMessageProfileValidatePasswords)
    } else {
      self.requestToChangePassword()
    }
  }
}

// MARK: - TableView Data source
extension ChangePasswordViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {

    return (self.tableViewRowDetails?.count)!
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    let tableViewData = (tableViewRowDetails?.object(at: indexPath.row) as? NSDictionary)!

    let cell =
      (tableView.dequeueReusableCell(
        withIdentifier: kSignInTableViewCellIdentifier,
        for: indexPath
      )
      as? SignInTableViewCell)!

    cell.populateCellData(data: tableViewData, securedText: true)
    var tagIncremental = 100
    if temporaryPassword.count > 0 {

      tagIncremental = 101
    } else {
      cell.textFieldValue?.isEnabled = true
    }
    cell.textFieldValue?.tag = indexPath.row + tagIncremental

    return cell
  }
}

// MARK: - TableView Delegates
extension ChangePasswordViewController: UITableViewDelegate {

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    tableView.deselectRow(at: indexPath, animated: true)

  }
}

// MARK: - Textfield Delegate
extension ChangePasswordViewController: UITextFieldDelegate {

  func textField(
    _ textField: UITextField,
    shouldChangeCharactersIn range: NSRange,
    replacementString string: String
  ) -> Bool {

    let tag: CPTextFeildTags = CPTextFeildTags(rawValue: textField.tag)!
    let finalString = textField.text! + string

    if tag == .newPassword || tag == .confirmPassword {
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

      if range.location == textField.text?.count && string == " " {

        textField.text = textField.text?.appending("\u{00a0}")
        return false
      }
      return true
    }
  }

  func textFieldDidEndEditing(_ textField: UITextField) {
    textField.text = textField.text?.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)

    let tag = CPTextFeildTags(rawValue: textField.tag)!

    switch tag {
    case .oldPassword:
      self.oldPassword = textField.text!
    case .newPassword:
      self.newPassword = textField.text!
    case .confirmPassword:
      self.confirmPassword = textField.text!
    }
  }
}

extension ChangePasswordViewController {

  func changePasswordSuccess() {

    if viewLoadFrom == .profile {

      UIUtilities.showAlertMessageWithActionHandler(
        NSLocalizedString(kTitleMessage, comment: ""),
        message: NSLocalizedString(kChangePasswordResponseMessage, comment: ""),
        buttonTitle: NSLocalizedString(kTitleOk, comment: ""),
        viewControllerUsed: self
      ) {
        _ = self.navigationController?.popViewController(animated: true)
      }
    } else if viewLoadFrom == .menuLogin {
      User.currentUser.saveAuthenticatedUserToDB()
      let leftController = slideMenuController()?.leftViewController as? LeftMenuViewController
      leftController?.createLeftmenuItems()
      leftController?.changeViewController(.studyList)
    } else if viewLoadFrom == .login {
      User.currentUser.saveAuthenticatedUserToDB()
      //  Create menu
      self.createMenuView()
    } else if viewLoadFrom == .joinStudy {
      User.currentUser.saveAuthenticatedUserToDB()
      let leftController = slideMenuController()?.leftViewController as? LeftMenuViewController
      leftController?.createLeftmenuItems()
      self.performSegue(withIdentifier: "unwindStudyHomeSegue", sender: self)
    }
  }

  func changePasswordfailed(with error: ApiError) {

    if error.code == HTTPError.forbidden.rawValue {  // Unauthorized
      self.presentDefaultAlertWithError(
        error: error,
        animated: true,
        action: {
          self.fdaSlideMenuController()?.navigateToHomeAfterUnauthorizedAccess()
        },
        completion: nil
      )
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
