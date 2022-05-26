//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

extension Date {
  
  /// This method will update date with the timezone change offset and removing daylight savings.
  mutating func updateWithOffset() {
    if let differenceCapturedInUD = UserDefaults.standard.value(forKey: "offset") as? Int, differenceCapturedInUD != 0 {
      let timeZoneCurrent = TimeZone.current
      let differenceFromCurrent = timeZoneCurrent.secondsFromGMT()
      
      var playerResult: InitialTimezone?
      let ud = UserDefaults.standard
      if let decoded = ud.object(forKey: "oldTimezone") as? NSData {
        let array = NSKeyedUnarchiver.unarchiveObject(with: decoded as Data) as! InitialTimezone
        playerResult = array
      }
      
      let timeZoneOriginal = playerResult?.playerName
      let differenceFromOriginal = timeZoneOriginal?.secondsFromGMT() ?? 0
      
      let offsetCurrentDaylight = TimeZone.current.daylightSavingTimeOffset(for: Date())
      
      if !(timeZoneOriginal?.isDaylightSavingTime() ?? true) {
        var difference = differenceFromCurrent - Int(offsetCurrentDaylight) - differenceFromOriginal
        
        difference *= -1
        if TimeZone.current.isDaylightSavingTime(for: Date()) {
          let offset = TimeZone.current.daylightSavingTimeOffset(for: Date())
          self.addTimeInterval(-offset)
        }
        if timeZoneOriginal!.isDaylightSavingTime(for: Date()) {
          let offset = timeZoneOriginal!.daylightSavingTimeOffset(for: Date())
          self.addTimeInterval(+offset)
        } else {
          if Int(offsetCurrentDaylight) != 0 {
            self.addTimeInterval(+offsetCurrentDaylight)
            self.addTimeInterval(TimeInterval(-(difference + differenceCapturedInUD)))
          } else {
            if Int(difference) == 0 {
              self.addTimeInterval(TimeInterval(-differenceCapturedInUD))//main offset
            } else {
              self.addTimeInterval(TimeInterval(-(difference + differenceCapturedInUD)))
            }
          }
        }
        self.addTimeInterval(TimeInterval(difference))
      } else {
        var difference = differenceFromCurrent - Int(offsetCurrentDaylight) - differenceFromOriginal
        difference *= -1
        if TimeZone.current.isDaylightSavingTime(for: Date()) {
          let offset = TimeZone.current.daylightSavingTimeOffset(for: Date())
          self.addTimeInterval(-offset)
          self.addTimeInterval(TimeInterval(difference))
        }
        if !timeZoneOriginal!.isDaylightSavingTime(for: Date()) {
          let offset = timeZoneOriginal!.daylightSavingTimeOffset(for: Date())
          if Int(offset) != 0 {
            self.addTimeInterval(+offset)
            self.addTimeInterval(TimeInterval(difference))
          } else {
            self.addTimeInterval(-offset)
            self.addTimeInterval(TimeInterval(-difference))
          }
        } else {
          let offsetTimezoneOriginalDL = timeZoneOriginal!.daylightSavingTimeOffset(for: Date())
          if Int(offsetCurrentDaylight) != 0 {
            self.addTimeInterval(-offsetCurrentDaylight)
          }
          if differenceFromCurrent < 0, timeZoneOriginal?.isDaylightSavingTime() ?? false,
             timeZoneCurrent.isDaylightSavingTime() {
            self.addTimeInterval(-(TimeInterval(-differenceFromCurrent + differenceFromOriginal)))
            if Int(offsetTimezoneOriginalDL) != 0 {
              self.addTimeInterval(TimeInterval(Int(offsetTimezoneOriginalDL)))
              if Int(offsetCurrentDaylight) != 0 {
                self.addTimeInterval(-offsetCurrentDaylight)
              }
            }
          }
          if differenceFromOriginal < 0, timeZoneOriginal?.isDaylightSavingTime() ?? false,
             timeZoneCurrent.isDaylightSavingTime() {
            self.addTimeInterval((TimeInterval(-difference + (Int(offsetCurrentDaylight)))))
            if differenceFromOriginal < 0, Int(offsetTimezoneOriginalDL) != 0 {
                if differenceFromCurrent < 0, timeZoneOriginal?.isDaylightSavingTime() ?? false,
                   timeZoneCurrent.isDaylightSavingTime(), Int(offsetCurrentDaylight) != 0 {
                    self.addTimeInterval(-offsetCurrentDaylight)
                }
                self.addTimeInterval(TimeInterval(Int(-offsetTimezoneOriginalDL)))
            }
          }
          if differenceFromOriginal > 0, differenceFromCurrent > 0, timeZoneOriginal?.isDaylightSavingTime() ?? false,
             timeZoneCurrent.isDaylightSavingTime(), Int(offsetTimezoneOriginalDL) != 0, Int(offsetCurrentDaylight) != 0 {
                self.addTimeInterval(-offsetCurrentDaylight)
          }
          
          let val1 = [differenceFromOriginal > 0, differenceFromCurrent > 0, timeZoneOriginal?.isDaylightSavingTime() ?? false,
          !timeZoneCurrent.isDaylightSavingTime(), Int(offsetTimezoneOriginalDL) != 0]
          let val2 = [differenceFromCurrent < 0, differenceFromOriginal > 0, timeZoneOriginal?.isDaylightSavingTime() ?? false,
                      !timeZoneCurrent.isDaylightSavingTime(), Int(offsetTimezoneOriginalDL) > 0]
          let val3 = [differenceFromOriginal < 0, differenceFromCurrent > 0, timeZoneOriginal?.isDaylightSavingTime() ?? false,
          !timeZoneCurrent.isDaylightSavingTime(), Int(offsetCurrentDaylight) == 0, Int(offsetTimezoneOriginalDL) > 0]
          
          if (val1.allSatisfy({$0}) || val2.allSatisfy({$0}) || val3.allSatisfy({$0})) {
            self.addTimeInterval(-offsetTimezoneOriginalDL)
          }
          self.addTimeInterval(TimeInterval(difference))
        }
      }
    }
  }
  
  func convertToTimeZone(timeDifference: Int, date2: Date) -> Date {
       let val1 = timeDifference
    let val2 =  date2.addingTimeInterval(TimeInterval(val1))
    return val2
  }
}

extension Date {
  
  // Convert local time to UTC (or GMT)
  func toGlobalTime() -> Date {
    let timezone = TimeZone.current
    let seconds = -TimeInterval(timezone.secondsFromGMT(for: self))
    return Date(timeInterval: seconds, since: self)
  }
  
  // Convert UTC (or GMT) to local time
  func toLocalTime() -> Date {
    let timezone = TimeZone.current
    let seconds = TimeInterval(timezone.secondsFromGMT(for: self))
    return Date(timeInterval: seconds, since: self)
  }
  
}

class InitialTimezone: NSObject, NSCoding {
  private var name: TimeZone!
  var playerName: TimeZone {
    get {
      return name
    }
    set {
      name = newValue
    }
  }
  
  init(playerName: TimeZone) {
    name = playerName
  }
  
  required convenience init(coder aDecoder: NSCoder) {
    let name = aDecoder.decodeObject(forKey: "name") as! TimeZone
    self.init(playerName: name)
  }
  
  func encode(with aCoder: NSCoder){
    aCoder.encode(name, forKey: "name")
  }
  
}
