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

//api keys
let kEnrollmentToken = "token"
let kParticipantId = "participantId"
let kEnrollmentTokenValid = "valid"
let kDeleteResponses = "delete"

class ResponseServices: NSObject {

  let networkManager = NetworkManager.sharedInstance()
  weak var delegate: NMWebServiceDelegate?
  var activityId: String!
  var keys: String!
  var requestParams: [String: Any]? = [:]
  var headerParams: [String: String]? = [:]
  var method: Method!

  private(set) var isOfflineSyncRequest = false

  private enum JSONKey {
    static let applicationId = "applicationId"
    static let studyVersion = "studyVersion"
    static let tokenIdentifier = "tokenIdentifier"
    static let tokenID = "tokenId"
    static let siteID = "siteId"
    static let appID = "appId"
    static let studyID = "studyId"
    static let activityID = "activityId"
    static let userID = "userId"
  }

  // MARK: Requests

  /// Creates a request to process `Activity` response
  /// - Parameters:
  ///   - responseData: Response in form of `JSONDictionary`
  ///   - delegate: Class object to receive response
  func processResponse(
       responseData: [String: Any],
       delegate: NMWebServiceDelegate
     ) {
    self.delegate = delegate

    let method = ResponseMethods.processResponse.method

    let currentUser = User.currentUser
    if let userStudyStatus = currentUser.participatedStudies.filter({
      $0.studyId == Study.currentStudy?.studyId!
    }).first {
      let currentStudy = Study.currentStudy

      let studyId = currentStudy?.studyId ?? ""
      let activiyId = Study.currentActivity?.actvityId ?? ""
      let activityName = Study.currentActivity?.shortName ?? ""
      let activityVersion = Study.currentActivity?.version ?? ""
      let currentRunId = Study.currentActivity?.currentRunId ?? 0
      let studyVersion = currentStudy?.version ?? ""
      let info =
        [
          kStudyId: studyId,
          kActivityId: activiyId,
          kActivityName: activityName,
          "version": activityVersion,
          kActivityRunId: "\(currentRunId)",
          JSONKey.studyVersion: studyVersion,
        ]

      let activityType = Study.currentActivity?.type?.rawValue

      let params =
        [
          kActivityType: activityType!,
          kActivityInfoMetaData: info,
          kParticipantId: userStudyStatus.participantId ?? "",
          JSONKey.tokenIdentifier: userStudyStatus.tokenIdentifier ?? "",
          JSONKey.siteID: userStudyStatus.siteID ?? "",
          kActivityResponseData: responseData,
          JSONKey.applicationId: AppConfiguration.appID,
        ] as [String: Any]

      let headers: [String: String] = [
        JSONKey.userID: currentUser.userId ?? ""
      ]
      self.sendRequestWith(method: method, params: params, headers: headers)
    }
  }
  
  func processUpdateResponse(
       responseData: [String: Any],
       activityStatus: UserActivityStatus,
       delegate: NMWebServiceDelegate
  ) {
    self.delegate = delegate
    
    let method = ResponseMethods.processResponse.method
    
    let currentUser = User.currentUser
    if let userStudyStatus = currentUser.participatedStudies.filter({
      $0.studyId == Study.currentStudy?.studyId!
    }).first {
      let currentStudy = Study.currentStudy
      
      let studyId = currentStudy?.studyId ?? ""
      let activiyId = Study.currentActivity?.actvityId ?? ""
      let activityName = Study.currentActivity?.shortName ?? ""
      let activityVersion = Study.currentActivity?.version ?? ""
      let currentRunId = Study.currentActivity?.currentRunId ?? 0
      let studyVersion = currentStudy?.version ?? ""
      let info =
      [
        kStudyId: studyId,
        kActivityId: activiyId,
        kActivityName: activityName,
        "version": activityVersion,
        kActivityRunId: "\(currentRunId)",
        JSONKey.studyVersion: studyVersion,
      ]
      
      let activityType = Study.currentActivity?.type?.rawValue
      
      let params =
      [
        kActivityType: activityType!,
        kActivityInfoMetaData: info,
        kParticipantId: userStudyStatus.participantId ?? "",
        JSONKey.tokenIdentifier: userStudyStatus.tokenIdentifier ?? "",
        JSONKey.siteID: userStudyStatus.siteID ?? "",
        kActivityResponseData: responseData,
        JSONKey.applicationId: AppConfiguration.appID,
        "activityRun": activityStatus.getParticipatedUserUpdateRunActivityStatus(),
      ] as [String: Any]
      
      let headers: [String: String] = [
        JSONKey.userID: currentUser.userId ?? ""
      ]
      self.sendRequestWith(method: method, params: params, headers: headers)
    }
  }

  /// Creates a request to receive participant response
  /// - Parameters:
  ///   - tableName: ID of `Activity` to create a query
  ///   - activityId: ActivityID
  ///   - keys: Combination of activities IDs
  ///   - participantId: Participant ID
  ///   - delegate: Class object to receive response
  func getParticipantResponse(
    activity: Activity,
    study: Study,
    keys: String,
    delegate: NMWebServiceDelegate
  ) {
    self.delegate = delegate
    self.activityId = activity.actvityId ?? ""
    self.keys = keys
    let method = ResponseMethods.getParticipantResponse.method
    let userStudyStatus = study.userParticipateState

    let params =
      [
        JSONKey.appID: AppConfiguration.appID,
        JSONKey.siteID: userStudyStatus?.siteID ?? "",
        JSONKey.studyID: study.studyId ?? "",
        JSONKey.activityID: self.activityId!,
        kParticipantId: userStudyStatus?.participantId ?? "",
        "activityVersion": activity.version ?? "",
        "questionKey": "",
        JSONKey.tokenID: userStudyStatus?.tokenIdentifier ?? "",
      ] as [String: Any]

    let headers: [String: String] = [JSONKey.userID: User.currentUser.userId ?? ""]

    self.sendRequestWith(method: method, params: params, headers: headers)
  }

  /// Creates a request to sync offline data
  /// - Parameters:
  ///   - method: instance of Method
  ///   - params: request params
  ///   - headers: request headers
  ///   - delegate: Class object to receive response
  func syncOfflineSavedData(
    method: Method,
    params: [String: Any]?,
    headers: [String: String]?,
    delegate: NMWebServiceDelegate
  ) {
    self.isOfflineSyncRequest = true
    self.delegate = delegate
    self.sendRequestWith(method: method, params: params!, headers: headers)
  }

  /// Creates a request to get `Activity` status
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getUserActivityState(studyId: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let params = [
      kStudyId: studyId,
      kParticipantId: Study.currentStudy?.userParticipateState.participantId ?? "",
    ]
    let headerParams = [
      kUserId: user.userId!,
      "Content-Type": "application/json",
    ]
    let method = ResponseMethods.activityState.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update `Activity` participation status
  /// - Parameters:
  ///   - studyId: ID of Study
  ///   - activityStatus: Instance of `UserActivityStatus` to update
  ///   - delegate: Class object to receive response
  func updateUserActivityParticipatedStatus(
    studyId: String,
    participantId: String,
    activityStatus: UserActivityStatus,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams =
      [
        kUserId: user.userId,
        kParticipantId: participantId,
      ] as? [String: String]

    let params =
      [
        kStudyId: studyId,
        kParticipantId: participantId,
        kActivity: [activityStatus.getParticipatedUserActivityStatus()],
      ] as [String: Any]
    let method = ResponseMethods.updateActivityState.method
    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  func updateToken(manager: NetworkManager, requestName: NSString, error: NSError) {
    HydraAPI.refreshToken { (status, error) in
      if status {
        self.handleUpdateTokenResponse()
      } else if let error = error {
        self.delegate?.failedRequest(
          manager,
          requestName:
            requestName,
          error:
            error.toNSError()
        )
      }
    }
  }

  // MARK: Parsers

  /// Handles Participant Response
  /// - Parameter response: Webservice response
  func handleGetParticipantResponse(response: [String: Any]) {

    guard !response.isEmpty else { return }
    var dashBoardResponse: [DashboardResponse] = []

    if let rows = response["rows"] as? [JSONDictionary] {

      for rowDetail in rows {
        if let dataDictArr = rowDetail["data"] as? [JSONDictionary] {
          // created date
          let data = dataDictArr[safe: 2] ?? [:]
          let dataCount = dataDictArr[safe: 3] ?? [:]
          
          //Spacial
          let dataScore = dataDictArr[safe: 2] ?? [:]
          let dataCountNumberofGames = dataDictArr[safe: 3] ?? [:]
          let dataCountNumberofFailures = dataDictArr[safe: 4] ?? [:]
          
          var date: String = ""

          if let createdDict = dataDictArr[safe: 1],
            let dateDetail = createdDict["Created"] as? JSONDictionary
          {
            date = dateDetail["value"] as? String ?? ""
          }

          // FetalKick
          if dataCount["count"] != nil && data["duration"] != nil {

            // count
            let countDetail = dataCount["count"] as? [String: Any]
            let count = (countDetail?["value"] as? Float)!

            // duration
            let durationDetail = data["duration"] as? [String: Any]
            let duration = durationDetail?["value"] as? Double ?? 0.0

            let valueDetail =
              [
                "value": duration / 60,  // Show duration in minutes which is stored in seconds on server.
                "count": count,
                "date": date,
              ] as [String: Any]

            let responseData1 = DashboardResponse(with: activityId, and: "duration")
            responseData1.values.append(valueDetail)
            dashBoardResponse.append(responseData1)
            
          } else if dataCountNumberofFailures["NumberofFailures"] != nil && dataCountNumberofGames["NumberofGames"] != nil
                      && dataScore[
                        "Score"
                      ]
                      != nil
          {
            let numberOfFailuresDetail = dataCountNumberofFailures["NumberofFailures"] as? [String: Any]
            let numberOfFailures = (numberOfFailuresDetail?["value"] as? Double)!
            let valueDetail1 =
              [
                "value": numberOfFailures,
                "count": Float(0.0),
                "date": date,
              ] as [String: Any]
            let responseData1 = DashboardResponse(with: activityId, and: "Numberoffailures")
            responseData1.values.append(valueDetail1)
            dashBoardResponse.append(responseData1)
            
            // numberOfGames
            let numberOfGamesDetail = dataCountNumberofGames["NumberofGames"] as? [String: Any]
            let numberOfGames = (numberOfGamesDetail?["value"] as? Double)!
            let valueDetail3 =
              [
                "value": numberOfGames,
                "count": Float(0.0),
                "date": date,
              ] as [String: Any]
            let responseData3 = DashboardResponse(with: activityId, and: "Numberofgames")
            responseData3.values.append(valueDetail3)
            dashBoardResponse.append(responseData3)
            
            // score
            let scoreDetail = dataScore["Score"] as? [String: Any]
            let score = (scoreDetail?["value"] as? Double)!
            
            let valueDetail2 =
              [
                "value": score,
                "count": Float(0.0),
                "date": date,
              ] as [String: Any]
            let responseData2 = DashboardResponse(with: activityId, and: "Score")
            responseData2.values.append(valueDetail2)
            dashBoardResponse.append(responseData2)
          } else {
            for (index, dict) in dataDictArr.enumerated() {
              guard index > 1,
                let key = dict.keys.first
              else { continue }
              let responseData = DashboardResponse(with: activityId, and: key)
              if let valueDict = dict[key] as? JSONDictionary {
                responseData.appendValues(from: valueDict, of: date)
                dashBoardResponse.append(responseData)
              }
            }

          }
        }
      }
    }

    StudyDashboard.instance.saveDashboardResponse(responseList: dashBoardResponse)
  }

  /// Handles `Activity` status response
  /// - Parameter response: Webservice response
  func handleGetActivityStatesResponse(response: [String: Any]) {
    let user = User.currentUser
    if let activites = response[kActivites] as? [[String: Any]] {
      if Study.currentStudy != nil {
        for activity in activites {
          let participatedActivity = UserActivityStatus(
            detail: activity,
            studyId: (Study.currentStudy?.studyId)!
          )
          user.participatedActivites.append(participatedActivity)
        }
      }
    }
  }

  func handleUpdateTokenResponse() {
    self.sendRequestWith(
      method: self.method,
      params: self.requestParams,
      headers: self.headerParams
    )
  }

  /// Sends request
  /// - Parameters:
  ///   - method: instance of `Method`
  ///   - params: request params
  ///   - headers: request headers
  private func sendRequestWith(method: Method, params: [String: Any]?, headers: [String: String]?) {

    self.requestParams = params
    self.headerParams = headers
    self.method = method

    networkManager.composeRequest(
      ResponseServerConfiguration.configuration,
      method: method,
      params: params as NSDictionary?,
      headers: headers as NSDictionary?,
      delegate: self
    )
  }
}
extension ResponseServices: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    delegate?.startedRequest(manager, requestName: requestName)
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    switch requestName {
    case ResponseMethods.getParticipantResponse.description as String:
      self.handleGetParticipantResponse(response: response as! [String: Any])
    case ResponseMethods.processResponse.description as String: break
    case ResponseMethods.updateActivityState.description as String: break
    case ResponseMethods.activityState.description as String:
      self.handleGetActivityStatesResponse(response: (response as? [String: Any])!)

    default:
      break  // Request was not sent with proper method name.
    }

    delegate?.finishedRequest(manager, requestName: requestName, response: response)
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    if error.code == HTTPError.tokenExpired.rawValue {
      // Update Refresh Token
      updateToken(manager: manager, requestName: requestName, error: error)
    } else {
      var errorInfo = error.userInfo
      var localError = error
      if error.code == HTTPError.forbidden.rawValue {
        errorInfo = ["NSLocalizedDescription": LocalizableString.sessionExpired.localizedString]
        localError = NSError(domain: error.domain, code: 403, userInfo: errorInfo)
      }

      // handle failed request due to network connectivity
      if requestName as String == ResponseMethods.processResponse.description
        || requestName as String == ResponseMethods.updateActivityState.description
      {

        if error.code == kNoNetworkErrorCode, !isOfflineSyncRequest {
          // Save in database if fails due to network
          // Ignore save for Sync request as the object already avaiable in the DB.
          DBHandler.saveRequestInformation(
            params: self.requestParams,
            headers: self.headerParams,
            method: requestName as String,
            server: SyncUpdate.ServerType.response.rawValue
          )
        }
      }
      delegate?.failedRequest(manager, requestName: requestName, error: localError)
    }
  }
}
