package de.proglove.katharinaUI;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

public class ScanText extends AppCompatEditText {
    /*@Deprecated
    private boolean poop = false;
    @Deprecated
    private String lastText = "";

    @Deprecated
    public void enablePoop(){
        poop = true;
    }*/

    protected boolean valid;
    protected boolean compare;
    protected String validator;
    protected String format;
    protected String scannedText;
    protected OnScanEventListener handler;
    private final String schema = "http://schemas.android.com/apk/res-auto";

    public ScanText(Context context){
        this(context, null);
    }

    public ScanText(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public ScanText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setCursorVisible(false);
        this.setShowSoftInputOnFocus(false);
        this.setLongClickable(false);
        this.setFocusable(true);
        this.setClickable(true);
        this.setInputType(this.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        this.setTypeface(Typeface.SANS_SERIF);
        this.valid = false;
        this.scannedText = "";
        this.validator = attrs.getAttributeValue(schema, "validator");
        this.format = attrs.getAttributeValue(schema, "format");
        this.handler = new OnScanEventListener();
        if(this.validator == null || this.validator.length() < 1){
            if(this.compare){
                this.validator = "";
            }else{
                this.validator = ".+";
            }
        }
        update();
    }

    public void setCompareString(String s){
        this.compare = true;
        this.validator = s;
        update();
    }

    public void setValidator(String s){
        this.compare = false;
        this.validator = s;
        update();
    }

    protected boolean validate(){
        this.valid = handler.validate(this, scannedText, validator, compare);
        if (this.valid) {
            this.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.input_bg_checked_mark, null));
            update();
        } else {
            scannedText = "";
            update();
            handler.onValidationFailed(this);
        }
        return valid;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        boolean focusNext = false;
        if(event.getAction() == KeyEvent.ACTION_UP){
            switch(keyCode){
                case KeyEvent.KEYCODE_DEL:
                    if(!this.valid && this.scannedText.length() > 0) {
                        this.scannedText = this.scannedText.substring(0, this.scannedText.length() - 1);
                    }
                    break;
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                case KeyEvent.KEYCODE_TAB:
                    focusNext = true;
                    break;
                default:
                    char in = (char) event.getUnicodeChar();
                    Character.UnicodeBlock block = Character.UnicodeBlock.of(in);
                    if(!this.valid && !Character.isISOControl(in) && block != null && block != Character.UnicodeBlock.SPECIALS) {
                        this.scannedText += Character.toString(in);
                    }
                    break;
            }
        }
        update();
        if(focusNext && this.validate()){
            handler.onNext(this);
        }
        return true;
    }

    public void setScanned(String input){
        scannedText = input;
        if(this.validate()){
            handler.onNext(this);
        }
    }

    public String getScanned(){
        return scannedText;
    }

    protected void update() {
        if(!this.compare) {
            super.setText(this.format(this.scannedText));
        }else{
            super.setText(this.format(this.validator));
        }
        /*if(poop) {
            lastText = super.getText().toString();
        }*/
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(focused && !valid){
            scannedText = "";
            update();
        }
        /*if(!focused && poop){
            String currText = super.getText().toString();
            if(!lastText.equals(currText)){
                if(currText.length() > lastText.length()) {
                    scannedText = getText(currText, lastText, false);
                    if(validate()){
                        handler.onNext(this);
                    }else{
                        scannedText = getText(currText, lastText, true);
                        if(validate()) {
                            handler.onNext(this);
                        } else {
                            scannedText = "";
                            update();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable(){
                                @Override
                                public void run() {
                                    requestFocus();
                                }
                            }, 10);
                        }
                    }
                    update();
                }
            }
        }*/
    }

    @Deprecated
    private static String getText(String currText, String lastText, boolean reverse){
        String ret = "";
        int i, j;
        if(reverse){
            for (i = currText.length() - 1, j = lastText.length() - 1; i >= 0 && j >= 0; i--, j--) {
                while (j >= 0 && currText.charAt(i) != lastText.charAt(j)){
                    ret += currText.charAt(i--);
                }
            }
            if(i < currText.length()){
                ret += currText.substring(0, i + 1);
            }
        }else {
            for (i = 0, j = 0; i < currText.length() && j < lastText.length(); i++, j++) {
                while (currText.charAt(i) != lastText.charAt(j) && j < lastText.length()){
                    ret += currText.charAt(i++);
                }
            }
            if(i < currText.length()){
                ret += currText.substring(i);
            }
        }
        return ret;
    }

    private CharSequence format(String s){
        if(s == null){
            s = "";
        }
        if(format == null) {
            return s;
        }
        SpannableStringBuilder builder = new SpannableStringBuilder();
        for (int i = 0, j = 0; i < format.length() && j < s.length(); i++) {
            switch (format.charAt(i)) {
                case ' ':
                    builder.append(" ");
                    break;
                case 'A':
                    builder.append(s.charAt(j++) + "", new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case 'a':
                default:
                    builder.append(s.charAt(j++));
                    break;
            }
        }
        return builder;
    }

    @Override
    protected boolean getDefaultEditable() {
        return false;
    }

    @Override
    public boolean isEnabled(){
        return super.isEnabled() && !valid;
    }

    public boolean isValid() {
        return valid;
    }

    public static class OnScanEventListener {
        private boolean valid;

        public OnScanEventListener(){};

        public void onNext(ScanText source) {
            if (source.hasFocus()) {
                int dirs[] = {FOCUS_FORWARD, FOCUS_RIGHT, FOCUS_DOWN, FOCUS_BACKWARD, FOCUS_LEFT, FOCUS_UP};
                for (int i = 0; i < dirs.length; i++) {
                    View v = source.focusSearch(dirs[i]);
                    if (v != null && v.requestFocus(dirs[i])) {
                        break;
                    }
                }
            }
        };
        public void onValidationFailed(ScanText source){};
        public boolean validate(ScanText source, String input, String validator, boolean compare){
            return (compare && input.equals(validator)) || (!compare && validate(source, input, validator));
        };
        public boolean validate(ScanText source, String input, String validator){
            return input.matches(validator);
        }
    }

    public void setOnScanEventListener(OnScanEventListener handler){
        this.handler = handler;
    }
}