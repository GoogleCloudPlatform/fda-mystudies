//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation
import UIKit

class ConsentServices: NSObject {

  let networkManager = NetworkManager.sharedInstance()
  weak var delegate: NMWebServiceDelegate?
  var requestParams: [String: Any]? = [:]
  var headerParams: [String: String]? = [:]
  var method: Method!

  // MARK: Requests

  /// Creates a request to update Consent status
  /// - Parameters:
  ///   - eligibilityStatus:
  ///   - consentStatus: Instance of `ConsentStatus`
  ///   - delegate: Class object to receive response
  func updateUserEligibilityConsentStatus(
    eligibilityStatus: Bool,
    consentStatus: ConsentStatus,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate

    let user = User.currentUser
    let headerParams = [
      kUserId: user.userId! as String,
      kUserAuthToken: user.authToken! as String,
    ]

    let currentConsent = ConsentBuilder.currentConsent
    let consentResult = currentConsent?.consentResult

    var consentVersion: String
    if let version = currentConsent?.version, !version.isEmpty {
      consentVersion = version
    } else {
      consentVersion = "1"
    }

    var userDataSharing: String
    if let isShareData = consentResult?.isShareDataWithPublic {
      userDataSharing = isShareData ? "Provided" : "Not Provided"
    } else {
      userDataSharing = "Not Applicable"
    }

    let base64data =
      consentResult?.consentPdfData?
      .base64EncodedString() ?? ""
    
    
    //Use image name from bundle to create NSData
//    let image : UIImage = UIImage(named:"imageNameHere")!
//    //Now use image to create into NSData format
//    let imageData:NSData = UIImagePNGRepresentation(image)!

//    //OR next possibility
//
//    //Use image's path to create NSData
//    let url:NSURL = NSURL(string : "urlHere")!
//    //Now use image to create into NSData format
//    let imageData:NSData = NSData.init(contentsOfURL: url)!

    
//    let img = UIImage(named:"leftIconBlue2") ?? UIImage()
    let img = loadImageFromDocumentDirectory(fileName: "ConsentSharingImage") ?? UIImage()
    
    let base64data2 = convertImageToBase64String(img: img)
    

    let consent =
      [
        kConsentDocumentVersion: consentVersion,
        kStatus: consentStatus.rawValue,
        kConsentpdf: base64data,
      ] as [String: Any]

    let params =
      [
        kStudyId: Study.currentStudy?.studyId ?? "",
        kEligibility: eligibilityStatus,
        "siteId": Study.currentStudy?.userParticipateState.siteID ?? "",
        kConsent: consent,
        kConsentSharing: userDataSharing,
        "dataSharingScreenShot": base64data2,
      ] as [String: Any]
    let method = ConsentServerMethods.updateEligibilityConsentStatus.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }
  
  func loadImageFromDocumentDirectory(fileName: String) -> UIImage? {

          let documentsUrl = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first!;
          let fileURL = documentsUrl.appendingPathComponent(fileName)
          do {
              let imageData = try Data(contentsOf: fileURL)
              return UIImage(data: imageData)
          } catch {}
          return nil
      }
  
  func convertImageToBase64String (img: UIImage) -> String {
      return img.jpegData(compressionQuality: 1)?.base64EncodedString() ?? ""
  }

  
//  func captureScreen() -> UIImage {
//
//      UIGraphicsBeginImageContextWithOptions(self.view.bounds.size, false, 0);
//
//      self.view.drawViewHierarchyInRect(view.bounds, afterScreenUpdates: true)
//
//      let image: UIImage = UIGraphicsGetImageFromCurrentImageContext()
//
//      UIGraphicsEndImageContext()
//
//      return image
//  }


  /// Creates a request to get Consent pdf
  /// - Parameters:
  ///   - studyId: ID of `Study`
  ///   - delegate: Class object to receive response
  func getConsentPDFForStudy(
    studyId: String,
    consentVersion: String,
    delegate: NMWebServiceDelegate
  ) {

    self.delegate = delegate

    let user = User.currentUser
    let params = [
      kStudyId: studyId,
      "consentVersion": "",
    ]

    let headerParams = [kUserId: user.userId!]
    let method = ConsentServerMethods.consentDocument.method

    self.sendRequestWith(method: method, params: params, headers: headerParams)
  }

  func updateToken(manager: NetworkManager, requestName: NSString, error: NSError) {
    HydraAPI.refreshToken { (status, error) in
      if status {
        self.handleUpdateTokenResponse()
      } else if let error = error {
        self.delegate?.failedRequest(
          manager,
          requestName:
            requestName,
          error:
            error.toNSError()
        )
      }
    }
  }

  // MARK: Parsers
  func handleUpdateTokenResponse() {
    self.sendRequestWith(
      method: self.method,
      params: self.requestParams,
      headers: self.headerParams
    )
  }

  /// Sends Request
  /// - Parameters:
  ///   - method: instance of `Method`
  ///   - params: request params
  ///   - headers: request headers
  private func sendRequestWith(method: Method, params: [String: Any]?, headers: [String: String]?) {

    self.requestParams = params
    self.headerParams = headers
    self.method = method
    networkManager.composeRequest(
      ConsentServerConfiguration.configuration,
      method: method,
      params: params as NSDictionary?,
      headers: headers as NSDictionary?,
      delegate: self
    )
  }

}
extension ConsentServices: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    delegate?.startedRequest(manager, requestName: requestName)
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    switch requestName {
    case ConsentServerMethods.updateEligibilityConsentStatus.description as String: break
    case ConsentServerMethods.consentDocument.description as String: break
    default: break
    }

    delegate?.finishedRequest(manager, requestName: requestName, response: response)

  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    if error.code == HTTPError.tokenExpired.rawValue {
      self.updateToken(manager: manager, requestName: requestName, error: error)
    } else {
      var errorInfo = error.userInfo
      var localError = error
      if error.code == HTTPError.forbidden.rawValue {
        errorInfo = ["NSLocalizedDescription": LocalizableString.sessionExpired.localizedString]
        localError = NSError.init(domain: error.domain, code: 403, userInfo: errorInfo)
      }
      delegate?.failedRequest(manager, requestName: requestName, error: localError)
    }
  }
}
