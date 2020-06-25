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
import UIKit

// Used to do filter based on Apply and Cancel actions
protocol StudyFilterDelegates: class {

  func appliedFilter(
    studyStatus: [String],
    pariticipationsStatus: [String],
    categories: [String],
    searchText: String,
    bookmarked: Bool
  )

  func didCancelFilter(_ cancel: Bool)

}

enum FilterType: String {
  case participantStatus
  case studyStatus
  case bookmark
  case category
}

struct StudyFilter {
  var studyStatus: [String] = []
  var pariticipationsStatus: [String] = []
  var categories: [String] = []
  var bookmark = true
  var searchText = ""
}

class StudyFilterViewController: UIViewController {

  // MARK: - Outlets

  @IBOutlet weak var collectionView: UICollectionView?

  @IBOutlet weak var cancelButton: UIButton?
  @IBOutlet weak var applyButton: UIButton?

  // MARK: - Properties

  weak var delegate: StudyFilterDelegates?

  private lazy var studyStatus: [String] = []
  private lazy var pariticipationsStatus: [String] = []
  private lazy var categories: [String] = []
  private lazy var searchText: String = ""
  private lazy var bookmark = true
  private var filterTypes: [FilterType] {
    if User.currentUser.userType == .loggedInUser {
      return [.participantStatus, .studyStatus, .bookmark]
    } else {
      return [.studyStatus]
    }
  }

  lazy var previousCollectionData: [[String]] = []

  // MARK: - Viewcontroller lifecycle
  override func viewDidLoad() {
    super.viewDidLoad()

    applyButton?.layer.borderColor = kUicolorForButtonBackground
    cancelButton?.layer.borderColor = kUicolorForCancelBackground

    if let layout = collectionView?.collectionViewLayout as? PinterestLayout {
      layout.delegate = self
    }

    if StudyFilterHandler.instance.filterOptions.count == 0 {
      let appDelegate = (UIApplication.shared.delegate as? AppDelegate)!
      appDelegate.setDefaultFilters(previousCollectionData: self.previousCollectionData)
    }

    self.collectionView?.reloadData()
  }

  // MARK: - Button Actions

  /// Navigate to Studylist screen on Apply button clicked.
  @IBAction func applyButtonAction(_ sender: AnyObject) {

    var isbookmarked = false

    for filterOptions in StudyFilterHandler.instance.filterOptions {

      let filterType = filterOptions.type
      let filterValues = (filterOptions.filterValues.filter({ $0.isSelected == true }))
      for value in filterValues {
        switch filterType {

        case .studyStatus:
          studyStatus.append(value.title)

        case .participantStatus:
          pariticipationsStatus.append(value.title)

        case .bookmark:
          if User.currentUser.userType == .loggedInUser {
            bookmark = (value.isSelected)
            isbookmarked = true
          } else {
            categories.append(value.title)
          }

        case .category:
          categories.append(value.title)

        }
      }
    }

    previousCollectionData = []
    previousCollectionData.append(studyStatus)

    if User.currentUser.userType == .loggedInUser {
      if isbookmarked {
        previousCollectionData.append((bookmark == true ? ["Bookmarked"] : []))
      } else {
        previousCollectionData.append([])
        bookmark = false
      }
    } else {
      previousCollectionData.append(categories)
      bookmark = false

    }
    previousCollectionData.append(pariticipationsStatus)
    previousCollectionData.append(categories.count == 0 ? [] : categories)

    delegate?.appliedFilter(
      studyStatus: studyStatus,
      pariticipationsStatus: pariticipationsStatus,
      categories: categories,
      searchText: searchText,
      bookmarked: bookmark
    )
    self.dismiss(animated: true, completion: nil)

  }

  /// Navigate to Studylist screen on Cancel button clicked.
  @IBAction func cancelButtonAction(_ sender: AnyObject) {
    self.delegate?.didCancelFilter(true)
    self.dismiss(animated: true, completion: nil)
  }
}

// MARK: - Collection Data source & Delegate
extension StudyFilterViewController: UICollectionViewDataSource {

  func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int)
    -> Int
  {
    return self.filterTypes.count
  }

  func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath)
    -> UICollectionViewCell
  {
    let cell =
      (collectionView.dequeueReusableCell(withReuseIdentifier: "cell", for: indexPath)
      as? FilterListCollectionViewCell)!

    let filterOption = StudyFilterHandler.instance.filterOptions[indexPath.row]
    cell.displayCollectionData(data: filterOption)

    return cell
  }

}

extension StudyFilterViewController: PinterestLayoutDelegate {

  // 1. Returns the photo height
  func collectionView(
    _ collectionView: UICollectionView,
    heightForPhotoAtIndexPath indexPath: IndexPath,
    withWidth width: CGFloat
  ) -> CGFloat {

    let filterOptions = StudyFilterHandler.instance.filterOptions[indexPath.row]
    var headerHeight: CGFloat = 0
    if filterOptions.title.count > 0 {
      headerHeight = 60
    }
    let cellTableViewHeight =
      CGFloat(filterOptions.filterValues.count)
      * FilterListCollectionViewCell.filterTableViewCellHeight

    let totalHeight: CGFloat = cellTableViewHeight + headerHeight
    return totalHeight
  }

  // 2. Returns the annotation size based on the text
  func collectionView(
    _ collectionView: UICollectionView,
    heightForAnnotationAtIndexPath indexPath: IndexPath,
    withWidth width: CGFloat
  ) -> CGFloat {
    return 0
  }

}

class StudyFilterHandler {
  var filterOptions: [FilterOptions] = []
  var previousAppliedFilters: [[String]] = []
  var searchText = ""
  static var instance = StudyFilterHandler()
}

class FilterOptions {
  var title: String
  lazy var filterValues: [FilterValues] = []
  var type: FilterType

  init(title: String, type: FilterType) {
    self.title = title
    self.type = type
  }
}

class FilterValues {
  var title: String!
  var isSelected = false
}

extension AppDelegate {

  /// setter method to set the default filter options if none are selected.
  func setDefaultFilters(previousCollectionData: [[String]]) {

    let resource = (User.currentUser.userType == .loggedInUser) ? "FilterData" : "AnonymousFilterData"

    let plistPath = Bundle.main.path(forResource: resource, ofType: ".plist", inDirectory: nil) ?? ""
    guard let filterData = NSMutableArray.init(contentsOfFile: plistPath) else { return }

    StudyFilterHandler.instance.filterOptions = []
    var filterOptionsList: [FilterOptions] = []

    for (index, options) in filterData.enumerated() {

      guard let dataDict = options as? JSONDictionary,
        let valuesDict = dataDict["studyData"] as? [JSONDictionary]
      else { continue }

      let headerTitle = dataDict["headerText"] as? String ?? ""
      let type = dataDict["type"] as? String ?? ""

      guard let filterType = FilterType(rawValue: type) else { continue }

      let filterOptions = FilterOptions(title: headerTitle, type: filterType)

      var selectedValues: [String] = []
      if previousCollectionData.count > 0 {
        selectedValues = previousCollectionData[index]
      }

      var filterValues: [FilterValues] = []
      for value in valuesDict {

        let filterValue = FilterValues()
        let name = value["name"] as? String ?? ""
        filterValue.title = name
        let isContained = selectedValues.contains(name)

        if isContained == false {

          if previousCollectionData.count == 0 {
            // This means that we are first time accessing the filter screen.
            filterValue.isSelected = value["isEnabled"] as? Bool ?? false
          } else {
            // Means that filter is already set.
            filterValue.isSelected = false
          }
        } else {
          filterValue.isSelected = true
        }
        filterValues.append(filterValue)
      }
      filterOptions.filterValues = filterValues
      filterOptionsList.append(filterOptions)
    }
    StudyFilterHandler.instance.filterOptions = filterOptionsList
  }

  /// Query the filters settings.
  func getDefaultFilterStrings() -> StudyFilter {

    var studyStatus: [String] = []
    var pariticipationsStatus: [String] = []
    var categories: [String] = []
    var bookmark = true

    // Parsing the filter options
    for filterOptions in StudyFilterHandler.instance.filterOptions {

      let filterType = filterOptions.type
      let filterValues = (filterOptions.filterValues.filter({ $0.isSelected == true }))
      for value in filterValues {
        switch filterType {

        case .studyStatus:
          studyStatus.append(value.title)

        case .participantStatus:
          pariticipationsStatus.append(value.title)

        case .bookmark:
          if User.currentUser.userType == .loggedInUser {
            bookmark = (value.isSelected)
          } else {
            categories.append(value.title)
          }

        case .category:
          categories.append(value.title)
        }
      }
    }

    if User.currentUser.userType == .loggedInUser {
      bookmark = false
    } else {
      bookmark = false
    }

    return StudyFilter(
      studyStatus: studyStatus,
      pariticipationsStatus: pariticipationsStatus,
      categories: categories,
      bookmark: bookmark,
      searchText: ""
    )
  }

}
