//
//  UITestingHelpers.swift
//  SafePassageDriver
//
//  Created by Surender on 31/07/19.
//  Copyright © 2019 BTC. All rights reserved.
//

import Foundation

var isUITesting: Bool {
  return ProcessInfo.processInfo.arguments.contains("UI-TESTING")
}
var isTestingLoginLogoutFlow: Bool {
  return ProcessInfo.processInfo.arguments.contains("LOGIN_LOGOUT_FLOW")
}
