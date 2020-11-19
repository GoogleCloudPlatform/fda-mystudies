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

let kMessageForSharingDashboard =
  "This action will create a shareable image file of the dashboard currently seen in this section. Proceed?"

enum TableViewCells: Int {
  case welcomeCell = 0
  case percentageCell
}

class StudyDashboardViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet var tableView: UITableView?

  @IBOutlet var labelStudyTitle: UILabel?
  @IBOutlet var buttonHome: UIButton!

  var dataSourceKeysForResponse: [[String: String]] = []
  lazy var tableViewRowDetails = NSMutableArray()
  lazy var todayActivitiesArray = NSMutableArray()
  lazy var statisticsArray = NSMutableArray()

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .lightContent
  }

  private static let responseDateFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.timeZone = TimeZone.init(identifier: "America/New_York")
    formatter.dateFormat = "YYYY/MM/dd HH:mm:ss"
    return formatter
  }()

  private static let localDateFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.timeZone = TimeZone.current
    formatter.dateFormat = "YYYY/MM/dd HH:mm:ss"
    return formatter
  }()

  /// Queries the stats and charts data from response server.
  lazy var responseDataFetch: ResponseDataFetch? = {
    if let study = Study.currentStudy {
      return ResponseDataFetch(study: study)
    }
    return nil
  }()

  // MARK: - ViewController Lifecycle
  override func viewDidLoad() {
    super.viewDidLoad()

    // load plist info
    let plistPath = Bundle.main.path(
      forResource: "StudyDashboard",
      ofType: ".plist",
      inDirectory: nil
    )
    let tableViewRowDetailsdat = NSMutableArray.init(contentsOfFile: plistPath!)

    let tableviewdata = (tableViewRowDetailsdat?[0] as? NSDictionary)!

    tableViewRowDetails = (tableviewdata["studyActivity"] as? NSMutableArray)!
    todayActivitiesArray = (tableviewdata["todaysActivity"] as? NSMutableArray)!
    statisticsArray = (tableviewdata["statistics"] as? NSMutableArray)!

    labelStudyTitle?.text = Study.currentStudy?.name

    // check if consent is udpated
    if StudyUpdates.studyConsentUpdated {
      let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
      appDelegate.checkConsentStatus(controller: self)
    }

  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)

    setNeedsStatusBarAppearanceUpdate()

    // Standalone App Settings
    if Utilities.isStandaloneApp() {
      buttonHome.setImage(UIImage(named: "menu_icn"), for: .normal)
      buttonHome.tag = 200
      self.slideMenuController()?.removeLeftGestures()
      self.slideMenuController()?.removeRightGestures()
    }

    // show navigationbar
    self.navigationController?.setNavigationBarHidden(true, animated: true)
    self.tableView?.reloadData()
    getResponse()
  }

  // MARK: - Utils

  private func loadStatsFromDB(for study: Study) {
    DBHandler.loadStatisticsForStudy(studyId: study.studyId) { (statiticsList) in
      if !statiticsList.isEmpty {
        StudyDashboard.instance.statistics = statiticsList
        self.tableView?.reloadData()
      }
    }
  }

  /// Get response from Response server.
  /// If response already fetched, query from DB.
  func getResponse() {
    guard let study = Study.currentStudy else { return }
    let key = "Response" + study.studyId
    if !(UserDefaults.standard.bool(forKey: key)) {
      self.addProgressIndicator(with: kDashSetupMessage)
      responseDataFetch?.checkUpdates { [unowned self] in
        self.loadStatsFromDB(for: study)
        self.removeProgressIndicator()
      }
    } else {
      loadStatsFromDB(for: study)
    }
  }

  // MARK: - Helper Methods

  /// Used to Create Eligibility Consent Task.
  func createEligibilityConsentTask() {

    let taskViewController: ORKTaskViewController?

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

    taskViewController?.navigationBar.prefersLargeTitles = false
    taskViewController?.modalPresentationStyle = .fullScreen
    present(taskViewController!, animated: true, completion: nil)
  }

  func shareScreenShotByMail() {

    // Create the UIImage
    UIGraphicsBeginImageContextWithOptions(view.bounds.size, self.view.isOpaque, 0.0)
    view.layer.render(in: UIGraphicsGetCurrentContext()!)
    let image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()

    (self.tabBarController as? StudyDashboardTabbarViewController)!.shareScreenshotByEmail(
      image: image,
      subject: kEmailSubjectDashboard,
      fileName: kEmailSubjectDashboard
    )

  }

  // MARK: - Button Actions

  /// Home button clicked.
  @IBAction func homeButtonAction(_ sender: AnyObject) {
    let button = sender as! UIButton
    if button.tag == 200 {
      self.slideMenuController()?.openLeft()
    } else {
      self.performSegue(withIdentifier: unwindToStudyListDashboard, sender: self)
    }
  }

  /// Share to others button clicked.
  @IBAction func shareButtonAction(_ sender: AnyObject) {

    UIUtilities.showAlertMessageWithTwoActionsAndHandler(
      NSLocalizedString(kTitleMessage, comment: ""),
      errorMessage: NSLocalizedString(kMessageForSharingDashboard, comment: ""),
      errorAlertActionTitle: NSLocalizedString(kTitleOK, comment: ""),
      errorAlertActionTitle2: NSLocalizedString(kTitleCancel, comment: ""),
      viewControllerUsed: self,
      action1: {
        self.shareScreenShotByMail()
      },
      action2: {
        // Handle cancel action
      }
    )

  }

}

// MARK: - TableView Datasource
extension StudyDashboardViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return 1
  }

  func numberOfSections(in tableView: UITableView) -> Int {
    if DBHandler.isChartsAvailable(for: Study.currentStudy?.studyId ?? "") {
      return tableViewRowDetails.count + 1
    } else {
      return tableViewRowDetails.count
    }
  }

  func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {

    // Used for the last cell Height (trends cell)
    if indexPath.section == tableViewRowDetails.count {
      return 50
    }

    let data = (self.tableViewRowDetails[indexPath.section] as? NSDictionary)!

    var heightValue: CGFloat = 0
    if (data["isTableViewCell"] as? String)! == "YES" {

      // Used for Table view Height in a cell
      switch indexPath.section {
      case TableViewCells.welcomeCell.rawValue:
        heightValue = 70
      case TableViewCells.percentageCell.rawValue:
        heightValue = 200
      default:
        return 0
      }

    } else {
      // Used for Collection View Height in a cell
      if (data["isStudy"] as? String)! == "YES" {
        heightValue = 130
      } else {
        heightValue = 210
      }
    }
    return heightValue
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    var cell: UITableViewCell?

    // Used to display the last cell trends
    if indexPath.section == tableViewRowDetails.count {
      cell =
        (tableView.dequeueReusableCell(withIdentifier: kTrendTableViewCell, for: indexPath)
        as? StudyDashboardTrendsTableViewCell)!
      return cell!
    }

    let tableViewData = (tableViewRowDetails.object(at: indexPath.section) as? NSDictionary)!

    if (tableViewData["isTableViewCell"] as? String)! == "YES" {

      // Used for Table view Cell
      switch indexPath.section {
      case TableViewCells.welcomeCell.rawValue:
        cell =
          (tableView.dequeueReusableCell(
            withIdentifier: kWelcomeTableViewCell,
            for: indexPath
          )
          as? StudyDashboardWelcomeTableViewCell)!
        (cell as? StudyDashboardWelcomeTableViewCell)!.displayFirstCelldata(
          data: tableViewData
        )

      case TableViewCells.percentageCell.rawValue:
        cell =
          (tableView.dequeueReusableCell(
            withIdentifier: kPercentageTableViewCell,
            for: indexPath
          )
          as? StudyDashboardStudyPercentageTableViewCell)!
        (cell as? StudyDashboardStudyPercentageTableViewCell)!.displayThirdCellData(
          data: tableViewData
        )

      default:
        return cell!
      }

    } else {
      cell =
        (tableView.dequeueReusableCell(
          withIdentifier: kStatisticsTableViewCell,
          for: indexPath
        )
        as? StudyDashboardStatisticsTableViewCell)!
      (cell as? StudyDashboardStatisticsTableViewCell)!.displayData()
      (cell as? StudyDashboardStatisticsTableViewCell)!.buttonDay?.setTitle(
        "  DAY  ",
        for: UIControl.State.normal
      )
      (cell as? StudyDashboardStatisticsTableViewCell)!.statisticsCollectionView?.reloadData()
    }
    return cell!
  }
}

// MARK: - TableView Delegates
extension StudyDashboardViewController: UITableViewDelegate {
  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {

    tableView.deselectRow(at: indexPath, animated: true)

    if indexPath.section == tableViewRowDetails.count {
      self.performSegue(withIdentifier: "chartSegue", sender: nil)
    }
  }
}

// MARK: - Webservice Delegates
extension StudyDashboardViewController: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    self.addProgressIndicator()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    if requestName as String == WCPMethods.eligibilityConsent.method.methodName {
      self.removeProgressIndicator()
      self.createEligibilityConsentTask()
    } else if requestName as String == WCPMethods.studyDashboard.method.methodName {
      self.removeProgressIndicator()
      self.tableView?.reloadData()
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    if requestName as String == WCPMethods.consentDocument.method.methodName {
      self.removeProgressIndicator()
    } else {
      self.removeProgressIndicator()
    }
  }
}

// MARK: - ORKTaskViewController Delegate
extension StudyDashboardViewController: ORKTaskViewControllerDelegate {

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
      ConsentBuilder.currentConsent?.consentResult?.consentDocument =
        ConsentBuilder
        .currentConsent?
        .consentDocument
      ConsentBuilder.currentConsent?.consentResult?.initWithORKTaskResult(
        taskResult: taskViewController.result
      )

    case ORKTaskViewControllerFinishReason.failed: break

    case ORKTaskViewControllerFinishReason.discarded: break

    case ORKTaskViewControllerFinishReason.saved: break

    @unknown default: break
    }
    taskViewController.dismiss(animated: true, completion: nil)
  }

  func taskViewController(
    _ taskViewController: ORKTaskViewController,
    stepViewControllerWillAppear stepViewController: ORKStepViewController
  ) {

    if (taskViewController.result.results?.count)! > 1,
      activityBuilder?.actvityResult?.result?.count == taskViewController.result.results?.count
    {
      // Removing the dummy result:Currentstep result which not presented yet
      activityBuilder?.actvityResult?.result?.removeLast()
    }
    // Handling show and hide of Back Button
    // For Verified Step , Completion Step, Visual Step, Review Step, Share Pdf Step
    if stepViewController.step?.identifier == kConsentCompletionStepIdentifier
      || stepViewController.step?.identifier == kVisualStepId
      || stepViewController.step?.identifier == kReviewTitle
      || stepViewController.step?.identifier == kConsentSharePdfCompletionStep
    {

      if stepViewController.step?.identifier == kEligibilityVerifiedScreen {
        stepViewController.continueButtonTitle = kContinueButtonTitle
      }
      stepViewController.backButtonItem = nil
    }  // checking if currentstep is View Pdf Step
    else if stepViewController.step?.identifier == kConsentViewPdfCompletionStep {

      // Back button is enabled
      stepViewController.backButtonItem?.isEnabled = true

      let orkStepResult: ORKStepResult? =
        taskViewController.result.results?[
          (taskViewController.result.results?.count)! - 2
        ] as! ORKStepResult?

      let consentSignatureResult: ConsentCompletionTaskResult? =
        orkStepResult?.results?.first
        as? ConsentCompletionTaskResult

      // Checking if Signature is consented after Review Step
      if consentSignatureResult?.didTapOnViewPdf == false {
        // Directly moving to completion step by skipping Intermediate PDF viewer screen
        stepViewController.goForward()
      }
    } else {
      // Back button is enabled
      stepViewController.backButtonItem?.isEnabled = true
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

    // CurrentStep is TokenStep
    if step.identifier == kEligibilityTokenStep {  // For EligibilityToken Step
      let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)
      let ttController =
        (gatewayStoryboard.instantiateViewController(
          withIdentifier: kEligibilityStepViewControllerIdentifier
        )
        as? EligibilityStepViewController)!
      ttController.descriptionText = step.text
      ttController.step = step

      return ttController
    } else if step.identifier == kConsentSharePdfCompletionStep {  // For SharePdfCompletion Step

      var totalResults = taskViewController.result.results
      let reviewStep: ORKStepResult?
      totalResults = totalResults?.filter({ $0.identifier == kReviewTitle })
      reviewStep = totalResults?.first as! ORKStepResult?

      if (reviewStep?.identifier)! == kReviewTitle && (reviewStep?.results?.count)! > 0 {
        let consentSignatureResult: ORKConsentSignatureResult? =
          reviewStep?.results?.first
          as? ORKConsentSignatureResult

        if consentSignatureResult?.consented == false {  //User disagreed on Consent
          taskViewController.dismiss(animated: true, completion: nil)
          _ = self.navigationController?.popViewController(animated: true)
          return nil

        } else {  // User consented

          let documentCopy: ORKConsentDocument =
            ((ConsentBuilder.currentConsent?.consentDocument)!.copy()
            as? ORKConsentDocument)!

          consentSignatureResult?.apply(to: documentCopy)
          let gatewayStoryboard = UIStoryboard(name: kFetalKickCounterStep, bundle: nil)
          let ttController =
            (gatewayStoryboard.instantiateViewController(
              withIdentifier: kConsentSharePdfStoryboardId
            )
            as? ConsentSharePdfStepViewController)!
          ttController.step = step
          ttController.consentDocument = documentCopy
          return ttController
        }
      } else {
        return nil
      }
    } else if step.identifier == kConsentViewPdfCompletionStep {  // For Pdf Completion Step

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
        ttController.pdfData = result?.pdfData
        return ttController

      } else {
        return nil
      }
    } else {
      return nil
    }
  }
}
