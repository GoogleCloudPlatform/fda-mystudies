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
import IQKeyboardManagerSwift
import SlideMenuControllerSwift
import UIKit
import WebKit

let kVerifyMessageFromSignIn =
  """
  Your registered email is pending verification. Please type in the Verification Code received in the email \
  to complete this step and proceed to using the app.
  """

enum SignInLoadFrom: Int {
  case gatewayOverview
  case joinStudy
  case menu
  case signUp
}

private enum SignInScheme: String {
  case forgotPassword
  case signup
  case terms
  case privacyPolicy
  case callback
  case activation
}

protocol SignInViewControllerDelegate: class {
  func didLogInCompleted()
  func didFailLogIn()
}

class SignInViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet var webKitView: WKWebView!

  // MARK: - Properties

  /// Progress view reflecting the current loading progress of the web view.
  let progressView = UIProgressView(progressViewStyle: .default)

  weak var delegate: SignInViewControllerDelegate?

  /// The observation object for the progress of the web view (we only receive notifications until it is deallocated).
  private var estimatedProgressObserver: NSKeyValueObservation?

  lazy var viewLoadFrom: SignInLoadFrom = .menu
  lazy var user = User.currentUser

  override var preferredStatusBarStyle: UIStatusBarStyle {
    return .default
  }

  deinit {
    debugPrint("\(self) Gone")
  }
  // MARK: - ViewController Lifecycle

  override func viewDidLoad() {
    super.viewDidLoad()
    setupNavigation()
    setupProgressView()
    setupEstimatedProgressObserver()
    DispatchQueue.main.async {
      self.webKitView.navigationDelegate = self
      self.load()
      self.initializeTermsAndPolicy()
    }
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    // unhide navigationbar
    self.navigationController?.setNavigationBarHidden(false, animated: true)

    if viewLoadFrom != .signUp {
      User.resetCurrentUser()
    }
    user = User.currentUser

    if viewLoadFrom == .gatewayOverview || viewLoadFrom == .joinStudy {
      self.addBackBarButton()
    } else if viewLoadFrom == .signUp {
      addCloseBarBtn()
    } else {
      self.setNavigationBarItem()
    }
    setNeedsStatusBarAppearanceUpdate()
  }

  override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    // hide navigationbar
    if viewLoadFrom == .gatewayOverview || Utilities.isStandaloneApp() {
      self.navigationController?.setNavigationBarHidden(true, animated: true)
    }
    SessionService.resetSession()
  }

  override func viewDidDisappear(_ animated: Bool) {
    super.viewDidDisappear(animated)
    self.webKitView.navigationDelegate = nil
    progressView.removeFromSuperview()
  }
  // MARK: - UI Utils

  private func setupNavigation() {
    self.title = NSLocalizedString(kSignInTitleText, comment: "")
    guard viewLoadFrom != .signUp else { return }  // No need to show info icon for auto login with registration.
    self.navigationItem.rightBarButtonItem = UIBarButtonItem(
      image: UIImage(named: "info"),
      style: .done,
      target: self,
      action: #selector(self.buttonInfoAction(_:))
    )
  }

  private func setupProgressView() {
    guard let navigationBar = navigationController?.navigationBar else { return }
    progressView.translatesAutoresizingMaskIntoConstraints = false
    navigationBar.addSubview(progressView)
    progressView.isHidden = true
    NSLayoutConstraint.activate([
      progressView.leadingAnchor.constraint(equalTo: navigationBar.leadingAnchor),
      progressView.trailingAnchor.constraint(equalTo: navigationBar.trailingAnchor),
      progressView.bottomAnchor.constraint(equalTo: navigationBar.bottomAnchor),
      progressView.heightAnchor.constraint(equalToConstant: 2.0),
    ])
  }

  private func addCloseBarBtn() {
    let closeBtn = UIBarButtonItem(
      image: UIImage(named: "close_1"),
      style: .plain,
      target: self,
      action: #selector(dismissSignUpNavigation)
    )
    self.navigationItem.leftBarButtonItem = closeBtn
  }

  // MARK: - Utils

  private func setupEstimatedProgressObserver() {
    estimatedProgressObserver = webKitView.observe(\.estimatedProgress, options: [.new]) { [weak self] webView, _ in
      self?.progressView.progress = Float(webView.estimatedProgress)
    }
  }

  func initializeTermsAndPolicy() {
    TermsAndPolicy.currentTermsAndPolicy = TermsAndPolicy()
    let policyURL = Branding.privacyPolicyURL
    let terms = Branding.termsAndConditionURL
    TermsAndPolicy.currentTermsAndPolicy?.initWith(terms: terms, policy: policyURL)
  }

  /// Loads the Login request on webview.
  private func load() {
    if let request = HydraAPI.loginRequest() {
      webKitView.load(request)
    }
  }

  fileprivate func handleDataCallback(_ url: URL) {
    if let code = url["code"],
      let userID = url["userId"],
      let status = Int(url["accountStatus"] ?? ""),
      let accountStatus = AccountStatus(rawValue: status)
    {
      let delegate = UIApplication.shared.delegate as? AppDelegate
      delegate?.calculateTimeZoneChange()
      User.currentUser.userId = userID
      switch accountStatus {
      case .verified:
        User.currentUser.verified = true
        grantVerifiedUser(with: code)
      case .pending:
        User.currentUser.verified = false
        navigateToVerifyController()
      case .tempPassword:
        User.currentUser.verified = true
        User.currentUser.isLoggedInWithTempPassword = true
        grantVerifiedUser(with: code)
      }
    }
  }

  private func handleScheme(for url: URL) {
    guard let scheme = SignInScheme(rawValue: url.lastPathComponent) else { return }
    switch scheme {
    case .forgotPassword, .signup:
      self.performSegue(withIdentifier: scheme.rawValue, sender: self)
    case .terms:
      let link = TermsAndPolicy.currentTermsAndPolicy?.termsURL ?? ""
      didLoadPrivacyOrTerms(title: kNavigationTitleTerms, link: link)
    case .privacyPolicy:
      let link = TermsAndPolicy.currentTermsAndPolicy?.policyURL ?? ""
      didLoadPrivacyOrTerms(title: kNavigationTitlePrivacyPolicy, link: link)
    case .callback:
      handleDataCallback(url)
    case .activation:
      User.currentUser.emailId = url["email"]
      User.currentUser.verified = false
      navigateToVerifyController()
    }
  }

  /// Grants the user for access token.
  /// - Parameter code: Login authentication code from callback.
  private func grantVerifiedUser(with code: String) {
    HydraAPI.grant(user: User.currentUser, with: code) { [weak self] (status, error) in
      if status {
        if self?.viewLoadFrom == .signUp {
          self?.signUpCompleted()
        } else {
          self?.userDidLoggedIn()
        }
      } else if let error = error {
        self?.presentDefaultAlertWithError(
          error: error,
          animated: true,
          action: {
            SessionService.resetSession()  // Reset the session.
            self?.load()  // Load the login again.
          },
          completion: nil
        )
      }
    }
  }

  // MARK: - Actions

  /// Loads the privacy or Terms screen
  /// - Parameters:
  ///   - title: Title for the screen.
  ///   - link: Link to load in webview.
  private func didLoadPrivacyOrTerms(title: String, link: String) {
    let mainStoryboard = UIStoryboard(name: "Main", bundle: .main)
    if let webViewController = mainStoryboard.instantiateViewController(withIdentifier: "WebViewController")
      as? UINavigationController,
      let webview = webViewController.viewControllers.first as? WebViewController
    {
      webview.requestLink = link
      webview.title = title
      self.navigationController?.present(webViewController, animated: true, completion: nil)
    }
  }

  // MARK: - Button Action

  /// To Display registration information.
  @IBAction func buttonInfoAction(_ sender: Any) {
    UIUtilities.showAlertWithTitleAndMessage(
      title: "Why Register?",
      message: kRegistrationInfoMessage as NSString
    )
  }

  /// Closes the navigation of Auto Login In.
  @objc private func dismissSignUpNavigation() {
    self.navigationController?.dismiss(
      animated: true,
      completion: {
        self.delegate?.didFailLogIn()  // User closes while auto login with temp ID.
      }
    )
  }

  /// Dismiss key board when clicked on Background.
  @objc func dismissKeyboard() {
    self.view.endEditing(true)
  }

  /// To Create Menu View before Navigating to DashBoard.
  func navigateToGatewayDashboard() {
    self.createMenuView()
  }

  /// Used to Naviagate Changepassword view controller using gateway storyboard.
  func navigateToChangePassword() {

    let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)

    let changePassword =
      (storyboard.instantiateViewController(withIdentifier: "ChangePasswordViewController")
      as? ChangePasswordViewController)!
    if viewLoadFrom == .menu {
      changePassword.viewLoadFrom = .menuLogin
    } else if viewLoadFrom == .joinStudy {
      changePassword.viewLoadFrom = .joinStudy
    } else {
      changePassword.viewLoadFrom = .login
    }
    self.navigationController?.pushViewController(changePassword, animated: true)
  }

  /// Used to navigate to Verification controller.
  func navigateToVerifyController() {
    self.performSegue(withIdentifier: "verificationSegue", sender: nil)
  }

  /// Method to update Left Menu using `FDASlideMenuViewController` and Gateway dashboard
  func createMenuView() {

    let storyboard = UIStoryboard(name: kStoryboardIdentifierGateway, bundle: nil)

    let fda =
      (storyboard.instantiateViewController(withIdentifier: kStoryboardIdentifierSlideMenuVC)
      as? FDASlideMenuViewController)!

    self.navigationController?.pushViewController(fda, animated: true)
  }

  // MARK: - Segue Methods

  @IBAction func unwindFromVerification(_ segue: UIStoryboardSegue) {}

  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {

    if let signUpController = segue.destination as? SignUpViewController {
      if viewLoadFrom == .menu {
        signUpController.viewLoadFrom = .menuLogin
      } else if viewLoadFrom == .joinStudy {
        signUpController.viewLoadFrom = .joinStudyLogin
      } else {
        signUpController.viewLoadFrom = .login
      }

    }
    if let verificationController = segue.destination as? VerificationViewController {

      if viewLoadFrom == .menu {
        verificationController.shouldCreateMenu = false
        verificationController.viewLoadFrom = .login
      } else if viewLoadFrom == .joinStudy {
        verificationController.viewLoadFrom = .joinStudy
        verificationController.shouldCreateMenu = false
      } else if viewLoadFrom == .gatewayOverview {

        verificationController.viewLoadFrom = .login
        verificationController.shouldCreateMenu = true
      }

      verificationController.labelMessage = kVerifyMessageFromSignIn
    }
  }
}

extension SignInViewController: UIGestureRecognizerDelegate {
  func gestureRecognizerShouldBegin(_ gestureRecognizer: UIGestureRecognizer) -> Bool {
    if gestureRecognizer.isKind(of: UITapGestureRecognizer.classForCoder()) {
      if gestureRecognizer.numberOfTouches == 2 {
        return false
      }
    }
    return true
  }
}

// MARK: - Webservices Delegate
extension SignInViewController {

  fileprivate func userDidLoggedIn() {
    let ud = UserDefaults.standard
    ud.set(true, forKey: kNotificationRegistrationIsPending)
    ud.synchronize()

    ORKPasscodeViewController.removePasscodeFromKeychain()

    if User.currentUser.isLoggedInWithTempPassword {
      self.navigateToChangePassword()
    } else {

      if viewLoadFrom == .gatewayOverview {
        self.navigateToGatewayDashboard()

      } else if viewLoadFrom == .joinStudy {

        let leftController = slideMenuController()?.leftViewController as? LeftMenuViewController
        leftController?.createLeftmenuItems()
        self.performSegue(withIdentifier: "unwindStudyHomeSegue", sender: self)

      } else {

        let leftController = slideMenuController()?.leftViewController as? LeftMenuViewController
        leftController?.createLeftmenuItems()
        leftController?.changeViewController(.studyList)
      }
    }
  }

  private func signUpCompleted() {
    self.navigationController?.dismiss(animated: true) {
      DispatchQueue.main.async {
        self.delegate?.didLogInCompleted()
      }
    }
  }

}

extension SignInViewController: WKNavigationDelegate {

  func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
    if progressView.isHidden {
      // Make sure our animation is visible.
      progressView.isHidden = false
    }

    UIView.animate(
      withDuration: 0.33,
      animations: {
        self.progressView.alpha = 1.0
      }
    )
  }

  func webView(
    _ webView: WKWebView,
    didFinish navigation: WKNavigation!
  ) {
    UIView.animate(
      withDuration: 0.33,
      animations: {
        self.progressView.alpha = 0.0
      },
      completion: { isFinished in
        // Update `isHidden` flag accordingly:
        //  - set to `true` in case animation was completly finished.
        //  - set to `false` in case animation was interrupted, e.g. due to starting of another animation.
        self.progressView.isHidden = isFinished
      }
    )
  }

  func webView(
    _ webView: WKWebView,
    didFail navigation: WKNavigation!,
    withError error: Error
  ) {
    self.view.makeToast(error.localizedDescription)
  }

  func webView(
    _ webView: WKWebView,
    didFailProvisionalNavigation navigation: WKNavigation!,
    withError error: Error
  ) {
    UIView.animate(
      withDuration: 0.33,
      animations: {
        self.progressView.alpha = 0.0
      },
      completion: nil
    )
  }

  func webView(
    _ webView: WKWebView,
    didReceive challenge: URLAuthenticationChallenge,
    completionHandler: @escaping (URLSession.AuthChallengeDisposition, URLCredential?) -> Void
  ) {
    if challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust {
      let cred = URLCredential(trust: challenge.protectionSpace.serverTrust!)
      completionHandler(.useCredential, cred)
    } else {
      completionHandler(.performDefaultHandling, nil)
    }
  }

  func webView(
    _ webView: WKWebView,
    decidePolicyFor navigationAction: WKNavigationAction,
    decisionHandler: @escaping (WKNavigationActionPolicy) -> Void
  ) {
    if let url = navigationAction.request.url, url.scheme == "app" {
      // Handle the callbacks
      handleScheme(for: url)
      decisionHandler(.cancel)
    } else {
      decisionHandler(.allow)
    }
  }
}
