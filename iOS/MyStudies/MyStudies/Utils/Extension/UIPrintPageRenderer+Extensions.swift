//  Copyright 2020 Google LLC
//
//  Use of this source code is governed by an MIT-style
//  license that can be found in the LICENSE file or at
//  https://opensource.org/licenses/MIT.

import Foundation

extension UIPrintPageRenderer {

  func generatePdfData() -> NSMutableData {
    let pdfData = NSMutableData()
    UIGraphicsBeginPDFContextToData(pdfData, self.paperRect, nil)
    self.prepare(forDrawingPages: NSMakeRange(0, self.numberOfPages))
    let printRect = UIGraphicsGetPDFContextBounds()
    for pdfPage in 0..<self.numberOfPages {
      UIGraphicsBeginPDFPage()
      self.drawPage(at: pdfPage, in: printRect)
    }
    UIGraphicsEndPDFContext()
    return pdfData
  }
}
