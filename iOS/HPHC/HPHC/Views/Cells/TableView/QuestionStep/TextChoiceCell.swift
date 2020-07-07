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

/// Customer `UITableViewCell` similar to `ORKTextChoiceCell`
class TextChoiceCell: UITableViewCell {

  // MARK: - Outlets
  @IBOutlet weak var titleLbl: UILabel!

  @IBOutlet weak var checkmarkView: UIImageView!
  @IBOutlet weak var detailedTextLbl: UILabel!

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
      }
    }
  }

  override func setSelected(_ selected: Bool, animated: Bool) {
    super.setSelected(selected, animated: animated)
    self.didSelected = selected
  }

}
