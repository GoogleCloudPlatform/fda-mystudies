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

let kActivityStepStartTime = "startTime"
let kActivityStepEndTime = "endTime"

let kActivityStepSkipped = "skipped"
let kActivityStepResultValue = "value"

/// to be used specifically for Active Task
let kActivityActiveKeyResultType = "resultType"

let kActivityActiveStepKey = "key"

let kSpatialSpanMemoryKeyScore = "Score"
let kSpatialSpanMemoryKeyNumberOfGames = "NumberofGames"
let kSpatialSpanMemoryKeyNumberOfFailures = "NumberofFailures"

let kTowerOfHanoiKeyPuzzleWasSolved = "puzzleWasSolved"
let kTowerOfHanoiKeyNumberOfMoves = "numberOfMoves"

let kFetalKickCounterDuration = "duration"
let kFetalKickCounterCount = "count"

enum ActiveStepResultType: String {
  case boolean = "boolean"
  case numeric = "numeric"
}

enum SpatialSpanMemoryType: Int {
  case score = 0
  case numberOfGames = 1
  case numberOfFailures = 2
}

enum TowerOfHanoiResultType: Int {
  case puzzleWasSolved = 0
  case numberOfMoves = 1
}

enum ActvityStepResultType: String {
  case formOrActiveTask = "grouped"
  case questionnaire = "questionnaire"

}

class ActivityStepResult {

  var type: ActivityStepType?
  var step: ActivityStep?
  var key: String?
  var startTime: Date?

  var endTime: Date?
  var skipped: Bool?

  /// stores the result value of step, it can be of any type
  var value: Any?

  /// Exclusively used for form step to store the formItem type
  var subTypeForForm: String?

  var questionStep: ActivityQuestionStep?

  /// default initializer method
  init() {
    /// instance of ActivityStep
    self.step = ActivityStep()
    /// specifies the step type
    self.type = .question
    /// stores the identifier of step
    self.key = ""
    self.startTime = Date.init(timeIntervalSinceNow: 0)
    self.endTime = Date.init(timeIntervalSinceNow: 0)
    self.skipped = false
    self.questionStep = nil
    self.subTypeForForm = ""

  }

  /// Initializer method which initializes all params
  /// - Parameter stepResult: stepResult is the ORKStepResult
  /// - Parameter activityType: which holds the activity type
  func initWithORKStepResult(stepResult: ORKStepResult, activityType: ActivityType) {

    if Utilities.isValidValue(someObject: stepResult.identifier as AnyObject?) {
      self.key = stepResult.identifier
    }

    self.startTime = stepResult.startDate
    self.endTime = stepResult.endDate

    if (stepResult.results?.count)! > 1 || (self.step != nil && self.step is ActivityFormStep) {
      self.type = .form
    } else {
      if activityType == .activeTask {
        self.type = .active
      }
    }
    self.setResultValue(stepResult: stepResult, activityType: activityType)

  }

  /// Creates ActivityStepDictionary from step instance and returns ResultDictionary for storing data to Api/Local
  func getActivityStepResultDict() -> [String: Any]? {

    var stepDict: [String: Any]? = [String: Any]()

    switch self.type! as ActivityStepType {

    case .instruction: stepDict?[kActivityStepResultType] = "null"

    case .question: stepDict?[kActivityStepResultType] = self.step?.resultType

    case .form:
      stepDict?[kActivityStepResultType] = ActvityStepResultType.formOrActiveTask.rawValue

    case .active:

      if self.step?.resultType != nil {

        stepDict?[kActivityStepResultType] =
          (self.step?.resultType as? String)!
            == "fetalKickCounter" ? "grouped" : (self.step?.resultType)

      } else {
        stepDict?[kActivityStepResultType] = "grouped"
      }
    default: break

    }

    if Utilities.isValidValue(someObject: self.key as AnyObject?) {

      stepDict?[kActivityStepKey] = self.key!
    }
    if self.startTime != nil && (Utilities.getStringFromDate(date: self.startTime!) != nil) {

      stepDict?[kActivityStartTime] = Utilities.getStringFromDate(date: self.startTime!)
    }
    if self.endTime != nil && (Utilities.getStringFromDate(date: self.endTime!) != nil) {

      stepDict?[kActivityEndTime] = Utilities.getStringFromDate(date: self.endTime!)
    }

    if self.value != nil {
      stepDict?[kActivityStepResultValue] = self.value
      // checking if step is skippable
      if self.value is [Any] || self.value is [String: Any] {

        if Utilities.isValidObject(someObject: self.value as AnyObject) {
          stepDict?[kActivityStepSkipped] = false

        } else {
          stepDict?[kActivityStepSkipped] = true
        }

      } else {
        if Utilities.isValidValue(someObject: self.value as AnyObject) {
          stepDict?[kActivityStepSkipped] = false

        } else {
          stepDict?[kActivityStepSkipped] = true
        }
      }
    } else {
      stepDict?[kActivityStepSkipped] = self.skipped ?? true
      stepDict?[kActivityStepResultValue] = self.value
    }
    return stepDict
  }

  /// Saves the result of Current Step
  /// - Parameter stepResult: stepResult which can be result of Questionstep/InstructionStep/ActiveTask
  /// - Parameter activityType: which holds the activity type
  func setResultValue(stepResult: ORKStepResult, activityType: ActivityType) {

    if (stepResult.results?.count)! > 0 {

      if activityType == .questionnaire {
        // for question Step

        if stepResult.results?.count == 1 && self.type != .form {

          if let questionstepResult: ORKQuestionResult? = stepResult.results?.first
            as? ORKQuestionResult?
          {
            self.setValue(questionstepResult: questionstepResult!)

          } else {

            // for consent step result we are storing the ORKConsentSignatureResult
            let consentStepResult: ORKConsentSignatureResult? =
              (stepResult.results?.first as? ORKConsentSignatureResult?)!
            self.value = consentStepResult

          }
        } else {
          // for form step result

          self.value = [ActivityStepResult]()
          var formResultArray: [Any] = [Any]()
          var j: Int! = 0
          var isAddMore: Bool? = false

          if (stepResult.results?.count)!
            > (self.step as? ActivityFormStep)!.itemsArray
            .count
          {
            isAddMore = true
          }
          var localArray: [[String: Any]] = [[String: Any]]()

          for (i, result) in stepResult.results!.enumerated() {

            let activityStepResult: ActivityStepResult? = ActivityStepResult()
            activityStepResult?.startTime = self.startTime
            activityStepResult?.endTime = self.endTime
            activityStepResult?.skipped = self.skipped

            let activityStep = ActivityStep()
            activityStepResult?.step = activityStep

            j = (i == 0 ? 0 : i % (self.step as? ActivityFormStep)!.itemsArray.count)

            // Checking if formStep is RepeatableFormStep
            if isAddMore! {
              if j == 0 {
                localArray.removeAll()
                localArray = [[String: Any]]()
              }

              let stepDict = (((self.step as? ActivityFormStep)!.itemsArray) as [[String: Any]])[j]

              activityStepResult?.key = stepDict["key"] as! String?

            } else {
              activityStepResult?.key = result.identifier
            }
            let itemDict = (self.step as? ActivityFormStep)!.itemsArray[j] as [String: Any]
            activityStepResult?.step?.resultType = (itemDict["resultType"] as? String)!
            if (result as? ORKQuestionResult) != nil {

              let questionResult: ORKQuestionResult? = (result as? ORKQuestionResult)

              if Utilities.isValidValue(
                someObject: (activityStepResult?.step?.resultType as? String as AnyObject)
              ) {
                self.subTypeForForm =
                  activityStepResult?.step?.resultType
                  as? String

              } else {
                self.subTypeForForm = ""
              }

              self.setValue(questionstepResult: questionResult!)

              activityStepResult?.value = self.value
              localArray.append((activityStepResult?.getActivityStepResultDict()!)!)

              // checking if more steps added in RepeatableFormStep
              if isAddMore! {
                if j + 1 == (self.step as? ActivityFormStep)!.itemsArray.count {
                  if localArray.count > 0 {
                    formResultArray.append(localArray)
                  }
                }
              }
            }
          }

          if isAddMore! {
            self.value = formResultArray

          } else {
            if localArray.count > 0 {
              formResultArray.append(localArray)
            }
            self.value = formResultArray
          }
        }

      } else if activityType == .activeTask {
        // For Active task like Fetal Kick, Spatial Span Memory & Towers of Honoi
        let activityResult: ORKResult? = stepResult.results?.first
        var resultArray: [[String: Any]]? = Array()

        if (activityResult as? ORKSpatialSpanMemoryResult) != nil {
          // Result Handling for Spatial Span Memory
          let stepTypeResult: ORKSpatialSpanMemoryResult? =
            activityResult
            as? ORKSpatialSpanMemoryResult

          if Utilities.isValidValue(someObject: stepTypeResult?.score as AnyObject?)
            && Utilities.isValidValue(
              someObject: stepTypeResult?.numberOfGames as AnyObject?
            )
            && Utilities.isValidValue(
              someObject: stepTypeResult?.numberOfFailures as AnyObject?
            )
          {

            for i in 0..<3 {
              var resultDict: [String: Any]? = Dictionary()

              resultDict?[kActivityActiveKeyResultType] =
                ActiveStepResultType.numeric
                .rawValue

              switch SpatialSpanMemoryType(rawValue: i)! as SpatialSpanMemoryType {
              case .score:
                resultDict?[kActivityActiveStepKey] = kSpatialSpanMemoryKeyScore
                resultDict?[kActivityStepResultValue] = stepTypeResult?.score

              case .numberOfGames:
                resultDict?[kActivityActiveStepKey] = kSpatialSpanMemoryKeyNumberOfGames
                resultDict?[kActivityStepResultValue] =
                  stepTypeResult?
                  .numberOfGames

              case .numberOfFailures:
                resultDict?[kActivityActiveStepKey] = kSpatialSpanMemoryKeyNumberOfFailures
                resultDict?[kActivityStepResultValue] =
                  stepTypeResult?
                  .numberOfFailures
              }

              if self.startTime != nil
                && (Utilities.getStringFromDate(date: self.startTime!) != nil)
              {

                resultDict?[kActivityStepStartTime] = Utilities.getStringFromDate(
                  date: self.startTime!
                )
              } else {
                let currentDate = Date()
                let dateString = Utilities.getStringFromDate(date: currentDate)

                resultDict?[kActivityStepStartTime] = dateString
              }
              if self.endTime != nil
                && (Utilities.getStringFromDate(date: self.endTime!) != nil)
              {

                resultDict?[kActivityStepEndTime] = Utilities.getStringFromDate(
                  date: self.endTime!
                )
              } else {
                let currentDate = Date()
                let dateString = Utilities.getStringFromDate(date: currentDate)
                resultDict?[kActivityStepEndTime] = dateString
              }
              resultDict?[kActivityStepSkipped] = self.skipped
              resultArray?.append(resultDict!)
            }
            self.key = Study.currentActivity?.actvityId
            self.value = resultArray
          } else {
            self.value = 0
          }
        } else if (activityResult as? ORKTowerOfHanoiResult) != nil {
          // Result Handling for Towers of Honoi
          let stepTypeResult: ORKTowerOfHanoiResult? =
            activityResult
            as? ORKTowerOfHanoiResult

          for i in 0..<2 {
            var resultDict: [String: Any]? = Dictionary()
            // Saving puzzleWasSolved & numberOfMoves
            if TowerOfHanoiResultType(rawValue: i) == .puzzleWasSolved {

              resultDict?[kActivityActiveStepKey] = kTowerOfHanoiKeyPuzzleWasSolved
              resultDict?[kActivityStepResultValue] = stepTypeResult?.puzzleWasSolved
              resultDict?[kActivityActiveKeyResultType] =
                ActiveStepResultType.boolean
                .rawValue

            } else {
              // numberOfMoves
              resultDict?[kActivityActiveStepKey] = kTowerOfHanoiKeyNumberOfMoves
              resultDict?[kActivityStepResultValue] = stepTypeResult?.moves?.count
              resultDict?[kActivityActiveKeyResultType] =
                ActiveStepResultType.numeric
                .rawValue
            }

            if self.startTime != nil
              && (Utilities.getStringFromDate(date: self.startTime!) != nil)
            {

              resultDict?[kActivityStepStartTime] = Utilities.getStringFromDate(
                date: self.startTime!
              )
            } else {
              let currentDate = Date()
              let dateString = Utilities.getStringFromDate(date: currentDate)

              resultDict?[kActivityStepStartTime] = dateString
            }

            // Saving Start & End Time of Step
            if self.endTime != nil
              && (Utilities.getStringFromDate(date: self.endTime!) != nil)
            {

              resultDict?[kActivityStepEndTime] = Utilities.getStringFromDate(
                date: self.endTime!
              )

            } else {
              let currentDate = Date()
              let dateString = Utilities.getStringFromDate(date: currentDate)

              resultDict?[kActivityStepEndTime] = dateString
            }
            resultDict?[kActivityStepSkipped] = self.skipped
            resultArray?.append(resultDict!)

          }
          self.key = Study.currentActivity?.actvityId
          self.value = resultArray

        } else if (activityResult as? FetalKickCounterTaskResult) != nil {
          // Result handling for FetalKickCounter
          let stepTypeResult: FetalKickCounterTaskResult? =
            activityResult
            as? FetalKickCounterTaskResult

          for i in 0..<2 {
            var resultDict: [String: Any]? = Dictionary()

            resultDict?[kActivityActiveKeyResultType] =
              ActiveStepResultType.numeric
              .rawValue

            // Saving Duration & Kick Counts
            if i == 0 {  //Duration
              resultDict?[kActivityActiveStepKey] = kFetalKickCounterDuration
              resultDict?[kActivityStepResultValue] = Double(
                (stepTypeResult?.duration) == nil ? 0 : (stepTypeResult?.duration)!
              )

            } else {  // Kick Count
              resultDict?[kActivityActiveStepKey] = kFetalKickCounterCount
              resultDict?[kActivityStepResultValue] = Double(
                (stepTypeResult?.totalKickCount) == nil
                  ? 0 : (stepTypeResult?.totalKickCount)!
              )
            }

            // Saving Start & End Time of Step
            if self.startTime != nil
              && (Utilities.getStringFromDate(date: self.startTime!) != nil)
            {
              resultDict?[kActivityStepStartTime] = Utilities.getStringFromDate(
                date: self.startTime!
              )
            } else {
              let currentDate = Date()
              let dateString = Utilities.getStringFromDate(date: currentDate)
              resultDict?[kActivityStepStartTime] = dateString
            }
            if self.endTime != nil
              && (Utilities.getStringFromDate(date: self.endTime!) != nil)
            {

              resultDict?[kActivityStepEndTime] = Utilities.getStringFromDate(
                date: self.endTime!
              )
            } else {
              let currentDate = Date()
              let dateString = Utilities.getStringFromDate(date: currentDate)

              resultDict?[kActivityStepEndTime] = dateString
            }
            resultDict?[kActivityStepSkipped] = self.skipped
            resultArray?.append(resultDict!)
          }
          self.value = resultArray
        }
      }
    }
  }

  /// Sets the questionStepResult value based on the QuestionStepType
  /// - Parameter questionstepResult: instance of type ORKQuestionResult
  func setValue(questionstepResult: ORKQuestionResult) {
    switch questionstepResult.questionType.rawValue {

    case ORKQuestionType.scale.rawValue:
      // scale and continuos scale

      if let stepTypeResult = questionstepResult as? ORKScaleQuestionResult {

        if Utilities.isValidValue(someObject: stepTypeResult.scaleAnswer as AnyObject?) {

          if self.step != nil && (self.step as? ActivityQuestionStep) != nil
            && ((self.step as? ActivityQuestionStep)?.resultType as? String)! == "continuousScale"
          {
            let formatDict: [String: Any]

            formatDict = ((self.step as? ActivityQuestionStep)?.formatDict)!
            let maxFractionDigit = formatDict[
              kStepQuestionContinuosScaleMaxFractionDigits
            ]

            if (maxFractionDigit as? Int)! == 0 {
              self.value = round((stepTypeResult.scaleAnswer as? Double)!)

            } else if (maxFractionDigit as? Int)! == 1 {
              let v = (stepTypeResult.scaleAnswer as? Double)!
              self.value = Double(round(10 * v) / 10)

            } else if (maxFractionDigit as? Int)! == 2 {
              let v = (stepTypeResult.scaleAnswer as? Double)!
              self.value = Double(round(100 * v) / 100)

            } else if (maxFractionDigit as? Int)! == 3 {
              let v = (stepTypeResult.scaleAnswer as? Double)!
              self.value = Double(round(1000 * v) / 1000)

            } else if (maxFractionDigit as? Int)! == 4 {
              let v = (stepTypeResult.scaleAnswer as? Double)!
              self.value = Double(round(10000 * v) / 10000)

            } else {
              self.value = stepTypeResult.scaleAnswer as? Double
            }
          } else {
            self.value = stepTypeResult.scaleAnswer as? Double
          }
        } else {
          self.skipped = true  // Result is not avaiable because the Step was skippable and skipped.
        }
      } else if let stepTypeResult = questionstepResult as? ORKChoiceQuestionResult {
        if let choices = stepTypeResult.choiceAnswers,
          choices.count > 0
        {
          self.value = stepTypeResult.choiceAnswers?.first
        } else {
          self.skipped = true  // Choice count should be greater than 0 unless user skipped the Step.
        }
      }

    case ORKQuestionType.singleChoice.rawValue:

      let stepTypeResult = (questionstepResult as? ORKChoiceQuestionResult)!
      var resultType: String? = (self.step?.resultType as? String)!

      // for form we have to assign the step type of each form item
      if resultType == "grouped" {
        resultType = self.subTypeForForm
      }

      if Utilities.isValidObject(someObject: stepTypeResult.choiceAnswers as AnyObject?) {
        if (stepTypeResult.choiceAnswers?.count)! > 0 {

          if resultType == QuestionStepType.imageChoice.rawValue
            || resultType
              == QuestionStepType
              .valuePicker.rawValue
          {

            // for image choice and valuepicker

            let resultValue: String! = "\(stepTypeResult.choiceAnswers!.first!)"

            self.value = (resultValue == nil ? "" : resultValue)

          } else {
            // for text choice
            var resultValue: [Any] = []
            let selectedValue = stepTypeResult.choiceAnswers?.first

            if let stringValue = selectedValue as? String {
              resultValue.append(stringValue)
            } else if let otherDict = selectedValue as? [String: Any] {
              resultValue.append(otherDict)
            } else {
              resultValue.append(selectedValue as Any)
            }

            self.value = resultValue
          }

        } else {
          if resultType == QuestionStepType.imageChoice.rawValue
            || resultType
              == QuestionStepType
              .valuePicker.rawValue
          {
            self.value = ""

          } else {
            self.skipped = true
          }
        }
      } else {
        if resultType == QuestionStepType.imageChoice.rawValue
          || resultType
            == QuestionStepType
            .valuePicker.rawValue
        {
          self.value = ""

        } else {
          self.skipped = true
        }
      }
    case ORKQuestionType.multipleChoice.rawValue:
      // textchoice + imageChoice
      let stepTypeResult = questionstepResult as? ORKChoiceQuestionResult
      if let answers = stepTypeResult?.choiceAnswers {
        var resultArray: [Any] = []
        for value in answers {
          if let stringValue = value as? String {
            resultArray.append(stringValue)
          } else if let otherDict = value as? [String: Any] {
            resultArray.append(otherDict)
          } else {
            resultArray.append(value)
          }
        }
        self.value = resultArray
      } else {
        self.skipped = true
      }

    case ORKQuestionType.boolean.rawValue:
      let stepTypeResult = questionstepResult as? ORKBooleanQuestionResult
      if let answer = stepTypeResult?.booleanAnswer {
        self.value = answer == 1 ? true : false
      } else {
        self.skipped = true
      }

    case ORKQuestionType.integer.rawValue, ORKQuestionType.decimal.rawValue:
      // numeric type
      let stepTypeResult = questionstepResult as? ORKNumericQuestionResult
      if let answer = stepTypeResult?.numericAnswer {
        self.value = Double(truncating: answer)
      } else {
        self.skipped = true
      }

    case ORKQuestionType.timeOfDay.rawValue:
      let stepTypeResult = questionstepResult as? ORKTimeOfDayQuestionResult

      if let dateComponent = stepTypeResult?.dateComponentsAnswer {

        let hour: Int = dateComponent.hour ?? 0
        let minute: Int = dateComponent.minute ?? 0
        let seconds: Int = dateComponent.second ?? 0

        self.value =
          ((hour < 10 ? ("0" + "\(hour)") : "\(hour)") + ":" + (minute < 10 ? ("0" + "\(minute)") : "\(minute)")
            + ":" + (seconds < 10 ? ("0" + "\(seconds)") : "\(seconds)"))
      } else {
        self.skipped = true
      }

    case ORKQuestionType.date.rawValue, ORKQuestionType.dateAndTime.rawValue:
      let stepTypeResult = questionstepResult as? ORKDateQuestionResult
      if let dateString = stepTypeResult?.dateAnswer,
        let date = Utilities.getStringFromDate(date: dateString)
      {
        self.value = date
      } else {
        self.skipped = true
      }

    case ORKQuestionType.text.rawValue:
      // text + email
      let stepTypeResult = questionstepResult as? ORKTextQuestionResult
      if let value = stepTypeResult?.answer as? String {
        self.value = value
      } else {
        self.skipped = true
      }

    case ORKQuestionType.timeInterval.rawValue:
      let stepTypeResult = questionstepResult as? ORKTimeIntervalQuestionResult
      if let intervalAnswer = stepTypeResult?.intervalAnswer {
        self.value = Double(truncating: intervalAnswer) / 3600
      } else {
        self.skipped = true
      }

    case ORKQuestionType.height.rawValue:
      let stepTypeResult = questionstepResult as? ORKNumericQuestionResult
      if let answer = stepTypeResult?.numericAnswer {
        self.value = Double(truncating: answer)
      } else {
        self.skipped = true
      }

    case ORKQuestionType.location.rawValue:
      let stepTypeResult = questionstepResult as? ORKLocationQuestionResult
      if let locationAnswer = stepTypeResult?.locationAnswer {
        if CLLocationCoordinate2DIsValid(locationAnswer.coordinate) {
          let lat = locationAnswer.coordinate.latitude
          let long = locationAnswer.coordinate.longitude
          self.value = "\(lat)" + "," + "\(long)"
        } else {
          self.value = "0.0,0.0"
        }
      } else {
        self.skipped = true
      }
    default: break
    }
  }

}
