package com.shenhua.libs.httplib.mvp.contract;

import android.view.View;

/**
 * Created by shenhua on 4/7/2017.
 * Email shenhuanet@126.com
 */
public class BaseContract {

    public interface BaseView {
        void showLoading(String msg);

        void hideLoading();

        void showError(String msg, View.OnClickListener onClickListener);

        void showEmpty(String msg, View.OnClickListener onClickListener);

        void showEmpty(String msg, View.OnClickListener onClickListener, int imageId);

        void showNetError(View.OnClickListener onClickListener);
    }

    public interface BasePresenter<V extends BaseView>{
        void execute(V v);
    }

    public interface BaseModel<T> {
    }

    public interface BaseCallback<T> {
        void onStart();

        void onSuccess(T t);

        void onError(int code);

        void onFinish();
    }
}