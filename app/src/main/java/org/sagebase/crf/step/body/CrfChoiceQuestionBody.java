/*
 *    Copyright 2017 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.sagebase.crf.step.body;

import android.content.res.Resources;
import android.support.annotation.ColorRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;
import org.sagebionetworks.research.crf.R;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by rianhouston on 11/27/17.
 */

public class CrfChoiceQuestionBody<T> extends SingleChoiceQuestionBody {

    protected Set<Object> currentSelectedMultipleSet;
    protected List<WeakReference<View>> bodyViewList;

    public CrfChoiceQuestionBody(Step step, StepResult result) {
        super(step, result);

        bodyViewList = new ArrayList<>();

        if (isMutlipleChoice()) {
            // Restore results
            currentSelectedMultipleSet = new HashSet<>();

            if (result != null && result.getResult() != null &&
                    (result.getResult() instanceof Object[])) {
                Object[] resultArray = (Object[])result.getResult();
                if (resultArray != null && resultArray.length > 0) {
                    currentSelectedMultipleSet.addAll(Arrays.asList(resultArray));
                }
            }
        }
    }

    protected boolean isMutlipleChoice() {
        if (format != null && format.getAnswerStyle() == AnswerFormat.ChoiceAnswerStyle.MultipleChoice) {
            return true;
        }
        return false;
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {

        Resources res = parent.getResources();
        LinearLayout view = new LinearLayout(parent.getContext());
        view.setOrientation(LinearLayout.VERTICAL);

        for (int i = 0; i < choices.length; i++) {
            Choice choice = choices[i];
            View v = inflater.inflate(R.layout.crf_single_choice_body, view,false);
            TextView tv = v.findViewById(R.id.single_choice_text);
            tv.setText(choice.getText());
            v.setId(i);

            boolean isSelected = isValueSelected(choice.getValue());
            v.setBackgroundResource(isSelected ? selectedBackground() : unselectedBackground());

            v.setOnClickListener(selectedView -> {
                Choice<T> c = choices[selectedView.getId()];
                Object selectedValue = c.getValue();
                boolean wasViewSelected = isValueSelected(c.getValue());
                if (isMutlipleChoice()) {
                    if (wasViewSelected) {
                        selectedView.setBackgroundResource(unselectedBackground());
                        currentSelectedMultipleSet.remove(selectedValue);
                    } else {
                        selectedView.setBackgroundResource(selectedBackground());
                        currentSelectedMultipleSet.add(selectedValue);
                    }
                } else {
                    currentSelected = selectedValue;
                    resetViewSelection();
                    selectedView.setBackgroundResource(selectedBackground());
                }
            });

            if(i != 0) {
                View space = new View(parent.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    res.getDimensionPixelSize(R.dimen.crf_single_choice_question_margin));
                space.setLayoutParams(lp);
                view.addView(space);
            }

            bodyViewList.add(new WeakReference<>(v));

            view.addView(v);
        }

        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setPadding(0, res.getDimensionPixelSize(R.dimen.crf_single_choice_question_margin), 0,
            res.getDimensionPixelSize(R.dimen.crf_single_choice_question_margin));

        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(res.getColor(viewBackground()));

        return view;
    }

    protected boolean isValueSelected(Object value) {
        boolean isSelected = false;
        if (isMutlipleChoice()) {
            isSelected = currentSelectedMultipleSet != null &&
                    currentSelectedMultipleSet.contains(value);
        } else {
            isSelected = currentSelected != null && currentSelected.equals(value);
        }
        return isSelected;
    }

    protected void resetViewSelection() {
        for (WeakReference<View> viewWeakReference: bodyViewList) {
            if (viewWeakReference != null && viewWeakReference.get() != null) {
                viewWeakReference.get().setBackgroundResource(unselectedBackground());
            }
        }
    }

    protected @ColorRes int viewBackground() {
        return R.color.whiteTwo;
    }

    protected @ColorRes int selectedBackground() {
        return R.color.whiteFour;
    }

    protected @ColorRes int unselectedBackground() {
        return R.color.whiteThree;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (isMutlipleChoice()) {
            if (skipped) {
                currentSelectedMultipleSet.clear();
                result.setResult((T[]) currentSelectedMultipleSet.toArray());
            } else {
                result.setResult((T[]) currentSelectedMultipleSet.toArray());
            }
            return result;
        }
        return super.getStepResult(skipped);
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        if (isMutlipleChoice()) {
            if (currentSelectedMultipleSet.isEmpty()) {
                return new BodyAnswer(false, org.researchstack.backbone.R.string.rsb_invalid_answer_choice);
            } else {
                return BodyAnswer.VALID;
            }
        }
        return super.getBodyAnswerState();
    }
}
