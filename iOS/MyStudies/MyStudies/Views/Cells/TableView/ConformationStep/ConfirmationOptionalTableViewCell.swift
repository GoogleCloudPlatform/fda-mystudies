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

let kDeleteButtonTag = 11220
let kRetainButtonTag = 11221

let kConfirmationOptionalDefaultTypeRetain = "retain"
let kConfirmationOptionalDefaultTypeDelete = "delete"

class ConfirmationOptionalTableViewCell: UITableViewCell {

  // MARK: - Outlets
  @IBOutlet var buttonDeleteData: UIButton!
  @IBOutlet var buttonRetainData: UIButton!
  @IBOutlet var labelTitle: UILabel!
  @IBOutlet var imageViewDeleteCheckBox: UIImageView!
  @IBOutlet var imageViewRetainCheckBox: UIImageView!

  // MARK: - Properties
  var study: StudyToDelete?

  // MARK: - Utils

  func configureCell(with study: StudyToDelete) {
    if let deleteData = study.shouldDelete, deleteData {
      if deleteData {
        imageViewRetainCheckBox?.image = #imageLiteral(resourceName: "notChecked")
        imageViewDeleteCheckBox?.image = #imageLiteral(resourceName: "checked")
      } else {
        imageViewRetainCheckBox?.image = #imageLiteral(resourceName: "checked")
        imageViewDeleteCheckBox?.image = #imageLiteral(resourceName: "notChecked")
      }
    } else {
      imageViewRetainCheckBox?.image = #imageLiteral(resourceName: "notChecked")
      imageViewDeleteCheckBox?.image = #imageLiteral(resourceName: "notChecked")
    }
    self.study = study
    self.labelTitle.text = study.name
  }

  // MARK: - Actions

  /// When user press on Delete data or Retail Data button
  @IBAction func deleteOrRetainDataButtonAction(_ sender: UIButton) {

    var deleteData = false
    if sender.tag == kDeleteButtonTag {
      imageViewDeleteCheckBox.image = #imageLiteral(resourceName: "checked")
      imageViewRetainCheckBox.image = #imageLiteral(resourceName: "notChecked")
      deleteData = true
    } else {
      imageViewRetainCheckBox.image = #imageLiteral(resourceName: "checked")
      imageViewDeleteCheckBox.image = #imageLiteral(resourceName: "notChecked")
      deleteData = false
    }
    study?.shouldDelete = deleteData
  }

}
