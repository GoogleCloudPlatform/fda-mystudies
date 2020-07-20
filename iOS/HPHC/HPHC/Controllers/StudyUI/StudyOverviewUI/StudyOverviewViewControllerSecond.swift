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
import SDWebImage
import UIKit

class StudyOverviewViewControllerSecond: UIViewController {

  // MARK: - Outlets
  @IBOutlet var buttonJoinStudy: UIButton?

  @IBOutlet var buttonLearnMore: UIButton?
  @IBOutlet var buttonVisitWebsite: UIButton?
  @IBOutlet var labelTitle: UILabel?
  @IBOutlet var labelDescription: UILabel?
  @IBOutlet var imageViewStudy: UIImageView?

  // MARK: - Properties
  var pageIndex: Int!

  var overViewWebsiteLink: String?
  var overviewSectionDetail: OverviewSection!

  // MARK: - Viewcontroller lifecycle
  override func viewDidLoad() {
    super.viewDidLoad()

    // Used to set border color for bottom view
    buttonJoinStudy?.layer.borderColor = kUicolorForButtonBackground
    if overviewSectionDetail.imageURL != nil {
      let url = URL.init(string: overviewSectionDetail.imageURL!)
      imageViewStudy?.sd_setImage(with: url, placeholderImage: nil)
    }
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)

    labelTitle?.text = overviewSectionDetail.title

    var fontSize = 18.0
    if DeviceType.isIPad || DeviceType.isIPhone4OrLess {
      fontSize = 13.0

    } else if DeviceType.isIPhone5 {
      fontSize = 14.0
    }

    if let attrStr = try? NSAttributedString(
      data: (overviewSectionDetail.text?.data(
        using: String.Encoding.unicode,
        allowLossyConversion: true
      )!)!,
      options: [
        NSAttributedString.DocumentReadingOptionKey.documentType: NSAttributedString
          .DocumentType
          .html,
      ],
      documentAttributes: nil
    ) {

      let attributedText: NSMutableAttributedString = NSMutableAttributedString(
        attributedString: attrStr
      )
      attributedText.addAttributes(
        [
          NSAttributedString.Key.font: UIFont(
            name: "HelveticaNeue",
            size: CGFloat(fontSize)
          )!,
        ],
        range: (attrStr.string as NSString).range(of: attrStr.string)
      )
      attributedText.addAttribute(
        NSAttributedString.Key.foregroundColor,
        value: UIColor.black,
        range: (attrStr.string as NSString).range(of: attrStr.string)
      )

      if Utilities.isValidValue(someObject: attrStr.string as AnyObject?) {
        self.labelDescription?.attributedText = attributedText

      } else {
        self.labelDescription?.text = ""
      }
    }
    self.labelDescription?.textAlignment = .center
  }

  // MARK: - Button Actions 

  @IBAction func buttonActionJoinStudy(_ sender: Any) {

    if User.currentUser.userType == UserType.anonymousUser {
      let leftController =
        slideMenuController()?.leftViewController
        as! LeftMenuViewController
      leftController.changeViewController(.reachOutSignIn)
    } else {
      //TEMP
      UIUtilities.showAlertWithTitleAndMessage(
        title: NSLocalizedString(kAlertMessageText, comment: "") as NSString,
        message: NSLocalizedString(kAlertMessageReachoutText, comment: "") as NSString
      )
    }
  }

  @IBAction func visitWebsiteButtonAction(_ sender: UIButton) {

    let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
    let webViewController =
      loginStoryboard.instantiateViewController(
        withIdentifier: "WebViewController"
      ) as! UINavigationController
    let webView = webViewController.viewControllers[0] as! WebViewController

    if sender.tag == 1188 {
      // Visit Website
      webView.requestLink = overViewWebsiteLink

    } else {
      // View Consent
      webView.htmlString = (Study.currentStudy?.consentDocument?.htmlString)
    }

    self.navigationController?.present(webViewController, animated: true, completion: nil)
  }
}
