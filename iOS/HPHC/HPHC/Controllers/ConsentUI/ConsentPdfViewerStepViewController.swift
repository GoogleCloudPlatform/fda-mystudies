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
import ResearchKit
import UIKit
import WebKit

let kPdfMimeType = "application/pdf"
let kUTF8Encoding = "UTF-8"
let kEmailSubject = "Signed Consent"
let kEmailSubjectDashboard = "Dashboard"
let kEmailSubjectCharts = "Charts"
let kConsentFileName = "Consent"
let kConsentFormat = ".pdf"

class ConsentPdfViewerStep: ORKStep {

  func showsProgress() -> Bool {
    return false
  }
}

// Displays Signed Consent Pdf and provides option to share by Email
class ConsentPdfViewerStepViewController: ORKStepViewController {

  // MARK: - Outlets
  @IBOutlet weak var webView: WKWebView!
  @IBOutlet weak var buttonEmailPdf: UIBarButtonItem?
  @IBOutlet weak var buttonNext: UIButton?

  var pdfData: Data?
  var consentTempURL: URL?

  // MARK: - ORKstepView Controller Init methods
  override init(step: ORKStep?) {
    super.init(step: step)
  }

  required init?(coder aDecoder: NSCoder) {
    super.init(coder: aDecoder)
  }

  override func hasNextStep() -> Bool {
    super.hasNextStep()
    return true
  }

  override func goForward() {
    super.goForward()
  }

  // MARK: - View controller Lifecycle

  override func viewDidLoad() {
    super.viewDidLoad()
    webView.navigationDelegate = self
    webView.contentScaleFactor = 1.0
  }

  override func viewDidAppear(_ animated: Bool) {
    super.viewDidAppear(animated)
    loadPDF()
  }

  override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    if let consentTempURL = self.consentTempURL {
      AKUtility.deleteFile(from: consentTempURL)
    }
  }

  /// Load PDF from the Data on WebView.
  private func loadPDF() {
    self.title = kConsent.uppercased()
    if let pdfData = self.pdfData {
      self.webView.load(
        pdfData,
        mimeType: "application/pdf",
        characterEncodingName: "UTF-8",
        baseURL: URL(fileURLWithPath: "")
      )
    }
  }

  /// SendConsentByMail used for sharing the Consent.
  func sendConsentByMail() {
    if let pdfData = self.pdfData {
      func attach(url: URL) {
        let items: [Any] = [kConsent.capitalized, url]
        let activityController = UIActivityViewController(activityItems: items, applicationActivities: nil)
        self.present(activityController, animated: true)
      }
      if let tempURL = self.consentTempURL {
        attach(url: tempURL)
      } else {
        let consentName = (Study.currentStudy?.name ?? "") + "-consent"
        ResourceDetailViewController.saveTempPdf(from: pdfData, name: consentName) { [weak self] (url) in
          if let url = url {
            attach(url: url)
          } else {
            self?.view.makeToast(kConsentShareError)
          }
        }
      }
    }
  }

  // MARK: - Button Actions

  @IBAction func buttonActionNext(sender: UIBarButtonItem?) {
    self.goForward()
  }

  @IBAction func buttonActionEmailPdf(sender: UIBarButtonItem?) {
    self.sendConsentByMail()
  }

}

// MARK: - WebView Delegate
extension ConsentPdfViewerStepViewController: WKNavigationDelegate {

  func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
    self.removeProgressIndicator()
  }

  func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
    self.removeProgressIndicator()

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
