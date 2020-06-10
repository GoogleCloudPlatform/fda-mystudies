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

package com.harvard.utils;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.harvard.R;
import java.util.List;

public class TempGraphHelper {
  public static LineChart updateLineChart(
      LineChart chart, int max, List<Entry> entries, List<String> valuesX, String barColor) {
    Resources res = chart.getContext().getResources();

    chart.setDrawBorders(false);
    XAxis axisX = chart.getXAxis();
    axisX.setPosition(XAxis.XAxisPosition.BOTTOM);
    axisX.setDrawAxisLine(true);
    axisX.setYOffset(32f);
    axisX.setDrawGridLines(false);
    axisX.setLabelsToSkip(0);
    axisX.setTextSize(14);
    axisX.setTextColor(R.color.black_shade);
    axisX.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

    YAxis axisY = chart.getAxisLeft();
    axisY.setDrawAxisLine(false);
    axisY.setDrawGridLines(true);
    axisY.setDrawZeroLine(true);
    axisY.setShowOnlyMinMax(false);
    axisY.setTextSize(14);
    axisY.setTextColor(R.color.black_shade);
    axisY.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

    chart.getAxisRight().setEnabled(false);
    chart.getLegend().setEnabled(false);
    chart.setDescription("");

    LineDataSet set = new LineDataSet(entries, "");
    set.setCircleColor(Color.parseColor(barColor));
    set.setCircleRadius(4f);
    set.setDrawCircleHole(false);
    set.setColor(Color.parseColor(barColor));
    set.setLineWidth(2f);
    set.setDrawValues(true);
    set.setDrawFilled(true);
    set.setFillColor(Color.parseColor(barColor));
    set.setFillAlpha(50);

    LineData data = new LineData(valuesX, set);
    if (entries.size() > 0) {
      chart.setData(data);
    }
    chart.fitScreen();
    chart.setNoDataText("No Data");
    chart.invalidate();

    return chart;
  }
}
