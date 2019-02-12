package com.rohitupreti.pinedittext;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;

public class PinEditText extends AppCompatEditText {

    public enum State{
        FILLED,
        EMPTY
    }

    private State state;

    private static final int[] STATE_FILLED = {R.attr.state_filled};
    private static final int[] STATE_EMPTY = {R.attr.state_empty};

    public PinEditText(Context context) {
        super(context);
    }

    public PinEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        int[] def = super.onCreateDrawableState(extraSpace+1);
        if(getState().equals(State.FILLED)){
            mergeDrawableStates(def, STATE_FILLED);
        }
        return def;
    }


    public State getState(){
        if(state==null){
            this.state = State.EMPTY;
            this.refreshDrawableState();
        }
        return state;
    }

    public void setState(State state){
        this.state = state;
        this.refreshDrawableState();
    }

    public void clearText() {
        this.setText("");
        this.setState(State.EMPTY);
    }

}
