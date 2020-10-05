//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import CryptoSwift
import Foundation

class SessionService {

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
  private(set) var correlationID: String = {
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
