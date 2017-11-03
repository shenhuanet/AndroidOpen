package com.shenhua.bannerdemo.droper;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

/**
 * 具有阻尼效果的 ScrollView （可用于设置界面）
 * Created by shenhua on 7/7/2016.
 */
public class DropScrollView extends ScrollView {

    private static final int size = 4;// 只允许拖动屏幕的1/4
    private View rootView; // ScrollView包含的整个根布局
    private float yDown; // 记录按下时的Y值
    private Rect rect = new Rect(); // 矩形

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (rootView == null) {
            return super.onTouchEvent(ev);
        } else {
            commOnTouchEvent(ev);
        }
        return super.onTouchEvent(ev);
    }

    private void commOnTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                yDown = ev.getY();
                break;
            case MotionEvent.ACTION_UP:
                if (isNeedAnimation()) {
                    animation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                final float preY = yDown;
                float nowY = ev.getY();
                int deltaY = (int) ((preY - nowY) / size);
                yDown = nowY;
                if (isNeedMove()) {
                    if (rect.isEmpty()) {
                        rect.set(rootView.getLeft(), rootView.getTop(), rootView.getRight(), rootView.getBottom());
                        return;
                    }
                    int yy = rootView.getTop() - deltaY;
                    rootView.layout(rootView.getLeft(), yy, rootView.getRight(), rootView.getBottom() - deltaY);
                }
                break;
        }

    }

    private void animation() {
        TranslateAnimation anim = new TranslateAnimation(0, 0, rootView.getTop() - rect.top, 0);
        anim.setDuration(200);
        rootView.startAnimation(anim);
        rootView.layout(rect.left, rect.top, rect.right, rect.bottom);
        rect.setEmpty();
    }

    private boolean isNeedMove() {
        int offset = rootView.getMeasuredHeight() - getHeight();
        int scrollY = getScrollY();
        return scrollY == 0 || scrollY == offset;
    }

    private boolean isNeedAnimation() {
        return !rect.isEmpty();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0)
            rootView = getChildAt(0);
    }

    public DropScrollView(Context context) {
        super(context);
    }

    public DropScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
