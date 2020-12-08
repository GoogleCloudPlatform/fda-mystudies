//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

extension Date {

  /// This method will update date with the timezone change offset and removing daylight savings.
  mutating func updateWithOffset() {
    if var difference = UserDefaults.standard.value(forKey: "offset") as? Int {
      difference *= -1
      if TimeZone.current.isDaylightSavingTime(for: self) {
        let offset = TimeZone.current.daylightSavingTimeOffset(for: self)
        self.addTimeInterval(-offset)
      }
      self.addTimeInterval(TimeInterval(difference))
    }
  }

}
