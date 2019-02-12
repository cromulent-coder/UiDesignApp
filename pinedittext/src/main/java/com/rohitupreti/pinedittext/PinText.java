package com.rohitupreti.pinedittext;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.TransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class PinText extends LinearLayout{

    private static final String TAG = PinText.class.getSimpleName();

    private ImageView mClearButton;
    private PinEditText[] mEditTexts;
    private List<FilledListener> filledListenerList;

    private int length;

    private int inputType;
    private static final int NUMBER = 0;
    private static final int STRING = 1;

    private int MAX_CHARS = 1;
    private static final int MAX_LINES = 1;
    private int textSize;
    private String hint;
    private int boxWidth;
    private boolean secure;
    private int boxHeight;

    private int focussed;

    public PinText(Context context) {
        super(context);
        initializeViews(context, null);
    }

    public PinText(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PinText,
                0, 0);
        initializeViews(context,a);
    }

    public PinText(Context context,
                   AttributeSet attrs,
                   int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PinText,
                0, 0);
        initializeViews(context, a);
    }

    /**
     * Inflates the views in the layout.
     *
     * @param context
     *           the current context for the view.
     */
    private void initializeViews(Context context, TypedArray arr) {
        this.inputType = arr.getInt(R.styleable.PinText_input, 0);
        this.secure = arr.getBoolean(R.styleable.PinText_secure, false);
        this.length = arr.getInt(R.styleable.PinText_length, 3);
        this.textSize = arr.getDimensionPixelSize(R.styleable.PinText_size, dpToPx(context, 12));
        this.hint = arr.getString(R.styleable.PinText_hintWord);
        this.hint = correctHintLength(hint, length);
        this.boxWidth = arr.getDimensionPixelSize(R.styleable.PinText_boxWidth, dpToPx(context, 20));
        this.boxHeight = arr.getDimensionPixelSize(R.styleable.PinText_boxHeight, dpToPx(context, 20));
        this.MAX_CHARS = arr.getInteger(R.styleable.PinText_maxCharPerField, 1);
        this.focussed = -1;

        this.mEditTexts = new PinEditText[this.length];

        int hintCharLength = hint.length()/length;

        Log.d(TAG, "Length: " + this.length);

        Log.d(TAG, this.inputType + " : " + this.length);
        this.setOrientation(HORIZONTAL);
        int px = dpToPx(getContext(), 8);
        int pxSmall = dpToPx(getContext(), 4);
        this.setPadding(px, px, px, px);

        LayoutParams params = new LayoutParams(
                boxWidth,
                boxHeight
        );
        params.setMargins(pxSmall, 0, 0, 0);

        for(int i=0; i<length; i++) {
            mEditTexts[i] = new PinEditText(context);
            mEditTexts[i].setBackgroundResource(R.drawable.background_line);
            mEditTexts[i].setMaxLines(MAX_LINES);
            mEditTexts[i].setFilters(setMaxLength(MAX_CHARS));
            mEditTexts[i].setGravity(Gravity.CENTER);
            mEditTexts[i].setHint(hint.substring(i*hintCharLength, i*hintCharLength + hintCharLength));
            mEditTexts[i].setCursorVisible(false);
            mEditTexts[i].setSelectAllOnFocus(true);
            mEditTexts[i].setHighlightColor(Color.alpha(0));
            mEditTexts[i].setState(PinEditText.State.EMPTY);
            mEditTexts[i].setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            mEditTexts[i].setInputType(getInputType(inputType, secure));
            mEditTexts[i].setTextColor(context.getResources().getColor(R.color.colorAccent));
            //mEditTexts[i].setHintTextColor(context.getResources().getColor(R.color.colorAccentLighter));
            if(secure) {
                mEditTexts[i].setTransformationMethod(new CustomCharPasswordTransformationMethod('●'));
            }
            if(i==length-1){
                params.setMargins(pxSmall, 0, pxSmall, 0);
            }
            mEditTexts[i].setLayoutParams(params);
            this.addView(mEditTexts[i]);
        }
    }

    private int getInputType(int inputType, boolean secure) {
        if(secure){
            Log.d(TAG, "Secure field: " + secure);
            if(inputType==NUMBER){
                return InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            }
            else{
                return InputType.TYPE_CLASS_TEXT |InputType.TYPE_NUMBER_VARIATION_PASSWORD;
            }
        }
        else{
            if(inputType==NUMBER){
                return InputType.TYPE_CLASS_NUMBER;
            }
            else{
                return InputType.TYPE_CLASS_TEXT;
            }
        }
    }

    private String correctHintLength(String hint, int length) {
        int hintLength = hint.length();

        if(hintLength%length!=0){
            int d = hintLength/length;
            d = d+1;
            //whitespaces
            d = d*length - hintLength;
            StringBuilder builder = new StringBuilder(hint);
            for(int i=0; i<d; i++){
                builder.append(" ");
            }
            hint = builder.toString();
        }
        return hint;
    }


    public InputFilter[] setMaxLength(int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        return filterArray;
    }



    private int dpToPx(Context context, int dp){
        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );
        return px;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        for(int i=0; i<length; i++){

            final int finalI = i;
            mEditTexts[i].setOnFocusChangeListener(new OnFocusChangeListener() {
                int k = finalI;
                @Override
                public void onFocusChange(View view, boolean b) {
                    Log.d(TAG, "Focussed : "  + b + " index " + finalI);
                    if(b) {
                        focussed = k;
                    }
                    else{
                        focussed = -1;
                    }
                }
            });

            mEditTexts[i].setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(keyCode == KeyEvent.KEYCODE_DEL) {
                        //this is for backspace
                        Log.d(TAG, "key Event delete called");
                        if(event.getAction()!=KeyEvent.ACTION_DOWN && ((PinEditText)v).getText().length()==0) {
                            if(focussed!=0){
                                mEditTexts[focussed].setState(PinEditText.State.EMPTY);
                                mEditTexts[focussed - 1].requestFocus();
                                mEditTexts[focussed].setSelection(mEditTexts[focussed].getText().length());
                            }
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
            });

            mEditTexts[i].addTextChangedListener(new TextWatcher() {
                int lb;
                int la;
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    Log.d(TAG, "Before Length : " + lb);
                    lb = charSequence.length();
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    la = editable.length();
                    Log.d(TAG, "After Length : " + la);
                    //implies a character addition
                    if(la>=lb) {
                        if (la == MAX_CHARS) {
                            Log.d(TAG, "filled state: " + focussed);
                            mEditTexts[focussed].setState(PinEditText.State.FILLED);
                            mEditTexts[focussed].refreshDrawableState();
                            if (focussed == length - 1) {
                                //do nothing
                                informListeners(getText());
                                mEditTexts[focussed].requestFocus();
                                mEditTexts[focussed].setSelection(mEditTexts[focussed].getText().length());
                            } else {
                                mEditTexts[focussed + 1].requestFocus();
                                mEditTexts[focussed].setSelection(mEditTexts[focussed].getText().length());
                            }
                        }
                    }
                    //implies a character deletion
                    else if(la<lb){
                        Log.d(TAG, focussed + " : Character deleted, going back to : " + (focussed-1));
                        if(lb==1 && la==0){
                            mEditTexts[focussed].setState(PinEditText.State.EMPTY);
                            if(focussed>0){
                                mEditTexts[focussed-1].requestFocus();
                                mEditTexts[focussed].setSelection(mEditTexts[focussed].getText().length());
                            }
                        }
                    }
                }

            });
        }
    }


    public void setText(String text){
        int fieldsRequired = (int) Math.ceil((text.length()*1.0)/MAX_CHARS);
        Log.d(TAG, "Total Fields : " + length + " :: "+ fieldsRequired);
        for(int i=0; i<length && i<fieldsRequired; i++){
            Log.d(TAG, "Current field no: " + i);
            focussed = i;
            if(i*MAX_CHARS+MAX_CHARS>text.length()){
                this.mEditTexts[i].setText(text.substring(i * MAX_CHARS, text.length()));
            }
            else {
                this.mEditTexts[i].setText(text.substring(i * MAX_CHARS, i * MAX_CHARS + MAX_CHARS));
            }
        }
    }

    public void clearText(){
        for(int i=0; i<length; i++){
            this.mEditTexts[i].setText("");
            this.mEditTexts[i].setState(PinEditText.State.EMPTY);
        }
        this.mEditTexts[0].requestFocus();
    }

    public String getText(){
        StringBuilder builder = new StringBuilder();
        for(int i=0; i<length; i++){
            builder.append(this.mEditTexts[i].getText().toString());
        }
        return builder.toString();
    }

    public void addFilledListener(FilledListener filledListener){
        if(this.filledListenerList==null){
            filledListenerList = new ArrayList<>();
        }
        filledListenerList.add(filledListener);
    }

    public void removeFilledListener(FilledListener filledListener){
        if(filledListenerList!=null){
            this.filledListenerList.remove(filledListener);
        }
    }

    private void informListeners(String content){
        if(filledListenerList==null){
            return;
        }
        for(FilledListener filledListener: filledListenerList){
            filledListener.onFilled(content);
        }
    }

    public void setTransformationMethod(TransformationMethod transformationMethod){
        for(int i=0; i<length; i++){
            mEditTexts[i].setTransformationMethod(transformationMethod);
            mEditTexts[i].invalidate();
        }
    }


    public interface FilledListener{
        void onFilled(String content);
    }

}

