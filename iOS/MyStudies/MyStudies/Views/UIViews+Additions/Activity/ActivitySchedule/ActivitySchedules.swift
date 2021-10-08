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

// MARK: - ActivitySchedules Class
class ActivitySchedules: UIView, UITableViewDelegate, UITableViewDataSource {

  // MARK: - Outlets
  @IBOutlet var tableview: UITableView?

  @IBOutlet var buttonCancel: UIButton!
  @IBOutlet var heightLayoutConstraint: NSLayoutConstraint!

  var activity: Activity!
  var highlightedRun = -1
  var activityCount = 0

  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
  }

  class func instanceFromNib(frame: CGRect, activity: Activity) -> ActivitySchedules {
    let view =
      (UINib(nibName: "ActivitySchedules", bundle: nil).instantiate(
        withOwner: nil,
        options: nil
      )[0]
      as? ActivitySchedules)!
    view.frame = frame
    view.activity = activity
    
    let frequencyType = activity.frequencyType
    var valActivityRuns = activity.activityRuns ?? []
    
    if frequencyType == Frequency.scheduled {
      let val1 = view.getRunsForActivity(activity: activity)
      
      let valStartdate = val1.startDate ?? activity.startDate
      var valEnddate = activity.endDate
      if val1.startDate != nil {
        valEnddate = val1.endDate ?? activity.endDate
      }
      
      valActivityRuns = view.setScheduledRuns(startTime: valStartdate, endTime: valEnddate, valActivityRuns: valActivityRuns)
      view.activityCount = valActivityRuns.count
      view.setHighlightedAnchorActivity(valActivityRuns)
    } else if activity.activityRuns.isEmpty == false {
      view.activityCount = activity.activityRuns.count
      view.setHighlightedActivity()
    } else {
      view.activityCount = activity.frequencyRuns?.count ?? 0
    }
    view.tableview?.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
    view.tableview?.delegate = view
    view.tableview?.dataSource = view
    let height = (view.activityCount * 44) + 104
    let maxViewHeight = Int(UIScreen.main.bounds.size.height - 67)
    view.heightLayoutConstraint.constant = CGFloat(
      (height > maxViewHeight) ? maxViewHeight : height
    )
    view.layoutIfNeeded()

    return view
  }

  // MARK: - Button Action
  @IBAction func buttonCancelClicked(_: UIButton) {
    self.removeFromSuperview()
  }

  // MARK: Tableview Delegates
  func numberOfSections(in tableView: UITableView) -> Int {
    return 1
  }

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    if activityCount != 0 {
      return activityCount
    } else if self.activity.activityRuns.isEmpty, let frequencyRuns = self.activity.frequencyRuns {
      return frequencyRuns.count
    } else {
      return self.activity.activityRuns.count
    }
  }

  func setHighlightedAnchorActivity(_ valActivityRuns: [ActivityRun]) {
    self.highlightedRun = activity.currentRunId
    if highlightedRun >= 0 && self.activity.activityRuns.count > 0 {
      var activityRun: ActivityRun
      if self.highlightedRun == 0 {
        activityRun = self.activity.activityRuns[highlightedRun]
      } else {
        activityRun = self.activity.activityRuns[highlightedRun - 1]
      }
      
      var highlightedRun2 = -1
      var highlightedRunFinal = highlightedRun
      for actRun in valActivityRuns {
        highlightedRun2 += 1
        if actRun.startDate == activityRun.startDate && actRun.endDate == activityRun.endDate {
          highlightedRunFinal = highlightedRun2
        }
      }
      
      if var endDate = valActivityRuns[highlightedRunFinal].endDate {
        endDate.updateWithOffset()
        let valCurrentDate = Date()
        if endDate > valCurrentDate {
          self.highlightedRun = highlightedRunFinal
        } else if (highlightedRunFinal + 1) < valActivityRuns.count {
          self.highlightedRun = highlightedRunFinal + 1
        } else {
          self.highlightedRun = valActivityRuns.count + 3
        }
      }
    } else if highlightedRun > 0 && self.activity.activityRuns.count == 0 {
      self.highlightedRun = valActivityRuns.count + 3
    }
  }
  
  func setHighlightedActivity() {
    self.highlightedRun = activity.currentRunId
    if highlightedRun > 0 {
      let activityRun = self.activity.activityRuns[highlightedRun - 1]
      if var endDate = activityRun.endDate {
        endDate.updateWithOffset()
        let valCurrentDate = Date()
        if endDate < valCurrentDate {
          self.highlightedRun = highlightedRun + 1
        }
      }
    }
  }
  
  func getRunsForActivity(activity: Activity) -> (startDate: Date?, endDate: Date?) {
    var startTime: Date?
    var endTime: Date?
    guard activity.state == "active" else {
      return (nil, nil)
    }
    // get joiningDate
    let studyStatus = User.currentUser.participatedStudies.filter({ $0.studyId == activity.studyId }
    ).last
    guard let joiningDate = studyStatus?.joiningDate else {
      return (nil, nil)
    }
    let start = activity.startDate?.utcDate()
    
    var endDateResult: ComparisonResult?
    if activity.endDate != nil {
      let end = activity.endDate?.utcDate()
      endDateResult = (end?.compare(joiningDate))! as ComparisonResult
    }
    let startDateResult = (start?.compare(joiningDate)) ?? .orderedAscending
    
    // check if user joined after activity is ended
    if endDateResult != nil && endDateResult == .orderedAscending {
      
    } else {
      // check if user joined before activity is started
      if startDateResult == .orderedDescending {
        startTime = start
      } else {
        startTime = joiningDate
      }
      
      endTime = activity.endDate?.utcDate()
    }
    return (startTime, endTime)
  }
  
/// Sets Schedule Runs
  func setScheduledRuns(startTime: Date?, endTime: Date?, valActivityRuns: [ActivityRun]) -> [ActivityRun] {
    var scheduledTimings: [[String: Any]] = []
    _ = startTime
    let endTime = endTime
    var activityRuns: [ActivityRun]! = []
    
    let offset = UserDefaults.standard.value(forKey: "offset") as? Int
    let activityEndTime = endTime?.addingTimeInterval(TimeInterval(offset!))
    var runId = 1
    
    let schedulingType = activity.schedulingType
    if schedulingType == .anchorDate {
      scheduledTimings = activity.anchorRuns!
    } else {
      scheduledTimings = activity.frequencyRuns!
    }
    
    for timing in scheduledTimings {
      var runStartDate: Date?
      var runEndDate: Date?
      
      if schedulingType == .anchorDate {
        let startDays = timing["startDays"] as? Int ?? 0
        let endDays = timing["endDays"] as? Int ?? 0
        _ = timing["time"] as? String ?? "00:00:00"
        
        var anchorDateInitial: Date?
        if activity.anchorDate?.anchorDateValue != nil {
          anchorDateInitial = activity.anchorDate?.anchorDateValue
        } else {
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
          anchorDateInitial = enrollmentDate
        }
        
        guard let anchorDate = anchorDateInitial else { return valActivityRuns }
        
        let startDateInterval = TimeInterval(60 * 60 * 24 * (startDays))
        let endDateInterval = TimeInterval(60 * 60 * 24 * (endDays))
        
        runStartDate = anchorDate.addingTimeInterval(startDateInterval)
        runEndDate = anchorDate.addingTimeInterval(endDateInterval)
        
        // update start date
        var startDateString = Utilities.formatterShort?.string(from: runStartDate!)
        let startTime = timing["startTime"] as? String ?? "00:00:00"
        startDateString = (startDateString ?? "") + " " + startTime
        let startdate = Utilities.findDateFromString(dateString: startDateString ?? "")
        
        // update end date
        var endDateString = Utilities.formatterShort?.string(from: runEndDate!)
        let endTime = timing["endTime"] as? String ?? "23:59:59"
        endDateString = (endDateString ?? "") + " " + endTime
        let endDate = Utilities.findDateFromString(dateString: endDateString ?? "")
        
        runStartDate = startdate
        runEndDate = endDate
      } else {
        // run start time creation
        let scheduledStartTime = timing[kScheduleStartTime]
        runStartDate = Utilities.getDateFromStringWithOutTimezone(
          dateString: scheduledStartTime! as! String
        )
        
        // run end time creation
        let scheduledEndTime = timing[kScheduleEndTime]
        runEndDate = Utilities.getDateFromStringWithOutTimezone(
          dateString: scheduledEndTime! as! String
        )
      }
      let offset = UserDefaults.standard.value(forKey: "offset") as? Int
      let updatedStartTime = runStartDate?.addingTimeInterval(TimeInterval(offset!))
      let updatedEndTime = runEndDate?.addingTimeInterval(TimeInterval(offset!))
      
      if activityEndTime! > updatedStartTime! {
        if !(updatedEndTime! < updatedStartTime!) {
          // append in activityRun array
          let activityRun = ActivityRun()
          activityRun.runId = runId
          activityRun.startDate = updatedStartTime
          activityRun.endDate = updatedEndTime
          activityRuns.append(activityRun)
          
          runId += 1
        }
      } else if updatedEndTime! > updatedStartTime! {
        let updatedEndTime = runEndDate?.addingTimeInterval(TimeInterval(offset!))
        
        if !(updatedEndTime! < updatedStartTime!) {
          // append in activityRun array
          let activityRun = ActivityRun()
          activityRun.runId = runId
          activityRun.startDate = updatedStartTime
          activityRun.endDate = updatedEndTime
          activityRuns.append(activityRun)
          
          runId += 1
        }
      }
    }
    return activityRuns
  }
  
  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
    let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
    cell.textLabel?.font = UIFont(name: "HelveticaNeue-Light", size: 13)
    
    let frequencyType = activity.frequencyType
    
    if frequencyType == Frequency.scheduled {
      var valActivityRuns = activity.activityRuns ?? []
      let val1 = self.getRunsForActivity(activity: activity)
      
      let valStartdate = val1.startDate ?? activity.startDate
      var valEnddate = activity.endDate
      if val1.startDate != nil {
        valEnddate = val1.endDate ?? activity.endDate
      }
      
      valActivityRuns = self.setScheduledRuns(startTime: valStartdate, endTime: valEnddate, valActivityRuns: valActivityRuns)
      if var startDate = valActivityRuns[indexPath.row].startDate,
         var endDate = valActivityRuns[indexPath.row].endDate {
        startDate.updateWithOffset()
        endDate.updateWithOffset()
        cell.textLabel?.text =
          ActivitySchedules.formatter.string(from: startDate)
          + " to "
          + ActivitySchedules.formatter.string(from: endDate)
      }
      if indexPath.row == highlightedRun {
        cell.textLabel?.textColor = kBlueColor
        
      } else if indexPath.row < highlightedRun {
        cell.textLabel?.textColor = UIColor.gray
      }
    } else if self.activity.activityRuns.isEmpty == false {
        let activityRun = self.activity.activityRuns[indexPath.row]
        if var startDate = activityRun.startDate,
          var endDate = activityRun.endDate {
          startDate.updateWithOffset()
          endDate.updateWithOffset()
          cell.textLabel?.text =
            ActivitySchedules.formatter.string(from: startDate)
            + " to "
            + ActivitySchedules.formatter.string(from: endDate)
        }

        if activityRun.runId == highlightedRun {
          cell.textLabel?.textColor = kBlueColor
        } else if activityRun.runId < highlightedRun {
          cell.textLabel?.textColor = UIColor.gray
        }
    } else if let frequesncyRuns = self.activity.frequencyRuns, frequesncyRuns.count > 0 {
        let frequencyRun = frequesncyRuns[indexPath.row]
        
        if let startDate = frequencyRun["startTime"] as? String, let endDate = frequencyRun["endTime"] as? String {
            if var startTime = Utilities.getDateFromStringWithOutTimezone(
                dateString: (startDate)
            ),
            var endTime = Utilities.getDateFromStringWithOutTimezone(
                dateString: (endDate)
            ) {
                startTime.updateWithOffset()
                endTime.updateWithOffset()
                cell.textLabel?.text =
                    ActivitySchedules.formatter.string(from: startTime)
                  + " to "
                  + ActivitySchedules.formatter.string(from: endTime)
            }
        }
          cell.textLabel?.textColor = UIColor.gray
    }
    
    cell.textLabel?.textAlignment = .center
    cell.textLabel?.adjustsFontSizeToFitWidth = true
    return cell
  }

static let formatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma, MMM dd, yyyy"
    return formatter
  }()
}

class ResponseDataFetch: NMWebServiceDelegate {

  var dataSourceKeysForResponse: [[String: String]] = []

  private var study: Study

  var completion: (() -> Void)?

  var isFetchComplete = false {
    willSet {
      let key = "Response" + study.studyId
      UserDefaults.standard.set(newValue, forKey: key)
    }
  }

  let fetchQueryGroup = DispatchGroup()

  static let responseDateFormatter: DateFormatter = {
    let dateFormatter = DateFormatter()
    let locale = Locale(identifier: "en_US_POSIX")
    dateFormatter.timeZone = TimeZone.current
    dateFormatter.locale = locale
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss:SSSZ"
    return dateFormatter
  }()

  public static let localDateFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.timeZone = TimeZone.current
    formatter.dateFormat = "yyyy/MM/dd HH:mm:ss"
    return formatter
  }()

  init(study: Study) {
    self.study = study
  }

  // MARK: Helper Methods
  func checkUpdates(completion: @escaping () -> Void) {
    self.completion = completion
    if StudyUpdates.studyActivitiesUpdated {
      self.sendRequestToGetDashboardInfo()
    } else {
      // Load Stats List from DB
      DBHandler.loadStatisticsForStudy(studyId: study.studyId) { (statiticsList) in
        if !statiticsList.isEmpty {
          StudyDashboard.instance.statistics = statiticsList
          self.getDataKeysForCurrentStudy()
        } else {
          // Fetch DashboardInfo
          self.sendRequestToGetDashboardInfo()
        }
      }
    }
  }

  func sendRequestToGetDashboardInfo() {
    WCPServices().getStudyDashboardInfo(studyId: study.studyId, delegate: self)
  }

  func getDataKeysForCurrentStudy() {

    DBHandler.getDataSourceKeyForActivity(studyId: study.studyId) { [unowned self] (activityKeys) in
      if !activityKeys.isEmpty {
        self.dataSourceKeysForResponse = activityKeys
        // GetDashboardResponse from server
        self.sendRequestToGetDashboardResponse()
      } else {
        self.isFetchComplete = true
        self.completion?()
      }
    }
  }

  func sendRequestToGetDashboardResponse() {

    for details in dataSourceKeysForResponse {
      let activityId = details["activityId"]
      let activity = Study.currentStudy?.activities.filter({ $0.actvityId == activityId })
        .first
      var keys = details["keys"] ?? ""
      if activity?.type == ActivityType.activeTask {
        if activity?.taskSubType == "fetalKickCounter" {
          keys = "\"count\",duration"
        } else if activity?.taskSubType == "towerOfHanoi" {
          keys = "numberOfMoves"
        } else if activity?.taskSubType == "spatialSpanMemory" {
          keys = "NumberofGames,Score,NumberofFailures"
        }
      }
      guard let currentActivity = activity,
        let study = Study.currentStudy
      else {
        continue
      }

      fetchQueryGroup.enter()
      // Get Survey Response from Server
      ResponseServices().getParticipantResponse(
        activity: currentActivity,
        study: study,
        keys: keys,
        delegate: self
      )
    }
    fetchQueryGroup.notify(queue: .main) {
      // Save responses.
      self.saveResponsesInDB()
    }
  }

  private func saveResponsesInDB() {
    // save response in database
    let responses = StudyDashboard.instance.dashboardResponse
    for response in responses {

      let activityId = response.activityId
      let activity = Study.currentStudy?.activities.filter({ $0.actvityId == activityId })
        .first
      var key = response.key
      if activity?.type == ActivityType.activeTask {

        if activity?.taskSubType == "fetalKickCounter"
          || activity?.taskSubType
            == "towerOfHanoi"
        {
          key = activityId!
        }
      }

      let values = response.values
      for value in values {
        let responseValue = value["value"] as? Double ?? 0.0
        let count = value["count"] as? Float ?? 0
        let dateString = value["date"] as? String ?? ""
        // SetData Format
        if let date = ResponseDataFetch.responseDateFormatter.date(from: dateString),
          let key = key,
          let activityID = activityId
        {
          // Save Stats to DB
          DBHandler.saveStatisticsDataFor(
            activityId: activityID,
            key: key,
            data: Float(responseValue),
            fkDuration: Int(count),
            date: date
          )
        }
      }
    }
    isFetchComplete = true
    completion?()
  }

  // MARK: Webservice Delegates
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {}

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    if requestName as String == WCPMethods.studyDashboard.method.methodName {
      self.getDataKeysForCurrentStudy()

    } else if requestName as String == ResponseMethods.getParticipantResponse.description {
      fetchQueryGroup.leave()
    }

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    if requestName as String == ResponseMethods.getParticipantResponse.description {
      fetchQueryGroup.leave()
    }
  }

}
