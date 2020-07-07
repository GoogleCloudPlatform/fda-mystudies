//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

extension WKWebView {

  /// Converts loaded HTML into PDF `Data`.
  func renderSelfToPdfData(htmlString: String) -> Data {
    let printFormatter = UIMarkupTextPrintFormatter(markupText: htmlString)
    let pdfData = createPdfFile(printFormatter: printFormatter)
    return Data(referencing: pdfData)
  }

  func createPdfFile(printFormatter: UIPrintFormatter) -> NSMutableData {

    let originalBounds = self.bounds
    self.bounds = CGRect(
      x: originalBounds.origin.x,
      y: bounds.origin.y,
      width: self.bounds.size.width,
      height: self.scrollView.contentSize.height
    )
    let pdfPageFrame = CGRect(x: 0, y: 0, width: self.bounds.size.width, height: self.scrollView.contentSize.height)
    let printPageRenderer = UIPrintPageRenderer()
    printPageRenderer.addPrintFormatter(printFormatter, startingAtPageAt: 0)
    printPageRenderer.setValue(NSValue(cgRect: UIScreen.main.bounds), forKey: "paperRect")
    printPageRenderer.setValue(NSValue(cgRect: pdfPageFrame), forKey: "printableRect")
    self.bounds = originalBounds
    return printPageRenderer.generatePdfData()
  }
}
