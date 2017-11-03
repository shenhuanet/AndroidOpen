package com.shenhua.alipaydemo.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by shenhua on 2016/6/12.
 */
public class HttpUtils {

    private static final String TAG = "HttpUtils";
    private static final int CONNECT_TIMEOUT = 15 * 1000;//建立连接最大等待时间
    private static final int READ_TIMEOUT = 1000;//读取数据最大等待时间
    private static final String CHARSET = "UTF-8";//Http请求内容编码格式

    /**
     * Post请求
     *
     * @param url
     * @param query
     * @return
     */
    public final String doPost(String url, String query) {
        String result = connect(url, query);
        if (result != null) {
            return result;
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
        String result = connect(url, null);
        if (result != null) {
            return result;
        } else {
            return null;
        }
    }

    public String connect(String urlSpec, String query) {
        if (TextUtils.isEmpty(urlSpec)) return "urlSpec is null";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlSpec);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setDoInput(true);
            connection.setRequestMethod("GET");// 默认为get请求
            connection.setRequestProperty("Accept-Charset", CHARSET);
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=" + CHARSET);
            if (!TextUtils.isEmpty(query)) {
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Length", String.valueOf(query.getBytes().length));
            }
            OutputStream os = connection.getOutputStream();
            os.write(query.getBytes());
            os.flush();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "connect: Http请求失败，响应码:" + connection.getResponseCode());
                return "HttpURLConnection ResponseCode is not ok";
            }
            InputStream is = connection.getInputStream();// 获取响应的输入流对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();// 创建字节输出流对象
            int len = 0;// 定义读取长度
            byte buffer[] = new byte[1024];// 定义缓冲区
            while ((len = is.read(buffer)) != -1) {// 按照缓冲区的大小，循环读取
                baos.write(buffer, 0, len);
            }
            // 释放资源
            is.close();
            baos.close();

            byte[] responseData = baos.toByteArray();
            if (responseData != null && responseData.length > 0) {
                return new String(responseData);
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "connect: Http请求失败，错误信息：" + e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }

}
