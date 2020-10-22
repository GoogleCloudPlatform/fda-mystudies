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

enum StatisticsType: String {
  case activity = "Activity"
  case sleep = "Sleep"
  case weight = "Weight"
  case nutrition = "Nutrition"
  case heartRate = "HeartRate"
  case bloodGlucose = "BloodGlucose"
  case activeTask = "ActiveTask"
  case babyKicks = "BabyKicks"
  case mood = "Mood"
  case other = "Other"

  var description: String {
    switch self {
    case .activity:
      return "Activity"
    case .sleep:
      return "Sleep"
    case .weight:
      return "Weight"
    case .nutrition:
      return "Nutrition"
    case .heartRate:
      return "Heart Rate"
    case .bloodGlucose:
      return "Blood Glucose"
    case .activeTask:
      return "Active Task"
    case .babyKicks:
      return "Baby Kicks"
    case .mood:
      return "Mood"
    case .other:
      return "Other"
    }
  }
}

class StudyDashboardStatisticsCollectionViewCell: UICollectionViewCell {

  // MARK: - Outlets
  @IBOutlet var statisticsImage: UIImageView?

  @IBOutlet var labelStatisticsText: UILabel?
  @IBOutlet var labelStatisticsCount: UILabel?
  @IBOutlet var labelUnit: UILabel?
  var stats: DashboardStatistics!

  /// Display DashboardStats for selected Tab.
  func displayStatisics(
    data: DashboardStatistics,
    startDate: Date,
    endDate: Date?,
    tab: SelectedTab
  ) {

    stats = data
    labelStatisticsText?.text = data.displayName
    labelUnit?.text = data.unit?.uppercased()

    self.displayStateTypeImage()

    switch tab {
    case .day:
      self.handleForDay(date: startDate)
    case .week:
      self.handleForWeek(startDate: startDate, endDate: endDate!)
    case .month:
      self.handleForMonth(date: startDate)
    }
  }

  func displayStateTypeImage() {

    if stats.statType == nil {
      statisticsImage?.image = UIImage(named: "activity")

    } else {

      switch stats.statType! {
      case StatisticsType.activity.description:
        statisticsImage?.image = UIImage(named: "stat_icn_active_task")
      case StatisticsType.sleep.description:
        statisticsImage?.image = UIImage(named: "stat_icn_sleep")
      case StatisticsType.weight.description:
        statisticsImage?.image = UIImage(named: "stat_icn_weight")
      case StatisticsType.nutrition.description:
        statisticsImage?.image = UIImage(named: "stat_icn_nutrition")
      case StatisticsType.heartRate.description:
        statisticsImage?.image = UIImage(named: "stat_icn_heart_rate")
      case StatisticsType.bloodGlucose.description:
        statisticsImage?.image = UIImage(named: "stat_icn_glucose")
      case StatisticsType.activeTask.description:
        statisticsImage?.image = UIImage(named: "stat_icn_active_task")
      case StatisticsType.babyKicks.description:
        statisticsImage?.image = UIImage(named: "stat_icn_baby_kicks")
      case StatisticsType.mood.description:
        statisticsImage?.image = UIImage(named: "stat_icn_mood")
      case StatisticsType.other.description:
        statisticsImage?.image = UIImage(named: "stat_icn_other")
      default:
        statisticsImage?.image = UIImage(named: "stat_icn_other")
      }
    }

  }

  /// Used to display Statistics cell.
  func displayStatisics(data: DashboardStatistics) {

    labelStatisticsText?.text = data.displayName
    let array = data.statList.map { $0.data }

    if data.calculation! == StatisticsFormula.maximum.rawValue {
      let max = array.max()
      labelStatisticsCount?.text = String(describing: max)
    }
    if data.calculation! == StatisticsFormula.minimum.rawValue {
      let min = array.min()
      labelStatisticsCount?.text = String(describing: min)
    }
    if data.calculation! == StatisticsFormula.average.rawValue {
      let sumArray = array.reduce(0, +)
      let avgArrayValue = sumArray / Float(array.count)
      labelStatisticsCount?.text = String(describing: avgArrayValue)
    }
    if data.calculation! == StatisticsFormula.summation.rawValue {
      let sumArray = array.reduce(0, +)
      labelStatisticsCount?.text = String(describing: sumArray)
    }
  }

  // MARK: - Date Handlers
  func handleForDay(date: Date) {

    let dataList: [DBStatisticsData] = stats.statList.filter({
      $0.startDate! >= date.startOfDay && $0.startDate! <= date.endOfDay!
    })

    let array = dataList.map { $0.data }

    if array.count == 0 {
      labelStatisticsCount?.text = "NA"
    } else {
      self.calculate(array: array)
    }
  }

  func handleForWeek(startDate: Date, endDate: Date) {

    let dataList: [DBStatisticsData] = stats.statList.filter({
      $0.startDate! >= startDate && $0.startDate! <= endDate
    })

    let array = dataList.map { $0.data }

    if array.count == 0 {
      labelStatisticsCount?.text = "NA"
    } else {
      self.calculate(array: array)
    }

  }

  func handleForMonth(date: Date) {

    let dataList: [DBStatisticsData] = stats.statList.filter({
      $0.startDate! >= date.startOfMonth() && $0.startDate! <= date.endOfMonth()
    })

    let array = dataList.map { $0.data }

    if array.count == 0 {
      labelStatisticsCount?.text = "NA"
    } else {
      self.calculate(array: array)
    }

  }

  /// Calculates the stats data and Updates Stats Label.
  func calculate(array: [Float]) {

    let data = self.stats!

    if data.calculation! == StatisticsFormula.maximum.rawValue {
      let max = array.max()
      let maxValue: String! = String(format: "%.2f", max! / 60)
      labelStatisticsCount?.text = maxValue
    }
    if data.calculation! == StatisticsFormula.minimum.rawValue {
      let min = array.min()
      let minValue = String(format: "%.2f", min! / 60)
      labelStatisticsCount?.text = minValue
    }
    if data.calculation! == StatisticsFormula.average.rawValue {
      let sumArray = array.reduce(0, +)
      let avgArrayValue = sumArray / Float(array.count)
      let avgValue = String(format: "%.2f", avgArrayValue / 60)
      labelStatisticsCount?.text = avgValue
    }
    if data.calculation! == StatisticsFormula.summation.rawValue {
      let sumArray = array.reduce(0, +)
      let sumValue = String(format: "%.2f", sumArray / 60)
      labelStatisticsCount?.text = sumValue
    }
  }

}
