package de.proglove.basicdemo;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import de.proglove.katharinaUI.ScanText;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class MainActivity extends AppCompatActivity {

    protected JsonContentView view;
    protected OkHttpClient client;
    protected boolean connected = false;
    protected SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        view = new JsonContentView(this, this);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(view);
        ImageView img = new ImageView(this);
        img.setImageDrawable(getDrawable(R.drawable.logos));
        img.setPadding(5,0,5,0);
        img.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        view.setHeader(img);
        view.setPreview(connectScreen());
    }

    public View connectScreen(){
        // basic screen with input for url and button for connection
        // url is stored in sharedpreference
        LinearLayout l = new LinearLayout(this);
        TextView tv = new TextView(this);
        final EditText et = new EditText(this);
        final Context ctx = this;
        Button bt = new Button(this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float density = metrics.density;
        tv.setText("URL");
        et.setText(pref.getString("url", "ws://192.168.188.150/gateway"));
        bt.setText("Connect");
        l.setOrientation(LinearLayout.VERTICAL);
        l.addView(tv);
        l.addView(et);
        l.addView(bt);
        l.setPadding((int)(5 * density + 0.5f),0,(int)(5 * density + 0.5f),0);
        tv.setPadding((int)(15 * density + 0.5f), (int)(5 * density + 0.5f), (int)(15 * density + 0.5f), (int)(3 * density + 0.5f));
        tv.setTextSize(18*density);
        et.setPadding((int)(15 * density + 0.5f), (int)(5 * density + 0.5f), (int)(15 * density + 0.5f), (int)(5 * density + 0.5f));
        et.setTextSize(30*density);
        bt.setPadding((int)(15 * density + 0.5f), (int)(5 * density + 0.5f), (int)(15 * density + 0.5f), (int)(5 * density + 0.5f));
        bt.setTextSize(45*density);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref.edit();
                String url = et.getText().toString();
                editor.putString("url", url);
                editor.apply();
                connect(url);
                view.setPreview(new View(ctx));
            }
        });
        return l;
    }

    public void connect(String url){
        if(!connected){
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.retryOnConnectionFailure(true);

            client = builder.build();

            JsonWebSocketListener listener = new JsonWebSocketListener(view);
            Request request = new Request.Builder().url(url).build();
            WebSocket ws = client.newWebSocket(request, listener);
            connected = true;
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            // Hide the status bar.
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
