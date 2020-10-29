//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import XCTest

class MyStudiesUITests: XCTestCase {

  override func setUpWithError() throws {
    // In UI tests it is usually best to stop immediately when a failure occurs.
    continueAfterFailure = false
  }

  func testLaunchPerformance() throws {
    if #available(macOS 10.15, iOS 13.0, tvOS 13.0, *) {
      // This measures how long it takes to launch your application.
      measure(metrics: [XCTOSSignpostMetric.applicationLaunch]) {
        XCUIApplication().launch()
      }
    }
  }

  func testRegistrationFlow() {

    let app = XCUIApplication()
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

    app.tables.buttons["notChecked"].tap()

    app.toolbars["Toolbar"].buttons["Done"].tap()
    app.buttons["Submit"].tap()

  }

}
