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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.studyappmodule.studymodel.Resource;
import com.harvard.studyappmodule.studymodel.StudyResource;
import com.harvard.utils.AppController;
import com.harvard.webservicemodule.apihelper.ApiCall;
import io.realm.Realm;
import io.realm.RealmList;

public class ResourcesFragment<T> extends Fragment implements ApiCall.OnAsyncRequestComplete {

  private RecyclerView studyRecyclerView;
  private Context context;
  private RealmList<Resource> resourceArrayList;
  private static final int RESOURCE_REQUEST_CODE = 213;
  DbServiceSubscriber dbServiceSubscriber;
  Realm realm;
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_resources, container, false);
    initializeXmlId(view);

    dbServiceSubscriber = new DbServiceSubscriber();
    realm = AppController.getRealmobj(getContext());

    resourceArrayList = new RealmList<>();
    Resource r = new Resource();
    r.setTitle(context.getResources().getString(R.string.app_glossary));
    r.setType("pdf");
    r.setContent("");
    resourceArrayList.add(r);

    r = new Resource();
    r.setTitle(context.getResources().getString(R.string.resourceTerms));
    r.setType("url");
    r.setContent(dbServiceSubscriber.getApps(realm).getTermsUrl());
    resourceArrayList.add(r);

    r = new Resource();
    r.setTitle(context.getResources().getString(R.string.resourcePolicy));
    r.setType("url");
    r.setContent(dbServiceSubscriber.getApps(realm).getPrivacyPolicyUrl());
    resourceArrayList.add(r);

    dbServiceSubscriber.closeRealmObj(realm);

    setResourceAdapter();
    /// till here/////
    return view;
  }

  private void initializeXmlId(View view) {
    studyRecyclerView = (RecyclerView) view.findViewById(R.id.studyRecyclerView);
  }

  @Override
  public <T> void asyncResponse(T response, int responseCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (responseCode == RESOURCE_REQUEST_CODE) {
      if (response != null) {
        StudyResource studyResource = (StudyResource) response;
        resourceArrayList = studyResource.getResources();
        if (resourceArrayList != null && resourceArrayList.size() != 0) {
          setResourceAdapter();
        }
      }
    }
  }

  private void setResourceAdapter() {
    studyRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    GatewayResourcesListAdapter gatewayResourcesListAdapter =
        new GatewayResourcesListAdapter(getActivity(), resourceArrayList);
    studyRecyclerView.setAdapter(gatewayResourcesListAdapter);
  }

  @Override
  public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    AppController.getHelperProgressDialog().dismissDialog();
    if (statusCode.equalsIgnoreCase("401")) {
      Toast.makeText(context, errormsg, Toast.LENGTH_SHORT).show();
      AppController.getHelperSessionExpired(context, errormsg);
    } else if (responseCode == RESOURCE_REQUEST_CODE) {
      Toast.makeText(getContext(), errormsg, Toast.LENGTH_SHORT).show();
    }
  }
}
