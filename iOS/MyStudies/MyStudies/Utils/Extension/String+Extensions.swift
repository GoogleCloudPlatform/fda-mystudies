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

extension String {
  var htmlToAttributedString: NSAttributedString? {
    do {
      guard let data = data(using: .utf8) else {
        return nil
      }
      return try NSAttributedString(
        data: data,
        options: [
          .documentType: NSAttributedString.DocumentType.html,
          .characterEncoding: String.Encoding.utf8.rawValue,
        ],
        documentAttributes: nil
      )
    } catch {
      return nil
    }
  }
  var htmlToString: String {
    return htmlToAttributedString?.string ?? ""
  }
}

// Mapping from XML/HTML character entity reference to character
// From http://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
private let characterEntities: [Substring: Character] = [
  // XML predefined entities:
  "&quot;": "\"",
  "&amp;": "&",
  "&apos;": "'",
  "&lt;": "<",
  "&gt;": ">",
  "&#34;": "'",
  // HTML character entity references:
  "&nbsp;": "\u{00a0}",
  // ...
  "&diams;": "♦",
]

extension String {

  /// Returns a new string made by replacing in the `String`
  /// all HTML character entity references with the corresponding
  /// character.
  var stringByDecodingHTMLEntities: String {

    // ===== Utility functions =====

    // Convert the number in the string to the corresponding
    // Unicode character, e.g.
    //    decodeNumeric("64", 10)   --> "@"
    //    decodeNumeric("20ac", 16) --> "€"
    func decodeNumeric(_ string: Substring, base: Int) -> Character? {
      guard let code = UInt32(string, radix: base),
        let uniScalar = UnicodeScalar(code)
      else { return nil }
      return Character(uniScalar)
    }

    // Decode the HTML character entity to the corresponding
    // Unicode character, return `nil` for invalid input.
    //     decode("&#64;")    --> "@"
    //     decode("&#x20ac;") --> "€"
    //     decode("&lt;")     --> "<"
    //     decode("&foo;")    --> nil
    func decode(_ entity: Substring) -> Character? {

      if entity.hasPrefix("&#x") || entity.hasPrefix("&#X") {
        return decodeNumeric(entity.dropFirst(3).dropLast(), base: 16)
      } else if entity.hasPrefix("&#") {
        return decodeNumeric(entity.dropFirst(2).dropLast(), base: 10)
      } else {
        return characterEntities[entity]
      }
    }

    // ===== Method starts here =====

    var result = ""
    var position = startIndex

    // Find the next '&' and copy the characters preceding it to `result`:
    while let ampRange = self[position...].range(of: "&") {
      result.append(contentsOf: self[position..<ampRange.lowerBound])
      position = ampRange.lowerBound

      // Find the next ';' and copy everything from '&' to ';' into `entity`
      guard let semiRange = self[position...].range(of: ";") else {
        // No matching ';'.
        break
      }
      let entity = self[position..<semiRange.upperBound]
      position = semiRange.upperBound

      if let decoded = decode(entity) {
        // Replace by decoded character:
        result.append(decoded)
      } else {
        // Invalid entity, copy verbatim:
        result.append(contentsOf: entity)
      }
    }
    // Copy remaining characters to `result`:
    result.append(contentsOf: self[position...])
    return result
  }
}

extension NSAttributedString {
  var attributedString2Html: String? {
    do {
      let htmlData = try self.data(from: NSRange(location: 0, length: self.length),
                                   documentAttributes:[.documentType: NSAttributedString.DocumentType.html])
      return String.init(data: htmlData, encoding: String.Encoding.utf8)
    } catch {
      print("error:", error)
      return nil
    }
  }
}
