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

import Foundation
import UIKit

enum TextFieldTags: Int {
  case emailId = 0
  case password
  case confirmPassword
}

class SignUpTableViewCell: UITableViewCell {

  // MARK: - Outlets
  @IBOutlet var labelType: UILabel?

  @IBOutlet var textFieldValue: UITextField?
  @IBOutlet var buttonChangePassword: UIButton?  // this button will be extensively used for profile screen

  /// To update Cell UI.
  /// - Parameters:
  ///   - data: Access the data from Dictionary.
  ///   - securedText: A Boolean value indicating textField secured or not.
  ///   - keyboardType: Type of Keyboard for textfield.
  func populateCellData(data: NSDictionary, securedText: Bool, keyboardType: UIKeyboardType?) {

    textFieldValue?.isSecureTextEntry = false
    if securedText == true {
      textFieldValue?.isSecureTextEntry = true
    }

    labelType?.text = NSLocalizedString((data["helpText"] as? String)!, comment: "")
    textFieldValue?.placeholder = NSLocalizedString(
      (data["placeHolder"] as? String)!,
      comment: ""
    )

    if keyboardType == nil {
      textFieldValue?.keyboardType = .default
    } else {
      textFieldValue?.keyboardType = keyboardType!
    }
  }

  /// Set cell UI from User Instance  (for Profile Class)/
  func setCellData(tag: TextFieldTags) {
    let user = User.currentUser
    switch tag {

    case .emailId:
      if Utilities.isValidValue(someObject: user.emailId as AnyObject?) {
        self.textFieldValue?.text = user.emailId
      } else {
        self.textFieldValue?.text = ""
      }

    case .password:
      if Utilities.isValidValue(someObject: user.password as AnyObject?) {
        self.textFieldValue?.text = user.password
      } else {
        self.textFieldValue?.text = ""
      }

    default: break

    }
  }
}
