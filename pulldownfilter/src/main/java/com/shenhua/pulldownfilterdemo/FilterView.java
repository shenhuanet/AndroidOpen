package com.shenhua.pulldownfilterdemo;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by shenhua on 16/8/28.
 */
public class FilterView extends LinearLayout implements View.OnClickListener {

    private static final int DURATION = 200;// 显示或隐藏时的时间
    private ImageView mFilterArrowImageView;
    private LinearLayout mContentViewLayout;
    private GridView mGridView;
    private View mMaskBgView;
    private Context mContext;
    private boolean isShowing = false;
    private int panelHeight;// 控制板高度，用于ObjectAnimator
    private OnFilterItemClickListener onItemFilterClickListener;// 筛选视图里面的Item点击事件回调

    public FilterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.view_filter_layout, this);
        initView(view);
        initListener();
    }

    private void initView(View view) {
        mFilterArrowImageView = (ImageView) view.findViewById(R.id.iv_filter_arrow);
        mContentViewLayout = (LinearLayout) view.findViewById(R.id.layout_content_view);
        mGridView = (GridView) view.findViewById(R.id.grid_view);
        mMaskBgView = view.findViewById(R.id.view_mask_bg);
        mMaskBgView.setVisibility(GONE);
        mContentViewLayout.setVisibility(GONE);
    }

    private void initListener() {
        mFilterArrowImageView.setOnClickListener(this);
        mMaskBgView.setOnClickListener(this);
        mContentViewLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_filter_arrow:
                if (!isShowing)
                    show();
                break;
            case R.id.view_mask_bg:
                hide();
                break;
        }
    }

    // 显示动画
    public void show() {
        isShowing = true;
        mMaskBgView.setVisibility(VISIBLE);
        mContentViewLayout.setVisibility(VISIBLE);
        mContentViewLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mContentViewLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                panelHeight = mContentViewLayout.getHeight();
                ObjectAnimator.ofFloat(mContentViewLayout, "translationY", -panelHeight, 0).setDuration(DURATION).start();
            }
        });
    }

    // 隐藏动画
    public void hide() {
        isShowing = false;
        mMaskBgView.setVisibility(View.GONE);
        ObjectAnimator.ofFloat(mContentViewLayout, "translationY", 0, -panelHeight).setDuration(DURATION).start();
    }

    // 是否已经显示
    public boolean isShowing() {
        return isShowing;
    }

    // 设置筛选数据
    public void setFilterAdapter() {
        mGridView.setVisibility(VISIBLE);
        FilterGridViewAdapter filterAdapter = new FilterGridViewAdapter(mContext);
        mGridView.setAdapter(filterAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                hide();
                if (onItemFilterClickListener != null) {
                    onItemFilterClickListener.onFilterItemClick(position);
                }
            }
        });
    }

    public void setOnFilterItemClickListener(OnFilterItemClickListener onItemFilterClickListener) {
        this.onItemFilterClickListener = onItemFilterClickListener;
    }

    public interface OnFilterItemClickListener {
        void onFilterItemClick(int position);
    }

}
