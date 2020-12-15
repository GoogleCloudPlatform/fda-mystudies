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

import AVKit
import Foundation
import MediaPlayer
import ResearchKit
import SDWebImage
import UIKit

class StudyOverviewViewControllerFirst: UIViewController {

  // MARK: - Outlets
  @IBOutlet var buttonJoinStudy: UIButton?

  @IBOutlet var buttonWatchVideo: UIButton?
  @IBOutlet var buttonVisitWebsite: UIButton?
  @IBOutlet var labelTitle: UILabel?
  @IBOutlet var labelDescription: UILabel?
  @IBOutlet var imageViewStudy: UIImageView?

  // MARK: - Properties
  var pageIndex: Int!

  var overViewWebsiteLink: String?
  var overviewSectionDetail: OverviewSection!
  var playerViewController: AVPlayerViewController!

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .lightContent
  }

  // MARK: - Viewcontroller Lifecycle
  override func viewDidLoad() {
    super.viewDidLoad()

    // Used to set border color for bottom view
    buttonJoinStudy?.layer.borderColor = kUicolorForButtonBackground
    loadOverviewImage()
    if overviewSectionDetail.link != nil {
      buttonWatchVideo?.isHidden = false
    } else {
      buttonWatchVideo?.isHidden = true
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
          .html
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
          )!
        ],
        range: (attrStr.string as NSString).range(of: attrStr.string)
      )
      attributedText.addAttribute(
        NSAttributedString.Key.foregroundColor,
        value: UIColor.white,
        range: (attrStr.string as NSString).range(of: attrStr.string)
      )

      if Utilities.isValidValue(someObject: attrStr.string as AnyObject?) {
        self.labelDescription?.attributedText = attributedText
      } else {
        self.labelDescription?.text = ""
      }
      self.labelDescription?.textAlignment = .center
    }

    setNeedsStatusBarAppearanceUpdate()
  }

  private func loadOverviewImage() {
    if let imageURLString = overviewSectionDetail.imageURL {
      let url = URL(string: imageURLString)
      imageViewStudy?.sd_imageIndicator = SDWebImageActivityIndicator.whiteLarge
      imageViewStudy?.sd_setImage(
        with: url,
        placeholderImage: nil,
        options: .highPriority,
        completed: { [weak self] (image, _, _, _) in
          self?.imageViewStudy?.sd_imageIndicator?.stopAnimatingIndicator()
          self?.imageViewStudy?.image = image
        }
      )
    }
  }

  @objc func playerDidFinishPlaying(note: NSNotification) {
    NotificationCenter.default.removeObserver(
      self,
      name: NSNotification.Name.AVPlayerItemDidPlayToEndTime,
      object: nil
    )
    self.playerViewController.dismiss(animated: true, completion: nil)
  }

  // MARK: - Button Actions

  @IBAction func watchVideoButtonAction(_ sender: Any) {

    guard let urlString = overviewSectionDetail.link,
      let url = URL(string: urlString)
    else { return }

    let extenstion = url.pathExtension

    if extenstion.count == 0 {
      UIApplication.shared.open(url, options: [:], completionHandler: nil)
    } else {
      try? AVAudioSession.sharedInstance().setMode(.moviePlayback)
      let player = AVPlayer(url: url)

      NotificationCenter.default.addObserver(
        self,
        selector: #selector(StudyOverviewViewControllerFirst.playerDidFinishPlaying(note:)),
        name: NSNotification.Name.AVPlayerItemDidPlayToEndTime,
        object: player.currentItem
      )
      playerViewController = AVPlayerViewController()
      playerViewController.player = player
      self.present(playerViewController, animated: true) {
        self.playerViewController.player!.play()
      }
    }
  }

  @IBAction func buttonActionJoinStudy(_ sender: Any) {

    if User.currentUser.userType == UserType.anonymousUser {
      let leftController =
        slideMenuController()?.leftViewController
        as! LeftMenuViewController
      leftController.changeViewController(.reachOutSignIn)
    }
  }

  @IBAction func visitWebsiteButtonAction(_ sender: Any) {

    if overViewWebsiteLink != nil {

      let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
      let webViewController =
        loginStoryboard.instantiateViewController(
          withIdentifier: "WebViewController"
        ) as! UINavigationController
      let webView = webViewController.viewControllers[0] as! WebViewController

      webView.requestLink = overViewWebsiteLink!
      self.navigationController?.present(webViewController, animated: true, completion: nil)
    }
  }
}

// MARK: - Webservice Delegates
extension StudyOverviewViewControllerFirst: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    self.addProgressIndicator()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    self.removeProgressIndicator()
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    self.removeProgressIndicator()
  }
}
