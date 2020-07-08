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

import Foundation
import ResearchKit

class ActivityInstructionStep: ActivityStep {

  /// Used for custom image if exist
  var image: UIImage?

  /// Used for saving the custom image in local path if exists any
  var imageLocalPath: String?

  /// Used for saving the custom image in server if exists any
  var imageServerURL: String?

  /// default Initializer Method
  override init() {
    super.init()
    self.imageLocalPath = ""
    self.imageServerURL = ""
    self.image = UIImage()

  }

  /// Initializer method which initializes all params
  /// - Parameter stepDict: Dictionary which contains all the properties of ActivityInstruction Step
  override func initWithDict(stepDict: [String: Any]) {

    if Utilities.isValidObject(someObject: stepDict as AnyObject?) {
      super.initWithDict(stepDict: stepDict)
    }
  }

  /// Creates instruction step based on ActivityStep data and returns ORKInstructionStep
  func getInstructionStep() -> ORKInstructionStep? {

    if Utilities.isValidValue(someObject: title as AnyObject?)
      && Utilities.isValidValue(
        someObject: text as AnyObject?
      ) && Utilities.isValidValue(someObject: key as AnyObject?)
    {

      let instructionStep = ORKInstructionStep(identifier: key!)

      instructionStep.title = NSLocalizedString(title!, comment: "")
      instructionStep.text = text!
      return instructionStep
    } else {
      return nil
    }
  }
}
