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
import ResearchKit
import UIKit

let kActivities = "activities"

let kActivityUnwindToStudyListIdentifier = "unwindeToStudyListIdentier"
let kActivityUpcomingAlertMessage =
  """
  This is an upcoming event
  """

let kActivityAbondonedAlertMessage =
  """
  The next run of this activity is not available yet. Please try again later.
  """

enum ActivityAvailabilityStatus: Int {
  case current
  case upcoming
  case past
}

class ActivitiesViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet var tableView: UITableView?

  @IBOutlet var labelNoNetworkAvailable: UILabel?

  // MARK: - Properties
  private lazy var tableViewSections: [[String: Any]]! = []

  private lazy var lastFetelKickIdentifer: String = ""  //TEMP
  private lazy var selectedIndexPath: IndexPath? = nil

  private lazy var isAnchorDateSet: Bool = false
  private lazy var taskControllerPresented = false

  /// To fetch the updated Activities.
  var refreshControl: UIRefreshControl?

  private lazy var allActivityList: [[String: Any]]! = []

  private var lastActivityResponse: JSONDictionary?

  /// Holds the applied FilterTypes.
  var selectedFilter: ActivityFilterType?

  private lazy var managedResult: [String: Any] = [:]

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  fileprivate func presentUpdatedConsent() {
    let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
    appDelegate.checkConsentStatus(controller: self)
  }

  // MARK: - Viewcontroller Lifecycle

  override func viewDidLoad() {
    super.viewDidLoad()

    addObservers()
    selectedFilter = ActivityFilterType.all

    self.tableView?.estimatedRowHeight = 126
    self.tableView?.rowHeight = UITableView.automaticDimension
    self.tableView?.tableFooterView = UIView()
    self.navigationItem.title = NSLocalizedString("Study activities", comment: "")
    self.tableView?.sectionHeaderHeight = 30

    self.navigationController?.navigationItem.rightBarButtonItem?.tintColor = UIColor.gray

    if (Study.currentStudy?.studyId) != nil {
      if StudyUpdates.studyConsentUpdated && StudyUpdates.studyEnrollAgain {
        NotificationHandler.instance.activityId = ""
        if StudyUpdates.studyVersion != nil {
          Study.currentStudy?.newVersion = StudyUpdates.studyVersion
        }
        presentUpdatedConsent()
      }
    }

    // create refresh control for pull to refresh
    refreshControl = UIRefreshControl()
    refreshControl?.attributedTitle = NSAttributedString(string: "Pull to refresh")
    refreshControl?.addTarget(
      self,
      action: #selector(refresh(sender:)),
      for: UIControl.Event.valueChanged
    )
    tableView?.addSubview(refreshControl!)

    setupStandaloneNotifications()
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    self.navigationController?.interactivePopGestureRecognizer?.isEnabled = false

    if Utilities.isStandaloneApp() {
      self.setNavigationBarItem()
    } else {
      self.addHomeButton()
    }

    if !taskControllerPresented {
      taskControllerPresented = false
      self.checkForActivitiesUpdates()
    }

    if tableViewSections.count == 0 {
      self.tableView?.isHidden = true
      self.labelNoNetworkAvailable?.isHidden = false

    } else {
      self.tableView?.isHidden = false
      self.labelNoNetworkAvailable?.isHidden = true
    }
    checkBlockerScreen()

  }

  // MARK: - Helper Methods

  private func setupStandaloneNotifications() {
    if Utilities.isStandaloneApp() {
      // Set notifications for standalone app here.
      DispatchQueue.main.async {
        StudyListViewController.configureNotifications()
      }
    }
  }

  private func addObservers() {
    // Add activity refresh notification observer.
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(refreshActivities),
      name: kRefreshActivities,
      object: nil
    )
  }

  @objc private func refreshActivities() {
    loadActivitiesFromDatabase()
  }

  /// Checks for Activity updates from WCP.
  func checkForActivitiesUpdates() {

    if StudyUpdates.studyActivitiesUpdated {

      self.sendRequestToGetActivityStates()

      /// Update status to false so notification can be registered again.
      Study.currentStudy?.activitiesLocalNotificationUpdated = false
      DBHandler.updateLocalNotificationScheduleStatus(
        studyId: Study.currentStudy?.studyId ?? "",
        status: false
      )

    } else {
      self.refreshControl?.endRefreshing()
      self.fetchActivityAnchorDateResponse()
    }
  }

  /// Verifies whether if FetalKick Task is Still running and calculate the time difference.
  func checkIfFetalKickCountRunning() {

    let ud = UserDefaults.standard

    if ud.bool(forKey: "FKC") && ud.object(forKey: kFetalKickStartTimeStamp) != nil {

      let activityId = (ud.object(forKey: kFetalKickActivityId) as? String)!
      let activity = Study.currentStudy?.activities?.filter({ $0.actvityId == activityId })
        .last

      Study.updateCurrentActivity(activity: activity!)
      // check in database
      DBHandler.loadActivityMetaData(
        activity: activity!,
        completionHandler: { (found) in

          if found {
            self.createActivity()
          }

        }
      )

    } else {
      /// check if user navigated from notification
      if !NotificationHandler.instance.activityId.isEmpty {
        userDidNavigateFromNotification()
      }
    }
  }

  func userDidNavigateFromNotification() {
    let activityId = NotificationHandler.instance.activityId
    let rowDetail = tableViewSections[0]
    let activities = rowDetail["activities"] as? [Activity] ?? []
    if let index = activities.firstIndex(where: { $0.actvityId == activityId }),
      let tableView = self.tableView
    {
      let indexPath = IndexPath(row: index, section: 0)
      self.selectedIndexPath = indexPath
      tableView.selectRow(at: indexPath, animated: true, scrollPosition: .middle)
      tableView.delegate?.tableView?(tableView, didSelectRowAt: indexPath)
    }
    NotificationHandler.instance.reset()
  }

  @objc func refresh(sender: AnyObject) {
    WCPServices().getStudyUpdates(study: Study.currentStudy!, delegate: self)
  }

  func fetchActivityAnchorDateResponse() {
    guard let currentStudy = Study.currentStudy else { return }
    AnchorDateHandler(study: currentStudy).fetchActivityAnchorDateResponse { [weak self] (_) in
      self?.loadActivitiesFromDatabase()
    }
  }

  /// To load the Activities data from database.
  func loadActivitiesFromDatabase() {
    guard let studyID = Study.currentStudy?.studyId else { return }
    if DBHandler.isActivitiesEmpty(studyID) {
      self.sendRequestToGetActivityStates()
    } else {

      DBHandler.loadActivityListFromDatabase(studyId: studyID) {
        (activities) in
        if activities.count > 0 {
          Study.currentStudy?.activities = activities
          self.handleActivityListResponse()
        }
      }
    }
  }

  /// To create an activity using ORKTaskViewController.
  func createActivity() {

    IQKeyboardManager.shared.enableAutoToolbar = false

    if Utilities.isValidObject(someObject: Study.currentActivity?.steps as AnyObject?) {
      // Create ActivityBuilder instance.
      ActivityBuilder.currentActivityBuilder = ActivityBuilder()
      ActivityBuilder.currentActivityBuilder.initWithActivity(
        activity: Study.currentActivity!
      )
    }

    let task: ORKTask?
    let taskViewController: ORKTaskViewController?

    task = ActivityBuilder.currentActivityBuilder.createTask()

    if task != nil {

      // Check if restorationData is available.
      if Study.currentActivity?.currentRun.restortionData != nil {
        let restoredData = Study.currentActivity?.currentRun.restortionData

        taskViewController = ORKTaskViewController(
          task: task,
          restorationData: restoredData,
          delegate: self
        )
      } else {

        taskViewController = ORKTaskViewController(task: task, taskRun: nil)
        taskViewController?.outputDirectory = FileManager.default.urls(
          for: .documentDirectory,
          in: .userDomainMask
        ).first!
      }

      taskViewController?.showsProgressInNavigationBar = true

      taskViewController?.title = "Activity"

      // Customize appearance of TaskViewController
      UIView.appearance(whenContainedInInstancesOf: [ORKTaskViewController.self]).tintColor =
        kUIColorForSubmitButtonBackground

      taskViewController?.delegate = self
      taskControllerPresented = true
      taskViewController?.navigationBar.prefersLargeTitles = false

      taskViewController?.modalPresentationStyle = .fullScreen
      present(taskViewController!, animated: true, completion: nil)

    } else {
      // Task creation failed
      UIUtilities.showAlertMessage(
        kAlertMessageText,
        errorMessage: NSLocalizedString("Invalid data!", comment: ""),
        errorAlertActionTitle: NSLocalizedString("OK", comment: ""),
        viewControllerUsed: self
      )
    }

  }

  /// To get Activity Availability Status.
  /// - Parameter activity: Instance of `Activity` to check it's status.
  func getActivityAvailabilityStatus(activity: Activity) -> ActivityAvailabilityStatus {

    var todayDate = Date().utcDate()

    let difference = UserDefaults.standard.value(forKey: "offset") as? Int
    if difference != nil {
      todayDate = todayDate.addingTimeInterval(TimeInterval(difference!))
    }

    if let status = activity.userParticipationStatus,
      status.status == .completed,
      activity.activityRuns.count == activity.currentRunId
    {
      return .past
    } else if activity.startDate != nil && activity.endDate != nil {

      let startDateResult = (activity.startDate?.compare(todayDate))! as ComparisonResult
      let endDateResult = (activity.endDate?.compare(todayDate))! as ComparisonResult

      if startDateResult == .orderedAscending && endDateResult == .orderedDescending {
        return .current

      } else if startDateResult == .orderedDescending {
        return .upcoming

      } else if endDateResult == .orderedAscending {
        return .past
      }
    } else if activity.startDate != nil {

      let startDateResult = (activity.startDate?.compare(todayDate))! as ComparisonResult

      if startDateResult == .orderedAscending {
        return .current

      } else if startDateResult == .orderedDescending {
        return .upcoming
      }
    }
    return .current
  }

  /// To handle Activity list response.
  func handleActivityListResponse() {

    tableViewSections = []
    allActivityList = []
    let activities = Study.currentStudy?.activities

    var currentActivities: [Activity] = []
    var upcomingActivities: [Activity] = []
    var pastActivities: [Activity] = []

    var isInActiveActivitiesAreAvailable: Bool! = false
    for activity in activities! {

      if activity.state == "active" || activity.state == nil {

        let status = self.getActivityAvailabilityStatus(activity: activity)
        switch status {
        case .current:
          currentActivities.append(activity)
        case .upcoming:
          upcomingActivities.append(activity)
        case .past:
          pastActivities.append(activity)
        }
      } else {

        isInActiveActivitiesAreAvailable = true
        DBHandler.deleteDBLocalNotification(
          activityId: activity.actvityId!,
          studyId: activity.studyId!
        )
      }
    }

    if isInActiveActivitiesAreAvailable {
      LocalNotification.refreshAllLocalNotification()
    }

    // Sort as per start date
    currentActivities.sort(by: { $0.startDate?.compare($1.startDate!) == .orderedAscending })
    upcomingActivities.sort(by: { $0.startDate?.compare($1.startDate!) == .orderedAscending })
    pastActivities.sort(by: { $0.startDate?.compare($1.startDate!) == .orderedAscending })

    let sortedCurrentActivities = currentActivities.sorted(
      by: { (activity1: Activity, activity2: Activity) -> Bool in

        return
          (activity1.userParticipationStatus.status.sortIndex
          < activity2
          .userParticipationStatus
          .status.sortIndex)
      })

    let currentDetails =
      ["title": "CURRENT", "activities": sortedCurrentActivities]
      as [String: Any]
    let upcomingDetails = ["title": "UPCOMING", "activities": upcomingActivities] as [String: Any]
    let pastDetails = ["title": "PAST", "activities": pastActivities] as [String: Any]

    allActivityList.append(currentDetails)
    allActivityList.append(upcomingDetails)
    allActivityList.append(pastDetails)

    tableViewSections = allActivityList

    if self.selectedFilter == .tasks || self.selectedFilter == .surveys {

      let filterType: ActivityType! = (selectedFilter == .surveys ? .questionnaire : .activeTask)
      self.updateSectionArray(activityType: filterType)
    }

    DispatchQueue.main.async { [weak self] in
      guard let self = self else { return }
      self.tableView?.reloadData()
      self.tableView?.isHidden = false
      self.labelNoNetworkAvailable?.isHidden = true
      self.updateCompletionAdherence()
    }

    if (User.currentUser.settings?.localNotifications)! {
      if !(Study.currentStudy?.activitiesLocalNotificationUpdated)! {
        //Register LocalNotifications
        LocalNotification.registerAllLocalNotificationFor(
          activities: (Study.currentStudy?.activities)!
        ) { (_, notificationlist) in
          Study.currentStudy?.activitiesLocalNotificationUpdated = true
          DBHandler.saveRegisteredLocaNotifications(notificationList: notificationlist)
          DBHandler.updateLocalNotificationScheduleStatus(
            studyId: (Study.currentStudy?.studyId)!,
            status: true
          )
          LocalNotification.refreshAllLocalNotification()
        }

      }
    }
    self.checkIfFetalKickCountRunning()
  }

  /// Updates Activity Run Status.
  /// - Parameter status: Status of the Activity.
  func updateActivityRun(status: UserActivityStatus.ActivityStatus, alert: Bool = true) {

    guard let activity = Study.currentActivity else { return }

    let activityStatus = User.currentUser.updateActivityStatus(
      studyId: activity.studyId!,
      activityId: activity.actvityId!,
      runId: String(activity.currentRunId),
      status: status
    )
    activityStatus.compeltedRuns = activity.compeltedRuns
    activityStatus.incompletedRuns = activity.incompletedRuns
    activityStatus.totalRuns = activity.totalRuns
    activityStatus.activityVersion = activity.version

    DispatchQueue.main.asyncAfter(deadline: .now() + 1.0) { [weak self] in
      guard let strongSelf = self else { return }
      // Update participationStatus to server
      ResponseServices().updateUserActivityParticipatedStatus(
        studyId: activity.studyId!,
        participantId: Study.currentStudy?.userParticipateState.participantId ?? "",
        activityStatus: activityStatus,
        delegate: strongSelf
      )
    }

    /// Update participationStatus to DB
    DBHandler.updateParticipationStatus(for: activity)

    if status == .completed {
      self.updateCompletionAdherence(with: alert)
    }

  }

  /// Calculates the Completion & Adherence based on following criteria.
  ///
  ///     completion = ((totalCompletedRuns + totalIncompletedRuns) * 100) /  (totalRuns)
  ///     adherence =  (totalCompletedRuns*100) / (totalCompletedRuns + totalIncompletedRuns)
  ///
  /// Also alerts the user about the study Completion Status.
  func updateCompletionAdherence(with alert: Bool = true) {

    var totalRuns = 0
    var totalCompletedRuns = 0
    var totalIncompletedRuns = 0
    let activities = Study.currentStudy?.activities.filter({ $0.state == "active" })

    /// Calculate Runs
    for activity in activities! {
      totalRuns += activity.totalRuns
      totalIncompletedRuns += activity.incompletedRuns
      totalCompletedRuns += activity.compeltedRuns

    }

    Study.currentStudy?.totalCompleteRuns = totalCompletedRuns
    Study.currentStudy?.totalIncompleteRuns = totalIncompletedRuns
    /// Calculate Completion & Adherence
    let completion = ceil(
      Double(
        self.divide(lhs: (totalCompletedRuns + totalIncompletedRuns) * 100, rhs: totalRuns)
      )
    )
    let adherence = ceil(
      Double(
        self.divide(
          lhs: totalCompletedRuns * 100,
          rhs: (totalCompletedRuns + totalIncompletedRuns)
        )
      )
    )

    guard let currentStudy = Study.currentStudy else { return }

    let status = User.currentUser.udpateCompletionAndAdherence(
      studyId: currentStudy.studyId,
      completion: Int(completion),
      adherence: Int(adherence)
    )

    /// Update to server
    EnrollServices().updateCompletionAdherence(studyStatus: status, delegate: self)
    /// Update Local DB
    DBHandler.updateStudyParticipationStatus(study: currentStudy)

    /// Compose Alert based on Completion
    let halfCompletionKey = "50pcShown" + currentStudy.studyId
    let fullCompletionKey = "100pcShown" + currentStudy.studyId
    let missedKey = "totalMissed" + currentStudy.studyId

    let ud = UserDefaults.standard
    if completion > 50 && completion < 100 {

      if !(ud.bool(forKey: halfCompletionKey)) {
        ud.set(true, forKey: halfCompletionKey)
      }

    }
    if completion == 100 && alert {

      if !(ud.bool(forKey: fullCompletionKey)) {
        let message =
          "The study " + (currentStudy.name ?? "")
          + " is 100 percent complete. Thank you for your participation."
        UIUtilities.showAlertWithMessage(alertMessage: message)
        ud.set(true, forKey: fullCompletionKey)

      }
    }

    // Alerts User about Completion
    if ud.object(forKey: missedKey) == nil {
      ud.set(totalIncompletedRuns, forKey: missedKey)

    } else if let previousMissed = ud.object(forKey: missedKey) as? Int {
      ud.set(totalIncompletedRuns, forKey: missedKey)
      if previousMissed < totalIncompletedRuns {
        // show alert
        let message =
          "We noticed you missed an activity " + (Study.currentStudy?.name!)!
          + " today. Your regular participation is important, we encourage you to complete study activities before they expire."
        UIUtilities.showAlertWithMessage(alertMessage: message)
      }
    }

    ud.synchronize()

  }

  func divide(lhs: Int, rhs: Int) -> Int {
    if rhs == 0 {
      return 0
    }
    return lhs / rhs
  }

  /// To update Activity Status To InProgress.
  func updateActivityStatusToInProgress() {
    self.updateActivityRun(status: .inProgress)
  }

  /// To update Activity Status To Complete.
  func updateActivityStatusToComplete(alert: Bool) {
    self.updateActivityRun(status: .completed, alert: alert)
  }

  /// Schedules AD resources with activity response.
  /// - Parameters:
  ///   - studyID: studyID for the resource
  ///   - activityID: resource source activity ID.
  ///   - activityResponse: Anchor date response of the source activity.
  fileprivate func scheduleAnchorDateResources(
    _ studyID: String,
    _ activityID: String,
    _ activityResponse: JSONDictionary
  ) {
    DispatchQueue.main.async {
      if DBHandler.updateTargetResourceAnchorDateDetail(
        studyId: studyID,
        activityId: activityID,
        response: activityResponse
      ) {
        // Schedule notifications for resources.
        ResourcesViewController.refreshNotifications()
      }
    }
  }

  /// Save completed staus in database.
  func updateRunStatusToComplete(with alert: Bool = true) {
    guard let currentActivity = Study.currentActivity,
      let activityID = currentActivity.actvityId,
      let studyID = currentActivity.studyId
    else { return }

    let key = "Response" + studyID
    UserDefaults.standard.set(false, forKey: key)
    
    currentActivity.compeltedRuns += 1
    DBHandler.updateRunToComplete(
      runId: currentActivity.currentRunId,
      activityId: activityID,
      studyId: studyID
    )
    self.updateActivityStatusToComplete(alert: alert)
    let activityResponse = self.lastActivityResponse ?? [:]
    lastActivityResponse = [:]
    let isActivitylifeTimeUpdated = DBHandler.updateTargetActivityAnchorDateDetail(
      studyId: studyID,
      activityId: activityID,
      response: activityResponse
    )
    scheduleAnchorDateResources(studyID, activityID, activityResponse)
    if isActivitylifeTimeUpdated {
      Study.currentStudy?.activitiesLocalNotificationUpdated = false
      self.loadActivitiesFromDatabase()
    } else {
      self.tableView?.reloadData()
    }
  }

  /// Update Run Status based on Run Id.
  func updateRunStatusForRunId(runId: Int) {

    let activity = Study.currentActivity!
    activity.compeltedRuns += 1
    DBHandler.updateRunToComplete(
      runId: runId,
      activityId: activity.actvityId!,
      studyId: activity.studyId!
    )

    // update run count information
    let incompleteRuns = activity.currentRunId - activity.compeltedRuns
    activity.incompletedRuns = (incompleteRuns < 0) ? 0 : incompleteRuns
    if activity.currentRun != nil {
      // Status is not completed
      if activity.userParticipationStatus.status
        != UserActivityStatus.ActivityStatus
        .completed
      {
        var incompleteRuns = activity.currentRunId - activity.compeltedRuns
        incompleteRuns -= 1
        activity.incompletedRuns = (incompleteRuns < 0) ? 0 : incompleteRuns
      }
    }

    let activityStatus = User.currentUser.updateActivityStatus(
      studyId: activity.studyId!,
      activityId: activity.actvityId!,
      runId: String(runId),
      status: .completed
    )
    activityStatus.compeltedRuns = activity.compeltedRuns
    activityStatus.incompletedRuns = activity.incompletedRuns
    activityStatus.totalRuns = activity.totalRuns
    activityStatus.activityVersion = activity.version

    // Update User Participation Status to server
    ResponseServices().updateUserActivityParticipatedStatus(
      studyId: activity.studyId!,
      participantId: Study.currentStudy?.userParticipateState.participantId ?? "",
      activityStatus: activityStatus,
      delegate: self
    )

    // Update User Participation Status to DB
    DBHandler.updateParticipationStatus(for: activity)

    self.updateCompletionAdherence()
    self.tableView?.reloadData()

  }

  /// Handler for studyUpdateResponse.
  func handleStudyUpdatesResponse() {
    guard let currentStudy = Study.currentStudy else { return }
    Study.currentStudy?.newVersion = StudyUpdates.studyVersion
    DBHandler.updateMetaDataToUpdateForStudy(study: currentStudy, updateDetails: nil)

    //Consent Updated
    if StudyUpdates.studyConsentUpdated && StudyUpdates.studyEnrollAgain {
      presentUpdatedConsent()

    } else if StudyUpdates.studyInfoUpdated {
      WCPServices().getStudyInformation(
        studyId: currentStudy.studyId,
        delegate: self
      )

    } else {
      self.checkForActivitiesUpdates()
    }

  }

  // MARK: Api Calls

  /// Send Request To Get ActivityStates.
  func sendRequestToGetActivityStates() {
    ResponseServices().getUserActivityState(studyId: (Study.currentStudy?.studyId)!, delegate: self)
  }

  /// Send Request To Get ActivityList.
  func sendRequesToGetActivityList() {
    WCPServices().getStudyActivityList(studyId: (Study.currentStudy?.studyId)!, delegate: self)
  }

  func sendRequestToGetDashboardInfo() {
    WCPServices().getStudyDashboardInfo(studyId: (Study.currentStudy?.studyId)!, delegate: self)
  }

  func sendRequestToGetResourcesInfo() {
    WCPServices().getResourcesForStudy(studyId: (Study.currentStudy?.studyId)!, delegate: self)
  }

  // MARK: - Button Actions

  @IBAction func homeButtonAction(_ sender: AnyObject) {
    self.performSegue(withIdentifier: kActivityUnwindToStudyListIdentifier, sender: self)
  }

  @IBAction func filterButtonAction(_ sender: AnyObject) {
    let frame = self.view.frame
    if self.selectedFilter == nil {
      self.selectedFilter = ActivityFilterType.all
    }
    //create and load FilterView
    let view = ActivityFilterView.instanceFromNib(
      frame: frame,
      selectedIndex: self.selectedFilter!
    )
    view.delegate = self
    self.tabBarController?.view.addSubview(view)
  }

}

// MARK: - TableView Datasource
extension ActivitiesViewController: UITableViewDataSource {

  func numberOfSections(in tableView: UITableView) -> Int {
    return tableViewSections.count
  }

  private func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
    return 30
  }

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {

    let rowDetail = tableViewSections[section]
    let activities = (rowDetail["activities"] as? [Activity])!
    if activities.count == 0 {
      return 1
    }
    return activities.count
  }

  func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
    let view = UIView.init(frame: CGRect(x: 0, y: 0, width: tableView.frame.width, height: 30))
    view.backgroundColor = kBackgroundTableViewColor

    let dayData = tableViewSections[section]

    let statusText = (dayData["title"] as? String)!

    let label = UILabel.init(
      frame: CGRect(x: 18, y: 0, width: view.frame.size.width, height: view.frame.size.height)
    )
    label.textAlignment = NSTextAlignment.natural
    label.text = statusText
    label.font = UIFont.boldSystemFont(ofSize: 14)
    label.translatesAutoresizingMaskIntoConstraints = true
    label.textColor = kGreyColor
    view.addSubview(label)

    return view
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    let rowDetail = tableViewSections[indexPath.section]
    let activities = (rowDetail["activities"] as? [Activity])!

    if activities.count == 0 {

      let cell = tableView.dequeueReusableCell(withIdentifier: "noData", for: indexPath)
      cell.isUserInteractionEnabled = false
      return cell
    } else {
      var cell =
        (tableView.dequeueReusableCell(
          withIdentifier: kActivitiesTableViewCell,
          for: indexPath
        )
        as? ActivitiesTableViewCell)!
      cell.delegate = self

      // Cell Data Setup
      cell.backgroundColor = UIColor.clear
      let availabilityStatus = ActivityAvailabilityStatus(rawValue: indexPath.section)
      let activity = activities[indexPath.row]

      // check for scheduled frequency
      if activity.frequencyType == .scheduled {

        cell =
          (tableView.dequeueReusableCell(
            withIdentifier: kActivitiesTableViewScheduledCell,
            for: indexPath
          )
          as? ActivitiesTableViewCell)!
        cell.delegate = self
      }

      // Set Cell data
      cell.populateCellDataWithActivity(
        activity: activity,
        availablityStatus: availabilityStatus!
      )

      return cell
    }
  }
}

// MARK: - TableView Delegates
extension ActivitiesViewController: UITableViewDelegate {

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    tableView.deselectRow(at: indexPath, animated: true)

    let availabilityStatus = ActivityAvailabilityStatus(rawValue: indexPath.section)!

    switch availabilityStatus {
    case .current:

      let rowDetail = tableViewSections[indexPath.section]
      let activities = (rowDetail["activities"] as? [Activity])!

      let activity = activities[indexPath.row]
      // Check for activity run status & if run is available
      if activity.currentRun != nil {
        if activity.userParticipationStatus != nil {
          let activityRunParticipationStatus = activity.userParticipationStatus
          if activityRunParticipationStatus?.status == .yetToJoin
            || activityRunParticipationStatus?
              .status == .inProgress
          {
            Study.updateCurrentActivity(activity: activities[indexPath.row])
            // check in database
            DBHandler.loadActivityMetaData(
              activity: activities[indexPath.row],
              completionHandler: { (found) in
                if found {
                  self.createActivity()
                } else {

                  // Fetch ActivityMetaData from Server
                  WCPServices().getStudyActivityMetadata(
                    studyId: (Study.currentStudy?.studyId)!,
                    activityId: (Study.currentActivity?.actvityId)!,
                    activityVersion: (Study.currentActivity?.version)!,
                    delegate: self
                  )
                }
              }
            )
            self.updateActivityStatusToInProgress()
            self.selectedIndexPath = indexPath
          } else if activity.currentRunId <= activity.totalRuns {
            // Run not yet available.
            self.view.makeToast(
              NSLocalizedString(kActivityAbondonedAlertMessage, comment: "")
            )
          }
        }
      } else if activity.userParticipationStatus?.status == .abandoned || activity.userParticipationStatus?.status == .yetToJoin {
        // Run not available.
        self.view.makeToast(
          NSLocalizedString(kActivityAbondonedAlertMessage, comment: "")
        )
      }

    case .upcoming:
        self.view.makeToast(
          NSLocalizedString(kActivityUpcomingAlertMessage, comment: "")
        )
    case .past: break
    }
  }
}

// MARK: - ActivitiesCell Delegate
extension ActivitiesViewController: ActivitiesCellDelegate {

  func activityCell(cell: ActivitiesTableViewCell, activity: Activity) {

    let frame = self.view.frame
    let view = ActivitySchedules.instanceFromNib(frame: frame, activity: activity)
    self.tabBarController?.view.addSubview(view)

  }
}

// MARK: - ActivityFilterDelegate
extension ActivitiesViewController: ActivityFilterViewDelegate {
  func setSelectedFilter(selectedIndex: ActivityFilterType) {

    // current filter is not same as existing filter
    if self.selectedFilter != selectedIndex {

      // currently filter type is all so no need to fetch all activities
      if self.selectedFilter == .all {

        let filterType: ActivityType! = (selectedIndex == .surveys ? .questionnaire : .activeTask)
        self.updateSectionArray(activityType: filterType)

      } else {  // existing filterType is either Task or Surveys

        // load all the sections from scratch
        self.tableViewSections = []
        self.tableViewSections = allActivityList

        // applying the new filter Type
        if selectedIndex == .surveys || selectedIndex == .tasks {
          let filterType: ActivityType! = (selectedIndex == .surveys ? .questionnaire : .activeTask)
          self.updateSectionArray(activityType: filterType)
        }
      }
      self.selectedFilter = selectedIndex
      self.tableView?.reloadData()
    } else {
      //current and newly selected filter types are same
    }
  }

  func updateSectionArray(activityType: ActivityType) {

    var updatedSectionArray: [[String: Any]]! = []
    for section in tableViewSections {
      let activities = (section[kActivities] as? [Activity])!
      var sectionDict: [String: Any]! = section
      sectionDict[kActivities] = activities.filter({
        $0.type == activityType
      })

      updatedSectionArray.append(sectionDict)
    }
    tableViewSections = []
    tableViewSections = updatedSectionArray
  }

}

// MARK: - Webservice Delegates
extension ActivitiesViewController: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    let requestName = requestName as String
    if requestName != EnrollmentMethods.updateStudyState.method.methodName
      && requestName != ResponseMethods.updateActivityState.method.methodName
      && requestName != WCPMethods.studyDashboard.method.methodName
      && requestName != WCPMethods.resources.method.methodName
    {
      if requestName == ResponseMethods.activityState.method.methodName {
        self.addProgressIndicator(with: kStudySetupMessage)
      } else {
        self.addProgressIndicator()
      }
    }
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    if requestName as String == ResponseMethods.activityState.method.methodName {
      self.sendRequesToGetActivityList()
    } else if requestName as String == WCPMethods.activityList.method.methodName {

      // get DashboardInfo
      self.sendRequestToGetDashboardInfo()
      self.fetchActivityAnchorDateResponse()
      self.refreshControl?.endRefreshing()
      StudyUpdates.studyActivitiesUpdated = false
      // Update StudymetaData for Study
      DBHandler.updateMetaDataToUpdateForStudy(study: Study.currentStudy!, updateDetails: nil)

    } else if requestName as String == WCPMethods.activity.method.methodName {
      self.removeProgressIndicator()
      self.createActivity()

    } else if requestName as String == WCPMethods.studyDashboard.method.methodName {
      self.removeProgressIndicator()
      self.sendRequestToGetResourcesInfo()

    } else if requestName as String == ResponseMethods.processResponse.method.methodName {
      self.removeProgressIndicator()
      self.updateRunStatusToComplete(with: false)
      self.checkForActivitiesUpdates()

    } else if requestName as String == WCPMethods.studyUpdates.method.methodName {

      // Handle response for study updates.
      if Study.currentStudy?.version == StudyUpdates.studyVersion {

        self.loadActivitiesFromDatabase()
        self.removeProgressIndicator()
        if self.refreshControl != nil && (self.refreshControl?.isRefreshing)! {
          self.refreshControl?.endRefreshing()
        }
      } else {
        Study.currentStudy?.newVersion = StudyUpdates.studyVersion
        self.handleStudyUpdatesResponse()
      }

    } else if requestName as String == WCPMethods.studyInfo.method.methodName {

      StudyUpdates.studyInfoUpdated = false
      DBHandler.updateMetaDataToUpdateForStudy(study: Study.currentStudy!, updateDetails: nil)

      self.checkForActivitiesUpdates()
    } else if requestName as String == WCPMethods.resources.method.methodName {
      DispatchQueue.main.async {
        ResourcesViewController.refreshNotifications()
      }
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    self.removeProgressIndicator()

    if self.refreshControl != nil && (self.refreshControl?.isRefreshing)! {
      self.refreshControl?.endRefreshing()
    }
    if error.code == HTTPError.forbidden.rawValue {
      UIUtilities.showAlertMessageWithActionHandler(
        kErrorTitle,
        message: error.localizedDescription,
        buttonTitle: kTitleOk,
        viewControllerUsed: self,
        action: {
          self.fdaSlideMenuController()?.navigateToHomeAfterUnauthorizedAccess()
        }
      )
      return
    }
    let requestName = requestName as String

    switch requestName {

    case ResponseMethods.activityState.method.methodName:
      if error.code != kNoNetworkErrorCode {
        self.loadActivitiesFromDatabase()
      } else {

        self.tableView?.isHidden = true
        self.labelNoNetworkAvailable?.isHidden = false

        UIUtilities.showAlertWithTitleAndMessage(
          title: NSLocalizedString(kErrorTitle, comment: "") as NSString,
          message: error.localizedDescription as NSString
        )
      }
    case ResponseMethods.processResponse.method.methodName:
      if error.code == kNoNetworkErrorCode {
        self.updateRunStatusToComplete(with: false)
      } else {
        self.lastActivityResponse = nil
      }

    default: break
    }
  }

}

// MARK: - ORKTaskViewController Delegate
extension ActivitiesViewController: ORKTaskViewControllerDelegate {

  func taskViewControllerSupportsSaveAndRestore(_ taskViewController: ORKTaskViewController)
    -> Bool
  {
    return true
  }

  /// This method will update the result for other choices for each step
  fileprivate func updateResultForChoiceQuestions(_ taskViewController: ORKTaskViewController) {
    if let results = taskViewController.result.results as? [ORKStepResult] {

      for result in results {
        if let choiceResult = result.results?.first as? ORKChoiceQuestionResult,
          let answers = choiceResult.answer as? [Any]
        {
          var selectedChoices: [Any] = []

          var otherChoiceDict =
            answers.filter({ $0 as? JSONDictionary != nil }).first
            as? JSONDictionary
          let otherValueKey = "otherValue"
          if let otherValue = otherChoiceDict?[otherValueKey] as? String {
            otherChoiceDict?.removeValue(forKey: otherValueKey)
            answers.forEach { (value) in
              if let value = value as? String {
                if value != otherValue {
                  selectedChoices.append(value)
                }
              } else {
                selectedChoices.append(otherChoiceDict!)
              }
            }
            choiceResult.answer = selectedChoices
          }

        }
      }
    }
  }

  public func taskViewController(
    _ taskViewController: ORKTaskViewController,
    didFinishWith reason: ORKTaskViewControllerFinishReason,
    error: Error?
  ) {

    IQKeyboardManager.shared.enableAutoToolbar = true
    self.managedResult.removeAll()
    updateResultForChoiceQuestions(taskViewController)

    switch reason {

    case ORKTaskViewControllerFinishReason.completed: break

    case ORKTaskViewControllerFinishReason.failed: break

    case ORKTaskViewControllerFinishReason.discarded:

      let study = Study.currentStudy
      let activity = Study.currentActivity
      activity?.currentRun.restortionData = nil
      DBHandler.updateActivityRestortionDataFor(
        activity: activity!,
        studyId: (study?.studyId)!,
        restortionData: nil
      )

      let ud = UserDefaults.standard
      ud.removeObject(forKey: "FKC")
      ud.removeObject(forKey: kFetalKickActivityId)
      ud.removeObject(forKey: kFetalKickCounterValue)
      ud.removeObject(forKey: kFetalKickStartTimeStamp)
      ud.removeObject(forKey: kFetalkickStudyId)
      ud.removeObject(forKey: kFetalKickCounterRunId)
      ud.synchronize()

      self.checkForActivitiesUpdates()

    case ORKTaskViewControllerFinishReason.saved:

      if taskViewController.task?.identifier == "ConsentTask" {
        // Do Nothing
      } else {
        ActivityBuilder.currentActivityBuilder.activity?.restortionData =
          taskViewController
          .restorationData
      }
      self.checkForActivitiesUpdates()

    @unknown default:
      break
    }

    var response: [String: Any]?

    if taskViewController.task?.identifier == "ConsentTask" {
      consentbuilder?.consentResult?.initWithORKTaskResult(
        taskResult: taskViewController.result
      )
    } else {

      if reason == ORKTaskViewControllerFinishReason.completed {

        ActivityBuilder.currentActivityBuilder.actvityResult?.initWithORKTaskResult(
          taskResult: taskViewController.result
        )

        response = ActivityBuilder.currentActivityBuilder.actvityResult?
          .getResultDictionary()

        Study.currentActivity?.userStatus = .completed

        if ActivityBuilder.currentActivityBuilder.actvityResult?.type
          == ActivityType
          .activeTask
        {

          if (taskViewController.result.results?.count)! > 0 {

            let orkStepResult: ORKStepResult? = (taskViewController.result.results?[1] as? ORKStepResult)!

            if (orkStepResult?.results?.count)! > 0 {

              let activeTaskResultType = ActiveStepType(
                rawValue: (ActivityBuilder.currentActivityBuilder.activity?.activitySteps?
                  .first?.resultType
                  as? String)!
              )

              switch activeTaskResultType! {

              case .fetalKickCounter:

                let fetalKickResult: FetalKickCounterTaskResult? =
                  orkStepResult?
                  .results?.first
                  as? FetalKickCounterTaskResult

                let activity = Study.currentActivity

                // Create the stats for FetalKick
                if let result = fetalKickResult {

                  let value = Float(result.duration) / 60.0
                  let kickcount = Float(result.totalKickCount)
                  let dict = ActivityBuilder.currentActivityBuilder.activity?
                    .steps?.first!
                  let key = (dict?[kActivityStepKey] as? String)!

                  // Save Stats to DB
                  DBHandler.saveStatisticsDataFor(
                    activityId: (activity?.actvityId)!,
                    key: key,
                    data: value,
                    fkDuration: Int(kickcount),
                    date: Date()
                  )

                  let ud = UserDefaults.standard
                  ud.removeObject(forKey: "FKC")
                  ud.removeObject(forKey: kFetalKickActivityId)
                  ud.removeObject(forKey: kFetalKickCounterValue)
                  ud.removeObject(forKey: kFetalKickStartTimeStamp)
                  ud.removeObject(forKey: kFetalkickStudyId)
                  ud.removeObject(forKey: kFetalKickCounterRunId)
                  ud.synchronize()

                }
              case .spatialSpanMemoryStep:
                let activity = Study.currentActivity

                // Create stats for SpatialSpanMemoryStep
                let spatialSpanResult: ORKSpatialSpanMemoryResult? =
                  orkStepResult?
                  .results?.first
                  as? ORKSpatialSpanMemoryResult

                // get score
                let scores = Float((spatialSpanResult?.score)!)
                let keyScore = "Score"
                // Save Stats to DB
                DBHandler.saveStatisticsDataFor(
                  activityId: (activity?.actvityId)!,
                  key: keyScore,
                  data: Float(scores),
                  fkDuration: Int(0),
                  date: Date()
                )

                //get numberOfFailures
                let numberOfFailures = Float((spatialSpanResult?.numberOfFailures)!)
                let keyNumberOfFailures = "NumberofFailures"
                // Save Stats to DB
                DBHandler.saveStatisticsDataFor(
                  activityId: (activity?.actvityId)!,
                  key: keyNumberOfFailures,
                  data: Float(numberOfFailures),
                  fkDuration: Int(0),
                  date: Date()
                )

                // get number of Games
                let numberOfGames = Float((spatialSpanResult?.numberOfGames)!)
                let keyNumberOfGames = "NumberofGames"
                // Save Stats to DB
                DBHandler.saveStatisticsDataFor(
                  activityId: (activity?.actvityId)!,
                  key: keyNumberOfGames,
                  data: Float(numberOfGames),
                  fkDuration: Int(0),
                  date: Date()
                )

              case .towerOfHanoi:

                // Create Stats for TowersOfHonoi
                let activity = Study.currentActivity
                let tohResult: ORKTowerOfHanoiResult? =
                  orkStepResult?.results?
                  .first
                  as? ORKTowerOfHanoiResult
                let key =
                  ActivityBuilder.currentActivityBuilder.activity?.steps?
                  .first![
                    kActivityStepKey
                  ] as? String

                let numberOfMoves = tohResult?.moves?.count

                // Save Stats to DB
                DBHandler.saveStatisticsDataFor(
                  activityId: (activity?.actvityId)!,
                  key: key!,
                  data: Float(numberOfMoves!),
                  fkDuration: Int(0),
                  date: Date()
                )

              default: break
              }
            }
          }
        }
        self.lastActivityResponse = response
        // Save response to server.
        ResponseServices().processResponse(responseData: response ?? [:], delegate: self)

      }
    }
    taskViewController.dismiss(
      animated: true,
      completion: {

        if reason == ORKTaskViewControllerFinishReason.completed {

          let ud = UserDefaults.standard
          if ud.bool(forKey: "FKC") {

            let runid = (ud.object(forKey: "FetalKickCounterRunid") as? Int)!

            if Study.currentActivity?.currentRun.runId != runid {
              // runid is changed
              self.updateRunStatusForRunId(runId: runid)
            } else {
              self.updateRunStatusToComplete()
            }
          }

        } else {
          self.tableView?.reloadData()
        }

      }
    )
  }

  func taskViewController(
    _ taskViewController: ORKTaskViewController,
    stepViewControllerWillAppear stepViewController: ORKStepViewController
  ) {

    if (taskViewController.result.results?.count)! > 1 {

      if activityBuilder?.actvityResult?.result?.count
        == taskViewController.result.results?
        .count
      {
        activityBuilder?.actvityResult?.result?.removeLast()
      } else {

        let study = Study.currentStudy
        let activity = Study.currentActivity

        if activity?.type != .activeTask {

          // Update RestortionData for Activity in DB
          DBHandler.updateActivityRestortionDataFor(
            activity: activity!,
            studyId: (study?.studyId)!,
            restortionData: taskViewController.restorationData!
          )
          activity?.currentRun.restortionData = taskViewController.restorationData!
        }

        let orkStepResult: ORKStepResult? =
          taskViewController.result.results?[
            (taskViewController.result.results?.count)! - 2
          ] as! ORKStepResult?
        let activityStepResult: ActivityStepResult? = ActivityStepResult()
        if (activity?.activitySteps?.count)! > 0 {

          let activityStepArray = activity?.activitySteps?.filter({
            $0.key == orkStepResult?.identifier
          })
          if (activityStepArray?.count)! > 0 {
            activityStepResult?.step = activityStepArray?.first
          }
        }
        activityStepResult?.initWithORKStepResult(
          stepResult: orkStepResult! as ORKStepResult,
          activityType: (ActivityBuilder.currentActivityBuilder.actvityResult?.type)!
        )

        /// check for anchor date.
        if study?.anchorDate != nil
          && study?.anchorDate?.anchorDateActivityId
            == activity?
            .actvityId
        {

          if (study?.anchorDate?.anchorDateQuestionKey)! == (activityStepResult?.key)! {
            if let value1 = activityStepResult?.value as? String {
              isAnchorDateSet = true
              study?.anchorDate?.setAnchorDateFromQuestion(date: value1)
            }
          }
        }

        /// save data for stats.
        if ActivityBuilder.currentActivityBuilder.actvityResult?.type == .questionnaire {

          if let value1 = activityStepResult?.value as? NSNumber {
            let value = value1.floatValue
            DBHandler.saveStatisticsDataFor(
              activityId: (activity?.actvityId)!,
              key: (activityStepResult?.key)!,
              data: value,
              fkDuration: 0,
              date: Date()
            )
          }
        }

        let ud = UserDefaults.standard

        let activityId: String? = ud.value(forKey: "FetalKickActivityId") as! String?
        // Go forward if fetal kick task is running
        if activity?.type == .activeTask
          && ud.bool(forKey: "FKC")
          && activityId != nil
          && activityId == Study.currentActivity?.actvityId
          && (stepViewController is ORKInstructionStepViewController)
        {

          DispatchQueue.main.asyncAfter(deadline: .now()) {
            stepViewController.goForward()
          }
        }

        // Disable back button
        if stepViewController is FetalKickCounterStepViewController {
          stepViewController.backButtonItem = nil
        }
      }
    }
  }

  // MARK: - StepViewController Delegate
  public func stepViewController(
    _ stepViewController: ORKStepViewController,
    didFinishWith direction: ORKStepViewControllerNavigationDirection
  ) {

  }

  public func stepViewControllerResultDidChange(_ stepViewController: ORKStepViewController) {

  }

  public func stepViewControllerDidFail(
    _ stepViewController: ORKStepViewController,
    withError error: Error?
  ) {

  }

  func taskViewController(
    _ taskViewController: ORKTaskViewController,
    viewControllerFor step: ORKStep
  ) -> ORKStepViewController? {

    if let step = step as? QuestionStep,
      step.answerFormat?.isKind(of: ORKTextChoiceAnswerFormat.self) ?? false
    {
      if let result = taskViewController.result.stepResult(forStepIdentifier: step.identifier) {
        self.managedResult[step.identifier] = result
      }
      var textChoiceQuestionController: TextChoiceQuestionController

      var result = taskViewController.result.result(forIdentifier: step.identifier)
      result =
        (result == nil)
        ? self.managedResult[step.identifier] as? ORKStepResult : result

      if let result = result {
        textChoiceQuestionController = TextChoiceQuestionController(
          step: step,
          result: result
        )
      } else {
        textChoiceQuestionController = TextChoiceQuestionController(step: step)
      }

      return textChoiceQuestionController
    }

    if let step = step as? CustomInstructionStep {
      return CustomInstructionStepViewController(step: step)
    }

    let storyboard = UIStoryboard.init(name: "FetalKickCounter", bundle: nil)

    if step is FetalKickCounterStep {

      let ttController =
        (storyboard.instantiateViewController(
          withIdentifier: "FetalKickCounterStepViewController"
        )
        as? FetalKickCounterStepViewController)!
      ttController.step = step
      return ttController
    } else if step is FetalKickIntroStep {

      let ttController =
        (storyboard.instantiateViewController(
          withIdentifier: "FetalKickIntroStepViewControllerIdentifier"
        )
        as? FetalKickIntroStepViewController)!
      ttController.step = step
      return ttController
    } else {
      return nil
    }

  }

  func taskViewController(
    _ taskViewController: ORKTaskViewController,
    didChange result: ORKTaskResult
  ) {

    // Saving the TextChoiceQuestionController result to publish it later.
    if taskViewController.currentStepViewController?.isKind(
      of: TextChoiceQuestionController.self
    )
      ?? false
    {
      if let result = result.stepResult(
        forStepIdentifier: taskViewController.currentStepViewController?.step?.identifier
          ?? ""
      ) {
        self.managedResult[result.identifier] = result
      }
    }
  }

}
