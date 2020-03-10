package com.harvard.studyappmodule.custom;

import com.harvard.studyappmodule.custom.activetask.StepCountClass;
import com.harvard.studyappmodule.custom.activetask.Tappingactivity;
import com.harvard.studyappmodule.custom.question.AudioQuestionbody;
import com.harvard.studyappmodule.custom.question.ContinuousScaleQuestion;
import com.harvard.studyappmodule.custom.question.CustomDateQuestionBody;
import com.harvard.studyappmodule.custom.question.DecimalUnitQuestionBody;
import com.harvard.studyappmodule.custom.question.EmailQuestion;
import com.harvard.studyappmodule.custom.question.FormBodyCustom;
import com.harvard.studyappmodule.custom.question.HeightQuestion;
import com.harvard.studyappmodule.custom.question.IntegerUnitQuestionBody;
import com.harvard.studyappmodule.custom.question.LocationQuestion;
import com.harvard.studyappmodule.custom.question.MultiChoiceImageQuestionBody;
import com.harvard.studyappmodule.custom.question.MultiChoiceTextQuestionBody;
import com.harvard.studyappmodule.custom.question.ScaleQuestion;
import com.harvard.studyappmodule.custom.question.ScaleTextQuestion;
import com.harvard.studyappmodule.custom.question.SingleChoiceTextQuestionBody;
import com.harvard.studyappmodule.custom.question.TaskInstructionStep;
import com.harvard.studyappmodule.custom.question.TaskIntroductionStep;
import com.harvard.studyappmodule.custom.question.TextQuestionRegexBody;
import com.harvard.studyappmodule.custom.question.TimeIntervalQuestion;
import com.harvard.studyappmodule.custom.question.valuePickerQuestion;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.ui.step.body.NotImplementedStepBody;

import java.io.Serializable;

/**
 * Created by Naveen Raj on 10/26/2016.
 */
public class AnswerFormatCustom implements Serializable {
    /**
     * Default constructor. The appropriate subclass of AnswerFormat should be used instead of this
     * directly.
     */
    public AnswerFormatCustom() {
    }

    /**
     * Returns the QuestionType for this answer format. Implement this in your subclass.
     *
     * @return the question type
     */
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.None;
    }

    /**
     * Interface that {@link AnswerFormat.Type} implements. Since you cannot add a value to an existing enum, you
     * may implement this interface instead to provide your own QuestionType that provides a {@link
     * org.researchstack.backbone.ui.step.body.StepBody} class.
     */
    public interface QuestionType {
        Class<?> getStepBodyClass();
    }

    /**
     * The type of question. (read-only)
     * <p>
     * The type provides a default {@link org.researchstack.backbone.ui.step.body.StepBody} for that
     * type of question. A custom StepLayout implementation may provide it's own StepBody rather
     * than using the default provided by this AnswerFormat.
     */
    public enum Type implements QuestionType {
        None(NotImplementedStepBody.class),
        MultipleImageChoice(MultiChoiceImageQuestionBody.class),
        MultipleTextChoice(MultiChoiceTextQuestionBody.class),
        SingleTextChoice(SingleChoiceTextQuestionBody.class),
        Audio(AudioQuestionbody.class),
        stepcount(StepCountClass.class),
        Tapping(Tappingactivity.class),
        spatialSpanMemory(NotImplementedStepBody.class),
        valuePicker(valuePickerQuestion.class),
        Scale(ScaleQuestion.class),
        ScaleText(ScaleTextQuestion.class),
        Location(LocationQuestion.class),
        ContinousScale(ContinuousScaleQuestion.class),
        Height(HeightQuestion.class),
        TimeInterval(TimeIntervalQuestion.class),
        TextRegex(TextQuestionRegexBody.class),
        Email(EmailQuestion.class),
        TaskIntroStep(TaskIntroductionStep.class),
        TaskinstructionStep(TaskInstructionStep.class),
        Integer(IntegerUnitQuestionBody.class),
        Decimal(DecimalUnitQuestionBody.class),
        Date(CustomDateQuestionBody.class),
        DateAndTime(CustomDateQuestionBody.class),
        TimeOfDay(CustomDateQuestionBody.class),
        Form(FormBodyCustom.class);

        private Class<?> stepBodyClass;

        Type(Class<?> stepBodyClass) {
            this.stepBodyClass = stepBodyClass;
        }

        @Override
        public Class<?> getStepBodyClass() {
            return stepBodyClass;
        }

    }

    /**
     * The style of the question (that is, single or multiple choice).
     */
    public enum CustomAnswerStyle {
        MultipleImageChoice,
        MultipleTextChoice,
        SingleTextChoice,
        Audio,
        Tapping,
        stepcount,
        valuePicker,
        Scale,
        ScaleText,
        ContinousScale,
        TimeofDay,
        TimeInterval,
        Height,
        Location,
        spatialSpanMemory,
        TextRegex,
        Email,
        TaskIntroStep,
        TaskinstructionStep,
        Integer,
        Decimal,
        Form;
    }

    public enum DateAnswerStyle
    {
        DateAndTime,
        Date,
        TimeOfDay
    }

}
