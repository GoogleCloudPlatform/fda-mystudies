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
    let id = SessionService.instance.correlationID
    XCTAssertTrue(!id.isEmpty, "Correlation ID shouldn't be empty")
    SessionService.resetSession()
  }

  func testCodeChallenge() {
    let code = SessionService.instance.codeChallenge
    XCTAssertTrue(!code.isEmpty, "Code Challenge shouldn't be empty")
    SessionService.resetSession()
  }

}
