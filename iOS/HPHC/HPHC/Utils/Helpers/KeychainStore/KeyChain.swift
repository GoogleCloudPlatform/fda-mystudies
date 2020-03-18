//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

let kUserAuthTokenKeychainKey = "authTokenKeychain"
let kUserRefreshTokenKeychainKey = "refreshTokenKeychain"
let kRealmEncryptionKeychainKey = "realmEncryptionKeychain"
let kRealmEncryptionDefaultKey = "tatu8uopi8qwec7"
let kEncryptionKey = "EncryptionKey"
let kEncryptionIV = "EncryptionIV"

open class FDAKeychain {

  open var loggingEnabled = false

  private init() {}
  public static let shared = FDAKeychain()

  open subscript(key: String) -> String? {
    get {
      return load(withKey: key)
    }
    set {
      DispatchQueue.global().sync(flags: .barrier) {
        self.save(newValue, forKey: key)
      }
    }
  }

  private func save(_ string: String?, forKey key: String) {
    let query = keychainQuery(withKey: key)
    let objectData: Data? = string?.data(using: .utf8, allowLossyConversion: false)

    if SecItemCopyMatching(query, nil) == noErr {
      if let dictData = objectData {
        _ = SecItemUpdate(query, NSDictionary(dictionary: [kSecValueData: dictData]))
      } else {
        _ = SecItemDelete(query)
      }
    } else {
      if let dictData = objectData {
        query.setValue(dictData, forKey: kSecValueData as String)
        _ = SecItemAdd(query, nil)
      }
    }
  }

  private func load(withKey key: String) -> String? {
    let query = keychainQuery(withKey: key)
    query.setValue(kCFBooleanTrue, forKey: kSecReturnData as String)
    query.setValue(kCFBooleanTrue, forKey: kSecReturnAttributes as String)

    var result: CFTypeRef?
    let status = SecItemCopyMatching(query, &result)

    guard
      let resultsDict = result as? NSDictionary,
      let resultsData = resultsDict.value(forKey: kSecValueData as String) as? Data,
      status == noErr
    else {
      return nil
    }
    return String(data: resultsData, encoding: .utf8)
  }

  private func keychainQuery(withKey key: String) -> NSMutableDictionary {
    let result = NSMutableDictionary()
    result.setValue(kSecClassGenericPassword, forKey: kSecClass as String)
    result.setValue(key, forKey: kSecAttrService as String)
    result.setValue(kSecAttrAccessibleAlwaysThisDeviceOnly, forKey: kSecAttrAccessible as String)
    return result
  }

  private func logPrint(_ items: Any...) {
    if loggingEnabled {
      Logger.sharedInstance.info(items)
    }
  }
}
