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

class AppUpdateBlocker: UIView {

  @IBOutlet var buttonUpgrade: UIButton!
  @IBOutlet var buttonYes: UIButton!
  @IBOutlet var buttonSkip: UIButton!
  @IBOutlet var labelMessage: UILabel!
  @IBOutlet var labelVersionNumber: UILabel!
  @IBOutlet var appIconView: UIImageView!

  var appIcon: UIImage? {
    guard let iconsDictionary = Bundle.main.infoDictionary?["CFBundleIcons"] as? NSDictionary,
      let primaryIconsDictionary = iconsDictionary["CFBundlePrimaryIcon"] as? NSDictionary,
      let iconFiles = primaryIconsDictionary["CFBundleIconFiles"] as? NSArray,
      // First will be smallest for the device class, last will be the largest for device class
      let lastIcon = iconFiles.lastObject as? String,
      let icon = UIImage(named: lastIcon)
    else {
      return nil
    }
    return icon
  }

  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
    // Used to set border color for bottom view
    buttonUpgrade?.layer.borderColor = UIColor.white.cgColor
    
    if Bool(UserManageApps.appDetails?.isForceUpdate ?? "false") ?? false {
      buttonUpgrade?.isHidden = false
      buttonYes?.isHidden = true
      buttonSkip?.isHidden = true
      labelMessage?.text = kMandatoryForceUMessage
    } else {
      buttonUpgrade?.isHidden = true
      buttonYes?.isHidden = false
      buttonSkip?.isHidden = false
      labelMessage?.text = kOpionalForceUMessage
    }
  }

  /// Class Func to Initialize the `AppUpdateBlocker`
  ///
  /// - Parameters:
  ///   - frame: Rect for the view
  /// - Returns: New object of `AppUpdateBlocker` Type
  class func instanceFromNib(
    frame: CGRect,
    detail: JSONDictionary
  ) -> AppUpdateBlocker {
    let view =
      UINib(nibName: "AppUpdateBlocker", bundle: nil).instantiate(withOwner: nil, options: nil)[0] as! AppUpdateBlocker
    view.frame = frame
    view.layoutIfNeeded()
    if Bool(UserManageApps.appDetails?.isForceUpdate ?? "false") ?? false {
      view.buttonUpgrade?.isHidden = false
      view.buttonYes?.isHidden = true
      view.buttonSkip?.isHidden = true
      view.labelMessage?.text = kMandatoryForceUMessage
    } else {
      view.buttonUpgrade?.isHidden = true
      view.buttonYes?.isHidden = false
      view.buttonSkip?.isHidden = false
      view.labelMessage?.text = kOpionalForceUMessage
    }
    
    return view
  }

  func configureView(with latestVersion: String) {
    self.buttonUpgrade.layer.borderColor = #colorLiteral(red: 0, green: 0.4862745098, blue: 0.7294117647, alpha: 1)
    self.buttonYes.layer.borderColor = #colorLiteral(red: 0, green: 0.4862745098, blue: 0.7294117647, alpha: 1)
    self.buttonSkip.layer.borderColor = #colorLiteral(red: 0, green: 0.4862745098, blue: 0.7294117647, alpha: 1)
    self.appIconView.image = self.appIcon
  }

  // MARK: - Actions

  @IBAction func buttonUpgradeAction() {
    guard let appleID = Branding.appleID, !appleID.isEmpty else {
      // Ask user to update from AppStore.
      self.makeToast(LocalizableString.appStoreUpdateText.localizedString)
      return
    }
    let appStoreLink = "https://apps.apple.com/app/apple-store"
    let appLink = appStoreLink + "/id" + appleID
    if let url = URL(string: appLink), UIApplication.shared.canOpenURL(url) {
      UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }
  }
  
  @IBAction func buttonYesAction() {
    guard let appleID = Branding.appleID, !appleID.isEmpty else {
      // Ask user to update from AppStore.
      self.makeToast(LocalizableString.appStoreUpdateText.localizedString)
      return
    }
    let appStoreLink = "https://apps.apple.com/app/apple-store"
    let appLink = appStoreLink + "/id" + appleID
    if let url = URL(string: appLink), UIApplication.shared.canOpenURL(url) {
      UIApplication.shared.open(url, options: [:], completionHandler: nil)
    }
  }
  
  @IBAction func buttonSkipAction() {
    self.isHidden = true
    self.removeFromSuperview()
  }

}
