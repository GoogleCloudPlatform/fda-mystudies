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

import UIKit

// MARK: - Api constants
let kNotificationSkip = "skip"
let kActivity = "activity"

// MARK: - Study constants
let kStudyTitle = "title"
let kStudyCategory = "category"
let kStudySponserName = "sponsorName"
let kStudyDescription = "description"
let kStudyTagLine = "tagline"
let kStudyVersion = "studyVersion"
let kStudyStatus = "status"
let kActivityStatus = "activityState"
let kStudyLogoURL = "logo"
let kStudySettings = "settings"
let kStudyEnrolling = "enrolling"
let kStudyPlatform = "platform"
let kStudyRejoin = "rejoin"
let kStudyParticipantId = "participantId"
let kStudyEnrolledDate = "enrolledDate"

// MARK: - Resources constants
let kResources = "resources"

// MARK: - Overview constants
let kOverViewInfo = "info"
let kOverviewType = "type"
let kOverviewImageLink = "image"
let kOverviewTitle = "title"
let kOverviewText = "text"
let kOverviewMediaLink = "videoLink"  // link
let kOverviewWebsiteLink = "website"

// MARK: - Notification constants
let kNotifications = "notifications"
let kNotificationId = "notificationId"
let kNotificationType = "type"
let kNotificationSubType = "subtype"
let kNotificationAudience = "audience"
let kNotificationTitle = "title"
let kNotificationMessage = "message"
let kNotificationStudyId = "studyId"
let kNotificationActivityId = "activityId"

// MARK: - Feedback constants
let kFeedbackSubject = "subject"
let kFeedbackBody = "body"

// MARK: - Contact-Us constants
let kContactusEmail = "email"
let kContactusFirstname = "firstName"

// MARK: - Study updates constants
let kStudyUpdates = "updates"
let kStudyCurrentVersion = "currentVersion"
let kEnrollAgain = "enrollAgain"
let kStudyConsent = "consent"
let kStudyActivities = "activities"
let kStudyResources = "resources"
let kStudyInfo = "info"

// MARK: - Study Withdrawal Configuration constants
let kStudyWithdrawalConfigration = "withdrawalConfig"
let kStudyWithdrawalMessage = "message"
let kStudyWithdrawalType = "type"

// MARK: - Study AnchorDate constants
let kStudyAnchorDate = "anchorDate"
let kStudyAnchorDateType = "type"
let kStudyAnchorDateActivityId = "activityId"
let kStudyAnchorDateActivityVersion = "activityVersion"
let kStudyAnchorDateQuestionKey = "key"
let kStudyAnchorDateQuestionInfo = "questionInfo"

class WCPServices: NSObject {
  let networkManager = NetworkManager.sharedInstance()
  weak var delegate: NMWebServiceDelegate?
  weak var delegateSource: NMWebServiceDelegate?

  // MARK: Requests

  /// Creates a request for App Updates
  /// - Parameter delegate: Class object to receive response
  func checkForAppUpdates(delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.versionInfo.method
    self.sendRequestWith(method: method, params: nil, headers: nil)
  }

  /// Creates a request to receive `Study` information
  /// - Parameter delegate: Class object to receive response
  func getStudyBasicInfo(_ delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.study.method
    let params: [String: String] = ["studyId": Utilities.standaloneStudyId()]
    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to receive collection of `Study`
  /// - Parameter delegate: Class object to receive response
  func getStudyList(_ delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.studyList.method
    let params = [String: Any]()
    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to receive Consent Document
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getConsentDocument(studyId: String, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let header = [kStudyId: studyId, "consentVersion": ""]
    let method = WCPMethods.consentDocument.method
    self.sendRequestWith(method: method, params: header, headers: nil)
  }

  /// Creates a request to receive Eligibility Consent Metadata
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getEligibilityConsentMetadata(studyId: String, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.eligibilityConsent.method
    let headerParams = [kStudyId: studyId]
    self.sendRequestWith(method: method, params: headerParams, headers: nil)
  }

  /// Creates a request to receive `Study` Resources
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getResourcesForStudy(studyId: String, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.resources.method
    let headerParams = [kStudyId: studyId]
    self.sendRequestWith(method: method, params: headerParams, headers: nil)
  }

  /// Creates a request to receive `Study` information
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getStudyInformation(studyId: String, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.studyInfo.method
    let params = [kStudyId: studyId]
    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to receive collection of `Activity`
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getStudyActivityList(studyId: String, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.activityList.method
    let headerParams = [kStudyId: studyId]
    self.sendRequestWith(method: method, params: headerParams, headers: nil)
  }

  /// Creates a request to receive `Activity` metadata
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - activityId: ID of `Activity`
  ///   - activityVersion: `Activity` Version
  ///   - delegate: Class object to receive response
  func getStudyActivityMetadata(
    studyId: String,
    activityId: String,
    activityVersion: String,
    delegate: NMWebServiceDelegate
  ) {
    self.delegate = delegate
    let method = WCPMethods.activity.method
    let headerParams = [
      kStudyId: studyId,
      kActivityId: activityId,
      kActivityVersion: activityVersion,
    ]
    self.sendRequestWith(method: method, params: headerParams, headers: nil)
  }

  /// Creates a request to receive `Study` dashboard information
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getStudyDashboardInfo(studyId: String, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.studyDashboard.method
    let params = [kStudyId: studyId]
    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to receive collection of Notification
  /// - Parameters:
  ///   - skip: Notification Count
  ///   - delegate: Class object to receive response
  func getNotification(skip: Int, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.notifications.method
    let user = User.currentUser
    let headerParams = [kNotificationSkip: "\(skip)",
                        kVerificationTime: user.verificationTime ?? ""]
    self.sendRequestWith(method: method, params: headerParams, headers: nil)
  }

  /// Creates a request to receive `Study` updates
  /// - Parameters:
  ///   - study: ID of `Study`
  ///   - delegate: Class object to receive response
  func getStudyUpdates(study: Study, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = WCPMethods.studyUpdates.method
    let headerParams = [
      kStudyId: study.studyId!,
      kStudyVersion: study.version ?? "0",
    ]
    self.sendRequestWith(method: method, params: headerParams, headers: nil)
  }

  // MARK: Parsers

  /// Handles `Study` information response
  /// - Parameter response: Webservice response
  func handleStudyBasicInfo(response: [String: Any]) {

    let studies = response[kStudies] as! [[String: Any]]
    var listOfStudies: [Study] = []
    for study in studies {
      let studyModelObj = Study(studyDetail: study)
      listOfStudies.append(studyModelObj)
    }
    // Assign to Gateway
    Gateway.instance.studies = listOfStudies

    // save in database
    DBHandler().saveStudies(studies: listOfStudies)
  }

  /// Handles `Study` list response
  /// - Parameter response: Webservice response
  func handleStudyList(response: [String: Any]) {

    let studies = response[kStudies] as! [[String: Any]]
    var listOfStudies: [Study] = []
    for study in studies {
      let studyModelObj = Study(studyDetail: study)
      listOfStudies.append(studyModelObj)
    }
    // Assign to Gateway
    Gateway.instance.studies = listOfStudies
    // save in database
    DBHandler().saveStudies(studies: listOfStudies)

  }

  /// Handles Consent Metadata response
  /// - Parameter response: Webservice response
  func handleEligibilityConsentMetaData(response: [String: Any]) {
    guard let consent = response[kConsent] as? [String: Any],
      let eligibility = response[kEligibility] as? [String: Any]
    else { return }

    if Utilities.isValidObject(someObject: consent as AnyObject?) {
      ConsentBuilder.currentConsent = ConsentBuilder()
      ConsentBuilder.currentConsent?.initWithMetaData(metaDataDict: consent)
    }

    if Utilities.isValidObject(someObject: eligibility as AnyObject?) {
      EligibilityBuilder.currentEligibility = EligibilityBuilder()
      EligibilityBuilder.currentEligibility?.initEligibilityWithDict(
        eligibilityDict: eligibility
      )
    }

  }

  /// Handles Gateway Resource List response
  /// - Parameter response: Webservice response
  func handleResourceListForGateway(response: [String: Any]) {

    let resources = response[kResources] as! [[String: Any]]
    var listOfResources: [Resource]! = []
    for resource in resources {
      let resourceObj = Resource(detail: resource)
      listOfResources.append(resourceObj)
    }

    // Assign to Gateway
    Gateway.instance.resources = listOfResources
  }

  /// Handles Study Resources response
  /// - Parameter response: Webservice response
  func handleResourceForStudy(response: [String: Any]) {

    let resources = response[kResources] as! [[String: Any]]
    var listOfResources: [Resource]! = []
    for resource in resources {
      let resourceObj = Resource()
      resourceObj.level = ResourceLevel.study
      resourceObj.setResource(dict: resource as NSDictionary)

      listOfResources.append(resourceObj)
    }

    // save in database
    DBHandler.saveResourcesForStudy(
      studyId: (Study.currentStudy?.studyId)!,
      resources: listOfResources
    )

    // assign to Gateway
    Study.currentStudy?.resources = listOfResources

  }

  /// Handles `Study` dashboard response
  /// - Parameter response: Webserive response
  func handleStudyDashboard(response: [String: Any]) {

    guard let dashboard = response["dashboard"] as? [String: Any] else { return }

    if Utilities.isValidObject(someObject: dashboard as AnyObject?) {

      if Study.currentStudy != nil {

        //stats
        let statsList = dashboard["statistics"] as! [[String: Any]]
        var listOfStats: [DashboardStatistics]! = []
        for stat in statsList {

          let dashboardStat = DashboardStatistics.init(detail: stat)
          listOfStats.append(dashboardStat)
        }

        StudyDashboard.instance.statistics = listOfStats
        // save stats in database
        DBHandler.saveDashBoardStatistics(
          studyId: (Study.currentStudy?.studyId)!,
          statistics: listOfStats
        )

        // charts
        let chartList = dashboard["charts"] as! [[String: Any]]
        var listOfCharts: [DashboardCharts]! = []
        for chart in chartList {

          let dashboardChart = DashboardCharts.init(detail: chart)
          listOfCharts.append(dashboardChart)
        }

        StudyDashboard.instance.charts = listOfCharts

        // save charts in database
        DBHandler.saveDashBoardCharts(
          studyId: (Study.currentStudy?.studyId)!,
          charts: listOfCharts
        )
      }
    }
  }

  /// Handles Consent Document response
  /// - Parameter response: Webserive response
  func handleConsentDocument(response: [String: Any]) {

    let consentDict = response[kConsent] as! [String: Any]

    if Utilities.isValidObject(someObject: consentDict as AnyObject?) {

      Study.currentStudy?.consentDocument = ConsentDocument()
      Study.currentStudy?.consentDocument?.initData(consentDoucumentdict: consentDict)
    }

  }

  /// Hnadles Terms and Policy response
  /// - Parameter response: Webservice response
  func handleTermsAndPolicy(response: [String: Any]) {

    TermsAndPolicy.currentTermsAndPolicy = TermsAndPolicy()
    TermsAndPolicy.currentTermsAndPolicy?.initWithDict(dict: response)

  }

  /// Handles `Study`  information
  /// - Parameter response: Webservice response
  func handleStudyInfo(response: [String: Any]) {

    if Study.currentStudy != nil {

      let overviewList = response[kOverViewInfo] as! [[String: Any]]
      var listOfOverviews: [OverviewSection] = []
      for overview in overviewList {
        let overviewObj = OverviewSection(detail: overview)
        listOfOverviews.append(overviewObj)
      }

      // create new Overview object
      let overview = Overview()
      overview.type = .study
      overview.sections = listOfOverviews
      overview.websiteLink = response[kOverViewWebsiteLink] as? String

      // update overview object to current study
      Study.currentStudy?.overview = overview

      // anchorDate
      if Utilities.isValidObject(someObject: response[kStudyAnchorDate] as AnyObject?) {

        let studyAndhorDate = StudyAnchorDate.init(
          detail: response[kStudyAnchorDate] as! [String: Any]
        )

        // update anchorDate to current study
        Study.currentStudy?.anchorDate = studyAndhorDate

        DBHandler.saveAnchorDateDetail(
          anchorDate: studyAndhorDate,
          studyId: (Study.currentStudy?.studyId)!
        )
      }

      // WithdrawalConfigration
      if Utilities.isValidObject(
        someObject: response[kStudyWithdrawalConfigration] as AnyObject?
      ) {

        let studyWithdrawalConfig = StudyWithdrawalConfigration.init(
          withdrawalConfigration: response[kStudyWithdrawalConfigration] as! [String: Any]
        )

        // update anchorDate to current study
        Study.currentStudy?.withdrawalConfigration = studyWithdrawalConfig
        DBHandler.saveWithdrawalConfigration(
          withdrawalConfigration: studyWithdrawalConfig,
          studyId: (Study.currentStudy?.studyId)!
        )
      }

      // save in database
      DBHandler.saveStudyOverview(overview: overview, studyId: (Study.currentStudy?.studyId)!)
    }

  }

  /// Handles Activity List response
  /// - Parameter response: Webservice response
  func handleStudyActivityList(response: [String: Any]) {

    // Actual
    let activities = response[kActivites] as! [[String: Any]]

    if Utilities.isValidObject(someObject: activities as AnyObject?) {

      if Study.currentStudy != nil {
        var activityList: [Activity] = []
        for activityDict in activities {

          let activity = Activity.init(
            studyId: (Study.currentStudy?.studyId)!,
            infoDict: activityDict
          )
          activityList.append(activity)
        }
        // save to current study object
        Study.currentStudy?.activities = activityList
        // save in database
        DBHandler.saveActivities(activities: (Study.currentStudy?.activities)!)
      }
    }
  }

  /// Handles Activity metadata response
  /// - Parameter response: Webservice response
  func handleGetStudyActivityMetadata(response: [String: Any]) {

    Study.currentActivity?.setActivityMetaData(
      activityDict: response[kActivity] as! [String: Any]
    )

    if Utilities.isValidObject(someObject: Study.currentActivity?.steps as AnyObject?) {

      ActivityBuilder.currentActivityBuilder = ActivityBuilder()
      ActivityBuilder.currentActivityBuilder.initWithActivity(
        activity: Study.currentActivity!
      )
    }

    // Save and Update activity meta data
    DBHandler.saveActivityMetaData(activity: Study.currentActivity!, data: response)
    DBHandler.updateActivityMetaData(activity: Study.currentActivity!)

  }

  /// Handles Notification List response
  /// - Parameter response: Webservice response
  func handleGetNotification(response: [String: Any]) {

    let notifications = response[kNotifications] as! [[String: Any]]
    var listOfNotifications: [AppNotification]! = []
    for notification in notifications {
      let overviewObj = AppNotification(detail: notification)
      listOfNotifications.append(overviewObj)
    }

    Gateway.instance.notification = listOfNotifications

    // save in database
    DBHandler().saveNotifications(notifications: listOfNotifications)

  }

  func handleStudyUpdates(response: [String: Any]) {

    if Utilities.isValidObject(someObject: response as AnyObject?) {
      _ = StudyUpdates(detail: response)
    }
  }

  /// Sends Request
  /// - Parameters:
  ///   - method: instance of Method
  ///   - params: request params
  ///   - headers: request headers
  private func sendRequestWith(method: Method, params: [String: Any]?, headers: [String: String]?) {

    networkManager.composeRequest(
      WCPConfiguration.configuration,
      method: method,
      params: params as NSDictionary?,
      headers: headers as NSDictionary?,
      delegate: delegateSource != nil ? delegateSource! : self
    )
  }

}
extension WCPServices: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    delegate?.startedRequest(manager, requestName: requestName)
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    let methodName = WCPMethods(rawValue: requestName as String)!

    switch methodName {
    case .gatewayInfo:
      self.handleResourceListForGateway(response: response as! [String: Any])
    case .study:
      self.handleStudyBasicInfo(response: response as! [String: Any])
    case .studyList:
      self.handleStudyList(response: response as! [String: Any])
    case .eligibilityConsent:
      self.handleEligibilityConsentMetaData(response: response as! [String: Any])
    case .resources:
      self.handleResourceForStudy(response: response as! [String: Any])
    case .consentDocument:
      self.handleConsentDocument(response: response as! [String: Any])
    case .studyInfo:
      self.handleStudyInfo(response: response as! [String: Any])
    case .activityList:
      self.handleStudyActivityList(response: response as! [String: Any])
    case .activity:
      self.handleGetStudyActivityMetadata(response: response as! [String: Any])
    case .studyDashboard:
      self.handleStudyDashboard(response: response as! [String: Any])
    case .termsPolicy:
      self.handleTermsAndPolicy(response: response as! [String: Any])
    case .notifications:
      self.handleGetNotification(response: response as! [String: Any])
    case .studyUpdates:
      self.handleStudyUpdates(response: response as! [String: Any])
    case .appUpdates: break

    default: break
    }

    delegate?.finishedRequest(manager, requestName: requestName, response: response)

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    delegate?.failedRequest(manager, requestName: requestName, error: error)
  }
}
