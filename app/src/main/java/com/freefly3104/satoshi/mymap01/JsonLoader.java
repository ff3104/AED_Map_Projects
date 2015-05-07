package com.freefly3104.satoshi.mymap01;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class JsonLoader extends AsyncTaskLoader<JSONArray> {

    private static final String TAG = "JsonLoader";
    private String urlText = null;

    public JsonLoader(Context context, String urlText) {
        super(context);
        this.urlText = urlText;
    }

    @Override
    public JSONArray loadInBackground() {

        URL url;
        try {
            url = new URL(urlText);
        } catch (MalformedURLException e) {
            Log.d(TAG, "invalid URL : " + urlText, e);
            return null;
        }

        HttpURLConnection conn = null;
        try {

            conn = (HttpsURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Connection", "close");
            conn.setFixedLengthStreamingMode(0);

            conn.connect();

            int code = conn.getResponseCode();
            Log.d(TAG, "Response code : " + code);

            if (code != 200) {
                Log.e(TAG, "HTTP GET Error : code=" + code);
                return null;
            }

            try {

                // レスポンス文字列取得
                BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];

                int length;
                while ((length = inputStream.read(buff)) != -1) {
                    if (length > 0) {
                        outputStream.write(buff, 0, length);
                    }
                }

                String data = outputStream.toString(); // JSONデータ
                JSONArray array = new JSONArray(data);
                return array;

            } catch (Exception e) {
                Log.d(TAG, "Error");
                return null;
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to get content : " + url, e);
            return null;
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ignore) {
                }
            }
        }

    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }
}
