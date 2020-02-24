// License Agreement for FDA My Studies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is
// hereby granted, free of charge, to any person obtaining a copy of this software and associated
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

class LabKeyServices: NSObject {

  let networkManager = NetworkManager.sharedInstance()
  var delegate: NMWebServiceDelegate?
  var activityId: String!
  var keys: String!
  var requestParams: [String: Any]? = [:]

  var headerParams: [String: String]? = [:]

  // MARK: Requests

  /// Creates a request to enroll in a `Study`
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - token: Enrollment Token Id
  ///   - delegate: Class object to receive response
  func enrollForStudy(studyId: String, token: String, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = ResponseMethods.enroll.method

    let params = [
      kEnrollmentToken: token,
      kStudyId: studyId
    ]

    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to verify Enrollment Token
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - token: Enrollment Token Id
  ///   - delegate: Class object to receive response
  func verifyEnrollmentToken(studyId: String, token: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let method = ResponseMethods.validateEnrollmentToken.method

    let params = [
      kEnrollmentToken: token,
      kStudyId: studyId
    ]

    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to withdraw from `Study`
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - participantId: Participant ID
  ///   - deleteResponses: withdraw status in form of `Bool`
  ///   - delegate: Class object to receive response
  func withdrawFromStudy(
    studyId: String, participantId: String, deleteResponses: Bool,
    delegate: NMWebServiceDelegate
  ) {
    self.delegate = delegate
    let method = ResponseMethods.withdrawFromStudy.method

    let params = [
      kParticipantId: participantId,
      kDeleteResponses: deleteResponses
    ] as [String: Any]

    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to process `Activity` response
  /// - Parameters:
  ///   - metaData: Activity MetaData in form of `JSONDictionary`
  ///   - activityType: Type of Activity
  ///   - responseData: Response in form of `JSONDictionary`
  ///   - participantId: Participant ID
  ///   - delegate: Class object to receive response
  func processResponse(
    metaData: [String: Any], activityType: String, responseData: [String: Any],
    participantId: String, delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate
    let method = ResponseMethods.processResponse.method

    let params = [
      kActivityType: activityType,
      kActivityInfoMetaData: metaData,
      kParticipantId: participantId,
      kActivityResponseData: responseData
    ] as [String: Any]

    print("processresponse \(params.preetyJSON())")
    self.sendRequestWith(method: method, params: params, headers: nil)

  }

  /// Creates a request to process `Activity` response
  /// - Parameters:
  ///   - responseData: Response in form of `JSONDictionary`
  ///   - delegate: Class object to receive response
  func processResponse(responseData: [String: Any], delegate: NMWebServiceDelegate) {
    self.delegate = delegate

    let method = ResponseMethods.processResponse.method

    let currentUser = User.currentUser
    if let userStudyStatus = currentUser.participatedStudies.filter({ $0.studyId == Study.currentStudy?.studyId! }).first {

      let studyId = Study.currentStudy?.studyId!
      let activiyId = Study.currentActivity?.actvityId!
      let activityName = Study.currentActivity?.shortName!
      let activityVersion = Study.currentActivity?.version!
      let currentRunId = Study.currentActivity?.currentRunId

      let info = [
        kStudyId: studyId!,
        kActivityId: activiyId!,
        kActivityName: activityName!,
        "version": activityVersion!,
        kActivityRunId: "\(currentRunId!)"
      ] as [String: String]

      let ActivityType = Study.currentActivity?.type?.rawValue

      let params = [
        kActivityType: ActivityType!,
        kActivityInfoMetaData: info,
        kParticipantId: userStudyStatus.participantId! as String,
        kActivityResponseData: responseData
      ] as [String: Any]

      print("processresponse : \(params.preetyJSON())")
      self.sendRequestWith(method: method, params: params, headers: nil)
    }
  }

  /// Creates a request to receive participant response
  /// - Parameters:
  ///   - tableName: ID of `Activity` to create a query
  ///   - activityId: ActivityID
  ///   - keys: //TBD
  ///   - participantId: Participant ID
  ///   - delegate: Class object to receive response
  func getParticipantResponse(
    tableName: String, activityId: String, keys: String, participantId: String,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate
    self.activityId = activityId
    self.keys = keys
    let method = ResponseMethods.executeSQL.method
    let query = "SELECT " + keys + ",Created" + " FROM " + tableName
    let params = [

      kParticipantId: participantId,
      "sql": query
    ] as [String: Any]

    self.sendRequestWith(method: method, params: params, headers: nil)
  }

  /// Creates a request to sync offline data
  /// - Parameters:
  ///   - method: instance of Method
  ///   - params: request params
  ///   - headers: request headers
  ///   - delegate: Class object to receive response
  func syncOfflineSavedData(
    method: Method, params: [String: Any]?, headers: [String: String]?,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate
    self.sendRequestWith(method: method, params: params!, headers: headers)
  }

  // MARK: Parsers
  func handleEnrollForStudy(response: [String: Any]) {
  }

  func handleVerifyEnrollmentToken(response: [String: Any]) {
  }

  func handleWithdrawFromStudy(response: [String: Any]) {
  }

  func handleProcessResponse(response: [String: Any]) {
  }

  func handleGetParticipantResponse1(response: [String: Any]) {
    print(response)
  }

  /// Handles Participant Response
  /// - Parameter response: Webservice response
  func handleGetParticipantResponse(response: [String: Any]) {

    var dashBoardResponse: [DashboardResponse] = []
    let keysArray = self.keys.components(separatedBy: ",")
    for key in keysArray {

      let newkey = key.replacingOccurrences(of: "\"", with: "")
      let responseData = DashboardResponse()
      responseData.activityId = activityId
      responseData.key = newkey
      responseData.type = "int"
      responseData.isPHI = "true"

      dashBoardResponse.append(responseData)
    }

    if let rows = response["rows"] as? [[String: Any]] {
      print("rows \(rows)")

      for rowDetail in rows {
        if let data = rowDetail["data"] as? [String: Any] {
          // created date
          let dateDetail = data["Created"] as? [String: Any]
          let date = (dateDetail?["value"] as? String)!

          // FetalKick
          if data["count"] != nil && data["duration"] != nil {

            // for responseData in dashBoardResponse{
            let responseData = dashBoardResponse.first
            // count
            let countDetail = data["count"] as? [String: Any]
            let count = (countDetail?["value"] as? Float)!

            // duration
            let durationDetail = data["duration"] as? [String: Any]
            let duration = (durationDetail?["value"] as? Float)!

            let valueDetail = [
              "value": duration,
              "count": count,
              "date": date
            ] as [String: Any]

            responseData?.values.append(valueDetail)

          }  // Speatial Memory
          else if data["NumberofFailures"] != nil && data["NumberofGames"] != nil && data[
            "Score"]
            != nil {

            for responseData in dashBoardResponse {
              if responseData.key == "NumberofFailures" {
                // numberOfFailuresDetail
                let numberOfFailuresDetail = data["NumberofFailures"] as? [String: Any]
                let numberOfFailures = (numberOfFailuresDetail?["value"] as? Float)!

                let valueDetail1 = [
                  "value": numberOfFailures,
                  "count": Float(0.0),
                  "date": date
                ] as [String: Any]
                responseData.values.append(valueDetail1)

              } else if responseData.key == "NumberofGames" {
                // numberOfGames
                let numberOfGamesDetail = data["NumberofGames"] as? [String: Any]
                let numberOfGames = (numberOfGamesDetail?["value"] as? Float)!

                let valueDetail3 = [
                  "value": numberOfGames,
                  "count": Float(0.0),
                  "date": date
                ] as [String: Any]

                responseData.values.append(valueDetail3)
              } else if responseData.key == "Score" {
                // score
                let scoreDetail = data["Score"] as? [String: Any]
                let score = (scoreDetail?["value"] as? Float)!

                let valueDetail2 = [
                  "value": score,
                  "count": Float(0.0),
                  "date": date
                ] as [String: Any]

                responseData.values.append(valueDetail2)
              }
            }

          } else {
            for responseData in dashBoardResponse {

              if let keyValue = data[responseData.key!] as? [String: Any] {

                if Utilities.isValidValue(
                  someObject: keyValue["value"] as AnyObject?) {
                  var value: Float = 0.0
                  if let n = keyValue["value"] as? NSNumber {
                    value = n.floatValue
                  }
                  let valueDetail = [
                    "value": value,
                    "count": Float(0.0),
                    "date": date
                  ] as [String: Any]

                  responseData.values.append(valueDetail)
                }
              }
            }
          }
        }
      }
    }

    StudyDashboard.instance.saveDashboardResponse(responseList: dashBoardResponse)
  }

  /// Sends request
  /// - Parameters:
  ///   - method: instance of `Method`
  ///   - params: request params
  ///   - headers: request headers
  private func sendRequestWith(method: Method, params: [String: Any], headers: [String: String]?) {

    self.requestParams = params
    self.headerParams = headers

    networkManager.composeRequest(
      ResponseServerConfiguration.configuration,
      method: method,
      params: params as NSDictionary?,
      headers: headers as NSDictionary?,
      delegate: self)
  }
}
extension LabKeyServices: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    delegate?.startedRequest(manager, requestName: requestName)
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {

    switch requestName {
    case ResponseMethods.validateEnrollmentToken.description as String: break

    case ResponseMethods.enroll.description as String:
      self.handleEnrollForStudy(response: response as! [String: Any])
    case ResponseMethods.getParticipantResponse.description as String: break
    case ResponseMethods.executeSQL.description as String:
      self.handleGetParticipantResponse(response: response as! [String: Any])
    case ResponseMethods.processResponse.description as String: break
    case ResponseMethods.withdrawFromStudy.description as String: break
    default:
      print("Request was not sent with proper method name")
    }

    delegate?.finishedRequest(manager, requestName: requestName, response: response)
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    delegate?.failedRequest(manager, requestName: requestName, error: error)

    if requestName as String == ResponseMethods.processResponse.description {

      if error.code == NoNetworkErrorCode {
        // save in database
        print("save in database")
        DBHandler.saveRequestInformation(
          params: self.requestParams, headers: self.headerParams,
          method: requestName as String,
          server: "response")
      }
    }
  }
}
