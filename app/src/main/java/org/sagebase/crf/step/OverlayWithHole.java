/*
 *    Copyright 2018 Sage Bionetworks
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

package org.sagebase.crf.step;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by liujoshua on 2/26/2018.
 */

public class OverlayWithHole extends android.support.v7.widget.AppCompatImageView {
    
    private RectF circleRect;
    private int radius;
    
    public OverlayWithHole(Context context, AttributeSet attrs) {
        super(context, attrs);
        //In versions > 3.0 need to define layer Type
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }
    
    public void setCircle(RectF rect, int radius) {
        this.circleRect = rect;
        this.radius = radius;
        //Redraw after defining circle
        postInvalidate();
    }
    
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draw Overlay

setCircle(        new RectF(0,0,getWidth(), getHeight()), getWidth()/2);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(getResources().getColor(android.R.color.white));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPaint(paint);
        
        //Draw transparent shape
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRoundRect(circleRect, radius, radius, paint);
    }
    
}
