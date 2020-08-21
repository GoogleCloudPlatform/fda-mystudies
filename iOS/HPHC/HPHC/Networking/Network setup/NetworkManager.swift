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
import Reachability
import UIKit

protocol NMWebServiceDelegate: class {

  /// Called when request is fired.Use this to show any activity indicator
  /// - Parameter manager: NetworkManager instance
  /// - Parameter requestName: Web request@objc  name
  func startedRequest(_ manager: NetworkManager, requestName: NSString)

  /// Called when request if finished. Handle your response or error in this delegate
  /// - Parameter manager: NetworkManager instance
  /// - Parameter requestName: Web request name
  /// - Parameter response: Web response of Dictionary format
  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?)

  /// Called when request failed, Handle errors in this delegate
  /// - Parameter manager:  NetworkManager instance
  /// - Parameter requestName: Web request name
  /// - Parameter error: Request error
  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError)
}

protocol NMAuthChallengeDelegate {

  /// Called when server throws for authentacation challenge
  /// - Parameter manager: NetworkManager instance
  /// - Parameter challenge: NSURLAuthenticationChallenge
  func networkCredential(_ manager: NetworkManager, challenge: URLAuthenticationChallenge)
    -> URLCredential

  ///  Called when request ask for authentication
  /// - Parameter manager: NetworkManager instance
  /// - Parameter challenge: NSURLAuthenticationChallenge
  func networkChallengeDisposition(
    _ manager: NetworkManager,
    challenge: URLAuthenticationChallenge
  )
    -> URLSession.AuthChallengeDisposition
}

class NetworkManager {

  static var instance: NetworkManager?
  var networkAvailability: Bool = true
  var reachability: Reachability?

  /// Checks network availability and returns bool value
  class func isNetworkAvailable() -> Bool {
    return self.sharedInstance().networkAvailability
  }

  /// Default Initializer
  init() {

    reachability = try? Reachability()
    NotificationCenter.default.addObserver(
      self,
      selector: #selector(reachabilityChanged(_:)),
      name: Notification.Name.reachabilityChanged,
      object: nil
    )

    do {
      try reachability?.startNotifier()
    } catch let error {
      Logger.sharedInstance.error(
        "could not start reachability notifier: ",
        error.localizedDescription
      )
    }
  }

  /// Create NetworkManager Instance
  class func sharedInstance() -> NetworkManager {
    self.instance = self.instance ?? NetworkManager()
    return self.instance!
  }

  /// Checks and return reachability status
  /// - Parameter notification: insance of Notification
  @objc func reachabilityChanged(_ notification: Notification) {

    if self.reachability!.connection != .unavailable {
      networkAvailability = true
    } else {
      networkAvailability = false
    }
    SyncUpdate.sharedInstance.updateData(isReachable: networkAvailability)
  }

  /// Compose request
  /// - Parameter requestName: requestName of type NSString
  /// - Parameter requestType: instance of type RequestType
  /// - Parameter method: instance of HttpMethod
  /// - Parameter params: parameters of type Dictionary
  /// - Parameter headers: headers of type Dictionary
  /// - Parameter delegate: NMWebServiceDelegate
  func composeRequest(
    _ requestName: NSString,
    requestType: RequestType,
    method: HTTPMethod,
    params: NSDictionary?,
    headers: NSDictionary?,
    delegate: NMWebServiceDelegate
  ) {

    let networkWSHandler = NetworkWebServiceHandler(
      delegate: delegate,
      challengeDelegate: UIApplication.shared.delegate as? NMAuthChallengeDelegate
    )
    networkWSHandler.networkManager = self
    networkWSHandler.composeRequestFor(
      requestName,
      requestType: requestType,
      method: method,
      params: params,
      headers: headers
    )

  }

  /// Compose request
  /// - Parameter configuration: instance of NetworkConfiguration
  /// - Parameter method: instance of Method
  /// - Parameter params: parameters of type Dictionary
  /// - Parameter headers: headers of type Dictionary
  /// - Parameter delegate: NMWebServiceDelegate
  func composeRequest(
    _ configuration: NetworkConfiguration,
    method: Method,
    params: NSDictionary?,
    headers: NSDictionary?,
    delegate: NMWebServiceDelegate
  ) {

    let networkWSHandler = NetworkWebServiceHandler(
      delegate: delegate,
      challengeDelegate: UIApplication.shared.delegate as? NMAuthChallengeDelegate
    )
    networkWSHandler.networkManager = self
    networkWSHandler.composeRequest(
      configuration,
      method: method,
      params: params,
      headers: headers
    )

  }

}
