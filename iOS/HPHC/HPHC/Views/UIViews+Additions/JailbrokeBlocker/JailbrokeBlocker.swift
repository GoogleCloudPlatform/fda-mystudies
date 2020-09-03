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

class JailbrokeBlocker: UIView {

  @IBOutlet var buttonUpgrade: UIButton!
  @IBOutlet var labelMessage: UILabel!
  @IBOutlet var labelVersionNumber: UILabel!

  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
    /// Used to set border color for bottom view
    buttonUpgrade?.layer.borderColor = UIColor.white.cgColor
  }

  /// Class func to Initialize the `JailbrokeBlocker` 
  ///
  /// - Parameters:
  ///   - frame: Rect for the view
  /// - Returns: New object of `JailbrokeBlocker` Type
  class func instanceFromNib(frame: CGRect) -> JailbrokeBlocker {

    let view =
      UINib(nibName: "JailbrokeBlocker", bundle: nil).instantiate(
        withOwner: nil,
        options: nil
      )[0] as! JailbrokeBlocker
    view.frame = frame
    view.layoutIfNeeded()
    return view

  }

  // MARK: - Action

  @IBAction func buttonUpgradeAction(_ sender: UIButton) {

    guard
      let url = URL(
        string: "https://itunes.apple.com/us/app/fda-mystudies/id1242835330?ls=1&mt=8"
      )
    else { return }
    if UIApplication.shared.canOpenURL(url) {
      UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }

  }

}
