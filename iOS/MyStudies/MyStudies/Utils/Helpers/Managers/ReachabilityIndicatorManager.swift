//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation
import UIKit

class ReachabilityIndicatorManager: NSObject {
  static var shared = ReachabilityIndicatorManager()
  var indicatorView: OfflineIndicatorView?
  
  func presentIndicator(viewController: UIViewController, isOffline: Bool) {
    
    if viewController.showOfflineIndicator() {
      if let indicatorView = self.indicatorView {
        viewController.view.addSubview(indicatorView)
      } else {
        self.indicatorView = self.setIndicatorView()
        viewController.view.addSubview(indicatorView!)
      }
      
      indicatorView?.frame = CGRect.init(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
      indicatorView?.isHidden = false
      indicatorView?.translatesAutoresizingMaskIntoConstraints = false
      indicatorView?.makeWidth(.equal, UIScreen.main.bounds.width)
      if isOffline == false {
        indicatorView?.isOffline(status: false)
        //                indicatorView?.makeHeight(.equal, 200)
      } else {
        indicatorView?.isOffline(status: true)
        //                indicatorView?.makeHeight(.equal, 275)
      }
      
      indicatorView?.alignCenterHorizontal(padding: 0)
      indicatorView?.alignTop(view: viewController.view, padding: 0)
    }
  }
  
  func shouldPresentIndicator(viewController: UIViewController, isOffline: Bool) {
    if let indicatorView = self.indicatorView {
      viewController.view.addSubview(indicatorView)
    } else {
      self.indicatorView = self.setIndicatorView()
      viewController.view.addSubview(indicatorView!)
    }
    
    indicatorView?.frame = CGRect.init(x: 0, y: 0, width: UIScreen.main.bounds.width, height: UIScreen.main.bounds.height)
    indicatorView?.isHidden = false
    indicatorView?.translatesAutoresizingMaskIntoConstraints = false
    indicatorView?.makeWidth(.equal, UIScreen.main.bounds.width)
    if isOffline == false {
      indicatorView?.isOffline(status: false)
      //                indicatorView?.makeHeight(.equal, 200)
    } else {
      indicatorView?.isOffline(status: true)
      //                indicatorView?.makeHeight(.equal, 275)
    }
    
    indicatorView?.alignCenterHorizontal(padding: 0)
    indicatorView?.alignTop(view: viewController.view, padding: 0)
  }
  
  func setLayoutContraints(view: UIView) {
    let leadingConstraint = NSLayoutConstraint(item: indicatorView!, attribute: .leading, relatedBy: .equal,
                                               toItem: view, attribute: .leading, multiplier: 1, constant: 0)
    let trailingConstraint = NSLayoutConstraint(item: indicatorView!, attribute: .trailing, relatedBy: .equal,
                                                toItem: view, attribute: .trailing, multiplier: 1, constant: 0)
    let topConstraint = NSLayoutConstraint(item: indicatorView!, attribute: .top, relatedBy: .equal,
                                           toItem: view, attribute: .top, multiplier: 1, constant: 0)
    let bottomConstraint = NSLayoutConstraint(item: indicatorView!, attribute: .bottom, relatedBy: .equal,
                                              toItem: view, attribute: .bottom, multiplier: 1, constant: 0)
    view.addConstraints([leadingConstraint, trailingConstraint, topConstraint, bottomConstraint])
  }
  
  func getVisibleViewController(_ rootViewController: UIViewController?) -> UIViewController? {
    
    var rootVC = rootViewController
    if rootVC == nil {
      rootVC = UIApplication.shared.keyWindow?.rootViewController
    }
    
    if rootVC?.presentedViewController == nil {
      if rootVC?.isKind(of: UINavigationController.self) == true {
        return (rootVC as! UINavigationController).topViewController
        
      }
      return rootVC
    }
    
    if let presented = rootVC?.presentedViewController {
      if presented.isKind(of: UINavigationController.self) {
        let navigationController = presented as! UINavigationController
        return navigationController.viewControllers.last!
      }
      
      if presented.isKind(of: UITabBarController.self) {
        let tabBarController = presented as! UITabBarController
        return tabBarController.selectedViewController!
      }
      
      return getVisibleViewController(presented)
    }
    return nil
  }
  
  func removeIndicator() {
    if let indicatorView = self.indicatorView {
      indicatorView.isHidden = true
      indicatorView.removeFromSuperview()
    }
    
  }
  
  func removeIndicator(viewController:UIViewController) {
    if let indicatorView = self.indicatorView {
      indicatorView.isHidden = true
      indicatorView.removeFromSuperview()
    } else {
      for subview in viewController.view.subviews where subview.tag == 5 {
        subview.isHidden = true
        subview.removeFromSuperview()
      }
    }
    
  }
  
  func setIndicatorView() -> OfflineIndicatorView {
    let view = OfflineIndicatorView()
    view.backgroundColor = UIColor.lightGray
    view.tag = 5
    return view
  }
}
