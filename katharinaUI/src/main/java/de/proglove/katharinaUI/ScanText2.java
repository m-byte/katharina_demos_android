package de.proglove.katharinaUI;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class ScanText2 extends AppCompatEditText {
    protected CharSequence sequence;
    protected boolean focused;
    protected boolean valid;
    protected int left;
    protected int right;
    protected int top;
    protected int bottom;

    public ScanText2(Context context){
        this(context, null);
    }

    public ScanText2(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public ScanText2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setCursorVisible(false);
        this.setShowSoftInputOnFocus(false);
        this.setLongClickable(false);
        this.setFocusable(true);
        this.setClickable(true);
        this.setInputType(this.getInputType() | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        this.setTypeface(Typeface.SANS_SERIF);
        this.valid = false;
        this.focused = false;
        update();
    }

    public void setValid(boolean b){
        this.valid = b;
        update();
    }

    public void setFocused(boolean b){
        this.focused = b;
        update();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        sequence = text;
    }

    public void update(){
        ViewGroup.LayoutParams params = this.getLayoutParams();
        if(this.valid){
            this.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.input_bg_checked_mark, null));
        }else if(this.focused){
            this.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.input_bg_focused, null));
        }else{
            this.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.input_bg_normal, null));
        }
        if(params != null) {
            this.setLayoutParams(params);
        }
        super.setPadding(left, top, right, bottom);
        super.setText(sequence);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(focused && !valid){
            update();
        }
    }

    @Override
    protected boolean getDefaultEditable() {
        return false;
    }

    @Override
    public boolean isEnabled(){
        return false;//return super.isEnabled() && !valid;
    }
}