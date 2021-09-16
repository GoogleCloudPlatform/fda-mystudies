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
import LocalAuthentication
import SlideMenuControllerSwift
import UIKit

let kProfileTableViewCellIdentifier = "ProfileTableViewCell"

let kLeadTimeSelectText = "Select lead time"
let kActionSheetDoneButtonTitle = "Done"
let kActionSheetCancelButtonTitle = "Cancel"

let kChangePasswordSegueIdentifier = "changePasswordSegue"
let kErrorTitle = ""
let kProfileAlertTitleText = "Profile"
let kProfileAlertUpdatedText = "Your profile has been updated."

let signupCellLastIndex = 2

let kProfileTitleText = "My account"

let kSignOutText = "Sign out"
let kLabelName = "LabelName"

let kUseTouchIdOrPasscode = "Use passcode or Touch ID to access app"
let kUseFaceIdOrPasscode = "Use passcode or Face ID to access app"

let kUsePasscodeToAccessApp = "Use passcode to access the app?"

let ktouchid = "touchIdEnabled"
let korkPasscode = "ORKPasscode"

// Cell Toggle Switch Types
enum ToggelSwitchTags: Int {
  case usePasscode = 3
  case useTouchId = 6
  case receivePush = 4
  case receiveStudyActivityReminders = 5
}

class ProfileViewController: UIViewController, SlideMenuControllerDelegate {

  // MARK: - Outlets
  @IBOutlet var tableViewProfile: UITableView?

  @IBOutlet var tableViewFooterViewProfile: UIView?
  @IBOutlet var buttonLeadTime: UIButton?
  @IBOutlet var editBarButtonItem: UIBarButtonItem?
  @IBOutlet var tableTopConstraint: NSLayoutConstraint?

  // MARK: - Properties
  var tableViewRowDetails: NSMutableArray?

  var datePickerView: UIDatePicker?
  var isCellEditable: Bool?
  lazy var user = User.currentUser
  lazy var isPasscodeViewPresented: Bool = false
  lazy var passcodeStateIsEditing: Bool = false
  lazy var isProfileEdited = false

  /// A Boolean indicates the user changing the App password.
  private var isChangePasswordEditing = false

  /// A Boolean indicates wheather user sign out initiated.
  private var isSigningOut = false

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  // MARK: - ViewController Lifecycle

  override func viewDidLoad() {
    super.viewDidLoad()

    // Load plist info
    let plistPath = Bundle.main.path(forResource: "Profile", ofType: ".plist", inDirectory: nil)

    tableViewRowDetails = NSMutableArray.init(contentsOfFile: plistPath!)

    // Resigning First Responder on outside tap
    let gestureRecognizer: UITapGestureRecognizer = UITapGestureRecognizer.init(
      target: self,
      action: #selector(ProfileViewController.dismissKeyboard)
    )
    self.tableViewProfile?.addGestureRecognizer(gestureRecognizer)

    // Initial data setup
    self.setInitialDate()

    self.fdaSlideMenuController()?.delegate = self
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    user = User.currentUser

    if !isPasscodeViewPresented {
      UserServices().getUserProfile(self as NMWebServiceDelegate)
    }
    self.setNavigationBarItem()
    self.tableViewProfile?.reloadData()
  }

  func leftDidClose() {
    // Left menu is closed
  }

  // MARK: - Button Actions

  /// Change password button clicked.
  @IBAction func buttonActionChangePassCode(_ sender: UIButton) {

    let passcodeViewController = ORKPasscodeViewController.passcodeEditingViewController(
      withText: kSetPasscodeDescription,
      delegate: self,
      passcodeType: .type4Digit
    )
    passcodeStateIsEditing = true
    isChangePasswordEditing = true
    self.navigationController?.present(passcodeViewController, animated: false, completion: {})
  }

  ///  Edit Profile button clicked
  @IBAction func editBarButtonAction(_ sender: UIBarButtonItem) {

    if self.isCellEditable! == false {
      self.isCellEditable = true

      self.buttonLeadTime?.isUserInteractionEnabled = true

      self.editBarButtonItem?.title = "Save"
      self.editBarButtonItem?.tintColor = UIColor.black
    } else {
      self.view.endEditing(true)

      if self.validateAllFields() {
        UserServices().updateUserProfile(self)
      }
    }
    self.tableViewProfile?.reloadData()
  }

  ///  Button action for LeadtimeButton, CancelButton & DoneButton.
  @IBAction func buttonActionLeadTime(_ sender: UIButton) {

    let alertView = UIAlertController(
      title: kLeadTimeSelectText,
      message: "\n\n\n\n\n\n\n\n\n\n\n",
      preferredStyle: UIAlertController.Style.actionSheet
    )

    datePickerView = UIDatePicker.init(
      frame: CGRect(x: 10, y: 30, width: alertView.view.frame.size.width - 40, height: 216)
    )

    datePickerView?.datePickerMode = .countDownTimer

    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "HH:mm"

    datePickerView?.date = dateFormatter.date(from: "00:00")!

    alertView.view.addSubview(datePickerView!)

    let action = UIAlertAction(
      title: kActionSheetDoneButtonTitle,
      style: UIAlertAction.Style.default,
      handler: {
        _ in

        let calender: Calendar? = Calendar.current

        if Utilities.isValidValue(someObject: self.datePickerView?.date as AnyObject?) {

          let dateComponent = calender?.dateComponents(
            [.hour, .minute],
            from: (self.datePickerView?.date)!
          )

          let title: String! =
            (((dateComponent?.hour)! as Int) < 10
              ? "0\((dateComponent?.hour)! as Int)"
              : "\((dateComponent?.hour)! as Int)") + ":"

            + (((dateComponent?.minute)! as Int) < 10
              ? "0\((dateComponent?.minute)! as Int)"
              : "\((dateComponent?.minute)! as Int)")

          self.buttonLeadTime?.setTitle(title!, for: .normal)
          self.user.settings?.leadTime = title
        }

      }
    )
    let actionCancel = UIAlertAction(
      title: kActionSheetCancelButtonTitle,
      style: UIAlertAction.Style.default,
      handler: {
        _ in

      }
    )

    alertView.addAction(action)
    alertView.addAction(actionCancel)
    present(alertView, animated: true, completion: nil)

  }

  /// Signout Button Clicked.
  @IBAction func buttonActionSignOut(_ sender: UIButton) {

    UIUtilities.showAlertMessageWithTwoActionsAndHandler(
      NSLocalizedString(kSignOutText, comment: ""),
      errorMessage: NSLocalizedString(kAlertMessageForSignOut, comment: ""),
      errorAlertActionTitle: NSLocalizedString(kSignOutText, comment: ""),
      errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""),
      viewControllerUsed: self,
      action1: {
        self.isSigningOut = true
        LeftMenuViewController.updatePushTokenToEmptyString(delegate: self)

      },
      action2: {
        // Handle cancel action
      }
    )

  }

  /// Delete Account clicked.
  @IBAction func buttonActionDeleteAccount(_ sender: UIButton) {

    if (Gateway.instance.studies?.count)! > 0 {
      let studies = Gateway.instance.studies
      var joinedStudies: [Study] = []
      if Utilities.isStandaloneApp() {
        let standaloneStudyId = Utilities.standaloneStudyId()
        joinedStudies =
          studies?.filter({
            ($0.userParticipateState.status == .enrolled
              || $0.userParticipateState
                .status
                == .completed)
              && ($0.studyId == standaloneStudyId)
          }) ?? []
      } else {
        joinedStudies =
          studies?.filter({
            $0.userParticipateState.status == .enrolled
              || $0.userParticipateState
                .status
                == .completed
          }) ?? []
      }

      if joinedStudies.count != 0 {
        self.performSegue(withIdentifier: "confirmationSegue", sender: joinedStudies)
      } else {

        let navTitle = Branding.productTitle
        var descriptionText = kDeleteAccountConfirmationMessage
        descriptionText = descriptionText.replacingOccurrences(
          of: "#APPNAME#",
          with: navTitle
        )

        UIUtilities.showAlertMessageWithTwoActionsAndHandler(
          NSLocalizedString(kTitleDeleteAccount, comment: ""),
          errorMessage: NSLocalizedString(descriptionText, comment: ""),
          errorAlertActionTitle: NSLocalizedString(kTitleDeleteAccount, comment: ""),
          errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""),
          viewControllerUsed: self,
          action1: {

            self.sendRequestToDeleteAccount()

          },
          action2: {
            // Handle cancel action
          }
        )
      }
    }

  }

  // MARK: - Utility Methods

  @objc func dismissKeyboard() {
    self.view.endEditing(true)
  }

  /// Api Call to SignOut.
  func sendRequestToSignOut() {
    addProgressIndicator()
    UserAPI.logout { (status, error) in
      self.removeProgressIndicator()
      if status {
        self.handleSignoutResponse()
      } else if let error = error {
        self.presentDefaultAlertWithError(
          error: error,
          animated: true,
          action: nil,
          completion: nil
        )
      }
    }
  }

  /// Api call to delete account.
  func sendRequestToDeleteAccount() {
    let studies: [StudyToDelete] = []
    UserServices().deActivateAccount(studiesToDelete: studies, delegate: self)
  }

  /// SignOut Response handler for slider menu setup.
  func handleSignoutResponse() {

    if ORKPasscodeViewController.isPasscodeStoredInKeychain() {
      ORKPasscodeViewController.removePasscodeFromKeychain()
    }

    let ud = UserDefaults.standard
    ud.set(false, forKey: kPasscodeIsPending)
    ud.set(false, forKey: kShowNotification)
    ud.synchronize()

    let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
    appDelegate.updateKeyAndInitializationVector()

    let leftController = (slideMenuController()?.leftViewController as? LeftMenuViewController)!
    leftController.changeViewController(.studyList)
    leftController.createLeftmenuItems()

  }

  /// DeleteAccount Response handler.
  func handleDeleteAccountResponse() {

    ORKPasscodeViewController.removePasscodeFromKeychain()

    let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
    appDelegate.updateKeyAndInitializationVector()

    UIUtilities.showAlertMessageWithActionHandler(
      NSLocalizedString(kTitleMessage, comment: ""),
      message: NSLocalizedString(kMessageAccountDeletedSuccess, comment: ""),
      buttonTitle: NSLocalizedString(kTitleOk, comment: ""),
      viewControllerUsed: self
    ) {

      if Utilities.isStandaloneApp() {

        UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
        Study.currentStudy = nil
        self.slideMenuController()?.leftViewController?.navigationController?
          .popToRootViewController(animated: false)
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
          UIApplication.shared.keyWindow?.removeProgressIndicatorFromWindow()
        }
      } else {
        self.slideMenuController()?.leftViewController?.navigationController?
          .popToRootViewController(animated: false)
        HomeViewController.setRootView()
      }

    }
  }

  /// Se tInitial Date sets lead Time.
  func setInitialDate() {

    if user.settings != nil
      && Utilities.isValidValue(
        someObject: user.settings?.leadTime as AnyObject?
      )
    {
      self.buttonLeadTime?.setTitle(user.settings?.leadTime, for: .normal)
    } else {
      // Settings/LeadTime is null
    }
    self.title = NSLocalizedString(kProfileTitleText, comment: "")
    self.isCellEditable = true

    self.buttonLeadTime?.isUserInteractionEnabled = false

    // 1. Create a authentication context
    let authenticationContext = LAContext()
    var error: NSError?

    // 2. Check if the device has a fingerprint sensor
    // If not, show the user an alert view and bail out!
    guard
      authenticationContext.canEvaluatePolicy(
        .deviceOwnerAuthenticationWithBiometrics,
        error: &error
      )
    else {
      return
    }

    var passcodeDict: [String: Any] = (tableViewRowDetails?[3] as? [String: Any])!

    guard
      let keychainPasscodeDict = try? ORKKeychainWrapper.object(forKey: korkPasscode)
        as? [String: Any]
    else {
      return
    }

    var istouchIdEnabled: Bool = false
    if keychainPasscodeDict.count > 0 {
      istouchIdEnabled = keychainPasscodeDict[ktouchid] as? Bool ?? false
    }

    var touchLabelText = kUsePasscodeToAccessApp
    if istouchIdEnabled {
      if authenticationContext.biometryType == .faceID {
        touchLabelText = kUseFaceIdOrPasscode
      } else if authenticationContext.biometryType == .touchID {
        touchLabelText = kUseTouchIdOrPasscode
      }
    }
    passcodeDict[kLabelName] = touchLabelText
    tableViewRowDetails?.replaceObject(at: 3, with: passcodeDict)

  }

  @objc func toggleValueChanged(_ sender: UISwitch) {

    let toggle = sender

    if user.settings != nil {

      switch ToggelSwitchTags(rawValue: sender.tag)! as ToggelSwitchTags {
      case .usePasscode:
        self.checkPasscode()

      case .useTouchId:
        user.settings?.touchId = toggle.isOn
        UserServices().updateUserProfile(self)
      case .receivePush:
        user.settings?.remoteNotifications = toggle.isOn
        UserServices().updateUserProfile(self)
      case .receiveStudyActivityReminders:
        user.settings?.localNotifications = toggle.isOn
        if (user.settings?.localNotifications)! {
          self.perform(
            #selector(self.registerLocalNotification),
            with: self,
            afterDelay: 1.0
          )
        } else {
          self.perform(
            #selector(self.cancelAllLocalNotifications),
            with: self,
            afterDelay: 1.0
          )
        }
        UserServices().updateUserProfile(self)
      }
      self.editBarButtonItem?.tintColor = UIColor.black
    }
  }

  @objc func cancelAllLocalNotifications() {
    LocalNotification.cancelAllLocalNotification()
  }

  @objc func registerLocalNotification() {
    LocalNotification.refreshAllLocalNotification()
  }

  ///  Button action for Change password button.
  @objc func pushToChangePassword(_ sender: UIButton) {
    self.performSegue(withIdentifier: kChangePasswordSegueIdentifier, sender: nil)
  }

  /// Validation to check entered email is valid or not.
  ///  - Returns: A Boolean value indicating wheather All the fields are valid.
  func validateAllFields() -> Bool {

    if (user.emailId?.isEmpty)! {
      self.showAlertMessages(textMessage: kMessageAllFieldsAreEmpty)
      return false
    } else if user.emailId == "" {
      self.showAlertMessages(textMessage: kMessageEmailBlank)
      return false
    } else if !(Utilities.isValidEmail(testStr: user.emailId!)) {
      self.showAlertMessages(textMessage: kMessageValidEmail)
      return false
    }
    return true
  }

  /// Method to show the alert using Utility
  /// - Parameter textMessage: Message to be displayed.
  func showAlertMessages(textMessage: String) {
    UIUtilities.showAlertMessage(
      "",
      errorMessage: NSLocalizedString(textMessage, comment: ""),
      errorAlertActionTitle: NSLocalizedString("OK", comment: ""),
      viewControllerUsed: self
    )
  }

  /// Used to check weather the user id FDA user or not.
  func checkPasscode() {
    if User.currentUser.userType == .loggedInUser {
      // FDA user
      if ORKPasscodeViewController.isPasscodeStoredInKeychain() == false {
        let passcodeStep = ORKPasscodeStep(identifier: kPasscodeStepIdentifier)
        passcodeStep.passcodeType = .type4Digit
        passcodeStep.text = kSetPasscodeDescription
        let task = ORKOrderedTask(
          identifier: kPasscodeTaskIdentifier,
          steps: [passcodeStep]
        )
        let taskViewController = ORKTaskViewController.init(task: task, taskRun: nil)
        taskViewController.delegate = self
        taskViewController.isNavigationBarHidden = true
        taskViewController.navigationBar.prefersLargeTitles = false
        taskViewController.modalPresentationStyle = .fullScreen
        self.navigationController?.present(
          taskViewController,
          animated: false,
          completion: nil
        )
      } else {
        let passcodeViewController =
          ORKPasscodeViewController
          .passcodeAuthenticationViewController(
            withText: "",
            delegate: self
          )
        passcodeStateIsEditing = true

        passcodeViewController.modalPresentationStyle = .fullScreen
        self.navigationController?.present(
          passcodeViewController,
          animated: false,
          completion: nil
        )
      }
    }
  }

  // MARK: - Segue Method

  /// Segue Delegate method for Navigation based on segue connected.
  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {

    if let changePassword = segue.destination as? ChangePasswordViewController {
      changePassword.viewLoadFrom = .profile

    } else if let confirmDelete = segue.destination as? ConfirmationViewController {
      confirmDelete.joinedStudies = (sender as? [Study])!

    }
  }
}

// MARK: - TableView Data source
extension ProfileViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return tableViewRowDetails!.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    let tableViewData = (tableViewRowDetails?.object(at: indexPath.row) as? NSDictionary)!

    if indexPath.row <= signupCellLastIndex {
      // for SignUp Cell data

      let cell =
        (tableView.dequeueReusableCell(withIdentifier: "CommonDetailsCell", for: indexPath)
        as? SignUpTableViewCell)!
      cell.textFieldValue?.text = ""

      var isSecuredEntry: Bool = false
      cell.isUserInteractionEnabled = self.isCellEditable!

      cell.textFieldValue?.tag = indexPath.row
      cell.textFieldValue?.delegate = self

      var keyBoardType: UIKeyboardType? = UIKeyboardType.default
      let textFieldTag = TextFieldTags(rawValue: indexPath.row)!

      // TextField properties set up according to index
      switch textFieldTag {
      case .password:

        cell.buttonChangePassword?.isUserInteractionEnabled = true
        cell.buttonChangePassword?.isHidden = false
        cell.buttonChangePassword?.addTarget(
          self,
          action: #selector(pushToChangePassword),
          for: .touchUpInside
        )
        cell.buttonChangePassword?.setTitleColor(
          kUIColorForSubmitButtonBackground,
          for: .normal
        )
        cell.textFieldValue?.isHidden = true
        cell.isUserInteractionEnabled = true

        isSecuredEntry = true
      case .emailId:
        keyBoardType = .emailAddress
        isSecuredEntry = false

      case .confirmPassword:  //  ChangePasscode

        cell.textFieldValue?.isHidden = true
        cell.buttonChangePassword?.isHidden = false
        cell.buttonChangePassword?.setTitle("Change passcode", for: .normal)

        if User.currentUser.settings?.passcode == true {
          cell.buttonChangePassword?.isUserInteractionEnabled = true

          cell.buttonChangePassword?.setTitleColor(
            kUIColorForSubmitButtonBackground,
            for: .normal
          )

          cell.isUserInteractionEnabled = true
        } else {
          cell.buttonChangePassword?.isUserInteractionEnabled = false
          cell.buttonChangePassword?.setTitleColor(UIColor.gray, for: .normal)

          cell.isUserInteractionEnabled = false
        }

        cell.buttonChangePassword?.addTarget(
          self,
          action: #selector(buttonActionChangePassCode),
          for: .touchUpInside
        )

      }
      // Cell data setup
      cell.populateCellData(
        data: tableViewData,
        securedText: isSecuredEntry,
        keyboardType: keyBoardType
      )

      cell.backgroundColor = UIColor.clear

      cell.setCellData(tag: TextFieldTags(rawValue: indexPath.row)!)

      if TextFieldTags(rawValue: indexPath.row) == .emailId {
        cell.textFieldValue?.isUserInteractionEnabled = false
      }

      return cell
    } else {
      // for ProfileTableViewCell data
      let cell =
        (tableView.dequeueReusableCell(
          withIdentifier: kProfileTableViewCellIdentifier,
          for: indexPath
        )
        as? ProfileTableViewCell)!
      cell.setCellData(dict: tableViewData)

      if user.settings != nil {
        cell.setToggleValue(indexValue: indexPath.row)
      }
      cell.switchToggle?.tag = indexPath.row
      // Toggle button Action
      cell.switchToggle?.addTarget(
        self,
        action: #selector(ProfileViewController.toggleValueChanged),
        for: .valueChanged
      )

      cell.isUserInteractionEnabled = self.isCellEditable!

      return cell
    }

  }
}

// MARK: - TableView Delegates
extension ProfileViewController: UITableViewDelegate {

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    tableView.deselectRow(at: indexPath, animated: true)

  }
}

// MARK: - Textfield Delegate
extension ProfileViewController: UITextFieldDelegate {

  func textField(
    _ textField: UITextField,
    shouldChangeCharactersIn range: NSRange,
    replacementString string: String
  ) -> Bool {

    let tag: TextFieldTags = TextFieldTags(rawValue: textField.tag)!
    // Disabling space editing

    let finalString = textField.text! + string

    if tag == .emailId {
      if string == " " || finalString.count > 255 {
        return false
      } else {
        return true
      }
    } else {
      return true
    }

  }

  func textFieldDidEndEditing(_ textField: UITextField) {
    // trimming white spaces
    textField.text = textField.text?.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)

    switch textField.tag {

    case TextFieldTags.emailId.rawValue:
      user.emailId! = textField.text!

    case TextFieldTags.password.rawValue:
      user.password! = textField.text!

    default:
      break
    }

  }
}

// MARK: - UserService Response handler
extension ProfileViewController: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    self.addProgressIndicator()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    if requestName as String == RegistrationMethods.userProfile.description {
      self.removeProgressIndicator()
      self.tableViewProfile?.reloadData()

      if (user.settings?.leadTime?.count)! > 0 {
        self.buttonLeadTime?.setTitle(user.settings?.leadTime, for: .normal)
      }

    } else if requestName as String == RegistrationMethods.updateUserProfile.description {

      if self.isSigningOut {
        self.isSigningOut.toggle()
        self.sendRequestToSignOut()
        return
      }
      self.removeProgressIndicator()
      self.isCellEditable = true
      self.editBarButtonItem?.title = "Edit"
      self.tableViewProfile?.reloadData()
      self.buttonLeadTime?.isUserInteractionEnabled = self.isCellEditable!
      DBHandler.saveUserSettingsToDatabase()

    } else if requestName as String == RegistrationMethods.deactivate.description {
      self.removeProgressIndicator()
      self.handleDeleteAccountResponse()
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    self.removeProgressIndicator()

    if error.code == HTTPError.forbidden.rawValue {  //unauthorized

      UIUtilities.showAlertMessageWithActionHandler(
        kErrorTitle,
        message: error.localizedDescription,
        buttonTitle: kTitleOk,
        viewControllerUsed: self,
        action: {
          self.fdaSlideMenuController()?.navigateToHomeAfterUnauthorizedAccess()
        }
      )
    } else {

      UIUtilities.showAlertWithTitleAndMessage(
        title: NSLocalizedString(kErrorTitle, comment: "") as NSString,
        message: error.localizedDescription as NSString
      )
    }
  }
}

// MARK: - ORKPasscode Delegate
extension ProfileViewController: ORKPasscodeDelegate {

  func passcodeViewControllerDidFinish(withSuccess viewController: UIViewController) {

    defer {
      if let settings = user.settings,
        !isChangePasswordEditing
      {
        settings.passcode = false
        if ORKPasscodeViewController.isPasscodeStoredInKeychain() {
          ORKPasscodeViewController.removePasscodeFromKeychain()
        }
        DispatchQueue.main.async {
          UserServices().updateUserProfile(self)
        }
      }
      isChangePasswordEditing = false
    }
    self.isPasscodeViewPresented = true

    // Recent Changes
    let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
    appDelegate.appIsResignedButDidNotEnteredBackground = false

    self.perform(#selector(dismissTaskViewController), with: self, afterDelay: 1)
    self.setInitialDate()
  }

  func passcodeViewControllerDidFailAuthentication(_ viewController: UIViewController) {

  }

  func passcodeViewControllerDidCancel(_ viewController: UIViewController) {

    if passcodeStateIsEditing {
      viewController.dismiss(
        animated: true,
        completion: {
          self.passcodeStateIsEditing = false
        }
      )
    }

  }

}

// MARK: - ORKTaskViewController Delegate
extension ProfileViewController: ORKTaskViewControllerDelegate {

  func taskViewControllerSupportsSaveAndRestore(_ taskViewController: ORKTaskViewController)
    -> Bool
  {
    return true
  }

  public func taskViewController(
    _ taskViewController: ORKTaskViewController,
    didFinishWith reason: ORKTaskViewControllerFinishReason,
    error: Error?
  ) {

    switch reason {

    case .completed:
      // Following will be executed only when passcode is setted for first time

      if taskViewController.task?.identifier != "ChangePassCodeTask" {
        user.settings?.passcode = true
        self.isPasscodeViewPresented = true
        DispatchQueue.main.async {
          UserServices().updateUserProfile(self)
        }
      }
      let ud = UserDefaults.standard
      ud.set(false, forKey: kPasscodeIsPending)
      ud.synchronize()

    case .failed: break
    case .discarded: break
    case .saved: break

    @unknown default:
      break
    }

    let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
    appDelegate.appIsResignedButDidNotEnteredBackground = false

    self.perform(#selector(dismissTaskViewController), with: self, afterDelay: 1)
  }

  @objc func dismissTaskViewController() {
    self.dismiss(
      animated: true,
      completion: {
      }
    )
  }

  func taskViewController(
    _ taskViewController: ORKTaskViewController,
    stepViewControllerWillAppear stepViewController: ORKStepViewController
  ) {

  }
}
