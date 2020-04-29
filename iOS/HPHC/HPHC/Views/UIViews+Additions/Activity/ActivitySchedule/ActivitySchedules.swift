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
    cell.textLabel?.text =
      ActivitySchedules.formatter.string(from: activityRun.startDate)
      + " to "
      + ActivitySchedules.formatter.string(from: activityRun.endDate)

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

  var dataSourceKeysForLabkey: [[String: String]] = []

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

  init() {

  }

  // MARK: Helper Methods
  func checkUpdates() {
    if StudyUpdates.studyActivitiesUpdated {
      self.sendRequestToGetDashboardInfo()

    } else {

      // Load Stats List from DB
      DBHandler.loadStatisticsForStudy(studyId: (Study.currentStudy?.studyId)!) {
        (statiticsList) in

        if statiticsList.count != 0 {
          StudyDashboard.instance.statistics = statiticsList
          self.getDataKeysForCurrentStudy()
          let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
          appDelegate.addAndRemoveProgress(add: true)

        } else {
          // Fetch DashboardInfo
          self.sendRequestToGetDashboardInfo()
        }
      }
    }
  }

  func sendRequestToGetDashboardInfo() {
    WCPServices().getStudyDashboardInfo(studyId: (Study.currentStudy?.studyId)!, delegate: self)
  }

  func handleExecuteSQLResponse() {

    if !self.dataSourceKeysForLabkey.isEmpty {
      self.dataSourceKeysForLabkey.removeFirst()
    }
    self.sendRequestToGetDashboardResponse()
  }

  func getDataKeysForCurrentStudy() {

    DBHandler.getDataSourceKeyForActivity(studyId: (Study.currentStudy?.studyId)!) {
      (activityKeys) in
      if activityKeys.count > 0 {
        self.dataSourceKeysForLabkey = activityKeys
        // GetDashboardResponse from server
        self.sendRequestToGetDashboardResponse()
      }
    }

  }

  func sendRequestToGetDashboardResponse() {

    if self.dataSourceKeysForLabkey.count != 0 {
      let details = self.dataSourceKeysForLabkey.first
      let activityId = details?["activityId"]
      let activity = Study.currentStudy?.activities.filter({ $0.actvityId == activityId })
        .first
      var keys = details?["keys"] ?? ""
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
        handleExecuteSQLResponse()
        return
      }
      // Get Survey Response from Server
      ResponseServices().getParticipantResponse(
        activity: currentActivity,
        study: study,
        keys: keys,
        delegate: self
      )
    } else {
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
          let responseValue = (value["value"] as? Float)!
          let count = (value["count"] as? Float)!
          let dateString = value["date"] as? String ?? ""
          // SetData Format
          if let date = ResponseDataFetch.responseDateFormatter.date(from: dateString) {
            let localDateAsString = ResponseDataFetch.localDateFormatter.string(from: date)
            if let localDate = ResponseDataFetch.localDateFormatter.date(
              from: localDateAsString
            ) {
              // Save Stats to DB
              DBHandler.saveStatisticsDataFor(
                activityId: activityId!,
                key: key!,
                data: responseValue,
                fkDuration: Int(count),
                date: localDate
              )
            }
          }
        }
      }
      let key = "LabKeyResponse" + (Study.currentStudy?.studyId)!
      UserDefaults.standard.set(true, forKey: key)

      let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
      appDelegate.addAndRemoveProgress(add: false)

    }
  }

  // MARK: Webservice Delegates
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {}

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    if requestName as String == WCPMethods.studyDashboard.method.methodName {
      self.getDataKeysForCurrentStudy()

    } else if requestName as String == ResponseMethods.getParticipantResponse.description {
      self.handleExecuteSQLResponse()
    }

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    if requestName as String == ResponseMethods.getParticipantResponse.description {
      self.handleExecuteSQLResponse()
    }
  }

}
