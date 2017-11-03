package com.shenhua.bannerdemo.droper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;

import com.shenhua.bannerdemo.R;

/**
 * 具有阻尼效果的 LinearLayout （可用于网页界面），具有层次效果
 * Created by shenhua on 7/7/2016.
 */
public class DropTopLinearLayout extends LinearLayout implements View.OnTouchListener {

    private Context mContext;
    private boolean loadOnce;
    private boolean ableToPull = true;
    private float yDown;
    private int distance;//下拉时手指移动的距离
    private MarginLayoutParams headerLayoutParams;
    private int touchSlop; //在被判定为滚动之前用户手指可以移动的最大值。
    private int headHeight = 700;
    private int realHeight;
    private boolean onceHeight = true;
    private boolean isDrop = false;

    private void init() {
        setBackgroundColor(mContext.getResources().getColor(R.color.colorGray));
        touchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
        setOrientation(VERTICAL);
        setOnTouchListener(this);
    }

    public DropTopLinearLayout(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public DropTopLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public DropTopLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (!isDrop) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = motionEvent.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = motionEvent.getRawY();
                    distance = (int) ((yMove - yDown) / 4);
                    if (distance <= 0) {
                        layout(getLeft(), 0, getRight(), getBottom());
                        return false;
                    }
                    layout(getLeft(), distance, getRight(), getBottom());
                    break;
                case MotionEvent.ACTION_UP:
                    if (distance > 0 && distance < 200) {
                        animToTop();
                    }
                    if (distance >= 200) {
                        layout(getLeft(), 200, getRight(), getBottom());
                        isDrop = true;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                animToTop();
                            }
                        }).start();
                    }
            }
        }
        return true;
    }

    private void animToTop() {
        TranslateAnimation anim = new TranslateAnimation(0, 0, distance, 0);
        anim.setDuration(300);
        startAnimation(anim);
        layout(getLeft(), 0, getRight(), getBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (onceHeight && getHeight() > 0) {
            realHeight = getHeight();
            onceHeight = false;
        }
    }

}
