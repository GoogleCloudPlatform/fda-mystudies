//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

enum Configuration {
  enum Error: Swift.Error {
    case missingKey, invalidValue
  }

  static func value<T>(for key: String) throws -> T where T: LosslessStringConvertible {
    guard let object = Bundle.main.object(forInfoDictionaryKey: key) else {
      throw Error.missingKey
    }

    switch object {
    case let value as T:
      return value
    case let string as String:
      guard let value = T(string) else { fallthrough }
      return value
    default:
      throw Error.invalidValue
    }
  }
}

enum API {

  private enum AppProtocol {
    private static let http = "http://"
    private static let https = "https://"
    static var value: String {
      #if DEBUG
        return https
      #else
        return https
      #endif
    }
  }

  static var studyDataStoreURL: String {
    return AppProtocol.value + ((try? Configuration.value(for: "STUDY_DATASTORE_URL")) ?? "")
  }

  static var responseURL: String {
    return AppProtocol.value + ((try? Configuration.value(for: "RESPONSE_DATASTORE_URL")) ?? "")
  }

  static var registrationURL: String {
    return AppProtocol.value + ((try? Configuration.value(for: "USER_DATASTORE_URL")) ?? "")
  }

  static var authURL: String {
    return AppProtocol.value + ((try? Configuration.value(for: "AUTH_URL")) ?? "")
  }

  static var hydraURL: String {
    return AppProtocol.value + ((try? Configuration.value(for: "HYDRA_BASE_URL")) ?? "")
  }
  
  static var enrollmentURL: String {
    return AppProtocol.value + ((try? Configuration.value(for: "ENROLLMENT_DATASTORE_URL")) ?? "")
  }

  static var consentMgmtURL: String {
    return AppProtocol.value + ((try? Configuration.value(for: "CONSENT_DATASTORE_URL")) ?? "")
  }

  static var authUsername: String {
    return (try? Configuration.value(for: "AUTH_USERNAME")) ?? ""
  }

  static var authPassword: String {
    return (try? Configuration.value(for: "AUTH_PASSWORD")) ?? ""
  }

  static var authClientID: String {
    return (try? Configuration.value(for: "HYDRA_CLIENT_ID")) ?? ""
  }
}
