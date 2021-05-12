/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Copyright 2020 Google LLC
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.studyappmodule.custom.question;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.harvard.R;
import com.harvard.studyappmodule.custom.ChoiceAnswerFormatCustom;
import com.harvard.studyappmodule.custom.QuestionStepCustom;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

public class ValuePickerQuestion<T> implements StepBody {
  private QuestionStepCustom step;
  private StepResult<String> result;
  private Choice<T>[] choices;
  private String currentSelected;
  private TextView textView;
  private String resultValue;

  public ValuePickerQuestion(Step step, StepResult result) {
    if (step instanceof QuestionStepCustom) {
      this.step = (QuestionStepCustom) step;
      this.result = result == null ? new StepResult<>(step) : result;
      ChoiceAnswerFormatCustom format = (ChoiceAnswerFormatCustom) this.step.getAnswerFormat1();
      this.choices = format.getChoices();
    } else {
      this.result = result == null ? new StepResult<>(step) : result;
      ChoiceAnswerFormat format2 = (ChoiceAnswerFormat) this.step.getAnswerFormat();
      this.choices = format2.getChoices();
    }
    // Restore results
    String resultValue = this.result.getResult();
    if (resultValue != null) {
      currentSelected = resultValue;
    }
  }

  @Override
  public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
    View view = getViewForType(viewType, inflater, parent);

    Resources res = parent.getResources();
    LinearLayout.MarginLayoutParams layoutParams =
        new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    layoutParams.leftMargin =
        res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_left);
    layoutParams.rightMargin =
        res.getDimensionPixelSize(org.researchstack.backbone.R.dimen.rsb_margin_right);
    view.setLayoutParams(layoutParams);

    return view;
  }

  private View getViewForType(int viewType, LayoutInflater inflater, ViewGroup parent) {
    if (viewType == VIEW_TYPE_DEFAULT) {
      return initViewDefault(inflater, parent);
    } else if (viewType == VIEW_TYPE_COMPACT) {
      return initViewCompact(inflater, parent);
    } else {
      throw new IllegalArgumentException("Invalid View Type");
    }
  }

  private View initViewDefault(final LayoutInflater inflater, ViewGroup parent) {
    final View body = inflater.inflate(R.layout.valuepicker, parent, false);

    textView = body.findViewById(R.id.textview);
    for (int i = 0; i < choices.length; i++) {
      if (currentSelected != null
          && currentSelected.equalsIgnoreCase(choices[i].getValue().toString())) {
        textView.setText((String) choices[i].getText());
        resultValue = choices[i].getValue().toString();
      }
    }
    textView.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            showDialog(inflater.getContext());
          }
        });

    return body;
  }

  private void showDialog(Context context) {

    final Dialog dialog = new Dialog(context);
    dialog.setCancelable(false);
    dialog.setContentView(R.layout.dialog_listview);

    TextView btndialog = (TextView) dialog.findViewById(R.id.btndialog);
    btndialog.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            dialog.dismiss();
          }
        });
    final String[] numberpickervalue = new String[choices.length];

    for (int i = 0; i < choices.length; i++) {
      numberpickervalue[i] = choices[i].getText();
    }

    ListView listView = (ListView) dialog.findViewById(R.id.listview);
    ArrayAdapter arrayAdapter =
        new ArrayAdapter(context, R.layout.list_item, R.id.tv, numberpickervalue);
    listView.setAdapter(arrayAdapter);

    listView.setOnItemClickListener(
        new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            textView.setText(numberpickervalue[position]);
            resultValue = choices[position].getValue().toString();

            dialog.dismiss();
          }
        });

    dialog.show();
  }

  private View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
    ViewGroup compactView = (ViewGroup) initViewDefault(inflater, parent);

    TextView label =
        (TextView)
            inflater.inflate(
                org.researchstack.backbone.R.layout.rsb_item_text_view_title_compact,
                compactView,
                false);
    label.setText(step.getTitle());

    compactView.addView(label, 0);

    return compactView;
  }

  @Override
  public StepResult getStepResult(boolean skipped) {
    if (skipped) {
      currentSelected = null;
      result.setResult(null);
    } else {
      result.setResult(resultValue);
    }
    return result;
  }

  @Override
  public BodyAnswer getBodyAnswerState() {
    return BodyAnswer.VALID;
  }
}