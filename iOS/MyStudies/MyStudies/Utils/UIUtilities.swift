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

import Foundation
import UIKit

public typealias AlertAction = () -> Void

class UIUtilities: NSObject {
  
  // MARK: - Alert composers

  /// Presents alert message
  class func showAlertWithTitleAndMessage(title: NSString, message: NSString) {

    let alert = UIAlertController(
      title: title as String,
      message: message as String,
      preferredStyle: UIAlertController.Style.alert
    )
    alert.addAction(
      UIAlertAction(
        title: NSLocalizedString("OK", comment: ""),
        style: .default,
        handler: nil
      )
    )
    var rootViewController = UIApplication.shared.keyWindow?.rootViewController
    if let navigationController = rootViewController as? UINavigationController {
      rootViewController = navigationController.viewControllers.first
    }
    if let tabBarController = rootViewController as? UITabBarController {
      rootViewController = tabBarController.selectedViewController
    }
    rootViewController?.present(alert, animated: true, completion: nil)

  }

  /// Presents alert message
  class func showAlertWithMessage(alertMessage: String) {
    self.showAlertWithTitleAndMessage(title: "", message: alertMessage as NSString)
  }

  class func showAlertMessageWithTwoActionsAndHandler(
    _ errorTitle: String,
    errorMessage: String,
    errorAlertActionTitle: String,
    errorAlertActionTitle2: String?,
    viewControllerUsed: UIViewController,
    action1: @escaping AlertAction,
    action2: @escaping AlertAction
  ) {
    let alert = UIAlertController(
      title: errorTitle,
      message: errorMessage,
      preferredStyle: UIAlertController.Style.alert
    )

    alert.addAction(
      UIAlertAction(
        title: errorAlertActionTitle,
        style: UIAlertAction.Style.default,
        handler: { (_) in
          action1()
        }
      )
    )
    if errorAlertActionTitle2 != nil {
      alert.addAction(
        UIAlertAction(
          title: errorAlertActionTitle2,
          style: UIAlertAction.Style.default,
          handler: { (_) in
            action2()
          }
        )
      )
    }

    viewControllerUsed.present(alert, animated: true, completion: nil)
  }

  class func showAlertMessageWithThreeActionsAndHandler(
    _ errorTitle: String,
    errorMessage: String,
    errorAlertActionTitle: String,
    errorAlertActionTitle2: String?,
    errorAlertActionTitle3: String?,
    viewControllerUsed: UIViewController,
    action1: @escaping AlertAction,
    action2: @escaping AlertAction,
    action3: @escaping AlertAction
  ) {
    let alert = UIAlertController(
      title: errorTitle,
      message: errorMessage,
      preferredStyle: UIAlertController.Style.alert
    )

    alert.addAction(
      UIAlertAction(
        title: errorAlertActionTitle,
        style: UIAlertAction.Style.default,
        handler: { (_) in
          action1()
        }
      )
    )
    if errorAlertActionTitle2 != nil {
      alert.addAction(
        UIAlertAction(
          title: errorAlertActionTitle2,
          style: UIAlertAction.Style.default,
          handler: { (_) in
            action2()
          }
        )
      )
    }

    if errorAlertActionTitle3 != nil {
      alert.addAction(
        UIAlertAction(
          title: errorAlertActionTitle3,
          style: UIAlertAction.Style.default,
          handler: { (_) in
            action3()
          }
        )
      )
    }

    viewControllerUsed.present(alert, animated: true, completion: nil)
  }

  class func showAlertMessageWithActionHandler(
    _ title: String,
    message: String,
    buttonTitle: String,
    viewControllerUsed: UIViewController,
    action: @escaping AlertAction
  ) {

    let alert = UIAlertController(
      title: title,
      message: message,
      preferredStyle: UIAlertController.Style.alert
    )

    alert.addAction(
      UIAlertAction(
        title: buttonTitle,
        style: UIAlertAction.Style.default,
        handler: { (_) in
          action()
        }
      )
    )

    viewControllerUsed.present(alert, animated: true, completion: nil)
  }

  class func showAlertMessage(
    _ errorTitle: String,
    errorMessage: String,
    errorAlertActionTitle: String,
    viewControllerUsed: UIViewController?
  ) {
    let alert = UIAlertController(
      title: errorTitle,
      message: errorMessage,
      preferredStyle: UIAlertController.Style.alert
    )
    alert.addAction(
      UIAlertAction(
        title: errorAlertActionTitle,
        style: UIAlertAction.Style.default,
        handler: nil
      )
    )
    viewControllerUsed!.present(alert, animated: true, completion: nil)
  }

}
