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

import android.content.res.Resources;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.harvard.R;
import com.harvard.studyappmodule.custom.QuestionStepCustom;
import com.harvard.utils.Logger;
import com.harvard.utils.VerticalSeekBar;
import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;
import com.jaygoo.widget.VerticalRangeSeekBar;
import java.text.NumberFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.body.BodyAnswer;
import org.researchstack.backbone.ui.step.body.StepBody;

public class ContinuousScaleQuestion<T> implements StepBody {
  private QuestionStepCustom step;
  private StepResult<Double> result;
  private ContinousScaleAnswerFormat format;
  private TextView currentvalue;
  private Double currentSelected;
  private RangeSeekBar seekBar;
  private int stepSection;
  private int min;
  private double value;

  public ContinuousScaleQuestion(Step step, StepResult result) {
    this.step = (QuestionStepCustom) step;
    this.result = result == null ? new StepResult<>(step) : result;
    this.format = (ContinousScaleAnswerFormat) this.step.getAnswerFormat1();

    Double resultValue = this.result.getResult();
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

  private View initViewDefault(LayoutInflater inflater, ViewGroup parent) {
    LinearLayout linearLayout = new LinearLayout(inflater.getContext());
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    linearLayout.setLayoutParams(
        new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

    final int max = format.getMaxValue();
    min = format.getMinValue();
    stepSection = format.getMaxfraction();

    View seekbarlayout;

    if (!format.isVertical()) {
      seekbarlayout = inflater.inflate(R.layout.seekbar_horizontal_layout, parent, false);
      seekBar = (RangeSeekBar) seekbarlayout.findViewById(R.id.seekbar);
    } else {
      seekbarlayout = inflater.inflate(R.layout.seekbar_vertical_layout, parent, false);
      seekBar = (VerticalRangeSeekBar) seekbarlayout.findViewById(R.id.seekbar);
    }
    if (stepSection != 0) {
      seekBar.setRange(0, (max - min) * (stepSection * 10));
    } else {
      seekBar.setRange(0, (max - min));
    }

    TextView mindesc = (TextView) seekbarlayout.findViewById(R.id.mindesc);
    ImageView minimage = (ImageView) seekbarlayout.findViewById(R.id.minimage);
    TextView maxdesc = (TextView) seekbarlayout.findViewById(R.id.maxdesc);
    ImageView maximage = (ImageView) seekbarlayout.findViewById(R.id.maximage);

    currentvalue = (TextView) seekbarlayout.findViewById(R.id.currentvalue);

    mindesc.setText(format.getMinDesc());
    maxdesc.setText(format.getMaxDesc());

    TextView mintitle = (TextView) seekbarlayout.findViewById(R.id.mintitle);
    TextView maxtitle = (TextView) seekbarlayout.findViewById(R.id.maxtitle);
    mintitle.setText(String.valueOf(min));
    maxtitle.setText(String.valueOf(max));

    if (!format.getMinImage().equalsIgnoreCase("")) {
      byte[] imageByteArray = Base64.decode(format.getMinImage().split(",")[1], Base64.DEFAULT);
      Glide.with(inflater.getContext()).load(imageByteArray).into(minimage);
    } else {
      minimage.setVisibility(View.GONE);
    }
    if (!format.getMaxImage().equalsIgnoreCase("")) {
      byte[] imageByteArray = Base64.decode(format.getMaxImage().split(",")[1], Base64.DEFAULT);
      Glide.with(inflater.getContext()).load(imageByteArray).into(maximage);
    } else {
      maximage.setVisibility(View.GONE);
    }

    currentvalue.setText(String.valueOf(min));

    seekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
      @Override
      public void onRangeChanged(RangeSeekBar rangeSeekBar, float v, float v1, boolean b) {
        setvaluetotext();
      }

      @Override
      public void onStartTrackingTouch(RangeSeekBar rangeSeekBar, boolean b) {

      }

      @Override
      public void onStopTrackingTouch(RangeSeekBar rangeSeekBar, boolean b) {

      }
    });

    if (currentSelected != null) {
      double selected;
      if (stepSection != 0) {
        selected =
            ((currentSelected - Double.parseDouble("" + min))
                * Double.parseDouble("" + stepSection * 10));
      } else {
        selected = ((currentSelected - Double.parseDouble("" + min)));
      }

      seekBar.setProgress((int) selected);
    } else {
      double defaultval = 0;
      if (format.getDefaultval() != null && !format.getDefaultval().equalsIgnoreCase("")) {
        try {
          defaultval = Double.parseDouble(format.getDefaultval());
        } catch (NumberFormatException e) {
          Logger.log(e);
          defaultval = 0;
        }
      } else {
        defaultval = 0;
      }
      if (stepSection != 0) {
        seekBar.setProgress((int) ((defaultval - min) * (stepSection * 10)));
      } else {
        seekBar.setProgress((int) ((defaultval - min)));
      }
    }
    if (format.isVertical()) {
      setvaluetotext();
    }

    linearLayout.removeAllViewsInLayout();
    linearLayout.addView(seekbarlayout);
    return linearLayout;
  }

  private void setvaluetotext() {

    if (stepSection != 0) {
      value =
          Double.parseDouble("" + min)
              + Double.parseDouble("" + (int) seekBar.getLeftSeekBar().getProgress())
              / Double.parseDouble("" + (stepSection * 10));
    } else {
      value = Double.parseDouble("" + min)
          + Double.parseDouble("" + (int) seekBar.getLeftSeekBar().getProgress());
    }
    NumberFormat nf = NumberFormat.getInstance();
    nf.setMaximumFractionDigits(stepSection);

    currentvalue.setText(String.valueOf(nf.format(value)));
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
      result.setResult(null);
    } else {
      result.setResult(value);
    }
    return result;
  }

  @Override
  public BodyAnswer getBodyAnswerState() {
    return BodyAnswer.VALID;
  }
}
