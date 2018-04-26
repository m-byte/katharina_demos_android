package de.proglove.katharinaUI;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.AttributeSet;

/**
 * Created by matthias on 3/1/18.
 */

public class ScanProgress extends ScanText {
    private ClipDrawable clip;
    protected int max;
    protected int level;

    public ScanProgress(Context context){
        this(context, null);
    }

    public ScanProgress(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public ScanProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMax(0);
    }

    public void setMax(int max){
        if(max < 1){
            this.max = 1;
        } else {
            this.max = max;
        }
        this.level = 0;
        update();
    }

    public void setLevel(int level){
        if(level < 0){
            this.level = 0;
        } else {
            this.level = level;
        }
        if(this.max < this.level){
            setMax(this.level);
        }
        update();
    }

    @Override
    protected boolean validate(){
        if (handler.validate(this, scannedText, validator, compare)) {
            level++;
            if(level == max){
                valid = true;
                int pL = getPaddingLeft();
                int pT = getPaddingTop();
                int pR = getPaddingRight();
                int pB = getPaddingBottom();
                setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.input_bg_checked, null));
                setPadding(pL, pT, pR, pB);
            }
            scannedText = "";
            update();
        } else {
            scannedText = "";
            update();
            handler.onValidationFailed(this);
        }
        return valid;
    }

    @Override
    protected void update(){
        SpannableString strMax = new SpannableString(max + "");
        SpannableStringBuilder builder = new SpannableStringBuilder("St. ");
        builder.append(String.format("%1$" + strMax.length() + "s/", this.level), new TypefaceSpan("monospace"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        strMax.setSpan(new StyleSpan(Typeface.BOLD), 0, strMax.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(strMax, new TypefaceSpan("monospace"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if(clip != null) {
            clip.setLevel(level * 10000 / max);
        }
        setText(builder);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        if(focused && clip == null){
            this.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.input_progress_bg, null));
            LayerDrawable bg = (LayerDrawable) this.getBackground();
            clip = (ClipDrawable) bg.findDrawableByLayerId(R.id.input_progress_clip);
        }
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }
}
