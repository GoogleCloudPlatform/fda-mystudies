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

import MessageUI
import UIKit
import WebKit

let resourcesDownloadPath = AKUtility.baseFilePath + "/Resources"

class GatewayResourceDetailViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet weak var webView: WKWebView!
  @IBOutlet weak var progressBar: UIProgressView?

  // MARK: - Properties
  var activityIndicator: UIActivityIndicatorView!
  var requestLink: String?
  var type: String?
  var htmlString: String?
  var resource: Resource?
  var isEmailComposerPresented: Bool?

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  // MARK: - UIViewController LifeCycle
  override func viewDidLoad() {
    super.viewDidLoad()
    self.hidesBottomBarWhenPushed = true
    self.addBackBarButton()
    self.isEmailComposerPresented = false
    self.title = resource?.title
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)

    if self.isEmailComposerPresented == false {

      if self.resource?.file?.link != nil {

        activityIndicator = UIActivityIndicatorView(style: .gray)
        activityIndicator.center = CGPoint(
          x: self.view.frame.midX,
          y: self.view.frame.midY - 100
        )

        self.view.addSubview(activityIndicator)

        activityIndicator.startAnimating()

        if self.resource?.file?.mimeType == .pdf {

          if self.resource?.file?.localPath != nil {

            if self.resource?.file?.localPath == "BundlePath" {

              let path = Bundle.main.path(
                forResource: self.resource?.file?.link!,
                ofType: ".pdf"
              )
              self.loadWebViewWithPath(path: path!)
            } else {
              let path = resourcesDownloadPath + "/" + (self.resource?.file?.localPath)!
              let pdfData = FileDownloadManager.decrytFile(pathURL: URL(string: path))
              self.loadWebViewWithData(data: pdfData!)

            }
          } else {
            self.startDownloadingfile()
          }
        } else {
          webView?.loadHTMLString(self.requestLink!, baseURL: nil)
        }
      } else {

      }
      webView.navigationDelegate = self
      webView.contentScaleFactor = 1.0
    }

  }

  func loadWebViewWithPath(path: String) {
    let url = URL(fileURLWithPath: path)
    let urlRequest = URLRequest(url: url)
    webView.load(urlRequest)
  }

  func loadWebViewWithData(data: Data) {
    self.webView.load(
      data,
      mimeType: "application/pdf",
      characterEncodingName: "UTF-8",
      baseURL: URL(fileURLWithPath: "")
    )
  }

  func startDownloadingfile() {

    if !FileManager.default.fileExists(atPath: resourcesDownloadPath) {
      try? FileManager.default.createDirectory(
        atPath: resourcesDownloadPath,
        withIntermediateDirectories: true,
        attributes: nil
      )
    }

    let fileURL = (self.resource?.file?.link)!
    let url = URL(string: fileURL)
    var fileName: NSString = url!.lastPathComponent as NSString

    fileName = AKUtility.getUniqueFileNameWithPath(
      (resourcesDownloadPath as NSString).appendingPathComponent(fileName as String)
        as NSString
    )

    let fdm = FileDownloadManager()
    fdm.delegate = self
    guard let encodedUrl = fileURL.addingPercentEncoding(withAllowedCharacters: .urlHostAllowed)
    else {
      return
    }

    fdm.downloadFile(
      fileName as String,
      fileURL: encodedUrl,
      destinationPath: resourcesDownloadPath
    )
  }

  // MARK: Button Actions

  @IBAction func cancelButtonClicked(_ sender: Any) {
    self.dismiss(animated: true, completion: nil)
  }

  @IBAction func buttonActionForward(_ sender: UIBarButtonItem) {
    self.shareResource()
  }

}

extension GatewayResourceDetailViewController: WKNavigationDelegate {

  func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
    self.activityIndicator.stopAnimating()
    self.activityIndicator.removeFromSuperview()
  }

  func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
    self.activityIndicator.stopAnimating()
    self.activityIndicator.removeFromSuperview()

    let buttonTitleOK = NSLocalizedString("OK", comment: "")
    let alert = UIAlertController(
      title: NSLocalizedString(kTitleError, comment: ""),
      message: error.localizedDescription,
      preferredStyle: UIAlertController.Style.alert
    )

    alert.addAction(
      UIAlertAction.init(
        title: buttonTitleOK,
        style: .default,
        handler: { (_) in

          self.dismiss(animated: true, completion: nil)

        }
      )
    )

    self.present(alert, animated: true, completion: nil)

  }
}

extension GatewayResourceDetailViewController {

  func shareResource() {

    var resourcePdfData: Data?
    let resourceLink = self.resource?.file?.link
    var resourcesPath: URL?

    if let pathType = self.resource?.file?.localPath,
      pathType == "BundlePath",
      let path = resourceLink
    {
      if let fileURL = Bundle.main.url(
        forResource: path,
        withExtension: "pdf"
      ) {
        resourcesPath = fileURL
      }
    } else if let path = resourceLink {
      let fullPath = resourcesDownloadPath + "/" + path
      if let url = URL(string: fullPath) {
        resourcePdfData = FileDownloadManager.decrytFile(pathURL: url)
      }
    }

    var items: [Any] = []
    if let pdfData = resourcePdfData {
      items = [pdfData]
    } else if let localPath = resourcesPath {
      items = [localPath]
    } else if let resourceHTML = resourceLink {
      items = [self.webView.renderSelfToPdfData(htmlString: resourceHTML)]
    }

    guard !items.isEmpty else {
      self.view.makeToast(kResourceShareError)
      return
    }
    let fileTitle = self.resource?.title ?? ""  // TODO: Update the name of PDF to title.
    items.insert(fileTitle, at: 0)
    let activityController = UIActivityViewController(activityItems: items, applicationActivities: nil)
    present(activityController, animated: true)
  }
}

extension GatewayResourceDetailViewController: FileDownloadManagerDelegates {

  func download(manager: FileDownloadManager, didUpdateProgress progress: Float) {
    self.progressBar?.progress = progress
  }

  func download(manager: FileDownloadManager, didFinishDownloadingAtPath path: String) {

    let fullPath = resourcesDownloadPath + "/" + path

    let data = FileDownloadManager.decrytFile(pathURL: URL.init(string: fullPath))

    if let pdfData = data {
      self.resource?.file?.localPath = path
      let mimeType = "application/" + "\(self.resource?.file?.mimeType?.rawValue ?? "")"
      self.webView.load(
        pdfData,
        mimeType: mimeType,
        characterEncodingName: "UTF-8",
        baseURL: URL(fileURLWithPath: "")
      )
    }
  }

  func download(manager: FileDownloadManager, didFailedWithError error: Error) {}

}
