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

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.harvard.R;
import com.harvard.utils.NetworkChangeReceiver;
import java.util.ArrayList;

public class ReachoutFragment<T> extends Fragment
    implements NetworkChangeReceiver.NetworkChangeCallback {

  private RecyclerView reachoutRecyclerView;
  private Context context;
  private NetworkChangeReceiver networkChangeReceiver;
  private TextView offlineIndicatior;

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_reachout, container, false);
    networkChangeReceiver = new NetworkChangeReceiver(this);
    initializeXmlId(view);
    setRecyclearView();
    return view;
  }

  private void initializeXmlId(View view) {
    reachoutRecyclerView = (RecyclerView) view.findViewById(R.id.reachoutRecyclerView);
    offlineIndicatior = view.findViewById(R.id.offlineIndicatior);
  }

  private void setRecyclearView() {
    reachoutRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    reachoutRecyclerView.setNestedScrollingEnabled(false);
    ArrayList<String> reachoutList = new ArrayList<>();
    reachoutList.add(getString(R.string.anonymous_feedback));
    reachoutList.add(getString(R.string.need_help));
    ReachoutListAdapter reachoutListAdapter = new ReachoutListAdapter(getActivity(), reachoutList);
    reachoutRecyclerView.setAdapter(reachoutListAdapter);
  }

  @Override
  public void onNetworkChanged(boolean status) {
    if (!status) {
      offlineIndicatior.setVisibility(View.VISIBLE);
    } else {
      offlineIndicatior.setVisibility(View.GONE);
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    context.registerReceiver(networkChangeReceiver, intentFilter);
  }

  @Override
  public void onPause() {
    super.onPause();
    if (networkChangeReceiver != null) {
      context.unregisterReceiver(networkChangeReceiver);
    }
  }
}
