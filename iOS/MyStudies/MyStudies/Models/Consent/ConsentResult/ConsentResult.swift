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
import ResearchKit

class ConsentResult {

  var startTime: Date?
  var endTime: Date?

  var consentId: String?
  var consentDocument: ORKConsentDocument?
  var consentPdfData: Data?
  var result: [ActivityStepResult]?

  /// A boolean indicating user allows their data to be shared publically.
  var isShareDataWithPublic: Bool?

  var token: String?
  var consentPath: String?

  /// Initializer
  init() {

    self.startTime = Date()
    self.endTime = Date()

    self.result = Array()

    self.consentDocument = ORKConsentDocument()
    self.consentPdfData = Data()
    self.token = ""
  }

  /// Initializer method creates consent result for generating PDF and saving to server
  /// - Parameter taskResult: Instance of ORKTaskResult and holds the step results
  func initWithORKTaskResult(taskResult: ORKTaskResult) {
    for stepResult in taskResult.results! {

      if let stepResultObj = stepResult as? ORKStepResult,
        let results = stepResultObj.results,
        results.count > 0
      {

        if stepResult.identifier == kConsentSharing,
          let stepResult = stepResult as? ORKStepResult,
          let sharingChoiceResult = stepResult.results?.first as? ORKChoiceQuestionResult?,
          let userResponse = sharingChoiceResult?.choiceAnswers?.first as? Bool
        {
          self.isShareDataWithPublic = userResponse
        } else if let signatureStepResult: ORKConsentSignatureResult? =
          (stepResult as? ORKStepResult)!.results?[0] as? ORKConsentSignatureResult?
        {

          signatureStepResult?.apply(to: self.consentDocument!)

          if self.consentPdfData?.count == 0 {
            self.consentPath =
              "Consent" + "_" + "\((Study.currentStudy?.studyId)!)"
              + ".pdf"

            self.consentDocument?.makePDF(
              completionHandler: { data, error in
                Logger.sharedInstance.error(
                  "data: \(String(describing: data))    \n  error: \(String(describing: error))"
                )

                var fullPath: String!
                let path = AKUtility.baseFilePath + "/study"
                let fileName: String =
                  "Consent" + "_"
                  + "\((Study.currentStudy?.studyId)!)"
                  + ".pdf"

                self.consentPath = fileName

                fullPath = path + "/" + fileName

                if !FileManager.default.fileExists(atPath: path) {
                  try? FileManager.default.createDirectory(
                    atPath: path,
                    withIntermediateDirectories: true,
                    attributes: nil
                  )
                }

                self.consentPdfData = Data()
                self.consentPdfData = data
                self.consentPath = fileName

                do {

                  if FileManager.default.fileExists(atPath: fullPath) {
                    try FileManager.default.removeItem(atPath: fullPath)
                  }
                  FileManager.default.createFile(
                    atPath: fullPath,
                    contents: data,
                    attributes: [:]
                  )

                  let defaultPath = fullPath
                  fullPath = "file://" + "\(fullPath!)"

                  try data?.write(to: URL(string: fullPath!)!)
                  FileDownloadManager.encyptFile(
                    pathURL: URL(string: defaultPath!)!
                  )
                } catch let error as NSError {
                  Logger.sharedInstance.error(error.localizedDescription)
                }
              })
          } else {

            var fullPath: String!
            let path = AKUtility.baseFilePath + "/study"
            let fileName: String =
              "Consent" + "_" + "\((Study.currentStudy?.studyId)!)"
              + ".pdf"

            self.consentPath = fileName

            fullPath = path + "/" + fileName

            if !FileManager.default.fileExists(atPath: path) {
              try? FileManager.default.createDirectory(
                atPath: path,
                withIntermediateDirectories: true,
                attributes: nil
              )
            }

            var data: Data? = Data.init()
            data = self.consentPdfData
            self.consentPath = fileName

            do {

              if FileManager.default.fileExists(atPath: fullPath) {
                try FileManager.default.removeItem(atPath: fullPath)
              }
              FileManager.default.createFile(
                atPath: fullPath,
                contents: data,
                attributes: [:]
              )
              fullPath = "file://" + "\(fullPath!)"

              try data?.write(to: URL(string: fullPath!)!)
              FileDownloadManager.encyptFile(pathURL: URL(string: fullPath!)!)

            } catch let error as NSError {
              Logger.sharedInstance.error(error.localizedDescription)
            }
          }
        } else if let tokenStepResult: EligibilityTokenTaskResult? =
          (stepResult as? ORKStepResult)!
          .results?[0] as? EligibilityTokenTaskResult?
        {
          self.token = tokenStepResult?.enrollmentToken
        }
      }
    }
  }
}
