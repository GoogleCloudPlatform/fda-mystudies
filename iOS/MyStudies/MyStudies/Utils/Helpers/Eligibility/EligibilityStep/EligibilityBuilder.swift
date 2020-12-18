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

let kEligibilityType = "type"
let kEligibilityTest = "test"
let kEligibilityCorrectAnswers = "correctAnswers"
let kEligibilityTokenTitle = "tokenTitle"

let kEligibilityInEligibleScreen = "InEligibleScreen"
let kEligibilityInEligibleDescriptionText = "Sorry, You are Ineligible"

let kEligibilityVerifiedScreen = "VerifiedScreen"
let kEligibilityCompletionDescriptionText =
  """
  Your enrollment token has been successfully validated. You are eligible to join the Study.
  Please click Continue to proceed to the Consent section.
  """
let kEligibilityCompletionTitle = "Eligibility Confirmed"

let kEligibilityStep = "steps"

let kEligibilityValidateScreen = "ValidatedScreen"
let kEligibilityValidationDescriptionText =
  "Your eligibility to participate in this study has been successfully verified.\nYou may proceed to the next steps."
let kEligibilityValidationTitle = "Validated!"

let kEligibilityTestInstructionStep = "EligibilityTestInstructionStep"
let kEligibilityTestInstructionTestTitle = "Eligibility Test"
let kEligibilityInstructionTestText =
  "Please answer some quick questions to confirm your eligibility for this study."

let kEligibilityCompletionTestDescriptionText =
  "Based on the answers you provided, you are eligible to participate in this study.\nYou may proceed to the next steps."

let kEligibilityCorrectAnswer = "answer"
let kEligibilityCorrectAnswerKey = "key"

enum EligibilityStepType: String {
  case token = "token"
  case test = "test"
  case both = "combined"
}

class EligibilityBuilder {

  /// type specifies Eligibility Type which can be token, test or both
  var type: EligibilityStepType?

  /// Custom token for Eligibility Token step title
  var tokenTitle: String?

  /// contains array of Dictionary of steps for the test
  var testArray: [Any]?

  static var currentEligibility: EligibilityBuilder?

  /// array of Dictionary of step Results
  var correctAnswers: [[String: Any]]?

  /// Initialize the Eligibility Builder
  init() {

    self.type = .token
    self.tokenTitle = ""
    self.testArray = Array()
    self.correctAnswers = [[String: Any]]()
  }

  /// Initialize the Eligibility Builder
  /// - Parameter eligibilityDict: Eligibility Dictionary
  func initEligibilityWithDict(eligibilityDict: [String: Any]) {

    if Utilities.isValidObject(someObject: eligibilityDict[kEligibilityTest] as AnyObject) {
      self.testArray = eligibilityDict[kEligibilityTest] as! [[String: Any]]
    }
    if Utilities.isValidValue(someObject: eligibilityDict[kEligibilityType] as AnyObject?) {
      self.type = EligibilityStepType(rawValue: eligibilityDict[kEligibilityType] as! String)
    }
    if Utilities.isValidObject(
      someObject: eligibilityDict[kEligibilityCorrectAnswers] as AnyObject
    ) {
      self.correctAnswers = eligibilityDict[kEligibilityCorrectAnswers] as? [[String: Any]]
    }
    if Utilities.isValidValue(someObject: eligibilityDict[kEligibilityTokenTitle] as AnyObject?) {
      self.tokenTitle = eligibilityDict[kEligibilityTokenTitle] as? String
    }
  }

  /// Returns eligibility Steps based on the EligibilityStepType.
  func getEligibilitySteps() -> [ORKStep]? {

    if self.type != nil {

      var stepsArray: [ORKStep]? = [ORKStep]()
      // Token Step
      if self.type == EligibilityStepType.token {

        // creating Eligibility Token Step
        let eligibilityStep: EligibilityStep? = EligibilityStep(
          identifier: kEligibilityTokenStep
        )
        eligibilityStep?.type = "TOKEN"

        if self.tokenTitle != nil {
          eligibilityStep?.text = self.tokenTitle!
        }

        stepsArray?.append(eligibilityStep!)

        // creating Token Validated Step
        let eligibilityValidationStep = CustomInstructionStep(
          identifier: kEligibilityValidateScreen
        )
        eligibilityValidationStep.text = kEligibilityValidationDescriptionText

        // Branding
        eligibilityValidationStep.title = Branding.validatedTitle

        eligibilityValidationStep.image = #imageLiteral(resourceName: "successBlueBig")
        stepsArray?.append(eligibilityValidationStep)

      } else if self.type == EligibilityStepType.test {  // Eligibility Test
        // add the Instruction step for eligibility Test

        let eligibilityTestInstructionStep = CustomInstructionStep(
          identifier: kEligibilityTestInstructionStep
        )
        eligibilityTestInstructionStep.text = kEligibilityInstructionTestText

        eligibilityTestInstructionStep.title = kEligibilityTestInstructionTestTitle
        stepsArray?.append(eligibilityTestInstructionStep)

        // test array will hold the questions, correct answers will hold the answers

        for stepDict in self.testArray! {
          let questionStep: ActivityQuestionStep? = ActivityQuestionStep()
          questionStep?.initWithDict(stepDict: stepDict as! [String: Any])
          stepsArray?.append((questionStep?.getQuestionStep())!)
        }

        // creating InEligibility Completion Step

        let eligibilityCompletionStep: InEligibilityStep? = InEligibilityStep(
          identifier: kInEligibilityStep
        )

        stepsArray?.append(eligibilityCompletionStep!)

      } else {
        // for both test & token

        // creating Token Step
        let eligibilityStep: EligibilityStep? = EligibilityStep(
          identifier: kEligibilityTokenStep
        )
        eligibilityStep?.type = "TOKEN"

        if self.tokenTitle != nil {
          eligibilityStep?.text = self.tokenTitle!
        }
        stepsArray?.append(eligibilityStep!)

        // add the Instruction step for eligibility Test
        let eligibilityTestInstructionStep = CustomInstructionStep(
          identifier: kEligibilityTestInstructionStep
        )
        eligibilityTestInstructionStep.text = kEligibilityInstructionTestText

        eligibilityTestInstructionStep.title = kEligibilityTestInstructionTestTitle
        stepsArray?.append(eligibilityTestInstructionStep)

        // creating Test Questions
        for stepDict in self.testArray! {

          let questionStep: ActivityQuestionStep? = ActivityQuestionStep()
          questionStep?.initWithDict(stepDict: stepDict as! [String: Any])
          // Questions are mandatory not skippable
          questionStep?.skippable = false
          stepsArray?.append((questionStep?.getQuestionStep())!)

        }

        // creating InEligibility Completion Step
        let eligibilityCompletionStep: InEligibilityStep? = InEligibilityStep(
          identifier: kInEligibilityStep
        )

        stepsArray?.append(eligibilityCompletionStep!)
      }

      if (stepsArray?.count)! > 0 {

        if self.type == EligibilityStepType.test || self.type == .both {

          // creating Eligibility Completion Step
          let eligibilityCompletionStep = CustomInstructionStep(
            identifier: kEligibilityVerifiedScreen
          )
          eligibilityCompletionStep.text = kEligibilityCompletionTestDescriptionText

          eligibilityCompletionStep.title = kEligibilityCompletionTitle
          eligibilityCompletionStep.image = #imageLiteral(resourceName: "successBlueBig")
          stepsArray?.append(eligibilityCompletionStep)
        }

        return stepsArray!
      } else {
        return nil
      }
    } else {
      return nil
    }
  }
}

class CustomInstructionStep: ORKInstructionStep {

  /// Returns an bool value
  func showsProgress() -> Bool {
    return false
  }
}
