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

package org.sagebase.crf.fitbit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.sagebase.crf.fitbit.model.ActivityDistanceResponse;
import org.sagebase.crf.fitbit.model.DistanceActivity;
import org.sagebase.crf.fitbit.model.HeartActivity;
import org.sagebase.crf.fitbit.model.HeartActivityResponse;
import org.sagebionetworks.research.crf.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FitbitManager.Callback {

  private static final String LOG_TAG = MainActivity.class.getCanonicalName();

  private FitbitManager mFitbit;

  private LinearLayout mChartContainer;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(LOG_TAG, "onCreate()");
    setContentView(R.layout.activity_fitbit);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    mFitbit = new FitbitManager(this, this);

    mChartContainer = (LinearLayout)findViewById(R.id.chart_container);

    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
    fab.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (mFitbit.isAuthenticated()) {
          //updateUser();
          queryFitbit();
        } else {
          mFitbit.authenticate();
        }
      }
    });

    if (mFitbit.handleAuthResponse(getIntent())) {
      //updateUser();
      queryFitbit();
    }

  }

  @Override
  protected void onResume() {
    super.onResume();
    Log.d(LOG_TAG, "onResume()");
  }

//  @Override
//  protected void onNewIntent(Intent intent) {
//    super.onNewIntent(intent);
//    // getIntent() should always return the most recent
//    setIntent(intent);
//
//    Log.d(LOG_TAG, "onNewIntent()");
//  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void updateUser() {
    TextView message = (TextView) findViewById(R.id.text_message);
    String user = mFitbit.getUserId();
    message.setText("Found access token for user: " + user);

  }

  private void queryFitbit() {
    mFitbit.fetchActivityData();
    mFitbit.fetchHeartRateData();
  }

  @Override
  public void onActivityResult(ActivityDistanceResponse response) {
    Log.d(LOG_TAG, "onActivityResult()");
    if(response != null) {
      Log.d(LOG_TAG, "Found distance entries: " + response.activitiesDistance.size());
      findViewById(R.id.fab).setVisibility(View.GONE);
      findViewById(R.id.instruction).setVisibility(View.GONE);

      BarChart bar = new BarChart(this);
      bar.setDescription("Acitivity Distance");

      ArrayList<String> xVals = new ArrayList<String>();
      ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
      int i = 0;
      for(DistanceActivity d: response.activitiesDistance) {
        yVals1.add(new BarEntry(d.value, i));
        xVals.add(new SimpleDateFormat("MM-dd").format(d.dateTime));
        i++;
      }

      BarDataSet set1 = new BarDataSet(yVals1, "Distance (miles)");
      ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
      dataSets.add(set1);
      BarData data = new BarData(xVals, dataSets);
      data.setValueTextSize(10f);
      //data.setBarWidth(0.9f);

      bar.setData(data);

      //IAxisValueFormatter xAxisFormatter = new CustomDayFormatter(values);


      YAxis leftAxis = bar.getAxisLeft();
      //leftAxis.setTypeface(mTfLight);
      leftAxis.setLabelCount(5, false);
      //leftAxis.setValueFormatter(xAxisFormatter);
      leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
      leftAxis.setSpaceTop(15f);
      leftAxis.setDrawGridLines(false);
      leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

      XAxis xAxis = bar.getXAxis();
      xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
      //xAxis.setTypeface(mTfLight);
      xAxis.setDrawGridLines(false);
      //xAxis.setGranularity(1f); // only intervals of 1 day
      //xAxis.setLabelCount(7);
      //xAxis.setValueFormatter(xAxisFormatter);

      bar.getAxisRight().setEnabled(false);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
      mChartContainer.addView(bar, params);

    }
  }

  @Override
  public void onHeartActivityResult(HeartActivityResponse response) {
    Log.d(LOG_TAG, "onHeartActivityResult()");
    if(response != null) {
      Log.d(LOG_TAG, "Found Heart results: " + response.activitiesHeart.size());
      findViewById(R.id.fab).setVisibility(View.GONE);
      findViewById(R.id.instruction).setVisibility(View.GONE);

      BarChart bar = new BarChart(this);
      bar.setDescription("Heart Rate");

      ArrayList<String> xVals = new ArrayList<String>();
      ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
      int i = 0;
      for(HeartActivity d: response.intraday.dataset) {
        yVals1.add(new BarEntry(Float.valueOf(d.value), i));
        xVals.add(String.valueOf(i));
        i++;
      }
      BarDataSet set1 = new BarDataSet(yVals1, "Heart Rate (bmp)");
      set1.setColor(Color.RED);
      ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
      dataSets.add(set1);
      BarData data = new BarData(xVals, dataSets);
      //data.setValueTextSize(10f);
      //data.setBarWidth(0.9f);

      bar.setData(data);

      //bar.getAxisRight().setEnabled(false);

      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600);
      mChartContainer.addView(bar, params);

    }
  }

}
