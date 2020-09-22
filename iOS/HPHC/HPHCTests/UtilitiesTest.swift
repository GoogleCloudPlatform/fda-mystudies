//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import XCTest
@testable import FDA_MyStudies

class UtilitiesTest: XCTestCase {
  
  func testDictionaryToData() {
    let dict: JSONDictionary = ["key1" : "value1",
                                "key2" : "value2"
    ]
    
    guard let dictToData = Utilities.dictionaryToData(value: dict)
      else { return XCTFail()}
    
    let expection = XCTestExpectation(description: "Dictionary To JSONData")
    
    if let authDict = dictToData.toJSONDictionary() {
      let valueCheck = authDict["key1"] as? String ?? ""
      XCTAssertEqual(valueCheck, "value1")
      expection.fulfill()
    }
    else {
      XCTFail()
    }
  }
  
  func testRandomString() {
    let randomString = Utilities.randomString(length: 12)
    
    let expection = XCTestExpectation(description: "Random String Length Verified")

    // Result lenght
    let resultLenght = randomString.count
    XCTAssertEqual(resultLenght, 12)
    
    let allowedCharacters = CharacterSet(charactersIn:"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789")
    let characterSet = CharacterSet(charactersIn: randomString)
    
    // Allowed characters
    let inCharRange = allowedCharacters.isSuperset(of: characterSet)
    XCTAssertEqual(inCharRange, true)
    
    expection.fulfill()
  }
  
  func testBase64ToBase64url() {
    let testString = "HG++thTs//"
    let resultString = testString.base64ToBase64url()
    XCTAssertEqual(resultString, "HG--thTs__")
  }
  
}
