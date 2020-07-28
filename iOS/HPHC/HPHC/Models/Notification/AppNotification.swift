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

enum Audience: String {
  case all = "All"
  case participants = "Participants"
  case limited = "Limited"
}

class AppLocalNotification: AppNotification {
  var startDate: Date?
  var endDate: Date?

  convenience init(
    with resource: Resource,
    id: String,
    startDate: Date,
    endDate: Date,
    studyID: String
  ) {
    self.init()
    self.id = id
    message = resource.notificationMessage
    title = LocalizableString.newResourceMessage.localizedString
    self.startDate = startDate
    self.endDate = endDate
    type =
      AppNotification.NotificationType
      .study
    subType =
      AppNotification
      .NotificationSubType.resource
    audience = Audience.limited
    studyId = studyID
  }

  override init() {
    super.init()
  }
}

class AppNotification {

  enum NotificationType: String {
    case gateway = "Gateway"
    case study = "Study"
  }

  enum NotificationSubType: String {
    case announcement = "Announcement"
    case study = "Study"
    case resource = "Resource"
    case activity = "Activity"
    case studyEvent
  }

  var id: String?
  var type: NotificationType = .gateway
  var subType: NotificationSubType!
  var audience: Audience!
  var title: String?
  var message: String?
  var studyId: String?
  var date: Date?

  var activityId: String?
  var read: Bool? = false

  init() {
  }

  /// Initializes all properties
  /// - Parameter detail: `JSONDictionary` contains all properties of `AppNotification`
  init(detail: [String: Any]) {

    if Utilities.isValidObject(someObject: detail as AnyObject?) {

      if Utilities.isValidValue(someObject: detail[kNotificationId] as AnyObject) {
        self.id = detail[kNotificationId] as? String
      }
      if Utilities.isValidValue(someObject: detail[kNotificationTitle] as AnyObject) {
        self.title = detail[kNotificationTitle] as? String
      }
      if Utilities.isValidValue(someObject: detail[kNotificationMessage] as AnyObject) {
        self.message = detail[kNotificationMessage] as? String
      }
      if Utilities.isValidValue(someObject: detail[kNotificationType] as AnyObject) {
        self.type = NotificationType(rawValue: detail[kNotificationType] as! String)!
      }
      if Utilities.isValidValue(someObject: detail[kNotificationSubType] as AnyObject) {
        self.subType = NotificationSubType(
          rawValue: detail[kNotificationSubType] as! String
        )!
      }
      if Utilities.isValidValue(someObject: detail[kNotificationAudience] as AnyObject) {
        self.audience = Audience(rawValue: detail[kNotificationAudience] as! String)!
      }

      if Utilities.isValidValue(someObject: detail[kNotificationStudyId] as AnyObject) {
        self.studyId = detail[kNotificationStudyId] as? String
      }
      if Utilities.isValidValue(someObject: detail["date"] as AnyObject) {
        self.date = Utilities.getDateFromString(dateString: (detail["date"] as? String)!)
      }

      if Utilities.isValidValue(someObject: detail[kNotificationActivityId] as AnyObject) {
        self.activityId = detail[kNotificationActivityId] as? String
      }
    }
  }

}
