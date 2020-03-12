/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
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
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import com.harvard.R;
import com.harvard.studyappmodule.studymodel.Resource;
import com.harvard.utils.AppController;
import com.harvard.utils.Logger;
import io.realm.RealmList;
import java.util.ArrayList;

public class GatewayResourcesListAdapter
    extends RecyclerView.Adapter<GatewayResourcesListAdapter.Holder> {
  private final Context mContext;
  private final ArrayList<Resource> mItems = new ArrayList<>();

  GatewayResourcesListAdapter(Context context, RealmList<Resource> items) {
    this.mContext = context;
    this.mItems.addAll(items);
  }

  @Override
  public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v =
        LayoutInflater.from(parent.getContext())
            .inflate(R.layout.resources_list_item, parent, false);
    return new Holder(v);
  }

  @Override
  public int getItemCount() {
    if (mItems == null) return 0;
    return mItems.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    final RelativeLayout mContainer;
    final AppCompatTextView mResourcesTitle;

    Holder(View itemView) {
      super(itemView);
      mContainer = (RelativeLayout) itemView.findViewById(R.id.container);
      mResourcesTitle = (AppCompatTextView) itemView.findViewById(R.id.resourcesTitle);
      setFont();
    }

    private void setFont() {
      try {
        mResourcesTitle.setTypeface(AppController.getTypeface(mContext, "regular"));
      } catch (Exception e) {
        Logger.log(e);
      }
    }
  }

  @Override
  public void onBindViewHolder(final Holder holder, final int position) {
    final int i = holder.getAdapterPosition();
    try {
      holder.mResourcesTitle.setText(mItems.get(i).getTitle());

      holder.mContainer.setOnClickListener(
          new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if (mItems.get(i).getType() != null) {
                Intent intent = new Intent(mContext, GatewayResourcesWebViewActivity.class);
                intent.putExtra("title", "" + mItems.get(i).getTitle().toString());
                intent.putExtra("type", "" + mItems.get(i).getType().toString());
                intent.putExtra("content", "" + mItems.get(i).getContent().toString());
                mContext.startActivity(intent);
              }
            }
          });
    } catch (Exception e) {
      Logger.log(e);
    }
  }
}
