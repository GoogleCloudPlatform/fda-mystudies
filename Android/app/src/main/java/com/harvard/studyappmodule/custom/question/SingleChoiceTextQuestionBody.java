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

import android.app.Activity;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import com.harvard.R;
import com.harvard.studyappmodule.custom.QuestionStepCustom;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

public class SingleChoiceTextQuestionBody<T>
    implements StepBody, CompoundButton.OnCheckedChangeListener {
  private QuestionStepCustom step;
  private StepResult<T[]> result;
  private ChoiceText<T>[] choices;
  private Set<T> currentSelected;

  private EditText otherText;
  private OtherOptionModel otherOptionModel;
  private String otherOptionValue = "";
  private boolean otherOptionMandatory = false;
  private boolean otherOptionText = false;

  public SingleChoiceTextQuestionBody(Step step, StepResult result) {
    this.step = (QuestionStepCustom) step;
    this.result = result == null ? new StepResult<>(step) : result;
    SingleChoiceTextAnswerFormat format = (SingleChoiceTextAnswerFormat) this.step.getAnswerFormat1();
    this.choices = format.getTextChoices();
    otherOptionModel = new OtherOptionModel();

    // Restore results
    currentSelected = new LinkedHashSet<>();

    for (int i = 0; i < choices.length; i++) {
      if (choices[i].getOther() != null) {
        ChoiceTextOtherOption choiceTextOtherOption = new ChoiceTextOtherOption();
        choiceTextOtherOption.setPlaceholder(choices[i].getOther().getPlaceholder());
        choiceTextOtherOption.setMandatory(choices[i].getOther().isMandatory());
        choiceTextOtherOption.setTextfieldReq(choices[i].getOther().isTextfieldReq());
        choices[i].setOther(choiceTextOtherOption);
      }
    }

    ArrayList list = null;
    T[] resultArray = this.result.getResult();
    if (resultArray != null && resultArray.length > 0) {
      currentSelected.addAll(Arrays.asList(resultArray));

      list = new ArrayList(Arrays.asList(resultArray));
      for (int i = 0; i < list.size(); i++) {
        if (list.get(i) != null) {
          try {
            JSONObject jsonObject = new JSONObject(list.get(i).toString());

            otherOptionModel.setOther(jsonObject.getString("other"));
            try {
              otherOptionModel.setText(jsonObject.getString("text"));
            } catch (JSONException e) {
              Logger.log(e);
            }
            currentSelected.remove(new Gson().toJson(otherOptionModel));
          } catch (JSONException e) {
            Logger.log(e);
          }
        }
      }
    }

    for (int i = 0; i < choices.length; i++) {
      if (choices[i].getOther() != null) {
        otherOptionValue = choices[i].getValue().toString();
        otherOptionMandatory = choices[i].getOther().isMandatory();
      }
    }
  }

  @Override
  public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
    View view = getViewForType(viewType, inflater, parent);

    Resources res = parent.getResources();
    LinearLayout.MarginLayoutParams layoutParams =
        new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left);
    layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right);
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

  private View initViewDefault(final LayoutInflater inflater, final ViewGroup parent) {
    final ArrayList<AppCompatCheckBox> selectedcheckbox = new ArrayList<>();
    final ArrayList<CompoundButton.OnCheckedChangeListener> checkedChangeListenerArrayList =
        new ArrayList<>();
    final RadioGroup radioGroup = new RadioGroup(inflater.getContext());
    radioGroup.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    radioGroup.setDividerDrawable(
        ContextCompat.getDrawable(parent.getContext(), R.drawable.rsb_divider_empty_8dp));

    if (choices.length >= 10) {
      SearchView editText = new SearchView(inflater.getContext());
      editText.setIconifiedByDefault(false);
      editText.setIconified(false);
      editText.clearFocus();
      LinearLayout.MarginLayoutParams layoutParams =
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      editText.setLayoutParams(layoutParams);
      editText.setOnQueryTextListener(
          new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
              return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
              for (int i = 0; i < choices.length; i++) {
                if (!choices[i].getValue().toString().equalsIgnoreCase(otherOptionValue)) {
                  if (choices[i].getText().toLowerCase().contains(s.toLowerCase())) {
                    radioGroup.findViewWithTag(i).setVisibility(View.VISIBLE);
                  } else {
                    radioGroup.findViewWithTag(i).setVisibility(View.GONE);
                  }
                }
              }
              return false;
            }
          });

      radioGroup.addView(editText);
    }

    otherText = new EditText(inflater.getContext());
    for (int i = 0; i < choices.length; i++) {
      final ChoiceText<T> item = choices[i];

      // Create & add the View to our body-view

      LinearLayout linearLayout =
          (LinearLayout) inflater.inflate(R.layout.checkboxdesc, radioGroup, false);
      final AppCompatCheckBox checkBox =
          (AppCompatCheckBox) linearLayout.findViewById(R.id.checkbox);
      final TextView descTxt = (TextView) linearLayout.findViewById(R.id.desc);
      checkBox.setText(item.getText());
      descTxt.setText(item.getDetail());
      checkBox.setId(i);

      if (item.getOther() != null && item.getOther().isTextfieldReq()) {
        LinearLayout.MarginLayoutParams otherTextlayoutParams =
            new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        otherText.setLayoutParams(otherTextlayoutParams);
        otherText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        otherText.setSingleLine(true);
        linearLayout.addView(otherText);
        otherText.setHint(item.getOther().getPlaceholder());
        otherText.setVisibility(View.GONE);
        otherOptionText = true;
      }
      linearLayout.setTag(i);
      radioGroup.addView(linearLayout);

      // Set initial state
      if (currentSelected != null && currentSelected.contains(item.getValue())) {
        checkBox.setChecked(true);
        selectedcheckbox.add(checkBox);

        if (item.getValue().toString().equalsIgnoreCase(otherOptionValue)) {
          otherText.setVisibility(View.VISIBLE);
          otherText.setText(otherOptionModel.getText());
        }
      }

      // Update result when value changes
      CompoundButton.OnCheckedChangeListener onCheckedChangeListener =
          new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if (isChecked) {
                AppController.getHelperHideKeyboard((Activity) inflater.getContext());
                for (int i = 0; i < selectedcheckbox.size(); i++) {
                  selectedcheckbox.get(i).setOnCheckedChangeListener(null);
                  selectedcheckbox.get(i).setChecked(false);
                  selectedcheckbox
                      .get(i)
                      .setOnCheckedChangeListener(
                          checkedChangeListenerArrayList.get(selectedcheckbox.get(i).getId()));
                }
                otherText.setVisibility(View.GONE);
                otherText.setText("");
                selectedcheckbox.clear();
                currentSelected.clear();
                currentSelected.remove(new Gson().toJson(otherOptionModel));
                otherOptionModel.setOther(null);
                otherOptionModel.setText(null);

                selectedcheckbox.add(checkBox);
                currentSelected.add(item.getValue());

                if (item.getOther() != null) {
                  otherText.setVisibility(View.VISIBLE);
                  otherText.requestFocus();

                  otherOptionModel.setOther(item.getText().toString());
                }
              } else {
                selectedcheckbox.remove(checkBox);
                currentSelected.remove(item.getValue());
                if (item.getOther() != null) {
                  AppController.getHelperHideKeyboard((Activity) inflater.getContext());
                  currentSelected.remove(new Gson().toJson(otherOptionModel));
                  otherOptionModel.setOther(null);
                  otherOptionModel.setText(null);
                  otherText.setVisibility(View.GONE);
                  otherText.setText("");
                }
              }
            }
          };
      checkedChangeListenerArrayList.add(onCheckedChangeListener);
      checkBox.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    if (otherOptionModel != null && otherOptionModel.getText() != null) {
      otherText.setText(otherOptionModel.getText());
      otherText.setVisibility(View.VISIBLE);
    }

    return radioGroup;
  }

  private View initViewCompact(LayoutInflater inflater, ViewGroup parent) {
    ViewGroup compactView = (ViewGroup) initViewDefault(inflater, parent);

    TextView label =
        (TextView) inflater.inflate(R.layout.rsb_item_text_view_title_compact, compactView, false);
    label.setText(step.getTitle());

    compactView.addView(label, 0);

    return compactView;
  }

  @Override
  public StepResult getStepResult(boolean skipped) {
    if (skipped) {
      currentSelected.clear();
      result.setResult((T[]) currentSelected.toArray());
    } else {
      if (otherOptionModel != null) {
        if (otherOptionText) {
          if (currentSelected.contains(otherOptionValue)) {
            otherOptionModel.setText(otherText.getText().toString());
            currentSelected.add((T) new Gson().toJson(otherOptionModel));
          } else {
            otherOptionModel.setText(otherText.getText().toString());
            currentSelected.remove((T) new Gson().toJson(otherOptionModel));
          }
        } else if (otherOptionModel.getOther() != null) {
          currentSelected.add((T) new Gson().toJson(otherOptionModel));
        }
      }

      result.setResult((T[]) currentSelected.toArray());
    }
    return result;
  }

  @Override
  public BodyAnswer getBodyAnswerState() {
    if (currentSelected.isEmpty()) {
      return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
    } else if (otherOptionMandatory
        && currentSelected.contains(otherOptionValue)
        && otherText.getText().toString().equalsIgnoreCase("")) {
      return new BodyAnswer(false, R.string.otherValuetxt);
    } else {
      return BodyAnswer.VALID;
    }
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {}
}
