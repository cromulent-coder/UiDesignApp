package com.rohitupreti.pinedittext;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

public class CustomCharPasswordTransformationMethod extends PasswordTransformationMethod {
    private final char c;

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    public CustomCharPasswordTransformationMethod(char c){
        this.c = c;
    }

    private class PasswordCharSequence implements CharSequence {
        private CharSequence mSource;
        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }
        public char charAt(int index) {
            return c; // This is the important part
        }
        public int length() {
            return mSource.length(); // Return default
        }
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
};
