// Copyright (c) 2015, Apple Inc. All rights reserved.
//
// Redistribution and use in source and binary forms, with or without modification,
// are permitted provided that the following conditions are met:
//
// 1.  Redistributions of source code must retain the above copyright notice, this
// list of conditions and the following disclaimer.
//
// 2.  Redistributions in binary form must reproduce the above copyright notice,
// this list of conditions and the following disclaimer in the documentation and/or
// other materials provided with the distribution.
//
// 3.  Neither the name of the copyright holder(s) nor the names of any contributors
// may be used to endorse or promote products derived from this software without
// specific prior written permission. No license is granted to the trademarks of
// the copyright holders even if such marks are included in this software.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
// FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
// SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
// CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
// OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

import ResearchKit
import UIKit

class ResearchContainerViewController: UIViewController, HealthClientType {

  // MARK: - Properties

  /// HealthClientType
  var healthStore: HKHealthStore?

  var contentHidden = false {
    didSet {
      guard contentHidden != oldValue && isViewLoaded else { return }
      children.first?.view.isHidden = contentHidden
    }
  }

  // MARK: - ViewController Lifecycle

  override func viewDidLoad() {
    super.viewDidLoad()

    if ORKPasscodeViewController.isPasscodeStoredInKeychain() {
      toStudy()
    } else {
      toOnboarding()
    }
  }

  override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
    super.prepare(for: segue, sender: sender)

    if let healthStore = healthStore {
      segue.destination.injectHealthStore(healthStore)
    }
  }

  // MARK: - Unwind segues Actions

  @IBAction func unwindToStudy(_ segue: UIStoryboardSegue) {
    toStudy()
  }

  @IBAction func unwindToWithdrawl(_ segue: UIStoryboardSegue) {
    toWithdrawl()
  }

  // MARK: - Transitions

  func toOnboarding() {
    performSegue(withIdentifier: "toOnboarding", sender: self)
  }

  func toStudy() {
    performSegue(withIdentifier: "toStudy", sender: self)
  }

  func toWithdrawl() {
  }
}

extension ResearchContainerViewController: ORKTaskViewControllerDelegate {

  public func taskViewController(
    _ taskViewController: ORKTaskViewController,
    didFinishWith reason: ORKTaskViewControllerFinishReason,
    error: Error?
  ) {
  }

}
