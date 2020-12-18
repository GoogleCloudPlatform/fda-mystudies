// License Agreement for FDA MyStudies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is
// hereby granted, free of charge, to any person obtaining a copy of this software and associated
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

protocol OtherTextChoiceCellDelegate: class {
  func didEndEditing(with text: String?)
}

class OtherTextChoiceCell: UITableViewCell {

  // MARK: - Outlets
  @IBOutlet weak var mainView: UIView!

  @IBOutlet weak var otherField: UITextField!
  @IBOutlet weak var titleLbl: UILabel!
  @IBOutlet weak var checkmarkView: UIImageView!
  @IBOutlet weak var detailedTextLbl: UILabel!
  @IBOutlet weak var otherView: UIView!
  @IBOutlet weak var otherViewHeightConstraint: NSLayoutConstraint!

  weak var delegate: OtherTextChoiceCellDelegate?

  var didSelected: Bool = false {
    didSet {
      if didSelected {
        self.titleLbl.textColor = #colorLiteral(
          red: 0.2431372549,
          green: 0.5411764706,
          blue: 0.9921568627,
          alpha: 1
        )
        self.checkmarkView.isHidden = false
      } else {
        self.titleLbl.textColor = #colorLiteral(red: 0, green: 0, blue: 0, alpha: 1)
        self.checkmarkView.isHidden = true
        self.otherField?.endEditing(true)
      }
    }
  }

  override func awakeFromNib() {
    super.awakeFromNib()
    self.mainView.clipsToBounds = true
    self.mainView.layer.cornerRadius = 10
    self.mainView.layer.maskedCorners = [.layerMinXMaxYCorner, .layerMaxXMaxYCorner]
    self.otherField?.delegate = self
  }

  func updateOtherView(isShow: Bool) {
    switch isShow {
    case true:
      self.otherViewHeightConstraint.constant = 70
    case false:
      self.otherViewHeightConstraint.constant = 0
    }
    self.layoutIfNeeded()
  }

}

extension OtherTextChoiceCell: UITextFieldDelegate {

  func textFieldShouldReturn(_ textField: UITextField) -> Bool {
    textField.endEditing(true)
    return true
  }

  func textFieldDidEndEditing(_ textField: UITextField) {
    delegate?.didEndEditing(with: textField.text)
  }

  func textField(
    _ textField: UITextField,
    shouldChangeCharactersIn range: NSRange,
    replacementString string: String
  ) -> Bool {
    return range.location < 250
  }
}
