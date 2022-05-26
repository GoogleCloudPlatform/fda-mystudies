//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import CryptoSwift
import Foundation

class SessionService {

  /// A Struct represents the Audit data for services.
  struct Audit {
    enum JSONKey {
      static let correlationID = "correlationId"
      static let appID = "appId"
      static let platform = "mobilePlatform"
      static let source = "source"
      static let appVersion = "appVersion"
      static let userID = "userId"
      static let appName = "appName"
    }

    static var headers: StringDictionary {
      return [
        JSONKey.correlationID: SessionService.correlationID,
        JSONKey.appID: AppConfiguration.appID,
        JSONKey.platform: Utilities.currentDevicePlatform(),
        JSONKey.source: "MOBILE APPS",
        JSONKey.appVersion: Utilities.getAppVersion(),
        JSONKey.userID: User.currentUser.userId ?? "",
        JSONKey.appName: Utilities.appName(),
      ]
    }
  }

  private init() {}

  private static var currentSession: SessionService?

  static var instance: SessionService {
    guard let session = currentSession else {
      let session = SessionService()
      currentSession = session
      return session
    }
    return session
  }

  static func resetSession() {
    currentSession = nil
  }

  /// Current App Session ID
  static var correlationID: String = {
    return UUID().uuidString
  }()

  /// This is a cryptographically random string using the characters A-Z, a-z, 0-9,
  /// and the punctuation characters -._~ (hyphen, period, underscore, and tilde),
  ///  between 43 and 128 characters long.
  private(set) var codeVerifier: String = {
    return String.randomString(length: 50)
  }()

  /// BASE64-URL-encoded string of the SHA256 hash of the code verifier
  var codeChallenge: String {
    let data = Data(codeVerifier.utf8)
    let encryrpted = data.sha256()
    return encryrpted.base64EncodedString().base64ToBase64url()
  }
}
