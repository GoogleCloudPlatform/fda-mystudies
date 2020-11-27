//
//  Date+Extensions.swift
//  HPHC
//
//  Copyright © 2020 ValidCare. All rights reserved.
//

import Foundation

extension Date {

  /// This method will update date with the timezone change offset and removing daylight savings.
  mutating func updateWithOffset() {
    if var difference = UserDefaults.standard.value(forKey: "offset") as? Int {
      difference = difference * -1
      if TimeZone.current.isDaylightSavingTime(for: self) {
        let offset = TimeZone.current.daylightSavingTimeOffset(for: self)
        self.addTimeInterval(-offset)
      }
      self.addTimeInterval(TimeInterval(difference))
    }
  }

}
