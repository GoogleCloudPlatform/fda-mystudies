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
import MessageUI
import UIKit
import WebKit

class WebViewController: UIViewController {

  // MARK: Outlets
  @IBOutlet weak var webView: WKWebView!
  @IBOutlet var barItemShare: UIBarButtonItem?

  static let headerString =
    """
    <header><meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, \
    minimum-scale=1.0, user-scalable=no'></header>
    """
  var activityIndicator: UIActivityIndicatorView!
  var requestLink: String?
  var pdfData: Data?
  var isEmailAvailable: Bool? = false
  var htmlString: String?

  var tempfileURL: URL?

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  override func viewDidLoad() {
    super.viewDidLoad()
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)

    // Used to add a loader
    activityIndicator = UIActivityIndicatorView(style: .gray)
    activityIndicator.center = CGPoint(x: self.view.frame.midX, y: self.view.frame.midY - 100)
    self.view.addSubview(activityIndicator)
    activityIndicator.startAnimating()

    if self.isEmailAvailable == false {
      barItemShare?.isEnabled = false
      self.navigationItem.rightBarButtonItem = nil
      barItemShare = nil
    }
    webView.navigationDelegate = self
    webView.contentScaleFactor = 1.0
    loadContentOnWebView()
    setNeedsStatusBarAppearanceUpdate()
  }

  override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    if let tempURL = self.tempfileURL {
      AKUtility.deleteFile(from: tempURL)
    }
  }

  final private func loadContentOnWebView() {

    if let requestLink = requestLink,
      !requestLink.isEmpty,
      let url = URL(string: requestLink)
    {
      let urlRequest = URLRequest(url: url)
      webView.load(urlRequest)
      self.navigationItem.rightBarButtonItems = []
    } else if let html = self.htmlString {
      webView.loadHTMLString(WebViewController.headerString + html, baseURL: nil)
    } else if let pdfData = pdfData {
      self.webView.load(
        pdfData,
        mimeType: "application/pdf",
        characterEncodingName: "UTF-8",
        baseURL: URL(fileURLWithPath: "")
      )
    } else {
      self.activityIndicator.stopAnimating()
      self.activityIndicator.removeFromSuperview()
    }
  }

  /// Dismiss ViewController
  @IBAction func cancelButtonClicked(_ sender: Any) {
    self.dismiss(animated: true, completion: nil)
  }

  @IBAction func buttonActionShare(_ sender: UIBarButtonItem) {
    self.shareDocument { [weak self] (status) in
      if !status {
        self?.view.makeToast(kResourceShareError)
      }
    }
  }

  /// Sends Consent Document by Email in pdf format
  func shareDocument(completion: @escaping (_ status: Bool) -> Void) {

    func attach(url: URL) {
      let items: [Any] = ["Document", url]
      let activityController = UIActivityViewController(activityItems: items, applicationActivities: nil)
      self.present(activityController, animated: true)
      completion(true)
    }

    func convertToTempPDF(data: Data) {
      ResourceDetailViewController.saveTempPdf(from: data, name: "Document") { (url) in
        if let url = url {
          attach(url: url)
        } else {
          completion(false)
        }
      }
    }

    if let tempURL = self.tempfileURL {
      attach(url: tempURL)
    } else if let pdfData = self.pdfData {
      convertToTempPDF(data: pdfData)
    } else if let resourceHTML = self.htmlString {
      let pdfData = self.webView.renderSelfToPdfData(htmlString: resourceHTML)
      convertToTempPDF(data: pdfData)
    } else {
      completion(false)
    }

  }

}

extension WebViewController: WKNavigationDelegate {

  func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
    self.activityIndicator.stopAnimating()
    self.activityIndicator.removeFromSuperview()
  }

  func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
    self.activityIndicator.stopAnimating()
    self.activityIndicator.removeFromSuperview()
    Logger.sharedInstance.error("\(error.localizedDescription)")
  }
}
