package com.harvard.studyappmodule.custom.activetask;

import com.harvard.studyappmodule.custom.AnswerFormatCustom;
import com.harvard.studyappmodule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 04/12/2017.
 */

public class TappingAnswerFormat extends ChoiceAnswerFormatCustom {

    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    private final int duration;
    private int mKickCount;


    public TappingAnswerFormat(CustomAnswerStyle style, int duration, int kickCount) {
        this.style = style;
        this.duration = duration;
        mKickCount = kickCount;
    }

    public int getKickCount() {
        return mKickCount;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    int getDuration() {
        return duration;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.Tapping;
    }
}
