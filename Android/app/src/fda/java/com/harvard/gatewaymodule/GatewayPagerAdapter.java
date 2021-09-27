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

package com.harvard.gatewaymodule;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.harvard.R;
import com.harvard.storagemodule.DbServiceSubscriber;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.Realm;

public class GatewayPagerAdapter extends PagerAdapter {

  private int size;
  private AppCompatImageView bgImg;
  private AppCompatTextView website;
  private AppCompatTextView welcome;
  private AppCompatTextView desc;
  private AppCompatTextView watchVideoLabel;
  private Context context;

  public GatewayPagerAdapter() {
    size = 2;
  }

  public GatewayPagerAdapter(int count) {
    size = count;
  }

  @Override
  public int getCount() {
    return size;
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override
  public void destroyItem(ViewGroup view, int position, Object object) {
    view.removeView((View) object);
  }

  @Override
  public Object instantiateItem(ViewGroup collection, int position) {
    final LayoutInflater inflater =
        (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    context = inflater.getContext();
    switch (position) {
      case 0:
        View view = inflater.inflate(R.layout.gateway_item1, null);
        initializeXmlId(0, view);
        setFont(0, view);
        collection.addView(view);
        return view;
      case 1:
        View view1 = inflater.inflate(R.layout.gateway_item2, null);
        initializeXmlId(1, view1);
        welcome.setText(context.getResources().getString(R.string.gateway_welcome1));
        desc.setText(context.getResources().getString(R.string.gateway_des1));
        setFont(1, view1);
        collection.addView(view1);
        return view1;
      case 2:
        View view2 = inflater.inflate(R.layout.gateway_item2, null);
        initializeXmlId(2, view2);
        welcome.setText(context.getResources().getString(R.string.gateway_welcome2));
        desc.setText(context.getResources().getString(R.string.gateway_des2));
        setFont(2, view2);
        setView(2);
        collection.addView(view2);
        return view2;
      case 3:
        View view3 = inflater.inflate(R.layout.gateway_item2, null);
        initializeXmlId(2, view3);
        welcome.setText(context.getResources().getString(R.string.gateway_welcome3));
        desc.setText(context.getResources().getString(R.string.gateway_des3));
        setFont(2, view3);
        setView(3);
        collection.addView(view3);
        return view3;
      default:
        return null;
    }
  }

  private void initializeXmlId(int pos, View view) {
    if (pos == 0) {
      website = (AppCompatTextView) view.findViewById(R.id.website);
      welcome = (AppCompatTextView) view.findViewById(R.id.welcome);
      desc = (AppCompatTextView) view.findViewById(R.id.desc);
      RelativeLayout watchVideo = (RelativeLayout) view.findViewById(R.id.watch_video);
      watchVideoLabel = (AppCompatTextView) view.findViewById(R.id.watchVideoLabel);

      watchVideo.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent intent =
                  new Intent(
                      Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=6FGGquOrVic"));
              context.startActivity(intent);
            }
          });
    } else {
      bgImg = (AppCompatImageView) view.findViewById(R.id.bgImg);
      website = (AppCompatTextView) view.findViewById(R.id.website);
      welcome = (AppCompatTextView) view.findViewById(R.id.welcome);
      desc = (AppCompatTextView) view.findViewById(R.id.desc);
    }

    website.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            DbServiceSubscriber dbServiceSubscriber = new DbServiceSubscriber();
            Realm realm = AppController.getRealmobj(context);
            if (!dbServiceSubscriber.getApps(realm).getAppWebsite().equalsIgnoreCase("")) {
              Intent browserIntent =
                  new Intent(
                      Intent.ACTION_VIEW, Uri.parse(dbServiceSubscriber.getApps(realm).getAppWebsite()));
              context.startActivity(browserIntent);
            }
          }
        });
  }

  private void setFont(int pos, View view) {
    try {
      if (pos == 0) {
        website.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
        welcome.setTypeface(AppController.getTypeface(view.getContext(), "bold"));
        desc.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
        watchVideoLabel.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
      } else {
        website.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
        welcome.setTypeface(AppController.getTypeface(view.getContext(), "thin"));
        desc.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
      }

    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void setView(int pos) {

    if (pos == 2) {
      bgImg.setImageResource(R.drawable.img_02);
    }
    if (pos == 3) {
      bgImg.setImageResource(R.drawable.img_03);
    }
  }
}
