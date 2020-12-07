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
    view.tableview?.register(UITableViewCell.self, forCellReuseIdentifier: "Cell")
    view.tableview?.delegate = view
    view.tableview?.dataSource = view
    let height = (activity.activityRuns.count * 44) + 104
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
    return self.activity.activityRuns.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    let cell = tableView.dequeueReusableCell(withIdentifier: "Cell", for: indexPath)
    cell.textLabel?.font = UIFont(name: "HelveticaNeue-Light", size: 13)
    let activityRun = self.activity.activityRuns[indexPath.row]
    if var startDate = activityRun.startDate,
      var endDate = activityRun.endDate
    {
      startDate.updateWithOffset()
      endDate.updateWithOffset()
      cell.textLabel?.text =
        ActivitySchedules.formatter.string(from: startDate)
        + " to "
        + ActivitySchedules.formatter.string(from: endDate)
    }

    if activityRun.runId == self.activity.currentRunId {
      cell.textLabel?.textColor = kBlueColor

    } else if activityRun.runId < self.activity.currentRunId {
      cell.textLabel?.textColor = UIColor.gray
    }
    cell.textLabel?.textAlignment = .center
    cell.textLabel?.adjustsFontSizeToFitWidth = true
    return cell
  }

  private static let formatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma, MMM dd, YYYY"
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
    formatter.dateFormat = "YYYY/MM/dd HH:mm:ss"
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
