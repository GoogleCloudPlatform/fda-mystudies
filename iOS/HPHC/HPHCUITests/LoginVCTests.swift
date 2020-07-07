// License Agreement for FDA MyStudies
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

import Embassy
import EnvoyAmbassador
import XCTest

class LoginVCTests: UITestBase {

  func testLoginVCFlows() {

    app.launch()
    app.buttons["Sign in"].tap()
    app.buttons["Forgot password?"].tap()
    app.navigationBars["PASSWORD HELP"].buttons["backIcon"].tap()

    let textViewsQuery = app.textViews
    textViewsQuery.links["Terms"].tap()
    app.navigationBars["TERMS"].buttons["Cancel"].tap()
    textViewsQuery.links["Privacy Policy"].tap()
    app.navigationBars["PRIVACY POLICY"].buttons["Cancel"].tap()
    app.navigationBars["SIGN IN"].buttons["info"].tap()
    app.alerts["Why Register?"].buttons["OK"].tap()
    app.buttons["New User? Sign Up"].tap()
    app.navigationBars["SIGN UP"].buttons["backIcon"].tap()
    let butttonSignin = app.buttons["Sign In"]
    XCTAssertTrue(butttonSignin.exists)

  }

  func testLogin() {

    router[DefaultRouter.loginPath] = JSONResponse { _ -> Any in
      return [
        "auth": "573973581",
        "verified": false,
        "message": "success",
        "userId": "b081ad2d-aff4-4f95-9f2c-e7b8541f3431",
        "refreshToken": "54914753-89eb-4bfd-af07-6a07ba4065e6",
      ]
    }

    app.launch()

    app.buttons["Sign in"].tap()
    let tfEmail = app.textFields["enter email"]
    let passwordtf = app.secureTextFields["enter password"]
    let butttonSignin = app.buttons["Sign In"]
    let toolbarBttonDone = app.toolbars["Toolbar"].buttons["Done"]

    tfEmail.tap()
    tfEmail.typeText("suri@grr.la")

    passwordtf.tap()
    passwordtf.typeText("Password@1")
    toolbarBttonDone.tap()
    butttonSignin.tap()

    XCTAssertFalse(butttonSignin.exists)

  }

}
extension XCUIElement {
  func clearText(andReplaceWith newText: String? = nil) {
    tap()
    tap()  // When there is some text, its parts can be selected on the first tap, the second tap clears the selection
    press(forDuration: 1.0)
    let selectAll = XCUIApplication().menuItems["Select All"]
    // For empty fields there will be no "Select All", so we need to check
    if selectAll.waitForExistence(timeout: 0.5), selectAll.exists {
      selectAll.tap()
      typeText(String(XCUIKeyboardKey.delete.rawValue))
    }
    if let newVal = newText { typeText(newVal) }
  }
}
