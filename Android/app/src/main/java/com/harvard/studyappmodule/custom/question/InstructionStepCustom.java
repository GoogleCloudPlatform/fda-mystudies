package com.harvard.studyappmodule.custom.question;

import org.researchstack.backbone.step.Step;

public class InstructionStepCustom extends Step
{
    public InstructionStepCustom(String identifier, String title, String detailText)
    {
        super(identifier, title);
        setText(detailText);
        setOptional(false);
    }

    @Override
    public Class getStepLayoutClass()
    {
        return InstructionStepLayoutCustom.class;
    }
}
