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

import Foundation
import UIKit

let maxWidth = 1000
let maxHeight = 100

enum DirectoryType: String {
  case study = "Study"
  case gateway = "Gateway"
}

struct ScreenSize {

  static let screenWidth = UIScreen.main.bounds.size.width
  static let screenHeight = UIScreen.main.bounds.size.height
  static let screenMaxLength = max(ScreenSize.screenWidth, ScreenSize.screenHeight)
  static let screenMinLength = min(ScreenSize.screenWidth, ScreenSize.screenHeight)
}

struct DeviceType {

  static let isIPhone4OrLess =
    UIDevice.current.userInterfaceIdiom == .phone
    && ScreenSize
      .screenMaxLength < 568.0

  static let isIPhone5 =
    UIDevice.current.userInterfaceIdiom == .phone
    && ScreenSize
      .screenMaxLength == 568.0

  static let isIPhoneSE2 =
    UIDevice.current.userInterfaceIdiom == .phone
    && ScreenSize
      .screenMaxLength == 667.0

  static let isIPhone8P =
    UIDevice.current.userInterfaceIdiom == .phone
    && ScreenSize
      .screenMaxLength == 736.0

  static let isIPad =
    UIDevice.current.userInterfaceIdiom == .pad
    && ScreenSize.screenMaxLength
      == 1024.0

  static let isIPhoneXOrHigh =
    UIDevice.current.userInterfaceIdiom == .phone
    && ScreenSize
      .screenMaxLength >= 812
}

struct OSVersion {
  static let systemVersion = (UIDevice.current.systemVersion as NSString).floatValue
  static let iOS7 = (OSVersion.systemVersion < 8.0 && OSVersion.systemVersion >= 7.0)
  static let iOS8 = (OSVersion.systemVersion >= 8.0 && OSVersion.systemVersion < 9.0)
  static let iOS9 = (OSVersion.systemVersion >= 9.0 && OSVersion.systemVersion < 10.0)
}

class Utilities: NSObject {

  class func isStandaloneApp() -> Bool {
    return Branding.isStandaloneStudyApp
  }

  class func standaloneStudyId() -> String {
    return Branding.standaloneStudyID
  }

  class func getUIColorFromHex(_ hexInt: Int, alpha: CGFloat? = 1.0) -> UIColor {

    let red = CGFloat((hexInt & 0xff0000) >> 16) / 255.0
    let green = CGFloat((hexInt & 0xff00) >> 8) / 255.0
    let blue = CGFloat((hexInt & 0xff) >> 0) / 255.0
    let alpha = alpha!

    // Create color object, specifying alpha as well
    let color = UIColor(red: red, green: green, blue: blue, alpha: alpha)
    return color
  }

  class func hexStringToUIColor(_ hex: String) -> UIColor {

    var cString: String = hex.trimmingCharacters(in: NSCharacterSet.whitespacesAndNewlines)

    if cString.hasPrefix("#") {
      let index = cString.index(cString.startIndex, offsetBy: 1)
      cString = String(cString[index...])
    }

    if (cString.count) != 6 {
      return UIColor.gray
    }

    var rgbValue: UInt32 = 0
    Scanner(string: cString).scanHexInt32(&rgbValue)

    return UIColor(
      red: CGFloat((rgbValue & 0xFF0000) >> 16) / 255.0,
      green: CGFloat((rgbValue & 0x00FF00) >> 8) / 255.0,
      blue: CGFloat(rgbValue & 0x0000FF) / 255.0,
      alpha: CGFloat(1.0)
    )
  }

  class func getDateFromStringWithFormat(_ dateFormate: String, resultDate: String) -> Date {

    let formatter = DateFormatter()
    formatter.locale = Locale(identifier: "us")
    formatter.dateStyle = DateFormatter.Style.full
    formatter.dateFormat = dateFormate
    let resDate = formatter.date(from: resultDate)

    return resDate!
  }

  // MARK: - Validation Methods

  class func isValidEmail(testStr: String) -> Bool {
    let emailRegEx = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,64}"
    let emailPred = NSPredicate(format: "SELF MATCHES %@", emailRegEx)
    return emailPred.evaluate(with: testStr)
  }

  /// checks all the validations for password
  class func isPasswordValid(text: String) -> Bool {
    let text = text

    let lowercaseLetterRegEx = ".*[a-z]+.*"
    let lowercaseTextTest = NSPredicate(format: "SELF MATCHES %@", lowercaseLetterRegEx)
    let lowercaseresult = lowercaseTextTest.evaluate(with: text)

    let capitalLetterRegEx = ".*[A-Z]+.*"
    let texttest = NSPredicate(format: "SELF MATCHES %@", capitalLetterRegEx)
    let capitalresult = texttest.evaluate(with: text)

    let numberRegEx = ".*[0-9]+.*"
    let texttest1 = NSPredicate(format: "SELF MATCHES %@", numberRegEx)
    let numberresult = texttest1.evaluate(with: text)

    let specialCharacterRegEx = ".*[!#$%&'()*+,-.:;\\[\\]<>=?@^_{}|~]+.*"
    let texttest2 = NSPredicate(format: "SELF MATCHES %@", specialCharacterRegEx)

    let specialresult = texttest2.evaluate(with: text)

    let textCountResult = text.count > 7 && text.count <= 64 ? true : false

    if capitalresult == false || numberresult == false || specialresult == false
      || textCountResult
        == false
      || lowercaseresult == false
    {
      return false
    }

    return true
  }

  // Method to validate a value for Null condition
  //     @someObject: can be String,Int or Bool
  //     returns a Boolean on valid data
  class func isValidValue(someObject: AnyObject?) -> Bool {

    guard let someObject = someObject else {
      // is null

      return false
    }
    // is not null
    if (someObject is NSNull) == false {

      if someObject as? Int != nil
        && ((someObject as? Int)! >= 0 || (someObject as? Int)! < 0)
      {
        return true

      } else if someObject as? String != nil && ((someObject as? String)?.count)! > 0
        && (someObject as? String) != ""
      {
        return true

      } else if someObject as? Bool != nil
        && (someObject as! Bool == true || someObject as! Bool == false)
      {
        return true

      } else if someObject as? Double != nil && (someObject as? Double)?.isFinite == true
        && (someObject as? Double)?.isZero == false && (someObject as? Double)! > 0
      {
        return true

      } else if someObject as? Date != nil {
        return true

      } else {
        return false
      }
    } else {
      return false
    }
  }

  // Method to Validate Object and checks for Null
  //     @someObject: can be either an Array or Dictionary
  //     returns a Boolean if someObject is not null
  class func isValidObject(someObject: AnyObject?) -> Bool {

    guard let someObject = someObject else {
      return false
    }

    if (someObject is NSNull) == false {
      if someObject as? [String: Any] != nil
        && (someObject as? [String: Any])?.isEmpty
          == false
        && ((someObject as? [String: Any])?.count)! > 0
      {
        return true

      } else if someObject as? NSArray != nil && ((someObject as? NSArray)?.count)! > 0 {
        return true

      } else {
        return false
      }

    } else {
      return false
    }
  }

  public static var shortFormatter: DateFormatter?

  public static var formatterShort: DateFormatter! {

    get {

      if let f = shortFormatter {
        return f
      } else {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        //formatter.dateStyle = .short
        formatter.timeZone = TimeZone.current
        shortFormatter = formatter
        return formatter
      }
    }
    set(newValue) {
      shortFormatter = newValue
    }
  }

  // Method to get DateFromString for default dateFormatter
  //     @dateString:a date String of format "yyyy-MM-dd'T'HH:mm:ssZ"
  //     returns date for the specified dateString in same format
  class func getDateFromString(dateString: String) -> Date? {

    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

    guard let date = dateFormatter.date(from: dateString) else {
      return nil
    }

    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
    let finalString = dateFormatter.string(from: date)
    let finalDate = dateFormatter.date(from: finalString)
    return finalDate
  }

  // Method to get DateFromString for default dateFormatter
  //     @dateString:a date String of format "yyyy-MM-dd'T'HH:mm:ssZ"
  //     returns date for the specified dateString in same format
  class func getDateFromStringWithOutTimezone(dateString: String) -> Date? {

    let dateWithoutTimeZoneArray = dateString.components(separatedBy: ".")
    let dateWithourTimeZone = dateWithoutTimeZoneArray[0] as String
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss"

    guard let date = dateFormatter.date(from: dateWithourTimeZone) else {
      return nil
    }

    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"

    let finalString = dateFormatter.string(from: date)
    let finalDate = dateFormatter.date(from: finalString)
    return finalDate

  }

  class func findDateFromString(dateString: String) -> Date? {
    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd HH:mm:ss"
    let finalDate = dateFormatter.date(from: dateString)
    return finalDate
  }

  // Method to get StringFromDate for default dateFormatter
  //     @date:a date  of format "yyyy-MM-dd'T'HH:mm:ssZ"
  //     returns dateString for the specified date in same format
  class func getStringFromDate(date: Date) -> String? {

    let dateFormatter = DateFormatter()
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    dateFormatter.timeZone = TimeZone.current

    let dateValue = dateFormatter.string(from: date)

    return dateValue
  }

  class func getAppVersion() -> String {
    let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as! String
    let bundleVersion = Bundle.main.infoDictionary?["CFBundleVersion"] as! String
    return version + "." + bundleVersion
  }

  class func currentDevicePlatform() -> String {
    return "IOS"
  }
  
  static func isVisible(view: UIView) -> Bool {
    func isVisible(view: UIView, inView: UIView?) -> Bool {
      guard let inView = inView else { return true }
      let viewFrame = inView.convert(view.bounds, from: view)
      if viewFrame.intersects(inView.bounds) {
        return isVisible(view: view, inView: inView.superview)
      }
      return false
    }
    return isVisible(view: view, inView: view.superview)
  }

  // MARK: Alert handlers

  class func showAlertWithTitleAndMessage(title: String, message: String, on vc: UIViewController) {

    let alertVC = UIAlertController(title: title, message: message, preferredStyle: .alert)
    let okAction = UIAlertAction(title: "Ok", style: .default, handler: nil)
    alertVC.addAction(okAction)
    vc.present(alertVC, animated: true, completion: nil)

  }

  class func randomString(length: Int) -> String {

    let letters: NSString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    let len = UInt32(letters.length)

    var randomString = ""

    for _ in 0..<length {
      let rand = arc4random_uniform(len)
      var nextChar = letters.character(at: Int(rand))
      randomString += NSString(characters: &nextChar, length: 1) as String
    }

    return randomString
  }

  class func dictionaryToData(value: JSONDictionary) -> Data? {
    guard
      let theJSONData = try? JSONSerialization.data(
        withJSONObject: value,
        options: []
      )
    else { return nil }
    return theJSONData
  }

  /// This method will get the user defined name of the app from info.plist.
  /// - Returns: Name of the app.
  static func appName() -> String {
    var varBundelName = ""
    if let bundleDisplayName = Bundle.main.object(forInfoDictionaryKey: "CFBundleDisplayName") as? String {
      varBundelName = bundleDisplayName
    } else if let bundleName = Bundle.main.object(forInfoDictionaryKey: "CFBundleName") as? String {
      varBundelName = bundleName
    }
    return UserManageApps.appDetails?.appName ?? varBundelName
  }
}

extension FileManager {

  // Method to get documentDirectory of Application
  //     return path of documentDirectory
  class func documentsDir() -> String {
    let paths =
      NSSearchPathForDirectoriesInDomains(.documentDirectory, .userDomainMask, true)
      as [String]
    return paths[0]
  }

}
