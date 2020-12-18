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

class FilterListCollectionViewCell: UICollectionViewCell {

  // MARK: - Outlets
  @IBOutlet weak var tableView: UITableView?

  @IBOutlet weak var tableViewHeader: UIView?
  @IBOutlet weak var labelHeaderTitle: UILabel?

  static let filterTableViewCellHeight: CGFloat = 50.0
  var headerName = ""
  var studyData = NSMutableArray()
  var filterOptions: FilterOptions!

  func displayCollectionData(data: FilterOptions) {

    filterOptions = data
    if filterOptions.title.count == 0 {
      tableView?.tableHeaderView = nil
    } else {
      labelHeaderTitle?.text = filterOptions.title
      tableView?.tableHeaderView = tableViewHeader
    }
    tableView?.reloadData()
    labelHeaderTitle?.sizeToFit()
  }
}
// MARK: - TableView Delegate & DataSource
extension FilterListCollectionViewCell: UITableViewDelegate, UITableViewDataSource {

  func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
    return filterOptions.filterValues.count  //studyData.count
  }

  func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

    let cell =
      tableView.dequeueReusableCell(withIdentifier: "cell", for: indexPath)
      as! FilterListTableViewCell

    let data = filterOptions.filterValues[indexPath.row]
    cell.populateCellWith(filterValue: data)
    if filterOptions.filterValues.count == 1 {
      // It should look like as a title.
      cell.updateNameLblAsTitle()
    }

    return cell
  }

  func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
    tableView.deselectRow(at: indexPath, animated: true)
    let data = filterOptions.filterValues[indexPath.row]
    data.isSelected = !data.isSelected

    tableView.reloadData()
  }

  func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
    return FilterListCollectionViewCell.filterTableViewCellHeight
  }

}
