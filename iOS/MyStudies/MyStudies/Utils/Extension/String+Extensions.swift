// License Agreement for FDA MyStudies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
// Copyright 2020 Google LLC
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
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

import UIKit

extension String {

  func toBase64() -> String {
    return Data(self.utf8).base64EncodedString()
  }

  func containsIgnoringCase(_ find: String) -> Bool {
    return self.range(of: find, options: .caseInsensitive, range: nil, locale: nil) != nil
  }
}

extension String {
  var isAlphanumeric: Bool {

    if Int(self) != nil {
      return false
    } else if !self.isEmpty && range(of: "[^a-zA-Z]", options: .regularExpression) == nil {
      return true
    } else {
      return !isEmpty && range(of: "[^a-zA-Z0-9]", options: .regularExpression) == nil
    }
  }

  static func randomString(length: Int) -> String {
    let c = Array("abcdefghjklmnpqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345789")
    let k = c.count

    var result = [Character](repeating: "-", count: length)

    for i in 0..<length {
      let r = Int.random(in: 0...(k - 1))
      result[i] = c[r]
    }

    return String(result)
  }

  func base64ToBase64url() -> String {
    return
      self
      .replacingOccurrences(of: "+", with: "-")
      .replacingOccurrences(of: "/", with: "_")
      .replacingOccurrences(of: "=", with: "")
  }

}

extension String {
  func estimatedLabelHeight(labelWidth: CGFloat, font: UIFont) -> CGFloat {

    let size = CGSize(width: labelWidth, height: 1000)
    let options = NSStringDrawingOptions.usesFontLeading.union(.usesLineFragmentOrigin)
    let attributes = [NSAttributedString.Key.font: font]
    let rectangleHeight = self.boundingRect(
      with: size,
      options: options,
      attributes: attributes,
      context: nil
    ).height
    return rectangleHeight
  }
}
