//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation
import XCTest
import Embassy
import EnvoyAmbassador

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
    continueAfterFailure = false
    setupWebApp()
    setupApp()
    mockWebServices()
  }

  /// Setup the Embassy web server for testing
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

  /// Set up XCUIApplication
  private func setupApp() {

    app = XCUIApplication()
    app.launchEnvironment["RESET_LOGIN"] = "1"
    app.launchArguments += ["UI-TESTING"]
    app.launchEnvironment["ENVOY_BASEURL"] = "http://localhost:\(port)"
  }

  /// Try to mock some of the default responses,
  /// can be overriden in the respective test cases later.
  func  mockWebServices() {}

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
