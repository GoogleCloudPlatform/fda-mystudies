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

// Step Constants
let kStepTitle = "title"
let kStepQuestionTypeValue = "QuestionType"

// Question Api Constants

let kStepQuestionPhi = "phi"
let kStepQuestionFormat = "format"
let kStepQuestionHealthDataKey = "healthDataKey"

// ScaleQuestion type Api Constants
let kStepQuestionScaleMaxValue = "maxValue"
let kStepQuestionScaleMinValue = "minValue"
let kStepQuestionScaleDefaultValue = "default"
let kStepQuestionScaleStep = "step"
let kStepQuestionScaleVertical = "vertical"
let kStepQuestionScaleMaxDesc = "maxDesc"
let kStepQuestionScaleMinDesc = "minDesc"
let kStepQuestionScaleMaxImage = "maxImage"
let kStepQuestionScaleMinImage = "minImage"

// ContinuosScaleQuestion Type Api Constants

let kStepQuestionContinuosScaleMaxValue = "maxValue"
let kStepQuestionContinuosScaleMinValue = "minValue"
let kStepQuestionContinuosScaleDefaultValue = "default"
let kStepQuestionContinuosScaleMaxFractionDigits = "maxFractionDigits"
let kStepQuestionContinuosScaleVertical = "vertical"
let kStepQuestionContinuosScaleMaxDesc = "maxDesc"
let kStepQuestionContinuosScaleMinDesc = "minDesc"
let kStepQuestionContinuosScaleMaxImage = "maxImage"
let kStepQuestionContinuosScaleMinImage = "minImage"

// TextScaleQuestion Type Api Constants
let kStepQuestionTextScaleTextChoices = "textChoices"
let kStepQuestionTextScaleDefault = "default"
let kStepQuestionTextScaleVertical = "vertical"

// ORKTextChoice Type Api Constants

let kORKTextChoiceText = "text"
let kORKTextChoiceValue = "value"
let kORKTextChoiceDetailText = "detail"
let kORKTextChoiceExclusive = "exclusive"
let kORKOTherChoice = "other"

// StepQuestionImageChoice Type Api Constants

let kStepQuestionImageChoices = "imageChoices"

let kStepQuestionImageChoiceImage = "image"
let kStepQuestionImageChoiceSelectedImage = "selectedImage"
let kStepQuestionImageChoiceText = "text"
let kStepQuestionImageChoiceValue = "value"

// TextChoiceQuestion Type Api Constants

let kStepQuestionTextChoiceTextChoices = "textChoices"
let kStepQuestionTextChoiceSelectionStyle = "selectionStyle"

let kStepQuestionTextChoiceSelectionStyleSingle = "Single"
let kStepQuestionTextChoiceSelectionStyleMultiple = "Multiple"

// NumericQuestion Type Api Constants

let kStepQuestionNumericStyle = "style"
let kStepQuestionNumericUnit = "unit"
let kStepQuestionNumericMinValue = "minValue"
let kStepQuestionNumericMaxValue = "maxValue"
let kStepQuestionNumericPlaceholder = "placeholder"

// DateQuestion Type Api Constants

let kStepQuestionDateStyle = "style"
let kStepQuestionDateMinDate = "minDate"
let kStepQuestionDateMaxDate = "maxDate"
let kStepQuestionDateDefault = "default"
let kStepQuestionDateRange = "dateRange"
let kStepQuestionDateStyleDate = "Date"
let kStepQuestionDateStyleDateTime = "Date-Time"

// TextQuestion Type Api Constants

let kStepQuestionTextMaxLength = "maxLength"
let kStepQuestionTextValidationRegex = "validationRegex"
let kStepQuestionTextInvalidMessage = "invalidMessage"
let kStepQuestionTextMultipleLines = "multipleLines"
let kStepQuestionTextPlaceholder = "placeholder"

/// EmailQuestion Type Api Constants
let kStepQuestionEmailPlaceholder = "placeholder"

/// TimeIntervalQuestion Type Api Constants
let kStepQuestionTimeIntervalDefault = "default"
let kStepQuestionTimeIntervalStep = "step"

/// height Type Api Constants
let kStepQuestionHeightMeasurementSystem = "measurementSystem"
let kStepQuestionHeightPlaceholder = "placeholder"

/// LocationQuestion Type Api Constants
let kStepQuestionLocationUseCurrentLocation = "useCurrentLocation"

typealias JSONDictionary = [String: Any]

enum DateRange: String {
  case untilCurrent = "untilCurrent"
  case afterCurrent = "afterCurrent"
  case custom = "custom"
  case defaultValue = ""
}

enum DateStyle: String {
  case date = "Date"
  case dateAndTime = "Date-Time"
}

enum HeightMeasurementSystem: String {
  case local = "Local"
  case metric = "Metric"
  case us = "US"
}

enum QuestionStepType: String {

  /// Step for  Boolean question.
  case boolean = "boolean"

  /// Step for  example of date entry.
  case date = "date"

  /// Step for  example of date and time entry.
  case dateTimeQuestionStep

  /// Step for  example of height entry.
  case height = "height"

  /// Step for  image choice question.
  case imageChoice = "imageChoice"

  /// Step for  location entry.
  case location = "location"

  /// Step with examples of numeric questions.
  case numeric = "numeric"

  case numericNoUnitQuestionStep

  /// Step with examples of questions with sliding scales.
  case scale = "scale"

  case continuousScale = "continuousScale"
  case discreteVerticalscale
  case continuousVerticalscale
  case textscale = "textScale"
  case textVerticalscale

  /// Step for  example of free text entry.
  case text = "text"

  /// Step for  example of a multiple choice question.
  case textChoice = "textChoice"

  /// Step for  example of time of day entry.
  case timeOfDay = "timeOfDay"

  /// Step for  example of time interval entry.
  case timeInterval = "timeInterval"

  /// Step for  value picker.
  case valuePicker = "valuePicker"

  /// Step for  example of validated text entry.
  case email = "email"

  case validatedtextDomain

  /// Image capture Step specific identifiers.
  case imageCaptureStep

  /// Video capture Step specific identifiers.
  case videoCaptureStep

  /// Step for  example of waiting.
  case waitStepDeterminate

  case waitStepIndeterminate

  /// Consent Step specific identifiers.
  case visualConsentStep

  case consentSharingStep
  case consentReviewStep
  case consentDocumentParticipantSignature
  case consentDocumentInvestigatorSignature

  /// Account creation Step specific identifiers.
  case registrationStep

  case waitStep
  case verificationStep

  // Login Step specific identifiers.
  case loginStep

  /// Passcode Step specific identifiers.
  case passcodeStep

  /// Video instruction Steps.
  case videoInstructionStep

}

class ActivityQuestionStep: ActivityStep {

  var formatDict: [String: Any]?

  var healthDataKey: String?

  /// Following params exclusively used for texscale
  var textScaleDefaultIndex: Int? = -1

  var textScaleDefaultValue: String? = ""

  /// default Initializer Method
  override init() {

    super.init()
    self.healthDataKey = ""
    self.formatDict? = Dictionary()
  }

  /// Initializer method which initializes all params
  /// - Parameter stepDict: Dictionary which contains all the properties of ActivityQuestion Step
  override func initWithDict(stepDict: [String: Any]) {

    if Utilities.isValidObject(someObject: stepDict as AnyObject?) {

      super.initWithDict(stepDict: stepDict)

      if Utilities.isValidObject(someObject: stepDict[kStepQuestionFormat] as AnyObject) {
        self.formatDict = (stepDict[kStepQuestionFormat] as? Dictionary)!
      }
      if Utilities.isValidValue(someObject: stepDict[kStepQuestionHealthDataKey] as AnyObject) {
        self.healthDataKey = stepDict[kStepQuestionHealthDataKey] as! String?
      }
    }
  }

  /// Creates question step based on QuestionStepType and returns ORKQuestionStep
  func getQuestionStep() -> ORKQuestionStep? {

    if Utilities.isValidValue(someObject: resultType as AnyObject?) {

      var questionStepAnswerFormat: ORKAnswerFormat?  // Contains the answerFormat for specific question Type

      var questionStep: ORKQuestionStep?  // Contains the QuestionStep instance
      var placeholderText: String? = ""

      // Assigning the answerFormat for the questionStep based on questionStep
      switch QuestionStepType(rawValue: (resultType as? String)!)! as QuestionStepType {
      case .scale:

        if Utilities.isValidValue(
          someObject: formatDict?[kStepQuestionScaleMaxValue] as AnyObject?
        )
          && Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionScaleMinValue] as AnyObject?
          )
          && Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionScaleDefaultValue] as AnyObject?
          )
          && Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionScaleStep] as AnyObject?
          )
          && Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionScaleVertical] as AnyObject?
          )
        {
          let maxDesc = formatDict?[kStepQuestionScaleMaxDesc] as? String
          let minDesc = formatDict?[kStepQuestionScaleMinDesc] as? String

          // Checking if difference is divisible
          let difference =
            (formatDict?[kStepQuestionScaleMaxValue] as? Int)! - (formatDict?[kStepQuestionScaleMinValue] as? Int)!
          let divisibleValue = difference % (formatDict?[kStepQuestionScaleStep] as? Int)!
          let stepsValue = difference / (formatDict?[kStepQuestionScaleStep] as? Int)!
          let defaultPosition = (formatDict?[kStepQuestionScaleDefaultValue] as? Int)!
          var defaultValue = defaultPosition * stepsValue

          // Setting the default Value if Exist
          if defaultValue > (formatDict?[kStepQuestionScaleMaxValue] as? Int)! {
            defaultValue = (formatDict?[kStepQuestionScaleMaxValue] as? Int)!
          }

          if ((formatDict?[kStepQuestionScaleMaxValue] as? Int)! != (formatDict?[kStepQuestionScaleMinValue] as? Int)!)
            && divisibleValue == 0 && (stepsValue >= 1 && stepsValue <= 13)
          {

            questionStepAnswerFormat = ORKAnswerFormat.scale(
              withMaximumValue: (formatDict?[kStepQuestionScaleMaxValue] as? Int)!,
              minimumValue: (formatDict?[kStepQuestionScaleMinValue] as? Int)!,
              defaultValue: (formatDict?[kStepQuestionScaleDefaultValue] as? Int)!,
              step: (formatDict?[kStepQuestionScaleStep] as? Int)!,
              vertical: (formatDict?[kStepQuestionScaleVertical] as? Bool)!,
              maximumValueDescription: maxDesc,
              minimumValueDescription: minDesc
            )
            // Setting the Max & Min Images if exist
            if Utilities.isValidValue(
              someObject: (formatDict?[kStepQuestionScaleMaxImage] as? String as AnyObject)
            )
              && Utilities.isValidValue(
                someObject: (formatDict?[kStepQuestionScaleMinImage] as? String as AnyObject)
              )
            {

              let minImageBase64String = (formatDict![kStepQuestionScaleMinImage] as? String)!
              let minNormalImageData = NSData(
                base64Encoded: minImageBase64String,
                options: .ignoreUnknownCharacters
              )
              let minNormalImage: UIImage = UIImage(
                data: minNormalImageData! as Data
              )!

              let maxImageBase64String = (formatDict![kStepQuestionScaleMaxImage] as? String)!
              let maxNormalImageData = NSData(
                base64Encoded: maxImageBase64String,
                options: .ignoreUnknownCharacters
              )
              let maxNormalImage: UIImage = UIImage(
                data: maxNormalImageData! as Data
              )!

              (questionStepAnswerFormat as? ORKScaleAnswerFormat)!.minimumImage = minNormalImage
              (questionStepAnswerFormat as? ORKScaleAnswerFormat)!.maximumImage = maxNormalImage
            }
          } else {
            return nil
          }
        } else {
          return nil
        }
      case .continuousScale:

        if Utilities.isValidValue(
          someObject: formatDict?[kStepQuestionContinuosScaleMaxFractionDigits]
            as AnyObject?
        )
          && formatDict?[kStepQuestionContinuosScaleVertical] != nil
        {

          let maxDesc = formatDict?[kStepQuestionContinuosScaleMaxDesc] as? String
          let minDesc = formatDict?[kStepQuestionContinuosScaleMinDesc] as? String
          var maxValue = 0.0
          var minValue = -1.0
          var defaultValue = 0.0
          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionContinuosScaleMaxValue] as AnyObject?
          ) {
            maxValue = (formatDict?[kStepQuestionContinuosScaleMaxValue] as? Double)!

          } else {
            if let value = (formatDict?[kStepQuestionContinuosScaleMaxValue] as? Double) {

              if value > 0 {
                minValue = (formatDict?[kStepQuestionContinuosScaleMaxValue] as? Double)!
              }
            }
          }
          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionContinuosScaleMaxValue] as AnyObject?
          ) {
            minValue = (formatDict?[kStepQuestionContinuosScaleMinValue] as? Double)!

          } else {
            if let value = (formatDict?[kStepQuestionContinuosScaleMinValue] as? Double) {

              if value > 0 {
                minValue = (formatDict?[kStepQuestionContinuosScaleMinValue] as? Double)!
              }
            }
          }

          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionContinuosScaleDefaultValue]
              as AnyObject?
          ) {
            defaultValue = (formatDict?[kStepQuestionContinuosScaleDefaultValue] as? Double)!

          } else {
            if let value = (formatDict?[kStepQuestionContinuosScaleDefaultValue] as? Double) {

              if value > 0 {
                defaultValue = (formatDict?[kStepQuestionContinuosScaleDefaultValue] as? Double)!
              }
            }
          }

          if (formatDict?[kStepQuestionContinuosScaleMinValue] as? Double)!
            != (formatDict?[kStepQuestionContinuosScaleMaxValue] as? Double)!
          {

            questionStepAnswerFormat = ORKAnswerFormat.continuousScale(
              withMaximumValue: maxValue,
              minimumValue: minValue,
              defaultValue: defaultValue,
              maximumFractionDigits: (formatDict?[kStepQuestionContinuosScaleMaxFractionDigits] as? Int)!,
              vertical: (formatDict?[kStepQuestionContinuosScaleVertical] as? Bool)!,
              maximumValueDescription: maxDesc,
              minimumValueDescription: minDesc
            )

            // setting the min & max images if exists
            if Utilities.isValidValue(
              someObject: (formatDict?[kStepQuestionContinuosScaleMaxImage] as? String as AnyObject)
            )
              && Utilities.isValidValue(
                someObject: (formatDict?[kStepQuestionContinuosScaleMinImage] as? String as AnyObject)
              )
            {

              let minImageBase64String = (formatDict![kStepQuestionContinuosScaleMinImage] as? String)!
              let minNormalImageData = NSData(
                base64Encoded: minImageBase64String,
                options: .ignoreUnknownCharacters
              )
              let minNormalImage: UIImage = UIImage(
                data: minNormalImageData! as Data
              )!

              let maxImageBase64String = (formatDict![kStepQuestionContinuosScaleMaxImage] as? String)!
              let maxNormalImageData = NSData(
                base64Encoded: maxImageBase64String,
                options: .ignoreUnknownCharacters
              )
              let maxNormalImage: UIImage = UIImage(
                data: maxNormalImageData! as Data
              )!

              (questionStepAnswerFormat as? ORKContinuousScaleAnswerFormat)!
                .minimumImage = minNormalImage
              (questionStepAnswerFormat as? ORKContinuousScaleAnswerFormat)!
                .maximumImage = maxNormalImage
            }
          } else {
            return nil
          }
        } else {
          return nil
        }
      case .textscale:

        if Utilities.isValidObject(
          someObject: formatDict?[kStepQuestionTextScaleTextChoices] as AnyObject?
        )
          && Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionTextScaleDefault] as AnyObject?
          )
          && Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionTextScaleVertical] as AnyObject?
          )
        {

          let textChoiceArray: [ORKTextChoice]?

          let defaultValue = (formatDict?[kStepQuestionTextScaleDefault] as? Int)!
          self.textScaleDefaultValue = "\(defaultValue)"

          let textChoiceDict =
            formatDict?[kStepQuestionTextScaleTextChoices] as? [Any]
            ?? []
          textChoiceArray = self.getTextChoices(dataArray: textChoiceDict).0

          questionStepAnswerFormat = ORKAnswerFormat.textScale(
            with: textChoiceArray!,
            defaultIndex: defaultValue - 1,
            vertical: (formatDict?[kStepQuestionTextScaleVertical] as? Bool)!
          )
        } else {
          return nil
        }
      case .valuePicker:

        if Utilities.isValidObject(
          someObject: formatDict?[kStepQuestionTextScaleTextChoices] as AnyObject?
        ) {

          let textChoiceArray: [ORKTextChoice]?

          let textChoiceDict =
            formatDict?[kStepQuestionTextScaleTextChoices] as? [Any]
            ?? []
          textChoiceArray = self.getTextChoices(dataArray: textChoiceDict).0

          questionStepAnswerFormat = ORKAnswerFormat.valuePickerAnswerFormat(
            with: textChoiceArray!
          )
        } else {
          return nil
        }
      case .imageChoice:

        if Utilities.isValidObject(
          someObject: formatDict?[kStepQuestionImageChoices] as AnyObject?
        ) {

          let imageChoiceArray: [ORKImageChoice]?
          imageChoiceArray = self.getImageChoices(
            dataArray: (formatDict?[kStepQuestionImageChoices] as? NSArray)!
          )
          if imageChoiceArray == nil {
            return nil
          }
          questionStepAnswerFormat = ORKAnswerFormat.choiceAnswerFormat(
            with: imageChoiceArray!
          )

        } else {
          return nil
        }
      case .textChoice:
        if Utilities.isValidObject(
          someObject: formatDict?[kStepQuestionTextChoiceTextChoices] as AnyObject?
        )
          && Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionTextChoiceSelectionStyle] as AnyObject?
          )
        {

          let textChoiceDict =
            formatDict?[kStepQuestionTextChoiceTextChoices] as? [Any]
            ?? []

          let choiceResult = self.getTextChoices(dataArray: textChoiceDict)
          var otherChoice: OtherChoice? = choiceResult.1

          otherChoice = (otherChoice == nil) ? OtherChoice() : otherChoice

          let textChoiceArray: [ORKTextChoice]? = choiceResult.0

          if ((formatDict?[kStepQuestionTextChoiceSelectionStyle] as? String)!)
            == kStepQuestionTextChoiceSelectionStyleSingle
          {
            // single choice
            questionStepAnswerFormat = ORKTextChoiceAnswerFormat(
              style: ORKChoiceAnswerStyle.singleChoice,
              textChoices: textChoiceArray!
            )
          } else if ((formatDict?[kStepQuestionTextChoiceSelectionStyle] as? String)!)
            == kStepQuestionTextChoiceSelectionStyleMultiple
          {
            // multiple choice
            questionStepAnswerFormat = ORKTextChoiceAnswerFormat(
              style: ORKChoiceAnswerStyle.multipleChoice,
              textChoices: textChoiceArray!
            )
          } else {
            return nil
          }

          questionStep = QuestionStep(
            identifier: key!,
            title: "",
            question: title!,
            answer: questionStepAnswerFormat!,
            otherChoice: otherChoice!
          )

          // By default a step is skippable
          if skippable == false {
            questionStep?.isOptional = false
          }
          // setting the placeholder Value if exist any
          if Utilities.isValidValue(someObject: placeholderText as AnyObject?) {
            questionStep?.placeholder = placeholderText
          }
          questionStep?.text = text
          return questionStep

        } else {
          return nil
        }

      case .boolean:
        questionStepAnswerFormat = ORKBooleanAnswerFormat()

      case .numeric:

        if Utilities.isValidValue(
          someObject: formatDict?[kStepQuestionNumericStyle] as AnyObject?
        ) {

          var maxValue = formatDict?[kStepQuestionNumericMaxValue] as? NSNumber

          let minValue = formatDict?[kStepQuestionNumericMinValue] as? NSNumber

          if maxValue != nil && maxValue == 0 {
            if let minValue = minValue,
              !(Int(truncating: minValue) < 0)
            {
              maxValue = nil  // Max value can't be zero if the min value is greater than 0.
            }
          }

          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionNumericPlaceholder] as AnyObject?
          ) {
            placeholderText = formatDict?[kStepQuestionNumericPlaceholder] as? String
          }

          let localizedQuestionStepAnswerFormatUnit = NSLocalizedString(
            (formatDict?[kStepQuestionNumericUnit] as? String)!,
            comment: ""
          )

          let style =
            ((formatDict?[kStepQuestionNumericStyle] as? String)! == "Decimal")
            ? 0 : 1

          switch ORKNumericAnswerStyle(rawValue: style)! as ORKNumericAnswerStyle {
          case .integer:  // Integer Question Step

            if Utilities.isValidValue(someObject: self.healthDataKey as AnyObject?) {

              let quantityTypeId: HKQuantityTypeIdentifier =
                HKQuantityTypeIdentifier
                .init(
                  rawValue: self.healthDataKey!
                )

              let quantityType = HKQuantityType.quantityType(
                forIdentifier: quantityTypeId
              )

              var unit: HKUnit?
              // Setting the Unit if valid unit exist
              if localizedQuestionStepAnswerFormatUnit != ""
                && self.isUnitValid(
                  unit: localizedQuestionStepAnswerFormatUnit
                )
              {
                unit = HKUnit.init(from: localizedQuestionStepAnswerFormatUnit)
              }

              questionStepAnswerFormat = ORKHealthKitQuantityTypeAnswerFormat.init(
                quantityType: quantityType!,
                unit: unit,
                style: ORKNumericAnswerStyle.integer
              )

            } else {
              if minValue != nil || maxValue != nil {
                questionStepAnswerFormat = ORKNumericAnswerFormat.init(
                  style: ORKNumericAnswerStyle.integer,
                  unit: localizedQuestionStepAnswerFormatUnit,
                  minimum: minValue,
                  maximum: maxValue
                )
              } else {
                questionStepAnswerFormat = ORKAnswerFormat.integerAnswerFormat(
                  withUnit: localizedQuestionStepAnswerFormatUnit
                )
              }
            }
          case .decimal:  // Decimal Question Step
            if Utilities.isValidValue(someObject: self.healthDataKey as AnyObject?) {

              let quantityTypeId = HKQuantityTypeIdentifier.init(
                rawValue: self.healthDataKey!
              )
              var unit: HKUnit?
              // Setting the Unit if valid unit exist
              if localizedQuestionStepAnswerFormatUnit != ""
                && self.isUnitValid(
                  unit: localizedQuestionStepAnswerFormatUnit
                )
              {
                unit = HKUnit.init(from: localizedQuestionStepAnswerFormatUnit)
              }
              questionStepAnswerFormat = ORKHealthKitQuantityTypeAnswerFormat.init(
                quantityType: HKQuantityType.quantityType(
                  forIdentifier: quantityTypeId
                )!,
                unit: unit,
                style: ORKNumericAnswerStyle.decimal
              )

            } else {

              if minValue != nil || maxValue != nil {
                questionStepAnswerFormat = ORKNumericAnswerFormat.init(
                  style: ORKNumericAnswerStyle.decimal,
                  unit: localizedQuestionStepAnswerFormatUnit,
                  minimum: minValue,
                  maximum: maxValue
                )
              } else {
                questionStepAnswerFormat = ORKAnswerFormat.decimalAnswerFormat(
                  withUnit: localizedQuestionStepAnswerFormatUnit
                )
              }
            }
          @unknown default:
            break
          }
        } else {
          return nil
        }
      case .timeOfDay:
        questionStepAnswerFormat = ORKAnswerFormat.timeOfDayAnswerFormat()

      case .date:

        if Utilities.isValidValue(
          someObject: formatDict?[kStepQuestionDateStyle] as AnyObject?
        ) {

          let dateFormatter = DateFormatter()
          dateFormatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

          var dateRange: DateRange? = DateRange.defaultValue

          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionDateRange] as AnyObject?
          ) {

            dateRange = DateRange.init(
              rawValue: (formatDict?[kStepQuestionDateRange] as? String)!
            )
          }

          let defaultDate: NSDate? =
            dateFormatter.date(
              from: (formatDict?[kStepQuestionDateDefault] as? String)!
            ) as NSDate?
          var minimumDate: NSDate? =
            dateFormatter.date(
              from: (formatDict?[kStepQuestionDateMinDate] as? String)!
            ) as NSDate?
          var maximumDate: NSDate? =
            dateFormatter.date(
              from: (formatDict?[kStepQuestionDateMaxDate] as? String)!
            ) as NSDate?

          // Setting the date Range
          switch dateRange! {
          case .untilCurrent:  // Any date less than Current Date
            maximumDate = Date.init(timeIntervalSinceNow: 0) as NSDate
          case .afterCurrent:  // Any date greater than Current
            minimumDate = Date.init(timeIntervalSinceNow: 86400) as NSDate
          case .defaultValue: break
          case .custom: break
          }

          switch DateStyle(rawValue: (formatDict?[kStepQuestionDateStyle] as? String)!)!
            as DateStyle
          {

          case .date:
            questionStepAnswerFormat = ORKAnswerFormat.dateAnswerFormat(
              withDefaultDate: defaultDate as Date?,
              minimumDate: minimumDate as Date?,
              maximumDate: maximumDate as Date?,
              calendar: NSCalendar.current
            )

          case .dateAndTime:
            questionStepAnswerFormat = ORKAnswerFormat.dateTime(
              withDefaultDate: defaultDate as Date?,
              minimumDate: minimumDate as Date?,
              maximumDate: maximumDate as Date?,
              calendar: NSCalendar.current
            )

          }

        } else {
          return nil
        }
      case .text:

        if Utilities.isValidValue(
          someObject: formatDict?[kStepQuestionTextMultipleLines] as AnyObject?
        ) {

          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionNumericPlaceholder] as AnyObject?
          ) {
            placeholderText = formatDict?[kStepQuestionNumericPlaceholder] as? String
          }

          var answerFormat = ORKAnswerFormat.textAnswerFormat()

          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionTextValidationRegex] as AnyObject?
          )
            && Utilities
              .isValidValue(
                someObject: formatDict?[kStepQuestionTextInvalidMessage] as AnyObject?
              )
          {

            var regex: NSRegularExpression?

            regex = try? NSRegularExpression(
              pattern: (formatDict?[kStepQuestionTextValidationRegex] as? String)!,
              options: []
            )

            if regex != nil {
              answerFormat = ORKAnswerFormat.textAnswerFormat(
                withValidationRegularExpression: regex!,
                invalidMessage: (formatDict?[kStepQuestionTextInvalidMessage] as? String)!
              )
            }
          } else {
            answerFormat.invalidMessage = nil
          }

          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionTextMaxLength] as AnyObject?
          ) {
            answerFormat.maximumLength = (formatDict?[kStepQuestionTextMaxLength] as? Int)!
          } else {
            answerFormat.maximumLength = 0
          }

          answerFormat.multipleLines = (formatDict?[kStepQuestionTextMultipleLines] as? Bool)!
          questionStepAnswerFormat = answerFormat

        } else {
          return nil
        }
      case .email:

        questionStepAnswerFormat = ORKAnswerFormat.emailAnswerFormat()
        if Utilities.isValidValue(
          someObject: formatDict?[kStepQuestionNumericPlaceholder] as AnyObject?
        ) {
          placeholderText = formatDict?[kStepQuestionNumericPlaceholder] as? String
        }
      case .timeInterval:

        if Utilities.isValidValue(
          someObject: formatDict?[kStepQuestionTimeIntervalStep] as AnyObject?
        ) {
          let defaultTimeInterval: Double?

          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionTimeIntervalDefault] as AnyObject?
          ) {
            defaultTimeInterval = Double(
              (formatDict?[kStepQuestionTimeIntervalDefault] as? Int)!
            )

          } else {
            defaultTimeInterval = Double(0.0)
          }

          if (formatDict?[kStepQuestionTimeIntervalStep] as? Int)! >= 1
            && (formatDict?[kStepQuestionTimeIntervalStep] as? Int)! <= 30
          {

            questionStepAnswerFormat = ORKAnswerFormat.timeIntervalAnswerFormat(
              withDefaultInterval: defaultTimeInterval!,
              step: (formatDict?[kStepQuestionTimeIntervalStep] as? Int)!
            )
          } else {
            return nil
          }
        } else {
          return nil
        }
      case .height:

        if Utilities.isValidValue(
          someObject: formatDict?[kStepQuestionHeightMeasurementSystem] as AnyObject?
        ) {

          let measurementSystem: ORKMeasurementSystem?
          switch HeightMeasurementSystem(
            rawValue: (formatDict?[kStepQuestionHeightMeasurementSystem] as? String)!
          )! {
          case .local:
            measurementSystem = .local
          case .metric:
            measurementSystem = .metric
          case .us:
            measurementSystem = .USC
          }
          questionStepAnswerFormat = ORKAnswerFormat.heightAnswerFormat(
            with: measurementSystem!
          )

          if Utilities.isValidValue(
            someObject: formatDict?[kStepQuestionNumericPlaceholder] as AnyObject?
          ) {
            placeholderText = formatDict?[kStepQuestionNumericPlaceholder] as? String
          }
        } else {
          return nil
        }
      case .location:
        if Utilities.isValidValue(
          someObject: formatDict?[kStepQuestionLocationUseCurrentLocation] as AnyObject?
        ) {
          let answerFormat = ORKAnswerFormat.locationAnswerFormat()
          answerFormat.useCurrentLocation = (formatDict?[kStepQuestionLocationUseCurrentLocation] as? Bool)!

          questionStepAnswerFormat = answerFormat
        } else {
          return nil
        }
      default: break
      }

      questionStep = ORKQuestionStep(
        identifier: key!,
        title: title!,
        answer: questionStepAnswerFormat
      )  // TBD: need to verify with API
      // By default a step is skippable
      if skippable == false {
        questionStep?.isOptional = false
      }
      // setting the placeholder Value if exist any
      if Utilities.isValidValue(someObject: placeholderText as AnyObject?) {
        questionStep?.placeholder = placeholderText
      }
      questionStep?.text = text
      return questionStep!
    } else {
      return nil
    }
  }

  /// Creates ORKTextChoice array and OtherChoice
  /// - Parameter dataArray: is either array of Dictionary or array of String which returns array of ORKTextChoice array and OtherChoice
  func getTextChoices(dataArray: [Any]) -> ([ORKTextChoice]?, OtherChoice?) {

    var textChoiceArray: [ORKTextChoice] = []
    var otherChoice: OtherChoice?

    if let dictArr = dataArray as? [JSONDictionary] {

      for dict in dictArr {

        let text = dict[kORKTextChoiceText] as? String ?? ""
        let value = dict[kORKTextChoiceValue] as? String ?? ""
        let detail = dict[kORKTextChoiceDetailText] as? String ?? ""
        let isExclusive = dict[kORKTextChoiceExclusive] as? Bool ?? false

        if let otherDict = dict[kORKOTherChoice] as? JSONDictionary {

          let placeholder = otherDict["placeholder"] as? String ?? "enter here"
          let isMandatory = otherDict["isMandatory"] as? Bool ?? false
          let textFieldReq = otherDict["textfieldReq"] as? Bool ?? false

          otherChoice = OtherChoice(
            isShowOtherCell: true,
            isShowOtherField: textFieldReq,
            otherTitle: text,
            placeholder: placeholder,
            isMandatory: isMandatory,
            isExclusive: isExclusive,
            detailText: detail,
            value: value
          )
          // No need to add other text choice
          continue
        }

        let choice = ORKTextChoice(
          text: text,
          detailText: detail,
          value: value as NSCoding & NSCopying & NSObjectProtocol,
          exclusive: isExclusive
        )

        textChoiceArray.append(choice)

      }

    } else if let titleArr = dataArray as? [String] {

      for (i, title) in titleArr.enumerated() {

        let choice = ORKTextChoice(
          text: title,
          value: i as NSCoding & NSCopying & NSObjectProtocol
        )

        if self.textScaleDefaultValue?.isEmpty == false && self.textScaleDefaultValue != "" {
          if title == self.textScaleDefaultValue {
            self.textScaleDefaultIndex = i
          }
        }
        textChoiceArray.append(choice)
      }

    }
    if textChoiceArray.isEmpty {
      return (nil, nil)
    } else {
      return (textChoiceArray, otherChoice)
    }

  }

  /// Creates ORKImageChoice array
  /// - Parameter dataArray: is array which returns array of ORKImageChoice array
  func getImageChoices(dataArray: NSArray) -> [ORKImageChoice]? {

    var imageChoiceArray: [ORKImageChoice]?
    imageChoiceArray = [ORKImageChoice]()

    if Utilities.isValidObject(someObject: dataArray) {

      for i in 0..<dataArray.count {

        if Utilities.isValidObject(someObject: dataArray[i] as AnyObject) {
          // Handle for array of dictionary
          let dict: NSDictionary = (dataArray[i] as? NSDictionary)!

          var value: String!

          if Utilities.isValidValue(
            someObject: dict[kStepQuestionImageChoiceValue] as AnyObject
          ) {
            value = (dict[kStepQuestionImageChoiceValue] as? String)!
          }

          if Utilities.isValidValue(
            someObject: dict[kStepQuestionImageChoiceImage] as AnyObject?
          )
            && Utilities.isValidValue(
              someObject: dict[kStepQuestionImageChoiceSelectedImage] as AnyObject?
            )
            && Utilities.isValidValue(
              someObject: dict[kStepQuestionImageChoiceText] as AnyObject?
            )
          {

            let base64String = (dict[kStepQuestionImageChoiceImage] as? String)!

            // generate ImageData from base64String
            let normalImageData = NSData(
              base64Encoded: base64String,
              options: .ignoreUnknownCharacters
            )

            // generate SelectedImageData from base64String
            let selectedImageData = NSData(
              base64Encoded: ((dict[kStepQuestionImageChoiceSelectedImage] as? String)!),
              options: .ignoreUnknownCharacters
            )

            // Create image Instance from Data
            let normalImage: UIImage = UIImage(data: (normalImageData as Data?)!)!
            let selectedImage: UIImage = UIImage(data: (selectedImageData as Data?)!)!

            // Create ORKImageChoice
            let choice = ORKImageChoice(
              normalImage: normalImage,
              selectedImage: selectedImage,
              text: dict[kStepQuestionImageChoiceText] as? String,
              value: value as NSCoding & NSCopying & NSObjectProtocol
            )

            imageChoiceArray?.append(choice)
          } else {
            return nil
          }
        } else {
          return nil
        }
      }
    } else {
      return nil
    }
    return imageChoiceArray!
  }

  /// Verifies whether the string passed is valid by comparing it with array of string specified for the Category
  /// - Parameter dataArray: contains string like kg,g and so on and returns a boolean indicating the validity of unit string.
  func isUnitValid(unit: String) -> Bool {

    let filePath = Bundle.main.path(forResource: "Units", ofType: ".json", inDirectory: nil)
    var resultDict: [String: Any]?
    let data = NSData(contentsOfFile: filePath!)
    do {
      resultDict =
        try JSONSerialization.jsonObject(with: data! as Data, options: [])
        as? [String: Any]

    } catch let error as NSError {
      Logger.sharedInstance.error("\(error)")
    }

    let categoryDict = (resultDict!["Category"] as? [String: String])!
    if let category = categoryDict[self.healthDataKey!] {
      let unitDict = (resultDict!["Unit"] as? [String: Any])!
      if let unitsArray = unitDict[category] as? [String] {
        if unitsArray.contains(unit) {
          return true
        } else {
          return false
        }
      }
      return false
    }
    return false
  }
}
