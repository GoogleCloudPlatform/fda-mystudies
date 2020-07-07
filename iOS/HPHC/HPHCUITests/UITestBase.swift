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
import Foundation
import XCTest

class UITestBase: XCTestCase {
  let port = 8080
  var router: Router!
  var eventLoop: EventLoop!
  var server: HTTPServer!
  var app: XCUIApplication!

  var eventLoopThreadCondition: NSCondition!
  var eventLoopThread: Thread!

  override func setUp() {
    super.setUp()
    setupWebApp()
    setupApp()
  }

  // setup the Embassy web server for testing
  private func setupWebApp() {
    eventLoop = try! SelectorEventLoop(selector: try! KqueueSelector())
    router = DefaultRouter()
    server = DefaultHTTPServer(eventLoop: eventLoop, port: 8080, app: router.app)

    // Start HTTP server to listen on the port
    try! server.start()

    eventLoopThreadCondition = NSCondition()
    eventLoopThread = Thread(target: self, selector: #selector(runEventLoop), object: nil)
    eventLoopThread.start()
  }

  // set up XCUIApplication
  private func setupApp() {
    app = XCUIApplication()
    app.launchEnvironment["RESET_LOGIN"] = "1"
    app.launchArguments += ["UI-TESTING"]
    app.launchEnvironment["ENVOY_BASEURL"] = "http://localhost:\(port)"
  }

  override func tearDown() {
    super.tearDown()
    app.terminate()
    server.stopAndWait()
    eventLoopThreadCondition.lock()
    eventLoop.stop()
    while eventLoop.running {
      if !eventLoopThreadCondition.wait(until: NSDate().addingTimeInterval(10) as Date) {
        fatalError("Join eventLoopThread timeout")
      }
    }
  }

  @objc private func runEventLoop() {
    eventLoop.runForever()
  }
}
