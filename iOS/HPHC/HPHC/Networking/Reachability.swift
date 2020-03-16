// Copyright (c) 2014, Ashley Mills
// All rights reserved.
// 
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 
// 1. Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
// 
// 2. Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation
// and/or other materials provided with the distribution.
// 
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

import Foundation
import SystemConfiguration

public enum ReachabilityError: Error {
  case FailedToCreateWithAddress(sockaddr_in)
  case FailedToCreateWithHostname(String)
  case UnableToSetCallback
  case UnableToSetDispatchQueue
}

public let ReachabilityChangedNotification = NSNotification.Name("ReachabilityChangedNotification")

func callback(
  reachability: SCNetworkReachability,
  flags: SCNetworkReachabilityFlags,
  info: UnsafeMutableRawPointer?
) {

  guard let info = info else { return }

  let reachability = Unmanaged<Reachability>.fromOpaque(info).takeUnretainedValue()

  DispatchQueue.main.async {
    reachability.reachabilityChanged()
  }
}

public class Reachability {

  public typealias NetworkReachable = (Reachability) -> Void
  public typealias NetworkUnreachable = (Reachability) -> Void

  public enum NetworkStatus: CustomStringConvertible {

    case notReachable, reachableViaWiFi, reachableViaWWAN

    public var description: String {
      switch self {
      case .reachableViaWWAN: return "Cellular"
      case .reachableViaWiFi: return "WiFi"
      case .notReachable: return "No Connection"
      }
    }
  }

  public var whenReachable: NetworkReachable?
  public var whenUnreachable: NetworkUnreachable?
  public var reachableOnWWAN: Bool

  /// Notification center on which "reachability changed" events are being posted
  public var notificationCenter: NotificationCenter = NotificationCenter.default

  public var currentReachabilityString: String {
    return "\(currentReachabilityStatus)"
  }

  public var currentReachabilityStatus: NetworkStatus {
    guard isReachable else { return .notReachable }

    if isReachableViaWiFi {
      return .reachableViaWiFi
    }
    if isRunningOnDevice {
      return .reachableViaWWAN
    }

    return .notReachable
  }

  fileprivate var previousFlags: SCNetworkReachabilityFlags?

  fileprivate var isRunningOnDevice: Bool = {
    #if targetEnvironment(simulator)
      return false
    #else
      return true
    #endif
  }()

  fileprivate var notifierRunning = false
  fileprivate var reachabilityRef: SCNetworkReachability?

  fileprivate let reachabilitySerialQueue = DispatchQueue(label: "uk.co.ashleymills.reachability")

  required public init(reachabilityRef: SCNetworkReachability) {
    reachableOnWWAN = true
    self.reachabilityRef = reachabilityRef
  }

  public convenience init?(hostname: String) {

    guard let ref = SCNetworkReachabilityCreateWithName(nil, hostname) else { return nil }

    self.init(reachabilityRef: ref)
  }

  public convenience init?() {

    var zeroAddress = sockaddr()
    zeroAddress.sa_len = UInt8(MemoryLayout<sockaddr>.size)
    zeroAddress.sa_family = sa_family_t(AF_INET)

    guard
      let ref:SCNetworkReachability = withUnsafePointer(
        to: &zeroAddress,
        {
          SCNetworkReachabilityCreateWithAddress(nil, UnsafePointer($0))
        }
      )
    else { return nil }

    self.init(reachabilityRef: ref)
  }

  deinit {
    stopNotifier()

    reachabilityRef = nil
    whenReachable = nil
    whenUnreachable = nil
  }
}

// MARK: - *** Notifier methods ***
extension Reachability {

  public func startNotifier() throws {

    guard let reachabilityRef = reachabilityRef, !notifierRunning else { return }

    var context = SCNetworkReachabilityContext(
      version: 0,
      info: nil,
      retain: nil,
      release: nil,
      copyDescription: nil
    )
    context.info = UnsafeMutableRawPointer(
      Unmanaged<Reachability>.passUnretained(self).toOpaque()
    )
    if !SCNetworkReachabilitySetCallback(reachabilityRef, callback, &context) {
      stopNotifier()
      throw ReachabilityError.UnableToSetCallback
    }

    if !SCNetworkReachabilitySetDispatchQueue(reachabilityRef, reachabilitySerialQueue) {
      stopNotifier()
      throw ReachabilityError.UnableToSetDispatchQueue
    }

    // Perform an intial check
    reachabilitySerialQueue.async {
      self.reachabilityChanged()
    }

    notifierRunning = true
  }

  public func stopNotifier() {
    defer { notifierRunning = false }
    guard let reachabilityRef = reachabilityRef else { return }

    SCNetworkReachabilitySetCallback(reachabilityRef, nil, nil)
    SCNetworkReachabilitySetDispatchQueue(reachabilityRef, nil)
  }

  // MARK: - *** Connection test methods ***
  public var isReachable: Bool {

    guard isReachableFlagSet else { return false }

    if isConnectionRequiredAndTransientFlagSet {
      return false
    }

    if isRunningOnDevice {
      if isOnWWANFlagSet && !reachableOnWWAN {
        // We don't want to connect when on 3G.
        return false
      }
    }

    return true
  }

  public var isReachableViaWWAN: Bool {
    // Check we're not on the simulator, we're REACHABLE and check we're on WWAN
    return isRunningOnDevice && isReachableFlagSet && isOnWWANFlagSet
  }

  public var isReachableViaWiFi: Bool {

    // Check we're reachable
    guard isReachableFlagSet else { return false }

    // If reachable we're reachable, but not on an iOS device (i.e. simulator), we must be on WiFi
    guard isRunningOnDevice else { return true }

    // Check we're NOT on WWAN
    return !isOnWWANFlagSet
  }

  public var description: String {

    let W = isRunningOnDevice ? (isOnWWANFlagSet ? "W" : "-") : "X"
    let R = isReachableFlagSet ? "R" : "-"
    let c = isConnectionRequiredFlagSet ? "c" : "-"
    let t = isTransientConnectionFlagSet ? "t" : "-"
    let i = isInterventionRequiredFlagSet ? "i" : "-"
    let C = isConnectionOnTrafficFlagSet ? "C" : "-"
    let D = isConnectionOnDemandFlagSet ? "D" : "-"
    let l = isLocalAddressFlagSet ? "l" : "-"
    let d = isDirectFlagSet ? "d" : "-"

    return "\(W)\(R) \(c)\(t)\(i)\(C)\(D)\(l)\(d)"
  }
}

extension Reachability {

  fileprivate func reachabilityChanged() {

    let flags = reachabilityFlags

    guard previousFlags != flags else { return }

    let block = isReachable ? whenReachable : whenUnreachable
    block?(self)

    self.notificationCenter.post(name: ReachabilityChangedNotification, object: self)

    previousFlags = flags
  }

  fileprivate var isOnWWANFlagSet: Bool {
    #if os(iOS)
      return reachabilityFlags.contains(.isWWAN)
    #else
      return false
    #endif
  }

  fileprivate var isReachableFlagSet: Bool {
    return reachabilityFlags.contains(.reachable)
  }

  fileprivate var isConnectionRequiredFlagSet: Bool {
    return reachabilityFlags.contains(.connectionRequired)
  }

  fileprivate var isInterventionRequiredFlagSet: Bool {
    return reachabilityFlags.contains(.interventionRequired)
  }

  fileprivate var isConnectionOnTrafficFlagSet: Bool {
    return reachabilityFlags.contains(.connectionOnTraffic)
  }

  fileprivate var isConnectionOnDemandFlagSet: Bool {
    return reachabilityFlags.contains(.connectionOnDemand)
  }

  fileprivate var isConnectionOnTrafficOrDemandFlagSet: Bool {
    return !reachabilityFlags.intersection([.connectionOnTraffic, .connectionOnDemand]).isEmpty
  }

  fileprivate var isTransientConnectionFlagSet: Bool {
    return reachabilityFlags.contains(.transientConnection)
  }

  fileprivate var isLocalAddressFlagSet: Bool {
    return reachabilityFlags.contains(.isLocalAddress)
  }

  fileprivate var isDirectFlagSet: Bool {
    return reachabilityFlags.contains(.isDirect)
  }

  fileprivate var isConnectionRequiredAndTransientFlagSet: Bool {
    return reachabilityFlags.intersection([.connectionRequired, .transientConnection]) == [
      .connectionRequired, .transientConnection,
    ]
  }

  fileprivate var reachabilityFlags: SCNetworkReachabilityFlags {

    guard let reachabilityRef = reachabilityRef else { return SCNetworkReachabilityFlags() }

    var flags = SCNetworkReachabilityFlags()
    let gotFlags = withUnsafeMutablePointer(to: &flags) {
      SCNetworkReachabilityGetFlags(reachabilityRef, UnsafeMutablePointer($0))
    }

    if gotFlags {
      return flags
    } else {
      return SCNetworkReachabilityFlags()
    }
  }
}
