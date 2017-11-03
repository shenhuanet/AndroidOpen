package com.shenhua.alipaydemo.utils;

import android.text.TextUtils;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.shenhua.alipaydemo.utils.LogHelper.LOGE;
import static com.shenhua.alipaydemo.utils.LogHelper.LOGW;

/**
 * Created by nodlee on 2016/4/28.
 */
public class HttpManager {
    private static final String TAG = "HttpManager";
    /**
     * 建立连接最大等待时间
     */
    private static final int CONNECT_TIMEOUT = 15 * 1000;
    /**
     * 读取数据最大等待时间
     */
    private static final int READ_TIMEOUT = 1000;
    /**
     * Http请求内容编码格式
     */
    private static final String CHARSET = "UTF-8";

    private final byte[] connect(String urlSpec, String query) {
        if (TextUtils.isEmpty(urlSpec)) return null;

        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=" + CHARSET);

            if (!TextUtils.isEmpty(query)) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
                bos.write(query.getBytes(CHARSET));
                bos.flush();
                bos.close();
            }

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                LOGW(TAG, "Http请求失败，响应码code=" + connection.getResponseCode());
                return null;
            }

            InputStream in = connection.getInputStream();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int byteReaded = 0;
            byte[] buffer = new byte[1024];

            while ((byteReaded = in.read(buffer)) > 0) {
                bos.write(buffer, 0, byteReaded);
            }

            in.close();
            bos.close();
            return bos.toByteArray();

        } catch (IOException e) {
            LOGE(TAG, "Http请求失败，错误信息：" + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    /**
     * Post请求
     *
     * @param url
     * @param query
     * @return
     */
    public final String doPost(String url, String query) {
        byte[] responseData = connect(url, query);

        if (responseData != null && responseData.length > 0) {
            return String.valueOf(responseData);
        } else {
            return null;
        }
    }

    /**
     * Get 请求
     *
     * @param url
     * @return
     */
    public final String doGet(String url) {
        byte[] responseData = connect(url, null);

        if (responseData != null && responseData.length > 0) {
            return String.valueOf(responseData);
        } else {
            return null;
        }
    }
}
