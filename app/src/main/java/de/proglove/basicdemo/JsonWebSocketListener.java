package de.proglove.basicdemo;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class JsonWebSocketListener extends WebSocketListener {
    protected JsonContentView jsonview;

    public JsonWebSocketListener(JsonContentView jsonview){
        super();
        this.jsonview = jsonview;
    }

    @Override
    public void onMessage(WebSocket webSocket, String text){
        jsonview.updateJSONUi(text);
    }
}
