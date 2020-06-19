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

class ResourcesTableViewCell: UITableViewCell {

  // MARK: - Outlets
  @IBOutlet var labelTitle: UILabel?
  @IBOutlet var subtextLbl: UILabel!

  override func awakeFromNib() {
    super.awakeFromNib()
    self.subtextLbl.isHidden = true
    self.labelTitle?.text = ""
  }

  /// Updates the cell UI.
  /// - Parameters:
  ///   - data: Title for the cell.
  ///   - subTitle: SubTitle for the cell.
  func populateCellData(data: String, subTitle: String = "") {
    labelTitle?.text = data
    if !subTitle.isEmpty {
      subtextLbl.isHidden = false
      subtextLbl.text = subTitle
    }
  }

  func animateAvailability(for resource: Resource) {

    if resource.availableToday {

      self.contentView.backgroundColor = UIColor.lightGray
      self.contentView.alpha = 0.6
      UIView.animate(
        withDuration: 0.4,
        delay: 0,
        options: [.curveEaseInOut, .repeat, .autoreverse],
        animations: {
          self.contentView.alpha = 0.1
        }
      ) { (_) in
        self.contentView.backgroundColor = UIColor.white
        self.contentView.alpha = 1.0
        resource.availableToday = false
      }

      DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
        self.contentView.layer.removeAllAnimations()
      }

    }
  }

}
