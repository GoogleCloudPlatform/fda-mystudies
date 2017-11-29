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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.SingleChoiceQuestionBody;
import org.sagebionetworks.research.crf.R;

/**
 * Created by rianhouston on 11/27/17.
 */

public class CrfSingleChoiceQuestionBody<T> extends SingleChoiceQuestionBody {
    public CrfSingleChoiceQuestionBody(Step step, StepResult result) {
        super(step, result);
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

            if (currentSelected != null) {
                tv.setEnabled(currentSelected.equals(choice.getValue()));
            }

            v.setOnClickListener(view1 -> {
                Choice<T> c = choices[view1.getId()];
                currentSelected = c.getValue();
                resetBackgroundColors((ViewGroup)view1.getParent());
                view1.setBackgroundColor(res.getColor(R.color.whiteFour));
            });

            if(i != 0) {
                View space = new View(parent.getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    res.getDimensionPixelSize(R.dimen.crf_single_choice_question_margin));
                space.setLayoutParams(lp);
                view.addView(space);
            }

             view.addView(v);

        }

        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setPadding(0, res.getDimensionPixelSize(R.dimen.crf_single_choice_question_margin), 0,
            res.getDimensionPixelSize(R.dimen.crf_single_choice_question_margin));

        view.setLayoutParams(layoutParams);
        view.setBackgroundColor(res.getColor(R.color.whiteFour));

        return view;
    }

    private void resetBackgroundColors(ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for(int i = 0; i < childCount; i++) {
            View v = viewGroup.getChildAt(i);
            v.setBackgroundColor(viewGroup.getResources().getColor(R.color.whiteThree));
        }
    }
}
