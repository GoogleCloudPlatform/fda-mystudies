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

import SlideMenuControllerSwift
import Toast_Swift
import UIKit

let kStoryboardIdentifierLogin = "Login"
let kStoryboardIdentifierHomeView = "HomeViewController"
let kLeftMenuSubtitle = "subTitle"
let kLeftMenuTitle = "menuTitle"
let kLeftMenuIconName = "iconName"
let kLeftMenuCellTitleHome = "Home"
let kLeftMenuCellTitleResources = "Resources"
let kLeftMenuCellTitleProfile = "My account"
let kLeftMenuCellTitleSignIn = "Sign in"
let kLeftMenuCellTitleNewUser = "New user?"
let kLeftMenuCellSubTitleValue = "Sign up"
let kAlertMessageReachoutText = "This feature will be available in the next sprint."

let kAlertMessageForSignOut = "Are you sure you want to sign out?"
let kAlertMessageSignOutSync =
  """
  Are you sure you want to sign out? \
  Incomplete activities and activities that were completed in offline mode must be re-started when you next sign in.
  """

let kAlertSignOutLaterTitle = "Sign out later"

// MARK: Segue Identifiers
let kLoginStoryboardIdentifier = "Login"

enum LeftMenu: Int {
  case studyList = 0
  case resources
  case profileReachOut
  case reachOutSignIn
  case signup
  case signOut
}

protocol LeftMenuProtocol: class {
  func changeViewController(_ menu: LeftMenu)
}

class LeftMenuViewController: UIViewController, LeftMenuProtocol {

  // MARK: - Outlets
  @IBOutlet weak var tableView: UITableView!

  @IBOutlet weak var labelVersion: UILabel!
  @IBOutlet weak var labelProductName: UILabel!
  @IBOutlet weak var labelPoweredBy: UILabel!
  @IBOutlet weak var tableHeaderView: UIView!
  @IBOutlet weak var tableFooterView: UIView!

  // MARK: - Properties
  lazy var menus: [[String: Any]] = [
    [
      "menuTitle": "Home",
      "iconName": "home_menu1-1",
      "menuType": LeftMenu.studyList,
    ],

    [
      "menuTitle": "Resources",
      "iconName": "resources_menu1",
      "menuType": LeftMenu.resources,
    ],
  ]

  /// Standalone
  var studyTabBarController: UITabBarController!

  var studyHomeViewController: UINavigationController!

  /// Gateway & standalone
  var studyListViewController: UINavigationController!

  var notificationController: UIViewController!
  var resourcesViewController: UINavigationController!
  var profileviewController: UIViewController!
  var nonMenuViewController: UIViewController!
  var reachoutViewController: UINavigationController!
  var signInViewController: UINavigationController!
  var signUpViewController: UINavigationController!

  var shouldAllowToGiveFeedback = true

  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
  }

  // MARK: - ViewController Lifecycle
  override func viewDidLoad() {
    super.viewDidLoad()

    self.view.isHidden = true

    shouldAllowToGiveFeedback = Branding.allowFeedback
    self.createLeftmenuItems()

    labelProductName.text = Branding.navigationTitleName
    labelPoweredBy.text = Branding.poweredByTitleName

    self.tableView.separatorColor = UIColor(
      red: 224 / 255,
      green: 224 / 255,
      blue: 224 / 255,
      alpha: 1.0
    )

    if Utilities.isStandaloneApp() {
      setupStandaloneMenu()
    } else {
      setupGatewayMenu()
    }

    self.labelVersion.text = "V" + "\(Utilities.getAppVersion())"
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    self.view.isHidden = false
  }

  final private func setupGatewayMenu() {

    let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)

    self.studyListViewController =
      (storyboard.instantiateViewController(
        withIdentifier: String(describing: StudyListViewController.classForCoder())
      )
      as? UINavigationController)!

    self.notificationController =
      (storyboard.instantiateViewController(
        withIdentifier: String(describing: NotificationViewController.classForCoder())
      )
      as? UINavigationController)!

    self.resourcesViewController =
      (storyboard.instantiateViewController(
        withIdentifier: String(
          describing: GatewayResourcesListViewController.classForCoder()
        )
      )
      as? UINavigationController)!

    self.profileviewController =
      (storyboard.instantiateViewController(
        withIdentifier: String(describing: ProfileViewController.classForCoder())
      )
      as? UINavigationController)!

    self.reachoutViewController =
      (storyboard.instantiateViewController(
        withIdentifier: String(describing: ReachoutOptionsViewController.classForCoder())
      )
      as? UINavigationController)!

  }

  // MARK: - UI Utils

  /// This method will setup the Menu in case of Standalone app.
  final private func setupStandaloneMenu() {

    let studyStoryBoard = UIStoryboard.init(name: kStudyStoryboard, bundle: Bundle.main)
    // for standalone
    self.studyTabBarController =
      studyStoryBoard.instantiateViewController(
        withIdentifier: kStudyDashboardTabbarControllerIdentifier
      )
      as! StudyDashboardTabbarViewController

    let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)

    self.studyListViewController =
      storyboard.instantiateViewController(
        withIdentifier: String(describing: StudyListViewController.classForCoder())
      )
      as? UINavigationController

    self.studyHomeViewController =
      studyStoryBoard.instantiateViewController(
        withIdentifier: String(describing: "StudyHomeNavigationController")
      )
      as? UINavigationController  // for standalone

    self.notificationController =
      storyboard.instantiateViewController(
        withIdentifier: String(describing: NotificationViewController.classForCoder())
      )
      as? UINavigationController

    self.resourcesViewController =
      storyboard.instantiateViewController(
        withIdentifier: String(describing: GatewayResourcesListViewController.classForCoder())
      )
      as? UINavigationController

    self.profileviewController =
      storyboard.instantiateViewController(
        withIdentifier: String(describing: ProfileViewController.classForCoder())
      )
      as? UINavigationController

    self.reachoutViewController =
      storyboard.instantiateViewController(
        withIdentifier: String(describing: ReachoutOptionsViewController.classForCoder())
      )
      as? UINavigationController

  }

  /// Used to create Login controller for new user using SignInViewController and SignUpViewController.
  func createControllersForAnonymousUser() {

    let loginStoryBoard = UIStoryboard(name: kLoginStoryboardIdentifier, bundle: nil)
    let signInController =
      (loginStoryBoard.instantiateViewController(
        withIdentifier: String(describing: SignInViewController.classForCoder())
      )
      as? SignInViewController)!
    self.signInViewController = UINavigationController(rootViewController: signInController)
    self.signInViewController.navigationBar.barStyle = .default
    self.signInViewController.navigationBar.isTranslucent = false

    let signUpController =
      (loginStoryBoard.instantiateViewController(
        withIdentifier: String(describing: SignUpViewController.classForCoder())
      )
      as? SignUpViewController)!
    self.signUpViewController = UINavigationController(rootViewController: signUpController)
    self.signUpViewController.navigationBar.barStyle = .default
    self.signUpViewController.navigationBar.isTranslucent = false
  }

  /// Used to create Left menu items.
  func createLeftmenuItems() {

    self.createControllersForAnonymousUser()

    let user = User.currentUser

    menus = [
      [
        "menuTitle": "Home",
        "iconName": "home_menu1-1",
        "menuType": LeftMenu.studyList,
      ]
    ]

    if !Utilities.isStandaloneApp() {

      menus.append(
        [
          "menuTitle": "Resources",
          "iconName": "resources_menu1",
          "menuType": LeftMenu.resources,
        ])
    }

    if user.userType == .loggedInUser {
      menus.append(
        [
          "menuTitle": "My account",
          "iconName": "profile_menu1",
          "menuType": LeftMenu.profileReachOut,
        ])

      if shouldAllowToGiveFeedback {
        menus.append(
          [
            "menuTitle": "Reach out",
            "iconName": "reachout_menu1",
            "menuType": LeftMenu.reachOutSignIn,
          ])
      }
      menus.append(
        [
          "menuTitle": "Sign out",
          "iconName": "ic_signout_menu",
          "menuType": LeftMenu.signOut,
        ])
    } else {
      if shouldAllowToGiveFeedback {
        menus.append(
          [
            "menuTitle": "Reach out",
            "iconName": "reachout_menu1",
            "menuType": LeftMenu.profileReachOut,
          ])
      }

      menus.append(
        [
          "menuTitle": "Sign in",
          "iconName": "signin_menu1",
          "menuType": LeftMenu.reachOutSignIn,
        ])

      menus.append(
        [
          "menuTitle": "New user?",
          "iconName": "newuser_menu1",
          "subTitle": "Sign up",
          "menuType": LeftMenu.signup,
        ])
    }

    // Setting proportion height of the header and footer view
    var height: CGFloat? = 0.0
    height = (UIScreen.main.bounds.size.height - CGFloat(menus.count * 70)) / 2

    self.tableHeaderView.frame.size = CGSize(
      width: self.tableHeaderView!.frame.size.width,
      height: height!
    )
    self.tableFooterView.frame.size = CGSize(
      width: self.tableFooterView!.frame.size.width,
      height: height!
    )
    self.tableView.frame.size = CGSize(
      width: self.tableView.frame.width,
      height: UIScreen.main.bounds.size.height
    )

    self.tableView.reloadData()

  }

  /// Used to change the view controller when clicked from the left menu.
  /// - Parameter menu:  Accepts the data from enum LeftMenu
  func changeViewController(_ menu: LeftMenu) {

    let isStandalone = Utilities.isStandaloneApp()

    switch menu {
    case .studyList:

      if isStandalone {
        if Study.currentStudy?.userParticipateState.status == .enrolled {
          self.slideMenuController()?.changeMainViewController(
            self.studyTabBarController,
            close: true
          )
        } else {
          self.slideMenuController()?.changeMainViewController(
            self.studyHomeViewController,
            close: true
          )
        }

      } else {
        self.slideMenuController()?.changeMainViewController(
          self.studyListViewController,
          close: true
        )
      }

    case .resources:
      self.slideMenuController()?.changeMainViewController(
        self.resourcesViewController,
        close: true
      )

    case .profileReachOut:

      if User.currentUser.userType == .loggedInUser {
        self.slideMenuController()?.changeMainViewController(
          self.profileviewController,
          close: true
        )

      } else {
        // go to ReachOut screen
        self.slideMenuController()?.changeMainViewController(
          self.reachoutViewController,
          close: true
        )
      }

    case .reachOutSignIn:
      if User.currentUser.userType == .loggedInUser {
        // go to reach out
        self.slideMenuController()?.changeMainViewController(
          self.reachoutViewController,
          close: true
        )

      } else {

        // go sign in
        self.slideMenuController()?.changeMainViewController(
          self.signInViewController,
          close: true
        )
      }
    case .signup:
      self.slideMenuController()?.changeMainViewController(
        self.signUpViewController,
        close: true
      )
    case .signOut:
      buttonActionSignOut()
    }
  }

  // MARK: - Button Actions

  /// Signout button clicked.
  /// - Parameter sender: Instance of UIButton.
  private func buttonActionSignOut() {

    DBHandler.isDataAvailableToSync { (available) in
      if available {

        UIUtilities.showAlertMessageWithTwoActionsAndHandler(
          NSLocalizedString(kSignOutText, comment: ""),
          errorMessage: NSLocalizedString(kAlertMessageSignOutSync, comment: ""),
          errorAlertActionTitle: NSLocalizedString(kSignOutText, comment: ""),
          errorAlertActionTitle2: NSLocalizedString(kAlertSignOutLaterTitle, comment: ""),
          viewControllerUsed: self,
          action1: {
            LeftMenuViewController.updatePushTokenToEmptyString(delegate: self)
          },
          action2: {
            // Cancel Action.
          }
        )
      } else {

        UIUtilities.showAlertMessageWithTwoActionsAndHandler(
          NSLocalizedString(kSignOutText, comment: ""),
          errorMessage: NSLocalizedString(kAlertMessageForSignOut, comment: ""),
          errorAlertActionTitle: NSLocalizedString(kSignOutText, comment: ""),
          errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""),
          viewControllerUsed: self,
          action1: {
            LeftMenuViewController.updatePushTokenToEmptyString(delegate: self)
          },
          action2: {
            // Cancel Action.
          }
        )
      }
    }

  }

  /// Call webservice to logout current user.
  func sendRequestToSignOut() {
    UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
    UserAPI.logout { (status, error) in
      if status {
        self.signout()
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

  /// This methods updates the push token on server with an empty string to avoid push notifications when user logs out.
  /// This change was done from client side due to Hydra limitations which is not able to
  /// set the push token to null with logout service.
  /// - Parameter delegate: Service delegate.
  final class func updatePushTokenToEmptyString(delegate: NMWebServiceDelegate) {
    UserServices().updateUserProfile(deviceToken: "", delegate: delegate)
  }

  /// As the user is Signed out Remove passcode from the keychain
  func signout() {

    ORKPasscodeViewController.removePasscodeFromKeychain()

    let ud = UserDefaults.standard
    ud.set(false, forKey: kPasscodeIsPending)
    ud.set(false, forKey: kShowNotification)
    ud.synchronize()

    StudyDashboard.instance.dashboardResponse = []
    let appDelegate = UIApplication.shared.delegate as! AppDelegate
    appDelegate.updateKeyAndInitializationVector()

    UIApplication.shared.keyWindow?.removeProgressIndicatorFromWindow()
    self.navigationController?.popToRootViewController(animated: true)
    if !Utilities.isStandaloneApp() {
      HomeViewController.setRootView()
    }
  }

}

// MARK: - UITableView Delegate
extension LeftMenuViewController: UITableViewDelegate {

  func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {

    if let menu = LeftMenu(rawValue: indexPath.row) {
      switch menu {
      case .studyList, .resources, .profileReachOut, .reachOutSignIn, .signup, .signOut:
        return 70.0
      }
    }
    return 0
  }

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {

    tableView.deselectRow(at: indexPath, animated: true)

    if let menu = menus[indexPath.row]["menuType"] as? LeftMenu {
      self.changeViewController(menu)
    }
  }

  func scrollViewDidScroll(_ scrollView: UIScrollView) {
    if self.tableView == scrollView {

    }
  }
}

// MARK: - UITableView DataSource
extension LeftMenuViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return menus.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    let dict: [String: Any]? = menus[indexPath.row]

    if dict?["subTitle"] != nil {
      var cell: LeftMenuCell?
      cell =
        tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
        as? LeftMenuCell
      cell?.populateCellData(data: menus[indexPath.row])
      return cell!
    } else {
      var cell: LeftMenuResourceTableViewCell?
      cell =
        tableView.dequeueReusableCell(
          withIdentifier: "LeftMenuResourceCell",
          for: indexPath
        )
        as? LeftMenuResourceTableViewCell
      cell?.populateCellData(data: menus[indexPath.row])
      return cell!
    }
  }
}

// MARK: - UserService Response handler
extension LeftMenuViewController: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    if requestName as String == RegistrationMethods.updateUserProfile.description {
      self.sendRequestToSignOut()
    } else {
      UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    UIApplication.shared.keyWindow?.removeProgressIndicatorFromWindow()

    if error.code == HTTPError.forbidden.rawValue {  // unauthorized
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
