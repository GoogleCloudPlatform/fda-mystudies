//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import UIKit

class NewProgressView: UIView {

  // MARK: - Outlets
  @IBOutlet var gifView: UIImageView!
  @IBOutlet var messageLbl: UILabel!

  class func instanceFromNib(frame: CGRect) -> NewProgressView? {
    let view =
      UINib(nibName: "NewProgressView", bundle: nil).instantiate(
        withOwner: nil,
        options: nil
      )[0] as? NewProgressView
    view?.frame = frame
    view?.layoutIfNeeded()
    return view
  }

  func showLoader(with message: String = "") {
    self.gifView.image = UIImage.gifImageWithName(kResourceName)
    self.messageLbl.isHidden = message.isEmpty
    self.messageLbl.text = message
  }

}
