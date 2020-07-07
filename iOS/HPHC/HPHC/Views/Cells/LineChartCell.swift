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

class GraphChartTableViewCell: UITableViewCell {
  @IBOutlet weak var graphView: ORKGraphChartView!
}

class LineChartCell: GraphChartTableViewCell {

  // MARK: - Outlets
  @IBOutlet weak var labelTitle: UILabel!

  @IBOutlet weak var labelAxisValue: UILabel!
  @IBOutlet weak var buttonForward: UIButton!
  @IBOutlet weak var buttonBackward: UIButton!

  // MARK: - Properties
  var currentChart: DashboardCharts! {
    didSet {
      self.graphView.dataSource = self
      self.graphView.reloadData()
    }
  }

  lazy var frequencyPageIndex = 0
  lazy var frequencyPageSize = 5
  lazy var pageNumber = 0
  lazy var hourOfDayDate = Date()

  var startDateOfWeek: Date?
  var endDateOfWeek: Date?

  var charActivity: Activity?

  lazy var plotPoints: [[ORKValueRange]] = []
  lazy var xAxisTitles: Array! = []

  var max: Float = 0.0
  var min: Float = 0.0

  /// CleanUp the reused `GraphChartTableViewCell`.
  override func prepareForReuse() {
    super.prepareForReuse()
    self.labelTitle.text = ""
    self.labelAxisValue.text = ""
    self.buttonForward.isHidden = true
    self.buttonBackward.isHidden = true
    max = 0.0
    min = 0.0
    plotPoints = []
    xAxisTitles = []
    self.graphView.dataSource = nil
    self.graphView.reloadData()
  }

  func getWeeklyAttributedText() -> NSAttributedString {

    let stringStartDate = LineChartCell.formatter.string(from: startDateOfWeek!) + " - "
    let stringEndDate = LineChartCell.formatter.string(from: endDateOfWeek!)

    let color = Utilities.getUIColorFromHex(0x007CBA)

    let attributedStartDate: NSMutableAttributedString = NSMutableAttributedString(
      string: stringStartDate
    )
    attributedStartDate.addAttribute(
      NSAttributedString.Key.foregroundColor,
      value: color,
      range: NSRange(location: 0, length: 2)
    )

    let attributedEndDate: NSMutableAttributedString = NSMutableAttributedString(
      string: stringEndDate
    )
    attributedEndDate.addAttribute(
      NSAttributedString.Key.foregroundColor,
      value: color,
      range: NSRange(location: 0, length: 2)
    )

    attributedStartDate.append(attributedEndDate)

    return attributedStartDate
  }

  func getSchedulesAttributedText(stringStartDate: String, stringEndDate: String)
    -> NSAttributedString
  {

    let stringStartDate2 = stringStartDate + " - "

    let color = Utilities.getUIColorFromHex(0x007CBA)

    let attributedStartDate: NSMutableAttributedString = NSMutableAttributedString(
      string: stringStartDate2
    )
    attributedStartDate.addAttribute(
      NSAttributedString.Key.foregroundColor,
      value: color,
      range: NSRange(location: 0, length: 2)
    )

    let attributedEndDate: NSMutableAttributedString = NSMutableAttributedString(
      string: stringEndDate
    )
    attributedEndDate.addAttribute(
      NSAttributedString.Key.foregroundColor,
      value: color,
      range: NSRange(location: 0, length: 2)
    )

    attributedStartDate.append(attributedEndDate)

    return attributedStartDate
  }

  func setupLineChart(chart: DashboardCharts) {

    currentChart = chart
    self.graphView.tintColor = UIColor.gray
    labelTitle.text = chart.displayName

    guard !chart.statList.isEmpty else { return }

    let array = chart.statList.compactMap { $0.data }
    if array.count != 0 {
      max = array.max()!
      min = array.min()!
    }

    var _: [ORKValueRange] = []

    let activity = Study.currentStudy?.activities.filter { $0.actvityId == chart.activityId }
      .last

    charActivity = activity

    if charActivity == nil || chart.dataSourceTimeRange == nil {
      self.buttonForward.isEnabled = false
      self.buttonBackward.isEnabled = false
      return
    }

    let timeRange = chart.dataSourceTimeRange!
    let chartTimeRange = ChartTimeRange(rawValue: timeRange)!

    if chart.scrollable {
      self.buttonForward.isHidden = false
      self.buttonBackward.isHidden = false
    } else {
      self.buttonForward.isHidden = true
      self.buttonBackward.isHidden = true
    }

    switch chartTimeRange {

    case .daysOfMonth:
      // current date
      let stringDate = LineChartCell.monthFormatter.string(from: Date())
      labelAxisValue.text = stringDate
      self.buttonForward.isEnabled = false
      self.handleDaysOfMonthForDate(date: Date())

    case .daysOfWeek:
      startDateOfWeek = Date().startOfWeek
      endDateOfWeek = Date().endOfWeek

      let attributedText = self.getWeeklyAttributedText()
      labelAxisValue.attributedText = attributedText

      self.buttonForward.isEnabled = false

      // Check for back button to disable
      let result = hourOfDayDate.compare((self.charActivity?.startDate)!)
      if result == .orderedSame || result == .orderedAscending {
        self.buttonBackward.isEnabled = false
      }
      xAxisTitles = Calendar.current.veryShortWeekdaySymbols

      self.handleDaysOfWeekForStartDate(startDate: startDateOfWeek!, endDate: endDateOfWeek!)

    case .monthsOfYear:
      // Current year
      let stringDate = LineChartCell.yearFormatter.string(from: Date())
      labelAxisValue.text = stringDate
      self.buttonForward.isEnabled = false

      // Check for back button to disable
      let result = hourOfDayDate.compare((self.charActivity?.startDate)!)
      if result == .orderedSame || result == .orderedAscending {
        self.buttonBackward.isEnabled = false
      }
      xAxisTitles = Calendar.current.shortMonthSymbols
      for i in 0...11 {
        if i == 0 || i == 3 || i == 6 || i == 9 || i == 11 {

        } else {
          xAxisTitles[i] = ""
        }
      }
      self.handleMonthsOfYearForDate(date: Date())

    case .weeksOfMonth:
      // Current date
      let stringDate = LineChartCell.monthFormatter.string(from: Date())
      labelAxisValue.text = stringDate
      self.handleWeeksOfMonthForDate(date: Date())

    case .runs:
      labelTitle.text = chart.displayName! + " (per run)"
      self.buttonForward.isEnabled = true
      plotForRunsType()

    case .hoursOfDay:
      labelTitle.text = chart.displayName! + " (per run)"
      // current date
      let stringDate = LineChartCell.formatter.string(from: Date())
      labelAxisValue.text = stringDate
      self.buttonForward.isEnabled = false
      self.handleHoursOfDayForDate(date: Date())

    }
  }

  // MARK: - Actions
  @IBAction func buttonForwardAction(_ sender: UIButton) {

    let timeRange = currentChart.dataSourceTimeRange!
    let chartTimeRange = ChartTimeRange(rawValue: timeRange)!
    let calendar = Calendar.current

    switch chartTimeRange {
    case .daysOfMonth:
      self.buttonForward.isEnabled = true
      self.buttonBackward.isEnabled = true
      hourOfDayDate = calendar.date(byAdding: .month, value: 1, to: hourOfDayDate)!
      let stringDate = LineChartCell.monthFormatter.string(from: hourOfDayDate)
      labelAxisValue.text = stringDate

      self.handleDaysOfMonthForDate(date: hourOfDayDate)

      let result = hourOfDayDate.compare(Date())
      if result == .orderedSame || result == .orderedDescending {
        self.buttonForward.isEnabled = false
      }

    case .daysOfWeek:
      self.buttonForward.isEnabled = true
      self.buttonBackward.isEnabled = true

      startDateOfWeek = calendar.date(byAdding: .day, value: 7, to: startDateOfWeek!)
      endDateOfWeek = calendar.date(byAdding: .day, value: 7, to: endDateOfWeek!)

      let attributedText = self.getWeeklyAttributedText()

      labelAxisValue.attributedText = attributedText

      self.handleDaysOfWeekForStartDate(startDate: startDateOfWeek!, endDate: endDateOfWeek!)

      let result = endDateOfWeek?.compare(Date())
      if result == .orderedSame || result == .orderedDescending {
        self.buttonForward.isEnabled = false
      }

    case .monthsOfYear:
      self.buttonForward.isEnabled = true
      self.buttonBackward.isEnabled = true
      hourOfDayDate = calendar.date(byAdding: .year, value: 1, to: hourOfDayDate)!
      let stringDate = LineChartCell.yearFormatter.string(from: hourOfDayDate)
      labelAxisValue.text = stringDate

      self.handleMonthsOfYearForDate(date: hourOfDayDate)

      let result = hourOfDayDate.compare(Date())
      if result == .orderedSame || result == .orderedDescending {
        self.buttonForward.isEnabled = false
      }

    case .weeksOfMonth:
      self.buttonForward.isEnabled = true
      self.buttonBackward.isEnabled = true
      hourOfDayDate = calendar.date(byAdding: .month, value: 1, to: hourOfDayDate)!
      let stringDate = LineChartCell.monthFormatter.string(from: hourOfDayDate)
      labelAxisValue.text = stringDate
      self.handleWeeksOfMonthForDate(date: hourOfDayDate)

    case .runs:
      self.buttonForward.isEnabled = true
      self.buttonBackward.isEnabled = true
      pageNumber += 1
      if !plotForRunsType() {
        pageNumber -= 1
      }

    case .hoursOfDay:
      self.buttonForward.isEnabled = true
      self.buttonBackward.isEnabled = true
      hourOfDayDate = calendar.date(byAdding: .day, value: 1, to: hourOfDayDate)!
      let stringDate = LineChartCell.formatter.string(from: hourOfDayDate)
      labelAxisValue.text = stringDate

      self.handleHoursOfDayForDate(date: hourOfDayDate)

      let result = hourOfDayDate.compare(Date())
      if result == .orderedSame || result == .orderedDescending {
        self.buttonForward.isEnabled = false
      }

    }
  }

  @IBAction func buttonBackwardAction(_ sender: UIButton) {

    let timeRange = currentChart.dataSourceTimeRange!
    let chartTimeRange = ChartTimeRange(rawValue: timeRange)!

    switch chartTimeRange {
    case .daysOfMonth:

      self.buttonBackward.isEnabled = true
      self.buttonForward.isEnabled = true
      let calendar = Calendar.current

      hourOfDayDate = calendar.date(byAdding: .month, value: -1, to: hourOfDayDate)!
      let stringDate = LineChartCell.monthFormatter.string(from: hourOfDayDate)
      labelAxisValue.text = stringDate

      self.handleDaysOfMonthForDate(date: hourOfDayDate)

      let result = hourOfDayDate.compare((self.charActivity?.startDate)!)
      if result == .orderedSame || result == .orderedAscending {
        self.buttonBackward.isEnabled = false
      }

    case .daysOfWeek:
      let calendar = Calendar.current
      self.buttonBackward.isEnabled = true
      self.buttonForward.isEnabled = true

      startDateOfWeek = calendar.date(byAdding: .day, value: -7, to: startDateOfWeek!)
      endDateOfWeek = calendar.date(byAdding: .day, value: -7, to: endDateOfWeek!)

      let attributedText = self.getWeeklyAttributedText()
      labelAxisValue.attributedText = attributedText

      self.handleDaysOfWeekForStartDate(startDate: startDateOfWeek!, endDate: endDateOfWeek!)

      let result = startDateOfWeek?.compare((self.charActivity?.startDate)!)
      if result == .orderedSame || result == .orderedAscending {
        self.buttonBackward.isEnabled = false
      }

    case .monthsOfYear:
      self.buttonBackward.isEnabled = true
      self.buttonForward.isEnabled = true
      let calendar = Calendar.current

      hourOfDayDate = calendar.date(byAdding: .year, value: -1, to: hourOfDayDate)!
      let stringDate = LineChartCell.yearFormatter.string(from: hourOfDayDate)
      labelAxisValue.text = stringDate

      self.handleMonthsOfYearForDate(date: hourOfDayDate)

      let result = hourOfDayDate.compare((self.charActivity?.startDate)!)
      if result == .orderedSame || result == .orderedAscending {
        self.buttonBackward.isEnabled = false
      }

    case .weeksOfMonth:
      self.buttonBackward.isEnabled = true
      self.buttonForward.isEnabled = true
      let calendar = Calendar.current

      hourOfDayDate = calendar.date(byAdding: .month, value: -1, to: hourOfDayDate)!
      let stringDate = LineChartCell.monthFormatter.string(from: hourOfDayDate)
      labelAxisValue.text = stringDate

      self.handleWeeksOfMonthForDate(date: hourOfDayDate)

    case .runs:
      self.buttonForward.isEnabled = true
      self.buttonBackward.isEnabled = true
      pageNumber -= 1
      if !plotForRunsType() {
        pageNumber += 1  // Last page.
      }

    case .hoursOfDay:
      self.buttonBackward.isEnabled = true
      self.buttonForward.isEnabled = true
      let calendar = Calendar.current

      hourOfDayDate = calendar.date(byAdding: .day, value: -1, to: hourOfDayDate)!
      let stringDate = LineChartCell.formatter.string(from: hourOfDayDate)
      labelAxisValue.text = stringDate

      self.handleHoursOfDayForDate(date: hourOfDayDate)

      let result = hourOfDayDate.compare((self.charActivity?.startDate)!)
      if result == .orderedSame || result == .orderedAscending {
        self.buttonBackward.isEnabled = false
      }

    }
  }

  func getNextSetOfFrequencyRuns() -> [[String: Any]] {

    var frequencyRunsSet: [[String: Any]] = []
    for index in frequencyPageIndex...((frequencyPageIndex + frequencyPageSize) - 1) {
      if index < ((charActivity?.frequencyRuns?.count)!) && index >= 0 {
        let run = charActivity?.frequencyRuns?[index]
        frequencyRunsSet.append(run!)
      }

    }
    return frequencyRunsSet
  }

  @discardableResult
  private func plotForRunsType() -> Bool {

    frequencyPageIndex = frequencyPageSize * pageNumber
    let frequencySet = self.getNextSetOfFrequencyRuns()

    if frequencySet.isEmpty {
      self.graphView.reloadData()
      return false
    } else {
      let sTime = frequencySet.first?["startTime"] as? String ?? ""
      let eTime = frequencySet.last?["endTime"] as? String ?? ""

      guard
        let startDate = Utilities.getDateFromStringWithOutTimezone(dateString: sTime)
          ?? DateHelper.formattedRunDateFromString(date: sTime),
        let endDate = Utilities.getDateFromStringWithOutTimezone(dateString: eTime)
          ?? DateHelper.formattedRunDateFromString(date: eTime)
      else { return false }

      let stringStartDate = LineChartCell.formatter.string(from: startDate)
      let stringEndDate = LineChartCell.formatter.string(from: endDate)
      let attributedText = self.getSchedulesAttributedText(
        stringStartDate: stringStartDate,
        stringEndDate: stringEndDate
      )

      labelAxisValue.attributedText = attributedText

      self.handleRunsForDate(startDate: startDate, endDate: endDate, runs: frequencySet)
      return true
    }
  }
  // MARK: - Data Calculation

  func handleDaysOfMonthForDate(date: Date) {

    let dataList: [DBStatisticsData] = currentChart.statList.filter({
      $0.startDate! >= date.startOfMonth() && $0.startDate! <= date.endOfMonth()
    })

    var points: [ORKValueRange] = []
    xAxisTitles = []
    plotPoints = []

    let calendar = Calendar.current
    let range = calendar.range(of: .day, in: .month, for: date)!
    let numDays = range.count

    for i in 1...numDays {

      if i == 1 || i == 5 || i == 10 || i == 15 || i == 20 || i == 25 || i == numDays {
        xAxisTitles.append(String(i))
      } else {
        xAxisTitles.append("")
      }

      points.append(ORKValueRange())
    }

    if dataList.count > 0 {
      for i in 0...dataList.count - 1 {

        let data = dataList[i]
        let responseDate = data.startDate
        let fk = data.fkDuration
        let day = LineChartCell.shortDateFormatter.string(from: responseDate!)
        let value = data.data

        points[Int(day)! - 1] = ORKValueRange(value: Double(value))
        self.replaceXTitleForActiveTask(value: fk, atIndex: (Int(day)! - 1))

      }
    }

    plotPoints.append(points)

    self.graphView.reloadData()

  }

  func handleDaysOfWeekForStartDate(startDate: Date, endDate: Date) {

    let dataList: [DBStatisticsData] = currentChart.statList.filter({
      $0.startDate! >= startDate && $0.startDate! <= endDate
    })

    var points: [ORKValueRange] = []

    plotPoints = []

    for _ in 1...xAxisTitles.count {
      points.append(ORKValueRange())
    }

    if dataList.count > 0 {
      for i in 0...dataList.count - 1 {

        let data = dataList[i]
        let responseDate = data.startDate
        let fk = data.fkDuration
        let day = LineChartCell.shortDayFormatter.string(from: responseDate!)
        let value = data.data
        let currentDay = DayValue(rawValue: day)!

        points[currentDay.dayIndex - 1] = ORKValueRange(value: Double(value))
        self.replaceXTitleForActiveTask(value: fk, atIndex: (currentDay.dayIndex - 1))

      }
    }

    plotPoints.append(points)
    self.graphView.reloadData()
  }

  func handleMonthsOfYearForDate(date: Date) {

    let dataList: [DBStatisticsData] = currentChart.statList.filter({
      $0.startDate! >= date.startOfYear() && $0.startDate! <= date.endOfYear()
    })

    var points: [ORKValueRange] = []

    plotPoints = []

    for _ in 1...xAxisTitles.count {
      points.append(ORKValueRange())
    }

    if dataList.count > 0 {
      for i in 0...dataList.count - 1 {

        let data = dataList[i]
        let responseDate = data.startDate
        let fk = data.fkDuration
        let month = LineChartCell.shortMonthFormatter.string(from: responseDate!)
        let value = data.data
        points[Int(month)! - 1] = ORKValueRange(value: Double(value))
        self.replaceXTitleForActiveTask(value: fk, atIndex: (Int(month)! - 1))

      }
    }

    plotPoints.append(points)
    self.graphView.reloadData()
  }

  func handleWeeksOfMonthForDate(date: Date) {

    let dataList: [DBStatisticsData] = currentChart.statList.filter({
      $0.startDate! >= date.startOfMonth() && $0.startDate! <= date.endOfMonth()
    })

    var points: [ORKValueRange] = []
    xAxisTitles = []
    plotPoints = []

    let calendar = Calendar.current
    let range = calendar.range(of: .weekOfMonth, in: .month, for: date)!
    let numWeeks = range.count

    for i in 1...numWeeks {

      points.append(ORKValueRange())
      xAxisTitles.append("W" + String(i))
    }

    if dataList.count > 0 {
      for i in 0...dataList.count - 1 {

        let data = dataList[i]
        let fk = data.fkDuration
        let responseDate = data.startDate
        let week = self.getWeekNumber(date: responseDate!)
        let value = data.data
        points[week - 1] = ORKValueRange(value: Double(value))
        self.replaceXTitleForActiveTask(value: fk, atIndex: (week - 1))

      }
    }

    plotPoints.append(points)

    self.graphView.reloadData()
  }

  func handleHoursOfDayForDate(date: Date) {

    let dataList: [DBStatisticsData] = currentChart.statList.filter({
      $0.startDate! >= date.startOfDay && $0.startDate! <= date.endOfDay!
    })

    var points: [ORKValueRange] = []
    xAxisTitles = []
    plotPoints = []

    if (charActivity?.frequencyRuns?.count)! > 0 {

      for i in 0...(charActivity?.frequencyRuns?.count)! - 1 {

        xAxisTitles.append(String(i + 1))
        points.append(ORKValueRange())

        let frequency = charActivity?.frequencyRuns?[i]
        let runStartTime = LineChartCell.dailyFormatter.date(
          from: (frequency?["startTime"] as? String)!
        )
        let endTime = LineChartCell.dailyFormatter.date(
          from: (frequency?["endTime"] as? String)!
        )

        if dataList.count > 0 {
          for j in 0...dataList.count - 1 {
            let data = dataList[j]
            let value = data.data
            let fk = data.fkDuration

            let dateAsString = LineChartCell.dailyFormatter.string(
              from: data.startDate!
            )
            let responseDate = LineChartCell.dailyFormatter2.date(from: dateAsString)

            if responseDate! > runStartTime! && responseDate! < endTime! {
              points[i] = ORKValueRange(value: Double(value))
              self.replaceXTitleForActiveTask(value: fk, atIndex: i)

            }
          }
        }

      }

    }

    plotPoints.append(points)
    self.graphView.reloadData()
  }

  func handleRunsForDate(startDate: Date, endDate: Date, runs: [[String: Any]]) {

    var dataList: [DBStatisticsData] = currentChart.statList.filter({
      $0.startDate! >= startDate && $0.startDate! <= endDate
    })

    if !currentChart.scrollable, dataList.count > frequencyPageSize {
      // If the chart is not scrollable, only show the latest responses based on the page size.
      let frequencyPageSizeData = dataList.suffix(from: dataList.count - frequencyPageSize)
      dataList = Array(frequencyPageSizeData)
    }

    let array = dataList.map { $0.data }
    var points: [ORKValueRange] = []
    xAxisTitles = []
    plotPoints = []

    if (runs.count) > 0 {

      for i in 0...(runs.count - 1) {

        // x axis title
        xAxisTitles.append(String(i + 1))

        if array.count > i {
          let data = dataList[i]
          let value = data.data
          let fk = data.fkDuration
          points.append(ORKValueRange(value: Double(value)))
          self.replaceXTitleForActiveTask(value: fk, atIndex: i)
        } else {
          points.append(ORKValueRange())
        }

      }

    }
    plotPoints.append(points)
    self.graphView.reloadData()
  }

  func replaceXTitleForActiveTask(value: Int, atIndex: Int) {

    if charActivity?.type == .activeTask {
      var title = (xAxisTitles[atIndex] as? String)!
      title += "\n" + "\(value)"
      xAxisTitles[atIndex] = title
    }

  }

  // MARK: - FORMATERS
  private static let formatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "dd, MMM YYYY"
    formatter.timeZone = TimeZone.init(abbreviation: "GMT")
    return formatter
  }()

  private static let yearFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "YYYY"
    formatter.timeZone = TimeZone.init(abbreviation: "GMT")
    return formatter
  }()

  private static let monthFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "MMM YYYY"
    formatter.timeZone = TimeZone.init(abbreviation: "GMT")
    return formatter
  }()

  public static let shortMonthFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "M"
    formatter.timeZone = TimeZone.init(abbreviation: "GMT")
    return formatter
  }()

  public static let shortDateFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "dd"
    formatter.timeZone = TimeZone.init(abbreviation: "GMT")
    return formatter
  }()

  public static let shortDayFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "E"
    formatter.timeZone = TimeZone.init(abbreviation: "GMT")
    return formatter
  }()

  func getWeekNumber(date: Date) -> Int {
    let calender = Calendar.current
    let week = calender.component(.weekOfMonth, from: date)
    return week
  }

  private static let oneTimeFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma, MMM dd YYYY"
    formatter.timeZone = TimeZone.init(abbreviation: "GMT")
    return formatter
  }()

  private static let dailyFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "HH:mm:ss"
    return formatter
  }()

  private static let dailyFormatter2: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "HH:mm:ss"
    return formatter
  }()

}
extension LineChartCell: ORKValueRangeGraphChartViewDataSource {

  func numberOfPlots(in graphChartView: ORKGraphChartView) -> Int {
    return plotPoints.count
  }

  func graphChartView(
    _ graphChartView: ORKGraphChartView,
    dataPointForPointIndex pointIndex: Int,
    plotIndex: Int
  ) -> ORKValueRange {
    return plotPoints[plotIndex][pointIndex]
  }

  func graphChartView(
    _ graphChartView: ORKGraphChartView,
    numberOfDataPointsForPlotIndex plotIndex: Int
  ) -> Int {
    return plotPoints[plotIndex].count
  }

  func maximumValue(for graphChartView: ORKGraphChartView) -> Double {
    return Double(max)
  }

  func minimumValue(for graphChartView: ORKGraphChartView) -> Double {
    return Double(min)
  }

  func graphChartView(
    _ graphChartView: ORKGraphChartView,
    titleForXAxisAtPointIndex pointIndex: Int
  ) -> String? {
    let point = xAxisTitles[pointIndex] as? String
    return point!
  }

  func graphChartView(
    _ graphChartView: ORKGraphChartView,
    drawsPointIndicatorsForPlotIndex plotIndex: Int
  ) -> Bool {
    if plotIndex == 1 {
      return false
    }
    return true
  }
}

extension Date {
  enum Gregorian {
    static let calendar = Calendar.current  //Calendar(identifier: .curr)
  }

  var startOfWeek: Date? {
    return Gregorian.calendar.date(
      from: Gregorian.calendar.dateComponents([.yearForWeekOfYear, .weekOfYear], from: self)
    )
  }

  var endOfWeek: Date? {
    return Gregorian.calendar.date(byAdding: .second, value: (7 * 86400) - 1, to: startOfWeek!)
  }

  func startOfMonth() -> Date {
    return Calendar.current.date(
      from: Calendar.current.dateComponents(
        [.year, .month],
        from: Calendar.current.startOfDay(for: self)
      )
    )!
  }

  func endOfMonth() -> Date {
    return Calendar.current.date(
      byAdding: DateComponents(month: 1, day: -1),
      to: self.startOfMonth()
    )!
  }

  func startOfYear() -> Date {
    return Calendar.current.date(
      from: Calendar.current.dateComponents(
        [.year],
        from: Calendar.current.startOfDay(for: self)
      )
    )!
  }

  func endOfYear() -> Date {
    return Calendar.current.date(
      byAdding: DateComponents(year: 1, second: -1),
      to: self.startOfYear()
    )!
  }

  var startOfDay: Date {
    return Calendar.current.startOfDay(for: self)
  }

  var endOfDay: Date? {
    var components = DateComponents()
    components.day = 1
    components.second = -1
    return Calendar.current.date(byAdding: components, to: startOfDay)
  }

  /// Returns the amount of years from another date
  func years(from date: Date) -> Int {
    return Calendar.current.dateComponents([.year], from: date, to: self).year ?? 0
  }

  /// Returns the amount of months from another date
  func months(from date: Date) -> Int {
    return Calendar.current.dateComponents([.month], from: date, to: self).month ?? 0
  }

  /// Returns the amount of weeks from another date
  func weeks(from date: Date) -> Int {
    return Calendar.current.dateComponents([.weekOfMonth], from: date, to: self).weekOfMonth
      ?? 0
  }

  /// Returns the amount of days from another date
  func days(from date: Date) -> Int {
    return Calendar.current.dateComponents([.day], from: date, to: self).day ?? 0
  }

  /// Returns the amount of hours from another date
  func hours(from date: Date) -> Int {
    return Calendar.current.dateComponents([.hour], from: date, to: self).hour ?? 0
  }

  /// Returns the amount of minutes from another date
  func minutes(from date: Date) -> Int {
    return Calendar.current.dateComponents([.minute], from: date, to: self).minute ?? 0
  }

  /// Returns the amount of seconds from another date
  func seconds(from date: Date) -> Int {
    return Calendar.current.dateComponents([.second], from: date, to: self).second ?? 0
  }

  /// Returns the a custom time interval description from another date
  func offset(from date: Date) -> String {
    if years(from: date) > 0 { return "\(years(from: date))y" }
    if months(from: date) > 0 { return "\(months(from: date))M" }
    if weeks(from: date) > 0 { return "\(weeks(from: date))w" }
    if days(from: date) > 0 { return "\(days(from: date))d" }
    if hours(from: date) > 0 { return "\(hours(from: date))h" }
    if minutes(from: date) > 0 { return "\(minutes(from: date))m" }
    if seconds(from: date) > 0 { return "\(seconds(from: date))s" }
    return ""
  }
}
