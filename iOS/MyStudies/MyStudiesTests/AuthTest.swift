//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Mockingjay
import XCTest

@testable import FDA_MyStudies

let delegateErrorMessage = "Delegate was not setup correctly. Missing XCTExpectation reference"

class LoginTest: XCTestCase {

  var delegate: UserServicesDelegate?

  override func setUp() {
    delegate = UserServicesDelegate()
  }

  override func tearDown() {
    delegate = nil
  }

  func testGrantCode() {
    guard let url = try? AuthRouter.codeGrant(params: [:], headers: [:]).asURLRequest().url,
      let urlStr = url?.absoluteString
    else {
      XCTFail()
      return
    }

    let responseDict: JSONDictionary = [
      User.JSONKey.accessToken: "accesstoken-12345",
      User.JSONKey.refreshToken: "FXm_WOcEddMDzumj",
      User.JSONKey.tokenType: "bearer",
    ]

    // Mock the response.
    stub(http(.post, uri: urlStr), json(responseDict))

    let expection = XCTestExpectation(description: "Code Grant Api Call")

    let code = "grant_code"  // Code from the callback URL.
    HydraAPI.grant(user: User.currentUser, with: code) { (status, _) in
      if status {
        expection.fulfill()
      } else {
        XCTFail()
      }
    }
    wait(for: [expection], timeout: 5.0)
    XCTAssertEqual(User.currentUser.refreshToken, responseDict[User.JSONKey.refreshToken] as? String)
    XCTAssertEqual(User.currentUser.authToken, "Bearer accesstoken-12345")
    User.resetCurrentUser()
  }

  func testCodeVerifier() {
    let verifier = SessionService.instance.codeVerifier
    let verifierCount = verifier.count
    XCTAssertTrue(
      verifierCount > 43 && verifierCount < 128,
      "Code verifier should be greater than 43 and less than 128"
    )
    SessionService.resetSession()
  }

  func testCorrelationId() {
    let id = SessionService.correlationID
    XCTAssertTrue(!id.isEmpty, "Correlation ID shouldn't be empty")
    SessionService.resetSession()
  }

  func testCodeChallenge() {
    let code = SessionService.instance.codeChallenge
    XCTAssertTrue(!code.isEmpty, "Code Challenge shouldn't be empty")
    SessionService.resetSession()
  }

  func testFailedRegistration() {

    let expection = expectation(description: "Registration Api Call")
    delegate?.asyncExpectation = expection

    let url = API.registrationURL + RegistrationMethods.register.rawValue

    let responseDict: JSONDictionary = [
      "code": 500,
      "message": "User Already registered",
    ]

    // Mock the response.
    stub(uri(url), json(responseDict, status: 500))

    guard let delegate = delegate else {
      XCTFail(delegateErrorMessage)
      return
    }

    let services = UserServices()
    services.delegate = delegate
    services.registerUser(delegate)

    waitForExpectations(timeout: 3.0) { (error) in

      if let error = error {
        XCTFail("waitForExpectationsWithTimeout errored: \(error)")
      }

      guard let result = self.delegate?.delegateAsyncResult else {
        XCTFail("Expected delegate to be called")
        return
      }
      let data = self.delegate?.apiResponse
      let error = self.delegate?.apiError

      XCTAssertEqual(error?.code, 500)
      XCTAssertEqual(error?.localizedDescription, "User Already registered")
      XCTAssertNotEqual(UserServicesDelegate.State.none, result)
      XCTAssertEqual(UserServicesDelegate.State.failed, result)
      XCTAssertNil(data)
    }
  }

  func testRefreshToken() {

    guard let url = try? AuthRouter.codeGrant(params: [:], headers: [:]).asURLRequest().url
    else { return XCTFail("Invalid Refresh Token URL") }

    let responseDict: JSONDictionary = [
      User.JSONKey.accessToken: "accesstoken-12345",
      User.JSONKey.refreshToken: "FXm_WOcEddMDzumj",
      User.JSONKey.tokenType: "bearer",
    ]

    guard let responseData = Utilities.dictionaryToData(value: responseDict)
    else { return XCTFail("Unable to convert Dictionary to Data") }

    // Mock the response.
    stub(http(.post, uri: url?.absoluteString ?? ""), jsonData(responseData))

    let expection = XCTestExpectation(description: "Refresh Token Api call")
    HydraAPI.refreshToken { (status, error) in
      if status {
        expection.fulfill()
      } else {
        XCTFail()
      }
    }
    wait(for: [expection], timeout: 5.0)
    XCTAssertEqual(User.currentUser.refreshToken, responseDict[User.JSONKey.refreshToken] as? String)
    XCTAssertEqual(User.currentUser.authToken, "Bearer accesstoken-12345")
    User.resetCurrentUser()
  }
  
}

class UserServicesDelegate: NMWebServiceDelegate {

  enum State: String {
    case none
    case start
    case finished
    case failed
  }

  var delegateAsyncResult: State = .none
  var apiResponse: [String: Any]?
  var apiError: NSError?
  var asyncExpectation: XCTestExpectation?

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    guard asyncExpectation != nil else {
      XCTFail(delegateErrorMessage)
      return
    }
    delegateAsyncResult = .start
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    guard let expectation = asyncExpectation else {
      XCTFail(delegateErrorMessage)
      return
    }
    apiError = error
    delegateAsyncResult = .failed
    expectation.fulfill()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    guard let expectation = asyncExpectation else {
      XCTFail(delegateErrorMessage)
      return
    }
    apiResponse = response as? [String: Any]
    delegateAsyncResult = .finished
    expectation.fulfill()
  }

}
