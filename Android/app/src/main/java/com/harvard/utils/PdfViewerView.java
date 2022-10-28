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

package com.harvard.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.tabs.TabLayout;
import com.harvard.R;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PdfViewerView extends ViewPager {

  private PdfRenderer pdfRender;
  Context context;
  private ParcelFileDescriptor mFileDescriptor;
  private PdfRenderer.Page currentPage;

  public PdfViewerView(Context context) {
    super(context);
    this.context = context;
  }

  public PdfViewerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
  }


  /**
   * Uses the {@link ParcelFileDescriptor} directly, see {@link PdfRenderer(ParcelFileDescriptor)}
   *
   * @param input {@link ParcelFileDescriptor}
   */
  public void setPdf(@NonNull ParcelFileDescriptor input) {
    if (pdfRender != null) {
      pdfRender.close();
    }
    try {
      pdfRender = new PdfRenderer(input);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    PdfAdapter pdfAdapter = new PdfAdapter(context, pdfRender);
    TabLayout tabLayout = (TabLayout) this.findViewById(R.id.tab_layout);
    tabLayout.setupWithViewPager(this, true);
    setAdapter(pdfAdapter);
  }

  /**
   * Sets the pdf with the file uses {@link #setPdf(ParcelFileDescriptor)} internally
   *
   * @param file of the pdf to load
   */
  public void setPdf(@NonNull File file) {
    try {
      mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
      setPdf(mFileDescriptor);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets a pdf to view from the assets directory
   *
   * @param assetFile the path and name of the asset to load
   */
  public void setPdfFromAsset(@NonNull String assetFile) {
    try {
      File file = new File(context.getFilesDir(), assetFile);
      if (!file.exists()) {
        // copy to the assets dir
        InputStream asset = context.getAssets().open(assetFile);
        FileOutputStream output = new FileOutputStream(file);
        final byte[] buffer = new byte[1024];
        int size;
        while ((size = asset.read(buffer)) != -1) {
          output.write(buffer, 0, size);
        }
        asset.close();
        output.close();
      }
      setPdf(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Sets a pdf to view from a byte array
   *
   * @param bytes for the file
   * @param name  the name of the file to save in the app's cache dir
   */
  public void setPdfFromBytes(@NonNull byte[] bytes, @NonNull String name) {
    File file = new File("/data/data/" + context.getPackageName() + "/files/", name);
    try {
      if (name.equalsIgnoreCase("temp.pdf") && file.exists()) {
        file.delete();
      }
      if (!file.exists()) {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bos.write(bytes);
        bos.flush();
        bos.close();
      }
      setPdf(file);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @return The current page count
   */
  public int getPageCount() {
    return pdfRender == null ? 0 : pdfRender.getPageCount();
  }

  /**
   * @return The current page via {@link #getCurrentItem()}
   */
  public int getCurrentPage() {
    return getCurrentItem();
  }

  private  class PdfAdapter extends PagerAdapter {

    @NonNull
    private final PdfRenderer renderer;
    private final int count;
    @NonNull
    private final Context context;

    private PdfAdapter(@NonNull Context context, @NonNull PdfRenderer renderer) {
      this.context = context;
      this.count = renderer.getPageCount();
      this.renderer = renderer;
    }

    @Override
    public int getCount() {
      return count;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      if (currentPage != null) {
        try {
          currentPage.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      currentPage = renderer.openPage(position);

      final Bitmap bitmap = Bitmap.createBitmap(currentPage.getWidth(), currentPage.getHeight(),
          Bitmap.Config.ARGB_8888);
      Runnable runnable = new Runnable() {
        public void run() {
          currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        }
      };
      runnable.run();

      PhotoView photoView = new PhotoView(context);

      photoView.setImageBitmap(bitmap);

      container.addView(photoView);

      return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      try {
        if (currentPage != null) {
          try {
            currentPage.close();
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      } catch (Exception e) {
        // no op, need to make sure it is closed
      }

      container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == object;
    }
  }

  @Override
  public boolean onTouchEvent(MotionEvent ev) {
    try {
      return super.onTouchEvent(ev);
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    try {
      return super.onInterceptTouchEvent(ev);
    } catch (IllegalArgumentException ex) {
      ex.printStackTrace();
    }
    return false;
  }

  public void destroyPdfRender() {
    try {
      if (currentPage != null) {
        currentPage.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (pdfRender != null) {
        pdfRender.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      if (mFileDescriptor != null) {
        mFileDescriptor.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}