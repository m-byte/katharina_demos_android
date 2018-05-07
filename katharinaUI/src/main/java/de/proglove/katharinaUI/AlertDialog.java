package de.proglove.katharinaUI;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by matthias on 2/28/18.
 */

public class AlertDialog {
    public static Dialog showError(Activity activity, String message, String title){
        final View focus = activity.getCurrentFocus();
        final Dialog dialog = new Dialog(activity){
            @Override
            public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
                this.cancel();
                return super.onKeyUp(keyCode, event);
            }
        };
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_error);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(focus != null) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            focus.requestFocus();
                        }
                    }, 10);
                }
            }
        });

        TextView txt_message = dialog.findViewById(R.id.txt_message);
        TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_message.setText(message);
        txt_title.setText(title);

        dialog.show();
        return dialog;
    }
}
