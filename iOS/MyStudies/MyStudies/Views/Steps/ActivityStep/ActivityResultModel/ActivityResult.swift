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

let kActivityResult = "result"
let kActivityResults = "results"

/// ActivityResult stores the result of each activity, whether it is questionary or active task.
class ActivityResult {

  var type: ActivityType?
  var activity: Activity?
  var startTime: Date?
  var endTime: Date?

  var result: [ActivityStepResult]?

  /// Default Initializer Method
  init() {
    self.type = .questionnaire
    self.activity = Activity()
    self.startTime = Date()
    self.endTime = Date()

    self.result = Array()
  }

  // Initializer method which initializes all params
  /// - Parameter taskResult: taskResult is the ORKTaskResult
  func initWithORKTaskResult(taskResult: ORKTaskResult) {

    if Utilities.isValidObject(someObject: self.result as AnyObject?) == false
      && self.result?
        .count
        == 0
    {

      for stepResult in taskResult.results! {
        let activityStepResult: ActivityStepResult? = ActivityStepResult()

        if (self.activity?.activitySteps?.count)! > 0 {

          let activityStepArray = self.activity?.activitySteps?.filter({
            $0.key == stepResult.identifier
          })
          if (activityStepArray?.count)! > 0 {
            activityStepResult?.step = activityStepArray?.first
          }

        }

        activityStepResult?.initWithORKStepResult(
          stepResult: (stepResult as? ORKStepResult)!,
          activityType: (self.activity?.type)!
        )

        // Completion steps results and Instruction step results are ignored
        if stepResult.identifier != "CompletionStep"
          && stepResult.identifier != kFetalKickInstructionStepIdentifier
          && stepResult.identifier != kFetalKickIntroductionStepIdentifier
          && stepResult.identifier != "instruction"
          && stepResult.identifier != "instruction1"
          && stepResult.identifier != "conclusion"
        {

          if activityStepResult?.step != nil
            && (activityStepResult?.step is ActivityInstructionStep) == false
          {
            self.result?.append(activityStepResult!)
          } else {
            if self.activity?.type == .activeTask {
              self.result?.append(activityStepResult!)
            }
          }
        }
      }
    }
  }

  /// Sets activy and activity type
  /// - Parameter activity: instance of Activity
  func setActivity(activity: Activity) {

    self.activity = activity
    self.type = activity.type
  }

  /// Creates dictionary for the step being used and returns the dictionary of activitysteps
  func getResultDictionary() -> [String: Any]? {

    var activityDict: [String: Any]? = [String: Any]()

    if self.type != nil {
      if self.type != .activeTask {

        activityDict?[kActivityActiveKeyResultType] = self.type?.rawValue
      }
    }

    if self.startTime != nil && (Utilities.getStringFromDate(date: self.startTime!) != nil) {

      activityDict?[kActivityStartTime] = Utilities.getStringFromDate(date: self.startTime!)
    }
    if self.endTime != nil && (Utilities.getStringFromDate(date: self.endTime!) != nil) {

      activityDict?[kActivityEndTime] = Utilities.getStringFromDate(date: self.endTime!)
    }

    if Utilities.isValidObject(someObject: result as AnyObject?) {

      var activityResultArray: [[String: Any]] = [[String: Any]]()
      for stepResult in result! {
        let activityStepResult = stepResult as ActivityStepResult

        activityResultArray.append(
          (activityStepResult.getActivityStepResultDict())! as [String: Any]
        )
      }

      activityDict?[kActivityResults] = activityResultArray
    }
    return activityDict!
  }
}
