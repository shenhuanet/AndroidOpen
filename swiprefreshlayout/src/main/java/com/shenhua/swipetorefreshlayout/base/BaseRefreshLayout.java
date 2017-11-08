package com.shenhua.swipetorefreshlayout.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * 下拉或上拉刷新加载基类
 * 头部需要实现这个抽象类，以告诉UcRefreshLayout头部的状态
 * Created by shenhua on 8/26/2016.
 */
public abstract class BaseRefreshLayout extends View {

    // 正在刷新
    public abstract boolean isLoading();

    // 已准备好刷新
    public abstract boolean isReadyLoad();

    // 执行加载动画
    public abstract void performLoading();

    // 执行加载完的效果，并把加载动画移除
    public abstract void performLoaded();

    // 执行拖动时的操作，参数 offset
    public abstract void performPull(float v);

    public BaseRefreshLayout(Context context) {
        super(context);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
