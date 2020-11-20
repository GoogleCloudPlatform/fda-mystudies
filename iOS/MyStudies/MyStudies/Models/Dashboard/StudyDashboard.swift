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

enum StatisticsFormula: String {

  case summation = "Summation"
  case average = "Average"
  case maximum = "Maximum"
  case minimum = "Minimum"

}

enum ChartTimeRange: String {

  /// s,m,t..s   f = daily
  case daysOfWeek = "days_of_week"

  /// 1,2,3,4..31   f = daily
  case daysOfMonth = "days_of_month"

  /// w1,w2,w3,w4.. w5   f = weekly
  case weeksOfMonth = "weeks_of_month"

  /// j,f,m..d  f = monthly
  case monthsOfYear = "months_of_year"

  /// f = sheduled
  case runs = "runs"

  /// f = withInADay
  case hoursOfDay = "hours_of_day"

}

class StudyDashboard {

  lazy var statistics: [DashboardStatistics]! = []
  lazy var charts: [DashboardCharts]! = []
  lazy var dashboardResponse: [DashboardResponse] = []

  static var instance = StudyDashboard()
  private init() {}

  func saveDashboardResponse(responseList: [DashboardResponse]) {
    self.dashboardResponse.append(contentsOf: responseList)
  }
}

class DashboardResponse {

  var key: String?  //Stats key
  var activityId: String?

  var type: String?
  lazy var values: [[String: Any]] = []
  var date: String?
  var isPHI: String?

  init(with activityID: String, and key: String) {
    self.key = key
    self.activityId = activityID
    self.isPHI = "true"
    self.type = "int"
  }

  func appendValues(from dict: JSONDictionary, of responseDate: String) {
    if let value = dict["value"] as? Double {
      let valueDetail =
        [
          "value": value,
          "count": Float(0.0),
          "date": responseDate,
        ] as [String: Any]
      self.values.append(valueDetail)
    }
  }
}

class DashboardStatistics {

  var statisticsId: String?
  var studyId: String?
  var title: String?
  var displayName: String?
  var unit: String?
  var calculation: String?
  var statType: String?
  var activityId: String?
  var activityVersion: String?
  var dataSourceType: String?
  var dataSourceKey: String?
  var statList = List<DBStatisticsData>()

  init() {}

  /// To Initialize the properties
  ///
  /// - Parameters:
  ///   - detail: Dictionary of properties value
  init(detail: JSONDictionary) {

    self.title = detail["title"] as? String ?? ""
    self.displayName = detail["displayName"] as? String ?? ""
    self.statType = detail["statType"] as? String ?? ""
    self.unit = detail["unit"] as? String ?? ""
    self.calculation = detail["calculation"] as? String

    let datasource = detail["dataSource"] as? [String: Any] ?? [:]
    self.dataSourceType = datasource["type"] as? String ?? ""
    self.dataSourceKey = datasource["key"] as? String ?? ""

    let activity = datasource["activity"] as? [String: Any] ?? [:]
    self.activityId = activity[kActivityId] as? String
    self.activityVersion = activity["version"] as? String

    self.studyId = Study.currentStudy?.studyId ?? ""
    self.statisticsId = self.studyId! + self.title!

  }

  init(dbStatistics: DBStatistics) {
    self.activityId = dbStatistics.activityId
    self.activityVersion = dbStatistics.activityVersion
    self.calculation = dbStatistics.calculation
    self.dataSourceKey = dbStatistics.dataSourceKey
    self.dataSourceType = dbStatistics.dataSourceType
    self.displayName = dbStatistics.displayName
    self.title = dbStatistics.title
    self.statType = dbStatistics.statType
    self.studyId = dbStatistics.studyId
    self.unit = dbStatistics.unit
    self.statList = dbStatistics.statisticsData
  }

}

class DashboardCharts {

  /// Basic
  var chartId: String?

  var studyId: String?
  var title: String?
  var displayName: String?
  var chartType: String?
  lazy var scrollable: Bool = true

  /// Datasource
  var activityId: String?

  var activityVersion: String?
  var dataSourceType: String?
  var dataSourceKey: String?
  var dataSourceTimeRange: String?
  var startTime: Date?
  var endTime: Date?

  /// Settings
  var barColor: String?

  lazy var numberOfPoints: Int = 0
  var chartSubType: String?

  lazy var statList = List<DBStatisticsData>()

  init() {}

  /// To Initialize the properties
  ///
  /// - Parameters:
  ///   - detail: Dictionary of properties value
  init(detail: JSONDictionary) {

    self.title = detail["title"] as? String ?? ""
    self.displayName = detail["displayName"] as? String ?? ""
    self.chartType = detail["type"] as? String ?? ""
    self.scrollable = detail["scrollable"] as? Bool ?? true

    //datasource
    let datasource = detail["dataSource"] as? [String: Any] ?? [:]
    self.dataSourceType = datasource["type"] as? String ?? ""
    self.dataSourceKey = datasource["key"] as? String ?? ""
    self.dataSourceTimeRange = datasource["timeRangeType"] as? String ?? ""

    // activity detail
    let activity = datasource["activity"] as? [String: Any] ?? [:]
    self.activityId = activity[kActivityId] as? String ?? ""
    self.activityVersion = activity["version"] as? String ?? ""

    //configuration
    let configuration = detail["configuration"] as? [String: Any] ?? [:]
    self.chartSubType = configuration["subType"] as? String ?? ""
    self.studyId = Study.currentStudy?.studyId

    self.chartId =
      self.studyId! + (self.activityId == nil ? "" : self.activityId!) + self
      .dataSourceKey!

  }

  init(dbChart: DBCharts) {
    self.activityId = dbChart.activityId
    self.activityVersion = dbChart.activityVersion
    self.chartType = dbChart.chartType
    self.chartSubType = dbChart.chartSubType
    self.dataSourceTimeRange = dbChart.dataSourceTimeRange
    self.dataSourceKey = dbChart.dataSourceKey
    self.dataSourceType = dbChart.dataSourceType
    self.displayName = dbChart.displayName
    self.title = dbChart.title
    self.scrollable = dbChart.scrollable
    self.studyId = dbChart.studyId
    self.statList = dbChart.statisticsData
  }
}
