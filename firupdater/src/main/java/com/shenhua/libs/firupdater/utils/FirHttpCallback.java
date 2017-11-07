package com.shenhua.libs.firupdater.utils;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Shenhua on 11/30/2016.
 * e-mail shenhuanet@126.com
 */
public abstract class FirHttpCallback<T> extends Handler {

    public abstract void onSuccess(T t);

    public abstract void onFailed(int errorCode, String msg);

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case FirUtil.SUCCESS:
                onSuccess((T) msg.obj);
                break;
            case FirUtil.FAILED:
                onFailed(msg.arg1, msg.obj.toString());
                break;
        }
    }
}
