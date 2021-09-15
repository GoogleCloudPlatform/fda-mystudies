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

let kConsentPdfKey = "consent"

let kUnwindToStudyListIdentifier = "unwindeToStudyListResourcesIdentifier"

private enum TableRow: ResourceRow {

  case about, consent, terms, privacy, leave

  var title: String {
    switch self {
    case .about:
      return LocalizableString.aboutStudy.localizedString
    case .consent:
      return Branding.consentPDFTitle
    case .terms:
      return LocalizableString.resourceTerms.localizedString
    case .privacy:
      return LocalizableString.resourcePrivacy.localizedString
    case .leave:
      return Branding.leaveStudyTitle
    }
  }

  var subTitle: String {
    switch self {
    case .leave:
      if Utilities.isStandaloneApp() {
        return LocalizableString.leaveSubtitle.localizedString
      } else {
        return ""
      }
    default:
      return ""
    }
  }
}

class ResourcesViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet var tableView: UITableView?

  // MARK: - Properties
  var resourceLink: String?
  var fileType: String?
  var navigateToStudyOverview: Bool? = false
  var withdrawlInformationNotFound = false
  var shouldDeleteData: Bool? = false

  var leaveStudy: String = TableRow.leave.title
  var resourceTerms: String = TableRow.terms.title
  var resourcePrivacy: String = TableRow.privacy.title
  var aboutTheStudy: String = TableRow.about.title
  var consentPDF: String = TableRow.consent.title

  private var tableRows: [ResourceRow] = []

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  override func viewDidLoad() {
    super.viewDidLoad()

    self.navigationItem.title = NSLocalizedString("Resources", comment: "")

    if StudyUpdates.studyConsentUpdated && StudyUpdates.studyEnrollAgain {
      let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
      appDelegate.checkConsentStatus(controller: self)
    }
    tableRows = getStaticResources()
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)

    self.tableView?.estimatedRowHeight = 65
    self.tableView?.rowHeight = UITableView.automaticDimension
    self.tableView?.tableFooterView = UIView()
    if Utilities.isStandaloneApp() {
      self.setNavigationBarItem()
    } else {
      self.addHomeButton()
    }
    setNeedsStatusBarAppearanceUpdate()

    self.navigationController?.setNavigationBarHidden(false, animated: true)

    self.tabBarController?.tabBar.isHidden = false

    if Study.currentStudy?.withdrawalConfigration?.message == nil
      && (Study.currentStudy?.withdrawalConfigration?.type == nil
        || Study.currentStudy?
          .withdrawalConfigration?.type == .notAvailable)
    {
      WCPServices().getStudyInformation(
        studyId: (Study.currentStudy?.studyId)!,
        delegate: self
      )

    } else if StudyUpdates.studyInfoUpdated {
      WCPServices().getStudyInformation(
        studyId: (Study.currentStudy?.studyId)!,
        delegate: self
      )

    } else {
      self.checkForResourceUpdate()
    }

  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
  }

  // MARK: - Utils

  private func getStaticResources() -> [ResourceRow] {
    if Utilities.isStandaloneApp() {
      let linkTerm: String = Branding.termsAndConditionURL
      let linkPrivacy: String = Branding.privacyPolicyURL
      if linkTerm != "" && linkPrivacy != "" {
        return [TableRow.about, TableRow.consent, TableRow.terms, TableRow.privacy, TableRow.leave]
      } else if linkTerm != "" {
        return [TableRow.about, TableRow.consent, TableRow.terms, TableRow.leave]
      } else if linkPrivacy != "" {
        return [TableRow.about, TableRow.consent, TableRow.privacy, TableRow.leave]
      }
      
      return [TableRow.about, TableRow.consent, TableRow.leave]
    }
    return [TableRow.about, TableRow.consent, TableRow.leave]
  }

  func checkForResourceUpdate() {

    if StudyUpdates.studyResourcesUpdated {
      WCPServices().getResourcesForStudy(
        studyId: (Study.currentStudy?.studyId)!,
        delegate: self
      )
    } else {
      self.checkIfResourcePresent()
    }
  }

  func updateAnchorDateLifeTime() {
    guard let currentStudy = Study.currentStudy else { return }
    AnchorDateHandler(study: currentStudy).fetchActivityAnchorDateForResource {
      (status) in
      if status {
        self.loadResourceFromDatabase()
      }
      ResourcesViewController.scheduleNotificationForResources()
    }
  }

  func checkIfResourcePresent() {
    if DBHandler.isResourcesEmpty((Study.currentStudy?.studyId)!) {
      WCPServices().getResourcesForStudy(
        studyId: (Study.currentStudy?.studyId)!,
        delegate: self
      )
    } else {
      self.loadResourceFromDatabase()
    }
  }

  func loadResourceFromDatabase() {
    guard let studyID = Study.currentStudy?.studyId else { return }
    DBHandler.loadResourcesForStudy(studyId: studyID) { (resources) in
      Study.currentStudy?.resources = resources
      self.handleResourcesReponse()
      self.updateAnchorDateLifeTime()
    }

  }

  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    if segue.identifier == "ResourceDetailViewSegueIdentifier" {

      let resourceDetail = (segue.destination as? ResourceDetailViewController)!
      resourceDetail.resource = (sender as? Resource)!
      if self.resourceLink != nil {
        resourceDetail.requestLink = self.resourceLink!
      }
      if self.fileType != nil {
        resourceDetail.type = self.fileType!
      }
      resourceDetail.hidesBottomBarWhenPushed = true
    }
  }

  @IBAction func homeButtonAction(_ sender: AnyObject) {
    self.navigationController?.navigationBar.isHidden = false
    self.performSegue(withIdentifier: kUnwindToStudyListIdentifier, sender: self)

  }

  func handleResourcesReponse() {

    tableRows.removeAll()
    var resources: [ResourceRow] = []

    let todayDate = Date()

    for resource in (Study.currentStudy?.resources)! {

      if resource.startDate != nil && resource.endDate != nil {

        let start = resource.startDate
        let end = resource.endDate

        let startDateResult = (start?.compare(todayDate))! as ComparisonResult
        let endDateResult = (end?.compare(todayDate))! as ComparisonResult

        // compare lifetime
        if (startDateResult == .orderedAscending || startDateResult == .orderedSame)
          && (endDateResult == .orderedDescending || endDateResult == .orderedSame)
        {

          resources.append(resource)

          // compare for today
          let endOfToday = resource.startDate

          if todayDate >= start! && todayDate <= endOfToday! {
            resource.availableToday = true
          }

        }
      } else {
        resources.append(resource)
      }

    }
    tableRows = [TableRow.about, TableRow.consent] + resources
    if Utilities.isStandaloneApp() {
      let linkTerm: String = Branding.termsAndConditionURL
      let linkPrivacy: String = Branding.privacyPolicyURL
      if linkTerm != "" && linkPrivacy != "" {
        tableRows.append(TableRow.terms)
        tableRows.append(TableRow.privacy)
      } else if linkTerm != "" {
        tableRows.append(TableRow.terms)
      } else if linkPrivacy != "" {
        tableRows.append(TableRow.privacy)
      }
    }
    tableRows.append(TableRow.leave)
    tableView?.isHidden = false
    tableView?.reloadData()

    StudyUpdates.studyResourcesUpdated = false
    DBHandler.updateMetaDataToUpdateForStudy(study: Study.currentStudy!, updateDetails: nil)
  }

  func handelTerms() {
    let link: String = Branding.termsAndConditionURL
    let title: String = kNavigationTitleTerms
    
    guard !link.isEmpty else { return }
    let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
    let webViewController =
      (loginStoryboard.instantiateViewController(withIdentifier: "WebViewController")
        as? UINavigationController)!
    let webview = (webViewController.viewControllers[0] as? WebViewController)!
    webview.requestLink = link
    webview.title = title
    self.navigationController?.present(webViewController, animated: true, completion: nil)
  }
  
  func handelPrivacy() {
    let link: String = Branding.privacyPolicyURL
    let title: String = kNavigationTitlePrivacyPolicy
    
    guard !link.isEmpty else { return }
    let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
    let webViewController =
      (loginStoryboard.instantiateViewController(withIdentifier: "WebViewController")
        as? UINavigationController)!
    let webview = (webViewController.viewControllers[0] as? WebViewController)!
    webview.requestLink = link
    webview.title = title
    self.navigationController?.present(webViewController, animated: true, completion: nil)
  }
  
  func handleLeaveStudy() {

    var withdrawalMessage = Study.currentStudy?.withdrawalConfigration?.message

    var withdrawalType = Study.currentStudy?.withdrawalConfigration?.type

    if withdrawalMessage == nil {
      withdrawalMessage = Utilities.isStandaloneApp() ? kResourceLeaveStandaloneStudy : kResourceLeaveGatewayStudy
    }

    if withdrawalType == nil || withdrawalType == .notAvailable {

      withdrawlInformationNotFound = true
      withdrawalType = .notAvailable
      noWithdrawInfoAlert(withdrawalMessage ?? "Are you sure you want to leave the study?")
    } else {
      UIUtilities.showAlertMessageWithTwoActionsAndHandler(
        NSLocalizedString((leaveStudy + " ?"), comment: ""),
        errorMessage: NSLocalizedString(withdrawalMessage!, comment: ""),
        errorAlertActionTitle: NSLocalizedString("Cancel", comment: ""),
        errorAlertActionTitle2: NSLocalizedString("Proceed", comment: ""),
        viewControllerUsed: self,
        action1: {},
        action2: {
          
          switch withdrawalType! as StudyWithdrawalConfigrationType {
          
          case .askUser:
            
            UIUtilities.showAlertMessageWithThreeActionsAndHandler(
              kImportantNoteMessage,
              errorMessage: kRetainDataOnLeaveStudy,
              errorAlertActionTitle: "Retain my data",
              errorAlertActionTitle2: "Delete my data",
              viewControllerUsed: self,
              action1: {
                // Retain Action
                
                self.shouldDeleteData = false
                self.withdrawalFromStudy(deleteResponse: false)
                
              },
              action2: {
                
                // Delete action
                self.shouldDeleteData = true
                self.withdrawalFromStudy(deleteResponse: true)
                
              }
            )
            
          case .deleteData:
            self.shouldDeleteData = true
            self.withdrawalFromStudy(deleteResponse: true)
            
          case .noAction:
            self.shouldDeleteData = false
            self.withdrawalFromStudy(deleteResponse: false)
            
          default: break
          }
        }
      )
    }

  }
  
  func noWithdrawInfoAlert(_ withdrawalMessage: String) {
    UIUtilities.showAlertMessageWithTwoActionsAndHandler(
      "",
      errorMessage: NSLocalizedString(withdrawalMessage, comment: ""),
      errorAlertActionTitle: NSLocalizedString("Yes", comment: ""),
      errorAlertActionTitle2: NSLocalizedString("Cancel", comment: ""),
      viewControllerUsed: self,
      action1: {
        self.shouldDeleteData = false
        self.withdrawalFromStudy(deleteResponse: false)
      },
      action2: {}
    )
  }

  func navigateToStudyHome() {

    let studyStoryBoard = UIStoryboard.init(name: kStudyStoryboard, bundle: Bundle.main)
    let studyHomeController =
      (studyStoryBoard.instantiateViewController(
        withIdentifier: String(describing: StudyHomeViewController.classForCoder())
      )
      as? StudyHomeViewController)!
    studyHomeController.hideViewConsentAfterJoining = true
    studyHomeController.loadViewFrom = .resource
    studyHomeController.hidesBottomBarWhenPushed = true
    self.navigationController?.pushViewController(studyHomeController, animated: true)

  }

  func navigateToWebView(link: String?, htmlText: String?, pdfData: Data?) {

    let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
    let webViewController =
      (loginStoryboard.instantiateViewController(withIdentifier: "WebViewController")
      as? UINavigationController)!
    let webView = (webViewController.viewControllers[0] as? WebViewController)!
    webView.isEmailAvailable = true

    if pdfData != nil {
      webView.pdfData = pdfData
    }

    self.navigationController?.present(webViewController, animated: true, completion: nil)
  }

  func checkDatabaseForStudyInfo(study: Study) {

    DBHandler.loadStudyOverview(studyId: (study.studyId)!) { (overview) in
      if overview != nil {
        study.overview = overview
        self.navigateToStudyHome()

      } else {

        self.navigateToStudyOverview = true
        self.sendRequestToGetStudyInfo(study: study)
      }
    }
  }

  func sendRequestToGetStudyInfo(study: Study) {

    WCPServices().getStudyInformation(studyId: study.studyId, delegate: self)
  }

  func pushToResourceDetails(with consentPath: String) {

    let path = AKUtility.baseFilePath + "/study"
    let fullPath = path + "/" + consentPath

    let pdfData = FileDownloadManager.decrytFile(pathURL: URL(string: fullPath))

    var isPDF: Bool = false
    if (pdfData?.count ?? 0) >= 1024  // only check if bigger
    {
      var pdfBytes = [UInt8]()
      pdfBytes = [0x25, 0x50, 0x44, 0x46]
      let pdfHeader = NSData(bytes: pdfBytes, length: 4)

      let myRange: Range = 0..<1024
      let foundRange = pdfData?.range(of: pdfHeader as Data, options: .anchored, in: myRange)
      if foundRange != nil && (foundRange?.count)! > 0 {
        isPDF = true
      } else {
        isPDF = false
        let currentStudy = Study.currentStudy
        ConsentServices().getConsentPDFForStudy(
          studyId: currentStudy?.studyId ?? "",
          consentVersion: currentStudy?.signedConsentVersion ?? "",
          delegate: self
        )
      }
    }

    if pdfData != nil && isPDF {
      self.navigateToWebView(link: "", htmlText: "", pdfData: pdfData)
    }
  }

  func saveConsentPdfToLocal(base64dataString: String) {

    let consentData = NSData(base64Encoded: base64dataString, options: .ignoreUnknownCharacters)

    var fullPath: String!
    let path = AKUtility.baseFilePath + "/study"
    let fileName: String = "Consent" + "_" + "\((Study.currentStudy?.studyId)!)" + ".pdf"

    fullPath = path + "/" + fileName

    if !FileManager.default.fileExists(atPath: path) {
      try? FileManager.default.createDirectory(
        atPath: path,
        withIntermediateDirectories: true,
        attributes: nil
      )
    }

    do {

      if FileManager.default.fileExists(atPath: fullPath) {
        try FileManager.default.removeItem(atPath: fullPath)
      }

      FileManager.default.createFile(
        atPath: fullPath,
        contents: consentData as Data?,
        attributes: [:]
      )

      let defaultPath = fullPath

      fullPath = "file://" + "\(fullPath!)"

      do {
        try consentData?.write(to: URL(string: fullPath!)!)
      } catch {
        Logger.sharedInstance.error(error)
      }

      FileDownloadManager.encyptFile(pathURL: URL(string: defaultPath!)!)

      Study.currentStudy?.signedConsentFilePath = fileName
      DBHandler.saveConsentInformation(study: Study.currentStudy!)

      self.pushToResourceDetails(with: fileName)

    } catch let error as NSError {
      Logger.sharedInstance.error(error.localizedDescription)
    }
  }

  func withdrawalFromStudy(deleteResponse: Bool) {
    let participantId = Study.currentStudy?.userParticipateState.participantId ?? ""
    EnrollServices().withdrawFromStudy(
      studyId: (Study.currentStudy?.studyId)!,
      participantId: participantId,
      deleteResponses: deleteResponse,
      delegate: self
    )
  }

  private func handleResponseForWithdraw(response: JSONDictionary) {
    // Clear all local data storage.

    // Delete the resources documents.
    AKUtility.deleteDirectoryFromDocuments(name: ResourceDetailViewController.resouceDirectory)

    let currentUser = User.currentUser
    let userActivityStatusList: [UserActivityStatus] = currentUser.participatedActivites.filter({
      $0.studyId == (Study.currentStudy?.studyId)!
    })

    for activityStatus in userActivityStatusList {
      let index = currentUser.participatedActivites.firstIndex(
        where: { $0.activityId == activityStatus.activityId && $0.studyId == activityStatus.studyId })
      currentUser.participatedActivites.remove(at: index!)
    }

    if let studyID = Study.currentStudy?.studyId {
      // Clear database storage
      DBHandler.deleteStudyData(studyId: studyID)

      // Clear local notification for study
      DBHandler.deleteStudyDBLocalNotifications(for: studyID)
    }

    // Update status to false so notification can be registered again
    Study.currentStudy?.activitiesLocalNotificationUpdated = false
    DBHandler.updateLocalNotificationScheduleStatus(
      studyId: (Study.currentStudy?.studyId)!,
      status: false
    )

    LocalNotification.refreshAllLocalNotification()

    self.removeProgressIndicator()
    self.navigationController?.navigationBar.isHidden = false

    if Utilities.isStandaloneApp() {

      UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
      Study.currentStudy = nil
      self.slideMenuController()?.leftViewController?.navigationController?
        .popToRootViewController(
          animated: true
        )
      DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
        UIApplication.shared.keyWindow?.removeProgressIndicatorFromWindow()
      }
    } else {
      self.performSegue(withIdentifier: "unwindeToStudyListResourcesIdentifier", sender: self)
    }

  }

  private func handleStudyInfoResponse(response: JSONDictionary) {
    StudyUpdates.studyInfoUpdated = false
    DBHandler.updateMetaDataToUpdateForStudy(study: Study.currentStudy!, updateDetails: nil)

    if self.navigateToStudyOverview == true {
      self.removeProgressIndicator()
      // this means that about the study has been tapped and get study info has been called
      self.navigateToStudyOverview = false
      self.tabBarController?.tabBar.isHidden = true

      self.navigateToStudyHome()

    } else if self.withdrawlInformationNotFound {

      self.removeProgressIndicator()
      self.withdrawlInformationNotFound = false
    } else {
      self.checkForResourceUpdate()
    }
    self.removeProgressIndicator()
  }

}

// MARK: TableView Data source
extension ResourcesViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return tableRows.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    if let cell = tableView.dequeueReusableCell(withIdentifier: kResourcesTableViewCell, for: indexPath)
      as? ResourcesTableViewCell
    {
      let rowType = tableRows[indexPath.row] as? TableRow
      if let row = rowType {
        cell.populateCellData(data: row.title, subTitle: row.subTitle)
      } else if let resource = self.tableRows[indexPath.row] as? Resource {
        // Update UI with resource data
        cell.populateCellData(data: resource.title)
        cell.animateAvailability(for: resource)
      }
      return cell
    }
    return UITableViewCell()
  }

}

// MARK: TableView Delegates
extension ResourcesViewController: UITableViewDelegate {

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    tableView.deselectRow(at: indexPath, animated: true)

    guard let currentStudy = Study.currentStudy else { return }

    let rowType = tableRows[safe: indexPath.row] as? TableRow

    if let row = rowType {
      switch row {
      case .about:
        self.checkDatabaseForStudyInfo(study: currentStudy)
      case .consent:
        if let consentPath = Study.currentStudy?.signedConsentFilePath, !consentPath.isEmpty {
          self.pushToResourceDetails(with: consentPath)
        } else {
          ConsentServices().getConsentPDFForStudy(
            studyId: currentStudy.studyId ?? "",
            consentVersion: currentStudy.signedConsentVersion ?? "",
            delegate: self
          )
        }
      case .leave:
        self.handleLeaveStudy()
      case .terms:
        handelTerms()
      case .privacy:
        handelPrivacy()
      }
    } else if let resource = self.tableRows[indexPath.row] as? Resource {
      resourceLink = resource.file?.getFileLink()
      fileType = resource.file?.getMIMEType()
      self.performSegue(withIdentifier: "ResourceDetailViewSegueIdentifier", sender: resource)
    }

  }

}

extension ResourcesViewController: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    self.addProgressIndicator()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    switch requestName as String {

    case WCPMethods.resources.method.methodName:
      self.removeProgressIndicator()
      self.loadResourceFromDatabase()

    case EnrollmentMethods.withdrawfromstudy.method.methodName:
      if !Utilities.isStandaloneApp() {
        if let response = response as? JSONDictionary {
          handleResponseForWithdraw(response: response)
        }
      } else {
        if let currentStudy = Study.currentStudy {
          let studyID = currentStudy.studyId ?? ""
          let studyName = currentStudy.name ?? ""
          let withdrawlInfo = currentStudy.withdrawalConfigration ?? StudyWithdrawalConfigration()
          let participantID = currentStudy.userParticipateState.participantId ?? ""
          let studyToDelete = StudyToDelete(
            studyId: studyID,
            participantId: participantID,
            studyName: studyName,
            withdrawalConfigration: withdrawlInfo
          )
          UserServices().deActivateAccount(
            studiesToDelete: [studyToDelete],
            delegate: self
          )
        } else {
          handleResponseForWithdraw(response: [:])
        }
      }

    case RegistrationMethods.deactivate.method.methodName:
      if let response = response as? JSONDictionary {
        handleResponseForWithdraw(response: response)
      }

    case RegistrationMethods.updatePreferences.method.methodName:
      self.removeProgressIndicator()

    case WCPMethods.studyInfo.rawValue:
      if let response = response as? JSONDictionary {
        handleStudyInfoResponse(response: response)
      }

    case ConsentServerMethods.consentDocument.method.methodName:
      self.removeProgressIndicator()
      let consentDict: [String: Any] = ((response as? [String: Any])![kConsentPdfKey] as? [String: Any])!

      if Utilities.isValidObject(someObject: consentDict as AnyObject?) {

        if Utilities.isValidValue(someObject: consentDict[kConsentVersion] as AnyObject?) {
          Study.currentStudy?.signedConsentVersion =
            consentDict[kConsentVersion]
            as? String
        } else {
          Study.currentStudy?.signedConsentVersion = "No_Version"
        }

        if Utilities.isValidValue(someObject: consentDict[kConsentPdfContent] as AnyObject?) {
          self.saveConsentPdfToLocal(
            base64dataString: (consentDict[kConsentPdfContent] as? String)!
          )
        }
      }

    default:
      self.removeProgressIndicator()

    }

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    if error.code == HTTPError.forbidden.rawValue {  //unauthorized  // unauthorized
      self.removeProgressIndicator()
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

      if requestName as String == WCPMethods.resources.method.methodName {
        self.removeProgressIndicator()
        tableRows = getStaticResources()
        self.tableView?.isHidden = false
        self.tableView?.reloadData()
      } else if requestName as String == EnrollmentMethods.withdrawfromstudy.description {
        self.removeProgressIndicator()
        UIUtilities.showAlertWithTitleAndMessage(
          title: NSLocalizedString(kErrorTitle, comment: "") as NSString,
          message: error.localizedDescription as NSString
        )
      } else {
        self.removeProgressIndicator()
        UIUtilities.showAlertWithTitleAndMessage(
          title: NSLocalizedString(kErrorTitle, comment: "") as NSString,
          message: error.localizedDescription as NSString
        )
        // checkDB if there is resources
        self.checkForResourceUpdate()
      }
    }
  }
}

extension String {
  /// Decodes string with html encoding.
  public var htmlDecoded: String {
    guard let encodedData = self.data(using: .utf8) else { return self }

    let attributedOptions: [NSAttributedString.DocumentReadingOptionKey: Any] = [
      .documentType: NSAttributedString.DocumentType.html,
      .characterEncoding: String.Encoding.utf8.rawValue,
    ]

    do {
      let attributedString = try NSAttributedString(
        data: encodedData,
        options: attributedOptions,
        documentAttributes: nil
      )
      return attributedString.string
    } catch {
      Logger.sharedInstance.error("Error: \(error)")
      return ""
    }
  }
}

extension ResourcesViewController {

  /// Schedules notifications for all the resources from DB associated with Study.
  class func refreshNotifications() {
    guard let studyID = Study.currentStudy?.studyId else { return }
    DBHandler.loadResourcesForStudy(studyId: studyID) { (resources) in
      Study.currentStudy?.resources = resources
      ResourcesViewController.scheduleNotificationForResources()
    }
  }

  final private class func scheduleNotificationForResources() {

    guard let study = Study.currentStudy,
      let resources = study.resources,
      let studyID = study.studyId
    else { return }

    for resource in resources {

      if resource.povAvailable,
        let startDate = resource.startDate,
        let endDate = resource.endDate,
        let resourceID = resource.resourcesId
      {
        let notificationID = resourceID + study.studyId
        DBHandler.isNotificationSetFor(
          notification: notificationID,
          completionHandler: { (found) in
            if !found {
              // Set start time of notification as 9 AM.
              let notification = AppLocalNotification(
                with: resource,
                id: notificationID,
                startDate: startDate,
                endDate: endDate,
                studyID: studyID
              )
              // Save Notification to Database
              DBHandler.saveLocalNotification(
                notification: notification
              )
              let message = resource.notificationMessage ?? ""
              let userInfo =
                [
                  "studyId": studyID,
                  "type": "resource",
                ] as JSONDictionary
              LocalNotification.scheduleNotificationOn(
                date: startDate,
                message: message,
                userInfo: userInfo,
                id: notification.id
              )
            }
          }
        )
      }
    }
  }
}
