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
import FirebaseAnalytics
import Reachability

let kHelperTextForFilteredStudiesNotFound = "No studies found for the filters applied."

let kHelperTextForSearchedStudiesNotFound =
  "No studies found for the search term(s) used."

let kHelperTextForOffline =
  "No study available right now.\nPlease remain signed in to get notified when there are new studies available."

let kNotificationViewControllerIdentifier = "NotificationViewControllerIdentifier"

class StudyListViewController: UIViewController {

  // MARK: - Outlets

  @IBOutlet weak var tableView: UITableView!

  @IBOutlet weak var labelHelperText: UILabel!

  lazy var studyListRequestFailed = false
  var searchView: SearchBarView?
  lazy var isComingFromFilterScreen: Bool = false
  lazy var studiesList: [Study] = []
  lazy var previousStudyList: [Study] = []
  private var reachability: Reachability!

  /// Gatewaystudylist
  lazy var allStudyList: [Study] = []

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  // MARK: - Viewcontroller lifecycle

  override func viewDidLoad() {
    super.viewDidLoad()
    
//    UserDefaults.standard.set("1stAugtestDev 0", forKey: "performActivityTaskBasedOnStudyStatus")
//    UserDefaults.standard.synchronize()
    
    print("777userInfoDetails---\(UserDefaults.standard.value(forKey: "newactivity1"))")
    print("888userInfoDetails---\(UserDefaults.standard.value(forKey: "newactivity2"))")
    
    let val31 = UserDefaults.standard.value(forKey: "newactivity1") as? String ?? ""
    
   let val4 = ((UserDefaults.standard.value(forKey: "newactivity2") as? String) ?? "").contains("A new activity")
    
    let val4b = ((UserDefaults.standard.value(forKey: "newactivity3") as? String) ?? "").contains("A new activity")
    
    let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
    let val4c = "\(appdelegate.notificationDetails)"
    let val4d = "\(appdelegate.notificationDetails)".contains("A new activity")
    
    let val5b = ((UserDefaults.standard.value(forKey: "newactivity4") as? String) ?? "").contains("A new activity")
    
    
//    if val4 && val31 == "" {
      if val4 {
      let val5 = ((UserDefaults.standard.value(forKey: "newactivity2") as? String) ?? "").components(separatedBy: "\"studyId\": ")
      if val5.count > 1 {
       print("8881userInfoDetails---\(val5[1])")
        let val6 = val5[1]
        let val7 = val6.components(separatedBy: ",")
        if val7.count > 1 {
          print("8882userInfoDetails---\("\(val7[0] ?? "") 0")")
          UserDefaults.standard.set("\(val7[0] ?? "") 0", forKey: "performActivityTaskBasedOnStudyStatus")
          UserDefaults.standard.synchronize()
        }
      }
    }
    else if val4b {
      let val5 = ((UserDefaults.standard.value(forKey: "newactivity3") as? String) ?? "").components(separatedBy: "\"studyId\": ")
      if val5.count > 1 {
       print("8881userInfoDetails---\(val5[1])")
        let val6 = val5[1]
        let val7 = val6.components(separatedBy: ",")
        if val7.count > 1 {
          print("8882userInfoDetails---\("\(val7[0] ?? "") 0")")
          UserDefaults.standard.set("\(val7[0] ?? "") 0", forKey: "performActivityTaskBasedOnStudyStatus")
          UserDefaults.standard.synchronize()
        }
      }
    } else if val4d {
      let val5 = val4c.components(separatedBy: "\"studyId\": ")
      if val5.count > 1 {
       print("8881userInfoDetails---\(val5[1])")
        let val6 = val5[1]
        let val7 = val6.components(separatedBy: ",")
        if val7.count > 1 {
          print("8882userInfoDetails---\("\(val7[0] ?? "") 0")")
          UserDefaults.standard.set("\(val7[0] ?? "") 0", forKey: "performActivityTaskBasedOnStudyStatus")
          UserDefaults.standard.synchronize()
        }
      }
    } else if val5b {
      let val5 = ((UserDefaults.standard.value(forKey: "newactivity4") as? String) ?? "").components(separatedBy: "\"studyId\": ")
      if val5.count > 1 {
       print("8881userInfoDetails---\(val5[1])")
        let val6 = val5[1]
        let val7 = val6.components(separatedBy: ",")
        if val7.count > 1 {
          print("8882userInfoDetails---\("\(val7[0] ?? "") 0")")
          UserDefaults.standard.set("\(val7[0] ?? "") 0", forKey: "performActivityTaskBasedOnStudyStatus")
          UserDefaults.standard.synchronize()
        }
      }
    }
    
    UserDefaults.standard.set("", forKey: "newactivity2")
    UserDefaults.standard.set("", forKey: "newactivity1")
    UserDefaults.standard.set("", forKey: "newactivity3")
    UserDefaults.standard.set("", forKey: "newactivity4")
    UserDefaults.standard.synchronize()
    
    
    addNavigationTitle()
    isComingFromFilterScreen = false
    DispatchQueue.main.async { [weak self] in
      self?.setupStudyListTableView()
    }
    if #available(iOS 15, *) {
      UITableView.appearance().sectionHeaderTopPadding = CGFloat(0)
    }
      print("1stAugtestDev---")
//      UserDefaults.standard.set("1stAugtestDev 2", forKey: "performTaskBasedOnStudyStatus")
//      UserDefaults.standard.synchronize()
  }
  
    override func viewWillDisappear(_ animated: Bool) {
        removeNotifier()
    }
    override func showOfflineIndicator() -> Bool {
        return true
    }
  override func viewWillAppear(_ animated: Bool) {
    setupNotifiers()
    if !isComingFromFilterScreen && !(self.slideMenuController()?.isLeftOpen() ?? true) {
      self.addProgressIndicator()
    }
    setNavigationBarColor()
    Utilities.removeImageLocalPath(localPathName: kConsentSharingImage)
    Utilities.removeImageLocalPath(localPathName: kConsentSharingImagePDF)
    UserDefaults.standard.setValue("", forKey: "enrollmentCompleted")
    UserDefaults.standard.synchronize()
      
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
  
    
    if isComingFromFilterScreen {
      isComingFromFilterScreen = false
      return
    }
    addRightNavigationItem()
    navigationController?.setNavigationBarHidden(false, animated: true)
    navigationController?.navigationBar.isHidden = false

    Study.currentStudy = nil
    let ud = UserDefaults.standard
    // Checking if User has missed out setting the passcode/TouchId
    let isPasscodePending = ud.value(forKey: kPasscodeIsPending) as? Bool ?? false

    if isPasscodePending == true {
      if User.currentUser.userType == .loggedInUser {
        tableView?.isHidden = true
        // Fetch the User Profile
        UserServices().getUserProfile(self as NMWebServiceDelegate)
      }
    }

    labelHelperText.isHidden = true
    setNavigationBarItem()

    if User.currentUser.userType == .loggedInUser {  // For LoggedIn User
      tableView?.estimatedRowHeight = 152
      tableView?.rowHeight = UITableView.automaticDimension

      if !(fdaSlideMenuController()?.isLeftOpen())! {
        sendRequestToGetUserPreference()
      }
    } else {  // For ananomous User
      tableView?.estimatedRowHeight = 152
      tableView?.rowHeight = UITableView.automaticDimension
      // Fetch StudyList
      sendRequestToGetStudyList()
    }
    tableView?.tableFooterView = UIView()
    setNeedsStatusBarAppearanceUpdate()
    self.navigationController?.view.layoutSubviews()

    StudyListViewController.configureNotifications()

    // Handling StudyList Request Failure condition
    if studyListRequestFailed {
      labelHelperText.isHidden = false
      labelHelperText.text = kHelperTextForOffline
    }
    
    if User.currentUser.userType != .loggedInUser {
      ud.set(true, forKey: kIsStudylistGeneral)
      ud.synchronize()
    } else {
      ud.set(false, forKey: kIsStudylistGeneral)
      ud.synchronize()
    }
    checkBlockerScreen()
      
      DispatchQueue.main.async {

//          self.performTaskBasedOnStudyStatus()

      let val1 = UserDefaults.standard.value(forKey: "performTaskBasedOnStudyStatus") as? String ?? ""
      UserDefaults.standard.set("", forKey: "performTaskBasedOnStudyStatus")
      UserDefaults.standard.synchronize()
      print("valvalval---\(val1)")
      let val = val1.components(separatedBy: " ")
//      var initialVC: UIViewController?

          UserDefaults.standard.set("325---\(val[0])---,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()

      if !(val[0] == "" || val[0] == nil) {
          if let study = Gateway.instance.studies?.filter { $0.studyId == val[0] }.last {
              UserDefaults.standard.set("525,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
              UserDefaults.standard.synchronize()

          print("study--\(study)")
          Study.updateCurrentStudy(study: study)
              let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
              appdelegate.notificationDetails = nil
        self.performTaskBasedOnStudyStatus()
      print("181userInfoDetails---")

      // push tabbar and switch to activty tab
//      if let initialVC = initialVC {
//        print("23userInfoDetails---")
        UserDefaults.standard.set("323,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()



//          let tabVal = Int(val[1]) ?? 0
//          if tabVal != 0 {
//        self.pushToTabbar(
//          viewController: self,
//          selectedTab: tabVal ?? 0
//        )
//      }

          }
      }
      }
      
  }
  
  override func viewDidDisappear(_ animated: Bool) {
    let ud = UserDefaults.standard
    ud.set(false, forKey: kIsStudylistGeneral)
    ud.synchronize()
  }
  
  // MARK: - Utility functions
  func setupNotifiers() {
    NotificationCenter.default.addObserver(self, selector: #selector(self.methodOfReceivedNotification(notification:)),
                                           name: Notification.Name("Menu Clicked"), object: nil)
    NotificationCenter.default.addObserver(self, selector: #selector(self.methodOfReceivedNotification1(notification:)),
                                           name: Notification.Name("LeftMenu Home"), object: nil)
    NotificationCenter.default.addObserver(self, selector:#selector(reachabilityChanged(note:)),
                                           name: Notification.Name.reachabilityChanged, object: nil);
    
    
    
    do {
      self.reachability = try Reachability()
      try self.reachability.startNotifier()
    } catch(let error) {
    }
  }
  
  func removeNotifier() {
    NotificationCenter.default.removeObserver(self, name: Notification.Name.reachabilityChanged, object: nil)
    NotificationCenter.default.removeObserver(self, name: Notification.Name("Menu Clicked"), object: nil)
    NotificationCenter.default.removeObserver(self, name: Notification.Name("LeftMenu Home"), object: nil)
  }
  
  @objc func reachabilityChanged(note: Notification) {
    let reachability = note.object as! Reachability
    switch reachability.connection {
    case .cellular:
      ReachabilityIndicatorManager.shared.removeIndicator(viewController: self)
      break
    case .wifi:
      ReachabilityIndicatorManager.shared.removeIndicator(viewController: self)
      break
    case .none:
      ReachabilityIndicatorManager.shared.presentIndicator(viewController: self, isOffline: true)
      
      break
    case .unavailable:
      ReachabilityIndicatorManager.shared.presentIndicator(viewController: self, isOffline: true)
      break
    }
  }

  // MARK: - UI Utils

  /// To update the navigation bar items , by adding Notification Button, Notification Indicator & Filter Button.
  func addRightNavigationItem() {
    guard navigationItem.rightBarButtonItems == nil
    else { return }  // No need to add again.
    let view = UIView(frame: CGRect(x: 0, y: 4, width: 110, height: 40))

    // Notification Button
    let button = addNotificationButton()
    view.addSubview(button)
    button.isExclusiveTouch = true

    // Notification Indicator
    let label = addNotificationIndication()
    view.addSubview(label)

    let isShowNotification = UserDefaults.standard.bool(forKey: kShowNotification)
    label.isHidden = isShowNotification ? false : true

    //  Filter Button
    let filterButton = addFilterButton()
    view.addSubview(filterButton)
    filterButton.isExclusiveTouch = true

    //  Search Button
    let searchButton = addSearchButton()
    view.addSubview(searchButton)
    searchButton.isExclusiveTouch = true

    let barButton = UIBarButtonItem(customView: view)
    navigationItem.rightBarButtonItems = [barButton]
  }

  func addSearchButton() -> UIButton {
    let searchButton = UIButton(type: .custom)
    searchButton.setImage(
      #imageLiteral(resourceName: "search_small"),
      for: UIControl.State.normal
    )
    searchButton.addTarget(self, action: #selector(searchButtonAction(_:)), for: .touchUpInside)
    searchButton.frame = CGRect(x: 0, y: 4, width: 30, height: 30)
    return searchButton
  }

  func addFilterButton() -> UIButton {
    let filterButton = UIButton(type: .custom)
    filterButton.setImage(
      #imageLiteral(resourceName: "filterIcon"),
      for: UIControl.State.normal
    )
    filterButton.addTarget(self, action: #selector(filterAction(_:)), for: .touchUpInside)
    filterButton.frame = CGRect(x: 40, y: 4, width: 30, height: 30)
    return filterButton
  }

  func addNotificationButton() -> UIButton {
    let button = UIButton(type: .custom)

    button.setImage(
      #imageLiteral(resourceName: "notification_grey"),
      for: UIControl.State.normal
    )
    button.addTarget(self, action: #selector(buttonActionNotification(_:)), for: .touchUpInside)
    button.frame = CGRect(x: 80, y: 4, width: 30, height: 30)
    return button
  }

  func addNotificationIndication() -> UILabel {
    let label = UILabel(frame: CGRect(x: 100, y: 4, width: 10, height: 10))
    label.font = UIFont.systemFont(ofSize: 10)
    label.textColor = UIColor.white

    label.textAlignment = NSTextAlignment.center
    label.backgroundColor = kUIColorForSubmitButtonBackground
    label.layer.cornerRadius = 5
    label.clipsToBounds = true
    label.text = ""
    return label
  }

  /// Add the navigation title from the branding plist.
  fileprivate func addNavigationTitle() {

    let navTitle = Branding.navigationTitleName
    let titleLabel = UILabel()
    titleLabel.text = NSLocalizedString(navTitle, comment: "")
    titleLabel.font = UIFont(name: "HelveticaNeue-Medium", size: 18)
    titleLabel.textAlignment = .left
    titleLabel.textColor = Utilities.getUIColorFromHex(0x007CBA)
    titleLabel.frame = CGRect(x: 0, y: 0, width: 300, height: 44)

    navigationItem.titleView = titleLabel
  }

  fileprivate func setupStudyListTableView() {
    let refresher = UIRefreshControl()
    refresher.addTarget(self, action: #selector(sendRequestToGetUserPreference), for: .valueChanged)
    refresher.attributedTitle = NSAttributedString(string: "Pull to refresh")
    tableView.refreshControl = refresher
  }
  
  @objc func methodOfReceivedNotification(notification: Notification) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Menu Clicked"
    ])
  }
  
  @objc func methodOfReceivedNotification1(notification: Notification) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "LeftMenu Home"
    ])
  }
  // MARK: - Utils
  func checkIfNotificationEnabled() {

    var notificationEnabledFromAppSettings = false
    // Checking the app notification settings.
    LocalNotification.notificationsEnabled { (status) in
      notificationEnabledFromAppSettings = status
    }

    if (User.currentUser.settings?.remoteNotifications)!,
      (User.currentUser.settings?.localNotifications)!,
      notificationEnabledFromAppSettings
    {  // Notifications are enabled
      // Do Nothing
    } else {  // Notification is Disabled
      let ud = UserDefaults.standard
      let previousDate = ud.object(forKey: "NotificationRemainder") as? Date
      let todayDate = Date()
      var daysLastSeen = 0
      if previousDate != nil {
        daysLastSeen = Schedule().getNumberOfDaysBetween(
          startDate: previousDate!,
          endDate: todayDate
        )
      }

      if daysLastSeen >= 7 {  // Notification is disabled for 7 or more Days
        UIUtilities.showAlertWithTitleAndMessage(
          title: NSLocalizedString(Branding.productTitle, comment: "") as NSString,
          message: kMessageAppNotificationOffStayup + kMessageAppNotificationOffRemainder
            as NSString
        )

        ud.set(Date(), forKey: "NotificationRemainder")
        ud.synchronize()
      }
    }
  }

  /// checkIfFetalKickCountRunning method verifies wheather if FetalKick Task is Still running and calculate the time difference.
  func checkIfFetalKickCountRunning() {
    let ud = UserDefaults.standard

    if ud.bool(forKey: "FKC"), ud.object(forKey: kFetalKickStartTimeStamp) != nil {
      let studyId = (ud.object(forKey: kFetalkickStudyId) as? String)!
      let study = Gateway.instance.studies?.filter { $0.studyId == studyId }.last

      if study?.userParticipateState.status == .enrolled, study?.status == .active {
        Study.updateCurrentStudy(study: study!)
        pushToStudyDashboard(animated: false)
      }
    } else {
      checkIfNotificationEnabled()
      if !NotificationHandler.instance.studyId.isEmpty {
        let studyId = NotificationHandler.instance.studyId
        let study = Gateway.instance.studies?.filter { $0.studyId == studyId }.first
        Study.updateCurrentStudy(study: study!)
          UserDefaults.standard.set("326---,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
        performMainTaskBasedOnStudyStatus()
      }
    }
  }

  /// Navigate to notification screen.
  func navigateToNotifications() {
    let gatewayStoryBoard = UIStoryboard(
      name: kStoryboardIdentifierGateway,
      bundle: Bundle.main
    )
    let notificationController =
      (gatewayStoryBoard.instantiateViewController(
        withIdentifier: kNotificationViewControllerIdentifier
      )
      as? NotificationViewController)!
    navigationController?.pushViewController(notificationController, animated: true)
  }

  /// Navigate to StudyHomeViewController screen.
  func navigateToStudyHome() {
    let studyStoryBoard = UIStoryboard(name: kStudyStoryboard, bundle: Bundle.main)
    let studyHomeController =
      (studyStoryBoard.instantiateViewController(
        withIdentifier: String(describing: StudyHomeViewController.classForCoder())
      )
      as? StudyHomeViewController)!
    studyHomeController.delegate = self
    navigationController?.pushViewController(studyHomeController, animated: true)
  }

  /// Navigate the screen to Study Dashboard tabbar viewcontroller screen.
  func pushToStudyDashboard(animated: Bool = true) {
    let studyStoryBoard = UIStoryboard(name: kStudyStoryboard, bundle: Bundle.main)

    let studyDashboard =
      (studyStoryBoard.instantiateViewController(
        withIdentifier: kStudyDashboardTabbarControllerIdentifier
      )
      as? StudyDashboardTabbarViewController)!

    navigationController?.navigationBar.isHidden = true
    navigationController?.pushViewController(studyDashboard, animated: animated)
  }

  /// Method to display taskViewController for passcode setup if
  /// passcode setup is enabled,called only once after signin.
  func setPassCode() {
    // Remove Passcode if already exist
    ORKPasscodeViewController.removePasscodeFromKeychain()

    let passcodeStep = ORKPasscodeStep(identifier: kPasscodeStepIdentifier)
    passcodeStep.passcodeType = .type4Digit
    passcodeStep.text = kSetPasscodeDescription

    let task = ORKOrderedTask(identifier: kPasscodeTaskIdentifier, steps: [passcodeStep])
    let taskViewController = ORKTaskViewController(task: task, taskRun: nil)
    taskViewController.delegate = self
    taskViewController.isNavigationBarHidden = true
    taskViewController.navigationBar.prefersLargeTitles = false
    taskViewController.modalPresentationStyle = .fullScreen
    navigationController?.present(
      taskViewController,
      animated: false,
      completion: {
        self.tableView?.isHidden = false
      }
    )
  }

  /// Load the study data from Database.
  func loadStudiesFromDatabase() {
    DBHandler.loadStudyListFromDatabase { studies in
      if studies.count > 0 {
        self.tableView?.isHidden = false
        self.studiesList = studies
        self.tableView?.reloadData()

        self.previousStudyList = studies
        self.allStudyList = studies
        Gateway.instance.studies = studies
        
        DispatchQueue.main.async {
            
  //          self.performTaskBasedOnStudyStatus()
            
        let val1 = UserDefaults.standard.value(forKey: "performActivityTaskBasedOnStudyStatus") as? String ?? ""
        UserDefaults.standard.set("", forKey: "performActivityTaskBasedOnStudyStatus")
        UserDefaults.standard.synchronize()
        print("valvalval---\(val1)")
        let val = val1.components(separatedBy: " ")
  //      var initialVC: UIViewController?
            
            UserDefaults.standard.set("325---\(val[0])---,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            
        if !(val[0] == "" || val[0] == nil) {
            if let study = Gateway.instance.studies?.filter { $0.studyId == val[0] }.last {
                UserDefaults.standard.set("525,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
                UserDefaults.standard.synchronize()
                
            print("study--\(study)")
            Study.updateCurrentStudy(study: study)
                let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
                appdelegate.notificationDetails = nil
          self.performTaskBasedOnStudyStatus()
        print("181userInfoDetails---")
        
        // push tabbar and switch to activty tab
  //      if let initialVC = initialVC {
  //        print("23userInfoDetails---")
          UserDefaults.standard.set("323,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
            
            
            
  //          let tabVal = Int(val[1]) ?? 0
  //          if tabVal != 0 {
  //        self.pushToTabbar(
  //          viewController: self,
  //          selectedTab: tabVal ?? 0
  //        )
  //      }
                
            }
        }
        }
        
        
        

        // Applying Filters
        let previousStudyFilters = StudyFilterHandler.instance.previousAppliedFilters
        if previousStudyFilters.count > 0 {

          if User.currentUser.userType == .loggedInUser {
            self.appliedFilter(
              studyStatus: previousStudyFilters.first!,
              participationStatus: previousStudyFilters[safe: 1] ?? [],
              searchText: ""
            )
          } else {
            self.appliedFilter(
              studyStatus: previousStudyFilters.first!,
              participationStatus: [],
              searchText: ""
            )
          }
        } else {
          let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!

          appDelegate.setDefaultFilters(previousCollectionData: [])

          // using default Filters
          let filterStrings = appDelegate.getDefaultFilterStrings()

          self.appliedFilter(
            studyStatus: filterStrings.studyStatus,
            participationStatus: filterStrings.pariticipationsStatus,
            searchText: filterStrings.searchText
          )
        }
        self.checkIfFetalKickCountRunning()
      } else {
        if !self.studyListRequestFailed {
          self.labelHelperText.isHidden = true
          self.tableView?.isHidden = false
          self.studyListRequestFailed = false

          self.sendRequestToGetStudyList()
        } else {
          self.tableView?.isHidden = true
          self.labelHelperText.isHidden = false
          self.labelHelperText.text = kHelperTextForOffline
        }
      }
    }
    let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
      self.removeProgressIndicator()
      appdelegate.window?.removeProgressIndicatorFromWindow()

  }

  /// Sort Studies based on the Study Status.
  func getSortedStudies(studies: [Study]) -> [Study] {
    var sortedstudies2 = studies.sorted(by: { $0.name!.lowercased() < $1.name!.lowercased() })
    sortedstudies2 = sortedstudies2.sorted(
      by: { (study1: Study, study2: Study) -> Bool in

        if study1.status == study2.status {
          return
            (study1.userParticipateState.status.sortIndex
            < study2.userParticipateState
            .status
            .sortIndex)
        }
        return (study1.status.sortIndex < study2.status.sortIndex)
      })
    return sortedstudies2
  }

  // MARK: - Helpers

  /// Request for notiffications and update to server.
  static func configureNotifications() {
    // Checking if registering notification is pending.
    if UserDefaults.standard.bool(forKey: kNotificationRegistrationIsPending),
       let appDelegate = UIApplication.shared.delegate as? AppDelegate {
      appDelegate.askForFCMNotification()
    }
  }
  // MARK: - Button Actions

  /// Navigate to notification screen on button clicked.
  @IBAction func buttonActionNotification(_: UIBarButtonItem) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Notification icon clicked"
    ])
    navigateToNotifications()
  }

  /// Refresh the StudyList.
  @objc func refresh(sender _: AnyObject) {
    sendRequestToGetStudyList()
  }

  /// Navigate to StudyFilter screen on button clicked.
  @IBAction func filterAction(_: UIBarButtonItem) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Filter icon clicked"
    ])
    isComingFromFilterScreen = true
    performSegue(withIdentifier: filterListSegue, sender: nil)
  }

  @IBAction func searchButtonAction(_: UIBarButtonItem) {
    Analytics.logEvent(analyticsButtonClickEventsName, parameters: [
      buttonClickReasonsKey: "Search icon clicked"
    ])

    searchView = SearchBarView.instanceFromNib(
      frame: CGRect(x: 0, y: -200, width: view.frame.size.width, height: 64.0),
      detail: nil
    )

    UIView.animate(
      withDuration: 0.2,
      delay: 0.0,
      options: UIView.AnimationOptions.preferredFramesPerSecond60,
      animations: { () -> Void in

        let y: CGFloat = DeviceType.isIPhoneXOrHigh ? 20.0 : 0.0
        self.searchView?.frame = CGRect(
          x: 0,
          y: y,
          width: self.view.frame.size.width,
          height: 64.0
        )
        self.searchView?.textFieldSearch?.becomeFirstResponder()
        self.searchView?.delegate = self
        self.slideMenuController()?.leftPanGesture?.isEnabled = false
        self.navigationController?.view.addSubview(self.searchView!)

        if StudyFilterHandler.instance.searchText.count > 0 {
          self.searchView?.textFieldSearch?.text = StudyFilterHandler.instance.searchText
        }
      },
      completion: nil
    )

  }

  // MARK: - Segue Methods

  override func prepare(for segue: UIStoryboardSegue, sender _: Any?) {
    // Get the new view controller using segue.destinationViewController.
    // Pass the selected object to the new view controller.

    if segue.identifier == filterListSegue {
      let filterVc = (segue.destination as? StudyFilterViewController)!

      if StudyFilterHandler.instance.previousAppliedFilters.count > 0 {
        filterVc.previousCollectionData = StudyFilterHandler.instance.previousAppliedFilters
      }
      filterVc.delegate = self
    }
  }

  @IBAction func unwindToStudyList(_: UIStoryboardSegue) {
    // unwindStudyListSegue
  }

  // MARK: - Database Methods

  /// Get the `Study` overview from DB if available and navigate.
  /// - Parameter study: Instance of `Study`.
  func checkDatabaseForStudyInfo(study: Study) {
    guard let studyId = study.studyId else { return }
    DBHandler.loadStudyOverview(studyId: studyId) { overview in
      if overview != nil {
        study.overview = overview
        self.navigateToStudyHome()
      } else {
        // Call API to get StudyInfo.
        self.sendRequestToGetStudyInfo(study: study)
      }
    }
  }

  // MARK: - Webservice Requests

  /// Send the webservice request to get Study List.
  @objc fileprivate func sendRequestToGetStudyList() {
    WCPServices().getStudyList(self)
  }

  /// Send the webservice request to get Study Info.
  /// - Parameter study: Instance of `Study`.
  func sendRequestToGetStudyInfo(study: Study) {
    WCPServices().getStudyInformation(studyId: study.studyId, delegate: self)
  }

  ///  Send the webservice request to get UserPreferences.
  @objc func sendRequestToGetUserPreference() {
    EnrollServices().getStudyStates(self)
  }

  // MARK: - Webservice Responses

  /// Handle the Study list webservice response.
  func handleStudyListResponse() {
    if (Gateway.instance.studies?.count)! > 0 {
      loadStudiesFromDatabase()
      let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!

      if appDelegate.notificationDetails != nil, User.currentUser.userType == .loggedInUser {
          
          
          UserDefaults.standard.set("545,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          
          UserDefaults.standard.set("", forKey: "performTaskBasedOnStudyStatus")
          UserDefaults.standard.synchronize()
        appDelegate.handleLocalAndRemoteNotification(
          userInfoDetails: appDelegate.notificationDetails!
        )
          UserDefaults.standard.set("546,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          appDelegate.notificationDetails = nil
      }
    } else {
      tableView?.isHidden = true
      labelHelperText.text = kHelperTextForOffline
      labelHelperText.isHidden = false
    }
    DispatchQueue.main.async {
      // Remove paused studies local notifications.
      if let updatedStudies = Gateway.instance.studies {
        let pausedStudies = updatedStudies.filter { $0.status == .closed || $0.status == .paused }
        for study in pausedStudies {
          DBHandler.deleteStudyDBLocalNotifications(for: study.studyId)
        }
        if pausedStudies.count > 0 {
          LocalNotification.refreshAllLocalNotification()
        }
      }
    }
  }

  /// Save information for study which feilds need to be updated.
  func handleStudyUpdatedInformation() {
    guard let currentStudy = Study.currentStudy else { return }
    if currentStudy.userParticipateState.status == UserStudyStatus.StudyStatus.yetToEnroll {
      StudyUpdates.studyConsentUpdated = false
      StudyUpdates.studyActivitiesUpdated = false
      StudyUpdates.studyResourcesUpdated = false
      StudyUpdates.studyEnrollAgain = false

      currentStudy.version = StudyUpdates.studyVersion
      currentStudy.newVersion = StudyUpdates.studyVersion
    }

    DBHandler.updateMetaDataToUpdateForStudy(study: currentStudy, updateDetails: nil)
    if StudyUpdates.studyInfoUpdated {
      sendRequestToGetStudyInfo(study: currentStudy)
    } else {
      navigateBasedOnUserStatus()
    }
  }

  /// navigateBasedOnUserStatus method navigates to StudyDashBoard or StudyHome based on UserParticipationStatus.
  func navigateBasedOnUserStatus() {
    guard let currentStudy = Study.currentStudy else { return }
    if User.currentUser.userType == UserType.loggedInUser {
      if currentStudy.status == .active {
        // handle accoring to UserStatus
        let userStudyStatus = currentStudy.userParticipateState.status

        if userStudyStatus == .completed || userStudyStatus == .enrolled {
          pushToStudyDashboard()
        } else if userStudyStatus == .yetToEnroll {
          checkDatabaseForStudyInfo(study: currentStudy)
        } else {
          checkDatabaseForStudyInfo(study: currentStudy)
        }
      } else {
        checkDatabaseForStudyInfo(study: currentStudy)
      }
    } else {
      checkDatabaseForStudyInfo(study: currentStudy)
    }
  }

  /// Checks `Study` status and do the action.
  func performTaskBasedOnStudyStatus(studyID: String? = nil) {
      UserDefaults.standard.set("527,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
    // Study ID from notification
    if let studyID = studyID,
        let study = studiesList.filter({ $0.studyId == studyID }).first {
      UserDefaults.standard.set("628,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      Study.updateCurrentStudy(study: study)
    }
    
    UserDefaults.standard.set("627,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    
    guard let study = Study.currentStudy else { return }
    UserDefaults.standard.set("629,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    if User.currentUser.userType == UserType.loggedInUser {
      UserDefaults.standard.set("630,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      let val = UserDefaults.standard.value(forKey: "pausedNotification") as? String ?? ""
      if Study.currentStudy?.status == .paused || val == "paused" {
        UserDefaults.standard.set("631,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        UserDefaults.standard.set("", forKey: "pausedNotification")
        UserDefaults.standard.synchronize()
        let userStudyStatus = study.userParticipateState.status
        UserDefaults.standard.set("632,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        if userStudyStatus == .completed || userStudyStatus == .enrolled {
          DispatchQueue.main.async {
            UserDefaults.standard.set("633,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            if (studyID == nil) {
              UIUtilities.showAlertWithTitleAndMessage(
                title: "",
                message: NSLocalizedString(
                  kMessageForStudyPausedAfterJoiningState,
                  comment: ""
                )
                as NSString
              )
            }
          }
        } else {
          UserDefaults.standard.set("634,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          checkForStudyUpdate(study: study)
        }
      } else if study.status == .active {
        UserDefaults.standard.set("635,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        let userStudyStatus = study.userParticipateState.status

        if userStudyStatus == .completed || userStudyStatus == .enrolled {
          UserDefaults.standard.set("636,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          // check if study version is udpated
          if study.version != study.newVersion {
            WCPServices().getStudyUpdates(study: study, delegate: self)
          } else {
            addProgressIndicator()
            perform(#selector(loadStudyDetails), with: self, afterDelay: 1)
          }
        } else if userStudyStatus == .yetToEnroll {
          UserDefaults.standard.set("637,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          checkDatabaseForStudyInfo(study: study)
        } else {
          UserDefaults.standard.set("638,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          checkForStudyUpdate(study: study)
        }
      }
    } else {
      UserDefaults.standard.set("639,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      checkForStudyUpdate(study: study)
    }
  }
    
  func performMainTaskBasedOnStudyStatus(studyID: String? = nil) {
      UserDefaults.standard.set("591,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
    // Study ID from notification
    if let studyID = studyID,
        let study = studiesList.filter({ $0.studyId == studyID }).first {
      UserDefaults.standard.set("592,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      Study.updateCurrentStudy(study: study)
    }
    
    UserDefaults.standard.set("593,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    
    guard let study = Study.currentStudy else { return }
    UserDefaults.standard.set("594,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
    UserDefaults.standard.synchronize()
    if User.currentUser.userType == UserType.loggedInUser {
      UserDefaults.standard.set("595,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      let val = UserDefaults.standard.value(forKey: "pausedNotification") as? String ?? ""
      if Study.currentStudy?.status == .paused || val == "paused" {
        UserDefaults.standard.set("596,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        UserDefaults.standard.set("", forKey: "pausedNotification")
        UserDefaults.standard.synchronize()
        let userStudyStatus = study.userParticipateState.status
        UserDefaults.standard.set("597,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        if userStudyStatus == .completed || userStudyStatus == .enrolled {
          DispatchQueue.main.async {
            UserDefaults.standard.set("598,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
            UserDefaults.standard.synchronize()
            if (studyID == nil) {
              UIUtilities.showAlertWithTitleAndMessage(
                title: "",
                message: NSLocalizedString(
                  kMessageForStudyPausedAfterJoiningState,
                  comment: ""
                )
                as NSString
              )
            }
          }
        } else {
          UserDefaults.standard.set("599,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          checkForStudyUpdate(study: study)
        }
      } else if study.status == .active {
        UserDefaults.standard.set("581,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
        UserDefaults.standard.synchronize()
        let userStudyStatus = study.userParticipateState.status

        if userStudyStatus == .completed || userStudyStatus == .enrolled {
          UserDefaults.standard.set("582,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          // check if study version is udpated
          if study.version != study.newVersion {
            WCPServices().getStudyUpdates(study: study, delegate: self)
          } else {
            addProgressIndicator()
            perform(#selector(loadStudyDetails), with: self, afterDelay: 1)
          }
        } else if userStudyStatus == .yetToEnroll {
          UserDefaults.standard.set("583,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          checkDatabaseForStudyInfo(study: study)
        } else {
          UserDefaults.standard.set("584,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
          UserDefaults.standard.synchronize()
          checkForStudyUpdate(study: study)
        }
      }
    } else {
      UserDefaults.standard.set("585,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
      checkForStudyUpdate(study: study)
    }
  }
  
    func pushToTabbar(viewController: UIViewController, selectedTab: Int, studyID: String? = nil) {
      UserDefaults.standard.set("586,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
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

  @objc func loadStudyDetails() {
    let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
    guard let study = Study.currentStudy
    else {
      self.removeProgressIndicator()
      appdelegate.window?.removeProgressIndicatorFromWindow()
      return
    }
    DBHandler.loadStudyDetailsToUpdate(
      studyId: study.studyId,
      completionHandler: { _ in

        self.pushToStudyDashboard()
        self.removeProgressIndicator()
        appdelegate.window?.removeProgressIndicatorFromWindow()
      }
    )
  }

  func checkForStudyUpdate(study: Study?) {
    if study?.version != study?.newVersion {
      WCPServices().getStudyUpdates(study: study!, delegate: self)
    } else {
      checkDatabaseForStudyInfo(study: study!)
    }
  }
}

// MARK: - Applied filter delegate

extension StudyListViewController: StudyFilterDelegates {

  // Based on applied filter call WS
  func appliedFilter(
    studyStatus: [String],
    participationStatus: [String],
    searchText: String
  ) {
    var previousCollectionData: [[String]] = []

    previousCollectionData.append(studyStatus)
    let currentUser = User.currentUser.userType ?? .anonymousUser
    if currentUser == .loggedInUser {
      previousCollectionData.append(participationStatus)
    }

    StudyFilterHandler.instance.previousAppliedFilters = previousCollectionData
    StudyFilterHandler.instance.searchText = ""

    if studyStatus.isEmpty
      || (participationStatus.isEmpty && currentUser != .anonymousUser)
        && searchText.isEmpty
    {
      self.studiesList = []
      refreshTableViewWith(searchText: searchText)
      return
    }
    // 2. Filter by study status.
    var statusFilteredStudies: [Study] = []
    if studyStatus.count > 0 {
      statusFilteredStudies = allStudyList.filter { studyStatus.contains($0.status.rawValue) }
    }

    // 3. Filter by user participation status.
    var participationFilteredStudies: [Study] = []
    if participationStatus.count > 0 {
      participationFilteredStudies = allStudyList.filter {
        participationStatus.contains($0.userParticipateState.status.description)
      }
    }

    // 4. Filter by searched text.
    var searchTextFilteredStudies: [Study] = []
    if !searchText.isEmpty {
      searchTextFilteredStudies = allStudyList.filter {
        ($0.name?.containsIgnoringCase(searchText))!
      }
    }

    // 5. Perform Intersections.
    let statusFilteredStudiesSet = Set<Study>(statusFilteredStudies)
    let pariticipationStatusFilteredSet = Set<Study>(participationFilteredStudies)
    let setSearchedTextStudies = Set<Study>(searchTextFilteredStudies)

    var statusFilteredSet = Set<Study>()

    if currentUser == .loggedInUser {
      statusFilteredSet = statusFilteredStudiesSet.intersection(pariticipationStatusFilteredSet)
    } else {
      statusFilteredSet = statusFilteredStudiesSet
    }
    if !setSearchedTextStudies.isEmpty, !searchText.isEmpty {
      statusFilteredSet = statusFilteredSet.intersection(setSearchedTextStudies)
    }

    // 6. Assigning Filtered result to Studlist
    let allStudiesArray: [Study] = Array(statusFilteredSet)
    studiesList = getSortedStudies(studies: allStudiesArray)
    refreshTableViewWith(searchText: searchText)
  }

  private func refreshTableViewWith(searchText: String) {
    previousStudyList = studiesList
    tableView?.reloadData()

    if studiesList.count == 0 {
      tableView?.isHidden = true
      labelHelperText.isHidden = false
      if studyListRequestFailed {
        labelHelperText.text = kHelperTextForOffline
      } else if searchText.isEmpty {
        labelHelperText.text = kHelperTextForFilteredStudiesNotFound
      } else {
        labelHelperText.text = kHelperTextForSearchedStudiesNotFound
      }
    } else {
      tableView?.isHidden = false
      labelHelperText.isHidden = true
    }
  }

  func didCancelFilter(_: Bool) {
    // Do Nothing
  }
}

// MARK: - TableView Data source

extension StudyListViewController: UITableViewDataSource {

  func tableView(_: UITableView, numberOfRowsInSection _: Int) -> Int {
    return studiesList.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    var cellIdentifier = "studyCell"

    // check if current user is anonymous
    let user = User.currentUser
    if user.userType == .anonymousUser {
      cellIdentifier = "anonymousStudyCell"
    }

    let cell =
      (tableView.dequeueReusableCell(withIdentifier: cellIdentifier, for: indexPath)
      as? StudyListCell)!

    cell.populateCellWith(study: studiesList[indexPath.row])

    return cell
  }
}

// MARK: - TableView Delegates

extension StudyListViewController: UITableViewDelegate {
  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    tableView.deselectRow(at: indexPath, animated: true)

    if searchView != nil {
      searchView?.removeFromSuperview()
      slideMenuController()?.leftPanGesture?.isEnabled = true
    }

    let study = studiesList[indexPath.row]
    Study.updateCurrentStudy(study: study)
      UserDefaults.standard.set("327---,\(UserDefaults.standard.value(forKey: "userInfoDetails") ?? "")", forKey: "userInfoDetails")
      UserDefaults.standard.synchronize()
    performMainTaskBasedOnStudyStatus()
  }
}

// MARK: SearchBarDelegate

extension StudyListViewController: searchBarDelegate {
  func didTapOnCancel() {
    slideMenuController()?.leftPanGesture?.isEnabled = true

    if studiesList.count == 0, previousStudyList.count > 0 {
      studiesList = previousStudyList
    } else if searchView?.textFieldSearch?.text?.count == 0 {
      if StudyFilterHandler.instance.previousAppliedFilters.count > 0 {
        let previousCollectionData = StudyFilterHandler.instance.previousAppliedFilters

        // Apply Filters
        if User.currentUser.userType == .loggedInUser {
          appliedFilter(
            studyStatus: previousCollectionData.first!,
            participationStatus: previousCollectionData[1],
            searchText: ""
          )
        } else {
          appliedFilter(
            studyStatus: previousCollectionData.first!,
            participationStatus: [],
            searchText: ""
          )
        }
      } else {
        let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!

        if StudyFilterHandler.instance.filterOptions.count > 0 {
          // setting default Filters
          let filterStrings = appDelegate.getDefaultFilterStrings()
          appliedFilter(
            studyStatus: filterStrings.studyStatus,
            participationStatus: filterStrings.pariticipationsStatus,
            searchText: filterStrings.searchText
          )
        }
      }
    }

    if searchView != nil {
      searchView?.removeFromSuperview()
    }

    tableView?.reloadData()

    if studiesList.count == 0 {
      labelHelperText.text = kHelperTextForSearchedStudiesNotFound
      tableView?.isHidden = true
      labelHelperText.isHidden = false
    } else {
      tableView?.isHidden = false
      labelHelperText.isHidden = true
    }
  }

  /// Searches for the text provided in the filtered StudyList.
  /// - Parameter text: SearchText irrespective of case.
  func search(text: String) {
    if studiesList.count == 0 {
      let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!

      if StudyFilterHandler.instance.filterOptions.count > 0 {
        let filterStrings = appDelegate.getDefaultFilterStrings()

        appliedFilter(
          studyStatus: filterStrings.studyStatus,
          participationStatus: filterStrings.pariticipationsStatus,
          searchText: filterStrings.searchText
        )
      }
    }

    // filter by searched Text
    var searchTextFilteredStudies: [Study]! = []
    if text.count > 0 {
      let searchText = text.lowercased()
      searchTextFilteredStudies = studiesList.filter {
        $0.name!.lowercased().containsIgnoringCase(searchText)
      }

      StudyFilterHandler.instance.searchText = text
      previousStudyList = studiesList
      studiesList = getSortedStudies(studies: searchTextFilteredStudies)

      if studiesList.count == 0 {
        labelHelperText.text = kHelperTextForSearchedStudiesNotFound
        tableView?.isHidden = true
        labelHelperText.isHidden = false
      }
    } else {
      StudyFilterHandler.instance.searchText = ""
    }

    tableView?.reloadData()

    if studiesList.count > 0 {
      if searchView != nil {
        searchView?.removeFromSuperview()
        slideMenuController()?.leftPanGesture?.isEnabled = true
      }
    }
  }
}

// MARK: - Webservices Delegates

extension StudyListViewController: NMWebServiceDelegate {
  func startedRequest(_: NetworkManager, requestName: NSString) {
    let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
    if !self.topMostViewController().isKind(of: ORKTaskViewController.self) {
      appdelegate.window?.addProgressIndicatorOnWindowFromTop()
    }
  }

  func finishedRequest(_: NetworkManager, requestName: NSString, response: AnyObject?) {

    let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!

    if requestName as String == WCPMethods.studyList.rawValue {
      tableView.refreshControl?.endRefreshing()
      handleStudyListResponse()
      appdelegate.window?.removeProgressIndicatorFromWindow()

    } else if requestName as String == WCPMethods.studyInfo.rawValue {
      appdelegate.window?.removeProgressIndicatorFromWindow()
      navigateBasedOnUserStatus()

    } else if requestName as String == EnrollmentMethods.studyState.description {
      sendRequestToGetStudyList()

    } else if requestName as String == WCPMethods.studyUpdates.rawValue {
      appdelegate.window?.removeProgressIndicatorFromWindow()
      handleStudyUpdatedInformation()

    } else if requestName as String == RegistrationMethods.userProfile.description {
      appdelegate.window?.removeProgressIndicatorFromWindow()
      if User.currentUser.settings?.passcode == true && ORKPasscodeViewController.isPasscodeStoredInKeychain() == false {
        setPassCode()

      } else {
        UserDefaults.standard.set(false, forKey: kPasscodeIsPending)
        UserDefaults.standard.synchronize()
      }

    } else if requestName as String == EnrollmentMethods.updateStudyState.description {
      appdelegate.window?.removeProgressIndicatorFromWindow()
    }
  }

  func failedRequest(_: NetworkManager, requestName: NSString, error: NSError) {

    let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
    appdelegate.window?.removeProgressIndicatorFromWindow()

    if error.code == HTTPError.forbidden.rawValue {  // unauthorized Access
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
      if requestName as String == EnrollmentMethods.studyState.description {
        sendRequestToGetStudyList()

      } else if requestName as String == WCPMethods.studyList.rawValue {
        studyListRequestFailed = true
        loadStudiesFromDatabase()

      } else {
        if requestName as String == RegistrationMethods.userProfile.description {}

        tableView.refreshControl?.endRefreshing()
        UIUtilities.showAlertWithTitleAndMessage(
          title: NSLocalizedString(kErrorTitle, comment: "") as NSString,
          message: error.localizedDescription as NSString
        )
      }
    }
  }
}

// MARK: - StudyHomeViewDontroller Delegate

extension StudyListViewController: StudyHomeViewDontrollerDelegate {
  func studyHomeJoinStudy() {
    DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
      let appdelegate = (UIApplication.shared.delegate as? AppDelegate)!
      appdelegate.window?.removeProgressIndicatorFromWindow()

      let leftController = (self.slideMenuController()?.leftViewController as? LeftMenuViewController)!
      leftController.changeViewController(.reachOutSignIn)
    }
  }
}

// MARK: - ORKTaskViewController Delegate

extension StudyListViewController: ORKTaskViewControllerDelegate {
  func taskViewControllerSupportsSaveAndRestore(_: ORKTaskViewController) -> Bool {
    return true
  }

  public func taskViewController(
    _ taskViewController: ORKTaskViewController,
    didFinishWith reason: ORKTaskViewControllerFinishReason,
    error _: Error?
  ) {
    switch reason {
    case ORKTaskViewControllerFinishReason.completed:
      let ud = UserDefaults.standard
      ud.set(false, forKey: kPasscodeIsPending)
      ud.synchronize()
    case ORKTaskViewControllerFinishReason.failed: break
    case ORKTaskViewControllerFinishReason.discarded: break
    case ORKTaskViewControllerFinishReason.saved: break
    @unknown default: break
    }

    perform(#selector(dismisscontroller), with: self, afterDelay: 1.5)
  }

  @objc func dismisscontroller() {
    dismiss(animated: true, completion: nil)
  }

  func taskViewController(
    _: ORKTaskViewController,
    stepViewControllerWillAppear _: ORKStepViewController
  ) {}
}

extension Array {
  subscript(safe index: Int) -> Element? {
    return indices ~= index ? self[index] : nil
  }
}
