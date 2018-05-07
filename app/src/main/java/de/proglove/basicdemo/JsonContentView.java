package de.proglove.basicdemo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import de.proglove.katharinaUI.AlertDialog;
import de.proglove.katharinaUI.ScanText2;

public class JsonContentView extends RelativeLayout {
    protected JSONArray layout;
    protected JSONObject content;
    protected Dialog errorDlg = null;
    protected HashMap<String, View> idmap;
    protected Activity parent;
    protected Context context;
    protected View header = null;
    protected View body = null;
    protected TextView footer = null;
    protected int layouthash;
    protected float density;
    protected boolean noUpdate = true;

    public JsonContentView(Context context, Activity activity) {
        super(context);
        this.context = context;
        this.parent = activity;

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        this.density = metrics.density;
    }

    public void setPreview(View v){
        if(noUpdate && v != null){
            if(body != null){
                this.removeView(body);
                body = null;
            }
            // use this to display a settings before the websocket connection has been established
            body = v;
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            lp.alignWithParent = true;
            if(header != null) {
                lp.addRule(RelativeLayout.BELOW, header.getId());
            }
            if(footer != null){
                if(footer.getId() == NO_ID){
                    footer.setId(View.generateViewId());
                }
                lp.addRule(RelativeLayout.ABOVE, footer.getId());
            }
            this.addView(body, lp);
        }
    }

    public void setHeader(View v){
        this.header = v;
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_TOP);
        params.addRule(CENTER_HORIZONTAL);
        params.topMargin = 0;
        v.setId(View.generateViewId());
        addView(v,params);
    }

    public void updateJSONUi(String input){
        final String in = input;
        parent.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                try {
                    updateJSON(new JSONObject(in));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void updateJSON(JSONObject input) throws JSONException {
        boolean newLayout = false;
        noUpdate = false;
        String error = null;

        // Get data
        if (input.has("layout")) {
            layout = input.getJSONArray("layout");
            // Only update if layout has changed
            newLayout = (layout.toString().hashCode() != layouthash);
        }
        content = input.optJSONObject("content");
        if (input.has("error")) {
            JSONObject err = input.getJSONObject("error");
            error = err.optString("value", null);
        }

        // Generate layout if required
        if (newLayout) {
            layouthash = layout.toString().hashCode();
            if(body != null){
                this.removeView(body);
                body = null;
            }
            if(footer != null){
                this.removeView(footer);
                footer = null;
            }
            idmap = new HashMap<String, View>();

            buildView(layout, null);

            if(body != null){
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                lp.alignWithParent = true;
                if(header != null) {
                    lp.addRule(RelativeLayout.BELOW, header.getId());
                }
                if(footer != null){
                    if(footer.getId() == NO_ID){
                        footer.setId(View.generateViewId());
                    }
                    lp.addRule(RelativeLayout.ABOVE, footer.getId());
                }
                this.addView(body, lp);
                if(LinearLayout.class.isAssignableFrom(body.getClass())){
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resizeView((LinearLayout) body);
                        }
                    }, 1);
                }
            }
            if(footer != null){
                this.addView(footer);
            }
        }

        // Update data
        if(content != null && body != null) {
            for (Iterator<String> keys = content.keys(); keys.hasNext(); ) {
                String key = keys.next();
                if (idmap.containsKey(key)) {
                    // Load objects
                    View view = idmap.get(key);
                    JSONObject attributes = content.getJSONObject(key);
                    CharSequence text = format(attributes.optString("value"), attributes.optString("format", null));
                    if (EditText.class.isAssignableFrom(view.getClass())) {
                        EditText element = (EditText) view;
                        element.setText(text);
                        if (ScanText2.class.isAssignableFrom(view.getClass())) {
                            ScanText2 scan = (ScanText2) view;
                            scan.setFocused(attributes.optBoolean("input", false));
                            scan.setValid(attributes.optBoolean("checked", false));
                        }
                    } else if (TextView.class.isAssignableFrom(view.getClass())) {
                        TextView element = (TextView) view;
                        element.setText(text, TextView.BufferType.SPANNABLE);
                    }
                }
            }
        }

        // Show or hide error dialog
        if(errorDlg != null){
            errorDlg.cancel();
            errorDlg = null;
        }
        if (error != null) {
            errorDlg = AlertDialog.showError(parent, error, "Fehler");
        }
    }

    private void resizeView(LinearLayout parent){
        int width = 0;
        float pwidth = 1;
        int count = 0;
        // we only really care about horizontal layouts (at the moment)
        if(parent.getOrientation() == LinearLayout.HORIZONTAL){
            width = parent.getWidth() - parent.getPaddingLeft() - parent.getPaddingRight();
        }

        for(int i = 0; i < parent.getChildCount(); i++){
            View v = parent.getChildAt(i);
            if(width > 0){
                float weight = ((LinearLayout.LayoutParams)v.getLayoutParams()).weight;
                width -= (int) (5 * density + 0.5f);
                if(weight > 0 && weight < 1){
                    pwidth -= weight;
                }else{
                    count++;
                }
            }
            // make sure to also check linearlayout children
            if(LinearLayout.class.isAssignableFrom(v.getClass())){
                resizeView((LinearLayout)v);
            }
        }
        if(width > 0) {
            width += (int) (5 * density + 0.5f);
            for (int i = 0; i < parent.getChildCount(); i++) {
                View v = parent.getChildAt(i);
                float weight = ((LinearLayout.LayoutParams) v.getLayoutParams()).weight;
                int vwidth = weight > 0 && weight < 1 ? (int) (weight * width + 0.5f) : (int) (width * pwidth / count + 0.5f);
                v.setLayoutParams(new LinearLayout.LayoutParams(vwidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    private void buildView(JSONArray structure, ViewGroup parent) {
        ShapeDrawable divider = new ShapeDrawable(new RectShape());
        divider.setIntrinsicWidth((int)(5 * density + 0.5f));
        divider.setIntrinsicHeight((int)(5 * density + 0.5f));
        divider.getPaint().setColor(Color.TRANSPARENT);
        for(int i = 0; i < structure.length(); i++){
            JSONObject obj = structure.optJSONObject(i);
            if(obj != null && obj.has("type")){
                View newView = null;
                switch(obj.optString("type")){
                    case "layout_v":
                        LinearLayout lv = new LinearLayout(context);
                        lv.setWeightSum(100);
                        lv.setOrientation(LinearLayout.VERTICAL);
                        buildView(obj.optJSONArray("content"), lv);
                        newView = lv;
                        lv.setDividerDrawable(divider);
                        lv.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                        lv.setPadding(0,0,0,0);
                        newView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        break;
                    case "layout_h":
                        LinearLayout lh = new LinearLayout(context);
                        lh.setWeightSum(100);
                        lh.setOrientation(LinearLayout.HORIZONTAL);
                        buildView(obj.optJSONArray("content"), lh);
                        newView = lh;
                        lh.setDividerDrawable(divider);
                        lh.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
                        lh.setPadding(0,0,0,0);
                        newView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT));
                        break;
                    case "static":
                        LinearLayout lls = new LinearLayout(context);
                        lls.setOrientation(LinearLayout.VERTICAL);
                        TextView lb1 = new TextView(context);
                        TextView st1 = new TextView(context);
                        lb1.setText(obj.optString("label"));
                        if(obj.has("id")){
                            idmap.put(obj.optString("id"), st1);
                        }
                        lls.addView(lb1);
                        lls.addView(st1);
                        lb1.setPadding((int)(15 * density + 0.5f), (int)(5 * density + 0.5f), (int)(15 * density + 0.5f), (int)(3 * density + 0.5f));
                        lb1.setTextSize(18*density);
                        st1.setPadding((int)(15 * density + 0.5f), (int)(5 * density + 0.5f), (int)(15 * density + 0.5f), (int)(5 * density + 0.5f));
                        st1.setTextSize(45*density);
                        lls.setPadding(0,0,0,0);
                        newView = lls;
                        break;
                    case "input_check":
                        LinearLayout llic = new LinearLayout(context);
                        llic.setOrientation(LinearLayout.VERTICAL);
                        TextView lb2 = new TextView(context);
                        ScanText2 st2 = new ScanText2(context);
                        lb2.setText(obj.optString("label"));
                        if(obj.has("id")){
                            idmap.put(obj.optString("id"), st2);
                        }
                        llic.addView(lb2);
                        llic.addView(st2);
                        lb2.setPadding((int)(15 * density + 0.5f), (int)(5 * density + 0.5f), (int)(15 * density + 0.5f), (int)(3 * density + 0.5f));
                        lb2.setTextSize(18*density);
                        st2.setPadding((int)(15 * density + 0.5f), (int)(5 * density + 0.5f), (int)(15 * density + 0.5f), (int)(5 * density + 0.5f));
                        st2.setTextSize(45*density);
                        llic.setPadding(0,0,0,0);
                        newView = llic;
                        break;
                    case "footer":
                        if(obj.has("id") && footer == null){
                            footer = new TextView(context);
                            footer.setTextAlignment(TEXT_ALIGNMENT_CENTER);
                            footer.setPadding((int)(5 * density + 0.5f), (int)(5 * density + 0.5f), (int)(5 * density + 0.5f), (int)(5 * density + 0.5f));
                            RelativeLayout.LayoutParams footerparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            footerparams.addRule(ALIGN_PARENT_BOTTOM);
                            //footer.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            footer.setLayoutParams(footerparams);
                            idmap.put(obj.optString("id"), footer);
                        }
                        break;
                }
                if(newView != null){
                    if(parent == null){
                        body = newView;
                        body.setPadding((int) (5 * density + 0.5f), 0, (int) (5 * density + 0.5f), 0);
                    }else{
                        String tmp = obj.optString("width", "100%");
                        tmp = tmp.substring(0, tmp.length() - 1);
                        float width = Float.parseFloat(tmp) < 0 ? 1 :  Float.parseFloat(tmp) / 100;
                        newView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, width));
                        parent.addView(newView);
                    }
                }
            }
        }
    }

    private CharSequence format(String s, String format){
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
}