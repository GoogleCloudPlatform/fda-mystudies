//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

enum DateHelper {

  static private let runDateFormat = "yyyy-MM-dd HH:mm:ss Z"
  static private let fullDayHourFormat = "HH:mm:ss"  // 15:25:25

  static var iso8601DateFormatter: DateFormatter {
    let dateFormatter = DateFormatter()
    let locale = Locale(identifier: "en_US_POSIX")
    dateFormatter.timeZone = TimeZone.current
    dateFormatter.locale = locale
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    return dateFormatter
  }

  static func dateFromString(date: String, format: String) -> Date? {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = format
    dateFormatter.timeZone = TimeZone.current
    dateFormatter.locale = Locale(identifier: "en_US_POSIX")
    return dateFormatter.date(from: date)
  }

  static func formattedRunDateFromString(date: String) -> Date? {
    return dateFromString(date: date, format: runDateFormat)
  }

  /// Adjusts the `Date` instance time.
  /// - Parameters:
  ///   - firstDate: Date for which time needs to be updated
  ///   - timeString: Time in "00:00:00" format.`
  /// - Returns: Updated `Date` instance.
  static func updateTime(
    of firstDate: Date?,
    with timeString: String = "00:00:00"
  ) -> Date? {
    let calendar = Calendar.current

    guard let timeStringDate = dateFromString(date: timeString, format: fullDayHourFormat),
      let firstDate = firstDate
    else {
      return nil
    }

    let firstComp = calendar.dateComponents(
      [Calendar.Component.year, Calendar.Component.month, Calendar.Component.day],
      from: firstDate
    )

    let secondComp = calendar.dateComponents(
      [Calendar.Component.hour, Calendar.Component.minute, Calendar.Component.second],
      from: timeStringDate
    )

    guard let hour = secondComp.hour, let minute = secondComp.minute,
      let sec = secondComp.second, let year = firstComp.year, let month = firstComp.month,
      let day = firstComp.day
    else {
      return nil
    }
    return createDate(year: year, month: month, day: day, hour: hour, min: minute, sec: sec)
  }

  static func createDate(
    year: Int,
    month: Int,
    day: Int,
    hour: Int = 0,
    min: Int = 0,
    sec: Int = 0
  ) -> Date? {
    var c = DateComponents()
    c.year = year
    c.month = month
    c.day = day
    c.hour = hour
    c.minute = min
    c.second = sec
    c.timeZone = TimeZone.current

    return Calendar.current.date(from: c)
  }
}
