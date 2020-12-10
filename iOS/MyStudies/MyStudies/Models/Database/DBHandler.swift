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

import RealmSwift
import UIKit

class DBHandler: NSObject {

  private static var realm: Realm? = {
    let key = FDAKeychain.shared[kRealmEncryptionKeychainKey]
    let data = Data.init(base64Encoded: key!)
    let encryptionConfig = Realm.Configuration(encryptionKey: data)
    return try? Realm(configuration: encryptionConfig)
  }()

  class func getRealmObject() -> Realm? {
    return DBHandler.realm
  }

  /// Used to save user details like userid, authkey, first name , last name etc
  func saveCurrentUser(user: User) {

    let realm = DBHandler.getRealmObject()!
    let dbUsers = realm.objects(DBUser.self)
    var dbUser = dbUsers.last

    if dbUser == nil {

      dbUser = DBUser()
      dbUser?.userType = (user.userType?.rawValue)!
      dbUser?.emailId = user.emailId!
      dbUser?.userId = user.userId
      dbUser?.verified = user.verified

      try? realm.write {
        realm.add(dbUser!, update: .all)
      }
    } else {
      let user = User.currentUser
      do {
        try realm.write {
          dbUser?.userType = (user.userType?.rawValue)!
          dbUser?.emailId = user.emailId!
          dbUser?.authToken = user.authToken
          dbUser?.verified = user.verified
          dbUser?.refreshToken = user.refreshToken
        }
      } catch let error {
        Logger.sharedInstance.error(error)
      }
    }
  }

  // Used to initialize the current logged in user
  func initilizeCurrentUser() -> Bool {

    let realm = DBHandler.getRealmObject()!
    let dbUsers = realm.objects(DBUser.self)
    let dbUser = dbUsers.last

    if dbUser != nil {
      let currentUser = User.currentUser
      currentUser.firstName = dbUser?.firstName
      currentUser.lastName = dbUser?.lastName
      currentUser.verified = dbUser?.verified ?? false
      currentUser.userId = dbUser?.userId
      currentUser.emailId = dbUser?.emailId
      currentUser.userType = (dbUser?.userType).map { UserType(rawValue: $0) }!

      let settings = Settings()
      settings.localNotifications = dbUser?.localNotificationEnabled
      settings.passcode = dbUser?.passcodeEnabled
      settings.remoteNotifications = dbUser?.remoteNotificationEnabled

      currentUser.settings = settings

      return true
    }
    return false
  }

  /// This method will save new user App settings like passcode & notifications.
  class func saveUserSettingsToDatabase() {

    let realm = DBHandler.getRealmObject()!
    let dbUsers = realm.objects(DBUser.self)
    let dbUser = dbUsers.last

    try? realm.write {
      let user = User.currentUser
      dbUser?.passcodeEnabled = (user.settings?.passcode)!
      dbUser?.localNotificationEnabled = (user.settings?.localNotifications)!
      dbUser?.remoteNotificationEnabled = (user.settings?.remoteNotifications)!

    }
  }

  /// Used to delete current logged in user
  class func deleteCurrentUser() {

    let realm = DBHandler.getRealmObject()!
    let dbUsers = realm.objects(DBUser.self)
    let dbUser = dbUsers.last
    try? realm.write {
      realm.delete(dbUser!)
    }
  }

  // MARK: - Study

  /// Save studies objects in Database.
  ///
  /// - Parameter studies: Ordered collection of Study Instances.
  func saveStudies(studies: [Study]) {

    let realm = DBHandler.getRealmObject()!
    let dbStudiesArray = realm.objects(DBStudy.self)

    var dbStudies: [DBStudy] = []
    for study in studies {

      // Some studies are already present in db
      var dbStudy: DBStudy?
      if dbStudiesArray.count > 0 {
        dbStudy = dbStudiesArray.filter({ $0.studyId == study.studyId }).last
      }

      if dbStudy == nil {
        dbStudy = DBHandler.getDBStudy(study: study)
        dbStudies.append(dbStudy!)
      } else {

        try? realm.write({
          dbStudy?.category = study.category
          dbStudy?.name = study.name
          dbStudy?.sponserName = study.sponserName
          dbStudy?.tagLine = study.description
          dbStudy?.logoURL = study.logoURL
          dbStudy?.startDate = study.startDate
          dbStudy?.endEnd = study.endEnd
          dbStudy?.status = study.status.rawValue
          dbStudy?.enrolling = study.studySettings.enrollingAllowed
          dbStudy?.rejoin = study.studySettings.rejoinStudyAfterWithdrawn
          dbStudy?.platform = study.studySettings.platform
          if let studyStatus = study.userParticipateState {
            dbStudy?.participatedStatus = studyStatus.status.rawValue
            dbStudy?.participatedId = studyStatus.participantId
            dbStudy?.siteID = studyStatus.siteID
            dbStudy?.tokenIdentifier = studyStatus.tokenIdentifier
            dbStudy?.joiningDate = studyStatus.joiningDate
            dbStudy?.completion = studyStatus.completion
            dbStudy?.adherence = studyStatus.adherence
            dbStudy?.bookmarked = studyStatus.bookmarked
          }
          if dbStudy?.participatedStatus
            == UserStudyStatus.StudyStatus.inProgress
            .rawValue
          {
            dbStudy?.updatedVersion = study.version

          } else {
            dbStudy?.updatedVersion = study.version
          }

        })

      }

    }

    try? realm.write {
      realm.add(dbStudies, update: .modified)
    }
  }

  /// Creates an instance of DBStudy.
  ///
  /// - Parameter study: Ordered collection of Study Objects.
  ///  - Returns: Realm object of `DBStudy`
  private class func getDBStudy(study: Study) -> DBStudy {

    let dbStudy = DBStudy()
    dbStudy.studyId = study.studyId
    dbStudy.category = study.category
    dbStudy.name = study.name
    dbStudy.sponserName = study.sponserName
    dbStudy.tagLine = study.description
    dbStudy.version = study.version
    dbStudy.updatedVersion = study.version
    dbStudy.logoURL = study.logoURL
    dbStudy.startDate = study.startDate
    dbStudy.endEnd = study.endEnd
    dbStudy.enrolling = study.studySettings.enrollingAllowed
    dbStudy.rejoin = study.studySettings.rejoinStudyAfterWithdrawn
    dbStudy.platform = study.studySettings.platform
    dbStudy.status = study.status.rawValue
    if let userStudyStatus = study.userParticipateState {
      dbStudy.participatedStatus = userStudyStatus.status.rawValue
      dbStudy.participatedId = userStudyStatus.participantId
      dbStudy.siteID = userStudyStatus.siteID
      dbStudy.tokenIdentifier = userStudyStatus.tokenIdentifier
      dbStudy.joiningDate = userStudyStatus.joiningDate
      dbStudy.completion = userStudyStatus.completion
      dbStudy.adherence = userStudyStatus.adherence
      dbStudy.bookmarked = userStudyStatus.bookmarked
    }
    dbStudy.withdrawalConfigrationMessage = study.withdrawalConfigration?.message
    dbStudy.withdrawalConfigrationType = study.withdrawalConfigration?.type?.rawValue
    return dbStudy

  }

  /// Fetches list of Studies from DB.
  ///
  /// - Parameter completionHandler: Completion handler with array of studies and returns nothing
  class func loadStudyListFromDatabase(completionHandler: @escaping ([Study]) -> Void) {

    let realm = DBHandler.getRealmObject()!
    let dbStudies = realm.objects(DBStudy.self)

    User.currentUser.participatedStudies.removeAll()
    var studies: [Study] = []
    for dbStudy in dbStudies {

      let study = Study()
      study.studyId = dbStudy.studyId
      study.category = dbStudy.category
      study.name = dbStudy.name
      study.sponserName = dbStudy.sponserName
      study.description = dbStudy.tagLine
      study.version = dbStudy.version
      study.newVersion = dbStudy.updatedVersion
      study.logoURL = dbStudy.logoURL
      study.startDate = dbStudy.startDate
      study.endEnd = dbStudy.endEnd
      study.status = StudyStatus(rawValue: dbStudy.status!)!
      study.signedConsentVersion = dbStudy.signedConsentVersion
      study.signedConsentFilePath = dbStudy.signedConsentFilePath
      study.activitiesLocalNotificationUpdated = dbStudy.activitiesLocalNotificationUpdated

      // Settings
      let studySettings = StudySettings()
      studySettings.enrollingAllowed = dbStudy.enrolling
      studySettings.rejoinStudyAfterWithdrawn = dbStudy.rejoin
      studySettings.platform = dbStudy.platform ?? ""
      study.studySettings = studySettings

      // Status
      let participatedStatus = UserStudyStatus()
      participatedStatus.status = UserStudyStatus.StudyStatus(
        rawValue: dbStudy.participatedStatus
      )!
      participatedStatus.bookmarked = dbStudy.bookmarked
      participatedStatus.studyId = dbStudy.studyId
      participatedStatus.participantId = dbStudy.participatedId
      participatedStatus.siteID = dbStudy.siteID ?? ""
      participatedStatus.tokenIdentifier = dbStudy.tokenIdentifier ?? ""
      participatedStatus.adherence = dbStudy.adherence
      participatedStatus.completion = dbStudy.completion
      participatedStatus.joiningDate = dbStudy.joiningDate

      study.userParticipateState = participatedStatus

      // Append to user class participatesStudies also
      User.currentUser.participatedStudies.append(participatedStatus)

      // AnchorDate
      let anchorDate = StudyAnchorDate()
      anchorDate.anchorDateActivityId = dbStudy.anchorDateActivityId
      anchorDate.anchorDateQuestionKey = dbStudy.anchorDateType
      anchorDate.anchorDateActivityVersion = dbStudy.anchorDateActivityVersion
      anchorDate.anchorDateQuestionKey = dbStudy.anchorDateQuestionKey
      anchorDate.anchorDateType = dbStudy.anchorDateType
      anchorDate.date = dbStudy.anchorDate

      study.anchorDate = anchorDate

      let withdrawalInfo = StudyWithdrawalConfigration()
      withdrawalInfo.message = dbStudy.withdrawalConfigrationMessage

      if dbStudy.withdrawalConfigrationType != nil {
        withdrawalInfo.type = StudyWithdrawalConfigrationType(
          rawValue: dbStudy.withdrawalConfigrationType!
        )
      } else {
        withdrawalInfo.type = .notAvailable
      }
      study.withdrawalConfigration = withdrawalInfo
      studies.append(study)
    }

    completionHandler(studies)
  }

  /// Save study overview.
  ///
  /// - Parameters:
  ///   - overview: Object of `Overview`
  ///   - studyId: StudyID to query `DBStudy` object from DB
  class func saveStudyOverview(overview: Overview, studyId: String) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBStudy.self).filter("studyId == %@", studyId)
    let dbStudy = studies.last

    let dbStudies = List<DBOverviewSection>()
    // Save overview
    for (sectionIndex, section) in overview.sections.enumerated() {
      let dbOverviewSection = DBOverviewSection()
      dbOverviewSection.title = section.title
      dbOverviewSection.link = section.link
      dbOverviewSection.imageURL = section.imageURL
      dbOverviewSection.text = section.text
      dbOverviewSection.type = section.type
      dbOverviewSection.studyId = studyId
      dbOverviewSection.sortOrder = sectionIndex
      dbOverviewSection.sectionId = studyId + "screen\(sectionIndex)"
      dbStudies.append(dbOverviewSection)
    }

    try? realm.write {
      realm.add(dbStudies, update: .all)
      dbStudy?.websiteLink = overview.websiteLink
    }

  }

  /// Saves withdrawal configration to DB.
  ///
  /// - Parameters:
  ///   - withdrawalConfigration:  instance of StudyWithdrawalConfigration.
  ///   - studyId: study for which configrations are to be updated.
  class func saveWithdrawalConfigration(
    withdrawalConfigration: StudyWithdrawalConfigration,
    studyId: String
  ) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBStudy.self).filter("studyId == %@", studyId)
    let dbStudy = studies.last

    try? realm.write {
      dbStudy?.withdrawalConfigrationMessage = withdrawalConfigration.message
      dbStudy?.withdrawalConfigrationType = withdrawalConfigration.type?.rawValue
    }

  }

  /// Saves anchor date to DB.
  ///
  /// - Parameters:
  ///   - anchorDate: Instance of StudyAnchorDate.
  ///   - studyId: Study for which anchorDate are to be updated.
  class func saveAnchorDateDetail(anchorDate: StudyAnchorDate, studyId: String) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBStudy.self).filter("studyId == %@", studyId)
    let dbStudy = studies.last

    try? realm.write {
      dbStudy?.anchorDateActivityId = anchorDate.anchorDateActivityId
      dbStudy?.anchorDateType = anchorDate.anchorDateType
      dbStudy?.anchorDateActivityVersion = anchorDate.anchorDateActivityVersion
      dbStudy?.anchorDateQuestionKey = anchorDate.anchorDateQuestionKey
    }
  }

  /// Saves anchor date to DB.
  ///
  /// - Parameters:
  ///   - date: New date of Anchor.
  ///   - studyId: Study for which configrations are to be updated.
  class func saveAnchorDate(date: Date, studyId: String) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBStudy.self).filter("studyId == %@", studyId)
    let dbStudy = studies.last

    try? realm.write {
      dbStudy?.anchorDate = date
    }
  }

  /// Fetches overview from DB.
  ///
  /// - Parameters:
  ///   - studyId: StudyID for which overview needs to be fetched.
  ///   - completionHandler: completion handler with instance of overview.
  class func loadStudyOverview(studyId: String, completionHandler: @escaping (Overview?) -> Void) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBOverviewSection.self).filter("studyId == %@", studyId)
      .sorted(byKeyPath: "sortOrder", ascending: true)
    let study = realm.objects(DBStudy.self).filter("studyId == %@", studyId).last

    if studies.count > 0 {

      /// initialize OverviewSection from database
      var overviewSections: [OverviewSection] = []
      for dbSection in studies {
        let section = OverviewSection()

        section.title = dbSection.title
        section.imageURL = dbSection.imageURL
        section.link = dbSection.link
        section.type = dbSection.type
        section.text = dbSection.text
        overviewSections.append(section)
      }

      // Create Overview object
      let overview = Overview()
      overview.type = .study
      overview.websiteLink = study?.websiteLink
      overview.sections = overviewSections
      completionHandler(overview)

    } else {
      completionHandler(nil)
    }
  }

  /// Updates `StudyUpdates` to DB.
  ///
  /// - Parameters:
  ///   - studyId: StudyID for which overview needs to be fetched.
  ///   - completionHandler: completion handler with instance of overview.
  class func updateMetaDataToUpdateForStudy(study: Study, updateDetails: StudyUpdates?) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBStudy.self).filter("studyId == %@", study.studyId ?? "")
    let dbStudy = studies.last

    try? realm.write {
      dbStudy?.updateResources = StudyUpdates.studyResourcesUpdated
      dbStudy?.updateConsent = StudyUpdates.studyConsentUpdated
      dbStudy?.updateActivities = StudyUpdates.studyActivitiesUpdated
      dbStudy?.updateInfo = StudyUpdates.studyInfoUpdated
      if StudyUpdates.studyVersion != nil {
        dbStudy?.version = StudyUpdates.studyVersion
      } else {
        dbStudy?.version = dbStudy?.updatedVersion
      }
    }

  }

  /// This method will update the participation status of the study.
  /// - Parameter study: Instance of the study participated.
  class func updateStudyParticipationStatus(study: Study) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBStudy.self).filter("studyId == %@", study.studyId ?? "")
    let dbStudy = studies.last

    try? realm.write {
      if let studyStatus = study.userParticipateState {
        dbStudy?.participatedStatus = studyStatus.status.rawValue
        dbStudy?.participatedId = studyStatus.participantId
        dbStudy?.siteID = studyStatus.siteID
        dbStudy?.tokenIdentifier = studyStatus.tokenIdentifier
        dbStudy?.joiningDate = studyStatus.joiningDate
        dbStudy?.completion = studyStatus.completion
        dbStudy?.adherence = studyStatus.adherence
      }
    }
  }

  /// Fetches StudyDetails from DB.
  /// - Parameters:
  ///   - studyId: ID of the Study to fetch details from Database.
  ///   - completionHandler: Bool to indicate if the Study detail available in Database or not.
  class func loadStudyDetailsToUpdate(
    studyId: String,
    completionHandler: @escaping (Bool) -> Void
  ) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBStudy.self).filter("studyId == %@", studyId)
    let dbStudy = studies.last

    StudyUpdates.studyActivitiesUpdated = (dbStudy?.updateActivities)!
    StudyUpdates.studyConsentUpdated = (dbStudy?.updateConsent)!
    StudyUpdates.studyResourcesUpdated = (dbStudy?.updateResources)!
    StudyUpdates.studyInfoUpdated = (dbStudy?.updateInfo)!
    completionHandler(true)
  }

  ///  Saves study consent Info to DB.
  /// - Parameter study: Instance of the study for which consent information to be saved.
  class func saveConsentInformation(study: Study) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBStudy.self).filter("studyId == %@", study.studyId ?? "")
    let dbStudy = studies.last

    try? realm.write {
      dbStudy?.signedConsentFilePath = study.signedConsentFilePath
      dbStudy?.signedConsentVersion = study.signedConsentVersion
    }
  }

  /// Updates local Notification scheduled status for the `Activity`.
  /// - Parameters:
  ///   - studyId: ID of the Study to query `DBStudy` object  from Database.
  ///   - status: Boolean value to indicate the Local notification scheduled.
  class func updateLocalNotificationScheduleStatus(studyId: String, status: Bool) {

    let realm = DBHandler.getRealmObject()!
    let study = realm.object(ofType: DBStudy.self, forPrimaryKey: studyId)

    try? realm.write {
      study?.activitiesLocalNotificationUpdated = status
    }

  }

  // MARK: - Activity

  /// Saves Activities to DB.
  /// - Parameter activities: Collection of `Activity` instances.
  class func saveActivities(activities: [Activity]) {

    let realm = DBHandler.getRealmObject()!
    let study = Study.currentStudy
    let dbActivityArray = realm.objects(DBActivity.self).filter({ $0.studyId == study?.studyId })

    var dbActivities: [DBActivity] = []
    var activityUpdated = false
    for activity in activities {

      var dbActivity: DBActivity?
      if dbActivityArray.count != 0 {
        dbActivity = dbActivityArray.filter({ $0.actvityId == activity.actvityId! }).last

        if dbActivity == nil {  // newly added activity
          dbActivity = DBHandler.getDBActivity(activity: activity)
          dbActivities.append(dbActivity!)
          activityUpdated = true
        } else {

          // check if version is updated
          if dbActivity?.version != activity.version {

            try? realm.write {
              realm.delete((dbActivity?.activityRuns)!)
              realm.delete(dbActivity!)
            }

            let updatedActivity = DBHandler.getDBActivity(activity: activity)
            dbActivities.append(updatedActivity)
            DBHandler.deleteMetaDataForActivity(
              activityId: activity.actvityId!,
              studyId: activity.studyId!
            )
            activityUpdated = true

          } else {
            try? realm.write {

              dbActivity?.currentRunId =
                activity.userParticipationStatus
                .activityRunId
              dbActivity?.participationStatus =
                activity.userParticipationStatus
                .status.rawValue
              dbActivity?.completedRuns =
                activity.userParticipationStatus
                .compeltedRuns
              dbActivity?.state = activity.state
            }
          }
        }

      } else {
        dbActivity = DBHandler.getDBActivity(activity: activity)
        dbActivities.append(dbActivity!)
        activityUpdated = true
      }
    }

    // keys for alerts
    if activityUpdated {
      let ud = UserDefaults.standard
      let halfCompletionKey = "50pcShown" + (Study.currentStudy?.studyId)!
      let fullCompletionKey = "100pcShown" + (Study.currentStudy?.studyId)!
      ud.set(false, forKey: halfCompletionKey)
      ud.set(false, forKey: fullCompletionKey)
    }

    if dbActivities.count > 0 {
      try? realm.write {
        realm.add(dbActivities, update: .all)
      }
    }
  }

  /// Fetches DBActivity object  from Database.
  /// - Parameter activity: Instance of Activity to query from Database.
  private class func getDBActivity(activity: Activity) -> DBActivity {

    let dbActivity = DBActivity()

    dbActivity.studyId = activity.studyId
    dbActivity.actvityId = activity.actvityId
    dbActivity.type = activity.type?.rawValue
    dbActivity.name = activity.name
    dbActivity.startDate = activity.startDate
    dbActivity.endDate = activity.endDate
    dbActivity.version = activity.version
    dbActivity.state = activity.state

    dbActivity.branching = activity.branching!
    dbActivity.frequencyType = activity.frequencyType.rawValue
    dbActivity.schedulingType = activity.schedulingType.rawValue
    dbActivity.currentRunId = activity.userParticipationStatus.activityRunId
    dbActivity.participationStatus = activity.userParticipationStatus.status.rawValue
    dbActivity.completedRuns = activity.userParticipationStatus.compeltedRuns
    dbActivity.id = activity.studyId! + activity.actvityId!
    dbActivity.taskSubType = activity.taskSubType

    let activityFrequencyDict = ["data": activity.frequencyRuns]
    let frequencyRunsData = try? JSONSerialization.data(
      withJSONObject: activityFrequencyDict,
      options: JSONSerialization.WritingOptions.prettyPrinted
    )
    dbActivity.frequencyRunsData = frequencyRunsData

    let activityAnchorDict = ["data": activity.anchorRuns]
    let anchorRunsdata = try? JSONSerialization.data(
      withJSONObject: activityAnchorDict,
      options: JSONSerialization.WritingOptions.prettyPrinted
    )
    dbActivity.anchorRunsData = anchorRunsdata

    // save overview
    let dbActivityRuns = List<DBActivityRun>()
    for activityRun in activity.activityRuns {

      let dbActivityRun = DBActivityRun()
      dbActivityRun.startDate = activityRun.startDate
      dbActivityRun.endDate = activityRun.endDate
      dbActivityRun.activityId = activity.actvityId
      dbActivityRun.studyId = activity.studyId
      dbActivityRun.runId = activityRun.runId
      dbActivityRun.isCompleted = activityRun.isCompleted
      dbActivityRuns.append(dbActivityRun)
    }

    // save anchor date
    dbActivity.sourceType = activity.anchorDate?.sourceType
    dbActivity.sourceActivityId = activity.anchorDate?.sourceActivityId
    dbActivity.sourceKey = activity.anchorDate?.sourceKey
    dbActivity.sourceFormKey = activity.anchorDate?.sourceFormKey
    dbActivity.startDays = activity.anchorDate?.startDays ?? 0
    dbActivity.startTime = activity.anchorDate?.startTime
    dbActivity.endDays = activity.anchorDate?.endDays ?? 0
    dbActivity.repeatInterval = activity.anchorDate?.repeatInterval ?? 0
    dbActivity.endTime = activity.anchorDate?.endTime

    dbActivity.activityRuns.append(objectsIn: dbActivityRuns)
    return dbActivity
  }

  /// Updates Activity restoration data for the questionnaire.
  /// - Parameters:
  ///   - activity: Activity instance for which restoration data to be updated.
  ///   - studyId: Study ID  to query `Activity` object from DB.
  ///   - restortionData: Restoration data for the questionnaire.
  class func updateActivityRestortionDataFor(
    activity: Activity,
    studyId: String,
    restortionData: Data?
  ) {
    let realm = DBHandler.getRealmObject()!
    let dbActivities = realm.objects(DBActivityRun.self).filter {
      $0.activityId == activity.actvityId
        && $0.studyId == studyId
        && $0.runId == activity.currentRun.runId
    }
    let dbActivity = dbActivities.last

    try? realm.write {
      dbActivity?.restortionData = restortionData
    }
  }

  /// Updates activities lifetime from anchor date received from source activity questionnaire response.
  /// - Parameters:
  ///   - studyId: StudyID to filter `DBActivity`.
  ///   - activityId: ActivityId to query target `DBActivity`.
  ///   - response: Response of the questionnaire.
  /// - Returns: Boolean status of Activity lifetime update.
  class func updateTargetActivityAnchorDateDetail(
    studyId: String,
    activityId: String,
    response: [String: Any]
  ) -> Bool {

    guard !response.isEmpty else { return false }
    let realm = DBHandler.getRealmObject()!
    let dbActivities = realm.objects(DBActivity.self)
      .filter {
        $0.sourceActivityId == activityId
          && $0.studyId == studyId
      }

    guard let dbActivity = dbActivities.last
    else {
      return false
    }

    // get source question value and key
    let results = response["results"] as! [[String: Any]]
    var quesStepKey: String
    var dictionary: [String: Any] = [:]
    let sourceKey = (dbActivity.sourceKey)!
    if dbActivity.sourceFormKey != nil && dbActivity.sourceFormKey!.count > 0 {
      quesStepKey = dbActivity.sourceFormKey!
      let quesResults = results.filter { $0["key"] as! String == quesStepKey }.first
      let resultsArray = ((quesResults!["value"] as? [[Any]])?.first) as? [[String: Any]]
      dictionary = resultsArray!.filter { $0["key"] as! String == sourceKey }.first!

    } else {
      dictionary = results.filter { $0["key"] as! String == sourceKey }.first!
    }

    guard let userInputDate = dictionary["value"] as? String else {
      return false
    }
    let date = Utilities.getDateFromString(dateString: userInputDate)

    for activity in dbActivities {
      if let anchorDate = date {
        self.updateActivityLifeTimeFor(activity, anchorDate: anchorDate)
      }
    }
    return date != nil
  }

  /// Updates resources lifetime from anchor date received from source activity questionnaire response.
  /// - Parameters:
  ///   - studyId: StudyID to filter `DBResource`.
  ///   - activityId: ActivityId to query target `DBResource`.
  ///   - response: Response of the questionnaire.
  /// - Returns: Boolean status of Resource lifetime update.
  class func updateTargetResourceAnchorDateDetail(
    studyId: String,
    activityId: String,
    response: [String: Any]
  ) -> Bool {

    guard !response.isEmpty else { return false }
    let realm = DBHandler.getRealmObject()!
    let dbResources = realm.objects(DBResources.self)
      .filter {
        $0.sourceActivityId == activityId
          && $0.studyId == studyId
          && $0.availabilityType == ActivityScheduleType.anchorDate.rawValue
      }

    guard let dbResource = dbResources.last,
      !response.isEmpty
    else {
      return false
    }

    // get source question value and key
    let results = response["results"] as? [JSONDictionary] ?? []
    var dictionary: [String: Any] = [:]
    let sourceKey = dbResource.sourceKey
    if let quesStepKey = dbResource.sourceFormKey {
      let quesResults = results.filter { $0["key"] as? String == quesStepKey }.first
      let resultsArray = ((quesResults!["value"] as? [[Any]])?.first) as? [JSONDictionary] ?? []
      dictionary = resultsArray.filter { $0["key"] as? String == sourceKey }.first ?? [:]
    } else {
      dictionary = results.filter { $0["key"] as? String == sourceKey }.first ?? [:]
    }

    guard let userInputDate = dictionary["value"] as? String else {
      return false
    }
    let date = Utilities.getDateFromString(dateString: userInputDate)
    if let anchorDate = date {
      DBHandler.updateResourceLifeTime(
        studyId,
        activityId: activityId,
        questionKey: sourceKey,
        anchorDateValue: anchorDate
      )
      return true
    }
    return false
  }

  /// Updates anchor date in the Database for `DBActivity`.
  /// - Parameters:
  ///   - dbActivity: Instance of  `DBActivity` for which anchor date will be updated.
  ///   - anchorDate: Calculated anchor date.
  class func updateActivityLifeTimeFor(_ dbActivity: DBActivity, anchorDate: Date) {

    var date = anchorDate
    let realm = DBHandler.getRealmObject()!

    // update start date
    var startDateStringEnrollment = Utilities.formatterShort?.string(from: date)
    let startTimeEnrollment = "00:00:00"
    startDateStringEnrollment = (startDateStringEnrollment ?? "") + " " + startTimeEnrollment
    date = Utilities.findDateFromString(dateString: startDateStringEnrollment ?? "")!

    let frequency = Frequency(rawValue: (dbActivity.frequencyType)!)!
    let lifeTime = DBHandler.getLifeTime(
      date,
      frequency: frequency,
      startDays: (dbActivity.startDays),
      endDays: (dbActivity.endDays),
      repeatInterval: (dbActivity.repeatInterval)
    )

    guard var anchorStartDate = lifeTime.0
    else { return }

    var anchorEndDate = lifeTime.1
    // Update Start date and time.
    if let startTime = dbActivity.startTime,
      let updatedStartDate = DateHelper.updateTime(of: anchorStartDate, with: startTime)
    {
      anchorStartDate = updatedStartDate
    } else if let activityStartDate = dbActivity.startDate {
      anchorStartDate = activityStartDate
    }

    // Update End date and time.
    if let endTime = dbActivity.endTime,
      let updatedEndDate = DateHelper.updateTime(of: anchorEndDate, with: endTime)
    {
      anchorEndDate = updatedEndDate
    } else if dbActivity.endDays == 0 {  // if nil == LifeTime Anchor activity.
      anchorEndDate = dbActivity.endDate
    }

    // calcuate runs for activity
    let currentDate = DBHandler.getCurrentDateWithTimeDifference()

    let activity = DBHandler.getActivityFromDBActivity(dbActivity, runDate: currentDate)
    activity.startDate = anchorStartDate
    activity.endDate = anchorEndDate
    activity.anchorDate?.anchorDateValue = date
    Schedule().getRunsForActivity(
      activity: activity,
      handler: { (runs) in
        let dbActivityRuns = List<DBActivityRun>()
        for activityRun in runs {
          let dbActivityRun = DBActivityRun(
            activityRun: activityRun,
            activity: activity
          )
          dbActivityRuns.append(dbActivityRun)
        }
        try? realm.write {
          dbActivity.activityRuns.append(objectsIn: dbActivityRuns)
          dbActivity.startDate = anchorStartDate
          dbActivity.endDate = anchorEndDate
          dbActivity.anchorDateValue = date
        }
      }
    )
  }

  /// This method will calculate lifetime of Activity.
  /// - Parameters:
  ///   - date: Anchor date of the `Activity`.
  ///   - frequency: Frequency of `Activity`.
  ///   - startDays: Number of days to calculate start date from Anchor date.
  ///   - endDays: Number of days to calculate end date for **OneTime frequency** `Activity` from Anchor date.
  ///   - repeatInterval: To calculate end date for  **Daily, Weekly, Monthly**  frequencies `Activity` from Anchor date.
  /// - Returns: Start date and End date.
  private class func getLifeTime(
    _ date: Date,
    frequency: Frequency,
    startDays: Int,
    endDays: Int,
    repeatInterval: Int
  ) -> (Date?, Date?) {

    var startDate: Date!
    var endDate: Date!

    switch frequency {
    case .oneTime:
      let startDateInterval = TimeInterval(60 * 60 * 24 * (startDays))
      let endDateInterval = TimeInterval(60 * 60 * 24 * (endDays))
      startDate = date.addingTimeInterval(startDateInterval)
      endDate = date.addingTimeInterval(endDateInterval)

    case .daily:
      let startDateInterval = TimeInterval(60 * 60 * 24 * (startDays))
      let endDateInterval = TimeInterval(60 * 60 * 24 * (repeatInterval))
      startDate = date.addingTimeInterval(startDateInterval)
      endDate = startDate.addingTimeInterval(endDateInterval)

    case .weekly:
      let startDateInterval = TimeInterval(60 * 60 * 24 * (startDays))
      let endDateInterval = TimeInterval(60 * 60 * 24 * 7 * (repeatInterval))
      startDate = date.addingTimeInterval(startDateInterval)
      endDate = startDate.addingTimeInterval(endDateInterval)

    case .monthly:
      let startDateInterval = TimeInterval(60 * 60 * 24 * (startDays))
      startDate = date.addingTimeInterval(startDateInterval)
      let calender = Calendar.current
      endDate = calender.date(byAdding: .month, value: (repeatInterval), to: startDate)

    case .scheduled:
      let startDateInterval = TimeInterval(60 * 60 * 24 * (startDays))
      let endDateInterval = TimeInterval(60 * 60 * 24 * (endDays))
      startDate = date.addingTimeInterval(startDateInterval)
      endDate = date.addingTimeInterval(endDateInterval)

    }

    return (startDate, endDate)
  }

  /// This method will calculate the collection of `Activity` with `nil` anchor date.
  /// - Parameter studyId: StudyID to query `DBActivity` objects.
  class func getActivitiesWithEmptyAnchorDateValue(_ studyId: String) -> [DBActivity] {
    let realm = DBHandler.getRealmObject()!
    let dbActivities: [DBActivity] = realm.objects(DBActivity.self)
      .filter {
        $0.studyId == studyId
          && $0.anchorDateValue == nil
          && $0.sourceType == "ActivityResponse"
      }
    return dbActivities
  }

  /// Updates `Activity` meta data to DB.
  /// - Parameter activity: Instance of `Activity` to query `DBActivity` object.
  class func updateActivityMetaData(activity: Activity) {

    let realm = DBHandler.getRealmObject()!

    let dbActivities = realm.objects(DBActivity.self)
      .filter {
        $0.actvityId == activity.actvityId
          && $0.studyId == activity.studyId
      }

    let dbActivity = dbActivities.last

    try? realm.write {
      dbActivity?.shortName = activity.shortName
    }

  }

  ///  Loads activity collection from DB for the study provided.
  /// - Parameters:
  ///   - studyId: StudyID to query related `DBActivity` object.
  ///   - completionHandler:  Returns the collection of `Activity` instances.
  class func loadActivityListFromDatabase(
    studyId: String,
    completionHandler: @escaping ([Activity]) -> Void
  ) {

    let realm = DBHandler.getRealmObject()!

    let dbActivities = realm.objects(DBActivity.self)
      .filter {
        $0.studyId == studyId
          && $0.startDate != nil
      }

    let date = DBHandler.getCurrentDateWithTimeDifference()
    var activities: [Activity] = []

    dbActivities.forEach { (activity) in
      let activity = DBHandler.getActivityFromDBActivity(activity, runDate: date)
      activities.append(activity)
    }
    completionHandler(activities)

  }

  private class func getCurrentDateWithTimeDifference() -> Date {

    var date = Date().utcDate()

    let difference = UserDefaults.standard.value(forKey: "offset") as? Int
    if difference != nil {
      date = date.addingTimeInterval(TimeInterval(difference!))
    }

    return date
  }

  /// This method is the query unmanaged `Activity` instance from `DBActivity` object for particular run date.
  /// - Parameters:
  ///   - dbActivity: Instance of  `DBActivity`.
  ///   - date: Run data,
  private class func getActivityFromDBActivity(_ dbActivity: DBActivity, runDate date: Date)
    -> Activity
  {

    // create activity instance
    let activity = Activity()
    activity.actvityId = dbActivity.actvityId
    activity.studyId = dbActivity.studyId
    activity.name = dbActivity.name
    activity.startDate = dbActivity.startDate
    activity.endDate = dbActivity.endDate
    activity.type = ActivityType(rawValue: dbActivity.type!)
    activity.frequencyType = Frequency(rawValue: dbActivity.frequencyType!)!
    activity.schedulingType = ActivityScheduleType(rawValue: dbActivity.schedulingType!)!
    activity.totalRuns = dbActivity.activityRuns.count
    activity.version = dbActivity.version
    activity.branching = dbActivity.branching
    activity.state = dbActivity.state
    activity.taskSubType = dbActivity.taskSubType

    let frequencyRuns =
      try? JSONSerialization.jsonObject(
        with: dbActivity.frequencyRunsData!,
        options: []
      ) as? JSONDictionary
    activity.frequencyRuns = frequencyRuns?["data"] as? [JSONDictionary]

    let anchorRuns =
      try? JSONSerialization.jsonObject(
        with: dbActivity.anchorRunsData!,
        options: []
      ) as? JSONDictionary
    activity.anchorRuns = anchorRuns?["data"] as? [JSONDictionary]

    if activity.totalRuns != 0 {

      // create activity run
      var runs: [ActivityRun] = []
      dbActivity.activityRuns.forEach { runs.append(ActivityRun(dbActivityRun: $0)) }

      activity.activityRuns = runs

      var runsBeforeToday: [ActivityRun]! = []
      var run: ActivityRun!

      if activity.frequencyType == Frequency.oneTime && activity.endDate == nil {
        run = runs.last
      } else {
        runsBeforeToday = runs.filter({ $0.endDate <= date })
        run =
          runs.filter {
            $0.startDate <= date
              && $0.endDate > date
          }.first  // current run
      }

      let completedRuns = runs.filter { $0.isCompleted == true }
      activity.compeltedRuns = completedRuns.count
      activity.currentRunId = (run != nil) ? (run?.runId)! : runsBeforeToday.count
      activity.currentRun = run
      activity.compeltedRuns = dbActivity.completedRuns
    }
    let userStatus = UserActivityStatus()
    userStatus.activityId = dbActivity.actvityId
    userStatus.activityRunId = String(activity.currentRunId)
    userStatus.studyId = dbActivity.studyId

    if String(activity.currentRunId) == dbActivity.currentRunId {
      userStatus.status = UserActivityStatus.ActivityStatus(
        rawValue: dbActivity.participationStatus
      )!
    }

    userStatus.compeltedRuns = activity.compeltedRuns
    userStatus.incompletedRuns = activity.incompletedRuns
    userStatus.totalRuns = activity.totalRuns

    let incompleteRuns = activity.currentRunId - activity.compeltedRuns
    activity.incompletedRuns =
      ((incompleteRuns < 0) || activity.totalRuns == 0)
      ? 0 : incompleteRuns

    if activity.currentRun == nil {
      userStatus.status = UserActivityStatus.ActivityStatus.abandoned

    } else {

      if userStatus.status != UserActivityStatus.ActivityStatus.completed {

        var incompleteRuns = activity.currentRunId - activity.compeltedRuns
        incompleteRuns -= 1
        activity.incompletedRuns = (incompleteRuns < 0) ? 0 : incompleteRuns
      }

    }
    activity.userParticipationStatus = userStatus

    // append to user class participatesStudies also
    let activityStatus = User.currentUser.participatedActivites.filter({
      $0.activityId == activity.actvityId && $0.studyId == activity.studyId
    }).first
    let index = User.currentUser.participatedActivites
      .firstIndex(
        where: {
          $0.activityId == activity.actvityId
            && $0.studyId == activity.studyId
        })

    if activityStatus != nil {
      User.currentUser.participatedActivites[index!] = userStatus
    } else {
      User.currentUser.participatedActivites.append(userStatus)
    }

    // save anchor date
    let anchorDate = AnchorDate()
    anchorDate.sourceType = dbActivity.sourceType
    anchorDate.sourceActivityId = dbActivity.sourceActivityId
    anchorDate.sourceKey = dbActivity.sourceKey
    anchorDate.sourceFormKey = dbActivity.sourceFormKey
    anchorDate.startDays = dbActivity.startDays
    anchorDate.startTime = dbActivity.startTime
    anchorDate.endDays = dbActivity.endDays
    anchorDate.repeatInterval = dbActivity.repeatInterval
    anchorDate.endTime = dbActivity.endTime
    activity.anchorDate = anchorDate

    return activity
  }

  static func divide(lhs: Int, rhs: Int) -> Int {
    if rhs == 0 {
      return 0
    }
    return lhs / rhs
  }

  /// This method marks the `DBActivityRun` object status as completed.
  /// - Parameters:
  ///   - runId: RunID to query corresponding `DBActivityRun`.
  ///   - activityId: ActivityID to query corresponding `DBActivityRun`.
  ///   - studyId: StudyID to query corresponding `DBActivityRun`.
  class func updateRunToComplete(runId: Int, activityId: String, studyId: String) {

    let realm = DBHandler.getRealmObject()!
    let dbRuns = realm.objects(DBActivityRun.self).filter(
      "studyId == %@ && activityId == %@ && runId == %d",
      studyId,
      activityId,
      runId
    )
    let dbRun = dbRuns.last

    try? realm.write {
      dbRun?.isCompleted = true
    }

  }

  /// This method will update the participation status of `Activity` in DB.
  /// - Parameter activity: Instance of `Activity` to query related `DBActivity` object from DB..
  class func updateParticipationStatus(for activity: Activity) {

    let realm = DBHandler.getRealmObject()!
    let studies = realm.objects(DBActivity.self)
      .filter {
        $0.actvityId == activity.actvityId
          && $0.studyId == activity.studyId
      }
    let dbActivity = studies.last

    try? realm.write {
      dbActivity?.currentRunId = activity.userParticipationStatus.activityRunId
      dbActivity?.participationStatus = activity.userParticipationStatus.status.rawValue
      dbActivity?.completedRuns = activity.compeltedRuns
    }
  }

  /// Saves the Request Information for the missed requests due to network failure for offline support.
  /// - Parameters:
  ///   - params: Request params.
  ///   - headers: Request Headers.
  ///   - method: Method type.
  ///   - server: Server endpoint.
  class func saveRequestInformation(
    params: [String: Any]?,
    headers: [String: Any]?,
    method: String,
    server: String
  ) {

    let realm = DBHandler.getRealmObject()!
    let dataSync = DBDataOfflineSync()

    if params != nil {
      let paramData = try? JSONSerialization.data(
        withJSONObject: params!,
        options: JSONSerialization.WritingOptions.prettyPrinted
      )
      dataSync.requestParams = paramData
    }

    if headers != nil {
      let headerData = try? JSONSerialization.data(
        withJSONObject: headers!,
        options: JSONSerialization.WritingOptions.prettyPrinted
      )
      dataSync.headerParams = headerData
    }

    dataSync.method = method
    dataSync.server = server
    dataSync.date = Date()

    try? realm.write {
      realm.add(dataSync)
    }

  }

  /// This method queries if there's any failed request exist in the DB.
  /// - Parameter completionHandler: Boolean to indicate failed request in the DB.
  class func isDataAvailableToSync(completionHandler: @escaping (Bool) -> Void) {

    let realm = DBHandler.getRealmObject()!
    let dbRuns = realm.objects(DBDataOfflineSync.self)

    if dbRuns.count > 0 {
      completionHandler(true)
    } else {
      completionHandler(false)
    }
  }

  // MARK: - Activity MetaData

  /// This method will add Meta Data for `Activity` in DB.
  /// - Parameters:
  ///   - activity: Instance of `Activity` for which the Meta Data is going to be added in DB.
  ///   - data: Data in form of `JSONDictionary`,
  class func saveActivityMetaData(activity: Activity, data: [String: Any]) {

    let realm = DBHandler.getRealmObject()!
    let dbMetaData = DBActivityMetaData()
    dbMetaData.actvityId = activity.actvityId
    dbMetaData.studyId = activity.studyId

    let metaData = try? JSONSerialization.data(
      withJSONObject: data,
      options: JSONSerialization.WritingOptions.prettyPrinted
    )
    dbMetaData.metaData = metaData

    try? realm.write {
      realm.add(dbMetaData)
    }
  }

  /// Fetches activityMeta data from DB, updates the activityBuilder instance and returns a Bool.
  /// - Parameters:
  ///   - activity: Instance of  `Activity` to query `DBActivityMetaData` object from DB.
  ///   - completionHandler: Returns Bool indicating meta data availability in DB.
  class func loadActivityMetaData(activity: Activity, completionHandler: @escaping (Bool) -> Void) {

    let realm = DBHandler.getRealmObject()!
    let dbMetaData = realm.objects(DBActivityMetaData.self)
      .filter {
        $0.actvityId == activity.actvityId
          && $0.studyId == activity.studyId
      }.last

    if let metaData = dbMetaData?.metaData,
      let response = try? JSONSerialization.jsonObject(with: metaData, options: [])
        as? [String: Any]
    {
      Study.currentActivity?.setActivityMetaData(
        activityDict: response[kActivity] as! [String: Any]
      )

      if Utilities.isValidObject(someObject: Study.currentActivity?.steps as AnyObject?) {
        ActivityBuilder.currentActivityBuilder = ActivityBuilder()
        ActivityBuilder.currentActivityBuilder.initWithActivity(
          activity: Study.currentActivity!
        )
      }
      completionHandler(true)
    } else {
      completionHandler(false)
    }
  }

  /// This method will delete the meta data from Db for `Activity`.
  /// - Parameters:
  ///   - activityId: ActivityId to query `DBActivityMetaData` from DB.
  ///   - studyId: SudyID to query `DBActivityMetaData` from DB.
  class func deleteMetaDataForActivity(activityId: String, studyId: String) {

    let realm = DBHandler.getRealmObject()!
    let dbMetaDataList = realm.objects(DBActivityMetaData.self)
      .filter { $0.actvityId == activityId && $0.studyId == studyId }

    if let metaData = dbMetaDataList.last {
      try? realm.write {
        realm.delete(metaData)
      }
    }
  }

  // MARK: - Dashboard - Statistics

  /// Saves the dashboard Stats to Database.
  /// - Parameters:
  ///   - studyId: StudyID which contains `Activity`.
  ///   - statistics: Collection of `DashboardStatistics` instances  which is to be added to DB.
  class func saveDashBoardStatistics(studyId: String, statistics: [DashboardStatistics]) {

    let realm = DBHandler.getRealmObject()!
    let dbStatisticsArray = realm.objects(DBStatistics.self)
      .filter { $0.studyId == studyId }

    var oldStatsDict = dbStatisticsArray.reduce(into: [DBStatistics: Bool]()) {
      $0[$1] = false  // Consider them not active here.
    }

    for stat in statistics {
      if let dbStat = dbStatisticsArray.first(where: { $0.statisticsId == stat.statisticsId }) {
        let updatedStat = DBHandler.getDBStatistics(stats: stat)
        oldStatsDict[dbStat] = true  // Stat is still active.
        let statData = dbStat.statisticsData
        try? realm.write {
          updatedStat.statisticsData = statData
          realm.add(updatedStat, update: .modified)
        }
      } else {
        let newDbStat = DBHandler.getDBStatistics(stats: stat)
        try? realm.write {
          realm.add(newDbStat, update: .all)
        }
      }
    }

    // Remove the deleted stats from database.
    let inactiveStats = oldStatsDict.filter({ $0.value == false }).compactMap({ $0.key })
    inactiveStats.forEach { (dbStat) in
      try? realm.write {
        realm.delete(dbStat.statisticsData)
        realm.delete(dbStat)
      }
    }

  }

  /// Creates an managed instance of DBStatistics from DashboardStatistics.
  /// - Parameter stats: Instance of `DashboardStatistics` to update properties of `DBStatistics`.
  private class func getDBStatistics(stats: DashboardStatistics) -> DBStatistics {

    let dbStatistics = DBStatistics()
    dbStatistics.activityId = stats.activityId
    dbStatistics.activityVersion = stats.activityVersion
    dbStatistics.calculation = stats.calculation
    dbStatistics.dataSourceKey = stats.dataSourceKey
    dbStatistics.dataSourceType = stats.dataSourceType
    dbStatistics.displayName = stats.displayName
    dbStatistics.title = stats.title
    dbStatistics.statType = stats.statType
    dbStatistics.studyId = stats.studyId
    dbStatistics.unit = stats.unit
    dbStatistics.statisticsId = stats.studyId! + stats.title!

    return dbStatistics

  }

  /// Fetches stats for the study provided and returns collection of DashboardStatistics
  /// - Parameters:
  ///   - studyId: studyId to query `DBStatistics` object from DB.
  ///   - completionHandler: Returns collection of `DashboardStatistics`instances.
  class func loadStatisticsForStudy(
    studyId: String,
    completionHandler: @escaping ([DashboardStatistics]) -> Void
  ) {

    let realm = DBHandler.getRealmObject()!
    let dbStatisticsList = realm.objects(DBStatistics.self).filter("studyId == %@", studyId)
    var statsList: [DashboardStatistics] = []

    dbStatisticsList.forEach { statsList.append(DashboardStatistics(dbStatistics: $0)) }
    completionHandler(statsList)

  }

  // MARK: - Dashboard - Charts

  ///  Adds/Updates `DBCharts` for the study in DB.
  /// - Parameters:
  ///   - studyId: StudyID for the Charts.
  ///   - charts:  Collection of `DashboardCharts` instances to create/update objects of `DBCharts` in DB.
  class func saveDashBoardCharts(studyId: String, charts: [DashboardCharts]) {

    let realm = DBHandler.getRealmObject()!
    let dbChartsArray = realm.objects(DBCharts.self).filter { $0.studyId == studyId }

    var oldChartsDict = dbChartsArray.reduce(into: [DBCharts: Bool]()) {
      $0[$1] = false  // Consider them not active here.
    }

    for chart in charts {
      if let dbChart = dbChartsArray.first(where: { $0.chartId == chart.chartId }) {
        let chartData = dbChart.statisticsData
        let updatedChart = DBHandler.getDBChart(chart: chart)
        oldChartsDict[dbChart] = true  // Chart is still active.
        try? realm.write {
          updatedChart.statisticsData = chartData
          realm.add(updatedChart, update: .modified)
        }
      } else {
        let newDbChart = DBHandler.getDBChart(chart: chart)
        try? realm.write {
          realm.add(newDbChart, update: .all)
        }
      }
    }

    // Remove the deleted charts from database.
    let inactiveCharts = oldChartsDict.filter({ $0.value == false }).compactMap({ $0.key })
    inactiveCharts.forEach { (dbChart) in
      try? realm.write {
        realm.delete(dbChart.statisticsData)
        realm.delete(dbChart)
      }
    }
  }

  /// Creates an instance of DBCharts from DashboardCharts.
  /// - Parameter chart: Instance of `DashboardCharts`.
  private class func getDBChart(chart: DashboardCharts) -> DBCharts {

    let dbChart = DBCharts()
    dbChart.activityId = chart.activityId
    dbChart.activityVersion = chart.activityVersion
    dbChart.chartType = chart.chartType
    dbChart.chartSubType = chart.chartSubType
    dbChart.dataSourceTimeRange = chart.dataSourceTimeRange
    dbChart.dataSourceKey = chart.dataSourceKey
    dbChart.dataSourceType = chart.dataSourceType
    dbChart.displayName = chart.displayName
    dbChart.title = chart.title
    dbChart.scrollable = chart.scrollable

    dbChart.studyId = chart.studyId
    dbChart.chartId =
      chart.studyId! + (chart.activityId ?? "") + chart
      .dataSourceKey!

    return dbChart
  }

  ///  Loads `DashboardCharts` for Study from DB.
  /// - Parameters:
  ///   - studyId: StudyID to query `DBCharts` object from DB.
  ///   - completionHandler:  Returns Instance of DashboardCharts.
  class func loadChartsForStudy(
    studyId: String,
    completionHandler: @escaping ([DashboardCharts]) -> Void
  ) {

    let realm = DBHandler.getRealmObject()!
    let dbChartList = realm.objects(DBCharts.self).filter("studyId == %@", studyId)

    var chartList: [DashboardCharts] = []
    dbChartList.forEach { chartList.append(DashboardCharts(dbChart: $0)) }
    completionHandler(chartList)

  }

  class func isChartsAvailable(for studyID: String) -> Bool {
    guard let realm = DBHandler.getRealmObject() else { return false }
    return !realm.objects(DBCharts.self)
      .filter("studyId == %@", studyID).isEmpty
  }

  /// Saves Statistics data for actvity into DB.
  /// - Parameters:
  ///   - activityId: ActivityID for which stats to be saved.
  ///   - key: Key for which the stats to be saved.
  ///   - data: Data value to be stored.
  ///   - fkDuration: Exclusively to be used for fetal kick task.
  ///   - date: Date of submission of response.
  class func saveStatisticsDataFor(
    activityId: String,
    key: String,
    data: Float,
    fkDuration: Int,
    date: Date
  ) {

    let realm = DBHandler.getRealmObject()!
    let dbStatisticsList = realm.objects(DBStatistics.self).filter(
      "activityId == %@ && dataSourceKey == %@",
      activityId,
      key
    )

    let dbChartsList = realm.objects(DBCharts.self).filter(
      "activityId == %@ && dataSourceKey == %@",
      activityId,
      key
    )

    let dbStatistics = dbStatisticsList.last
    let dbChart = dbChartsList.last

    //save data
    let statData = DBStatisticsData()
    statData.startDate = date
    statData.data = data
    statData.fkDuration = fkDuration

    try? realm.write {
      if dbStatistics != nil {
        dbStatistics?.statisticsData.append(statData)
      }
      if dbChart != nil {
        dbChart?.statisticsData.append(statData)
      }
    }
  }

  /// Fetches data source keys needed to get DBstats from Server.
  /// - Parameters:
  ///   - studyId: studyId to query  corresponding`DBStatistics` objects from DB.
  ///   - completionHandler: `Array` of ActivityId and Keys value stored in `Dictionary`.
  class func getDataSourceKeyForActivity(
    studyId: String,
    completionHandler: @escaping ([[String: String]]) -> Void
  ) {

    let realm = DBHandler.getRealmObject()!
    let dbStatisticsList = realm.objects(DBStatistics.self).filter { $0.studyId == studyId }
    let dbChartsList = realm.objects(DBCharts.self).filter { $0.studyId == studyId }

    let statActivities: [String] = dbStatisticsList.compactMap { $0.activityId! }
    let chartActivities: [String] = dbChartsList.compactMap { $0.activityId! }

    let set1 = Set(statActivities)
    let set2 = Set(chartActivities)

    let allActivities = set1.union(set2)

    var activityAndQuestionKeys: [[String: String]] = []

    for activityId in allActivities {

      let statList = dbStatisticsList.filter({ $0.activityId == activityId })
      let chatList = dbChartsList.filter({ $0.activityId == activityId })

      let statsKeys = statList.compactMap { $0.dataSourceKey }
      let chartKeys = chatList.compactMap { $0.dataSourceKey }

      var keys: [String] = []
      for key in statsKeys {
        if !keys.contains(key) {
          keys.append(key)
        }
      }

      for key in chartKeys {
        if !keys.contains(key) {
          keys.append(key)
        }
      }

      let keyString = keys.joined(separator: ",")
      let dict =
        [
          "activityId": activityId,
          "keys": keyString,
        ] as [String: String]

      activityAndQuestionKeys.append(dict)

    }
    return completionHandler(activityAndQuestionKeys)
  }

  // MARK: - RESOURCES

  ///  Saves Resources for Study To DB
  /// - Parameters:
  ///   - studyId: StudyId of the `Resource`.
  ///   - resources:  Collection of `Resource` instances to create `DBResources` objects in DB.
  class func saveResourcesForStudy(studyId: String, resources: [Resource]) {

    let realm = DBHandler.getRealmObject()!
    let dbResourcesArray = realm.objects(DBResources.self).filter { $0.studyId == studyId }

    var dbResourcesList: [DBResources] = []
    for resource in resources {

      var dbResource: DBResources?
      if dbResourcesArray.count != 0 {
        dbResource = dbResourcesArray.filter { $0.resourceId == resource.resourcesId }.last

        if dbResource == nil {

          dbResource = DBHandler.getDBResource(resource: resource)
          dbResource?.studyId = studyId
          dbResourcesList.append(dbResource!)
        } else {

          try? realm.write {

            dbResource?.title = resource.title

            dbResource?.audience = resource.audience?.rawValue
            dbResource?.endDate = resource.endDate
            dbResource?.startDate = resource.startDate
            dbResource?.key = resource.key
            dbResource?.povAvailable = resource.povAvailable
            dbResource?.serverUrl = resource.file?.link
            dbResource?.level = resource.level?.rawValue
            dbResource?.notificationMessage = resource.notificationMessage

            if resource.povAvailable {
              dbResource?.anchorDateEndDays = resource.anchorDateEndDays!
              dbResource?.anchorDateStartDays = resource.anchorDateStartDays!
            }
          }
        }
      } else {

        dbResource = DBHandler.getDBResource(resource: resource)
        dbResource?.studyId = studyId
        dbResourcesList.append(dbResource!)
      }
    }

    let newlist = resources
    let dbResourceIds: [String] = dbResourcesArray.compactMap { $0.resourceId! }
    let resourceIds: [String] = newlist.compactMap { $0.resourcesId! }
    let dbset: Set<String> = Set(dbResourceIds)
    let set: Set<String> = Set(resourceIds)

    let toBeDelete = dbset.subtracting(set)

    for aId in toBeDelete {
      let dbResource = dbResourcesArray.filter { $0.resourceId == aId }.last
      try? realm.write {
        realm.delete(dbResource!)
      }
    }

    if dbResourcesList.count > 0 {
      try? realm.write {
        realm.add(dbResourcesList, update: .all)
      }
    }
  }

  /// This method will create object of `DBResources`.
  /// - Parameter resource: Instance of `Resource` to initialize `DBResources` properties in DB.
  /// - Returns: Instance of managed `DBResources` object.
  private class func getDBResource(resource: Resource) -> DBResources {

    let dbResource = DBResources()
    dbResource.resourceId = resource.resourcesId
    dbResource.title = resource.title
    dbResource.audience = resource.audience?.rawValue
    dbResource.endDate = resource.endDate
    dbResource.startDate = resource.startDate
    dbResource.key = resource.key
    dbResource.povAvailable = resource.povAvailable
    dbResource.serverUrl = resource.file?.link
    dbResource.level = resource.level?.rawValue
    dbResource.type = resource.type
    dbResource.notificationMessage = resource.notificationMessage

    if resource.povAvailable {
      dbResource.anchorDateEndDays = resource.anchorDateEndDays!
      dbResource.anchorDateStartDays = resource.anchorDateStartDays!
    }

    dbResource.availabilityType = resource.availabilityType.rawValue
    dbResource.sourceType = resource.sourceType?.rawValue
    dbResource.sourceActivityId = resource.sourceActivityId
    dbResource.sourceKey = resource.sourceKey
    dbResource.sourceFormKey = resource.sourceFormKey
    dbResource.startTime = resource.startTime
    dbResource.endTime = resource.endTime

    return dbResource
  }

  /// This method will query `DBResources` objects from DB with no Anchor Dates.
  /// - Parameter studyId: StudyId  to query associated `DBResources` from DB.
  class func getResourceWithEmptyAnchorDateValue(_ studyId: String) -> [DBResources] {
    let realm = DBHandler.getRealmObject()!
    let dbResources: [DBResources] = realm.objects(DBResources.self)
      .filter {
        $0.studyId == studyId
          && $0.startDate == nil
          && $0.sourceType == "ActivityResponse"
      }
    return dbResources
  }

  /// This method checks if any Activity is available in DB from a Study.
  /// - Parameter studyId: StudyId to query `DBActivity` objects in DB.
  /// - Returns: Boolean indicating activity in DB for passed StudyID.
  class func isActivitiesEmpty(_ studyId: String) -> Bool {
    let realm = DBHandler.getRealmObject()!
    return
      realm.objects(DBActivity.self)
      .filter { $0.studyId == studyId }.count == 0
  }

  /// This method checks if any Resource is available in DB from a Study.
  /// - Parameter studyId: StudyId to query `DBResources` objects in DB.
  /// - Returns: Boolean indicating `Resource` in DB for passed StudyID.
  class func isResourcesEmpty(_ studyId: String) -> Bool {
    let realm = DBHandler.getRealmObject()!
    return realm.objects(DBResources.self).filter({ $0.studyId == studyId }).count == 0
  }

  ///  Loads resources for study from DB
  /// - Parameters:
  ///   - studyId: StudyId to query `DBResources` objects in DB.
  ///   - completionHandler:  Returns Collection of unmanaged `Resource` instances.
  class func loadResourcesForStudy(
    studyId: String,
    completionHandler: @escaping ([Resource]) -> Void
  ) {

    let realm = DBHandler.getRealmObject()!
    let dbResourceList = realm.objects(DBResources.self)
      .filter {
        $0.studyId == studyId
          && ($0.povAvailable == false
            || $0.startDate != nil)
      }

    var resourceList: [Resource] = []
    dbResourceList.forEach { resourceList.append(Resource(dbResource: $0)) }
    completionHandler(resourceList)
  }

  /// This method queries resources with availablity and no startDate.
  /// - Parameters:
  ///   - studyId: StudyID to query associated Resources from DB.
  ///   - activityId: activityId to query associated Resources from DB.
  ///   - questionKey: questionKey description
  class func resourceListFor(_ studyId: String, activityId: String?, questionKey: String?)
    -> [DBResources]
  {
    let realm = DBHandler.getRealmObject()!
    var dbResourceList: [DBResources] = realm.objects(DBResources.self)
      .filter {
        $0.studyId == studyId
          && $0.povAvailable == true
          && $0.startDate == nil
      }

    if activityId != nil {
      dbResourceList = dbResourceList.filter { $0.sourceActivityId == activityId }
    }
    if questionKey != nil {
      dbResourceList = dbResourceList.filter { $0.sourceKey == questionKey }
    }

    return dbResourceList
  }

  /// This method will update
  /// - Parameters:
  ///   - studyId:
  ///   - activityId:
  ///   - questionKey:
  ///   - anchorDateValue:
  @discardableResult
  class func updateResourceLifeTime(
    _ studyId: String,
    activityId: String?,
    questionKey: String?,
    anchorDateValue: Date
  ) -> (Bool) {

    let resourceList = DBHandler.resourceListFor(
      studyId,
      activityId: activityId,
      questionKey: questionKey
    )

    var startDateStringEnrollment = Utilities.formatterShort?.string(from: anchorDateValue)
    let startTimeEnrollment = "00:00:00"
    startDateStringEnrollment = (startDateStringEnrollment ?? "") + " " + startTimeEnrollment
    let anchorDate = Utilities.findDateFromString(dateString: startDateStringEnrollment ?? "")

    var resourceUpdatedStatus = false
    for resource in resourceList {

      resourceUpdatedStatus = true

      self.saveLifeTimeFor(resource: resource, anchorDate: anchorDate!)

    }
    return resourceUpdatedStatus

  }

  class func saveLifeTimeFor(resource: DBResources, anchorDate: Date) {

    let realm = DBHandler.getRealmObject()!
    let startDateInterval = TimeInterval(60 * 60 * 24 * (resource.anchorDateStartDays))  // start of day
    let endDateInterval = TimeInterval(60 * 60 * 24 * (resource.anchorDateEndDays + 1) - 1)  // end of day

    var startDate = anchorDate.addingTimeInterval(startDateInterval)
    var endDate = anchorDate.addingTimeInterval(endDateInterval)

    // update start date
    var startDateString = Utilities.formatterShort?.string(from: startDate)
    let startTime = (resource.startTime == nil) ? "00:00:00" : (resource.startTime)!
    startDateString = (startDateString ?? "") + " " + startTime
    startDate = Utilities.findDateFromString(dateString: startDateString ?? "")!

    // update end date
    var endDateString = Utilities.formatterShort?.string(from: endDate)
    let endTime = (resource.endTime == nil) ? "23:59:59" : (resource.endTime)!
    endDateString = (endDateString ?? "") + " " + endTime
    endDate = Utilities.findDateFromString(dateString: endDateString ?? "")!

    try? realm.write {
      resource.startDate = startDate
      resource.endDate = endDate
    }
  }

  // MARK: - NOTIFICATION

  /// Saves notification to DB
  /// - Parameter notifications: Collection of `AppNotification` instances.
  func saveNotifications(notifications: [AppNotification]) {

    let realm = DBHandler.getRealmObject()!

    var dbNotificationList: [DBNotification] = []
    for notification in notifications {

      let dbNotification = DBNotification()
      dbNotification.id = notification.id!
      dbNotification.title = notification.title
      dbNotification.message = notification.message

      dbNotification.studyId = notification.studyId ?? ""
      dbNotification.activityId = notification.activityId ?? ""

      dbNotification.isRead = notification.read!
      dbNotification.notificationType = notification.type.rawValue
      dbNotification.subType = notification.subType.rawValue
      dbNotification.audience = notification.audience!.rawValue
      dbNotification.date = notification.date!
      dbNotificationList.append(dbNotification)

    }

    try? realm.write {
      realm.add(dbNotificationList, update: .all)
    }
  }

  /// Loads notification list from DB
  /// - Parameter completionHandler: Returns the Array of `AppNotification` instances.
  class func loadNotificationListFromDatabase(
    completionHandler: @escaping ([AppNotification]) -> Void
  ) {

    let realm = DBHandler.getRealmObject()!
    let dbNotifications = realm.objects(DBNotification.self).sorted(
      byKeyPath: "date",
      ascending: false
    )

    var notificationList: [AppNotification] = []
    for dbnotification in dbNotifications {

      let notification = AppNotification()

      notification.id = dbnotification.id
      notification.title = dbnotification.title
      notification.message = dbnotification.message
      notification.studyId = dbnotification.studyId
      notification.activityId = dbnotification.activityId
      notification.type = AppNotification.NotificationType(
        rawValue: dbnotification.notificationType!
      )!
      notification.subType = AppNotification.NotificationSubType(
        rawValue: dbnotification.subType!
      )!
      notification.audience = Audience(rawValue: dbnotification.audience!)!
      notification.date = dbnotification.date
      notification.read = dbnotification.isRead
      notificationList.append(notification)

    }
    completionHandler(notificationList)
  }

  class func saveRegisteredLocaNotifications(notificationList: [AppLocalNotification]) {
    for notification in notificationList {
      DBHandler.saveLocalNotification(notification: notification)
    }
  }

  /// Saves scheduled `DBLocalNotification` to DB.
  /// - Parameter notification: Array of `AppLocalNotification` instances to be stored in DB.
  class func saveLocalNotification(notification: AppLocalNotification) {

    let realm = DBHandler.getRealmObject()!
    let dbNotification = DBLocalNotification()
    dbNotification.id = notification.id
    dbNotification.title = notification.title
    dbNotification.message = notification.message
    dbNotification.studyId = notification.studyId ?? ""
    dbNotification.activityId = notification.activityId ?? ""
    dbNotification.isRead = notification.read!
    dbNotification.notificationType = notification.type.rawValue
    dbNotification.subType = notification.subType.rawValue
    dbNotification.audience = notification.audience!.rawValue
    dbNotification.startDate = notification.startDate
    dbNotification.endDate = notification.endDate

    try? realm.write {
      realm.add(dbNotification, update: .all)
    }
  }

  /// Fetches list of `DBLocalNotification` from DB.
  /// - Parameter completionHandler: Pass array of `AppLocalNotification` in completion handler.
  class func getLocalNotification(completionHandler: @escaping ([AppLocalNotification]) -> Void) {

    let realm = DBHandler.getRealmObject()!
    let todayDate = Date()
    let dbNotifications = realm.objects(DBLocalNotification.self).sorted(
      byKeyPath: "startDate",
      ascending: false
    ).filter({ $0.startDate! <= todayDate && $0.endDate! >= todayDate })

    var notificationList: [AppLocalNotification] = []
    for dbnotification in dbNotifications {

      let notification = AppLocalNotification()
      notification.id = dbnotification.id
      notification.title = dbnotification.title
      notification.message = dbnotification.message
      notification.studyId = dbnotification.studyId
      notification.activityId = dbnotification.activityId
      notification.type = AppNotification.NotificationType(
        rawValue: dbnotification.notificationType!
      )!

      notification.subType = AppNotification.NotificationSubType(
        rawValue: dbnotification.subType!
      )!
      notification.audience = Audience(rawValue: dbnotification.audience!)!

      notification.read = dbnotification.isRead
      notification.startDate = dbnotification.startDate
      notification.endDate = dbnotification.endDate
      notificationList.append(notification)
    }

    completionHandler(notificationList)
  }

  ///  Fetches the sorted top most 50 `DBLocalNotification` from DB.
  /// - Parameter completionHandler: Pass array of `AppLocalNotification` instances in completion handler.
  class func getRecentLocalNotification(
    completionHandler: @escaping ([AppLocalNotification]) -> Void
  ) {

    let realm = DBHandler.getRealmObject()!
    let todayDate = Date()
    let dbNotifications = realm.objects(DBLocalNotification.self).sorted(
      byKeyPath: "startDate",
      ascending: true
    ).filter({ $0.startDate! >= todayDate })

    var notificationList: [AppLocalNotification] = []

    for (i, dbnotification) in dbNotifications.enumerated() {

      if i == 50 {
        break
      }

      let notification = AppLocalNotification()

      notification.id = dbnotification.id
      notification.title = dbnotification.title
      notification.message = dbnotification.message
      notification.studyId = dbnotification.studyId
      notification.activityId = dbnotification.activityId
      notification.type = AppNotification.NotificationType(
        rawValue: dbnotification.notificationType!
      )!

      notification.subType = AppNotification.NotificationSubType(
        rawValue: dbnotification.subType!
      )!
      notification.audience = Audience(rawValue: dbnotification.audience!)!

      notification.read = dbnotification.isRead
      notification.startDate = dbnotification.startDate
      notification.endDate = dbnotification.endDate
      notificationList.append(notification)

    }
    completionHandler(notificationList)
  }

  class func isNotificationSetFor(
    notification: String,
    completionHandler: @escaping (Bool) -> Void
  ) {
    let realm = DBHandler.getRealmObject()!

    let dbNotifications = realm.object(
      ofType: DBLocalNotification.self,
      forPrimaryKey: notification
    )

    if dbNotifications == nil {
      completionHandler(false)
    }
    completionHandler(true)

  }

  // MARK: - DELETE

  /// This method will delete all the data from Realm Default configuration.
  class func deleteAll() {

    let realm = DBHandler.getRealmObject()!
    try? realm.write {
      realm.deleteAll()
    }
  }

  /// This method will delete Study activity runs, MetaData, Charts & Stats.
  /// - Parameter studyId: StudyId to query Study from the DB.
  class func deleteStudyData(studyId: String) {

    let realm = DBHandler.getRealmObject()!

    // delete activites and its metadata
    let dbActivities = realm.objects(DBActivity.self).filter("studyId == %@", studyId)
    dbActivities.forEach { (dbActivity) in
      DBHandler.deleteMetaDataForActivity(
        activityId: (dbActivity.actvityId)!,
        studyId: (dbActivity.studyId)!
      )

      try? realm.write {
        realm.delete((dbActivity.activityRuns))
        realm.delete(dbActivity)
      }
    }

    // delete stats
    let dbStatisticsArray = realm.objects(DBStatistics.self).filter({ $0.studyId == studyId })
    dbStatisticsArray.forEach { (stat) in
      try? realm.write {
        realm.delete((stat.statisticsData))
        realm.delete(stat)
      }
    }

    // delete chart
    let dbChartsArray = realm.objects(DBCharts.self).filter { $0.studyId == studyId }
    dbChartsArray.forEach { (chart) in
      try? realm.write {
        realm.delete(chart.statisticsData)
        realm.delete(chart)
      }
    }

    // delete resource
    let dbResourcesArray = realm.objects(DBResources.self)
      .filter { $0.studyId == studyId }

    try? realm.write {
      realm.delete(dbResourcesArray)
    }

  }

  /// This method will delete the objects of `DBNotification` from DB associate with activity and study.
  /// - Parameters:
  ///   - activityId: ActivityID associated with the Notification in DB.
  ///   - studyId: StudyID associated with the Notification in DB.
  class func deleteDBLocalNotification(activityId: String, studyId: String) {

    let realm = DBHandler.getRealmObject()!

    let dbNotifications = realm.objects(DBLocalNotification.self).filter(
      "activityId == %@ && studyId == %@",
      activityId,
      studyId
    )

    if dbNotifications.count > 0 {
      try? realm.write {
        realm.delete(dbNotifications)
      }
    }
  }

  /// This method will delete the objects of `DBLocalNotification` from DB associate with study.
  /// - Parameters:
  ///   - studyId: StudyID associated with the Notification in DB.
  class func deleteStudyDBLocalNotifications(for studyId: String) {

    let realm = DBHandler.getRealmObject()!

    let dbNotifications = realm.objects(DBLocalNotification.self).filter(
      "studyId == %@",
      studyId
    )

    try? realm.write {
      realm.delete(dbNotifications)
    }
  }

  class func logout() {
    let appDomain = Bundle.main.bundleIdentifier!
    UserDefaults.standard.removePersistentDomain(forName: appDomain)
    UserDefaults.standard.synchronize()

    // Delete from database
    DBHandler.deleteCurrentUser()

    // Reset user object
    User.resetCurrentUser()

    // Delete complete database
    DBHandler.deleteAll()

    // Cancel all local notification
    LocalNotification.cancelAllLocalNotification()

    LocalNotification.removeAllDeliveredNotifications()

    // Reset Filters
    StudyFilterHandler.instance.previousAppliedFilters = []
    StudyFilterHandler.instance.searchText = ""

    // Delete keychain values
    FDAKeychain.shared[kUserAuthTokenKeychainKey] = nil
    FDAKeychain.shared[kUserRefreshTokenKeychainKey] = nil
  }
}
