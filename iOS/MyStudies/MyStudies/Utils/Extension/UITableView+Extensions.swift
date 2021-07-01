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

extension UITableView {
  func asFullImage() -> UIImage? {
    guard self.numberOfSections > 0, self.numberOfRows(inSection: 0) > 0 else {
      return nil
    }
    self.scrollToRow(at: IndexPath(row: 0, section: 0), at: .top, animated: false)
    
    var height: CGFloat = 0.0
    for section in 0..<self.numberOfSections {
      var cellHeight: CGFloat = 0.0
      for row in 0..<self.numberOfRows(inSection: section) {
        let indexPath = IndexPath(row: row, section: section)
        guard let cell = self.cellForRow(at: indexPath) else { continue }
        cellHeight = cell.frame.size.height
      }
      height += cellHeight * CGFloat(self.numberOfRows(inSection: section))
    }
    UIGraphicsBeginImageContextWithOptions(CGSize(width: self.contentSize.width, height: height), false, UIScreen.main.scale)
    
    for section in 0..<self.numberOfSections {
      for row in 0..<self.numberOfRows(inSection: section) {
        let indexPath = IndexPath(row: row, section: section)
        guard let cell = self.cellForRow(at: indexPath) else { continue }
        cell.contentView.drawHierarchy(in: cell.frame, afterScreenUpdates: true)
        if row < self.numberOfRows(inSection: section) - 1 {
          self.scrollToRow(at: IndexPath(row: row+1, section: section), at: .bottom, animated: false)
        }
      }
    }
    let image = UIGraphicsGetImageFromCurrentImageContext()
    UIGraphicsEndImageContext()
    
    return image!
  }
}
