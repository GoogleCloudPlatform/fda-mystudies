//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import XCTest

class MyStudiesUITests: UITestBase {

  override func setUp() {
    super.setUp()
  }
  override func setUpWithError() throws {
    // In UI tests it is usually best to stop immediately when a failure occurs.
    continueAfterFailure = false
  }

  func testRegistrationFlow() {

    app.launch()

    app.buttons["New User?"].tap()

    let tablesQuery2 = app.tables
    let tablesQuery = tablesQuery2

    let emailField = tablesQuery.textFields["enter email"]
    emailField.tap()
    emailField.typeText("test@xyz.la")

    let passwordField = tablesQuery.secureTextFields["add password"]
    passwordField.tap()
    passwordField.typeText("Test@124")

    let confirmPasswordField = tablesQuery.secureTextFields["confirm password"]
    confirmPasswordField.tap()
    confirmPasswordField.typeText("Test@124")

    let doneButton = app.toolbars["Toolbar"].buttons["Done"]
    if doneButton.exists {
      doneButton.swipeUp()
    }

    app.tables.buttons["notChecked"].tap()

    app.buttons["Submit"].tap()

    let verificationStep = app.staticTexts["Verification Step"]
    wait(forElement: verificationStep, timeout: 5)
    XCTAssertTrue(verificationStep.exists)

    let verificationField = app.textFields.firstMatch
    verificationField.tap()
    verificationField.typeText("gysas#4x")

    app.toolbars["Toolbar"].buttons["Done"].tap()
    app.buttons["Continue"].tap()
  }

  func testChangePasswordForLoggedUser() {
    app.launchArguments += ["LOGGED_USER"]
    app.launch()

    let menuBtn = app.navigationBars.firstMatch.buttons["menu icn"]
    if menuBtn.exists {
      menuBtn.tap()
    } else {
      app.navigationBars.firstMatch.buttons.firstMatch.tap()
    }
    
    app.staticTexts["My Account"].tap()

    // Error alert
    app.alerts.buttons["OK"].tap()
    app.staticTexts["CHANGE PASSWORD"].tap()

    XCTAssertTrue(app.secureTextFields["enter current password"].exists,
                  "Current password field not found")
    XCTAssertTrue(app.secureTextFields["enter new password"].exists,
                  "New password field not found")
    XCTAssertTrue(app.secureTextFields["confirm password"].exists,
                  "Confirm password field not found")
  }

}

extension XCTestCase {
    func wait(forElement element: XCUIElement, timeout: TimeInterval) {
        let predicate = NSPredicate(format: "exists == 1")
        // This will make the test runner continously evalulate the
        // predicate, and wait until it matches.
        expectation(for: predicate, evaluatedWith: element)
        waitForExpectations(timeout: timeout)
    }
}
