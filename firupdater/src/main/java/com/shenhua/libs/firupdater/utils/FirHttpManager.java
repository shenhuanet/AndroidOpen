package com.shenhua.libs.firupdater.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Shenhua on 11/30/2016.
 * e-mail shenhuanet@126.com
 */
public class FirHttpManager {

    private static final String TAG = "FirHttpManager";
    private static final int CONNECT_TIMEOUT = 15000;
    private static final int READ_TIMEOUT = 10000;
    private static final String CHARSET = "UTF-8";

    /**
     * Post
     *
     * @param url   URL
     * @param query params
     * @return string
     */
    public final String doPost(String url, String query) {
        byte[] responseData = connect(url, query);
        if (responseData != null && responseData.length > 0) {
            return new String(responseData);
        } else {
            return null;
        }
    }

    /**
     * Get
     *
     * @param url url
     * @return string
     */
    public final String doGet(String url) {
        byte[] responseData = connect(url, null);
        if (responseData != null && responseData.length > 0) {
            return new String(responseData);
        } else {
            return null;
        }
    }

    private byte[] connect(String urlSpec, String query) {
        if (TextUtils.isEmpty(urlSpec)) return null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);
            if (query != null) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
                bos.write(query.getBytes(CHARSET));
                bos.flush();
                bos.close();
            }
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.i(TAG, "connect: Http connect error code = " + connection.getResponseCode());
                return null;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            int byteRead;
            byte[] buffer = new byte[1024];
            while ((byteRead = in.read(buffer)) > 0) {
                bos.write(buffer, 0, byteRead);
            }
            in.close();
            bos.close();
            return bos.toByteArray();
        } catch (IOException e) {
            Log.i(TAG, "connect: " + e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
