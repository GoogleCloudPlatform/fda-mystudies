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
import ResearchKit

// MARK: Api Constants
let kActivityType = "type"
let kActivityInfoMetaData = "metadata"

let kActivityInfo = "info"

let kActivityResponseData = "data"

let kActivityStudyId = "studyId"
let kActivityActivityId = "qId"
let kActivityName = "name"

let kActivityConfiguration = "configuration"

let kActivityFrequency = "frequency"
let kActivityFrequencyRuns = "runs"
let kActivityManualAnchorRuns = "anchorRuns"
let kActivityFrequencyType = "type"

let kActivityStartTime = "startTime"
let kActivityEndTime = "endTime"

let kActivitySteps = "steps"

/// schedule Api Keys
let kActivityLifetime = "lifetime"
let kActivityRunLifetime = "runLifetime"

/// questionnaireConfiguration
let kActivityBranching = "branching"
let kActivityRandomization = "randomization"

let kActivityLastModified = "lastModified"
let kActivityTaskSubType = "taskSubType"

enum ActivityType: String {
  case questionnaire = "questionnaire"
  case activeTask = "task"
}

enum Frequency: String {
  case oneTime = "One time"
  case daily = "Daily"
  case weekly = "Weekly"
  case monthly = "Monthly"
  case scheduled = "Manually Schedule"

  var description: String {
    switch self {
    case .oneTime:
      return "One Time"
    case .daily:
      return "Daily"
    case .weekly:
      return "Weekly"
    case .monthly:
      return "Monthly"
    case .scheduled:
      return "Aperiodic"
    }
  }
}

enum ActivityState: String {
  case active
  case deleted
}

enum ActivityScheduleType: String {
  case regular = "Regular"
  case anchorDate = "AnchorDate"
}

enum AnchorDateSourceType: String {
  case enrollmentDate = "EnrollmentDate"
  case activityResponse = "ActivityResponse"
}

/// Model Activity represents a Questionery or Active Task
class Activity {

  var type: ActivityType?

  /// Unique id of each activity
  var actvityId: String?

  var studyId: String?

  /// this will come in activity list used to display
  var name: String?

  /// this will come in meta data
  var shortName: String?

  var version: String?

  var state: String?
  var lastModified: Date?
  var userStatus: UserActivityStatus.ActivityStatus = .yetToJoin
  var startDate: Date?
  var endDate: Date?
  var branching: Bool?
  var randomization: Bool?

  var schedule: Schedule?
  var steps: [[String: Any]]? = []

  /// array of ORKSteps stores each step involved in Questionary
  var orkSteps: [ORKStep]? = []

  var activitySteps: [ActivityStep]? = []

  var frequencyRuns: [[String: Any]]? = []

  /// scheduled mannuly for anchor date
  var anchorRuns: [[String: Any]]? = []

  var frequencyType: Frequency = .oneTime

  var result: ActivityResult?

  /// stores the restortionData for current activity
  var restortionData: Data?

  var totalRuns = 0

  var currentRunId = 1
  var compeltedRuns = 0
  var incompletedRuns = 0
  var activityRuns: [ActivityRun]! = []
  var currentRun: ActivityRun! = nil
  var userParticipationStatus: UserActivityStatus! = nil

  /// used for active tasks
  var taskSubType: String? = ""

  var anchorDate: AnchorDate?

  var schedulingType: ActivityScheduleType = .regular

  /// Default Initializer
  init() {

    self.type = .questionnaire
    self.actvityId = ""
    self.studyId = ""
    self.name = ""
    self.lastModified = nil
    self.userStatus = .yetToJoin
    self.startDate = nil
    self.endDate = nil

    self.shortName = ""
    self.taskSubType = ""

    // questionnaireConfigurations
    self.branching = false
    self.randomization = false

    // Steps
    self.steps = Array()

    self.schedule = nil
    self.result = nil
    self.restortionData = Data()
    self.orkSteps = [ORKStep]()

    self.activitySteps = [ActivityStep]()
    // contains the runs of Activity
    self.frequencyRuns = [[String: Any]]()
    self.frequencyType = .oneTime
  }

  /// Initializer method which initializes all params
  /// - Parameter studyId: which hold StudyID
  /// - Parameter infoDict: Dictionary which contains all the properties of Activity
  init(studyId: String, infoDict: [String: Any]) {

    self.studyId = studyId

    // Need to reCheck with actual dictionary when passed
    if Utilities.isValidObject(someObject: infoDict as AnyObject?) {

      if Utilities.isValidValue(someObject: infoDict[kActivityId] as AnyObject) {
        self.actvityId = (infoDict[kActivityId] as? String)!
      }

      if Utilities.isValidValue(someObject: infoDict[kActivityVersion] as AnyObject) {
        self.version = (infoDict[kActivityVersion] as? String)!
      }

      if Utilities.isValidValue(someObject: infoDict[kActivityTitle] as AnyObject) {
        self.name = (infoDict[kActivityTitle] as? String)!
      }
      if Utilities.isValidValue(someObject: infoDict["state"] as AnyObject) {
        self.state = (infoDict["state"] as? String)!
      }
      if Utilities.isValidValue(someObject: infoDict[kActivityBranching] as AnyObject) {
        self.branching = (infoDict[kActivityBranching] as? Bool)!
      }
      if Utilities.isValidValue(someObject: infoDict[kActivityType] as AnyObject) {
        self.type = ActivityType(rawValue: (infoDict[kActivityType] as? String)!)
      }

      if Utilities.isValidValue(someObject: infoDict[kActivityStartTime] as AnyObject) {
        self.startDate = Utilities.getDateFromStringWithOutTimezone(
          dateString: (infoDict[kActivityStartTime] as? String)!
        )
      }
      if Utilities.isValidValue(someObject: infoDict["schedulingType"] as AnyObject) {
        let scheduleValue = infoDict["schedulingType"] as? String ?? "Regular"
        self.schedulingType = ActivityScheduleType(rawValue: scheduleValue)!
      }
      if Utilities.isValidValue(someObject: infoDict[kActivityEndTime] as AnyObject) {
        self.endDate = Utilities.getDateFromStringWithOutTimezone(
          dateString: (infoDict[kActivityEndTime] as? String)!
        )
      }

      if Utilities.isValidObject(someObject: infoDict[kActivityFrequency] as AnyObject?) {

        let frequencyDict: Dictionary = (infoDict[kActivityFrequency] as? [String: Any])!

        if Utilities.isValidObject(
          someObject: frequencyDict[kActivityFrequencyRuns] as AnyObject
        ) {
          self.frequencyRuns = frequencyDict[kActivityFrequencyRuns] as? [[String: Any]] ?? []
        }
        if Utilities.isValidObject(
          someObject: frequencyDict[kActivityManualAnchorRuns] as AnyObject
        ) {
          self.anchorRuns = frequencyDict[kActivityManualAnchorRuns] as? [[String: Any]]
        }

        if Utilities.isValidValue(
          someObject: frequencyDict[kActivityFrequencyType] as AnyObject
        ) {
          self.frequencyType = Frequency(
            rawValue: (frequencyDict[kActivityFrequencyType] as? String)!
          )!
        }

      }

      // AnchorDate
      let anchorDateDetail = infoDict["anchorDate"] as? [String: Any]
      if anchorDateDetail != nil && self.schedulingType == .anchorDate {
        setActivityAvailability(anchorDateDetail ?? [:])
      }

      let currentUser = User.currentUser
      if let userActivityStatus = currentUser.participatedActivites.filter({
        $0.activityId == self.actvityId && $0.studyId == self.studyId
      }).first {
        self.userParticipationStatus = userActivityStatus

      } else {
        self.userParticipationStatus = UserActivityStatus()
      }

      if Utilities.isValidValue(someObject: infoDict[kActivityTaskSubType] as AnyObject) {
        self.taskSubType = (infoDict[kActivityTaskSubType] as? String)!
      }

      if self.startDate != nil
        && (self.schedulingType == .regular || self.anchorDate?.sourceType == "EnrollmentDate")
      {
        self.calculateActivityRuns(studyId: self.studyId!)
      }
    }

  }

  /// Sets Activty's MetaData
  /// - Parameter activityDict: Dictionary which contains the properties of Activity
  func setActivityMetaData(activityDict: [String: Any]) {

    if Utilities.isValidObject(someObject: activityDict as AnyObject?) {

      if Utilities.isValidValue(someObject: activityDict[kActivityType] as AnyObject) {
        self.type? = ActivityType(rawValue: (activityDict[kActivityType] as? String)!)!

      }
      self.setInfo(infoDict: (activityDict[kActivityInfoMetaData] as? [String: Any])!)

      if Utilities.isValidObject(someObject: activityDict[kActivitySteps] as AnyObject?) {
        self.setStepArray(stepArray: (activityDict[kActivitySteps] as? Array)!)

      }
    }
  }

  /// Sets info part of activity from ActivityMetaData
  /// - Parameter infoDict: Dictionary which contains the properties of Activity
  func setInfo(infoDict: [String: Any]) {

    if Utilities.isValidObject(someObject: infoDict as AnyObject?) {

      if Utilities.isValidValue(someObject: infoDict["name"] as AnyObject) {
        self.shortName = infoDict["name"] as? String
      }

      if Utilities.isValidValue(someObject: infoDict[kActivityVersion] as AnyObject) {
        self.version = infoDict[kActivityVersion] as? String
      }
    }
  }

  /// Sets Configuration
  /// - Parameter configurationDict: Dictionary which contains the properties of Activity
  func setConfiguration(configurationDict: [String: Any]) {

    if Utilities.isValidObject(someObject: configurationDict as AnyObject?) {
      if Utilities.isValidValue(
        someObject: configurationDict[kActivityBranching] as AnyObject
      ) {
        self.branching = configurationDict[kActivityBranching] as? Bool
      }
      if Utilities.isValidValue(
        someObject: configurationDict[kActivityRandomization] as AnyObject
      ) {
        self.randomization = configurationDict[kActivityId] as? Bool
      }
    }
  }

  /// Sets  Activity Availability like Start Date, End Date
  /// - Parameter availability: Dictionary which contains the properties of Activity
  func setActivityAvailability(_ availability: [String: Any]) {

    self.anchorDate = AnchorDate.init(availability)
    if self.anchorDate?.sourceType == "EnrollmentDate" {
      var enrollmentDate = Study.currentStudy?.userParticipateState.joiningDate

      // update start date
      var startDateStringEnrollment = Utilities.formatterShort?.string(from: enrollmentDate!)
      let startTimeEnrollment = "00:00:00"
      startDateStringEnrollment =
        (startDateStringEnrollment ?? "") + " "
        + startTimeEnrollment
      enrollmentDate = Utilities.findDateFromString(
        dateString: startDateStringEnrollment ?? ""
      )

      self.anchorDate?.anchorDateValue = enrollmentDate
      let lifeTime = self.updateLifeTime(self.anchorDate!, frequency: self.frequencyType)

      // update start date
      var startDateString = Utilities.formatterShort?.string(from: lifeTime.0!)
      let startTime =
        (self.anchorDate?.startTime == nil)
        ? "00:00:00" : (self.anchorDate?.startTime)!
      startDateString = (startDateString ?? "") + " " + startTime
      let startdate = Utilities.findDateFromString(dateString: startDateString ?? "")

      // Update End date and time.
      var endDate: Date?
      if let anchorEndDate = lifeTime.1,
        let endTime = self.anchorDate?.endTime,
        let updatedEndDate = DateHelper.updateTime(of: anchorEndDate, with: endTime)
      {
        endDate = updatedEndDate
      } else if self.anchorDate?.endDays == 0 {  // LifeTime Anchor activity.
        endDate = nil
      }

      self.startDate = startdate
      self.endDate = endDate
    }
  }

  //method to set step array

  /// Sets Step Array
  /// - Parameter stepArray: Array of Steps of type Dictionary
  func setStepArray(stepArray: [[String: Any]]) {

    if Utilities.isValidObject(someObject: stepArray as AnyObject?) {
      self.steps? = stepArray
    }
  }

  /// Sets ORKSteps
  /// - Parameter orkStepArray: array of ORKSteps
  func setORKSteps(orkStepArray: [ORKStep]) {
    if Utilities.isValidObject(someObject: orkStepArray as AnyObject?) {
      self.orkSteps = orkStepArray
    }
  }

  //method to set step array

  /// Sets Acitivity Steps
  /// - Parameter stepArray: array of ActivityStep
  func setActivityStepArray(stepArray: [ActivityStep]) {

    if Utilities.isValidObject(someObject: stepArray as AnyObject?) {
      self.activitySteps? = stepArray
    }
  }

  /// Calculates activity runs
  /// - Parameter studyId: which holds StudyID
  func calculateActivityRuns(studyId: String) {

    Schedule().getRunsForActivity(
      activity: self,
      handler: { (runs) in
        if runs.count > 0 {
          self.activityRuns = runs
        }
      }
    )
  }

  /// Returns the RestortionData which is of type Data
  func getRestortionData() -> Data {
    return self.restortionData!
  }

  /// Sets restortion Data
  /// - Parameter restortionData: RestortionData which is of type Data
  func setRestortionData(restortionData: Data) {
    self.restortionData = restortionData
  }

  /// Update and returns StartDate and EndDate which is of type Date
  /// - Parameter anchorDate: instance of AnchorDate
  /// - Parameter frequency: instance of Frequency
  func updateLifeTime(_ anchorDate: AnchorDate, frequency: Frequency) -> (Date?, Date?) {
    guard let date = anchorDate.anchorDateValue else {
      return (nil, nil)
    }

    return updateLifeTime(date, frequency: frequency)
  }

  /// Returns StartDate and EndDate which is of type Date
  /// - Parameter date: which hold anchor date value
  /// - Parameter frequency: instance of Frequency
  func updateLifeTime(_ date: Date, frequency: Frequency) -> (Date?, Date?) {

    var startDate: Date!
    var endDate: Date!

    switch frequency {
    case .oneTime:

      let startDateInterval = TimeInterval(60 * 60 * 24 * (self.anchorDate?.startDays)!)
      let endDateInterval = TimeInterval(60 * 60 * 24 * (self.anchorDate?.endDays)!)

      startDate = date.addingTimeInterval(startDateInterval)
      endDate = date.addingTimeInterval(endDateInterval)

    case .daily:

      let startDateInterval = TimeInterval(60 * 60 * 24 * (self.anchorDate?.startDays)!)
      let endDateInterval = TimeInterval(60 * 60 * 24 * (self.anchorDate?.repeatInterval)!)
      startDate = date.addingTimeInterval(startDateInterval)
      endDate = startDate.addingTimeInterval(endDateInterval)

    case .weekly:

      let startDateInterval = TimeInterval(60 * 60 * 24 * (self.anchorDate?.startDays)!)
      let endDateInterval = TimeInterval(60 * 60 * 24 * 7 * (self.anchorDate?.repeatInterval)!)
      startDate = date.addingTimeInterval(startDateInterval)
      endDate = startDate.addingTimeInterval(endDateInterval)
    case .monthly:

      let startDateInterval = TimeInterval(60 * 60 * 24 * (self.anchorDate?.startDays)!)
      startDate = date.addingTimeInterval(startDateInterval)
      let calender = Calendar.current
      endDate = calender.date(
        byAdding: .month,
        value: (self.anchorDate?.repeatInterval)!,
        to: startDate
      )
    case .scheduled:

      let startDateInterval = TimeInterval(60 * 60 * 24 * (self.anchorDate?.startDays)!)
      let endDateInterval = TimeInterval(60 * 60 * 24 * (self.anchorDate?.endDays)!)

      startDate = date.addingTimeInterval(startDateInterval)
      endDate = date.addingTimeInterval(endDateInterval)

    }
    return (startDate, endDate)
  }
}

class AnchorDate {

  var sourceType: String?
  var sourceActivityId: String?
  var sourceKey: String?
  var sourceFormKey: String?
  var startDays: Int = 0
  var startTime: String?
  var endDays: Int = 0
  var repeatInterval: Int = 0
  var endTime: String?
  var anchorDateValue: Date?

  /// default Initializer Method
  init() {

  }

  /// Initializer method which initializes all params
  /// - Parameter anchorDateDetail: Dictionary which contains all the properties of AnchorDate
  init(_ anchorDateDetail: [String: Any]) {

    self.sourceType = anchorDateDetail["sourceType"] as? String
    self.sourceActivityId = anchorDateDetail["sourceActivityId"] as? String
    self.sourceKey = anchorDateDetail["sourceKey"] as? String
    self.sourceFormKey = anchorDateDetail["sourceFormKey"] as? String

    let anchorStart = anchorDateDetail["start"] as? [String: Any]
    self.startDays = anchorStart?["anchorDays"] as? Int ?? 0
    self.startTime = anchorStart?["time"] as? String

    let anchorEnd = anchorDateDetail["end"] as? [String: Any]
    self.endDays = anchorEnd?["anchorDays"] as? Int ?? 0
    self.endTime = anchorEnd?["time"] as? String
    self.repeatInterval = anchorEnd?["repeatInterval"] as? Int ?? 0

  }
}
