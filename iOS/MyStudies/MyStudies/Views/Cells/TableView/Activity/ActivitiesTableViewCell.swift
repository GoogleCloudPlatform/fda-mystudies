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

let kActivityTitle = "title"

protocol ActivitiesCellDelegate: class {
  func activityCell(cell: ActivitiesTableViewCell, activity: Activity)
}

class ActivitiesTableViewCell: UITableViewCell {

  // MARK: - Outlets
  @IBOutlet var imageIcon: UIImageView?

  @IBOutlet var labelDays: UILabel?
  @IBOutlet var labelHeading: UILabel?
  @IBOutlet var labelTime: UILabel?
  @IBOutlet var labelStatus: UILabel?
  @IBOutlet var labelRunStatus: UILabel?
  @IBOutlet var buttonMoreSchedules: UIButton?
  @IBOutlet var buttonMoreSchedulesBottomLine: UIView?
  @IBOutlet var disabledView: UIView!

  weak var delegate: ActivitiesCellDelegate?
  var currentActivity: Activity! = nil
  var availabilityStatus: ActivityAvailabilityStatus = .current

  override func prepareForReuse() {
    self.disabledView.isHidden = true
    selectionStyle = .default
    super.prepareForReuse()
  }

  /// Change the cell background color.
  override func setSelected(_ selected: Bool, animated: Bool) {
    let color = labelStatus?.backgroundColor
    super.setSelected(selected, animated: animated)
    // Configure the view for the selected state
    if selected {
      labelStatus?.backgroundColor = color
    }
  }

  override func setHighlighted(_ highlighted: Bool, animated: Bool) {
    let color = labelStatus?.backgroundColor
    super.setHighlighted(highlighted, animated: animated)
    if highlighted {
      labelStatus?.backgroundColor = color
    }
  }

  /// Updates the cell selection state.
  private func updateSelfSelectionState() {
    // Disable selection for past and upcoming activities.
    if availabilityStatus == .past
      || availabilityStatus == .upcoming
    {
      selectionStyle = .none
    } else if let status = currentActivity.userParticipationStatus,
      status.status == .abandoned || status.status == .completed
    {
      selectionStyle = .none
      disabledView.isHidden = false
    } else {
      selectionStyle = .default
      disabledView.isHidden = true
    }
  }

  /// Update cell UI
  /// - Parameters:
  ///   - activity:  Access the value from Activity class.
  ///   - availablityStatus: Access the value from ActivityAvailabilityStatus enum.
  func populateCellDataWithActivity(
    activity: Activity,
    availablityStatus: ActivityAvailabilityStatus
  ) {

    self.labelTime?.text = ""
    self.availabilityStatus = availablityStatus
    self.currentActivity = activity
    self.labelHeading?.text = activity.name
    self.labelDays?.text = activity.frequencyType.description

    if availablityStatus != .upcoming {

      labelRunStatus?.isHidden = false

      if activity.currentRunId == 0 {
        labelStatus?.isHidden = true

      } else {
        labelStatus?.isHidden = false
      }
    } else {

      labelRunStatus?.isHidden = true
      labelStatus?.isHidden = true
    }
    self.calculateActivityTimings(activity: activity)
    self.setUserStatusForActivity(activity: activity)

    // update activity run details as compelted and missed
    self.updateUserRunStatus(activity: activity)

    updateSelfSelectionState()

    if availablityStatus == .past {

      if activity.incompletedRuns != 0 {
        self.labelStatus?.backgroundColor = UIColor.red
        self.labelStatus?.text = UserActivityStatus.ActivityStatus.abandoned.description

      } else if activity.totalRuns == 0 {
        self.labelStatus?.backgroundColor = UIColor.gray
        self.labelStatus?.text = UserActivityStatus.ActivityStatus.expired.description
      } else {
        self.labelStatus?.backgroundColor = kGreenColor
        self.labelStatus?.text = UserActivityStatus.ActivityStatus.completed.description
      }
    }
  }

  /// Update the status of `Activity` `Run`.
  ///  - Parameter activity: Instance of run Activity.
  func updateUserRunStatus(activity: Activity) {

    let currentRunId = (activity.totalRuns != 0) ? String(activity.currentRunId) : "0"

    self.labelRunStatus?.text =
      "Run: " + currentRunId + " of " + String(activity.totalRuns) + ", "
      + String(activity.compeltedRuns) + " done" + ", " + String(activity.incompletedRuns)
      + " missed"

    if activity.totalRuns <= 1 {
      self.buttonMoreSchedules?.isHidden = true
      self.buttonMoreSchedulesBottomLine?.isHidden = true

    } else {
      self.buttonMoreSchedules?.isHidden = false
      self.buttonMoreSchedulesBottomLine?.isHidden = false
      let moreSchedulesTitle = "+" + String(activity.totalRuns - 1) + " more"
      self.buttonMoreSchedules?.setTitle(moreSchedulesTitle, for: .normal)
    }
  }

  /// Set User Status For Activity.
  /// - Parameter activity: Instance of activity.
  func setUserStatusForActivity(activity: Activity) {

    let currentUser = User.currentUser

    if let userActivityStatus = currentUser.participatedActivites.filter({
      $0.activityId == activity.actvityId && $0.studyId == activity.studyId
        && $0
          .activityRunId
          == String(activity.currentRunId)
    }).first {

      // assign to study
      activity.userParticipationStatus = userActivityStatus

      // user study status
      labelStatus?.text = userActivityStatus.status.description

      switch userActivityStatus.status {
      case .inProgress:
        self.labelStatus?.backgroundColor = kYellowColor
      case .yetToJoin:
        self.labelStatus?.backgroundColor = kBlueColor
      case .abandoned:
        self.labelStatus?.backgroundColor = UIColor.red
      case .completed:
        self.labelStatus?.backgroundColor = kGreenColor
      case .expired:
        self.labelStatus?.backgroundColor = UIColor.gray
      }
    } else {

      self.labelStatus?.backgroundColor = kBlueColor

      // if in database their is no staus update for past activities
      // assigning abandoned status by default
      if self.availabilityStatus == .past {

        self.labelStatus?.backgroundColor = UIColor.red
        self.labelStatus?.text = UserActivityStatus.ActivityStatus.abandoned.description

      } else {

        let activityStatus = UserActivityStatus()
        activityStatus.status = .yetToJoin
        activity.userParticipationStatus = activityStatus

        self.labelStatus?.backgroundColor = kBlueColor
        self.labelStatus?.text = UserActivityStatus.ActivityStatus.yetToJoin.description
      }
    }
  }

  ///  Used to calculate Activity Timings.
  /// - Parameter activity: Access the value from Activity class.
  func calculateActivityTimings(activity: Activity) {

    var startDate = activity.startDate
    var endDate = activity.endDate

    startDate?.updateWithOffset()
    endDate?.updateWithOffset()

    let frequency = activity.frequencyType

    if activity.type == ActivityType.activeTask {
      imageIcon?.image = UIImage.init(named: "taskIcon")
    } else {

      imageIcon?.image = UIImage.init(named: "surveyIcon")
    }

    switch frequency {
    case .oneTime:
      setActivityTimingsForOneTime(
        startDate: startDate,
        endDate: endDate,
        activity: activity
      )

    case .daily:
      var runStartTimingsList: [String] = []
      for dict in activity.frequencyRuns! {
        let startTime = dict[kScheduleStartTime] as! String
        let runStartTime = ActivitiesTableViewCell.dailyFormatter.date(from: startTime)
        let runStartTimeAsString = ActivitiesTableViewCell.timeFormatter.string(
          from: runStartTime!
        )
        runStartTimingsList.append(runStartTimeAsString)
      }
      var runStartTime = runStartTimingsList.joined(separator: " | ")
      runStartTime += " everyday"

      let dailyStartDate = ActivitiesTableViewCell.dailyActivityFormatter.string(
        from: startDate!
      )
      let endDate = ActivitiesTableViewCell.dailyActivityFormatter.string(from: endDate!)
      labelTime?.text = runStartTime + "\n" + dailyStartDate + " to " + endDate

    case .weekly:
      var weeklyStartTime = ActivitiesTableViewCell.weeklyformatter.string(from: startDate!)
      weeklyStartTime = weeklyStartTime.replacingOccurrences(of: "+", with: "every")
      weeklyStartTime = weeklyStartTime.replacingOccurrences(of: ";", with: "\n")
      let endDate = ActivitiesTableViewCell.formatter.string(from: endDate!)

      labelTime?.text = weeklyStartTime + " to " + endDate

    case .monthly:
      var monthlyStartTime = ActivitiesTableViewCell.monthlyformatter.string(from: startDate!)
      monthlyStartTime = monthlyStartTime.replacingOccurrences(of: "+", with: "on day")
      monthlyStartTime = monthlyStartTime.replacingOccurrences(of: ";", with: "each month\n")
      if let endDate = endDate {
        let endDateString = DateHelper.formattedShortMonthYear(from: endDate)
        labelTime?.text = monthlyStartTime + " to " + endDateString
      } else {
        labelTime?.text = monthlyStartTime
      }

    case .scheduled:
      var runStartDate: Date?
      var runEndDate: Date?
      if let firstRun = activity.activityRuns.first {
        runStartDate = firstRun.startDate
        runEndDate = firstRun.endDate
      } else if activity.currentRun != nil {
        runStartDate = activity.currentRun.startDate
        runEndDate = activity.currentRun.endDate
      } else {
        let run = activity.activityRuns.filter({ $0.runId == activity.currentRunId }).first
        runStartDate = run?.startDate
        runEndDate = run?.endDate
      }
      if runEndDate == nil || runStartDate == nil {
        runStartDate = activity.startDate
        runEndDate = activity.endDate
      }
      if var startDate = runStartDate,
        var endDate = runEndDate
      {
        startDate.updateWithOffset()
        endDate.updateWithOffset()
        let currentRunStartDate = ActivitiesTableViewCell.customScheduleFormatter.string(
          from: startDate
        )
        let currentRunEndDate = ActivitiesTableViewCell.customScheduleFormatter.string(
          from: endDate
        )
        labelTime?.text = currentRunStartDate + " to " + currentRunEndDate
      }
    }
  }

  private func setActivityTimingsForOneTime(
    startDate: Date?,
    endDate: Date?,
    activity: Activity
  ) {
    var startDateString = ""
    if let startDate = startDate {
      startDateString = ActivitiesTableViewCell.oneTimeFormatter.string(from: startDate)
    }
    var endDateString = ""
    if let endDate = endDate {
      endDateString = ActivitiesTableViewCell.oneTimeFormatter.string(from: endDate)
    }
    startDateString = startDateString.replacingOccurrences(of: ";", with: "on")
    endDateString = endDateString.replacingOccurrences(of: ";", with: "on")

    if activity.isStudyLifeTime && activity.isLaunchWithStudy {
      return
    } else if activity.isLaunchWithStudy,
      let endDate = endDate
    {
      if endDate > Date() {
        labelTime?.text = "Ends: " + endDateString
      } else {
        labelTime?.text = "Ended: " + endDateString
      }
    } else if activity.isStudyLifeTime,
      let startDate = startDate
    {
      if startDate > Date() {
        labelTime?.text = "Starts: " + startDateString
      }
    } else if !endDateString.isEmpty {
      labelTime?.text = startDateString + " - " + endDateString
    }
  }

  // MARK: - Button Action

  /// Clicked on  More Schedules.
  @IBAction func buttonMoreSchedulesClicked(_: UIButton) {
    self.delegate?.activityCell(cell: self, activity: self.currentActivity)
  }

  /// Formatters for different frequency types.
  private static let formatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "MMM dd, yyyy"
    return formatter
  }()

  private static let dailyActivityFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "MMM dd, yyyy"
    return formatter
  }()

  private static let oneTimeFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma ; MMM dd, yyyy"
    return formatter
  }()

  private static var customScheduleFormatter: DateFormatter {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma, MMM dd, yyyy"
    return formatter
  }

  private static let weeklyformatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma + EEE;MMM dd, yyyy"
    return formatter
  }()

  private static let monthlyformatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma + dd ;MMM yyyy"
    return formatter
  }()

  private static let timeFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma"
    return formatter
  }()

  private static let dailyFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "HH:mm:ss"
    return formatter
  }()
}
