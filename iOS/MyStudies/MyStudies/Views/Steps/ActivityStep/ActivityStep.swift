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

// MARK: Api Constants

let kActivityStepType = "type"

let kActivityStepActivityId = "activityId"
let kActivityStepResultType = "resultType"
let kActivityStepKey = "key"
let kActivityStepTitle = "title"
let kActivityStepText = "text"
let kActivityStepSkippable = "skippable"
let kActivityStepGroupName = "groupName"
let kActivityStepRepeatable = "repeatable"
let kActivityStepRepeatableText = "repeatableText"
let kActivityStepDestinations = "destinations"

// MARK: Enum for ActivityStepType
enum ActivityStepType: String {
  case form = "form"
  case instruction = "instruction"
  case question = "question"
  case active = "task"

  case taskSpatialSpanMemory = "spatialSpanMemory"

  case taskTowerOfHanoi = "towerOfHanoi"

}

/// ActivityStep model class resembles ORKStep and stores all the properties of ORKStep. It contains additional properties for particular Step Type.
class ActivityStep {

  ///Stores the uniqueId of activity
  var activityId: String?

  /// specifies different activitystep types like instruction, question
  var type: ActivityStepType?

  var resultType: Any?

  /// Identifier
  var key: String?

  /// Title for ORKStep
  var title: String?

  /// Text for ORKStep
  var text: String?

  var skippable: Bool?

  var groupName: String?

  /// used for RepeatableFormStep
  var repeatable: Bool?

  /// used for RepeatableFormStep to add more form steps
  var repeatableText: String?

  /// stores the destination step for branching
  var destinations: [[String: Any]]?

  /// default Initializer Method
  init() {

    self.activityId = ""
    self.type = .question
    self.resultType = ""
    self.key = ""
    self.title = ""
    self.text = ""
    self.skippable = false
    self.groupName = ""
    self.repeatable = false
    self.repeatableText = ""
    self.destinations = Array()

  }

  /// Initializer method which initializes all params
  init(
    activityId: String,
    type: ActivityStepType,
    resultType: String,
    key: String,
    title: String,
    text: String,
    skippable: Bool,
    groupName: String,
    repeatable: Bool,
    repeatableText: String,
    destinations: [[String: Any]]
  ) {

    self.activityId = activityId
    self.type = type
    self.resultType = resultType
    self.key = key
    self.title = title
    self.text = text
    self.skippable = skippable
    self.groupName = groupName
    self.repeatable = repeatable
    self.repeatableText = repeatableText
    self.destinations = destinations
  }

  /// Initializer method which initializes all params
  /// - Parameter stepDict: Dictionary which contains all the properties of ActivityStep
  func initWithDict(stepDict: [String: Any]) {

    if Utilities.isValidObject(someObject: stepDict as AnyObject?) {

      if Utilities.isValidValue(someObject: stepDict[kActivityStepActivityId] as AnyObject) {
        self.activityId = stepDict[kActivityStepActivityId] as? String
      }

      if Utilities.isValidValue(someObject: stepDict[kActivityStepType] as AnyObject) {
        self.type = stepDict[kActivityStepType] as? ActivityStepType
      }
      if Utilities.isValidValue(someObject: stepDict[kActivityStepResultType] as AnyObject) {
        self.resultType = stepDict[kActivityStepResultType] as? String
      }
      if Utilities.isValidValue(someObject: stepDict[kActivityStepKey] as AnyObject) {
        self.key = stepDict[kActivityStepKey] as? String
      }
      if Utilities.isValidValue(someObject: stepDict[kActivityStepTitle] as AnyObject) {
        self.title = stepDict[kActivityStepTitle] as? String
      }
      if Utilities.isValidValue(someObject: stepDict[kActivityStepText] as AnyObject) {
        self.text = stepDict[kActivityStepText] as? String
      }
      if Utilities.isValidValue(someObject: stepDict[kActivityStepSkippable] as AnyObject) {
        self.skippable = stepDict[kActivityStepSkippable] as? Bool
      }
      if Utilities.isValidValue(someObject: stepDict[kActivityStepGroupName] as AnyObject) {
        self.groupName = stepDict[kActivityStepGroupName] as? String
      }
      if Utilities.isValidValue(someObject: stepDict[kActivityStepRepeatable] as AnyObject) {
        self.repeatable = stepDict[kActivityStepRepeatable] as? Bool
      }
      if Utilities.isValidValue(
        someObject: stepDict[kActivityStepRepeatableText] as AnyObject
      ) {
        self.repeatableText = stepDict[kActivityStepRepeatableText] as? String
      }
      if Utilities.isValidObject(someObject: stepDict[kActivityStepDestinations] as AnyObject) {
        self.destinations = stepDict[kActivityStepDestinations] as? [[String: Any]]
      }
    }
  }

}
