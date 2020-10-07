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

class AnchorDateQueryMetaData {

  enum FetchAnchorDateFor {
    case activity
    case resource
  }

  var activity: DBActivity!
  var resource: DBResources!
  var sourceKey: String!
  var sourceActivityId: String!
  var sourceFormKey: String?
  var anchorDate: Date?
  var sourceFormId: String!
  var isFinishedFetching: Bool = false
  var fetchAnchorDateFor: FetchAnchorDateFor = .activity
  lazy var sourceActivityVersion: String = ""

  init() {}

}

class AnchorDateHandler {

  var emptyAnchorDateMetaDataList: [AnchorDateQueryMetaData] = []
  typealias AnchordDateFetchCompletionHandler = (_ success: Bool) -> Void
  var handler: AnchordDateFetchCompletionHandler!
  var study: Study

  static let anchorDateFormatter: DateFormatter = {
    let dateFormatter = DateFormatter()
    let locale = Locale(identifier: "en_US_POSIX")
    dateFormatter.timeZone = TimeZone.current
    dateFormatter.locale = locale
    dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    return dateFormatter
  }()

  init(study: Study) {
    self.study = study
  }

  func fetchActivityAnchorDateResponse(
    _ completionHandler: @escaping AnchordDateFetchCompletionHandler
  ) {

    handler = completionHandler
    // Get activities from database for anchor date is not present
    let activities = DBHandler.getActivitiesWithEmptyAnchorDateValue(
      (study.studyId)
    )

    guard activities.count != 0 else {
      handler(false)
      return
    }

    for activity in activities {

      let act = User.currentUser.participatedActivites.filter({
        $0.activityId == activity.sourceActivityId
      }).last

      if act != nil && (act?.status == .completed) {
        let emptyAnchorDateDetail = AnchorDateQueryMetaData()
        emptyAnchorDateDetail.activity = activity
        emptyAnchorDateDetail.sourceKey = activity.sourceKey
        emptyAnchorDateDetail.sourceActivityId = activity.sourceActivityId
        emptyAnchorDateDetail.sourceFormKey = activity.sourceFormKey
        emptyAnchorDateMetaDataList.append(emptyAnchorDateDetail)
      }
    }

    guard !emptyAnchorDateMetaDataList.isEmpty else {
      handler(false)
      return
    }

    let queryGroup = DispatchGroup()

    for anchorMetaData in emptyAnchorDateMetaDataList {
      queryGroup.enter()
      requestAnchorDateForActivityResponse(for: anchorMetaData) {
        queryGroup.leave()
      }
    }

    queryGroup.notify(queue: DispatchQueue.main) {
      self.saveAnchorDateInDatabase()
      self.handler(true)
    }
  }

  func fetchActivityAnchorDateForResource(
    _ completionHandler: @escaping AnchordDateFetchCompletionHandler
  ) {

    handler = completionHandler

    let resources = DBHandler.getResourceWithEmptyAnchorDateValue(
      study.studyId
    )

    guard resources.count != 0 else {
      handler(false)
      return
    }

    for resource in resources {

      let act = User.currentUser.participatedActivites.filter {
        $0.activityId == resource.sourceActivityId
      }.last

      if act != nil && (act?.status == UserActivityStatus.ActivityStatus.completed) {

        let emptyAnchorDateDetail = AnchorDateQueryMetaData()
        emptyAnchorDateDetail.fetchAnchorDateFor = .resource
        emptyAnchorDateDetail.resource = resource
        emptyAnchorDateDetail.sourceActivityVersion = act?.activityVersion ?? ""
        emptyAnchorDateDetail.sourceKey = resource.sourceKey
        emptyAnchorDateDetail.sourceActivityId = resource.sourceActivityId
        emptyAnchorDateDetail.sourceFormKey = resource.sourceFormKey
        emptyAnchorDateMetaDataList.append(emptyAnchorDateDetail)
      }

    }

    guard emptyAnchorDateMetaDataList.count != 0 else {
      handler(false)
      return
    }

    let queryGroup = DispatchGroup()

    for anchorMetaData in emptyAnchorDateMetaDataList {
      queryGroup.enter()
      requestAnchorDateForActivityResponse(for: anchorMetaData) {
        queryGroup.leave()
      }
    }

    queryGroup.notify(queue: DispatchQueue.main) {
      self.saveAnchorDateInDatabase()
      self.handler(true)
    }
  }

  func requestAnchorDateForActivityResponse(
    for emptyAnchorDateDetail: AnchorDateQueryMetaData,
    completion: @escaping () -> Void
  ) {

    let method = ResponseMethods.getParticipantResponse.method

    let sourceActivityID = emptyAnchorDateDetail.sourceActivityId ?? ""
    let appID = AppConfiguration.appID
    let studyID = self.study.studyId ?? ""
    let tokenIdentifier = study.userParticipateState.tokenIdentifier ?? ""
    let siteID = study.userParticipateState.siteID ?? ""
    let activityVersion =
      emptyAnchorDateDetail.activity?.version ?? emptyAnchorDateDetail.sourceActivityVersion
    let participantId = study.userParticipateState.participantId ?? ""

    var urlString = ResponseServerURLConstants.DevelopmentURL + method.methodName + "?"
    urlString += "&appId" + "=" + appID
    urlString += "&studyId" + "=" + studyID
    urlString += "&siteId" + "=" + siteID
    urlString += "&activityId" + "=" + sourceActivityID
    urlString += "&activityVersion" + "=" + activityVersion
    urlString += "&participantId" + "=" + participantId
    urlString += "&questionKey"
    urlString += "&tokenIdentifier" + "=" + tokenIdentifier

    urlString =
      urlString.addingPercentEncoding(
        withAllowedCharacters: CharacterSet.urlQueryAllowed
      ) ?? ""

    guard let requestUrl = URL(string: urlString) else {
      completion()
      return
    }

    let headers: [String: String] = [
      "accessToken": User.currentUser.authToken ?? "",
      "userId": User.currentUser.userId ?? "",
    ]

    var request = URLRequest(
      url: requestUrl,
      cachePolicy: URLRequest.CachePolicy.reloadIgnoringLocalCacheData,
      timeoutInterval: NetworkConnectionConstants.ConnectionTimeoutInterval
    )
    request.httpMethod = method.methodType.methodTypeAsString
    request.allHTTPHeaderFields = headers

    let session = URLSession.shared
    let dataTask = session.dataTask(
      with: request as URLRequest,
      completionHandler: { (data, response, error) -> Void in

        if error != nil {
          completion()
        } else if let response = response, let data = data {

          let status = NetworkConstants.checkResponseHeaders(response)
          let statusCode = status.0
          if statusCode == 200 || statusCode == 0 {

            guard let responseDict = data.toJSONDictionary(),
              let rows = responseDict["rows"] as? [JSONDictionary],
              let latestResponse = rows.last
            else {
              completion()
              return
            }

            if let data = latestResponse["data"] as? [JSONDictionary],
              let userResponseDict = data[safe: 2],
              let anchorDateObject = userResponseDict[emptyAnchorDateDetail.sourceKey]
                as? [String: String],
              let anchorDateString = anchorDateObject["value"]
            {
              let date = AnchorDateHandler.anchorDateFormatter.date(from: anchorDateString)
              emptyAnchorDateDetail.anchorDate = date
              completion()
            } else {
              completion()
            }
          } else {
            completion()
          }
        } else {
          completion()
        }
      }
    )
    dataTask.resume()
  }

  func saveAnchorDateInDatabase() {

    let listItems = emptyAnchorDateMetaDataList.filter {
      $0.anchorDate != nil
    }
    if !listItems.isEmpty {
      // To reshedule the notifications for anchor date activities.
      DBHandler.updateLocalNotificationScheduleStatus(
        studyId: study.studyId,
        status: false
      )
      Study.currentStudy?.activitiesLocalNotificationUpdated = false
    }
    for item in listItems {
      if item.fetchAnchorDateFor == .activity {
        DBHandler.updateActivityLifeTimeFor(item.activity, anchorDate: item.anchorDate!)
      } else if item.fetchAnchorDateFor == .resource {

        var startDateStringEnrollment = Utilities.formatterShort?.string(
          from: item.anchorDate!
        )
        let startTimeEnrollment = "00:00:00"
        startDateStringEnrollment =
          (startDateStringEnrollment ?? "") + " "
          + startTimeEnrollment
        let anchorDate = Utilities.findDateFromString(
          dateString: startDateStringEnrollment ?? ""
        )
        DBHandler.saveLifeTimeFor(resource: item.resource, anchorDate: anchorDate!)
      }
    }
  }

}
