package de.proglove.basicdemo;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import de.proglove.katharinaUI.ScanText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        showMainScreen();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showMainScreen() {
        ScanText editUser;
        ScanText editPassword;
        ImageView logo;

        setContentView(R.layout.activity_username_and_password); //TODO: Set main activity

        editUser = findViewById(R.id.editTextUser);
        editPassword = findViewById(R.id.editTextPassword);
        logo = findViewById(R.id.imageViewLogo);

        // Scan into User Name:
        editUser.requestFocus();
        editUser.setOnScanEventListener(new ScanText.OnScanEventListener() {
            @Override
            public void onNext(ScanText source) {
                //TODO: action on scan event
            }
        });

        // Touch event on logo:
        logo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //TODO: action on touch event
                return true;
            }
        });

        // Double-tap listener:
        //TODO: targetOfListener can be a view, a button, a logo, etc, that waits for the double-tap
        /*targetOfListener.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && (keyCode == KeyEvent.KEYCODE_F1 || keyCode == KeyEvent.KEYCODE_ENTER)){
                    showNextScreen();
                    return true;
                }
                return false;
            }
        });*/
    }
}
