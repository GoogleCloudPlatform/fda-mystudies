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

package com.harvard.studyappmodule;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import androidx.appcompat.widget.AppCompatTextView;
import com.harvard.R;
import com.harvard.utils.CustomFirebaseAnalytics;

public class CustomDialogClass<V> extends Dialog implements View.OnClickListener {

  private NumberPicker hourPicker = null;
  private NumberPicker minPicker = null;
  private AppCompatTextView doneBtn;
  private final ProfileFragment profileFragment;
  private final String[] mins15 = {"00", "15", "30", "45"};
  private CustomFirebaseAnalytics analyticsInstance;
  CustomDialogClass(Activity a, ProfileFragment profileFragment) {
    super(a);
    this.profileFragment = profileFragment;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_cutom_dialog);
    hourPicker = (NumberPicker) findViewById(R.id.picker_hour);
    minPicker = (NumberPicker) findViewById(R.id.picker_min);
    doneBtn = (AppCompatTextView) findViewById(R.id.doneBtn);
    doneBtn.setOnClickListener(this);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(getContext());
    // if hr=24 then setvalue 1 means min arrays 0'th pos value
    minPicker.setOnValueChangedListener(
        new NumberPicker.OnValueChangeListener() {
          @Override
          public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            if (hourPicker.getValue() == 0 && minPicker.getValue() == 1) {
              minPicker.setValue(2);
            }
          }
        });
    hourPicker.setOnValueChangedListener(
        new NumberPicker.OnValueChangeListener() {
          @Override
          public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            if (hourPicker.getValue() == 0 && minPicker.getValue() == 1) {
              minPicker.setValue(2);
            }
          }
        });

    hourPicker.setMinValue(0);
    hourPicker.setMaxValue(23);
    hourPicker.setValue(0);
    minPicker.setValue(1);
    setMinsPicker();
    hourPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
    minPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
  }

  private void setMinsPicker() {
    int ml = mins15.length;
    // Prevent ArrayOutOfBoundExceptions by setting
    // values array to null so its not checked
    minPicker.setDisplayedValues(null);
    // 1 means value is '0'
    minPicker.setMinValue(1);
    minPicker.setMaxValue(ml);
    minPicker.setDisplayedValues(mins15);
  }

  @Override
  public void onClick(View v) {
    Bundle eventProperties = new Bundle();
    eventProperties.putString(
        CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
        getContext().getString(R.string.custom_dialog_done));
    analyticsInstance.logEvent(CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
    String selectedValue = hourPicker.getValue() + ":" + mins15[minPicker.getValue() - 1];
    profileFragment.updatePickerTime(selectedValue);
    dismiss();
  }
}
