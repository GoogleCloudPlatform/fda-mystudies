//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Mockingjay
import XCTest

@testable import FDA_MyStudies

class LoginTest: XCTestCase {

  func testGrantCode() {
    guard let url = try? AuthRouter.codeGrant(params: [:], headers: [:]).asURLRequest().url
    else { return XCTFail() }

    let responseDict: JSONDictionary = [
      User.JSONKey.accessToken: "accesstoken-12345",
      User.JSONKey.refreshToken: "FXm_WOcEddMDzumj",
      User.JSONKey.tokenType: "bearer",
    ]

    guard let responseData = Utilities.dictionaryToData(value: responseDict)
    else { return XCTFail() }

    // Mock the response.
    stub(http(.post, uri: url?.absoluteString ?? ""), jsonData(responseData))
    let expection = XCTestExpectation(description: "Api call")

    let router = AuthRouter.codeGrant(params: [:], headers: [:])

    APIService.instance.requestForData(with: router) { (data, status, error) in
      if let authDict = data?.toJSONDictionary() {
        User.currentUser.authenticate(with: authDict)
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
