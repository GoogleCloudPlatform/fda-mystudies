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

protocol StudyListDelegates: class {
  func studyBookmarked(_ cell: StudyListCell, bookmarked: Bool, forStudy study: Study)
}

class StudyListCell: UITableViewCell {

  @IBOutlet var labelStudyUserStatus: UILabel?
  @IBOutlet var labelStudyTitle: UILabel?
  @IBOutlet var labelStudyShortDescription: UILabel?
  @IBOutlet var labelStudySponserName: UILabel?
  @IBOutlet var labelCompletionValue: UILabel?
  @IBOutlet var labelAdherenceValue: UILabel?
  @IBOutlet var labelStudyStatus: UILabel?
  @IBOutlet var buttonBookmark: UIButton?
  @IBOutlet var progressBarCompletion: UIProgressView?
  @IBOutlet var progressBarAdherence: UIProgressView?
  @IBOutlet var studyLogoImage: UIImageView?
  @IBOutlet var studyUserStatusIcon: UIImageView?
  @IBOutlet var studyStatusIndicator: UIView?

  var selectedStudy: Study!
  weak var delegate: StudyListDelegates?

  private var placeholderImage: UIImage? {
    return UIImage(named: "placeholder")
  }
  /// Cell cleanup.
  override func prepareForReuse() {
    super.prepareForReuse()
    studyLogoImage?.image = placeholderImage
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

    labelStudyShortDescription?.text = study.description
    if study.sponserName != nil {
      labelStudySponserName?.text = study.sponserName!
    }

    progressBarCompletion?.layer.cornerRadius = 2
    progressBarCompletion?.layer.masksToBounds = true

    let attributedString =
      labelStudySponserName?.attributedText?.mutableCopy()
      as! NSMutableAttributedString

    let foundRange = attributedString.mutableString.range(of: study.category!)
    attributedString.addAttributes(
      [NSAttributedString.Key.font: UIFont(name: "HelveticaNeue-Bold", size: 12)!],
      range: foundRange
    )
    labelStudySponserName?.attributedText = attributedString

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

    labelStudyStatus?.text = study.status.rawValue.uppercased()

    switch study.status {
    case .active:
      studyStatusIndicator?.backgroundColor = Utilities.getUIColorFromHex(0x4caf50)  //green
    case .upcoming:
      studyStatusIndicator?.backgroundColor = Utilities.getUIColorFromHex(0x007cba)  //app color
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
      case .upcoming:
        labelStudyUserStatus?.text = userStudyStatus.status.upcomingStudyDescription
      default:
        labelStudyUserStatus?.text = userStudyStatus.status.description
      }

      // update completion %
      self.labelCompletionValue?.text = String(userStudyStatus.completion) + "%"
      self.labelAdherenceValue?.text = String(userStudyStatus.adherence) + "%"
      self.progressBarCompletion?.progress = Float(userStudyStatus.completion) / 100
      self.progressBarAdherence?.progress = Float(userStudyStatus.adherence) / 100

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

      // bookMarkStatus
      buttonBookmark?.isSelected = userStudyStatus.bookmarked
    } else {
      study.userParticipateState = UserStudyStatus()
      labelStudyUserStatus?.text = UserStudyStatus.StudyStatus.yetToEnroll.description
      studyUserStatusIcon?.image = #imageLiteral(resourceName: "yet_to_join_icn")
      buttonBookmark?.isSelected = false
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

  // MARK: - Button Actions

  /// Button bookmark clicked and delegate it back to Study home and
  /// Study list View controller.
  @IBAction func buttonBookmardAction(_ sender: UIButton) {
    if sender.isSelected {
      sender.isSelected = false
    } else {
      sender.isSelected = true
    }
    delegate?.studyBookmarked(self, bookmarked: sender.isSelected, forStudy: self.selectedStudy)
  }
}
