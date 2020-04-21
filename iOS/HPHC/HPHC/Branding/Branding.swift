//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

enum Branding {

  private enum JSONKey {
    static let joinStudyButtonTitle = "JoinStudyButtonTitle"
    static let viewConsentButtonTitle = "ViewConsentButtonTitle"
    static let visitWebsiteButtonTitle = "VisitWebsiteButtonTitle"
    static let consentPDF = "ConsentPDF"
    static let leaveStudy = "LeaveStudy"
    static let leaveStudyConfirmationText = "LeaveStudyConfirmationText"
    static let websiteLink = "WebsiteLink"
    static let websiteButtonTitle = "WebsiteButtonTitle"
    static let termsAndConditionURL = "TermsAndConditionURL"
    static let privacyPolicyURL = "PrivacyPolicyURL"
    static let validatedTitle = "ValidatedTitle"
    static let allowFeedback = "AllowFeedback"
    static let navigationTitleName = "NavigationTitleName"
    static let poweredByTitleName = "PoweredByTitleName"
    static let productTitleNAme = "ProductTitleName"
  }

  private static var brandConfig: JSONDictionary {
    var nsDictionary: NSDictionary?
    if let path = Bundle.main.path(forResource: "Branding", ofType: "plist") {
      nsDictionary = NSDictionary(contentsOfFile: path)
    }
    return nsDictionary as? JSONDictionary ?? [:]
  }

  static var joinStudyButtonTitle: String {
    return brandConfig[JSONKey.joinStudyButtonTitle] as? String ?? ""
  }

  static var viewConsentButtonTitle: String {
    return brandConfig[JSONKey.viewConsentButtonTitle] as? String ?? ""
  }

  static var visitWebsiteButtonTitle: String {
    return brandConfig[JSONKey.visitWebsiteButtonTitle] as? String ?? ""
  }

  static var consentPDFTitle: String {
    return brandConfig[JSONKey.consentPDF] as? String ?? ""
  }

  static var leaveStudyTitle: String {
    return brandConfig[JSONKey.leaveStudy] as? String ?? ""
  }

  static var leaveStudyConfirmationText: String {
    return brandConfig[JSONKey.leaveStudyConfirmationText] as? String ?? ""
  }

  static var websiteLink: String {
    return brandConfig[JSONKey.websiteLink] as? String ?? ""
  }

  static var websiteButtonTitle: String {
    return brandConfig[JSONKey.websiteButtonTitle] as? String ?? Branding.websiteLink
  }

  static var termsAndConditionURL: String {
    return brandConfig[JSONKey.termsAndConditionURL] as? String ?? ""
  }

  static var privacyPolicyURL: String {
    return brandConfig[JSONKey.privacyPolicyURL] as? String ?? ""
  }

  static var validatedTitle: String {
    return brandConfig[JSONKey.validatedTitle] as? String ?? ""
  }

  static var allowFeedback: Bool {
    return brandConfig[JSONKey.allowFeedback] as? Bool ?? true
  }

  static var navigationTitleName: String {
    return brandConfig[JSONKey.navigationTitleName] as? String ?? ""
  }

  static var poweredByTitleName: String {
    return brandConfig[JSONKey.poweredByTitleName] as? String ?? ""
  }

  /// AppName
  static var productTitle: String {
    return brandConfig[JSONKey.productTitleNAme] as? String ?? ""
  }
}
