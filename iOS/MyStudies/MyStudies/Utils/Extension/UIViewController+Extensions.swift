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

import QuickLook
import UIKit

let kResourceName = "fda_preload"

extension UINavigationController {
  open override var childForStatusBarStyle: UIViewController? {
    return visibleViewController
  }
}

extension UIViewController {

  func topMostViewController() -> UIViewController {

    if self.presentedViewController == nil {
      return self
    }
    if let navigation = self.presentedViewController as? UINavigationController {
      return navigation.visibleViewController!.topMostViewController()
    }
    if let tab = self.presentedViewController as? UITabBarController {
      if let selectedTab = tab.selectedViewController {
        return selectedTab.topMostViewController()
      }
      return tab.topMostViewController()
    }
    return self.presentedViewController!.topMostViewController()
  }

  func setNavigationBarItem() {

    self.addLeftBarButtonWithImage(UIImage(named: "menu_icn")!)
    self.slideMenuController()?.removeLeftGestures()
    self.slideMenuController()?.removeRightGestures()
    self.slideMenuController()?.addLeftGestures()
    self.slideMenuController()?.addRightGestures()
  }

  func showAlert(title: String, message: String) {

    let alert = UIAlertController(
      title: title,
      message: message,
      preferredStyle: UIAlertController.Style.alert
    )
    alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))

    self.present(alert, animated: true, completion: nil)
  }

  func addProgressIndicator(with message: String = "") {

    guard !self.view.subviews.contains(where: { $0.isKind(of: NewProgressView.self) }),
      let progressView = NewProgressView.instanceFromNib(frame: view.bounds)
    else { return }
    progressView.showLoader(with: message)
    self.navigationItem.leftBarButtonItem?.isEnabled = false
    self.navigationItem.rightBarButtonItem?.isEnabled = true
    self.navigationItem.backBarButtonItem?.isEnabled = false
    slideMenuController()?.removeLeftGestures()
    slideMenuController()?.view.isUserInteractionEnabled = false
    self.navigationController?.navigationBar.isUserInteractionEnabled = false

    self.view.addSubview(progressView)
    progressView.alpha = 0
    progressView.translatesAutoresizingMaskIntoConstraints = false

    NSLayoutConstraint.activate([
      progressView.leadingAnchor.constraint(equalTo: self.view.leadingAnchor),
      progressView.trailingAnchor.constraint(equalTo: self.view.trailingAnchor),
      progressView.topAnchor.constraint(equalTo: self.view.topAnchor),
      progressView.bottomAnchor.constraint(equalTo: self.view.bottomAnchor),
    ])

    UIView.animate(withDuration: 0.3) {
      progressView.alpha = 1
    }
  }

  func removeProgressIndicator() {

    self.navigationItem.leftBarButtonItem?.isEnabled = true
    self.navigationItem.rightBarButtonItem?.isEnabled = true
    self.navigationItem.backBarButtonItem?.isEnabled = true
    self.navigationController?.navigationBar.isUserInteractionEnabled = true
    slideMenuController()?.view.isUserInteractionEnabled = true
    slideMenuController()?.addLeftGestures()

    if let progressView = self.view.subviews
      .first(where: { $0.isKind(of: NewProgressView.self) })
    {
      UIView.animate(
        withDuration: 0.2,
        animations: {
          progressView.alpha = 0
        }
      ) { (_) in
        progressView.removeFromSuperview()
      }
    }
  }

}

extension UIViewController {

  /// Adds a custom back button to navigation bar.
  /// - Returns: Instance of child `UIButton`
  @discardableResult
  public func addBackBarButton() -> UIButton {

    let customView = UIView(frame: CGRect(x: -15, y: 0, width: 46, height: 36))

    let backbutton: UIButton = UIButton.init(frame: customView.frame)
    backbutton.setImage(#imageLiteral(resourceName: "backIcon"), for: .normal)
    backbutton.addTarget(self, action: #selector(self.popController), for: .touchUpInside)
    customView.addSubview(backbutton)
    let backBarButton = UIBarButtonItem(customView: customView)
    navigationItem.leftBarButtonItem = backBarButton
    return backbutton
  }

  public func addHomeButton() {

    let customView = UIView.init(frame: CGRect.init(x: -15, y: 0, width: 46, height: 36))

    let backbutton: UIButton = UIButton.init(frame: customView.frame)
    backbutton.setImage(#imageLiteral(resourceName: "homeIcon"), for: .normal)
    backbutton.addTarget(
      self,
      action: #selector(self.popToSpecificController),
      for: .touchUpInside
    )
    customView.addSubview(backbutton)

    navigationItem.leftBarButtonItem = UIBarButtonItem.init(customView: customView)
  }

  @objc public func popToSpecificController() {

    var identifier: String? = ""

    switch self {
    case is ResourcesViewController:
      identifier = kUnwindToStudyListIdentifier
    case is ActivitiesViewController:
      identifier = kActivityUnwindToStudyListIdentifier
    default:
      break
    }

    if identifier != "" {
      self.performSegue(withIdentifier: identifier!, sender: self)
    }
  }

  @objc public func popController() {
    _ = self.navigationController?.popViewController(animated: true)
  }
}

extension UIViewController {

  /// Creates and presents a new toast view.
  /// - Parameter error: Instance of `ApiError`.
  func makeToast(with error: ApiError) {
    self.view.makeToast(error.message ?? error.code?.description, title: error.title)
  }

  func presentDefaultAlertWithError(
    error: ErrorPresentable,
    animated: Bool,
    action: (() -> Void)?,
    completion: (() -> Void)?
  ) {

    let alert = UIAlertController(
      title: error.title,
      message: error.message,
      preferredStyle: .alert
    )

    let okAction = UIAlertAction(
      title: LocalizableString.ok.localizedString,
      style: .default
    ) { (_) in
      action?()
    }
    alert.addAction(okAction)
    present(alert, animated: animated) { [weak self] in
      self?.view.endEditing(true)
      completion?()
    }
  }

}
