//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

class EnrollServices: NSObject {

  let networkManager = NetworkManager.sharedInstance()
  weak var delegate: NMWebServiceDelegate?
  var requestParams: [String: Any] = [:]
  var headerParams: [String: String] = [:]
  var method: Method!

  private(set) var isOfflineSyncRequest = false

  // MARK: - Requests

  /// Creates a request to withdraw from `Study`
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - participantId: Participant ID
  ///   - deleteResponses: withdraw status in form of `Bool`
  ///   - delegate: Class object to receive response
  func withdrawFromStudy(
    studyId: String,
    participantId: String,
    deleteResponses: Bool,
    delegate: NMWebServiceDelegate
  ) {
    self.delegate = delegate
    let method = EnrollmentMethods.withdrawfromstudy.method

    let params =
      [
        kParticipantId: participantId,
        kDeleteResponses: deleteResponses,
        "studyId": studyId,
      ] as [String: Any]
    let headers = ["userId": User.currentUser.userId ?? ""]
    self.sendRequestWith(method: method, params: params, headers: headers)
  }

  /// Creates a request to get `Study` States
  /// - Parameter delegate: Class object to receive response
  func getStudyStates(_ delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!] as [String: String]
    let method = EnrollmentMethods.studyState.method

    self.sendRequestWith(method: method, params: nil, headers: headerParams)
  }

  /// Creates a request to update `Study` status
  /// - Parameters:
  ///   - studyStatus: Instance of `UserStudyStatus` to update
  ///   - delegate: Class object to receive response
  func updateCompletionAdherence(studyStatus: UserStudyStatus, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!]

    let params = [kStudies: [studyStatus.getCompletionAdherence()]] as [String: Any]
    let method = EnrollmentMethods.updateStudyState.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update `Study` bookmark status
  /// - Parameters:
  ///   - studyStatus: Instance of `UserStudyStatus` to update
  ///   - delegate: Class object to receive response
  func updateStudyBookmarkStatus(studyStatus: UserStudyStatus, delegate: NMWebServiceDelegate) {
    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId!]

    let params = [kStudies: [studyStatus.getBookmarkUserStudyStatus()]] as [String: Any]
    let method = EnrollmentMethods.updateStudyState.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to update `Study` participation status
  /// - Parameters:
  ///   - studyStauts: Instance of `UserStudyStatus` to update
  ///   - delegate: Class object to receive response
  func updateUserParticipatedStatus(studyStauts: UserStudyStatus, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [kUserId: user.userId] as [String: String]
    let params = [kStudies: [studyStauts.getParticipatedUserStudyStatus()]] as [String: Any]
    let method = EnrollmentMethods.updateStudyState.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to enroll in a `Study`
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - token: Enrollment Token Id
  ///   - delegate: Class object to receive response
  func enrollForStudy(studyId: String, token: String, delegate: NMWebServiceDelegate) {
    self.delegate = delegate
    let method = EnrollmentMethods.enroll.method

    let params = [
      kEnrollmentToken: token,
      kStudyId: studyId,
    ]
    let headers: [String: String] = [
      "userId": User.currentUser.userId ?? "",
    ]
    self.sendRequestWith(method: method, params: params, headers: headers)
  }

  /// Creates a request to verify Enrollment Token
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - token: Enrollment Token Id
  ///   - delegate: Class object to receive response
  func verifyEnrollmentToken(studyId: String, token: String, delegate: NMWebServiceDelegate) {

    self.delegate = delegate

    let method = EnrollmentMethods.validateEnrollmentToken.method

    let params = [
      kEnrollmentToken: token,
      kStudyId: studyId,
    ]

    let headers = [
      kUserId: User.currentUser.userId ?? "",
    ]

    self.sendRequestWith(method: method, params: params, headers: headers)
  }

  /// Creattes a request to sync offline data
  /// - Parameters:
  ///   - method: Instance of `Method`
  ///   - params:  Request Params
  ///   - headers: Request headers
  ///   - delegate: Class object to receive response
  func syncOfflineSavedData(
    method: Method,
    params: [String: Any]?,
    headers: [String: String]?,
    delegate: NMWebServiceDelegate
  ) {
    isOfflineSyncRequest = true
    self.delegate = delegate
    self.sendRequestWith(method: method, params: params, headers: headers)
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

  func handleUpdateTokenResponse() {
    self.sendRequestWith(
      method: self.method,
      params: self.requestParams,
      headers: self.headerParams
    )
  }

  /// Handles `Study` status response
  /// - Parameter response: Webservice response
  func handleGetStudyStatesResponse(response: [String: Any]) {
    let user = User.currentUser
    user.participatedStudies.removeAll()
    if let studies = response[kStudies] as? [[String: Any]] {

      for study in studies {
        let participatedStudy = UserStudyStatus(detail: study)
        user.participatedStudies.append(participatedStudy)
      }
    }
  }

  func handleEnrollForStudy(response: [String: Any]) {}

  /// Sends Request
  /// - Parameters:
  ///   - method: instance of `Method`
  ///   - params: request params
  ///   - headers: request headers
  private func sendRequestWith(method: Method, params: [String: Any]?, headers: [String: String]?) {

    self.requestParams = params ?? [:]
    self.headerParams = headers ?? [:]
    self.method = method
    networkManager.composeRequest(
      EnrollmentServerConfiguration.configuration,
      method: method,
      params: params as NSDictionary?,
      headers: headers as NSDictionary?,
      delegate: self
    )
  }

}
extension EnrollServices: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    delegate?.startedRequest(manager, requestName: requestName)
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    switch requestName {
    case EnrollmentMethods.studyState.description as String:
      self.handleGetStudyStatesResponse(response: response as? JSONDictionary ?? [:])

    case EnrollmentMethods.updateStudyState.description as String: break
    case EnrollmentMethods.validateEnrollmentToken.description as String: break

    case EnrollmentMethods.enroll.description as String:
      self.handleEnrollForStudy(response: response as? [String: Any] ?? [:])

    case AuthServerMethods.getRefreshedToken.description as String:
      self.handleUpdateTokenResponse()
      return

    default: break
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
        localError = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
      }

      // Handle failed request due to network connectivity
      // Save in database if fails due to network
      // Ignore save for Sync request as the object already avaiable in the DB.
      if requestName as String == EnrollmentMethods.updateStudyState.description {
        if error.code == kNoNetworkErrorCode, !isOfflineSyncRequest {
          DBHandler.saveRequestInformation(
            params: self.requestParams,
            headers: self.headerParams,
            method: requestName as String,
            server: SyncUpdate.ServerType.enrollment.rawValue
          )
        }
      }
      delegate?.failedRequest(manager, requestName: requestName, error: localError)
    }
  }
}
