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

let kConfirmationSegueIdentifier = "confirmationSegue"
let kHeaderDescription =
  """
  You have chosen to delete your app account. \
  This will result in automatic withdrawal from the studies you were enrolled in.
  """

let kHeaderDescriptionStandalone =
  """
  You have chosen to delete your app account. \
  This will result in automatic withdrawal from the studies you were enrolled in.
  """

let kConfirmWithdrawlSelectOptionsAlert =
  "Please select a data retention preference for all studies in the list."
let kResponseDataDeletedText = "Response data will be deleted"
let kResponseDataRetainedText = "Response data will be retained"

let kConfirmationCellType = "type"
let kConfirmationCellTypeOptional = "Optional"
let kConfrimationOptionalCellIdentifier = "ConfirmationOptionalCell"
let kConfrimationCellIdentifier = "ConfirmationCell"
let kConfirmationTitle = "title"
let kConfirmationPlaceholder = "placeHolder"
let kConfirmationPlist = "Confirmation"
let kConfirmationNavigationTitle = "DELETE ACCOUNT"
let kPlistFileType = ".plist"

class StudyToDelete {
  var studyId: String
  var shouldDelete: Bool?
  var participantId: String
  var name: String
  var withdrawalConfigration: StudyWithdrawalConfigration
  internal init(
    studyId: String,
    participantId: String,
    studyName: String,
    withdrawalConfigration: StudyWithdrawalConfigration
  ) {
    self.studyId = studyId
    self.participantId = participantId
    self.name = studyName
    self.withdrawalConfigration = withdrawalConfigration
  }
}

class ConfirmationViewController: UIViewController {

  // MARK: - Outlets

  @IBOutlet var buttonDeleteAccount: UIButton?
  @IBOutlet var buttonDoNotDeleteAccount: UIButton?
  @IBOutlet var headerDescriptionLbl: UILabel?

  // MARK: - Properties
  lazy var studiesToDisplay: [Study] = []
  lazy var joinedStudies: [Study]! = []
  var studyWithoutWCData: Study?
  lazy var studiesToWithdraw: [StudyToDelete] = []

  // MARK: - View Controller LifeCycle

  override func viewDidLoad() {
    super.viewDidLoad()

    let navTitle = Branding.productTitle
    var descriptionText =
      Utilities.isStandaloneApp()
      ? kHeaderDescriptionStandalone : kHeaderDescription
    descriptionText = descriptionText.replacingOccurrences(of: "#APPNAME#", with: navTitle)

    // setting the headerdescription
    self.headerDescriptionLbl?.text = descriptionText

    // setting border color for footer buttons
    self.buttonDeleteAccount?.layer.borderColor = kUicolorForButtonBackground
    self.buttonDoNotDeleteAccount?.layer.borderColor = kUicolorForButtonBackground

    self.navigationController?.setNavigationBarHidden(false, animated: true)

    self.title = NSLocalizedString(kConfirmationNavigationTitle, comment: "")

    self.checkWithdrawlConfigurationForNextStudy()

  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    self.addBackBarButton()
  }

  // MARK: - Utils

  private func checkWithdrawlConfigurationForNextStudy() {

    if let study = joinedStudies.first {
      if study.withdrawalConfigration?.type == StudyWithdrawalConfigrationType.notAvailable {
        Study.updateCurrentStudy(study: study)
        self.sendRequestToGetInfoForStudy(study: study)
      } else {
        studiesToDisplay.append(study)
        joinedStudies.removeFirst()
        self.checkWithdrawlConfigurationForNextStudy()
      }
    } else {
      self.removeProgressIndicator()
      self.createListOfStudiesToDelete()
    }
  }

  private func createListOfStudiesToDelete() {

    for study in studiesToDisplay {
      let studyId = study.studyId ?? ""
      let participantId = study.userParticipateState.participantId ?? ""
      let studyName = study.name ?? ""
      let withdrawalConfigration = study.withdrawalConfigration ?? StudyWithdrawalConfigration()
      let withdrawnStudy = StudyToDelete(
        studyId: studyId,
        participantId: participantId,
        studyName: studyName,
        withdrawalConfigration: withdrawalConfigration
      )
      withdrawnStudy.shouldDelete = false
      studiesToWithdraw.append(withdrawnStudy)
    }
  }

  // MARK: - Webservice Response Handlers
  private func sendRequestToGetInfoForStudy(study: Study) {
    WCPServices().getStudyInformation(studyId: study.studyId, delegate: self)
  }

  /// Handle delete account webservice response.
  private func handleDeleteAccountResponse() {

    ORKPasscodeViewController.removePasscodeFromKeychain()

    let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
    appDelegate.updateKeyAndInitializationVector()

    UIUtilities.showAlertMessageWithActionHandler(
      NSLocalizedString(kTitleMessage, comment: ""),
      message: NSLocalizedString(kMessageAccountDeletedSuccess, comment: ""),
      buttonTitle: NSLocalizedString(kTitleOk, comment: ""),
      viewControllerUsed: self
    ) { [weak self] in
      UIApplication.shared.keyWindow?.addProgressIndicatorOnWindowFromTop()
      Study.currentStudy = nil
      self?.slideMenuController()?.leftViewController?.navigationController?
        .popToRootViewController(
          animated: true
        )
      DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
        UIApplication.shared.keyWindow?.removeProgressIndicatorFromWindow()
      }
    }
  }

  /// Update the properties with the webservice response.
  private func handleStudyInformationResonse() {
    studiesToDisplay.append(Study.currentStudy!)
    joinedStudies.removeFirst()
    self.checkWithdrawlConfigurationForNextStudy()
  }

  // MARK: - Button Actions

  /// Delete account button clicked.
  @IBAction func deleteAccountAction(_ sender: UIButton) {

    var found: Bool = false
    for withdrawnStudy in studiesToWithdraw where withdrawnStudy.shouldDelete == nil {
      UIUtilities.showAlertWithMessage(
        alertMessage: NSLocalizedString(
          kConfirmWithdrawlSelectOptionsAlert,
          comment: ""
        )
      )
      found = true
      break
    }
    if !found {
      self.withdrawnFromNextStudy()
    }
  }

  func withdrawnFromNextStudy() {
    UserServices().deActivateAccount(studiesToDelete: studiesToWithdraw, delegate: self)
  }

  /// Don't Delete button action.
  @IBAction func doNotDeleteAccountAction(_ sender: UIButton) {
    _ = self.navigationController?.popViewController(animated: true)
  }
}

// MARK: - UserService Response handler
extension ConfirmationViewController: NMWebServiceDelegate {
  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    self.addProgressIndicator()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    if requestName as String == RegistrationMethods.deactivate.description {
      self.removeProgressIndicator()
      self.handleDeleteAccountResponse()
    } else if requestName as String == WCPMethods.studyInfo.rawValue {
      self.handleStudyInformationResonse()
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    if error.code == HTTPError.forbidden.rawValue {  //unauthorized
      self.removeProgressIndicator()
      UIUtilities.showAlertMessageWithActionHandler(
        kErrorTitle,
        message: error.localizedDescription,
        buttonTitle: kTitleOk,
        viewControllerUsed: self,
        action: {
          self.fdaSlideMenuController()?.navigateToHomeAfterUnauthorizedAccess()
        }
      )
    } else {
      if requestName as String == WCPMethods.studyInfo.rawValue {
        self.removeProgressIndicator()
      } else {
        self.removeProgressIndicator()
        UIUtilities.showAlertWithTitleAndMessage(
          title: NSLocalizedString(kErrorTitle, comment: "") as NSString,
          message: error.localizedDescription as NSString
        )
      }
    }
  }
}
