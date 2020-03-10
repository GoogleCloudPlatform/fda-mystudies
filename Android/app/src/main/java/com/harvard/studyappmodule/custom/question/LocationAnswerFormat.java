package com.harvard.studyappmodule.custom.question;

import com.harvard.studyappmodule.custom.AnswerFormatCustom;
import com.harvard.studyappmodule.custom.ChoiceAnswerFormatCustom;

/**
 * Created by Naveen Raj on 04/26/2017.
 */

public class LocationAnswerFormat extends ChoiceAnswerFormatCustom {

    private final ChoiceAnswerFormatCustom.CustomAnswerStyle style;
    boolean useCurrentLocation;


    public LocationAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle style, boolean useCurrentLocation) {
        this.style = style;
        this.useCurrentLocation = useCurrentLocation;
    }

    public ChoiceAnswerFormatCustom.CustomAnswerStyle getStyle() {
        return style;
    }

    public boolean isUseCurrentLocation() {
        return useCurrentLocation;
    }

    @Override
    public AnswerFormatCustom.QuestionType getQuestionType() {
        return Type.Location;
    }
}
