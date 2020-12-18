// License Agreement for FDA MyStudies
// Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is
// hereby granted, free of charge, to any person obtaining a copy of this software and associated
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

/// The `CellConfigurable` protocol is adopted by cells that want to provide an easy
/// way to obtain the `UINib` and a `reuseIdentifier`.
///
/// This framework has a default implementation that uses the name of the class
/// for the `reuseIdentifier` and assumes the nib name is the same as the
/// `reuseIdentifier`, in case your nib name is different you can either return
/// the name of your nib in the `reuseIdentifier` variable or return your nib
/// implementing the `nib` variable.
///
/// This framework also adds conformance to the protocol to `UITableViewCell` and
/// `UICollectionViewCell`
public protocol CellConfigurable {
  static var reuseIdentifier: String { get }
  static var nib: UINib { get }
}

extension CellConfigurable where Self: UITableViewCell {
  public static var reuseIdentifier: String {
    return String(describing: self)
  }

  public static var nib: UINib {
    return UINib(nibName: reuseIdentifier, bundle: nil)
  }
}

extension CellConfigurable where Self: UICollectionViewCell {
  public static var reuseIdentifier: String {
    return String(describing: self)
  }

  public static var nib: UINib {
    return UINib(nibName: reuseIdentifier, bundle: nil)
  }
}

extension UITableViewCell: CellConfigurable {}
extension UICollectionViewCell: CellConfigurable {}

/// The `CellRegistrable` protocol is adopted objects that want to provide an easy
/// way to register the cells that conform to the `CellConfigurable` protocol.
///
/// This framework adds conformance to the protocol to `UITableView` and
/// `UICollectionView`
public protocol CellRegistrable {
  func registerCell(cell: CellConfigurable.Type)
}

extension UITableView: CellRegistrable {
  public func registerCell(cell: CellConfigurable.Type) {
    register(cell.nib, forCellReuseIdentifier: cell.reuseIdentifier)
  }
}

extension UICollectionView: CellRegistrable {
  public func registerCell(cell: CellConfigurable.Type) {
    register(cell.nib, forCellWithReuseIdentifier: cell.reuseIdentifier)
  }
}

extension UIView {

  /// This is a function to get subViews of a particular type from view recursively. It would look recursively in all subviews and return back the subviews of the type T
  func allSubViewsOf<T: UIView>(type: T.Type) -> [T] {
    var all = [T]()
    func getSubview(view: UIView) {
      if let aView = view as? T {
        all.append(aView)
      }
      guard view.subviews.count > 0 else { return }
      view.subviews.forEach { getSubview(view: $0) }
    }
    getSubview(view: self)
    return all
  }

}
