//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import XCTest

@testable import FDA_MyStudies

class CustomInstructionStepTest: XCTestCase {

  func testRichTextString() {
    let richText =
    """
    &lt;div&gt;&lt;p&gt;&lt;span style=&quot;font-weight: \
    700;&quot;&gt;&lt;i&gt;Instruction \
    Test&lt;/i&gt;&lt;/span&gt;&lt;/p&gt;
    """
    let regex = "<[^>]+>"
    XCTAssertNotNil(richText.stringByDecodingHTMLEntities.range(of: regex, options: .regularExpression))
  }
  
  func testAttributedText() {
    let richText =
    """
    &lt;div&gt;&lt;p&gt;&lt;span style=&quot;font-weight: \
    700;&quot;&gt;&lt;i&gt;Instruction \
    Test&lt;/i&gt;&lt;/span&gt;&lt;/p&gt;
    """
    
    let attributedText = richText.stringByDecodingHTMLEntities.htmlToAttributedString
    
    XCTAssertNotNil(attributedText)
  }
  
  func testNormalTextString() {
    let richText =
    """
    Some intructions.
    """
    let regex = "<[^>]+>"
    XCTAssertNil(richText.range(of: regex, options: .regularExpression))
  }

}
