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
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.PagerAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.harvard.R;
import com.harvard.studyappmodule.studymodel.StudyInfo;
import com.harvard.utils.AppController;
import com.harvard.utils.CustomFirebaseAnalytics;
import com.harvard.utils.Logger;
import io.realm.RealmList;

public class StudyInfoPagerAdapter extends PagerAdapter {

  private int size;
  private AppCompatTextView title;
  private WebView desc;
  private RelativeLayout watchVideo;
  private AppCompatTextView watchVideoLabel;
  private Context context;
  private RealmList<StudyInfo> info;
  private AppCompatImageView bgImg;
  private CustomFirebaseAnalytics analyticsInstance;

  StudyInfoPagerAdapter(Context context, RealmList<StudyInfo> info, String studyId) {
    size = info.size();
    this.context = context;
    this.info = info;
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
    LayoutInflater inflater =
        (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    analyticsInstance = CustomFirebaseAnalytics.getInstance(context);
    if (info.get(position).getType().equalsIgnoreCase("video")) {
      View view = inflater.inflate(R.layout.study_info_item1, null);
      initializeXmlId(position, view);
      setFont(position, view);
      bindEvents(position);
      setData(position);
      collection.addView(view);
      return view;
    } else {
      View view1 = inflater.inflate(R.layout.study_info_item2, null);
      initializeXmlId(position, view1);
      setFont(position, view1);
      bindEvents(position);
      setData(position);
      collection.addView(view1);
      return view1;
    }
  }

  private void setData(int pos) {
    title.setText(info.get(pos).getTitle());
    desc.setBackgroundColor(Color.TRANSPARENT);
    String txtcolor;
    if (info.get(pos).getType().equalsIgnoreCase("video")) {
      txtcolor = "white";
    } else {
      txtcolor = "black";
    }
    String html = "&lt;font color=\"" + txtcolor + "\"&gt;" + (info.get(pos).getText()) + "&lt;/font&gt;";
    if (Build.VERSION.SDK_INT >= 24) {
      desc.loadDataWithBaseURL(null,
              Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY).toString(), "text/html", "UTF-8", null);
    } else {
      desc.loadDataWithBaseURL(null, Html.fromHtml(html).toString(), "text/html", "UTF-8", null);
    }
    RequestOptions requestOptions = new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false);

    Glide.with(context)
            .load(Base64.decode(info.get(pos).getImage().split(",")[1], Base64.DEFAULT))
            .thumbnail(0.5f)
            .apply(requestOptions)
            .into(bgImg);
  }

  private void initializeXmlId(int pos, View view) {
    if (info.get(pos).getType().equalsIgnoreCase("video")) {
      title = (AppCompatTextView) view.findViewById(R.id.title);
      desc = (WebView) view.findViewById(R.id.desc);
      watchVideo = (RelativeLayout) view.findViewById(R.id.watch_video);
      watchVideoLabel = (AppCompatTextView) view.findViewById(R.id.watchVideoLabel);
      bgImg = (AppCompatImageView) view.findViewById(R.id.bgImg);
    } else {
      title = (AppCompatTextView) view.findViewById(R.id.title);
      desc = (WebView) view.findViewById(R.id.desc);
      bgImg = (AppCompatImageView) view.findViewById(R.id.bgImg);
    }
  }

  private void setFont(int pos, View view) {
    try {
      if (info.get(pos).getType().equalsIgnoreCase("video")) {
        title.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
        watchVideoLabel.setTypeface(AppController.getTypeface(view.getContext(), "regular"));
      } else {
        title.setTypeface(AppController.getTypeface(view.getContext(), "thin"));
      }
    } catch (Exception e) {
      Logger.log(e);
    }
  }

  private void bindEvents(final int pos) {
    if (info.get(pos).getType().equalsIgnoreCase("video")) {
      if (info.get(pos).getLink().equalsIgnoreCase("")) {
        watchVideo.setVisibility(View.INVISIBLE);
        watchVideo.setClickable(false);
      } else {
        watchVideo.setVisibility(View.VISIBLE);
        watchVideo.setClickable(true);
        watchVideo.setOnClickListener(
            new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                Bundle eventProperties = new Bundle();
                eventProperties.putString(
                    CustomFirebaseAnalytics.Param.BUTTON_CLICK_REASON,
                    context.getString(R.string.watch_video));
                analyticsInstance.logEvent(
                    CustomFirebaseAnalytics.Event.ADD_BUTTON_CLICK, eventProperties);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.get(pos).getLink()));
                context.startActivity(intent);
              }
            });
      }
    }
  }
}
