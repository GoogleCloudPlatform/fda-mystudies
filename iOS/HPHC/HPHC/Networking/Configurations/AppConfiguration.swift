//
//  Configuration.swift
//  HPHC
//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

private enum Configuration {
  enum Error: Swift.Error {
    case missingKey, invalidValue
  }
  
  static func value<T>(for key: String) throws -> T where T: LosslessStringConvertible {
    guard let object = Bundle.main.object(forInfoDictionaryKey:key) else {
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
  
  static private let baseProtocol = "https://"
  
  static var wcpURL: String {
    return baseProtocol + ((try? Configuration.value(for: "WCP_URL")) ?? "")
  }
  
  static var responseURL: String {
    return baseProtocol + ((try? Configuration.value(for: "RESPONSE_URL")) ?? "")
  }
  
  static var registrationURL: String {
    return baseProtocol + ((try? Configuration.value(for: "REGISTRATION_URL")) ?? "")
  }
 
  static var authUsername: String {
    return baseProtocol + ((try? Configuration.value(for: "AUTH_USERNAME")) ?? "")
  }
  
  static var authPassword: String {
    return baseProtocol + ((try? Configuration.value(for: "AUTH_PASSWORD")) ?? "")
  }
  
}
