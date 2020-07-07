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

package com.harvard.passcodemodule;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import com.harvard.R;

public class PasscodeView extends ViewGroup {

  private EditText editText;
  private int digitCount;
  private int digitWidth;
  private int digitRadius;
  private int outerStrokeWidth;
  private int innerStrokeWidth;
  private int digitSpacing;
  private int digitElevation;
  private int innerColor;
  private OnFocusChangeListener onFocusChangeListener;
  private PasscodeEntryListener passcodeEntryListener;

  public PasscodeView(Context context) {
    this(context, null);
  }

  public PasscodeView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public PasscodeView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);

    // Get style information
    TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.PasscodeView);
    digitCount = array.getInt(R.styleable.PasscodeView_numDigits, 4);

    // Dimensions
    DisplayMetrics metrics = getResources().getDisplayMetrics();
    digitRadius =
        array.getDimensionPixelSize(
            R.styleable.PasscodeView_digitRadius,
            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, metrics));
    outerStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics);
    innerStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, metrics);
    digitWidth = (digitRadius + outerStrokeWidth) * 2;

    digitSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, metrics);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      digitElevation = array.getDimensionPixelSize(R.styleable.PasscodeView_digitElevation, 0);
    }

    // Get theme to resolve defaults
    Resources.Theme theme = getContext().getTheme();

    int controlColor = Color.DKGRAY;
    // Text colour, default to android:colorControlNormal from theme
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      TypedValue controlColor2 = new TypedValue();
      theme.resolveAttribute(android.R.attr.colorControlNormal, controlColor2, true);
      controlColor =
              controlColor2.resourceId > 0
              ? getResources().getColor(controlColor2.resourceId)
              : controlColor2.data;
    }
    controlColor = array.getColor(R.styleable.PasscodeView_controlColor, controlColor);

    // Accent colour, default to android:colorAccent from theme
    int highlightedColor = Color.LTGRAY;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      TypedValue accentColor = new TypedValue();
      theme.resolveAttribute(R.attr.colorControlHighlight, accentColor, true);
      highlightedColor =
          accentColor.resourceId > 0
              ? getResources().getColor(accentColor.resourceId)
              : accentColor.data;
    }
    highlightedColor =
        array.getColor(R.styleable.PasscodeView_controlColorActivated, highlightedColor);

    // color for the inner circle
    innerColor = Color.CYAN;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      TypedValue innerColor2 = new TypedValue();
      theme.resolveAttribute(android.R.attr.colorPrimary, innerColor2, true);
      innerColor =
              innerColor2.resourceId > 0
              ? getResources().getColor(innerColor2.resourceId)
              : innerColor2.data;
    }
    innerColor = array.getColor(R.styleable.PasscodeView_digitColorFilled, innerColor);

    // color for the inner circle border
    int innerBorderColor = Color.GREEN;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      TypedValue innerBorderColor2 = new TypedValue();
      theme.resolveAttribute(android.R.attr.colorPrimaryDark, innerBorderColor2, true);
      innerBorderColor =
              innerBorderColor2.resourceId > 0
              ? getResources().getColor(innerBorderColor2.resourceId)
              : innerBorderColor2.data;
    }
    innerBorderColor =
        array.getColor(R.styleable.PasscodeView_digitColorBorder, innerBorderColor);

    // Recycle the typed array
    array.recycle();

    // Add child views
    setupViews();
  }

  @Override
  public boolean shouldDelayChildPressedState() {
    return false;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    // Measure children
    for (int i = 0; i < getChildCount(); i++) {
      getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
    }

    // Calculate the size of the view
    int width = (digitWidth * digitCount) + (digitSpacing * (digitCount - 1));
    setMeasuredDimension(
        width + getPaddingLeft() + getPaddingRight() + (digitElevation * 2),
        digitWidth + getPaddingTop() + getPaddingBottom() + (digitElevation * 2));
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    // Position the child views
    for (int i = 0; i < digitCount; i++) {
      View child = getChildAt(i);
      int left = i * digitWidth + (i > 0 ? i * digitSpacing : 0);
      child.layout(
          left + getPaddingLeft() + digitElevation,
          getPaddingTop() + (digitElevation / 2),
          left + getPaddingLeft() + digitElevation + digitWidth,
          getPaddingTop() + (digitElevation / 2) + digitWidth);
    }

    // Add the edit text as a 1px wide view to allow it to focus
    getChildAt(digitCount).layout(0, 0, 1, getMeasuredHeight());
  }

  private void setupViews() {
    setWillNotDraw(false);
    // Add a digit view for each digit
    for (int i = 0; i < digitCount; i++) {
      DigitView digitView = new DigitView(getContext(), i);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        digitView.setElevation(digitElevation);
      }
      addView(digitView);
    }

    // Add an "invisible" edit text to handle input
    editText = new EditText(getContext());
    editText.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    editText.setTextColor(getResources().getColor(android.R.color.transparent));
    editText.setCursorVisible(false);
    editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(digitCount)});
    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    editText.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
    editText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
    editText.setOnFocusChangeListener(
        new OnFocusChangeListener() {
          @Override
          public void onFocusChange(View v, boolean hasFocus) {
            // Update the selected state of the views
            int length = editText.getText().length();
            updateChilViewSelectionStates(length, hasFocus);
            // Make sure the cursor is at the end
            editText.setSelection(length);

            // Provide focus change events to any listener
            if (onFocusChangeListener != null) {
              onFocusChangeListener.onFocusChange(PasscodeView.this, hasFocus);
            }
          }
        });

    editText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {}

          @Override
          public void afterTextChanged(Editable s) {
            int length = s.length();
            updateChilViewSelectionStates(length, editText.hasFocus());

            if (length == digitCount && passcodeEntryListener != null) {
              passcodeEntryListener.onPasscodeEntered(s.toString());
            }
          }
        });
    addView(editText);

    invalidate();
  }

  private void updateChilViewSelectionStates(int length, boolean hasFocus) {
    for (int i = 0; i < digitCount; i++) {
      getChildAt(i).setSelected(hasFocus && i == length);
    }
  }

  /**
   * Get the {@link Editable} from the EditText.
   *
   * @return
   */
  public Editable getText() {
    return editText.getText();
  }

  /**
   * Set text to the EditText.
   *
   * @param text
   */
  public void setText(CharSequence text) {
    if (text.length() > digitCount) {
      text = text.subSequence(0, digitCount);
    }
    editText.setText(text);
    invalidateChildViews();
  }

  /** Clear passcode input. */
  public void clearText() {
    editText.setText("");
    invalidateChildViews();
  }

  private void invalidateChildViews() {
    for (int i = 0; i < digitCount; i++) {
      getChildAt(i).invalidate();
    }
  }

  public void setPasscodeEntryListener(PasscodeEntryListener passcodeEntryListener) {
    this.passcodeEntryListener = passcodeEntryListener;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      requestToShowKeyboard();
      return true;
    }
    return super.onTouchEvent(event);
  }

  /** Requests the view to be focused and the keyboard to be popped-up. */
  public void requestToShowKeyboard() {
    // Make sure this view is focused
    editText.requestFocus();

    // Show keyboard
    InputMethodManager inputMethodManager =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    inputMethodManager.showSoftInput(editText, 0);
  }

  @Override
  public OnFocusChangeListener getOnFocusChangeListener() {
    return onFocusChangeListener;
  }

  @Override
  public void setOnFocusChangeListener(OnFocusChangeListener l) {
    onFocusChangeListener = l;
  }

  @Override
  protected Parcelable onSaveInstanceState() {
    Parcelable parcelable = super.onSaveInstanceState();
    SavedState savedState = new SavedState(parcelable);
    savedState.editTextValue = editText.getText().toString();
    return savedState;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    SavedState savedState = (SavedState) state;
    super.onRestoreInstanceState(savedState.getSuperState());
    editText.setText(savedState.editTextValue);
    editText.setSelection(savedState.editTextValue.length());
  }

  static class SavedState extends BaseSavedState {

    public static final Creator<SavedState> CREATOR =
        new Creator<SavedState>() {
          @Override
          public SavedState createFromParcel(Parcel in) {
            return new SavedState(in);
          }

          @Override
          public SavedState[] newArray(int size) {
            return new SavedState[size];
          }
        };
    String editTextValue;

    SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel source) {
      super(source);
      editTextValue = source.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
      super.writeToParcel(dest, flags);
      dest.writeString(editTextValue);
    }
  }

  class DigitView extends View {

    private Paint outerPaint;
    private Paint innerPaint;
    private int position = 0;

    public DigitView(Context context, int position) {
      this(context);
      this.position = position;
    }

    public DigitView(Context context) {
      this(context, null);
    }

    public DigitView(Context context, AttributeSet attrs) {
      this(context, attrs, 0);
    }

    public DigitView(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);

      init();
    }

    void init() {
      setWillNotDraw(false);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
      }
      outerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      outerPaint.setAlpha(255);
      outerPaint.setDither(true);
      outerPaint.setStyle(Paint.Style.STROKE);
      outerPaint.setStrokeWidth(outerStrokeWidth);
      outerPaint.setStrokeCap(Paint.Cap.ROUND);
      outerPaint.setStrokeJoin(Paint.Join.ROUND);

      innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
      innerPaint.setAlpha(255);
      innerPaint.setStyle(Paint.Style.FILL_AND_STROKE);
      innerPaint.setStrokeWidth(innerStrokeWidth);
      innerPaint.setStrokeCap(Paint.Cap.ROUND);
      innerPaint.setStrokeJoin(Paint.Join.ROUND);
      innerPaint.setColor(innerColor);

      invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      setMeasuredDimension(digitWidth, digitWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
      float center = getWidth() / 2;

      if (isSelected()) {
        outerPaint.setColor(getResources().getColor(R.color.colorPrimary));
      } else {
        outerPaint.setColor(getResources().getColor(R.color.colorSecondaryBg));
      }
      canvas.drawColor(Color.TRANSPARENT);
      canvas.drawCircle(center, center, digitRadius, outerPaint);
      if (editText.getText().length() > position) {
        canvas.drawCircle(center, center, digitRadius, innerPaint);
      }
    }
  }

  /** Listener that gets notified when the complete passcode has been entered. */
  public interface PasscodeEntryListener {
    /**
     * Called when all the digits of the passcode has been entered.
     *
     * @param passcode - The entered passcode
     */
    void onPasscodeEntered(String passcode);
  }
}
