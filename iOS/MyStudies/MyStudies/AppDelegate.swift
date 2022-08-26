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

import CallKit
import IQKeyboardManagerSwift
import RealmSwift
import UIKit
import UserNotifications
import Firebase
import FirebaseAnalytics
import Reachability

@UIApplicationMain

class AppDelegate: UIResponder, UIApplicationDelegate, MessagingDelegate {

  var window: UIWindow?

  var notificationDetails: [String: Any]? = [String: Any]()

  var appIsResignedButDidNotEnteredBackground: Bool? = false

  var alertVCPresented: UIAlertController?

  var isPasscodePresented: Bool? = false

  var isComprehensionFailed: Bool? = false

  var parentViewControllerForAlert: UIViewController?

  var iscomingFromForgotPasscode: Bool = false

  var isAppLaunched: Bool? = false

  let healthStore = HKHealthStore()

  var containerViewController: ResearchContainerViewController? {
    return window?.rootViewController as? ResearchContainerViewController
  }

  var selectedController: UIViewController?

  var shouldAddForceUpgradeScreen = false

  var retryView: ComprehensionFailure?

  var blockerScreen: AppUpdateBlocker?
  var passcodeParentControllerWhileSetup: UIViewController?
    
  private var reachability: Reachability!

  /// to be used in case of ineligible
  var consentToken: String? = ""

  /// Register for Remote Notification
  func askForNotification() {
    UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { (granted, _) in
      // 1. Check if permission granted
      guard granted else { return }
      // 2. Attempt registration for remote notifications on the main thread
      DispatchQueue.main.async {
        UIApplication.shared.registerForRemoteNotifications()
      }
    }
  }
  
  func askForFCMNotification() {
    if #available(iOS 10.0, *) {
      // For iOS 10 display notification (sent via APNS)
      UNUserNotificationCenter.current().delegate = self
      
      let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
      UNUserNotificationCenter.current().requestAuthorization(
        options: authOptions,
        completionHandler: { _, _ in }
      )
    }
    UIApplication.shared.registerForRemoteNotifications()
    getFCMToken()
  }
  
  func getFCMToken() {
    Messaging.messaging().token { token, error in
      if error != nil {
      } else if let token = token {
        if User.currentUser.userType == .loggedInUser {
          User.currentUser.settings?.remoteNotifications = true
          User.currentUser.settings?.localNotifications = true
          // Update device Token to Local server
          UserServices().updateUserProfile(deviceToken: token, delegate: self)
        }
      }
    }
  }

  /// Updates Key & InitializationVector for Encryption
  func updateKeyAndInitializationVector() {

    if User.currentUser.userType == .loggedInUser {  // Registered/LoggedIn User
      // Key byte size shouldn't exceed more than 16.
      FDAKeychain.shared[kEncryptionKey] = String.randomString(length: 16)
    } else {  // Anonymous User
      FDAKeychain.shared[kEncryptionKey] = kEncryptionKey
    }

    if UIDevice.current.model == kIsIphoneSimulator {
      FDAKeychain.shared[kEncryptionIV] = kdefaultIVForEncryption
    } else {
      // Device
      var udid = UIDevice.current.identifierForVendor?.uuidString
      let index = udid?.index((udid?.endIndex)!, offsetBy: -20)
      udid = String((udid?[..<index!])!)
      FDAKeychain.shared[kEncryptionIV] = udid
    }
  }

  /// Handler for TimeZone changes, updates time zone in the local database
  func calculateTimeZoneChange() {
    
    let timeZoneCurrent = TimeZone.current
    let valTimezone = timeZoneCurrent
    let differenceFromCurrent = timeZoneCurrent.secondsFromGMT()
    
    // Saving TimeZone to User Defaults
    let ud = UserDefaults.standard
    let setuptimeDiff = ud.value(forKey: ksetUpTimeIdentifier) as? Int
    
    // Saving time difference
    if setuptimeDiff == nil {
      ud.set(differenceFromCurrent, forKey: ksetUpTimeIdentifier)
      ud.set(0, forKey: "offset")
      
      let timezoneArray = InitialTimezone.init(playerName: valTimezone)
      let encodedData = NSKeyedArchiver.archivedData(withRootObject: timezoneArray)
      ud.set(encodedData, forKey: "oldTimezone")
    } else {
      let difference = differenceFromCurrent - setuptimeDiff!
      ud.set(difference, forKey: "offset")
      if difference != 0 {
        Schedule.utcFormatter = nil
        Schedule.currentZoneFormatter = nil
      }
    }
    ud.synchronize()
  }

  // MARK: - Realm Migragion
  func generateRealmKeys() {

    // Realm Encryption key generation
    if FDAKeychain.shared[kRealmEncryptionKeychainKey] == nil {
      // Generate 64 bytes of random data to serve as the encryption key
      var realmKey = kRealmEncryptionDefaultKey
      var key = Data(count: 64)
      let result = key.withUnsafeMutableBytes {
        SecRandomCopyBytes(kSecRandomDefault, 64, $0.baseAddress!)
      }
      if result == errSecSuccess {
        realmKey = key.base64EncodedString()
      }
      FDAKeychain.shared[kRealmEncryptionKeychainKey] = realmKey
    }
  }

  func checkForRealmMigration() {

    self.generateRealmKeys()

    let key = FDAKeychain.shared[kRealmEncryptionKeychainKey]
    let keyData = Data(base64Encoded: key!)

    let config = Realm.Configuration(
      encryptionKey: keyData,
      schemaVersion: 1,
      migrationBlock: { _, oldSchemaVersion in
        // We haven’t migrated anything yet, so oldSchemaVersion == 0
        if oldSchemaVersion < 1 {
          // Nothing to do!
          // Realm will automatically detect new properties and removed properties
          // And will update the schema on disk automatically
        }
      }
    )

    // Tell Realm to use this new configuration object for the default Realm
    Realm.Configuration.defaultConfiguration = config
  }

  // MARK: - App Delegates methods

  func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    setupReachability()
    // Check if Database needs migration
    self.checkForRealmMigration()
    blockerScreen?.isHidden = true
    blockerScreen?.removeFromSuperview()
    // Override point for customization after application launch.
    UNUserNotificationCenter.current().delegate = self
    self.isAppLaunched = true
    IQKeyboardManager.shared.enable = true
    self.customizeNavigationBar()
    
    NotificationCenter.default.addObserver(self, selector: #selector(self.receivedORKAction(_:)),
                                           name: Notification.Name("ORKAction"), object: nil)
    // Use Firebase library to configure APIs
    FirebaseApp.configure()
    Messaging.messaging().delegate = self

    UIView.appearance(whenContainedInInstancesOf: [ORKTaskViewController.self]).tintColor =
      kUIColorForSubmitButtonBackground

    /// Check For Manage Apps details
    self.addAndRemoveProgress(add: true)
    UserServices().getUserManageApps(self)
    
    UIApplication.shared.applicationIconBadgeNumber = 0
    
    UserDefaults.standard.removeObject(forKey: "applict")

    let ud1 = UserDefaults.standard

    // Check if App is launched because of Notification Received
    if launchOptions != nil && launchOptions?[.sourceApplication] == nil {

      ud1.set("not null", forKey: "launch")

      // Launched from push notification
      let notification = launchOptions?[.remoteNotification]

      if Utilities.isValidObject(someObject: notification as AnyObject) {  // Launched from Remote Notification

        notificationDetails = notification as? [String: Any]
print("1notificationDetails---\(notificationDetails)")
        
        UserDefaults.standard.set("\(notificationDetails)", forKey: "newactivity3")
        UserDefaults.standard.synchronize()
        
        let ud = UserDefaults.standard
        ud.set(true, forKey: kShowNotification)
        ud.synchronize()

      } else {  // Regular Launch

        ud1.set("invalid", forKey: "launch")
        let ud = UserDefaults.standard
        ud.set(false, forKey: kShowNotification)
        ud.synchronize()
      }
    }
    return true
  }

  func applicationWillResignActive(_ application: UIApplication) {
    // Use this method to pause ongoing tasks, disable timers, and invalidate graphics rendering callbacks. Games should use this method to pause the game.

    // set Flag to handle background to foreground transition
    self.appIsResignedButDidNotEnteredBackground = true
  }

  func applicationDidEnterBackground(_ application: UIApplication) {
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.

    // set Flag to handle foreground to background transition
    self.appIsResignedButDidNotEnteredBackground = false
    let ud = UserDefaults.standard
    ud.set(false, forKey: kPasscodeIsPending)
    blockerScreen?.isHidden = true
    blockerScreen?.removeFromSuperview()
  }

  func application(
    _ application: UIApplication,
    performFetchWithCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) ->
      Void
  ) {}

  func applicationWillEnterForeground(_ application: UIApplication) {

    self.checkPasscode(viewController: (application.windows[0].rootViewController)!)
    self.checkForStudyUpdates()
    let number = UIApplication.shared.applicationIconBadgeNumber
    if number >= 1 {
      self.updateNotification(userInfoDetails: nil)
    }
    // Check For Manage Apps details
    self.addAndRemoveProgress(add: true)
    UserServices().getUserManageApps(self)
  }

  func applicationDidBecomeActive(_ application: UIApplication) {

    UIApplication.shared.applicationIconBadgeNumber = 0
    // Clear the delivered notifications when user enter's in the app.
    DispatchQueue.main.async {
      LocalNotification.removeAllDeliveredNotifications()
    }

    if self.appIsResignedButDidNotEnteredBackground! {

      let navController = application.windows[0].rootViewController

      let isTaskViewControllerVisible = (navController as? UINavigationController)?
        .visibleViewController?.isKind(of: ORKTaskViewController.self)

      let navigationTitle =
        ((navController as? UINavigationController)?.visibleViewController
        as? ORKTaskViewController)?.title ?? ""

      if (navController as? UINavigationController) != nil
        && isTaskViewControllerVisible
          == false
      {

        if (navController as? UINavigationController)?.visibleViewController?.isKind(
          of: ORKPasscodeViewController.self
        ) == false {
          // Request for Passcode
          self.checkPasscode(viewController: navController!)
        }

      } else if (navController as? UINavigationController) != nil
        && isTaskViewControllerVisible == true
        && navigationTitle == "Activity"
      {

        if (navController as? UINavigationController)?.visibleViewController?.isKind(
          of: ORKPasscodeViewController.self
        ) == false {
          // Request for Passcode
          self.checkPasscode(viewController: navController!)
        }
      } else if (navController) != nil {
        self.checkPasscode(viewController: navController!)
      }
    }

    // Check if App running on Jailbreak Device
    if AppDelegate.jailbroken(application: application) {

      let navigationController = (self.window?.rootViewController as? UINavigationController)!
      let appBlocker = JailbrokeBlocker.instanceFromNib(
        frame: navigationController.view.frame
      )
      UIApplication.shared.keyWindow?.addSubview(appBlocker)
      UIApplication.shared.keyWindow?.bringSubviewToFront(appBlocker)

    }

    // Update TimeZone Changes if any
    self.calculateTimeZoneChange()

    if self.isAppLaunched! {
      self.isAppLaunched = false
      DispatchQueue.main.async {
        // Update Local Notifications
        self.checkForRegisteredNotifications()
      }
    }
  }

  func applicationWillTerminate(_ application: UIApplication) {
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    blockerScreen?.isHidden = true
    blockerScreen?.removeFromSuperview()
  }

  func setupReachability() {
      do {
          self.reachability = try Reachability()
          } catch(let error) { }
  }
  // MARK: - NOTIFICATION

  func application(
    _ application: UIApplication,
    didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
  ) {
    ///UnComment the below for APNS approach of Push Notification
    let deviceTokenString = deviceToken.reduce("", { $0 + String(format: "%02X", $1) })
    if User.currentUser.userType == .loggedInUser {
      User.currentUser.settings?.remoteNotifications = true
      User.currentUser.settings?.localNotifications = true
//      // Update device Token to Local server
//      UserServices().updateUserProfile(deviceToken: deviceTokenString, delegate: self)
    }
  }

  // MARK: - Jailbreak Methods

  /// Checks Jailbroken status
  /// - Parameter application: `UIApplication` instance
  /// - Returns: `Boolean` state of Jailbroken
  public static func jailbroken(application: UIApplication) -> Bool {
    guard let cydiaUrlScheme = NSURL(string: "cydia://package/com.example.package") else {
      return isJailbroken()
    }
    return application.canOpenURL(cydiaUrlScheme as URL) || isJailbroken()
  }

  /// Checks Jailbroken status
  /// - Returns: `Boolean` state of Jailbroken
  static func isJailbroken() -> Bool {

    if UIDevice.current.model != kIphoneSimulator {
      return false
    }

    let fileManager = FileManager.default
    if fileManager.fileExists(atPath: "/Applications/Cydia.app")
      || fileManager.fileExists(
        atPath: "/Library/MobileSubstrate/MobileSubstrate.dylib"
      )
      || fileManager.fileExists(
        atPath: "/bin/bash"
      ) || fileManager.fileExists(atPath: "/usr/sbin/sshd")
      || fileManager
        .fileExists(atPath: "/etc/apt")
      || fileManager.fileExists(atPath: "/usr/bin/ssh")
    {
      return true
    }

    if canOpen(path: "/Applications/Cydia.app")
      || canOpen(
        path: "/Library/MobileSubstrate/MobileSubstrate.dylib"
      ) || canOpen(path: "/bin/bash")
      || canOpen(path: "/usr/sbin/sshd") || canOpen(path: "/etc/apt")
      || canOpen(
        path: "/usr/bin/ssh"
      )
    {
      return true
    }

    let path = "/private/" + NSUUID().uuidString
    do {
      try "anyString".write(toFile: path, atomically: true, encoding: String.Encoding.utf8)
      try fileManager.removeItem(atPath: path)
      return true
    } catch {
      return false
    }
  }

  static func canOpen(path: String) -> Bool {
    let file = fopen(path, "r")
    guard file != nil else { return false }
    fclose(file)
    return true
  }

  // MARK: - Add Retry Screen

  /// Present Retry View
  /// - Parameter viewController: `UIViewController` instance
  func addRetryScreen(viewController: UIViewController?) {
    if let navigationController = (self.window?.rootViewController as? UINavigationController) {
      self.retryView = ComprehensionFailure.instanceFromNib(
        frame: navigationController.view.frame,
        detail: nil
      )
      
      if viewController != nil {
        retryView?.delegate = (viewController as? ComprehensionFailureDelegate)!
      } else {
        retryView?.delegate = self
      }
      UIApplication.shared.keyWindow?.addSubview(retryView!)
      UIApplication.shared.keyWindow?.bringSubviewToFront(retryView!)
    } else if let windowBounds = UIApplication.shared.keyWindow?.bounds {
      self.retryView = ComprehensionFailure.instanceFromNib(
        frame: windowBounds,
        detail: nil
      )
      
      if viewController != nil {
        retryView?.delegate = (viewController as? ComprehensionFailureDelegate)!
      } else {
        retryView?.delegate = self
      }
      UIApplication.shared.keyWindow?.addSubview(retryView!)
      UIApplication.shared.keyWindow?.bringSubviewToFront(retryView!)
    }
  }

  // MARK: - Custom Navigation Bar

  func customizeNavigationBar() {
    UINavigationBar.appearance().titleTextAttributes = [
      NSAttributedString.Key.font: UIFont(name: "HelveticaNeue-Medium", size: 18)!
    ]
  }

  // MARK: - Checker Methods

  /// Registers pending notifications based on UserType
  func checkForRegisteredNotifications() {

    if User.currentUser.userType == .loggedInUser {

      let center = UNUserNotificationCenter.current()
      center.getPendingNotificationRequests(
        completionHandler: { requests in
          if requests.count < 50 {
            DispatchQueue.main.async {
              // This should get called on main thread to avoid crash.
              LocalNotification.refreshAllLocalNotification()
            }
          }
        })
    }
  }

  /// Webservice request call to SignOut
  func sendRequestToSignOut() {
    UserAPI.logout { (status, error) in
      if status {
        if self.iscomingFromForgotPasscode {
          self.iscomingFromForgotPasscode.toggle()
          self.handleSignoutAfterLogoutResponse()
        } else {
          self.handleSignoutResponse()
        }
      } else if let error = error {
        // error
        self.window?.topMostController()?.presentDefaultAlertWithError(
          error: error,
          animated: true,
          action: nil,
          completion: nil
        )
      }
    }
  }

  /// Check the  current Consent Status for Updated Version
  /// - Parameter controller: Instance of `UIVIewController`
  func checkConsentStatus(controller: UIViewController) {
    
    self.selectedController = controller
    
    if StudyUpdates.studyConsentUpdated && StudyUpdates.studyEnrollAgain {
      // Study consent is updated: Please Present Consent UI.
      //      guard let navigationController = self.window?.rootViewController as? UINavigationController else { return }
      //      var topController: UIViewController = navigationController
      //      if navigationController.viewControllers.count > 0 {
      //        topController = navigationController.viewControllers.first!
      //      }
      
      var topController: UIViewController?
      if let navigationController = self.window?.rootViewController as? UINavigationController {
        print("111---")
        topController = navigationController
        if navigationController.viewControllers.count > 0 {
          topController = navigationController.viewControllers.first!
        }
      } else {
        print("222---")
        let navigationController = self.window?.rootViewController as? UIViewController
        topController = navigationController
        //        topController = (self.window?.topMostController())!
      }
      
      if let topController2 = topController {
        
        UIUtilities.showAlertMessageWithTwoActionsAndHandler(
          NSLocalizedString(kConsentUpdatedTitle, comment: ""),
          errorMessage: NSLocalizedString(kMessageConsentUpdated, comment: ""),
          errorAlertActionTitle: NSLocalizedString(kReviewTitle, comment: ""),
          errorAlertActionTitle2: nil,
          viewControllerUsed: topController2,
          action1: {
              do {
                  self.reachability = try Reachability()
                } catch(let error) { }
              if self.reachability.connection != .unavailable {
//                  self.addAndRemoveProgress(add: true)
                  WCPServices().getEligibilityConsentMetadata(
                    studyId: (Study.currentStudy?.studyId)!,
                    delegate: self as NMWebServiceDelegate
                  )
              } else {
                  if controller.isKind(of: ActivitiesViewController.self) {
                      self.addAndRemoveProgress(add: false)
                      controller.removeProgressIndicator()
                      ReachabilityIndicatorManager.shared.presentIndicator(viewController: controller, isOffline: true)
                  }
              }
            
          },
          action2: {}
        )
      }
    }
  }

  /// Creates Eligibility Consent Task
  func createEligibilityConsentTask() {

    let taskViewController: ORKTaskViewController?

    //create orderedTask
    let consentTask: ORKOrderedTask? =
      ConsentBuilder.currentConsent?.createConsentTask()
      as! ORKOrderedTask?

    taskViewController = ORKTaskViewController(task: consentTask, taskRun: nil)

    taskViewController?.delegate = self
    taskViewController?.outputDirectory = FileManager.default.urls(
      for: .documentDirectory,
      in: .userDomainMask
    ).first!

    taskViewController?.navigationItem.title = nil

    UIView.appearance(whenContainedInInstancesOf: [ORKTaskViewController.self]).tintColor =
      kUIColorForSubmitButtonBackground

    var topVC = UIApplication.shared.keyWindow?.rootViewController
    // Fetching the current Visible Controller
    while topVC?.presentedViewController != nil {
      topVC = topVC?.presentedViewController
    }

    self.addAndRemoveProgress(add: false)
    // present consent task
    taskViewController?.navigationBar.prefersLargeTitles = false
    taskViewController?.modalPresentationStyle = .fullScreen
    topVC?.present(taskViewController!, animated: true, completion: nil)
  }

  /// Handler for local notification
  /// - Parameter userInfoDetails: Contains the info for notification
  func handleLocalNotification(userInfoDetails: [String: Any]) {
    UserDefaults.standard.set("900,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    var initialVC: UIViewController?

    // getting topmost visible controller
    let navigationController = self.window?.rootViewController as? UINavigationController
    let menuVC = navigationController?.viewControllers.last
    if let menuVC = menuVC as? FDASlideMenuViewController {
      let mainController = menuVC.mainViewController
      if mainController is UINavigationController {
        initialVC = (mainController as? UINavigationController)?.viewControllers.last
      }
    }

    NotificationHandler.instance.appOpenFromNotification = true
    NotificationHandler.instance.studyId = userInfoDetails[kStudyId] as? String ?? ""
    NotificationHandler.instance.activityId = userInfoDetails[kActivityId] as? String ?? ""

    if let dashboardTabBarVC = initialVC as? UITabBarController {
      dashboardTabBarVC.selectedIndex = 0  // Go to activities screen.
      if let activitiesVC =
        (dashboardTabBarVC.viewControllers?.first as? UINavigationController)?
        .topViewController as? ActivitiesViewController
      {
        activitiesVC.userDidNavigateFromNotification()
      }
    } else if let leftController =
      (menuVC as? FDASlideMenuViewController)?.leftViewController
      as? LeftMenuViewController
    {
      leftController.changeViewController(.studyList)
      leftController.createLeftmenuItems()
    }
    
    if let dashboardTabBar = initialVC as? UITabBarController {
      dashboardTabBar.selectedIndex = 2 // Go to resources screen.
      if let resourcesVC = (dashboardTabBar.viewControllers?.first as? UINavigationController)?.topViewController as? ResourcesViewController
      {
        resourcesVC.userDidNavigateFromNotification()
      }
    }

  }

  /// Handler for local & remote notification
  /// - Parameter userInfoDetails: contains the info for notification
  func handleLocalAndRemoteNotification(userInfoDetails: JSONDictionary?) {
    
    let studyId2 = userInfoDetails?[kStudyId] as? String ?? ""
    
    UserDefaults.standard.set("\(studyId2)", forKey: "newactivity1")
    
    let userInfoDetails2 = userInfoDetails
    
    UserDefaults.standard.set("\(userInfoDetails)", forKey: "newactivity2")
    UserDefaults.standard.synchronize()
    
    
    var initialVC: UIViewController?
    notificationDetails = nil//NEEEW
    NotificationHandler.instance.reset()
    UserDefaults.standard.set("30,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    print("30userInfoDetails---")
    if let dashboardTabBar = initialVC as? UITabBarController {
      UserDefaults.standard.set("31,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      print("31userInfoDetails---")
      dashboardTabBar.selectedIndex = 2 // Go to resources screen.
      if let resourcesVC = (dashboardTabBar.viewControllers?.first as? UINavigationController)?.topViewController as? ResourcesViewController
      {
        print("32userInfoDetails---")
        UserDefaults.standard.set("32,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        resourcesVC.userDidNavigateFromNotification()
      }
    }
    
    if let studyId = userInfoDetails?[kStudyId] as? String,
       !studyId.isEmpty
    {
      print("33userInfoDetails---")
      UserDefaults.standard.set("33,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      let notificationType = userInfoDetails![kNotificationType] as? String ?? ""
      let subType = AppNotification.NotificationSubType(rawValue: (userInfoDetails![kNotificationSubType] as? String ?? "")) ?? .announcement
      
      switch AppNotification.NotificationType(rawValue: notificationType) {
      case .gateway:
        print("34userInfoDetails---")
        UserDefaults.standard.set("34,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        hanldeGatewayNotificationType(userInfoDetails: userInfoDetails!, subType: subType)
        break
      case .study:
        print("35userInfoDetails---")
        UserDefaults.standard.set("35,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        handleStudyNotificationType(userInfoDetails: userInfoDetails!, subType: subType)
      default:
        print("36userInfoDetails---")
        UserDefaults.standard.set("36,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        print(notificationType)
        
      }
    }
  }
  
  private func handleStudyNotificationType(userInfoDetails: [String: Any], subType: AppNotification.NotificationSubType) {
    
    print("1userInfoDetails---\(userInfoDetails)")
    UserDefaults.standard.set("1,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    if let studyId = userInfoDetails[kStudyId] as? String,
       !studyId.isEmpty
    {
      print("2userInfoDetails---")
      UserDefaults.standard.set("2,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      var initialVC: UIViewController?
      
      // fetch the visible view controller
      let navigationController = self.window?.rootViewController as? UINavigationController
      let menuVC = navigationController?.viewControllers.last
      if menuVC is FDASlideMenuViewController {
        print("3userInfoDetails---")
        UserDefaults.standard.set("3,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        let mainController = (menuVC as? FDASlideMenuViewController)?
          .mainViewController
        if mainController is UINavigationController {
          let nav = mainController as? UINavigationController
          initialVC = nav?.viewControllers.last
          print("4userInfoDetails---")
          UserDefaults.standard.set("4,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
        }
      }
      // Handling Notifications based on SubType
      switch subType {
        
      case .study, .studyEvent:  // Study Notifications
        print("6userInfoDetails---")
        UserDefaults.standard.set("6,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        let leftController =
                  (menuVC as? FDASlideMenuViewController)?.leftViewController
                  as? LeftMenuViewController
        
        if (initialVC is StudyListViewController) {
          print("7userInfoDetails---")
          UserDefaults.standard.set("7,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          let val = userInfoDetails["message"] as? String ?? ""
          if val.containsIgnoringCase("has been paused") {
            print("8userInfoDetails---")
            UserDefaults.standard.set("8,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            UserDefaults.standard.set("paused", forKey: "pausedNotification")
            UserDefaults.standard.synchronize()
          }
            if val.containsIgnoringCase("has been resumed") && subType == .studyEvent {
                print("700userInfoDetails---")
                UserDefaults.standard.set("700,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
                UserDefaults.standard.synchronize()
                self.notificationDetails = nil
                leftController?.changeViewController(.studyList)
                leftController?.createLeftmenuItems()
                
            } else {
          (initialVC as? StudyListViewController)!.addRightNavigationItem()
            
            UserDefaults.standard.set("528,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            
          (initialVC as? StudyListViewController)!.performTaskBasedOnStudyStatus(studyID: studyId)
            }
        } else if !(initialVC is StudyListViewController) {
          print("9userInfoDetails---")
          UserDefaults.standard.set("9,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          if initialVC is ProfileViewController
              || initialVC
              is ReachoutOptionsViewController
              || initialVC is GatewayResourcesListViewController || initialVC is ActivitiesViewController || initialVC is ResourcesViewController ||
              initialVC is StudyDashboardViewController || initialVC is StudyDashboardTabbarViewController ||
              initialVC is NotificationViewController || initialVC is LeftMenuViewController
          {
            print("10userInfoDetails---")
            UserDefaults.standard.set("10,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            NotificationHandler.instance.appOpenFromNotification = true
            NotificationHandler.instance.studyId = studyId
            
            leftController?.changeViewController(.studyList)
            leftController?.createLeftmenuItems()
            
           }
        } else {
          print("11userInfoDetails---")
          UserDefaults.standard.set("11,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          NotificationHandler.instance.appOpenFromNotification = true
          NotificationHandler.instance.studyId = studyId
    
          
          leftController?.changeViewController(.studyList)
          leftController?.createLeftmenuItems()
        }
        
      case .activity:  // Activity Notifications
        print("12userInfoDetails---")
        UserDefaults.standard.set("12,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        NotificationHandler.instance.appOpenFromNotification = true
          if let initialVC1 = initialVC as? StudyListViewController {
              
              UserDefaults.standard.set("529,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
              UserDefaults.standard.synchronize()
              if let initialVC2 = (initialVC as? StudyListViewController)?.performTaskBasedOnStudyStatus(studyID: studyId) {
                  
                  
                  
                  print("13userInfoDetails---")
                  UserDefaults.standard.set("133,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
                  UserDefaults.standard.synchronize()
//                  (initialVC as? StudyListViewController)!.performTaskBasedOnStudyStatus(studyID: studyId)
                  
                  // push tabbar and switch to activty tab
                  if let initialVC = initialVC {
                      print("14userInfoDetails---")
                      UserDefaults.standard.set("134,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
                      UserDefaults.standard.synchronize()
                      self.pushToTabbar(
                        viewController: initialVC,
                        selectedTab: subType == .activity ? 0 : 2
                      )
                  }
              } else {
                  print("120userInfoDetails---")
                  UserDefaults.standard.set("135,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
                  
                  UserDefaults.standard.set("\(studyId) 0", forKey: "performTaskBasedOnStudyStatus")
                  UserDefaults.standard.synchronize()
                  
              }
          } else if initialVC is UITabBarController {
          print("15userInfoDetails---")
          UserDefaults.standard.set("15,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          // switch to activity tab
          (initialVC as? UITabBarController)?.selectedIndex =
          subType == .activity ? 0 : 2
        } else {
            print("110userInfoDetails---")
            UserDefaults.standard.set("136,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
                
                UserDefaults.standard.set("\(studyId)", forKey: "performTaskBasedOnStudyStatus")
            UserDefaults.standard.synchronize()
            // switch to activity tab
            (initialVC as? UITabBarController)?.selectedIndex =
            subType == .announcement ? 0 : 2
          }
        
      case .resource:
        print("16userInfoDetails---")
        UserDefaults.standard.set("16,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        if !(initialVC is UITabBarController) {
          print("17userInfoDetails---")
          UserDefaults.standard.set("17,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          if Gateway.instance.studies?.isEmpty == false {
            guard let study = Gateway.instance.studies?.filter({ $0.studyId == studyId })
                .first
            else {
              print("19userInfoDetails---")
              UserDefaults.standard.set("19,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
              UserDefaults.standard.synchronize()
              return }
            Study.updateCurrentStudy(study: study)
          }
          
          // push tabbar and switch to resource tab
          if let initialVC = initialVC {
            print("18userInfoDetails---")
            UserDefaults.standard.set("18,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            self.pushToTabbar(
              viewController: initialVC,
              selectedTab: 2
            )
          }
        }
        else {
          print("20userInfoDetails---")
          UserDefaults.standard.set("20,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          (initialVC as? UITabBarController)?.selectedIndex = 2
        }
        
      case .announcement:
        print("21userInfoDetails---")
        UserDefaults.standard.set("21,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        if let initialVC1 = initialVC as? StudyListViewController {
          print("22userInfoDetails---")
          UserDefaults.standard.set("22---\(initialVC),\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
            
            UserDefaults.standard.set("530,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            
            if let initialVC2 = (initialVC as? StudyListViewController)?.performTaskBasedOnStudyStatus(studyID: studyId) {
                
//          (initialVC as? StudyListViewController)!.performTaskBasedOnStudyStatus(studyID: studyId)
          print("81userInfoDetails---")
          UserDefaults.standard.set("81,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          // push tabbar and switch to activty tab
          if let initialVC = initialVC {
            print("23userInfoDetails---")
            UserDefaults.standard.set("23,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            self.pushToTabbar(
              viewController: initialVC,
              selectedTab: subType == .announcement ? 0 : 2
            )
          }
          print("80userInfoDetails---")
          UserDefaults.standard.set("80,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
                
            } else {
                print("120userInfoDetails---")
                UserDefaults.standard.set("121,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
                    
                    UserDefaults.standard.set("\(studyId) 0", forKey: "performTaskBasedOnStudyStatus")
                UserDefaults.standard.synchronize()
              
              }
        } else if initialVC is UITabBarController {
            print("24userInfoDetails---")
            UserDefaults.standard.set("24,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            // switch to activity tab
            (initialVC as? UITabBarController)?.selectedIndex =
            subType == .announcement ? 0 : 2
          }
          else {
          print("110userInfoDetails---")
          UserDefaults.standard.set("111,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
              
              UserDefaults.standard.set("\(studyId)", forKey: "performTaskBasedOnStudyStatus")
          UserDefaults.standard.synchronize()
          // switch to activity tab
          (initialVC as? UITabBarController)?.selectedIndex =
          subType == .announcement ? 0 : 2
        }
      }
    }
    print("25userInfoDetails---")
    UserDefaults.standard.set("25,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
  }
  
  private func hanldeGatewayNotificationType(userInfoDetails: [String: Any], subType: AppNotification.NotificationSubType) {
    print("40userInfoDetails---")
    UserDefaults.standard.set("40,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    if let studyId = userInfoDetails[kStudyId] as? String,
       !studyId.isEmpty
    {
      print("41userInfoDetails---")
      UserDefaults.standard.set("41,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      var initialVC: UIViewController?
      
      // fetch the visible view controller
      let navigationController = self.window?.rootViewController as? UINavigationController
      let menuVC = navigationController?.viewControllers.last
      if menuVC is FDASlideMenuViewController {
        print("42userInfoDetails---")
        UserDefaults.standard.set("42,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        let mainController = (menuVC as? FDASlideMenuViewController)?
          .mainViewController
        if mainController is UINavigationController {
          print("43userInfoDetails---")
          UserDefaults.standard.set("43,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          let nav = mainController as? UINavigationController
          initialVC = nav?.viewControllers.last
        }
      }
      // Handling Notifications based on SubType
      switch subType {
        
      case .study, .studyEvent:  // Study Notifications
        print("44userInfoDetails---")
        UserDefaults.standard.set("44,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        let leftController =
                  (menuVC as? FDASlideMenuViewController)?.leftViewController
                  as? LeftMenuViewController
        
        if (initialVC is StudyListViewController) {
          print("45userInfoDetails---")
          UserDefaults.standard.set("45,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          (initialVC as? StudyListViewController)!.addRightNavigationItem()
            UserDefaults.standard.set("531,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
          (initialVC as? StudyListViewController)!.performTaskBasedOnStudyStatus(studyID: studyId)
        } else if !(initialVC is StudyListViewController) {
          print("46userInfoDetails---")
          UserDefaults.standard.set("46,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          if initialVC is ProfileViewController || initialVC is ReachoutOptionsViewController || initialVC is GatewayResourcesListViewController ||
              initialVC is ActivitiesViewController || initialVC is ResourcesViewController ||
              initialVC is StudyDashboardViewController || initialVC is StudyDashboardTabbarViewController ||
              initialVC is NotificationViewController || initialVC is LeftMenuViewController
          {
            print("47userInfoDetails---")
            UserDefaults.standard.set("47,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            NotificationHandler.instance.appOpenFromNotification = true
            NotificationHandler.instance.studyId = studyId
            
            leftController?.changeViewController(.studyList)
            leftController?.createLeftmenuItems()

          }
        } else {
          print("48userInfoDetails---")
          UserDefaults.standard.set("48,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          NotificationHandler.instance.appOpenFromNotification = true
          NotificationHandler.instance.studyId = studyId
          
          leftController?.changeViewController(.studyList)
          leftController?.createLeftmenuItems()
        }
        
      case .activity:  // Activity Notifications
        print("49userInfoDetails---")
        UserDefaults.standard.set("49,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        if !(initialVC is UITabBarController) {
          print("50userInfoDetails---")
          UserDefaults.standard.set("50,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
            
            UserDefaults.standard.set("532,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
          (initialVC as? StudyListViewController)!.performTaskBasedOnStudyStatus(studyID: studyId)
        
          // push tabbar and switch to activty tab
          if let initialVC = initialVC {
            print("51userInfoDetails---")
            UserDefaults.standard.set("51,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            self.pushToTabbar(
              viewController: initialVC,
              selectedTab: subType == .activity ? 0 : 2
            )
          }
        } else {
          print("52userInfoDetails---")
          UserDefaults.standard.set("52,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          (initialVC as? UITabBarController)?.selectedIndex =
          subType == .activity ? 0 : 2
        }
        
      case .resource:  // Resource Notifications
        print("53userInfoDetails---")
        UserDefaults.standard.set("53,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        if !(initialVC is UITabBarController) {
          print("54userInfoDetails---")
          UserDefaults.standard.set("54,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          if Gateway.instance.studies?.isEmpty == false {
            print("55userInfoDetails---")
            UserDefaults.standard.set("55,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            guard let study = Gateway.instance.studies?.filter({ $0.studyId == studyId })
                    .first
            else {
              print("56userInfoDetails---")
              UserDefaults.standard.set("56,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
              UserDefaults.standard.synchronize()
              return }
            print("57userInfoDetails---")
            UserDefaults.standard.set("57,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            Study.updateCurrentStudy(study: study)
          }
          
          // push tabbar and switch to resource tab
          if let initialVC = initialVC {
            print("58userInfoDetails---")
            UserDefaults.standard.set("58,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            self.pushToTabbar(
              viewController: initialVC,
              selectedTab: 2
            )
          }
        } else {
          print("59userInfoDetails---")
          UserDefaults.standard.set("59,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          (initialVC as? UITabBarController)?.selectedIndex = 2
        }
        
      case .announcement:
        print("60userInfoDetails---")
        UserDefaults.standard.set("60,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        if !(initialVC is UITabBarController) {
          print("61userInfoDetails---")
          UserDefaults.standard.set("61,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
            
            UserDefaults.standard.set("533,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
          (initialVC as? StudyListViewController)!.performTaskBasedOnStudyStatus(studyID: studyId)
          
          // push tabbar and switch to activty tab
          if let initialVC = initialVC {
            print("62userInfoDetails---")
            UserDefaults.standard.set("62,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            self.pushToTabbar(
              viewController: initialVC,
              selectedTab: subType == .announcement ? 0 : 2
            )
          }
        } else {
          print("63userInfoDetails---")
          UserDefaults.standard.set("63,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          (initialVC as? UITabBarController)?.selectedIndex =
          subType == .announcement ? 0 : 2
        }
      }
    }
  }
  
  func navigateToStudyHome(viewController: UIViewController, studyID: String? = nil) {
    print("70userInfoDetails---")
    UserDefaults.standard.set("70,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    let studyStoryBoard = UIStoryboard(name: kStudyStoryboard, bundle: Bundle.main)
    let studyHomeController =
      (studyStoryBoard.instantiateViewController(
        withIdentifier: String(describing: StudyHomeViewController.classForCoder())
      )
      as? StudyHomeViewController)!
    viewController.navigationController?.pushViewController(studyHomeController, animated: true)
  }

  /// Push to tabbar Controller with tabs Activity, Dashboard & Resource
  /// - Parameters:
  ///   - viewController: Instance of `UIViewController`
  ///   - selectedTab: Selected tab in form of `Int`
  func pushToTabbar(viewController: UIViewController, selectedTab: Int, studyID: String? = nil) {
    DispatchQueue.main.async {
    let studyStoryBoard = UIStoryboard.init(name: kStudyStoryboard, bundle: Bundle.main)

    let studyDashboard =
      (studyStoryBoard.instantiateViewController(
        withIdentifier: kStudyDashboardTabbarControllerIdentifier
      )
      as? StudyDashboardTabbarViewController)!

    studyDashboard.selectedIndex = selectedTab
    viewController.navigationController?.navigationBar.isHidden = true
    viewController.navigationController?.pushViewController(studyDashboard, animated: true)
    }
  }

  /// Verifies passcode if enabled or set passcode
  /// - Parameter viewController: Instance of `UIViewController`
  func checkPasscode(viewController: UIViewController) {

    guard
      ((viewController.presentedViewController as? ORKTaskViewController)?
        .currentStepViewController)?.step?.identifier
        != kPasscodeStepIdentifier
    else { return }  // If already presented. Return.
    if User.currentUser.userType == .loggedInUser {  // FDA user

      if User.currentUser.settings?.passcode! == true {
        // Passcode already exist
        if ORKPasscodeViewController.isPasscodeStoredInKeychain() == false {
          let passcodeStep = ORKPasscodeStep(identifier: kPasscodeStepIdentifier)
          passcodeStep.passcodeType = .type4Digit
          passcodeStep.text = kSetPasscodeDescription
          let task = ORKOrderedTask(
            identifier: kPasscodeTaskIdentifier,
            steps: [passcodeStep]
          )
          let taskViewController = ORKTaskViewController(task: task, taskRun: nil)

          if viewController.isKind(of: UINavigationController.self) {
            taskViewController.delegate = self

          } else {
            taskViewController.delegate =
              viewController
              as? ORKTaskViewControllerDelegate
          }
          taskViewController.isNavigationBarHidden = true
          passcodeParentControllerWhileSetup = viewController
          isPasscodePresented = true
          blockerScreen?.isHidden = true
          taskViewController.navigationBar.prefersLargeTitles = false
          taskViewController.modalPresentationStyle = .fullScreen
          viewController.present(taskViewController, animated: false, completion: nil)

        } else {
          guard
            ORKPasscodeViewController.isPasscodeStoredInKeychain()
              && !(containerViewController?.presentedViewController
                is ORKPasscodeViewController)
          else { return }
          window?.makeKeyAndVisible()

          let passcodeViewController =
            ORKPasscodeViewController
            .passcodeAuthenticationViewController(withText: "\n", delegate: self)
          var topVC = UIApplication.shared.keyWindow?.rootViewController
          var parentController: UIViewController?
          passcodeViewController.setNavigationBarHidden(true, animated: false)
          while topVC?.presentedViewController != nil {
            parentController = topVC
            topVC = topVC?.presentedViewController
          }

          if topVC is UIAlertController {  // topmost Visible Controller is AlertController
            alertVCPresented = (topVC as? UIAlertController)!

            if (parentController is ORKPasscodeViewController) == false {
              topVC?.dismiss(animated: true, completion: nil)
            }
            topVC = parentController
            parentViewControllerForAlert = topVC
          }
          passcodeParentControllerWhileSetup = nil

          // PasscodeController or TaskViewController is not presented
          if (topVC?.presentedViewController?.isKind(of: ORKPasscodeViewController.self)
            == false
            && (topVC?.presentedViewController?.isKind(
              of: ORKTaskViewController.self
            ))!)
            || (topVC != nil && topVC?.isKind(of: ORKPasscodeViewController.self) == false)
          {

            isPasscodePresented = true
            blockerScreen?.isHidden = true

            if isComprehensionFailed! {
              self.retryView?.isHidden = true
            }
            passcodeViewController.navigationBar.prefersLargeTitles = false
            passcodeViewController.modalPresentationStyle = .fullScreen
            topVC!.present(passcodeViewController, animated: false, completion: nil)
          }
        }
      }
    }
  }

  /// Adds and remove indicator on `UIWindow`
  /// - Parameter add: `Boolean` state to show/hide
  func addAndRemoveProgress(add: Bool) {

    if add {
      self.window?.addProgressIndicatorOnWindow()
    } else {
      self.window?.removeProgressIndicatorFromWindow()
    }
  }

  /// get study updates if exist
  func checkForStudyUpdates() {

    if Study.currentStudy != nil
      && Study.currentStudy?.userParticipateState.status
        == UserStudyStatus.StudyStatus.enrolled
    {

      let userStudyStatus = (Study.currentStudy?.userParticipateState.status)!

      if userStudyStatus == .enrolled || userStudyStatus == .yetToEnroll {
        WCPServices().getStudyUpdates(study: Study.currentStudy!, delegate: self)
      }
    }
  }

  /// Handler for User Signout response, resets all user related data from local database
  func handleSignoutResponse() {
    if ORKPasscodeViewController.isPasscodeStoredInKeychain() {
      ORKPasscodeViewController.removePasscodeFromKeychain()
    }

    // Update User Defaults
    let ud = UserDefaults.standard
    ud.set(true, forKey: kPasscodeIsPending)
    ud.set(false, forKey: kShowNotification)
    ud.synchronize()

    // Update Key & InitializationVector
    self.updateKeyAndInitializationVector()

    let navigationController = (self.window?.rootViewController as? UINavigationController)!

    if navigationController.viewControllers.count > 0 {
      let slideMenuController =
        navigationController.viewControllers.last
        as? FDASlideMenuViewController

      // Remove progress
      self.addAndRemoveProgress(add: false)

      if slideMenuController != nil {
        User.resetCurrentUser()
        let ud = UserDefaults.standard
        ud.removeObject(forKey: kUserAuthToken)
        ud.removeObject(forKey: kUserId)
        ud.synchronize()
        // Navigate to StudyHome
        slideMenuController?.fdaSlideMenuController()?.navigateToHomeAfterSignout()
      }
    }
  }

  /// Handler for updating User defaults
  func handleSignoutAfterLogoutResponse() {
    if ORKPasscodeViewController.isPasscodeStoredInKeychain() {
      ORKPasscodeViewController.removePasscodeFromKeychain()
    }
    let ud = UserDefaults.standard
    ud.set(true, forKey: kPasscodeIsPending)
    ud.set(false, forKey: kShowNotification)
    ud.synchronize()

    let navigationController = (self.window?.rootViewController as? UINavigationController)!

    // fetch the visible view controller
    if navigationController.viewControllers.count > 0 {
      let slideMenuController =
        (navigationController.viewControllers.last as? FDASlideMenuViewController)!

      if !Utilities.isStandaloneApp() {
        self.addAndRemoveProgress(add: false)
        let leftController = (slideMenuController.leftViewController as? LeftMenuViewController)!
        leftController.changeViewController(.reachOutSignIn)
        leftController.createLeftmenuItems()
        
      } else {
        UIApplication.shared.keyWindow?.removeProgressIndicatorFromWindow()
        navigationController.popToRootViewController(animated: true)
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
          UIApplication.shared.keyWindow?.removeProgressIndicatorFromWindow()
        }
      }
    }
  }

  /// Checks for `StudyListViewController` and adds right navigation item
  func updateNotification(userInfoDetails: [String:Any]?) {
      
      UserDefaults.standard.set("223,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      
    let ud = UserDefaults.standard
    ud.set(true, forKey: kShowNotification)
    ud.synchronize()
    var nav: UINavigationController?
    // fetch the visible view controller
    guard let navigationController = self.window?.rootViewController as? UINavigationController else {
      return
    }

    let menuVC = navigationController.viewControllers.last

    if menuVC is FDASlideMenuViewController {
      let mainController = (menuVC as? FDASlideMenuViewController)!.mainViewController

      if mainController is UINavigationController {
        nav = (mainController as? UINavigationController)!
        let studyListVC = nav?.viewControllers.last
        if studyListVC is StudyListViewController {
          (studyListVC as? StudyListViewController)!.addRightNavigationItem()
          if let studyId = userInfoDetails?[kStudyId] as? String,
             !studyId.isEmpty
          {
            let notificationType = userInfoDetails![kNotificationType] as? String ?? ""
            
            let subType = AppNotification.NotificationSubType(rawValue: (userInfoDetails![kNotificationSubType] as? String ?? "")) ?? .announcement
            
            switch AppNotification.NotificationType(rawValue: notificationType) {
            case .gateway:
              hanldeGatewayNotificationType(userInfoDetails: userInfoDetails!, subType: subType)
              break
            case .study:
              handleStudyNotificationType(userInfoDetails: userInfoDetails!, subType: subType)
            default:
              print(notificationType)
              
            }
          }
        }
      }
    }
  }

  func updateActiveNotification() {
    let ud = UserDefaults.standard
    ud.set(true, forKey: kShowNotification)
    ud.synchronize()
    var nav: UINavigationController?
    // fetch the visible view controller
    guard let navigationController = self.window?.rootViewController as? UINavigationController else {
      return
    }
    
    let menuVC = navigationController.viewControllers.last
    
    if menuVC is FDASlideMenuViewController {
      let mainController = (menuVC as? FDASlideMenuViewController)!.mainViewController
      
      if mainController is UINavigationController {
        nav = (mainController as? UINavigationController)!
        let studyListVC = nav?.viewControllers.last
        if studyListVC is StudyListViewController {
          (studyListVC as? StudyListViewController)!.addRightNavigationItem()
          
        }
      }
    }
  }

  private func refreshStudyActivitiesState(with userInfo: JSONDictionary) {
    guard let currentStudyID = Study.currentStudy?.studyId,
      let studyID = userInfo["studyId"] as? String,
      currentStudyID == studyID
    else { return }
    DispatchQueue.main.async {
      NotificationCenter.default.post(name: kRefreshActivities, object: nil)
    }
  }

  /// Handler for Study Update Info
  func handleStudyUpdatedInformation() {

    if Study.currentStudy != nil {

      Study.currentStudy?.newVersion = StudyUpdates.studyVersion
      if Study.currentStudy?.version == Study.currentStudy?.newVersion {
        return

      } else {
        DBHandler.updateMetaDataToUpdateForStudy(
          study: Study.currentStudy!,
          updateDetails: nil
        )

        // fetch the visible view controller
        var nav: UINavigationController?
        let navigationController = (self.window?.rootViewController as? UINavigationController)!
        let menuVC = navigationController.viewControllers.last

        if menuVC is FDASlideMenuViewController {
          let mainController = (menuVC as? FDASlideMenuViewController)!.mainViewController

          if mainController is UINavigationController {
            nav = (mainController as? UINavigationController)!
            let tabbarVC = nav?.viewControllers.last

            if tabbarVC is StudyDashboardTabbarViewController {
              let studyTabBar = (tabbarVC as? StudyDashboardTabbarViewController)!
              // Storing selected tabbar controller
              selectedController =
                ((studyTabBar.viewControllers?[studyTabBar.selectedIndex])
                as? UINavigationController)!.viewControllers.last
            }
          }
        }

        let studyStatus = StudyStatus(rawValue: StudyUpdates.studyStatus!)!

        if studyStatus != .active {  // Study is Active
          _ = nav?.popToRootViewController(animated: true)
          var message = ""
          switch studyStatus {

          case .paused:
            message = NSLocalizedString(kMessageForStudyPausedState, comment: "")

          case .closed:
            message = NSLocalizedString(kMessageForStudyClosedState, comment: "")

          default: break
          }

          let alert = UIAlertController(
            title: "" as String,
            message: message as String,
            preferredStyle: UIAlertController.Style.alert
          )
          alert.addAction(
            UIAlertAction(
              title: NSLocalizedString("OK", comment: ""),
              style: .default,
              handler: nil
            )
          )
        } else {

          if selectedController != nil {

            if !self.isPasscodePresented! {
              // Check for Consent Updated
              self.checkConsentStatus(controller: self.selectedController!)
            }

            if self.selectedController is ActivitiesViewController {
              (self.selectedController as? ActivitiesViewController)!
                .checkForActivitiesUpdates()

            } else if self.selectedController is ResourcesViewController {
              (self.selectedController as? ResourcesViewController)!
                .checkForResourceUpdate()
            }
          }
        }
      }
    }
  }

  /// Webservice call to update consent status to server
  @objc func updateEligibilityConsentStatus() {

    let notificationName = Notification.Name(kPDFCreationNotificationId)

    // Stop listening notification
    NotificationCenter.default.removeObserver(self, name: notificationName, object: nil)

    // Update Consent status to Server
    ConsentServices().updateUserEligibilityConsentStatus(
      eligibilityStatus: true,
      consentStatus: (ConsentBuilder.currentConsent?.consentStatus)!,
      delegate: self
    )
  }

  func popViewControllerAfterConsentDisagree() {

    if self.selectedController is StudyDashboardViewController {
      (self.selectedController as? StudyDashboardViewController)!.homeButtonAction(UIButton())

    } else if self.selectedController is ActivitiesViewController {
      (self.selectedController as? ActivitiesViewController)!.homeButtonAction(UIButton())

    } else if self.selectedController is ResourcesViewController {
      (self.selectedController as? ResourcesViewController)!.homeButtonAction(UIButton())
    }
  }

  @objc func dismissTaskViewController() {
    passcodeParentControllerWhileSetup?.dismiss(animated: true, completion: nil)
    passcodeParentControllerWhileSetup = nil
  }

  // MARK: - Consent Handlers
  func studyEnrollmentFinished() {
    NotificationCenter.default.post(
      name: NSNotification.Name(rawValue: "NotificationStudyEnrollmentCompleted"),
      object: nil
    )
  }

  /// Saves Consent to `Study` and DB, Updates consent status to server
  /// - Parameter taskViewController: <#taskViewController description#>
  func studyEnrollmentStarted(taskViewController: ORKTaskViewController) {

    // Saving Consent Document
    ConsentBuilder.currentConsent?.consentResult?.consentDocument =
      ConsentBuilder
      .currentConsent?
      .consentDocument
    ConsentBuilder.currentConsent?.consentResult?.initWithORKTaskResult(
      taskResult: taskViewController.result
    )

    // Save consent to study
    Study.currentStudy?.signedConsentVersion = ConsentBuilder.currentConsent?.version!
    Study.currentStudy?.signedConsentFilePath = ConsentBuilder.currentConsent?.consentResult?
      .consentPath!

    // Save also in DB
    DBHandler.saveConsentInformation(study: Study.currentStudy!)

    // Update consent is updaeted in db
    Study.currentStudy?.version = StudyUpdates.studyVersion
    Study.currentStudy?.newVersion = StudyUpdates.studyVersion
    StudyUpdates.studyConsentUpdated = false
    StudyUpdates.studyEnrollAgain = false
    DBHandler.updateMetaDataToUpdateForStudy(study: Study.currentStudy!, updateDetails: nil)

    if self.isComprehensionFailed! {
      self.isComprehensionFailed = false
    }

    ConsentBuilder.currentConsent?.consentStatus = .completed

    if ConsentBuilder.currentConsent?.consentResult?.consentPdfData?.count == 0 {

      DispatchQueue.main.asyncAfter(deadline: .now() + 3) {
        self.updateEligibilityConsentStatus()
      }

    } else {
      // Update Consent Status to server
      ConsentServices().updateUserEligibilityConsentStatus(
        eligibilityStatus: true,
        consentStatus: (ConsentBuilder.currentConsent?.consentStatus)!,
        delegate: self
      )
    }
  }

}

// MARK: - Handle network responses
extension AppDelegate {

  /// Handle App update
  private func handleAppUpdateResponse() {
    blockerScreen?.isHidden = true
    blockerScreen?.removeFromSuperview()
    if let latestVersion = UserManageApps.appDetails?.latestVersion,
       let isForceUpdate = UserManageApps.appDetails?.isForceUpdate {
      let appVersion = Utilities.getAppVersion()
      guard var isForceUpdate = Bool(isForceUpdate) else { return }
      
      let ud = UserDefaults.standard
      let valFromSplash = ud.value(forKey: kFromSplashScreen) as? Bool ?? false
      let valFromBackground = ud.value(forKey: kFromBackground) as? Int ?? 0
      let valIsStudylistGeneral = ud.value(forKey: kIsStudylistGeneral) as? Bool ?? false
      
      if valFromSplash {
        isForceUpdate = true
      } else if valFromBackground < Upgrade.fromSplash.rawValue {
        if !isForceUpdate {
          ud.set(Upgrade.optionalShown.rawValue, forKey: kFromBackground)
          ud.synchronize()
        }
        isForceUpdate = true
        ud.set(true, forKey: kIsShowUpdateAppVersion)
        ud.synchronize()
      } else if valIsStudylistGeneral && valFromBackground >= Upgrade.pendingUpdate.rawValue {
        isForceUpdate = true
        ud.set(Upgrade.optionalShown.rawValue, forKey: kFromBackground)
        ud.set(true, forKey: kIsShowUpdateAppVersion)
        ud.synchronize()
      } else {
        let ud = UserDefaults.standard
        var valFromBackground = ud.value(forKey: kFromBackground) as? Int ?? 0
        valFromBackground += 1
        ud.set(valFromBackground, forKey: kFromBackground)
        ud.synchronize()
        
        isForceUpdate = false
        blockerScreen?.isHidden = true
        blockerScreen?.removeFromSuperview()
      }
      ud.set(false, forKey: kFromSplashScreen)
      ud.synchronize()
      
      if appVersion != latestVersion,
         latestVersion.compare(appVersion, options: .numeric, range: nil, locale: nil)
          == ComparisonResult.orderedDescending, isForceUpdate {
        if let windowBounds = UIApplication.shared.keyWindow?.bounds {
          // load and Update blockerScreen
          self.shouldAddForceUpgradeScreen = true
          let blockerView = AppUpdateBlocker.instanceFromNib(frame: windowBounds, detail: [:])
          self.blockerScreen = blockerView
          self.blockerScreen?.configureView(with: latestVersion)
          if User.currentUser.userType == .loggedInUser {
            if User.currentUser.settings?.passcode == false {
              UIApplication.shared.keyWindow?.addSubview(blockerView)
            } else {
              UIApplication.shared.keyWindow?.addSubview(blockerView)
              blockerView.isHidden = true
            }
          } else {
            UIApplication.shared.keyWindow?.addSubview(blockerView)
          }
        }
      }
    }
  }
  
  /// Handle App update
  func showAppVersionUpdate() {
    let ud = UserDefaults.standard
    
    ud.set(Upgrade.optionalShown.rawValue, forKey: kFromBackground)
    ud.set(true, forKey: kIsShowUpdateAppVersion)
    ud.synchronize()
    if let latestVersion = UserManageApps.appDetails?.latestVersion,
       (UserManageApps.appDetails?.isForceUpdate) != nil {
      let appVersion = Utilities.getAppVersion()
      
      if appVersion != latestVersion,
         latestVersion.compare(appVersion, options: .numeric, range: nil, locale: nil)
          == ComparisonResult.orderedDescending {
        if let windowBounds = UIApplication.shared.keyWindow?.bounds {
          // load and Update blockerScreen
          self.shouldAddForceUpgradeScreen = true
          let blockerView = AppUpdateBlocker.instanceFromNib(frame: windowBounds, detail: [:])
          self.blockerScreen = blockerView
          self.blockerScreen?.configureView(with: latestVersion)
          if User.currentUser.userType == .loggedInUser {
            blockerScreen?.isHidden = false
            UIApplication.shared.keyWindow?.addSubview(blockerView)
          } else {
            blockerScreen?.isHidden = false
            UIApplication.shared.keyWindow?.addSubview(blockerView)
          }
        }
      }
    }
  }
  
}

// MARK: Webservices delegates

extension AppDelegate: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
      if requestName as String == WCPMethods.eligibilityConsent.method.methodName {
          self.addAndRemoveProgress(add: true)
      }
  }
  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    if requestName as String == WCPMethods.eligibilityConsent.method.methodName {
      self.addAndRemoveProgress(add: false)
      self.createEligibilityConsentTask()

    } else if requestName as String
      == ConsentServerMethods.updateEligibilityConsentStatus.method
      .methodName {
      self.addAndRemoveProgress(add: false)
      self.studyEnrollmentFinished()
      if let currentStudy = Study.currentStudy {
        currentStudy.version = currentStudy.newVersion
      }
    } else if requestName as String == WCPMethods.studyUpdates.rawValue {
      self.handleStudyUpdatedInformation()

    } else if requestName as String == RegistrationMethods.updateUserProfile.description {

      if iscomingFromForgotPasscode {
        self.sendRequestToSignOut()
        return
      }
      let ud = UserDefaults.standard
      ud.set(false, forKey: kNotificationRegistrationIsPending)
      ud.synchronize()
    } else if requestName as String ==  RegistrationMethods.apps.description {
      handleAppUpdateResponse()
      self.addAndRemoveProgress(add: false)
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    // Remove Progress
    self.addAndRemoveProgress(add: false)
    if requestName as String == WCPMethods.eligibilityConsent.method.methodName {
      self.popViewControllerAfterConsentDisagree()
    } else if requestName as String ==  RegistrationMethods.apps.description {
      let ud = UserDefaults.standard
      ud.set(false, forKey: kFromSplashScreen)
      ud.set(Upgrade.fromSplash.rawValue, forKey: kFromBackground)
      ud.synchronize()
    }
  }
}

// MARK: - ORKTaskViewController Delegate
extension AppDelegate: ORKTaskViewControllerDelegate {

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

    case ORKTaskViewControllerFinishReason.completed:
      if !(taskViewController.task?.identifier == kConsentTaskIdentifier) {  // other surveys/Active tasks/ Passcode
        let ud = UserDefaults.standard
        ud.set(true, forKey: kPasscodeIsPending)
        ud.synchronize()
        self.appIsResignedButDidNotEnteredBackground = false
      }

    case ORKTaskViewControllerFinishReason.failed: break

    case ORKTaskViewControllerFinishReason.discarded:
      if taskViewController.task?.identifier == kConsentTaskIdentifier {
        self.popViewControllerAfterConsentDisagree()
      }
      if self.isComprehensionFailed! {
        self.isComprehensionFailed = false
      }

    case ORKTaskViewControllerFinishReason.saved:
      if taskViewController.task?.identifier == kConsentTaskIdentifier {
        self.popViewControllerAfterConsentDisagree()
      }

    @unknown default:
      break
    }

    if passcodeParentControllerWhileSetup != nil {

      // Adding delay to allow Keypad to dismiss
      if #available(iOS 10.3.0, *) {
        self.perform(#selector(dismissTaskViewController), with: self, afterDelay: 2)
      } else {
        self.dismissTaskViewController()
      }
    } else {
      taskViewController.dismiss(animated: true, completion: nil)
    }

    if taskViewController.task?.identifier == kConsentTaskIdentifier
      && reason
        == ORKTaskViewControllerFinishReason.completed
    {

    }
  }

  func taskViewController(
    _ taskViewController: ORKTaskViewController,
    stepViewControllerWillAppear stepViewController: ORKStepViewController
  ) {

    if taskViewController.task?.identifier == kConsentTaskIdentifier {

      if (taskViewController.result.results?.count)! > 1 {

        if activityBuilder?.actvityResult?.result?.count
          == taskViewController.result
          .results?.count
        {
          // Removing the dummy result:Currentstep result which not presented yet
          activityBuilder?.actvityResult?.result?.removeLast()
        }
      }

      // Handling show and hide of Back Button
      // For Verified Step, Completion Step, Visual Step, Review Step, Share Pdf Step
      if stepViewController.step?.identifier == kConsentCompletionStepIdentifier
        || stepViewController.step?.identifier == kVisualStepId
        || stepViewController.step?.identifier == kConsentSharePdfCompletionStep
        || stepViewController.step?.identifier == kEligibilityVerifiedScreen
      {
        if stepViewController.step?.identifier == kEligibilityVerifiedScreen {
          stepViewController.continueButtonTitle = kContinueButtonTitle
        }
        stepViewController.backButtonItem = nil
      }  // Checking if currentstep is View Pdf Step
      else if stepViewController.step?.identifier == kConsentViewPdfCompletionStep {

        // Back button is enabled
        stepViewController.backButtonItem = nil

        let orkStepResult: ORKStepResult? =
          taskViewController.result.results?[
            (taskViewController.result.results?.count)! - 2
          ] as! ORKStepResult?

        let consentSignatureResult: ConsentCompletionTaskResult? =
          orkStepResult?.results?
          .first
          as? ConsentCompletionTaskResult

        // Checking if Signature is consented after Review Step
        if consentSignatureResult?.didTapOnViewPdf == false {
          // Directly moving to completion step by skipping Intermediate PDF viewer screen
          stepViewController.goForward()
        }
      } else {
        if taskViewController.task?.identifier == "ConsentTask" {
          stepViewController.backButtonItem = nil
        } else {
          stepViewController.backButtonItem?.isEnabled = true
        }
      }
    }
  }
    func taskViewController(_ taskViewController: ORKTaskViewController, willChange result: ORKTaskResult) {
        if let identifier =
            taskViewController.currentStepViewController?.step?.identifier {
            do {
                self.reachability = try Reachability()
              } catch(let error) { }
            if reachability.connection == .unavailable && identifier == "Review" {
                taskViewController.view.hideAllToasts()
                UIUtilities.showAlertMessageWithActionHandler(
                  "You are offline",
                  message:
                    "You may require internet connection to move forward with this flow. Kindly check the internet and try enrolling again later.",
                  buttonTitle: kTitleOk,
                  viewControllerUsed: taskViewController,
                  action: {
                      taskViewController.dismiss(
                        animated: true,
                        completion: nil
                      )
                  }
                )
            }
        }
    }
    func taskViewController(_ taskViewController: ORKTaskViewController, didChange result: ORKTaskResult) {
        print("---------Result change result")
//        if let identifier =
      //taskViewController.currentStepViewController?.step?.identifier {
            do {
                self.reachability = try Reachability()
              } catch(let error) { }
            if reachability.connection == .unavailable {
                taskViewController.view.hideAllToasts()
                UIUtilities.showAlertMessageWithActionHandler(
                  "You are offline",
                  message:
                    "You may require internet connection to move forward with this flow. Kindly check the internet and try enrolling again later.",
                  buttonTitle: kTitleOk,
                  viewControllerUsed: taskViewController,
                  action: {
                      taskViewController.dismiss(
                        animated: true,
                        completion: nil
                      )
                  }
                )
            }
//        }
    }
  // MARK: - StepViewController Delegate

    public func stepViewController(
      stepViewController: ORKStepViewController,
      didFinishWith _: ORKStepViewControllerNavigationDirection
    ) {

        print("\n---------step navigation next button")
        if reachability.connection == .unavailable {

            stepViewController.view.hideAllToasts()
            UIUtilities.showAlertMessageWithActionHandler(
              "You are offline",
              message: "You may require internet connection to move forward with this flow. Kindly check the internet and try enrolling again later.",
              buttonTitle: kTitleOk,
              viewControllerUsed: stepViewController,
              action: {
                  stepViewController.dismiss(
                    animated: true,
                    completion: nil
                  )
              }
            )

            
        }
    }
  func taskViewController(_ taskViewController: ORKTaskViewController, stepViewControllerWillDisappear stepViewController: ORKStepViewController,
                          navigationDirection direction: ORKStepViewControllerNavigationDirection) {
      if reachability.connection == .unavailable {
          taskViewController.view.hideAllToasts()
          UIUtilities.showAlertMessageWithActionHandler(
            "You are offline",
            message: "You may require internet connection to move forward with this flow. Kindly check the internet and try enrolling again later.",
            buttonTitle: kTitleOk,
            viewControllerUsed: taskViewController,
            action: {
              taskViewController.dismiss(
                animated: true,
                completion: nil
                )
            }
          )
      }
  }
  public func stepViewControllerResultDidChange(_ stepViewController: ORKStepViewController) {
  }

  public func stepViewControllerDidFail(
    _ stepViewController: ORKStepViewController,
    withError error: Error?
  ) {}

  func taskViewController(
    _ taskViewController: ORKTaskViewController,
    viewControllerFor step: ORKStep
  ) -> ORKStepViewController? {

    if taskViewController.task?.identifier == kConsentTaskIdentifier {
        
        do {
            self.reachability = try Reachability()
          } catch(let error) { }
        if reachability.connection == .unavailable {
            taskViewController.view.hideAllToasts()
            UIUtilities.showAlertMessageWithActionHandler(
              "You are offline",
              message: "You may require internet connection to move forward with this flow. Kindly check the internet and try enrolling again later.",
              buttonTitle: kTitleOk,
              viewControllerUsed: taskViewController,
              action: {
                  taskViewController.dismiss(
                    animated: true,
                    completion: nil
                  )
              }
            )
        }
      // CurrentStep is TokenStep
      if step.identifier != kEligibilityTokenStep
        && step.identifier
          != kConsentSharePdfCompletionStep
        && step.identifier
          != kConsentViewPdfCompletionStep
        && step.identifier != kComprehensionCompletionStepIdentifier
        && step.identifier
          != kReviewTitle
      {

        return nil

      } else if step.identifier == kEligibilityTokenStep {  // For EligibilityTokenStep

        let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)

        let ttController =
          (gatewayStoryboard.instantiateViewController(
            withIdentifier: kEligibilityStepViewControllerIdentifier
          )
          as? EligibilityStepViewController)!
        ttController.descriptionText = step.text
        ttController.step = step

        return ttController

      } else if step.identifier == kConsentSharePdfCompletionStep {  // For ConsentShareCompletion Step

        var totalResults = taskViewController.result.results
        let reviewStep: ORKStepResult?

        totalResults = totalResults?.filter({ $0.identifier == kReviewTitle })

        reviewStep = (totalResults?.first as? ORKStepResult)!

        if (reviewStep?.identifier)! == kReviewTitle && (reviewStep?.results?.count)! > 0 {
          let consentSignatureResult: ORKConsentSignatureResult? =
            reviewStep?.results?
            .first
            as? ORKConsentSignatureResult

          if consentSignatureResult?.consented == false {  // Disgreed
              if reachability.connection != .unavailable {
                  taskViewController.dismiss(animated: true, completion: nil)
                  self.popViewControllerAfterConsentDisagree()
              }
            
            return nil

          } else if reachability.connection != .unavailable {  // Consented

            // Copying consent document
            let documentCopy: ORKConsentDocument =
              ((ConsentBuilder.currentConsent?.consentDocument)!.copy()
              as? ORKConsentDocument)!

            consentSignatureResult?.apply(to: documentCopy)
            // instantiating ConsentSharePdfStep
            let gatewayStoryboard = UIStoryboard(
              name: kFetalKickCounterStep,
              bundle: nil
            )
            let ttController =
              (gatewayStoryboard.instantiateViewController(
                withIdentifier: kConsentSharePdfStoryboardId
              )
              as? ConsentSharePdfStepViewController)!
            ttController.step = step
            ttController.consentDocument = documentCopy

            // start enrollment process

            DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
              self.studyEnrollmentStarted(taskViewController: taskViewController)
            }

            return ttController
          } else {
              return taskViewController.currentStepViewController
          }
        } else {
          return nil
        }

      } else if step.identifier == kConsentViewPdfCompletionStep {  // For PDFViewerStep

        // fetching reviewStep
        let reviewSharePdfStep: ORKStepResult? =
          taskViewController.result.results?.last
          as! ORKStepResult?

        let result = (reviewSharePdfStep?.results?.first as? ConsentCompletionTaskResult)

        if (result?.didTapOnViewPdf)! {
          let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)

          let ttController =
            (gatewayStoryboard.instantiateViewController(
              withIdentifier: kConsentViewPdfStoryboardId
            )
            as? ConsentPdfViewerStepViewController)!
          ttController.step = step
          // Pdf data is passed to Viewer for display
          ttController.pdfData = result?.pdfData

          return ttController

        } else {
          return nil
        }

      } else if step.identifier == kComprehensionCompletionStepIdentifier {
        // comprehension test is available
        if (ConsentBuilder.currentConsent?.comprehension?.questions?.count)! > 0 {

          let stepIndex: Int? = taskViewController.result.results?.firstIndex(
            where: { $0.identifier == kVisualStepId })

          if let visualStepIndex = stepIndex, visualStepIndex >= 0 {

            var i = visualStepIndex + 2  // holds the index of  question
            var j = 0  // holds the index of correct answer

            var userScore = 0

            // Pass score Calculation
            while i < (taskViewController.result.results?.count)! {

              let textChoiceResult: ORKChoiceQuestionResult =
                (((taskViewController.result.results?[i] as? ORKStepResult)!
                .results?.first)
                as? ORKChoiceQuestionResult)!

              let correctAnswerDict: [String: Any]? = ConsentBuilder.currentConsent?
                .comprehension?
                .correctAnswers?[j]
              let answerArray: [String] =
                (correctAnswerDict?[kConsentComprehensionAnswer] as? [String])!
              let evaluationType: Evaluation? = Evaluation(
                rawValue: (correctAnswerDict?[kConsentComprehensionEvaluation] as? String)!
              )
              let answeredSet = Set((textChoiceResult.choiceAnswers! as? [String])!)

              let correctAnswerSet = Set(answerArray)
              // Evaluation Type
              switch evaluationType! {
              case .any:

                if answeredSet.isSubset(of: correctAnswerSet) {
                  userScore += 1
                }
              case .all:

                if answeredSet == correctAnswerSet {
                  userScore += 1
                }
              }

              j += 1
              i += 1
            }

            if userScore >= (ConsentBuilder.currentConsent?.comprehension?.passScore)! {
              // User Failed to pass the Score
              return nil
            } else {
              // User passed the Score
              self.isComprehensionFailed = true
              self.addRetryScreen(viewController: nil)

              taskViewController.dismiss(animated: true, completion: nil)
            }

          }
          // If by chance we didnt get visualStepIndex i.e There's no visual step.
          // Do Nothing
          return nil

        } else {
          // comprehension test is not available
          return nil
        }

      } else if step.identifier == kReviewTitle {
        // if sharing step exists && allowWithoutSharing is set

        let shareStep: ORKStepResult? =
          taskViewController.result.results?.last
          as! ORKStepResult?

        ConsentBuilder.currentConsent?.sharingConsent?.allowWithoutSharing = true

        if shareStep?.identifier == kConsentSharing
          && ConsentBuilder.currentConsent?
            .sharingConsent
            != nil
          && (ConsentBuilder.currentConsent?.sharingConsent?.allowWithoutSharing)!
            == false
        {

          let result = (shareStep?.results?.first as? ORKChoiceQuestionResult)

          if (result?.choiceAnswers?.first as? Bool)! == true {  // User agreed to share
            return nil

          } else {  // User disagreed to share
            taskViewController.dismiss(
              animated: true,
              completion: {

                self.popViewControllerAfterConsentDisagree()

                // Alert User
                UIUtilities.showAlertWithTitleAndMessage(
                  title: "Message",
                  message: NSLocalizedString(
                    kShareConsentFailureAlert,
                    comment: ""
                  ) as NSString
                )
              }
            )
            return nil
          }

        } else {
          return nil
        }

      } else {
        return nil
      }

    } else {
      // other than consent step mostly passcode step
      return nil
    }
  }
}

// MARK: Passcode Delegate
extension AppDelegate: ORKPasscodeDelegate {
  func passcodeViewControllerDidFinish(withSuccess viewController: UIViewController) {
    containerViewController?.contentHidden = false
    self.appIsResignedButDidNotEnteredBackground = false

    viewController.dismiss(
      animated: true,
      completion: {
        self.isPasscodePresented = false

        if self.shouldAddForceUpgradeScreen {

          if self.blockerScreen?.isHidden == true {
            self.blockerScreen?.isHidden = false

          } else {
            UIApplication.shared.keyWindow?.addSubview(self.blockerScreen!)
          }
        }

        if self.isComprehensionFailed! {

          if self.retryView != nil {
            self.retryView?.isHidden = false
          }
        }

        if self.selectedController != nil {
          self.checkConsentStatus(controller: self.selectedController!)
        }
      }
    )

    if alertVCPresented != nil {
      parentViewControllerForAlert?.present(
        alertVCPresented!,
        animated: true,
        completion: {
          self.alertVCPresented = nil
        }
      )
    }
  }

  func passcodeViewControllerDidFailAuthentication(_ viewController: UIViewController) {}

  func passcodeViewControllerText(forForgotPasscode viewController: UIViewController) -> String {
    return kforgotPasscodeTitle
  }

  func passcodeViewControllerForgotPasscodeTapped(_ viewController: UIViewController) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Forgot Passcode?ActionClicked"
    ])
    
    var topVC = UIApplication.shared.keyWindow?.rootViewController

    while topVC?.presentedViewController != nil {
      topVC = topVC?.presentedViewController
    }

    UIUtilities.showAlertMessageWithTwoActionsAndHandler(
      NSLocalizedString(kMessagePasscode, comment: ""),
      errorMessage: NSLocalizedString(kMessagePasscodeSignOut, comment: ""),
      errorAlertActionTitle: NSLocalizedString(kTitleOK, comment: ""),
      errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""),
      viewControllerUsed: topVC!,
      action1: {
        Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
          buttonClickReasonsKey: "ForgotPasscodeAlert OK"
        ])
        self.iscomingFromForgotPasscode = true
        self.window?.addProgressIndicatorOnWindowFromTop()

        viewController.dismiss(
          animated: true,
          completion: {
            // fetch top view controller
            var topVC = UIApplication.shared.keyWindow?.rootViewController
            while topVC?.presentedViewController != nil {

              topVC = topVC?.presentedViewController
            }

            if topVC is ORKTaskViewController {
              topVC?.dismiss(animated: true, completion: nil)
            }

            self.iscomingFromForgotPasscode = true
            // Remove the device token first and initiate sign out.
            LeftMenuViewController.updatePushTokenToEmptyString(delegate: self)
          }
        )
      },
      action2: {
        Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
          buttonClickReasonsKey: "ForgotPasscodeAlert Cancel"
        ])
      }
    )
  }
}

// MARK: - ComprehensionFailureDelegate

extension AppDelegate: ComprehensionFailureDelegate {
  func didTapOnCancel() {
    self.popViewControllerAfterConsentDisagree()
  }

  func didTapOnRetry() {
    // Create Consent Task on Retry
    self.createEligibilityConsentTask()
  }
  
  @objc func receivedORKAction(_ notification: Notification) {
    let value = notification.userInfo
    if let action = value?["ORKAction"] as? String {
      Analytics.logEvent(analyticsButtonClickEventsName, parameters: [buttonClickReasonsKey: action])
    }
  }

}

// MARK: - UNUserNotification Delegate

@available(iOS 10, *)
extension AppDelegate: UNUserNotificationCenterDelegate {

  /// Receive displayed notifications for iOS 10 devices.
  func userNotificationCenter(
    _ center: UNUserNotificationCenter,
    willPresent notification: UNNotification,
    withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) ->
      Void
  ) {
    
    UserDefaults.standard.set("903,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    
    let userInfo = notification.request.content.userInfo

    if userInfo.count > 0 && userInfo.keys.contains(kType) {
      self.updateActiveNotification()
    }
    if let userInfo = userInfo as? JSONDictionary {
      refreshStudyActivitiesState(with: userInfo)
    }; 
    completionHandler([UNNotificationPresentationOptions.alert, .sound, .badge])
  }

  func userNotificationCenter(
    _ center: UNUserNotificationCenter,
    didReceive response: UNNotificationResponse,
    withCompletionHandler completionHandler: @escaping () -> Void
  ) {
    UserDefaults.standard.set("901,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    
    let userInfo = response.notification.request.content.userInfo
    UIApplication.shared.applicationIconBadgeNumber = 0
    
    UserDefaults.standard.set("\(userInfo as? JSONDictionary ?? [:])", forKey: "newactivity4")
            UserDefaults.standard.synchronize()

    if UIApplication.shared.applicationState == UIApplication.State.background
      || UIApplication.shared.applicationState == UIApplication.State.active
    {

      self.handleLocalAndRemoteNotification(userInfoDetails: (userInfo as? JSONDictionary ?? [:]))
    }

    // UserInfo is valid & contains Type for Notification
   else if userInfo.count > 0 && userInfo.keys.contains(kType) {
      self.handleLocalAndRemoteNotification(userInfoDetails: (userInfo as? JSONDictionary ?? [:]))
    }
    completionHandler()
  }
}

extension UIWindow {

  /// Adds progress below navigation bar
  func addProgressIndicatorOnWindow(with message: String = "") {
    let frame = UIApplication.shared.keyWindow?.bounds ?? UIScreen.main.bounds
    addProgressIndicatorOnWindowFromTop(with: message, frame: frame)
  }

  /// Adds Progress on complete screen, including navigation bar
  func addProgressIndicatorOnWindowFromTop(
    with message: String = "",
    frame: CGRect? = nil
  ) {
    let isProgressAdded = self.subviews
      .contains(where: { $0.isKind(of: NewProgressView.self) })
    if !isProgressAdded {
      let frame = frame ?? UIScreen.main.bounds
      if let progressView = NewProgressView.instanceFromNib(frame: frame) {
        progressView.showLoader(with: message)
        progressView.alpha = 0
        self.addSubview(progressView)
        
        let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
        if let valBlocker = appDelegate.blockerScreen, Utilities.isVisible(view: valBlocker) {
          UIApplication.shared.keyWindow?.bringSubviewToFront(appDelegate.blockerScreen!)
        }
        
        UIView.animate(withDuration: 0.3) {
          progressView.alpha = 1
        }
      }
    }
  }

  /// Removes progress from window
  func removeProgressIndicatorFromWindow() {
    if let progressView = self.subviews
      .first(where: { $0.isKind(of: NewProgressView.self) })
    {
      UIView.animate(
        withDuration: 0.2,
        animations: {
          progressView.alpha = 0
        },
        completion: { (_) in
          progressView.removeFromSuperview()
        }
      )
    }
  }
}

enum EnumORKAction: String {
  case ORKCancel, ORKDone, ORKSave, ORKSkip, ORKContinue, ORKClearAnswer, ORKButtonTapped, ORKBackButton, ORKEndTask,
       ORKProceed, ORKLearnMore, ORKSaveForLater, ORKCancelAlert, ORKReviewAgreeAlert, ORKReviewCancel, ORKReviewAgree, ORKReviewDisAgree,
       ORKContinueButton, ORKLearnMoreDone, ORKKeyboardDone, ORKKeyboardPlusMinus, ORKTryAgain, ORKNext, ORKClearSign, ORKPasscodeCancel,
       ORKPasscodeInvalidAlertOK, ORKActivityTimeOut, ORKCopyRightOkAlert, ORKShowCopyRight, ORKPlaybackNextItem, ORKOK
}
