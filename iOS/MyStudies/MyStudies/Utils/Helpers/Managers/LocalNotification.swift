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
import UserNotifications

/// LocalNotification manages registration and refreshing notification for the activities and studies
class LocalNotification: NSObject {

  /// StudyList
  static var studies: [Study] = []

  /// NotificationList
  static var notificationList: [AppLocalNotification] = []

  static var handler: ((Bool) -> Void) = { _ in }

  /// Registers local ntification for joined studies
  /// - Parameter completionHandler: returns bool value
  class func registerLocalNotificationForJoinedStudies(
    completionHandler: @escaping (Bool) -> Void
  ) {

    studies =
      (Gateway.instance.studies?.filter({
        $0.userParticipateState.status == UserStudyStatus.StudyStatus.inProgress
          && $0
            .status
            == .active
      }))!

    handler = completionHandler
    LocalNotification.registerForStudy()

  }

  /// This method is used to register for study
  class func registerForStudy() {

    if studies.count > 0 {

      let study = studies.first
      LocalNotification.registerForStudy(study: study!) { (done) in
        if done {
          if (studies.count) > 0 {
            studies.removeFirst()
            LocalNotification.registerForStudy()
          }
        }
      }
    } else {
      handler(true)
    }
  }

  /// Registers all local notification
  /// - Parameter study: instance of Study
  /// - Parameter completionHandler: returns bool value
  class func registerForStudy(study: Study, completionHandler: @escaping (Bool) -> Void) {

    DBHandler.loadActivityListFromDatabase(studyId: study.studyId) { (activities) in
      if activities.count > 0 {

        LocalNotification.registerAllLocalNotificationFor(
          activities: activities,
          completionHandler: {
            (_, _) in
            completionHandler(true)
          }
        )
      } else {
        completionHandler(true)
      }
    }
  }

  fileprivate static func scheduleNotificationForOneTime(_ run: ActivityRun, _ activity: Activity) {
    if run.endDate != nil {
      let date = run.endDate.addingTimeInterval(-24 * 3600)  // 24 hours before
      let message =
        "The activity " + (activity.name ?? "")
        + " will expire in 24 hours. Your participation is important. Please visit the study to complete it now."
      LocalNotification.composeRunNotification(
        startDate: date,
        endDate: run.endDate,
        message: message,
        run: run,
        isExpiry: true
      )
    }

    if let startDate = run.startDate,
      let endDate = run.endDate
    {
      // start notification
      let startMessage =
        "A new run of the one time activity " + (activity.name ?? "")
        + ", is now available. Your participation is important. Please visit the study to complete it now."
      LocalNotification.composeRunNotification(
        startDate: startDate,
        endDate: endDate,
        message: startMessage,
        run: run
      )
    }
  }

  /// Registesr local notification for activities based on the frequency type
  /// - Parameter activities: array of Activity
  /// - Parameter completionHandler: return status which is bool value and array of AppLocalNotifications
  class func registerAllLocalNotificationFor(
    activities: [Activity],
    completionHandler: @escaping (Bool, [AppLocalNotification]) -> Void
  ) {

    LocalNotification.notificationList.removeAll()

    let date = Date()
    for activity in activities {
      guard activity.state == ActivityState.active.rawValue else { continue }
      var runsBeforeToday: [ActivityRun] = []
      if activity.frequencyType == Frequency.oneTime && activity.endDate == nil {
        runsBeforeToday = activity.activityRuns
      } else {
        runsBeforeToday = activity.activityRuns.filter({ $0.endDate >= date })
      }

      for run in runsBeforeToday {

        switch activity.frequencyType {

        case .oneTime:
          scheduleNotificationForOneTime(run, activity)

        case .daily:
          if activity.frequencyRuns?.count == 1 {
            let date = run.startDate!  // 24 hours before
            let message =
              "A new run of the daily activity " + activity.name!
              + ", is now available. Your participation is important. Please visit the study to complete it now."
            LocalNotification.composeRunNotification(
              startDate: date,
              endDate: run.endDate,
              message: message,
              run: run
            )
          } else {
            let date = run.startDate!  // 24 hours before
            let message1 = "A new run of the daily activity " + activity.name!
            let runEndDate = run.endDate.addingTimeInterval(1)  // Round off.
            let message2 =
              ", is now available and is valid until "
              + LocalNotification
              .timeFormatter.string(from: runEndDate)
            let message3 =
              ". Your participation is important. Please visit the study to complete it now."
            let message = message1 + message2 + message3
            LocalNotification.composeRunNotification(
              startDate: date,
              endDate: run.endDate,
              message: message,
              run: run
            )
          }

        case .weekly:
          // expiry notificaiton
          let date = run.endDate.addingTimeInterval(-24 * 3600)
          let message =
            "The current run of the weekly activity " + activity.name!
            + " will expire in 24 hours. Your participation is important. Please visit the study to complete it now."
          LocalNotification.composeRunNotification(
            startDate: date,
            endDate: run.endDate,
            message: message,
            run: run,
            isExpiry: true
          )
          // start notification
          let startMessage =
            "A new run of the weekly activity " + activity.name!
            + ", is now available. Please visit the study to complete it now."
          LocalNotification.composeRunNotification(
            startDate: run.startDate!,
            endDate: run.endDate,
            message: startMessage,
            run: run
          )

        case .monthly:
          let date = run.endDate.addingTimeInterval(-72 * 3600)
          let message =
            "The current run of the monthly activity " + activity.name!
            + " will expire in 3 days. Your participation is important. Please visit the study to complete it now."
          LocalNotification.composeRunNotification(
            startDate: date,
            endDate: run.endDate,
            message: message,
            run: run,
            isExpiry: true
          )
          // start notification
          let startMessage =
            "A new run of the monthly activity " + activity.name!
            + ", is now available. Please visit the study to complete it now."
          LocalNotification.composeRunNotification(
            startDate: run.startDate!,
            endDate: run.endDate,
            message: startMessage,
            run: run
          )

        case .scheduled:
          let date = run.startDate!  // 24 hours before
          let endDate = LocalNotification.oneTimeFormatter.string(from: run.endDate!)
          let message1 = "A new run of the scheduled activity " + activity.name!
          let message2 = ", is now available and is valid until " + "\(endDate)"
          let message3 =
            ". Your participation is important. Please visit the study to complete it now."
          let message = message1 + message2 + message3
          LocalNotification.composeRunNotification(
            startDate: date,
            endDate: run.endDate,
            message: message,
            run: run
          )
        }
      }
    }
    completionHandler(true, LocalNotification.notificationList)
  }

  /// Creates a notification by using start date, end date, message and activityrun
  /// - Parameter startDate: instance of date
  /// - Parameter endDate: instance of date
  /// - Parameter message: message of type string
  /// - Parameter run: instance of ActivityRun
  class func composeRunNotification(
    startDate: Date,
    endDate: Date,
    message: String,
    run: ActivityRun,
    isExpiry: Bool = false
  ) {

    _ =
      [
        kStudyId: run.studyId,
        kActivityId: run.activityId,
      ] as? [String: String]

    // create App local notification object
    var id = String(run.runId) + run.activityId + run.studyId
    if isExpiry {
      id += "Expiry"
    }
    let notification = AppLocalNotification()
    notification.id = id
    notification.message = message
    notification.activityId = run.activityId
    notification.title = ""
    notification.startDate = startDate
    notification.endDate = endDate
    notification.type = AppNotification.NotificationType.study
    notification.subType = AppNotification.NotificationSubType.activity
    notification.audience = Audience.limited
    notification.studyId = run.studyId  //(Study.currentStudy?.studyId)!

    LocalNotification.notificationList.append(notification)
  }

  /// Schedules a notification
  /// - Parameter date: instance of date
  /// - Parameter message: message of type string
  /// - Parameter userInfo: info of type Dictionary
  /// - Parameter id: id of type int
  class func scheduleNotificationOn(
    date: Date,
    message: String,
    userInfo: [String: Any],
    id: String?
  ) {
    var date = date
    if date > Date() {
      let content = UNMutableNotificationContent()
      content.body = message
      content.userInfo = userInfo
      content.sound = UNNotificationSound.default
      content.badge = 1

      date.updateWithOffset()
      let dateComponents = Calendar(identifier: .iso8601)
        .dateComponents(
          [.year, .month, .day, .hour, .minute, .second],
          from: date
        )
      let notificationTrigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: false)

      let id = id ?? Utilities.randomString(length: 10)
      let request = UNNotificationRequest(identifier: id, content: content, trigger: notificationTrigger)
      let center = UNUserNotificationCenter.current()
      center.add(request)

    }

  }

  /// Deletes a notification for a studyId and activityId
  /// - Parameter studyId: studyId of type string
  /// - Parameter activityid: activityId of type string
  class func removeLocalNotificationfor(studyId: String, activityid: String) {

    let notificationCenter = UNUserNotificationCenter.current()
    notificationCenter.getPendingNotificationRequests { (allNotificaiton) in

      var nIdentifers: [String] = []
      for notification in allNotificaiton {
        let userInfo = notification.content.userInfo
        if userInfo[kStudyId] != nil && userInfo[kActivityId] != nil {
          if userInfo[kStudyId] as! String == studyId
            && userInfo[kActivityId] as! String
              == activityid
          {
            nIdentifers.append(notification.identifier)
          }
        }
      }
      notificationCenter.removePendingNotificationRequests(withIdentifiers: nIdentifers)
    }

  }

  /// Removes local notification for a studyId
  /// - Parameter studyId: studyId of type string
  class func removeLocalNotificationfor(studyId: String) {

    let notificationCenter = UNUserNotificationCenter.current()
    notificationCenter.getPendingNotificationRequests { (allNotificaiton) in

      var nIdentifers: [String] = []
      for notification in allNotificaiton {
        let userInfo = notification.content.userInfo
        if userInfo[kStudyId] != nil {
          if userInfo[kStudyId] as! String == studyId {
            nIdentifers.append(notification.identifier)
          }
        }
      }
      notificationCenter.removePendingNotificationRequests(withIdentifiers: nIdentifers)
    }

  }

  /// Cancels all local notifications
  class func cancelAllLocalNotification() {
    UNUserNotificationCenter.current().removeAllPendingNotificationRequests()
  }

  /// Checked wheather notifications are anabled or not.
  /// - Parameter completion: A Boolean  value indication notification enable status.
  class func notificationsEnabled(completion: @escaping (_ status: Bool) -> Void) {
    let center = UNUserNotificationCenter.current()
    center.getNotificationSettings { (settings) in
      if settings.authorizationStatus == .authorized {
        completion(true)
        return
      }
      if #available(iOS 12.0, *), settings.authorizationStatus == .provisional {
        completion(true)
        return
      }
      completion(false)
    }
  }

  /// Cancels existing notifications and reschedules the top 50 notification from local notifications list
  class func refreshAllLocalNotification() {

    // Fetch top 50 notifications
    DBHandler.getRecentLocalNotification { (localNotifications) in

      // Cancel Old scheduled Local Notifications.
      LocalNotification.cancelAllLocalNotification()

      if localNotifications.count > 0 {

        for notification in localNotifications {

          // Generate User Info
          let userInfo = [
            kStudyId: notification.studyId!,
            kActivityId: notification.activityId!,
          ]

          // Reschedule top 50 Local Notifications.
          LocalNotification.scheduleNotificationOn(
            date: notification.startDate!,
            message: notification.message!,
            userInfo: userInfo,
            id: notification.id
          )
        }
      }
    }
  }

  /// Removes all of the app’s delivered notifications from Notification Center.
  static func removeAllDeliveredNotifications() {
    let center = UNUserNotificationCenter.current()
    center.removeAllDeliveredNotifications()
  }

  private static let timeFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma"
    return formatter
  }()

  private static let oneTimeFormatter: DateFormatter = {
    let formatter = DateFormatter()
    formatter.dateFormat = "hh:mma, MMM dd YYYY"
    return formatter
  }()

}

class NotificationHandler: NSObject {
  var studyId: String! = ""
  var activityId: String! = ""
  var appOpenFromNotification = false
  static var instance = NotificationHandler()

  func reset() {
    self.studyId = ""
    self.activityId = ""
    self.appOpenFromNotification = false
  }
}
