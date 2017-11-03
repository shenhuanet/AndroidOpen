package com.shenhua.alipaydemo.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 自定义HTTP请求参数键值对容器 </br>
 * 如果传入值value为空，自动处理为空字符串 </br>
 * 数据效果：&key=value&key2=value
 *
 * Created by nodlee on 2016/4/28.
 */
public class QueryString {

    private StringBuilder query = new StringBuilder();

    public synchronized void add(String name, String value) {
        query.append("&");
        encode(name, value);
    }

    public synchronized void encode(String name, String value) {
        if (name == null) {
            throw new IllegalArgumentException("键name不能为空");
        }

        // 容错处理
        if (value == null) {
            value = "";
        }

        try {
            query.append(URLEncoder.encode(name, "UTF-8"));
            query.append("=");
            query.append(value.length() == 0 ? value : URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private synchronized String getQuery() {
        return query.toString();
    }

    @Override
    public String toString() {
        return getQuery();
    }
}
