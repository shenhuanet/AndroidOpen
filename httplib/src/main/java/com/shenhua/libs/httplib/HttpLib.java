package com.shenhua.libs.httplib;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by shenhua on 4/7/2017.
 * Email shenhuanet@126.com
 */
public class HttpLib {

    private static HttpLib sInstance = null;
    private Retrofit retrofit;
    private static OkHttpClient okHttpClient;

    public static HttpLib getInstance() {
        synchronized (HttpLib.class) {
            if (sInstance == null) {
                sInstance = new HttpLib();
            }
            return sInstance;
        }
    }

    /**
     * 获取retrofit基础服务
     *
     * @param context 上下文
     * @param baseUrl url
     * @return retrofit
     */
    public Retrofit getBaseRetrofitService(Context context, String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(baseUrl).client(getOkHttpClient(context))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * 获取okHttpClient
     *
     * @param context 上下文
     * @return OkHttpClient
     */
    public OkHttpClient getOkHttpClient(Context context) {
        return getOkHttpClient(context, false);
    }

    /**
     * 获取okHttpClient
     *
     * @param context 上下文
     * @param useLog  是否打印接收到的log信息
     * @return OkHttpClient
     */
    public OkHttpClient getOkHttpClient(Context context, boolean useLog) {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            if (useLog) {
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(loggingInterceptor);
            }
            Cache cache = new Cache(context.getExternalCacheDir(), 100 * 1024 * 1024);
            builder.cache(cache);
            builder.addInterceptor(new RewriteCacheControlInterceptor(context));
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    private class RewriteCacheControlInterceptor implements Interceptor {

        Context context;

        RewriteCacheControlInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            CacheControl.Builder cb = new CacheControl.Builder();
            cb.maxAge(0, TimeUnit.SECONDS);
            cb.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cb.build();
            Request request = chain.request();
            if (!NetworkUtils.isConnectedNet(context)) {
                request = request.newBuilder().cacheControl(cacheControl).build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isConnectedNet(context)) {
                int maxAge = 0;
                return originalResponse.newBuilder().removeHeader("Pragma")
                        .header("Cache-Control", "public,max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 30;
                return originalResponse.newBuilder().removeHeader("Pragma")
                        .header("Cache-Control", "public,only-if-xcached,max-stale=" + maxStale)
                        .build();
            }
        }
    }

    /**
     * 创建okHttp get 请求可观测对象
     *
     * @param context 上下文
     * @param url     完整的url路径
     * @return Observable
     */
    public Observable createHtmlGetObservable(final Context context, final String url) {
        return createHtmlGetObservable(context, url, false, "utf-8");
    }

    /**
     * 创建okHttp get 请求可观测对象
     *
     * @param context 上下文
     * @param url     完整的url路径
     * @param charset 编码
     * @return Observable
     */
    public Observable createHtmlGetObservable(final Context context, final String url, final String charset) {
        return createHtmlGetObservable(context, url, false, charset);
    }

    /**
     * 创建okHttp get 请求可观测对象
     *
     * @param context 上下文
     * @param url     完整的url路径
     * @param useLog  是否打印接收到的log信息
     * @return Observable
     */
    public Observable createHtmlGetObservable(final Context context, final String url, final boolean useLog) {
        return createHtmlGetObservable(context, url, useLog, "utf-8");
    }

    /**
     * 创建okHttp get 请求可观测对象
     *
     * @param context 上下文
     * @param url     完整的url路径
     * @param useLog  是否打印接收到的log信息
     * @param charset 编码
     * @return Observable
     */
    public Observable createHtmlGetObservable(final Context context, final String url, final boolean useLog, final String charset) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                Request request = new Request.Builder().url(url).get().build();
                Call call = getOkHttpClient(context, useLog).newCall(request);
                try {
                    Response response = call.execute();
                    String result = new String(response.body().bytes(), charset);
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 创建okHttp post 请求可观测对象
     *
     * @param context  上下文
     * @param url      完整的url路径
     * @param formBody 请求体
     * @return Observable
     */
    public Observable createHtmlPostObservable(final Context context, final String url, final RequestBody formBody) {
        return createHtmlPostObservable(context, url, formBody, false, "utf-8");
    }

    /**
     * 创建okHttp post 请求可观测对象
     *
     * @param context  上下文
     * @param url      完整的url路径
     * @param formBody 请求体
     * @param charset  编码
     * @return Observable
     */
    public Observable createHtmlPostObservable(final Context context, final String url, final RequestBody formBody, String charset) {
        return createHtmlPostObservable(context, url, formBody, false, charset);
    }

    /**
     * 创建okHttp post 请求可观测对象
     *
     * @param context  上下文
     * @param url      完整的url路径
     * @param formBody 请求体
     * @param useLog   是否打印接收到的log信息
     * @return Observable
     */
    public Observable createHtmlPostObservable(final Context context, final String url, final RequestBody formBody, final boolean useLog) {
        return createHtmlPostObservable(context, url, formBody, useLog, "utf-8");
    }

    /**
     * 创建okHttp post 请求可观测对象
     *
     * @param context  上下文
     * @param url      完整的url路径
     * @param formBody 请求体
     * @param useLog   是否打印接收到的log信息
     * @param charset  编码
     * @return Observable
     */
    public Observable createHtmlPostObservable(final Context context, final String url, final RequestBody formBody, final boolean useLog, final String charset) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                Request request = new Request.Builder().url(url).post(formBody).build();
                Call call = getOkHttpClient(context, useLog).newCall(request);
                try {
                    Response response = call.execute();
                    String result = new String(response.body().bytes(), charset);
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    /**
     * 简单的http get 请求
     *
     * @param url url
     * @return 返回的字符串
     */
    public String doSimpleHttpGet(String url) {
        Request request = new Request.Builder().url(url).get().build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 简单的http post 请求
     *
     * @param url  url
     * @param body 请求体
     * @return 返回的字符串
     */
    public String doSimpleHttpPost(String url, RequestBody body) {
        Request request = new Request.Builder().url(url).post(body).build();
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = builder.build();
        Call call = okHttpClient.newCall(request);
        try {
            Response response = call.execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 构建单一文件上传part
     *
     * @param file 文件
     * @return MultipartBody.Part
     */
    public MultipartBody.Part buildSingleFile(File file) {
        if (file == null) return null;
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData("image", file.getName(), requestBody);
    }

    /**
     * 构建多文件上传part
     *
     * @param files lists文件
     * @return MultipartBody.Part[]
     */
    public MultipartBody.Part[] buildMultiFilesUsePart(List<File> files) {
        if (files == null || files.size() == 0) return null;
        RequestBody requestFile;
        MultipartBody.Part[] results = new MultipartBody.Part[files.size()];
        for (int i = 0; i < files.size(); i++) {
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), files.get(i));
            results[i] = MultipartBody.Part.createFormData("image", files.get(i).getName(), requestFile);
        }
        return results;
    }

    /**
     * 构建多文件上传part
     *
     * @param files 文件数组
     * @return MultipartBody.Part[]
     */
    public MultipartBody.Part[] buildMultiFilesUsePart(File[] files) {
        if (files == null || files.length == 0) return null;
        RequestBody requestFile;
        MultipartBody.Part[] results = new MultipartBody.Part[files.length];
        for (int i = 0; i < files.length; i++) {
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), files[i]);
            results[i] = MultipartBody.Part.createFormData("image", files[i].getName(), requestFile);
        }
        return results;
    }

    /**
     * 构建多文件上传Map
     *
     * @param files 文件
     * @return Map
     */
    public Map<String, RequestBody> buildMultiFilesUssMap(List<File> files) {
        if (files == null || files.size() == 0) return null;
        Map<String, RequestBody> map = new HashMap<>();
        RequestBody requestFile;
        for (int i = 0; i < files.size(); i++) {
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), files.get(i));
            map.put("image\"; filename=\"" + files.get(i).getName(), requestFile);
        }
        return map;
    }

    /**
     * 构建多文件上传Map
     *
     * @param files 文件
     * @return Map
     */
    public Map<String, RequestBody> buildMultiFilesUssMap(File[] files) {
        if (files == null || files.length == 0) return null;
        Map<String, RequestBody> map = new HashMap<>();
        RequestBody requestFile;
        for (File file : files) {
            requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            map.put("image\"; filename=\"" + file.getName(), requestFile);
        }
        return map;
    }
}
