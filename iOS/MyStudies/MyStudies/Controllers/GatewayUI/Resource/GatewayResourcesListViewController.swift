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

class GatewayResourcesListViewController: UIViewController {

  @IBOutlet var tableView: UITableView?
  private var tableRows: [TableRow] = []
  
  /// Load the collection of `Resources` from plist file and assign it to Gateway.
  func loadResources() {

    let plistPath = Bundle.main.path(
      forResource: "Resources",
      ofType: ".plist",
      inDirectory: nil
    )
    let arrayContent = NSMutableArray.init(contentsOfFile: plistPath!)

    var listOfResources: [Resource] = []
    for resource in arrayContent! {
      let resourceObj = Resource(detail: resource as! [String: Any])
      listOfResources.append(resourceObj)
    }
    // Assign to Gateway
    Gateway.instance.resources = listOfResources

    self.tableView?.reloadData()

  }

  // MARK: - ViewController Lifecycle.
  override func viewDidLoad() {
    super.viewDidLoad()
    self.navigationItem.title = NSLocalizedString("Resources", comment: "")
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    self.setNavigationBarItem()
    populateRows()
    self.loadResources()
  }

  func handleResourcesReponse() {
    self.tableView?.reloadData()
  }
  
  func handelTerms() {
    let link: String = Branding.termsAndConditionURL
    let title: String = kNavigationTitleTerms
    
    guard !link.isEmpty else { return }
    let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
    let webViewController =
      (loginStoryboard.instantiateViewController(withIdentifier: "WebViewController")
        as? UINavigationController)!
    let webview = (webViewController.viewControllers[0] as? WebViewController)!
    webview.requestLink = link
    webview.title = title
    self.navigationController?.present(webViewController, animated: true, completion: nil)
  }
  
  func handelPrivacy() {
    let link: String = Branding.privacyPolicyURL
    let title: String = kNavigationTitlePrivacyPolicy
    
    guard !link.isEmpty else { return }
    let loginStoryboard = UIStoryboard.init(name: "Main", bundle: Bundle.main)
    let webViewController =
      (loginStoryboard.instantiateViewController(withIdentifier: "WebViewController")
        as? UINavigationController)!
    let webview = (webViewController.viewControllers[0] as? WebViewController)!
    webview.requestLink = link
    webview.title = title
    self.navigationController?.present(webViewController, animated: true, completion: nil)
  }
  
  func populateRows() {
    if !Utilities.isStandaloneApp() {
      let linkTerm: String = Branding.termsAndConditionURL
      let linkPrivacy: String = Branding.privacyPolicyURL
      if linkTerm != "" && linkPrivacy != "" {
        tableRows.append(.terms)
        tableRows.append(.privacy)
      } else if linkTerm != "" {
        tableRows.append(.terms)
      } else if linkPrivacy != "" {
        tableRows.append(.privacy)
      }
    }
  }
}
// MARK: TableView Data source
extension GatewayResourcesListViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    if !Utilities.isStandaloneApp() {
      let linkTerm: String = Branding.termsAndConditionURL
      let linkPrivacy: String = Branding.privacyPolicyURL
      if linkTerm != "" && linkPrivacy != "" {
        return (Gateway.instance.resources?.count)! + 2
      } else if linkTerm != "" {
        return (Gateway.instance.resources?.count)! + 1
      } else if linkPrivacy != "" {
        return (Gateway.instance.resources?.count)! + 1
      }
      return (Gateway.instance.resources?.count)!
    }
    return (Gateway.instance.resources?.count)!
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    let cell =
      tableView.dequeueReusableCell(withIdentifier: "resourcesCell", for: indexPath)
      as! ResourcesListCell

    if indexPath.row < (Gateway.instance.resources?.count)! {
    let resource = Gateway.instance.resources?[indexPath.row]
    cell.labelResourceTitle?.text = resource?.title
    } else {
    let rowType = tableRows[(indexPath.row - (Gateway.instance.resources?.count)!)]
    cell.populateCellData(data: rowType.title)
    }
    return cell
  }
}

// MARK: TableView Delegates
extension GatewayResourcesListViewController: UITableViewDelegate {

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    tableView.deselectRow(at: indexPath, animated: true)

    if indexPath.row < (Gateway.instance.resources?.count)! {
    let resource = Gateway.instance.resources?[indexPath.row]
    let storyboard = UIStoryboard(name: kStudyStoryboard, bundle: nil)
    let resourceDetail =
      storyboard.instantiateViewController(
        withIdentifier: "ResourceDetailViewControllerIdentifier"
      )
      as! GatewayResourceDetailViewController
    resourceDetail.resource = resource
    self.navigationController?.pushViewController(resourceDetail, animated: true)
    } else {
      if tableRows[(indexPath.row - (Gateway.instance.resources?.count)!)] == .terms {
        handelTerms()
      } else {
        handelPrivacy()
      }
    }

  }
}

extension GatewayResourcesListViewController: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    self.addProgressIndicator()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    self.removeProgressIndicator()

    if requestName as String == WCPMethods.gatewayInfo.method.methodName {
      self.handleResourcesReponse()
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {
    self.removeProgressIndicator()
  }
}

private enum TableRow {
  case terms, privacy
  
  var title: String {
    switch self {
    case .terms:
      return LocalizableString.resourceTerms.localizedString
    case .privacy:
      return LocalizableString.resourcePrivacy.localizedString
    }
  }
}

protocol ResourceAppLevel {
  var title: String { get }
}
