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
import UIKit

struct FeedbackDetail {

  static var feedback: String = ""
  static var subject: String = ""

  init() {
    FeedbackDetail.feedback = ""
    FeedbackDetail.subject = ""
  }
}

class FeedBackViewController: UIViewController {

  // MARK: - Outlets
  @IBOutlet var buttonSubmit: UIButton?

  @IBOutlet var tableView: UITableView?

  // MARK: - Properties
  var feedbackText: String = ""

  // MARK: - ViewController Lifecycle
  override func viewDidLoad() {
    super.viewDidLoad()

    self.navigationItem.title = NSLocalizedString("Leave us your feedback", comment: "")

    // Used to set border color for bottom view
    buttonSubmit?.layer.borderColor = kUicolorForButtonBackground

    self.tableView?.estimatedRowHeight = 123
    self.tableView?.rowHeight = UITableView.automaticDimension

    self.addBackBarButton()

    _ = FeedbackDetail.init()
  }

  // MARK: - Button Actions

  /// Validations after clicking on submit button
  /// If all the validations satisfy send user feedback request
  /// - Parameter sender: Instance of submit UIButton.
  @IBAction func buttonSubmitAciton(_ sender: UIButton) {
    self.view.endEditing(true)

    if FeedbackDetail.subject.isEmpty && FeedbackDetail.feedback.isEmpty {
      UIUtilities.showAlertWithMessage(
        alertMessage: NSLocalizedString(kMessageAllFieldsAreEmpty, comment: "")
      )
    } else if FeedbackDetail.subject.isEmpty {
      UIUtilities.showAlertWithMessage(
        alertMessage: NSLocalizedString("Please enter the message", comment: "")
      )
    } else if FeedbackDetail.feedback.isEmpty {
      UIUtilities.showAlertWithMessage(
        alertMessage: NSLocalizedString("Please provide your feedback", comment: "")
      )
    } else {
      UserServices().sendUserFeedback(delegate: self)
    }
  }
}

// MARK: - TableView Datasource
extension FeedBackViewController: UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return 3
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    var cell: UITableViewCell?

    if indexPath.row == 0 {
      cell =
        tableView.dequeueReusableCell(
          withIdentifier: kFeedbackTableViewCellIdentifier1,
          for: indexPath
        )
        as! FeedBackTableViewCell

    } else if indexPath.row == 1 {
      let cell =
        tableView.dequeueReusableCell(
          withIdentifier: kContactUsTableViewCellIdentifier,
          for: indexPath
        )
        as! ContactUsTableViewCell
      cell.textFieldValue?.tag = indexPath.row
      return cell
    } else {
      cell =
        tableView.dequeueReusableCell(withIdentifier: "textviewCell", for: indexPath)
        as! TextviewCell

    }

    return cell ?? UITableViewCell()
  }
}

// MARK: - TableView Delegates
extension FeedBackViewController: UITableViewDelegate {

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    tableView.deselectRow(at: indexPath, animated: true)
  }
}

// MARK: - UITextview Delegate
extension FeedBackViewController: UITextViewDelegate {

  func textViewDidChange(_ textView: UITextView) {
    let currentOffset = tableView?.contentOffset
    UIView.setAnimationsEnabled(false)
    tableView?.beginUpdates()
    tableView?.endUpdates()
    UIView.setAnimationsEnabled(true)
    tableView?.setContentOffset(currentOffset!, animated: false)
  }

  func textViewDidEndEditing(_ textView: UITextView) {
    if textView.tag == 101 && textView.text.count == 0 {
      textView.text = "Enter your feedback here"
      textView.textColor = UIColor.lightGray
      textView.tag = 100
    } else {
      //self.feedbackText = textView.text!
      FeedbackDetail.feedback = textView.text!
    }
  }

  func textViewDidBeginEditing(_ textView: UITextView) {

    if textView.tag == 100 {
      textView.text = ""
      textView.textColor = UIColor.black
      textView.tag = 101
    }
  }
}

// MARK: - Textfield Delegate
extension FeedBackViewController: UITextFieldDelegate {

  func textFieldDidEndEditing(_ textField: UITextField) {
    textField.text = textField.text?.trimmingCharacters(in: CharacterSet.whitespacesAndNewlines)
    FeedbackDetail.subject = textField.text!
  }

  func textField(
    _ textField: UITextField,
    shouldChangeCharactersIn range: NSRange,
    replacementString string: String
  ) -> Bool {
    if var text = textField.text,
      range.location == text.count,
      string == " "
    {
      let noBreakSpace: Character = "\u{00a0}"
      text.append(noBreakSpace)
      textField.text = text
      return false
    }
    return true
  }
}

// MARK: Webservice Delegates
extension FeedBackViewController: NMWebServiceDelegate {

  func startedRequest(_ manager: NetworkManager, requestName: NSString) {
    self.addProgressIndicator()
  }

  func finishedRequest(_ manager: NetworkManager, requestName: NSString, response: AnyObject?) {
    self.removeProgressIndicator()

    UIUtilities.showAlertMessageWithActionHandler(
      "",
      message: NSLocalizedString(kMessageFeedbackSubmittedSuccessfuly, comment: ""),
      buttonTitle: kTitleOk,
      viewControllerUsed: self
    ) {
      _ = self.navigationController?.popViewController(animated: true)
    }
  }

  func failedRequest(_ manager: NetworkManager, requestName: NSString, error: NSError) {

    self.removeProgressIndicator()
    UIUtilities.showAlertWithTitleAndMessage(
      title: NSLocalizedString("Error", comment: "") as NSString,
      message: error.localizedDescription as NSString
    )
  }
}
