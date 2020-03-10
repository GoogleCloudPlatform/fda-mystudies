package com.harvard.studyappmodule.custom.question;

import com.harvard.studyappmodule.custom.AnswerFormatCustom;
import com.harvard.studyappmodule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 04/20/2017.
 */

public class MultiChoiceImageAnswerFormat<T> extends ChoiceAnswerFormatCustom {

    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    private ChoiceCustomImage<T>[] choicechoices;

    public MultiChoiceImageAnswerFormat(CustomAnswerStyle style, ChoiceCustomImage<T>[] choicechoices) {
        this.style = style;
        this.choicechoices = choicechoices;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    public ChoiceCustomImage<T>[] getChoicechoices() {
        return choicechoices;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.MultipleImageChoice;
    }
}