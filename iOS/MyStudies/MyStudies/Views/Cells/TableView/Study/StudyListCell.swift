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

import SDWebImage
import UIKit

class StudyListCell: UITableViewCell {

  @IBOutlet var labelStudyUserStatus: UILabel?
  @IBOutlet var labelStudyTitle: UILabel?
  @IBOutlet var labelCompletionValue: UILabel?
  @IBOutlet var labelStudyStatus: UILabel?
  @IBOutlet var progressBarCompletion: UIProgressView?
  @IBOutlet var studyLogoImage: UIImageView?
  @IBOutlet var studyUserStatusIcon: UIImageView?
  @IBOutlet var studyStatusIndicator: UIView?

  var selectedStudy: Study!

  private var placeholderImage: UIImage? {
    return UIImage(named: "placeholder")
  }
  /// Cell cleanup.
  override func prepareForReuse() {
    studyLogoImage?.image = placeholderImage
    progressBarCompletion?.isHidden = true
    labelCompletionValue?.isHidden = true
    super.prepareForReuse()
  }

  /// Used to change the cell background color.
  override func setSelected(_ selected: Bool, animated: Bool) {
    let color = studyStatusIndicator?.backgroundColor
    super.setSelected(selected, animated: animated)
    // Configure the view for the selected state
    if selected {
      studyStatusIndicator?.backgroundColor = color
    }
  }

  ///  Used to set the cell state ie Highlighted.
  override func setHighlighted(_ highlighted: Bool, animated: Bool) {
    let color = studyStatusIndicator?.backgroundColor
    super.setHighlighted(highlighted, animated: animated)
    if highlighted {
      studyStatusIndicator?.backgroundColor = color
    }
  }

  /// Used to populate the cell data.
  /// - Parameter study: Access the data from Study class.
  func populateCellWith(study: Study) {

    selectedStudy = study
    labelStudyTitle?.text = study.name
    updateStudyImage(study)

    progressBarCompletion?.layer.cornerRadius = 2
    progressBarCompletion?.layer.masksToBounds = true

    // study status
    self.setStudyStatus(study: study)

    if User.currentUser.userType == .anonymousUser {
      // do nothing
    } else {
      // set participatedStudies
      self.setUserStatusForStudy(study: study)
    }
  }

  /// Used to set the Study State.
  /// - Parameter study: Access the data from Study Class.
  func setStudyStatus(study: Study) {

    labelStudyStatus?.text = study.status.rawValue

    switch study.status {
    case .active:
      studyStatusIndicator?.backgroundColor = Utilities.getUIColorFromHex(0x4caf50)  //green
    case .closed:
      studyStatusIndicator?.backgroundColor = Utilities.getUIColorFromHex(0xFF0000)  //red color
    case .paused:
      studyStatusIndicator?.backgroundColor = Utilities.getUIColorFromHex(0xf5af37)  //orange color
    }
  }

  /// Used to set UserStatus ForStudy.
  /// - Parameter study: Access the data from Study Class.
  func setUserStatusForStudy(study: Study) {
    let currentUser = User.currentUser
    if let userStudyStatus = currentUser.participatedStudies.filter({ $0.studyId == study.studyId })
      .first
    {

      // assign to study
      study.userParticipateState = userStudyStatus

      // user study status
      switch study.status {
      case .active:
        labelStudyUserStatus?.text = userStudyStatus.status.description
      case .closed:
        labelStudyUserStatus?.text = userStudyStatus.status.closedStudyDescription
      default:
        labelStudyUserStatus?.text = userStudyStatus.status.description
      }
      if userStudyStatus.status == .enrolled {
        // update completion %
        self.labelCompletionValue?.text = String(userStudyStatus.completion) + "%"
        self.progressBarCompletion?.progress = Float(userStudyStatus.completion) / 100
        self.labelCompletionValue?.isHidden = false
        self.progressBarCompletion?.isHidden = false
      }

      switch userStudyStatus.status {
      case .enrolled:
        studyUserStatusIcon?.image = #imageLiteral(resourceName: "in_progress_icn")
      case .yetToEnroll:
        studyUserStatusIcon?.image = #imageLiteral(resourceName: "yet_to_join_icn")
      case .notEligible:
        studyUserStatusIcon?.image = #imageLiteral(resourceName: "not_eligible_icn")
      case .withdrawn:
        studyUserStatusIcon?.image = #imageLiteral(resourceName: "withdrawn_icn1")
      case .completed:
        studyUserStatusIcon?.image = #imageLiteral(resourceName: "completed_icn")

      }
    } else {
      study.userParticipateState = UserStudyStatus()
      labelStudyUserStatus?.text = UserStudyStatus.StudyStatus.yetToEnroll.description
      studyUserStatusIcon?.image = #imageLiteral(resourceName: "yet_to_join_icn")
    }
  }

  /// Updates the icon for `Study`
  /// - Parameter study: Instance of Study.
  fileprivate func updateStudyImage(_ study: Study) {
    // Update study logo using SDWEBImage and cache it.
    if let logoURLString = study.logoURL,
      let url = URL(string: logoURLString)
    {
      studyLogoImage?.sd_setImage(
        with: url,
        placeholderImage: placeholderImage,
        options: .progressiveLoad,
        completed: { [weak self] (image, _, _, _) in
          if let image = image {
            self?.studyLogoImage?.image = image
          }
        }
      )
    }
  }
}
