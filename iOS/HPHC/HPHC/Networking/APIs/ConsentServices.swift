//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

class ConsentServices: NSObject {

  let networkManager = NetworkManager.sharedInstance()
  var delegate: NMWebServiceDelegate?
  var requestParams: [String: Any]? = [:]
  var headerParams: [String: String]? = [:]
  var method: Method!
  var failedRequestServices = FailedUserServices()

  // MARK: Requests

  /// Creates a request to update Consent status
  /// - Parameters:
  ///   - eligibilityStatus:
  ///   - consentStatus: Instance of `ConsentStatus`
  ///   - delegate: Class object to receive response
  func updateUserEligibilityConsentStatus(
    eligibilityStatus: Bool,
    consentStatus: ConsentStatus,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [
      kUserId: user.userId! as String,
      kUserAuthToken: user.authToken! as String,
    ]

    let currentConsent = ConsentBuilder.currentConsent
    let consentResult = currentConsent?.consentResult

    var consentVersion: String
    if let version = currentConsent?.version, !version.isEmpty {
      consentVersion = version
    } else {
      consentVersion = "1"
    }

    var userDataSharing: String
    if let isShareData = consentResult?.isShareDataWithPublic {
      userDataSharing = "\(isShareData)"
    } else {
      userDataSharing = "n/a"
    }
    let base64data =
      consentResult?.consentPdfData?
      .base64EncodedString() ?? ""

    let consent =
      [
        kConsentDocumentVersion: consentVersion,
        kStatus: consentStatus.rawValue,
        kConsentpdf: base64data,
      ] as [String: Any]

    let params =
      [
        kStudyId: Study.currentStudy?.studyId ?? "",
        kEligibility: eligibilityStatus,
        kConsent: consent,
        kConsentSharing: userDataSharing,
      ] as [String: Any]
    let method = ConsentServerMethods.updateEligibilityConsentStatus.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  /// Creates a request to get Consent pdf
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getConsentPDFForStudy(
    studyId: String,
    consentVersion: String,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate

    let user = User.currentUser
    let params = [
      kStudyId: studyId,
      "consentVersion": "",
    ]

    let headerParams = [kUserId: user.userId!]
    let method = ConsentServerMethods.consentDocument.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  // MARK: Parsers
  func handleUpdateTokenResponse(response: [String: Any]) {

    let headerParams =
      self.failedRequestServices.headerParams == nil
      ? [:] : self.failedRequestServices.headerParams
    self.sendRequestWith(
      method: self.failedRequestServices.method,
      params: (self.requestParams == nil ? nil : self.requestParams),
      headers: headerParams
    )

  }

  /// Sends Request
  /// - Parameters:
  ///   - method: instance of `Method`
  ///   - params: request params
  ///   - headers: request headers
  private func sendRequestWith(method: Method, params: [String: Any]?, headers: [String: String]?) {

    self.requestParams = params
    self.headerParams = headers
    self.method = method
    networkManager.composeRequest(
      ConsentServerConfiguration.configuration,
      method: method,
      params: params as NSDictionary?,
      headers: headers as NSDictionary?,
      delegate: self
    )
  }

}
extension ConsentServices: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    delegate?.startedRequest(manager, requestName: requestName)
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    switch requestName {
    case ConsentServerMethods.updateEligibilityConsentStatus.description as String: break
    case ConsentServerMethods.consentDocument.description as String: break
    case AuthServerMethods.getRefreshedToken.description as String:
      self.handleUpdateTokenResponse(response: (response as? [String: Any])!)
    default: break
    }

    delegate?.finishedRequest(manager, requestName: requestName, response: response)

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    if requestName as String == AuthServerMethods.getRefreshedToken.description && error.code == 401 {  // Session expired.
      delegate?.failedRequest(manager, requestName: requestName, error: error)
    } else if error.code == 401 {

      self.failedRequestServices.headerParams = self.headerParams
      self.failedRequestServices.requestParams = self.requestParams
      self.failedRequestServices.method = self.method

      if User.currentUser.refreshToken == ""
        && requestName as String
          != AuthServerMethods
          .login
          .description
      {
        // Unauthorized Access
        let errorInfo = ["NSLocalizedDescription": "Your Session is Expired"]
        let localError = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
        delegate?.failedRequest(manager, requestName: requestName, error: localError)
      } else {
        // Update Refresh Token
        AuthServices().updateToken(delegate: self)
      }
    } else {
      var errorInfo = error.userInfo
      var localError = error
      if error.code == 403 {
        errorInfo = ["NSLocalizedDescription": "Your Session is Expired"]
        localError = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
      }
      delegate?.failedRequest(manager, requestName: requestName, error: localError)
    }
  }
}
