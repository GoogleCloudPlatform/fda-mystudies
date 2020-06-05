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
import SafariServices
import UIKit
import WebKit

class ResourceDetailViewController: UIViewController {

  // MARK: - Outles
  @IBOutlet var webView: WKWebView!
  @IBOutlet var bottomToolBar: UIToolbar!
  @IBOutlet var activityIndicator: UIActivityIndicatorView!

  // MARK: - Properties
  var requestLink: String?
  var type: String?
  var htmlString: String?
  var resource: Resource?

  /// Resource converted from HTML string and saved in Cache directory.
  var tempResourceFilePath: URL?

  private var isFileAvailable = false

  static var resouceDirectory: String {
    return "Resources" + "/" + (Study.currentStudy?.studyId ?? "")
  }

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    self.hidesBottomBarWhenPushed = true
    self.addBackBarButton()
    self.title = resource?.title
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    webView.navigationDelegate = self
    webView.contentScaleFactor = 1.0
    loadWebView()
  }

  override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    DispatchQueue.main.async {
      if let tempResource = self.tempResourceFilePath {
        AKUtility.deleteFile(from: tempResource)
      }
    }
    webView.navigationDelegate = nil
  }

  // MARK: - UI
  fileprivate func loadWebView() {

    if let resourceLink = self.resource?.file?.link {
      if self.resource?.file?.mimeType == .pdf {
        if self.resource?.file?.localPath == "BundlePath" {
          if let path = Bundle.main.path(
            forResource: resourceLink,
            ofType: ".pdf"
          ) {
            isFileAvailable = true
            self.loadWebViewWithPath(path: path)
          }
        } else if let resourceLink = self.resource?.file?.link,
          let resourceURL = URL(string: resourceLink)
        {
          activityIndicator.startAnimating()
          activityIndicator.isHidden.toggle()
          if let url = checkIfFileExists(pdfNameFromUrl: resourceURL.lastPathComponent) {
            webView.loadFileURL(url, allowingReadAccessTo: url)
            self.isFileAvailable = true
          } else {
            self.webView.load(URLRequest(url: resourceURL))
          }
        }
      } else if let htmlString = self.htmlString {
        webView.allowsBackForwardNavigationGestures = false
        webView.loadHTMLString(WebViewController.headerString + htmlString, baseURL: nil)
      } else if let requestLink = self.requestLink,
        let url = URL(string: requestLink)
      {
        self.webView.load(URLRequest(url: url))
      } else if let resourceHtmlString = self.resource?.file?.link {
        webView.allowsBackForwardNavigationGestures = false
        webView.loadHTMLString(WebViewController.headerString + resourceHtmlString, baseURL: nil)
      }
    }
  }

  // MARK: - UI Utils.
  /// To Load web page with `URL` string path.
  /// - Parameter path: Path of the url.
  func loadWebViewWithPath(path: String) {

    guard
      let url: URL = URL(
        string: path.addingPercentEncoding(withAllowedCharacters: .urlPathAllowed)!
      )
    else { return }
    let urlRequest = URLRequest(url: url)

    webView?.allowsBackForwardNavigationGestures = true
    _ = webView?.load(urlRequest)

  }

  func loadWebViewWithData(data: Data) {

    webView.allowsBackForwardNavigationGestures = true
    self.webView.load(
      data,
      mimeType: "application/pdf",
      characterEncodingName: "UTF-8",
      baseURL: URL(fileURLWithPath: "")
    )
  }

  // MARK: - Button Actions

  @IBAction func cancelButtonClicked(_ sender: Any) {
    self.dismiss(animated: true, completion: nil)
  }

  @IBAction func buttonActionForward(_ sender: UIBarButtonItem) {
    self.shareResource { [weak self] (status) in
      if !status {
        self?.view.makeToast(kResourceShareError)
      }
    }
  }

  @IBAction func buttonActionBack(_ sender: UIBarButtonItem) {

    if webView.canGoBack {
      webView.goBack()
    } else if webView.backForwardList.backList.count == 0 {
      if self.resource?.file?.mimeType != .pdf,
        let htmlString = self.requestLink
      {
        webView.loadHTMLString(WebViewController.headerString + htmlString, baseURL: nil)
      }
    }
  }

  @IBAction func buttonActionGoForward(_ sender: UIBarButtonItem) {
    if webView.canGoForward {
      webView.goForward()
    }
  }

}

extension ResourceDetailViewController: WKNavigationDelegate {

  func webView(_ webView: WKWebView, didFinish navigation: WKNavigation) {
    self.activityIndicator.stopAnimating()
    if self.resource?.file?.mimeType == .pdf, let url = webView.url, !isFileAvailable {
      savePdf(for: url)
    }
  }

  func webView(_ webView: WKWebView, didFail navigation: WKNavigation, withError error: Error) {
    self.view.makeToast(error.localizedDescription)
  }

  func webView(
    _ webView: WKWebView,
    decidePolicyFor navigationAction: WKNavigationAction,
    decisionHandler: (@escaping (WKNavigationActionPolicy) -> Void)
  ) {
    switch navigationAction.navigationType {
    case .linkActivated:
      if navigationAction.targetFrame == nil {
        webView.load(navigationAction.request)
      }
    default:
      break
    }
    self.activityIndicator.startAnimating()
    decisionHandler(.allow)
  }

  func webView(
    _ webView: WKWebView,
    decidePolicyFor navigationResponse: WKNavigationResponse,
    decisionHandler: @escaping (WKNavigationResponsePolicy) -> Void
  ) {

    switch navigationResponse.response {

    case let response as HTTPURLResponse:
      if self.resource?.file?.mimeType == .pdf,
        response.statusCode == HTTPError.notFound.rawValue
      {
        self.presentDefaultAlertWithError(
          error: ApiError(code: .notFound),
          animated: true,
          action: { [weak self] in
            self?.navigationController?.popViewController(animated: true)
          },
          completion: nil
        )
        self.activityIndicator.stopAnimating()
        decisionHandler(.cancel)
      } else {
        decisionHandler(.allow)
      }

    default:
      decisionHandler(.allow)
    }
  }
}

// MARK: - Mail Compose Delegate
extension ResourceDetailViewController {

  func shareResource(completion: @escaping (_ status: Bool) -> Void) {
    let resourceLink = self.resource?.file?.link

    if let pathType = self.resource?.file?.localPath,
      pathType == "BundlePath",
      let path = resourceLink
    {
      if let fileURL = Bundle.main.url(
        forResource: path,
        withExtension: "pdf"
      ) {
        attachResource(from: fileURL)
        completion(true)
      }
    } else if self.resource?.file?.mimeType == .pdf,
      isFileAvailable,
      let path = resourceLink,
      let fileName = URL(string: path)?.lastPathComponent,
      let documentURL = checkIfFileExists(pdfNameFromUrl: fileName)
    {
      attachResource(from: documentURL)
      completion(true)
    } else if let resourceHTML = resourceLink,
      self.resource?.file?.mimeType != .pdf
    {
      let pdfData = webView.renderSelfToPdfData(htmlString: resourceHTML)
      if let tempPath = tempResourceFilePath {
        attachResource(from: tempPath)
        completion(true)
      } else {
        ResourceDetailViewController.saveTempPdf(from: pdfData, name: self.resource?.file?.name ?? "Resource") {
          [weak self] (url) in
          self?.tempResourceFilePath = url
          if let tempPath = url {
            self?.attachResource(from: tempPath)
            completion(true)
          } else {
            completion(false)
          }
        }
      }
    } else {
      completion(false)
    }

  }

  func attachResource(from url: URL) {

    var items: [Any] = [url]
    let fileTitle = self.resource?.title ?? ""
    items.insert(fileTitle, at: 0)
    let activityController = UIActivityViewController(
      activityItems: items,
      applicationActivities: nil
    )
    present(activityController, animated: true)
  }

}

extension ResourceDetailViewController {

  func savePdf(for url: URL) {

    DispatchQueue.global(qos: .background).async { [weak self] in

      let pdfData = try? Data(contentsOf: url)
      let pdfNameFromUrl = url.lastPathComponent
      let actualPath = AKUtility.cacheDirectoryPath.appendingPathComponent(pdfNameFromUrl)

      do {
        try pdfData?.write(to: actualPath, options: .atomic)
        AKUtility.moveFileToDocuments(
          fromUrl: actualPath,
          toDirectory: ResourceDetailViewController.resouceDirectory,
          withName: pdfNameFromUrl
        )
        self?.isFileAvailable = true
      } catch {
        Logger.sharedInstance.error(error)
      }
    }
  }

  static func saveTempPdf(
    from data: Data,
    name: String,
    completion: @escaping (_ url: URL?) -> Void
  ) {
    DispatchQueue.global(qos: .background).async {
      let pdfNameFromUrl = name + ".pdf"
      let tempPath = AKUtility.cacheDirectoryPath.appendingPathComponent(pdfNameFromUrl)
      do {
        try data.write(to: tempPath, options: .atomic)
        DispatchQueue.main.async {
          completion(tempPath)
        }
      } catch {
        Logger.sharedInstance.error(error)
        DispatchQueue.main.async {
          completion(nil)
        }
      }
    }
  }

  func checkIfFileExists(pdfNameFromUrl: String) -> URL? {
    let fileExist = AKUtility.checkFileExistAt(
      directory: ResourceDetailViewController.resouceDirectory,
      filename: pdfNameFromUrl
    )
    if fileExist.exist {
      return fileExist.filepath
    } else {
      return nil
    }
  }

}
